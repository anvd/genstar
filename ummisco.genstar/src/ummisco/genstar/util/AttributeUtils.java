package ummisco.genstar.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttributeWithRangeInput;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;

public class AttributeUtils {

	/**
	 * Creates a range value attributes.
	 * 
	 * Values are encoded as follows: "0:4; 5:17; 18:24; 25:34; 35:49; 50:64; 65:100".
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
		StringTokenizer valueTokens = new StringTokenizer(values, INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.ATTRIBUTE_VALUE_DELIMITER);
		if (valueTokens.countTokens() == 0) { throw new GenstarException("No value is defined for the attribute '" + attributeNameOnData + "'"); }
		List<String> rangeTokens = new ArrayList<String>();
		while (valueTokens.hasMoreTokens()) { rangeTokens.add(valueTokens.nextToken()); }
		
		// 2. Created range values from the parsed tokens.
		for (String t : rangeTokens) {
			StringTokenizer minMaxValueTokens = new StringTokenizer(t, INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER);
			if (minMaxValueTokens.countTokens() != 2) { throw new GenstarException("Invalid attribute range value format (file: " + minMaxValueTokens.toString() + ")"); }
			
			rangeAttribute.add(new RangeValue(dataType, minMaxValueTokens.nextToken().trim(), minMaxValueTokens.nextToken().trim()));
		}
		
		generator.addAttribute(rangeAttribute);
	}

	/**
	 * Creates a unique value attribute.
	 * 
	 * Values are encoded as follows: "C0; C1; C2; C3; C4; C5; C6; C7"
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
		StringTokenizer valueTokenizers = new StringTokenizer(values, INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.ATTRIBUTE_VALUE_DELIMITER);
		if (valueTokenizers.countTokens() == 0) { throw new GenstarException("No value is defined for the attribute '" + attributeNameOnData + "'"); }
		List<String> uniqueValueTokens = new ArrayList<String>();
		while (valueTokenizers.hasMoreTokens()) { uniqueValueTokens.add(valueTokenizers.nextToken()); }
		
		// 2. Create unique values from the parsed tokens.
		for (String t : uniqueValueTokens) { uniqueValueAttribute.add(new UniqueValue(dataType, t.trim())); }
		
		generator.addAttribute(uniqueValueAttribute);
	}
	
	
	/**
	 * Creates a unique value attribute with range input.
	 * The range value is encoded as follows: min_value:max_value
	 * 
	 * @param generator
	 * @param attributeNameOnData
	 * @param attributeNameOnEntity
	 * @param dataType
	 * @param values
	 * @param valueClassOnEntity
	 * @throws GenstarException
	 */
	static void createUniqueValueAttributeWithRangeInput(final ISyntheticPopulationGenerator generator, final String attributeNameOnData, final String attributeNameOnEntity, 
			final DataType dataType, final String values, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException {
		
		// 1. Parse minValue and maxValue
		StringTokenizer valueTokenizers = new StringTokenizer(values, INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER);
		if (valueTokenizers.countTokens() == 0) { throw new GenstarException("No value is defined for the attribute '" + attributeNameOnData + "'"); }
		if (valueTokenizers.countTokens() != 2) { throw new GenstarException("Invalid unique value with range input format. Attribute name on data: " + attributeNameOnData + ". Valid format is min_value:max_value"); }
		
		int minValueInt = -1;
		int maxValueInt = -1;

		String minValueToken = valueTokenizers.nextToken();
		String maxValueToken = valueTokenizers.nextToken();
		
		if (minValueToken.equals(INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.UNIQUE_VALUES_ATTRIBUTE_WITH_RANGE_INPUT.MIN_VALUE_STRING)) {
			minValueInt = Integer.MIN_VALUE;
		} else if (minValueToken.equals(INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.UNIQUE_VALUES_ATTRIBUTE_WITH_RANGE_INPUT.MAX_VALUE_STRING)) {
			minValueInt = Integer.MAX_VALUE;
		}
		
		if (maxValueToken.equals(INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.UNIQUE_VALUES_ATTRIBUTE_WITH_RANGE_INPUT.MIN_VALUE_STRING)) {
			maxValueInt = Integer.MIN_VALUE;
		} else if (maxValueToken.equals(INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.UNIQUE_VALUES_ATTRIBUTE_WITH_RANGE_INPUT.MAX_VALUE_STRING)) {
			maxValueInt = Integer.MAX_VALUE;
		}
		
		if (minValueInt == -1) {
			try {
				minValueInt = Integer.parseInt(minValueToken);
			} catch (NumberFormatException nfe) {
				throw new GenstarException("Invalid attribute " + attributeNameOnData + " format. " + minValueToken + " is not a valid integer");
			}
		}
		
		if (maxValueInt == -1) {
			try {
				maxValueInt = Integer.parseInt(maxValueToken);
			} catch (NumberFormatException nfe) {
				throw new GenstarException("Invalid attribute " + attributeNameOnData + " format. " + maxValueToken + " is not a valid integer");
			}
		}

		
		// 2. Create unique values from the parsed tokens.
		UniqueValue minValue = new UniqueValue(DataType.INTEGER, Integer.toString(minValueInt));
		UniqueValue maxValue = new UniqueValue(DataType.INTEGER, Integer.toString(maxValueInt));

		
		// 3. create the attribute then add it to the generator
		UniqueValuesAttributeWithRangeInput uniqueValueAttributeWithRangeInput = new UniqueValuesAttributeWithRangeInput(generator, attributeNameOnData, attributeNameOnEntity, minValue, maxValue);
		generator.addAttribute(uniqueValueAttributeWithRangeInput);
	}
	
	
	public static void createAttributesFromCsvFile(final ISyntheticPopulationGenerator generator, final GenstarCsvFile attributesFile) throws GenstarException {
		
		List<List<String>> attributeFileContent = attributesFile.getContent();
		if ( attributeFileContent == null || attributeFileContent.isEmpty() ) { throw new GenstarException("Empty attribute file. File: " + attributesFile.getPath()); }
		
		if (attributesFile.getColumns() != INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.NB_OF_COLS) { 
			throw new GenstarException("Attributes file must have " + INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.NB_OF_COLS + " columns. File: " + attributesFile.getPath()); 
		}
		
		// 1. Parse the header
		List<String> fileHeader = attributesFile.getHeaders();
		if (fileHeader.size() != INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.NB_OF_COLS) {
			throw new GenstarException("Attributes file header must have " + INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.NB_OF_COLS + " columns. File: " + attributesFile.getPath());
		}
		for (int i=0; i<fileHeader.size(); i++) {
			if (!fileHeader.get(i).equals(INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.HEADERS[i])) {
				throw new GenstarException("Invalid attribute file header must be " + INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.HEADER_STR + ". File: " + attributesFile.getPath());
			}
		}
		
		// 2. Parse and initialize attributes
		int line = 1;
		for ( List<String> attributeInfo : attributeFileContent) {
			
			if (attributeInfo.size() != INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.NB_OF_COLS) { throw new GenstarException("Invalid attribute file format: each row must have " + INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.NB_OF_COLS + " columns (file: " + attributesFile.getPath() + "."); }
			
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
			if (valueTypeOnDataStr.equals(INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.RANGE_VALUE_NAME)) {
				try {
					createRangeValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity);
				} catch (Exception ex) {
					throw new GenstarException("Can not create range value attribute. File: " + attributesFilePath + ", line: " + line, ex);
				}
			} else if (valueTypeOnDataStr.equals(INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.UNIQUE_VALUE_NAME)) {
				try {
					createUniqueValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity);
				} catch (Exception ex) {
					throw new GenstarException("Can not create unique value attribute. File: " + attributesFilePath + ", line: " + line, ex);
				}
			} else if (valueTypeOnDataStr.equals(INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.UNIQUE_VALUE_WITH_RANGE_INPUT_NAME)) {
				try {
					createUniqueValueAttributeWithRangeInput(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity);
				} catch (Exception ex) {
					throw new GenstarException("Can not create unique value attribute with range input. File: " + attributesFilePath + ", line: " + line, ex);
				}
			} else {
				throw new GenstarException("Invalid attribute file: unsupported value type (file: " + attributesFilePath + ")");
			}
			
			line++;
		}
	}

}
