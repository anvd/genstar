package ummisco.genstar.example;

import java.util.List;
import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.util.PROPERTY_FILES;

public class IpuPopulationExtractor {

	public IpuPopulationExtractor() {}
	
	public static void main(String[] args) throws Exception {
		String basePath = "example_data/IpuPopulationExtractor/";
		String[] extractorPropertiesFilePaths = {
			basePath + "ExtractedIpuPopulationProperties_ZeroPointOnePercent.properties",
			basePath + "ExtractedIpuPopulationProperties_TenPercents.properties",
			basePath + "ExtractedIpuPopulationProperties_ThirtyPercents.properties"
		};
		
		for (String ePropertiesFilePath : extractorPropertiesFilePaths) {
			extractIpuPopulation(ePropertiesFilePath);
		}
	}
	
	private static void extractIpuPopulation(final String propertiesFilePath) throws Exception {
		
		// 0. Load the property file
		Properties populationExtractorProperties = GenstarService.loadPropertyFile(propertiesFilePath);
		
		
		// 1. Generate the population
		IPopulation extractedPopulation = GenstarService.extractIpuPopulation(populationExtractorProperties);

		
		// 2. Print population information
		System.out.println("Information of the extracted population");
		System.out.println("Group population name: " + extractedPopulation.getName());
		System.out.println("Number of group entities: " + extractedPopulation.getNbOfEntities());
		System.out.println("Extracted percent: " + populationExtractorProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.PERCENTAGE_PROPERTY));
		
		List<AbstractAttribute> attributes = extractedPopulation.getAttributes();
		System.out.println("Number of group attributes: " + attributes.size());
		for (AbstractAttribute attr : attributes) { System.out.println("\t" + attr.toString()); }
		
		String componentPopulationName = null;
		int totalNumberOfExtractedComponentEntities = 0;
		List<Entity> groupEntities = extractedPopulation.getEntities();
		for (Entity gEntity : groupEntities) {
			for (IPopulation componentPop : gEntity.getComponentPopulations()) { 
				totalNumberOfExtractedComponentEntities += componentPop.getNbOfEntities();
				if (componentPopulationName == null) { componentPopulationName = componentPop.getName(); }
			}
		}
		System.out.println("Component population name: " + componentPopulationName);
		System.out.println("Total number of component entities: " + totalNumberOfExtractedComponentEntities + "\n");
	}
}
