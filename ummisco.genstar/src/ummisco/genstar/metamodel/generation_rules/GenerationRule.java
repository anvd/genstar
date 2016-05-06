package ummisco.genstar.metamodel.generation_rules;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.IWithAttributes;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.util.PersistentObject;


public abstract class GenerationRule implements IWithAttributes {
	
	protected int generationRuleID = PersistentObject.NEW_OBJECT_ID;
	
	protected ISyntheticPopulationGenerator populationGenerator;
	
	protected String name;
	
	
	public GenerationRule(final ISyntheticPopulationGenerator populationGenerator, final String name) throws GenstarException {
		if (populationGenerator == null) { throw new GenstarException("'populationGenerator' parameter can not be null"); }
		if (name == null || name.trim().length() == 0) { throw new GenstarException("'name' parameter can not be null or empty"); }

		this.populationGenerator = populationGenerator;
		this.name = name;
	}
	
	public void setGenerationRuleID(final int generationRuleID) {
		this.generationRuleID = generationRuleID;
	}
	
	public int getGenerationRuleID() {
		return generationRuleID;
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
