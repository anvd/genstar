package ummisco.genstar.dao;

import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public interface GenerationRuleDAO {
	
	public abstract GenerationRule findGenerationRule(final String generationRuleName) throws GenstarDAOException;
	
	public abstract GenerationRule findGenerationRuleByID(final int generationRuleID) throws GenstarDAOException;
	
	public abstract void deleteGenerationRule(final GenerationRule generationRule) throws GenstarDAOException;

	public abstract void deleteGenerationRule(final int generationRuleID) throws GenstarDAOException;
	
	
	public abstract void createGenerationRules(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException;
	
	public abstract void udateGenerationRules(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException;

}
