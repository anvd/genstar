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
	
	private Map<AbstractAttribute, AttributeValue> attributeValues; // TODO change to Map<String, AttributeValue> <attribute name on data, attribute value>
	
	private int frequency = 0;
	
	private Set<AbstractAttribute> attributes;
	
	
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
		
		this.attributeValues = new HashMap<AbstractAttribute, AttributeValue>(attributeValues);
		this.attributes = new HashSet<AbstractAttribute>(attributeValues.keySet());
	}
	
	public AttributeValue getAttributeValue(final AbstractAttribute attribute) {
		return attributeValues.get(attribute);
	}
	
	public Map<AbstractAttribute, AttributeValue> getAttributeValues() {
		Map<AbstractAttribute, AttributeValue> copy = new HashMap<AbstractAttribute, AttributeValue>();
		copy.putAll(attributeValues);
		
		return copy;
	}
	
	public Map<String, AttributeValue> getAttributeValuesWithNamesOnEntityAsKey() {
		Map<String, AttributeValue> result = new HashMap<String, AttributeValue>();
		
		for (AbstractAttribute attr : attributeValues.keySet()) {
			result.put(attr.getNameOnEntity(), attributeValues.get(attr));
		}
		
		return result;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(final int frequency) {
		if (frequency < 0) { throw new IllegalArgumentException("'frequency' parameter must be positive"); }
		
		this.frequency = frequency;
	}
	
	public boolean matchEntity(final Collection<? extends AbstractAttribute> inputAttributes, final Entity entity) throws GenstarException {
		if (inputAttributes == null || entity == null) { throw new IllegalArgumentException("Neither 'inputAttributes' nor 'entity' parameter can be null"); }
		
		// attribute values on entity
		EntityAttributeValue entityAttributeValue;
		Map<AbstractAttribute, EntityAttributeValue> entityAttributeValues = new HashMap<AbstractAttribute, EntityAttributeValue>();
		for (AbstractAttribute attribute : inputAttributes) {
			entityAttributeValue = entity.getEntityAttributeValueByNameOnData(attribute.getNameOnData());
			
			if (entityAttributeValue == null) { return false; } // or throw exception?
			
			entityAttributeValues.put(attribute, entityAttributeValue);
		}
		
		// attribute values on distribution element
		AttributeValue attributeValueOnData;
		Map<AbstractAttribute, AttributeValue> inputAttributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (AbstractAttribute attribute : inputAttributes) {
			attributeValueOnData = attributeValues.get(attribute);
			
			if (attributeValueOnData == null) { return false; } // or throw exception?
			inputAttributeValues.put(attribute, attributeValueOnData); 
		}
		
		if (entityAttributeValues.size() != inputAttributeValues.size()) { return false; }
		
		for (AbstractAttribute attribute : entityAttributeValues.keySet()) {
			if (!entityAttributeValues.get(attribute).isAttributeValueOnEntityMatched(inputAttributeValues.get(attribute))) { return false; }
		}
		
		
		return true;
	}
	
	public boolean matchEntity(final Entity entity) throws GenstarException {
		if (entity == null) { throw new GenstarException("Parameter entity can not be null"); }
		
		return this.matchEntity(attributes, entity);
	}
	
	public boolean matchAttributeValues(final Map<AbstractAttribute, ? extends AttributeValue> otherAttributeValues) {
		if (otherAttributeValues == null || otherAttributeValues.isEmpty()) { throw new IllegalArgumentException("'otherAttributeValues' parameter can not be null or empty"); }
		
		AttributeValue attributeValue;
		for (AbstractAttribute attribute : otherAttributeValues.keySet()) {
			attributeValue = attributeValues.get(attribute);
			if (attributeValue == null || attributeValue.compareTo(otherAttributeValues.get(attribute)) != 0) { return false; }
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
		for (AbstractAttribute attr : attributeValues.keySet()) {
			retVal.append("[ attribute : " + attr.getNameOnData() + ", value : " + attributeValues.get(attr) + "], ");
		}
		
		retVal.append("WITH frequency = " + frequency);
		
		return retVal.toString();
	}
	
	public Set<AbstractAttribute> getAttributes() {
		return new HashSet<AbstractAttribute>(attributes);
	}
}
