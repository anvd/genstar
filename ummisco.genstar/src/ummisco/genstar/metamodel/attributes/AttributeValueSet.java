package ummisco.genstar.metamodel.attributes;

import java.util.Set;

import ummisco.genstar.exception.GenstarException;

public interface AttributeValueSet {
	
	// TODO change to valuesOnData
	public abstract Set<AttributeValue> values();
	
	public abstract boolean add(final AttributeValue value) throws GenstarException;
	
	public abstract boolean addAll(final Set<AttributeValue> values) throws GenstarException;
	
	public abstract boolean remove(final AttributeValue value);
	
	public abstract boolean containsInstanceOfAttributeValue(final AttributeValue value);
	
	public abstract boolean containsValueOfAttributeValue(final AttributeValue value);
	
	public abstract AttributeValue getInstanceOfAttributeValue(final AttributeValue value);

	public abstract void clear();
}
