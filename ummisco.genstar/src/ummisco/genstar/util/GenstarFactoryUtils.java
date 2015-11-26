package ummisco.genstar.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import msi.gama.util.file.CsvWriter;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.GroupComponentSampleData;
import ummisco.genstar.ipf.ISampleData;
import ummisco.genstar.ipf.SampleData;
import ummisco.genstar.ipf.SampleDataGenerationRule;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.CustomGenerationRule;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.IMultipleRulesGenerator;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;

public class GenstarFactoryUtils {

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
	
	public static final class SAMPLE_DATA_PROPERTIES_FILE_FORMAT {
		
		public static final int SINGLE_SAMPLE_DATA_NUMBER_OF_PROPERTIES = 5;
		
		public static final int GROUP_COMPONENT_SAMPLE_DATA_NUMBER_OF_PROPERTIES = 9;
		
		
		public static final String ATTRIBUTES_PROPERTY = "ATTRIBUTES";
		
		public static final String SAMPLE_DATA_PROPERTY = "SAMPLE_DATA";
		
		public static final String CONTROLLED_ATTRIBUTES_PROPERTY = "CONTROLLED_ATTRIBUTES";
		
		public static final String CONTROLLED_TOTALS_PROPERTY = "CONTROLLED_TOTALS";
		
		public static final String SUPPLEMENTARY_ATTRIBUTES_PROPERTY = "SUPPLEMENTARY_ATTRIBUTES"; 
		
		public static final String COMPONENT_SAMPLE_DATA_PROPERTY = "COMPONENT_SAMPLE_DATA";
		
		public static final String COMPONENT_ATTRIBUTES_PROPERTY = "COMPONENT_ATTRIBUTES";
		
		public static final String GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY = "GROUP_ID_ATTRIBUTE_ON_GROUP";
		
		public static final String GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY = "GROUP_ID_ATTRIBUTE_ON_COMPONENT";
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
			throw new GenstarException("CSV file must have " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS + " columns. File: " + attributesFile.getPath()); 
		}
		
		// 1. Parse the header
		List<String> fileHeader = attributesFile.getHeaders();
		if (fileHeader.size() != CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS) {
			throw new GenstarException("Attribute file header must have " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS + " columns. File: " + attributesFile.getPath());
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
			
			AbstractAttribute attribute = generator.getAttribute(attributeName);
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
		
		AbstractAttribute inferringAttribute = generator.getAttribute(inferringAttributeName);
		if (inferringAttribute == null) { throw new GenstarException("Inferring attribute (" + inferringAttributeName + ") not found in the generator."); }
		
		AbstractAttribute inferredAttribute = generator.getAttribute(inferredAttributeName);
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
			final GenstarCSVFile supplementaryAttributesFile) throws GenstarException {
		
		SampleDataGenerationRule rule = new SampleDataGenerationRule(generator, ruleName, controlledAttributesFile, controlledTotalsFile, supplementaryAttributesFile);
		
		ISampleData sampleData = new SampleData(rule, sampleFile);
		rule.setSampleData(sampleData);
		
		generator.setGenerationRule(rule);
		generator.setNbOfEntities(rule.getIPF().getNbOfEntitiesToGenerate());
	}
	
	
	public static void createGroupComponentSampleDataGenerationRule(final ISingleRuleGenerator groupGenerator, final String ruleName, final GenstarCSVFile groupSampleFile,
			final GenstarCSVFile groupControlledAttributesFile, final GenstarCSVFile groupControlledTotalsFile, final GenstarCSVFile groupSupplementaryAttributesFile,
			final GenstarCSVFile componentSampleFile, final GenstarCSVFile componentAttributesFile, final String groupIdAttributeNameOnGroup,
			final String groupIdAttributeNameOnComponent) throws GenstarException {
		
		SampleDataGenerationRule rule = new SampleDataGenerationRule(groupGenerator, ruleName, groupControlledAttributesFile, groupControlledTotalsFile, groupSupplementaryAttributesFile);
		
		ISingleRuleGenerator componentGenerator = new SingleRuleGenerator("Component Generator");
		createAttributesFromCSVFile(componentGenerator, componentAttributesFile);
		
		AbstractAttribute groupIdAttributeOnGroup = rule.getAttribute(groupIdAttributeNameOnGroup);
		if (groupIdAttributeOnGroup == null) { throw new GenstarException("'" + groupIdAttributeNameOnGroup + "' is not a valid attribute"); }
		groupIdAttributeOnGroup.setIdentity(true);
		
		AbstractAttribute groupIdAttributeOnComponent = componentGenerator.getAttribute(groupIdAttributeNameOnComponent);
		if (groupIdAttributeOnComponent == null) { throw new GenstarException("'" + groupIdAttributeOnComponent + "' is not a valid attribute"); }
		groupIdAttributeOnComponent.setIdentity(true);
		
		ISampleData groupSampleData = new SampleData(rule, groupSampleFile);
		ISampleData componentSampleData = new SampleData(componentGenerator, componentSampleFile);
		
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
			
			supplementaryAttr = generator.getAttribute(aRow.get(0));
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
			
			AbstractAttribute attribute = generator.getAttribute(attributeName);
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
			String attributeNameOnSample = sampleDataHeader.get(col);
			AbstractAttribute attribute = generationRule.getAttribute(attributeNameOnSample);
			if (attribute != null) {
				attributeIndexes.put(col, attribute);
			}
		}
		
		// 3.2. calculate the number of attribute values
		List<List<String>> contents = sampleDataFile.getContent();
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		Set<AttributeValuesFrequency> attributeValuesFrequencies = generationRule.getAttributeValuesFrequencies();
		for (int row=0; row<contents.size(); row++) {
			attributeValues.clear();
			
			for (int col : attributeIndexes.keySet()) {
				String attributeValueString = contents.get(row).get(col);
				
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
				if (avf.isMatch(attributeValues)) { 
					avf.setFrequency(avf.getFrequency() + 1);
					break;
				}
			}
		}
		 
		return generationRule;
	}
	
	
	public static void generateSimpleSampleData(final GenstarCSVFile attributesFile, final int entities, final String outputCSVFilePath) throws GenstarException {
		if (attributesFile == null) { throw new GenstarException("Parameter attributesFile can not be null"); }
		if (entities <= 0) { throw new GenstarException("Parameter entities must be positive"); }
		
		ISingleRuleGenerator generator = new SingleRuleGenerator("dummy single rule generator");
		createAttributesFromCSVFile(generator, attributesFile);
		
		List<AbstractAttribute> attributes = new ArrayList<AbstractAttribute>(generator.getAttributes());
		List<String[]> fileContent = new ArrayList<String[]>();
		int columns = attributes.size();
		
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
		
		// 1. write attributes' name as the first line of fileContent
		String header[] = new String[columns];
		for (int i=0; i<attributes.size(); i++) { header[i] = attributes.get(i).getNameOnData(); }
		fileContent.add(header);
		
		// 2. write generated entities to fileContent
		String aRow[];
		int size;
		List<AttributeValue> values;
		AttributeValue valueOnData;
		for (int i=0; i<entities; i++) {
			aRow = new String[columns];
			for (int col=0; col<attributes.size(); col++) {
				values = attributeValues.get(attributes.get(col));
				size = attributeValueSizes.get(attributes.get(col));
				valueOnData = values.get(SharedInstances.RandomNumberGenerator.nextInt(size));
				
				if (valueOnDataSameAsValueOnEntity.get(attributes.get(col))) {
					aRow[col] = valueOnData.toCSVString();
				} else { // valueOnData != valueOnEntity
					aRow[col] = valueOnData.cast(valueOnEntityClasses.get(attributes.get(col))).toCSVString();
				}
			}
			
			fileContent.add(aRow);
		}
		
		// 3. write fileContent to the outputCSVFilePath CSV file
		try {
			CsvWriter writer = new CsvWriter(outputCSVFilePath);
			for ( String[] ss : fileContent ) { writer.writeRecord(ss); } 
			writer.close();
		} catch (IOException e) {
			throw new GenstarException("Failed to write sample data to CSV file.", e);
		}
	}
	
	
	public static void generateGroupComponentSampleData(final GenstarCSVFile groupAttributesFile, final GenstarCSVFile componentAttributesFile,
			final String groupIdAttributeNameOnGroupEntity, final String groupIdAttributeNameOnComponentEntity, final String groupSizeAttributeName, 
			final int nbOfGroupEntities, final String groupOutputCSVFilePath, final String componentOutputCSVFilePath) throws GenstarException {
		
		// 0. parameters validation
		if (groupAttributesFile == null) { throw new GenstarException("Parameter groupAttributesFile can not be null"); }
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
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttribute(groupIdAttributeNameOnGroupEntity);
		if (groupIdAttributeOnGroupEntity == null) { throw new GenstarException(groupIdAttributeNameOnGroupEntity + " is considered as group identity attribute but not found among the available group attributes"); }
		groupIdAttributeOnGroupEntity.setIdentity(true);
		// Important Note: groupIdOnGroupAttribute is an integer, beginning with 0, with 1 as increment
		// ID should not be defined in the attributesFile?
		
		// 3.  retrieve reference to groupSizeAttribute
		AbstractAttribute groupSizeAttribute = groupGenerator.getAttribute(groupSizeAttributeName);
		if (groupSizeAttribute == null) { throw new GenstarException(groupSizeAttributeName + " is considered as group size attribute but not found among the available group attributes"); }
		if (!groupSizeAttribute.getValueClassOnEntity().equals(UniqueValue.class)) { throw new GenstarException(groupSizeAttributeName + " attribute must have unique value"); }
		if (!groupSizeAttribute.getDataType().equals(DataType.INTEGER)) { throw new GenstarException(groupSizeAttributeName + " attribute must have " + DataType.INTEGER.getName() + " as data type"); }
		
		// 4. cache indexes of groupIdAttributeOnGroupEntity and groupSizeAttribute
		int groupIdAttributeIndexOnGroupEntity = -1;
		int groupSizeAttributeIndex = -1;
		for (int i=0; i<groupAttributes.size(); i++) {
			if (groupAttributes.get(i).equals(groupSizeAttribute)) { groupSizeAttributeIndex = i; }
			if (groupAttributes.get(i).equals(groupIdAttributeOnGroupEntity)) { groupIdAttributeIndexOnGroupEntity = i; }
		}

		// 5. generate group entities
		List<String[]> generatedGroupEntities = generateGroupEntities(groupAttributes, groupIdAttributeOnGroupEntity, nbOfGroupEntities);
		
		
		// 6. create component attributes from componentAttributesFile
		ISingleRuleGenerator componentGenerator = new SingleRuleGenerator("component dummy generator");
		createAttributesFromCSVFile(componentGenerator, componentAttributesFile);
		List<AbstractAttribute> componentAttributes = new ArrayList<AbstractAttribute>(componentGenerator.getAttributes());
		
		AbstractAttribute groupIdAttributeOnComponentEntity = componentGenerator.getAttribute(groupIdAttributeNameOnComponentEntity);
		if (groupIdAttributeOnComponentEntity == null) { throw new GenstarException(groupIdAttributeNameOnComponentEntity + " is considered as group identity attribute on component but not found among the available component attributes"); }
		groupIdAttributeOnComponentEntity.setIdentity(true);
		
		// 7. generate component entities
		List<String[]> generatedGroupEntitiesWithoutHeader = new ArrayList<String[]>(generatedGroupEntities);
		generatedGroupEntitiesWithoutHeader.remove(0);
		List<String[]> generatedComponentEntities = generateComponentEntities(componentAttributes, groupIdAttributeOnComponentEntity, 
				generatedGroupEntitiesWithoutHeader, groupIdAttributeIndexOnGroupEntity, groupSizeAttributeIndex);
		
		// 8. write groupFileContent and componentFileContent to 2 CSV files
		try {
			// group entities
			CsvWriter groupWriter = new CsvWriter(groupOutputCSVFilePath);
			for ( String[] ss : generatedGroupEntities ) { groupWriter.writeRecord(ss); } 
			groupWriter.close();
			
			// component entities
			CsvWriter componentWriter = new CsvWriter(componentOutputCSVFilePath);
			for ( String[] ss : generatedComponentEntities ) { componentWriter.writeRecord(ss); } 
			componentWriter.close();
		} catch (IOException e) {
			throw new GenstarException("Failed to write sample data to CSV file.", e);
		}
	}
	

	private static List<String[]> generateGroupEntities(final List<AbstractAttribute> groupAttributes, final AbstractAttribute groupIdAttributeOnGroupEntity, final int nbOfGroupEntities) throws GenstarException {
		int groupColumns = groupAttributes.size();
		List<String[]> groupFileContent = new ArrayList<String[]>();
		int groupIdValue = 0;
		
		
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

		// 2. write group attribute names as the first line of group file content
		String[] groupHeader = new String[groupColumns];
		for (int i=0; i<groupAttributes.size(); i++) { groupHeader[i] = groupAttributes.get(i).getNameOnData(); }
		groupFileContent.add(groupHeader);
		
		// 3. write generated entities to groupFileContent
		String aRowOfGroup[];
		int valuesSize;
		List<AttributeValue> values;
		AttributeValue valueOnData;
		for (int i=0; i<nbOfGroupEntities; i++) {
			aRowOfGroup = new String[groupColumns];
			
			for (int col=0; col<groupColumns; col++) {
				if (col == groupIdAttributeIndexOnGroupEntity) { // ID attribute: value is computed automatically
					aRowOfGroup[col] = Integer.toString(groupIdValue);
					groupIdValue++;
				} else {
					values = groupAttributeValues.get(groupAttributes.get(col));
					valuesSize = groupAttributeValueSizes.get(groupAttributes.get(col));
					valueOnData = values.get(SharedInstances.RandomNumberGenerator.nextInt(valuesSize));
					
					if (valueOnDataSameAsValueOnEntity.get(groupAttributes.get(col))) {
						aRowOfGroup[col] = valueOnData.toCSVString();
					} else { // valueOnData != valueOnEntity
						aRowOfGroup[col] = valueOnData.cast(valueOnEntityClasses.get(groupAttributes.get(col))).toCSVString();
					}
				}
			}
			
			groupFileContent.add(aRowOfGroup);
		}
		
		return groupFileContent;
	}


	private static List<String[]> generateComponentEntities(final List<AbstractAttribute> componentAttributes, 
			final AbstractAttribute groupIdAttributeOnComponentEntity, final List<String[]> groupEntities, 
			final int groupIdAttributeIndexOnGroupEntity, final int groupSizeAttributeIndex) throws GenstarException {
		
		List<String[]> componentFileContent = new ArrayList<String[]>();		
		
		// 0. cache index of groupIdAttributeNameOnComponentEntity
		int groupIdAttributeIndexOnComponentEntity = -1;
		for (int i=0; i<componentAttributes.size(); i++) {
			if (componentAttributes.get(i).equals(groupIdAttributeOnComponentEntity)) { 
				groupIdAttributeIndexOnComponentEntity = i;
				break;
			}
		}
		
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
		 
		// 2. write component attribute names as the first line of component file content
		String[] componentHeader = new String[componentAttributes.size()];
		for (int i=0; i<componentAttributes.size(); i++) {  componentHeader[i] = componentAttributes.get(i).getNameOnData(); }
		componentFileContent.add(componentHeader);
		
		// 3. write generated entities to componentFileContent
		String groupID;
		int groupSize;
		int valuesSize;
		List<AttributeValue> values;
		AttributeValue valueOnData;
		for (String[] groupEntity : groupEntities) {
			groupID = groupEntity[groupIdAttributeIndexOnGroupEntity];
			groupSize = Integer.parseInt(groupEntity[groupSizeAttributeIndex]);
			
			// generate component entities for each group entity based on groupSize attribute value
			for (int componentNb=0; componentNb<groupSize; componentNb++) {
				
				String[] componentEntity = new String[componentAttributes.size()];
				for (int col=0; col<componentAttributes.size(); col++) {
					if (col == groupIdAttributeIndexOnComponentEntity) { // component's groupID
						componentEntity[col] = groupID;
					} else { // other columns/fields
						values = componentAttributeValues.get(componentAttributes.get(col));
						valuesSize = componentAttributeValueSizes.get(componentAttributes.get(col));
						valueOnData = values.get(SharedInstances.RandomNumberGenerator.nextInt(valuesSize));
					
						if (valueOnDataSameAsValueOnEntity.get(componentAttributes.get(col))) {
							componentEntity[col] = valueOnData.toCSVString();
						} else { // valueOnData != valueOnEntity
							componentEntity[col] = valueOnData.cast(valueOnEntityClasses.get(componentAttributes.get(col))).toCSVString();
						}						 
					}
				}
				
				componentFileContent.add(componentEntity);
			}
		}
		
		return componentFileContent;
	}
}