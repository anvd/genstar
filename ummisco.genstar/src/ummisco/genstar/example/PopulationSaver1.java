package ummisco.genstar.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.util.GenstarUtils;

public class PopulationSaver1 {

	
	public PopulationSaver1() {}
	
	public static void main(String[] args) throws Exception {
		String randomPeoplePopulationPropertiesFilePath = "example_data/PopulationSaver1/RandomPeoplePopulationProperties.properties";
		
		// 0. Load the property file
		Properties peoplePopulationProperties = GenstarService.loadPropertyFile(randomPeoplePopulationPropertiesFilePath);
		
		
		// 1. Generate the population
		IPopulation peoplePopulation = GenstarService.generateRandomSinglePopulation(peoplePopulationProperties);
		
		
		// 2. Save the population to CSV files
		Map<String, String> populationOutputFilePaths = new HashMap<String, String>(); 
		populationOutputFilePaths.put("people", "example_data/PopulationSaver1/people_population.csv");
		
		Map<String, String> savedPopulationFilePaths = GenstarUtils.writePopulationToCsvFile(peoplePopulation, populationOutputFilePaths);
		
		for (String populationName : savedPopulationFilePaths.keySet()) {
			System.out.println("\'" + populationName + "\' population is saved to \'" + savedPopulationFilePaths.get(populationName) + "\'");
		}
		System.out.println("Please open those files to observe the result");
		
	}	 
	 
}
