package ummisco.genstar.metamodel.generation_rules;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.Entity;

public abstract class SampleFreeGenerationRule extends GenerationRule implements Comparable<SampleFreeGenerationRule>  {

	protected int order = -1;

	
	public SampleFreeGenerationRule(final SampleFreeGenerator populationGenerator, final String name) throws GenstarException {
		super(populationGenerator, name);
	}
	
	@Override public SampleFreeGenerator getGenerator() {
		return (SampleFreeGenerator) super.getGenerator();
	}

	
	public void setOrder(final int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
	
	@Override public int compareTo(final SampleFreeGenerationRule other) {
		return this.order - other.order;
	}

	public abstract void generate(final Entity entity) throws GenstarException;
}
