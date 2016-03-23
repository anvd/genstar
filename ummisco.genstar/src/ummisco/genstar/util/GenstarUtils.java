package ummisco.genstar.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.GroupComponentSampleData;
import ummisco.genstar.ipf.ISampleData;
import ummisco.genstar.ipf.SampleData;
import ummisco.genstar.ipf.SampleDataGenerationRule;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.CustomGenerationRule;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.IMultipleRulesGenerator;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.SyntheticPopulation;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;

import com.google.common.collect.Sets;


// TODO refactoring
// divide this class into two: GenstarFactory & GenstarUtils
public class GenstarUtils {

	public static class AttributeValuesFrequencyComparator implements Comparator<AttributeValuesFrequency> {
		
		List<AbstractAttribute> sortingAttributes = null;
		
		public AttributeValuesFrequencyComparator(final List<AbstractAttribute> sortingAttributes) throws GenstarException {
			if (sortingAttributes == null || sortingAttributes.isEmpty()) { throw new GenstarException("'sortingAttributes' parameter can be neither null nor empty"); }
			
			this.sortingAttributes = new ArrayList<AbstractAttribute>(sortingAttributes);
		}

		@Override
		public int compare(final AttributeValuesFrequency valueFrequency1, final AttributeValuesFrequency valueFrequency2) {
			if (valueFrequency1 == null || valueFrequency2 == null) { throw new IllegalArgumentException("Input parameters can not be null"); }
			
			for (AbstractAttribute sAttribute : sortingAttributes) {
				AttributeValue value1 = valueFrequency1.getAttributeValue(sAttribute);
				AttributeValue value2 = valueFrequency2.getAttributeValue(sAttribute);

				if (value1 == null) { throw new IllegalArgumentException("'valueFrequency1' AttributeValuesFrequency doesn't contain " + sAttribute.getNameOnData() + " attribute."); }
				if (value2 == null) { throw new IllegalArgumentException("'valueFrequency2' AttributeValuesFrequency doesn't contain " + sAttribute.getNameOnData() + " attribute."); }
				
				int retVal = value1.compareTo(value2);
				if (retVal != 0) { return retVal; }
			}
			
			return 0;
		}
	}	
	
	
	public static final class CSV_FILE_FORMATS {

		public static final class ATTRIBUTE_METADATA {
			static final String ATTRIBUTE_VALUE_DELIMITER = ";";
			public static final String MIN_MAX_VALUE_DELIMITER = ":";
			public static final String FIELD_DELIMITER = ",";
				
			// Header of attribute meta-data file: 
			//		Name On Data,Name On Entity,Data Type,Value Type On Data,Values,Value Type On Entity
			static final String HEADER_STR = "Name On Data,Name On Entity, Data Type,Value Type,Values,Value Type On Entity";
			static final int NB_OF_COLS = 6;
			static String[] HEADERS = new String[NB_OF_COLS];
			static {
				HEADERS[0] = "Name On Data";
				HEADERS[1] = "Name On Entity";
				HEADERS[2] = "Data Type";
				HEADERS[3] = "Value Type On Data";
				HEADERS[4] = "Values";
				HEADERS[5] = "Value Type On Entity";
			}
			
			// Value Type Names
			static final String UNIQUE_VALUE_NAME = "Unique";
			static final String RANGE_VALUE_NAME = "Range";
		}
		
		
		public static final class GENERATION_RULE_METADATA {
			
			// Header of Generation Rule meta-data file:
			//		Name, File, Type
			public static final String HEADER_STR = "Name,File,Rule Type";
			public static final int NB_OF_COLS = 3;
			public static String[] HEADERS = new String[NB_OF_COLS];
			static {
				HEADERS[0] = "Name";
				HEADERS[1] = "File";
				HEADERS[2] = "Rule Type";
			}
			
			public static final String JAVA_CLASS_PARAMETER_DELIMITER = "?";
		}
		
		
		public static final class FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA {
			
			public static final String ATTRIBUTE_NAME_TYPE_DELIMITER = ":";
			
			public static final String INPUT_ATTRIBUTE = "Input";
			public static final String OUTPUT_ATTRIBUTE = "Output";
			public static final String FREQUENCY = "Frequency";
		}
	}
	
	public static final class FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES {
		
		public static final String POPULATION_NAME_PROPERTY = "POPULATION_NAME";

		public static final String ATTRIBUTES_PROPERTY = "ATTRIBUTES";
	
		public static final String GENERATION_RULES_PROPERTY = "GENERATION_RULES";
		
		public static final String NUMBER_OF_ENTITIES = "NUMBER_OF_ENTITIES";
	}
	
	public static final class SAMPLE_DATA_POPULATION_PROPERTIES {	
		
		public static final String POPULATION_NAME_PROPERTY = "POPULATION_NAME";
		
		public static final String ATTRIBUTES_PROPERTY = "ATTRIBUTES";
		
		public static final String ID_ATTRIBUTE_PROPERTY = "ID_ATTRIBUTE"; // TODO remove as of not use?
		
		public static final String SAMPLE_DATA_PROPERTY = "SAMPLE_DATA";
		
		public static final String CONTROLLED_ATTRIBUTES_PROPERTY = "CONTROLLED_ATTRIBUTES";
		
		public static final String CONTROLLED_TOTALS_PROPERTY = "CONTROLLED_TOTALS";
		
		public static final String SUPPLEMENTARY_ATTRIBUTES_PROPERTY = "SUPPLEMENTARY_ATTRIBUTES"; 
		
		public static final String MAX_ITERATIONS_PROPERTY = "MAX_ITERATIONS";
		
		
		// ATTENTION: if this property exists then the corresponding sample data is a group_component sample data
		public static final String COMPONENT_POPULATION_NAME_PROPERTY = "COMPONENT_POPULATION_NAME";
		
		public static final String COMPONENT_SAMPLE_DATA_PROPERTY = "COMPONENT_SAMPLE_DATA";
		
		public static final String COMPONENT_ATTRIBUTES_PROPERTY = "COMPONENT_ATTRIBUTES";
		
		public static final String COMPONENT_ID_ATTRIBUTE_PROPERTY = "COMPONENT_ID_ATTRIBUTE"; // TODO remove as of not use?
		
		public static final String GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY = "GROUP_ID_ATTRIBUTE_ON_GROUP";
		
		public static final String GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY = "GROUP_ID_ATTRIBUTE_ON_COMPONENT";
		
		public static final String COMPONENT_REFERENCE_ON_GROUP_PROPERTY = "COMPONENT_REFERENCE_ON_GROUP";
		
		public static final String GROUP_REFERENCE_ON_COMPONENT_PROPERTY = "GROUP_REFERENCE_ON_COMPONENT";
	}
	
	public static final class RANDOM_SINGLE_POPULATION_PROPERTIES {
		
		public static final String POPULATION_NAME_PROPERTY = "POPULATION_NAME";
		
		public static final String ATTRIBUTES_PROPERTY = "ATTRIBUTES";
		
		public static final String NB_OF_ENTITIES_PROPERTY = "NB_OF_ENTITIES";
	}
	
	public static final class RANDOM_COMPOUND_POPULATION_PROPERTIES {
		
		public static final String GROUP_POPULATION_NAME_PROPERTY = "GROUP_POPULATION_NAME";
		
		public static final String GROUP_ATTRIBUTES_PROPERTY = "GROUP_ATTRIBUTES";
		
		public static final String NB_OF_GROUP_ENTITIES_PROPERTY = "NB_OF_GROUP_ENTITIES";
		
		public static final String GROUP_SIZE_ATTRIBUTE_PROPERTY = "GROUP_SIZE_ATTRIBUTE";
		
		public static final String COMPONENT_POPULATION_NAME_PROPERTY = "COMPONENT_POPULATION_NAME";
		
		public static final String COMPONENT_ATTRIBUTES_PROPERTY = "COMPONENT_ATTRIBUTES";
		
		public static final String GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY = "GROUP_ID_ATTRIBUTE_ON_GROUP";
		
		public static final String GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY = "GROUP_ID_ATTRIBUTE_ON_COMPONENT";
	}
	
	public static final class CONTROL_TOTALS_PROPERTIES {
		
		public static final String ATTRIBUTES_PROPERTY = "ATTRIBUTES";
		
		public static final String ID_ATTRIBUTE_PROPERTY = "ID_ATTRIBUTE";
		
		public static final String CONTROLLED_ATTRIBUTES_PROPERTY = "CONTROLLED_ATTRIBUTES";
		
		public static final String POPULATION_DATA_PROPERTY = "POPULATION_DATA";
	}

	
	/**
	 * Creates a range value attributes.
	 * 
	 * Values are encoded as follows: "0:4, 5:17, 18:24, 25:34, 35:49, 50:64, 65:100".
	 * Each pair (e.g., "0:4") is a range value delimited by a ":" in which min value on the left of the colon while max value is on the right.
	 * 
	 * @param generator
	 * @param attributeNameOnData
	 * @param attributeNameOnEntity
	 * @param dataType
	 * @param values
	 * @param valueClassOnEntity
	 * @throws GenstarException
	 */
	static void createRangeValueAttribute(final ISyntheticPopulationGenerator generator, final String attributeNameOnData, final String attributeNameOnEntity, 
			final DataType dataType, final String values, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException {
		
		RangeValuesAttribute rangeAttribute = new RangeValuesAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, UniqueValue.class);
		
		// 1. Parse and accumulate each range value token into a list.
		StringTokenizer valueTokens = new StringTokenizer(values, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.ATTRIBUTE_VALUE_DELIMITER);
		if (valueTokens.countTokens() == 0) { throw new GenstarException("No value is defined for the attribute '" + attributeNameOnData + "'"); }
		List<String> rangeTokens = new ArrayList<String>();
		while (valueTokens.hasMoreTokens()) { rangeTokens.add(valueTokens.nextToken()); }
		
		// 2. Created range values from the parsed tokens.
		for (String t : rangeTokens) {
			StringTokenizer minMaxValueTokens = new StringTokenizer(t, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
			if (minMaxValueTokens.countTokens() != 2) { throw new GenstarException("Invalid attribute range value format (file: " + minMaxValueTokens.toString() + ")"); }
			
			rangeAttribute.add(new RangeValue(dataType, minMaxValueTokens.nextToken().trim(), minMaxValueTokens.nextToken().trim()));
		}
		
		generator.addAttribute(rangeAttribute);
	}

	/**
	 * Create an unique value attribute.
	 * 
	 * Values are encoded as follows: "C0, C1, C2, C3, C4, C5, C6, C7"
	 * 
	 * @param generator
	 * @param attributeNameOnData
	 * @param attributeNameOnEntity
	 * @param dataType
	 * @param values
	 * @param valueClassOnEntity
	 * @throws GenstarException
	 */
	static void createUniqueValueAttribute(final ISyntheticPopulationGenerator generator, final String attributeNameOnData, final String attributeNameOnEntity, 
			final DataType dataType, final String values, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException {
		
		UniqueValuesAttribute uniqueValueAttribute = new UniqueValuesAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, valueClassOnEntity);
		
		// 1. Parse and accumulate each unique value token into a list.
		StringTokenizer valueTokenizers = new StringTokenizer(values, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.ATTRIBUTE_VALUE_DELIMITER);
		if (valueTokenizers.countTokens() == 0) { throw new GenstarException("No value is defined for the attribute '" + attributeNameOnData + "'"); }
		List<String> uniqueValueTokens = new ArrayList<String>();
		while (valueTokenizers.hasMoreTokens()) { uniqueValueTokens.add(valueTokenizers.nextToken()); }
		
		// 2. Create unique values from the parsed tokens.
		for (String t : uniqueValueTokens) { uniqueValueAttribute.add(new UniqueValue(dataType, t.trim())); }
		
		generator.addAttribute(uniqueValueAttribute);
	}
	
	
	public static void createAttributesFromCSVFile(final ISyntheticPopulationGenerator generator, final GenstarCSVFile attributesFile) throws GenstarException {
		
		List<List<String>> fileContent = attributesFile.getContent();
		if ( fileContent == null || fileContent.isEmpty() ) { throw new GenstarException("Empty attribute file. File: " + attributesFile.getPath()); }
		
		if (attributesFile.getColumns() != CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS) { 
			throw new GenstarException("Attributes file must have " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS + " columns. File: " + attributesFile.getPath()); 
		}
		
		// 1. Parse the header
		List<String> fileHeader = attributesFile.getHeaders();
		if (fileHeader.size() != CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS) {
			throw new GenstarException("Attributes file header must have " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS + " columns. File: " + attributesFile.getPath());
		}
		for (int i=0; i<fileHeader.size(); i++) {
			if (!fileHeader.get(i).equals(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.HEADERS[i])) {
				throw new GenstarException("Invalid attribute file header must be " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.HEADER_STR + ". File: " + attributesFile.getPath());
			}
		}
		
		// 2. Parse and initialize attributes
		for ( List<String> attributeInfo : fileContent) {
			
			if (attributeInfo.size() != CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS) { throw new GenstarException("Invalid attribute file format: each row must have " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS + " columns (file: " + attributesFile.getPath() + "."); }
			
			String attributeNameOnData = (String)attributeInfo.get(0);
			String attributeNameOnEntity = (String)attributeInfo.get(1);
			
			String dataTypeStr = (String)attributeInfo.get(2);
			DataType dataType = DataType.fromName(dataTypeStr);
			if (dataType == null) { throw new GenstarException(dataTypeStr + " is not a supported data type."); }
			
			String valueTypeOnDataStr = (String)attributeInfo.get(3);
			String values = (String)attributeInfo.get(4);
			
			String valueTypeOnEntityStr = (String)attributeInfo.get(5);
			Class<? extends AttributeValue> valueClassOnEntity = AttributeValue.getClassByName(valueTypeOnEntityStr);
			
			
			String attributesFilePath = attributesFile.getPath();
			if (valueTypeOnDataStr.equals(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.RANGE_VALUE_NAME)) {
				try {
					createRangeValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity);
				} catch (Exception ex) {
					throw new GenstarException("Can not create range value attribute. File: " + attributesFilePath, ex);
				}
			} else if (valueTypeOnDataStr.equals(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.UNIQUE_VALUE_NAME)) {
				try {
					createUniqueValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity);
				} catch (Exception ex) {
					throw new GenstarException("Can not create unique value attribute. File: " + attributesFilePath, ex);
				}
			} else {
				throw new GenstarException("Invalid attribute file: unsupported value type (file: " + attributesFilePath + ")");
			}
		}
	}
	
	public static void createFrequencyDistributionGenerationRule(final IMultipleRulesGenerator generator, final String ruleName, final GenstarCSVFile ruleFile) throws GenstarException {
		
		List<List<String>> fileContent = ruleFile.getContent();
		if ( fileContent == null || fileContent.isEmpty() ) { throw new GenstarException("Frequency Distribution Generation Rule file is empty (file: " + ruleFile.getPath() + ")"); }
		
		
		// 1. Parse the header
		List<String> header = ruleFile.getHeaders();
		if (header.size() < 2) { throw new GenstarException("Invalid Frequency Distribution Generation Rule file header : header must have at least 2 elements (file: " + ruleFile.getPath() + ")"); }
		
		
		// 2. Create the rule then add attributes
		FrequencyDistributionGenerationRule generationRule = new FrequencyDistributionGenerationRule(generator, ruleName);
		List<AbstractAttribute> concerningAttributes = new ArrayList<AbstractAttribute>();
		for (int headerIndex=0; headerIndex < (header.size() - 1); headerIndex++) {
			StringTokenizer attributeToken = new StringTokenizer(header.get(headerIndex), CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.ATTRIBUTE_NAME_TYPE_DELIMITER);
			if (attributeToken.countTokens() != 2) {
				StringBuffer invalidTokens = new StringBuffer();
				while (attributeToken.hasMoreElements()) {
					invalidTokens.append(attributeToken.nextToken());
					invalidTokens.append(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.ATTRIBUTE_NAME_TYPE_DELIMITER);
				}
				
				throw new GenstarException("Invalid header format (" + invalidTokens.toString() + ") found in Frequency Distribution Generation Rule (file: " + ruleFile.getPath() + ")"); 
			}
			
			String attributeName = attributeToken.nextToken();
			String attributeType = attributeToken.nextToken();
			
			AbstractAttribute attribute = generator.getAttributeByNameOnData(attributeName);
			if (attribute == null) { throw new GenstarException("Unknown attribute (" + attributeName + ") found in Frequency Distribution Generation Rule (file: " + ruleFile.getPath() + ")"); }
			concerningAttributes.add(attribute);
			
			if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.INPUT_ATTRIBUTE)) {
				generationRule.appendInputAttribute(attribute);
			} else if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.OUTPUT_ATTRIBUTE)) {
				generationRule.appendOutputAttribute(attribute);
			} else {
				throw new GenstarException("Invalid attribute type (" + attributeType + ") found in Frequency Distribution Generation Rule (file: " + ruleFile.getPath() + ")");
			}
		}
		
		generationRule.generateAttributeValuesFrequencies();
		
		// 3. Set frequencies
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (int rowIndex = 0; rowIndex < fileContent.size(); rowIndex++) {
			attributeValues.clear();
			// IList frequencyInfo = fileContent.getRow(scope, rowIndex);
			List<String> frequencyInfo = fileContent.get(rowIndex);
			
			for (int attributeIndex=0; attributeIndex<(frequencyInfo.size()-1); attributeIndex++) {
				AbstractAttribute concerningAttribute = concerningAttributes.get(attributeIndex);
				DataType dataType = concerningAttribute.getDataType();
				
				if (concerningAttribute instanceof RangeValuesAttribute) {
					StringTokenizer minMaxValueToken = new StringTokenizer((String)frequencyInfo.get(attributeIndex), CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
					if (minMaxValueToken.countTokens() != 2) { throw new GenstarException("Invalid Frequency Distribution Generation Rule file format: invalid range attribute value"); }
					
					String minValue = minMaxValueToken.nextToken().trim();
					String maxValue = minMaxValueToken.nextToken().trim();
					RangeValue rangeValue = new RangeValue(dataType, minValue, maxValue);
					
					attributeValues.put(concerningAttribute, rangeValue);
				} else { // UniqueValuesAttribute
					String value = (String)frequencyInfo.get(attributeIndex);
					UniqueValue uniqueValue = new UniqueValue(dataType, value.trim());
					
					attributeValues.put(concerningAttribute, uniqueValue);
				}
			}
			
			// set frequency
			generationRule.setFrequency(attributeValues, Integer.parseInt(((String)frequencyInfo.get(frequencyInfo.size() - 1)).trim()));
		}
		
		// add generation rule to the generator
		generator.appendGenerationRule(generationRule);
	}	
	
	
	public static void createAttributeInferenceGenerationRule(final IMultipleRulesGenerator generator, final String ruleName, final GenstarCSVFile ruleFile) throws GenstarException {
		
		List<List<String>> fileContent = ruleFile.getContent();
		if ( fileContent == null || fileContent.isEmpty() ) { throw new GenstarException("Attribute Inference Generation Rule file is empty (file: " + ruleFile.getPath() + ")"); }
		int rows = ruleFile.getRows();
		
		
		// 1. Parse the header
		List<String> header = ruleFile.getHeaders();
		if (header.size() != 2) { throw new GenstarException("Invalid Attribute Inference Generation Rule file header : header must have 2 elements (file: " + ruleFile.getPath() + ")"); }
		String inferringAttributeName = header.get(0);
		String inferredAttributeName = header.get(1);
		
		AbstractAttribute inferringAttribute = generator.getAttributeByNameOnData(inferringAttributeName);
		if (inferringAttribute == null) { throw new GenstarException("Inferring attribute (" + inferringAttributeName + ") not found in the generator."); }
		
		AbstractAttribute inferredAttribute = generator.getAttributeByNameOnData(inferredAttributeName);
		if (inferredAttribute == null) { throw new GenstarException("Inferred attribute (" + inferredAttributeName + ") not found in the generator."); }
		
		if (rows != inferringAttribute.values().size()) { throw new GenstarException("Generation Rule must contain exacly the same number of attribute values defined in the inferring attribute and inferred attribute (file: " + ruleFile.getPath() + ")"); }
		
		
		// 2. Create the rule & set inference data
		AttributeInferenceGenerationRule generationRule = new AttributeInferenceGenerationRule(generator, ruleName, inferringAttribute, inferredAttribute);
		Map<AttributeValue, AttributeValue> inferenceData = new HashMap<AttributeValue, AttributeValue>();
		AttributeValue inferringValue, inferredValue;
		for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
			List<String> inferenceInfo = fileContent.get(rowIndex);
			
			if (inferringAttribute instanceof RangeValuesAttribute) {
				StringTokenizer minMaxValueToken = new StringTokenizer((String)inferenceInfo.get(0), CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
				if (minMaxValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value format: " + (String)inferenceInfo.get(0) + " (file: " + ruleFile.getPath() + ")"); }
				
				String minValue = minMaxValueToken.nextToken().trim();
				String maxValue = minMaxValueToken.nextToken().trim();
				inferringValue = inferringAttribute.getInstanceOfAttributeValue(new RangeValue(inferringAttribute.getDataType(), minValue, maxValue));
			} else {
				inferringValue = inferringAttribute.getInstanceOfAttributeValue(new UniqueValue(inferringAttribute.getDataType(), (String)inferenceInfo.get(0)));
			}
			
			if (inferredAttribute instanceof RangeValuesAttribute) {
				StringTokenizer minMaxValueToken = new StringTokenizer((String)inferenceInfo.get(1), CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
				if (minMaxValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value format: " + (String)inferenceInfo.get(1) + " (file: " + ruleFile.getPath() + ")"); }
				
				String minValue = minMaxValueToken.nextToken().trim();
				String maxValue = minMaxValueToken.nextToken().trim();
				inferredValue = inferredAttribute.getInstanceOfAttributeValue(new RangeValue(inferredAttribute.getDataType(), minValue, maxValue));
			} else {
				inferredValue = inferredAttribute.getInstanceOfAttributeValue(new UniqueValue(inferredAttribute.getDataType(), (String)inferenceInfo.get(1)));
			}
			
			if (inferringValue == null || inferredValue == null) { throw new GenstarException("Invalid Attribute Inference Generation Rule file content: Some attribute values are not contained in the inferring attribute or inferred attribute (file: " + ruleFile.getPath() + ")"); }
			if (inferenceData.containsKey(inferringValue)) { throw new GenstarException("Invalid Attribute Inference Generation Rule file content: inferringValue in " + inferringValue + ":" + inferredValue + " has already been found in a previous correspondence (file: " + ruleFile.getPath() + ")"); }
			if (inferenceData.containsValue(inferredValue)) { throw new GenstarException("Invalid Attribute Inference Generation Rule file content: inferredValue in " + inferringValue + ":" + inferredValue + " has already been found in a previous correspondence (file: " + ruleFile.getPath() + ")"); }
			
			inferenceData.put(inferringValue, inferredValue);
		}
		generationRule.setInferenceData(inferenceData);
		
		// add generation rule to the generator
		generator.appendGenerationRule(generationRule);		
	}	
	
	
	public static void createSampleDataGenerationRule(final ISingleRuleGenerator generator, final String ruleName, final GenstarCSVFile sampleFile,
			final GenstarCSVFile controlledAttributesFile, final GenstarCSVFile controlledTotalsFile, 
			final GenstarCSVFile supplementaryAttributesFile, final AbstractAttribute idAttribute, final int maxIterations) throws GenstarException {
		
		SampleDataGenerationRule rule = new SampleDataGenerationRule(generator, ruleName, controlledAttributesFile, controlledTotalsFile, supplementaryAttributesFile, maxIterations);
		
		if (idAttribute != null) {
			if (!generator.getAttributes().contains(idAttribute)) { throw new GenstarException(idAttribute.getNameOnEntity() + " is not recognized as an attribute of the generator"); }
			idAttribute.setIdentity(true);
		}
		
		ISampleData sampleData = new SampleData(generator.getPopulationName(), generator.getAttributes(), sampleFile);
		rule.setSampleData(sampleData);
		
		generator.setGenerationRule(rule);
		generator.setNbOfEntities(rule.getIPF().getNbOfEntitiesToGenerate());
	}
	
	
	public static void createGroupComponentSampleDataGenerationRule(final ISingleRuleGenerator groupGenerator, final String ruleName, final GenstarCSVFile groupSampleFile,
			final GenstarCSVFile groupControlledAttributesFile, final GenstarCSVFile groupControlledTotalsFile, final GenstarCSVFile groupSupplementaryAttributesFile,
			final GenstarCSVFile componentSampleFile, final GenstarCSVFile componentAttributesFile, final String componentPopulationName, 
			final int maxIterations, final Map<String, String> generatorProperties) throws GenstarException {
		
		SampleDataGenerationRule rule = new SampleDataGenerationRule(groupGenerator, ruleName, groupControlledAttributesFile, groupControlledTotalsFile, groupSupplementaryAttributesFile, maxIterations);

		ISingleRuleGenerator componentGenerator = new SingleRuleGenerator("Component Generator");
		createAttributesFromCSVFile(componentGenerator, componentAttributesFile);
		componentGenerator.setPopulationName(componentPopulationName);
		
		String groupIdAttributeNameOnGroup = generatorProperties.get(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
		AbstractAttribute groupIdAttributeOnGroup = rule.getAttributeByNameOnData(groupIdAttributeNameOnGroup);
		if (groupIdAttributeOnGroup == null) { throw new GenstarException("'" + groupIdAttributeNameOnGroup + "' is not a valid attribute"); }
		groupIdAttributeOnGroup.setIdentity(true);
		
		String groupIdAttributeNameOnComponent = generatorProperties.get(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
		AbstractAttribute groupIdAttributeOnComponent = componentGenerator.getAttributeByNameOnData(groupIdAttributeNameOnComponent);
		if (groupIdAttributeOnComponent == null) { throw new GenstarException("'" + groupIdAttributeOnComponent + "' is not a valid attribute"); }
		groupIdAttributeOnComponent.setIdentity(true);
		
		ISampleData groupSampleData = new SampleData(groupGenerator.getPopulationName(), groupGenerator.getAttributes(), groupSampleFile);
		String componentReferenceOnGroup = generatorProperties.get(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.COMPONENT_REFERENCE_ON_GROUP_PROPERTY);
		if (componentReferenceOnGroup != null) { 
			groupSampleData.addComponentReference(componentGenerator.getPopulationName(), componentReferenceOnGroup);
		}
		
		ISampleData componentSampleData = new SampleData(componentGenerator.getPopulationName(), componentGenerator.getAttributes(), componentSampleFile);
		String groupReferenceOnComponent = generatorProperties.get(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_REFERENCE_ON_COMPONENT_PROPERTY);
		if (groupReferenceOnComponent != null) {
			componentSampleData.addGroupReference(groupGenerator.getPopulationName(), groupReferenceOnComponent);
		}
		
		ISampleData groupComponentSampleData = new GroupComponentSampleData(groupSampleData, componentSampleData, groupIdAttributeOnGroup, groupIdAttributeOnComponent);
		rule.setSampleData(groupComponentSampleData);

		groupGenerator.setGenerationRule(rule);
		groupGenerator.setNbOfEntities(rule.getIPF().getNbOfEntitiesToGenerate());
	}

		
	static List<AbstractAttribute> parseSupplementaryAttributesCSVFile(final ISyntheticPopulationGenerator generator, final GenstarCSVFile supplementaryAttributesFile) throws GenstarException {
		List<AbstractAttribute> supplementaryAttributes = new ArrayList<AbstractAttribute>();
		
		AbstractAttribute supplementaryAttr;
		List<String> aRow;
		for (int r=0; r<supplementaryAttributesFile.getRows(); r++) {
			aRow = supplementaryAttributesFile.getRow(r);
			if (aRow.size() != 1) { throw new GenstarException("Invalid supplementary attribute file format: each row contains only one supplementary attribute. File: " + supplementaryAttributesFile.getPath()); }
			
			supplementaryAttr = generator.getAttributeByNameOnData(aRow.get(0));
			if (supplementaryAttr == null) { throw new GenstarException("Attribute '" + aRow.get(0) + "' not found on the generator."); }
			
			supplementaryAttributes.add(supplementaryAttr);
		}
		
		return supplementaryAttributes;
	}
	
	public static void createCustomGenerationRule(final IMultipleRulesGenerator generator, final String ruleName, final String ruleJavaClass) throws GenstarException {
		try {
			StringTokenizer ruleJavaClassTokenizer = new StringTokenizer(ruleJavaClass, CSV_FILE_FORMATS.GENERATION_RULE_METADATA.JAVA_CLASS_PARAMETER_DELIMITER);
			int tokens = ruleJavaClassTokenizer.countTokens();
			if (tokens > 2 || tokens == 0) { throw new GenstarException("Invalid custom generation rule: " + ruleJavaClass); }
			
			// parse java class name and parameter values
			String javaClassName = ruleJavaClassTokenizer.nextToken();
			String parameterValuesStr = "";
			if (tokens == 2) { parameterValuesStr = ruleJavaClassTokenizer.nextToken(); }
			
			Class ruleJavaClazz = Class.forName(javaClassName);
			Constructor customRuleConstructor = ruleJavaClazz.getConstructor(ISyntheticPopulationGenerator.class, String.class, String.class);
			CustomGenerationRule customRule = (CustomGenerationRule) customRuleConstructor.newInstance(generator, ruleName, parameterValuesStr);

			generator.appendGenerationRule(customRule);
		} catch (final Exception e) {
			if (e instanceof GenstarException) { throw (GenstarException)e; }
			else if (e instanceof InvocationTargetException) { e.printStackTrace(); }
			else throw new GenstarException(e.getMessage());
		}
	}	
	
	
	public static FrequencyDistributionGenerationRule createFrequencyDistributionFromSampleData(final ISyntheticPopulationGenerator generator, 
			final GenstarCSVFile distributionFormatFile, final GenstarCSVFile sampleDataFile) throws GenstarException {
		
		// 1. Parse the header
		List<String> distributionFormatHeader = distributionFormatFile.getHeaders();
		if (distributionFormatHeader.size() < 1) { throw new GenstarException("Header must have at least 1 elements (file: " + distributionFormatFile.getPath() + ")"); }
		
		
		// 2. Create the rule then add attributes
		FrequencyDistributionGenerationRule generationRule = new FrequencyDistributionGenerationRule(generator, "Dummy Frequency Distribution Generation Rule");
		List<AbstractAttribute> concerningAttributes = new ArrayList<AbstractAttribute>();
		for (int headerIndex=0; headerIndex < distributionFormatHeader.size(); headerIndex++) {
			StringTokenizer attributeToken = new StringTokenizer(distributionFormatHeader.get(headerIndex), CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.ATTRIBUTE_NAME_TYPE_DELIMITER);
			if (attributeToken.countTokens() != 2) {
				StringBuffer invalidTokens = new StringBuffer();
				while (attributeToken.hasMoreElements()) {
					invalidTokens.append(attributeToken.nextToken());
					invalidTokens.append(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.ATTRIBUTE_NAME_TYPE_DELIMITER);
				}
				
				throw new GenstarException("Invalid header format (" + invalidTokens.toString() + ") found in " + distributionFormatFile.getPath() + "."); 
			}
			
			String attributeName = attributeToken.nextToken();
			String attributeType = attributeToken.nextToken();
			
			AbstractAttribute attribute = generator.getAttributeByNameOnData(attributeName);
			if (attribute == null) { throw new GenstarException("Unknown attribute (" + attributeName + ") found in " + distributionFormatFile.getPath() + "."); }
			concerningAttributes.add(attribute);
			
			if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.INPUT_ATTRIBUTE)) {
				generationRule.appendInputAttribute(attribute);
			} else if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.OUTPUT_ATTRIBUTE)) {
				generationRule.appendOutputAttribute(attribute);
			} else {
				throw new GenstarException("Invalid attribute type (" + attributeType + ") found in Frequency Distribution Generation Rule (file: " + distributionFormatFile.getPath() + ")");
			}
		}
		
		generationRule.generateAttributeValuesFrequencies();
		
		// 3. Set frequencies
		// 3.1. save the index of attributes of the sample data
		List<String> sampleDataHeader = sampleDataFile.getHeaders();
		SortedMap<Integer, AbstractAttribute> attributeIndexes = new TreeMap<Integer, AbstractAttribute>();
		for (int col=0; col<sampleDataHeader.size(); col++) {
			String attributeNameOnEntity = sampleDataHeader.get(col);
			AbstractAttribute attribute = generationRule.getAttributeByNameOnEntity(attributeNameOnEntity);
			if (attribute != null) {
				attributeIndexes.put(col, attribute);
			};
		}
		
		// 3.2. calculate the number of attribute values
		List<List<String>> sampleData = sampleDataFile.getContent();
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		Set<AttributeValuesFrequency> attributeValuesFrequencies = generationRule.getAttributeValuesFrequencies();
		for (int row=0; row<sampleData.size(); row++) {
			attributeValues.clear();
			
			for (int col : attributeIndexes.keySet()) {
				String attributeValueString = sampleData.get(row).get(col);
				
				List<String> valueList = new ArrayList<String>();
				
				if (attributeValueString.contains(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER)) { // range value
					StringTokenizer rangeValueToken = new StringTokenizer(attributeValueString, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
					if (rangeValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value: '" + attributeValueString + "'. File: " + sampleDataFile.getPath()); }
					valueList.add(rangeValueToken.nextToken());
					valueList.add(rangeValueToken.nextToken());
				} else { // unique value
					valueList.add(attributeValueString);
				}
				
				AbstractAttribute concerningAttribute = attributeIndexes.get(col);
				AttributeValue value = concerningAttribute.findCorrespondingAttributeValue(valueList);
				if (value == null) { throw new GenstarException("'" + attributeValueString + "' is not a valid value of '" + concerningAttribute.getNameOnData() + "' attribute. File: " + sampleDataFile.getPath()); }
				
				attributeValues.put(concerningAttribute, value);
			}
			
			for (AttributeValuesFrequency avf : attributeValuesFrequencies) {
				if (avf.matchAttributeValues(attributeValues)) { 
					avf.setFrequency(avf.getFrequency() + 1);
					break;
				}
			}
		}
		 
		return generationRule;
	}
	
	
	// TODO improve this algo to remove bias
	public static List<Integer> findSubsetSum(final int total, final int numberOfElements) throws GenstarException {
		// parameters validation
		if (total < 1) { throw new GenstarException("Parameter total must be positive"); }
		if (numberOfElements < 1) { throw new GenstarException("Parameter numberOfElements must be positive"); }
		if (total < numberOfElements) { throw new GenstarException("total can not be smaller than numberOfElements"); }
		
		int internalTotal = total; 
		
		internalTotal -= (1 * numberOfElements);
		
		List<Integer> result = new ArrayList<Integer>();
		for (int i=0; i<numberOfElements-1; i++) {
			if (internalTotal == 0) { result.add(1); }
			else {

				int centerNumber = internalTotal / (numberOfElements - i);
				
//				int rndNumber = SharedInstances.RandomNumberGenerator.nextInt(internalTotal);
				int rndNumber = SharedInstances.RandomNumberGenerator.nextInt(centerNumber);
				result.add(1 + rndNumber);
				internalTotal -= rndNumber;
			}
		}
		
		result.add(1 + internalTotal);
		
		return result;
	}
	
	
	public static List<List<Map<AbstractAttribute, AttributeValue>>> buildControlledAttributesValuesSubsets(final Set<AbstractAttribute> controlledAttributes) throws GenstarException {
		
		// parameters validation
		if (controlledAttributes == null) { throw new GenstarException("Parameter controlledAttributes can not be null"); }
		if (controlledAttributes.size() < 2) { throw new GenstarException("controlledAttributes must contain at least 2 attributes"); }
		
		Set<Set<AbstractAttribute>> controlledAttributesSubsets = Sets.powerSet(controlledAttributes);
		
		Set<Set<AbstractAttribute>> validControlledAttributesSubSets = new HashSet<Set<AbstractAttribute>>();
		int validSubsetSize = controlledAttributes.size() - 1;
		for (Set<AbstractAttribute> subset : controlledAttributesSubsets) {
			if (subset.size() == validSubsetSize) { validControlledAttributesSubSets.add(subset); }
		}
		
		List<List<Map<AbstractAttribute, AttributeValue>>> resultingControlledAttributesValuesSubsets = new ArrayList<List<Map<AbstractAttribute, AttributeValue>>>();
		for (Set<AbstractAttribute> validSubsetAttributes : validControlledAttributesSubSets) {
			
			// build attributesValuesMaps
			List<Set<AttributeValue>> attributesPossibleValues = new ArrayList<Set<AttributeValue>>();
			for (AbstractAttribute attribute : validSubsetAttributes) {  attributesPossibleValues.add(attribute.values());  }
			List<Map<AbstractAttribute, AttributeValue>> attributeValuesMaps = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
			for (List<AttributeValue> cartesian : Sets.cartesianProduct(attributesPossibleValues)) {
				attributeValuesMaps.add(buildAttributeValueMap(validSubsetAttributes, cartesian));
			}
			
			
			List<Map<AbstractAttribute, AttributeValue>> controlledAttributesValuesSubset = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
			
			for (Map<AbstractAttribute, AttributeValue> attributeValuesMap : attributeValuesMaps) {
				Set<AbstractAttribute> subsetAttributes = attributeValuesMap.keySet();
				
//				if (validSubsetAttributes.size() == subsetAttributes.size() && subsetAttributes.containsAll(validSubsetAttributes)) { // TODO redundant condition: validSubsetAttributes.size() == subsetAttributes.size()
				if (subsetAttributes.containsAll(validSubsetAttributes)) {
					controlledAttributesValuesSubset.add(attributeValuesMap);
				}
			}
			
			if (!controlledAttributesValuesSubset.isEmpty()) {
				resultingControlledAttributesValuesSubsets.add(controlledAttributesValuesSubset);
			}
		}
		
		return resultingControlledAttributesValuesSubsets;
	}
	
	
	public static List<List<String>> generateControlTotals(final GenstarCSVFile attributesFile, final int total) throws GenstarException {
		// parameters validation
		if (attributesFile == null) { throw new GenstarException("Parameter attributesFile can not be null"); }
		if (total < 1) { throw new GenstarException("Parameter controlTotal must be positive"); }
		
		ISingleRuleGenerator generator = new SingleRuleGenerator("dummy single rule generator");
		createAttributesFromCSVFile(generator, attributesFile);
		
		// generate frequencies / control totals
		List<List<Map<AbstractAttribute, AttributeValue>>> controlledAttributesValuesSubsets = buildControlledAttributesValuesSubsets(new HashSet<AbstractAttribute>(generator.getAttributes()));
		List<List<String>> controlTotals = new ArrayList<List<String>>();
		for (List<Map<AbstractAttribute, AttributeValue>> controlledAttributesValuesSubset : controlledAttributesValuesSubsets) {

			List<Integer> subsetSum = findSubsetSum(total, controlledAttributesValuesSubset.size());
			int i=0;
			for (Map<AbstractAttribute, AttributeValue> attributeValueMap : controlledAttributesValuesSubset) {
				
				// write each row
				List<String> controlTotal = new ArrayList<String>();
				for (Map.Entry<AbstractAttribute, AttributeValue> entry : attributeValueMap.entrySet()) {
					controlTotal.add(entry.getKey().getNameOnData());
					controlTotal.add(entry.getValue().toCsvString());
				}
				
				// write frequency
				controlTotal.add(Integer.toString(subsetSum.get(i)));
				i++;

				controlTotals.add(controlTotal);
			}
		}
		
		return controlTotals;
	}
	
	
	public static void writeControlTotalsToCsvFile(final List<List<String>> controlTotals, final String csvFilePath) throws GenstarException {
		// parameters validation
		if( controlTotals == null) { throw new GenstarException("Parameter controlTotals can not be null"); }
		if (csvFilePath == null) { throw new GenstarException("Parameter csvFilePath can not be null"); }
		
		try {
			CsvWriter writer = new CsvWriter(csvFilePath);
			for (List<String> row : controlTotals) {
				writer.writeRecord(row.toArray(new String[0]));
			}
			
			writer.flush();
			writer.close();
		} catch (final Exception e) {
			throw new GenstarException(e);
		}
	}
	
	
	public static ISyntheticPopulation generateRandomSinglePopulation(final String populationName, final GenstarCSVFile attributesFile, 
			final int minEntitiesOfEachAttributeValuesSet, final int maxEntitiesOfEachAttributeValuesSet) throws GenstarException {
		// parameters validation
		if (populationName == null || populationName.isEmpty()) { throw new GenstarException("Parameter populationName can not be null or empty"); }
		if (attributesFile == null) { throw new GenstarException("Parameter attributesFile can not be null"); }
		if (minEntitiesOfEachAttributeValuesSet < 1) { throw new GenstarException("minEntitiesOfEachAttributeValuesSet can not be smaller than 1"); }
		if (maxEntitiesOfEachAttributeValuesSet < minEntitiesOfEachAttributeValuesSet) { throw new GenstarException("maxEntitiesOfEachAttributeValuesSet can not be smaller than minEntitiesOfEachAttributeValuesSet"); }
		
		ISingleRuleGenerator generator = new SingleRuleGenerator("dummy single rule generator");
		createAttributesFromCSVFile(generator, attributesFile);

		Set<AbstractAttribute> attributes = new HashSet<AbstractAttribute>(generator.getAttributes());
		ISyntheticPopulation syntheticPopulation = new SyntheticPopulation(populationName, generator.getAttributes());

		// build attributeValuesMaps
		List<Set<AttributeValue>> attributesPossibleValues = new ArrayList<Set<AttributeValue>>();
		Map<AbstractAttribute, Boolean> valueOnDataSameAsValueOnEntity = new HashMap<AbstractAttribute, Boolean>();
		for (AbstractAttribute attribute : attributes) { 
			attributesPossibleValues.add(attribute.values()); 
			
			if (attribute.getValueClassOnData().equals(attribute.getValueClassOnEntity())) { valueOnDataSameAsValueOnEntity.put(attribute, true); }
			else { valueOnDataSameAsValueOnEntity.put(attribute, false); }
			
		}
		List<Map<AbstractAttribute, AttributeValue>> attributeValuesMaps = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
		for (List<AttributeValue> cartesian : Sets.cartesianProduct(attributesPossibleValues)) {
			attributeValuesMaps.add(buildAttributeValueMap(attributes, cartesian));
		}
		
		
		// build attributeValuesOnEntityMaps
		List<Map<String, AttributeValue>> attributeValuesOnEntityMaps = new ArrayList<Map<String, AttributeValue>>();
		for (Map<AbstractAttribute, AttributeValue> attributeValuesOnData : attributeValuesMaps) {
			Map<String, AttributeValue> attributeValuesOnEntity = new HashMap<String, AttributeValue>();
			
			for (Map.Entry<AbstractAttribute, AttributeValue> entry : attributeValuesOnData.entrySet()) {
				if (valueOnDataSameAsValueOnEntity.get(entry.getKey())) {
					attributeValuesOnEntity.put(entry.getKey().getNameOnEntity(), entry.getValue());
				} else {
					attributeValuesOnEntity.put(entry.getKey().getNameOnEntity(), entry.getValue().cast(entry.getKey().getValueClassOnEntity()));
				}
			}
			
			attributeValuesOnEntityMaps.add(attributeValuesOnEntity);
		}
		
		
		// generate the population (sample data)
		if (minEntitiesOfEachAttributeValuesSet == maxEntitiesOfEachAttributeValuesSet) {
			for (Map<String, AttributeValue> attributeValuesOnEntity : attributeValuesOnEntityMaps) {
				for (int entityIndex=0; entityIndex<minEntitiesOfEachAttributeValuesSet; entityIndex++) {
					syntheticPopulation.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity);
				}
				
			}
		} else {
			int entityDifference = maxEntitiesOfEachAttributeValuesSet - minEntitiesOfEachAttributeValuesSet;
			for (Map<String, AttributeValue> attributeValuesOnEntity : attributeValuesOnEntityMaps) {
				int nbOfEntities = minEntitiesOfEachAttributeValuesSet + SharedInstances.RandomNumberGenerator.nextInt(entityDifference);
				
				for (int entityIndex=0; entityIndex<nbOfEntities; entityIndex++) {
					syntheticPopulation.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity);
				}
			}
		}
		
		return syntheticPopulation;
	}
	
	
	public static ISyntheticPopulation generateRandomSinglePopulation(final String populationName, final GenstarCSVFile attributesFile, final int entities) throws GenstarException {
		// parameters validation
		if (populationName == null || populationName.isEmpty()) { throw new GenstarException("Parameter populationName can not be null or empty"); }
		if (attributesFile == null) { throw new GenstarException("Parameter attributesFile can not be null"); }
		if (entities <= 0) { throw new GenstarException("Parameter entities must be positive"); }
		
		ISingleRuleGenerator generator = new SingleRuleGenerator("dummy single rule generator");
		createAttributesFromCSVFile(generator, attributesFile);
		
		List<AbstractAttribute> attributes = new ArrayList<AbstractAttribute>(generator.getAttributes());
		
		// cache values to use later on
		Map<AbstractAttribute, List<AttributeValue>> attributeValues = new HashMap<AbstractAttribute, List<AttributeValue>>();
		Map<AbstractAttribute, Integer> attributeValueSizes = new HashMap<AbstractAttribute, Integer>();
		Map<AbstractAttribute, Boolean> valueOnDataSameAsValueOnEntity = new HashMap<AbstractAttribute, Boolean>();
		Map<AbstractAttribute, Class> valueOnEntityClasses = new HashMap<AbstractAttribute, Class>();
		for (AbstractAttribute attr : attributes) { 
			attributeValues.put(attr, new ArrayList<AttributeValue>(attr.values())); 
			attributeValueSizes.put(attr, attr.values().size());
			valueOnEntityClasses.put(attr, attr.getValueClassOnEntity());
			
			if (attr.getValueClassOnData().equals(attr.getValueClassOnEntity())) { valueOnDataSameAsValueOnEntity.put(attr, true); }
			else { valueOnDataSameAsValueOnEntity.put(attr, false); }
		}
		
		ISyntheticPopulation syntheticPopulation = new SyntheticPopulation(populationName, attributes);
		
		// generate the population
		int size;
		List<AttributeValue> values;
		AttributeValue valueOnData;
		Map<String, AttributeValue> attributeValuesOnEntity = new HashMap<String, AttributeValue>();
		for (int i=0; i<entities; i++) {
			for (int attrIndex=0; attrIndex<attributes.size(); attrIndex++) {
				values = attributeValues.get(attributes.get(attrIndex));
				size = attributeValueSizes.get(attributes.get(attrIndex));
				valueOnData = values.get(SharedInstances.RandomNumberGenerator.nextInt(size));
				
				if (valueOnDataSameAsValueOnEntity.get(attributes.get(attrIndex))) {
					attributeValuesOnEntity.put(attributes.get(attrIndex).getNameOnEntity(), valueOnData);
				} else { // valueOnData != valueOnEntity
					attributeValuesOnEntity.put(attributes.get(attrIndex).getNameOnEntity(), valueOnData.cast(valueOnEntityClasses.get(attributes.get(attrIndex))));
				}
			}
	
			syntheticPopulation.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity);
		}
		
		return syntheticPopulation;
	}
	

	public static ISyntheticPopulation generateRandomCompoundPopulation(final String groupPopulationName, final GenstarCSVFile groupAttributesFile, 
			final String componentPopulationName, final GenstarCSVFile componentAttributesFile, final String groupIdAttributeNameOnGroupEntity, 
			final String groupIdAttributeNameOnComponentEntity, final String groupSizeAttributeName, final int nbOfGroupEntities) throws GenstarException {
		
		// 0. parameters validation
		if (groupPopulationName == null) { throw new GenstarException("Parameter groupPopulationName can not be null"); }
		if (groupAttributesFile == null) { throw new GenstarException("Parameter groupAttributesFile can not be null"); }
		if (componentPopulationName == null) { throw new GenstarException("Parameter componentPopulationName can not be null"); }
		if (componentAttributesFile == null) { throw new GenstarException("Parameter componentAttributesFile can not be null"); }
		if (groupIdAttributeNameOnGroupEntity == null) { throw new GenstarException("Parameter groupIdAttributeNameOnGroupEntity can not be null"); }
		if (groupIdAttributeNameOnComponentEntity == null) { throw new GenstarException("Parameter groupIdAttributeNameOnComponentEntity can not be null"); }
		if (groupSizeAttributeName == null) { throw new GenstarException("Parameter groupSizeAttributeName can not be null"); }
		if (nbOfGroupEntities <= 0) { throw new GenstarException("Parameter nbOfGroupEntities must be positive"); }

		
		// 1. create group attributes from groupAttributesFile
		ISingleRuleGenerator groupGenerator = new SingleRuleGenerator("group dummy generator");
		createAttributesFromCSVFile(groupGenerator, groupAttributesFile);
		List<AbstractAttribute> groupAttributes = new ArrayList<AbstractAttribute>(groupGenerator.getAttributes());
		
		// 2. retrieve reference to groupIdAttributeOnGroupEntity
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData(groupIdAttributeNameOnGroupEntity);
		if (groupIdAttributeOnGroupEntity == null) { throw new GenstarException(groupIdAttributeNameOnGroupEntity + " is considered as group identity attribute but not found among the available group attributes"); }
		groupIdAttributeOnGroupEntity.setIdentity(true);
		// Important Note: groupIdOnGroupAttribute is an integer, beginning with 0, taking 1 as increment
		// ID should not be defined in the attributesFile
		
		// 3.  retrieve reference to groupSizeAttribute
		AbstractAttribute groupSizeAttribute = groupGenerator.getAttributeByNameOnData(groupSizeAttributeName);
		if (groupSizeAttribute == null) { throw new GenstarException(groupSizeAttributeName + " is considered as group size attribute but not found among the available group attributes"); }
		if (!groupSizeAttribute.getValueClassOnEntity().equals(UniqueValue.class)) { throw new GenstarException(groupSizeAttributeName + " attribute must have unique value"); }
		if (!groupSizeAttribute.getDataType().equals(DataType.INTEGER)) { throw new GenstarException(groupSizeAttributeName + " attribute must have " + DataType.INTEGER.getName() + " as data type"); }
		
		// 4. generate group population
		ISyntheticPopulation groupPopulation = generateGroupPopulation(groupPopulationName, groupAttributes, groupIdAttributeOnGroupEntity, nbOfGroupEntities);

		// 5. create component attributes from componentAttributesFile
		ISingleRuleGenerator componentGenerator = new SingleRuleGenerator("component dummy generator");
		createAttributesFromCSVFile(componentGenerator, componentAttributesFile);
		List<AbstractAttribute> componentAttributes = new ArrayList<AbstractAttribute>(componentGenerator.getAttributes());

		AbstractAttribute groupIdAttributeOnComponentEntity = componentGenerator.getAttributeByNameOnData(groupIdAttributeNameOnComponentEntity);
		if (groupIdAttributeOnComponentEntity == null) { throw new GenstarException(groupIdAttributeNameOnComponentEntity + " is considered as group identity attribute on component but not found among the available component attributes"); }
		groupIdAttributeOnComponentEntity.setIdentity(true);

		// 6. generate component population
		generateComponentPopulation(groupPopulation, componentPopulationName, componentAttributes, groupIdAttributeOnGroupEntity,
				groupIdAttributeOnComponentEntity, groupSizeAttribute);
		
		
		return groupPopulation;
	}
	
	
	public static final Map<String, String> writePopulationToCSVFile(final ISyntheticPopulation population, final Map<String, String> csvFilePathsByPopulationNames) throws GenstarException {
		// parameters validation
		if (population == null) { throw new GenstarException("Parameter population can not be null"); }
		if (csvFilePathsByPopulationNames == null) { throw new GenstarException("Parameter csvFilePathsByPopulationNames can not be null"); }
		
		// build CsvWriters
		Map<String, CsvWriter> csvWriters = new HashMap<String, CsvWriter>();
		buildCsvWriters(population, csvWriters, csvFilePathsByPopulationNames);
		
		// write to csv files
		doWritePopulationToCsvFile(population, csvWriters);
		
		// TODO optimization: merge build + write processes into one
		
		// close CsvWriters
		for (CsvWriter writer : csvWriters.values()) { writer.close(); }
		
		return csvFilePathsByPopulationNames;
	}
	
	private static final void buildCsvWriters(final ISyntheticPopulation population, final Map<String, CsvWriter> csvWriters, 
			final Map<String, String> csvFilePathsByPopulationNames) throws GenstarException {
		try {
			String populationName = population.getName();
			String csvFilePath = csvFilePathsByPopulationNames.get(populationName);
			CsvWriter writer = csvWriters.get(populationName);
			
			// build writer
			if (csvFilePath != null && writer == null) {
				writer = new CsvWriter(csvFilePath);
				csvWriters.put(populationName, writer);
				List<AbstractAttribute> attributes = population.getAttributes();
				
				// write the header
				String header[] = new String[attributes.size()];
				for (int i=0; i<attributes.size(); i++) { header[i] = attributes.get(i).getNameOnEntity(); } // TODO name on data or name on entity?
				writer.writeRecord(header);
			}
			
			// recursively build writers for component populations
			for (Entity e : population.getEntities()) {
				for (ISyntheticPopulation componentPopulation : e.getComponentPopulations()) {
					String componentPopulationName = componentPopulation.getName();
					if (csvWriters.get(componentPopulationName) == null && csvFilePathsByPopulationNames.get(componentPopulationName) != null) {
						buildCsvWriters(componentPopulation, csvWriters, csvFilePathsByPopulationNames);
					}
				}
			}
		} catch (final IOException e) {
			throw new GenstarException(e);
		}
	}
	
	private static final void doWritePopulationToCsvFile(final ISyntheticPopulation population, final Map<String, CsvWriter> csvWriters) throws GenstarException {
		
		try {
			CsvWriter writer = csvWriters.get(population.getName());
			
			if (writer != null) {
				List<AbstractAttribute> attributes = population.getAttributes();
				String[] entityValues = new String[attributes.size()]; 
				
				// write population's entities to CSV file
				for (Entity e : population.getEntities()) {
					Map<String, EntityAttributeValue> attributeValues = e.getEntityAttributeValues();
					for (int attrIndex=0; attrIndex<attributes.size(); attrIndex++) {
						entityValues[attrIndex] = attributeValues.get(attributes.get(attrIndex).getNameOnData()).getAttributeValueOnEntity().toCsvString();
					}
					
					writer.writeRecord(entityValues);
					
					// recursively write entity's component populations
					for (ISyntheticPopulation componentPopulation : e.getComponentPopulations()) {
						if (csvWriters.get(componentPopulation.getName()) != null) {
							doWritePopulationToCsvFile(componentPopulation, csvWriters);
						}
					}
				}
			}	
		} catch (final IOException e) {
			throw new GenstarException(e);
		}
	}
	
	
	private static ISyntheticPopulation generateGroupPopulation(final String populationName, final List<AbstractAttribute> groupAttributes, final AbstractAttribute groupIdAttributeOnGroupEntity, final int nbOfGroupEntities) throws GenstarException {
		ISyntheticPopulation groupPopulation = new SyntheticPopulation(populationName, groupAttributes);
	
		// 0. find groupIdAttributeIndexOnGroupEntity
		int groupIdAttributeIndexOnGroupEntity = -1;
		for (int i=0; i<groupAttributes.size(); i++) {
			if (groupAttributes.get(i).equals(groupIdAttributeOnGroupEntity)) { 
				groupIdAttributeIndexOnGroupEntity = i;
				break;
			}
		}

		// 1. cache attribute values and their positions for later use
		Map<AbstractAttribute, List<AttributeValue>> groupAttributeValues = new HashMap<AbstractAttribute, List<AttributeValue>>();
		Map<AbstractAttribute, Integer> groupAttributeValueSizes = new HashMap<AbstractAttribute, Integer>();
		Map<AbstractAttribute, Boolean> valueOnDataSameAsValueOnEntity = new HashMap<AbstractAttribute, Boolean>();
		Map<AbstractAttribute, Class> valueOnEntityClasses = new HashMap<AbstractAttribute, Class>();
		for (AbstractAttribute attr : groupAttributes) {
			groupAttributeValues.put(attr, new ArrayList<AttributeValue>(attr.values())); 
			groupAttributeValueSizes.put(attr, attr.values().size());
			valueOnEntityClasses.put(attr, attr.getValueClassOnEntity());
			
			if (attr.getValueClassOnData().equals(attr.getValueClassOnEntity())) { valueOnDataSameAsValueOnEntity.put(attr, true); }
			else { valueOnDataSameAsValueOnEntity.put(attr, false); }
		}

		// 2. generate entities
		Map<String, AttributeValue> attributeValuesOnEntity = new HashMap<String, AttributeValue>();
		int groupIdValue = 0;
		int groupColumns = groupAttributes.size();
		int valuesSize;
		List<AttributeValue> values;
		AttributeValue valueOnData;
		for (int i=0; i<nbOfGroupEntities; i++) {
			
			for (int col=0; col<groupColumns; col++) {
				if (col == groupIdAttributeIndexOnGroupEntity) { // ID attribute: value is computed automatically
					attributeValuesOnEntity.put(groupIdAttributeOnGroupEntity.getNameOnEntity(), new UniqueValue(DataType.INTEGER, Integer.toString(groupIdValue)));
					groupIdValue++;
				} else {
					values = groupAttributeValues.get(groupAttributes.get(col));
					valuesSize = groupAttributeValueSizes.get(groupAttributes.get(col));
					valueOnData = values.get(SharedInstances.RandomNumberGenerator.nextInt(valuesSize));
					
					if (valueOnDataSameAsValueOnEntity.get(groupAttributes.get(col))) {
						attributeValuesOnEntity.put(groupAttributes.get(col).getNameOnEntity(), valueOnData);
					} else { // valueOnData != valueOnEntity
						attributeValuesOnEntity.put(groupAttributes.get(col).getNameOnEntity(), valueOnData.cast(valueOnEntityClasses.get(groupAttributes.get(col))));
					}
				}
			}
			
			groupPopulation.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity);
		}
		
		return groupPopulation;
	}

	
	private static void generateComponentPopulation(final ISyntheticPopulation groupPopulation, final String componentPopulationName, 
			final List<AbstractAttribute> componentAttributes, final AbstractAttribute groupIdAttributeOnGroupEntity, 
			final AbstractAttribute groupIdAttributeOnComponentEntity, final AbstractAttribute groupSizeAttribute) throws GenstarException {
		
		// 1. cache attribute values and their positions for later use
		Map<AbstractAttribute, List<AttributeValue>> componentAttributeValues = new HashMap<AbstractAttribute, List<AttributeValue>>();
		Map<AbstractAttribute, Integer> componentAttributeValueSizes = new HashMap<AbstractAttribute, Integer>();
		Map<AbstractAttribute, Boolean> valueOnDataSameAsValueOnEntity = new HashMap<AbstractAttribute, Boolean>();
		Map<AbstractAttribute, Class> valueOnEntityClasses = new HashMap<AbstractAttribute, Class>();
		for (AbstractAttribute attr : componentAttributes) { 
			componentAttributeValues.put(attr, new ArrayList<AttributeValue>(attr.values())); 
			componentAttributeValueSizes.put(attr, attr.values().size());
			valueOnEntityClasses.put(attr, attr.getValueClassOnEntity());
			
			if (attr.getValueClassOnData().equals(attr.getValueClassOnEntity())) { valueOnDataSameAsValueOnEntity.put(attr, true); }
			else { valueOnDataSameAsValueOnEntity.put(attr, false); }
		}

		int groupSize;
		List<AttributeValue> values;
		AttributeValue valueOnData;
		AttributeValue groupIDValue;
		int valuesSize;
		Map<String, AttributeValue> attributeValuesOnComponentEntity = new HashMap<String, AttributeValue>();
		for (Entity groupEntity : groupPopulation.getEntities()) {
			groupSize = ((UniqueValue) groupEntity.getEntityAttributeValueByNameOnData(groupSizeAttribute.getNameOnData()).getAttributeValueOnEntity()).getIntValue();
			groupIDValue = groupEntity.getEntityAttributeValueByNameOnData(groupIdAttributeOnGroupEntity.getNameOnData()).getAttributeValueOnEntity();
			
			if (groupSize > 0) {
				ISyntheticPopulation componentPopulation = groupEntity.createComponentPopulation(componentPopulationName, componentAttributes);

				for (int i=0; i<groupSize; i++) {
					
					for (AbstractAttribute componentAttr : componentAttributes) {
						if (componentAttr.equals(groupIdAttributeOnComponentEntity)) { // component's groupID
							attributeValuesOnComponentEntity.put(groupIdAttributeOnComponentEntity.getNameOnEntity(), groupIDValue);
						} else { // other columns/fields
							values = componentAttributeValues.get(componentAttr);
							valuesSize = componentAttributeValueSizes.get(componentAttr);
							valueOnData = values.get(SharedInstances.RandomNumberGenerator.nextInt(valuesSize));
						
							if (valueOnDataSameAsValueOnEntity.get(componentAttr)) {
								attributeValuesOnComponentEntity.put(componentAttr.getNameOnEntity(), valueOnData);
								// componentEntity[col] = valueOnData.toCSVString();
							} else { // valueOnData != valueOnEntity
								attributeValuesOnComponentEntity.put(componentAttr.getNameOnEntity(), valueOnData.cast(valueOnEntityClasses.get(componentAttr)));
								// componentEntity[col] = valueOnData.cast(valueOnEntityClasses.get(componentAttributes.get(col))).toCSVString();
							}					 
						}
					}
					
					componentPopulation.createEntityWithAttributeValuesOnEntity(attributeValuesOnComponentEntity);
				}
			}
		}
	}

	
	public static AttributeValue createAttributeValue(final Class<? extends AttributeValue> attributeClass, final DataType dataType, final List<String> stringValue) throws GenstarException {
		if (attributeClass == null || dataType == null || stringValue == null) {
			throw new GenstarException("Parameters attributeClass, dataType, stringValue can not be null");
		}
		
		if (attributeClass.equals(UniqueValue.class)) {
			if (stringValue.size() >= 1) { return new UniqueValue(dataType, stringValue.get(0)); }

			throw new GenstarException("Invalid stringValue " + stringValue);
		}
		
		if (attributeClass.equals(RangeValue.class)) {
			if (stringValue.size() == 1) { return new RangeValue(dataType, stringValue.get(0), stringValue.get(0)); }
			if (stringValue.size() >= 2) { return new RangeValue(dataType, stringValue.get(0), stringValue.get(1)); }
			
			throw new GenstarException("Invalid stringValue " + stringValue);
		}
		
		return null;
	}
	
	
	public static Map<AbstractAttribute, AttributeValue> buildAttributeValueMap(final Set<AbstractAttribute> attributes, final List<AttributeValue> values) throws GenstarException {
		Map<AbstractAttribute, AttributeValue> retVal = new HashMap<AbstractAttribute, AttributeValue>();
		
		List<AbstractAttribute> copyAttributes = new ArrayList<AbstractAttribute>();
		copyAttributes.addAll(attributes);
		AbstractAttribute concernedAttr;
		for (AttributeValue v : values) {
			concernedAttr = null;
			for (AbstractAttribute attr : copyAttributes) {
				if (attr.containsInstanceOfAttributeValue(v)) { 
					retVal.put(attr, v); 
					concernedAttr = attr;
					break;
				}
			}
			copyAttributes.remove(concernedAttr);
		}
		
		return retVal;
	}
	
	
	public static List<Integer> analyseIpfPopulation(final ISyntheticPopulation population, final GenstarCSVFile controlledAttributesListFile, 
			final GenstarCSVFile controlTotalsFile) throws GenstarException {
		
		// parameters validation
		if (population == null || controlledAttributesListFile == null || controlTotalsFile == null) {
			throw new GenstarException("Parameters population, controlledAttributesListFile, controlTotalsFile can not be null");
		}
		
		List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
		List<List<String>> controlledAttributesList = controlledAttributesListFile.getContent();
		if (controlledAttributesList.size() < 2) { throw new GenstarException("The number of controlled attributes must be at least 2 (file: " + controlledAttributesListFile.getPath() + ")."); }
		for (int line=0; line<controlledAttributesList.size(); line++) {
			List<String> row = controlledAttributesList.get(line);
			if (row.size() > 1) { throw new GenstarException("Invalid controlledAttributesListFile file format (file: " + controlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			
			String controlledAttrName = row.get(0);
			AbstractAttribute controlledAttribute = population.getAttributeByNameOnData(controlledAttrName);
			if (controlledAttribute == null) { throw new GenstarException("Invalid controlled attribute: " + controlledAttrName + ". File: " + controlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			if (controlledAttributes.contains(controlledAttribute)) { throw new GenstarException("Duplicated controlled attribute: " + controlledAttrName + ". File: " + controlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			
			controlledAttributes.add(controlledAttribute);
		}

		// read attribute values frequencies from control totals file then do the analysis
		List<AttributeValuesFrequency> attributeValuesFrequencies = readAttributeValuesFrequenciesFromControlTotalsFile(controlTotalsFile, controlledAttributes);
		List<Integer> generatedFrequencies = new ArrayList<Integer>();
		List<Entity> entities = population.getEntities();
		for (AttributeValuesFrequency avf : attributeValuesFrequencies) {
			int matched = 0;
			for (Entity e : entities) { if (avf.matchEntity(e)) { matched++; } }
			generatedFrequencies.add(matched);
		}
		
		return generatedFrequencies;
	}
	
	
	public static GenstarCSVFile writeAnalsysisResultToFile(final GenstarCSVFile controlTotalsFile, final List<Integer> analysisResult, final String csvOutputFilePath) throws GenstarException {
		
		// parameters validation
		if (controlTotalsFile == null || analysisResult == null || csvOutputFilePath == null) {
			throw new GenstarException("Parameters controlTotalsFile, analysisResult, csvOutputFilePath can not be null");
		}
		
		if (controlTotalsFile.getRows() != analysisResult.size()) {
			throw new GenstarException("controlTotalsFile's row is different from analsysisResult's size (" + controlTotalsFile.getRows() + " v.s. " + analysisResult.size() + ")");
		}
		
		// write analysis result to file
		CsvWriter writer = new CsvWriter(csvOutputFilePath);
		try {
			int line=0;
			for (List<String> row : controlTotalsFile.getContent()) {
				String[] newRow = new String[row.size() + 1];
				newRow = Arrays.copyOf(row.toArray(new String[0]), newRow.length);
				newRow[newRow.length - 1] = analysisResult.get(line).toString();
				
				writer.writeRecord(newRow);
				line++;
			}
		} catch (IOException ioe) {
			throw new GenstarException(ioe);
		} finally {
			writer.close();
		}
		
		return new GenstarCSVFile(csvOutputFilePath, false);
	}
	

	public static Set<AttributeValuesFrequency> generateAttributeValuesFrequencies(final Set<AbstractAttribute> attributes) throws GenstarException {
		
		Set<AttributeValuesFrequency> avfs = new HashSet<AttributeValuesFrequency>();
		
		List<Set<AttributeValue>> attributesPossibleValues = new ArrayList<Set<AttributeValue>>();
		for (AbstractAttribute attr : attributes) { attributesPossibleValues.add(attr.values()); }
		Set<List<AttributeValue>> cartesianValueSet = Sets.cartesianProduct(attributesPossibleValues);
		for (List<AttributeValue> cartesian : cartesianValueSet) {
			avfs.add(new AttributeValuesFrequency(GenstarUtils.buildAttributeValueMap(attributes, cartesian)));
		}
		
		return avfs;
	}
	
	
	public static List<AttributeValuesFrequency> readAttributeValuesFrequenciesFromControlTotalsFile(final GenstarCSVFile controlTotalsFile, 
			final List<AbstractAttribute> controlledAttributes) throws GenstarException {
		
		// parameters validation
		if (controlTotalsFile == null || controlledAttributes == null) {
			throw new GenstarException("Parameters controlTotalsFile, controlledAttributes can not be null");
		}
		
		if (controlledAttributes.size() < 2) { throw new GenstarException("controlledAttributes must have at least two attributes/elements"); }
		
		// ensure that controlledAttributes doesn't contain duplicated (controlled) attributes
		ISyntheticPopulationGenerator generator = controlledAttributes.get(0).getPopulationGenerator();
		Map<String, AbstractAttribute> controlledAttributesMap = new HashMap<String, AbstractAttribute>();
		for (AbstractAttribute controlledAttr : controlledAttributes) {
			if (controlledAttributesMap.containsKey(controlledAttr.getNameOnData())) { throw new GenstarException("Duplicated controlled attributes (" + controlledAttr.getNameOnData() + ")"); }
			if (!controlledAttr.getPopulationGenerator().equals(generator)) { throw new GenstarException("Controlled attributes belong to different population generator"); }
			
			controlledAttributesMap.put(controlledAttr.getNameOnData(), controlledAttr);
		}
		
		
		// build controlled attributes values subsets to verify the validity of control totals (file content)
		List<List<Map<AbstractAttribute, AttributeValue>>> controlledAttributesValuesSubsets = GenstarUtils.buildControlledAttributesValuesSubsets(new HashSet<AbstractAttribute>(controlledAttributes));
		int controlledAttributesValuesSubsetsSize = 0;
		for (List<Map<AbstractAttribute, AttributeValue>> subset : controlledAttributesValuesSubsets) { controlledAttributesValuesSubsetsSize += subset.size(); }
		
		// size verification
		List<List<String>> controlTotalsFileContent = controlTotalsFile.getContent();
		if (controlledAttributesValuesSubsetsSize != controlTotalsFileContent.size()) {
			throw new GenstarException("Mismatched between required/valid number of controlled totals and supplied number of controlled totals. Required values: " + controlledAttributesValuesSubsetsSize + ", supplied values (in control total file): " + controlTotalsFileContent.size());
		}
		
		
		// build generatedAvfsByGroups from controlledAttributesValuesSubsets
		Map<Integer, List<AttributeValuesFrequency>> generatedAvfsByGroups = new HashMap<Integer, List<AttributeValuesFrequency>>();
		Map<Integer, List<AttributeValuesFrequency>> alreadyValidAvfsByGroups = new HashMap<Integer, List<AttributeValuesFrequency>>();
		Map<Integer, Integer> parsedAvfsByGroupsTotalFrequencies = new HashMap<Integer, Integer>();
		int groupId = 0;
		for (List<Map<AbstractAttribute, AttributeValue>> subset : controlledAttributesValuesSubsets) {
			List<AttributeValuesFrequency> avfs = new ArrayList<AttributeValuesFrequency>();
			for (Map<AbstractAttribute, AttributeValue> attributeValues : subset) {
				avfs.add(new AttributeValuesFrequency(attributeValues));
			}
			
			generatedAvfsByGroups.put(groupId, avfs);
			alreadyValidAvfsByGroups.put(groupId, new ArrayList<AttributeValuesFrequency>());
			parsedAvfsByGroupsTotalFrequencies.put(groupId, 0);
			groupId++;
		}
		
		
		int controlTotalLineLength = ((controlledAttributes.size() - 1) * 2) + 1;
		

		// read the control totals file line by line to initialize AttributeValueFrequencies
		List<AttributeValuesFrequency> avFrequencies = new ArrayList<AttributeValuesFrequency>();
		
		AbstractAttribute attribute;
		AttributeValue attributeValue;
		List<String> valueList = new ArrayList<String>();
		int line = 1;
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (List<String> aRow : controlTotalsFileContent) {
			if (aRow.size() != controlTotalLineLength) throw new GenstarException("Invalid attribute values frequency format. File: " + controlTotalsFile.getPath() + ", line: " + line);
			line++;
			
			attributeValues.clear();
			for (int col=0; col<(aRow.size() - 1); col+=2) { // Parse each line of the file
				// 1. parse the attribute name column
				attribute = generator.getAttributeByNameOnData(aRow.get(col));
				if (attribute == null) { throw new GenstarException("'" + aRow.get(col) + "' is not a valid attribute. File: " + controlTotalsFile.getPath() + ", line: " + line + "."); }
				if (!controlledAttributes.contains(attribute)) { throw new GenstarException("'" + aRow.get(col) + "' is not a controlled attribute."); }
				if (attributeValues.containsKey(attribute)) { throw new GenstarException("Duplicated attribute : '" + attribute.getNameOnData() + "'"); }
				
				// 2. parse the attribute value column
				valueList.clear();
				String attributeValueString = aRow.get(col+1);
				if (attributeValueString.contains(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER)) { // range value
					StringTokenizer rangeValueToken = new StringTokenizer(attributeValueString, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
					if (rangeValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value: '" + attributeValueString + "'. File: " + controlTotalsFile.getPath()); }
					valueList.add(rangeValueToken.nextToken());
					valueList.add(rangeValueToken.nextToken());
				} else { // unique value
					valueList.add(attributeValueString);
				}
				
				
				attributeValue = attribute.findCorrespondingAttributeValue(valueList);
				if (attributeValue == null) { throw new GenstarException("Attribute value '" + aRow.get(col+1) + "' not found in valid attribute values of " + attribute.getNameOnData()
						+ ". File: " + controlTotalsFile.getPath() + ", line: " + line); }
				
				attributeValues.put(attribute, attributeValue);
			}
			
			// "frequency" is the last column
			int frequency = Integer.parseInt(aRow.get(aRow.size() - 1));
			if (frequency <= 0) { throw new GenstarException("frequency value must be positiv. File: " + controlTotalsFile.getPath() + ", line: " + line); }
			
			
			// TODO verify attributeValues
			AttributeValuesFrequency matched = null;
			int matchedGroupIdentity = -1;
			for (Integer groupIdentity : generatedAvfsByGroups.keySet()) {
				for (AttributeValuesFrequency avf : generatedAvfsByGroups.get(groupIdentity)) {
					if (avf.matchAttributeValues(attributeValues)) {
						matched = avf;
						matchedGroupIdentity = groupIdentity;
						break;
					}
				}
				
				if (matched != null) { break; }
			}
			
			
			// attribute values is not recognized
			if (matched == null) { throw new GenstarException("Line " + line + " is not a valid controlled total. File: " + controlTotalsFile.getPath()); }
			
			// duplication
			for (Integer groupIdentity : alreadyValidAvfsByGroups.keySet()) {
				for (AttributeValuesFrequency avf : alreadyValidAvfsByGroups.get(groupIdentity)) {
					if (avf.matchAttributeValues(attributeValues)) {
						throw new GenstarException("Duplicated control total. Line: " + line + ", file: " + controlTotalsFile.getPath());
					}
				}
			}
			
			alreadyValidAvfsByGroups.get(matchedGroupIdentity).add(matched);
			generatedAvfsByGroups.get(matchedGroupIdentity).remove(matched);
			
			avFrequencies.add(new AttributeValuesFrequency(attributeValues, frequency));
			int groupTotalFrequency = parsedAvfsByGroupsTotalFrequencies.get(matchedGroupIdentity) + frequency;
			parsedAvfsByGroupsTotalFrequencies.put(matchedGroupIdentity, groupTotalFrequency);
		}		
		

		// verify that totals of all groups are equal 
		int groupTotal = -1;
		boolean allTotalsAreEqual = true;
		for (Integer groupIdentity : parsedAvfsByGroupsTotalFrequencies.keySet()) {
			if (groupTotal == -1) {
				groupTotal = parsedAvfsByGroupsTotalFrequencies.get(groupIdentity);
			} else {
				if (groupTotal != parsedAvfsByGroupsTotalFrequencies.get(groupIdentity)) {
					allTotalsAreEqual = false;
					break;
				}
			}
		}
		
		if (!allTotalsAreEqual) { throw new GenstarException("Sum control totals of controlled attribute groups are not equal"); }

		
		return avFrequencies;
	}
	
}