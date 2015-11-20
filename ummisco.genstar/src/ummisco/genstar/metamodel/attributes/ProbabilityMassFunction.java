package ummisco.genstar.metamodel.attributes;

import java.util.SortedMap;
import java.util.TreeMap;

import ummisco.genstar.util.SharedInstances;

public class ProbabilityMassFunction {

	private SortedMap<AttributeValue, UniqueValue> data;
	
	private Class<? extends AttributeValue> keyClass;
	
	private DataType valueType;
	
	
	// internal data +
	private double doubleTotalProba = 0d;
	
	private float floatTotalProba = 0f;
	
	private int intTotalProba = 0;
	// internal data -
	
	
	public ProbabilityMassFunction(final SortedMap<AttributeValue, UniqueValue> data) {
		if (data == null || data.isEmpty()) { throw new IllegalArgumentException("'data' can be neither null nor empty"); }
		
		keyClass = data.firstKey().getClass();
		valueType = data.get(data.firstKey()).dataType;
		if (!valueType.isNumericValue()) {
			throw new IllegalArgumentException("All values of 'data' must be numeric value type (i.e., integer, float, double).");
		}
		
		for (AttributeValue key : data.keySet()) {
			if (!key.getClass().equals(keyClass)) { throw new IllegalArgumentException("All keys of 'data' must be instances of " + keyClass.getSimpleName()); }
			if (!valueType.equals(data.get(key).dataType)) {
				throw new IllegalArgumentException("All values of 'data' must have " + valueType.getName() + " as value type.");
			}
		}
		
		validateDataValue(data);
		
		this.data = new TreeMap<AttributeValue, UniqueValue>(data);
	}
	
	private void validateDataValue(final SortedMap<? extends AttributeValue, UniqueValue> data) {
		switch (valueType) {
			case INTEGER:
				int intValue;
				for (AttributeValue key : data.keySet()) {
					intValue = Integer.parseInt(data.get(key).getStringValue());
					if (intValue < 0) { throw new IllegalArgumentException("values of data must not be negative"); }
					intTotalProba += intValue;
				}
				if (intTotalProba == 0) {  throw new IllegalArgumentException("'data' must contain at least one positive value"); }
				break;
			
			case FLOAT:
				float floatValue;
				for (AttributeValue key : data.keySet()) {
					floatValue = Float.parseFloat(data.get(key).getStringValue());
					if (floatValue < 0) { throw new IllegalArgumentException("values of data must not be negative"); }
					floatTotalProba += floatValue;
				}
				if (floatTotalProba == 0) { throw new IllegalArgumentException("'data' must contain at least one positive value"); }
				break;
				
			case DOUBLE:
				double doubleValue;
				for (AttributeValue key : data.keySet()) {
					doubleValue = Double.parseDouble(data.get(key).getStringValue());
					if (doubleValue < 0) { throw new IllegalArgumentException("values of data must not be negative"); }
					doubleTotalProba += doubleValue;
				}
				if (doubleTotalProba == 0) { throw new IllegalArgumentException("'data' must contain at least one positive value"); }
				break;
		}
	}
	
	public SortedMap<AttributeValue, UniqueValue> getData() {
		return data;
	}
	
	public AttributeValue nextValue() {
		switch (valueType) {
			case INTEGER:
				int nextInt = SharedInstances.RandomNumberGenerator.nextInt(intTotalProba);
				int cummulativeIntProba = 0;
				for (AttributeValue key : data.keySet()) {
					cummulativeIntProba += Integer.parseInt(data.get(key).getStringValue());
					if (cummulativeIntProba >= nextInt) { return key; }
				}
				
				break;
			
			case FLOAT:
				float nextFloat = SharedInstances.RandomNumberGenerator.nextFloat();
				float scaledNextFloat = nextFloat * floatTotalProba;
				float cummulativeFloatProba = 0;
				for (AttributeValue key : data.keySet()) {
					cummulativeFloatProba += Float.parseFloat(data.get(key).getStringValue());
					if (cummulativeFloatProba >= scaledNextFloat) { return key; }
				}
				 
				break;
				
			case DOUBLE:
				double nextDouble = SharedInstances.RandomNumberGenerator.nextDouble();
				double scaledNextDouble = nextDouble * doubleTotalProba;
				double cummulativeDoubleProba = 0;
				for (AttributeValue key : data.keySet()) {
					cummulativeDoubleProba += Double.parseDouble(data.get(key).getStringValue());
					if (cummulativeDoubleProba >= scaledNextDouble) { return key; }
				}
				
				break;
		}
		
		return null;
	}
}
