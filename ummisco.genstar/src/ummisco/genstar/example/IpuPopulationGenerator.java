package ummisco.genstar.example;

import java.util.List;
import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;

public class IpuPopulationGenerator {

	public IpuPopulationGenerator() {}
	
	public static void main(String[] args) throws Exception {
		String propertiesFilePath = "example_data/IpuPopulationGenerator/IpuPopulationProperties.properties";
		
		// 0. Load the property file
		Properties ipuPopulationProperties = GenstarService.loadPropertyFile(propertiesFilePath);
		
		
		// 1. Generate the population
		IPopulation compoundPopulation = GenstarService.generateIpuPopulation(ipuPopulationProperties);
		
		
		// 2. Print population information
		System.out.println("Group population name: " + compoundPopulation.getName());
		System.out.println("Number of group entities:" + compoundPopulation.getNbOfEntities());
		
		
		int totalNumberOfComponentEntities = 0;
		List<Entity> groupEntities = compoundPopulation.getEntities();
		for (Entity groupE : groupEntities) {
			List<IPopulation> componentPopulations = groupE.getComponentPopulations();
			if (!componentPopulations.isEmpty()) {
				EntityAttributeValue householdID = groupE.getEntityAttributeValueByNameOnData("HouseholdID");
				System.out.println("Detail of " + compoundPopulation.getName() + " entity with householdID = " + householdID.getAttributeValueOnEntity().toCsvString());
				for (IPopulation componentPop : componentPopulations) {
					System.out.println("\tComponent population name: " + componentPop.getName());
					System.out.println("\tComponent population's number of entities: " + componentPop.getNbOfEntities());
					
					totalNumberOfComponentEntities += componentPop.getNbOfEntities();
				}
			}
		}
		System.out.println("Total number of component's entities: " + totalNumberOfComponentEntities);
		
		
		System.out.println("Open file(s) in \'example_data/IpuPopulationGenerator/result_analysis/\' folder to look at the generation result analysis");
	}
}
