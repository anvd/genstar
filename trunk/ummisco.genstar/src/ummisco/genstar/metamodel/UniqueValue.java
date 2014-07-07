package ummisco.genstar.metamodel;

import ummisco.genstar.exception.GenstarException;

public class UniqueValue extends AttributeValue {
	
	public static final int UNIQUE_VALUE_TYPE = 1;
	
	static { AttributeValue.registerValueTypeID(UniqueValue.class, UNIQUE_VALUE_TYPE); }
	
	private String stringValue;

	
	public UniqueValue(final UniqueValue origin) throws GenstarException {
		this(origin.dataType, origin.stringValue);
	}
	
	public UniqueValue(final DataType dataType) throws GenstarException {
		this(dataType, dataType.getDefaultStringValue());
	}
	
	public UniqueValue(final DataType dataType, final String stringValue) throws GenstarException {
		super(dataType);
		
		if (stringValue == null) { throw new GenstarException("'stringValue' parameter can not be null"); }
		
		verifyValueValidity(stringValue);
		this.stringValue = stringValue;
	}
	
	private void verifyValueValidity(final String stringValue) throws GenstarException {
		if (!dataType.isValueValid(stringValue)) {
			throw new GenstarException("'" + stringValue + "' is not a valid " + dataType.getName() + " value.");
		}
	}

	public String getStringValue() {
		return stringValue;
	}
	
	public void setStringValue(final String stringValue) throws GenstarException {
		if (stringValue == null) { throw new GenstarException("'stringValue' parameter can not be null"); }
		verifyValueValidity(stringValue);
		
		this.stringValue = stringValue;
	}

	@Override
	public String toString() {
		return "UniqueValue of " + dataType.getName() + " : " + stringValue;
	}
	
	@Override
	public int compareTo(final AttributeValue other) {
		if (other instanceof UniqueValue) {
			UniqueValue otherValue = (UniqueValue) other;
			if ( !this.dataType.equals(otherValue.dataType) ) { 
				throw new IllegalArgumentException("Can not compare two instances of UniqueValue having different valueTypes : " + dataType.getName() + " v.s. " + otherValue.dataType.getName());
			}
			
			Comparable thisComparableValue = dataType.getComparableValue(stringValue);
			Comparable otherComparableValue = otherValue.dataType.getComparableValue(otherValue.stringValue);
			return thisComparableValue.compareTo(otherComparableValue);
		}
		
		throw new IllegalArgumentException("Can not compare an instance of " + this.getClass().getSimpleName() + " to an instance of " + other.getClass().getSimpleName());
	}

	@Override
	public AttributeValue cast(final Class<? extends AttributeValue> targetType) throws GenstarException {
		if (targetType == null) { throw new IllegalArgumentException("'targetType' parameter can not be null"); }
		
		String targetClassName = targetType.getName();
		
		if (targetClassName.equals(this.getClass().getName())) { return this; }
		if (targetClassName.equals(RangeValue.class.getName())) { return new RangeValue(dataType, stringValue, stringValue); }
		
		throw new GenstarException("'targetType' is not an appropriate type");
	}

	@Override
	public boolean isValueMatch(final AttributeValue otherValue) {
		if (otherValue == null) { throw new IllegalArgumentException("'otherValue' parameter can not be null"); }
		
		if (otherValue instanceof UniqueValue) {
			
			// "numeric" valueType
			if (this.dataType.isNumericValue() && otherValue.dataType.isNumericValue()) {
				double thisDoubleValue = Double.parseDouble(stringValue);
				double otherDoubleValue = Double.parseDouble( ((UniqueValue)otherValue).stringValue);
				
				return thisDoubleValue == otherDoubleValue;
			}
			
			if ( !(this.dataType.equals(otherValue.dataType)) ) { return false; }
			
			return this.compareTo(otherValue) == 0; 
		}
		
		if (otherValue instanceof RangeValue) { return ( (RangeValue) otherValue ).isValueMatch(this); }
		
		return false;
	}

	@Override
	public int getValueTypeID() {
		return UNIQUE_VALUE_TYPE;
	}

}
