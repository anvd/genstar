package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

public class DerbyAttributeDAO extends AbstractDerbyDAO implements AttributeDAO {
	
	private PreparedStatement createAttributeStmt, populateAttributesStmt;
	
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
			createAttributeStmt.setInt(1, syntheticPopulationGenerator.getID());

			for (AbstractAttribute attribute : syntheticPopulationGenerator.getAttributes()) {
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
		} catch (final Exception e) {
			if (e instanceof GenstarDAOException) { throw (GenstarDAOException)e; }
			throw new GenstarDAOException(e);
		}
		
	}
	
	@Override
	public void updateAttributes(final ISyntheticPopulationGenerator populationGenerator) throws GenstarDAOException {
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
