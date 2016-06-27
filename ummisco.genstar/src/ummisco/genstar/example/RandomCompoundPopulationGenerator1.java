package ummisco.genstar.example;

import java.util.List;
import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;

public class RandomCompoundPopulationGenerator1 {

	public RandomCompoundPopulationGenerator1() {}
	
	public static void main(String[] args) throws Exception {
		String randomCompoundPopulationPropertiesFilePath = "example_data/RandomCompoundPopulationGenerator1/RandomCompoundPopulationProperties1.properties";
		
		// 0. Load the property file
		Properties compoundPopulationProperties = GenstarService.loadPropertyFile(randomCompoundPopulationPropertiesFilePath);
		
		
		// 1. Generate the population
		IPopulation compoundPopulation = GenstarService.generateRandomCompoundPopulation(compoundPopulationProperties);
		
		
		// 2. Print the (generated) population information
		System.out.println("Group population name: " + compoundPopulation.getName());
		System.out.println("Number of group entities: " + compoundPopulation.getNbOfEntities());
		List<AbstractAttribute> attributes = compoundPopulation.getAttributes();
		System.out.println("Number of group entity's attributes: " + attributes.size());
		for (AbstractAttribute attribute : attributes) {
			System.out.println("\t" + attribute.toString());
		}
		
		IPopulation componentPopulation = null;
		int totalNumberOfComponentEntities = 0;
		List<Entity> groupEntities = compoundPopulation.getEntities();
		for (Entity groupE : groupEntities) {
			List<IPopulation> componentPopulations = groupE.getComponentPopulations();
			if (!componentPopulations.isEmpty()) {
				if (componentPopulation == null) { componentPopulation = componentPopulations.get(0); } 
				for (IPopulation componentPop : componentPopulations) {
					totalNumberOfComponentEntities += componentPop.getNbOfEntities();
				}
			}
		}
		System.out.println("Component population name: " + componentPopulation.getName());
		System.out.println("Number of component's entities: " + totalNumberOfComponentEntities);
		List<AbstractAttribute> componentAttributes = componentPopulation.getAttributes();
		System.out.println("Number of component entity's attributes: " + componentAttributes.size());
		for (AbstractAttribute attribute : componentAttributes) {
			System.out.println("\t" + attribute.toString());
		}
	}	  
	 
	
}
