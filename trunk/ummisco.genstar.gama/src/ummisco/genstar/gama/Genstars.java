package ummisco.genstar.gama;

import static msi.gama.common.interfaces.IKeyword.GENSTAR_ENTITY;
import static msi.gama.common.interfaces.IKeyword.GENSTAR_POPULATION;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import msi.gama.metamodel.agent.IMacroAgent;
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
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.extensions.genstar.IGamaPopulationsLinker;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types;
import ummisco.genstar.dao.GenstarDAOFactory;
import ummisco.genstar.dao.SyntheticPopulationGeneratorDAO;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.DataType;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.EntityAttributeValue;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.RangeValue;
import ummisco.genstar.metamodel.RangeValuesAttribute;
import ummisco.genstar.metamodel.SyntheticPopulationGenerator;
import ummisco.genstar.metamodel.UniqueValue;
import ummisco.genstar.metamodel.UniqueValuesAttribute;

/**
 * A set of Genstar-related operators.
 */
public abstract class Genstars {	
	
	
	// 2. generates the population of a generator then returns the synthetic population
	@operator(value = "generate_population", category = { IOperatorCategory.GENSTAR })
	@doc(value = "Asks the population generator, whose name is specified by the right operand, to generate a synthetic population",
		usages = { @usage(value = "returns a map representing the generated synthetic population.") },
		comment = "",
		examples = { @example(value = "generate_population('paris_inhabitant_generator')", equals = "a map repsenting the generated synthetic population which can be directly fed to 'create' statement to create agents.", test = false) },
		see = {  })
	public static List generatePopulation(final IScope scope, final String generatorName) {
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
//				map = new GamaMap<String, Object>();
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
	
	
	private static final class CSV_FILE_FORMATS {

		static final class ATTRIBUTE_METADATA {
			static final String ATTRIBUTE_VALUE_DELIMITER = ",";
			static final String MIN_MAX_VALUE_DELIMITER = ":";
				
			// Header of attribute meta-data file: 
			//		“Name On Data”, “Name On Entity”, “Data Type”, “Value Type On Data”, “Values”, “Value Type On Entity”
			static final String HEADER_STR = "\"Name On Data\", \"Name On Entity\", \"Data Type\", \"Value Type\", \"Values\", \"Value Type On Entity\"";
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
		
		
		static final class GENERATION_RULE_METADATA {
			
			// Header of Generation Rule meta-data file:
			//		“Name”, “File”, “Type”
			static final String HEADER_STR = "\"Name\", \"File\", \"Rule Type\"";
			static final int NB_OF_COLS = 3;
			static String[] HEADERS = new String[NB_OF_COLS];
			static {
				HEADERS[0] = "Name";
				HEADERS[1] = "File";
				HEADERS[2] = "Rule Type";
			}
			
			// Generation Rule Names
			static final String FREQUENCY_DISTRIBUTION_GENERATION_RULE = FrequencyDistributionGenerationRule.RULE_TYPE_NAME;
			static final String ATTRIBUTE_INFERENCE_GENERATION_RULE = AttributeInferenceGenerationRule.RULE_TYPE_NAME;
		}
		
		
		static final class FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA {
			
			static final String ATTRIBUTE_NAME_TYPE_DELIMITER = ":";
			
			static final String INPUT_ATTRIBUTE = "Input";
			static final String OUTPUT_ATTRIBUTE = "Output";
			
			static final String RANGE_VALUE_DELIMITER = ":";
		}
		
		
		static final class ATTRIBUTE_INFERENCE_GENERATION_RULE_METADATA {
			
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
	private static void createRangeValueAttribute(final ISyntheticPopulationGenerator generator, final String attributeNameOnData, final String attributeNameOnEntity, 
			final DataType dataType, final String values, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException {
		
		RangeValuesAttribute rangeAttribute = new RangeValuesAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, UniqueValue.class);
		
		// 1. Parse and accumulate each range value token into a list.
		StringTokenizer valueTokens = new StringTokenizer(values, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.ATTRIBUTE_VALUE_DELIMITER);
		if (valueTokens.countTokens() == 0) { throw new GenstarException("Invalid attribute file format: no value is defined for the attribute"); }
		List<String> rangeTokens = new ArrayList<String>();
		while (valueTokens.hasMoreTokens()) { rangeTokens.add(valueTokens.nextToken()); }
		
		// 2. Created range values from the parsed tokens.
		for (String t : rangeTokens) {
			StringTokenizer minMaxValueTokens = new StringTokenizer(t, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
			if (minMaxValueTokens.countTokens() != 2) { throw new GenstarException("Invalid attribute file format: invalid range value"); }
			
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
	private static void createUniqueValueAttribute(final ISyntheticPopulationGenerator generator, final String attributeNameOnData, final String attributeNameOnEntity, 
			final DataType dataType, final String values, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException {
		
		UniqueValuesAttribute uniqueValueAttribute = new UniqueValuesAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, valueClassOnEntity);
		
		// 1. Parse and accumulate each unique value token into a list.
		StringTokenizer valueTokenizers = new StringTokenizer(values, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.ATTRIBUTE_VALUE_DELIMITER);
		if (valueTokenizers.countTokens() == 0) { throw new GenstarException("Invalid attribute file format: no value is defined for the attribute"); }
		List<String> uniqueValueTokens = new ArrayList<String>();
		while (valueTokenizers.hasMoreTokens()) { uniqueValueTokens.add(valueTokenizers.nextToken()); }
		
		// 2. Create unique values from the parsed tokens.
		for (String t : uniqueValueTokens) { uniqueValueAttribute.add(new UniqueValue(dataType, t.trim())); }
		
		generator.addAttribute(uniqueValueAttribute);
	}

	private static void createAttributesFromCSVFile(final IScope scope, final ISyntheticPopulationGenerator generator, final GamaCSVFile attributesCSVFile) throws GenstarException {
		
		IMatrix fileContent = attributesCSVFile.getContents(scope);
		if ( fileContent == null || fileContent.isEmpty(scope) ) { throw new GenstarException("Invalid attribute file: content is empty"); }
		int rows = fileContent.getRows(scope);
		
		if (fileContent.getCols(scope) != CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS) { throw new GenstarException("CVS file must have " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS + " columns."); }

		// 1. Parse the header
		int headerIndex = 0;
		IList<String> header = GamaListFactory.create(Types.STRING, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS);
		for ( Object obj : fileContent.getRow(scope, 0) ) {
			header.add(Cast.asString(scope, obj));
			
			if (!header.get(headerIndex).equals(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.HEADERS[headerIndex])) {
				throw new GenstarException("Invalid attribute file format: Header must be " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.HEADER_STR);
			}
			headerIndex++;
		}
		
		// 2. Parse and initialize attributes
		for ( int rowIndex = 1; rowIndex < rows; rowIndex++ ) {
			
			final IList attributeInfo = fileContent.getRow(scope, rowIndex);
			if (attributeInfo.size() != CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS) { throw new GenstarException("Invalid attribute file format: each row must have " + CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS + " columns."); }
			
			String attributeNameOnData = (String)attributeInfo.get(0);
			String attributeNameOnEntity = (String)attributeInfo.get(1);
			
			String dataTypeStr = (String)attributeInfo.get(2);
			DataType dataType = DataType.fromName(dataTypeStr);
			if (dataType == null) { throw new GenstarException(dataTypeStr + " is not a supported data type."); }
			
			String valueTypeOnDataStr = (String)attributeInfo.get(3);
			String values = (String)attributeInfo.get(4);
			
			String valueTypeOnEntityStr = (String)attributeInfo.get(5);
			Class<? extends AttributeValue> valueClassOnEntity = AttributeValue.getClassByName(valueTypeOnEntityStr);
			
			
			if (valueTypeOnDataStr.equals(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.RANGE_VALUE_NAME)) {
				createRangeValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity);
			} else if (valueTypeOnDataStr.equals(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.UNIQUE_VALUE_NAME)) {
				createUniqueValueAttribute(generator, attributeNameOnData, attributeNameOnEntity, dataType, values, valueClassOnEntity);
			} else {
				throw new GenstarException("Invalid attribute file format: unsupported value type.");
			}
		}
	}
	
	private static void createFrequencyDistributionGenerationRule(final IScope scope, final ISyntheticPopulationGenerator generator, final String ruleName, GamaCSVFile ruleDataFile) throws GenstarException {
		
		IMatrix fileContent = ruleDataFile.getContents(scope);
		if ( fileContent == null || fileContent.isEmpty(scope) ) { throw new GenstarException("Invalid Frequency Distribution Generation Rule file format: content is empty"); }
		int rows = fileContent.getRows(scope);
		
		
		
		// 1. Parse the header
		// 		Header format is a list of elements consisting of two parts delimited by a colon:
		//			1. The first part is all the elements in the header except for the last one. 
		//				Each element is a pair denoting an attribute and whether the attribute is "input" or "output",
		//			2. The second part is the last element of the header representing the frequency.
		IList<String> header = GamaListFactory.create(Types.STRING);
		for ( Object obj : fileContent.getRow(scope, 0) ) { header.add(Cast.asString(scope, obj)); }
		if (header.size() < 2) { throw new GenstarException("Invalid Frequency Distribution Generation Rule file format: invalid header"); }
		
//		System.out.println("Header of " + ruleDataFile.getName());
//		for (String h : header) {
//			System.out.print(h + ", ");
//		}
//		System.out.println();
		
		// 2. Create the rule then add attributes
		FrequencyDistributionGenerationRule generationRule = new FrequencyDistributionGenerationRule(generator, ruleName);
		List<AbstractAttribute> concerningAttributes = new ArrayList<AbstractAttribute>();
		for (int headerIndex=0; headerIndex < (header.size() - 1); headerIndex++) {
			StringTokenizer attributeToken = new StringTokenizer(header.get(headerIndex), CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.ATTRIBUTE_NAME_TYPE_DELIMITER);
			if (attributeToken.countTokens() != 2) {
				StringBuffer invalidTokens = new StringBuffer();
				while (attributeToken.hasMoreElements()) {
					invalidTokens.append(attributeToken.nextToken());
					invalidTokens.append(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.ATTRIBUTE_NAME_TYPE_DELIMITER);
				}
				
				throw new GenstarException("Invalid header format (" + invalidTokens.toString() + ") found in Frequency Distribution Generation Rule (file: " + ruleDataFile.getPath() + ")"); 
			}
			
			String attributeName = attributeToken.nextToken();
			String attributeType = attributeToken.nextToken();
			
			AbstractAttribute attribute = generator.getAttribute(attributeName);
			if (attribute == null) { 
				throw new GenstarException("Unknown attribute (" + attributeName + ") found in Frequency Distribution Generation Rule (file: " + ruleDataFile.getPath() + ")"); 
			}
			concerningAttributes.add(attribute);
			
			if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.INPUT_ATTRIBUTE)) {
				generationRule.appendInputAttribute(attribute);
			} else if (attributeType.equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.OUTPUT_ATTRIBUTE)) {
				generationRule.appendOutputAttribute(attribute);
			} else {
				throw new GenstarException("Invalid attribute type (" + attributeType + ") found in Frequency Distribution Generation Rule (file: " + ruleDataFile.getPath() + ")");
			}
		}
		
		generationRule.generateAttributeValuesFrequencies();
		
		// 3. Set frequencies
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (int rowIndex=1; rowIndex<rows; rowIndex++) {
			attributeValues.clear();
			IList frequencyInfo = fileContent.getRow(scope, rowIndex);
			
			for (int attributeIndex=0; attributeIndex<(frequencyInfo.size()-1); attributeIndex++) {
				AbstractAttribute concerningAttribute = concerningAttributes.get(attributeIndex);
				DataType dataType = concerningAttribute.getDataType();
				
				if (concerningAttribute instanceof RangeValuesAttribute) {
					StringTokenizer minMaxValueToken = new StringTokenizer((String)frequencyInfo.get(attributeIndex), CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_GENERATION_RULE_METADATA.RANGE_VALUE_DELIMITER);
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
	
	private static void createAttributeInferenceGenerationRule(final IScope scope, final ISyntheticPopulationGenerator generator, final String ruleName, GamaCSVFile ruleDataFile) throws GenstarException {
		// FIXME implement the method
		throw new UnsupportedOperationException("not yet implemented"); 
	}
	
	private static void createGenerationRulesFromCSVFile(final IScope scope, final ISyntheticPopulationGenerator generator, final GamaCSVFile distributionsCSVFile) throws GenstarException {
		IMatrix fileContent = distributionsCSVFile.getContents(scope);
		if ( fileContent == null || fileContent.isEmpty(scope) ) { throw new GenstarException("Invalid Generation Rule file: content is empty"); }
		int rows = fileContent.getRows(scope);

		if (fileContent.getCols(scope) != CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS) { throw new GenstarException("CVS file must have " + CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS + " columns."); }

		// 1. Parse the header
		int headerIndex = 0;
		IList<String> header = GamaListFactory.create(Types.STRING, CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS);
		for ( Object obj : fileContent.getRow(scope, 0) ) {
			header.add(Cast.asString(scope, obj));
			
			if (!header.get(headerIndex).equals(CSV_FILE_FORMATS.GENERATION_RULE_METADATA.HEADERS[headerIndex])) {
				throw new GenstarException("Invalid Generation Rule file format: Header must be " + CSV_FILE_FORMATS.GENERATION_RULE_METADATA.HEADER_STR);
			}
			headerIndex++;
		}
		
		
		// 2. Parse and initialize distributions
		for ( int rowIndex = 1; rowIndex < rows; rowIndex++ ) {
			
			final IList generationRuleInfo = fileContent.getRow(scope, rowIndex);
			if (generationRuleInfo.size() != CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS) { throw new GenstarException("Invalid Generation Rule file format: each row must have " +  CSV_FILE_FORMATS.GENERATION_RULE_METADATA.NB_OF_COLS  + " columns."); }
			
			String ruleName = (String)generationRuleInfo.get(0);
			
			String ruleDataFilePath = (String)generationRuleInfo.get(1);
//			GamaCSVFile ruleDataFile = (GamaCSVFile) Files.from(scope, ruleDataFilePath);
			GamaCSVFile ruleDataFile = new GamaCSVFile(scope, ruleDataFilePath);
			
			String ruleTypeName = (String)generationRuleInfo.get(2);
			
			
			if (ruleTypeName.equals(CSV_FILE_FORMATS.GENERATION_RULE_METADATA.FREQUENCY_DISTRIBUTION_GENERATION_RULE)) {
				createFrequencyDistributionGenerationRule(scope, generator, ruleName, ruleDataFile);
			} else if (ruleTypeName.equals(CSV_FILE_FORMATS.GENERATION_RULE_METADATA.ATTRIBUTE_INFERENCE_GENERATION_RULE)) {
				createAttributeInferenceGenerationRule(scope, generator, ruleName, ruleDataFile);
			} else {
				throw new GenstarException("Invalid Generation Rule file format: unsupported generation rule (" + ruleTypeName + ")");
			}
		}
	}
	
	// generate a population from CSV files
	@operator(value = "population_from_csv", category = { IOperatorCategory.GENSTAR })
	@doc(value = "generates a synthetic population from the input data provided by the CVS files. The generated population is intended passed to the 'create' statement to create agents.",
		returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "population_from_csv('MIRO_Attributes_MetaData.csv', 'MIRO_GenerationRules_MetaData.csv', 14000)",
			equals = "",
			test = false) }, see = {  })
	public static List generatePopulationFromCSV_Data(final IScope scope, final String attributesCSVFilePath, final String generationRulesCSVFilePath, final int nbOfAgents) {
		
		// Verification of attributes
		GamaCSVFile attributesCSVFile = new GamaCSVFile(scope, attributesCSVFilePath);
		GamaCSVFile generationRulesCSVFile = new GamaCSVFile(scope, generationRulesCSVFilePath);
		if (nbOfAgents <= 0) { GamaRuntimeException.error("Number of agents must be positive", scope); }
		
		IList returnedPopulation = GamaListFactory.create(Types.MAP);
		returnedPopulation.add(GENSTAR_POPULATION);
		try {
			// 1. Create the generator
			ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("Population Generator", nbOfAgents);
			createAttributesFromCSVFile(scope,generator, attributesCSVFile);
			createGenerationRulesFromCSVFile(scope, generator, generationRulesCSVFile);
			
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
	examples = { @example(value = "",
		equals = "",
		test = false) }, see = {  })
	public static boolean linkPopulations(final IScope scope, final String linkerName, final List<List<IMacroAgent>> populations) {
		
		// 1. search for the linker on the AbstractGamlAdditions
		IGamaPopulationsLinker populationsLinker = AbstractGamlAdditions.POPULATIONS_LINKERS.get(linkerName);
		
		if (populationsLinker == null) {
			throw GamaRuntimeException.error("Populations linker : " + linkerName + " does not exist.", scope);
		}
		
		// 2. ask the linker to do the job
		populationsLinker.establishRelationship(scope, populations);
		
		return true;
	}
	
	
}
