package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import ummisco.genstar.dao.AttributeValuesFrequencyDAO;
import ummisco.genstar.dao.FrequencyDistributionGenerationRuleDAO;
import ummisco.genstar.dao.InputOutputAttributeDAO;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public class DerbyFrequencyDistributionGenerationRuleDAO extends AbstractDerbyDAO implements FrequencyDistributionGenerationRuleDAO {
	
	private InputOutputAttributeDAO inputOutputAttributeDAO;

	private AttributeValuesFrequencyDAO attributeValuesFrequencyDAO;
	

	public DerbyFrequencyDistributionGenerationRuleDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, "");
		
		inputOutputAttributeDAO = daoFactory.getInputOutputAttributeDAO();
		attributeValuesFrequencyDAO = daoFactory.getAttributeValuesFrequencyDAO();
	}

	@Override
	public FrequencyDistributionGenerationRule findFrequencyDistributionGenerationRuleByName(final String frequencyDistributionGenerationRuleName) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public FrequencyDistributionGenerationRule findFrequencyDistributionGenerationRuleByID(final int frequencyDistributionGenerationRuleID) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void createFrequencyDistributionGenerationRule(final FrequencyDistributionGenerationRule frequencyDistributionGenerationRule) throws GenstarDAOException {
		inputOutputAttributeDAO.createInputOutputAttributes(frequencyDistributionGenerationRule);
		attributeValuesFrequencyDAO.createAttributeValuesFrequecies(frequencyDistributionGenerationRule);
	}

	@Override
	public void updateFrequencyDistributionGenerationRule(final FrequencyDistributionGenerationRule frequencyDistributionGenerationRule) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void deleteFrequencyDistributionGenerationRule(final FrequencyDistributionGenerationRule frequencyDistributionGenerationRule) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void deleteFrequencyDistributionGenerationRule(final int frequencyDistributionGenerationRuleID) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public FrequencyDistributionGenerationRule findRule(final ISyntheticPopulationGenerator populationGenerator,
			final int generationRuleID, final String name) throws GenstarDAOException {
		
		try {
			FrequencyDistributionGenerationRule rule = new FrequencyDistributionGenerationRule(populationGenerator, name);
			rule.setGenerationRuleID(generationRuleID);
			
			// populate input & output attributes
			inputOutputAttributeDAO.populateInputOutputAttributes(rule);
			rule.generateAttributeValuesFrequencies();
			
			// populate frequency distribution elements
			attributeValuesFrequencyDAO.populateAttributeValuesFrequencies(rule);

			return rule;
		} catch (GenstarException e) {
			throw new GenstarDAOException(e);
		}
	}

}
