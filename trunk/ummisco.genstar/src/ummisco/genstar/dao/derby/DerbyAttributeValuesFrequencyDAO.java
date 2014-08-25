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

import ummisco.genstar.dao.AttributeValuesFrequencyDAO;
import ummisco.genstar.dao.AttributeValuesFrequencyDataDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.ATTRIBUTE_VALUES_FREQUENCY_TABLE;
import ummisco.genstar.dao.derby.DBMS_Tables.INPUT_OUTPUT_ATTRIBUTE_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.AttributeValuesFrequency;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.RangeValuesAttribute;
import ummisco.genstar.metamodel.UniqueValuesAttribute;
import ummisco.genstar.util.PersistentObject;

public class DerbyAttributeValuesFrequencyDAO extends AbstractDerbyDAO implements AttributeValuesFrequencyDAO {

	private PreparedStatement createAttributeValuesFrequeciesStmt, populateAttributeValuesFrequenciesStmt, updateAttributeValuesFrequenciesStmt;
	
	private AttributeValuesFrequencyDataDAO attributeValuesFrequencyDataDAO;
	
	
	public DerbyAttributeValuesFrequencyDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.ATTRIBUTE_VALUES_FREQUENCY_TABLE.TABLE_NAME);
		
		try {
			createAttributeValuesFrequeciesStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
					+ ATTRIBUTE_VALUES_FREQUENCY_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
					+ ATTRIBUTE_VALUES_FREQUENCY_TABLE.FREQUENCY_COLUMN_NAME + ") VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			populateAttributeValuesFrequenciesStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME 
					+ " WHERE " + ATTRIBUTE_VALUES_FREQUENCY_TABLE.GENERATION_RULE_ID_COLUMN_NAME + " = ?");
			
			updateAttributeValuesFrequenciesStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME
					+ " WHERE " + ATTRIBUTE_VALUES_FREQUENCY_TABLE.GENERATION_RULE_ID_COLUMN_NAME + " = ? FOR UPDATE"
					, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
		
		attributeValuesFrequencyDataDAO = daoFactory.getAttributeValuesFrequencyDataDAO();
	}

	@Override
	public void createAttributeValuesFrequencies(final FrequencyDistributionGenerationRule generationRule) throws GenstarDAOException {
		internalCreateAttributeValuesFrequencies(generationRule.getGenerationRuleID(), generationRule.getAttributeValuesFrequencies());
	}
	
	private void internalCreateAttributeValuesFrequencies(final int generationRuleID, final Set<AttributeValuesFrequency> attributeValuesFrequencies) throws GenstarDAOException {
		try {
			createAttributeValuesFrequeciesStmt.setInt(1, generationRuleID);
			
			for (AttributeValuesFrequency avFrequency : attributeValuesFrequencies) {
				createAttributeValuesFrequeciesStmt.setInt(2, avFrequency.getFrequency());
				createAttributeValuesFrequeciesStmt.executeUpdate();
				
				// retrieve the ID of the newly created FrequencyDistributionElement
				ResultSet generatedKeySet = createAttributeValuesFrequeciesStmt.getGeneratedKeys();
				if (generatedKeySet.next()) { avFrequency.setID(generatedKeySet.getInt(1)); }
				generatedKeySet.close();
				generatedKeySet = null;
				 
				
				// Save the attribute values
				attributeValuesFrequencyDataDAO.createAttributeValuesFrequencyData(avFrequency);
			}
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}
	

	@Override
	public void populateAttributeValuesFrequencies(final FrequencyDistributionGenerationRule rule) throws GenstarDAOException {
		try {
			int atttributeValuesFrequencyID;
			int frequency;
			
			populateAttributeValuesFrequenciesStmt.setInt(1, rule.getGenerationRuleID());
			ResultSet resultSet = populateAttributeValuesFrequenciesStmt.executeQuery();
			while (resultSet.next()) {
				atttributeValuesFrequencyID = resultSet.getInt(ATTRIBUTE_VALUES_FREQUENCY_TABLE.ATTRIBUTE_VALUES_FREQUENCY_ID_COLUMN_NAME);
				frequency = resultSet.getInt(ATTRIBUTE_VALUES_FREQUENCY_TABLE.FREQUENCY_COLUMN_NAME);
				
				attributeValuesFrequencyDataDAO.populateAttributeValuesFrequencyData(rule, atttributeValuesFrequencyID, frequency);
			}
			
			resultSet.close();
			resultSet = null;
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public void updateAttributeValuesFrequencies(final FrequencyDistributionGenerationRule generationRule) throws GenstarDAOException {
		
		try {
			updateAttributeValuesFrequenciesStmt.setInt(1, generationRule.getGenerationRuleID());
			ResultSet resultSet = updateAttributeValuesFrequenciesStmt.executeQuery();
			
			int avfID;
			List<Integer> attributeValueFrequencyIDsInDBMS = new ArrayList<Integer>();
			while (resultSet.next()) { attributeValueFrequencyIDsInDBMS.add(resultSet.getInt(ATTRIBUTE_VALUES_FREQUENCY_TABLE.ATTRIBUTE_VALUES_FREQUENCY_ID_COLUMN_NAME)); }
			
			// 1. build the list of attributeValueFrequencyIDs to create, update and delete
			resultSet.beforeFirst();
			Set<AttributeValuesFrequency> attributeValueFrequenciesToCreate = new HashSet<AttributeValuesFrequency>();
			Map<Integer, AttributeValuesFrequency> attributeValueFrequenciesToUpdate = new HashMap<Integer, AttributeValuesFrequency>();
			for (AttributeValuesFrequency avf : generationRule.getAttributeValuesFrequencies()) {
				avfID = avf.getID();
				
				if (avfID == PersistentObject.NEW_OBJECT_ID) {
					attributeValueFrequenciesToCreate.add(avf);
				} else {
					attributeValueFrequenciesToUpdate.put(avfID, avf);
				}
			}

			
			// 2. remove "deleted" AttributeValuesFrequencies or update existing AttributeValuesFrequencies
			AttributeValuesFrequency attrVF;
			Set<Integer> attributeValueFrequencyIDsToUpdate = new HashSet<Integer>(attributeValueFrequenciesToUpdate.keySet());
			
			resultSet.beforeFirst();
			while (resultSet.next()) {
				avfID = resultSet.getInt(ATTRIBUTE_VALUES_FREQUENCY_TABLE.ATTRIBUTE_VALUES_FREQUENCY_ID_COLUMN_NAME);
				
				if (attributeValueFrequencyIDsToUpdate.contains(avfID)) { // update the AttributeValuesFrequency
					attrVF = attributeValueFrequenciesToUpdate.get(avfID);
					
					
					// update the AttributeValuesFrequency
					resultSet.updateInt(ATTRIBUTE_VALUES_FREQUENCY_TABLE.FREQUENCY_COLUMN_NAME, attrVF.getFrequency());
					resultSet.updateRow();
					
					// update the frequency's data
					attributeValuesFrequencyDataDAO.updateAttributeValuesFrequencyData(attrVF);
					
				} else { // delete the attribute
					resultSet.deleteRow();
				}
			}
			resultSet.close();
			resultSet = null;
			 

			// 3. create new AttributeValuesFrequencies
			internalCreateAttributeValuesFrequencies(generationRule.getGenerationRuleID(), attributeValueFrequenciesToCreate);
			
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

}
