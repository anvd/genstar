package ummisco.genstar;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

import org.junit.Test;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.BondyData;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.IPopulationsLinker;
import ummisco.genstar.smach.SmachStupidPopLinker;

public class GenstarGeneratorTest_Bondy {
	

	@Test public void testGenstarGenerator1() throws GenstarException {
		BondyData bondyData = new BondyData();
		
		ISyntheticPopulationGenerator inhabitantPopGenerator = bondyData.getInhabitantPopGenerator();
		ISyntheticPopulationGenerator householdPopGenerator = bondyData.getHouseholdPopGenerator();
		IPopulationsLinker smachStupidPopLinker = new SmachStupidPopLinker();
		
		GenstarGenerator generator = new GenstarGenerator();
		generator.addPopulationGenerator(inhabitantPopGenerator);
		generator.addPopulationGenerator(householdPopGenerator);
		
		generator.setPopulationsLiker(smachStupidPopLinker);
		
		long start = System.currentTimeMillis();
		generator.run();
		long end = System.currentTimeMillis();
		System.out.println("Generation duration : " + ( (end - start) / 1000 ) + " seconds");
		
		List<Entity> pickedInhabitants = new ArrayList<Entity>();
		ISyntheticPopulation inhabitantPopulation = null, householdPopulation = null;
		List<ISyntheticPopulation> concernedPopulations = smachStupidPopLinker.getPopulations();
		if (concernedPopulations.get(0).getName().equals("Population of Bondy's Inhabitants")) {
			inhabitantPopulation = concernedPopulations.get(0);
			householdPopulation = concernedPopulations.get(1);
		} else {
			inhabitantPopulation = concernedPopulations.get(1);
			householdPopulation = concernedPopulations.get(0);
		}
		
		for (Entity householdEntity : householdPopulation.getEntities()) {
			pickedInhabitants.addAll(householdEntity.getMembers());
		}
		
		List<Entity> unpickedInhabitants = new ArrayList<Entity>(inhabitantPopulation.getEntities());
		unpickedInhabitants.removeAll(pickedInhabitants);
		assertTrue(pickedInhabitants.size() > 0);
		assertTrue(unpickedInhabitants.size() + pickedInhabitants.size() == inhabitantPopulation.getInitialNbOfEntities());
		
	}

	@Test public void testGenstarGenerator2() throws GenstarException {
		BondyData bondyData = new BondyData();
		
		ISyntheticPopulationGenerator inhabitantPopGenerator = bondyData.getInhabitantPopGenerator();
		ISyntheticPopulationGenerator householdPopGenerator = bondyData.getHouseholdPopGenerator();
		IPopulationsLinker smachStupidPopLinker = new SmachStupidPopLinker();
		
		GenstarGenerator generator = new GenstarGenerator();
		generator.addPopulationGenerator(inhabitantPopGenerator);
		generator.addPopulationGenerator(householdPopGenerator);
		
		generator.setPopulationsLiker(smachStupidPopLinker);
		generator.setTotalRound(5);
		
		long start = System.currentTimeMillis();
		generator.run();
		long end = System.currentTimeMillis();
		System.out.println("Generation duration : " + ( (end - start) / 1000 ) + " seconds");
		
		List<Entity> pickedInhabitants = new ArrayList<Entity>();
		ISyntheticPopulation inhabitantPopulation = null, householdPopulation = null;
		List<ISyntheticPopulation> concernedPopulations = smachStupidPopLinker.getPopulations();
		if (concernedPopulations.get(0).getName().equals("Population of Bondy's Inhabitants")) {
			inhabitantPopulation = concernedPopulations.get(0);
			householdPopulation = concernedPopulations.get(1);
		} else {
			inhabitantPopulation = concernedPopulations.get(1);
			householdPopulation = concernedPopulations.get(0);
		}
		
		for (Entity householdEntity : householdPopulation.getEntities()) {
			pickedInhabitants.addAll(householdEntity.getMembers());
		}
		
		List<Entity> unpickedInhabitants = new ArrayList<Entity>(inhabitantPopulation.getEntities());
		unpickedInhabitants.removeAll(pickedInhabitants);
		assertTrue(pickedInhabitants.size() > 0);
		assertTrue(unpickedInhabitants.size() + pickedInhabitants.size() == inhabitantPopulation.getInitialNbOfEntities());
	}
}
