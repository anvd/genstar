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
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.SampleDataGenerationRule;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.CustomGenerationRule;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.IMultipleRulesGenerator;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarFactoryUtils;

public class GamaGenstarFactoryUtils {

	static void createSampleDataGenerationRule(final IScope scope, final ISingleRuleGenerator generator, final String ruleName, final Properties sampleDataProperties) throws GenstarException {
		
		// Read the necessary data from the properties
		
		// ID_ATTRIBUTE
		AbstractAttribute idAttribute = null;
		String idAttributeName = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.ID_ATTRIBUTE_PROPERTY);
		if (idAttributeName != null) {
			idAttribute = generator.getAttributeByNameOnData(idAttributeName);
			if (idAttribute == null) { throw new GenstarException(idAttributeName + " is not recognized as an attribute on the generator"); }
		}
		
		// POPULATION_PROPERTY
		String populationName = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
		if (populationName == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + "' not found in the property file."); };
		generator.setPopulationName(populationName);
		
		// SAMPLE_DATA_PROPERTY
		String sampleDataFilePath = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.SAMPLE_DATA_PROPERTY);
		if (sampleDataFilePath == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCSVFile sampleCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, sampleDataFilePath, true), true);
		
		// CONTROLLED_ATTRIBUTES_PROPERTY 
		String controlledAttributesFilePath = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (controlledAttributesFilePath == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCSVFile controlledAttributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, controlledAttributesFilePath, true), false);
		
		// CONTROLLED_TOTALS_PROPERTY
		String controlledTotalsFilePath = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.CONTROLLED_TOTALS_PROPERTY);
		if (controlledTotalsFilePath == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.CONTROLLED_TOTALS_PROPERTY + "' not found in the property file."); }
		GenstarCSVFile controlledTotalsCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, controlledTotalsFilePath, true), false);
		
		// SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String supplementaryAttributesFilePath = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (supplementaryAttributesFilePath == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.SUPPLEMENTARY_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCSVFile supplementaryAttributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, supplementaryAttributesFilePath, true), false);

		// with component populations
		if (sampleDataProperties.size() >= GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_COMPONENT_SAMPLE_DATA_NUMBER_OF_PROPERTIES) {

			// COMPONENT_POPULATION_PROPERTY
			String componentPopulationName = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
			if (componentPopulationName == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY + "' not found in the property file."); }
			
			// COMPONENT_SAMPLE_DATA_PROPERTY
			String componentSampleDataFilePath = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY);
			if (componentSampleDataFilePath == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
			GenstarCSVFile componentSampleDataFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, componentSampleDataFilePath, true), true);
			
			// COMPONENT_ATTRIBUTES_PROPERTY
			String componentAttributesFilePath = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
			if (componentAttributesFilePath == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
			GenstarCSVFile componentAttributesFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true), true);
			 
			// GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY
			String groupIdAttributeNameOnGroup = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
			if (groupIdAttributeNameOnGroup == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + "' not found in the property file."); }
			 
			// GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY
			String groupIdAttributeNameOnComponent = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
			if (groupIdAttributeNameOnComponent == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + "' not found in the property file."); }
			 
			
			// optional/supplementary properties (COMPONENT_REFERENCE_ON_GROUP, GROUP_REFERENCE_ON_COMPONENT)
			Map<String, String> supplementaryProperties = new HashMap<String, String>();
			supplementaryProperties.put(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_REFERENCE_ON_GROUP_PROPERTY, sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_REFERENCE_ON_GROUP_PROPERTY));
			supplementaryProperties.put(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_REFERENCE_ON_COMPONENT_PROPERTY, sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_REFERENCE_ON_COMPONENT_PROPERTY));
			
			supplementaryProperties.put(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY, groupIdAttributeNameOnGroup);
			supplementaryProperties.put(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY, groupIdAttributeNameOnComponent);
			
			
			GenstarFactoryUtils.createGroupComponentSampleDataGenerationRule(generator, ruleName, sampleCSVFile,
					controlledAttributesCSVFile, controlledTotalsCSVFile, supplementaryAttributesCSVFile,
					componentSampleDataFile, componentAttributesFile, componentPopulationName, supplementaryProperties);
			
		} else { // without component populations
			GenstarFactoryUtils.createSampleDataGenerationRule(generator, ruleName, sampleCSVFile, controlledAttributesCSVFile, controlledTotalsCSVFile, supplementaryAttributesCSVFile, idAttribute);
		}
		
	}

	public static void createGenerationRulesFromCSVFile(final IScope scope, final IMultipleRulesGenerator generator, final GenstarCSVFile distributionsCSVFile) throws GenstarException {
		List<List<String>> fileContent = distributionsCSVFile.getContent();
		if ( fileContent == null || fileContent.isEmpty() ) { throw new GenstarException("Invalid Generation Rule file: content is empty (file: " + distributionsCSVFile.getPath()  + ")"); }
		int rows = fileContent.size();

		if (distributionsCSVFile.getColumns() != GenstarFactoryUtils.CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS) { throw new GenstarException("CVS file must have " + GenstarFactoryUtils.CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS + " columns, (file: " + distributionsCSVFile.getPath() + ")"); }

		// 1. Parse the header
		List<String> header = distributionsCSVFile.getHeaders();
		for (int i=0; i<header.size(); i++) {
			if (!header.get(i).equals(GenstarFactoryUtils.CSV_FILE_FORMATS.GENERATION_RULE_METADATA.HEADERS[i])) {
				throw new GenstarException("Invalid Generation Rule file header. Header must be " + GenstarFactoryUtils.CSV_FILE_FORMATS.GENERATION_RULE_METADATA.HEADER_STR + " (file: " + distributionsCSVFile.getPath() + ")");
			}
		}
		
		
		// 2. Parse and initialize distributions
		for ( int rowIndex = 0; rowIndex < rows; rowIndex++ ) {
			
			final List<String> generationRuleInfo = fileContent.get(rowIndex);
			if (generationRuleInfo.size() != GenstarFactoryUtils.CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS) { throw new GenstarException("Invalid Generation Rule file format: each row must have " +  GenstarFactoryUtils.CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS + " columns, (file: " + distributionsCSVFile.getPath() + ")"); }
			
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
				GenstarFactoryUtils.createFrequencyDistributionGenerationRule(generator, ruleName, ruleDataFile);
			} else if (ruleTypeName.equals(AttributeInferenceGenerationRule.RULE_TYPE_NAME)) {
				GenstarFactoryUtils.createAttributeInferenceGenerationRule(generator, ruleName, ruleDataFile);
			} else if (ruleTypeName.equals(SampleDataGenerationRule.RULE_TYPE_NAME)) {
				throw new GenstarException("Unsupported generation rule type: " + SampleDataGenerationRule.RULE_TYPE_NAME);
				//createSampleDataGenerationRule(scope, generator, ruleName, properties);
			} else if (ruleTypeName.equals(CustomGenerationRule.RULE_TYPE_NAME)) { 
				GenstarFactoryUtils.createCustomGenerationRule(generator, ruleName, ruleDataFilePathOrJavaClass);
			} else {
				throw new GenstarException("Unsupported generation rule (" + ruleTypeName + "), file: " + distributionsCSVFile.getPath());
			}
		}
	}	
}
