package ummisco.genstar.metamodel.sample_data;

import ummisco.genstar.metamodel.population.IPopulation;


public abstract class AbstractSampleData implements ISampleData {
	
	protected String populationName;

	protected IPopulation sampleEntityPopulation;

	@Override
	public IPopulation getSampleEntityPopulation() {
		return sampleEntityPopulation;
	}
}
