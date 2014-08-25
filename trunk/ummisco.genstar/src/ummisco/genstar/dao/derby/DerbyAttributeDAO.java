package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ummisco.genstar.dao.AttributeDAO;
import ummisco.genstar.dao.RangeValueDAO;
import ummisco.genstar.dao.UniqueValueDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.ATTRIBUTE_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.DataType;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.RangeValue;
import ummisco.genstar.metamodel.RangeValuesAttribute;
import ummisco.genstar.metamodel.UniqueValue;
import ummisco.genstar.metamodel.UniqueValuesAttribute;
import ummisco.genstar.util.PersistentObject;

public class DerbyAttributeDAO extends AbstractDerbyDAO implements AttributeDAO {
	
	private PreparedStatement createAttributeStmt, populateAttributesStmt, updateAttributesStmt;
	
	private RangeValueDAO rangeValueDAO;
	
	private UniqueValueDAO uniqueValueDAO;
	
	public DerbyAttributeDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.ATTRIBUTE_TABLE.TABLE_NAME);
		
		try {
			createAttributeStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
					+ ATTRIBUTE_TABLE.POPULATION_GENERATOR_ID_COLUMN_NAME + ", "
					+ ATTRIBUTE_TABLE.NAME_ON_DATA_COLUMN_NAME + ", "
					+ ATTRIBUTE_TABLE.NAME_ON_ENTITY_COLUMN_NAME + ", "
					+ ATTRIBUTE_TABLE.DATA_TYPE_COLUMN_NAME + ", "
					+ ATTRIBUTE_TABLE.VALUE_TYPE_ON_DATA_COLUMN_NAME + ", "
					+ ATTRIBUTE_TABLE.VALUE_TYPE_ON_ENTITY_COLUMN_NAME
					+ ") VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			populateAttributesStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ ATTRIBUTE_TABLE.POPULATION_GENERATOR_ID_COLUMN_NAME + " = ?");
			
			updateAttributesStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ ATTRIBUTE_TABLE.POPULATION_GENERATOR_ID_COLUMN_NAME + " = ? FOR UPDATE", 
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}

		rangeValueDAO = daoFactory.getRangeValueDAO();
		uniqueValueDAO = daoFactory.getUniqueValueDAO();
	}

	@Override
	public AbstractAttribute findAttributeByName(final String attributeName) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public AbstractAttribute findAttributeByID(final int attributeID) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void createAttributes(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException {
		
		try {
			internalCreateAttributes(syntheticPopulationGenerator.getID(), syntheticPopulationGenerator.getAttributes());
		} catch (Exception e) {
			if (e instanceof GenstarDAOException) { throw (GenstarDAOException)e; }
			throw new GenstarDAOException(e);
		}
		
	}
	
	private void internalCreateAttributes(final int syntheticPopulationGeneratorID, final Set<AbstractAttribute> attributes) throws Exception {
		createAttributeStmt.setInt(1, syntheticPopulationGeneratorID);
		
		for (AbstractAttribute attribute : attributes) {
			// firstly, save the information of AbstractAttribute
			createAttributeStmt.setString(2, attribute.getNameOnData());
			createAttributeStmt.setString(3, attribute.getNameOnEntity());
			createAttributeStmt.setInt(4, attribute.getDataType().getID());
			createAttributeStmt.setInt(5, AttributeValue.getIdByClass(attribute.getValueClassOnData()));
			createAttributeStmt.setInt(6, AttributeValue.getIdByClass(attribute.getValueClassOnEntity()));
			createAttributeStmt.executeUpdate();
			
			// retrieve the ID of the newly created AbstractAttribute
			ResultSet generatedKeySet = createAttributeStmt.getGeneratedKeys();
			if (generatedKeySet.next()) { attribute.setAttributeID(generatedKeySet.getInt(1)); }
			generatedKeySet.close();
			generatedKeySet = null;
			 
			
			// secondly, ask the appropriate ValueDAO (UniqueValueDAO or RangeValueDAO) to save the attribute values
			if (attribute instanceof RangeValuesAttribute) {
				rangeValueDAO.createRangeValues((RangeValuesAttribute) attribute);
			} else { // attribute instanceof UniqueValuesAttribute
				uniqueValueDAO.createUniqueValues((UniqueValuesAttribute) attribute);
			}
		}
	}
	
	@Override
	public void updateAttributes(final ISyntheticPopulationGenerator populationGenerator) throws GenstarDAOException {
		
		try {
			updateAttributesStmt.setInt(1, populationGenerator.getID());
			ResultSet resultSet = updateAttributesStmt.executeQuery();

			int attributeID;
			List<Integer> attributeIDsInDBMS = new ArrayList<Integer>();
			while (resultSet.next()) { attributeIDsInDBMS.add(resultSet.getInt(ATTRIBUTE_TABLE.ATTRIBUTE_ID_COLUMN_NAME)); }
			
			// 1. build the list of attributeIDs to create, update and delete
			Set<AbstractAttribute> attributesToCreate = new HashSet<AbstractAttribute>();
			Map<Integer, AbstractAttribute> attributesToUpdate = new HashMap<Integer, AbstractAttribute>();
			for (AbstractAttribute attr : populationGenerator.getAttributes()) {
				attributeID = attr.getAttributeID();
				
				if (attributeID == PersistentObject.NEW_OBJECT_ID) {
					attributesToCreate.add(attr);
				} else {
					attributesToUpdate.put(attributeID, attr);
				}
			}

			
			// 2. remove "deleted" attributes or update existing attributes
			AbstractAttribute attribute;
			resultSet.beforeFirst();
			Set<Integer> attributeIDsToUpdate = new HashSet<Integer>(attributesToUpdate.keySet());
			while (resultSet.next()) {
				attributeID = resultSet.getInt(ATTRIBUTE_TABLE.ATTRIBUTE_ID_COLUMN_NAME);
				
				if (attributeIDsToUpdate.contains(attributeID)) { // update the attribute
					attribute = attributesToUpdate.get(attributeID);
					
					// firstly, update the attribute
					resultSet.updateString(ATTRIBUTE_TABLE.NAME_ON_DATA_COLUMN_NAME, attribute.getNameOnData());
					resultSet.updateString(ATTRIBUTE_TABLE.NAME_ON_ENTITY_COLUMN_NAME, attribute.getNameOnEntity());
					resultSet.updateInt(ATTRIBUTE_TABLE.DATA_TYPE_COLUMN_NAME, attribute.getDataType().getID());
					// can not change valueClassOnData
					resultSet.updateInt(ATTRIBUTE_TABLE.VALUE_TYPE_ON_ENTITY_COLUMN_NAME, AttributeValue.getIdByClass(attribute.getValueClassOnEntity()));
					resultSet.updateRow();
										
					
					// secondly, ask the appropriate ValueDAO (UniqueValueDAO or RangeValueDAO) to update the attribute values
					if (attribute instanceof RangeValuesAttribute) {
						rangeValueDAO.updateRangeValues((RangeValuesAttribute) attribute);
					} else { // attribute instanceof UniqueValuesAttribute
						uniqueValueDAO.updateUniqueValues((UniqueValuesAttribute) attribute);
					}
					
				} else { // delete the attribute
					resultSet.deleteRow();
				}
			}
			
			
			// 3. create new attributes
			this.internalCreateAttributes(populationGenerator.getID(), attributesToCreate);

			
			resultSet.close();
			resultSet = null;
		} catch (Exception e) {
			if (e instanceof GenstarDAOException) { throw (GenstarDAOException)e; }
			throw new GenstarDAOException(e);
		}
	}
	
	@Override
	public void populateAttributes(final ISyntheticPopulationGenerator populationGenerator) throws GenstarDAOException {
		try {
			int attributeID;
			String nameOnData;
			String nameOnEntity;
			int dataTypeID;
			int valueTypeOnDataID;
			int valueTypeOnEntityID;
			
			populateAttributesStmt.setInt(1, populationGenerator.getID());
			ResultSet resultSet = populateAttributesStmt.executeQuery();
			while (resultSet.next()) {
				attributeID = resultSet.getInt(ATTRIBUTE_TABLE.ATTRIBUTE_ID_COLUMN_NAME);
				nameOnData = resultSet.getString(ATTRIBUTE_TABLE.NAME_ON_DATA_COLUMN_NAME);
				nameOnEntity = resultSet.getString(ATTRIBUTE_TABLE.NAME_ON_ENTITY_COLUMN_NAME);
				dataTypeID = resultSet.getInt(ATTRIBUTE_TABLE.DATA_TYPE_COLUMN_NAME);
				valueTypeOnDataID = resultSet.getInt(ATTRIBUTE_TABLE.VALUE_TYPE_ON_DATA_COLUMN_NAME);
				valueTypeOnEntityID = resultSet.getInt(ATTRIBUTE_TABLE.VALUE_TYPE_ON_ENTITY_COLUMN_NAME);
				
				DataType dataType = DataType.getDataTypeByID(dataTypeID);
				Class<? extends AttributeValue> valueClassOnEntity = (valueTypeOnEntityID == UniqueValue.UNIQUE_VALUE_TYPE) ? UniqueValue.class : RangeValue.class;
				
				// initialize the appropriate Attribute class
				if (valueTypeOnDataID == UniqueValue.UNIQUE_VALUE_TYPE) {
					UniqueValuesAttribute uniqueValuesAttribute = new UniqueValuesAttribute(populationGenerator, nameOnData, nameOnEntity, dataType, valueClassOnEntity);
					uniqueValuesAttribute.setAttributeID(attributeID);
					populationGenerator.addAttribute(uniqueValuesAttribute);
					
					// populate unique attribute values
					uniqueValueDAO.populateUniqueValues(uniqueValuesAttribute);
					
				} else { // RangeValue
					RangeValuesAttribute rangeValuesAttribute = new RangeValuesAttribute(populationGenerator, nameOnData, nameOnEntity, dataType, valueClassOnEntity);
					rangeValuesAttribute.setAttributeID(attributeID);
					populationGenerator.addAttribute(rangeValuesAttribute);
					
					// populate range attribute values
					rangeValueDAO.populateRangeValues(rangeValuesAttribute);
				}
			}
			
			resultSet.close();
			resultSet = null;
			
		} catch (Exception e) {
			if (e instanceof GenstarDAOException) { throw (GenstarDAOException)e; }
			throw new GenstarDAOException(e);
		}
	}
}
