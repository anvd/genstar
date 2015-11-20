package ummisco.genstar.metamodel;

import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;


public interface ISyntheticPopulationGenerator { // FIXME change class name -> ISingleSyntheticPopulationGenerator
	
	public abstract void setID(final int id);
	
	public abstract int getID();

	public abstract String getGeneratorName();
	
	public abstract void setPopulationName(final String populationName);
	
	public abstract String getPopulationName();
	
	public abstract int getNbOfEntities();
	
	public abstract void setNbOfEntities(final int nbOfEntities);

	public abstract Set<AbstractAttribute> getAttributes();

	public abstract boolean containAttribute(final AbstractAttribute attribute);

	public abstract boolean containAttribute(final String attributeNameOnData);

	public abstract AbstractAttribute getAttribute(final String attributeNameOnData);

	public abstract void addAttribute(final AbstractAttribute attribute) throws GenstarException;

	public abstract void removeAttribute(final AbstractAttribute attribute);

	public abstract ISyntheticPopulation generate() throws GenstarException;
}
