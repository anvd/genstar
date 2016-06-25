package ummisco.genstar.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.IpfGenerationRule;
import ummisco.genstar.ipu.IpuGenerationRule;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttributeWithRangeInput;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.metamodel.sample_data.SampleData;

import com.google.common.collect.Sets;

public class GenstarService {

	public static IPopulation generateIpfPopulation(final Properties ipfPopulationProperties) throws GenstarException {
		// 1. Read the ATTRIBUTES_PROPERTIES then create the generator
		String attributesCsvFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesCsvFilePath == null) { throw new GenstarException(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found "); }
		GenstarCsvFile attributesCsvFile = new GenstarCsvFile(attributesCsvFilePath, true);
		SampleBasedGenerator generator = new SampleBasedGenerator("single rule generator");
		AttributeUtils.createAttributesFromCsvFile(generator, attributesCsvFile);
		
		// 2. Create the generation rule
		createIpfGenerationRule(generator, "ipf generation rule", ipfPopulationProperties);
		
		// 3. Generate the population
		IPopulation population = generator.generate();
		
		// 4. Perform the result analysis if necessary
		String analysisOutputFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.ANALYSIS_OUTPUT_PROPERTY);
		if (analysisOutputFilePath != null) {
			IpfGenerationRule generationRule = (IpfGenerationRule)generator.getGenerationRule();
			GenstarCsvFile controlledAttributesListFile = generationRule.getControlledAttributesFile();
			GenstarCsvFile controlTotalsFile = generationRule.getControlTotalsFile();
			
			IpfUtils.analyseIpfPopulation(population, controlledAttributesListFile, controlTotalsFile, analysisOutputFilePath);
		}
		
		
		return population;
	}
	
	
	public static IPopulation generateIpuPopulation(final Properties ipuPopulationProperties) throws GenstarException {
		
		// 1. Read the ATTRIBUTES_PROPERTIES then create the generator
		String groupAttributesCsvFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesCsvFilePath == null) { throw new GenstarException(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY + " property not found "); }
		GenstarCsvFile groupAttributesCsvFile = new GenstarCsvFile(groupAttributesCsvFilePath, true);
		SampleBasedGenerator groupGenerator = new SampleBasedGenerator("single rule generator");
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesCsvFile);
		
		// 2. Create the generation rule
		createIpuGenerationRule(groupGenerator, "ipu generation rule", ipuPopulationProperties);
		
		// 3. Generate the population
		IPopulation population = groupGenerator.generate();
		
		// 4. Perform the post generation analysis if necessary
		// GROUP_ANALYSIS_OUTPUT_PROPERTY
		String groupAnalysisOutput = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ANALYSIS_OUTPUT_PROPERTY);
		
		// COMPONENT_ANALYSIS_OUTPUT_PROPERTY
		String componentAnalysisOutput = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_ANALYSIS_OUTPUT_PROPERTY);
		
		if (groupAnalysisOutput != null && componentAnalysisOutput != null) {
			String componentPopulationName = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
			
			IpuGenerationRule generationRule = (IpuGenerationRule) groupGenerator.getGenerationRule();
			GenstarCsvFile groupControlledAttributesListFile = generationRule.getGroupControlledAttributesFile();
			GenstarCsvFile groupControlTotalsFile = generationRule.getGroupControlTotalsFile();
			GenstarCsvFile componentControlledAttributesListFile = generationRule.getComponentControlledAttributesFile();
			GenstarCsvFile componentControlTotalsFile = generationRule.getComponentControlTotalsFile();
			
			Map<String, String> analysisOutputFilePaths = new HashMap<String, String>();
			analysisOutputFilePaths.put(population.getName(), groupAnalysisOutput);
			analysisOutputFilePaths.put(componentPopulationName, componentAnalysisOutput);

			IpuUtils.analyseIpuPopulation(population, componentPopulationName, groupControlledAttributesListFile, 
					groupControlTotalsFile, componentControlledAttributesListFile, componentControlTotalsFile, analysisOutputFilePaths);
		}
		
		
		return population;
	}
	
	
	public static IPopulation generateFrequencyDistributionPopulation(final Properties frequencyDistributionPopulationProperties, final List<String> frequencyDistributionGenerationRuleFilePaths) throws GenstarException {
		
		// 1. Read the properties
		String populationName = frequencyDistributionPopulationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
		if (populationName == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + " property not found"); }
		
		String attributesCSVFilePath = frequencyDistributionPopulationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found"); }
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile(attributesCSVFilePath, true);
		
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
		List<GenstarCsvFile> generationRuleFiles = new ArrayList<GenstarCsvFile>();
		int ruleID = 0;
		for (String fdGenerationRuleFilePath : frequencyDistributionGenerationRuleFilePaths) {
			GenstarCsvFile fdGenerationRuleFile = new GenstarCsvFile(fdGenerationRuleFilePath, true);
			
			FrequencyDistributionUtils.createFrequencyDistributionGenerationRuleFromRuleDataFile(generator, "Frequency Distribution Generation Rule " + ruleID, fdGenerationRuleFile);
			generationRuleFiles.add(fdGenerationRuleFile);
	 
			ruleID++;
		}
		
		
		// 3. Generate the population
		IPopulation population = generator.generate();
		
		
		// 4. perform the post generation analysis if necessary  
		String analysisResultOutputFolderPath = frequencyDistributionPopulationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.ANALYSIS_OUTPUT_FOLDER_PROPERTY);
		if (analysisResultOutputFolderPath != null) {
			FrequencyDistributionUtils.analyseFrequencyDistributionPopulation(population, generationRuleFiles, analysisResultOutputFolderPath);
		}
		
		
		return population;
	}
	
	
	public static List<String> generateFrequencyDistributionsFromSampleOrPopulationData(final Properties frequencyDistributionsProperties, final List<String> distributionFormatFilePaths, final List<String> resultDistributionFilePaths) throws GenstarException {
		
		// TODO parameters validation
		
		// 1. Read the properties
		String attributesCSVFilePath = frequencyDistributionsProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTIONS_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTIONS_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found"); }
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile(attributesCSVFilePath, true);

		String sampleOrPopulationDataFilePath = frequencyDistributionsProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTIONS_PROPERTIES.POPULATION_DATA_PROPERTY);
		if (sampleOrPopulationDataFilePath == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTIONS_PROPERTIES.POPULATION_DATA_PROPERTY + " property not found"); }
		GenstarCsvFile sampleOrPopulationDataFile = new GenstarCsvFile(sampleOrPopulationDataFilePath, true);
		
		String frequencyDistributionFormatsListFilePath = frequencyDistributionsProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTIONS_PROPERTIES.FREQUENCY_DISTRIBUTION_FORMATS_PROPERTY);
		if (frequencyDistributionFormatsListFilePath == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTIONS_PROPERTIES.FREQUENCY_DISTRIBUTION_FORMATS_PROPERTY + " property not found"); }
		GenstarCsvFile frequencyDistributionFormatsListFile = new GenstarCsvFile(frequencyDistributionFormatsListFilePath, true);
		
		
		// 2. parse the frequencyDistributionFormatsListFile
		
		// 2.1. header verification
		List<String> frequencyDistributionFormatsListFileHeader = frequencyDistributionFormatsListFile.getHeaders();
		if (frequencyDistributionFormatsListFileHeader.size() != CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_FORMATS_LIST.NB_OF_COLS) { // header length verification
			throw new GenstarException("Invalid frequency distribution formats list file header: file header must have " 
											+ CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_FORMATS_LIST.NB_OF_COLS + " columns."
											+ " File: " + frequencyDistributionFormatsListFile.getPath());
		}
		
		if (!frequencyDistributionFormatsListFileHeader.get(0).equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_FORMATS_LIST.HEADERS[0])) { // header's first column
			throw new GenstarException("Invalid header content. First element of the header must be \'" + CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_FORMATS_LIST.HEADERS[0]
					+ "\'. File: " + frequencyDistributionFormatsListFile.getPath());
		}
		
		if (!frequencyDistributionFormatsListFileHeader.get(1).equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_FORMATS_LIST.HEADERS[1])) { // header's second column
			throw new GenstarException("Invalid header content. Second element of the header must be \'" + CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_FORMATS_LIST.HEADERS[1] 
					+ "\'. File: " + frequencyDistributionFormatsListFile.getPath());
		}
		
		// 2.2. read distribution format lists file content (first column: path to format file, second column: corresponding output file)
		List<GenstarCsvFile> distributionFormatFiles = new ArrayList<GenstarCsvFile>();
		for (String distributionFormatFilePath : distributionFormatFilePaths) {
			distributionFormatFiles.add(new GenstarCsvFile(distributionFormatFilePath, true));
		}
		
		
		
		// 3. create the generator then add attributes
		SampleFreeGenerator generator = new SampleFreeGenerator("generator");
		AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
		
		
		// 4. generate frequency distributions and save results to files
		return FrequencyDistributionUtils.generateAndSaveFrequencyDistributions(generator, sampleOrPopulationDataFile, distributionFormatFiles, resultDistributionFilePaths);
	}
	
	
	public static IPopulation generateRandomSinglePopulation(final Properties populationProperties) throws GenstarException {
		// 1. Read the properties
		String populationName = populationProperties.getProperty(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
		if (populationName == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + " property not found"); }
		
		String attributesFileProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesFileProperty == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found"); }
		GenstarCsvFile attributesFile = new GenstarCsvFile(attributesFileProperty, true);
		
		String nbOfEntitiesProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.NB_OF_ENTITIES_PROPERTY);
		if (nbOfEntitiesProperty == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.NB_OF_ENTITIES_PROPERTY + " property not found"); }
		int nbOfEntities = Integer.parseInt(nbOfEntitiesProperty);
		if (nbOfEntities <= 0) { throw new GenstarException("Value of " + nbOfEntitiesProperty + " property must be a positive integer"); }
		
		String idAttributeNameOnData = populationProperties.getProperty(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.ID_ATTRIBUTE_PROPERTY);

		
		// 2. Generate the population
		return GenstarUtils.generateRandomSinglePopulation(populationName, attributesFile, idAttributeNameOnData, nbOfEntities);
	}
	
	
	public static IPopulation generateRandomCompoundPopulation(final Properties populationProperties) throws GenstarException {
		// 1. Read the properties
		String groupPopulationName = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY);
		if (groupPopulationName == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY + " property not found"); }
		
		String groupAttributesFileProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesFileProperty == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY + " property not found "); }
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(groupAttributesFileProperty, true);
		
		String componentPopulationName = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
		if (componentPopulationName == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY + " property not found"); }
		
		String componentAttributesFileProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFileProperty == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY ); }
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(componentAttributesFileProperty, true);
		

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
	
	
	public static String generateIpfControlTotalsFromPopulationData(final Properties ipfControlTotalsProperties) throws GenstarException {
		
		try {
			
			// 1. initialize CSV files
			String attributesCSVFilePath = ipfControlTotalsProperties.getProperty(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.ATTRIBUTES_PROPERTY);
			if (attributesCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found."); }
			GenstarCsvFile attributesCSVFile = new GenstarCsvFile(attributesCSVFilePath, true);
			
			String idAttributeNameOnData = ipfControlTotalsProperties.getProperty(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.ID_ATTRIBUTE_PROPERTY);
			
			String controlledAttributesFilePath = ipfControlTotalsProperties.getProperty(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
			if (controlledAttributesFilePath == null) { throw new GenstarException(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY + " property not found."); }
			GenstarCsvFile controlledAttributesFile = new GenstarCsvFile(controlledAttributesFilePath, false);
			
			String populationFilePath = ipfControlTotalsProperties.getProperty(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.POPULATION_DATA_PROPERTY);
			if (populationFilePath == null) { throw new GenstarException(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.POPULATION_DATA_PROPERTY + " property not found."); }
			GenstarCsvFile populationFile = new GenstarCsvFile(populationFilePath, true);
			
			String controlTotalsOuputFilePath = ipfControlTotalsProperties.getProperty(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.OUTPUT_FILE_PROPERTY);
			if (controlTotalsOuputFilePath == null) { throw new GenstarException(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.OUTPUT_FILE_PROPERTY + " property not found."); }
			
			int controlAttributesRows = controlledAttributesFile.getRows();
			if (controlledAttributesFile.getColumns() != 1 || controlAttributesRows < 2) {
				throw new GenstarException("Invalid controlAttributesCSVFile format. File: " + controlledAttributesFile);
			}
			Set<String> controlledAttributeNames = new HashSet<String>();
			for (List<String> row : controlledAttributesFile.getContent()) { controlledAttributeNames.add(row.get(0)); }
			
			
			// TODO move the logic of generating Ipf control totals to IpfUtils
			// 2. create the generator then add attributes
			ISyntheticPopulationGenerator generator = new SampleFreeGenerator("dummy generator", 10);
			AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
			
			
			// 3. process ID attribute
			if (idAttributeNameOnData != null) {
				AbstractAttribute idAttribute = generator.getAttributeByNameOnData(idAttributeNameOnData);
				if (idAttribute == null) { throw new GenstarException(idAttributeNameOnData + " is not recognized as an attribute a valid"); }
				if (controlledAttributeNames.contains(idAttributeNameOnData)) { throw new GenstarException("Identity attribute (" + idAttributeNameOnData + ") can not be a controlled attribute."); }
			}

			
			// 4. ensure that all controlled attributes exist
			for (String attributeNameOnData : controlledAttributeNames) { 
				if (generator.getAttributeByNameOnData(attributeNameOnData) == null) {
					throw new GenstarException(attributeNameOnData + " is not a valid attribute. File: " + controlledAttributesFilePath);
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
			SampleData populationData = new SampleData("dummy population", generator.getAttributes(), populationFile);
			List<Entity> populationEntities = populationData.getSampleEntityPopulation().getEntities();
			Map<Integer, List<AbstractAttribute>> cachedAvfAttributes = new HashMap<Integer, List<AbstractAttribute>>();
			Map<Integer, AttributeValuesFrequency> cachedAvfs = new HashMap<Integer, AttributeValuesFrequency>();
			for (int index=0; index<attributeValuesFrequencies.size(); index++) {
				cachedAvfAttributes.put(index, new ArrayList<AbstractAttribute>(attributeValuesFrequencies.get(index).getAttributes()));
				cachedAvfs.put(index, attributeValuesFrequencies.get(index));
			}
			
			// TODO improve this loop be calling ?population.getMatchingEntitiesByAttributeValuesOnEntity? or ?population.getMatchingEntitiesByAttributeValuesOnData?
			for (int index=0; index<attributeValuesFrequencies.size(); index++) {
				List<AbstractAttribute> avfAttributes = new ArrayList<AbstractAttribute>(cachedAvfAttributes.get(index));
				
				for (Entity sampleEntity : populationEntities) {
					AttributeValuesFrequency avf = cachedAvfs.get(index);
					if (avf.matchAttributeValuesOnData(sampleEntity.getAttributesValuesOnData(avfAttributes))) { avf.increaseFrequency(); }
				}
			}
			
			
			// 8. build CSV file content of the output file
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
			
			// 9. write the output file
			CsvWriter writer = new CsvWriter(controlTotalsOuputFilePath);
			for ( String[] ss : csvFileContents ) { writer.writeRecord(ss); }
			writer.close();
			
			return controlTotalsOuputFilePath;
		} catch (Exception e) {
			throw new GenstarException(e);
		}
	}
	
	
	public static Map<String, String> generateIpuControlTotalsFromPopulationData(final Properties ipuControlTotalsProperties) throws GenstarException {
		
		// Read the properties
		
		// GROUP_POPULATION_NAME_PROPERTY
		String groupPopulationName = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY);
		if (groupPopulationName == null) { throw new GenstarException(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY + " property not found in the property file."); }

		// COMPONENT_POPULATION_NAME_PROPERTY
		String componentPopulationName = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
		if (componentPopulationName == null) { throw new GenstarException(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY + " property not found in the property file."); }
		
		
		// GROUP_CONTROLLED_ATTRIBUTES_PROPERTY 
		String groupControlledAttributesFilePath = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (groupControlledAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupControlledAttributesListFile = new GenstarCsvFile(groupControlledAttributesFilePath, false);

		// COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY
		String componentControlledAttributesFilePath = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (componentControlledAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentControlledAttributesListFile = new GenstarCsvFile(componentControlledAttributesFilePath, false);

		
		// GROUP_OUTPUT_FILE_PROPERTY
		String groupControlTotalsOuputFilePath = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.GROUP_OUTPUT_FILE_PROPERTY);
		if (groupControlTotalsOuputFilePath == null) { throw new GenstarException(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.GROUP_OUTPUT_FILE_PROPERTY + " property not found"); }
		
		// COMPONENT_OUTPUT_FILE_PROPERTY
		String componentControlTotalsOuputFilePath = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.COMPONENT_OUTPUT_FILE_PROPERTY);
		if (componentControlTotalsOuputFilePath == null) { throw new GenstarException(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.COMPONENT_OUTPUT_FILE_PROPERTY + " property not found"); }
		
		
		// 1. load compound population
		IPopulation compoundPopulation = GenstarService.loadCompoundPopulation(ipuControlTotalsProperties);
		
		// 2. build Ipu control totals
		Map<String, List<AttributeValuesFrequency>> ipuControlTotals = IpuUtils.buildIpuControlTotalsOfCompoundPopulation(compoundPopulation, componentPopulationName, groupControlledAttributesListFile, componentControlledAttributesListFile);
		
		// 3. save the generated control totals to CSV files
		IpuUtils.writeIpuControlTotalsToCsvFile(ipuControlTotals.get(groupPopulationName), groupControlTotalsOuputFilePath);
		IpuUtils.writeIpuControlTotalsToCsvFile(ipuControlTotals.get(componentPopulationName), componentControlTotalsOuputFilePath);
		
		
		Map<String, String> controlTotalsFilePaths = new HashMap<String, String>();
		controlTotalsFilePaths.put(groupPopulationName, groupControlTotalsOuputFilePath);
		controlTotalsFilePaths.put(componentPopulationName, componentControlTotalsOuputFilePath);
		
		return controlTotalsFilePaths;
	}
	
	
	public static void createIpfGenerationRule(final SampleBasedGenerator generator, final String ruleName, final Properties ipfPopulationProperties) throws GenstarException {
		
		// Read the  properties
		
		// POPULATION_NAME_PROPERTY
		String populationName = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
		if (populationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + "' not found in the property file."); };
		generator.setPopulationName(populationName);
		
		// SAMPLE_DATA_PROPERTY
		String sampleDataFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SAMPLE_DATA_PROPERTY);
		if (sampleDataFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile sampleCSVFile = new GenstarCsvFile(sampleDataFilePath, true);
		
		// CONTROLLED_ATTRIBUTES_PROPERTY 
		String controlledAttributesFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (controlledAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile controlledAttributesCSVFile = new GenstarCsvFile(controlledAttributesFilePath, false);
		
		// CONTROL_TOTALS_PROPERTY
		String controlledTotalsFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROL_TOTALS_PROPERTY);
		if (controlledTotalsFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROL_TOTALS_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile controlledTotalsCSVFile = new GenstarCsvFile(controlledTotalsFilePath, false);
		
		// SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String supplementaryAttributesFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (supplementaryAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SUPPLEMENTARY_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile supplementaryAttributesCSVFile = new GenstarCsvFile(supplementaryAttributesFilePath, false);
		
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
			GenstarCsvFile componentSampleDataFile = new GenstarCsvFile(componentSampleDataFilePath, true);
			
			// COMPONENT_ATTRIBUTES_PROPERTY
			String componentAttributesFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
			if (componentAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
			GenstarCsvFile componentAttributesFile = new GenstarCsvFile(componentAttributesFilePath, true);
			 
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

	
	public static void createIpuGenerationRule(final SampleBasedGenerator groupGenerator, final String ruleName, final Properties ipuPopulationProperties) throws GenstarException {

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
		GenstarCsvFile groupSampleDataFile = new GenstarCsvFile(groupSampleDataFilePath, true);
		
		// GROUP_CONTROLLED_ATTRIBUTES_PROPERTY 
		String groupControlledAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (groupControlledAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupControlledAttributesFile = new GenstarCsvFile(groupControlledAttributesFilePath, false);
		
		// GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY
		String groupIdAttributeNameOnGroup = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
		if (groupIdAttributeNameOnGroup == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + "' not found in the property file."); }

		// GROUP_CONTROL_TOTALS_PROPERTY
		String groupControlTotalsFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROL_TOTALS_PROPERTY);
		if (groupControlTotalsFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROL_TOTALS_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupControlledTotalsFile = new GenstarCsvFile(groupControlTotalsFilePath, false);
		
		// GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String groupSupplementaryAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (groupSupplementaryAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupSupplementaryAttributesFile = new GenstarCsvFile(groupSupplementaryAttributesFilePath, false);
		 

		// COMPONENT_POPULATION_NAME_PROPERTY
		String componentPopulationName = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
		if (componentPopulationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY + "' not found in the property file."); };
		
		// COMPONENT_SAMPLE_DATA_PROPERTY
		String componentSampleDataFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY);
		if (componentSampleDataFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentSampleDataFile = new GenstarCsvFile(componentSampleDataFilePath, true);
		
		// COMPONENT_ATTRIBUTES_PROPERTY
		String componentAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(componentAttributesFilePath, true);
		 
		// COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY
		String componentControlledAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (componentControlledAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentControlledAttributesFile = new GenstarCsvFile(componentControlledAttributesFilePath, false);
		
		// COMPONENT_CONTROL_TOTALS_PROPERTY
		String componentControlTotalsFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROL_TOTALS_PROPERTY);
		if (componentControlTotalsFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROL_TOTALS_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentControlTotalsFile = new GenstarCsvFile(componentControlTotalsFilePath, false);
		
		
		// GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY
		String groupIdAttributeNameOnComponent = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
		if (groupIdAttributeNameOnComponent == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + "' not found in the property file."); }
		 
		// COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String componentSupplementaryAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (componentSupplementaryAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentSupplementaryAttributesFile = new GenstarCsvFile(componentSupplementaryAttributesFilePath, false);
				
		
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
	
	
	public static IPopulation extractIpuPopulation(final Properties ipuSourcePopulationProperties) throws GenstarException {
		
		// 0. Read the properties
		
		// GROUP_ATTRIBUTES_PROPERTY
		String groupAttributesFilePath = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(groupAttributesFilePath, true);
		
		// GROUP_POPULATION_NAME_PROPERTY
		String groupPopulationName = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY);
		if (groupPopulationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY + "' not found in the property file."); }
		
		// GROUP_POPULATION_DATA_PROPERTY
		String groupPopulationFilePath = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY);
		if (groupPopulationFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupPopulationFile = new GenstarCsvFile(groupPopulationFilePath, true);
		
		// GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY
		String groupIdAttributeNameOnDataOfGroupEntity = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
		if (groupIdAttributeNameOnDataOfGroupEntity == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + "' not found in the property file."); }
		
		
		// COMPONENT_ATTRIBUTES_PROPERTY
		String componentAttributesFilePath =  ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(componentAttributesFilePath, true);
		
		// COMPONENT_POPULATION_NAME_PROPERTY
		String componentPopulationName = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
		if (componentPopulationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY + "' not found in the property file."); }
		
		// COMPONENT_POPULATION_DATA_PROPERTY
		String componentPopulationFilePath = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY);
		if (componentPopulationFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentPopulationFile = new GenstarCsvFile(componentPopulationFilePath, true);
		
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
		GenstarCsvFile ipuGroupControlledAttributesFile = new GenstarCsvFile(ipuGroupControlledFilePath, false);
		
		// 1. parse controlled attributes
		Set<AbstractAttribute> ipuControlledAttributes = new HashSet<AbstractAttribute>();
		for (List<String> row : ipuGroupControlledAttributesFile.getContent()) {
			for (String attributeNameOnData : row) {
				AbstractAttribute attr = originalCompoundPopulation.getAttributeByNameOnData(attributeNameOnData);
				if (attr == null) { throw new GenstarException("Attribute '" + attributeNameOnData + "' defined in " + ipuGroupControlledFilePath + " not found in the population"); }
				ipuControlledAttributes.add(attr);
			}
		}
		
		AbstractAttribute _groupIdAttributeOnGroupEntity = originalCompoundPopulation.getAttributeByNameOnData(groupIdAttributeNameOnDataOfGroupEntity);
		if (_groupIdAttributeOnGroupEntity == null) {
			throw new GenstarException("\'" + groupIdAttributeNameOnDataOfGroupEntity + "\' is not a valid attribute");
		}
		if (!(_groupIdAttributeOnGroupEntity instanceof UniqueValuesAttributeWithRangeInput)) {
			throw new GenstarException("\'" + groupIdAttributeNameOnDataOfGroupEntity + "\' must be an instance of UniqueValuesAttributeWithRangeInput");
		}
		UniqueValuesAttributeWithRangeInput groupIdAttributeOnGroupEntity = (UniqueValuesAttributeWithRangeInput)_groupIdAttributeOnGroupEntity;

		UniqueValuesAttributeWithRangeInput groupIdAttributeOnComponentEntity = null;
		for (Entity groupEntity : originalCompoundPopulation.getEntities()) {
			IPopulation componentPopulation = groupEntity.getComponentPopulation(componentPopulationName);
			if (componentPopulation != null) { 
				AbstractAttribute _groupIdAttributeOnComponentEntity = componentPopulation.getAttributeByNameOnData(groupIdAttributeNameOnDataOfGroupEntity);
				if (!(_groupIdAttributeOnComponentEntity instanceof UniqueValuesAttributeWithRangeInput)) {
					throw new GenstarException("\'" + groupIdAttributeNameOnDataOfGroupEntity + "\' must be an instance of UniqueValuesAttributeWithRangeInput");
				}
				groupIdAttributeOnComponentEntity = (UniqueValuesAttributeWithRangeInput)_groupIdAttributeOnComponentEntity;
				break;
			}
		}

		
		// 2. extract the sample population
		return IpuUtils.extractIpuPopulation(originalCompoundPopulation, percentage, ipuControlledAttributes, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity, componentPopulationName);
	}
	
	
	public static IPopulation extractIpfSinglePopulation(final Properties ipfSinglePopulationProperties) throws GenstarException {
		// 0. Read the properties
		
		// ATTRIBUTES_PROPERTY
		String attributesFilePath = ipfSinglePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile attributesFile = new GenstarCsvFile(attributesFilePath, true);
		
		// POPULATION_NAME_PROPERTY
		String populationName = ipfSinglePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
		if (populationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + "' not found in the property file."); }
		
		// POPULATION_DATA_PROPERTY
		String populationFilePath = ipfSinglePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.POPULATION_DATA_PROPERTY);
		if (populationFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.POPULATION_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile singlePopulationFile = new GenstarCsvFile(populationFilePath, true);
		
		// CONTROLLED_ATTRIBUTES_PROPERTY
		String ipfControlledAttributesFilePath = ipfSinglePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (ipfControlledAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile ipfControlledAttributesFile = new GenstarCsvFile(ipfControlledAttributesFilePath, false);
		
		// ID_ATTRIBUTE_PROPERTY
		String idAttributeName = ipfSinglePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.ID_ATTRIBUTE_PROPERTY);
		
		// PERCENTAGE_PROPERTY
		String percentageProperty = ipfSinglePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.PERCENTAGE_PROPERTY);
		if (percentageProperty == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.PERCENTAGE_PROPERTY + "' not found in the property file."); }
		float percentage = 0;
		try {
			percentage = Float.parseFloat(percentageProperty);
		} catch (NumberFormatException nfe) {
			throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.PERCENTAGE_PROPERTY + "' is not a float number");
		}
		

		// 1. Load the population from file
		IPopulation originalSinglePopulation = GenstarUtils.loadSinglePopulation(PopulationType.SYNTHETIC_POPULATION, populationName, attributesFile, singlePopulationFile);
		
		
		// 2. parse controlled attributes
		Set<AbstractAttribute> ipfControlledAttributes = new HashSet<AbstractAttribute>();
		for (List<String> row : ipfControlledAttributesFile.getContent()) {
			for (String attributeNameOnData : row) {
				AbstractAttribute attr = originalSinglePopulation.getAttributeByNameOnData(attributeNameOnData);
				if (attr == null) { throw new GenstarException("Attribute '" + attributeNameOnData + "' defined in " + ipfControlledAttributesFilePath + " not found in the population"); }
				ipfControlledAttributes.add(attr);
			}
		}
		
		
		UniqueValuesAttributeWithRangeInput idAttribute = null;
		if (idAttributeName != null) {
			AbstractAttribute _idAttribute = originalSinglePopulation.getAttributeByNameOnData(idAttributeName);
			
			if (_idAttribute == null) { throw new GenstarException("\'" + idAttributeName + "\' is not a valid attribute of the population"); }
			if (!(_idAttribute instanceof UniqueValuesAttributeWithRangeInput)) { throw new GenstarException("Identity attribute (" + idAttributeName + ") must be an instance of UniqueValuesAttributeWithRangeInput"); }
			idAttribute = (UniqueValuesAttributeWithRangeInput)_idAttribute;
		}
		
		
		// 3. Extract the loaded population
		return IpfUtils.extractIpfSinglePopulation(originalSinglePopulation, percentage, ipfControlledAttributes, idAttribute);
	}
	
	
	public static IPopulation extractIpfCompoundPopulation(final Properties ipfCompoundPopulationProperties) throws GenstarException {
		// 0. Read the properties
		
		// GROUP_ATTRIBUTES_PROPERTY
		String groupAttributesFilePath = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(groupAttributesFilePath, true);
		
		// GROUP_POPULATION_NAME_PROPERTY
		String groupPopulationName = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY);
		if (groupPopulationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY + "' not found in the property file."); }
		
		// GROUP_POPULATION_DATA_PROPERTY
		String groupPopulationFilePath = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY);
		if (groupPopulationFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile groupPopulationFile = new GenstarCsvFile(groupPopulationFilePath, true);
	
		// CONTROLLED_ATTRIBUTES_PROPERTY
		String ipfGroupControlledAttributesFilePath = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (ipfGroupControlledAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile ipfGroupControlledAttributesFile = new GenstarCsvFile(ipfGroupControlledAttributesFilePath, false);
		
		// GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY
		String groupIdAttributeNameOnDataOfGroupEntity = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
		if (groupIdAttributeNameOnDataOfGroupEntity == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + "' not found in the property file."); }
		
		
		// COMPONENT_ATTRIBUTES_PROPERTY
		String componentAttributesFilePath =  ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(componentAttributesFilePath, true);
		
		// COMPONENT_POPULATION_NAME_PROPERTY
		String componentPopulationName = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
		if (componentPopulationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY + "' not found in the property file."); }
		
		// COMPONENT_POPULATION_DATA_PROPERTY
		String componentPopulationFilePath = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY);
		if (componentPopulationFilePath == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY + "' not found in the property file."); }
		GenstarCsvFile componentPopulationFile = new GenstarCsvFile(componentPopulationFilePath, true);
		
		// GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY
		String groupIdAttributeNameOnDataOfComponentEntity = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
		if (groupIdAttributeNameOnDataOfComponentEntity == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + "' not found in the property file."); }

	
		// PERCENTAGE_PROPERTY
		String percentageProperty = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.PERCENTAGE_PROPERTY);
		if (percentageProperty == null) { throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.PERCENTAGE_PROPERTY + "' not found in the property file."); }
		float percentage = 0;
		try {
			percentage = Float.parseFloat(percentageProperty);
		} catch (NumberFormatException nfe) {
			throw new GenstarException("Property '" + PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.PERCENTAGE_PROPERTY + "' is not a float number");
		}
		
		
		// COMPONENT_REFERENCE_ON_GROUP_PROPERTY
		String componentReferenceOnGroup = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.COMPONENT_REFERENCE_ON_GROUP_PROPERTY);
		
		// GROUP_REFERENCE_ON_COMPONENT_PROPERTY
		String groupReferenceOnComponent = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_REFERENCE_ON_COMPONENT_PROPERTY);

		// 1. Load the population from file
		IPopulation originalCompoundPopulation = GenstarUtils.loadCompoundPopulation(PopulationType.SYNTHETIC_POPULATION, 
				groupPopulationName, groupAttributesFile, groupPopulationFile, componentPopulationName, 
				componentAttributesFile, componentPopulationFile, groupIdAttributeNameOnDataOfGroupEntity, groupIdAttributeNameOnDataOfComponentEntity,
				componentReferenceOnGroup, groupReferenceOnComponent);
		
		
		// 2. parse controlled attributes
		Set<AbstractAttribute> ipfGroupControlledAttributes = new HashSet<AbstractAttribute>();
		for (List<String> row : ipfGroupControlledAttributesFile.getContent()) {
			for (String attributeNameOnData : row) {
				AbstractAttribute attr = originalCompoundPopulation.getAttributeByNameOnData(attributeNameOnData);
				if (attr == null) { throw new GenstarException("Attribute '" + attributeNameOnData + "' defined in " + ipfGroupControlledAttributesFilePath + " not found in the population"); }
				ipfGroupControlledAttributes.add(attr);
			}
		}

		AbstractAttribute _groupIdAttributeOnGroupEntity = originalCompoundPopulation.getAttributeByNameOnData(groupIdAttributeNameOnDataOfGroupEntity);
		if (_groupIdAttributeOnGroupEntity == null) {
			throw new GenstarException("\'" + groupIdAttributeNameOnDataOfGroupEntity + "\' is not a valid attribute");
		}
		if (!(_groupIdAttributeOnGroupEntity instanceof UniqueValuesAttributeWithRangeInput)) {
			throw new GenstarException("\'" + groupIdAttributeNameOnDataOfGroupEntity + "\' must be an instance of UniqueValuesAttributeWithRangeInput");
		}
		UniqueValuesAttributeWithRangeInput groupIdAttributeOnGroupEntity = (UniqueValuesAttributeWithRangeInput)_groupIdAttributeOnGroupEntity;

		UniqueValuesAttributeWithRangeInput groupIdAttributeOnComponentEntity = null;
		for (Entity groupEntity : originalCompoundPopulation.getEntities()) {
			IPopulation componentPopulation = groupEntity.getComponentPopulation(componentPopulationName);
			if (componentPopulation != null) { 
				AbstractAttribute _groupIdAttributeOnComponentEntity = componentPopulation.getAttributeByNameOnData(groupIdAttributeNameOnDataOfGroupEntity);
				if (!(_groupIdAttributeOnComponentEntity instanceof UniqueValuesAttributeWithRangeInput)) {
					throw new GenstarException("\'" + groupIdAttributeNameOnDataOfGroupEntity + "\' must be an instance of UniqueValuesAttributeWithRangeInput");
				}
				groupIdAttributeOnComponentEntity = (UniqueValuesAttributeWithRangeInput)_groupIdAttributeOnComponentEntity;
				break;
			}
		}

		
		// 3. Extract the loaded population
		return IpfUtils.extractIpfCompoundPopulation(originalCompoundPopulation, percentage, ipfGroupControlledAttributes, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity, componentPopulationName);
	}
	
	
	public static IPopulation loadSinglePopulation(final Properties singlePopulationProperties) throws GenstarException {
		
		// 0. Read the properties
		String populationName = singlePopulationProperties.getProperty(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
		if (populationName == null) { throw new GenstarException("Property '" + PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + "' not found in the property file."); }
		
		String attributesFileProperty = singlePopulationProperties.getProperty(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesFileProperty == null) { throw new GenstarException(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found in the property file."); }
		GenstarCsvFile attributesFile = new GenstarCsvFile(attributesFileProperty, true);
		
		String populationCSVFilePath = singlePopulationProperties.getProperty(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.POPULATION_DATA_PROPERTY);
		if (populationCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.POPULATION_DATA_PROPERTY + " property not found in the property file."); }
		GenstarCsvFile populationCSVFile = new GenstarCsvFile(populationCSVFilePath, true);

		
		// 1. load the single population
		return GenstarUtils.loadSinglePopulation(PopulationType.SYNTHETIC_POPULATION, populationName, attributesFile, populationCSVFile);
	}
	
	
	public static IPopulation loadCompoundPopulation(final Properties compoundPopulationProperties) throws GenstarException {
		
		// 0. Read the properties
		
		String groupPopulationName = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY);
		if (groupPopulationName == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY + " property not found in the property file."); }
		
		String groupAttributesFilePath = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesFilePath == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY + " property not found in the property file."); }
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(groupAttributesFilePath, true);
		
		String groupPopulationFilePath = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY);
		if (groupPopulationFilePath == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY + " property not found in the property file."); }
		GenstarCsvFile groupPopulationFile = new GenstarCsvFile(groupPopulationFilePath, true);
		
		
		String componentPopulationName = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
		if (componentPopulationName == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY + " property not found in the property file."); }
		
		String componentAttributesFilePath = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFilePath == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY ); }
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(componentAttributesFilePath, true);
		
		String componentPopulationFilePath = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY);
		if (componentPopulationFilePath == null) { throw new GenstarException(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY + " property not found in the property file."); }
		GenstarCsvFile componentPopulationFile = new GenstarCsvFile(componentPopulationFilePath, true);
		

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
}
