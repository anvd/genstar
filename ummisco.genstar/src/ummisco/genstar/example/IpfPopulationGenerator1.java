package ummisco.genstar.example;

import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.population.IPopulation;

public class IpfPopulationGenerator1 {
	
	public IpfPopulationGenerator1() {}
	
	public static void main(String[] args) throws Exception {
		String propertiesFilePath = "example_data/IpfPopulationGenerator1/ipf_configuration.properties";
		
		// 0. Load the property file
		Properties ipfPopulationProperties = GenstarService.loadPropertyFile(propertiesFilePath);
		
		// 1. Generate the population
		IPopulation population = GenstarService.generateIpfPopulation(ipfPopulationProperties);
		
		// 2. Print population information
		System.out.println("Population name: " + population.getName());
		System.out.println("Number of entities:" + population.getNbOfEntities());
		System.out.println("Open file(s) in \'example_data/IpfPopulationGenerator1/result_analysis/RESULT_ANALYSIS_generated_people_population.csv\' folder to look at the generation result analysis");
	}
	 
}
