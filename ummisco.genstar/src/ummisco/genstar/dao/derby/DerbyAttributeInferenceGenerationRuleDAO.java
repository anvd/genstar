package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import ummisco.genstar.dao.AttributeInferenceDataDAO;
import ummisco.genstar.dao.AttributeInferenceGenerationRuleDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;

public class DerbyAttributeInferenceGenerationRuleDAO extends AbstractDerbyDAO implements AttributeInferenceGenerationRuleDAO {
	
	private PreparedStatement createAttributeInferenceGenerationRuleStmt;
	
	private AttributeInferenceDataDAO  attributeInferenceDataDAO;
	
	public DerbyAttributeInferenceGenerationRuleDAO(DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE.TABLE_NAME);
		
		try {
			createAttributeInferenceGenerationRuleStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
					+ ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
					+ ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE.INFERRING_ATTRIBUTE_ID_COLUMN_NAME + ", "
					+ ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE.INFERRED_ATTRIBUTE_ID_COLUMN_NAME
					+ ") VALUES (?, ?, ?)");
			
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
		
		attributeInferenceDataDAO = daoFactory.getAttributeInferenceDataDAO();
		
	}

	@Override
	public AttributeInferenceGenerationRule findAttributeInferenceGenerationRule(final String attributeInferenceGenerationRuleName) throws GenstarDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AttributeInferenceGenerationRule findAttributeInferenceGenerationRuleID(final int attributeInferenceGenerationRuleID) throws GenstarDAOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createAttributeInferenceGenerationRule(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException {
		try {
			// firstly, create the attributeInferenceGenerationRule
			createAttributeInferenceGenerationRuleStmt.setInt(1, attributeInferenceGenerationRule.getGenerationRuleID());
			createAttributeInferenceGenerationRuleStmt.setInt(2, attributeInferenceGenerationRule.getInferringAttribute().getAttributeID());
			createAttributeInferenceGenerationRuleStmt.setInt(3, attributeInferenceGenerationRule.getInferredAttribute().getAttributeID());
			createAttributeInferenceGenerationRuleStmt.executeUpdate();
			
			
			// secondly, create the inference data
			attributeInferenceDataDAO.createInferenceData(attributeInferenceGenerationRule);
			
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public void updateAttributeInferenceGenerationRule(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAttributeInferenceGenerationRule(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAttributeInferenceGenerationRule(final int attributeInferenceGenerationRuleID) throws GenstarDAOException {
		// TODO Auto-generated method stub
		
	}

}
