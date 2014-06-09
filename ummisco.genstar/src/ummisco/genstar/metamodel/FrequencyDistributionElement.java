package ummisco.genstar.metamodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;

public class FrequencyDistributionElement { // TODO change to AttributeValueFrequency
	
	private int frequencyDistributionElementID = -1;
	
	private FrequencyDistributionGenerationRule frequencyDistributionGenerationRule = null;

	private Map<AbstractAttribute, AttributeValue> attributeValues;
	
	private int frequency = 0;
	
	
	public FrequencyDistributionElement(final FrequencyDistributionGenerationRule frequencyDistributionGenerationRule, final Map<AbstractAttribute, AttributeValue> data) 
		throws GenstarException {
		if (frequencyDistributionGenerationRule == null) { throw new IllegalArgumentException("'frequencyDistributionGenerationRule' can not be null"); }
		if (data == null || data.size() == 0) { throw new IllegalArgumentException("'data' parameter can not be null or empty"); }
		
		for (AbstractAttribute attribute : data.keySet()) {
			if (!frequencyDistributionGenerationRule.containAttribute(attribute)) {
				throw new GenstarException("'frequencyDistributionGenerationRule' doesn't contain one or some attributes of 'data'");
			}
			
			if (!attribute.contains(data.get(attribute))) {
				throw new GenstarException("Some attribute values don't belong to the corresponding attributes.");
			}
		}
		
		
		this.attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		this.attributeValues.putAll(data); // TODO : validation?
	}
	
	
	public AttributeValue getAttributeValue(final AbstractAttribute attribute) {
		return attributeValues.get(attribute);
	}
	
	public Map<AbstractAttribute, AttributeValue> getAttributeValues() {
		return attributeValues;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(final int frequency) {
		if (frequency < 0) { throw new IllegalArgumentException("'frequency' parameter must be positive"); }
		
		this.frequency = frequency;
	}
	
	public boolean isMatchEntity(final Collection<? extends AbstractAttribute> inputAttributes, final Entity entity) {
		if (inputAttributes == null || entity == null) { throw new IllegalArgumentException("Neither 'inputAttributes' nor 'entity' parameter can be null"); }
		
		// attribute values on entity
		EntityAttributeValue entityAttributeValue;
		Map<AbstractAttribute, EntityAttributeValue> entityAttributeValues = new HashMap<AbstractAttribute, EntityAttributeValue>();
		for (AbstractAttribute attribute : inputAttributes) {
			entityAttributeValue = entity.getEntityAttributeValue(attribute.getNameOnEntity());
			
			if (entityAttributeValue == null) { return false; } // or throw exception?
			
			entityAttributeValues.put(attribute, entityAttributeValue);
		}
		
		// attribute values on distribution element
		AttributeValue attributeValueOnData;
		Map<AbstractAttribute, AttributeValue> inputAttributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (AbstractAttribute attribute : inputAttributes) {
			attributeValueOnData = attributeValues.get(attribute); // TODO ok?
			
			if (attributeValueOnData == null) { return false; } // or throw exception?
			inputAttributeValues.put(attribute, attributeValueOnData); 
		}
		
		if (entityAttributeValues.size() != inputAttributeValues.size()) { return false; }
		
		for (AbstractAttribute attribute : entityAttributeValues.keySet()) {
			if (!entityAttributeValues.get(attribute).isValueMatch(inputAttributeValues.get(attribute))) { return false; }
		}
		
		
		return true;
	}
	
	public boolean isMatchDataSet(final Map<AbstractAttribute, ? extends AttributeValue> dataSet) {
		if (dataSet == null || dataSet.isEmpty()) { throw new IllegalArgumentException("'dataSet' parameter can not be null or empty"); }
		
		AttributeValue attributeValue;
		for (AbstractAttribute attribute : dataSet.keySet()) {
			attributeValue = attributeValues.get(attribute);
			
			if (attributeValue == null || !attributeValue.equals(dataSet.get(attribute))) { return false; } // TODO : attention : "equals" may not work as expected
		}
		
		
		return true;
	}

	public int getFrequencyDistributionElementID() {
		return frequencyDistributionElementID;
	}


	public void setFrequencyDistributionElementID(
			int frequencyDistributionElementID) {
		this.frequencyDistributionElementID = frequencyDistributionElementID;
	}


	public FrequencyDistributionGenerationRule getFrequencyDistributionGenerationRule() {
		return frequencyDistributionGenerationRule;
	}


	public void setFrequencyDistributionGenerationRule(
			FrequencyDistributionGenerationRule frequencyDistributionGenerationRule) {
		this.frequencyDistributionGenerationRule = frequencyDistributionGenerationRule;
	}


	@Override
	public String toString() {
		StringBuffer retVal = new StringBuffer("DistributionElement with : ");
		for (AbstractAttribute attr : attributeValues.keySet()) {
			retVal.append("[ attribute : " + attr.getNameOnData() + ", value : " + attributeValues.get(attr) + "], ");
		}
		
		return retVal.toString();
	}
}
