package ummisco.genstar.metamodel;

import java.util.HashMap;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;

public abstract class AttributeValue implements Comparable<AttributeValue> {
	
	private static Map<Class<? extends AttributeValue>, Integer> valueTypeIDs = new HashMap<Class<? extends AttributeValue>, Integer>();
	private static Map<Integer, Class<? extends AttributeValue>> idValueTypes = new HashMap<Integer, Class<? extends AttributeValue>>();
	public static void registerValueTypeID(final Class<? extends AttributeValue> clazz, final int id) { 
		valueTypeIDs.put(clazz,  id);
		idValueTypes.put(id, clazz);
	}

	public static int getIdByClass(final Class<? extends AttributeValue> clazz) { return valueTypeIDs.get(clazz); }
	
	

	protected int attributeValueID = -1;
	
	protected DataType dataType;
	
	public AttributeValue(final DataType dataType) throws GenstarException {
		if (dataType == null) { throw new GenstarException("'valueType' parameter can not be null"); }
		
		this.dataType = dataType;
	}
	
	public DataType getDataType() {
		return dataType;
	}
	
	@Override public int hashCode() {
		return 1;
	}
	
	public abstract boolean isValueMatch(final AttributeValue otherValue);
	
	public abstract AttributeValue cast(final Class<? extends AttributeValue> targetType) throws GenstarException;
	
	public void setAttributeValueID(final int attributeValueID) {
		this.attributeValueID = attributeValueID;
	}
	
	public int getAttributeValueID() {
		return attributeValueID;
	}
	
	public abstract int getValueTypeID();
}
