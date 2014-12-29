package ummisco.genstar.dao;

import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public interface FrequencyDistributionGenerationRuleDAO {

	public abstract FrequencyDistributionGenerationRule findFrequencyDistributionGenerationRuleByName(final String frequencyDistributionGenerationRuleName) throws GenstarDAOException;
	
	public abstract FrequencyDistributionGenerationRule findFrequencyDistributionGenerationRuleByID(final int frequencyDistributionGenerationRuleID) throws GenstarDAOException;
	
	public abstract void createFrequencyDistributionGenerationRule(final FrequencyDistributionGenerationRule frequencyDistributionGenerationRule) throws GenstarDAOException;
	
	public abstract void updateFrequencyDistributionGenerationRule(final FrequencyDistributionGenerationRule frequencyDistributionGenerationRule) throws GenstarDAOException;
	
	public abstract void deleteFrequencyDistributionGenerationRule(final FrequencyDistributionGenerationRule frequencyDistributionGenerationRule) throws GenstarDAOException;

	public abstract void deleteFrequencyDistributionGenerationRule(final int frequencyDistributionGenerationRuleID) throws GenstarDAOException;

	public abstract FrequencyDistributionGenerationRule findRule(final ISyntheticPopulationGenerator populationGenerator, final int generationRuleID, final String name) throws GenstarDAOException;
}