package ummisco.genstar.gama;

import static msi.gama.common.interfaces.IKeyword.GENSTAR_ENTITY;
import static msi.gama.common.interfaces.IKeyword.GENSTAR_POPULATION;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.shape.ILocation;
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
import msi.gama.util.file.CsvWriter;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.file.IGamaFile;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.extensions.genstar.IGamaPopulationsLinker;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.IMultipleRulesGenerator;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.MultipleRulesGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarFactoryUtils;

/**
 * A set of Genstar-related operators.
 */
public abstract class Genstars {
	
		
	@operator(value = "population_from_csv", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
	@doc(value = "generates a synthetic population from the input data provided by the CVS files. The generated population can be passed to the 'create' statement to create agents.",
		returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list synthetic_population <- population_from_csv('Attributes.csv', 'GenerationRules.csv', 14000)",
			equals = "",
			test = false) }, see = { "frequency_distribution_from_sample", "link_populations" })
	public static IList generatePopulationFromCSV_Data(final IScope scope, final String attributesCSVFilePath, final String generationRulesCSVFilePath, final int nbOfAgents) {

		try {
			// Verification of attributes
			GenstarCSVFile attributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			GenstarCSVFile generationRulesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, generationRulesCSVFilePath, true), true);
			if (nbOfAgents <= 0) { GamaRuntimeException.error("Number of agents must be positive", scope); }

			
			// 1. Create the generator
			IMultipleRulesGenerator generator = new MultipleRulesGenerator("Population Generator", nbOfAgents);
			GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
			GamaGenstarFactoryUtils.createGenerationRulesFromCSVFile(scope, generator, generationRulesCSVFile);
			
			return runGeneratorAndConvertGeneratedData(generator);
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
		examples = { @example(value = "string result_file <- frequency_distribution_from_sample('Attributes.csv', 'SampleData.csv', 'DistributionFormat.csv', 'ResultingDistribution.csv')",
			equals = "a string representing the path to the 'ResultingDistribution.csv' CSV file that contains the resulting frequency distribution generation rule",
			test = false) }, see = { "population_from_csv", "link_populations" })
	public static IGamaFile<IMatrix<Object>, Object, ILocation, Object> generateFrequencyDistribution(final IScope scope, final String attributesCSVFilePath, 
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
					row[i] = av.toCSVString();
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
	
	
	@operator(value = "ipf_single_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
	@doc(value = "generates a synthetic population from the input data provided by the CVS files using the IPF algorithm. The generated population can be passed to the 'create' statement to create agents.",
	returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
	special_cases = { "" },
	comment = "",
	examples = { @example(value = "list synthetic_population <- ipf_single_population('single_population_configuration.properties')",
		equals = "",
		test = false) }, see = { "population_from_csv" })
	public static IList generateIPFSinglePopulation(final IScope scope, final String configurationPropertiesFilePath) {
		try {
			
			// 0. Load the properties file
			Properties sampleDataPropeties = null;
			File sampleDataPropertyFile = new File(FileUtils.constructAbsoluteFilePath(scope, configurationPropertiesFilePath, true));
			try {
				FileInputStream propertyInputStream = new FileInputStream(sampleDataPropertyFile);
				sampleDataPropeties = new Properties();
				sampleDataPropeties.load(propertyInputStream);
			} catch (FileNotFoundException e) {
				throw new GenstarException(e);
			} catch (IOException e) {
				throw new GenstarException(e);
			}
			
			// 1. Create the generator
			String attributesCSVFilePath = sampleDataPropeties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.ATTRIBUTES_PROPERTY);
			GenstarCSVFile attributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			ISingleRuleGenerator generator = new SingleRuleGenerator("single rule generator");
			GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
			
			// 2. Create the generation rule
			GamaGenstarFactoryUtils.createSampleDataGenerationRule(scope, generator, "sample data generation rule", sampleDataPropeties);
			
			return runGeneratorAndConvertGeneratedData(generator);
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
		test = false) }, see = { "population_from_csv" })
	public static IList generateIPFCompoundPopulation(final IScope scope, final String configurationPropertiesFilePath) {
		
		try {
			// 0. Load the properties file
			Properties sampleDataPropeties = null;
			File sampleDataPropertyFile = new File(FileUtils.constructAbsoluteFilePath(scope, configurationPropertiesFilePath, true));
			try {
				FileInputStream propertyInputStream = new FileInputStream(sampleDataPropertyFile);
				sampleDataPropeties = new Properties();
				sampleDataPropeties.load(propertyInputStream);
			} catch (FileNotFoundException e) {
				throw new GenstarException(e);
			} catch (IOException e) {
				throw new GenstarException(e);
			}
			
			// 1. Create the generator
			String attributesCSVFilePath = sampleDataPropeties.getProperty(GenstarFactoryUtils.SAMPLE_DATA_PROPERTIES_FILE_FORMAT.ATTRIBUTES_PROPERTY);
			GenstarCSVFile attributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			ISingleRuleGenerator generator = new SingleRuleGenerator("single rule generator");
			GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
			
			// 2. Create the generation rule
			GamaGenstarFactoryUtils.createSampleDataGenerationRule(scope, generator, "sample data generation rule", sampleDataPropeties);
	
			return convertGenstarPopulationToGamaPopulation(generator.generate());
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
				map.put(eav.getAttribute().getNameOnEntity(), Genstar2GamaTypeConversion.convertGenstar2GamaType(eav.getAttributeValueOnEntity()));
			}
			
			map.put(GENSTAR_ENTITY, entity); // ... for traceback purpose
			
			syntheticPopulation.add(map);
		}
		
		return syntheticPopulation;
	}
	
	private static IList convertGenstarPopulationToGamaPopulation(final ISyntheticPopulation genstarPopulation) throws GenstarException {
		IList gamaPopulation = GamaListFactory.create();
		
		
		gamaPopulation.add(genstarPopulation.getName()); // first element is the population name
		gamaPopulation.add(genstarPopulation.getGroupReferences()); // second element is references to "group" agents
		gamaPopulation.add(genstarPopulation.getComponentReferences()); // third element is the references to "component" agents
		
		// Convert the genstar population to format understood by GAML "genstar_create" statement
		GamaMap map;
		for (Entity entity : genstarPopulation.getEntities()) {
			map = GamaMapFactory.create();
			for (EntityAttributeValue eav : entity.getEntityAttributeValues().values()) {
				map.put(eav.getAttribute().getNameOnEntity(), Genstar2GamaTypeConversion.convertGenstar2GamaType(eav.getAttributeValueOnEntity()));
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
