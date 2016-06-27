package ummisco.genstar.example;

import java.util.List;
import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.population.IPopulation;

public class RandomPopulationGenerator1 {

	public RandomPopulationGenerator1() {}
	
	public static void main(String[] args) throws Exception {
		String randomHouseholdPopulationPropertiesFilePath = "example_data/RandomPopulationGenerator1/RandomHouseholdPopulationProperties.properties";
		
		// 0. Load the property file
		Properties householdPopulationProperties = GenstarService.loadPropertyFile(randomHouseholdPopulationPropertiesFilePath);
		
		
		// 1. Generate the population
		IPopulation householdPopulation = GenstarService.generateRandomSinglePopulation(householdPopulationProperties);
		
		
		// 2. Print the (generated) population information
		System.out.println("Population name: " + householdPopulation.getName());
		System.out.println("Number of entities: " + householdPopulation.getNbOfEntities());
		List<AbstractAttribute> attributes = householdPopulation.getAttributes();
		System.out.println("Number of attributes: " + attributes.size());
		for (AbstractAttribute attribute : attributes) {
			System.out.println("\t" + attribute.toString());
		}
	}	  
}
