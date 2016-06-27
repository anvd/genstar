package ummisco.genstar.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.util.GenstarCsvFile;

public class FrequencyDistributionPopulationGenerator3 {

	public FrequencyDistributionPopulationGenerator3() {}
	
	public static void main(String[] args) throws Exception {
		String propertiesFilePath = "example_data/FrequencyDistributionPopulationGenerator3/HouseholdPopulation_Experiment_3.properties";
		
		// 0. Load the property file
		Properties frequencyDistributionPopulationProperties = GenstarService.loadPropertyFile(propertiesFilePath);
		
		// 1. Build the list of frequency distribution generation rule file paths
		List<String> frequencyDistributionGenerationRuleFilePaths = new ArrayList<String>();
		GenstarCsvFile householdGenerationRulesFile = new GenstarCsvFile("example_data/FrequencyDistributionPopulationGenerator3/Household_GenerationRules.csv", false);
		for (List<String> householdGenerationRulesFileRow : householdGenerationRulesFile.getContent()) {
			frequencyDistributionGenerationRuleFilePaths.add(householdGenerationRulesFileRow.get(0));
		}
		
		// 2. Generate the population
		IPopulation population = GenstarService.generateFrequencyDistributionPopulation(frequencyDistributionPopulationProperties, frequencyDistributionGenerationRuleFilePaths);
		
		
		// 3. Print population information
		System.out.println("Population name: " + population.getName());
		System.out.println("Number of entities:" + population.getNbOfEntities());
		System.out.println("Open file(s) in \'example_data/FrequencyDistributionPopulationGenerator3/result_analysis/\' folder to look at the generation result analysis");
	}
	 
}
