package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import ummisco.genstar.dao.AttributeInferenceDataDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.INFERENCE_RANGE_ATTRIBUTE_DATA1_TABLE;
import ummisco.genstar.dao.derby.DBMS_Tables.INFERENCE_RANGE_ATTRIBUTE_DATA2_TABLE;
import ummisco.genstar.dao.derby.DBMS_Tables.INFERENCE_VALUE_ATTRIBUTE_DATA1_TABLE;
import ummisco.genstar.dao.derby.DBMS_Tables.INFERENCE_VALUE_ATTRIBUTE_DATA2_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.UniqueValuesAttribute;

public class DerbyAttributeInferenceDataDAO extends AbstractDerbyDAO implements AttributeInferenceDataDAO {
	
	private DerbyInferenceRangeAttributeData1DAO helperDAO1;
	private DerbyInferenceRangeAttributeData2DAO helperDAO2;
	private DerbyInferenceValueAttributeData1DAO helperDAO3;
	private DerbyInferenceValueAttributeData2DAO helperDAO4;

	public DerbyAttributeInferenceDataDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, "");
		
		helperDAO1 = new DerbyInferenceRangeAttributeData1DAO(daoFactory);
		helperDAO2 = new DerbyInferenceRangeAttributeData2DAO(daoFactory);
		helperDAO3 = new DerbyInferenceValueAttributeData1DAO(daoFactory);
		helperDAO4 = new DerbyInferenceValueAttributeData2DAO(daoFactory);
	}

	@Override
	public void createInferenceData(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException {
		
		// ask the corresponding "helper" DAO to create the inference data
		AbstractAttribute inferringAttribute = attributeInferenceGenerationRule.getInferringAttribute();
		AbstractAttribute inferredAttribute = attributeInferenceGenerationRule.getInferredAttribute();
		
		if (inferringAttribute instanceof UniqueValuesAttribute) {
			if (inferredAttribute instanceof UniqueValuesAttribute) {
				helperDAO1.createInferenceData(attributeInferenceGenerationRule);
			} else { // inferedAttribute instanceof RangeValuesAttribute
				helperDAO3.createInferenceData(attributeInferenceGenerationRule);
			}
		} else { // inferringAttribute instanceof RangeValuesAttribute
			if (inferredAttribute instanceof UniqueValuesAttribute) {
				helperDAO2.createInferenceData(attributeInferenceGenerationRule);
			} else { // inferedAttribute instanceof RangeValuesAttribute
				helperDAO4.createInferenceData(attributeInferenceGenerationRule);
			}
		}
	}

	
	private class DerbyInferenceRangeAttributeData1DAO extends AbstractDerbyDAO implements AttributeInferenceDataDAO {
		
		private PreparedStatement createInferenceRangeAttributeData1Stmt;
		

		public DerbyInferenceRangeAttributeData1DAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
			super(daoFactory, DBMS_Tables.INFERENCE_RANGE_ATTRIBUTE_DATA1_TABLE.TABLE_NAME);
			
			try {
				createInferenceRangeAttributeData1Stmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
						+ INFERENCE_RANGE_ATTRIBUTE_DATA1_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
						+ INFERENCE_RANGE_ATTRIBUTE_DATA1_TABLE.INFERRING_UNIQUE_VALUE_ID_COLUMN_NAME + ", "
						+ INFERENCE_RANGE_ATTRIBUTE_DATA1_TABLE.INFERRED_RANGE_VALUE_ID_COLUMN_NAME
						+ ") VALUES (?, ?, ?)");
				
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}

		@Override
		public void createInferenceData(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException {
			
			// create the inference data
			try {
				createInferenceRangeAttributeData1Stmt.setInt(1, attributeInferenceGenerationRule.getGenerationRuleID());
				
				Map<AttributeValue, AttributeValue> inferenceData = attributeInferenceGenerationRule.getInferenceData();
				for (AttributeValue inferringValue : inferenceData.keySet()) {
						createInferenceRangeAttributeData1Stmt.setInt(2, inferringValue.getAttributeValueID());
						createInferenceRangeAttributeData1Stmt.setInt(3, inferenceData.get(inferringValue).getAttributeValueID());
						createInferenceRangeAttributeData1Stmt.executeUpdate();
				}
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}
	}	
	
	
	private class DerbyInferenceRangeAttributeData2DAO extends AbstractDerbyDAO implements AttributeInferenceDataDAO {
		
		private PreparedStatement createInferenceRangeAttributeData2Stmt;
		
		
		public DerbyInferenceRangeAttributeData2DAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
			super(daoFactory, DBMS_Tables.INFERENCE_RANGE_ATTRIBUTE_DATA2_TABLE.TABLE_NAME);
			
			
			try {
				createInferenceRangeAttributeData2Stmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
						+ INFERENCE_RANGE_ATTRIBUTE_DATA2_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
						+ INFERENCE_RANGE_ATTRIBUTE_DATA2_TABLE.INFERRING_RANGE_VALUE_ID_COLUMN_NAME + ", "
						+ INFERENCE_RANGE_ATTRIBUTE_DATA2_TABLE.INFERRED_RANGE_VALUE_ID_COLUMN_NAME
						+ ") VALUES (?, ?, ?)");
				
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}

		@Override
		public void createInferenceData(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException {
			
			// create the inference data
			try {
				createInferenceRangeAttributeData2Stmt.setInt(1, attributeInferenceGenerationRule.getGenerationRuleID());
	
				Map<AttributeValue, AttributeValue> inferenceData = attributeInferenceGenerationRule.getInferenceData();
				for (AttributeValue inferringValue : inferenceData.keySet()) {
						createInferenceRangeAttributeData2Stmt.setInt(2, inferringValue.getAttributeValueID());
						createInferenceRangeAttributeData2Stmt.setInt(3, inferenceData.get(inferringValue).getAttributeValueID());
						createInferenceRangeAttributeData2Stmt.executeUpdate();
				}
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}
	}
	

	private class DerbyInferenceValueAttributeData1DAO extends AbstractDerbyDAO implements AttributeInferenceDataDAO {

		private PreparedStatement createInferenceValueAttributeData1Stmt;
		 
		
		public DerbyInferenceValueAttributeData1DAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
			super(daoFactory, DBMS_Tables.INFERENCE_VALUE_ATTRIBUTE_DATA1_TABLE.TABLE_NAME);
			
			try {
				createInferenceValueAttributeData1Stmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
						+ INFERENCE_VALUE_ATTRIBUTE_DATA1_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
						+ INFERENCE_VALUE_ATTRIBUTE_DATA1_TABLE.INFERRING_UNIQUE_VALUE_ID_COLUMN_NAME + ", "
						+ INFERENCE_VALUE_ATTRIBUTE_DATA1_TABLE.INFERRED_UNIQUE_VALUE_ID_COLUMN_NAME
						+ ") VALUES (?, ?, ?)");
				
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}

		@Override
		public void createInferenceData(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException {
			
			try {
				// create the inference data
				createInferenceValueAttributeData1Stmt.setInt(1, attributeInferenceGenerationRule.getGenerationRuleID());
				
				Map<AttributeValue, AttributeValue> inferenceData = attributeInferenceGenerationRule.getInferenceData();
				for (AttributeValue inferringValue : inferenceData.keySet()) {
						createInferenceValueAttributeData1Stmt.setInt(2, inferringValue.getAttributeValueID());
						createInferenceValueAttributeData1Stmt.setInt(3, inferenceData.get(inferringValue).getAttributeValueID());
						createInferenceValueAttributeData1Stmt.executeUpdate();
				}
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}
	}
	

	private class DerbyInferenceValueAttributeData2DAO extends AbstractDerbyDAO implements AttributeInferenceDataDAO {
		
		private PreparedStatement createInferenceValueAttributeData2Stmt;
		
		
		public DerbyInferenceValueAttributeData2DAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
			super(daoFactory, DBMS_Tables.INFERENCE_VALUE_ATTRIBUTE_DATA2_TABLE.TABLE_NAME);
			
			try {
				createInferenceValueAttributeData2Stmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
						+ INFERENCE_VALUE_ATTRIBUTE_DATA2_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
						+ INFERENCE_VALUE_ATTRIBUTE_DATA2_TABLE.INFERRING_RANGE_VALUE_ID_COLUMN_NAME + ", "
						+ INFERENCE_VALUE_ATTRIBUTE_DATA2_TABLE.INFERRED_UNIQUE_VALUE_ID_COLUMN_NAME
						+ ") VALUES (?, ?, ?)");
				
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}

		@Override
		public void createInferenceData(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException {
			
			try {
				// create the inference data
				createInferenceValueAttributeData2Stmt.setInt(1, attributeInferenceGenerationRule.getGenerationRuleID());
				
				Map<AttributeValue, AttributeValue> inferenceData = attributeInferenceGenerationRule.getInferenceData();
				for (AttributeValue inferringValue : inferenceData.keySet()) {
						createInferenceValueAttributeData2Stmt.setInt(2, inferringValue.getAttributeValueID());
						createInferenceValueAttributeData2Stmt.setInt(3, inferenceData.get(inferringValue).getAttributeValueID());
						createInferenceValueAttributeData2Stmt.executeUpdate();
				}
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}
	}
	
}
