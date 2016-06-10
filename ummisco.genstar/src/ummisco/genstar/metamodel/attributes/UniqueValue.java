package ummisco.genstar.metamodel.attributes;

import ummisco.genstar.exception.GenstarException;

public class UniqueValue extends AttributeValue {
	
	public static final int UNIQUE_VALUE_TYPE = 1;
	
	public static final String UNIQUE_VALUE_NAME = "Unique";
	
	private String stringValue;
	
	public UniqueValue(final UniqueValue origin, final AbstractAttribute attribute) throws GenstarException {
		this(origin.dataType, origin.stringValue, attribute);
	}
	
	public UniqueValue(final DataType dataType, final AbstractAttribute attribute) throws GenstarException {
		this(dataType, dataType.getDefaultStringValue(), attribute);
	}
	
	public UniqueValue(final DataType dataType, final String stringValue, final AbstractAttribute attribute) throws GenstarException {
		super(dataType, attribute);
		
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
	
	public int getIntValue() {
		return Integer.parseInt(stringValue);
	}
	
	public float getFloatValue() {
		return Float.parseFloat(stringValue);
	}
	
	public boolean getBooleanValue() {
		return Boolean.parseBoolean(stringValue);
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
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int compareTo(final AttributeValue other) {
		if (other instanceof UniqueValue) {
			UniqueValue otherValue = (UniqueValue) other;
			if ( !this.dataType.equals(otherValue.dataType) ) { 
				throw new IllegalArgumentException("Can not compare two instances of UniqueValue having different valueTypes : " + dataType.getName() + " v.s. " + otherValue.dataType.getName());
			}
			
			// FIXME performance issue due to object creation!
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
		if (targetClassName.equals(RangeValue.class.getName())) { return new RangeValue(dataType, stringValue, stringValue, this.getAttribute()); }
		
		throw new GenstarException("'targetType' is not an appropriate type");
	}

	@Override
	public boolean isValueMatched(final AttributeValue otherValue) {
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
		
		if (otherValue instanceof RangeValue) { return ( (RangeValue) otherValue ).isValueMatched(this); }
		
		return false;
	}
	

	@Override
	public int getValueTypeID() {
		return UNIQUE_VALUE_TYPE;
	}

	@Override
	public String getValueTypeName() {
		return UNIQUE_VALUE_NAME;
	}

	@Override
	public String toCsvString() {
		return stringValue;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
		result = prime * result + ((getAttribute() == null) ? 0 : getAttribute().getNameOnData().hashCode()); // WARNING: avoid stackoverflow for recursive call to hashcode
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UniqueValue other = (UniqueValue) obj;
		if (stringValue == null) {
			if (other.stringValue != null)
				return false;
		} else if (!stringValue.equals(other.stringValue))
			return false;
		if (getAttribute() == null) {
			if (other.getAttribute() != null)
				return false;
		} else if (!getAttribute().getNameOnData().equals(other.getAttribute().getNameOnData())) // WARNING: avoid stackoverflow for recursive call to equals
			return false;
		return true;
	}

}
