package ummisco.genstar.metamodel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ummisco.genstar.exception.AttributeException;

public class EnumerationOfRangesAttribute extends EnumerationValueAttribute {
	
	private Set<RangeValue> rangeValues;
	
	
	public EnumerationOfRangesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String nameOnData, final ValueType valueType) throws AttributeException {
		this(populationGenerator, nameOnData, nameOnData, valueType, RangeValue.class);
	}

	public EnumerationOfRangesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String nameOnData, final ValueType valueType, final Class<? extends AttributeValue> entityAttributeValueClass) throws AttributeException {
		this(populationGenerator, nameOnData, nameOnData, valueType, entityAttributeValueClass);
	}

	public EnumerationOfRangesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String nameOnData, final String nameOnEntity, final ValueType valueType) throws AttributeException {
		this(populationGenerator, nameOnData, nameOnEntity, valueType, RangeValue.class);
	}

	public EnumerationOfRangesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String nameOnData, final String nameOnEntity, final ValueType valueType, final Class<? extends AttributeValue> valueClassOnEntity) throws AttributeException {
		super(populationGenerator, nameOnData, nameOnEntity, valueType, valueClassOnEntity);

		if (!valueType.isNumericValue()) { throw new IllegalArgumentException(this.getClass().getName() + " only supports Double, Float and Integer value."); }
		
		this.valueClassOnData = RangeValue.class;
		this.rangeValues = new HashSet<RangeValue>();
		try {
			this.setDefaultValue(valueClassOnData.getConstructor(ValueType.class).newInstance(valueType));
		} catch (Exception e) {
			throw new AttributeException(e);
		}
	}
	
	// FIXME move up to parent class, use the "valueClassOnData" for the validation process
	@Override public boolean add(final AttributeValue rangeValue) throws AttributeException {
		if (rangeValue == null) { throw new AttributeException("'rangeValue' parameter can not be null."); }
		
		if (!rangeValue.getClass().equals(this.valueClassOnData)) { throw new AttributeException("'rangeValue' must be an instance of " + valueClassOnData.getSimpleName()); }
		
		if (!this.valueType.equals(rangeValue.valueType)) { throw new AttributeException("Incompatible valueType between rangeValue and attribute : " + rangeValue.valueType.getName() + " v.s. " + this.valueType.getName()); }
		
		if (this.contains(rangeValue)) { return false; }
		
		
		rangeValues.add((RangeValue) rangeValue);

		// fire event
		internalFireEvent();
		
		return true;
	}
	
	@Override public boolean addAll(final Set<AttributeValue> values) throws AttributeException {
		if (values == null) { throw new AttributeException("'values' parameter can not be null"); }
		
		boolean changed = false;
		for (AttributeValue v : values) {
			if (v instanceof RangeValue) {
				if (add(v)) { changed = true; }
			}
		}
		
		if (changed) { internalFireEvent(); }
		return changed;
	}
	
	@Override public boolean remove(final AttributeValue value)  {
		if (value == null) { return false; }
		
		if (value instanceof RangeValue) {
			AttributeValue target = null;
			for (RangeValue v : rangeValues) {
				if (value.compareTo(v) == 0) {
					target = v;
					break;
				}
			}
			
			if (target != null) {
				rangeValues.remove(target);
				internalFireEvent();
				return true;
			}
		}
		
		return false;
	}
	
	@Override public boolean contains(final AttributeValue value) {
		
		return rangeValues.contains(value);
		
//		if (value == null) { return false; }
//		if (!(value instanceof RangeValue)) { return false; }
//		
//		for (AttributeValue v : rangeValues) {
//			if (v.compareTo(value) == 0) { return true; }
//		}
//		
//		return false;
	}
	
	@Override public void clear() {
		rangeValues.clear();
	}
	
	@Override
	public Set<AttributeValue> values() throws AttributeException {
		Set<AttributeValue> attributeValues = new HashSet<AttributeValue>();
		attributeValues.addAll(rangeValues);

		return attributeValues;
	}

	@Override
	public AttributeValue valueFromString(final List<String> stringValue) throws AttributeException {
		if (stringValue == null || stringValue.isEmpty()) { throw new AttributeException("'stringValue' parameter can not be null"); }
		if (stringValue.size() < 2) { throw new IllegalArgumentException("length of 'stringValue' must be at least 2"); }
		
		return new RangeValue(valueType, stringValue.get(0), stringValue.get(1));
	}
}
