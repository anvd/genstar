package ummisco.genstar.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.IWithAttributes;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.sample_free.FrequencyDistributionGenerationRule;


public class FrequencyDistributionUtils {


	public static void analyseFrequencyDistributionPopulation(final IPopulation population, final List<GenstarCsvFile> generationRuleFiles, final String analysisResultOutputFolderPath) throws GenstarException {
		
		// TODO parameters validation
		
		for (GenstarCsvFile ruleFile : generationRuleFiles) {

			List<AbstractAttribute> ruleAttributes = parseFrequencyDistributionFileHeader(population, null, ruleFile);
			List<List<String>> outputFileContent = new ArrayList<List<String>>();
			
			// output file header
			List<String> outputFileHeader = new ArrayList<String>(ruleFile.getHeaders());
			outputFileHeader.add("Generated Frequency");
			outputFileContent.add(outputFileHeader);
			
			// build output file content row by row
			for (List<String> ruleFileRow : ruleFile.getContent()) { 
				List<String> outputFileRow = new ArrayList<String>(ruleFileRow);
				
				Map<AbstractAttribute, AttributeValue> rowAttributeValues = parseFrequencyDistributionFileRow(ruleAttributes, ruleFileRow);
				outputFileRow.add(Integer.toString(population.getMatchingEntitiesByAttributeValuesOnData(rowAttributeValues).size())); // append "Generated Frequency"
				
				outputFileContent.add(outputFileRow);
			}
			
			
			// build output CSV file path
			String csvFilePath = ruleFile.getPath();
			String outputCsvFileName = csvFilePath.substring(csvFilePath.lastIndexOf("/") + 1);
			String outputCsvFilePath = analysisResultOutputFolderPath + (analysisResultOutputFolderPath.endsWith("/") ? "" : "/") + "RESULT_ANALYSIS_" + outputCsvFileName;

			
			// write output file to disk
			GenstarUtils.writeStringContentToCsvFile(outputFileContent, outputCsvFilePath);
		}
	}
	
	
	public static FrequencyDistributionGenerationRule createFrequencyDistributionGenerationRule(final SampleFreeGenerator generator, final String ruleName, final GenstarCsvFile ruleFile) throws GenstarException {
		
		List<List<String>> fileContent = ruleFile.getContent();
		if ( fileContent == null || fileContent.isEmpty() ) { throw new GenstarException("Frequency Distribution Generation Rule file is empty (file: " + ruleFile.getPath() + ")"); }
		
		
		// 1. Parse the header
		List<String> header = ruleFile.getHeaders();
		if (header.size() < 2) { throw new GenstarException("Invalid Frequency Distribution Generation Rule file header : header must have at least 2 elements (file: " + ruleFile.getPath() + ")"); }
		
		
		// 2. Create the rule then add attributes
		FrequencyDistributionGenerationRule generationRule = new FrequencyDistributionGenerationRule(generator, ruleName);
		List<AbstractAttribute> ruleAttributes = parseFrequencyDistributionFileHeader(generator, generationRule, ruleFile);
		generationRule.generateAttributeValuesFrequencies();
		
		
		// 3. Parse the frequency distribution file (row by row) and set frequencies
//		parseFrequencyDistributionFileContentAndSetFrequencies(generationRule, ruleAttributes, ruleFile);
		for (List<String> ruleFileRow : ruleFile.getContent()) { 
			Map<AbstractAttribute, AttributeValue> rowAttributeValues = parseFrequencyDistributionFileRow(ruleAttributes, ruleFileRow);
			int frequency = Integer.parseInt(ruleFileRow.get(ruleFileRow.size() - 1));
			generationRule.setFrequency(rowAttributeValues, frequency);
		}
		
		
		// 4. append generation rule to the generator
		generator.appendGenerationRule(generationRule);
		
		return generationRule;
	}
	
	
	// TODO change method to "parseFrequencyDistributionFileHeaderAndAddAttributes"
	private static List<AbstractAttribute> parseFrequencyDistributionFileHeader(final IWithAttributes attributesProvider, final FrequencyDistributionGenerationRule generationRule, final GenstarCsvFile frequencyDistributionGenerationRuleFile) throws GenstarException {
		
		List<String> header = frequencyDistributionGenerationRuleFile.getHeaders();
		if (header.size() < 2) { throw new GenstarException("Invalid Frequency Distribution Generation Rule file header : header must have at least 2 elements (file: " + frequencyDistributionGenerationRuleFile.getPath() + ")"); }
		
		// read and parse the header
		List<AbstractAttribute> ruleAttributes = new ArrayList<AbstractAttribute>();
		for (int headerIndex=0; headerIndex < (header.size() - 1); headerIndex++) { 
			StringTokenizer attributeToken = new StringTokenizer(header.get(headerIndex), CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE.ATTRIBUTE_NAME_TYPE_DELIMITER);
			if (attributeToken.countTokens() != 2) {
				StringBuffer invalidTokens = new StringBuffer();
				while (attributeToken.hasMoreElements()) {
					invalidTokens.append(attributeToken.nextToken());
					invalidTokens.append(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE.ATTRIBUTE_NAME_TYPE_DELIMITER);
				}
				
				throw new GenstarException("Invalid header format (" + invalidTokens.toString() + ") found in Frequency Distribution Generation Rule (file: " + frequencyDistributionGenerationRuleFile.getPath() + ")"); 
			}
			
			String attributeName = attributeToken.nextToken();
			String attributeType = attributeToken.nextToken();
			
			AbstractAttribute attribute = attributesProvider.getAttributeByNameOnData(attributeName);
			if (attribute == null) { throw new GenstarException("Unknown attribute (" + attributeName + ") found in Frequency Distribution Generation Rule (file: " + frequencyDistributionGenerationRuleFile.getPath() + ")"); }
			ruleAttributes.add(attribute);

			// add attribute to generation rule if necessary
			if (generationRule != null) {
				if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE.INPUT_ATTRIBUTE)) {
					generationRule.appendInputAttribute(attribute);
				} else if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE.OUTPUT_ATTRIBUTE)) {
					generationRule.appendOutputAttribute(attribute);
				} else {
					throw new GenstarException("Invalid attribute type (" + attributeType + ") found in Frequency Distribution Generation Rule (file: " + frequencyDistributionGenerationRuleFile.getPath() + ")");
				}
			}
		}
		
		return ruleAttributes;
	}
	
	
	private static Map<AbstractAttribute, AttributeValue> parseFrequencyDistributionFileRow(final List<AbstractAttribute> ruleAttributes, final List<String> fileRow) throws GenstarException {
		
		// TODO parameters validation

		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (int attributeIndex=0; attributeIndex<(fileRow.size()-1); attributeIndex++) {
			AbstractAttribute concerningAttribute = ruleAttributes.get(attributeIndex);
			DataType dataType = concerningAttribute.getDataType();
			
			if (concerningAttribute instanceof RangeValuesAttribute) {
				StringTokenizer minMaxValueToken = new StringTokenizer((String)fileRow.get(attributeIndex), CSV_FILE_FORMATS.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER);
				if (minMaxValueToken.countTokens() != 2) { throw new GenstarException("Invalid Frequency Distribution Generation Rule file format: invalid range attribute value"); }
				
				String minValue = minMaxValueToken.nextToken().trim();
				String maxValue = minMaxValueToken.nextToken().trim();
				RangeValue rangeValue = new RangeValue(dataType, minValue, maxValue);
				
				attributeValues.put(concerningAttribute, rangeValue);
			} else if (concerningAttribute instanceof UniqueValuesAttribute) {
				String value = (String)fileRow.get(attributeIndex);
				UniqueValue uniqueValue = new UniqueValue(dataType, value.trim());
				
				attributeValues.put(concerningAttribute, uniqueValue);
			} else {
				throw new GenstarException("Unsupported attribute type: " + concerningAttribute.getClass().getName());
			}
		}

		return attributeValues;
	}


	public static FrequencyDistributionGenerationRule createFrequencyDistributionGenerationRuleFromSampleDataOrPopulationFile(final SampleFreeGenerator generator, 
			final GenstarCsvFile distributionFormatFile, final GenstarCsvFile sampleDataOrPopulationFile) throws GenstarException {
		
		// 1. Parse the header
		List<String> distributionFormatHeader = distributionFormatFile.getHeaders();
		if (distributionFormatHeader.size() < 1) { throw new GenstarException("Header must have at least 1 elements (file: " + distributionFormatFile.getPath() + ")"); }
		
		
		// 2. Create the rule then add attributes
		FrequencyDistributionGenerationRule generationRule = new FrequencyDistributionGenerationRule(generator, "Dummy Frequency Distribution Generation Rule");
		List<AbstractAttribute> concerningAttributes = new ArrayList<AbstractAttribute>();
		for (int headerIndex=0; headerIndex < distributionFormatHeader.size(); headerIndex++) {
			StringTokenizer attributeToken = new StringTokenizer(distributionFormatHeader.get(headerIndex), CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE.ATTRIBUTE_NAME_TYPE_DELIMITER);
			if (attributeToken.countTokens() != 2) {
				StringBuffer invalidTokens = new StringBuffer();
				while (attributeToken.hasMoreElements()) {
					invalidTokens.append(attributeToken.nextToken());
					invalidTokens.append(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE.ATTRIBUTE_NAME_TYPE_DELIMITER);
				}
				
				throw new GenstarException("Invalid header format (" + invalidTokens.toString() + ") found in " + distributionFormatFile.getPath() + "."); 
			}
			
			String attributeName = attributeToken.nextToken();
			String attributeType = attributeToken.nextToken();
			
			AbstractAttribute attribute = generator.getAttributeByNameOnData(attributeName);
			if (attribute == null) { throw new GenstarException("Unknown attribute (" + attributeName + ") found in " + distributionFormatFile.getPath() + "."); }
			concerningAttributes.add(attribute);
			
			if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE.INPUT_ATTRIBUTE)) {
				generationRule.appendInputAttribute(attribute);
			} else if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE.OUTPUT_ATTRIBUTE)) {
				generationRule.appendOutputAttribute(attribute);
			} else {
				throw new GenstarException("Invalid attribute type (" + attributeType + ") found in Frequency Distribution Generation Rule (file: " + distributionFormatFile.getPath() + ")");
			}
		}
		
		generationRule.generateAttributeValuesFrequencies();
		
		// 3. Set frequencies
		// 3.1. save the index of attributes of the sample data
		List<String> sampleDataHeader = sampleDataOrPopulationFile.getHeaders();
		SortedMap<Integer, AbstractAttribute> attributeIndexes = new TreeMap<Integer, AbstractAttribute>();
		for (int col=0; col<sampleDataHeader.size(); col++) {
			String attributeNameOnEntity = sampleDataHeader.get(col);
			AbstractAttribute attribute = generationRule.getAttributeByNameOnEntity(attributeNameOnEntity);
			if (attribute != null) {
				attributeIndexes.put(col, attribute);
			};
		}
		
		// 3.2. calculate the number of attribute values
		List<List<String>> sampleData = sampleDataOrPopulationFile.getContent();
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		Set<AttributeValuesFrequency> attributeValuesFrequencies = generationRule.getAttributeValuesFrequencies();
		for (int row=0; row<sampleData.size(); row++) {
			attributeValues.clear();
			
			for (int col : attributeIndexes.keySet()) {
				String attributeValueString = sampleData.get(row).get(col);
				
				List<String> valueList = new ArrayList<String>();
				
				if (attributeValueString.contains(CSV_FILE_FORMATS.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER)) { // range value
					StringTokenizer rangeValueToken = new StringTokenizer(attributeValueString, CSV_FILE_FORMATS.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER);
					if (rangeValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value: '" + attributeValueString + "'. File: " + sampleDataOrPopulationFile.getPath()); }
					valueList.add(rangeValueToken.nextToken());
					valueList.add(rangeValueToken.nextToken());
				} else { // unique value
					valueList.add(attributeValueString);
				}
				
				AbstractAttribute concerningAttribute = attributeIndexes.get(col);
				AttributeValue value = concerningAttribute.getMatchingAttributeValueOnData(valueList);
				if (value == null) { throw new GenstarException("'" + attributeValueString + "' is not a valid value of '" + concerningAttribute.getNameOnData() + "' attribute. File: " + sampleDataOrPopulationFile.getPath()); }
				
				attributeValues.put(concerningAttribute, value);
			}
			
			for (AttributeValuesFrequency avf : attributeValuesFrequencies) {
				if (avf.matchAttributeValuesOnData(attributeValues)) { 
					avf.setFrequency(avf.getFrequency() + 1);
					break;
				}
			}
		}
		 
		return generationRule;
	}
}
