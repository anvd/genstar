package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ummisco.genstar.dao.AttributeInferenceDataDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.INFERENCE_DATA_RANGE_INFER_RANGE_TABLE;
import ummisco.genstar.dao.derby.DBMS_Tables.INFERENCE_DATA_RANGE_INFER_UNIQUE_TABLE;
import ummisco.genstar.dao.derby.DBMS_Tables.INFERENCE_DATA_UNIQUE_INFER_RANGE_TABLE;
import ummisco.genstar.dao.derby.DBMS_Tables.INFERENCE_DATA_UNIQUE_INFER_UNIQUE_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.UniqueValuesAttribute;

public class DerbyAttributeInferenceDataDAO extends AbstractDerbyDAO implements AttributeInferenceDataDAO {
	
	private UniqueInferRangeInferenceDataDAO uniqueInferRangeInferenceDataDAO;
	private RangeInferRangeInferenceDataDAO rangeInferRangeInferenceDataDAO;
	private UniqueInferUniqueInferenceDataDAO uniqueInferUniqueInferenceDataDAO;
	private RangeInferUniqueInferenceDataDAO rangeInferUniqueInferenceDataDAO;

	public DerbyAttributeInferenceDataDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, "");
		
		uniqueInferRangeInferenceDataDAO = new UniqueInferRangeInferenceDataDAO(daoFactory);
		rangeInferRangeInferenceDataDAO = new RangeInferRangeInferenceDataDAO(daoFactory);
		uniqueInferUniqueInferenceDataDAO = new UniqueInferUniqueInferenceDataDAO(daoFactory);
		rangeInferUniqueInferenceDataDAO = new RangeInferUniqueInferenceDataDAO(daoFactory);
	}

	@Override
	public void createInferenceData(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException {
		
		// ask the corresponding "helper" DAO to create the inference data
		AbstractAttribute inferringAttribute = attributeInferenceGenerationRule.getInferringAttribute();
		AbstractAttribute inferredAttribute = attributeInferenceGenerationRule.getInferredAttribute();
		
		if (inferringAttribute instanceof UniqueValuesAttribute) {
			if (inferredAttribute instanceof UniqueValuesAttribute) {
				uniqueInferUniqueInferenceDataDAO.createInferenceData(attributeInferenceGenerationRule);
			} else { // inferedAttribute instanceof RangeValuesAttribute
				uniqueInferRangeInferenceDataDAO.createInferenceData(attributeInferenceGenerationRule);
			}
		} else { // inferringAttribute instanceof RangeValuesAttribute
			if (inferredAttribute instanceof UniqueValuesAttribute) {
				rangeInferUniqueInferenceDataDAO.createInferenceData(attributeInferenceGenerationRule);
			} else { // inferedAttribute instanceof RangeValuesAttribute
				rangeInferRangeInferenceDataDAO.createInferenceData(attributeInferenceGenerationRule);
			}
		}
	}

	
	private class UniqueInferRangeInferenceDataDAO extends AbstractDerbyDAO implements AttributeInferenceDataDAO {
		
		private PreparedStatement createUniqueInferRangeInferenceDataStmt, populateInferenceDataStmt;
		

		public UniqueInferRangeInferenceDataDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
			super(daoFactory, DBMS_Tables.INFERENCE_DATA_UNIQUE_INFER_RANGE_TABLE.TABLE_NAME);
			
			try {
				createUniqueInferRangeInferenceDataStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
					+ INFERENCE_DATA_UNIQUE_INFER_RANGE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
					+ INFERENCE_DATA_UNIQUE_INFER_RANGE_TABLE.INFERRING_UNIQUE_VALUE_ID_COLUMN_NAME + ", "
					+ INFERENCE_DATA_UNIQUE_INFER_RANGE_TABLE.INFERRED_RANGE_VALUE_ID_COLUMN_NAME
					+ ") VALUES (?, ?, ?)");
				
				populateInferenceDataStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
						+ INFERENCE_DATA_UNIQUE_INFER_RANGE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + " = ?");
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}

		@Override
		public void createInferenceData(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException {
			
			// create the inference data
			try {
				createUniqueInferRangeInferenceDataStmt.setInt(1, attributeInferenceGenerationRule.getGenerationRuleID());
				
				Map<AttributeValue, AttributeValue> inferenceData = attributeInferenceGenerationRule.getInferenceData();
				for (AttributeValue inferringValue : inferenceData.keySet()) {
					createUniqueInferRangeInferenceDataStmt.setInt(2, inferringValue.getAttributeValueID());
					createUniqueInferRangeInferenceDataStmt.setInt(3, inferenceData.get(inferringValue).getAttributeValueID());
					createUniqueInferRangeInferenceDataStmt.executeUpdate();
				}
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}

		@Override
		public void populateInferenceData(final AttributeInferenceGenerationRule rule) throws GenstarDAOException {
			
			try {
				Map<AttributeValue, AttributeValue> inferenceData = new HashMap<AttributeValue, AttributeValue>();
				Map<AttributeValue, AttributeValue> tmpInferenceData = new HashMap<AttributeValue, AttributeValue>(rule.getInferenceData());
				
				int inferringUniqueValueID;
				int inferredRangeValueID;
				AttributeValue inferringUniqueValue, inferredRangeValue;
				
				populateInferenceDataStmt.setInt(1, rule.getGenerationRuleID());
				ResultSet resultSet = populateInferenceDataStmt.executeQuery();
				while (resultSet.next()) {
					inferringUniqueValueID = resultSet.getInt(INFERENCE_DATA_UNIQUE_INFER_RANGE_TABLE.INFERRING_UNIQUE_VALUE_ID_COLUMN_NAME);
					inferredRangeValueID = resultSet.getInt(INFERENCE_DATA_UNIQUE_INFER_RANGE_TABLE.INFERRED_RANGE_VALUE_ID_COLUMN_NAME);
					inferringUniqueValue = null;
					inferredRangeValue = null;
					
					for (AttributeValue inferringValue : tmpInferenceData.keySet()) {
						if (inferringValue.getAttributeValueID() == inferringUniqueValueID) {
							inferringUniqueValue = inferringValue;
							break;
						}
					}
					
					for (AttributeValue inferredValue : tmpInferenceData.values()) {
						if (inferredValue.getAttributeValueID() == inferredRangeValueID) {
							inferredRangeValue = inferredValue;
							break;
						}
					}
					
//					if (inferringUniqueValue == null || inferredRangeValue == null) { // this (should) never happens actually!
//					}
					
					inferenceData.put(inferringUniqueValue, inferredRangeValue);
				}
				rule.setInferenceData(inferenceData);
				
				resultSet.close();
				resultSet = null;
			} catch (Exception e) {
				throw new GenstarDAOException(e);
			} 
			
		}
	}	
	
	
	private class RangeInferRangeInferenceDataDAO extends AbstractDerbyDAO implements AttributeInferenceDataDAO {
		
		private PreparedStatement createRangeInferRangeInferenceDataStmt, populateInferenceDataStmt;
		
		
		public RangeInferRangeInferenceDataDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
			super(daoFactory, DBMS_Tables.INFERENCE_DATA_RANGE_INFER_RANGE_TABLE.TABLE_NAME);
			
			
			try {
				createRangeInferRangeInferenceDataStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
					+ INFERENCE_DATA_RANGE_INFER_RANGE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
					+ INFERENCE_DATA_RANGE_INFER_RANGE_TABLE.INFERRING_RANGE_VALUE_ID_COLUMN_NAME + ", "
					+ INFERENCE_DATA_RANGE_INFER_RANGE_TABLE.INFERRED_RANGE_VALUE_ID_COLUMN_NAME
					+ ") VALUES (?, ?, ?)");
				
				populateInferenceDataStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
						+ INFERENCE_DATA_RANGE_INFER_RANGE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + " = ?");

			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}

		@Override
		public void createInferenceData(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException {
			
			// create the inference data
			try {
				createRangeInferRangeInferenceDataStmt.setInt(1, attributeInferenceGenerationRule.getGenerationRuleID());
	
				Map<AttributeValue, AttributeValue> inferenceData = attributeInferenceGenerationRule.getInferenceData();
				for (AttributeValue inferringValue : inferenceData.keySet()) {
					createRangeInferRangeInferenceDataStmt.setInt(2, inferringValue.getAttributeValueID());
					createRangeInferRangeInferenceDataStmt.setInt(3, inferenceData.get(inferringValue).getAttributeValueID());
					createRangeInferRangeInferenceDataStmt.executeUpdate();
				}
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}

		@Override
		public void populateInferenceData(final AttributeInferenceGenerationRule rule) throws GenstarDAOException {
			try {
				Map<AttributeValue, AttributeValue> inferenceData = new HashMap<AttributeValue, AttributeValue>();
				Map<AttributeValue, AttributeValue> tmpInferenceData = new HashMap<AttributeValue, AttributeValue>(rule.getInferenceData());
				
				int inferringRangeValueID;
				int inferredRangeValueID;
				AttributeValue inferringRangeValue, inferredRangeValue;
				
				populateInferenceDataStmt.setInt(1, rule.getGenerationRuleID());
				ResultSet resultSet = populateInferenceDataStmt.executeQuery();
				while (resultSet.next()) {
					inferringRangeValueID = resultSet.getInt(INFERENCE_DATA_RANGE_INFER_RANGE_TABLE.INFERRING_RANGE_VALUE_ID_COLUMN_NAME);
					inferredRangeValueID = resultSet.getInt(INFERENCE_DATA_RANGE_INFER_RANGE_TABLE.INFERRED_RANGE_VALUE_ID_COLUMN_NAME);
					inferringRangeValue = null;
					inferredRangeValue = null;
					
					for (AttributeValue inferringValue : tmpInferenceData.keySet()) {
						if (inferringValue.getAttributeValueID() == inferringRangeValueID) {
							inferringRangeValue = inferringValue;
							break;
						}
					}
					
					for (AttributeValue inferredValue : tmpInferenceData.values()) {
						if (inferredValue.getAttributeValueID() == inferredRangeValueID) {
							inferredRangeValue = inferredValue;
							break;
						}
					}
					
//					if (inferringRangeValue == null || inferredRangeValue == null) { // this (should) never happens actually!
//					}
					
					inferenceData.put(inferringRangeValue, inferredRangeValue);
				}
				rule.setInferenceData(inferenceData);
				
				resultSet.close();
				resultSet = null;
			} catch (Exception e) {
				throw new GenstarDAOException(e);
			}
		}  
	}
	

	private class UniqueInferUniqueInferenceDataDAO extends AbstractDerbyDAO implements AttributeInferenceDataDAO {

		private PreparedStatement createUniqueInferUniqueInferenceDataStmt, populateInferenceDataStmt;
		 
		
		public UniqueInferUniqueInferenceDataDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
			super(daoFactory, DBMS_Tables.INFERENCE_DATA_UNIQUE_INFER_UNIQUE_TABLE.TABLE_NAME);
			
			try {
				createUniqueInferUniqueInferenceDataStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
					+ INFERENCE_DATA_UNIQUE_INFER_UNIQUE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
					+ INFERENCE_DATA_UNIQUE_INFER_UNIQUE_TABLE.INFERRING_UNIQUE_VALUE_ID_COLUMN_NAME + ", "
					+ INFERENCE_DATA_UNIQUE_INFER_UNIQUE_TABLE.INFERRED_UNIQUE_VALUE_ID_COLUMN_NAME
					+ ") VALUES (?, ?, ?)");
				
				populateInferenceDataStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
						+ INFERENCE_DATA_UNIQUE_INFER_UNIQUE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + " = ?");
								
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}

		@Override
		public void createInferenceData(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException {
			
			try {
				// create the inference data
				createUniqueInferUniqueInferenceDataStmt.setInt(1, attributeInferenceGenerationRule.getGenerationRuleID());
				
				Map<AttributeValue, AttributeValue> inferenceData = attributeInferenceGenerationRule.getInferenceData();
				for (AttributeValue inferringValue : inferenceData.keySet()) {
					createUniqueInferUniqueInferenceDataStmt.setInt(2, inferringValue.getAttributeValueID());
					createUniqueInferUniqueInferenceDataStmt.setInt(3, inferenceData.get(inferringValue).getAttributeValueID());
					createUniqueInferUniqueInferenceDataStmt.executeUpdate();
				}
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}

		@Override
		public void populateInferenceData(final AttributeInferenceGenerationRule rule) throws GenstarDAOException {
			
			try {
				Map<AttributeValue, AttributeValue> inferenceData = new HashMap<AttributeValue, AttributeValue>();
				Map<AttributeValue, AttributeValue> tmpInferenceData = new HashMap<AttributeValue, AttributeValue>(rule.getInferenceData());
				
				int inferringUniqueValueID;
				int inferredUniqueValueID;
				AttributeValue inferringUniqueValue, inferredUniqueValue;
				
				populateInferenceDataStmt.setInt(1, rule.getGenerationRuleID());
				ResultSet resultSet = populateInferenceDataStmt.executeQuery();
				while (resultSet.next()) {
					inferringUniqueValueID = resultSet.getInt(INFERENCE_DATA_UNIQUE_INFER_UNIQUE_TABLE.INFERRING_UNIQUE_VALUE_ID_COLUMN_NAME);
					inferredUniqueValueID = resultSet.getInt(INFERENCE_DATA_UNIQUE_INFER_UNIQUE_TABLE.INFERRED_UNIQUE_VALUE_ID_COLUMN_NAME);
					inferringUniqueValue = null;
					inferredUniqueValue = null;
					
					for (AttributeValue inferringValue : tmpInferenceData.keySet()) {
						if (inferringValue.getAttributeValueID() == inferringUniqueValueID) {
							inferringUniqueValue = inferringValue;
							break;
						}
					}
					
					for (AttributeValue inferredValue : tmpInferenceData.values()) {
						if (inferredValue.getAttributeValueID() == inferredUniqueValueID) {
							inferredUniqueValue = inferredValue;
							break;
						}
					}
					
//					if (inferringUniqueValue == null || inferredUniqueValue == null) { // this (should) never happens actually!
//					}
					
					inferenceData.put(inferringUniqueValue, inferredUniqueValue);
				}
				rule.setInferenceData(inferenceData);
				
				resultSet.close();
				resultSet = null;
			} catch (Exception e) {
				throw new GenstarDAOException(e);
			}
		} 
	}
	

	private class RangeInferUniqueInferenceDataDAO extends AbstractDerbyDAO implements AttributeInferenceDataDAO {
		
		private PreparedStatement createRangeInferUniqueInferenceDataStmt, populateInferenceDataStmt;
		
		
		public RangeInferUniqueInferenceDataDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
			super(daoFactory, DBMS_Tables.INFERENCE_DATA_RANGE_INFER_UNIQUE_TABLE.TABLE_NAME);
			
			try {
				createRangeInferUniqueInferenceDataStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
						+ INFERENCE_DATA_RANGE_INFER_UNIQUE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
						+ INFERENCE_DATA_RANGE_INFER_UNIQUE_TABLE.INFERRING_RANGE_VALUE_ID_COLUMN_NAME + ", "
						+ INFERENCE_DATA_RANGE_INFER_UNIQUE_TABLE.INFERRED_UNIQUE_VALUE_ID_COLUMN_NAME
						+ ") VALUES (?, ?, ?)");
				
				populateInferenceDataStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
						+ INFERENCE_DATA_RANGE_INFER_UNIQUE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + " = ?");
				
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}

		@Override
		public void createInferenceData(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException {
			
			try {
				// create the inference data
				createRangeInferUniqueInferenceDataStmt.setInt(1, attributeInferenceGenerationRule.getGenerationRuleID());
				
				Map<AttributeValue, AttributeValue> inferenceData = attributeInferenceGenerationRule.getInferenceData();
				for (AttributeValue inferringValue : inferenceData.keySet()) {
					createRangeInferUniqueInferenceDataStmt.setInt(2, inferringValue.getAttributeValueID());
					createRangeInferUniqueInferenceDataStmt.setInt(3, inferenceData.get(inferringValue).getAttributeValueID());
					createRangeInferUniqueInferenceDataStmt.executeUpdate();
				}
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}

		@Override
		public void populateInferenceData(final AttributeInferenceGenerationRule rule) throws GenstarDAOException {
			
			try {
				Map<AttributeValue, AttributeValue> inferenceData = new HashMap<AttributeValue, AttributeValue>();
				Map<AttributeValue, AttributeValue> tmpInferenceData = new HashMap<AttributeValue, AttributeValue>(rule.getInferenceData());
				
				int inferringRangeValueID;
				int inferredUniqueValueID;
				AttributeValue inferringRangeValue, inferredUniqueValue;
				
				populateInferenceDataStmt.setInt(1, rule.getGenerationRuleID());
				ResultSet resultSet = populateInferenceDataStmt.executeQuery();
				while (resultSet.next()) {
					inferringRangeValueID = resultSet.getInt(INFERENCE_DATA_RANGE_INFER_UNIQUE_TABLE.INFERRING_RANGE_VALUE_ID_COLUMN_NAME);
					inferredUniqueValueID = resultSet.getInt(INFERENCE_DATA_RANGE_INFER_UNIQUE_TABLE.INFERRED_UNIQUE_VALUE_ID_COLUMN_NAME);
					inferringRangeValue = null;
					inferredUniqueValue = null;
					
					for (AttributeValue inferringValue : tmpInferenceData.keySet()) {
						if (inferringValue.getAttributeValueID() == inferringRangeValueID) {
							inferringRangeValue = inferringValue;
							break;
						}
					}
					
					for (AttributeValue inferredValue : tmpInferenceData.values()) {
						if (inferredValue.getAttributeValueID() == inferredUniqueValueID) {
							inferredUniqueValue = inferredValue;
							break;
						}
					}
					
//					if (inferringRangeValue == null || inferredUniqueValue == null) { // this (should) never happens actually!
//					}
					
					inferenceData.put(inferringRangeValue, inferredUniqueValue);
				}
				rule.setInferenceData(inferenceData);
				
				resultSet.close();
				resultSet = null;
			} catch (Exception e) {
				throw new GenstarDAOException(e);
			}
			
		}
	}


	@Override
	public void populateInferenceData(final AttributeInferenceGenerationRule rule) throws GenstarDAOException {
		
		AbstractAttribute inferringAttribute = rule.getInferringAttribute();
		AbstractAttribute inferredAttribute = rule.getInferredAttribute();
		
		if (inferringAttribute instanceof UniqueValuesAttribute) {
			if (inferredAttribute instanceof UniqueValuesAttribute) {
				uniqueInferUniqueInferenceDataDAO.populateInferenceData(rule);
			} else { // inferedAttribute instanceof RangeValuesAttribute
				uniqueInferRangeInferenceDataDAO.populateInferenceData(rule);
			}
		} else { // inferringAttribute instanceof RangeValuesAttribute
			if (inferredAttribute instanceof UniqueValuesAttribute) {
				rangeInferUniqueInferenceDataDAO.populateInferenceData(rule);
			} else { // inferedAttribute instanceof RangeValuesAttribute
				rangeInferRangeInferenceDataDAO.populateInferenceData(rule);
			}
		} 
	}
	
}
