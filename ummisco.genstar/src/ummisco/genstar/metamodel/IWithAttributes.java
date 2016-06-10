package ummisco.genstar.metamodel;

import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;

public interface IWithAttributes {
	
	public abstract boolean containAttribute(final AbstractAttribute attribute) throws GenstarException;

	public abstract Set<AbstractAttribute> getAttributes();

	public abstract AbstractAttribute getAttributeByNameOnData(final String attributeNameOnData) throws GenstarException;
	
	public abstract AbstractAttribute getAttributeByNameOnEntity(final String attributeNameOnEntity) throws GenstarException;
}
