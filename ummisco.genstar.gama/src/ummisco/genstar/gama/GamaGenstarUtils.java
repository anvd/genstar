package ummisco.genstar.gama;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import com.google.common.collect.Sets;

import msi.gama.common.util.FileUtils;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.IpfGenerationRule;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.generation_rules.SampleFreeGenerationRule;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.Population;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.metamodel.sample_data.SampleData;
import ummisco.genstar.sample_free.AttributeInferenceGenerationRule;
import ummisco.genstar.sample_free.CustomSampleFreeGenerationRule;
import ummisco.genstar.sample_free.FrequencyDistributionGenerationRule;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.CsvWriter;
import ummisco.genstar.util.FrequencyDistributionUtils;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.GenstarUtils;
import ummisco.genstar.util.CSV_FILE_FORMATS;
import ummisco.genstar.util.IpfUtils;
import ummisco.genstar.util.IpuUtils;

public class GamaGenstarUtils {
	
	static IPopulation generateIpfPopulation(final IScope scope, final Properties ipfPopulationProperties) throws GenstarException {
		// 1. Read the ATTRIBUTES_PROPERTIES then create the generator
		String attributesCsvFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesCsvFilePath == null) { throw new GenstarException(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found "); }
		GenstarCsvFile attributesCsvFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, attributesCsvFilePath, true), true);
		SampleBasedGenerator generator = new SampleBasedGenerator("single rule generator");
		AttributeUtils.createAttributesFromCsvFile(generator, attributesCsvFile);
		
		// 2. Create the generation rule
		createIpfGenerationRule(scope, generator, "ipf generation rule", ipfPopulationProperties);
		
		return generator.generate();
	}
	
	
	static IPopulation generateIpuPopulation(final IScope scope, final Properties ipuPopulationProperties) throws GenstarException {
		
		// 1. Read the ATTRIBUTES_PROPERTIES then create the generator
		String groupAttributesCsvFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesCsvFilePath == null) { throw new GenstarException(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY + " property not found "); }
		GenstarCsvFile groupAttributesCsvFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupAttributesCsvFilePath, true), true);
		SampleBasedGenerator groupGenerator = new SampleBasedGenerator("single rule generator");
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesCsvFile);
		
		// 2. Create the generation rule
		createIpuGenerationRule(scope, groupGenerator, "ipu generation rule", ipuPopulationProperties);
		
		return groupGenerator.generate();
	}
	
	
	static IPopulation generateFrequencyDistributionPopulation(final IScope scope, final Properties frequencyDistributionPopulationProperties) throws GenstarException {
		
		// 1. Read the properties
		String populationName = frequencyDistributionPopulationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
		if (populationName == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + " property not found"); }
		
		String attributesCSVFilePath = frequencyDistributionPopulationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found"); }
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
		
		String generationRulesCSVFilePath = frequencyDistributionPopulationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.GENERATION_RULES_PROPERTY);
		if (generationRulesCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.GENERATION_RULES_PROPERTY + " property not found"); }
		GenstarCsvFile generationRulesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, generationRulesCSVFilePath, true), true);
		
		String nbOfEntitiesProperty = frequencyDistributionPopulationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.NUMBER_OF_ENTITIES_PROPERTY);
		if (nbOfEntitiesProperty == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.NUMBER_OF_ENTITIES_PROPERTY + " property not found"); }
		int nbOfEntities = 0;
		try {
			nbOfEntities = Integer.parseInt(nbOfEntitiesProperty);
		} catch (NumberFormatException nfe) {
			throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.NUMBER_OF_ENTITIES_PROPERTY + " property can not contain a negative integer.");
		}
		if (nbOfEntities <= 0) { throw new GenstarException("Value of " + nbOfEntitiesProperty + " property must be a positive integer"); }
		
		
		// 2. Create the generator
		SampleFreeGenerator generator = new SampleFreeGenerator("Population Generator", nbOfEntities);
		generator.setPopulationName(populationName);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
		List<GenstarCsvFile> generationRuleFiles = GamaGenstarUtils.createSampleFreeGenerationRules(scope, generator, generationRulesCSVFile);
		
		IPopulation population = generator.generate();
		
		
		// 3. perform the post generation analysis if necessary  
		String analysisResultOutputFolderPath = frequencyDistributionPopulationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.ANALYSIS_RESULT_OUTPUT_FOLDER_PROPERTY);
		if (analysisResultOutputFolderPath != null) {
			String reconstructedFolderPath = FileUtils.constructAbsoluteFilePath(scope, analysisResultOutputFolderPath, true);
			FrequencyDistributionUtils.analyseFrequencyDistributionPopulation(population, generationRuleFiles, reconstructedFolderPath);
		}
		
		
		// 4. Generate the population
		return population;
	}
	
	
	static String createFrequencyDistributionFromSampleDataOrPopulationFile(final IScope scope, final String attributesCsvFilePath, 
			final String sampleDataCsvFilePath, final String distributionFormatCsvFilePath, final String resultDistributionCsvFilePath) throws GenstarException {
//	TODO review this method due to two separate responsibilities
//		+ build the frequency distribution
//		+ save the frequency distribution to file
		
		
		try {
			// initialize CSV files
			GenstarCsvFile attributesCsvFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, attributesCsvFilePath, true), true);
			GenstarCsvFile sampleDataCsvFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, sampleDataCsvFilePath, true), true);
			GenstarCsvFile distributionFormatCsvFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, distributionFormatCsvFilePath, true), true);
			
			// 1. create the generator then add attributes
			SampleFreeGenerator generator = new SampleFreeGenerator("dummy generator", 10);
			AttributeUtils.createAttributesFromCsvFile(generator, attributesCsvFile);
			
			// 2. read the distribution file format to know the input and output attributes
			List<String> attributeNamesOnData = distributionFormatCsvFile.getHeaders();
			List<String> attributeNamesOnDataWithoutInputOutput = new ArrayList<String>();
			if (attributeNamesOnData.size() < 1) { throw new GenstarException("First line of distribution format file must contain at least 2 elements. File: " + distributionFormatCsvFile.getPath() + "."); }
			for (int index=0; index < attributeNamesOnData.size(); index++) {
				String attribute = attributeNamesOnData.get(index);
				StringTokenizer t = new StringTokenizer(attribute, CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE.ATTRIBUTE_NAME_TYPE_DELIMITER);
				if (t.countTokens() != 2) { throw new GenstarException("Element must have format attribute_name:Input or attribute_name:Output. File: " + distributionFormatCsvFile.getPath() + "."); }
				
				String attributeNameOnData = t.nextToken();
				if (generator.getAttributeByNameOnData(attributeNameOnData) == null) { throw new GenstarException("Attribute '" + attributeNameOnData + "' is not defined. File: " + distributionFormatCsvFile.getPath() + "."); }
				attributeNamesOnDataWithoutInputOutput.add(attributeNameOnData);
				
				String attributeType = t.nextToken();
				if (!attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE.INPUT_ATTRIBUTE) && !attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE.OUTPUT_ATTRIBUTE)) {
					throw new GenstarException("Attribute " + attributeNameOnData + " must either be Input or Output. File: " + distributionFormatCsvFile.getPath() + ".");
				}
			}
			
			// 3. create a frequency generation rule from the sample data then write the generation rule to file.
			FrequencyDistributionGenerationRule fdGenerationRule = FrequencyDistributionUtils.createFrequencyDistributionGenerationRuleFromSampleDataOrPopulationFile(generator, distributionFormatCsvFile, sampleDataCsvFile);
			
//			List<String[]> contents = new ArrayList<String[]>();
			List<List<String>> contents = new ArrayList<List<String>>();
			
			// write the header
			String[] generationRuleAttributeNamesArray = new String[attributeNamesOnData.size() + 1];
			attributeNamesOnData.toArray(generationRuleAttributeNamesArray);
			generationRuleAttributeNamesArray[generationRuleAttributeNamesArray.length - 1] = "Frequency";
//			contents.add(generationRuleAttributeNamesArray);
			contents.add(Arrays.asList(generationRuleAttributeNamesArray));
			
			
			List<AbstractAttribute> generationRuleAttributes = new ArrayList<AbstractAttribute>();
			for (String a : attributeNamesOnDataWithoutInputOutput) { generationRuleAttributes.add(generator.getAttributeByNameOnData(a)); }
			
			// sort the attributeValueFrequencies
			List<AttributeValuesFrequency> sortedAttributeValueFrequencies = new ArrayList<AttributeValuesFrequency>(fdGenerationRule.getAttributeValuesFrequencies());
			Collections.sort(sortedAttributeValueFrequencies, new GenstarUtils.AttributeValuesFrequencyComparator(generationRuleAttributes));
			
			
			for (AttributeValuesFrequency avf : sortedAttributeValueFrequencies) {
//				String[] row = new String[attributeNamesOnData.size() + 1];
				List<String> row = new ArrayList<String>();

				// build the string representation of each set of attribute values
				for (int i=0; i<generationRuleAttributes.size(); i++) {
					AttributeValue av = avf.getAttributeValueOnData(generationRuleAttributes.get(i));
//					row[i] = av.toCsvString();
					row.add(av.toCsvString());
				}
//				row[row.length - 1] = Integer.toString(avf.getFrequency());
				row.add(Integer.toString(avf.getFrequency()));
				
				contents.add(row);
			}
			
			// TODO delegate to GenstarUtils.writeStringContentToCsvFile
			// save the CSV file
			String exportFileName = FileUtils.constructAbsoluteFilePath(scope, resultDistributionCsvFilePath, false);
//			CsvWriter writer = new CsvWriter(exportFileName);
//			for ( String[] ss : contents ) { writer.writeRecord(ss); }
//			writer.close();
			
			GenstarUtils.writeStringContentToCsvFile(contents, exportFileName);
			
			return exportFileName;
		} catch (Exception e) {
			throw new GenstarException(e);
		}
	}
	
	
	
	static IPopulation generateRandomSinglePopulation(final IScope scope, final Properties populationProperties) throws GenstarException {
		// 1. Read the properties
		String populationName = populationProperties.getProperty(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
		if (populationName == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + " property not found"); }
		
		String attributesFileProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesFileProperty == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found"); }
		GenstarCsvFile attributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, attributesFileProperty, true), true);
		
		String nbOfEntitiesProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.NB_OF_ENTITIES_PROPERTY);
		if (nbOfEntitiesProperty == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.NB_OF_ENTITIES_PROPERTY + " property not found"); }
		int nbOfEntities = Integer.parseInt(nbOfEntitiesProperty);
		if (nbOfEntities <= 0) { throw new GenstarException("Value of " + nbOfEntitiesProperty + " property must be a positive integer"); }
		
		String idAttributeNameOnData = populationProperties.getProperty(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.ID_ATTRIBUTE_PROPERTY);

		
		// 2. Generate the population
		return GenstarUtils.generateRandomSinglePopulation(populationName, attributesFile, idAttributeNameOnData, nbOfEntities);
	}
	
	
	static IPopulation generateRandomCompoundPopulation(final IScope scope, final Properties populationProperties) throws GenstarException {
		// 1. Read the properties
		String groupPopulationName = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY);
		if (groupPopulationName == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY + " property not found"); }
		
		String groupAttributesFileProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesFileProperty == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY + " property not found "); }
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupAttributesFileProperty, true), true);
		
		String componentPopulationName = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
		if (componentPopulationName == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY + " property not found"); }
		
		String componentAttributesFileProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFileProperty == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY ); }
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentAttributesFileProperty, true), true);
		

		String groupIdAttributeNameOnGroupEntity = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
		if (groupIdAttributeNameOnGroupEntity == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + " property not found"); }
		
		String groupIdAttributeNameOnComponentEntity = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
		if (groupIdAttributeNameOnComponentEntity == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + " property not found"); }
		
		String groupSizeAttributeName = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_SIZE_ATTRIBUTE_PROPERTY);
		if (groupSizeAttributeName == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_SIZE_ATTRIBUTE_PROPERTY + " property not found"); }
		
		String componentReferenceOnGroup = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_REFERENCE_ON_GROUP_PROPERTY);
		String groupReferenceOnComponent = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_REFERENCE_ON_COMPONENT_PROPERTY);
				
		
		String nbOfGroupEntitiesProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.NB_OF_GROUP_ENTITIES_PROPERTY);
		if (nbOfGroupEntitiesProperty != null) {
			int nbOfGroupEntities = Integer.parseInt(nbOfGroupEntitiesProperty);
			if (nbOfGroupEntities <= 0) { throw new GenstarException("Value of " + nbOfGroupEntitiesProperty + " property must be a positive integer"); }
			
			// 2. Generate the population
			return GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, componentAttributesFile, 
					groupIdAttributeNameOnGroupEntity, groupIdAttributeNameOnComponentEntity, groupSizeAttributeName, nbOfGroupEntities, componentReferenceOnGroup, groupReferenceOnComponent);
		} else {
			String minGroupEntitiesOfEachAttributeValuesSetProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.MIN_GROUP_ENTITIES_OF_EACH_ATTRIBUTE_VALUES_SET_PROPERTY);
			String maxGroupEntitiesOfEachAttributeValuesSetProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.MAX_GROUP_ENTITIES_OF_EACH_ATTRIBUTE_VALUES_SET_PROPERTY);
			
			if (minGroupEntitiesOfEachAttributeValuesSetProperty == null || maxGroupEntitiesOfEachAttributeValuesSetProperty == null) {
				throw new GenstarException("If " + PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.NB_OF_GROUP_ENTITIES_PROPERTY + " is null then neither "
						+ PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.MIN_GROUP_ENTITIES_OF_EACH_ATTRIBUTE_VALUES_SET_PROPERTY + " nor "
						+ PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.MAX_GROUP_ENTITIES_OF_EACH_ATTRIBUTE_VALUES_SET_PROPERTY + " property can be null");
			}
			
			int minGroupEntitiesOfEachAttributeValuesSet = Integer.parseInt(minGroupEntitiesOfEachAttributeValuesSetProperty);
			int maxGroupEntitiesOfEachAttributeValuesSet = Integer.parseInt(maxGroupEntitiesOfEachAttributeValuesSetProperty);
			
			// 2. Generate the population 
			return GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, componentAttributesFile, 
					groupIdAttributeNameOnGroupEntity, groupIdAttributeNameOnComponentEntity, groupSizeAttributeName, 
					minGroupEntitiesOfEachAttributeValuesSet, maxGroupEntitiesOfEachAttributeValuesSet, componentReferenceOnGroup, groupReferenceOnComponent);
		}
		
	}
	
	
	// TODO check duplication with GamaGenstarUtils.createFrequencyDistributionFromSample
	static String generateControlTotals(final IScope scope, final String controlTotalPropertiesFilePath, final String resultControlsTotalFilePath) throws GenstarException {
		// change method name to "generateIpfControlTotals"
		// delegate to IpfUtils.parseIpfControlTotalsFile

		
		try {
			// 0. Load the property file
			Properties controlTotalProperties = null;
			File propertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, controlTotalPropertiesFilePath, true));
			try {
				FileInputStream propertyInputStream = new FileInputStream(propertiesFile);
				controlTotalProperties = new Properties();
				controlTotalProperties.load(propertyInputStream);
			} catch (FileNotFoundException e) {
				throw new GenstarException(e);
			} catch (IOException e) {
				throw new GenstarException(e);
			}
			
			
			// 1. initialize CSV files
			String attributesCSVFilePath = controlTotalProperties.getProperty(PROPERTY_FILES.CONTROL_TOTALS_PROPERTIES.ATTRIBUTES_PROPERTY);
			if (attributesCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.CONTROL_TOTALS_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found in " + controlTotalPropertiesFilePath); }
			GenstarCsvFile attributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			
			String idAttributeNameOnData = controlTotalProperties.getProperty(PROPERTY_FILES.CONTROL_TOTALS_PROPERTIES.ID_ATTRIBUTE_PROPERTY);
			
			String controlAttributesCSVFilePath = controlTotalProperties.getProperty(PROPERTY_FILES.CONTROL_TOTALS_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
			if (controlAttributesCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.CONTROL_TOTALS_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY + " property not found in " + controlTotalPropertiesFilePath); }
			GenstarCsvFile controlAttributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlAttributesCSVFilePath, true), false);
			
			String populationCSVFilePath = controlTotalProperties.getProperty(PROPERTY_FILES.CONTROL_TOTALS_PROPERTIES.POPULATION_DATA_PROPERTY);
			if (populationCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.CONTROL_TOTALS_PROPERTIES.POPULATION_DATA_PROPERTY + " property not found in " + controlTotalPropertiesFilePath); }
			GenstarCsvFile populationCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, populationCSVFilePath, true), true);
			
			int controlAttributesRows = controlAttributesCSVFile.getRows();
			if (controlAttributesCSVFile.getColumns() != 1 || controlAttributesRows < 2) {
				throw new GenstarException("Invalid controlAttributesCSVFile format. File: " + controlAttributesCSVFile);
			}
			Set<String> controlledAttributeNames = new HashSet<String>();
			for (List<String> row : controlAttributesCSVFile.getContent()) { controlledAttributeNames.add(row.get(0)); }
			
			// 2. create the generator then add attributes
			ISyntheticPopulationGenerator generator = new SampleFreeGenerator("dummy generator", 10);
			AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
			
			// 3. process ID attribute
			if (idAttributeNameOnData != null) {
				AbstractAttribute idAttribute = generator.getAttributeByNameOnData(idAttributeNameOnData);
				if (idAttribute == null) { throw new GenstarException(idAttributeNameOnData + " is not recognized as an attribute a valid"); }
			}
			
			// 4. ensure that all controlled attributes exist
			for (String attributeNameOnData : controlledAttributeNames) { 
				if (generator.getAttributeByNameOnData(attributeNameOnData) == null) {
					throw new GenstarException(attributeNameOnData + " is not a valid attribute. File: " + controlAttributesCSVFilePath);
				}
			}

			// 5. only retain the "valid" controlledAttributeNameSets ("valid" means what?)
			int controlTotalsAttributeNumbers = controlAttributesRows - 1;
			Set<Set<String>> controlledAttributeNameSets = Sets.powerSet(controlledAttributeNames);
			Set<Set<String>> validControlledAttributeNameSets = new HashSet<Set<String>>();
			for (Set<String> candidate : controlledAttributeNameSets) {
				if (candidate.size() == controlTotalsAttributeNumbers) { validControlledAttributeNameSets.add(candidate); }
			}
			
			// 6. build a list of AttributeValueFrequencies
			List<AttributeValuesFrequency> attributeValuesFrequencies = new ArrayList<AttributeValuesFrequency>();
			for (Set<String> controlledAttributeName : validControlledAttributeNameSets) {
				Set<AbstractAttribute> controlledAttributes = new HashSet<AbstractAttribute>();
				List<Set<AttributeValue>> attributesPossibleValues = new ArrayList<Set<AttributeValue>>();
				
				for (String attributeNameOnData : controlledAttributeName) {
					AbstractAttribute controlledAttribute = generator.getAttributeByNameOnData(attributeNameOnData); 
					controlledAttributes.add(controlledAttribute);
					attributesPossibleValues.add(controlledAttribute.valuesOnData());
				}
				
				Set<List<AttributeValue>> cartesianSet = Sets.cartesianProduct(attributesPossibleValues);

				for (List<AttributeValue> cartesian : cartesianSet) {
					attributeValuesFrequencies.add(new AttributeValuesFrequency(GenstarUtils.buildAttributeValueMap(controlledAttributes, cartesian)));
				}
			}
			
			// 7. calculate frequencies
			SampleData populationData = new SampleData("dummy population", generator.getAttributes(), populationCSVFile);
			List<Entity> sampleEntities = populationData.getSampleEntityPopulation().getEntities();
			Map<Integer, List<AbstractAttribute>> cachedAvfAttributes = new HashMap<Integer, List<AbstractAttribute>>();
			Map<Integer, AttributeValuesFrequency> cachedAvfs = new HashMap<Integer, AttributeValuesFrequency>();
			for (int index=0; index<attributeValuesFrequencies.size(); index++) {
				cachedAvfAttributes.put(index, new ArrayList<AbstractAttribute>(attributeValuesFrequencies.get(index).getAttributes()));
				cachedAvfs.put(index, attributeValuesFrequencies.get(index));
			}
			
			// TODO improve this loop be calling ?population.getMatchingEntitiesByAttributeValuesOnEntity? or ?population.getMatchingEntitiesByAttributeValuesOnData?
			for (int index=0; index<attributeValuesFrequencies.size(); index++) {
				List<AbstractAttribute> avfAttributes = new ArrayList<AbstractAttribute>(cachedAvfAttributes.get(index));
				
				for (Entity sampleEntity : sampleEntities) {
					AttributeValuesFrequency avf = cachedAvfs.get(index);
					if (avf.matchAttributeValuesOnData(sampleEntity.getAttributesValuesOnData(avfAttributes))) { avf.increaseFrequency(); }
				}
			}
			
			// 8. build CSV file content
			List<String[]> csvFileContents = new ArrayList<String[]>();
			for (AttributeValuesFrequency avf : attributeValuesFrequencies) {
				String[] row = new String[(avf.getAttributes().size() * 2) + 1];

				// build the string representation of each set of attribute values
				int i=0;
				for (Map.Entry<AbstractAttribute, AttributeValue> entry : avf.getAttributeValuesOnData().entrySet()) {
					row[i] = entry.getKey().getNameOnData();
					i++;
					row[i] = entry.getValue().toCsvString(); // BUG different types of attribute values???
					i++;
				}
				
				row[row.length - 1] = Integer.toString(avf.getFrequency());
				
				csvFileContents.add(row);
			}
			
			// 9. write CSV file
			String exportFileName = FileUtils.constructAbsoluteFilePath(scope, resultControlsTotalFilePath, false);
			CsvWriter writer = new CsvWriter(exportFileName);
			for ( String[] ss : csvFileContents ) { writer.writeRecord(ss); }
			writer.close();
			
			return exportFileName;
		} catch (Exception e) {
			throw new GenstarException(e);
		}
	}
	
	
	static void createIpfGenerationRule(final IScope scope, final SampleBasedGenerator generator, final String ruleName, final Properties ipfPopulationProperties) throws GenstarException {
		
		// Read the  properties
		
		// POPULATION_NAME_PROPERTY
		String populationName = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
		if (populationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + "' not found in the property file."); };
		generator.setPopulationName(populationName);
		
		// SAMPLE_DATA_PROPERTY
		String sampleDataFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SAMPLE_DATA_PROPERTY);
		if (sampleDataFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile sampleCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, sampleDataFilePath, true), true);
		
		// CONTROLLED_ATTRIBUTES_PROPERTY 
		String controlledAttributesFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (controlledAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile controlledAttributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlledAttributesFilePath, true), false);
		
		// CONTROL_TOTALS_PROPERTY
		String controlledTotalsFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROL_TOTALS_PROPERTY);
		if (controlledTotalsFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROL_TOTALS_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile controlledTotalsCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlledTotalsFilePath, true), false);
		
		// SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String supplementaryAttributesFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (supplementaryAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SUPPLEMENTARY_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile supplementaryAttributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, supplementaryAttributesFilePath, true), false);
		
		// MAX_ITERATIONS_PROPERTY
		String maxIterationsValue = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.MAX_ITERATIONS_PROPERTY);
		int maxIterations = IpfGenerationRule.DEFAULT_MAX_ITERATIONS;
		if (maxIterationsValue != null) { maxIterations = Integer.parseInt(maxIterationsValue); }
		

		// COMPONENT_POPULATION_PROPERTY.
		String componentPopulationName = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);

		// compound population (with component populations)
		if (componentPopulationName != null) { // If COMPONENT_POPULATION_PROPERTY exists, then this is a compound sample data
			
			// COMPONENT_REFERENCE_ON_GROUP_PROPERTY
			String componentReferenceOnGroup = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_REFERENCE_ON_GROUP_PROPERTY);
			
			// COMPONENT_SAMPLE_DATA_PROPERTY
			String componentSampleDataFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY);
			if (componentSampleDataFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
			GenstarCsvFile componentSampleDataFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentSampleDataFilePath, true), true);
			
			// COMPONENT_ATTRIBUTES_PROPERTY
			String componentAttributesFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
			if (componentAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
			GenstarCsvFile componentAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true), true);
			 
			// GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY
			String groupIdAttributeNameOnDataOfGroupEntity = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
			if (groupIdAttributeNameOnDataOfGroupEntity == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + "' not found in the property file."); }
			 
			// GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY
			String groupIdAttributeNameOnDataOfComponentEntity = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
			if (groupIdAttributeNameOnDataOfComponentEntity == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + "' not found in the property file."); }
			 
			
			// GROUP_REFERENCE_ON_COMPONENT_PROPERTY
			String groupReferenceOnComponent = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.GROUP_REFERENCE_ON_COMPONENT_PROPERTY);
			
			
			IpfUtils.createCompoundIpfGenerationRule(generator, ruleName, 
					sampleCSVFile, controlledAttributesCSVFile, controlledTotalsCSVFile, supplementaryAttributesCSVFile, componentReferenceOnGroup, groupIdAttributeNameOnDataOfGroupEntity, 
					componentSampleDataFile, componentAttributesFile, componentPopulationName, groupReferenceOnComponent, groupIdAttributeNameOnDataOfComponentEntity,
					maxIterations);
			
		} else { //single population (without component populations)
			IpfUtils.createIpfGenerationRule(generator, ruleName, sampleCSVFile, controlledAttributesCSVFile, controlledTotalsCSVFile, supplementaryAttributesCSVFile, maxIterations);
		}
		
	}

	
	static void createIpuGenerationRule(final IScope scope, final SampleBasedGenerator groupGenerator, final String ruleName, final Properties ipuPopulationProperties) throws GenstarException {

		// Read the  properties
		
		// GROUP_ID_ATTRIBUTE
		AbstractAttribute groupIdAttributeOfGroupEntity = null;
		String groupIdAttributeNameOnDataOfGroupEntity = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
		if (groupIdAttributeNameOnDataOfGroupEntity != null) {
			groupIdAttributeOfGroupEntity = groupGenerator.getAttributeByNameOnData(groupIdAttributeNameOnDataOfGroupEntity);
			if (groupIdAttributeOfGroupEntity == null) { throw new GenstarException(groupIdAttributeNameOnDataOfGroupEntity + " is not recognized as an attribute on the generator"); }
		} else {
			throw new GenstarException(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + " property not found");
		}
		
		// GROUP_POPULATION_NAME_PROPERTY
		String groupPopulationName = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY);
		if (groupPopulationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY + "' not found in the property file."); };
		groupGenerator.setPopulationName(groupPopulationName);
		
		// GROUP_SAMPLE_DATA_PROPERTY
		String groupSampleDataFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SAMPLE_DATA_PROPERTY);
		if (groupSampleDataFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupSampleDataFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupSampleDataFilePath, true), true);
		
		// GROUP_CONTROLLED_ATTRIBUTES_PROPERTY 
		String groupControlledAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (groupControlledAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupControlledAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupControlledAttributesFilePath, true), false);
		
		// GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY
		String groupIdAttributeNameOnGroup = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
		if (groupIdAttributeNameOnGroup == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + "' not found in the property file."); }

		// GROUP_CONTROL_TOTALS_PROPERTY
		String groupControlTotalsFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROL_TOTALS_PROPERTY);
		if (groupControlTotalsFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROL_TOTALS_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupControlledTotalsFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupControlTotalsFilePath, true), false);
		
		// GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String groupSupplementaryAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (groupSupplementaryAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupSupplementaryAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupSupplementaryAttributesFilePath, true), false);
		 

		// COMPONENT_POPULATION_NAME_PROPERTY
		String componentPopulationName = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
		if (componentPopulationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY + "' not found in the property file."); };
		
		// COMPONENT_SAMPLE_DATA_PROPERTY
		String componentSampleDataFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY);
		if (componentSampleDataFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentSampleDataFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentSampleDataFilePath, true), true);
		
		// COMPONENT_ATTRIBUTES_PROPERTY
		String componentAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true), true);
		 
		// COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY
		String componentControlledAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (componentControlledAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentControlledAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentControlledAttributesFilePath, true), false);
		
		// COMPONENT_CONTROL_TOTALS_PROPERTY
		String componentControlTotalsFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROL_TOTALS_PROPERTY);
		if (componentControlTotalsFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROL_TOTALS_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentControlTotalsFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentControlTotalsFilePath, true), false);
		
		
		// GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY
		String groupIdAttributeNameOnComponent = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
		if (groupIdAttributeNameOnComponent == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + "' not found in the property file."); }
		 
		// COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String componentSupplementaryAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (componentSupplementaryAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentSupplementaryAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentSupplementaryAttributesFilePath, true), false);
				
		
		// MAX_ITERATIONS_PROPERTY
		String maxIterationsValue = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.MAX_ITERATIONS_PROPERTY);
		int maxIterations = IpfGenerationRule.DEFAULT_MAX_ITERATIONS;
		if (maxIterationsValue != null) { maxIterations = Integer.parseInt(maxIterationsValue); }

		
		// COMPONENT_REFERENCE_ON_GROUP
		String componentReferenceOnGroup = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_REFERENCE_ON_GROUP_PROPERTY);
		
		// GROUP_REFERENCE_ON_COMPONENT
		String groupReferenceOnComponent = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_REFERENCE_ON_COMPONENT_PROPERTY);
		
		
		IpuUtils.createIpuGenerationRule(groupGenerator, 
			groupSampleDataFile, groupIdAttributeNameOnGroup,  groupControlledAttributesFile, groupControlledTotalsFile, groupSupplementaryAttributesFile, componentReferenceOnGroup,
			componentAttributesFile, componentPopulationName, componentSampleDataFile, groupIdAttributeNameOnComponent, componentControlledAttributesFile, componentControlTotalsFile, componentSupplementaryAttributesFile, groupReferenceOnComponent,
			maxIterations);
	}
	
	
	public static List<GenstarCsvFile> createSampleFreeGenerationRules(final IScope scope, final SampleFreeGenerator generator, final GenstarCsvFile sampleFreeGenerationRulesListFile) throws GenstarException {
		
		List<GenstarCsvFile> generationRuleFiles = new ArrayList<GenstarCsvFile>();
		
		List<List<String>> fileContent = sampleFreeGenerationRulesListFile.getContent();
		if ( fileContent == null || fileContent.isEmpty() ) { throw new GenstarException("Invalid Generation Rule file: content is empty (file: " + sampleFreeGenerationRulesListFile.getPath()  + ")"); }
		int rows = fileContent.size();

		if (sampleFreeGenerationRulesListFile.getColumns() != CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.NB_OF_COLS) { throw new GenstarException("CVS file must have " + CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.NB_OF_COLS + " columns, (file: " + sampleFreeGenerationRulesListFile.getPath() + ")"); }

		// 1. Parse the header
		List<String> header = sampleFreeGenerationRulesListFile.getHeaders();
		for (int i=0; i<header.size(); i++) {
			if (!header.get(i).equals(CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.HEADERS[i])) {
				throw new GenstarException("Invalid Generation Rule file header. Header must be " + CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.HEADER_STR + " (file: " + sampleFreeGenerationRulesListFile.getPath() + ")");
			}
		}
		
		
		// 2. Parse and initialize distributions
		for ( int rowIndex = 0; rowIndex < rows; rowIndex++ ) {
			
			final List<String> generationRuleInfo = fileContent.get(rowIndex);
			if (generationRuleInfo.size() != CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.NB_OF_COLS) { throw new GenstarException("Invalid Generation Rule file format: each row must have " +  CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.NB_OF_COLS + " columns, (file: " + sampleFreeGenerationRulesListFile.getPath() + ")"); }
			
			String ruleName = (String)generationRuleInfo.get(0);
			String ruleDataFilePathOrJavaClass = (String)generationRuleInfo.get(1);
			String ruleTypeName = (String)generationRuleInfo.get(2);
			GenstarCsvFile ruleDataFile = null;
			
			// initialize the (sample free) data file
			if (!ruleTypeName.equals(CustomSampleFreeGenerationRule.RULE_TYPE_NAME)) {
				
				if (ruleTypeName.equals(IpfGenerationRule.RULE_TYPE_NAME)) { throw new GenstarException("Unsupported generation rule type: " + IpfGenerationRule.RULE_TYPE_NAME); }
				
				ruleDataFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, ruleDataFilePathOrJavaClass, true), true); // Frequency Distribution or Attribute Inference
			}
			
			
			if (ruleTypeName.equals(FrequencyDistributionGenerationRule.RULE_TYPE_NAME)) {
				FrequencyDistributionUtils.createFrequencyDistributionGenerationRule(generator, ruleName, ruleDataFile);
				generationRuleFiles.add(ruleDataFile); // ATTENTION: only save FrequencyDistributionGenerationRule file
			} else if (ruleTypeName.equals(AttributeInferenceGenerationRule.RULE_TYPE_NAME)) {
				GenstarUtils.createAttributeInferenceGenerationRule(generator, ruleName, ruleDataFile);
			} else if (ruleTypeName.equals(CustomSampleFreeGenerationRule.RULE_TYPE_NAME)) { 
				// ATTENTION: not "save" custom generation rule
				GenstarUtils.createCustomGenerationRule(generator, ruleName, ruleDataFilePathOrJavaClass);
			} else {
				throw new GenstarException("Unsupported generation rule (" + ruleTypeName + "), file: " + sampleFreeGenerationRulesListFile.getPath());
			}
		}
		
		return generationRuleFiles;
	}
	
	
	public static IPopulation extractIpuPopulation(final IScope scope, final Properties ipuSourcePopulationProperties) throws GenstarException {
		
		// 0. Read the properties
		
		// GROUP_ATTRIBUTES_PROPERTY
		String groupAttributesFilePath = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupAttributesFilePath, true), true);
		
		// GROUP_POPULATION_NAME_PROPERTY
		String groupPopulationName = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY);
		if (groupPopulationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY + "' not found in the property file."); }
		
		// GROUP_POPULATION_DATA_PROPERTY
		String groupPopulationFilePath = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY);
		if (groupPopulationFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupPopulationFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupPopulationFilePath, true), true);
		
		// GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY
		String groupIdAttributeNameOnDataOfGroupEntity = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
		if (groupIdAttributeNameOnDataOfGroupEntity == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + "' not found in the property file."); }
		
		
		// COMPONENT_ATTRIBUTES_PROPERTY
		String componentAttributesFilePath =  ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true), true);
		
		// COMPONENT_POPULATION_NAME_PROPERTY
		String componentPopulationName = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
		if (componentPopulationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY + "' not found in the property file."); }
		
		// COMPONENT_POPULATION_DATA_PROPERTY
		String componentPopulationFilePath = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY);
		if (componentPopulationFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentPopulationFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentPopulationFilePath, true), true);
		
		// GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY
		String groupIdAttributeNameOnDataOfComponentEntity = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
		if (groupIdAttributeNameOnDataOfComponentEntity == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + "' not found in the property file."); }
		
		
		// COMPONENT_REFERENCE_ON_GROUP_PROPERTY
		String componentReferenceOnGroup = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_REFERENCE_ON_GROUP_PROPERTY);
		
		// GROUP_REFERENCE_ON_COMPONENT_PROPERTY
		String groupReferenceOnComponent = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_REFERENCE_ON_COMPONENT_PROPERTY);
		
		
		IPopulation originalCompoundPopulation = GenstarUtils.loadCompoundPopulation(PopulationType.SYNTHETIC_POPULATION, 
				groupPopulationName, groupAttributesFile, groupPopulationFile, componentPopulationName, 
				componentAttributesFile, componentPopulationFile, groupIdAttributeNameOnDataOfGroupEntity, groupIdAttributeNameOnDataOfComponentEntity,
				componentReferenceOnGroup, groupReferenceOnComponent);
		
		// PERCENTAGE_PROPERTY
		String percentageProperty = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.PERCENTAGE_PROPERTY);
		if (percentageProperty == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.PERCENTAGE_PROPERTY + "' not found in the property file."); }
		float percentage = 0;
		try {
			percentage = Float.parseFloat(percentageProperty);
		} catch (NumberFormatException nfe) {
			throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.PERCENTAGE_PROPERTY + "' is not a float number");
		}
		
		// GROUP_CONTROLLED_ATTRIBUTES_PROPERTY
		String ipuGroupControlledFilePath = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (ipuGroupControlledFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile ipuGroupControlledAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, ipuGroupControlledFilePath, true), false);
		
		// 1. parse controlled attributes
		Set<AbstractAttribute> ipuControlledAttributes = new HashSet<AbstractAttribute>();
		for (List<String> row : ipuGroupControlledAttributesFile.getContent()) {
			for (String attributeNameOnData : row) {
				AbstractAttribute attr = originalCompoundPopulation.getAttributeByNameOnData(attributeNameOnData);
				if (attr == null) { throw new GenstarException("Attribute '" + attributeNameOnData + "' defined in " + ipuGroupControlledFilePath + "not found in the population"); }
				ipuControlledAttributes.add(attr);
			}
		}

		
		// 2. extract the sample population
		return IpuUtils.extractIpuPopulation(originalCompoundPopulation, percentage, ipuControlledAttributes);
	}
	
	
	public static IPopulation loadSinglePopulation(final IScope scope, final Properties singlePopulationProperties) throws GenstarException {
		
		// 0. Read the properties
		String populationName = singlePopulationProperties.getProperty(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
		if (populationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + "' not found in the property file."); }
		
		String attributesFileProperty = singlePopulationProperties.getProperty(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesFileProperty == null) { throw new GenstarException(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found in the property file."); }
		GenstarCsvFile attributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, attributesFileProperty, true), true);
		
		String populationCSVFilePath = singlePopulationProperties.getProperty(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.POPULATION_DATA_PROPERTY);
		if (populationCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.POPULATION_DATA_PROPERTY + " property not found in the property file."); }
		GenstarCsvFile populationCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, populationCSVFilePath, true), true);

		
		// 1. load the single population
		return GenstarUtils.loadSinglePopulation(PopulationType.SYNTHETIC_POPULATION, populationName, attributesFile, populationCSVFile);
	}
	
	
	public static IPopulation loadCompoundPopulation(final IScope scope, final Properties compoundPopulationProperties) throws GenstarException {
		
		// 0. Read the properties
		
		String groupPopulationName = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY);
		if (groupPopulationName == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY + " property not found in the property file."); }
		
		String groupAttributesFileProperty = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesFileProperty == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY + " property not found in the property file."); }
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupAttributesFileProperty, true), true);
		
		String groupPopulationCSVFilePath = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY);
		if (groupPopulationCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY + " property not found in the property file."); }
		GenstarCsvFile groupPopulationFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupPopulationCSVFilePath, true), true);
		
		
		String componentPopulationName = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
		if (componentPopulationName == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY + " property not found in the property file."); }
		
		String componentAttributesFileProperty = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFileProperty == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY ); }
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentAttributesFileProperty, true), true);
		
		String componentPopulationCSVFilePath = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY);
		if (componentPopulationCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY + " property not found in the property file."); }
		GenstarCsvFile componentPopulationFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentPopulationCSVFilePath, true), true);
		

		String groupIdAttributeNameOnDataOfGroupEntity = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
		if (groupIdAttributeNameOnDataOfGroupEntity == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + " property not found in the property file."); }
		
		String groupIdAttributeNameOnDataOfComponentEntity = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
		if (groupIdAttributeNameOnDataOfComponentEntity == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + " property not found in the property file."); }
		
		
		String componentReferenceOnGroup = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_REFERENCE_ON_GROUP_PROPERTY);
		String groupReferenceOnComponent = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_REFERENCE_ON_COMPONENT_PROPERTY);
		 
		

		// 1. load the compound population
		return GenstarUtils.loadCompoundPopulation(PopulationType.SYNTHETIC_POPULATION, groupPopulationName, groupAttributesFile, groupPopulationFile, 
				componentPopulationName, componentAttributesFile, componentPopulationFile, 
				groupIdAttributeNameOnDataOfGroupEntity, groupIdAttributeNameOnDataOfComponentEntity,
				componentReferenceOnGroup, groupReferenceOnComponent);
	}


	public static void analyseFrequencyDistributionPopulation(final IScope scope, final IList population, final Properties populationProperties) {
		
	}

	
	public static IPopulation convertGamaPopulationToGenstarPopulation(final IScope scope, final IList gamaPopulation, final Map<String, String> populationAttributesFilePathsByPopulationNames) throws GenstarException {
		
		// build population attributes
		Map<String, List<AbstractAttribute>> populationAttributes = new HashMap<String, List<AbstractAttribute>>();
		for (String populationName : populationAttributesFilePathsByPopulationNames.keySet()) {
			ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
			
			GenstarCsvFile attributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, populationAttributesFilePathsByPopulationNames.get(populationName), true), true);
			AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
			
			populationAttributes.put(populationName, generator.getAttributes());
		}
		
		return convertGamaPopulationToGenstarPopulation(null, gamaPopulation, populationAttributes);
	}

	
	private static IPopulation convertGamaPopulationToGenstarPopulation(final Entity host, final IList gamaPopulation, 
			final Map<String, List<AbstractAttribute>> populationsAttributes) throws GenstarException {
		if (gamaPopulation == null) { throw new GenstarException("Parameter gamaPopulation can not be null"); }
		if (gamaPopulation.size() < 3) { throw new GenstarException("gamaPopulation is not a valid gama synthetic population format"); }
		if (populationsAttributes == null) { throw new GenstarException("Parameter populationsAttributes can not be null"); }
		
		// 1. First three elements of a GAMA synthetic population
		String populationName =  (String)gamaPopulation.get(0); // first element is the population name
		Map<String, String> groupReferences = (Map<String, String>)gamaPopulation.get(1); // second element contains references to "group" agents
		Map<String, String> componentReferences = (Map<String, String>)gamaPopulation.get(2); // third element contains references to "component" agents
		// what to do with group and component references?
		
		IPopulation genstarPopulation = null; // 2. create the genstar population appropriately
		if (host == null) {
			genstarPopulation = new Population(PopulationType.SYNTHETIC_POPULATION, populationName, populationsAttributes.get(populationName));
		} else {
			genstarPopulation = host.createComponentPopulation(populationName, populationsAttributes.get(populationName));
		}
		
		// 3. extract GAMA init values
		IList<GamaMap> gamaInits = GamaListFactory.create();
		gamaInits.addAll(gamaPopulation.subList(3, gamaPopulation.size()));

		// 4. convert entities (each entity is a map)
		for (GamaMap gamaEntityInitValues : gamaInits) {
			
			// extract the initial values
			GamaMap<String, String> mirrorGamaEntityInitValues = null;
			IList<IList> gamaComponentPopulations = (IList<IList>) gamaEntityInitValues.get(IPopulation.class);
			if (gamaComponentPopulations == null) { // without Genstar component populations
				mirrorGamaEntityInitValues = gamaEntityInitValues;
			} else {
				mirrorGamaEntityInitValues = GamaMapFactory.create();
				mirrorGamaEntityInitValues.putAll(gamaEntityInitValues);
				mirrorGamaEntityInitValues.remove(IPopulation.class);
			}
			
			// convert string representation (of the initial values) to AttributeValue
			Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>();
			for (String attributeNameOnEntity : mirrorGamaEntityInitValues.keySet()) {
				attributeValuesOnEntity.put(genstarPopulation.getAttributeByNameOnEntity(attributeNameOnEntity), 
						GenstarGamaTypesConverter.convertGama2GenstarType(genstarPopulation.getAttributeByNameOnEntity(attributeNameOnEntity), 
								GenstarGamaTypesConverter.convertGamaAttributeValueToString(mirrorGamaEntityInitValues.get(attributeNameOnEntity) ) ) );
			}
			
			Entity genstarEntity = genstarPopulation.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity); // create the entity
			if (gamaComponentPopulations != null) { // recursively convert component populations
				for (IList gamaComponentPopulation : gamaComponentPopulations) {
					convertGamaPopulationToGenstarPopulation(genstarEntity, gamaComponentPopulation, populationsAttributes);
				}
			}
		}
		
		return genstarPopulation;
	}
	
	
	public static IList convertGenstarPopulationToGamaPopulation(final IPopulation genstarPopulation) throws GenstarException {
		IList gamaPopulation = GamaListFactory.create();
		
		// First three elements of a GAMA synthetic population
		gamaPopulation.add(genstarPopulation.getName()); // first element is the population name
		gamaPopulation.add(genstarPopulation.getGroupReferences()); // second element contains references to "group" agents
		gamaPopulation.add(genstarPopulation.getComponentReferences()); // third element contains references to "component" agents
		
		// Convert the genstar population to the format understood by GAML "genstar_create" statement
		GamaMap map;
		for (Entity entity : genstarPopulation.getEntities()) {
			map = GamaMapFactory.create();
			for (EntityAttributeValue eav : entity.getEntityAttributeValues()) {
				map.put(eav.getAttribute().getNameOnEntity(), GenstarGamaTypesConverter.convertGenstar2GamaType(eav.getAttributeValueOnEntity()));
			}

			gamaPopulation.add(map);
			
			// Recursively convert genstar component populations
			IList componentPopulations = GamaListFactory.create();
			for (IPopulation componentPopulation : entity.getComponentPopulations()) {
				componentPopulations.add(convertGenstarPopulationToGamaPopulation(componentPopulation));
			}
			
			if (!componentPopulations.isEmpty()) { map.put(IPopulation.class, componentPopulations); }
		}

		return gamaPopulation;
	}

}
