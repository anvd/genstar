package ummisco.genstar.example;

import java.util.List;
import java.util.Properties;

import ummisco.genstar.GenstarService;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.util.PROPERTY_FILES;

public class IpfPopulationExtractor2 {

	public IpfPopulationExtractor2() {}
	
	public static void main(String[] args) throws Exception {
		String basePath = "example_data/IpfPopulationExtractor2/";
		String[] extractorPropertiesFilePaths = {
			basePath + "ExtractedIpfPopulationProperties_with_ID_OnePercent.properties",
			basePath + "ExtractedIpfPopulationProperties_with_ID_TenPercents.properties",
			basePath + "ExtractedIpfPopulationProperties_with_ID_ThirtyPercents.properties"
		};
		
		for (String ePropertiesFilePath : extractorPropertiesFilePaths) {
			extractIpfPopulation(ePropertiesFilePath);
		}
	}
	
	private static void extractIpfPopulation(final String propertiesFilePath) throws Exception {
		
		// 0. Load the property file
		Properties populationExtractorProperties = GenstarService.loadPropertyFile(propertiesFilePath);
		
		// 1. Generate the population
		IPopulation extractedPopulation = GenstarService.extractIpfSinglePopulation(populationExtractorProperties);

		// 2. Print population information
		System.out.println("Information of the extracted population");
		System.out.println("Population name: " + extractedPopulation.getName());
		System.out.println("Number of entities: " + extractedPopulation.getNbOfEntities());
		System.out.println("Extracted percent: " + populationExtractorProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.PERCENTAGE_PROPERTY));
		
		List<AbstractAttribute> attributes = extractedPopulation.getAttributes();
		System.out.println("Number of attributes: " + attributes.size());
		for (AbstractAttribute attr : attributes) { System.out.println("\t" + attr.toString()); }
		
		System.out.println("Please compare this information to the original population found in " + populationExtractorProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.POPULATION_DATA_PROPERTY) + "\n");
	}
}
