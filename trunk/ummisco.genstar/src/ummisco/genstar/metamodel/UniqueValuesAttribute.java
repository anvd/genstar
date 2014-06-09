package ummisco.genstar.metamodel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;

public class UniqueValuesAttribute extends AbstractAttribute {
		
	
	private Set<UniqueValue> values;

	public UniqueValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String dataVarName, final DataType valueType) throws GenstarException {
		this(populationGenerator, dataVarName, dataVarName, valueType, UniqueValue.class);
	}

	public UniqueValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String dataVarName, final DataType valueType, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException {
		this(populationGenerator, dataVarName, dataVarName, valueType, valueClassOnEntity);
	}

	public UniqueValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String dataVarName, final String entityVarName, final DataType valueType) throws GenstarException {
		this(populationGenerator, dataVarName, entityVarName, valueType, UniqueValue.class);		
	}

	public UniqueValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String dataVarName, final String entityVarName, final DataType valueType, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException {
		super(populationGenerator, dataVarName, entityVarName, valueType, valueClassOnEntity);

		this.valueClassOnData = UniqueValue.class;
		this.values = new HashSet<UniqueValue>();
		try {
			this.setDefaultValue(valueClassOnData.getConstructor(DataType.class).newInstance(valueType));
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
		
		if (this.contains(value)) { return false; }
		
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

	@Override public boolean contains(final AttributeValue value) {
		return values.contains(value);
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
	public AttributeValue valueFromString(final List<String> stringValue) throws GenstarException {
		if (stringValue == null || stringValue.isEmpty()) { throw new GenstarException("'stringValue' parameter can not be null"); }
		
		return new UniqueValue(dataType, stringValue.get(0));
	}
	
}
