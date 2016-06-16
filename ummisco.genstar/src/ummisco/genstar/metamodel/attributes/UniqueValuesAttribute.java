package ummisco.genstar.metamodel.attributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;

public class UniqueValuesAttribute extends AbstractAttribute {
		
	
	private Set<UniqueValue> valuesOnData;

	protected UniqueValuesAttribute(final String nameOnData, final DataType dataType) throws GenstarException {
		this(nameOnData, nameOnData, dataType, UniqueValue.class);
	}

	protected UniqueValuesAttribute(final String nameOnData, final DataType dataType, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException {
		this(nameOnData, nameOnData, dataType, valueClassOnEntity);
	}

	protected UniqueValuesAttribute(final String nameOnData, final String nameOnEntity, final DataType dataType) throws GenstarException {
		this(nameOnData, nameOnEntity, dataType, UniqueValue.class);		
	}

	protected UniqueValuesAttribute(final String nameOnData, final String nameOnEntity, final DataType dataType, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException {
		super(nameOnData, nameOnEntity, dataType, valueClassOnEntity, null);

		this.valueClassOnData = UniqueValue.class;
		this.valuesOnData = new HashSet<UniqueValue>();
		try {
			this.setDefaultValue(valueClassOnData.getConstructor(DataType.class, AbstractAttribute.class).newInstance(dataType, this));
		} catch (final Exception e) {
			throw new GenstarException(e);
		}
	}
	
	public void addStringValue(final String stringValue) throws GenstarException {
		if (stringValue == null || stringValue.trim().length() == 0) { throw new IllegalArgumentException("'stringValue' parameter can neither be null nor empty"); }
		
		if (!dataType.isValueValid(stringValue)) { throw new IllegalArgumentException("'" + stringValue + "' is not a(n) " + dataType.getName() + " value."); }
		
		// valuesOnData.add(new UniqueValue(dataType, stringValue));
		this.add(new UniqueValue(dataType, stringValue, this));

		// fire event
		internalFireEvent();
	}
	
	@Override public boolean add(final AttributeValue value) throws GenstarException {
		if (value == null) { throw new GenstarException("'value' parameter can not be null"); }
		if (!(value.getClass().equals(valueClassOnData))) { throw new GenstarException("value must be an instance of " + valueClassOnData.getName()); }
		if (!this.dataType.equals(value.dataType)) { throw new GenstarException("Incompatible valueType"); }
		
//		if (this.containsInstanceOfAttributeValue(value) || this.containsValueOfAttributeValue(value)) { return false; }
//		if (this.getInstanceOfAttributeValue(value) != null || this.containsValueOfAttributeValue(value)) { return false; }
		if (this.getInstanceOfAttributeValue(value) != null) { return false; }
		
		// FIXME containsValue
		
		valuesOnData.add((UniqueValue)value);

		// fire event
		internalFireEvent();
		return true;
	}
	
	@Override public boolean addAll(final Set<AttributeValue> values) throws GenstarException {
		if (values == null) { throw new GenstarException("'values' parameter can not be null"); }
		
		boolean changed = false;
		for (AttributeValue v : values) {
			if (v instanceof UniqueValue) {
				if (add(v)) { changed = true; }
			}
		}
		
		if (changed) { internalFireEvent(); }
		return changed;
	}

	@Override public boolean remove(final AttributeValue value) {
		if (value == null) { return false; }
		
		if (value instanceof UniqueValue) {
			AttributeValue target = null;
			for (UniqueValue v : valuesOnData) {
				if (value.compareTo(v) == 0) {
					target = v;
					break;
				}
			}
			
			if (target != null) {
				valuesOnData.remove(target);
				internalFireEvent();
				return true;
			}
		}
		
		return false;
	}

//	@Override public boolean containsInstanceOfAttributeValue(final AttributeValue value) {
//		return valuesOnData.contains(value);
//	}

	/*
	@Override
	public boolean containsValueOfAttributeValue(final AttributeValue value) {
//		if (this.containsInstanceOfAttributeValue(value)) { return true; }
		if (this.getInstanceOfAttributeValue(value) != null) { return true; }
		
		for (AttributeValue v : valuesOnData) { if (v.compareTo(value) == 0) return true; }
		
		return false;
	}
	*/
	
	@Override
	public AttributeValue getInstanceOfAttributeValue(final AttributeValue value) {
		if (valuesOnData.contains(value)) { return value; }
		
		for (AttributeValue v : valuesOnData) { if (v.compareTo(value) == 0) return v; }
		
		return null;
	}
	

	@Override public void clear() {
		valuesOnData.clear();
	}

	@Override
	public Set<AttributeValue> valuesOnData() {
		Set<AttributeValue> attributeValues = new HashSet<AttributeValue>();
		attributeValues.addAll(valuesOnData);
		
		return attributeValues;
	}

	@Override
	public AttributeValue findCorrespondingAttributeValueOnData(final List<String> stringValue) throws GenstarException {
		if (stringValue == null || stringValue.isEmpty()) { throw new GenstarException("'stringValue' parameter can not be null or empty"); }
		
		return getInstanceOfAttributeValue(new UniqueValue(dataType, stringValue.get(0), this));
	}
	
	@Override public AttributeValue findMatchingAttributeValueOnData(final AttributeValue attributeValue) throws GenstarException {
		if (attributeValue instanceof UniqueValue) { return this.getInstanceOfAttributeValue(attributeValue); }
		
		RangeValue rangeValue = (RangeValue) attributeValue;
		if (rangeValue.getDataType().isNumericValue() && this.dataType.isNumericValue()) {
			for (UniqueValue value : valuesOnData) {
				if (rangeValue.cover(value)) { return value; }
			}
		}
		
		return null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((valuesOnData == null) ? 0 : valuesOnData.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UniqueValuesAttribute other = (UniqueValuesAttribute) obj;
		if (valuesOnData == null) {
			if (other.valuesOnData != null)
				return false;
		} else if (!valuesOnData.equals(other.valuesOnData))
			return false;
		return true;
	}
}
