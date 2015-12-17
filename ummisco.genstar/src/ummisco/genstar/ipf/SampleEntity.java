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

	private Map<String, AttributeValue> attributeValuesOnEntity = Collections.EMPTY_MAP; // attribute name on data : attribute value from CSV file ( = attribute value on entity)
	
	private Map<String, SampleEntityPopulation> componentSampleEntityPopulations = Collections.EMPTY_MAP; // <population name, sample entity population>

	
	public SampleEntity(final SampleEntityPopulation population) throws GenstarException {
		if (population == null) { throw new GenstarException("Parameter 'population' can not be null"); }
		this.population = population;
	}
	
	public void setAttributeValuesOnEntity(final Map<String, AttributeValue> values) throws GenstarException {
		if (values == null) { throw new GenstarException("Parameter values can not be null"); }
		
		if (attributeValuesOnEntity == Collections.EMPTY_MAP) {
			attributeValuesOnEntity = new HashMap<String, AttributeValue>();
			for (AbstractAttribute attr : population.getAttributes()) { attributeValuesOnEntity.put(attr.getNameOnData(), null); }
		}
		
		for (String attrNameOnData : values.keySet()) {
			if (attributeValuesOnEntity.containsKey(attrNameOnData)) { attributeValuesOnEntity.put(attrNameOnData, values.get(attrNameOnData)); }
			else { throw new GenstarException(attrNameOnData + " is not recognized as an attribute"); }
			
			// TODO verify that the attribute can accept the value
		}
	}
	
	public Map<String, AttributeValue> getAttributeValuesOnEntity() {
		Map<String, AttributeValue> copy = new HashMap<String, AttributeValue>(attributeValuesOnEntity);
		return copy;
	}
	
	public boolean isMatched(final Map<String, AttributeValue> criteria) {
		if (criteria == null || criteria.isEmpty()) { return true; }
		
		AttributeValue criterionValue, sampleEntityValue;
		
		for (String criterionAttrNameOnData : criteria.keySet()) {
			sampleEntityValue = attributeValuesOnEntity.get(criterionAttrNameOnData);
			if (sampleEntityValue != null) {
				criterionValue = criteria.get(criterionAttrNameOnData);
				if (!criterionValue.isValueMatched(sampleEntityValue)) { return false; }
			}
		}
		
		return true;
	}
	
	public AttributeValue getAttributeValueOnEntity(final String attributeNameOndata) {
		return attributeValuesOnEntity.get(attributeNameOndata);
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
	
	public SampleEntityPopulation getPopulation() {
		return population;
	}
}
