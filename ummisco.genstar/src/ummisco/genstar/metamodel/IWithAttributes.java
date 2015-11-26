package ummisco.genstar.metamodel;

import java.util.List;

import ummisco.genstar.metamodel.attributes.AbstractAttribute;

public interface IWithAttributes {
	
	public abstract List<AbstractAttribute> getAttributes();

	public abstract AbstractAttribute getAttribute(final String attributeNameOnData);
}
