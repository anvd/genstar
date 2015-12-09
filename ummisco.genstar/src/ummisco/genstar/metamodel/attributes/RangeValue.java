package ummisco.genstar.metamodel.attributes;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.util.SharedInstances;

public class RangeValue extends AttributeValue {
	
	public static final int RANGE_VALUE_TYPE = 2;
	
	public static final String RANGE_VALUE_NAME = "Range";
	
	private String minStringValue = "";
	
	private String maxStringValue = "";
	
	
	public RangeValue(final RangeValue origin) throws GenstarException {
		this(origin.dataType, origin.minStringValue, origin.maxStringValue);
	}
	
	public RangeValue(final DataType dataType) throws GenstarException {
		this(dataType, dataType.getDefaultStringValue(), dataType.getDefaultStringValue());
	}
	
	public RangeValue(final DataType dataType, final String minStringValue, final String maxStringValue) throws GenstarException {
		super(dataType);
		
		if (minStringValue == null) { throw new GenstarException("'minStringValue' can not be null"); }
		if (maxStringValue == null) { throw new GenstarException("'maxStringValue' can not be null"); }
		if (!dataType.isNumericValue()) { throw new GenstarException(this.getClass().getName() + " only supports Double, Float and Integer value."); }
		
		setMinStringValue(minStringValue);
		setMaxStringValue(maxStringValue);
		
		verifyValidity();
	}
	
	private void verifyValidity() throws GenstarException {
		Comparable minComparable = dataType.getComparableValue(minStringValue);
		Comparable maxComparable = dataType.getComparableValue(maxStringValue);
		
		if (minComparable.compareTo(maxComparable) > 0) {
			throw new GenstarException("Min value must not be greater than Max value.");
		}
	}

	public String getMinStringValue() {
		return minStringValue;
	}

	public void setMinStringValue(final String minStringValue) throws GenstarException {
		if (minStringValue == null) { throw new GenstarException("'minStringValue' can not be null"); }
		if (!dataType.isValueValid(minStringValue)) { throw new GenstarException("'" + minStringValue + "'" + " is not a valid " + dataType.getName() + " value"); }
		
		this.minStringValue = minStringValue;
	}

	public String getMaxStringValue() {
		return maxStringValue;
	}

	public void setMaxStringValue(final String maxStringValue) throws GenstarException {
		if (maxStringValue == null) { throw new GenstarException("'maxStringValue' can not be null"); }
		if (!dataType.isValueValid(maxStringValue)) { throw new GenstarException("'" + maxStringValue + "'" + " is not a valid " + dataType.getName() + " value"); }
		
		this.maxStringValue = maxStringValue;
	}

	@Override
	public String toString() {
		return "RangeValue of " + dataType.getName() + " : [" + minStringValue + ", " + maxStringValue + "]";
	}
	
	@Override
	public int compareTo(final AttributeValue other) {
		if (other instanceof RangeValue) {
			RangeValue otherRangeValue = (RangeValue) other;
			if (!dataType.equals(otherRangeValue.dataType)) {
				throw new IllegalArgumentException("Can not compare two instances of RangeValue having different valueTypes : " + dataType.getName() + " v.s. " + otherRangeValue.dataType.getName());
			}
			
			// compare the maxValues
			Comparable thisMax = dataType.getComparableValue(maxStringValue);
			Comparable otherMax = otherRangeValue.dataType.getComparableValue(otherRangeValue.maxStringValue);
			if (thisMax.compareTo(otherMax) != 0) { return thisMax.compareTo(otherMax); }
			
			// compare the minValue
			Comparable thisMin = dataType.getComparableValue(minStringValue);
			Comparable otherMin = otherRangeValue.dataType.getComparableValue(otherRangeValue.minStringValue);
			return thisMin.compareTo(otherMin);
		}
	
		throw new IllegalArgumentException("Can not compare an instance of RangeValue to an instance of " + other.getClass().getSimpleName());
	}
	
	private String getValueInRange() {
		switch (dataType) {
			case INTEGER:
				int minIntValue = Integer.parseInt(minStringValue);
				int maxIntValue = Integer.parseInt(maxStringValue);
				if (minIntValue == 0 && maxIntValue == 0) { return "0"; }

				return Integer.toString(minIntValue + SharedInstances.RandomNumberGenerator.nextInt((maxIntValue + 1) - minIntValue));
			
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
		
		if (numericValue.dataType.isNumericValue()) {
			double minDoubleValue = Double.parseDouble(minStringValue);
			double maxDoubleValue = Double.parseDouble(maxStringValue);
			double value = Double.parseDouble( (numericValue.getStringValue()) );

			return (minDoubleValue <= value && value <= maxDoubleValue);
		}
		
		return false;
	}
	
	public boolean cover(final String numericString) throws GenstarException {
		return cover(new UniqueValue(dataType, numericString));
	}

	public boolean isInferior(final AttributeValue otherValue) {
		if (otherValue == null) { throw new IllegalArgumentException("'otherValue' must not be null"); }
		
		if (otherValue instanceof UniqueValue) {
			
			UniqueValue otherUniqueValue = (UniqueValue) otherValue;
			if (otherUniqueValue.dataType.isNumericValue()) {
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
			
			if (otherUniqueValue.dataType.isNumericValue()) {
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
	public AttributeValue cast(final Class<? extends AttributeValue> targetType) throws GenstarException {
		if (targetType == null) { throw new IllegalArgumentException("'targetType' parameter can not be null"); }
		
		String targetClassName = targetType.getName();
		
		if (targetClassName.equals(this.getClass().getName())) { return this; }
		if (targetClassName.equals(UniqueValue.class.getName())) { return new UniqueValue(dataType, getValueInRange()); }
		
		throw new GenstarException("'targetType' is not an appropriate type");
	}

	@Override
	public boolean isValueMatched(final AttributeValue otherValue) {
		
		if (otherValue == null) { throw new IllegalArgumentException("'otherValue' parameter can not be null"); }
		
		double minDoubleValue = Double.parseDouble(minStringValue);
		double maxDoubleValue = Double.parseDouble(maxStringValue);

		if (otherValue instanceof UniqueValue) { 
			if (otherValue.getDataType().isNumericValue()) {
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

	@Override
	public int getValueTypeID() {
		return RANGE_VALUE_TYPE;
	}

	@Override
	public String getValueTypeName() {
		return RANGE_VALUE_NAME;
	}

	@Override
	public String toCSVString() {
		return minStringValue + ":" + maxStringValue;
	}	
}
