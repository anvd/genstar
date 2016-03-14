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

import com.google.common.collect.Sets;

import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.file.IGamaFile;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.extensions.genstar.IGamaPopulationsLinker;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.SampleData;
import ummisco.genstar.ipf.SampleEntity;
import ummisco.genstar.ipf.SampleEntityPopulation;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.IMultipleRulesGenerator;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.MultipleRulesGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.SyntheticPopulation;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.util.CsvWriter;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarFactoryUtils;

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
			String populationName = populationProperties.getProperty(GenstarFactoryUtils.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
			if (populationName == null) { throw new GenstarException(GenstarFactoryUtils.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			
			String attributesCSVFilePath = populationProperties.getProperty(GenstarFactoryUtils.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
			if (attributesCSVFilePath == null) { throw new GenstarException(GenstarFactoryUtils.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			GenstarCSVFile attributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			
			String generationRulesCSVFilePath = populationProperties.getProperty(GenstarFactoryUtils.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.GENERATION_RULES_PROPERTY);
			if (generationRulesCSVFilePath == null) { throw new GenstarException(GenstarFactoryUtils.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.GENERATION_RULES_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			GenstarCSVFile generationRulesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, generationRulesCSVFilePath, true), true);
			
			String nbOfEntitiesProperty = populationProperties.getProperty(GenstarFactoryUtils.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.NUMBER_OF_ENTITIES);
			if (nbOfEntitiesProperty == null) { throw new GenstarException(GenstarFactoryUtils.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.NUMBER_OF_ENTITIES + " property not found in " + populationPropertiesFilePath); }
			int nbOfEntities = 0;
			try {
				nbOfEntities = Integer.parseInt(nbOfEntitiesProperty);
			} catch (NumberFormatException nfe) {
				throw new GenstarException(GenstarFactoryUtils.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.NUMBER_OF_ENTITIES + " property can not contain a negative integer.");
			}
			if (nbOfEntities <= 0) { throw new GenstarException("Value of " + nbOfEntitiesProperty + " property must be a positive integer"); }
			
			
			// 2. Create the generator
			IMultipleRulesGenerator generator = new MultipleRulesGenerator("Population Generator", nbOfEntities);
			generator.setPopulationName(populationName);
			GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
			GamaGenstarFactoryUtils.createGenerationRulesFromCSVFile(scope, generator, generationRulesCSVFile);
			
			return convertGenstarPopulationToGamaPopulation(generator.generate());
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
			GenstarCSVFile attributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			GenstarCSVFile sampleDataCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, sampleDataCSVFilePath, true), true);
			GenstarCSVFile distributionFormatCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, distributionFormatCSVFilePath, true), true);
			
			// 1. create the generator then add attributes
			ISyntheticPopulationGenerator generator = new MultipleRulesGenerator("dummy generator", 10);
			GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
			
			// 2. read the distribution file format to know the input and output attributes
			List<String> attributeNamesOnData = distributionFormatCSVFile.getHeaders();
			List<String> attributeNamesOnDataWithoutInputOutput = new ArrayList<String>();
			if (attributeNamesOnData.size() < 1) { throw new GenstarException("First line of distribution format file must contain at least 2 elements. File: " + distributionFormatCSVFile.getPath() + "."); }
			for (int index=0; index < attributeNamesOnData.size(); index++) {
				String attribute = attributeNamesOnData.get(index);
				StringTokenizer t = new StringTokenizer(attribute, GenstarFactoryUtils.CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.ATTRIBUTE_NAME_TYPE_DELIMITER);
				if (t.countTokens() != 2) { throw new GenstarException("Element must have format attribute_name:Input or attribute_name:Output. File: " + distributionFormatCSVFile.getPath() + "."); }
				
				String attributeName = t.nextToken();
				if (!generator.containAttribute(attributeName)) { throw new GenstarException("Attribute '" + attributeName + "' is not defined. File: " + distributionFormatCSVFile.getPath() + "."); }
				attributeNamesOnDataWithoutInputOutput.add(attributeName);
				
				String attributeType = t.nextToken();
				if (!attributeType.equals(GenstarFactoryUtils.CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.INPUT_ATTRIBUTE) && !attributeType.equals(GenstarFactoryUtils.CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.OUTPUT_ATTRIBUTE)) {
					throw new GenstarException("Attribute " + attributeName + " must either be Input or Output. File: " + distributionFormatCSVFile.getPath() + ".");
				}
			}
			
			// 3. create a frequency generation rule from the sample data then write the generation rule to file.
			FrequencyDistributionGenerationRule fdGenerationRule = GenstarFactoryUtils.createFrequencyDistributionFromSampleData(generator, distributionFormatCSVFile, sampleDataCSVFile);
			
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
			Collections.sort(sortedAttributeValueFrequencies, new GenstarFactoryUtils.AttributeValuesFrequencyComparator(generationRuleAttributes));
			
			for (AttributeValuesFrequency avf : sortedAttributeValueFrequencies) {
				String[] row = new String[attributeNamesOnData.size() + 1];

				// build the string representation of each set of attribute values
				for (int i=0; i<generationRuleAttributes.size(); i++) {
					AttributeValue av = avf.getAttributeValue(generationRuleAttributes.get(i));
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
			
			return new GamaCSVFile(scope, exportFileName, GenstarFactoryUtils.CSV_FILE_FORMATS.ATTRIBUTE_METADATA.FIELD_DELIMITER, Types.STRING, true);
		} catch (final Exception e) {
			if (e instanceof GamaRuntimeException) { throw (GamaRuntimeException) e; }
			else { throw GamaRuntimeException.create(e, scope); }
		}
	}
	
	
	@operator(value = "control_totals", type = IType.FILE, category = { IOperatorCategory.GENSTAR })
	@doc(value = "generates the control totals then save them to a CSV file",
		returns = "a reference to the file containing the resulting control totals",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "file result_file <- control_totals('controlTotalPropertiesFilePath.properties', 'resultControlsTotalFilePath.csv')",
			equals = "a file containing the resulting control totals and locating at the path specified by resultControlsTotalFilePath.csv",
			test = false) }, see = { "population_from_csv", "link_populations" })
	public static IGamaFile generateControlTotals(final IScope scope, final String controlTotalPropertiesFilePath, final String resultControlsTotalFilePath) {
		
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
			String attributesCSVFilePath = controlTotalProperties.getProperty(GenstarFactoryUtils.CONTROL_TOTALS_PROPERTIES.ATTRIBUTES_PROPERTY);
			if (attributesCSVFilePath == null) { throw new GenstarException(GenstarFactoryUtils.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found in " + controlTotalPropertiesFilePath); }
			GenstarCSVFile attributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			
			String idAttributeNameOnData = controlTotalProperties.getProperty(GenstarFactoryUtils.CONTROL_TOTALS_PROPERTIES.ID_ATTRIBUTE_PROPERTY);
			
			String controlAttributesCSVFilePath = controlTotalProperties.getProperty(GenstarFactoryUtils.CONTROL_TOTALS_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
			if (controlAttributesCSVFilePath == null) { throw new GenstarException(GenstarFactoryUtils.CONTROL_TOTALS_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY + " property not found in " + controlTotalPropertiesFilePath); }
			GenstarCSVFile controlAttributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, controlAttributesCSVFilePath, true), false);
			
			String populationCSVFilePath = controlTotalProperties.getProperty(GenstarFactoryUtils.CONTROL_TOTALS_PROPERTIES.POPULATION_DATA_PROPERTY);
			if (populationCSVFilePath == null) { throw new GenstarException(GenstarFactoryUtils.CONTROL_TOTALS_PROPERTIES.POPULATION_DATA_PROPERTY + " property not found in " + controlTotalPropertiesFilePath); }
			GenstarCSVFile populationCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, populationCSVFilePath, true), true);
			
			int controlAttributesRows = controlAttributesCSVFile.getRows();
			if (controlAttributesCSVFile.getColumns() != 1 || controlAttributesRows < 2) {
				throw new GenstarException("Invalid controlAttributesCSVFile format. File: " + controlAttributesCSVFile);
			}
			Set<String> controlledAttributeNames = new HashSet<String>();
			for (List<String> row : controlAttributesCSVFile.getContent()) { controlledAttributeNames.add(row.get(0)); }
			
			// 2. create the generator then add attributes
			ISyntheticPopulationGenerator generator = new MultipleRulesGenerator("dummy generator", 10);
			GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
			
			// 3. process ID attribute
			if (idAttributeNameOnData != null) {
				AbstractAttribute idAttribute = generator.getAttributeByNameOnData(idAttributeNameOnData);
				if (idAttribute == null) { throw new GenstarException(idAttributeNameOnData + " is not recognized as an attribute a valid"); }
				idAttribute.setIdentity(true);
			}
			
			// 4. ensure that all controlled attributes exist
			for (String attributeNameOnData : controlledAttributeNames) { 
				if (!generator.containAttribute(attributeNameOnData)) {
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
					attributesPossibleValues.add(controlledAttribute.values());
				}
				
				Set<List<AttributeValue>> cartesianSet = Sets.cartesianProduct(attributesPossibleValues);

				for (List<AttributeValue> catesian : cartesianSet) {
					attributeValuesFrequencies.add(new AttributeValuesFrequency(GenstarFactoryUtils.buildAttributeValueMap(controlledAttributes, catesian)));
				}
			}
			
			// 7. calculate frequencies
			SampleData populationData = new SampleData("dummy population", generator.getAttributes(), populationCSVFile);
			for (SampleEntity sampleEntity : populationData.getSampleEntityPopulation().getSampleEntities()) {
				for (AttributeValuesFrequency avf : attributeValuesFrequencies) {
					if (sampleEntity.isMatched(avf.getAttributeValuesWithNamesOnEntityAsKey())) { avf.setFrequency(avf.getFrequency() + 1); }
				}
			}
			
			// 8. build CSV file content
			List<String[]> csvFileContents = new ArrayList<String[]>();
			for (AttributeValuesFrequency avf : attributeValuesFrequencies) {
				String[] row = new String[(avf.getAttributes().size() * 2) + 1];

				// build the string representation of each set of attribute values
				int i=0;
				for (Map.Entry<AbstractAttribute, AttributeValue> entry : avf.getAttributeValues().entrySet()) {
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
			
			return new GamaCSVFile(scope, exportFileName, GenstarFactoryUtils.CSV_FILE_FORMATS.ATTRIBUTE_METADATA.FIELD_DELIMITER, Types.STRING, false);
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
		test = false) }, see = { "frequency_distribution_single_population" })
	public static IList generateIPFSinglePopulation(final IScope scope, final String populationPropertiesFilePath) {
		try {
			
			// 0. Load the properties file
			Properties populationPropeties = null;
			File populationPropertyFile = new File(FileUtils.constructAbsoluteFilePath(scope, populationPropertiesFilePath, true));
			try {
				FileInputStream propertyInputStream = new FileInputStream(populationPropertyFile);
				populationPropeties = new Properties();
				populationPropeties.load(propertyInputStream);
			} catch (FileNotFoundException e) {
				throw new GenstarException(e);
			} catch (IOException e) {
				throw new GenstarException(e);
			}
			
			
			// 1. Read the properties
			String populationName = populationPropeties.getProperty(GenstarFactoryUtils.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
			if (populationName == null) { throw new GenstarException(GenstarFactoryUtils.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + " property not found in " + populationPropertiesFilePath); }

			String attributesCSVFilePath = populationPropeties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
			if (attributesCSVFilePath == null) { throw new GenstarException(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			GenstarCSVFile attributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			
			
			// 2. Create the generator
			ISingleRuleGenerator generator = new SingleRuleGenerator("single rule generator");
			generator.setPopulationName(populationName);
			GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
			
			
			// 3. Create the generation rule
			GamaGenstarFactoryUtils.createSampleDataGenerationRule(scope, generator, "sample data generation rule", populationPropeties);
			
			
//			return runGeneratorAndConvertGeneratedData(generator); TODO remove this line
			return convertGenstarPopulationToGamaPopulation(generator.generate());
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
		test = false) }, see = { "ipf_population, frequency_distribution_population" })
	public static IList generateIPFCompoundPopulation(final IScope scope, final String populationPropertiesFilePath) {
		
		try {
			// 0. Load the properties file
			Properties sampleDataProperties = null;
			File sampleDataPropertyFile = new File(FileUtils.constructAbsoluteFilePath(scope, populationPropertiesFilePath, true));
			try {
				FileInputStream propertyInputStream = new FileInputStream(sampleDataPropertyFile);
				sampleDataProperties = new Properties();
				sampleDataProperties.load(propertyInputStream);
			} catch (FileNotFoundException e) {
				throw new GenstarException(e);
			} catch (IOException e) {
				throw new GenstarException(e);
			}
			
			// 1. Create the generator
			String attributesCSVFilePath = sampleDataProperties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
			GenstarCSVFile attributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			ISingleRuleGenerator generator = new SingleRuleGenerator("single rule generator");
			GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
			
			// 2. Create the generation rule
			GamaGenstarFactoryUtils.createSampleDataGenerationRule(scope, generator, "sample data generation rule", sampleDataProperties);
	
			return convertGenstarPopulationToGamaPopulation(generator.generate());
		} catch (GenstarException e) {
			throw GamaRuntimeException.error(e.getMessage(), scope);
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
			String populationName = populationProperties.getProperty(GenstarFactoryUtils.RANDOM_SINGLE_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY);
			if (populationName == null) { throw new GenstarException(GenstarFactoryUtils.RANDOM_SINGLE_POPULATION_PROPERTIES.POPULATION_NAME_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			
			String attributesFileProperty = populationProperties.getProperty(GenstarFactoryUtils.RANDOM_SINGLE_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
			if (attributesFileProperty == null) { throw new GenstarException(GenstarFactoryUtils.RANDOM_SINGLE_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			GenstarCSVFile attributesFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, attributesFileProperty, true), true);
			
			String nbOfEntitiesProperty = populationProperties.getProperty(GenstarFactoryUtils.RANDOM_SINGLE_POPULATION_PROPERTIES.NB_OF_ENTITIES_PROPERTY);
			if (nbOfEntitiesProperty == null) { throw new GenstarException(GenstarFactoryUtils.RANDOM_SINGLE_POPULATION_PROPERTIES.NB_OF_ENTITIES_PROPERTY + " property not found in " + populationPropertiesFilePath); }
			int nbOfEntities = Integer.parseInt(nbOfEntitiesProperty);
			if (nbOfEntities <= 0) { throw new GenstarException("Value of " + nbOfEntitiesProperty + " property must be a positive integer"); }

			
			// 2. Generate the population
			ISyntheticPopulation generatedPopulation = GenstarFactoryUtils.generateRandomSinglePopulation(populationName, attributesFile, nbOfEntities);
			
			
			// 3. Convert the population to IList
			return convertGenstarPopulationToGamaPopulation(generatedPopulation);
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
			String groupPopulationName = populationProperties.getProperty(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY);
			if (groupPopulationName == null) { throw new GenstarException(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_NAME_PROPERTY + " property not found in " + populationConfigurationFile); }
			
			String groupAttributesFileProperty = populationProperties.getProperty(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
			if (groupAttributesFileProperty == null) { throw new GenstarException(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY + " property not found in " + populationConfigurationFile); }
			GenstarCSVFile groupAttributesFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, groupAttributesFileProperty, true), true);
			
			String componentPopulationName = populationProperties.getProperty(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
			if (componentPopulationName == null) { throw new GenstarException(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY + " property not found in " + populationConfigurationFile); }
			
			String componentAttributesFileProperty = populationProperties.getProperty(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
			if (componentAttributesFileProperty == null) { throw new GenstarException(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY ); }
			GenstarCSVFile componentAttributesFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, componentAttributesFileProperty, true), true);
			
			String nbOfGroupEntitiesProperty = populationProperties.getProperty(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.NB_OF_GROUP_ENTITIES_PROPERTY);
			if (nbOfGroupEntitiesProperty == null) { throw new GenstarException(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.NB_OF_GROUP_ENTITIES_PROPERTY + " property not found in " + populationConfigurationFile); }
			int nbOfGroupEntities = Integer.parseInt(nbOfGroupEntitiesProperty);
			if (nbOfGroupEntities <= 0) { throw new GenstarException("Value of " + nbOfGroupEntitiesProperty + " property must be a positive integer"); }
			
			String groupIdAttributeNameOnGroupEntity = populationProperties.getProperty(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY);
			if (groupIdAttributeNameOnGroupEntity == null) { throw new GenstarException(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY + " property not found in " + populationConfigurationFile); }
			
			String groupIdAttributeNameOnComponentEntity = populationProperties.getProperty(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY);
			if (groupIdAttributeNameOnComponentEntity == null) { throw new GenstarException(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY + " property not found in " + populationConfigurationFile); }
			
			String groupSizeAttributeName = populationProperties.getProperty(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_SIZE_ATTRIBUTE_PROPERTY);
			if (groupSizeAttributeName == null) { throw new GenstarException(GenstarFactoryUtils.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_SIZE_ATTRIBUTE_PROPERTY + " property not found in " + populationConfigurationFile); }
					
			
			// 2. Generate the population
			ISyntheticPopulation generatedPopulation = GenstarFactoryUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, componentAttributesFile, 
					groupIdAttributeNameOnGroupEntity, groupIdAttributeNameOnComponentEntity, groupSizeAttributeName, nbOfGroupEntities);
			
			return convertGenstarPopulationToGamaPopulation(generatedPopulation);			
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
				ISyntheticPopulationGenerator generator = new SingleRuleGenerator("dummy generator");
				
				GenstarCSVFile attributesFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, populationAttributesFilePaths.get(populationName), true), true);
				GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesFile);
				
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
			ISyntheticPopulation genstarPopulation = convertGamaPopulationToGenstarPopulation(null, gamaPopulation, populationAttributes);
			
			// write Gen* synthetic populations to CSV files
			return GenstarFactoryUtils.writePopulationToCSVFile(genstarPopulation, rebuiltPopulationOutputFilePaths);
		} catch (GenstarException e) {
			throw GamaRuntimeException.error(e.getMessage(), scope);
		}
	}
	
	
	private static IList runGeneratorAndConvertGeneratedData(final ISyntheticPopulationGenerator generator) throws GenstarException {
		IList syntheticPopulation = GamaListFactory.create(Types.MAP);
		syntheticPopulation.add(GENSTAR_POPULATION);
		
		// 1. Generate the population
		ISyntheticPopulation generatedPopulation = generator.generate();
		
		// 2. Convert the generated "data" to format understood by GAML "create" statement
		Map<String, Object> map;
		for (Entity entity : generatedPopulation.getEntities()) {
			
			map = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
			for (EntityAttributeValue eav : entity.getEntityAttributeValues().values()) {
				map.put(eav.getAttribute().getNameOnEntity(), GenstarGamaTypesConverter.convertGenstar2GamaType(eav.getAttributeValueOnEntity()));
			}
			
			map.put(GENSTAR_ENTITY, entity); // ... for traceback purpose
			
			syntheticPopulation.add(map);
		}
		
		return syntheticPopulation;
	}

	private static ISyntheticPopulation convertGamaPopulationToGenstarPopulation(final Entity host, final IList gamaPopulation, 
			final Map<String, List<AbstractAttribute>> populationsAttributes) throws GenstarException {
		if (gamaPopulation == null) { throw new GenstarException("Parameter gamaPopulation can not be null"); }
		if (gamaPopulation.size() < 3) { throw new GenstarException("gamaPopulation is not a valid gama synthetic population format"); }
		if (populationsAttributes == null) { throw new GenstarException("Parameter populationsAttributes can not be null"); }
		
		// 1. First three elements of a GAMA synthetic population
		String populationName =  (String)gamaPopulation.get(0); // first element is the population name
		Map<String, String> groupReferences = (Map<String, String>)gamaPopulation.get(1); // second element contains references to "group" agents
		Map<String, String> componentReferences = (Map<String, String>)gamaPopulation.get(2); // third element contains references to "component" agents
		// what to do with group and component references?
		
		ISyntheticPopulation genstarPopulation = null; // 2. create the genstar population appropriately
		if (host == null) {
			genstarPopulation = new SyntheticPopulation(populationName, populationsAttributes.get(populationName));
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
			IList<IList> gamaComponentPopulations = (IList<IList>) gamaEntityInitValues.get(ISyntheticPopulation.class);
			if (gamaComponentPopulations == null) { // without Genstar component populations
				mirrorGamaEntityInitValues = gamaEntityInitValues;
			} else {
				mirrorGamaEntityInitValues = GamaMapFactory.create();
				mirrorGamaEntityInitValues.putAll(gamaEntityInitValues);
				mirrorGamaEntityInitValues.remove(ISyntheticPopulation.class);
			}
			
			// convert string representation (of the initial values) to AttributeValue
			Map<String, AttributeValue> attributeValuesOnEntity = new HashMap<String, AttributeValue>();
			for (String attributeNameOnEntity : mirrorGamaEntityInitValues.keySet()) {
				attributeValuesOnEntity.put(attributeNameOnEntity, 
						GenstarGamaTypesConverter.convertGama2GenstarType(genstarPopulation.getAttributebyNameOnEntity(attributeNameOnEntity), 
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
	
	private static IList convertGenstarPopulationToGamaPopulation(final ISyntheticPopulation genstarPopulation) throws GenstarException {
		IList gamaPopulation = GamaListFactory.create();
		
		// First three elements of a GAMA synthetic population
		gamaPopulation.add(genstarPopulation.getName()); // first element is the population name
		gamaPopulation.add(genstarPopulation.getGroupReferences()); // second element contains references to "group" agents
		gamaPopulation.add(genstarPopulation.getComponentReferences()); // third element contains references to "component" agents
		
		// Convert the genstar population to the format understood by GAML "genstar_create" statement
		GamaMap map;
		for (Entity entity : genstarPopulation.getEntities()) {
			map = GamaMapFactory.create();
			for (EntityAttributeValue eav : entity.getEntityAttributeValues().values()) {
				map.put(eav.getAttribute().getNameOnEntity(), GenstarGamaTypesConverter.convertGenstar2GamaType(eav.getAttributeValueOnEntity()));
			}

			gamaPopulation.add(map);
			
			// Recursively convert genstar component populations
			IList componentPopulations = GamaListFactory.create();
			for (ISyntheticPopulation componentPopulation : entity.getComponentPopulations()) {
				componentPopulations.add(convertGenstarPopulationToGamaPopulation(componentPopulation));
			}
			
			if (!componentPopulations.isEmpty()) { map.put(ISyntheticPopulation.class, componentPopulations); }
		}

		return gamaPopulation;
	}
	
	
}
