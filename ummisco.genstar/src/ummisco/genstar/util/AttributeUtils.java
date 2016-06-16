package ummisco.genstar.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import idees.genstar.configuration.GSAttDataType;
import idees.genstar.datareader.exception.GenstarIllegalRangedData;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeFactory;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
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
	 * @throws GenstarIllegalRangedData 
	 */
	static void createRangeValueAttribute(final ISyntheticPopulationGenerator generator, final String attributeNameOnData, final String attributeNameOnEntity, 
			final DataType dataType, final String values, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException, GenstarIllegalRangedData {
		AttributeFactory af = new AttributeFactory();
		AbstractAttribute rangeAttribute = null;
				//new RangeValuesAttribute(attributeNameOnData, attributeNameOnEntity, dataType, UniqueValue.class);
		
		// 1. Parse and accumulate each range value token into a list.
		StringTokenizer valueTokens = new StringTokenizer(values, CSV_FILE_FORMATS.ATTRIBUTES.ATTRIBUTE_VALUE_DELIMITER);
		if (valueTokens.countTokens() == 0) { throw new GenstarException("No value is defined for the attribute '" + attributeNameOnData + "'"); }
		List<String> rangeTokens = new ArrayList<String>();
		while (valueTokens.hasMoreTokens()) { rangeTokens.add(valueTokens.nextToken()); }
		
		// 2. Created range values from the parsed tokens.
		List<String> rangeString = new ArrayList<>();
		for (String t : rangeTokens) {
			StringTokenizer minMaxValueTokens = new StringTokenizer(t, CSV_FILE_FORMATS.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER);
			if (minMaxValueTokens.countTokens() != 2) { throw new GenstarException("Invalid attribute range value format (file: " + minMaxValueTokens.toString() + ")"); }
			rangeString.add(minMaxValueTokens.nextToken().trim()+" - "+minMaxValueTokens.nextToken().trim());
		}
		rangeAttribute = af.createAttribute(attributeNameOnData, attributeNameOnEntity, dataType, rangeString, GSAttDataType.range);
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
	 * @throws GenstarIllegalRangedData 
	 */
	static void createUniqueValueAttribute(final ISyntheticPopulationGenerator generator, final String attributeNameOnData, final String attributeNameOnEntity, 
			final DataType dataType, final String values, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException, GenstarIllegalRangedData {
		
		AttributeFactory af = new AttributeFactory();
		AbstractAttribute uniqueValueAttribute = null;
		
		// 1. Parse and accumulate each unique value token into a list.
		StringTokenizer valueTokenizers = new StringTokenizer(values, CSV_FILE_FORMATS.ATTRIBUTES.ATTRIBUTE_VALUE_DELIMITER);
		if (valueTokenizers.countTokens() == 0) { throw new GenstarException("No value is defined for the attribute '" + attributeNameOnData + "'"); }
		List<String> uniqueValueTokens = new ArrayList<String>();
		while (valueTokenizers.hasMoreTokens()) { uniqueValueTokens.add(valueTokenizers.nextToken()); }
		
		// 2. Create unique values from the parsed tokens.
		List<String> uniqueValues = new ArrayList<>();
		for (String t : uniqueValueTokens) { uniqueValues.add(t.trim()); }
		
		uniqueValueAttribute = af.createAttribute(attributeNameOnData, attributeNameOnEntity, dataType, uniqueValues, GSAttDataType.unique);
		generator.addAttribute(uniqueValueAttribute);
	}
	
	
	public static void createAttributesFromCsvFile(final ISyntheticPopulationGenerator generator, final GenstarCsvFile attributesFile) throws GenstarException {
		
		List<List<String>> attributeFileContent = attributesFile.getContent();
		if ( attributeFileContent == null || attributeFileContent.isEmpty() ) { throw new GenstarException("Empty attribute file. File: " + attributesFile.getPath()); }
		
		if (attributesFile.getColumns() != CSV_FILE_FORMATS.ATTRIBUTES.NB_OF_COLS) { 
			throw new GenstarException("Attributes file must have " + CSV_FILE_FORMATS.ATTRIBUTES.NB_OF_COLS + " columns. File: " + attributesFile.getPath()); 
		}
		
		// 1. Parse the header
		List<String> fileHeader = attributesFile.getHeaders();
		if (fileHeader.size() != CSV_FILE_FORMATS.ATTRIBUTES.NB_OF_COLS) {
			throw new GenstarException("Attributes file header must have " + CSV_FILE_FORMATS.ATTRIBUTES.NB_OF_COLS + " columns. File: " + attributesFile.getPath());
		}
		for (int i=0; i<fileHeader.size(); i++) {
			if (!fileHeader.get(i).equals(CSV_FILE_FORMATS.ATTRIBUTES.HEADERS[i])) {
				throw new GenstarException("Invalid attribute file header must be " + CSV_FILE_FORMATS.ATTRIBUTES.HEADER_STR + ". File: " + attributesFile.getPath());
			}
		}
		
		// 2. Parse and initialize attributes
		int line = 1;
		for ( List<String> attributeInfo : attributeFileContent) {
			
			if (attributeInfo.size() != CSV_FILE_FORMATS.ATTRIBUTES.NB_OF_COLS) { throw new GenstarException("Invalid attribute file format: each row must have " + CSV_FILE_FORMATS.ATTRIBUTES.NB_OF_COLS + " columns (file: " + attributesFile.getPath() + "."); }
			
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
			if (valueTypeOnDataStr.equals(CSV_FILE_FORMATS.ATTRIBUTES.RANGE_VALUE_NAME)) {
				try {
					createRangeValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity);
				} catch (Exception ex) {
					throw new GenstarException("Can not create range value attribute. File: " + attributesFilePath + ", line: " + line, ex);
				}
			} else if (valueTypeOnDataStr.equals(CSV_FILE_FORMATS.ATTRIBUTES.UNIQUE_VALUE_NAME)) {
				try {
					createUniqueValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity);
				} catch (Exception ex) {
					throw new GenstarException("Can not create unique value attribute. File: " + attributesFilePath + ", line: " + line, ex);
				}
			} else {
				throw new GenstarException("Invalid attribute file: unsupported value type (file: " + attributesFilePath + ")");
			}
			
			line++;
		}
	}

}
