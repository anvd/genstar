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
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import ummisco.genstar.dao.AttributeInferenceGenerationRuleDAO;
import ummisco.genstar.dao.FrequencyDistributionGenerationRuleDAO;
import ummisco.genstar.dao.GenerationRuleDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.GENERATION_RULE_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.util.PersistentObject;

public class DerbyGenerationRuleDAO extends AbstractDerbyDAO implements GenerationRuleDAO {
	
	private PreparedStatement createGenerationRulesStmt, populateGenerationRulesStmt, updateGenerationRulesStmt;
	
	
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
			
			populateGenerationRulesStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ GENERATION_RULE_TABLE.POPULATION_GENERATOR_ID_COLUMN_NAME + " = ? ORDER BY " + GENERATION_RULE_TABLE.RULE_ORDER_COLUMN_NAME);
			
			updateGenerationRulesStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ GENERATION_RULE_TABLE.POPULATION_GENERATOR_ID_COLUMN_NAME + " = ? FOR UPDATE"
					, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
		
		attributeInferenceGenerationRuleDAO = daoFactory.getAttributeInferenceGenerationRuleDAO();
		frequencyDistributionGenerationRuleDAO = daoFactory.getFrequencyDistributionGenerationRuleDAO();
	}
	
	
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
		
		try {
			internalCreateGenerationRules(syntheticPopulationGenerator.getID(), syntheticPopulationGenerator.getGenerationRules());
		} catch (Exception e) {
			if (e instanceof GenstarDAOException) { throw (GenstarDAOException)e; }
			throw new GenstarDAOException(e);
		}
	}
	
	private void internalCreateGenerationRules(final int generatorID, final NavigableSet<GenerationRule> rules) throws Exception {
		// 1. insert data to the GenerationRule table
		// 2. delegate the sub-sequence "insert" task to the sub-DAO classes according to the type of GenerationRule

		createGenerationRulesStmt.setInt(1, generatorID);

		for (GenerationRule rule : rules) {
			createGenerationRulesStmt.setString(2, rule.getName());
			createGenerationRulesStmt.setInt(3, rule.getOrder());
			createGenerationRulesStmt.setInt(4, rule.getRuleTypeID());
			
			createGenerationRulesStmt.executeUpdate();
			
			// retrieve the ID of the newly created generation rule
			ResultSet generatedKeySet = createGenerationRulesStmt.getGeneratedKeys();
			if (generatedKeySet.next()) { rule.setGenerationRuleID(generatedKeySet.getInt(1)); }
			generatedKeySet.close();
			generatedKeySet = null;
			
			if (rule.getRuleTypeID() == AttributeInferenceGenerationRule.ATTRIBUTE_INFERENCE_GENERATION_RULE_ID) {
				attributeInferenceGenerationRuleDAO.createAttributeInferenceGenerationRule( (AttributeInferenceGenerationRule) rule); 
			} else {
				frequencyDistributionGenerationRuleDAO.createFrequencyDistributionGenerationRule( (FrequencyDistributionGenerationRule) rule);
			}
		}
	}

	@Override
	public void updateGenerationRules(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException {
		try {
			updateGenerationRulesStmt.setInt(1, syntheticPopulationGenerator.getID());
			ResultSet resultSet = updateGenerationRulesStmt.executeQuery();
			
			List<Integer> generationRuleIDsInDBMS = new ArrayList<Integer>();
			while (resultSet.next()) { generationRuleIDsInDBMS.add(resultSet.getInt(GENERATION_RULE_TABLE.GENERATION_RULE_ID_COLUMN_NAME)); }
			
			// 1. build the list of generationRuleIDs to create, update and delete
			int ruleID;
			NavigableSet<GenerationRule> rulesToCreate = new TreeSet<GenerationRule>();
			Map<Integer, GenerationRule> rulesToUpdate = new HashMap<Integer, GenerationRule>();
			for (GenerationRule rule : syntheticPopulationGenerator.getGenerationRules()) {
				ruleID = rule.getGenerationRuleID();
				
				if (ruleID == PersistentObject.NEW_OBJECT_ID) {
					rulesToCreate.add(rule);
				} else {
					rulesToUpdate.put(ruleID, rule);
				}
			}
			
			
			// 2. remove "deleted" generation rules or update existing generation rules
			GenerationRule rule;
			resultSet.beforeFirst();
			Set<Integer> ruleIDsToUpdate = new HashSet<Integer>(rulesToUpdate.keySet());
			while (resultSet.next()) {
				ruleID = resultSet.getInt(GENERATION_RULE_TABLE.GENERATION_RULE_ID_COLUMN_NAME);
				
				if (ruleIDsToUpdate.contains(ruleID)) { // update the rule
					rule = rulesToUpdate.get(ruleID);
					
					// firstly, update the rule
					resultSet.updateString(GENERATION_RULE_TABLE.NAME_COLUMN_NAME, rule.getName());
					resultSet.updateInt(GENERATION_RULE_TABLE.RULE_ORDER_COLUMN_NAME, rule.getOrder());
					resultSet.updateRow();
					
					// secondly, update the rule data
					if (rule.getRuleTypeID() == AttributeInferenceGenerationRule.ATTRIBUTE_INFERENCE_GENERATION_RULE_ID) {
						attributeInferenceGenerationRuleDAO.updateAttributeInferenceGenerationRule( (AttributeInferenceGenerationRule) rule); 
					} else {
						frequencyDistributionGenerationRuleDAO.updateFrequencyDistributionGenerationRule( (FrequencyDistributionGenerationRule) rule);
					}
				} else { // delete the rule
					resultSet.deleteRow();
				}
			}
			
			
			// 3. create new generation rules
			internalCreateGenerationRules(syntheticPopulationGenerator.getID(), rulesToCreate);
			
			resultSet.close();
			resultSet = null;
		} catch (Exception e) {
			if (e instanceof GenstarDAOException) { throw (GenstarDAOException)e; }
			throw new GenstarDAOException(e);
		}
	}


	@Override
	public void populateGenerationRules(final ISyntheticPopulationGenerator populationGenerator) throws GenstarDAOException {
		try {
			int generationRuleID;
			String name;
			int ruleOrder;
			int ruleType;
			
			populateGenerationRulesStmt.setInt(1, populationGenerator.getID());
			ResultSet resultSet = populateGenerationRulesStmt.executeQuery();
			while (resultSet.next()) {
				generationRuleID = resultSet.getInt(GENERATION_RULE_TABLE.GENERATION_RULE_ID_COLUMN_NAME);
				name = resultSet.getString(GENERATION_RULE_TABLE.NAME_COLUMN_NAME);
				ruleOrder = resultSet.getInt(GENERATION_RULE_TABLE.RULE_ORDER_COLUMN_NAME);
				ruleType = resultSet.getInt(GENERATION_RULE_TABLE.RULE_TYPE_COLUMN_NAME);
				
				if (ruleType == FrequencyDistributionGenerationRule.FREQUENCY_DISTRIBUTION_GENERATION_RULE_ID) {
					
					FrequencyDistributionGenerationRule rule = frequencyDistributionGenerationRuleDAO.findRule(populationGenerator, generationRuleID, name);
					populationGenerator.appendGenerationRule(rule);
				} else { // AttributeInferenceGenerationRule
					
					 AttributeInferenceGenerationRule rule = attributeInferenceGenerationRuleDAO.findRule(populationGenerator, generationRuleID, name);
					 populationGenerator.appendGenerationRule(rule);
				}
			}
			
			// TODO populate generation rules
			
			resultSet.close();
			resultSet = null;
		} catch (Exception e) {
			if (e instanceof GenstarDAOException) { throw (GenstarDAOException)e; }
			throw new GenstarDAOException(e);
		}
	}

}
