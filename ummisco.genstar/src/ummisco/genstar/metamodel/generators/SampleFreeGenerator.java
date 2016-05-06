package ummisco.genstar.metamodel.generators;

import java.util.NavigableSet;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.generation_rules.GenerationRule;
import ummisco.genstar.metamodel.generation_rules.SampleFreeGenerationRule;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.Population;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.sample_free.FrequencyDistributionGenerationRule;

public class SampleFreeGenerator extends AbstractSyntheticPopulationGenerator {
	
	protected SortedMap<Integer, SampleFreeGenerationRule> generationRules; // rule order begins by 0

	public SampleFreeGenerator(final String generatorName, final int nbOfEntities) throws GenstarException {
		this(generatorName, nbOfEntities, "no-name population");
	}
	
	public SampleFreeGenerator(final String generatorName, final int nbOfEntities, final String populationName) throws GenstarException {
		super(generatorName);
		
		if (nbOfEntities <= 0) { throw new IllegalArgumentException("'nbOfEntities' must be a positive integer"); }
		if (populationName == null || populationName.trim().length() == 0) { throw new GenstarException("'populationName' parameter can neither be null nor empty"); }
		
		this.populationName = populationName.trim();
		this.nbOfEntities = nbOfEntities;
		this.generationRules = new TreeMap<Integer, SampleFreeGenerationRule>();
	}


	public int getNbOfRules() {
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
	
	public void appendGenerationRule(final SampleFreeGenerationRule rule) {
		verifyRuleValidity(rule);
		
		int index = generationRules.size();
		setRuleOrder(rule, index);
		generationRules.put(index, rule);
	}
	
	public void insertGenerationRule(final SampleFreeGenerationRule rule, final int order) {
		verifyRuleValidity(rule);
		
		if (order < 0 || order > generationRules.size()) { throw new IllegalArgumentException("Can not insert the generation rule : 'order' parameter must be in range[0, " + generationRules.size() + "]"); }
		
		int newIndex = 0;
		int oldIndex = 0;
		int size = generationRules.size();
		SortedMap<Integer, SampleFreeGenerationRule> newRules = new TreeMap<Integer, SampleFreeGenerationRule>();
		
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
		SampleFreeGenerationRule secondPartRule;
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
	
	public void removeGenerationRule(final FrequencyDistributionGenerationRule rule) {
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
		
		SortedMap<Integer, SampleFreeGenerationRule> newRules = new TreeMap<Integer, SampleFreeGenerationRule>();
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
		SampleFreeGenerationRule secondPartRule;
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
	
	public void changeGenerationRuleOrder(final FrequencyDistributionGenerationRule rule, final int newOrder) {
		if (rule == null) { throw new IllegalArgumentException("'rule' parameter must not be null"); }
		int oldOrder = this.getGenerationRuleOrder(rule);
		if (oldOrder == -1) { throw new IllegalArgumentException("'" + generatorName + "' population doesn't contain '" + rule.getName() + "' generation rule"); }
		if (newOrder < 0 || (newOrder > generationRules.size() - 1)) { throw new IllegalArgumentException("'newOrder' parameter must be in range[0" + "," + (generationRules.size() - 1)  + "]"); }
		
		if (newOrder == oldOrder) { return; }
		
		removeGenerationRule(rule);
		insertGenerationRule(rule, newOrder);
	}
	
	public int getGenerationRuleOrder(final FrequencyDistributionGenerationRule rule) {
		if (rule == null) { throw new IllegalArgumentException("'rule' parameter can not be null"); }
		
		for (int order : generationRules.keySet()) {
			if (generationRules.get(order).equals(rule)) { return order; }
		}
		
		return -1;
	}
	
	public SampleFreeGenerationRule getGenerationRuleAtOrder(final int order) {
		if (order < 0 || order > (generationRules.size() - 1)) {
			throw new IllegalArgumentException("'order' parameter must be in range[0, " + (generationRules.size() - 1) + "]");
		}
		
		return generationRules.get(order);
	}
	
	public boolean containGenerationRule(final FrequencyDistributionGenerationRule rule) {
		if (rule == null) { return false; }
		
		for (int order : generationRules.keySet()) {
			if (generationRules.get(order).equals(rule)) { return true; }
		}
		
		return false;
	}
	
	public boolean containGenerationRuleName(final String ruleName) {
		for (GenerationRule d : generationRules.values()) {
			if (d.getName().equals(ruleName)) { return true; }
		}
		
		return false;
	}
	
	public NavigableSet<SampleFreeGenerationRule> getGenerationRules() {
		NavigableSet<SampleFreeGenerationRule> retVal = new TreeSet<SampleFreeGenerationRule>();
		retVal.addAll(generationRules.values());
		
		return retVal;
	}

	@Override public IPopulation generate() throws GenstarException {
		IPopulation population = new Population(PopulationType.SYNTHETIC_POPULATION, populationName, this.getAttributes());
		population.createEntities(nbOfEntities);
		
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

	private void setRuleOrder(final SampleFreeGenerationRule rule, final int order) {
		rule.setOrder(order);
	}
}
