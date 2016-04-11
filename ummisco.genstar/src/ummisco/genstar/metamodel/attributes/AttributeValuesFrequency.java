package ummisco.genstar.metamodel.attributes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.util.PersistentObject;

public class AttributeValuesFrequency {
	
	private int attributeValuesFrequencyID = PersistentObject.NEW_OBJECT_ID;
	
	private Map<AbstractAttribute, AttributeValue> attributeValuesOnData; // TODO change to Map<String, AttributeValue> <attribute name on data, attribute value>
	
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
			if (!attribute.containsInstanceOfAttributeValue(attributeValues.get(attribute))) {
				throw new GenstarException("Some attribute values don't belong to the corresponding attributes.");
			}
		}
		
		this.attributeValuesOnData = new HashMap<AbstractAttribute, AttributeValue>(attributeValues);
//		this.attributes = new HashSet<AbstractAttribute>(attributeValues.keySet());
	}
	
	public AttributeValue getAttributeValueOnData(final AbstractAttribute attribute) {
		return attributeValuesOnData.get(attribute);
	}
	
	public Map<AbstractAttribute, AttributeValue> getAttributeValuesOnData() {
		Map<AbstractAttribute, AttributeValue> copy = new HashMap<AbstractAttribute, AttributeValue>();
		copy.putAll(attributeValuesOnData);
		
		return copy;
	}
	
	// TODO remove or refactor
//	public Map<String, AttributeValue> getAttributeValuesWithNamesOnEntityAsKey() {
//		Map<String, AttributeValue> result = new HashMap<String, AttributeValue>();
//		
//		for (AbstractAttribute attr : attributeValuesOnData.keySet()) {
//			result.put(attr.getNameOnEntity(), attributeValuesOnData.get(attr));
//		}
//		
//		return result;
//	}

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
		
		/*
		// build a list of EntityAttributeValues according to inputAttributes
		EntityAttributeValue entityAttributeValue;
		Map<AbstractAttribute, EntityAttributeValue> entityAttributeValues = new HashMap<AbstractAttribute, EntityAttributeValue>();
		for (AbstractAttribute attribute : inputAttributes) {
			entityAttributeValue = entity.getEntityAttributeValue(attribute);
			
			if (entityAttributeValue == null) { return false; } // or throw exception?
			
			entityAttributeValues.put(attribute, entityAttributeValue);
		}
		
		// attribute values on distribution element
		AttributeValue attributeValueOnData;
		Map<AbstractAttribute, AttributeValue> inputAttributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (AbstractAttribute attribute : inputAttributes) {
			attributeValueOnData = attributeValuesOnData.get(attribute);
			
			if (attributeValueOnData == null) { return false; } // or throw exception?
			inputAttributeValues.put(attribute, attributeValueOnData); 
		}
		
		if (entityAttributeValues.size() != inputAttributeValues.size()) { return false; }
		
		for (AbstractAttribute attribute : entityAttributeValues.keySet()) {
			if (!entityAttributeValues.get(attribute).isAttributeValueOnEntityMatched(inputAttributeValues.get(attribute))) { return false; }
		}
		*/
		
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

	public int getID() {
		return attributeValuesFrequencyID;
	}

	public void setID(final int attributeValuesFrequencyID) {
		this.attributeValuesFrequencyID = attributeValuesFrequencyID;
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
