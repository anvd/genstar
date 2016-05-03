package ummisco.genstar.gama;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import msi.gama.common.util.FileUtils;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.IpfGenerationRule;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.CustomSampleFreeGenerationRule;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.IPopulation;
import ummisco.genstar.metamodel.PopulationType;
import ummisco.genstar.metamodel.Population;
import ummisco.genstar.metamodel.SampleBasedGenerator;
import ummisco.genstar.metamodel.SampleFreeGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.GenstarUtils;
import ummisco.genstar.util.INPUT_DATA_FORMATS;

public class GamaGenstarUtils {

	// TODO change to "createSampleBasedGenerationRule"
	static void createSampleDataGenerationRule(final IScope scope, final SampleBasedGenerator generator, final String ruleName, final Properties sampleDataProperties) throws GenstarException {
		
		// Read the necessary data from the properties
		
		// ID_ATTRIBUTE
		AbstractAttribute idAttribute = null;
		String idAttributeName = sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.ID_ATTRIBUTE_PROPERTY);
		if (idAttributeName != null) {
			idAttribute = generator.getAttributeByNameOnData(idAttributeName);
			if (idAttribute == null) { throw new GenstarException(idAttributeName + " is not recognized as an attribute on the generator"); }
		}
		
		// POPULATION_NAME_PROPERTY
		String populationName = sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.POPULATION_NAME_PROPERTY);
		if (populationName == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.POPULATION_NAME_PROPERTY + "' not found in the property file."); };
		generator.setPopulationName(populationName);
		
		// SAMPLE_DATA_PROPERTY
		String sampleDataFilePath = sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.SAMPLE_DATA_PROPERTY);
		if (sampleDataFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile sampleCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, sampleDataFilePath, true), true);
		
		// CONTROLLED_ATTRIBUTES_PROPERTY 
		String controlledAttributesFilePath = sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (controlledAttributesFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile controlledAttributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlledAttributesFilePath, true), false);
		
		// CONTROLLED_TOTALS_PROPERTY
		String controlledTotalsFilePath = sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.CONTROLLED_TOTALS_PROPERTY);
		if (controlledTotalsFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.CONTROLLED_TOTALS_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile controlledTotalsCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlledTotalsFilePath, true), false);
		
		// SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String supplementaryAttributesFilePath = sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (supplementaryAttributesFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.SUPPLEMENTARY_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile supplementaryAttributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, supplementaryAttributesFilePath, true), false);
		
		// MAX_ITERATIONS_PROPERTY
		String maxIterationsValue = sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.MAX_ITERATIONS_PROPERTY);
		int maxIterations = IpfGenerationRule.DEFAULT_MAX_ITERATIONS;
		if (maxIterationsValue != null) { maxIterations = Integer.parseInt(maxIterationsValue); }
		

		// COMPONENT_POPULATION_PROPERTY.
		String componentPopulationName = sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.COMPONENT_POPULATION_NAME_PROPERTY);

		// with component populations
		if (componentPopulationName != null) { // If COMPONENT_POPULATION_PROPERTY exists, then this is a group_component sample data
			
			// COMPONENT_SAMPLE_DATA_PROPERTY
			String componentSampleDataFilePath = sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.COMPONENT_SAMPLE_DATA_PROPERTY);
			if (componentSampleDataFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.COMPONENT_SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
			GenstarCsvFile componentSampleDataFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentSampleDataFilePath, true), true);
			
			// COMPONENT_ATTRIBUTES_PROPERTY
			String componentAttributesFilePath = sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.COMPONENT_ATTRIBUTES_PROPERTY);
			if (componentAttributesFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.COMPONENT_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
			GenstarCsvFile componentAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true), true);
			 
			// GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY
			String groupIdAttributeNameOnGroup = sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
			if (groupIdAttributeNameOnGroup == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + "' not found in the property file."); }
			 
			// GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY
			String groupIdAttributeNameOnComponent = sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
			if (groupIdAttributeNameOnComponent == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + "' not found in the property file."); }
			 
			
			// optional/supplementary properties (COMPONENT_REFERENCE_ON_GROUP, GROUP_REFERENCE_ON_COMPONENT)
			Map<String, String> supplementaryProperties = new HashMap<String, String>();
			supplementaryProperties.put(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.COMPONENT_REFERENCE_ON_GROUP_PROPERTY, sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.COMPONENT_REFERENCE_ON_GROUP_PROPERTY));
			supplementaryProperties.put(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.GROUP_REFERENCE_ON_COMPONENT_PROPERTY, sampleDataProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.GROUP_REFERENCE_ON_COMPONENT_PROPERTY));
			
			supplementaryProperties.put(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY, groupIdAttributeNameOnGroup);
			supplementaryProperties.put(INPUT_DATA_FORMATS.PROPERTY_FILES.SAMPLE_DATA_POPULATION.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY, groupIdAttributeNameOnComponent);
			
			
			GenstarUtils.createGroupComponentSampleDataGenerationRule(generator, ruleName, sampleCSVFile,
					controlledAttributesCSVFile, controlledTotalsCSVFile, supplementaryAttributesCSVFile,
					componentSampleDataFile, componentAttributesFile, componentPopulationName, maxIterations, supplementaryProperties);
			
		} else { // without component populations
			GenstarUtils.createSampleDataGenerationRule(generator, ruleName, sampleCSVFile, controlledAttributesCSVFile, controlledTotalsCSVFile, supplementaryAttributesCSVFile, idAttribute, maxIterations);
		}
		
	}

	
	// TODO change to "createSampleFreeGenerationRulesFromCsvFile"
	public static void createGenerationRulesFromCSVFile(final IScope scope, final SampleFreeGenerator generator, final GenstarCsvFile distributionsCSVFile) throws GenstarException {
		List<List<String>> fileContent = distributionsCSVFile.getContent();
		if ( fileContent == null || fileContent.isEmpty() ) { throw new GenstarException("Invalid Generation Rule file: content is empty (file: " + distributionsCSVFile.getPath()  + ")"); }
		int rows = fileContent.size();

		if (distributionsCSVFile.getColumns() != INPUT_DATA_FORMATS.CSV_FILES.GENERATION_RULES.NB_OF_COLS) { throw new GenstarException("CVS file must have " + INPUT_DATA_FORMATS.CSV_FILES.GENERATION_RULES.NB_OF_COLS + " columns, (file: " + distributionsCSVFile.getPath() + ")"); }

		// 1. Parse the header
		List<String> header = distributionsCSVFile.getHeaders();
		for (int i=0; i<header.size(); i++) {
			if (!header.get(i).equals(INPUT_DATA_FORMATS.CSV_FILES.GENERATION_RULES.HEADERS[i])) {
				throw new GenstarException("Invalid Generation Rule file header. Header must be " + INPUT_DATA_FORMATS.CSV_FILES.GENERATION_RULES.HEADER_STR + " (file: " + distributionsCSVFile.getPath() + ")");
			}
		}
		
		
		// 2. Parse and initialize distributions
		for ( int rowIndex = 0; rowIndex < rows; rowIndex++ ) {
			
			final List<String> generationRuleInfo = fileContent.get(rowIndex);
			if (generationRuleInfo.size() != INPUT_DATA_FORMATS.CSV_FILES.GENERATION_RULES.NB_OF_COLS) { throw new GenstarException("Invalid Generation Rule file format: each row must have " +  INPUT_DATA_FORMATS.CSV_FILES.GENERATION_RULES.NB_OF_COLS + " columns, (file: " + distributionsCSVFile.getPath() + ")"); }
			
			String ruleName = (String)generationRuleInfo.get(0);
			String ruleDataFilePathOrJavaClass = (String)generationRuleInfo.get(1);
			String ruleTypeName = (String)generationRuleInfo.get(2);
			GenstarCsvFile ruleDataFile = null;
			Properties properties = null;
			if (!ruleTypeName.equals(CustomSampleFreeGenerationRule.RULE_TYPE_NAME)) {
				if (ruleTypeName.equals(IpfGenerationRule.RULE_TYPE_NAME)) { // Sample Data Configuration is a property file
					File sampleDataPropertyFile = new File(FileUtils.constructAbsoluteFilePath(scope, ruleDataFilePathOrJavaClass, true));
					try {
						FileInputStream propertyInputStream = new FileInputStream(sampleDataPropertyFile);
						properties = new Properties();
						properties.load(propertyInputStream);
					} catch (FileNotFoundException e) {
						throw new GenstarException(e);
					} catch (IOException e) {
						throw new GenstarException(e);
					}
				} else { // Frequency Distribution or Attribute Inference
					ruleDataFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, ruleDataFilePathOrJavaClass, true), true);
				}
			}
			
			
			if (ruleTypeName.equals(FrequencyDistributionGenerationRule.RULE_TYPE_NAME)) {
				GenstarUtils.createFrequencyDistributionGenerationRule(generator, ruleName, ruleDataFile);
			} else if (ruleTypeName.equals(AttributeInferenceGenerationRule.RULE_TYPE_NAME)) {
				GenstarUtils.createAttributeInferenceGenerationRule(generator, ruleName, ruleDataFile);
			} else if (ruleTypeName.equals(IpfGenerationRule.RULE_TYPE_NAME)) {
				throw new GenstarException("Unsupported generation rule type: " + IpfGenerationRule.RULE_TYPE_NAME);
				//createSampleDataGenerationRule(scope, generator, ruleName, properties);
			} else if (ruleTypeName.equals(CustomSampleFreeGenerationRule.RULE_TYPE_NAME)) { 
				GenstarUtils.createCustomGenerationRule(generator, ruleName, ruleDataFilePathOrJavaClass);
			} else {
				throw new GenstarException("Unsupported generation rule (" + ruleTypeName + "), file: " + distributionsCSVFile.getPath());
			}
		}
	}	

	
	public static IPopulation convertGamaPopulationToGenstarPopulation(final Entity host, final IList gamaPopulation, 
			final Map<String, List<AbstractAttribute>> populationsAttributes) throws GenstarException {
		if (gamaPopulation == null) { throw new GenstarException("Parameter gamaPopulation can not be null"); }
		if (gamaPopulation.size() < 3) { throw new GenstarException("gamaPopulation is not a valid gama synthetic population format"); }
		if (populationsAttributes == null) { throw new GenstarException("Parameter populationsAttributes can not be null"); }
		
		// 1. First three elements of a GAMA synthetic population
		String populationName =  (String)gamaPopulation.get(0); // first element is the population name
		Map<String, String> groupReferences = (Map<String, String>)gamaPopulation.get(1); // second element contains references to "group" agents
		Map<String, String> componentReferences = (Map<String, String>)gamaPopulation.get(2); // third element contains references to "component" agents
		// what to do with group and component references?
		
		IPopulation genstarPopulation = null; // 2. create the genstar population appropriately
		if (host == null) {
			genstarPopulation = new Population(PopulationType.SYNTHETIC_POPULATION, populationName, populationsAttributes.get(populationName));
		} else {
			genstarPopulation = host.createComponentPopulation(populationName, populationsAttributes.get(populationName));
		}
		
		// 3. extract GAMA init values
		IList<GamaMap> gamaInits = GamaListFactory.create();
		gamaInits.addAll(gamaPopulation.subList(3, gamaPopulation.size()));

		// 4. convert entities (each entity is a map)
		for (GamaMap gamaEntityInitValues : gamaInits) {
			
			// extract the initial values
			GamaMap<String, String> mirrorGamaEntityInitValues = null;
			IList<IList> gamaComponentPopulations = (IList<IList>) gamaEntityInitValues.get(IPopulation.class);
			if (gamaComponentPopulations == null) { // without Genstar component populations
				mirrorGamaEntityInitValues = gamaEntityInitValues;
			} else {
				mirrorGamaEntityInitValues = GamaMapFactory.create();
				mirrorGamaEntityInitValues.putAll(gamaEntityInitValues);
				mirrorGamaEntityInitValues.remove(IPopulation.class);
			}
			
			// convert string representation (of the initial values) to AttributeValue
			Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>();
			for (String attributeNameOnEntity : mirrorGamaEntityInitValues.keySet()) {
				attributeValuesOnEntity.put(genstarPopulation.getAttributeByNameOnEntity(attributeNameOnEntity), 
						GenstarGamaTypesConverter.convertGama2GenstarType(genstarPopulation.getAttributeByNameOnEntity(attributeNameOnEntity), 
								GenstarGamaTypesConverter.convertGamaAttributeValueToString(mirrorGamaEntityInitValues.get(attributeNameOnEntity) ) ) );
			}
			
			Entity genstarEntity = genstarPopulation.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity); // create the entity
			if (gamaComponentPopulations != null) { // recursively convert component populations
				for (IList gamaComponentPopulation : gamaComponentPopulations) {
					convertGamaPopulationToGenstarPopulation(genstarEntity, gamaComponentPopulation, populationsAttributes);
				}
			}
		}
		
		return genstarPopulation;
	}
	
	
	public static IList convertGenstarPopulationToGamaPopulation(final IPopulation genstarPopulation) throws GenstarException {
		IList gamaPopulation = GamaListFactory.create();
		
		// First three elements of a GAMA synthetic population
		gamaPopulation.add(genstarPopulation.getName()); // first element is the population name
		gamaPopulation.add(genstarPopulation.getGroupReferences()); // second element contains references to "group" agents
		gamaPopulation.add(genstarPopulation.getComponentReferences()); // third element contains references to "component" agents
		
		// Convert the genstar population to the format understood by GAML "genstar_create" statement
		GamaMap map;
		for (Entity entity : genstarPopulation.getEntities()) {
			map = GamaMapFactory.create();
			for (EntityAttributeValue eav : entity.getEntityAttributeValues()) {
				map.put(eav.getAttribute().getNameOnEntity(), GenstarGamaTypesConverter.convertGenstar2GamaType(eav.getAttributeValueOnEntity()));
			}

			gamaPopulation.add(map);
			
			// Recursively convert genstar component populations
			IList componentPopulations = GamaListFactory.create();
			for (IPopulation componentPopulation : entity.getComponentPopulations()) {
				componentPopulations.add(convertGenstarPopulationToGamaPopulation(componentPopulation));
			}
			
			if (!componentPopulations.isEmpty()) { map.put(IPopulation.class, componentPopulations); }
		}

		return gamaPopulation;
	}
}
