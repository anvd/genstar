package ummisco.genstar.example;

import java.util.List;
import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.population.IPopulation;

public class RandomPopulationGenerator2 {

	public RandomPopulationGenerator2() {}
	
	public static void main(String[] args) throws Exception {
		String randomPeoplePopulationPropertiesFilePath = "example_data/RandomPopulationGenerator2/RandomPeoplePopulationProperties.properties";
		
		// 0. Load the property file
		Properties peoplePopulationProperties = GenstarService.loadPropertyFile(randomPeoplePopulationPropertiesFilePath);
		
		
		// 1. Generate the population
		IPopulation peoplePopulation = GenstarService.generateRandomSinglePopulation(peoplePopulationProperties);
		
		
		// 2. Print the (generated) population information
		System.out.println("Population name: " + peoplePopulation.getName());
		System.out.println("Number of entities: " + peoplePopulation.getNbOfEntities());
		List<AbstractAttribute> attributes = peoplePopulation.getAttributes();
		System.out.println("Number of attributes: " + attributes.size());
		for (AbstractAttribute attribute : attributes) {
			System.out.println("\t" + attribute.toString());
		}
	}	  
}
