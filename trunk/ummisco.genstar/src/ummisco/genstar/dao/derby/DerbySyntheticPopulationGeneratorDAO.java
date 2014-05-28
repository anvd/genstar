package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.SyntheticPopulationGeneratorDAO;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.BondyData;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public class DerbySyntheticPopulationGeneratorDAO extends AbstractDerbyDAO implements SyntheticPopulationGeneratorDAO {

	public DerbySyntheticPopulationGeneratorDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

	@Override
	public ISyntheticPopulationGenerator findSyntheticPopulationGenerator(final String populationGeneratorName) throws GenstarDAOException {
		
		// mockup for testing
		try {
			BondyData bondyData = new BondyData();
			return bondyData.getHouseholdPopGenerator();
		} catch (GenstarException e) {
			throw new GenstarDAOException(e);
		}
		
		
		
		// TODO make a "real" implementation
		
	}

}
