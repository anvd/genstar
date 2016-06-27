package ummisco.genstar.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.util.GenstarUtils;

public class PopulationSaver3 {

	public PopulationSaver3() {}
	
	public static void main(String[] args) throws Exception {
		String randomCompoundPopulationPropertiesFilePath = "example_data/PopulationSaver3/RandomCompoundPopulationProperties.properties";
		
		// 0. Load the property file
		Properties compoundPopulationProperties = GenstarService.loadPropertyFile(randomCompoundPopulationPropertiesFilePath);
		
		
		// 1. Generate the compound population
		IPopulation compoundPopulation = GenstarService.generateRandomCompoundPopulation(compoundPopulationProperties);
		
		
		// 2. Save the population to CSV files
		Map<String, String> populationOutputFilePaths = new HashMap<String, String>(); 
		populationOutputFilePaths.put("household", "example_data/PopulationSaver3/household_population.csv");
		populationOutputFilePaths.put("people", "example_data/PopulationSaver3/people_population.csv");
		
		Map<String, String> savedPopulationFilePaths = GenstarUtils.writePopulationToCsvFile(compoundPopulation, populationOutputFilePaths);
		
		for (String populationName : savedPopulationFilePaths.keySet()) {
			System.out.println("\'" + populationName + "\' population is saved to \'" + savedPopulationFilePaths.get(populationName) + "\'");
		}
		System.out.println("Please open those files to observe the result");
		
	}	 
	 
}
