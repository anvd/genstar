package ummisco.genstar.metamodel;

import ummisco.genstar.exception.AttributeException;

public abstract class AttributeValue implements Comparable<AttributeValue> {
	
	protected ValueType valueType;
	
	public AttributeValue(final ValueType valueType) throws AttributeException {
		if (valueType == null) { throw new AttributeException("'valueType' parameter can not be null"); }
		
		this.valueType = valueType;
	}
	
	public ValueType getValueType() {
		return valueType;
	}
	
	@Override public int hashCode() {
		return 1;
	}
	
	public abstract boolean isValueMatch(final AttributeValue otherValue);
	
	public abstract AttributeValue cast(final Class<? extends AttributeValue> targetType) throws AttributeException;
}
