package ummisco.genstar.metamodel.generators;

import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.IWithAttributes;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.population.IPopulation;


public interface ISyntheticPopulationGenerator extends IWithAttributes {
	
	/**
	 * Unused method
	 * @param id
	 */
	@Deprecated
	public abstract void setID(final int id);
	
	/**
	 * Unused method
	 * @return
	 */
	@Deprecated
	public abstract int getID();

	/**
	 * Only used for consol print and test / debug
	 * @return
	 */
	@Deprecated
	public abstract String getGeneratorName();
	
	public abstract void setPopulationName(final String populationName);
	
	public abstract String getPopulationName();
	
	public abstract int getNbOfEntities();
	
	public abstract void setNbOfEntities(final int nbOfEntities);

	/**
	 * Add {@code attribute} to the {@link AbstractAttribute} set. This set describe attributes
	 * of entity to synthesis
	 * 
	 * @param attribute
	 * @return <code>true</code> if attribute is actually added, <code>false</code> otherwise
	 * @throws GenstarException
	 */
	public abstract boolean addAttribute(final AbstractAttribute attribute) throws GenstarException;

	/**
	 * Remove {@code attribute} from {@link AbstractAttribute} set. This set represent attributes
	 * of entity to synthesis
	 * 
	 * @param attribute
	 * @return <code>true</code> if attribute has been remove, <code>false</code> otherwise
	 */
	public abstract boolean removeAttribute(final AbstractAttribute attribute);

	public abstract IPopulation generate() throws GenstarException;
	
	public abstract Map<String, String> getProperties();
	
	public abstract String getProperty(final String name);
	
	public abstract void setProperty(final String name, final String value) throws GenstarException;
}
