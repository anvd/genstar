package ummisco.genstar.metamodel;

import ummisco.genstar.exception.GenstarException;

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
