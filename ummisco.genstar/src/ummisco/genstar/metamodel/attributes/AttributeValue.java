package ummisco.genstar.metamodel.attributes;

import java.util.HashMap;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.util.PersistentObject;

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

	protected int attributeValueID = PersistentObject.NEW_OBJECT_ID;
	
	protected DataType dataType;
	
	protected AbstractAttribute attribute;
	
	protected AttributeValue(final DataType dataType) throws GenstarException {
		if (dataType == null) { throw new GenstarException("'dataType' parameter can not be null"); }
		
		this.dataType = dataType;
	}
	
	protected AttributeValue(final DataType dataType, final AbstractAttribute attribut) throws GenstarException{
		this(dataType);
		this.attribute = attribut;
	}
	
	public DataType getDataType() {
		return dataType;
	}
	
	public abstract boolean isValueMatched(final AttributeValue otherValue);
	
	public abstract AttributeValue cast(final Class<? extends AttributeValue> targetType) throws GenstarException;
	
	// FIXME refactoring:
	// 			1. create a "PersistableObject" class that contains DBMS related-attributes
	//			2. them move this method to the class
	public void setAttributeValueID(final int attributeValueID) {
		this.attributeValueID = attributeValueID;
	}
	
	public int getAttributeValueID() {
		return attributeValueID;
	}
	
	public AbstractAttribute getAttribute(){
		return attribute;
	}
	
	public abstract int getValueTypeID();
	
	public abstract String getValueTypeName();
	
	public abstract String toCsvString();

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attribute == null) ? 0 : attribute.getNameOnData().hashCode());
		result = prime * result + attributeValueID;
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
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
		AttributeValue other = (AttributeValue) obj;
		if (attribute == null) {
			if (other.attribute != null)
				return false;
		} else if (!attribute.equals(other.attribute))
			return false;
		if (attributeValueID != other.attributeValueID)
			return false;
		if (dataType != other.dataType)
			return false;
		return true;
	}
}
