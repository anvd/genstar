package ummisco.genstar.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.util.GenstarUtils;

public class PopulationSaver2 {

	public PopulationSaver2() {}
	
	public static void main(String[] args) throws Exception {
		String randomHouseholdPopulationPropertiesFilePath = "example_data/PopulationSaver2/RandomHouseholdPopulationProperties.properties";
		
		// 0. Load the property file
		Properties householdPopulationProperties = GenstarService.loadPropertyFile(randomHouseholdPopulationPropertiesFilePath);
		
		
		// 1. Generate the population
		IPopulation householdPopulation = GenstarService.generateRandomSinglePopulation(householdPopulationProperties);
		
		
		// 2. Save the population to CSV files
		Map<String, String> populationOutputFilePaths = new HashMap<String, String>(); 
		populationOutputFilePaths.put("household", "example_data/PopulationSaver2/household_population.csv");
		
		Map<String, String> savedPopulationFilePaths = GenstarUtils.writePopulationToCsvFile(householdPopulation, populationOutputFilePaths);
		
		for (String populationName : savedPopulationFilePaths.keySet()) {
			System.out.println("\'" + populationName + "\' population is saved to \'" + savedPopulationFilePaths.get(populationName) + "\'");
		}
		System.out.println("Please open those files to observe the result");
		
	}	 
}
