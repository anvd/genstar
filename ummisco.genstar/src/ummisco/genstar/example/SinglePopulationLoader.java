package ummisco.genstar.example;

import java.util.List;
import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.population.IPopulation;

public class SinglePopulationLoader {

	public SinglePopulationLoader() {}
	
	
	public static void main(String[] args) throws Exception {
		String properties_file_path = "example_data/SinglePopulationLoader/SinglePopulationProperties.properties";
		
		// 0. Load the property file
		Properties singlePopulationProperties = GenstarService.loadPropertyFile(properties_file_path);
		
		
		// 1. Load the population
		IPopulation singlePopulation = GenstarService.loadSinglePopulation(singlePopulationProperties);
		
		
		// 2. Print population's information
		System.out.println("Population name: " + singlePopulation.getName());
		System.out.println("Number of entities: " + singlePopulation.getNbOfEntities());
		
		List<AbstractAttribute> attributes = singlePopulation.getAttributes();
		System.out.println("Number of attributes: " + attributes.size());
		for (AbstractAttribute attr : attributes) { System.out.println("\t" + attr.toString()); }
	}
}
