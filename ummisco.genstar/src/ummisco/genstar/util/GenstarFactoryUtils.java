package ummisco.genstar.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.DataType;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.RangeValue;
import ummisco.genstar.metamodel.RangeValuesAttribute;
import ummisco.genstar.metamodel.UniqueValue;
import ummisco.genstar.metamodel.UniqueValuesAttribute;

public class GenstarFactoryUtils {

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
			//		Name, File, Type
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
	
	
	public static void createAttributesFromCSVFile(final ISyntheticPopulationGenerator generator, final GenstarCSVFile attributesCSVFile) throws GenstarException {
		
		List<List<String>> fileContent = attributesCSVFile.getContent();
		if ( fileContent == null || fileContent.isEmpty() ) { throw new GenstarException("Empty attribute file. File: " + attributesCSVFile.getFilePath()); }
		
		if (attributesCSVFile.getColumns() != CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS) { 
			throw new GenstarException("CSV file must have " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS + " columns. File: " + attributesCSVFile.getFilePath()); 
		}
		
		// 1. Parse the header
		List<String> fileHeader = attributesCSVFile.getHeaders();
		if (fileHeader.size() != CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS) {
			throw new GenstarException("Attribute file header must have " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS + " columns. File: " + attributesCSVFile.getFilePath());
		}
		for (int i=0; i<fileHeader.size(); i++) {
			if (!fileHeader.get(i).equals(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.HEADERS[i])) {
				throw new GenstarException("Invalid attribute file header must be " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.HEADER_STR + ". File: " + attributesCSVFile.getFilePath());
			}
		}
		
		// 2. Parse and initialize attributes
		for ( List<String> attributeInfo : fileContent) {
			
			if (attributeInfo.size() != CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS) { throw new GenstarException("Invalid attribute file format: each row must have " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS + " columns (file: " + attributesCSVFile.getFilePath() + "."); }
			
			String attributeNameOnData = (String)attributeInfo.get(0);
			String attributeNameOnEntity = (String)attributeInfo.get(1);
			
			String dataTypeStr = (String)attributeInfo.get(2);
			DataType dataType = DataType.fromName(dataTypeStr);
			if (dataType == null) { throw new GenstarException(dataTypeStr + " is not a supported data type."); }
			
			String valueTypeOnDataStr = (String)attributeInfo.get(3);
			String values = (String)attributeInfo.get(4);
			
			String valueTypeOnEntityStr = (String)attributeInfo.get(5);
			Class<? extends AttributeValue> valueClassOnEntity = AttributeValue.getClassByName(valueTypeOnEntityStr);
			
			
			String attributesFilePath = attributesCSVFile.getFilePath();
			if (valueTypeOnDataStr.equals(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.RANGE_VALUE_NAME)) {
				createRangeValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity, attributesFilePath);
			} else if (valueTypeOnDataStr.equals(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.UNIQUE_VALUE_NAME)) {
				createUniqueValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity, attributesFilePath);
			} else {
				throw new GenstarException("Invalid attribute file: unsupported value type (file: " + attributesFilePath + ")");
			}
		}
	}
}
