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
import ummisco.genstar.ipf.SampleDataGenerationRule;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.CustomGenerationRule;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.IMultipleRulesGenerator;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.metamodel.SyntheticPopulation;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarUtils;

public class GamaGenstarUtils {

	static void createSampleDataGenerationRule(final IScope scope, final ISingleRuleGenerator generator, final String ruleName, final Properties sampleDataProperties) throws GenstarException {
		
		// Read the necessary data from the properties
		
		// ID_ATTRIBUTE
		AbstractAttribute idAttribute = null;
		String idAttributeName = sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.ID_ATTRIBUTE_PROPERTY);
		if (idAttributeName != null) {
			idAttribute = generator.getAttributeByNameOnData(idAttributeName);
			if (idAttribute == null) { throw new GenstarException(idAttributeName + " is not recognized as an attribute on the generator"); }
		}
		
		// POPULATION_NAME_PROPERTY
		String populationName = sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
		if (populationName == null) { throw new GenstarException("Property '" + GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + "' not found in the property file."); };
		generator.setPopulationName(populationName);
		
		// SAMPLE_DATA_PROPERTY
		String sampleDataFilePath = sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.SAMPLE_DATA_PROPERTY);
		if (sampleDataFilePath == null) { throw new GenstarException("Property '" + GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCSVFile sampleCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, sampleDataFilePath, true), true);
		
		// CONTROLLED_ATTRIBUTES_PROPERTY 
		String controlledAttributesFilePath = sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (controlledAttributesFilePath == null) { throw new GenstarException("Property '" + GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCSVFile controlledAttributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, controlledAttributesFilePath, true), false);
		
		// CONTROLLED_TOTALS_PROPERTY
		String controlledTotalsFilePath = sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.CONTROLLED_TOTALS_PROPERTY);
		if (controlledTotalsFilePath == null) { throw new GenstarException("Property '" + GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.CONTROLLED_TOTALS_PROPERTY + "' not found in the property file."); }
		GenstarCSVFile controlledTotalsCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, controlledTotalsFilePath, true), false);
		
		// SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String supplementaryAttributesFilePath = sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (supplementaryAttributesFilePath == null) { throw new GenstarException("Property '" + GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.SUPPLEMENTARY_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCSVFile supplementaryAttributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, supplementaryAttributesFilePath, true), false);
		
		// MAX_ITERATIONS_PROPERTY
		String maxIterationsValue = sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.MAX_ITERATIONS_PROPERTY);
		int maxIterations = SampleDataGenerationRule.DEFAULT_MAX_ITERATIONS;
		if (maxIterationsValue != null) { maxIterations = Integer.parseInt(maxIterationsValue); }
		

		// COMPONENT_POPULATION_PROPERTY.
		String componentPopulationName = sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);

		// with component populations
		if (componentPopulationName != null) { // If COMPONENT_POPULATION_PROPERTY exists, then this is a group_component sample data
			
			// COMPONENT_SAMPLE_DATA_PROPERTY
			String componentSampleDataFilePath = sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY);
			if (componentSampleDataFilePath == null) { throw new GenstarException("Property '" + GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
			GenstarCSVFile componentSampleDataFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, componentSampleDataFilePath, true), true);
			
			// COMPONENT_ATTRIBUTES_PROPERTY
			String componentAttributesFilePath = sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
			if (componentAttributesFilePath == null) { throw new GenstarException("Property '" + GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
			GenstarCSVFile componentAttributesFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true), true);
			 
			// GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY
			String groupIdAttributeNameOnGroup = sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
			if (groupIdAttributeNameOnGroup == null) { throw new GenstarException("Property '" + GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + "' not found in the property file."); }
			 
			// GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY
			String groupIdAttributeNameOnComponent = sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
			if (groupIdAttributeNameOnComponent == null) { throw new GenstarException("Property '" + GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + "' not found in the property file."); }
			 
			
			// optional/supplementary properties (COMPONENT_REFERENCE_ON_GROUP, GROUP_REFERENCE_ON_COMPONENT)
			Map<String, String> supplementaryProperties = new HashMap<String, String>();
			supplementaryProperties.put(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_REFERENCE_ON_GROUP_PROPERTY, sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_REFERENCE_ON_GROUP_PROPERTY));
			supplementaryProperties.put(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_REFERENCE_ON_COMPONENT_PROPERTY, sampleDataProperties.getProperty(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_REFERENCE_ON_COMPONENT_PROPERTY));
			
			supplementaryProperties.put(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY, groupIdAttributeNameOnGroup);
			supplementaryProperties.put(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY, groupIdAttributeNameOnComponent);
			
			
			GenstarUtils.createGroupComponentSampleDataGenerationRule(generator, ruleName, sampleCSVFile,
					controlledAttributesCSVFile, controlledTotalsCSVFile, supplementaryAttributesCSVFile,
					componentSampleDataFile, componentAttributesFile, componentPopulationName, maxIterations, supplementaryProperties);
			
		} else { // without component populations
			GenstarUtils.createSampleDataGenerationRule(generator, ruleName, sampleCSVFile, controlledAttributesCSVFile, controlledTotalsCSVFile, supplementaryAttributesCSVFile, idAttribute, maxIterations);
		}
		
	}

	
	public static void createGenerationRulesFromCSVFile(final IScope scope, final IMultipleRulesGenerator generator, final GenstarCSVFile distributionsCSVFile) throws GenstarException {
		List<List<String>> fileContent = distributionsCSVFile.getContent();
		if ( fileContent == null || fileContent.isEmpty() ) { throw new GenstarException("Invalid Generation Rule file: content is empty (file: " + distributionsCSVFile.getPath()  + ")"); }
		int rows = fileContent.size();

		if (distributionsCSVFile.getColumns() != GenstarUtils.CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS) { throw new GenstarException("CVS file must have " + GenstarUtils.CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS + " columns, (file: " + distributionsCSVFile.getPath() + ")"); }

		// 1. Parse the header
		List<String> header = distributionsCSVFile.getHeaders();
		for (int i=0; i<header.size(); i++) {
			if (!header.get(i).equals(GenstarUtils.CSV_FILE_FORMATS.GENERATION_RULE_METADATA.HEADERS[i])) {
				throw new GenstarException("Invalid Generation Rule file header. Header must be " + GenstarUtils.CSV_FILE_FORMATS.GENERATION_RULE_METADATA.HEADER_STR + " (file: " + distributionsCSVFile.getPath() + ")");
			}
		}
		
		
		// 2. Parse and initialize distributions
		for ( int rowIndex = 0; rowIndex < rows; rowIndex++ ) {
			
			final List<String> generationRuleInfo = fileContent.get(rowIndex);
			if (generationRuleInfo.size() != GenstarUtils.CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS) { throw new GenstarException("Invalid Generation Rule file format: each row must have " +  GenstarUtils.CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS + " columns, (file: " + distributionsCSVFile.getPath() + ")"); }
			
			String ruleName = (String)generationRuleInfo.get(0);
			String ruleDataFilePathOrJavaClass = (String)generationRuleInfo.get(1);
			String ruleTypeName = (String)generationRuleInfo.get(2);
			GenstarCSVFile ruleDataFile = null;
			Properties properties = null;
			if (!ruleTypeName.equals(CustomGenerationRule.RULE_TYPE_NAME)) {
				if (ruleTypeName.equals(SampleDataGenerationRule.RULE_TYPE_NAME)) { // Sample Data Configuration is a property file
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
					ruleDataFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, ruleDataFilePathOrJavaClass, true), true);
				}
			}
			
			
			if (ruleTypeName.equals(FrequencyDistributionGenerationRule.RULE_TYPE_NAME)) {
				GenstarUtils.createFrequencyDistributionGenerationRule(generator, ruleName, ruleDataFile);
			} else if (ruleTypeName.equals(AttributeInferenceGenerationRule.RULE_TYPE_NAME)) {
				GenstarUtils.createAttributeInferenceGenerationRule(generator, ruleName, ruleDataFile);
			} else if (ruleTypeName.equals(SampleDataGenerationRule.RULE_TYPE_NAME)) {
				throw new GenstarException("Unsupported generation rule type: " + SampleDataGenerationRule.RULE_TYPE_NAME);
				//createSampleDataGenerationRule(scope, generator, ruleName, properties);
			} else if (ruleTypeName.equals(CustomGenerationRule.RULE_TYPE_NAME)) { 
				GenstarUtils.createCustomGenerationRule(generator, ruleName, ruleDataFilePathOrJavaClass);
			} else {
				throw new GenstarException("Unsupported generation rule (" + ruleTypeName + "), file: " + distributionsCSVFile.getPath());
			}
		}
	}	

	
	public static ISyntheticPopulation convertGamaPopulationToGenstarPopulation(final Entity host, final IList gamaPopulation, 
			final Map<String, List<AbstractAttribute>> populationsAttributes) throws GenstarException {
		if (gamaPopulation == null) { throw new GenstarException("Parameter gamaPopulation can not be null"); }
		if (gamaPopulation.size() < 3) { throw new GenstarException("gamaPopulation is not a valid gama synthetic population format"); }
		if (populationsAttributes == null) { throw new GenstarException("Parameter populationsAttributes can not be null"); }
		
		// 1. First three elements of a GAMA synthetic population
		String populationName =  (String)gamaPopulation.get(0); // first element is the population name
		Map<String, String> groupReferences = (Map<String, String>)gamaPopulation.get(1); // second element contains references to "group" agents
		Map<String, String> componentReferences = (Map<String, String>)gamaPopulation.get(2); // third element contains references to "component" agents
		// what to do with group and component references?
		
		ISyntheticPopulation genstarPopulation = null; // 2. create the genstar population appropriately
		if (host == null) {
			genstarPopulation = new SyntheticPopulation(populationName, populationsAttributes.get(populationName));
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
			IList<IList> gamaComponentPopulations = (IList<IList>) gamaEntityInitValues.get(ISyntheticPopulation.class);
			if (gamaComponentPopulations == null) { // without Genstar component populations
				mirrorGamaEntityInitValues = gamaEntityInitValues;
			} else {
				mirrorGamaEntityInitValues = GamaMapFactory.create();
				mirrorGamaEntityInitValues.putAll(gamaEntityInitValues);
				mirrorGamaEntityInitValues.remove(ISyntheticPopulation.class);
			}
			
			// convert string representation (of the initial values) to AttributeValue
			Map<String, AttributeValue> attributeValuesOnEntity = new HashMap<String, AttributeValue>();
			for (String attributeNameOnEntity : mirrorGamaEntityInitValues.keySet()) {
				attributeValuesOnEntity.put(attributeNameOnEntity, 
						GenstarGamaTypesConverter.convertGama2GenstarType(genstarPopulation.getAttributebyNameOnEntity(attributeNameOnEntity), 
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
	
	
	public static IList convertGenstarPopulationToGamaPopulation(final ISyntheticPopulation genstarPopulation) throws GenstarException {
		IList gamaPopulation = GamaListFactory.create();
		
		// First three elements of a GAMA synthetic population
		gamaPopulation.add(genstarPopulation.getName()); // first element is the population name
		gamaPopulation.add(genstarPopulation.getGroupReferences()); // second element contains references to "group" agents
		gamaPopulation.add(genstarPopulation.getComponentReferences()); // third element contains references to "component" agents
		
		// Convert the genstar population to the format understood by GAML "genstar_create" statement
		GamaMap map;
		for (Entity entity : genstarPopulation.getEntities()) {
			map = GamaMapFactory.create();
			for (EntityAttributeValue eav : entity.getEntityAttributeValues().values()) {
				map.put(eav.getAttribute().getNameOnEntity(), GenstarGamaTypesConverter.convertGenstar2GamaType(eav.getAttributeValueOnEntity()));
			}

			gamaPopulation.add(map);
			
			// Recursively convert genstar component populations
			IList componentPopulations = GamaListFactory.create();
			for (ISyntheticPopulation componentPopulation : entity.getComponentPopulations()) {
				componentPopulations.add(convertGenstarPopulationToGamaPopulation(componentPopulation));
			}
			
			if (!componentPopulations.isEmpty()) { map.put(ISyntheticPopulation.class, componentPopulations); }
		}

		return gamaPopulation;
	}
}
