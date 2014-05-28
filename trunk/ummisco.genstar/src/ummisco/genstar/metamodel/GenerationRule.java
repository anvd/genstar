package ummisco.genstar.metamodel;

import ummisco.genstar.exception.GenerationException;
import ummisco.genstar.exception.GenstarException;

public abstract class GenerationRule {
	
	protected ISyntheticPopulationGenerator populationGenerator;
	
	protected String name;
	
	
	public GenerationRule(final ISyntheticPopulationGenerator populationGenerator, final String name) throws GenstarException {
		if (populationGenerator == null) { throw new GenstarException("'populationGenerator' parameter can not be null"); }
		if (name == null || name.trim().length() == 0) { throw new GenstarException("'name' parameter can not be null or empty"); }

		this.populationGenerator = populationGenerator;
		this.name = name;
	}
	
	public ISyntheticPopulationGenerator getGenerator() {
		return populationGenerator;
	}
	
	public String getName() {
		return name;
	}

	public abstract void generate(final Entity entity) throws GenerationException;
}
