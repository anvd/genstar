package ummisco.genstar.metamodel.attributes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;

public class RecorderAttribute extends AbstractAttribute {
	
	private AttributeValue recordedValue;

	protected RecorderAttribute(String attributeNameOnData, String attributeNameOnEntity, DataType dataType, AbstractAttribute recordedAttribute) 
			throws GenstarException {
		super(attributeNameOnData, attributeNameOnEntity, dataType, UniqueValue.class, recordedAttribute);
	}

	@Override
	public AttributeValue findCorrespondingAttributeValueOnData(List<String> stringValue) throws GenstarException {
		return null;
	}

	@Override
	public AttributeValue findMatchingAttributeValueOnData(AttributeValue attributeValue) throws GenstarException {
		if(attributeValue.isValueMatched(recordedValue))
			return recordedValue;
		return null;
	}

	@Override
	public Set<AttributeValue> valuesOnData() {
		return new HashSet<>(Arrays.asList(recordedValue));
	}

	@Override
	public boolean add(AttributeValue value) throws GenstarException {
		if(recordedValue != null)
			return false;
		this.recordedValue = value;
		return true;
	}

	@Override
	public boolean addAll(Set<AttributeValue> values) throws GenstarException {
		if(recordedValue != null || values.size() != 1)
			return false;
		this.recordedValue = values.iterator().next();
		return true;
	}

	@Override
	public boolean remove(AttributeValue value) {
		if(!recordedValue.equals(value))
			return false;
		clear();
		return true;
	}

	@Override
	public AttributeValue getInstanceOfAttributeValue(AttributeValue value) {
		if(value.isValueMatched(recordedValue))
			return recordedValue;
		return null;
	}

	@Override
	public void clear() {
		recordedValue = null;
	}

}
