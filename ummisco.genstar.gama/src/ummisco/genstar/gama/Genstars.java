package ummisco.genstar.gama;

import static msi.gama.common.interfaces.IKeyword.GENSTAR_ENTITY;
import static msi.gama.common.interfaces.IKeyword.GENSTAR_POPULATION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.shape.ILocation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
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
import ummisco.genstar.dao.GenstarDAOFactory;
import ummisco.genstar.dao.SyntheticPopulationGeneratorDAO;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.AttributeValuesFrequency;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.EntityAttributeValue;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SyntheticPopulationGenerator;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarFactoryUtils;

/**
 * A set of Genstar-related operators.
 */
public abstract class Genstars {	
	
	
	// 2. generates the population of a generator then returns the synthetic population
	@operator(value = "retrieve_saved_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
	@doc(value = "Asks the population generator, whose name is specified by the right operand, to generate a synthetic population",
		usages = { @usage(value = "returns a map representing the generated synthetic population.") },
		comment = "",
		examples = { @example(value = "generate_population('paris_inhabitant_generator')", equals = "a map repsenting the generated synthetic population which can be directly fed to 'create' statement to create agents.", test = false) },
		see = {  })
	public static IList retrieveSavedPopulation(final IScope scope, final String generatorName) {
		try {
			IList returnedPopulation = GamaListFactory.create(Types.MAP);
			returnedPopulation.add(GENSTAR_POPULATION); // TODO define constant and change name -> "synthetic_population". "genstar_population" is for "complex" population!

			SyntheticPopulationGeneratorDAO syntheticPopulationGeneratorDAO = GenstarDAOFactory.getDAOFactory().getSyntheticPopulationGeneratorDAO();
			ISyntheticPopulationGenerator generator = syntheticPopulationGeneratorDAO.findSyntheticPopulationGeneratorByName(generatorName);
			if (generator == null) { 
				GamaRuntimeException.warning("'" + generatorName + "' + population generator not found.", scope);
				return returnedPopulation;
			}
			
			ISyntheticPopulation generatedPopulation = generator.generate();
			
			
			Map<String, Object> map;
			for (Entity entity : generatedPopulation.getEntities()) {
				map = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
				for (Map.Entry<String, EntityAttributeValue> entry : entity.getAttributeValues().entrySet()) {
					map.put(entry.getKey(), Genstar2GamaTypeConversion.convertGenstar2GamaType(entry.getValue().getAttributeValueOnEntity()));
				}
				returnedPopulation.add(map);
			}

			return returnedPopulation;
		} catch (final Exception e) {
			GamaRuntimeException.error(e.getMessage(), GAMA.getRuntimeScope());
			return GamaListFactory.EMPTY_LIST;
		}
	}
	
	
	@operator(value = "population_from_csv", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
	@doc(value = "generates a synthetic population from the input data provided by the CVS files. The generated population can be passed to the 'create' statement to create agents.",
		returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list synthetic_population <- population_from_csv('Attributes.csv', 'GenerationRules.csv', 14000)",
			equals = "",
			test = false) }, see = { "frequency_distribution_from_sample", "link_populations" })
	public static IList generatePopulationFromCSV_Data(final IScope scope, final String attributesCSVFilePath, final String generationRulesCSVFilePath, final int nbOfAgents) {
		
		IList returnedPopulation = GamaListFactory.create(Types.MAP);
		returnedPopulation.add(GENSTAR_POPULATION); // first element is a String, instead of a map -> change first element to a map [GENSTAR_POPULATION::GENSTAR_POPULATION]

		try {
			// Verification of attributes
//			GamaCSVFile attributesCSVFile = new GamaCSVFile(scope, attributesCSVFilePath, ",", Types.STRING, true);
//			GamaCSVFile generationRulesCSVFile = new GamaCSVFile(scope, generationRulesCSVFilePath, ",", Types.STRING, true);
			GenstarCSVFile attributesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true), true);
			GenstarCSVFile generationRulesCSVFile = new GenstarCSVFile(FileUtils.constructAbsoluteFilePath(scope, generationRulesCSVFilePath, true), true);
			if (nbOfAgents <= 0) { GamaRuntimeException.error("Number of agents must be positive", scope); }

			
			// 1. Create the generator
			ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("Population Generator", nbOfAgents);
			GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
			GamaGenstarFactoryUtils.createGenerationRulesFromCSVFile(scope, generator, generationRulesCSVFile);
			
			// 2. Generate the population
			ISyntheticPopulation generatedPopulation = generator.generate();
			
			// 3. Convert generated "data" to "format" understood by GAML "create" statement
			Map<String, Object> map;
			for (Entity entity : generatedPopulation.getEntities()) {
				
				map = GamaMapFactory.create(Types.STRING, Types.NO_TYPE);
				for (Map.Entry<String, EntityAttributeValue> entry : entity.getAttributeValues().entrySet()) {
					map.put(entry.getKey(), Genstar2GamaTypeConversion.convertGenstar2GamaType(entry.getValue().getAttributeValueOnEntity()));
				}
				
				map.put(GENSTAR_ENTITY, entity); // ... for traceback purpose
				
				returnedPopulation.add(map);
			}
		} catch (final GenstarException e) {
			throw GamaRuntimeException.error(e.getMessage(), scope);
		}

		// return the generated population
		return returnedPopulation;
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
		examples = { @example(value = "string result_file <- generate_frequency_distribution('Attributes.csv', 'SampleData.csv', 'DistributionFormat.csv', 'ResultingDistribution.csv')",
			equals = "a string representing the path to the 'ResultingDistribution.csv' CSV file that contains the resulting frequency distribution generation rule",
			test = false) }, see = { "population_from_csv", "link_populations" })
	public static IGamaFile<IMatrix<Object>, Object, ILocation, Object> generateFrequencyDistribution(final IScope scope, final String attributesCSVFilePath, 
			final String sampleDataCSVFilePath, final String distributionFormatCSVFilePath, final String resultDistributionCSVFilePath) {
		
		try {
			// initialize CSV files
			GamaCSVFile attributesCSVFile = new GamaCSVFile(scope, attributesCSVFilePath, GenstarUtilsDeprecated.CSV_FILE_FORMATS.ATTRIBUTE_METADATA.FIELD_DELIMITER, Types.STRING, true);
			GamaCSVFile sampleDataCSVFile = new GamaCSVFile(scope, sampleDataCSVFilePath, GenstarUtilsDeprecated.CSV_FILE_FORMATS.ATTRIBUTE_METADATA.FIELD_DELIMITER, Types.STRING, true);
			GamaCSVFile distributionFormatCSVFile = new GamaCSVFile(scope, distributionFormatCSVFilePath, GenstarUtilsDeprecated.CSV_FILE_FORMATS.ATTRIBUTE_METADATA.FIELD_DELIMITER, Types.STRING, true);
			
			// 1. create the generator then add attributes
			ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("dummy generator", 10);
			GenstarUtilsDeprecated.createAttributesFromCSVFile(scope, generator, attributesCSVFile);
			
			// 2. read the distribution file format to know the input and output attributes
			IList<String> attributeNames = distributionFormatCSVFile.getAttributes(scope);
			List<String> attributeNamesWithoutInputOutput = new ArrayList<String>();
			if (attributeNames.size() < 1) { throw new GenstarException("First line of distribution format file must contain at least 2 elements. File: " + distributionFormatCSVFile.getPath() + "."); }
			for (int index=0; index < attributeNames.size(); index++) {
				String attribute = attributeNames.get(index);
				StringTokenizer t = new StringTokenizer(attribute, GenstarUtilsDeprecated.CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.ATTRIBUTE_NAME_TYPE_DELIMITER);
				if (t.countTokens() != 2) { throw new GenstarException("Element must have format attribute_name:Input or attribute_name:Output. File: " + distributionFormatCSVFile.getPath() + "."); }
				
				String attributeName = t.nextToken();
				if (!generator.containAttribute(attributeName)) { throw new GenstarException("Attribute '" + attributeName + "' is not defined. File: " + distributionFormatCSVFile.getPath() + "."); }
				attributeNamesWithoutInputOutput.add(attributeName);
				
				String attributeType = t.nextToken();
				if (!attributeType.equals(GenstarUtilsDeprecated.CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.INPUT_ATTRIBUTE) && !attributeType.equals(GenstarUtilsDeprecated.CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.OUTPUT_ATTRIBUTE)) {
					throw new GenstarException("Attribute " + attributeName + " must either be Input or Output. File: " + distributionFormatCSVFile.getPath() + ".");
				}
			}
			
			// 3. create a frequency generation rule from the sample data then write the generation rule to file.
			FrequencyDistributionGenerationRule fdGenerationRule = GenstarUtilsDeprecated.createFrequencyDistributionFromSampleData(scope, generator, distributionFormatCSVFile, sampleDataCSVFile);
			
			List<String[]> contents = new ArrayList<String[]>();
			
			// write the header
			String[] generationRuleAttributeNamesArray = new String[attributeNames.size() + 1];
			attributeNames.toArray(generationRuleAttributeNamesArray);
			generationRuleAttributeNamesArray[generationRuleAttributeNamesArray.length - 1] = "Frequency";
			contents.add(generationRuleAttributeNamesArray);
			
			
			List<AbstractAttribute> generationRuleAttributes = new ArrayList<AbstractAttribute>();
			for (String a : attributeNamesWithoutInputOutput) { generationRuleAttributes.add(generator.getAttribute(a)); }
			
			// sort the attributeValueFrequencies
			List<AttributeValuesFrequency> sortedAttributeValueFrequencies = new ArrayList<AttributeValuesFrequency>(fdGenerationRule.getAttributeValuesFrequencies());
			Collections.sort(sortedAttributeValueFrequencies, new GenstarUtilsDeprecated.AttributeValuesFrequencyComparator(generationRuleAttributes));
			
			for (AttributeValuesFrequency avf : sortedAttributeValueFrequencies) {
			String[] row = new String[attributeNames.size() + 1];

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
			
			return new GamaCSVFile(scope, exportFileName, GenstarUtilsDeprecated.CSV_FILE_FORMATS.ATTRIBUTE_METADATA.FIELD_DELIMITER, Types.STRING, true);
		} catch (final Exception e) {
			if (e instanceof GamaRuntimeException) { throw (GamaRuntimeException) e; }
			else { throw GamaRuntimeException.create(e, scope); }
		}
	}
}
