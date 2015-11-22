package ummisco.genstar.metamodel;

import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import ummisco.genstar.exception.GenstarException;

public class MultipleRulesGenerator extends AbstractSyntheticPopulationGenerator implements IMultipleRulesGenerator {
	
	protected SortedMap<Integer, GenerationRule> generationRules; // rule order begins by 0

	public MultipleRulesGenerator(final String generatorName, final int nbOfEntities) throws GenstarException {
		this(generatorName, nbOfEntities, "no-name population");
	}
	
	public MultipleRulesGenerator(final String generatorName, final int nbOfEntities, final String populationName) throws GenstarException {
		super(generatorName);
		
		if (nbOfEntities <= 0) { throw new IllegalArgumentException("'nbOfEntities' must be a positive integer"); }
		if (populationName == null || populationName.trim().length() == 0) { throw new GenstarException("'populationName' parameter can neither be null nor empty"); }
		
		this.populationName = populationName.trim();
		this.nbOfEntities = nbOfEntities;
		this.generationRules = new TreeMap<Integer, GenerationRule>();
	}


	@Override public int getNbOfRules() {
		return generationRules.size();
	}

	private void verifyRuleValidity(final GenerationRule rule) {
		if (rule == null) { throw new IllegalArgumentException("'rule' parameter can not be null"); }

		if (!this.equals(rule.getGenerator())) { throw new IllegalArgumentException("Can not add '" + rule.getName() + "' to '" + this.getGeneratorName() + "' population."
				+ " Because of population difference problem : rule's population is " + rule.getGenerator() + " is different from " + this); }
		
		if (generationRules.values().contains(rule)) { throw new IllegalArgumentException("Can not add '" + rule.getName() + "' to '" + this.getGeneratorName() + "' population."
				+ " Because this population has already contained the generation rule."); }

		if (this.containGenerationRuleName(rule.getName())) {
			throw new IllegalArgumentException("Can not add '" + rule.getName() + "' to '" + this.getGeneratorName() + "' population."
					+ " Because this population has already contained a generation rule with '" + rule.getName() + "' as name.");
		}
	}
	
	@Override public void appendGenerationRule(final GenerationRule rule) {
		verifyRuleValidity(rule);
		
		int index = generationRules.size();
		setRuleOrder(rule, index);
		generationRules.put(index, rule);
	}
	
	@Override public void insertGenerationRule(final GenerationRule rule, final int order) {
		verifyRuleValidity(rule);
		
		if (order < 0 || order > generationRules.size()) { throw new IllegalArgumentException("Can not insert the generation rule : 'order' parameter must be in range[0, " + generationRules.size() + "]"); }
		
		int newIndex = 0;
		int oldIndex = 0;
		int size = generationRules.size();
		SortedMap<Integer, GenerationRule> newRules = new TreeMap<Integer, GenerationRule>();
		
		// process the first part
		while (oldIndex < order) {
			newRules.put(oldIndex, generationRules.get(oldIndex));
			oldIndex++;
		}
		
		// insert the rule
		newRules.put(order, rule);
		setRuleOrder(rule, order);
		newIndex = order + 1;
		
		// process the second part
		GenerationRule secondPartRule;
		while (oldIndex < size) {
			secondPartRule = generationRules.get(oldIndex);
			setRuleOrder(secondPartRule, newIndex);
			newRules.put(newIndex, secondPartRule);
			newIndex++;
			oldIndex++;
		}
		
		generationRules.clear();
		generationRules.putAll(newRules);
	}
	
	@Override public void removeGenerationRule(final GenerationRule rule) {
		if (rule == null) { return; }
		
		int removedOrder = 0;
		boolean contain  = false;
		for (int order : generationRules.keySet()) {
			if (generationRules.get(order).equals(rule)) {
				removedOrder = order;
				contain = true;
				break;
			}
		}
		
		if (!contain) { return; }
		
		SortedMap<Integer, GenerationRule> newRules = new TreeMap<Integer, GenerationRule>();
		int oldIndex = 0;
		int size = generationRules.size();
		
		// process the first part
		while (oldIndex < removedOrder) {
			newRules.put(oldIndex, generationRules.get(oldIndex));
			oldIndex++;
		}
		
		// skip the removed rule
		int newIndex = removedOrder + 1;
		
		// process the second part
		GenerationRule secondPartRule;
		while (newIndex < size) {
			secondPartRule = generationRules.get(newIndex);
			newRules.put(oldIndex, secondPartRule);
			this.setRuleOrder(secondPartRule, oldIndex);
			oldIndex++;
			newIndex++;
		}
		
		generationRules.clear();
		generationRules.putAll(newRules);
	}
	
	@Override public void changeGenerationRuleOrder(final GenerationRule rule, final int newOrder) {
		if (rule == null) { throw new IllegalArgumentException("'rule' parameter must not be null"); }
		int oldOrder = this.getGenerationRuleOrder(rule);
		if (oldOrder == -1) { throw new IllegalArgumentException("'" + generatorName + "' population doesn't contain '" + rule.getName() + "' generation rule"); }
		if (newOrder < 0 || (newOrder > generationRules.size() - 1)) { throw new IllegalArgumentException("'newOrder' parameter must be in range[0" + "," + (generationRules.size() - 1)  + "]"); }
		
		if (newOrder == oldOrder) { return; }
		
		removeGenerationRule(rule);
		insertGenerationRule(rule, newOrder);
	}
	
	@Override public int getGenerationRuleOrder(final GenerationRule rule) {
		if (rule == null) { throw new IllegalArgumentException("'rule' parameter can not be null"); }
		
		for (int order : generationRules.keySet()) {
			if (generationRules.get(order).equals(rule)) { return order; }
		}
		
		return -1;
	}
	
	@Override public GenerationRule getGenerationRuleAtOrder(final int order) {
		if (order < 0 || order > (generationRules.size() - 1)) {
			throw new IllegalArgumentException("'order' parameter must be in range[0, " + (generationRules.size() - 1) + "]");
		}
		
		return generationRules.get(order);
	}
	
	@Override public boolean containGenerationRule(final GenerationRule rule) {
		if (rule == null) { return false; }
		
		for (int order : generationRules.keySet()) {
			if (generationRules.get(order).equals(rule)) { return true; }
		}
		
		return false;
	}
	
	@Override public boolean containGenerationRuleName(final String ruleName) {
		for (GenerationRule d : generationRules.values()) {
			if (d.getName().equals(ruleName)) { return true; }
		}
		
		return false;
	}
	
	@Override public NavigableSet<GenerationRule> getGenerationRules() {
		NavigableSet<GenerationRule> retVal = new TreeSet<GenerationRule>();
		retVal.addAll(generationRules.values());
		
		return retVal;
	}

	@Override public ISyntheticPopulation generate() throws GenstarException {
		ISyntheticPopulation population = new SyntheticPopulation(this, populationName, nbOfEntities);
		
		for (Entity e : population.getEntities()) {
			for (int order=0; order<generationRules.size(); order++) { generationRules.get(order).generate(e); }
			// FIXME optimization : 
			// 		Hypothesis : 
			//			if first rule is a FrequencyDistributionGenerationRule contains only output attributes
			//				and if it AttributeValues mirrors exactly real data's value (i.e., the sum of AttributeValues == nbOfEntities)
			//				then an optimization can be made to improve the exactness of the generation output
			// 			The optimization is as follows : generate
		}
		
		return population;
	}

	private void setRuleOrder(final GenerationRule rule, final int order) {
		rule.setOrder(order);
	}
}
