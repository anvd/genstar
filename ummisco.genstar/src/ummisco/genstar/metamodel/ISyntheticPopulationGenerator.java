package ummisco.genstar.metamodel;

import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;


public interface ISyntheticPopulationGenerator extends IWithAttributes { // FIXME change class name -> ISingleSyntheticPopulationGenerator
	
	public abstract void setID(final int id);
	
	public abstract int getID();

	public abstract String getGeneratorName();
	
	public abstract void setPopulationName(final String populationName);
	
	public abstract String getPopulationName();
	
	public abstract int getNbOfEntities();
	
	public abstract void setNbOfEntities(final int nbOfEntities);

	// consider to move these methods to IWithAttributes +
	public abstract boolean containAttribute(final AbstractAttribute attribute);

	public abstract boolean containAttribute(final String attributeNameOnData);

	public abstract void addAttribute(final AbstractAttribute attribute) throws GenstarException;

	public abstract void removeAttribute(final AbstractAttribute attribute);
	// consider to move these methods to IWithAttributes -

	public abstract ISyntheticPopulation generate() throws GenstarException;
	
	public abstract Map<String, String> getProperties();
	
	public abstract String getProperty(final String name);
	
	public abstract void setProperty(final String name, final String value) throws GenstarException;
}
