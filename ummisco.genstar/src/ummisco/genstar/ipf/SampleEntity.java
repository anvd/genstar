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

	private Map<String, AttributeValue> attributeValuesOnEntity = Collections.EMPTY_MAP; // attribute name on entity : attribute value from CSV file ( = attribute value on entity)
	
	private Map<String, SampleEntityPopulation> componentSampleEntityPopulations = Collections.EMPTY_MAP; // <population name, sample entity population>

	
	public SampleEntity(final SampleEntityPopulation population) throws GenstarException {
		if (population == null) { throw new GenstarException("Parameter 'population' can not be null"); }
		this.population = population;
	}
	
	public void setAttributeValuesOnEntity(final Map<String, AttributeValue> values) throws GenstarException {
		if (values == null) { throw new GenstarException("Parameter values can not be null"); }
		
		if (attributeValuesOnEntity == Collections.EMPTY_MAP) {
			attributeValuesOnEntity = new HashMap<String, AttributeValue>();
			for (AbstractAttribute attr : population.getAttributes()) { attributeValuesOnEntity.put(attr.getNameOnEntity(), null); }
		}
		
		for (String attrNameOnEntity : values.keySet()) {
			if (attributeValuesOnEntity.containsKey(attrNameOnEntity)) {
				AbstractAttribute attribute = population.getAttributebyNameOnEntity(attrNameOnEntity);
				
				if (attribute != null) {
					AttributeValue value = values.get(attrNameOnEntity);
					if (!attribute.getValueClassOnEntity().equals(value.getClass())) { // verify that the attribute can accept the value
						throw new GenstarException("Incompatible value classes between " + attribute.getValueClassOnEntity().getName() + " and " + value.getClass().getName());
					}
					
					attributeValuesOnEntity.put(attrNameOnEntity, value); 
				} else { // should never happen
					throw new GenstarException(attrNameOnEntity + " is not recognized as an attribute");
				}
			} else { throw new GenstarException(attrNameOnEntity + " is not recognized as an attribute"); }
		}
	}
	
	public Map<String, AttributeValue> getAttributeValuesOnEntity() {
		Map<String, AttributeValue> copy = new HashMap<String, AttributeValue>(attributeValuesOnEntity);
		return copy;
	}
	
	public boolean isMatched(final Map<String, AttributeValue> criteria) throws GenstarException {
		if (criteria == null || criteria.isEmpty()) { return true; }
		
		AttributeValue criterionValue, sampleEntityValue;
		
		for (String attributeNameOnEntity : criteria.keySet()) {
			sampleEntityValue = attributeValuesOnEntity.get(attributeNameOnEntity);
			if (sampleEntityValue != null) {
				criterionValue = criteria.get(attributeNameOnEntity);
				if (!criterionValue.isValueMatched(sampleEntityValue)) { return false; }
			} else {
				int attrSize = 0;
				StringBuffer avaiableAttributesStr = new StringBuffer();
				for (String attr : attributeValuesOnEntity.keySet()) { 
					avaiableAttributesStr.append(attr);
					if (attrSize < attributeValuesOnEntity.size() - 1) avaiableAttributesStr.append(", ");
					attrSize++;
				}
				
				throw new GenstarException("Unrecognized attribute " + attributeNameOnEntity + " on SampleEntity, available attributes : " + avaiableAttributesStr.toString());
			}
		}
		
		return true;
	}
	
	public AttributeValue getAttributeValueOnEntity(final String attributeNameOnEntity) {
		return attributeValuesOnEntity.get(attributeNameOnEntity);
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
