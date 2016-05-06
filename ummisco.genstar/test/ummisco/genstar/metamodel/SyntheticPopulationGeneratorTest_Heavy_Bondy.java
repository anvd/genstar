package ummisco.genstar.metamodel;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.population.IPopulation;

public class SyntheticPopulationGeneratorTest_Heavy_Bondy {

	private BondyData bondyData;
	
	
	public SyntheticPopulationGeneratorTest_Heavy_Bondy() throws GenstarException {
		bondyData = new BondyData();
	}
	
	@Test public void testGenerateFullInhabitantPopulation() throws GenstarException {
		ISyntheticPopulationGenerator inhabitantPopGenerator = bondyData.getInhabitantPopGenerator();
		IPopulation inhabitantPop = inhabitantPopGenerator.generate();
		
		assertTrue(inhabitantPopGenerator.getNbOfEntities() == inhabitantPop.getEntities().size());
	}
	
	@Test public void testGenerateFullHouseholdPopulation() throws GenstarException {
		ISyntheticPopulationGenerator householdPopGenerator = bondyData.getHouseholdPopGenerator();
		IPopulation householdPop = householdPopGenerator.generate();
		
		assertTrue(householdPopGenerator.getNbOfEntities() == householdPop.getEntities().size());
	}
}
