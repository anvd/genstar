package ummisco.genstar.metamodel.attributes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.population.Entity;

public class AttributeValuesFrequency {
	
	private Map<AbstractAttribute, AttributeValue> attributeValuesOnData;
	
	private int frequency = 0;
	
	private Set<AbstractAttribute> cachedAttributes;
	
	
	public AttributeValuesFrequency(final Map<AbstractAttribute, AttributeValue> attributeValues, final int frequency) throws GenstarException {
		this(attributeValues);
		setFrequency(frequency);
	}
	
	public AttributeValuesFrequency(final Map<AbstractAttribute, AttributeValue> attributeValues) 
		throws GenstarException {
		if (attributeValues == null || attributeValues.size() == 0) { throw new IllegalArgumentException("'data' parameter can not be null or empty"); }
		
		for (AbstractAttribute attribute : attributeValues.keySet()) {
			if (!(attribute.containInstanceOfAttributeValue(attributeValues.get(attribute)))) {
				throw new GenstarException("Some attribute values don't belong to the corresponding attributes.");
			}
		}
		
		this.attributeValuesOnData = new HashMap<AbstractAttribute, AttributeValue>(attributeValues);
	}
	
	public AttributeValue getAttributeValueOnData(final AbstractAttribute attribute) {
		return attributeValuesOnData.get(attribute);
	}
	
	public Map<AbstractAttribute, AttributeValue> getAttributeValuesOnData() {
		Map<AbstractAttribute, AttributeValue> copy = new HashMap<AbstractAttribute, AttributeValue>();
		copy.putAll(attributeValuesOnData);
		
		return copy;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public void increaseFrequency() {
		frequency += 1;
	}

	public void setFrequency(final int frequency) {
		if (frequency < 0) { throw new IllegalArgumentException("'frequency' parameter must be positive"); }
		
		this.frequency = frequency;
	}
	
	public boolean matchEntity(final Collection<? extends AbstractAttribute> inputAttributes, final Entity entity) throws GenstarException {
		if (inputAttributes == null || entity == null) { throw new IllegalArgumentException("Neither 'inputAttributes' nor 'entity' parameter can be null"); }
		
		EntityAttributeValue entityAttributeValue;
		AttributeValue attributeValueOnData;
		for (AbstractAttribute attribute : inputAttributes) {
			entityAttributeValue = entity.getEntityAttributeValue(attribute);
			attributeValueOnData = attributeValuesOnData.get(attribute);
			
			if (entityAttributeValue == null || attributeValueOnData == null) { return false; }
			if (!entityAttributeValue.isAttributeValueOnDataMatched(attributeValueOnData)) { return false; }
		}
		
		
		return true;
	}
	
	public boolean matchEntity(final Entity entity) throws GenstarException {
		if (entity == null) { throw new GenstarException("Parameter entity can not be null"); }
		if (cachedAttributes == null) { cachedAttributes = new HashSet<AbstractAttribute>(attributeValuesOnData.keySet()); }
		
		return this.matchEntity(cachedAttributes, entity);
	}
	
	public boolean matchAttributeValuesOnData(final Map<AbstractAttribute, ? extends AttributeValue> otherAttributeValuesOnData) {
		if (otherAttributeValuesOnData == null || otherAttributeValuesOnData.isEmpty()) { throw new IllegalArgumentException("'otherAttributeValuesOnData' parameter can not be null or empty"); }
		
		AttributeValue attributeValue;
		for (AbstractAttribute attribute : otherAttributeValuesOnData.keySet()) {
			attributeValue = attributeValuesOnData.get(attribute);
			if (attributeValue == null || attributeValue.compareTo(otherAttributeValuesOnData.get(attribute)) != 0) { return false; }
		}
		
		return true;
	}

	@Override
	public String toString() {
		StringBuffer retVal = new StringBuffer("AttributeValuesFrequency with : ");
		for (AbstractAttribute attr : attributeValuesOnData.keySet()) {
			retVal.append("[ attribute : " + attr.getNameOnData() + ", value : " + attributeValuesOnData.get(attr) + "], ");
		}
		
		retVal.append("WITH frequency = " + frequency);
		
		return retVal.toString();
	}
	
	public Set<AbstractAttribute> getAttributes() {
		return new HashSet<AbstractAttribute>(attributeValuesOnData.keySet());
	}
}
