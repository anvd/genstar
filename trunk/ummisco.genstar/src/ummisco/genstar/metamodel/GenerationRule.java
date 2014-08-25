package ummisco.genstar.metamodel;

import java.util.List;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.util.PersistentObject;

public abstract class GenerationRule implements Comparable<GenerationRule> {
	
	protected int generationRuleID = PersistentObject.NEW_OBJECT_ID;
	
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
	
	@Override public int compareTo(final GenerationRule other) {
		return this.order - other.order;
	}
	
//	public abstract Set<AbstractAttribute> getAttributes();
	public abstract List<AbstractAttribute> getAttributes();

	public abstract int getRuleTypeID();

	public abstract void generate(final Entity entity) throws GenstarException;

	public abstract AbstractAttribute findAttributeByNameOnData(final String attributeNameOnData);
}
