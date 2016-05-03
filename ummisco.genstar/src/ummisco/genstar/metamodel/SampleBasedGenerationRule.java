package ummisco.genstar.metamodel;

import ummisco.genstar.exception.GenstarException;

public abstract class SampleBasedGenerationRule extends GenerationRule {

	public SampleBasedGenerationRule(final SampleBasedGenerator populationGenerator, final String name) throws GenstarException {
		super(populationGenerator, name);
	}

	@Override public SampleBasedGenerator getGenerator() {
		return (SampleBasedGenerator) super.getGenerator();
	}

	public abstract IPopulation generate() throws GenstarException;
}
