package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ummisco.genstar.dao.AttributeInferenceGenerationRuleDAO;
import ummisco.genstar.dao.FrequencyDistributionGenerationRuleDAO;
import ummisco.genstar.dao.GenerationRuleDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.GENERATION_RULE_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public class DerbyGenerationRuleDAO extends AbstractDerbyDAO implements GenerationRuleDAO {
	
	private PreparedStatement createGenerationRulesStmt;
	
	
	private AttributeInferenceGenerationRuleDAO attributeInferenceGenerationRuleDAO;
	
	private FrequencyDistributionGenerationRuleDAO frequencyDistributionGenerationRuleDAO;
	

	public DerbyGenerationRuleDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.GENERATION_RULE_TABLE.TABLE_NAME);
		
		try {
			createGenerationRulesStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " (" +  GENERATION_RULE_TABLE.POPULATION_GENERATOR_ID_COLUMN_NAME + ", "
					+ GENERATION_RULE_TABLE.NAME_COLUMN_NAME  + ", " 
					+ GENERATION_RULE_TABLE.RULE_ORDER_COLUMN_NAME + ", "
					+ GENERATION_RULE_TABLE.RULE_TYPE_COLUMN_NAME
					+ ") VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
		
		attributeInferenceGenerationRuleDAO = daoFactory.getAttributeInferenceGenerationRuleDAO();
		frequencyDistributionGenerationRuleDAO = daoFactory.getFrequencyDistributionGenerationRuleDAO();
	}

	// FIXME GenerationRule is an abstract class!!! Are these methods necessary? 
	
	
	@Override
	public GenerationRule findGenerationRule(final String generationRuleName) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public GenerationRule findGenerationRuleByID(final int generationRuleID) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void deleteGenerationRule(final GenerationRule generationRule) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void deleteGenerationRule(final int generationRuleID) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	
	@Override
	public void createGenerationRules(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException {
		// TODO
		// 1. insert data to the GenerationRule table
		// 2. delegate the sub-sequence "insert" task to the sub-DAO classes according to the type of GenerationRule
		try {
			for (GenerationRule rule : syntheticPopulationGenerator.getGenerationRules()) {
				createGenerationRulesStmt.setInt(1, rule.getGenerator().getID());
				createGenerationRulesStmt.setString(2, rule.getName());
				createGenerationRulesStmt.setInt(3, rule.getOrder());
				createGenerationRulesStmt.setInt(4, rule.getRuleType());
				
				createGenerationRulesStmt.executeUpdate();
				
				// retrieve the ID of the newly created generation rule
				ResultSet generatedKeySet = createGenerationRulesStmt.getGeneratedKeys();
				if (generatedKeySet.next()) { rule.setGenerationRuleID(generatedKeySet.getInt(1)); }
				generatedKeySet.close();
				generatedKeySet = null;
				
				if (rule.getRuleType() == AttributeInferenceGenerationRule.ATTRIBUTE_INFERENCE_GENERATION_RULE_ID) {
					attributeInferenceGenerationRuleDAO.createAttributeInferenceGenerationRule( (AttributeInferenceGenerationRule) rule); 
				} else {
					frequencyDistributionGenerationRuleDAO.createFrequencyDistributionGenerationRule( (FrequencyDistributionGenerationRule) rule);
				}
			}
		} catch (final Exception e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public void udateGenerationRules(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
