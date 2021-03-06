package ummisco.genstar.metamodel;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.population.IPopulation;

public class SyntheticPopulationGeneratorTest_Bondy { 
	
	private BondyData bondyData;
	
	public SyntheticPopulationGeneratorTest_Bondy() throws GenstarException {
		bondyData = new BondyData();
	}

	@Test public void testGenerateBondyInhabitantPopulation() throws GenstarException {
		ISyntheticPopulationGenerator inhabitantPopulationGenerator = bondyData.getInhabitantPopGenerator();
		inhabitantPopulationGenerator.setNbOfEntities(10);
		
		IPopulation population = inhabitantPopulationGenerator.generate();
		assertTrue(population.getEntities().size() == 10);
	}
	
	@Test public void testGenerateBondyHouseholdPopulation() throws GenstarException {
		ISyntheticPopulationGenerator householdPopulationGenerator = bondyData.getHouseholdPopGenerator();
		householdPopulationGenerator.setNbOfEntities(10);
		
		IPopulation population = householdPopulationGenerator.generate();
		assertTrue(population.getEntities().size() == 10);
	}

	
}
