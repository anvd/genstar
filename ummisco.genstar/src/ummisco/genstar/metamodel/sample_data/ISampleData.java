package ummisco.genstar.metamodel.sample_data;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;

public interface ISampleData {

	public abstract IPopulation getSampleEntityPopulation();

	public abstract void recodeIdAttributes(final Entity targetEntity) throws GenstarException;
}
