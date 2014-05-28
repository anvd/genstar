package ummisco.genstar.metamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ummisco.genstar.exception.AttributeException;
import ummisco.genstar.exception.GenerationException;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.util.SharedInstances;

import com.google.common.collect.Sets;

public class FrequencyDistributionGenerationRule extends GenerationRule { // TODO  implements AttributeChangedListener
	
	private SortedMap<Integer, EnumerationValueAttribute> inputAttributes;
	
	private SortedMap<Integer, EnumerationValueAttribute> outputAttributes;
	
	private List<FrequencyDistributionElement> elements;
	

	// TODO table specification, should put in the UI class or here?
	// define : column attribute, row attribute
	// with three-attribute distribution -> use an attribute to separate the tables, i.e., table splitter
	
	
	public FrequencyDistributionGenerationRule(final ISyntheticPopulationGenerator population, final String name) throws GenstarException {
		super(population, name);
		
		this.inputAttributes = new TreeMap<Integer, EnumerationValueAttribute>();
		this.outputAttributes = new TreeMap<Integer, EnumerationValueAttribute>();
		this.elements = new ArrayList<FrequencyDistributionElement>();
	}
	
	public List<AbstractAttribute> getInputAttributes() {
		List<AbstractAttribute> retVal = new ArrayList<AbstractAttribute>();
		for (int order=0; order < inputAttributes.size(); order++) { retVal.add(inputAttributes.get(order)); }
		
		return retVal;
	}
	
	public List<AbstractAttribute> getOutputAttributes() {
		List<AbstractAttribute> retVal = new ArrayList<AbstractAttribute>();
		for (int order=0; order < outputAttributes.size(); order++) { retVal.add(outputAttributes.get(order)); }
		
		return retVal;
	}
	
	public AbstractAttribute getAttributeByDataAttributeName(final String dataAttributeName) {
		if (dataAttributeName == null || dataAttributeName.isEmpty()) { throw new IllegalArgumentException("'name' parameter can neither be null nor empty"); }
		
		for (AbstractAttribute iAttr : inputAttributes.values()) {
			if (iAttr.getNameOnData().equals(dataAttributeName)) { return iAttr; }
		}
		
		for (AbstractAttribute oAttr : outputAttributes.values()) {
			if (oAttr.getNameOnData().equals(dataAttributeName)) { return oAttr; }
		}
		
		return null;
	}
	
	public List<FrequencyDistributionElement> getDistributionElements() {
		return new ArrayList<FrequencyDistributionElement>(elements);
	}
	
	public List<FrequencyDistributionElement> findDistributionElements(final Map<EnumerationValueAttribute, AttributeValue> attributeValues) throws AttributeException {
		if (attributeValues == null || attributeValues.isEmpty()) { throw new IllegalArgumentException("'data' parameter can not be null or empty"); }
		
		List<FrequencyDistributionElement> retVal = new ArrayList<FrequencyDistributionElement>();
		for (FrequencyDistributionElement e : elements) {
			if (e.isMatchDataSet(attributeValues)) { retVal.add(e); }
		}
		
		return retVal;
	}
	
	private void verifyAddedAttributeValidity(final AbstractAttribute attribute) throws AttributeException {
		if (attribute == null) { throw new AttributeException("'attribute' parameter can not be null"); }
		
		if (!attribute.getPopulationGenerator().equals(this.getGenerator())) { throw new AttributeException("Can not add '" + attribute.getNameOnEntity() + "' attribute to '" + this.getName() + "' distribution."
				+ " Because of population different problem : attribute's population is " + attribute.getPopulationGenerator() + " while distribution's population is " + populationGenerator); }
		
		if (!attribute.getPopulationGenerator().containAttribute(attribute)) { throw new AttributeException("Can not add attribute the distribution because the attribute is not yet added to the population"); }
		
		if (inputAttributes.values().contains(attribute)) { throw new AttributeException("'inputAttributes' already contains '" + attribute.getNameOnEntity() + "' attribute."); }
		
		if (outputAttributes.values().contains(attribute)) { throw new AttributeException("'outputAttributes' already contains '" + attribute.getNameOnEntity() + "' attribute."); }
	}
	
	public boolean containInputAttributeWithName(final String name) {
		for (AbstractAttribute attr : inputAttributes.values()) {
			if (attr.getNameOnEntity().equals(name)) { return true; }
		}
		
		return false;
	}
	
	public boolean containOutputAttributeWithName(final String name) {
		for (AbstractAttribute attr : outputAttributes.values()) {
			if (attr.getNameOnEntity().equals(name)) { return true; }
		}
		
		return false;
	}
	
	public boolean containAttributeWithName(final String name) {
		return containInputAttributeWithName(name) || containOutputAttributeWithName(name);
	}
	
	public void appendInputAttribute(final EnumerationValueAttribute inputAttribute) throws AttributeException {
		verifyAddedAttributeValidity(inputAttribute);
		
		int index = inputAttributes.size();
		inputAttributes.put(index, inputAttribute);
	}
	
	public void insertInputAttribute(final EnumerationValueAttribute inputAttribute, final int order) throws AttributeException {
		verifyAddedAttributeValidity(inputAttribute);
		
		if (order < 0 || order > inputAttributes.size()) { throw new AttributeException("Can not insert input attribute : 'order' parameter must be in range[0, " + inputAttributes.size() + "]"); }
		
		
		int newIndex = 0;
		int oldIndex = 0;
		int size = inputAttributes.size();
		SortedMap<Integer, EnumerationValueAttribute> newInputAttributes = new TreeMap<Integer, EnumerationValueAttribute>();
		
		// process the first part
		while (oldIndex < order) {
			newInputAttributes.put(oldIndex, inputAttributes.get(oldIndex));
			oldIndex++;
		}
		
		// insert the input attribute
		newInputAttributes.put(order, inputAttribute);
		newIndex = order + 1;
		
		// process the second part
		while (oldIndex < size) {
			newInputAttributes.put(newIndex, inputAttributes.get(oldIndex));
			newIndex++;
			oldIndex++;
		}
		
		inputAttributes.clear();
		inputAttributes.putAll(newInputAttributes);
	}
	
	public void removeInputAttribute(final AbstractAttribute inputAttribute) {
		if (inputAttribute == null) { return; }
		
		int removedOrder = 0;
		boolean contain  = false;
		for (int order : inputAttributes.keySet()) {
			if (inputAttributes.get(order).equals(inputAttribute)) {
				removedOrder = order;
				contain = true;
				break;
			}
		}
		
		if (!contain) { return; }
		
		SortedMap<Integer, EnumerationValueAttribute> newInputAttributes = new TreeMap<Integer, EnumerationValueAttribute>();
		int oldIndex = 0;
		int size = inputAttributes.size();
		
		// process the first part
		while (oldIndex < removedOrder) {
			newInputAttributes.put(oldIndex, inputAttributes.get(oldIndex));
			oldIndex++;
		}
		
		// skip the removed input attribute
		int newIndex = removedOrder + 1;
		
		// process the second part
		while (newIndex < size) {
			newInputAttributes.put(oldIndex, inputAttributes.get(newIndex));
			oldIndex++;
			newIndex++;
		}
		
		inputAttributes.clear();
		inputAttributes.putAll(newInputAttributes);
		 
	}
	
	public void changeInputAttributeOrder(final EnumerationValueAttribute inputAttribute, final int newOrder) throws AttributeException {
		if (inputAttribute == null) { throw new IllegalArgumentException("'inputAttribute' parameter must not be null"); }
		int oldOrder = this.getInputAttributeOrder(inputAttribute);
		if (oldOrder == -1) { throw new IllegalArgumentException("'" + name + "' distribution doesn't contain " + inputAttribute.getNameOnEntity() + " as an input attribute."); }
		if (newOrder < 0 || (newOrder > inputAttributes.size() - 1)) { throw new IllegalArgumentException("'newOrder' parameter must be in range[0" + "," + (inputAttributes.size() - 1)  + "]"); }
		
		if (newOrder == oldOrder) { return; }
		
		removeInputAttribute(inputAttribute);
		insertInputAttribute(inputAttribute, newOrder);
	}
	
	public int getInputAttributeOrder(final AbstractAttribute inputAttribute) {
		if (inputAttribute == null) { throw new IllegalArgumentException("'inputAttribute' parameter can not be null"); }
		
		for (int order : inputAttributes.keySet()) {
			if (inputAttributes.get(order).equals(inputAttribute)) { return order; }
		}
		
		return -1;
	}
	
	public AbstractAttribute getInputAttributeAtOrder(final int order) throws AttributeException {
		if (order < 0 || order > (inputAttributes.size() - 1)) {
			throw new AttributeException("'order' parameter must be in range[0, " + (inputAttributes.size() - 1) + "]");
		}
		
		return inputAttributes.get(order);
	}
	
	public void appendOutputAttribute(final EnumerationValueAttribute outputAttribute) throws AttributeException {
		verifyAddedAttributeValidity(outputAttribute);

		int index = outputAttributes.size();
		outputAttributes.put(index, outputAttribute);
	}
	
	public void insertOutputAttribute(final EnumerationValueAttribute outputAttribute, final int order) throws AttributeException {
		verifyAddedAttributeValidity(outputAttribute);
		
		if (order < 0 || order > outputAttributes.size()) { throw new AttributeException("Can not insert input attribute : 'order' parameter must be in range[0, " + outputAttributes.size() + "]"); }
		
		
		int newIndex = 0;
		int oldIndex = 0;
		int size = outputAttributes.size();
		SortedMap<Integer, EnumerationValueAttribute> newOutputAttributes = new TreeMap<Integer, EnumerationValueAttribute>();
		
		// process the first part
		while (oldIndex < order) {
			newOutputAttributes.put(oldIndex, outputAttributes.get(oldIndex));
			oldIndex++;
		}
		
		// insert the input attribute
		newOutputAttributes.put(order, outputAttribute);
		newIndex = order + 1;
		
		// process the second part
		while (oldIndex < size) {
			newOutputAttributes.put(newIndex, outputAttributes.get(oldIndex));
			newIndex++;
			oldIndex++;
		}
		
		outputAttributes.clear();
		outputAttributes.putAll(newOutputAttributes);
	}
	
	public void removeOutputAttribute(final AbstractAttribute outputAttribute) {
		if (outputAttribute == null) { return; }
		
		int removedOrder = 0;
		boolean contain  = false;
		for (int order : outputAttributes.keySet()) {
			if (outputAttributes.get(order).equals(outputAttribute)) {
				removedOrder = order;
				contain = true;
				break;
			}
		}
		
		if (!contain) { return; }
		
		SortedMap<Integer, EnumerationValueAttribute> newOutputAttributes = new TreeMap<Integer, EnumerationValueAttribute>();
		int oldIndex = 0;
		int size = outputAttributes.size();
		
		// process the first part
		while (oldIndex < removedOrder) {
			newOutputAttributes.put(oldIndex, outputAttributes.get(oldIndex));
			oldIndex++;
		}
		
		// skip the removed output attribute
		int newIndex = removedOrder + 1;
		
		// process the second part
		while (newIndex < size) {
			newOutputAttributes.put(oldIndex, outputAttributes.get(newIndex));
			oldIndex++;
			newIndex++;
		}
		
		outputAttributes.clear();
		outputAttributes.putAll(newOutputAttributes);
	}
	
	public void changeOutputAttributeOrder(final EnumerationValueAttribute outputAttribute, final int newOrder) throws AttributeException {
		if (outputAttribute == null) { throw new IllegalArgumentException("'outputAttribute' parameter must not be null"); }
		int oldOrder = this.getOutputAttributeOrder(outputAttribute);
		if (oldOrder == -1) { throw new IllegalArgumentException("'" + name + "' distribution doesn't contain " + outputAttribute.getNameOnEntity() + " as an output attribute."); }
		if (newOrder < 0 || (newOrder > outputAttributes.size() - 1)) { throw new IllegalArgumentException("'newOrder' parameter must be in range[0" + "," + (outputAttributes.size() - 1)  + "]"); }
		
		if (newOrder == oldOrder) { return; }
		
		removeOutputAttribute(outputAttribute);
		insertOutputAttribute(outputAttribute, newOrder);
	}
	
	public int getOutputAttributeOrder(final AbstractAttribute outputAttribute) {
		if (outputAttribute == null) { throw new IllegalArgumentException("'outputAttribute' parameter can not be null"); }
		
		for (int order : outputAttributes.keySet()) {
			if (outputAttributes.get(order).equals(outputAttribute)) { return order; }
		}
		
		return -1;
	}
	
	public AbstractAttribute getOutputAttributeAtOrder(final int order) {
		if (order < 0 || order > (outputAttributes.size() - 1)) {
			throw new IllegalArgumentException("'order' parameter must be in range[0, " + (outputAttributes.size() - 1) + "]");
		}
		
		return outputAttributes.get(order);
	}
	
	
	private Map<EnumerationValueAttribute, AttributeValue> buildAttributeValueMap(final List<EnumerationValueAttribute> attributes, List<AttributeValue> values) throws AttributeException {
		Map<EnumerationValueAttribute, AttributeValue> retVal = new HashMap<EnumerationValueAttribute, AttributeValue>();
		
		List<EnumerationValueAttribute> copyAttributes = new ArrayList<EnumerationValueAttribute>();
		copyAttributes.addAll(attributes);
		EnumerationValueAttribute concernedAttr;
		for (AttributeValue v : values) {
			concernedAttr = null;
			for (EnumerationValueAttribute enumAttr : copyAttributes) {
				if (enumAttr.contains(v)) { 
					retVal.put(enumAttr, v); 
					concernedAttr = enumAttr;
					break;
				}
			}
			copyAttributes.remove(concernedAttr);
		}
		
		return retVal;
	}

	public void generateFrequencyElements() throws AttributeException { // FIXME make this method be automatically invoke when an attribute is added or removed!
		
		elements.clear(); // do clean up
		
		List<Set<AttributeValue>> attributesPossibleValues = new ArrayList<Set<AttributeValue>>();
		
		for (EnumerationValueAttribute inputAttr : inputAttributes.values()) {
			attributesPossibleValues.add(inputAttr.values());
		}
		
		for (EnumerationValueAttribute outputAttr : outputAttributes.values()) {
			attributesPossibleValues.add(outputAttr.values());
		}
		
		List<EnumerationValueAttribute> allAttributes = new ArrayList<EnumerationValueAttribute>();
		allAttributes.addAll(inputAttributes.values());
		allAttributes.addAll(outputAttributes.values());
		Set<List<AttributeValue>> cartesianSet = Sets.cartesianProduct(attributesPossibleValues);
		for (List<AttributeValue> catesian : cartesianSet) {
			elements.add(new FrequencyDistributionElement(buildAttributeValueMap(allAttributes, catesian)));
		}
	}
	
	// TODO same-purpose-method as setFrequency(Map<String, AttributeValue> attributeValues, int frequency) ?
	public void setFrequency(final Map<EnumerationValueAttribute, AttributeValue> attributeValues, final int frequency) throws AttributeException {
		if (attributeValues == null || attributeValues.isEmpty()) { throw new AttributeException("'attributeValues' parameter can be neither null nor empty"); }
		if (frequency < 0) { throw new AttributeException("'frequency' must not be negative"); }
		
		for (FrequencyDistributionElement e : this.findDistributionElements(attributeValues)) {
			e.setFrequency(frequency);
		}
	}

	public void generate(final Entity entity) throws GenerationException {
		if (entity == null) { throw new GenerationException("'entity' parameter can not be null"); }
		
		List<FrequencyDistributionElement> matchingDistributionElements = new ArrayList<FrequencyDistributionElement>();
		for (FrequencyDistributionElement e : elements) {
			if (e.isMatchEntity(inputAttributes.values(), entity)) { matchingDistributionElements.add(e); }
		}
		
		int total = 0;
		for (FrequencyDistributionElement de : matchingDistributionElements) { total += de.getFrequency(); }
		
		EnumerationValueAttribute outputAttribute;
		AttributeValue attributeValue;
		int currentTotal = 0;
		
		// no matching distribution elements -> use attribute's default value
		if (total == 0) {
			for (int order=0; order < outputAttributes.size(); order++) {
				outputAttribute = outputAttributes.get(order);
				try {
					entity.putAttributeValue(new EntityAttributeValue(outputAttribute, outputAttribute.getDefaultValue()));
				} catch (final AttributeException e1) {
					throw new GenerationException(e1);
				}
			}
			
			return;
		}
		
		int selectedTotal = SharedInstances.RandomNumberGenerator.nextInt(total);
		for (FrequencyDistributionElement de : matchingDistributionElements) { 
			currentTotal += de.getFrequency();
			
			if (currentTotal >= selectedTotal) {
				for (int order=0; order<outputAttributes.size(); order++) {
					outputAttribute = outputAttributes.get(order);
					attributeValue = de.getAttributeValue(outputAttribute);
					
					try {
						entity.putAttributeValue(new EntityAttributeValue(outputAttribute, attributeValue));
					} catch (final AttributeException e1) {
						throw new GenerationException(e1);
					}
				}
				
				break;
			}
		} 
	}
	
}
