package ummisco.genstar.metamodel.attributes;

import ummisco.genstar.exception.GenstarException;


public class EntityAttributeValue {
	
	private AbstractAttribute attribute;

	private AttributeValue attributeValueOnData;

	private AttributeValue attributeValueOnEntity;
	
	
	public EntityAttributeValue(final AbstractAttribute attribute, final AttributeValue attributeValueOnData, final AttributeValue attributeValueOnEntity) throws GenstarException {
		if (attribute == null || attributeValueOnData == null || attributeValueOnEntity == null) {
			throw new GenstarException("None of attribute, attributeValueOnData, attributeValueOnEntity parameters can be null");
		}
		
		if (!(attribute.containInstanceOfAttributeValue(attributeValueOnData))) {
			throw new GenstarException(attributeValueOnData + " is not a valid value of " + attribute.getNameOnData() + " attribute");
		}

		AttributeValue matchingAttributeValueOnData = attribute.getMatchingAttributeValueOnData(attributeValueOnEntity);
		if (matchingAttributeValueOnData == null) { throw new GenstarException(attributeValueOnEntity + " is not a valid value of " + attribute.getNameOnData() + " attribute."); }
		if (!matchingAttributeValueOnData.equals(attributeValueOnData)) { throw new GenstarException(attributeValueOnData + " and " + attributeValueOnEntity + " are not compatible values"); }
		
		this.attribute = attribute;
		this.attributeValueOnData = attributeValueOnData;
		this.attributeValueOnEntity = attributeValueOnEntity;
	}
	
	public EntityAttributeValue(final AbstractAttribute attribute, final AttributeValue attributeValueOnData) throws GenstarException {
		if (attribute == null || attributeValueOnData == null) { throw new GenstarException("Neither 'attribute' nor 'attributeValueOnData' parameter can be null"); }
		
		if (!(attribute.containInstanceOfAttributeValue(attributeValueOnData))) {
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
	
	public boolean isAttributeValueOnDataMatched(final AttributeValue attributeValueOnData) {
		if (attributeValueOnData == null) { return false; }
		
		return this.attributeValueOnData.isValueMatched(attributeValueOnData);
	}
	
	@Override public String toString() {
		return "EntityAttributeValue with : attributeValueOnData : " + attributeValueOnData.toString() + "; attributeValueOnEntity : " + attributeValueOnEntity.toString();
	}
}
