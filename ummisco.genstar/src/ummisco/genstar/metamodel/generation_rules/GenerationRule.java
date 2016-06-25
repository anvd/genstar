package ummisco.genstar.metamodel.generation_rules;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.IWithAttributes;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;


public abstract class GenerationRule implements IWithAttributes {
	
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

	public abstract int getRuleTypeID();
	
	public abstract String getRuleTypeName();

}
