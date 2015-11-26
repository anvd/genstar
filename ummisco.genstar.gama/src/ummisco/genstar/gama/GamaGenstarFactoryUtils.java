package ummisco.genstar.gama;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
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
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarFactoryUtils;

public class GamaGenstarFactoryUtils {

	static void createSampleDataGenerationRule(final IScope scope, final ISingleRuleGenerator generator, final String ruleName, final Properties sampleDataProperties) throws GenstarException {
		
		// Read the necessary data from the properties
		
		// SAMPLE_DATA_PROPERTY
		String sampleDataFilePath = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.SAMPLE_DATA_PROPERTY);
		if (sampleDataFilePath == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.SAMPLE_DATA_PROPERTY + "' not found"); }
		GenstarCSVFile sampleCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, sampleDataFilePath, true), true);
		
		// CONTROLLED_ATTRIBUTES_PROPERTY 
		String controlledAttributesFilePath = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (controlledAttributesFilePath == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.CONTROLLED_ATTRIBUTES_PROPERTY + "' not found"); }
		GenstarCSVFile controlledAttributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, controlledAttributesFilePath, true), false);
		
		// CONTROLLED_TOTALS_PROPERTY
		String controlledTotalsFilePath = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.CONTROLLED_TOTALS_PROPERTY);
		if (controlledTotalsFilePath == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.CONTROLLED_TOTALS_PROPERTY + "' not found"); }
		GenstarCSVFile controlledTotalsCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, controlledTotalsFilePath, true), false);
		
		// SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String supplementaryAttributesFilePath = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (supplementaryAttributesFilePath == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.SUPPLEMENTARY_ATTRIBUTES_PROPERTY + "' not found"); }
		GenstarCSVFile supplementaryAttributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, supplementaryAttributesFilePath, true), false);
		
		String componentSampleDataFilePath = null; // COMPONENT_SAMPLE_DATA_PROPERTY
		GenstarCSVFile componentSampleDataFile = null;
		String componentAttributesFilePath = null; // COMPONENT_ATTRIBUTES_PROPERTY
		GenstarCSVFile componentAttributesFile = null;
		String groupIdAttributeNameOnGroup = null; // GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY
		String groupIdAttributeNameOnComponent = null; // GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY
		
		if (sampleDataProperties.size() == GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.GROUP_COMPONENT_SAMPLE_DATA_NUMBER_OF_PROPERTIES) {
			 componentSampleDataFilePath = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.COMPONENT_SAMPLE_DATA_PROPERTY);
			 if (componentSampleDataFilePath == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.COMPONENT_SAMPLE_DATA_PROPERTY + "' not found"); }
			 componentSampleDataFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, componentSampleDataFilePath, true), true);
			 
			 componentAttributesFilePath = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.COMPONENT_ATTRIBUTES_PROPERTY);
			 if (componentAttributesFilePath == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.COMPONENT_ATTRIBUTES_PROPERTY + "' not found"); }
			 componentAttributesFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, controlledAttributesFilePath, true), true);
			 
			 groupIdAttributeNameOnGroup = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
			 if (groupIdAttributeNameOnGroup == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + "' not found"); }
			 
			 groupIdAttributeNameOnComponent = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
			 if (groupIdAttributeNameOnComponent == null) { throw new GenstarException("Property '" + GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + "' not found"); }
		}
		
		if (componentSampleDataFilePath != null) {
			GenstarFactoryUtils.createGroupComponentSampleDataGenerationRule(generator, ruleName, sampleCSVFile, controlledAttributesCSVFile, controlledTotalsCSVFile, supplementaryAttributesCSVFile, componentSampleDataFile, componentAttributesFile, groupIdAttributeNameOnGroup, groupIdAttributeNameOnComponent);
		} else {
			GenstarFactoryUtils.createSampleDataGenerationRule(generator, ruleName, sampleCSVFile, controlledAttributesCSVFile, controlledTotalsCSVFile, supplementaryAttributesCSVFile);
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
