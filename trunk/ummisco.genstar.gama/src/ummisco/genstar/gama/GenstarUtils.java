package ummisco.genstar.gama;

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

import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.Types;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.AttributeValuesFrequency;
import ummisco.genstar.metamodel.CustomGenerationRule;
import ummisco.genstar.metamodel.DataType;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.RangeValue;
import ummisco.genstar.metamodel.RangeValuesAttribute;
import ummisco.genstar.metamodel.UniqueValue;
import ummisco.genstar.metamodel.UniqueValuesAttribute;

public class GenstarUtils {

	static class AttributeValuesFrequencyComparator implements Comparator<AttributeValuesFrequency> {
		
		List<AbstractAttribute> sortingAttributes = null;
		
		AttributeValuesFrequencyComparator(final List<AbstractAttribute> sortingAttributes) throws GenstarException {
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
			static final String MIN_MAX_VALUE_DELIMITER = ":";
			static final String FIELD_DELIMITER = ",";
				
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
			//		“Name”, “File”, “Type”
			static final String HEADER_STR = "Name,File,Rule Type";
			static final int NB_OF_COLS = 3;
			static String[] HEADERS = new String[NB_OF_COLS];
			static {
				HEADERS[0] = "Name";
				HEADERS[1] = "File";
				HEADERS[2] = "Rule Type";
			}
			
			public static final String JAVA_CLASS_PARAMETER_DELIMITER = "?";
		}
		
		
		public static final class FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA {
			
			static final String ATTRIBUTE_NAME_TYPE_DELIMITER = ":";
			
			static final String INPUT_ATTRIBUTE = "Input";
			static final String OUTPUT_ATTRIBUTE = "Output";
			static final String FREQUENCY = "Frequency";
		}
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
			final DataType dataType, final String values, final Class<? extends AttributeValue> valueClassOnEntity, final String attributesFilePath) throws GenstarException {
		
		RangeValuesAttribute rangeAttribute = new RangeValuesAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, UniqueValue.class);
		
		// 1. Parse and accumulate each range value token into a list.
		StringTokenizer valueTokens = new StringTokenizer(values, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.ATTRIBUTE_VALUE_DELIMITER);
		if (valueTokens.countTokens() == 0) { throw new GenstarException("Invalid attribute file : no value is defined for the attribute (file: " + attributesFilePath + ")"); }
		List<String> rangeTokens = new ArrayList<String>();
		while (valueTokens.hasMoreTokens()) { rangeTokens.add(valueTokens.nextToken()); }
		
		// 2. Created range values from the parsed tokens.
		for (String t : rangeTokens) {
			StringTokenizer minMaxValueTokens = new StringTokenizer(t, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
			if (minMaxValueTokens.countTokens() != 2) { throw new GenstarException("Invalid attribute file: invalid range value format (file: " + attributesFilePath + ")"); }
			
			rangeAttribute.add(new RangeValue(dataType, minMaxValueTokens.nextToken().trim(), minMaxValueTokens.nextToken().trim()));
		}
		
		generator.addAttribute(rangeAttribute);
	}
	
	public static void createRangeValueAttributePublicProxy(final ISyntheticPopulationGenerator generator, final String attributeNameOnData, final String attributeNameOnEntity, 
			final DataType dataType, final String values, final Class<? extends AttributeValue> valueClassOnEntity, final String attributesFilePath) throws GenstarException {
		createRangeValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity, attributesFilePath);
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
			final DataType dataType, final String values, final Class<? extends AttributeValue> valueClassOnEntity, final String attributesFilePath) throws GenstarException {
		
		UniqueValuesAttribute uniqueValueAttribute = new UniqueValuesAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, valueClassOnEntity);
		
		// 1. Parse and accumulate each unique value token into a list.
		StringTokenizer valueTokenizers = new StringTokenizer(values, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.ATTRIBUTE_VALUE_DELIMITER);
		if (valueTokenizers.countTokens() == 0) { throw new GenstarException("Invalid attribute file : no value is defined for the attribute (file: " + attributesFilePath + ")"); }
		List<String> uniqueValueTokens = new ArrayList<String>();
		while (valueTokenizers.hasMoreTokens()) { uniqueValueTokens.add(valueTokenizers.nextToken()); }
		
		// 2. Create unique values from the parsed tokens.
		for (String t : uniqueValueTokens) { uniqueValueAttribute.add(new UniqueValue(dataType, t.trim())); }
		
		generator.addAttribute(uniqueValueAttribute);
	}

	public static void createUniqueValueAttributePublicProxy(final ISyntheticPopulationGenerator generator, final String attributeNameOnData, final String attributeNameOnEntity, 
			final DataType dataType, final String values, final Class<? extends AttributeValue> valueClassOnEntity, final String attributesFilePath) throws GenstarException {
		createUniqueValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity, attributesFilePath);
	}

	static void createAttributesFromCSVFile(final IScope scope, final ISyntheticPopulationGenerator generator, final GamaCSVFile attributesCSVFile) throws GenstarException {
		
		IMatrix fileContent = attributesCSVFile.getContents(scope);
		if ( fileContent == null || fileContent.isEmpty(scope) ) { throw new GenstarException("Empty attribute file. File: " + attributesCSVFile.getPath()); }
		int rows = fileContent.getRows(scope);
		
		if (fileContent.getCols(scope) != CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS) { 
			throw new GenstarException("CSV file must have " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS + " columns. File: " + attributesCSVFile.getPath()); 
		}

		// 1. Parse the header
		List<String> fileHeader = attributesCSVFile.getAttributes(scope);
		if (fileHeader.size() != CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS) {
			throw new GenstarException("Attribute file header must have " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS + " columns. File: " + attributesCSVFile.getPath());
		}
		for (int i=0; i<fileHeader.size(); i++) {
			if (!fileHeader.get(i).equals(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.HEADERS[i])) {
				throw new GenstarException("Invalid attribute file header must be " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.HEADER_STR + ". File: " + attributesCSVFile.getPath());
			}
		}
		
		
		// 2. Parse and initialize attributes
		for ( int rowIndex = 0; rowIndex < rows; rowIndex++ ) {
			
			final IList attributeInfo = fileContent.getRow(scope, rowIndex);
			if (attributeInfo.size() != CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS) { throw new GenstarException("Invalid attribute file format: each row must have " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS + " columns (file: " + attributesCSVFile.getPath() + "."); }
			
			String attributeNameOnData = (String)attributeInfo.get(0);
			String attributeNameOnEntity = (String)attributeInfo.get(1);
			
			String dataTypeStr = (String)attributeInfo.get(2);
			DataType dataType = DataType.fromName(dataTypeStr);
			if (dataType == null) { throw new GenstarException(dataTypeStr + " is not a supported data type."); }
			
			String valueTypeOnDataStr = (String)attributeInfo.get(3);
			String values = (String)attributeInfo.get(4);
			
			String valueTypeOnEntityStr = (String)attributeInfo.get(5);
			Class<? extends AttributeValue> valueClassOnEntity = AttributeValue.getClassByName(valueTypeOnEntityStr);
			
			
			String attributesFilePath = attributesCSVFile.getPath();
			if (valueTypeOnDataStr.equals(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.RANGE_VALUE_NAME)) {
				createRangeValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity, attributesFilePath);
			} else if (valueTypeOnDataStr.equals(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.UNIQUE_VALUE_NAME)) {
				createUniqueValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity, attributesFilePath);
			} else {
				throw new GenstarException("Invalid attribute file: unsupported value type (file: " + attributesFilePath + ")");
			}
		}
	}
	
	static void createFrequencyDistributionGenerationRule(final IScope scope, final ISyntheticPopulationGenerator generator, final String ruleName, GamaCSVFile ruleDataFile) throws GenstarException {
		
		IMatrix fileContent = ruleDataFile.getContents(scope);
		if ( fileContent == null || fileContent.isEmpty(scope) ) { throw new GenstarException("Frequency Distribution Generation Rule file is empty (file: " + ruleDataFile.getPath() + ")"); }
		int rows = fileContent.getRows(scope);
		
		
		// 1. Parse the header
		List<String> header = ruleDataFile.getAttributes(scope);
		if (header.size() < 2) { throw new GenstarException("Invalid Frequency Distribution Generation Rule file header : header must have at least 2 elements (file: " + ruleDataFile.getPath() + ")"); }
		
		
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
				
				throw new GenstarException("Invalid header format (" + invalidTokens.toString() + ") found in Frequency Distribution Generation Rule (file: " + ruleDataFile.getPath() + ")"); 
			}
			
			String attributeName = attributeToken.nextToken();
			String attributeType = attributeToken.nextToken();
			
			AbstractAttribute attribute = generator.getAttribute(attributeName);
			if (attribute == null) { throw new GenstarException("Unknown attribute (" + attributeName + ") found in Frequency Distribution Generation Rule (file: " + ruleDataFile.getPath() + ")"); }
			concerningAttributes.add(attribute);
			
			if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.INPUT_ATTRIBUTE)) {
				generationRule.appendInputAttribute(attribute);
			} else if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.OUTPUT_ATTRIBUTE)) {
				generationRule.appendOutputAttribute(attribute);
			} else {
				throw new GenstarException("Invalid attribute type (" + attributeType + ") found in Frequency Distribution Generation Rule (file: " + ruleDataFile.getPath() + ")");
			}
		}
		
		generationRule.generateAttributeValuesFrequencies();
		
		// 3. Set frequencies
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
			attributeValues.clear();
			IList frequencyInfo = fileContent.getRow(scope, rowIndex);
			
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
	
	static void createAttributeInferenceGenerationRule(final IScope scope, final ISyntheticPopulationGenerator generator, final String ruleName, GamaCSVFile ruleDataFile) throws GenstarException {
		
		IMatrix fileContent = ruleDataFile.getContents(scope);
		if ( fileContent == null || fileContent.isEmpty(scope) ) { throw new GenstarException("Attribute Inference Generation Rule file is empty (file: " + ruleDataFile.getPath() + ")"); }
		int rows = fileContent.getRows(scope);
		
		
		// 1. Parse the header
		List<String> header = ruleDataFile.getAttributes(scope);
		if (header.size() != 2) { throw new GenstarException("Invalid Attribute Inference Generation Rule file header : header must have 2 elements (file: " + ruleDataFile.getPath() + ")"); }
		String inferringAttributeName = header.get(0);
		String inferredAttributeName = header.get(1);
		
		AbstractAttribute inferringAttribute = generator.getAttribute(inferringAttributeName);
		if (inferringAttribute == null) { throw new GenstarException("Inferring attribute (" + inferringAttributeName + ") not found in the generator."); }
		
		AbstractAttribute inferredAttribute = generator.getAttribute(inferredAttributeName);
		if (inferredAttribute == null) { throw new GenstarException("Inferred attribute (" + inferredAttributeName + ") not found in the generator."); }
		
		if (rows != inferringAttribute.values().size()) { throw new GenstarException("Generation Rule must contain exacly the same number of attribute values defined in the inferring attribute and inferred attribute (file: " + ruleDataFile.getPath() + ")"); }
		
		
		// 2. Create the rule & set inference data
		AttributeInferenceGenerationRule generationRule = new AttributeInferenceGenerationRule(generator, ruleName, inferringAttribute, inferredAttribute);
		Map<AttributeValue, AttributeValue> inferenceData = new HashMap<AttributeValue, AttributeValue>();
		AttributeValue inferringValue, inferredValue;
		for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
			IList inferenceInfo = fileContent.getRow(scope, rowIndex);
			
			if (inferringAttribute instanceof RangeValuesAttribute) {
				StringTokenizer minMaxValueToken = new StringTokenizer((String)inferenceInfo.get(0), CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
				if (minMaxValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value format: " + (String)inferenceInfo.get(0) + " (file: " + ruleDataFile.getPath() + ")"); }
				
				String minValue = minMaxValueToken.nextToken().trim();
				String maxValue = minMaxValueToken.nextToken().trim();
				inferringValue = inferringAttribute.getInstanceOfAttributeValue(new RangeValue(inferringAttribute.getDataType(), minValue, maxValue));
			} else {
				inferringValue = inferringAttribute.getInstanceOfAttributeValue(new UniqueValue(inferringAttribute.getDataType(), (String)inferenceInfo.get(0)));
			}
			
			if (inferredAttribute instanceof RangeValuesAttribute) {
				StringTokenizer minMaxValueToken = new StringTokenizer((String)inferenceInfo.get(1), CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
				if (minMaxValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value format: " + (String)inferenceInfo.get(1) + " (file: " + ruleDataFile.getPath() + ")"); }
				
				String minValue = minMaxValueToken.nextToken().trim();
				String maxValue = minMaxValueToken.nextToken().trim();
				inferredValue = inferredAttribute.getInstanceOfAttributeValue(new RangeValue(inferredAttribute.getDataType(), minValue, maxValue));
			} else {
				inferredValue = inferredAttribute.getInstanceOfAttributeValue(new UniqueValue(inferredAttribute.getDataType(), (String)inferenceInfo.get(1)));
			}
			
			if (inferringValue == null || inferredValue == null) { throw new GenstarException("Invalid Attribute Inference Generation Rule file content: Some attribute values are not contained in the inferring attribute or inferred attribute (file: " + ruleDataFile.getPath() + ")"); }
			if (inferenceData.containsKey(inferringValue)) { throw new GenstarException("Invalid Attribute Inference Generation Rule file content: inferringValue in " + inferringValue + ":" + inferredValue + " has already been found in a previous correspondence (file: " + ruleDataFile.getPath() + ")"); }
			if (inferenceData.containsValue(inferredValue)) { throw new GenstarException("Invalid Attribute Inference Generation Rule file content: inferredValue in " + inferringValue + ":" + inferredValue + " has already been found in a previous correspondence (file: " + ruleDataFile.getPath() + ")"); }
			
			inferenceData.put(inferringValue, inferredValue);
		}
		generationRule.setInferenceData(inferenceData);
		
		// add generation rule to the generator
		generator.appendGenerationRule(generationRule);		
	}
	
	static void createCustomGenerationRule(final IScope scope, final ISyntheticPopulationGenerator generator, final String ruleName, final String ruleJavaClass) throws GenstarException {
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
	
	static void createGenerationRulesFromCSVFile(final IScope scope, final ISyntheticPopulationGenerator generator, final GamaCSVFile distributionsCSVFile) throws GenstarException {
		IMatrix fileContent = distributionsCSVFile.getContents(scope);
		if ( fileContent == null || fileContent.isEmpty(scope) ) { throw new GenstarException("Invalid Generation Rule file: content is empty (file: " + distributionsCSVFile.getPath()  + ")"); }
		int rows = fileContent.getRows(scope);

		if (fileContent.getCols(scope) != CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS) { throw new GenstarException("CVS file must have " + CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS + " columns, (file: " + distributionsCSVFile.getPath() + ")"); }

		// 1. Parse the header
		List<String> header = distributionsCSVFile.getAttributes(scope);
		for (int i=0; i<header.size(); i++) {
			if (!header.get(i).equals(CSV_FILE_FORMATS.GENERATION_RULE_METADATA.HEADERS[i])) {
				throw new GenstarException("Invalid Generation Rule file header. Header must be " + CSV_FILE_FORMATS.GENERATION_RULE_METADATA.HEADER_STR + " (file: " + distributionsCSVFile.getPath() + ")");
			}
		}
		
		
		// 2. Parse and initialize distributions
		for ( int rowIndex = 0; rowIndex < rows; rowIndex++ ) {
			
			final IList generationRuleInfo = fileContent.getRow(scope, rowIndex);
			if (generationRuleInfo.size() != CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS) { throw new GenstarException("Invalid Generation Rule file format: each row must have " +  CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS + " columns, (file: " + distributionsCSVFile.getPath() + ")"); }
			
			String ruleName = (String)generationRuleInfo.get(0);
			String ruleDataFilePathOrJavaClass = (String)generationRuleInfo.get(1);
			String ruleTypeName = (String)generationRuleInfo.get(2);
			GamaCSVFile ruleDataFile = null;
			if (!ruleTypeName.equals(CustomGenerationRule.RULE_TYPE_NAME)) {
				ruleDataFile = new GamaCSVFile(scope, ruleDataFilePathOrJavaClass, ",", Types.STRING, true);
			}
			
			
			if (ruleTypeName.equals(FrequencyDistributionGenerationRule.RULE_TYPE_NAME)) {
				createFrequencyDistributionGenerationRule(scope, generator, ruleName, ruleDataFile);
			} else if (ruleTypeName.equals(AttributeInferenceGenerationRule.RULE_TYPE_NAME)) {
				createAttributeInferenceGenerationRule(scope, generator, ruleName, ruleDataFile);
			} else if (ruleTypeName.equals(CustomGenerationRule.RULE_TYPE_NAME)) { 
				createCustomGenerationRule(scope, generator, ruleName, ruleDataFilePathOrJavaClass);
			} else {
				throw new GenstarException("Invalid Generation Rule file format: unsupported generation rule (" + ruleTypeName + "), file: " + distributionsCSVFile.getPath());
			}
		}
	}

	static FrequencyDistributionGenerationRule createFrequencyDistributionFromSampleData(final IScope scope, final ISyntheticPopulationGenerator generator, 
			final GamaCSVFile distributionFormatCSVFile, final GamaCSVFile sampleDataCSVFile) throws GenstarException {
		
		
		// 1. Parse the header
		List<String> distributionFormatHeader = distributionFormatCSVFile.getAttributes(scope);
		if (distributionFormatHeader.size() < 1) { throw new GenstarException("Header must have at least 1 elements (file: " + distributionFormatCSVFile.getPath() + ")"); }
		
		
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
				
				throw new GenstarException("Invalid header format (" + invalidTokens.toString() + ") found in " + distributionFormatCSVFile.getPath() + "."); 
			}
			
			String attributeName = attributeToken.nextToken();
			String attributeType = attributeToken.nextToken();
			
			AbstractAttribute attribute = generator.getAttribute(attributeName);
			if (attribute == null) { throw new GenstarException("Unknown attribute (" + attributeName + ") found in " + distributionFormatCSVFile.getPath() + "."); }
			concerningAttributes.add(attribute);
			
			if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.INPUT_ATTRIBUTE)) {
				generationRule.appendInputAttribute(attribute);
			} else if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.OUTPUT_ATTRIBUTE)) {
				generationRule.appendOutputAttribute(attribute);
			} else {
				throw new GenstarException("Invalid attribute type (" + attributeType + ") found in Frequency Distribution Generation Rule (file: " + distributionFormatCSVFile.getPath() + ")");
			}
		}
		
		generationRule.generateAttributeValuesFrequencies();
		
		// 3. Set frequencies
		// 3.1. save the index of attributes of the sample data
		List<String> sampleDataHeader = sampleDataCSVFile.getAttributes(scope);
		SortedMap<Integer, AbstractAttribute> attributeIndexes = new TreeMap<Integer, AbstractAttribute>();
		for (int col=0; col<sampleDataHeader.size(); col++) {
			String attributeNameOnSample = sampleDataHeader.get(col);
			AbstractAttribute attribute = generationRule.findAttributeByNameOnData(attributeNameOnSample);
			if (attribute != null) {
				attributeIndexes.put(col, attribute);
			}
		}
		
		// 3.2. calculate the number of attribute values
		IMatrix contents = sampleDataCSVFile.getContents(scope);
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		Set<AttributeValuesFrequency> attributeValuesFrequencies = generationRule.getAttributeValuesFrequencies();
		for (int row=0; row<contents.getRows(scope); row++) {
			attributeValues.clear();
			
			for (int col : attributeIndexes.keySet()) {
				String attributeValueString = (String) contents.get(scope, col, row);
				List<String> valueList = new ArrayList<String>();
				
				if (attributeValueString.contains(GenstarUtils.CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER)) { // range value
					StringTokenizer rangeValueToken = new StringTokenizer(attributeValueString, GenstarUtils.CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
					if (rangeValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value: '" + attributeValueString + "'. File: " + sampleDataCSVFile.getPath()); }
					valueList.add(rangeValueToken.nextToken());
					valueList.add(rangeValueToken.nextToken());
				} else { // unique value
					valueList.add(attributeValueString);
				}
				
				AbstractAttribute concerningAttribute = attributeIndexes.get(col);
				AttributeValue value = concerningAttribute.findCorrespondingAttributeValue(valueList);
				if (value == null) { throw new GenstarException("'" + attributeValueString + "' is not a valid value of '" + concerningAttribute.getNameOnData() + "' attribute. File: " + sampleDataCSVFile.getPath()); }
				
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
}
