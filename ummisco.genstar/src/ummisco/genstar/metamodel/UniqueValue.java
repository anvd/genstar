package ummisco.genstar.metamodel;

import ummisco.genstar.exception.AttributeException;

public class UniqueValue extends AttributeValue { // TODO find a better name!
	
	private String stringValue;

	
	public UniqueValue(final UniqueValue origin) throws AttributeException {
		this(origin.valueType, origin.stringValue);
	}
	
	public UniqueValue(final ValueType valueType) throws AttributeException {
		this(valueType, valueType.getDefaultStringValue());
	}
	
	public UniqueValue(final ValueType valueType, final String stringValue) throws AttributeException {
		super(valueType);
		
		if (stringValue == null) { throw new AttributeException("'stringValue' parameter can not be null"); }
		
		verifyValueValidity(stringValue);
		this.stringValue = stringValue;
	}
	
	private void verifyValueValidity(final String stringValue) throws AttributeException {
		if (!valueType.isValueValid(stringValue)) {
			throw new AttributeException("'" + stringValue + "' is not a valid " + valueType.getName() + " value.");
		}
	}

	public String getStringValue() {
		return stringValue;
	}
	
	public void setStringValue(final String stringValue) throws AttributeException {
		if (stringValue == null) { throw new AttributeException("'stringValue' parameter can not be null"); }
		verifyValueValidity(stringValue);
		
		this.stringValue = stringValue;
	}

	@Override
	public String toString() {
		return "UniqueValue of " + valueType.getName() + " : " + stringValue;
	}
	
	@Override
	public boolean equals(final Object other) {
		if (other instanceof UniqueValue) {
			UniqueValue otherValue = (UniqueValue) other;
			return this.valueType.equals(((UniqueValue) other).valueType) 
					&& this.stringValue.equals(otherValue.stringValue);
		}
		
		return super.equals(other);
	}

	@Override
	public int compareTo(final AttributeValue other) {
		if (other instanceof UniqueValue) {
			UniqueValue otherValue = (UniqueValue) other;
			if (!this.valueType.equals(otherValue.valueType)) { 
				throw new IllegalArgumentException("Can not compare two instances of UniqueValue having different valueTypes : " + valueType.getName() + " v.s. " + otherValue.valueType.getName());
			}
			
			Comparable thisComparableValue = valueType.getComparableValue(stringValue);
			Comparable otherComparableValue = otherValue.valueType.getComparableValue(otherValue.stringValue);
			return thisComparableValue.compareTo(otherComparableValue);
		}
		
		throw new IllegalArgumentException("Can not compare an instance of " + this.getClass().getSimpleName() + " to an instance of " + other.getClass().getSimpleName());
	}

	@Override
	public AttributeValue cast(final Class<? extends AttributeValue> targetType) throws AttributeException {
		if (targetType == null) { throw new IllegalArgumentException("'targetType' parameter can not be null"); }
		
		String targetClassName = targetType.getName();
		
		if (targetClassName.equals(this.getClass().getName())) { return this; }
		if (targetClassName.equals(RangeValue.class.getName())) { return new RangeValue(valueType, stringValue, stringValue); }
		
		throw new AttributeException("'targetType' is not an appropriate type");
	}

	@Override
	public boolean isValueMatch(final AttributeValue otherValue) {
		if (otherValue == null) { throw new IllegalArgumentException("'otherValue' parameter can not be null"); }
		
		if (otherValue instanceof UniqueValue) {
			
			// "numeric" valueType
			if (this.valueType.isNumericValue() && otherValue.valueType.isNumericValue()) {
				double thisDoubleValue = Double.parseDouble(stringValue);
				double otherDoubleValue = Double.parseDouble( ((UniqueValue)otherValue).stringValue);
				
				return thisDoubleValue == otherDoubleValue;
			}
			
			if ( !(this.valueType.equals(otherValue.valueType)) ) { return false; }
			
			return this.compareTo(otherValue) == 0; 
		}
		
		if (otherValue instanceof RangeValue) { return ( (RangeValue) otherValue ).isValueMatch(this); }
		
		return false;
	}

}
