package ummisco.genstar.metamodel;

import ummisco.genstar.exception.GenstarException;

public class SingleRuleGenerator extends AbstractSyntheticPopulationGenerator implements ISingleRuleGenerator {
	
	private GenerationRule generationRule;
	
	
	public SingleRuleGenerator(final String generatorName) throws GenstarException {
		super(generatorName);
	}

	@Override
	public ISyntheticPopulation generate() throws GenstarException {
		ISyntheticPopulation population = new SyntheticPopulation(this, populationName, nbOfEntities);
		
		for (Entity e : population.getEntities()) { generationRule.generate(e); }
		
		return population;
	}

	@Override
	public void setGenerationRule(GenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' parameter can not be null"); }
		this.generationRule = generationRule;
	}

	@Override
	public GenerationRule getGenerationRule() throws GenstarException {
		return generationRule;
	}
}
