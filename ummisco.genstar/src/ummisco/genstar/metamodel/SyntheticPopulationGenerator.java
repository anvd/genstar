package ummisco.genstar.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import ummisco.genstar.exception.GenstarException;


public class SyntheticPopulationGenerator implements ISyntheticPopulationGenerator {
	
	private int id = -1;
	
	private String name;
	
	private SortedMap<String, AbstractAttribute> attributes; // <attribute name on data, attribute>
	
	private SortedMap<Integer, GenerationRule> generationRules;
	
	private int nbOfEntities;
	
	
	public SyntheticPopulationGenerator(final String name, final int nbOfEntities) throws GenstarException {
		if (name == null || name.trim().length() == 0) { throw new GenstarException("'name' parameter can neither be null nor empty"); }
		if (nbOfEntities <= 0) { throw new IllegalArgumentException("'nbOfEntities' must be a positive integer"); }
		
		this.name = name.trim();
		this.nbOfEntities = nbOfEntities;
		this.attributes = new TreeMap<String, AbstractAttribute>();
		this.generationRules = new TreeMap<Integer, GenerationRule>();
	}
	
	@Override public void setID(final int id) {
		this.id = id;
	}
	
	@Override public int getID() {
		return id;
	}
	
	@Override public String getName() {
		return name;
	}
	
	@Override public int getNbOfEntities() {
		return nbOfEntities;
	}
	
	@Override public void setNbOfEntities(final int nbOfEntities) {
		if (nbOfEntities <= 0) { throw new IllegalArgumentException("'nbOfEntities' must be a positive integer"); }
		this.nbOfEntities = nbOfEntities;
	}

	@Override public int getNbOfRules() {
		return generationRules.size();
	}

	@Override public Collection<AbstractAttribute> getAttributes() {
		return new ArrayList<AbstractAttribute>(attributes.values());
	}
	
	@Override public boolean containAttribute(final AbstractAttribute attribute) {
		if (attribute == null) { return false; }
		
		return attributes.containsValue(attribute);
	}
	
	@Override public boolean containAttribute(final String dataAttributeName) {
		if (dataAttributeName == null) { return false; }
		
		return attributes.keySet().contains(dataAttributeName);
	}
	
	@Override public AbstractAttribute getAttribute(final String attributeNameOnData) {
		if (attributeNameOnData == null) { throw new IllegalArgumentException("'dataAttributeName' parameter can not be null"); }
		return attributes.get(attributeNameOnData);
	}
	
	@Override public void addAttribute(final AbstractAttribute attribute) throws GenstarException {
		if (attribute == null) { throw new GenstarException("'attribute' parameter can not be null"); }
		if (containAttribute(attribute.getNameOnData())) { throw new GenstarException("'" + name + "' population already contains '" + attribute.getNameOnData() + "' attribute."); }
		if (!attribute.getPopulationGenerator().equals(this)) { throw new GenstarException("Can not add '" + attribute.getNameOnData() + "' attribute to '" + this.getName() + 
				"' population. Because attribute's population is " + attribute.getPopulationGenerator() + " (different from " + this + " population)."); }
		
		
		attributes.put(attribute.getNameOnData(), attribute);
	}
	
	@Override public void removeAttribute(final AbstractAttribute attribute) {
		throw new UnsupportedOperationException("Not yet implemented");
		// TODO implement it!
	}
	
	private void verifyRuleValidity(final GenerationRule rule) {
		if (rule == null) { throw new IllegalArgumentException("'rule' parameter can not be null"); }

		if (!this.equals(rule.getGenerator())) { throw new IllegalArgumentException("Can not add '" + rule.getName() + "' to '" + this.getName() + "' population."
				+ " Because of population difference problem : rule's population is " + rule.getGenerator() + " is different from " + this); }
		
		if (generationRules.values().contains(rule)) { throw new IllegalArgumentException("Can not add '" + rule.getName() + "' to '" + this.getName() + "' population."
				+ " Because this population has already contained the generation rule."); }

		if (this.containGenerationRuleName(rule.getName())) {
			throw new IllegalArgumentException("Can not add '" + rule.getName() + "' to '" + this.getName() + "' population."
					+ " Because this population has already contained a generation rule with '" + rule.getName() + "' as name.");
		}
	}
	
	@Override public void appendGenerationRule(final GenerationRule rule) {
		verifyRuleValidity(rule);
		
		int index = generationRules.size();
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
		newIndex = order + 1;
		
		// process the second part
		while (oldIndex < size) {
			newRules.put(newIndex, generationRules.get(oldIndex));
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
		while (newIndex < size) {
			newRules.put(oldIndex, generationRules.get(newIndex));
			oldIndex++;
			newIndex++;
		}
		
		generationRules.clear();
		generationRules.putAll(newRules);
	}
	
	@Override public void changeGenerationRuleOrder(final GenerationRule rule, final int newOrder) {
		if (rule == null) { throw new IllegalArgumentException("'rule' parameter must not be null"); }
		int oldOrder = this.getGenerationRuleOrder(rule);
		if (oldOrder == -1) { throw new IllegalArgumentException("'" + name + "' population doesn't contain '" + rule.getName() + "' generation rule"); }
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
	
	@Override public List<GenerationRule> getGenerationRules() {
		List<GenerationRule> retVal = new ArrayList<GenerationRule>();
		for (int order=0; order<generationRules.size(); order++) { retVal.add(generationRules.get(order)); }
		
		return retVal;
	}

	@Override public ISyntheticPopulation generate() throws GenstarException {
		ISyntheticPopulation population = new SyntheticPopulation(name, nbOfEntities);
		
		for (Entity e : population.getEntities()) {
			for (int order=0; order<generationRules.size(); order++) { generationRules.get(order).generate(e); }
		}
		
		return population;
	}
}
