package ummisco.genstar.metamodel.attributes;

import java.util.HashMap;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;

public abstract class AttributeValue implements Comparable<AttributeValue> {
	
	private static Map<Class<? extends AttributeValue>, Integer> valueTypeIDs = new HashMap<Class<? extends AttributeValue>, Integer>();
	private static Map<Integer, Class<? extends AttributeValue>> idValueTypes = new HashMap<Integer, Class<? extends AttributeValue>>();
	private static Map<String, Class<? extends AttributeValue>> nameValueTypes = new HashMap<String, Class<? extends AttributeValue>>();
	public static void registerValueTypeID(final Class<? extends AttributeValue> clazz, final int id, final String name) { 
		valueTypeIDs.put(clazz,  id);
		idValueTypes.put(id, clazz);
		nameValueTypes.put(name, clazz);
	}

	public static int getIdByClass(final Class<? extends AttributeValue> clazz) { return valueTypeIDs.get(clazz); }
	
	public static Class<? extends AttributeValue> getClassByName(final String name) { return nameValueTypes.get(name); }
	
	static {
		AttributeValue.registerValueTypeID(UniqueValue.class, UniqueValue.UNIQUE_VALUE_TYPE, UniqueValue.UNIQUE_VALUE_NAME);
		AttributeValue.registerValueTypeID(RangeValue.class, RangeValue.RANGE_VALUE_TYPE, RangeValue.RANGE_VALUE_NAME);
	}
	
	protected DataType dataType;
	
	public AttributeValue(final DataType dataType) throws GenstarException {
		if (dataType == null) { throw new GenstarException("'dataType' parameter can not be null"); }
		
		this.dataType = dataType;
	}
	
	public DataType getDataType() {
		return dataType;
	}
	
	@Override public int hashCode() {
		return 1;
	}
	
	public abstract boolean isValueMatched(final AttributeValue otherValue);
	
	public abstract AttributeValue cast(final Class<? extends AttributeValue> targetType) throws GenstarException;
	
	public abstract int getValueTypeID();
	
	public abstract String getValueTypeName();
	
	public abstract String toCsvString();
}
