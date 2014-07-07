package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ummisco.genstar.dao.AttributeInferenceDataDAO;
import ummisco.genstar.dao.AttributeInferenceGenerationRuleDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public class DerbyAttributeInferenceGenerationRuleDAO extends AbstractDerbyDAO implements AttributeInferenceGenerationRuleDAO {
	
	private PreparedStatement createAttributeInferenceGenerationRuleStmt;
	
	private PreparedStatement findRuleStmt;
	
	private AttributeInferenceDataDAO  attributeInferenceDataDAO;
	
	public DerbyAttributeInferenceGenerationRuleDAO(DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE.TABLE_NAME);
		
		try {
			createAttributeInferenceGenerationRuleStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
					+ ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
					+ ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE.INFERRING_ATTRIBUTE_ID_COLUMN_NAME + ", "
					+ ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE.INFERRED_ATTRIBUTE_ID_COLUMN_NAME
					+ ") VALUES (?, ?, ?)");
			
			
			findRuleStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " + ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + " = ?"); 
			
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

	@Override
	public AttributeInferenceGenerationRule findRule(final ISyntheticPopulationGenerator populationGenerator,
			final int generationRuleID, final String name) throws GenstarDAOException {
		AttributeInferenceGenerationRule rule = null;
		
		try {
			findRuleStmt.setInt(1, generationRuleID);
			ResultSet resultSet = findRuleStmt.executeQuery();
			
			if (resultSet.next()) {
				int inferringAttributeID = resultSet.getInt(ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE.INFERRING_ATTRIBUTE_ID_COLUMN_NAME);
				int inferredAttributeID = resultSet.getInt(ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE.INFERRED_ATTRIBUTE_ID_COLUMN_NAME);
				AbstractAttribute inferringAttribute = null, inferredAttribute = null;
				
				for (AbstractAttribute attr : populationGenerator.getAttributes()) {
					
					if (attr.getAttributeID() == inferringAttributeID) { inferringAttribute = attr; }
					if (attr.getAttributeID() == inferredAttributeID) { inferredAttribute = attr; }
					
					if (inferringAttribute != null && inferringAttribute != null) { break;}
				}

				rule = new AttributeInferenceGenerationRule(populationGenerator, name, inferringAttribute, inferredAttribute);
				rule.setGenerationRuleID(generationRuleID);
				
				// populate inference data
				attributeInferenceDataDAO.populateInferenceData(rule);
			}
			
			resultSet.close();
			resultSet = null;
		} catch (Exception e) {
			throw new GenstarDAOException(e);
		}
		
		return rule;
	}

}
