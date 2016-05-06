package ummisco.genstar.metamodel.attributes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;


public class UniqueValuesAttributeWithRangeInput extends AbstractAttribute {
	
	private List<UniqueValue> internalValuesOnData;
	
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
		
		initializeValuesOnData();
	}
	
	private void initializeValuesOnData() throws GenstarException {
		internalValuesOnData = new ArrayList<UniqueValue>();
		
		int minValueInt = minValue.getIntValue();
		int maxValueInt = maxValue.getIntValue();
		
		for (int i=minValueInt; i<=maxValueInt; i++) {
			internalValuesOnData.add(new UniqueValue(DataType.INTEGER, Integer.toString(i)));
		}
	}

	@Override
	public Set<AttributeValue> valuesOnData() {
		Set<AttributeValue> valuesOnData = new HashSet<AttributeValue>(internalValuesOnData);
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

	/*
	@Override
	public boolean containsValueOfAttributeValue(AttributeValue value) {
		if (value instanceof UniqueValue && value.getDataType().equals(DataType.INTEGER)) {
			UniqueValue _value = (UniqueValue)value;
			int intValue = _value.getIntValue();
			return (minValue.getIntValue() <= intValue) && (intValue <= maxValue.getIntValue());
		}

		return false;
	}
	*/

	@Override
	public AttributeValue getInstanceOfAttributeValue(final AttributeValue value) {
		if (!(value instanceof UniqueValue) || !value.getDataType().equals(DataType.INTEGER)) { return null; }
		
		if (internalValuesOnData.contains(value)) { return value; }
		
		int intValue = ((UniqueValue)value).getIntValue();
		if (intValue < minValue.getIntValue() || intValue > maxValue.getIntValue()) { return null; }
		
		int index = intValue - minValue.getIntValue();
		return internalValuesOnData.get(index);
	}

	@Override
	public void clear() {}

	@Override
	public AttributeValue findCorrespondingAttributeValueOnData(final List<String> stringValue) throws GenstarException {
		if (stringValue == null || stringValue.isEmpty()) { throw new GenstarException("'stringValue' parameter can not be null or empty"); }
		return getInstanceOfAttributeValue(new UniqueValue(dataType, stringValue.get(0)));
	}

	@Override
	public AttributeValue findMatchingAttributeValueOnData(final AttributeValue attributeValue) throws GenstarException {
		if (attributeValue instanceof UniqueValue) { return this.getInstanceOfAttributeValue(attributeValue); }

		throw new GenstarException("Not supported operation for attributeValue of " + attributeValue.getClass().getName());
	}

}
