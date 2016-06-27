package ummisco.genstar.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.util.GenstarCsvFile;

public class FrequencyDistributionPopulationGenerator4 {

	public FrequencyDistributionPopulationGenerator4() {}
	
	public static void main(String[] args) throws Exception {
		String propertiesFilePath = "example_data/FrequencyDistributionPopulationGenerator4/PeoplePopulation_Experiment_4.properties";
		
		// 0. Load the property file
		Properties frequencyDistributionPopulationProperties = GenstarService.loadPropertyFile(propertiesFilePath);
		
		// 1. Build the list of frequency distribution generation rule file paths
		List<String> frequencyDistributionGenerationRuleFilePaths = new ArrayList<String>();
		GenstarCsvFile peopleGenerationRulesFile = new GenstarCsvFile("example_data/FrequencyDistributionPopulationGenerator4/People_GenerationRules.csv", false);
		for (List<String> peopleGenerationRulesFileRow : peopleGenerationRulesFile.getContent()) {
			frequencyDistributionGenerationRuleFilePaths.add(peopleGenerationRulesFileRow.get(0));
		}
		
		// 2. Generate the population
		IPopulation population = GenstarService.generateFrequencyDistributionPopulation(frequencyDistributionPopulationProperties, frequencyDistributionGenerationRuleFilePaths);
		
		
		// 3. Print population information
		System.out.println("Population name: " + population.getName());
		System.out.println("Number of entities:" + population.getNbOfEntities());
		System.out.println("Open file(s) in \'example_data/FrequencyDistributionPopulationGenerator4/result_analysis/\' folder to look at the generation result analysis");
	}
	 
}
