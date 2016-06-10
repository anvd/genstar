package ummisco.genstar.metamodel.generators;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.generation_rules.SampleBasedGenerationRule;
import ummisco.genstar.metamodel.population.IPopulation;

@Deprecated
public class SampleBasedGenerator extends AbstractSyntheticPopulationGenerator {
	
	private SampleBasedGenerationRule generationRule;
	
	
	public SampleBasedGenerator(final String generatorName) throws GenstarException {
		super(generatorName);
	}

	@Override
	public IPopulation generate() throws GenstarException {
		return generationRule.generate();
	}

	public void setGenerationRule(final SampleBasedGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' parameter can not be null"); }
		this.generationRule = generationRule;
	}

	public SampleBasedGenerationRule getGenerationRule() throws GenstarException {
		return generationRule;
	}
}
