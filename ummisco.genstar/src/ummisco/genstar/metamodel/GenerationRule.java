package ummisco.genstar.metamodel;

import ummisco.genstar.exception.GenstarException;

public abstract class GenerationRule {
	
	protected int generationRuleID = -1;
	
	protected ISyntheticPopulationGenerator populationGenerator;
	
	protected String name;
	
	protected int order = -1;
	
	
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
	
	public void setOrder(final int order) {
		this.order = order;
	}
	
	public int getOrder() {
		return order;
	}
	
	public abstract int getRuleType();

	public abstract void generate(final Entity entity) throws GenstarException;
}