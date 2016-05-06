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
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.Population;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.sample_free.AttributeInferenceGenerationRule;
import ummisco.genstar.sample_free.CustomSampleFreeGenerationRule;
import ummisco.genstar.sample_free.FrequencyDistributionGenerationRule;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.GenstarUtils;
import ummisco.genstar.util.INPUT_DATA_FORMATS;
import ummisco.genstar.util.IpfUtils;
import ummisco.genstar.util.IpuUtils;

public class GamaGenstarUtils {
	
	static IPopulation generateIpfPopulation(final IScope scope, final Properties ipfPopulationProperties) throws GenstarException {
		// 1. Read the ATTRIBUTES_PROPERTIES then create the generator
		String attributesCsvFilePath = ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.ATTRIBUTES_PROPERTY);
		if (attributesCsvFilePath == null) { throw new GenstarException(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.ATTRIBUTES_PROPERTY + " property not found "); }
		GenstarCsvFile attributesCsvFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, attributesCsvFilePath, true), true);
		SampleBasedGenerator generator = new SampleBasedGenerator("single rule generator");
		AttributeUtils.createAttributesFromCsvFile(generator, attributesCsvFile);
		
		// 2. Create the generation rule
		createIpfGenerationRule(scope, generator, "ipf generation rule", ipfPopulationProperties);
		
		return generator.generate();
	}
	
	static IPopulation generateIpuPopulation(final IScope scope, final Properties ipuPopulationProperties) throws GenstarException {
		
		// 1. Read the ATTRIBUTES_PROPERTIES then create the generator
		String groupAttributesCsvFilePath = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesCsvFilePath == null) { throw new GenstarException(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_ATTRIBUTES_PROPERTY + " property not found "); }
		GenstarCsvFile groupAttributesCsvFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupAttributesCsvFilePath, true), true);
		SampleBasedGenerator groupGenerator = new SampleBasedGenerator("single rule generator");
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesCsvFile);
		
		// 2. Create the generation rule
		createIpuGenerationRule(scope, groupGenerator, "ipu generation rule", ipuPopulationProperties);
		
		return groupGenerator.generate();
	}
	

	static void createIpfGenerationRule(final IScope scope, final SampleBasedGenerator generator, final String ruleName, final Properties ipfPopulationProperties) throws GenstarException {
		
		// Read the  properties
		
		// ID_ATTRIBUTE
		AbstractAttribute idAttribute = null;
		String idAttributeName = ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.ID_ATTRIBUTE_PROPERTY);
		if (idAttributeName != null) {
			idAttribute = generator.getAttributeByNameOnData(idAttributeName);
			if (idAttribute == null) { throw new GenstarException(idAttributeName + " is not recognized as an attribute on the generator"); }
		}
		
		// POPULATION_NAME_PROPERTY
		String populationName = ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.POPULATION_NAME_PROPERTY);
		if (populationName == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.POPULATION_NAME_PROPERTY + "' not found in the property file."); };
		generator.setPopulationName(populationName);
		
		// SAMPLE_DATA_PROPERTY
		String sampleDataFilePath = ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.SAMPLE_DATA_PROPERTY);
		if (sampleDataFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile sampleCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, sampleDataFilePath, true), true);
		
		// CONTROLLED_ATTRIBUTES_PROPERTY 
		String controlledAttributesFilePath = ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (controlledAttributesFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile controlledAttributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlledAttributesFilePath, true), false);
		
		// CONTROL_TOTALS_PROPERTY
		String controlledTotalsFilePath = ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.CONTROL_TOTALS_PROPERTY);
		if (controlledTotalsFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.CONTROL_TOTALS_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile controlledTotalsCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlledTotalsFilePath, true), false);
		
		// SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String supplementaryAttributesFilePath = ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (supplementaryAttributesFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.SUPPLEMENTARY_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile supplementaryAttributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, supplementaryAttributesFilePath, true), false);
		
		// MAX_ITERATIONS_PROPERTY
		String maxIterationsValue = ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.MAX_ITERATIONS_PROPERTY);
		int maxIterations = IpfGenerationRule.DEFAULT_MAX_ITERATIONS;
		if (maxIterationsValue != null) { maxIterations = Integer.parseInt(maxIterationsValue); }
		

		// COMPONENT_POPULATION_PROPERTY.
		String componentPopulationName = ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.COMPONENT_POPULATION_NAME_PROPERTY);

		// with component populations
		if (componentPopulationName != null) { // If COMPONENT_POPULATION_PROPERTY exists, then this is a group_component sample data
			
			// COMPONENT_SAMPLE_DATA_PROPERTY
			String componentSampleDataFilePath = ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.COMPONENT_SAMPLE_DATA_PROPERTY);
			if (componentSampleDataFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.COMPONENT_SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
			GenstarCsvFile componentSampleDataFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentSampleDataFilePath, true), true);
			
			// COMPONENT_ATTRIBUTES_PROPERTY
			String componentAttributesFilePath = ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.COMPONENT_ATTRIBUTES_PROPERTY);
			if (componentAttributesFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.COMPONENT_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
			GenstarCsvFile componentAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true), true);
			 
			// GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY
			String groupIdAttributeNameOnGroup = ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
			if (groupIdAttributeNameOnGroup == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + "' not found in the property file."); }
			 
			// GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY
			String groupIdAttributeNameOnComponent = ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
			if (groupIdAttributeNameOnComponent == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + "' not found in the property file."); }
			 
			
			// optional/supplementary properties (COMPONENT_REFERENCE_ON_GROUP, GROUP_REFERENCE_ON_COMPONENT)
			Map<String, String> supplementaryProperties = new HashMap<String, String>();
			supplementaryProperties.put(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.COMPONENT_REFERENCE_ON_GROUP_PROPERTY, ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.COMPONENT_REFERENCE_ON_GROUP_PROPERTY));
			supplementaryProperties.put(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.GROUP_REFERENCE_ON_COMPONENT_PROPERTY, ipfPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.GROUP_REFERENCE_ON_COMPONENT_PROPERTY));
			
			supplementaryProperties.put(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY, groupIdAttributeNameOnGroup);
			supplementaryProperties.put(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY, groupIdAttributeNameOnComponent);
			
			
			IpfUtils.createCompoundIpfGenerationRule(generator, ruleName, sampleCSVFile,
					controlledAttributesCSVFile, controlledTotalsCSVFile, supplementaryAttributesCSVFile,
					componentSampleDataFile, componentAttributesFile, componentPopulationName, maxIterations, supplementaryProperties);
			
		} else { // without component populations
			IpfUtils.createIpfGenerationRule(generator, ruleName, sampleCSVFile, controlledAttributesCSVFile, controlledTotalsCSVFile, supplementaryAttributesCSVFile, idAttribute, maxIterations);
		}
		
	}

	
	static void createIpuGenerationRule(final IScope scope, final SampleBasedGenerator groupGenerator, final String ruleName, final Properties ipuPopulationProperties) throws GenstarException {

		// Read the  properties
		
		// GROUP_ID_ATTRIBUTE
		AbstractAttribute groupIdAttributeOfGroupEntity = null;
		String groupIdAttributeNameOnDataOfGroupEntity = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
		if (groupIdAttributeNameOnDataOfGroupEntity != null) {
			groupIdAttributeOfGroupEntity = groupGenerator.getAttributeByNameOnData(groupIdAttributeNameOnDataOfGroupEntity);
			if (groupIdAttributeOfGroupEntity == null) { throw new GenstarException(groupIdAttributeNameOnDataOfGroupEntity + " is not recognized as an attribute on the generator"); }
		} else {
			throw new GenstarException(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + " property not found");
		}
		
		// GROUP_POPULATION_NAME_PROPERTY
		String groupPopulationName = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_POPULATION_NAME_PROPERTY);
		if (groupPopulationName == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_POPULATION_NAME_PROPERTY + "' not found in the property file."); };
		groupGenerator.setPopulationName(groupPopulationName);
		
		// GROUP_SAMPLE_DATA_PROPERTY
		String groupSampleDataFilePath = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_SAMPLE_DATA_PROPERTY);
		if (groupSampleDataFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupSampleDataFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupSampleDataFilePath, true), true);
		
		// GROUP_CONTROLLED_ATTRIBUTES_PROPERTY 
		String groupControlledAttributesFilePath = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (groupControlledAttributesFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupControlledAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupControlledAttributesFilePath, true), false);
		
		// GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY
		String groupIdAttributeNameOnGroup = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
		if (groupIdAttributeNameOnGroup == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + "' not found in the property file."); }

		// GROUP_CONTROL_TOTALS_PROPERTY
		String groupControlTotalsFilePath = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_CONTROL_TOTALS_PROPERTY);
		if (groupControlTotalsFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_CONTROL_TOTALS_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupControlledTotalsFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupControlTotalsFilePath, true), false);
		
		// GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String groupSupplementaryAttributesFilePath = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (groupSupplementaryAttributesFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupSupplementaryAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupSupplementaryAttributesFilePath, true), false);
		 

		// COMPONENT_POPULATION_NAME_PROPERTY
		String componentPopulationName = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.COMPONENT_POPULATION_NAME_PROPERTY);
		if (componentPopulationName == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.COMPONENT_POPULATION_NAME_PROPERTY + "' not found in the property file."); };
		
		// COMPONENT_SAMPLE_DATA_PROPERTY
		String componentSampleDataFilePath = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.COMPONENT_SAMPLE_DATA_PROPERTY);
		if (componentSampleDataFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.COMPONENT_SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentSampleDataFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentSampleDataFilePath, true), true);
		
		// COMPONENT_ATTRIBUTES_PROPERTY
		String componentAttributesFilePath = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.COMPONENT_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true), true);
		 
		// COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY
		String componentControlledAttributesFilePath = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (componentControlledAttributesFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentControlledAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentControlledAttributesFilePath, true), false);
		
		// COMPONENT_CONTROL_TOTALS_PROPERTY
		String componentControlTotalsFilePath = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.COMPONENT_CONTROL_TOTALS_PROPERTY);
		if (componentControlTotalsFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.COMPONENT_CONTROL_TOTALS_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentControlTotalsFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentControlTotalsFilePath, true), false);
		
		
		// GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY
		String groupIdAttributeNameOnComponent = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
		if (groupIdAttributeNameOnComponent == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + "' not found in the property file."); }
		 
		// COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String componentSupplementaryAttributesFilePath = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (componentSupplementaryAttributesFilePath == null) { throw new GenstarException("Property '" + INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentSupplementaryAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentSupplementaryAttributesFilePath, true), false);
				
		
		// MAX_ITERATIONS_PROPERTY
		String maxIterationsValue = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.MAX_ITERATIONS_PROPERTY);
		int maxIterations = IpfGenerationRule.DEFAULT_MAX_ITERATIONS;
		if (maxIterationsValue != null) { maxIterations = Integer.parseInt(maxIterationsValue); }

		// optional/supplementary properties (COMPONENT_REFERENCE_ON_GROUP, GROUP_REFERENCE_ON_COMPONENT) ????
//		Map<String, String> supplementaryProperties = new HashMap<String, String>();
//		supplementaryProperties.put(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.COMPONENT_REFERENCE_ON_GROUP_PROPERTY, ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.COMPONENT_REFERENCE_ON_GROUP_PROPERTY));
//		supplementaryProperties.put(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.GROUP_REFERENCE_ON_COMPONENT_PROPERTY, ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.GROUP_REFERENCE_ON_COMPONENT_PROPERTY));
//		
//		supplementaryProperties.put(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY, groupIdAttributeNameOnGroup);
//		supplementaryProperties.put(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY, groupIdAttributeNameOnComponent);
		 
		// ?? missing COMPONENT_REFERENCE_ON_GROUP, GROUP_REFERENCE_ON_COMPONENT
		
		// COMPONENT_REFERENCE_ON_GROUP
		String componentReferenceOnGroup = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.COMPONENT_REFERENCE_ON_GROUP_PROPERTY);
		
		// GROUP_REFERENCE_ON_COMPONENT
		String groupReferenceOnComponent = ipuPopulationProperties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPU_POPULATION.GROUP_REFERENCE_ON_COMPONENT_PROPERTY);
		
		
		IpuUtils.createIpuGenerationRule(groupGenerator, 
			groupSampleDataFile, groupIdAttributeNameOnGroup,  groupControlledAttributesFile, groupControlledTotalsFile, groupSupplementaryAttributesFile, componentReferenceOnGroup,
			componentAttributesFile, componentPopulationName, componentSampleDataFile, groupIdAttributeNameOnComponent, componentControlledAttributesFile, componentControlTotalsFile, componentSupplementaryAttributesFile, groupReferenceOnComponent,
			maxIterations);
	}
	
	
	public static void createSampleFreeGenerationRules(final IScope scope, final SampleFreeGenerator generator, final GenstarCsvFile sampleFreeGenerationRulesListFile) throws GenstarException {
		List<List<String>> fileContent = sampleFreeGenerationRulesListFile.getContent();
		if ( fileContent == null || fileContent.isEmpty() ) { throw new GenstarException("Invalid Generation Rule file: content is empty (file: " + sampleFreeGenerationRulesListFile.getPath()  + ")"); }
		int rows = fileContent.size();

		if (sampleFreeGenerationRulesListFile.getColumns() != INPUT_DATA_FORMATS.CSV_FILES.GENERATION_RULES.NB_OF_COLS) { throw new GenstarException("CVS file must have " + INPUT_DATA_FORMATS.CSV_FILES.GENERATION_RULES.NB_OF_COLS + " columns, (file: " + sampleFreeGenerationRulesListFile.getPath() + ")"); }

		// 1. Parse the header
		List<String> header = sampleFreeGenerationRulesListFile.getHeaders();
		for (int i=0; i<header.size(); i++) {
			if (!header.get(i).equals(INPUT_DATA_FORMATS.CSV_FILES.GENERATION_RULES.HEADERS[i])) {
				throw new GenstarException("Invalid Generation Rule file header. Header must be " + INPUT_DATA_FORMATS.CSV_FILES.GENERATION_RULES.HEADER_STR + " (file: " + sampleFreeGenerationRulesListFile.getPath() + ")");
			}
		}
		
		
		// 2. Parse and initialize distributions
		for ( int rowIndex = 0; rowIndex < rows; rowIndex++ ) {
			
			final List<String> generationRuleInfo = fileContent.get(rowIndex);
			if (generationRuleInfo.size() != INPUT_DATA_FORMATS.CSV_FILES.GENERATION_RULES.NB_OF_COLS) { throw new GenstarException("Invalid Generation Rule file format: each row must have " +  INPUT_DATA_FORMATS.CSV_FILES.GENERATION_RULES.NB_OF_COLS + " columns, (file: " + sampleFreeGenerationRulesListFile.getPath() + ")"); }
			
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
				throw new GenstarException("Unsupported generation rule (" + ruleTypeName + "), file: " + sampleFreeGenerationRulesListFile.getPath());
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
