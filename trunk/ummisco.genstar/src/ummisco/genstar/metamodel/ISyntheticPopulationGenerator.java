package ummisco.genstar.metamodel;

import java.util.NavigableSet;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;


public interface ISyntheticPopulationGenerator { // FIXME change class name -> ISingleSyntheticPopulationGenerator
	
	public abstract void setID(final int id);
	
	public abstract int getID();

	public abstract String getGeneratorName();
	
	public abstract void setPopulationName(final String populationName);
	
	public abstract String getPopulationName();
	
	public abstract int getNbOfEntities();
	
	public abstract void setNbOfEntities(final int nbOfEntities);

	public abstract int getNbOfRules();

	public abstract Set<AbstractAttribute> getAttributes();

	public abstract boolean containAttribute(final AbstractAttribute attribute);

	public abstract boolean containAttribute(final String attributeNameOnData);

	public abstract AbstractAttribute getAttribute(final String attributeNameOnData);

	public abstract void addAttribute(final AbstractAttribute attribute) throws GenstarException;

	public abstract void removeAttribute(final AbstractAttribute attribute);

	public abstract void appendGenerationRule(final GenerationRule rule);

	public abstract void insertGenerationRule(final GenerationRule rule, final int order);

	public abstract void removeGenerationRule(final GenerationRule rule);

	public abstract void changeGenerationRuleOrder(final GenerationRule rule, final int newOrder);

	public abstract int getGenerationRuleOrder(final GenerationRule rule);

	public abstract GenerationRule getGenerationRuleAtOrder(final int order);

	public abstract boolean containGenerationRule(final GenerationRule rule);

	public abstract boolean containGenerationRuleName(final String ruleName);

	public abstract NavigableSet<GenerationRule> getGenerationRules();

	public abstract ISyntheticPopulation generate() throws GenstarException;
}
