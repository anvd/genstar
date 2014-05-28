package ummisco.genstar.metamodel;

import java.util.Set;

import ummisco.genstar.exception.AttributeException;

public abstract class EnumerationValueAttribute extends AbstractAttribute {

	public EnumerationValueAttribute(final ISyntheticPopulationGenerator populationGenerator, final String dataAttributeName,
			final String entityAttributeName, ValueType valueType, final Class<? extends AttributeValue> valueClassOnEntity) throws AttributeException {
		super(populationGenerator, dataAttributeName, entityAttributeName, valueType, valueClassOnEntity);
	}

	public abstract Set<AttributeValue> values() throws AttributeException;
	
	public abstract boolean add(final AttributeValue value) throws AttributeException;
	
	public abstract boolean addAll(final Set<AttributeValue> values) throws AttributeException;
	
	public abstract boolean remove(final AttributeValue value);
	
	public abstract boolean contains(final AttributeValue value);

	public abstract void clear();
	
}
