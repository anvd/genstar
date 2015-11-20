package ummisco.genstar.metamodel;

import ummisco.genstar.exception.GenstarException;

public interface ISingleRuleGenerator extends ISyntheticPopulationGenerator {

	public abstract void setGenerationRule(final GenerationRule generationRule) throws GenstarException;
	
	public abstract GenerationRule getGenerationRule() throws GenstarException;
}
