package ummisco.genstar.metamodel.attributes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;

public class RangeValuesAttribute extends AbstractAttribute {
	
	private Set<RangeValue> valuesOnData;
	
	
	public RangeValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String nameOnData, final DataType dataType) throws GenstarException {
		this(populationGenerator, nameOnData, nameOnData, dataType, RangeValue.class);
	}

	public RangeValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String nameOnData, final DataType dataType, final Class<? extends AttributeValue> entityAttributeValueClass) throws GenstarException {
		this(populationGenerator, nameOnData, nameOnData, dataType, entityAttributeValueClass);
	}

	public RangeValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String nameOnData, final String nameOnEntity, final DataType dataType) throws GenstarException {
		this(populationGenerator, nameOnData, nameOnEntity, dataType, RangeValue.class);
	}

	public RangeValuesAttribute(final ISyntheticPopulationGenerator populationGenerator, final String nameOnData, final String nameOnEntity, final DataType dataType, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException {
		super(populationGenerator, nameOnData, nameOnEntity, dataType, valueClassOnEntity);

		if (!dataType.isNumericValue()) { throw new IllegalArgumentException(this.getClass().getName() + " only supports Double, Float and Integer value."); }
		
		this.valueClassOnData = RangeValue.class;
		this.valuesOnData = new HashSet<RangeValue>();
		try {
			this.setDefaultValue(valueClassOnData.getConstructor(DataType.class).newInstance(dataType));
		} catch (Exception e) {
			throw new GenstarException(e);
		}
	}
	
	// FIXME move up to parent class, use the "valueClassOnData" for the validation process
	@Override public boolean add(final AttributeValue rangeValue) throws GenstarException {
		if (rangeValue == null) { throw new GenstarException("'rangeValue' parameter can not be null."); }
		
		if (!rangeValue.getClass().equals(this.valueClassOnData)) { throw new GenstarException("'rangeValue' must be an instance of " + valueClassOnData.getSimpleName()); }
		
		if (!this.dataType.equals(rangeValue.dataType)) { throw new GenstarException("Incompatible valueType between rangeValue and attribute : " + rangeValue.dataType.getName() + " v.s. " + this.dataType.getName()); }
		
//		if (this.containsInstanceOfAttributeValue(rangeValue) || this.containsValueOfAttributeValue(rangeValue)) { return false; }
//		if ((this.getInstanceOfAttributeValue(rangeValue) != null) || this.containsValueOfAttributeValue(rangeValue)) { return false; }
		if (this.getInstanceOfAttributeValue(rangeValue) != null) { return false; }
		
		valuesOnData.add((RangeValue) rangeValue);

		// fire event
		internalFireEvent();
		
		return true;
	}
	
	@Override public boolean addAll(final Set<AttributeValue> values) throws GenstarException {
		if (values == null) { throw new GenstarException("'values' parameter can not be null"); }
		
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
			for (RangeValue v : valuesOnData) {
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
		
		if (isIdentity) {
			if (stringValue.size() == 1) { return new RangeValue(dataType, stringValue.get(0), stringValue.get(0)); }
			if (stringValue.size() >= 2) { return new RangeValue(dataType, stringValue.get(0), stringValue.get(1)); }
			
			throw new GenstarException("Invalid stringValue " + stringValue);
		}
		
		if (stringValue.size() == 1) {
			for (AttributeValue v : valuesOnData) { if ( ((RangeValue) v).cover(stringValue.get(0)) ) return v; }
			return null;
		}
		
		return getInstanceOfAttributeValue(new RangeValue(dataType, stringValue.get(0), stringValue.get(1)));
	}

	@Override public AttributeValue findMatchingAttributeValueOnData(final AttributeValue attributeValue) throws GenstarException {
		if (attributeValue instanceof RangeValue) { return this.getInstanceOfAttributeValue(attributeValue); }
		
		UniqueValue uniqueValue = (UniqueValue) attributeValue;
		if (attributeValue.getDataType().isNumericValue()) {
			for (RangeValue value : valuesOnData ) {
				if (value.cover(uniqueValue)) { return value; }
			}
		}
		
		return null;
	}
}
