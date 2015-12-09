package ummisco.genstar.ipf;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class SampleEntity { // TODO in the future, remove this class, use Entity only
	
	private SampleEntityPopulation population;

	private Map<String, AttributeValue> attributeValues = Collections.EMPTY_MAP; // attribute name on data : attribute value (from CSV file)
	
	private Map<String, SampleEntityPopulation> componentSampleEntityPopulations = Collections.EMPTY_MAP; // <population name, sample entity population>

	
	public SampleEntity(final SampleEntityPopulation population) throws GenstarException {
		if (population == null) { throw new GenstarException("Parameter 'population' can not be null"); }
		this.population = population;
	}
	
	public void setAttributeValues(final Map<String, AttributeValue> values) throws GenstarException {
		if (values == null) { throw new GenstarException("Parameter values can not be null"); }
		
		if (attributeValues == Collections.EMPTY_MAP) {
			attributeValues = new HashMap<String, AttributeValue>();
			for (AbstractAttribute attr : population.getAttributes()) { attributeValues.put(attr.getNameOnData(), null); }
		}
		
		for (String attrNameOnData : values.keySet()) {
			if (attributeValues.containsKey(attrNameOnData)) { attributeValues.put(attrNameOnData, values.get(attrNameOnData)); }
		}
	}
	
	public Map<String, AttributeValue> getAttributeValues() {
		Map<String, AttributeValue> copy = new HashMap<String, AttributeValue>(attributeValues);
		return copy;
	}
	
	public boolean isMatched(final Map<String, AttributeValue> criteria) {
		if (criteria == null || criteria.isEmpty()) { return true; }
		
		AttributeValue criterionValue, sampleEntityValue;
		
		for (String criterionAttrNameOnData : criteria.keySet()) {
			sampleEntityValue = attributeValues.get(criterionAttrNameOnData);
			if (sampleEntityValue != null) {
				criterionValue = criteria.get(criterionAttrNameOnData);
				if (!criterionValue.isValueMatched(sampleEntityValue)) { return false; }
			}
		}
		
		return true;
	}
	
	public AttributeValue getAttributeValue(final String attributeNameOndata) {
		return attributeValues.get(attributeNameOndata);
	}
		
	public SampleEntityPopulation createComponentPopulation(final String populationName, final List<AbstractAttribute> attributes) throws GenstarException {
		if (populationName == null || populationName.isEmpty()) { throw new GenstarException("Parameter populationName can neither be null nor empty"); }
		if (attributes == null || attributes.isEmpty()) { throw new GenstarException("Parameter attributes can neither be null nor empty"); }
		
		if (componentSampleEntityPopulations == Collections.EMPTY_MAP) {
			componentSampleEntityPopulations = new HashMap<String, SampleEntityPopulation>();
		}
		
		if (componentSampleEntityPopulations.get(populationName) != null) {
			throw new GenstarException("Sample Entity Population " + populationName + " has already existed");
		}
		
		SampleEntityPopulation population = new SampleEntityPopulation(populationName, attributes);
		componentSampleEntityPopulations.put(populationName, population);
		
		return population;
	}
	
	public SampleEntityPopulation getComponentPopulation(final String populationName) {
		return componentSampleEntityPopulations.get(populationName);
	}
	
	public Map<String, SampleEntityPopulation> getComponentSampleEntityPopulations() {
		return new HashMap<String, SampleEntityPopulation>(componentSampleEntityPopulations);
	}
}
