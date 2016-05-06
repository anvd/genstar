package ummisco.genstar.gama;

import static msi.gama.common.interfaces.IKeyword.GENSTAR_ENTITY;
import static msi.gama.common.interfaces.IKeyword.GENSTAR_POPULATION;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import msi.gama.common.util.FileUtils;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.file.IGamaFile;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.extensions.genstar.IGamaPopulationsLinker;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.sample_data.SampleData;
import ummisco.genstar.sample_free.FrequencyDistributionGenerationRule;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.CsvWriter;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.GenstarUtils;
import ummisco.genstar.util.INPUT_DATA_FORMATS;
import ummisco.genstar.util.IpfUtils;

import com.google.common.collect.Sets;

/**
 * A set of Genstar-related operators.
 */
public abstract class Genstars {
	
		
	@operator(value = "frequency_distribution_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
	@doc(value = "generates a synthetic population from the input data provided by the CVS files. The generated population can be passed to the 'create' statement to create agents.",
		returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list synthetic_population <- frequency_distribution_population('Attributes.csv', 'GenerationRules.csv', 14000)",
			equals = "",
			test = false) }, see = { "frequency_distribution_from_sample", "link_populations" })
	public static IList generatePopulationFromFrequencyDistribution(final IScope scope, final String populationPropertiesFilePath) {

		try {
			// 0. Load the property file
			Properties populationProperties = null;
			File propertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, populationPropertiesFilePath, true));
			try {
				FileInputStream propertyInputStream = new FileInputStream(propertiesFile);
				populationProperties = new Properties();
				populationProperties.load(propertyInputStream);
			} catch (FileNotFoundException e) {
				throw new GenstarException(e);
			} catch (IOException e) {
				throw new GenstarException(e);
			}
			
			// 1. Read the properties
			String populationName = populationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION.POPULATION_NAME_PROPERTY);
			if (populationName == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION.POPULATION_NAME_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			
			String attributesCSVFilePath = populationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION.ATTRIBUTES_PROPERTY);
			if (attributesCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION.ATTRIBUTES_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			GenstarCsvFile attributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			
			String generationRulesCSVFilePath = populationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION.GENERATION_RULES_PROPERTY);
			if (generationRulesCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION.GENERATION_RULES_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			GenstarCsvFile generationRulesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, generationRulesCSVFilePath, true), true);
			
			String nbOfEntitiesProperty = populationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION.NUMBER_OF_ENTITIES);
			if (nbOfEntitiesProperty == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION.NUMBER_OF_ENTITIES + " property not found in " + populationPropertiesFilePath); }
			int nbOfEntities = 0;
			try {
				nbOfEntities = Integer.parseInt(nbOfEntitiesProperty);
			} catch (NumberFormatException nfe) {
				throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION.NUMBER_OF_ENTITIES + " property can not contain a negative integer.");
			}
			if (nbOfEntities <= 0) { throw new GenstarException("Value of " + nbOfEntitiesProperty + " property must be a positive integer"); }
			
			
			// 2. Create the generator
			SampleFreeGenerator generator = new SampleFreeGenerator("Population Generator", nbOfEntities);
			generator.setPopulationName(populationName);
			AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
			GamaGenstarUtils.createSampleFreeGenerationRules(scope, generator, generationRulesCSVFile);
			
			return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(generator.generate());
		} catch (final GenstarException e) {
			throw GamaRuntimeException.error(e.getMessage(), scope);
		}
	}
	
	
	@operator(value = "link_populations", category = { IOperatorCategory.GENSTAR })
	@doc(value = "Links populations",
	returns = "",
	special_cases = { "" },
	comment = "",
	examples = { @example(value = "bool linking_result <- link_populations('household_inhabitant_linker', household_inhabitant_populations)",
		equals = "a boolean value indicating where the linking process is successful or not",
		test = false) }, see = { "population_from_csv", "frequency_distribution_from_sample" })
	public static boolean linkPopulations(final IScope scope, final String linkerName, final IList<IList<IMacroAgent>> populations) {
		
		// 1. search for the linker on the AbstractGamlAdditions
		IGamaPopulationsLinker populationsLinker = AbstractGamlAdditions.POPULATIONS_LINKERS.get(linkerName);
		
		if (populationsLinker == null) { throw GamaRuntimeException.error("Populations linker : " + linkerName + " does not exist.", scope); }
		
		// 2. ask the linker to do the job
		populationsLinker.establishRelationship(scope, populations);
		
		return true;
	}
	

	@operator(value = "frequency_distribution_from_sample", type = IType.FILE, category = { IOperatorCategory.GENSTAR })
	@doc(value = "generates a frequency distribution generation rule from a sample data then saves the resulting generation rule to a CSV file",
		returns = "a boolean value, indicating where the operator is successful or not",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "file result_file <- frequency_distribution_from_sample('Attributes.csv', 'SampleData.csv', 'DistributionFormat.csv', 'ResultingDistribution.csv')",
			equals = "a file containing the resulting frequency distribution generation rule and locating at the resultDistributionCSVFilePath path",
			test = false) }, see = { "population_from_csv", "link_populations" })
	public static IGamaFile createFrequencyDistributionFromSample(final IScope scope, final String attributesCSVFilePath, 
			final String sampleDataCSVFilePath, final String distributionFormatCSVFilePath, final String resultDistributionCSVFilePath) {
		
		try {
			// initialize CSV files
			GenstarCsvFile attributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			GenstarCsvFile sampleDataCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, sampleDataCSVFilePath, true), true);
			GenstarCsvFile distributionFormatCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, distributionFormatCSVFilePath, true), true);
			
			// 1. create the generator then add attributes
			SampleFreeGenerator generator = new SampleFreeGenerator("dummy generator", 10);
			AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
			
			// 2. read the distribution file format to know the input and output attributes
			List<String> attributeNamesOnData = distributionFormatCSVFile.getHeaders();
			List<String> attributeNamesOnDataWithoutInputOutput = new ArrayList<String>();
			if (attributeNamesOnData.size() < 1) { throw new GenstarException("First line of distribution format file must contain at least 2 elements. File: " + distributionFormatCSVFile.getPath() + "."); }
			for (int index=0; index < attributeNamesOnData.size(); index++) {
				String attribute = attributeNamesOnData.get(index);
				StringTokenizer t = new StringTokenizer(attribute, INPUT_DATA_FORMATS.CSV_FILES.FREQUENCY_DISTRIBUTION_GENERATION_RULE.ATTRIBUTE_NAME_TYPE_DELIMITER);
				if (t.countTokens() != 2) { throw new GenstarException("Element must have format attribute_name:Input or attribute_name:Output. File: " + distributionFormatCSVFile.getPath() + "."); }
				
				String attributeNameOnData = t.nextToken();
				if (generator.getAttributeByNameOnData(attributeNameOnData) == null) { throw new GenstarException("Attribute '" + attributeNameOnData + "' is not defined. File: " + distributionFormatCSVFile.getPath() + "."); }
				attributeNamesOnDataWithoutInputOutput.add(attributeNameOnData);
				
				String attributeType = t.nextToken();
				if (!attributeType.equals(INPUT_DATA_FORMATS.CSV_FILES.FREQUENCY_DISTRIBUTION_GENERATION_RULE.INPUT_ATTRIBUTE) && !attributeType.equals(INPUT_DATA_FORMATS.CSV_FILES.FREQUENCY_DISTRIBUTION_GENERATION_RULE.OUTPUT_ATTRIBUTE)) {
					throw new GenstarException("Attribute " + attributeNameOnData + " must either be Input or Output. File: " + distributionFormatCSVFile.getPath() + ".");
				}
			}
			
			// 3. create a frequency generation rule from the sample data then write the generation rule to file.
			FrequencyDistributionGenerationRule fdGenerationRule = GenstarUtils.createFrequencyDistributionGenerationRuleFromSampleData(generator, distributionFormatCSVFile, sampleDataCSVFile);
			
			List<String[]> contents = new ArrayList<String[]>();
			
			// write the header
			String[] generationRuleAttributeNamesArray = new String[attributeNamesOnData.size() + 1];
			attributeNamesOnData.toArray(generationRuleAttributeNamesArray);
			generationRuleAttributeNamesArray[generationRuleAttributeNamesArray.length - 1] = "Frequency";
			contents.add(generationRuleAttributeNamesArray);
			
			
			List<AbstractAttribute> generationRuleAttributes = new ArrayList<AbstractAttribute>();
			for (String a : attributeNamesOnDataWithoutInputOutput) { generationRuleAttributes.add(generator.getAttributeByNameOnData(a)); }
			
			// sort the attributeValueFrequencies
			List<AttributeValuesFrequency> sortedAttributeValueFrequencies = new ArrayList<AttributeValuesFrequency>(fdGenerationRule.getAttributeValuesFrequencies());
			Collections.sort(sortedAttributeValueFrequencies, new GenstarUtils.AttributeValuesFrequencyComparator(generationRuleAttributes));
			
			//TODO refactor this code, use GenstarUtils.writeControlTotalsToCsvFile(controlTotals, csvFilePath);
			// write attribute values frequencies to CSV file, one line for each AttributeValuesFrequency object
			for (AttributeValuesFrequency avf : sortedAttributeValueFrequencies) {
				String[] row = new String[attributeNamesOnData.size() + 1];

				// build the string representation of each set of attribute values
				for (int i=0; i<generationRuleAttributes.size(); i++) {
					AttributeValue av = avf.getAttributeValueOnData(generationRuleAttributes.get(i));
					row[i] = av.toCsvString();
				}
				row[row.length - 1] = Integer.toString(avf.getFrequency());
				
				contents.add(row);
			}
			
			// save the CSV file
			String exportFileName = FileUtils.constructAbsoluteFilePath(scope, resultDistributionCSVFilePath, false);
			CsvWriter writer = new CsvWriter(exportFileName);
			for ( String[] ss : contents ) { writer.writeRecord(ss); }
			writer.close();
			
			return new GamaCSVFile(scope, exportFileName, INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.FIELD_DELIMITER, Types.STRING, true);
		} catch (final Exception e) {
			if (e instanceof GamaRuntimeException) { throw (GamaRuntimeException) e; }
			else { throw GamaRuntimeException.create(e, scope); }
		}
	}
	
	
	
	// TODO change to "ipf_control_totals"
	@operator(value = "control_totals", type = IType.FILE, category = { IOperatorCategory.GENSTAR })
	@doc(value = "generates the control totals then save them to a CSV file",
		returns = "a reference to the file containing the resulting control totals",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "file result_file <- control_totals('controlTotalPropertiesFilePath.properties', 'resultControlsTotalFilePath.csv')",
			equals = "a file containing the resulting control totals and locating at the path specified by resultControlsTotalFilePath.csv",
			test = false) }, see = { "population_from_csv", "link_populations" })
	public static IGamaFile generateControlTotals(final IScope scope, final String controlTotalPropertiesFilePath, final String resultControlsTotalFilePath) {
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
			String attributesCSVFilePath = controlTotalProperties.getProperty(PROPERTY_FILES.CONTROL_TOTALS.ATTRIBUTES_PROPERTY);
			if (attributesCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION.ATTRIBUTES_PROPERTY + " property not found in " + controlTotalPropertiesFilePath); }
			GenstarCsvFile attributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			
			String idAttributeNameOnData = controlTotalProperties.getProperty(PROPERTY_FILES.CONTROL_TOTALS.ID_ATTRIBUTE_PROPERTY);
			
			String controlAttributesCSVFilePath = controlTotalProperties.getProperty(PROPERTY_FILES.CONTROL_TOTALS.CONTROLLED_ATTRIBUTES_PROPERTY);
			if (controlAttributesCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.CONTROL_TOTALS.CONTROLLED_ATTRIBUTES_PROPERTY + " property not found in " + controlTotalPropertiesFilePath); }
			GenstarCsvFile controlAttributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlAttributesCSVFilePath, true), false);
			
			String populationCSVFilePath = controlTotalProperties.getProperty(PROPERTY_FILES.CONTROL_TOTALS.POPULATION_DATA_PROPERTY);
			if (populationCSVFilePath == null) { throw new GenstarException(PROPERTY_FILES.CONTROL_TOTALS.POPULATION_DATA_PROPERTY + " property not found in " + controlTotalPropertiesFilePath); }
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
				idAttribute.setIdentity(true);
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
			
			return new GamaCSVFile(scope, exportFileName, INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.FIELD_DELIMITER, Types.STRING, false);
		} catch (Exception e) { 
			if (e instanceof GamaRuntimeException) { throw (GamaRuntimeException) e; }
			else { throw GamaRuntimeException.create(e, scope); }
		}
		
	}
	
	
	@operator(value = "ipf_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
	@doc(value = "generates a synthetic population from the input data provided by the CVS files using the IPF algorithm. The generated population can be passed to the 'create' statement to create agents.",
	returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
	special_cases = { "" },
	comment = "",
	examples = { @example(value = "list synthetic_population <- ipf_population('single_population_configuration.properties')",
		equals = "",
		test = false) }, see = { "ipf_compound_population, ipu_population, frequency_distribution_single_population" })
	public static IList generateIPFSinglePopulation(final IScope scope, final String ipfSinglePopulationPropertiesFilePath) {
		try {
			
			// 0. Load the properties file
			Properties ipfSinglePopulationPropeties = null;
			File populationPropertyFile = new File(FileUtils.constructAbsoluteFilePath(scope, ipfSinglePopulationPropertiesFilePath, true));
			try {
				FileInputStream propertyInputStream = new FileInputStream(populationPropertyFile);
				ipfSinglePopulationPropeties = new Properties();
				ipfSinglePopulationPropeties.load(propertyInputStream);
			} catch (FileNotFoundException e) {
				throw new GenstarException(e);
			} catch (IOException e) {
				throw new GenstarException(e);
			}
			
			/*
			// 1. Read the ATTRIBUTES_PROPERTIES then create the generator
			String attributesCSVFilePath = populationPropeties.getProperty(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.ATTRIBUTES_PROPERTY);
			if (attributesCSVFilePath == null) { throw new GenstarException(INPUT_DATA_FORMATS.PROPERTY_FILES.IPF_POPULATION.ATTRIBUTES_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			GenstarCsvFile attributesCSVFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			SampleBasedGenerator generator = new SampleBasedGenerator("single rule generator");
			AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
			
			
			// 2. Create the generation rule
			GamaGenstarUtils.createIpfGenerationRule(scope, generator, "sample data generation rule", populationPropeties);
			*/
			
			// 1. Generate the Ge* population
			IPopulation genstarIpfSinglePopulation = GamaGenstarUtils.generateIpfPopulation(scope, ipfSinglePopulationPropeties);

			// 3. Convert the Gen* population to GAMA population
			return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(genstarIpfSinglePopulation);
		} catch (GenstarException e) {
			throw GamaRuntimeException.error(e.getMessage(), scope);
		}
	}
	
	
	@operator(value = "ipf_compound_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
	@doc(value = "generates a synthetic population from the input data provided by the CVS files using the IPF algorithm. The generated population can be passed to the 'create' statement to create agents.",
	returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
	special_cases = { "" },
	comment = "",
	examples = { @example(value = "list synthetic_population <- ipf_compound_population('compound_population_configuration.properties')",
		equals = "",
		test = false) }, see = { "ipf_population, ipu_population, frequency_distribution_population" })
	public static IList generateIPFCompoundPopulation(final IScope scope, final String ipfCompoundPopulationPropertiesFilePath) {
		
		try {
			// 0. Load the properties file
			Properties ipfCompoundPopulationProperties = null;
			File ipfCompoundPopulationPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, ipfCompoundPopulationPropertiesFilePath, true));
			try {
				FileInputStream propertyInputStream = new FileInputStream(ipfCompoundPopulationPropertiesFile);
				ipfCompoundPopulationProperties = new Properties();
				ipfCompoundPopulationProperties.load(propertyInputStream);
			} catch (FileNotFoundException e) {
				throw new GenstarException(e);
			} catch (IOException e) {
				throw new GenstarException(e);
			}
			
			
			// 1. Generate the Gen* population
			IPopulation genstarIpfCompoundPopulation = GamaGenstarUtils.generateIpfPopulation(scope, ipfCompoundPopulationProperties);
			
			// 2. Convert the Gen* population to GAMA population
			return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(genstarIpfCompoundPopulation);
		} catch (GenstarException e) {
			throw GamaRuntimeException.error(e.getMessage(), scope);
		}
	}
	
	
	@operator(value = "ipu_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
	@doc(value = "generates a synthetic population using the IPU algorithm. The generated population can be passed to the 'create' statement to create agents.",
	returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
	special_cases = { "" },
	comment = "",
	examples = { @example(value = "list synthetic_population <- ipu_population('ipu_population_configuration.properties')",
		equals = "",
		test = false) }, see = { "ipf_population, ipf_compound_population, frequency_distribution_population" })
	public static IList generateIpuPopulation(final IScope scope, final String ipuPopulationPropertiesFilePath) {
		
		try {
			// 0. Load the properties file
			Properties ipuPopulationProperties = null;
			File ipuPopulationPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, ipuPopulationPropertiesFilePath, true));
			try {
				FileInputStream propertyInputStream = new FileInputStream(ipuPopulationPropertiesFile);
				ipuPopulationProperties = new Properties();
				ipuPopulationProperties.load(propertyInputStream);
			} catch (FileNotFoundException e) {
				throw new GenstarException(e);
			} catch (IOException e) {
				throw new GenstarException(e);
			}
			
			// 1. generate the Gen* population
			IPopulation genstarIpuPopulation = GamaGenstarUtils.generateIpuPopulation(scope, ipuPopulationProperties);
			
			// 2. convert the Gen* population to GAMA population
			return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(genstarIpuPopulation);
		} catch (GenstarException ge) {
			throw GamaRuntimeException.error(ge.getMessage(), scope);
		}
	}
	
	
	@operator(value = "random_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
	@doc(value = "generates a random (single) synthetic population using the configuration information in the populationConfigurationFile property file.",
	returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
	special_cases = { "" },
	comment = "The property file contains the following properties:",
	examples = { @example(value = "list synthetic_population <- random_population('single_population_configuration.properties')",
		equals = "",
		test = false) }, see = { "random_compound_population" })
	public static IList generateRandomSinglePopulation(final IScope scope, final String populationPropertiesFilePath) {
		try {
			// 0. Load the properties file
			Properties populationProperties = null;
			File populationPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, populationPropertiesFilePath, true));
			try {
				FileInputStream propertyInputStream = new FileInputStream(populationPropertiesFile);
				populationProperties = new Properties();
				populationProperties.load(propertyInputStream);
			} catch (FileNotFoundException e) {
				throw new GenstarException(e);
			} catch (IOException e) {
				throw new GenstarException(e);
			}
			
			// 1. Read the properties
			String populationName = populationProperties.getProperty(PROPERTY_FILES.RANDOM_SINGLE_POPULATION.POPULATION_NAME_PROPERTY);
			if (populationName == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_SINGLE_POPULATION.POPULATION_NAME_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			
			String attributesFileProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_SINGLE_POPULATION.ATTRIBUTES_PROPERTY);
			if (attributesFileProperty == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_SINGLE_POPULATION.ATTRIBUTES_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			GenstarCsvFile attributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, attributesFileProperty, true), true);
			
			String nbOfEntitiesProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_SINGLE_POPULATION.NB_OF_ENTITIES_PROPERTY);
			if (nbOfEntitiesProperty == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_SINGLE_POPULATION.NB_OF_ENTITIES_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			int nbOfEntities = Integer.parseInt(nbOfEntitiesProperty);
			if (nbOfEntities <= 0) { throw new GenstarException("Value of " + nbOfEntitiesProperty + " property must be a positive integer"); }

			
			// 2. Generate the population
			IPopulation generatedPopulation = GenstarUtils.generateRandomSinglePopulation(populationName, attributesFile, nbOfEntities);
			
			
			// 3. Convert the population to IList
			return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(generatedPopulation);
		} catch (GenstarException e) {
			throw GamaRuntimeException.error(e.getMessage(), scope);
		}
	}
	
	
	@operator(value = "random_compound_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
	@doc(value = "generates a random (compound) synthetic population using the configuration information in the populationConfigurationFile property file.",
	returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
	special_cases = { "" },
	comment = "The property file contains the following properties:",
	examples = { @example(value = "list synthetic_population <- random_compound_population('compound_population_configuration.properties')",
		equals = "",
		test = false) }, see = { "random_population" })
	public static IList generateRandomCompoundPopulation(final IScope scope, final String populationConfigurationFile) {
		
		try {
			// 0. Load the properties file
			Properties populationProperties = null;
			File populationPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, populationConfigurationFile, true));
			try {
				FileInputStream propertyInputStream = new FileInputStream(populationPropertiesFile);
				populationProperties = new Properties();
				populationProperties.load(propertyInputStream);
			} catch (FileNotFoundException e) {
				throw new GenstarException(e);
			} catch (IOException e) {
				throw new GenstarException(e);
			}

			
			// 1. Read the properties
			String groupPopulationName = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.GROUP_POPULATION_NAME_PROPERTY);
			if (groupPopulationName == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.GROUP_POPULATION_NAME_PROPERTY + " property not found in " + populationConfigurationFile); }
			
			String groupAttributesFileProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.GROUP_ATTRIBUTES_PROPERTY);
			if (groupAttributesFileProperty == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.GROUP_ATTRIBUTES_PROPERTY + " property not found in " + populationConfigurationFile); }
			GenstarCsvFile groupAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, groupAttributesFileProperty, true), true);
			
			String componentPopulationName = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.COMPONENT_POPULATION_NAME_PROPERTY);
			if (componentPopulationName == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.COMPONENT_POPULATION_NAME_PROPERTY + " property not found in " + populationConfigurationFile); }
			
			String componentAttributesFileProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.COMPONENT_ATTRIBUTES_PROPERTY);
			if (componentAttributesFileProperty == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.COMPONENT_ATTRIBUTES_PROPERTY ); }
			GenstarCsvFile componentAttributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, componentAttributesFileProperty, true), true);
			
			String nbOfGroupEntitiesProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.NB_OF_GROUP_ENTITIES_PROPERTY);
			if (nbOfGroupEntitiesProperty == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.NB_OF_GROUP_ENTITIES_PROPERTY + " property not found in " + populationConfigurationFile); }
			int nbOfGroupEntities = Integer.parseInt(nbOfGroupEntitiesProperty);
			if (nbOfGroupEntities <= 0) { throw new GenstarException("Value of " + nbOfGroupEntitiesProperty + " property must be a positive integer"); }
			
			String groupIdAttributeNameOnGroupEntity = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
			if (groupIdAttributeNameOnGroupEntity == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + " property not found in " + populationConfigurationFile); }
			
			String groupIdAttributeNameOnComponentEntity = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
			if (groupIdAttributeNameOnComponentEntity == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + " property not found in " + populationConfigurationFile); }
			
			String groupSizeAttributeName = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.GROUP_SIZE_ATTRIBUTE_PROPERTY);
			if (groupSizeAttributeName == null) { throw new GenstarException(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION.GROUP_SIZE_ATTRIBUTE_PROPERTY + " property not found in " + populationConfigurationFile); }
					
			
			// 2. Generate the population
			IPopulation generatedPopulation = GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, componentAttributesFile, 
					groupIdAttributeNameOnGroupEntity, groupIdAttributeNameOnComponentEntity, groupSizeAttributeName, nbOfGroupEntities);
			
			return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(generatedPopulation);			
		} catch (GenstarException e) {
			throw GamaRuntimeException.error(e.getMessage(), scope);
		}
	}

	
	@operator(value = "population_to_csv", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
	@doc(value = "writes the synthetic population(s) to CSV file(s).",
	returns = "",
	special_cases = { "" },
	comment = "",
	examples = { @example(value = "map outputFilePaths <- population_to_csv(gamaPopulation, populationOutputFilePaths, populationAttributesFilePaths, populationIdAttributes)",
		equals = "",
		test = false) }, see = { "" })
	public static final Map<String, String> writePopulationsToCsvFiles(final IScope scope, final IList gamaPopulation, final Map<String, String> populationOutputFilePaths, 
			final Map<String, String> populationAttributesFilePaths, final Map<String, String> populationIdAttributes) {
		
		try {
			// build population attributes
			Map<String, List<AbstractAttribute>> populationAttributes = new HashMap<String, List<AbstractAttribute>>();
			for (String populationName : populationAttributesFilePaths.keySet()) {
				ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
				
				GenstarCsvFile attributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, populationAttributesFilePaths.get(populationName), true), true);
				AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
				
				populationAttributes.put(populationName, generator.getAttributes());
				
				
				String idAttributeName = populationIdAttributes.get(populationName);
				if (idAttributeName != null) {
					AbstractAttribute idAttribute = generator.getAttributeByNameOnData(idAttributeName);
					if (idAttribute == null) { throw new GenstarException(idAttributeName + " is not a valid attribute."); } // TODO implement IWithAttributes.getAttributesStringRepresentation
					idAttribute.setIdentity(true);
				}
			}
			
			// re-build populationOutputFilePaths
			Map<String, String> rebuiltPopulationOutputFilePaths = new HashMap<String, String>();
			for (Map.Entry<String, String> populationOutputFilePathsEntry : populationOutputFilePaths.entrySet()) {
				rebuiltPopulationOutputFilePaths.put(populationOutputFilePathsEntry.getKey(), FileUtils.constructAbsoluteFilePath(scope, populationOutputFilePathsEntry.getValue(), false));
			}
			
			// convert GAMA synthetic populations to Gen* synthetic populations
			IPopulation genstarPopulation = GamaGenstarUtils.convertGamaPopulationToGenstarPopulation(null, gamaPopulation, populationAttributes);
			
			// write Gen* synthetic populations to CSV files
			return GenstarUtils.writePopulationToCsvFile(genstarPopulation, rebuiltPopulationOutputFilePaths);
		} catch (GenstarException e) {
			throw GamaRuntimeException.error(e.getMessage(), scope);
		}
	}
	
	
	private static List<Integer> analyseIpfPopulation(final IScope scope, final IList gamaPopulation, final String attributesFilePath, 
			final String controlledAttributesListFilePath, final String controlTotalsFilePath) throws GenstarException {
		// convert GAMA population to Gen* population
		String populationName = (String)gamaPopulation.get(0); // first element is the population name

		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
		
		GenstarCsvFile attributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, attributesFilePath, true), true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);

		Map<String, List<AbstractAttribute>> populationsAttributes = new HashMap<String, List<AbstractAttribute>>();
		populationsAttributes.put(populationName, generator.getAttributes());
		IPopulation genstarPopulation = GamaGenstarUtils.convertGamaPopulationToGenstarPopulation(null, gamaPopulation, populationsAttributes);
		
		// do the analysis
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlTotalsFilePath, true), false);
		GenstarCsvFile controlledAttributesListFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlledAttributesListFilePath, true), false);
		return IpfUtils.analyseIpfPopulation(genstarPopulation, controlledAttributesListFile, controlTotalsFile);
	}
	
	
	@operator(value = "analyse_ipf_population_to_console", type = IType.LIST, content_type = IType.INT, category = { IOperatorCategory.GENSTAR })
	@doc(value = "analyze a synthetic population with respect to the control totals then write analysis result to the GAMA console if necessary",
	returns = "",
	special_cases = { "" },
	comment = "",
	examples = { @example(value = "list<int> analysisResult <- analyse_ipf_population_to_console(gamaPopulation, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath, writeResultToConsole)",
		equals = "",
		test = false) }, see = { "" })
	public static List<Integer> analyseIpfPopulation_ToConsole(final IScope scope, final IList gamaPopulation, final String attributesFilePath, 
			final String controlledAttributesListFilePath, final String controlTotalsFilePath) {
		
		try {
			
			// TODO analyseIpfPopulation need the information of ID attribute -> option 1: analyse compound Ipf population
			GenstarCsvFile controlTotalsFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlTotalsFilePath, true), false);
			List<Integer> generatedFrequencies = analyseIpfPopulation(scope, gamaPopulation, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath);
			
			// write analysis result to GAMA console
			GuiUtils.informConsole("Row format: (attribute name, attribute value)+, control total, generated total");
			int line = 0;
			for (List<String> controlTotalsRow : controlTotalsFile.getContent()) {
				StringBuffer aRow = new StringBuffer();
				for (String e : controlTotalsRow) { aRow.append(e); aRow.append(","); }
				aRow.append(generatedFrequencies.get(line));
				line++;
				
				GuiUtils.informConsole(aRow.toString());				
			}
			
			return generatedFrequencies;
		} catch (GenstarException e){
			throw GamaRuntimeException.error(e.getMessage(), scope);
		}
	}
	
	
	@operator(value = "analyse_ipf_population_to_file", type = IType.LIST, content_type = IType.INT, category = { IOperatorCategory.GENSTAR })
	@doc(value = "analyze a synthetic population with respect to the control totals then write analysis result to the GAMA console if necessary",
	returns = "",
	special_cases = { "" },
	comment = "",
	examples = { @example(value = "list<int> analysisResult <- analyse_ipf_population_to_file(gamaPopulation, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath, outputFilePath)",
		equals = "",
		test = false) }, see = { "" })
	public static List<Integer> analyseIpfPopulation_ToFile(final IScope scope, final IList gamaPopulation, final String attributesFilePath, 
			final String controlledAttributesListFilePath, final String controlTotalsFilePath, final String outputFilePath) {
		
		try {
			
			GenstarCsvFile controlTotalsFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlTotalsFilePath, true), false);
			List<Integer> generatedFrequencies = analyseIpfPopulation(scope, gamaPopulation, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath);

			// output file
			CsvWriter outputFileWriter = new CsvWriter(FileUtils.constructAbsoluteFilePath(scope, outputFilePath, false));
			
			// write analysis result to output file
			int line = 0;
			for (List<String> controlTotalsRow : controlTotalsFile.getContent()) {
				List<String> aRow = new ArrayList<String>();
				for (String e : controlTotalsRow) { aRow.add(e); }
				aRow.add(Integer.toString(generatedFrequencies.get(line)));
				line++;
				
				outputFileWriter.writeRecord(aRow.toArray(new String[0]));			
			}
			outputFileWriter.flush();
			outputFileWriter.close();
			
			return generatedFrequencies;
		} catch (GenstarException e){
			throw GamaRuntimeException.error(e.getMessage(), scope);
		} catch (IOException ioe) {
			throw GamaRuntimeException.error(ioe.getMessage(), scope);
		}
		
	}
	
	// TODO analyseIpfCompoundPopulation
	
	
	
	private static IList runGeneratorAndConvertGeneratedData(final ISyntheticPopulationGenerator generator) throws GenstarException {
		IList syntheticPopulation = GamaListFactory.create(Types.MAP);
		syntheticPopulation.add(GENSTAR_POPULATION);
		
		// 1. Generate the population
		IPopulation generatedPopulation = generator.generate();
		
		// 2. Convert the generated "data" to format understood by GAML "create" statement
		Map<String, Object> map;
		for (Entity entity : generatedPopulation.getEntities()) {
			
			map = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
			for (EntityAttributeValue eav : entity.getEntityAttributeValues()) {
				map.put(eav.getAttribute().getNameOnEntity(), GenstarGamaTypesConverter.convertGenstar2GamaType(eav.getAttributeValueOnEntity()));
			}
			
			map.put(GENSTAR_ENTITY, entity); // ... for traceback purpose
			
			syntheticPopulation.add(map);
		}
		
		return syntheticPopulation;
	}
}
