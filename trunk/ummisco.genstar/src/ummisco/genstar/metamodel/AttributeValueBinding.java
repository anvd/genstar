package ummisco.genstar.metamodel;

import ummisco.genstar.exception.AttributeException;

public class AttributeValueBinding { // TODO remove
	
	private AbstractAttribute attribute;
	
	private AttributeValue value;
	
	
	public AttributeValueBinding(final AbstractAttribute attribute, final AttributeValue value) throws AttributeException {
		if (attribute == null) { throw new AttributeException("'attribute' parameter can not be null"); }
		if (value == null) { throw new AttributeException("'value' parameter can not be null"); }
		if (!value.getValueType().equals(attribute.valueType)) { throw new AttributeException("Incoherent ValueType between attribute and value : attribute's valueType is " + attribute.getValueType()
				+ " while value's valueType is " + value.getValueType()); }
		
		this.attribute = attribute;
		this.value = value;
	}
	
	public AbstractAttribute getAttribute() {
		return attribute;
	}
	
	public AttributeValue getValue() {
		return value;
	}
	
	@Override
	public boolean equals(final Object other) {
		if (other instanceof AttributeValueBinding) {
			AttributeValueBinding otherAttributeValue = (AttributeValueBinding) other;
			return this.attribute.equals(otherAttributeValue.attribute) && this.value.equals(otherAttributeValue.value);
		} else {
			return super.equals(other);
		}
	}
	
}
