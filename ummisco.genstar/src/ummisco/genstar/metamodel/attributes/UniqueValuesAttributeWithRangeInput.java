package ummisco.genstar.metamodel.attributes;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

// refactor the AbstractAttribute inheritance tree, "valuesOnData" method is not applicable for this class
// 
public class UniqueValuesAttributeWithRangeInput extends AbstractAttribute {
	
	private Map<Integer, UniqueValue> internalValuesOnData = Collections.EMPTY_MAP;
	
	private UniqueValue minValue;
	
	private UniqueValue maxValue;
	

	public UniqueValuesAttributeWithRangeInput(final ISyntheticPopulationGenerator populationGenerator, final String nameOnData, final String nameOnEntity, 
			final UniqueValue minValue, final UniqueValue maxValue) throws GenstarException {
		super(populationGenerator, nameOnData, nameOnEntity, DataType.INTEGER, UniqueValue.class);
		
		// parameters validation
		if (minValue == null) { throw new GenstarException("'minValue' parameter can not be null"); }
		if (maxValue == null) { throw new GenstarException("'maxValue' parameter can not be null"); }
		if (!minValue.dataType.equals(DataType.INTEGER) || !maxValue.dataType.equals(DataType.INTEGER)) { throw new GenstarException("data type of minValue and maxValue must be integer"); }
		if (minValue.getIntValue() > maxValue.getIntValue()) { throw new GenstarException("minValue can not be bigger than maxValue"); }
		
		this.valueClassOnData = UniqueValue.class;
		try {
			this.setDefaultValue(valueClassOnData.getConstructor(DataType.class).newInstance(dataType));
		} catch (final Exception e) {
			throw new GenstarException(e);
		}
		
		this.minValue = new UniqueValue(minValue);
		this.maxValue = new UniqueValue(maxValue);
	}

	@Override
	public Set<AttributeValue> valuesOnData() {
		Set<AttributeValue> valuesOnData = new HashSet<AttributeValue>();
		valuesOnData.add(minValue);
		valuesOnData.add(maxValue);
		
		return valuesOnData;
	}

	@Override
	public boolean add(AttributeValue value) throws GenstarException {
		return false;
	}

	@Override
	public boolean addAll(Set<AttributeValue> values) throws GenstarException {
		return false;
	}

	@Override
	public boolean remove(AttributeValue value) {
		return false;
	}

	@Override
	public boolean containsInstanceOfAttributeValue(AttributeValue value) {
		
		return false;
	}

	@Override
	public boolean containsValueOfAttributeValue(AttributeValue value) {
		if (value instanceof UniqueValue && value.getDataType().equals(DataType.INTEGER)) {
			UniqueValue _value = (UniqueValue)value;
			int intValue = _value.getIntValue();
			return (minValue.getIntValue() <= intValue) && (intValue <= maxValue.getIntValue());
		}

		return false;
	}

	@Override
	public AttributeValue getInstanceOfAttributeValue(final AttributeValue value) {
		if (!(value instanceof UniqueValue) || !value.getDataType().equals(DataType.INTEGER)) { return null; }
		
		if (internalValuesOnData.containsValue(value)) { return value; }
		
		if (internalValuesOnData == Collections.EMPTY_MAP) { internalValuesOnData = new HashMap<Integer, UniqueValue>(); }
		
		int intValue = ((UniqueValue)value).getIntValue();
		if (intValue < minValue.getIntValue() || intValue > maxValue.getIntValue()) { return null; }
		
		UniqueValue savedAttributeValue = internalValuesOnData.get(intValue);
		if (savedAttributeValue != null) { return savedAttributeValue; }
		
		try {
			savedAttributeValue = new UniqueValue(DataType.INTEGER, Integer.toString(intValue));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		internalValuesOnData.put(intValue, savedAttributeValue);
		
		return savedAttributeValue;
	}

	@Override
	public void clear() {
		
	}

	@Override
	public AttributeValue findCorrespondingAttributeValueOnData(final List<String> stringValue) throws GenstarException {
		
		if (stringValue == null || stringValue.isEmpty()) { throw new GenstarException("'stringValue' parameter can not be null or empty"); }
		
//		if (isIdentity) {
//			if (stringValue.size() == 1) { return new UniqueValue(dataType, stringValue.get(0)); }
//		}
		
		return getInstanceOfAttributeValue(new UniqueValue(dataType, stringValue.get(0)));
	}

	@Override
	public AttributeValue findMatchingAttributeValueOnData(final AttributeValue attributeValue) throws GenstarException {
		if (attributeValue instanceof UniqueValue) { return this.getInstanceOfAttributeValue(attributeValue); }

		throw new GenstarException("Not supported operation for attributeValue of " + attributeValue.getClass().getName());
	}

}
