package ummisco.genstar.example;

import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.population.IPopulation;

public class IpfPopulationGenerator2 {

	public IpfPopulationGenerator2() {}
	
	public static void main(String[] args) throws Exception {
		String propertiesFilePath = "example_data/IpfPopulationGenerator2/ipf_configuration_10iterations.properties";
		
		// 0. Load the property file
		Properties ipfPopulationProperties = GenstarService.loadPropertyFile(propertiesFilePath);
		
		// 1. Generate the population
		IPopulation population = GenstarService.generateIpfPopulation(ipfPopulationProperties);
		
		// 3. Print population information
		System.out.println("Population name: " + population.getName());
		System.out.println("Number of entities:" + population.getNbOfEntities());
		System.out.println("Open file(s) in \'example_data/IpfPopulationGenerator2/result_analysis/RESULT_ANALYSIS_generated_people_population_10iterations.csv\' folder to look at the generation result analysis");
	}
	
}
