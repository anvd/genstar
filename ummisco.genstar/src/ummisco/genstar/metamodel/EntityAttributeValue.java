package ummisco.genstar.metamodel;

import ummisco.genstar.exception.AttributeException;

public class EntityAttributeValue {
	
	private AbstractAttribute attribute;

	private AttributeValue attributeValueOnEntity;
	
	private AttributeValue attributeValueOnData;
	
	
	public EntityAttributeValue(final AbstractAttribute attribute, final AttributeValue attributeValueOnData) throws AttributeException {
		if (attribute == null || attributeValueOnData == null) { throw new IllegalArgumentException("Neither 'attribute' nor 'attributeValueOnData' parameter can not be null"); }
		
		// FIXME validation : attribute can accept attributeValueOnData
		
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
	
	public boolean isValueMatch(final AttributeValue attributeValue) {
		if (attributeValue == null) { return false; }
		
		return attributeValueOnEntity.isValueMatch(attributeValue);
	}
	
	@Override public String toString() {
		return "EntityAttributeValue with : attributeValueOnData : " + attributeValueOnData.toString() + "; attributeValueOnEntity : " + attributeValueOnEntity.toString();
	}
}
