package ummisco.genstar.metamodel;

import ummisco.genstar.exception.AttributeException;
import ummisco.genstar.util.SharedInstances;

public class RangeValue extends AttributeValue {
	
	private String minStringValue = "";
	
	private String maxStringValue = "";
	
	
	public RangeValue(final RangeValue origin) throws AttributeException {
		this(origin.valueType, origin.minStringValue, origin.maxStringValue);
	}
	
	public RangeValue(final ValueType valueType) throws AttributeException {
		this(valueType, valueType.getDefaultStringValue(), valueType.getDefaultStringValue());
	}
	
	public RangeValue(final ValueType valueType, final String minStringValue, final String maxStringValue) throws AttributeException {
		super(valueType);
		
		if (minStringValue == null) { throw new AttributeException("'minStringValue' can not be null"); }
		if (maxStringValue == null) { throw new AttributeException("'maxStringValue' can not be null"); }
		if (!valueType.isNumericValue()) { throw new AttributeException(this.getClass().getName() + " only supports Double, Float and Integer value."); }
		
		setMinStringValue(minStringValue);
		setMaxStringValue(maxStringValue);
		
		verifyValidity();
	}
	
	private void verifyValidity() throws AttributeException {
		Comparable minComparable = valueType.getComparableValue(minStringValue);
		Comparable maxComparable = valueType.getComparableValue(maxStringValue);
		
		if (minComparable.compareTo(maxComparable) > 0) {
			throw new AttributeException("Min value must not be greater than Max value.");
		}
	}

	public String getMinStringValue() {
		return minStringValue;
	}

	public void setMinStringValue(final String minStringValue) throws AttributeException {
		if (minStringValue == null) { throw new AttributeException("'minStringValue' can not be null"); }
		if (!valueType.isValueValid(minStringValue)) { throw new AttributeException("'" + minStringValue + "'" + " is not a valid " + valueType.getName() + " value"); }
		
		this.minStringValue = minStringValue;
	}

	public String getMaxStringValue() {
		return maxStringValue;
	}

	public void setMaxStringValue(final String maxStringValue) throws AttributeException {
		if (maxStringValue == null) { throw new AttributeException("'maxStringValue' can not be null"); }
		if (!valueType.isValueValid(maxStringValue)) { throw new AttributeException("'" + maxStringValue + "'" + " is not a valid " + valueType.getName() + " value"); }
		
		this.maxStringValue = maxStringValue;
	}

	@Override
	public String toString() {
		return "RangeValue of " + valueType.getName() + " : [" + minStringValue + ", " + maxStringValue + "]";
	}
	
	@Override
	public boolean equals(final Object other) {
		if (other instanceof RangeValue) {
			RangeValue otherRange = (RangeValue) other;
			return this.valueType.equals(((RangeValue) other).valueType) 
					&& minStringValue.equals(otherRange.minStringValue) && maxStringValue.equals(otherRange.maxStringValue);
		}
		
		return super.equals(other);
	}

	@Override
	public int compareTo(final AttributeValue other) {
		if (other instanceof RangeValue) {
			RangeValue otherRangeValue = (RangeValue) other;
			if (!valueType.equals(otherRangeValue.valueType)) {
				throw new IllegalArgumentException("Can not compare two instances of RangeValue  having different valueTypes : " + valueType.getName() + " v.s. " + otherRangeValue.valueType.getName());
			}
			
			// compare the maxValues
			Comparable thisMax = valueType.getComparableValue(maxStringValue);
			Comparable otherMax = otherRangeValue.valueType.getComparableValue(otherRangeValue.maxStringValue);
			if (thisMax.compareTo(otherMax) != 0) { return thisMax.compareTo(otherMax); }
			
			// compare the minValue
			Comparable thisMin = valueType.getComparableValue(minStringValue);
			Comparable otherMin = otherRangeValue.valueType.getComparableValue(otherRangeValue.minStringValue);
			return thisMin.compareTo(otherMin);
		}
	
		throw new IllegalArgumentException("Can not compare an instance of RangeValue to an instance of " + other.getClass().getSimpleName());
	}
	
	private String getValueInRange() {
		switch (valueType) {
			case INTEGER:
				int minIntValue = Integer.parseInt(minStringValue);
				int maxIntValue = Integer.parseInt(maxStringValue);
				if (minIntValue == 0 && maxIntValue == 0) { return "0"; }

				return Integer.toString(minIntValue + SharedInstances.RandomNumberGenerator.nextInt(maxIntValue - minIntValue));
			
			case FLOAT:
				float minFloatValue = Float.parseFloat(minStringValue);
				float maxFloatValue = Float.parseFloat(maxStringValue);
				if (minFloatValue == 0f && maxFloatValue == 0f) { return "0"; }
				
				return Float.toString(minFloatValue + (SharedInstances.RandomNumberGenerator.nextFloat() * (maxFloatValue - minFloatValue)));
			
			case DOUBLE:
				double minDoubleValue = Double.parseDouble(minStringValue);
				double maxDoubleValue = Double.parseDouble(maxStringValue);
				if (minDoubleValue == 0d && maxDoubleValue == 0d) { return "0"; }
				
				return Double.toString(minDoubleValue + (SharedInstances.RandomNumberGenerator.nextDouble() * (maxDoubleValue - minDoubleValue)));
		}
		
		return null;
	}
	
	public boolean cover(final UniqueValue numericValue) {
		
		if (numericValue.valueType.isNumericValue()) {
			double minDoubleValue = Double.parseDouble(minStringValue);
			double maxDoubleValue = Double.parseDouble(maxStringValue);
			double value = Double.parseDouble( (numericValue.getStringValue()) );

			return (minDoubleValue <= value && value <= maxDoubleValue);
		}
		
		return false;
	}

	public boolean isInferior(final AttributeValue otherValue) {
		if (otherValue == null) { throw new IllegalArgumentException("'otherValue' must not be null"); }
		
		if (otherValue instanceof UniqueValue) {
			
			UniqueValue otherUniqueValue = (UniqueValue) otherValue;
			if (otherUniqueValue.valueType.isNumericValue()) {
				Double otherDoubleValue = Double.parseDouble(otherUniqueValue.getStringValue());
				Double maxDoubleValue = Double.parseDouble(maxStringValue);
				
				return maxDoubleValue < otherDoubleValue;
			}
			
			return false; 
		}
		
		if (otherValue instanceof RangeValue) {
			RangeValue otherRangeValue = (RangeValue) otherValue;
			Double otherMinDoubleValue = Double.parseDouble(otherRangeValue.getMinStringValue());
			Double thisMaxDoubleValue = Double.parseDouble(maxStringValue);
			
			return thisMaxDoubleValue < otherMinDoubleValue;
		}
		
		
		return false;
	}

	public boolean isSuperior(final AttributeValue otherValue) {
		if (otherValue == null) { throw new IllegalArgumentException("'otherValue' must not be null"); }
		
		if (otherValue instanceof UniqueValue) {
			UniqueValue otherUniqueValue = (UniqueValue) otherValue;
			
			if (otherUniqueValue.valueType.isNumericValue()) {
				Double otherDoubleValue = Double.parseDouble(otherUniqueValue.getStringValue());
				Double minDoubleValue = Double.parseDouble(minStringValue);
				
				return otherDoubleValue < minDoubleValue;
			}
			
			return false;
		}
		
		if (otherValue instanceof RangeValue) {
			RangeValue otherRangeValue = (RangeValue) otherValue;
			Double otherMaxDoubleValue = Double.parseDouble(otherRangeValue.maxStringValue);
			Double thisMinDoubleValue = Double.parseDouble(this.minStringValue);
			
			return thisMinDoubleValue > otherMaxDoubleValue;
		}
		
		return false;
	}

	@Override
	public AttributeValue cast(final Class<? extends AttributeValue> targetType) throws AttributeException {
		if (targetType == null) { throw new IllegalArgumentException("'targetType' parameter can not be null"); }
		
		String targetClassName = targetType.getName();
		
		if (targetClassName.equals(this.getClass().getName())) { return this; }
		if (targetClassName.equals(UniqueValue.class.getName())) { return new UniqueValue(valueType, getValueInRange()); }
		
		throw new AttributeException("'targetType' is not an appropriate type");
	}

	@Override
	public boolean isValueMatch(final AttributeValue otherValue) {
		
		if (otherValue == null) { throw new IllegalArgumentException("'otherValue' parameter can not be null"); }
		
		double minDoubleValue = Double.parseDouble(minStringValue);
		double maxDoubleValue = Double.parseDouble(maxStringValue);

		if (otherValue instanceof UniqueValue) { 
			if (otherValue.getValueType().isNumericValue()) {
				double otherUniqueValue = Double.parseDouble( ( (UniqueValue) otherValue).getStringValue() );
				
				return (minDoubleValue <= otherUniqueValue) && (otherUniqueValue <= maxDoubleValue);
			}
		}
		
		if (otherValue instanceof RangeValue) { 
			RangeValue otherRange = (RangeValue) otherValue;
			double otherMinDoubleValue = Double.parseDouble(otherRange.minStringValue);
			double otherMaxDoubleValue = Double.parseDouble(otherRange.maxStringValue);
			
			return (minDoubleValue == otherMinDoubleValue) && (maxDoubleValue == otherMaxDoubleValue);
		}
		
		return false;
	}	
}
