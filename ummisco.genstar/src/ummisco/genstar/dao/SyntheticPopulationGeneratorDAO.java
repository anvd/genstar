package ummisco.genstar.dao;

import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public interface SyntheticPopulationGeneratorDAO {

	public abstract ISyntheticPopulationGenerator findSyntheticPopulationGenerator(final String populationGeneratorName) throws GenstarDAOException;

}
