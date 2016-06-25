package ummisco.genstar.metamodel.generators;

import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.IWithAttributes;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.population.IPopulation;


public interface ISyntheticPopulationGenerator extends IWithAttributes {
	
	public abstract String getGeneratorName();
	
	public abstract void setPopulationName(final String populationName);
	
	public abstract String getPopulationName();
	
	public abstract int getNbOfEntities();
	
	public abstract void setNbOfEntities(final int nbOfEntities);

	public abstract void addAttribute(final AbstractAttribute attribute) throws GenstarException;

	public abstract void removeAttribute(final AbstractAttribute attribute);

	public abstract IPopulation generate() throws GenstarException;
	
	public abstract Map<String, String> getProperties();
	
	public abstract String getProperty(final String name);
	
	public abstract void setProperty(final String name, final String value) throws GenstarException;
}
