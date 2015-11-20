package ummisco.genstar.metamodel;

import java.util.NavigableSet;

public interface IMultipleRulesGenerator extends ISyntheticPopulationGenerator {
	
	public abstract void appendGenerationRule(final GenerationRule rule);

	public abstract void insertGenerationRule(final GenerationRule rule, final int order);

	public abstract void removeGenerationRule(final GenerationRule rule);

	public abstract void changeGenerationRuleOrder(final GenerationRule rule, final int newOrder);

	public abstract int getGenerationRuleOrder(final GenerationRule rule);

	public abstract GenerationRule getGenerationRuleAtOrder(final int order);

	public abstract boolean containGenerationRule(final GenerationRule rule);

	public abstract boolean containGenerationRuleName(final String ruleName);

	public abstract NavigableSet<GenerationRule> getGenerationRules();

	public abstract int getNbOfRules();
}
