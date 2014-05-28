package ummisco.genstar.metamodel;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ummisco.genstar.exception.GenerationException;
import ummisco.genstar.exception.GenstarException;

public class SyntheticPopulationGeneratorTest_Heavy_Bondy {

	private BondyData bondyData;
	
	
	public SyntheticPopulationGeneratorTest_Heavy_Bondy() throws GenstarException {
		bondyData = new BondyData();
	}
	
	@Test public void testGenerateFullInhabitantPopulation() throws GenerationException {
		ISyntheticPopulationGenerator inhabitantPopGenerator = bondyData.getInhabitantPopGenerator();
		ISyntheticPopulation inhabitantPop = inhabitantPopGenerator.generate();
		
		assertTrue(inhabitantPopGenerator.getNbOfEntities() == inhabitantPop.getEntities().size());
	}
	
	@Test public void testGenerateFullHouseholdPopulation() throws GenerationException {
		ISyntheticPopulationGenerator householdPopGenerator = bondyData.getHouseholdPopGenerator();
		ISyntheticPopulation householdPop = householdPopGenerator.generate();
		
		assertTrue(householdPopGenerator.getNbOfEntities() == householdPop.getEntities().size());
	}
}
