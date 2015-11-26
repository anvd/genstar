package ummisco.genstar.metamodel.attributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public class UniqueValuesAttribute extends AbstractAttribute {
		
	
	private Set<UniqueValue> values;

	public UniqueValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String nameOnData, final DataType dataType) throws GenstarException {
		this(populationGenerator, nameOnData, nameOnData, dataType, UniqueValue.class);
	}

	public UniqueValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String nameOnData, final DataType dataType, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException {
		this(populationGenerator, nameOnData, nameOnData, dataType, valueClassOnEntity);
	}

	public UniqueValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String nameOnData, final String nameOnEntity, final DataType dataType) throws GenstarException {
		this(populationGenerator, nameOnData, nameOnEntity, dataType, UniqueValue.class);		
	}

	public UniqueValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String nameOnData, final String nameOnEntity, final DataType dataType, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException {
		super(populationGenerator, nameOnData, nameOnEntity, dataType, valueClassOnEntity);

		this.valueClassOnData = UniqueValue.class;
		this.values = new HashSet<UniqueValue>();
		try {
			this.setDefaultValue(valueClassOnData.getConstructor(DataType.class).newInstance(dataType));
		} catch (final Exception e) {
			throw new GenstarException(e);
		}
	}
	
	public void addStringValue(final String stringValue) throws GenstarException {
		if (stringValue == null || stringValue.trim().length() == 0) { throw new IllegalArgumentException("'stringValue' parameter can neither be null nor empty"); }
		
		if (!dataType.isValueValid(stringValue)) { throw new IllegalArgumentException("'" + stringValue + "' is not a(n) " + dataType.getName() + " value."); }
		
		values.add(new UniqueValue(dataType, stringValue));

		// fire event
		internalFireEvent();
	}
	
	@Override public boolean add(final AttributeValue value) throws GenstarException {
		if (value == null) { throw new GenstarException("'value' parameter can not be null"); }
		if (!(value.getClass().equals(valueClassOnData))) { throw new GenstarException("value must be an instance of " + valueClassOnData.getName()); }
		if (!this.dataType.equals(value.dataType)) { throw new GenstarException("Incompatible valueType"); }
		
		if (this.containsInstanceOfAttributeValue(value) || this.containsValueOfAttributeValue(value)) { return false; }
		
		// FIXME containsValue
		
		values.add((UniqueValue)value);

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
			for (UniqueValue v : values) {
				if (value.compareTo(v) == 0) {
					target = v;
					break;
				}
			}
			
			if (target != null) {
				values.remove(target);
				internalFireEvent();
				return true;
			}
		}
		
		return false;
	}

	@Override public boolean containsInstanceOfAttributeValue(final AttributeValue value) {
		return values.contains(value);
	}

	@Override
	public boolean containsValueOfAttributeValue(final AttributeValue value) {
		if (this.containsInstanceOfAttributeValue(value)) { return true; }
		
		for (AttributeValue v : values) { if (v.compareTo(value) == 0) return true; }
		
		return false;
	}
	
	@Override
	public AttributeValue getInstanceOfAttributeValue(final AttributeValue value) {
		if (values.contains(value)) { return value; }
		
		for (AttributeValue v : values) { if (v.compareTo(value) == 0) return v; }
		
		return null;
	}
	

	@Override public void clear() {
		values.clear();
	}

	@Override
	public Set<AttributeValue> values() {
		Set<AttributeValue> attributeValues = new HashSet<AttributeValue>();
		attributeValues.addAll(values);
		
		return attributeValues;
	}

	@Override
	public AttributeValue findCorrespondingAttributeValue(final List<String> stringValue) throws GenstarException {
		if (stringValue == null || stringValue.isEmpty()) { throw new GenstarException("'stringValue' parameter can not be null or empty"); }
		
		if (isIdentity) {
			if (stringValue.size() == 1) { return new UniqueValue(dataType, stringValue.get(0)); }
		}
		
		return getInstanceOfAttributeValue(new UniqueValue(dataType, stringValue.get(0)));
	}
	
}
