package ummisco.genstar.metamodel.generation_rules;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.sample_data.ISampleData;

public abstract class SampleBasedGenerationRule extends GenerationRule {
	
	protected int maxIterations = 3;

	public SampleBasedGenerationRule(final SampleBasedGenerator populationGenerator, final String name) throws GenstarException {
		super(populationGenerator, name);
	}

	@Override public SampleBasedGenerator getGenerator() {
		return (SampleBasedGenerator) super.getGenerator();
	}
	
	public abstract ISampleData getSampleData();

	public abstract IPopulation generate() throws GenstarException;

	public void setMaxIterations(final int maxIterations) {
		if (maxIterations <= 0) { throw new IllegalArgumentException("'maxIterations' parameter must be a positive integer."); }
		
		this.maxIterations = maxIterations;
	}
	
	public int getMaxIterations() {
		return maxIterations;
	}
}
