package ummisco.genstar.metamodel;

import java.util.Set;

import ummisco.genstar.exception.GenstarException;

public interface AttributeValueSet {
	
	public abstract Set<AttributeValue> values();
	
	public abstract boolean add(final AttributeValue value) throws GenstarException;
	
	public abstract boolean addAll(final Set<AttributeValue> values) throws GenstarException;
	
	public abstract boolean remove(final AttributeValue value);
	
	public abstract boolean contains(final AttributeValue value);

	public abstract void clear();

}
