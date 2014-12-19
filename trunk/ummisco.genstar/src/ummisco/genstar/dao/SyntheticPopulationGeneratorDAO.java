package ummisco.genstar.dao;

import java.util.List;

import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public interface SyntheticPopulationGeneratorDAO {

	public abstract ISyntheticPopulationGenerator findSyntheticPopulationGeneratorByName(final String populationGeneratorName) throws GenstarDAOException;
	
	public abstract void createSyntheticPopulationGenerator(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException;
	
	public abstract void updateSyntheticPopulationGenerator(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException;
	
	public abstract void deleteSyntheticPopulationGenerator(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException;
	
	public abstract List<String> getPopulationGeneratorNames() throws GenstarDAOException;
}
