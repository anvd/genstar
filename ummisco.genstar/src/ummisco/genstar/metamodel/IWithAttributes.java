package ummisco.genstar.metamodel;

import java.util.List;

import ummisco.genstar.metamodel.attributes.AbstractAttribute;

public interface IWithAttributes { // change to IPopulationAttributes
	
	public abstract List<AbstractAttribute> getAttributes();

	public abstract AbstractAttribute getAttributeByNameOnData(final String attributeNameOnData);
}
