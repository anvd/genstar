package ummisco.genstar.dao.derby;

import org.junit.Test;

import ummisco.genstar.dao.GenstarDAOFactory;
import ummisco.genstar.dao.SyntheticPopulationGeneratorDAO;
import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public class DerbySyntheticPopulationGeneratorDAOTest {
	
	private GenstarDAOFactory daoFactory = null;
	private SyntheticPopulationGeneratorDAO syntheticPopulationGeneratorDAO = null;

	
	public DerbySyntheticPopulationGeneratorDAOTest() throws GenstarDAOException {
		daoFactory = GenstarDAOFactory.getDAOFactory();
		syntheticPopulationGeneratorDAO = daoFactory.getSyntheticPopulationGeneratorDAO();
	}
	
	@Test public void testCreateSyntheticPopulationGenerator() throws GenstarException {
		BondyData bondyData = new BondyData();
		ISyntheticPopulationGenerator bondyHhouseholdPopulationGenerator = bondyData.getHouseholdPopGenerator();
		syntheticPopulationGeneratorDAO.createSyntheticPopulationGenerator(bondyHhouseholdPopulationGenerator);
	}
}
