package ummisco.genstar.metamodel.attributes;

import ummisco.genstar.exception.GenstarException;

public class EntityAttributeValue {
	
	private AbstractAttribute attribute;

	private AttributeValue attributeValueOnEntity;
	
	private AttributeValue attributeValueOnData;
	
	
	public EntityAttributeValue(final AbstractAttribute attribute, final AttributeValue attributeValueOnData, final AttributeValue attributeValueOnEntity) throws GenstarException {
		if (attribute == null || attributeValueOnData == null || attributeValueOnEntity == null) {
			throw new GenstarException("None of attribute, attributeValueOnData, attributeValueOnEntity paramters can be null");
		}
		
		if (!attribute.containsValueOfAttributeValue(attributeValueOnData)) {
			throw new GenstarException(attributeValueOnData + " is not a valid value of " + attribute.getNameOnData() + " attribute");
		}
		
		AttributeValue matchingAttributeValueOnData = attribute.findMatchingAttributeValue(attributeValueOnEntity);
		if (matchingAttributeValueOnData == null) {
			throw new GenstarException("No matching attribute value on data found for " +  attributeValueOnEntity);
		}
		if (matchingAttributeValueOnData.compareTo(attributeValueOnData) != 0) {
			throw new GenstarException("attributeValueOnData and attributeValueOnEntity are not compatible values");
		}
		
		this.attribute = attribute;
		this.attributeValueOnData = attributeValueOnData;
		this.attributeValueOnEntity = attributeValueOnEntity;
	}
	
	public EntityAttributeValue(final AbstractAttribute attribute, final AttributeValue attributeValueOnData) throws GenstarException {
		if (attribute == null || attributeValueOnData == null) { throw new GenstarException("Neither 'attribute' nor 'attributeValueOnData' parameter can be null"); }
		
		if (!attribute.containsValueOfAttributeValue(attributeValueOnData)) {
			throw new GenstarException(attributeValueOnData + " is not a valid value of " + attribute.getNameOnData() + " attribute");
		}
		
		this.attribute = attribute;
		this.attributeValueOnData = attributeValueOnData;
		this.attributeValueOnEntity = attributeValueOnData.cast(attribute.getValueClassOnEntity());
	}
	
	public AbstractAttribute getAttribute() {
		return attribute;
	}

	public AttributeValue getAttributeValueOnData() {
		return attributeValueOnData;
	}
	
	public AttributeValue getAttributeValueOnEntity() {
		return attributeValueOnEntity;
	}
	
	public boolean isAttributeValueOnEntityMatched(final AttributeValue attributeValueOnEntity) {
		if (attributeValueOnEntity == null) { return false; }
		
		return this.attributeValueOnEntity.isValueMatched(attributeValueOnEntity);
	}
	
	@Override public String toString() {
		return "EntityAttributeValue with : attributeValueOnData : " + attributeValueOnData.toString() + "; attributeValueOnEntity : " + attributeValueOnEntity.toString();
	}
}
