package ummisco.genstar.util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
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
import ummisco.genstar.ipf.IpfGenerationRule;
import ummisco.genstar.ipu.IpuGenerationRule;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.Population;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.metamodel.sample_data.CompoundSampleData;
import ummisco.genstar.metamodel.sample_data.ISampleData;
import ummisco.genstar.metamodel.sample_data.SampleData;
import ummisco.genstar.sample_free.AttributeInferenceGenerationRule;
import ummisco.genstar.sample_free.CustomSampleFreeGenerationRule;
import ummisco.genstar.sample_free.FrequencyDistributionGenerationRule;

import com.google.common.collect.Sets;


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
				AttributeValue value1 = valueFrequency1.getAttributeValueOnData(sAttribute);
				AttributeValue value2 = valueFrequency2.getAttributeValueOnData(sAttribute);

				if (value1 == null) { throw new IllegalArgumentException("'valueFrequency1' AttributeValuesFrequency doesn't contain " + sAttribute.getNameOnData() + " attribute."); }
				if (value2 == null) { throw new IllegalArgumentException("'valueFrequency2' AttributeValuesFrequency doesn't contain " + sAttribute.getNameOnData() + " attribute."); }
				
				int retVal = value1.compareTo(value2);
				if (retVal != 0) { return retVal; }
			}
			
			return 0;
		}
	}	

	
	public static void createFrequencyDistributionGenerationRule(final SampleFreeGenerator generator, final String ruleName, final GenstarCsvFile ruleFile) throws GenstarException {
		
		List<List<String>> fileContent = ruleFile.getContent();
		if ( fileContent == null || fileContent.isEmpty() ) { throw new GenstarException("Frequency Distribution Generation Rule file is empty (file: " + ruleFile.getPath() + ")"); }
		
		
		// 1. Parse the header
		List<String> header = ruleFile.getHeaders();
		if (header.size() < 2) { throw new GenstarException("Invalid Frequency Distribution Generation Rule file header : header must have at least 2 elements (file: " + ruleFile.getPath() + ")"); }
		
		
		// 2. Create the rule then add attributes
		FrequencyDistributionGenerationRule generationRule = new FrequencyDistributionGenerationRule(generator, ruleName);
		List<AbstractAttribute> concerningAttributes = new ArrayList<AbstractAttribute>();
		for (int headerIndex=0; headerIndex < (header.size() - 1); headerIndex++) {
			StringTokenizer attributeToken = new StringTokenizer(header.get(headerIndex), INPUT_DATA_FORMATS.CSV_FILES.FREQUENCY_DISTRIBUTION_GENERATION_RULE.ATTRIBUTE_NAME_TYPE_DELIMITER);
			if (attributeToken.countTokens() != 2) {
				StringBuffer invalidTokens = new StringBuffer();
				while (attributeToken.hasMoreElements()) {
					invalidTokens.append(attributeToken.nextToken());
					invalidTokens.append(INPUT_DATA_FORMATS.CSV_FILES.FREQUENCY_DISTRIBUTION_GENERATION_RULE.ATTRIBUTE_NAME_TYPE_DELIMITER);
				}
				
				throw new GenstarException("Invalid header format (" + invalidTokens.toString() + ") found in Frequency Distribution Generation Rule (file: " + ruleFile.getPath() + ")"); 
			}
			
			String attributeName = attributeToken.nextToken();
			String attributeType = attributeToken.nextToken();
			
			AbstractAttribute attribute = generator.getAttributeByNameOnData(attributeName);
			if (attribute == null) { throw new GenstarException("Unknown attribute (" + attributeName + ") found in Frequency Distribution Generation Rule (file: " + ruleFile.getPath() + ")"); }
			concerningAttributes.add(attribute);
			
			if (attributeType.equals(INPUT_DATA_FORMATS.CSV_FILES.FREQUENCY_DISTRIBUTION_GENERATION_RULE.INPUT_ATTRIBUTE)) {
				generationRule.appendInputAttribute(attribute);
			} else if (attributeType.equals(INPUT_DATA_FORMATS.CSV_FILES.FREQUENCY_DISTRIBUTION_GENERATION_RULE.OUTPUT_ATTRIBUTE)) {
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
					StringTokenizer minMaxValueToken = new StringTokenizer((String)frequencyInfo.get(attributeIndex), INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER);
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
	
	
	public static void createAttributeInferenceGenerationRule(final SampleFreeGenerator generator, final String ruleName, final GenstarCsvFile ruleFile) throws GenstarException {
		
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
		
		if (rows != inferringAttribute.valuesOnData().size()) { throw new GenstarException("Generation Rule must contain exacly the same number of attribute values defined in the inferring attribute and inferred attribute (file: " + ruleFile.getPath() + ")"); }
		
		
		// 2. Create the rule & set inference data
		AttributeInferenceGenerationRule generationRule = new AttributeInferenceGenerationRule(generator, ruleName, inferringAttribute, inferredAttribute);
		Map<AttributeValue, AttributeValue> inferenceData = new HashMap<AttributeValue, AttributeValue>();
		AttributeValue inferringValue, inferredValue;
		for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
			List<String> inferenceInfo = fileContent.get(rowIndex);
			
			if (inferringAttribute instanceof RangeValuesAttribute) {
				StringTokenizer minMaxValueToken = new StringTokenizer((String)inferenceInfo.get(0), INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER);
				if (minMaxValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value format: " + (String)inferenceInfo.get(0) + " (file: " + ruleFile.getPath() + ")"); }
				
				String minValue = minMaxValueToken.nextToken().trim();
				String maxValue = minMaxValueToken.nextToken().trim();
				inferringValue = inferringAttribute.getInstanceOfAttributeValue(new RangeValue(inferringAttribute.getDataType(), minValue, maxValue));
			} else {
				inferringValue = inferringAttribute.getInstanceOfAttributeValue(new UniqueValue(inferringAttribute.getDataType(), (String)inferenceInfo.get(0)));
			}
			
			if (inferredAttribute instanceof RangeValuesAttribute) {
				StringTokenizer minMaxValueToken = new StringTokenizer((String)inferenceInfo.get(1), INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER);
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
	
	
	

		
	static List<AbstractAttribute> parseSupplementaryAttributesCsvFile(final ISyntheticPopulationGenerator generator, final GenstarCsvFile supplementaryAttributesFile) throws GenstarException {
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
	
	public static void createCustomGenerationRule(final SampleFreeGenerator generator, final String ruleName, final String ruleJavaClass) throws GenstarException {
		try {
			StringTokenizer ruleJavaClassTokenizer = new StringTokenizer(ruleJavaClass, INPUT_DATA_FORMATS.CSV_FILES.GENERATION_RULES.JAVA_CLASS_PARAMETER_DELIMITER);
			int tokens = ruleJavaClassTokenizer.countTokens();
			if (tokens > 2 || tokens == 0) { throw new GenstarException("Invalid custom generation rule: " + ruleJavaClass); }
			
			// parse java class name and parameter values
			String javaClassName = ruleJavaClassTokenizer.nextToken();
			String parameterValuesStr = "";
			if (tokens == 2) { parameterValuesStr = ruleJavaClassTokenizer.nextToken(); }
			
			Class ruleJavaClazz = Class.forName(javaClassName);
			Constructor customRuleConstructor = ruleJavaClazz.getConstructor(ISyntheticPopulationGenerator.class, String.class, String.class);
			CustomSampleFreeGenerationRule customRule = (CustomSampleFreeGenerationRule) customRuleConstructor.newInstance(generator, ruleName, parameterValuesStr);

			generator.appendGenerationRule(customRule);
		} catch (final Exception e) {
			if (e instanceof GenstarException) { throw (GenstarException)e; }
			else if (e instanceof InvocationTargetException) { e.printStackTrace(); }
			else throw new GenstarException(e.getMessage());
		}
	}	
	
	
	public static FrequencyDistributionGenerationRule createFrequencyDistributionGenerationRuleFromSampleData(final SampleFreeGenerator generator, 
			final GenstarCsvFile distributionFormatFile, final GenstarCsvFile sampleDataFile) throws GenstarException {
		
		// 1. Parse the header
		List<String> distributionFormatHeader = distributionFormatFile.getHeaders();
		if (distributionFormatHeader.size() < 1) { throw new GenstarException("Header must have at least 1 elements (file: " + distributionFormatFile.getPath() + ")"); }
		
		
		// 2. Create the rule then add attributes
		FrequencyDistributionGenerationRule generationRule = new FrequencyDistributionGenerationRule(generator, "Dummy Frequency Distribution Generation Rule");
		List<AbstractAttribute> concerningAttributes = new ArrayList<AbstractAttribute>();
		for (int headerIndex=0; headerIndex < distributionFormatHeader.size(); headerIndex++) {
			StringTokenizer attributeToken = new StringTokenizer(distributionFormatHeader.get(headerIndex), INPUT_DATA_FORMATS.CSV_FILES.FREQUENCY_DISTRIBUTION_GENERATION_RULE.ATTRIBUTE_NAME_TYPE_DELIMITER);
			if (attributeToken.countTokens() != 2) {
				StringBuffer invalidTokens = new StringBuffer();
				while (attributeToken.hasMoreElements()) {
					invalidTokens.append(attributeToken.nextToken());
					invalidTokens.append(INPUT_DATA_FORMATS.CSV_FILES.FREQUENCY_DISTRIBUTION_GENERATION_RULE.ATTRIBUTE_NAME_TYPE_DELIMITER);
				}
				
				throw new GenstarException("Invalid header format (" + invalidTokens.toString() + ") found in " + distributionFormatFile.getPath() + "."); 
			}
			
			String attributeName = attributeToken.nextToken();
			String attributeType = attributeToken.nextToken();
			
			AbstractAttribute attribute = generator.getAttributeByNameOnData(attributeName);
			if (attribute == null) { throw new GenstarException("Unknown attribute (" + attributeName + ") found in " + distributionFormatFile.getPath() + "."); }
			concerningAttributes.add(attribute);
			
			if (attributeType.equals(INPUT_DATA_FORMATS.CSV_FILES.FREQUENCY_DISTRIBUTION_GENERATION_RULE.INPUT_ATTRIBUTE)) {
				generationRule.appendInputAttribute(attribute);
			} else if (attributeType.equals(INPUT_DATA_FORMATS.CSV_FILES.FREQUENCY_DISTRIBUTION_GENERATION_RULE.OUTPUT_ATTRIBUTE)) {
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
				
				if (attributeValueString.contains(INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER)) { // range value
					StringTokenizer rangeValueToken = new StringTokenizer(attributeValueString, INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER);
					if (rangeValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value: '" + attributeValueString + "'. File: " + sampleDataFile.getPath()); }
					valueList.add(rangeValueToken.nextToken());
					valueList.add(rangeValueToken.nextToken());
				} else { // unique value
					valueList.add(attributeValueString);
				}
				
				AbstractAttribute concerningAttribute = attributeIndexes.get(col);
				AttributeValue value = concerningAttribute.findCorrespondingAttributeValueOnData(valueList);
				if (value == null) { throw new GenstarException("'" + attributeValueString + "' is not a valid value of '" + concerningAttribute.getNameOnData() + "' attribute. File: " + sampleDataFile.getPath()); }
				
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
	
	
	public static void writeContentToCsvFile(final List<List<String>> fileContent, final String csvFilePath) throws GenstarException {
		// parameters validation
		if( fileContent == null) { throw new GenstarException("Parameter fileContent can not be null"); }
		if (csvFilePath == null) { throw new GenstarException("Parameter csvFilePath can not be null"); }
		
		try {
			CsvWriter writer = new CsvWriter(csvFilePath);
			for (List<String> row : fileContent) {
				writer.writeRecord(row.toArray(new String[0]));
			}
			
			writer.flush();
			writer.close();
		} catch (final Exception e) {
			throw new GenstarException(e);
		}
	}
	
	
	public static IPopulation generateRandomSinglePopulation(final String populationName, final GenstarCsvFile attributesFile, 
			final int minEntitiesOfEachAttributeValuesSet, final int maxEntitiesOfEachAttributeValuesSet) throws GenstarException {
		// parameters validation
		if (populationName == null || populationName.isEmpty()) { throw new GenstarException("Parameter populationName can not be null or empty"); }
		if (attributesFile == null) { throw new GenstarException("Parameter attributesFile can not be null"); }
		if (minEntitiesOfEachAttributeValuesSet < 1) { throw new GenstarException("minEntitiesOfEachAttributeValuesSet can not be smaller than 1"); }
		if (maxEntitiesOfEachAttributeValuesSet < minEntitiesOfEachAttributeValuesSet) { throw new GenstarException("maxEntitiesOfEachAttributeValuesSet can not be smaller than minEntitiesOfEachAttributeValuesSet"); }
		
		SampleBasedGenerator generator = new SampleBasedGenerator("dummy single rule generator");
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);

		Set<AbstractAttribute> attributes = new HashSet<AbstractAttribute>(generator.getAttributes());
		IPopulation syntheticPopulation = new Population(PopulationType.SYNTHETIC_POPULATION, populationName, generator.getAttributes());

		// build attributeValuesMaps
		List<Set<AttributeValue>> attributesPossibleValues = new ArrayList<Set<AttributeValue>>();
		Map<AbstractAttribute, Boolean> valueOnDataSameAsValueOnEntity = new HashMap<AbstractAttribute, Boolean>();
		for (AbstractAttribute attribute : attributes) { 
			attributesPossibleValues.add(attribute.valuesOnData()); 
			
			if (attribute.getValueClassOnData().equals(attribute.getValueClassOnEntity())) { valueOnDataSameAsValueOnEntity.put(attribute, true); }
			else { valueOnDataSameAsValueOnEntity.put(attribute, false); }
			
		}
		List<Map<AbstractAttribute, AttributeValue>> attributeValuesMaps = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
		for (List<AttributeValue> cartesian : Sets.cartesianProduct(attributesPossibleValues)) {
			attributeValuesMaps.add(buildAttributeValueMap(attributes, cartesian));
		}
		
		
		// build attributeValuesOnEntityMaps
		List<Map<AbstractAttribute, AttributeValue>> attributeValuesOnEntityMaps = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
		for (Map<AbstractAttribute, AttributeValue> attributeValuesOnData : attributeValuesMaps) {
			Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>();
			
			for (Map.Entry<AbstractAttribute, AttributeValue> entry : attributeValuesOnData.entrySet()) {
				if (valueOnDataSameAsValueOnEntity.get(entry.getKey())) {
					attributeValuesOnEntity.put(entry.getKey(), entry.getValue());
				} else {
					attributeValuesOnEntity.put(entry.getKey(), entry.getValue().cast(entry.getKey().getValueClassOnEntity()));
				}
			}
			
			attributeValuesOnEntityMaps.add(attributeValuesOnEntity);
		}
		
		
		// generate the population (sample data)
		if (minEntitiesOfEachAttributeValuesSet == maxEntitiesOfEachAttributeValuesSet) {
			for (Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity : attributeValuesOnEntityMaps) {
				for (int entityIndex=0; entityIndex<minEntitiesOfEachAttributeValuesSet; entityIndex++) {
					syntheticPopulation.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity);
				}
				
			}
		} else {
			int entityDifference = maxEntitiesOfEachAttributeValuesSet - minEntitiesOfEachAttributeValuesSet;
			for (Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity : attributeValuesOnEntityMaps) {
				int nbOfEntities = minEntitiesOfEachAttributeValuesSet + SharedInstances.RandomNumberGenerator.nextInt(entityDifference);
				
				for (int entityIndex=0; entityIndex<nbOfEntities; entityIndex++) {
					syntheticPopulation.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity);
				}
			}
		}
		
		return syntheticPopulation;
	}
	
	
	public static IPopulation generateRandomSinglePopulation(final String populationName, final GenstarCsvFile attributesFile, final int entities) throws GenstarException {
		// parameters validation
		if (populationName == null || populationName.isEmpty()) { throw new GenstarException("Parameter populationName can not be null or empty"); }
		if (attributesFile == null) { throw new GenstarException("Parameter attributesFile can not be null"); }
		if (entities <= 0) { throw new GenstarException("Parameter entities must be positive"); }
		
		SampleBasedGenerator generator = new SampleBasedGenerator("dummy single rule generator");
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		List<AbstractAttribute> attributes = new ArrayList<AbstractAttribute>(generator.getAttributes());
		
		// cache values to use later on
		Map<AbstractAttribute, List<AttributeValue>> attributeValues = new HashMap<AbstractAttribute, List<AttributeValue>>();
		Map<AbstractAttribute, Integer> attributeValueSizes = new HashMap<AbstractAttribute, Integer>();
		Map<AbstractAttribute, Boolean> valueOnDataSameAsValueOnEntity = new HashMap<AbstractAttribute, Boolean>();
		Map<AbstractAttribute, Class> valueOnEntityClasses = new HashMap<AbstractAttribute, Class>();
		for (AbstractAttribute attr : attributes) { 
			attributeValues.put(attr, new ArrayList<AttributeValue>(attr.valuesOnData())); 
			attributeValueSizes.put(attr, attr.valuesOnData().size());
			valueOnEntityClasses.put(attr, attr.getValueClassOnEntity());
			
			if (attr.getValueClassOnData().equals(attr.getValueClassOnEntity())) { valueOnDataSameAsValueOnEntity.put(attr, true); }
			else { valueOnDataSameAsValueOnEntity.put(attr, false); }
		}
		
		IPopulation syntheticPopulation = new Population(PopulationType.SYNTHETIC_POPULATION, populationName, attributes);
		
		// generate the population
		int size;
		List<AttributeValue> values;
		AttributeValue valueOnData;
		Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>();
		for (int i=0; i<entities; i++) {
			for (int attrIndex=0; attrIndex<attributes.size(); attrIndex++) {
				values = attributeValues.get(attributes.get(attrIndex));
				size = attributeValueSizes.get(attributes.get(attrIndex));
				valueOnData = values.get(SharedInstances.RandomNumberGenerator.nextInt(size));
				
				if (valueOnDataSameAsValueOnEntity.get(attributes.get(attrIndex))) {
					attributeValuesOnEntity.put(attributes.get(attrIndex), valueOnData);
				} else { // valueOnData != valueOnEntity
					attributeValuesOnEntity.put(attributes.get(attrIndex), valueOnData.cast(valueOnEntityClasses.get(attributes.get(attrIndex))));
				}
			}
	
			syntheticPopulation.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity);
		}
		
		return syntheticPopulation;
	}
	

	public static IPopulation generateRandomCompoundPopulation(final String groupPopulationName, final GenstarCsvFile groupAttributesFile, 
			final String componentPopulationName, final GenstarCsvFile componentAttributesFile, final String groupIdAttributeNameOnDataOfGroupEntity, 
			final String groupIdAttributeNameOnDataOfComponentEntity, final String groupSizeAttributeNameOnData, final int nbOfGroupEntities) throws GenstarException {
		
		// 0. parameters validation
		if (groupPopulationName == null) { throw new GenstarException("Parameter groupPopulationName can not be null"); }
		if (groupAttributesFile == null) { throw new GenstarException("Parameter groupAttributesFile can not be null"); }
		if (componentPopulationName == null) { throw new GenstarException("Parameter componentPopulationName can not be null"); }
		if (componentAttributesFile == null) { throw new GenstarException("Parameter componentAttributesFile can not be null"); }
		if (groupIdAttributeNameOnDataOfGroupEntity == null) { throw new GenstarException("Parameter groupIdAttributeNameOnDataOfGroupEntity can not be null"); }
		if (groupIdAttributeNameOnDataOfComponentEntity == null) { throw new GenstarException("Parameter groupIdAttributeNameOnDataOfComponentEntity can not be null"); }
		if (groupSizeAttributeNameOnData == null) { throw new GenstarException("Parameter groupSizeAttributeNameOnData can not be null"); }
		if (nbOfGroupEntities <= 0) { throw new GenstarException("Parameter nbOfGroupEntities must be positive"); }

		
		// 1. create group attributes from groupAttributesFile
		SampleBasedGenerator groupGenerator = new SampleBasedGenerator("group dummy generator");
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesFile);
		List<AbstractAttribute> groupAttributes = groupGenerator.getAttributes();
		
		// 2. retrieve reference to groupIdAttributeOnGroupEntity
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData(groupIdAttributeNameOnDataOfGroupEntity);
		if (groupIdAttributeOnGroupEntity == null) { throw new GenstarException(groupIdAttributeNameOnDataOfGroupEntity + " is considered as group identity attribute but not found among the available group attributes"); }
		groupIdAttributeOnGroupEntity.setIdentity(true);
		// Important Note: groupIdOnGroupAttribute is an integer, beginning with 0, taking 1 as increment
		// ID should not be defined in the attributesFile
		
		// 3.  retrieve reference to groupSizeAttribute
		AbstractAttribute groupSizeAttribute = groupGenerator.getAttributeByNameOnData(groupSizeAttributeNameOnData);
		if (groupSizeAttribute == null) { throw new GenstarException(groupSizeAttributeNameOnData + " is considered as group size attribute but not found among the available group attributes"); }
		if (!groupSizeAttribute.getValueClassOnEntity().equals(UniqueValue.class)) { throw new GenstarException(groupSizeAttributeNameOnData + " attribute must have unique value"); }
		if (!groupSizeAttribute.getDataType().equals(DataType.INTEGER)) { throw new GenstarException(groupSizeAttributeNameOnData + " attribute must have " + DataType.INTEGER.getName() + " as data type"); }
		
		// 4. generate group population
		IPopulation groupPopulation = generateGroupPopulation(groupPopulationName, groupAttributes, groupIdAttributeOnGroupEntity, nbOfGroupEntities);

		// 5. create component attributes from componentAttributesFile
		SampleBasedGenerator componentGenerator = new SampleBasedGenerator("component dummy generator");
		AttributeUtils.createAttributesFromCsvFile(componentGenerator, componentAttributesFile);
		List<AbstractAttribute> componentAttributes = new ArrayList<AbstractAttribute>(componentGenerator.getAttributes());

		AbstractAttribute groupIdAttributeOnComponentEntity = componentGenerator.getAttributeByNameOnData(groupIdAttributeNameOnDataOfComponentEntity);
		if (groupIdAttributeOnComponentEntity == null) { throw new GenstarException(groupIdAttributeNameOnDataOfComponentEntity + " is considered as group identity attribute on component but not found among the available component attributes"); }

		// 6. generate component population
		generateComponentPopulation(groupPopulation, componentPopulationName, componentAttributes, groupIdAttributeOnGroupEntity,
				groupIdAttributeOnComponentEntity, groupSizeAttribute);
		
		
		return groupPopulation;
	}
	
	
	public static IPopulation generateRandomCompoundPopulation(final String groupPopulationName, final GenstarCsvFile groupAttributesFile, 
			final String componentPopulationName, final GenstarCsvFile componentAttributesFile, final String groupIdAttributeNameOnDataOfGroupEntity, 
			final String groupIdAttributeNameOnDataOfComponentEntity, final String groupSizeAttributeNameOnData, final int minGroupEntitiesOfEachAttributeValuesSet, 
			final int maxGroupEntitiesOfEachAttributeValuesSet) throws GenstarException {
		
		// 0. parameters validation
		if (groupPopulationName == null) { throw new GenstarException("Parameter groupPopulationName can not be null"); }
		if (groupAttributesFile == null) { throw new GenstarException("Parameter groupAttributesFile can not be null"); }
		if (componentPopulationName == null) { throw new GenstarException("Parameter componentPopulationName can not be null"); }
		if (componentAttributesFile == null) { throw new GenstarException("Parameter componentAttributesFile can not be null"); }
		if (groupIdAttributeNameOnDataOfGroupEntity == null) { throw new GenstarException("Parameter groupIdAttributeNameOnDataOfGroupEntity can not be null"); }
		if (groupIdAttributeNameOnDataOfComponentEntity == null) { throw new GenstarException("Parameter groupIdAttributeNameOnDataOfComponentEntity can not be null"); }
		if (groupSizeAttributeNameOnData == null) { throw new GenstarException("Parameter groupSizeAttributeNameOnData can not be null"); }
		if (minGroupEntitiesOfEachAttributeValuesSet < 0 || maxGroupEntitiesOfEachAttributeValuesSet < 0) { throw new GenstarException("minGroupEntitiesOfEachAttributeValuesSet and maxGroupEntitiesOfEachAttributeValuesSet can not be negative"); }
		if (maxGroupEntitiesOfEachAttributeValuesSet < minGroupEntitiesOfEachAttributeValuesSet) { throw new GenstarException("maxGroupEntitiesOfEachAttributeValuesSet can not be smaller than minGroupEntitiesOfEachAttributeValuesSet"); }
		
		
		// 1. create group attributes from groupAttributesFile
		SampleBasedGenerator groupGenerator = new SampleBasedGenerator("group dummy generator");
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesFile);

		
		// 2. retrieve reference to groupIdAttributeOnGroupEntity
		AbstractAttribute groupIdAttributeOfGroupEntity = groupGenerator.getAttributeByNameOnData(groupIdAttributeNameOnDataOfGroupEntity);
		if (groupIdAttributeOfGroupEntity == null) { throw new GenstarException(groupIdAttributeNameOnDataOfGroupEntity + " is considered as group identity attribute but not found among the available group attributes"); }

		
		// 3. build groupAttributeValuesMaps
		Set<AbstractAttribute> groupAttributesWithoutID = new HashSet<AbstractAttribute>(groupGenerator.getAttributes());
		groupAttributesWithoutID.remove(groupIdAttributeOfGroupEntity); // Important: Identity attribute is not taken into account  
		
		List<Set<AttributeValue>> groupAttributePossibleValuesOnData = new ArrayList<Set<AttributeValue>>();
		Map<AbstractAttribute, Boolean> valuesOnDataSameAsValuesOnEntity = new HashMap<AbstractAttribute, Boolean>();
		for (AbstractAttribute groupAttr : groupAttributesWithoutID) {
			groupAttributePossibleValuesOnData.add(groupAttr.valuesOnData());
			
			if (groupAttr.getValueClassOnData().equals(groupAttr.getValueClassOnEntity())) { valuesOnDataSameAsValuesOnEntity.put(groupAttr, true); }
			else { valuesOnDataSameAsValuesOnEntity.put(groupAttr, false); }
		}
		
		List<Map<AbstractAttribute, AttributeValue>> groupAttributeValuesOnDataList = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
		for (List<AttributeValue> cartesian : Sets.cartesianProduct(groupAttributePossibleValuesOnData)) {
			groupAttributeValuesOnDataList.add(buildAttributeValueMap(groupAttributesWithoutID, cartesian));
		}
		
		
		// 4. build groupAttributeValuesOnEntityList
		List<Map<AbstractAttribute, AttributeValue>> groupAttributeValuesOnEntityList = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
		for (Map<AbstractAttribute, AttributeValue> groupAttributeValuesOnData : groupAttributeValuesOnDataList) {
			Map<AbstractAttribute, AttributeValue> groupAttributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>();
			
			for (Map.Entry<AbstractAttribute, AttributeValue> entry : groupAttributeValuesOnData.entrySet()) {
				if (valuesOnDataSameAsValuesOnEntity.get(entry.getKey())) {
					groupAttributeValuesOnEntity.put(entry.getKey(), entry.getValue());
				} else {
					groupAttributeValuesOnEntity.put(entry.getKey(), entry.getValue().cast(entry.getKey().getValueClassOnEntity()));
				}
			}
			
			groupAttributeValuesOnEntityList.add(groupAttributeValuesOnEntity);
		}
		
		
		// 5. retrieve reference to groupSizeAttribute
		AbstractAttribute groupSizeAttribute = groupGenerator.getAttributeByNameOnData(groupSizeAttributeNameOnData);
		if (groupSizeAttribute == null) { throw new GenstarException(groupSizeAttributeNameOnData + " is considered as group size attribute but not found among the available group attributes"); }
		if (!groupSizeAttribute.getValueClassOnEntity().equals(UniqueValue.class)) { throw new GenstarException(groupSizeAttributeNameOnData + " attribute must have unique value"); }
		if (!groupSizeAttribute.getDataType().equals(DataType.INTEGER)) { throw new GenstarException(groupSizeAttributeNameOnData + " attribute must have " + DataType.INTEGER.getName() + " as data type"); }

				
		// 6. generate group population
		int groupID = 0;
		IPopulation groupPopulation = new Population(PopulationType.SYNTHETIC_POPULATION, groupPopulationName, groupGenerator.getAttributes());
		if (minGroupEntitiesOfEachAttributeValuesSet == maxGroupEntitiesOfEachAttributeValuesSet) {
			for (Map<AbstractAttribute, AttributeValue> groupAttributeValuesOnEntity : groupAttributeValuesOnEntityList) {
				for (int entityIndex=0; entityIndex<minGroupEntitiesOfEachAttributeValuesSet; entityIndex++) {
					Map<AbstractAttribute, AttributeValue> copyGroupAttributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>(groupAttributeValuesOnEntity);
					copyGroupAttributeValuesOnEntity.put(groupIdAttributeOfGroupEntity, new UniqueValue(DataType.INTEGER, Integer.toString(groupID))); // change ID value
					
					groupPopulation.createEntityWithAttributeValuesOnEntity(copyGroupAttributeValuesOnEntity);
					groupID++;
				}
			}
		} else {
			int entityDifference = maxGroupEntitiesOfEachAttributeValuesSet - minGroupEntitiesOfEachAttributeValuesSet;
			for (Map<AbstractAttribute, AttributeValue> groupAttributeValuesOnEntity : groupAttributeValuesOnEntityList) {
				int nbOfEntities = minGroupEntitiesOfEachAttributeValuesSet + SharedInstances.RandomNumberGenerator.nextInt(entityDifference);
				
				for (int entityIndex=0; entityIndex<nbOfEntities; entityIndex++) {
					Map<AbstractAttribute, AttributeValue> copyGroupAttributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>(groupAttributeValuesOnEntity);
					copyGroupAttributeValuesOnEntity.put(groupIdAttributeOfGroupEntity, new UniqueValue(DataType.INTEGER, Integer.toString(groupID))); // change ID value
					
					groupPopulation.createEntityWithAttributeValuesOnEntity(copyGroupAttributeValuesOnEntity);
					groupID++;
				}
			}
		}

		
		// 7. create component attributes from componentAttributesFile. GenstarUtils.generateComponentPopulation
		SampleBasedGenerator componentGenerator = new SampleBasedGenerator("component dummy generator");
		AttributeUtils.createAttributesFromCsvFile(componentGenerator, componentAttributesFile);
		List<AbstractAttribute> componentAttributes = new ArrayList<AbstractAttribute>(componentGenerator.getAttributes());

		
		// 8. retrieve reference to groupIdAttributeOnComponentEntity
		AbstractAttribute groupIdAttributeOfComponentEntity = componentGenerator.getAttributeByNameOnData(groupIdAttributeNameOnDataOfComponentEntity);
		if (groupIdAttributeOfComponentEntity == null) { throw new GenstarException(groupIdAttributeNameOnDataOfComponentEntity + " is considered as group identity attribute on component but not found among the available component attributes"); }

		
		// 9. generate the component populations
		GenstarUtils.generateComponentPopulation(groupPopulation, componentPopulationName, componentAttributes, 
				groupIdAttributeOfGroupEntity, groupIdAttributeOfComponentEntity, groupSizeAttribute);

		
		return groupPopulation;
	}
	
	
	public static IPopulation loadSinglePopulation(final PopulationType populationType, final String populationName, final GenstarCsvFile attributesFile, 
			final GenstarCsvFile singlePopulationFile) throws GenstarException { 
		
		SampleBasedGenerator generator = new SampleBasedGenerator("generator");
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		return GenstarUtils.loadSinglePopulation(populationType, populationName, generator.getAttributes(), singlePopulationFile);
	}
	
	
	public static IPopulation loadSinglePopulation(final PopulationType populationType, final String populationName, final List<AbstractAttribute> attributes, final GenstarCsvFile singlePopulationFile) throws GenstarException {
		
		// parameters validation
		if (populationType == null || populationName == null || attributes == null || singlePopulationFile == null) {
			throw new GenstarException("No parameter can be null");
		}
		
		IPopulation population = new Population(populationType, populationName, attributes);
		
		Map<String, AbstractAttribute> attributeMap = new HashMap<String, AbstractAttribute>(); // attributeNameOnEntity :: attribute
		for (AbstractAttribute attr : attributes) { attributeMap.put(attr.getNameOnEntity(), attr); }
		
		// 1. parse CSV file header
		List<String> sampleDataHeaders = singlePopulationFile.getHeaders();
		Map<Integer, AbstractAttribute> attributeIndexes = new TreeMap<Integer, AbstractAttribute>();
		for (int col=0; col<sampleDataHeaders.size(); col++) {
			String attributeNameOnSampleEntity = sampleDataHeaders.get(col);
			AbstractAttribute attribute = attributeMap.get(attributeNameOnSampleEntity);
			if (attribute != null) { attributeIndexes.put(col, attribute); }
		}

		// 2. verify that the CSV file contains all the "required" attributes
		if (attributeMap.size() != attributeIndexes.size()) {
			List<String> attributeOnSampleFile = new ArrayList<String>();
			for (AbstractAttribute attr : attributeIndexes.values()) { attributeOnSampleFile.add(attr.getNameOnEntity()); }
			
			List<String> attributeOnSEPopulation = new ArrayList<String>(attributeMap.keySet());
			
			attributeOnSEPopulation.removeAll(attributeOnSampleFile);
			if (!attributeOnSEPopulation.isEmpty()) {
				StringBuffer missingAttributes = new StringBuffer();
				
				int size = 0;
				for (String attrName : attributeOnSEPopulation) {
					missingAttributes.append(attrName);
					if (size < attributeOnSEPopulation.size() - 1) { missingAttributes.append(", "); }
					size++;
				}
				
				throw new GenstarException("Missing required attribute(s) : " + missingAttributes.toString() + ". Sample data file : " + singlePopulationFile.getPath());
			}
		}

		// 3. initialize sample entities
		List<String> rowContent;
		AbstractAttribute attribute;
		AttributeValue value;
		String valueStr;
		List<Map<AbstractAttribute, AttributeValue>> sampleEntitiesAttributeValues = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
		Map<AbstractAttribute, AttributeValue> sampleAttributes;
		for (int row=0; row<(singlePopulationFile.getRows()-1); row++) { // first line is the header
			rowContent = singlePopulationFile.getRow(row);
			
			sampleAttributes = new HashMap<AbstractAttribute, AttributeValue>();
			for (int attributeColumn : attributeIndexes.keySet()) { // only care about "recognized" attributes
				attribute = attributeIndexes.get(attributeColumn);
				valueStr = rowContent.get(attributeColumn);
				List<String> valueStrList = new ArrayList<String>();
				valueStrList.add(valueStr);
				
				value = attribute.findCorrespondingAttributeValueOnData(valueStrList); // ensure that the value is accepted by the attribute
				if (value == null) { 
					throw new GenstarException("'" + valueStr + "' defined in the sample data is not recognized as a value of " + attribute.getNameOnEntity()
								+ " attribute. File: " + singlePopulationFile.getPath() + " at row: " + (row + 2) + ", column: " + (attributeColumn + 1) + "."); 
				}
				
				if (attribute.getValueClassOnData().equals(attribute.getValueClassOnEntity())) { // valueOnClass == valueOnEntity
					sampleAttributes.put(attributeIndexes.get(attributeColumn), value);
				} else {
					sampleAttributes.put(attributeIndexes.get(attributeColumn), 
							GenstarUtils.createAttributeValue(attribute.getValueClassOnEntity(), attribute.getDataType(), valueStrList));
				}
			}
			sampleEntitiesAttributeValues.add(sampleAttributes);
		}
		
		population.createEntitiesWithAttributeValuesOnEntities(sampleEntitiesAttributeValues);
		
		
		return population;
	}
	
	
	public static IPopulation loadCompoundPopulation(final PopulationType populationType, 
			final String groupPopulationName, final GenstarCsvFile groupAttributesFile, final GenstarCsvFile groupPopulationFile,
			final String componentPopulationName, final GenstarCsvFile componentAttributesFile, final GenstarCsvFile componentPopulationFile, 
			final String groupIdAttributeNameOnDataOfGroupEntity, final String groupIdAttributeNameOnDataOfComponentEntity) throws GenstarException {
		
		IPopulation groupPopulation = GenstarUtils.loadSinglePopulation(populationType, groupPopulationName, groupAttributesFile, groupPopulationFile);
		IPopulation componentPopulation = GenstarUtils.loadSinglePopulation(populationType, componentPopulationName, componentAttributesFile, componentPopulationFile);
		
		AbstractAttribute groupIdAttributeOfGroupEntity = groupPopulation.getAttributeByNameOnData(groupIdAttributeNameOnDataOfGroupEntity);
		AbstractAttribute groupIdAttributeOfComponentEntity = componentPopulation.getAttributeByNameOnData(groupIdAttributeNameOnDataOfComponentEntity);
		
		return loadCompoundPopulation(populationType, groupPopulation, componentPopulation, groupIdAttributeOfGroupEntity, groupIdAttributeOfComponentEntity);
	}
	

	public static IPopulation loadCompoundPopulation(final PopulationType populationType, final IPopulation groupPopulation, final IPopulation componentPopulation,
			final AbstractAttribute groupIdAttributeOfGroupEntity, final AbstractAttribute groupIdAttributeOfComponentEntity) throws GenstarException {
		
		// parameters validation
		if (populationType == null || groupPopulation == null || componentPopulation == null || groupIdAttributeOfGroupEntity == null || groupIdAttributeOfComponentEntity == null) { 
			throw new GenstarException("No parameter can be null"); 
		}
		
		
		IPopulation compoundPopulation = new Population(populationType, groupPopulation.getName(), groupPopulation.getAttributes());
		compoundPopulation.addGroupReferences(groupPopulation.getGroupReferences());
		compoundPopulation.addComponentReferences(groupPopulation.getComponentReferences());
		
		List<Entity> groupSampleEntities = groupPopulation.getEntities();
		List<Entity> componentSampleEntities = componentPopulation.getEntities();
		
		
		Entity copyGroupSampleEntity;
		EntityAttributeValue groupIdEntityAttributeValue;
		AttributeValue groupIdAttributeValueOnGroupSampleEntity;
		
		for (Entity groupSampleEntity : groupSampleEntities) {
			copyGroupSampleEntity = compoundPopulation.createEntity(groupSampleEntity.getEntityAttributeValues());
			
			groupIdEntityAttributeValue = copyGroupSampleEntity.getEntityAttributeValue(groupIdAttributeOfGroupEntity);
			if (groupIdEntityAttributeValue == null) { throw new GenstarException("groupSampleEntity doesn't contain " + groupIdAttributeOfGroupEntity.getNameOnEntity() + " as ID attribute"); }
			
			groupIdAttributeValueOnGroupSampleEntity = groupIdEntityAttributeValue.getAttributeValueOnEntity();
			
			Map<AbstractAttribute, AttributeValue> componentMatchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
			componentMatchingCriteria.put(groupIdAttributeOfComponentEntity, groupIdAttributeValueOnGroupSampleEntity);
			
			List<Entity> matchedComponentEntities = new ArrayList<Entity>();
			for (Entity componentEntity : componentSampleEntities) {
				if (componentEntity.matchAttributeValuesOnEntity(componentMatchingCriteria)) { matchedComponentEntities.add(componentEntity);  }
			}
			
			// create component sample entities
			if (!matchedComponentEntities.isEmpty()) {
				List<List<EntityAttributeValue>> componentSampleEntityAVs = new ArrayList<List<EntityAttributeValue>>();
				for (Entity matchedComEntity : matchedComponentEntities) { componentSampleEntityAVs.add(matchedComEntity.getEntityAttributeValues()); }
				
				IPopulation copyComponentPopulation = copyGroupSampleEntity.createComponentPopulation(componentPopulation.getName(), componentPopulation.getAttributes());
				copyComponentPopulation.addGroupReferences(componentPopulation.getGroupReferences());
				copyComponentPopulation.addComponentReferences(componentPopulation.getComponentReferences());
				
				copyComponentPopulation.createEntities(componentSampleEntityAVs);

				componentSampleEntities.removeAll(matchedComponentEntities);
			}
		}
		 

		return compoundPopulation;
	}
	
	
	public static final Map<String, String> writePopulationToCsvFile(final IPopulation population, final Map<String, String> csvFilePathsByPopulationNames) throws GenstarException {
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
	
	private static final void buildCsvWriters(final IPopulation population, final Map<String, CsvWriter> csvWriters, 
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
				for (IPopulation componentPopulation : e.getComponentPopulations()) {
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
	
	private static final void doWritePopulationToCsvFile(final IPopulation population, final Map<String, CsvWriter> csvWriters) throws GenstarException {
		
		try {
			CsvWriter writer = csvWriters.get(population.getName());
			
			if (writer != null) {
				List<AbstractAttribute> attributes = population.getAttributes();
				String[] entityValues = new String[attributes.size()]; 
				
				// write population's entities to CSV file
				for (Entity e : population.getEntities()) {
					
					Map<AbstractAttribute, EntityAttributeValue> entityAttributeValuesMap = new HashMap<AbstractAttribute, EntityAttributeValue>();
					for (EntityAttributeValue eav : e.getEntityAttributeValues()) { entityAttributeValuesMap.put(eav.getAttribute(), eav); }
					
					
					for (int attrIndex=0; attrIndex<attributes.size(); attrIndex++) {
						entityValues[attrIndex] = entityAttributeValuesMap.get(attributes.get(attrIndex)).getAttributeValueOnEntity().toCsvString();
					}
					
					writer.writeRecord(entityValues);
					
					// recursively write entity's component populations
					for (IPopulation componentPopulation : e.getComponentPopulations()) {
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
	
	
	private static IPopulation generateGroupPopulation(final String populationName, final List<AbstractAttribute> groupAttributes, final AbstractAttribute groupIdAttributeOnGroupEntity, final int nbOfGroupEntities) throws GenstarException {
		IPopulation groupPopulation = new Population(PopulationType.SYNTHETIC_POPULATION, populationName, groupAttributes);
	
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
			groupAttributeValues.put(attr, new ArrayList<AttributeValue>(attr.valuesOnData())); 
			groupAttributeValueSizes.put(attr, attr.valuesOnData().size());
			valueOnEntityClasses.put(attr, attr.getValueClassOnEntity());
			
			if (attr.getValueClassOnData().equals(attr.getValueClassOnEntity())) { valueOnDataSameAsValueOnEntity.put(attr, true); }
			else { valueOnDataSameAsValueOnEntity.put(attr, false); }
		}

		// 2. generate entities
		Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>();
		int groupIdValue = 0;
		int groupColumns = groupAttributes.size();
		int valuesSize;
		List<AttributeValue> values;
		AttributeValue valueOnData;
		for (int i=0; i<nbOfGroupEntities; i++) {
			
			for (int col=0; col<groupColumns; col++) {
				if (col == groupIdAttributeIndexOnGroupEntity) { // ID attribute: value is computed automatically
					attributeValuesOnEntity.put(groupIdAttributeOnGroupEntity, new UniqueValue(DataType.INTEGER, Integer.toString(groupIdValue)));
					groupIdValue++; // increase group ID attribute value by 1
				} else {
					values = groupAttributeValues.get(groupAttributes.get(col));
					valuesSize = groupAttributeValueSizes.get(groupAttributes.get(col));
					valueOnData = values.get(SharedInstances.RandomNumberGenerator.nextInt(valuesSize));
					
					if (valueOnDataSameAsValueOnEntity.get(groupAttributes.get(col))) {
						attributeValuesOnEntity.put(groupAttributes.get(col), valueOnData);
					} else { // valueOnData != valueOnEntity
						attributeValuesOnEntity.put(groupAttributes.get(col), valueOnData.cast(valueOnEntityClasses.get(groupAttributes.get(col))));
					}
				}
			}
			
			groupPopulation.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity);
		}
		
		return groupPopulation;
	}

	
	private static void generateComponentPopulation(final IPopulation groupPopulation, final String componentPopulationName, 
			final List<AbstractAttribute> componentAttributes, final AbstractAttribute groupIdAttributeOnGroupEntity, 
			final AbstractAttribute groupIdAttributeOnComponentEntity, final AbstractAttribute groupSizeAttribute) throws GenstarException {
		
		// 1. cache attribute values and their positions for later use
		Map<AbstractAttribute, List<AttributeValue>> componentAttributeValuesWithoutGroupIdAttributeOnComponentEntity = new HashMap<AbstractAttribute, List<AttributeValue>>();
		Map<AbstractAttribute, Integer> componentAttributeValueSizes = new HashMap<AbstractAttribute, Integer>();
		Map<AbstractAttribute, Boolean> valueOnDataSameAsValueOnEntity = new HashMap<AbstractAttribute, Boolean>();
		Map<AbstractAttribute, Class> valueOnEntityClasses = new HashMap<AbstractAttribute, Class>();
		for (AbstractAttribute attr : componentAttributes) { 
			if (attr.equals(groupIdAttributeOnComponentEntity)) { continue; }
			
			componentAttributeValuesWithoutGroupIdAttributeOnComponentEntity.put(attr, new ArrayList<AttributeValue>(attr.valuesOnData())); 
			componentAttributeValueSizes.put(attr, attr.valuesOnData().size());
			valueOnEntityClasses.put(attr, attr.getValueClassOnEntity());
			
			if (attr.getValueClassOnData().equals(attr.getValueClassOnEntity())) { valueOnDataSameAsValueOnEntity.put(attr, true); }
			else { valueOnDataSameAsValueOnEntity.put(attr, false); }
		}

		int groupSize;
		List<AttributeValue> values;
		AttributeValue valueOnData;
		AttributeValue groupIDValue;
		int valuesSize;
		Map<AbstractAttribute, AttributeValue> attributeValuesOnComponentEntity = new HashMap<AbstractAttribute, AttributeValue>();
		for (Entity groupEntity : groupPopulation.getEntities()) {
			groupSize = ((UniqueValue) groupEntity.getEntityAttributeValueByNameOnData(groupSizeAttribute.getNameOnData()).getAttributeValueOnEntity()).getIntValue();
			groupIDValue = groupEntity.getEntityAttributeValueByNameOnData(groupIdAttributeOnGroupEntity.getNameOnData()).getAttributeValueOnEntity();
			
			if (groupSize > 0) {
				IPopulation componentPopulation = groupEntity.createComponentPopulation(componentPopulationName, componentAttributes);

				for (int i=0; i<groupSize; i++) {
					
					for (AbstractAttribute componentAttr : componentAttributes) {
						if (componentAttr.equals(groupIdAttributeOnComponentEntity)) { // component's groupID
							attributeValuesOnComponentEntity.put(groupIdAttributeOnComponentEntity, groupIDValue);
						} else { // other columns/fields
							values = componentAttributeValuesWithoutGroupIdAttributeOnComponentEntity.get(componentAttr);
							valuesSize = componentAttributeValueSizes.get(componentAttr);
							valueOnData = values.get(SharedInstances.RandomNumberGenerator.nextInt(valuesSize));
						
							if (valueOnDataSameAsValueOnEntity.get(componentAttr)) {
								attributeValuesOnComponentEntity.put(componentAttr, valueOnData);
								// componentEntity[col] = valueOnData.toCSVString();
							} else { // valueOnData != valueOnEntity
								attributeValuesOnComponentEntity.put(componentAttr, valueOnData.cast(valueOnEntityClasses.get(componentAttr)));
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
//				if (attr.containsInstanceOfAttributeValue(v)) { 
				if (attr.getInstanceOfAttributeValue(v) != null) { 
					retVal.put(attr, v); 
					concernedAttr = attr;
					break;
				}
			}
			copyAttributes.remove(concernedAttr);
		}
		
		return retVal;
	}
	

	public static Set<AttributeValuesFrequency> generateAttributeValuesFrequencies(final Set<AbstractAttribute> attributes) throws GenstarException {
		
		Set<AttributeValuesFrequency> avfs = new HashSet<AttributeValuesFrequency>();
		
		List<Set<AttributeValue>> attributesPossibleValues = new ArrayList<Set<AttributeValue>>();
		for (AbstractAttribute attr : attributes) { attributesPossibleValues.add(attr.valuesOnData()); }
		Set<List<AttributeValue>> cartesianValueSet = Sets.cartesianProduct(attributesPossibleValues);
		for (List<AttributeValue> cartesian : cartesianValueSet) {
			avfs.add(new AttributeValuesFrequency(GenstarUtils.buildAttributeValueMap(attributes, cartesian)));
		}
		
		return avfs;
	}
	
	
	public static Entity replicateSampleEntity(final Entity sourceSampleEntity, final IPopulation targetPopulation) throws GenstarException {
		Entity replicatedEntity = targetPopulation.createEntity(sourceSampleEntity.getEntityAttributeValues());
		
		List<IPopulation> sourceComponentPopulations = sourceSampleEntity.getComponentPopulations();
		for (IPopulation sourceComponentPop : sourceComponentPopulations) {
			IPopulation tagetComponentSamplePopulation = replicatedEntity.createComponentPopulation(sourceComponentPop.getName(), sourceComponentPop.getAttributes());
			tagetComponentSamplePopulation.addGroupReferences(sourceComponentPop.getGroupReferences());
			tagetComponentSamplePopulation.addComponentReferences(sourceComponentPop.getComponentReferences());
			
			for (Entity sourceComponentEntity : sourceComponentPop.getEntities()) {
				replicateSampleEntity(sourceComponentEntity, tagetComponentSamplePopulation);
			}
		}
		
		return replicatedEntity;
	}
	
	
	public static void transferData(final Entity source, final Entity target) throws GenstarException {
		
		// 1. transfer data from source to target Entity
		target.setEntityAttributeValues(source.getEntityAttributeValues());
		
		// 2. recursively, transfer source' components to target's components
		for (IPopulation sourceComponentPopulation : source.getComponentPopulations()) {
			
			IPopulation targetComponentPopulation = target.getComponentPopulation(sourceComponentPopulation.getName());
			if (targetComponentPopulation == null) {
				targetComponentPopulation = target.createComponentPopulation(sourceComponentPopulation.getName(), sourceComponentPopulation.getAttributes());
				targetComponentPopulation.addGroupReferences(sourceComponentPopulation.getGroupReferences());
				targetComponentPopulation.addComponentReferences(sourceComponentPopulation.getComponentReferences());
			}
			
			for (Entity sourceComponentSampleEntity : sourceComponentPopulation.getEntities()) {
				List<Entity> targetComponentEntities = targetComponentPopulation.createEntities(1);
				transferData(sourceComponentSampleEntity, targetComponentEntities.get(0));
			}
		}
	}
	
}