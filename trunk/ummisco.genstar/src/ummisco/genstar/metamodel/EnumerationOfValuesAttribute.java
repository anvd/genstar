package ummisco.genstar.metamodel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ummisco.genstar.exception.AttributeException;

public class EnumerationOfValuesAttribute extends EnumerationValueAttribute {
	
	private Set<UniqueValue> values;

	public EnumerationOfValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String dataVarName, final ValueType valueType) throws AttributeException {
		this(populationGenerator, dataVarName, dataVarName, valueType, UniqueValue.class);
	}

	public EnumerationOfValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String dataVarName, final ValueType valueType, final Class<? extends AttributeValue> valueClassOnEntity) throws AttributeException {
		this(populationGenerator, dataVarName, dataVarName, valueType, valueClassOnEntity);
	}

	public EnumerationOfValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String dataVarName, final String entityVarName, final ValueType valueType) throws AttributeException {
		this(populationGenerator, dataVarName, entityVarName, valueType, UniqueValue.class);		
	}

	public EnumerationOfValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String dataVarName, final String entityVarName, final ValueType valueType, final Class<? extends AttributeValue> valueClassOnEntity) throws AttributeException {
		super(populationGenerator, dataVarName, entityVarName, valueType, valueClassOnEntity);

		this.valueClassOnData = UniqueValue.class;
		this.values = new HashSet<UniqueValue>();
		try {
			this.setDefaultValue(valueClassOnData.getConstructor(ValueType.class).newInstance(valueType));
		} catch (final Exception e) {
			throw new AttributeException(e);
		}
	}
	
	public void addStringValue(final String stringValue) throws AttributeException {
		if (stringValue == null || stringValue.trim().length() == 0) { throw new IllegalArgumentException("'stringValue' parameter can neither be null nor empty"); }
		
		if (!valueType.isValueValid(stringValue)) { throw new IllegalArgumentException("'" + stringValue + "' is not a(n) " + valueType.getName() + " value."); }
		
		values.add(new UniqueValue(valueType, stringValue));

		// fire event
		internalFireEvent();
	}
	
	@Override public boolean add(final AttributeValue value) throws AttributeException {
		if (value == null) { throw new AttributeException("'value' parameter can not be null"); }
		if (!(value.getClass().equals(valueClassOnData))) { throw new AttributeException("value must be an instance of " + valueClassOnData.getName()); }
		if (!this.valueType.equals(value.valueType)) { throw new AttributeException("Incompatible valueType"); }
		
		if (this.contains(value)) { return false; }
		
		values.add((UniqueValue)value);

		// fire event
		internalFireEvent();
		return true;
	}
	
	@Override public boolean addAll(final Set<AttributeValue> values) throws AttributeException {
		if (values == null) { throw new AttributeException("'values' parameter can not be null"); }
		
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

	@Override public boolean contains(final AttributeValue value) {
		
		return values.contains(value);
		
//		if (value == null) { return false; }
//		if (!(value instanceof UniqueValue)) { return false; }
//		
//		for (AttributeValue v : values) {
//			if (v.compareTo(value) == 0) { return true; }
//		}
//		
//		return false;
	}

	@Override public void clear() {
		values.clear();
	}

	@Override
	public Set<AttributeValue> values() throws AttributeException {
		Set<AttributeValue> attributeValues = new HashSet<AttributeValue>();
		attributeValues.addAll(values);
		
		return attributeValues;
	}

	@Override
	public AttributeValue valueFromString(final List<String> stringValue) throws AttributeException {
		if (stringValue == null || stringValue.isEmpty()) { throw new AttributeException("'stringValue' parameter can not be null"); }
		
		return new UniqueValue(valueType, stringValue.get(0));
	}
	
}
