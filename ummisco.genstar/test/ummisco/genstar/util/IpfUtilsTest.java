package ummisco.genstar.util;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.IpfGenerationRule;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttributeWithRangeInput;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.metamodel.sample_data.CompoundSampleData;
import ummisco.genstar.metamodel.sample_data.SampleData;

@RunWith(JMockit.class)
public class IpfUtilsTest {

	@Test public void testBuildIpfControlledAttributesValuesSubsets() throws GenstarException {
		// test_data/ummisco/genstar/util/IpfUtils/testBuildIpfControlledAttributesValuesSubsets/controlled_attributes1.csv
		GenstarCsvFile controlledAttributesFile1 = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testBuildIpfControlledAttributesValuesSubsets/controlled_attributes1.csv", true);
		SampleBasedGenerator generator1 = new SampleBasedGenerator("dummy single rule generator");
		AttributeUtils.createAttributesFromCsvFile(generator1, controlledAttributesFile1);
		
		// generate frequencies / control totals
		List<List<Map<AbstractAttribute, AttributeValue>>> controlledAttributesValuesSubsets1 = IpfUtils.buildIpfControlledAttributesValuesSubsets(new HashSet<AbstractAttribute>(generator1.getAttributes()));
		
		// 4 controlled attributes
		assertTrue(controlledAttributesValuesSubsets1.size() == 4);
		for (List<Map<AbstractAttribute, AttributeValue>> subset : controlledAttributesValuesSubsets1) {
			int nbPossibilities = 1;
			for (AbstractAttribute attribute : subset.get(0).keySet()) { nbPossibilities *= attribute.valuesOnData().size(); }
			assertTrue(nbPossibilities == subset.size());
			
			Set<AbstractAttribute> attributeSet = subset.get(0).keySet();
			for (Map<AbstractAttribute, AttributeValue> entry : subset) {
				assertTrue(attributeSet.size() == entry.size() && attributeSet.containsAll(entry.keySet()));
			}
		}
	
	
		// test_data/ummisco/genstar/util/IpfUtils/testBuildIpfControlledAttributesValuesSubsets/controlled_attributes2.csv
		GenstarCsvFile controlledAttributesFile2 = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testBuildIpfControlledAttributesValuesSubsets/controlled_attributes2.csv", true);
		SampleBasedGenerator generator2 = new SampleBasedGenerator("dummy single rule generator");
		AttributeUtils.createAttributesFromCsvFile(generator2, controlledAttributesFile2);
		
		// generate frequencies / control totals
		List<List<Map<AbstractAttribute, AttributeValue>>> controlledAttributesValuesSubsets2 = IpfUtils.buildIpfControlledAttributesValuesSubsets(new HashSet<AbstractAttribute>(generator2.getAttributes()));

		// 3 controlled attributes
		assertTrue(controlledAttributesValuesSubsets2.size() == 3);
	}
	
	@Test(expected = GenstarException.class) public void testGenerateControlTotalsFromTotalWithNullControlledAttributesFile() throws GenstarException {
		IpfUtils.generateIpfControlTotalsFromTotal(null, 1);
	}
	
	@Test(expected = GenstarException.class) public void testGenerateControlTotalsFromTotalWithNonPositiveTotal(@Mocked final GenstarCsvFile controlledAttributesFile) throws GenstarException {
		IpfUtils.generateIpfControlTotalsFromTotal(controlledAttributesFile, 0);
	}
	
	@Test public void testGenerateIpfControlTotalsFromTotal() throws GenstarException {
		GenstarCsvFile controlledAttributesFile1 = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testGenerateIpfControlTotalsFromTotal/controlled_attributes1.csv", true);
		List<List<String>> result1 = IpfUtils.generateIpfControlTotalsFromTotal(controlledAttributesFile1, 1000);
		
		/*
			Household Size, Household Income, Household Type: 3*2*3 = 18
			Household Size, Household Income, Number Of Cars: 3*2*4 = 24
			Household Size,Household Type, Number Of Cars: 3*3*4 = 36
			Household Income, Household Type, Number Of Cars: 2*3*4 = 24
			--> 18 + 24 + 36 + 24 = 102		
		*/
		assertTrue(result1.size() == 102); 
		for (List<String> row1 : result1) { assertTrue(row1.size() == 7); }

		GenstarCsvFile controlledAttributesFile2 = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testGenerateIpfControlTotalsFromTotal/controlled_attributes2.csv", true);
		List<List<String>> result3 = IpfUtils.generateIpfControlTotalsFromTotal(controlledAttributesFile2, 10000);
		
		/*
			Household Size, Household Income: 3*2 = 6
			Household Size, Household Type: 3*3 = 9
			Household Income, Household Type: 2*3 = 6	
			6 + 9 + 6 = 21	 
		*/
		assertTrue(result3.size() == 21);
		for (List<String> row3 : result3) { assertTrue(row3.size() == 5); }
	}

	@Test(expected = GenstarException.class) public void testFindSubsetSumWithNonPositiveTotal() throws GenstarException {
		IpfUtils.findSubsetSum(0, 10);
	}
	
	
	@Test(expected = GenstarException.class) public void testFindSubsetSumWithNonPositiveN() throws GenstarException {
		IpfUtils.findSubsetSum(10, 0);
	}
	
	
	@Test(expected = GenstarException.class) public void testFindSubsetSumWithTotalSmallerThanN() throws GenstarException {
		IpfUtils.findSubsetSum(9, 10);
	}
	
	
	@Test public void testFindSubsetSum() throws GenstarException {
		List<Integer> subset1 = IpfUtils.findSubsetSum(100000, 10);
		assertTrue(subset1.size() == 10);
		int sumSubset1 = 0;
		for (int i=0; i<subset1.size(); i++) { 
			assertTrue(subset1.get(i) >= 1);
			sumSubset1 += subset1.get(i); 
		}
		assertTrue(sumSubset1 == 100000);
		
		
		List<Integer> subset2 = IpfUtils.findSubsetSum(1000000, 15);
		assertTrue(subset2.size() == 15);
		int sumSubset2 = 0;
		for (int i=0; i<subset2.size(); i++) {
			assertTrue(subset2.get(i) >= 1);
			sumSubset2 += subset2.get(i);
		}
		assertTrue(sumSubset2 == 1000000);
		
		List<Integer> subset3 = IpfUtils.findSubsetSum(2, 1);
		assertTrue(subset3.size() == 1);
		assertTrue(subset3.get(subset3.size() - 1) == 2);
		
		List<Integer> subset4 = IpfUtils.findSubsetSum(2, 2);
		assertTrue(subset4.size() == 2);
		assertTrue(subset4.get(subset4.size() - 1) == 1);
		assertTrue(subset4.get(0) == 1);
	}

	
	@Test(expected = GenstarException.class) public void testReadAttributeValuesFrequenciesFromControlTotalsFileWithDupplicatedControlledAttributes() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFile/controlled_attributes1.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, controlledAttributesFile);
		
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFile/control_totals1.csv", false);
		
		List<AbstractAttribute> duplicatedAttributes = new ArrayList<AbstractAttribute>();
		
		List<AbstractAttribute> attributes = generator.getAttributes();
		duplicatedAttributes.add(attributes.get(0));
		duplicatedAttributes.add(attributes.get(0));
		
		IpfUtils.parseAttributeValuesFrequenciesFromIpfControlTotalsFile(controlTotalsFile, duplicatedAttributes);
	}
	
	@Test(expected = GenstarException.class) public void testReadAttributeValuesFrequenciesFromControlTotalsFileWithControlledAttributesFromDifferentGenerators() throws GenstarException {
		ISyntheticPopulationGenerator generator1 = new SampleBasedGenerator("dummy generator");
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFile/controlled_attributes1.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator1, controlledAttributesFile);
		
		ISyntheticPopulationGenerator generator2 = new SampleBasedGenerator("dummy generator");
		AttributeUtils.createAttributesFromCsvFile(generator2, controlledAttributesFile);

		List<AbstractAttribute> attributes1 = generator1.getAttributes();
		List<AbstractAttribute> attributes2 = generator2.getAttributes();
		
		List<AbstractAttribute> attributesFromDifferentGenerators = new ArrayList<AbstractAttribute>();
		attributesFromDifferentGenerators.add(attributes1.get(0));
		attributesFromDifferentGenerators.add(attributes2.get(1));

		GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFile/control_totals1.csv", false);

		IpfUtils.parseAttributeValuesFrequenciesFromIpfControlTotalsFile(controlTotalsFile, attributesFromDifferentGenerators);
	}

	@Test(expected = GenstarException.class) public void testReadAttributeValuesFrequenciesFromControlTotalsFileWithMismatchedControlTotalsFileContentAndAttributesValuesSizes() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFileWithMismatchedControlTotalsFileContentAndAttributesValuesSizes/controlled_attributes1.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, controlledAttributesFile);
		
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFileWithMismatchedControlTotalsFileContentAndAttributesValuesSizes/control_totals1.csv", false);
		
		IpfUtils.parseAttributeValuesFrequenciesFromIpfControlTotalsFile(controlTotalsFile, generator.getAttributes());
	}

	@Test(expected = GenstarException.class) public void testReadAttributeValuesFrequenciesFromControlTotalsFileWithInvalidControlTotalFormat() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/IpfUtils/util/testReadAttributeValuesFrequenciesFromControlTotalsFileWithInvalidControlTotalFormat/controlled_attributes1.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, controlledAttributesFile);
		
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFileWithInvalidControlTotalFormat/control_totals1.csv", false);
		
		IpfUtils.parseAttributeValuesFrequenciesFromIpfControlTotalsFile(controlTotalsFile, generator.getAttributes());
	}

	@Test(expected = GenstarException.class) public void testReadAttributeValuesFrequenciesFromControlTotalsFileWithUnregconizedAttribute() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFileWithUnregconizedAttribute/controlled_attributes1.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, controlledAttributesFile);
		
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFileWithUnregconizedAttribute/control_totals1.csv", false);
		
		IpfUtils.parseAttributeValuesFrequenciesFromIpfControlTotalsFile(controlTotalsFile, generator.getAttributes());
	}

	@Test(expected = GenstarException.class) public void testReadAttributeValuesFrequenciesFromControlTotalsFileWithDuplicatedControlTotals() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFileWithDuplicatedControlTotals/controlled_attributes1.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, controlledAttributesFile);
		
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFileWithDuplicatedControlTotals/control_totals1.csv", false);
		
		IpfUtils.parseAttributeValuesFrequenciesFromIpfControlTotalsFile(controlTotalsFile, generator.getAttributes());
		
	}
	
	
	@Test public void testReadAttributeValuesFrequenciesFromControlTotalsFile() throws GenstarException {
		
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFile/controlled_attributes1.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, controlledAttributesFile);
		
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFile/control_totals1.csv", false);
		
		List<AttributeValuesFrequency> avfs = IpfUtils.parseAttributeValuesFrequenciesFromIpfControlTotalsFile(controlTotalsFile, generator.getAttributes());
		
		assertTrue(avfs.size() == controlTotalsFile.getRows());
		
		// verify order
		int line = 0;
		AbstractAttribute attribute;
		AttributeValue attributeValue;
		List<String> valueList = new ArrayList<String>();
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>(); ;
		for (List<String> aRow : controlTotalsFile.getContent()) {
			
			attributeValues.clear();
			for (int col=0; col<(aRow.size() - 1); col+=2) { // Parse each line of the file
				// 1. parse the attribute name column
				attribute = generator.getAttributeByNameOnData(aRow.get(col));
				
				// 2. parse the attribute value column
				valueList.clear();
				String attributeValueString = aRow.get(col+1);
				if (attributeValueString.contains(CSV_FILE_FORMATS.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER)) { // range value
					StringTokenizer rangeValueToken = new StringTokenizer(attributeValueString, CSV_FILE_FORMATS.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER);
					valueList.add(rangeValueToken.nextToken());
					valueList.add(rangeValueToken.nextToken());
				} else { // unique value
					valueList.add(attributeValueString);
				}
				
				attributeValue = attribute.getMatchingAttributeValueOnData(valueList);
				attributeValues.put(attribute, attributeValue);
			}
			
			assertTrue(avfs.get(line).matchAttributeValuesOnData(attributeValues));
			
			// "frequency" is the last column
			assertTrue(avfs.get(line).getFrequency() == Integer.parseInt(aRow.get(aRow.size() - 1)));
			line++;
		}		
	}

	
	@Test public void testAnalyseIpfPopulation() throws GenstarException {
		
		// delete the csvOutputFile if is exists
		String csvOutputFilePath = "test_data/ummisco/genstar/util/IpfUtils/testAnalyseIpfPopulation/analysis_result.csv";
		File outputFile = new File(csvOutputFilePath);
		if (outputFile.exists()) { outputFile.delete(); }
		
		// generate the sample data if necessary
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testAnalyseIpfPopulation/attributes.csv", true);
		String sampleFilePath = "test_data/ummisco/genstar/util/IpfUtils/testAnalyseIpfPopulation/sample_data.csv";
		File sampleFile = new File(sampleFilePath);
		if (!sampleFile.exists()) {
			String populationName = "household population";
			IPopulation generatedPopulation = GenstarUtils.generateRandomSinglePopulation(populationName, attributesFile, null, 1, 3);
			
			Map<String, String> csvFilePathsByPopulationNames = new HashMap<String, String>();
			csvFilePathsByPopulationNames.put(populationName, sampleFilePath);
			GenstarUtils.writePopulationToCsvFile(generatedPopulation, csvFilePathsByPopulationNames);
		}
		
		
		// 1. create the generator and the attributes
		SampleBasedGenerator generator = new SampleBasedGenerator("dummy generator");
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		// 2. create sample data generation rule (GenstarFactoryUtils.createSampleDataGenerationRule)
		GenstarCsvFile sampleCSVFile = new GenstarCsvFile(sampleFilePath, true);
		GenstarCsvFile controlledAttributesListFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testAnalyseIpfPopulation/controlled_attributes_list.csv", false);
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testAnalyseIpfPopulation/control_totals.csv", false);
		GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testAnalyseIpfPopulation/supplementary_attributes_list.csv", false);
//		IpfUtils.createIpfGenerationRule(generator, "dummy rule", sampleCSVFile, controlledAttributesListFile, controlTotalsFile, supplementaryAttributesFile, null, IpfGenerationRule.DEFAULT_MAX_ITERATIONS);
		IpfUtils.createIpfGenerationRule(generator, "dummy rule", sampleCSVFile, controlledAttributesListFile, controlTotalsFile, supplementaryAttributesFile, IpfGenerationRule.DEFAULT_MAX_ITERATIONS);
		
		// 3. generate the population
		IPopulation population = generator.generate();
		
		// 4. do the analysis
		List<Integer> analsysisResult = IpfUtils.analyseIpfPopulation(population, controlledAttributesListFile, controlTotalsFile);
		
		assertTrue(analsysisResult.size() == controlTotalsFile.getRows());
	}


	@Test public void testCreateIpfGenerationRule() throws GenstarException {
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testCreateIpfGenerationRule/attributes.csv", true);
		
		SampleBasedGenerator generator = new SampleBasedGenerator("single rule generator");
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		GenstarCsvFile sampleFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testCreateIpfGenerationRule/sample_data.csv", true);
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testCreateIpfGenerationRule/controlled_attributes.csv", false);
		GenstarCsvFile controlledTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testCreateIpfGenerationRule/control_totals.csv", false);
		GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testCreateIpfGenerationRule/supplementary_attributes.csv", false);
		
		
//		AbstractAttribute householdIdAttribute = generator.getAttributeByNameOnEntity("householdID");
//		AbstractAttribute householdIdAttribute = null;
		
		assertTrue(generator.getGenerationRule() == null);
//		IpfUtils.createIpfGenerationRule(generator, "sample data generation rule", sampleFile, controlledAttributesFile, controlledTotalsFile, supplementaryAttributesFile, householdIdAttribute, IpfGenerationRule.DEFAULT_MAX_ITERATIONS);
		IpfUtils.createIpfGenerationRule(generator, "sample data generation rule", sampleFile, controlledAttributesFile, controlledTotalsFile, supplementaryAttributesFile, IpfGenerationRule.DEFAULT_MAX_ITERATIONS);
		
		IpfGenerationRule rule = (IpfGenerationRule) generator.getGenerationRule();
		assertTrue(rule != null);
		assertTrue(rule.getSampleData() instanceof SampleData);
		assertTrue(generator.getNbOfEntities() == rule.getIPF().getNbOfEntitiesToGenerate());
	}
	
	
	@Test public void testCreateGroupComponentIpfGenerationRule() throws GenstarException {
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testCreateGroupComponentIpfGenerationRule/group_attributes.csv", true);
		
		SampleBasedGenerator groupGenerator = new SampleBasedGenerator("group rule generator");
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, attributesFile);
		
		
		GenstarCsvFile groupSampleFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testCreateGroupComponentIpfGenerationRule/group_sample.csv", true);
		GenstarCsvFile groupControlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testCreateGroupComponentIpfGenerationRule/group_controlled_attributes.csv", false);
		GenstarCsvFile groupControlledTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testCreateGroupComponentIpfGenerationRule/group_control_totals.csv", false);
		GenstarCsvFile groupSupplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testCreateGroupComponentIpfGenerationRule/group_supplementary_attributes.csv", false);
		
		GenstarCsvFile componentSampleFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testCreateGroupComponentIpfGenerationRule/component_sample.csv", true);
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testCreateGroupComponentIpfGenerationRule/component_attributes.csv", true);
		
		String groupIdAttributeNameOnDataOfGroupEntity = "Household ID";
		String groupIdAttributeNameOnDataOfComponentEntity = "Household ID";
		String componentPopulationName = "people";
		
		
		assertTrue(groupGenerator.getGenerationRule() == null);
		IpfUtils.createCompoundIpfGenerationRule(groupGenerator, "group component sample data generation rule", groupSampleFile, groupControlledAttributesFile, groupControlledTotalsFile, 
				groupSupplementaryAttributesFile, null, groupIdAttributeNameOnDataOfGroupEntity,
				componentSampleFile, componentAttributesFile, componentPopulationName, null, groupIdAttributeNameOnDataOfComponentEntity,
				IpfGenerationRule.DEFAULT_MAX_ITERATIONS);
		
		IpfGenerationRule rule = (IpfGenerationRule)groupGenerator.getGenerationRule();
		assertTrue(rule != null);
		assertTrue(rule.getSampleData() instanceof CompoundSampleData);
		assertTrue(rule.getIPF().getNbOfEntitiesToGenerate() == groupGenerator.getNbOfEntities());
	}
	
	
	@Test public void testBuildIpfEntityCategories() throws GenstarException {
		
		/*
	public static Map<AttributeValuesFrequency, List<Entity>> buildIpfEntityCategories(final IPopulation population, final Set<AbstractAttribute> ipfControlledAttributes) throws GenstarException {
		 */
		
		String base_path = "test_data/ummisco/genstar/util/IpfUtils/testBuildIpfEntityCategories/";
		String populationName = "household";
		String attributesFileName = "group_attributes.csv";
		GenstarCsvFile attributesFile = new GenstarCsvFile(base_path + attributesFileName, true);
		
		String populationFileName = "group_population.csv";
		
		// 0. load the population from files
		GenstarCsvFile populationFile = new GenstarCsvFile(base_path + populationFileName, true);
		IPopulation loadedSinglePopulation = GenstarUtils.loadSinglePopulation(PopulationType.SYNTHETIC_POPULATION, populationName, attributesFile, populationFile);
		
		AbstractAttribute householdSizeAttr = loadedSinglePopulation.getAttributeByNameOnData("Household Size");
		AbstractAttribute householdIncomeAttr = loadedSinglePopulation.getAttributeByNameOnData("Household Income");
		AbstractAttribute householdTypeAttr = loadedSinglePopulation.getAttributeByNameOnData("Household Type");
		
		// build Ipf entity categories with 2 controlled attributes
		Set<AbstractAttribute> twoControlledAttributes = new HashSet<AbstractAttribute>();
		twoControlledAttributes.add(householdSizeAttr);
		twoControlledAttributes.add(householdIncomeAttr);
		
		Map<AttributeValuesFrequency, List<Entity>> entityCategories1 = IpfUtils.buildIpfEntityCategories(loadedSinglePopulation, twoControlledAttributes);
		assertTrue(entityCategories1.size() == 4);
		
		/* group_attributes.csv
			Name On Data,Name On Entity,Data Type,Value Type On Data,Values,Value Type On Entity
			Household ID,householdID,int,UniqueWithRangeInput,0:99,Unique
			Household Size,householdSize,int,Unique,1;2,Unique
			Household Income,householdIncome,string,Unique,High;Low,Unique
			Household Type,householdType,string,Unique,type1;type2,Unique
		 */
		AttributeValue highIncome = new UniqueValue(DataType.STRING, "High");
		AttributeValue lowIncome = new UniqueValue(DataType.STRING, "Low");
		
		AttributeValue type1 = new UniqueValue(DataType.STRING, "type1");
		AttributeValue type2 = new UniqueValue(DataType.STRING, "type2");
		
		AttributeValue size1 = new UniqueValue(DataType.INTEGER, "1");
		AttributeValue size2 = new UniqueValue(DataType.INTEGER, "2");
		
		Map<AbstractAttribute, AttributeValue> highMap = new HashMap<AbstractAttribute, AttributeValue>();
		highMap.put(householdIncomeAttr, highIncome);
		
		Map<AbstractAttribute, AttributeValue> lowMap = new HashMap<AbstractAttribute, AttributeValue>();
		lowMap.put(householdIncomeAttr, lowIncome);
		
		Map<AbstractAttribute, AttributeValue> type1Map = new HashMap<AbstractAttribute, AttributeValue>();
		type1Map.put(householdTypeAttr, type1);
		
		Map<AbstractAttribute, AttributeValue> type2Map = new HashMap<AbstractAttribute, AttributeValue>();
		type2Map.put(householdTypeAttr, type2);
		
		for (Map.Entry<AttributeValuesFrequency, List<Entity>> eCategory : entityCategories1.entrySet()) {
			AttributeValuesFrequency avf = eCategory.getKey(); 
			assertTrue(avf.getAttributes().size() == 1);

			// High == 10
			if (avf.matchAttributeValuesOnData(highMap)) { assertTrue(avf.getFrequency() == 10); continue; }
			
			// Low == 10
			if (avf.matchAttributeValuesOnData(lowMap)) { assertTrue(avf.getFrequency() == 10); continue; }
			
			// type1 == 9
			if (avf.matchAttributeValuesOnData(type1Map)) { assertTrue(avf.getFrequency() == 9); continue; }
			
			// type2 == 11
			if (avf.matchAttributeValuesOnData(type2Map)) { assertTrue(avf.getFrequency() == 11); continue; }
		}

		
		// build Ipf entity categories with 3 controlled attributes
		Set<AbstractAttribute> threeControlledAttributes = new HashSet<AbstractAttribute>();
		threeControlledAttributes.add(householdSizeAttr);
		threeControlledAttributes.add(householdIncomeAttr);
		threeControlledAttributes.add(householdTypeAttr);
		
		Map<AttributeValuesFrequency, List<Entity>> entityCategories2 = IpfUtils.buildIpfEntityCategories(loadedSinglePopulation, threeControlledAttributes);
		assertTrue(entityCategories2.size() == 12);
		
		/*
		 * group_population.csv
			householdID,householdSize,householdIncome,householdType
			0,1,High,type1
			1,1,High,type1
			2,1,High,type1
			3,1,High,type2
			4,1,High,type2
			5,1,Low,type1
			6,1,Low,type1
			7,1,Low,type2
			8,1,Low,type2
			9,1,Low,type2
			10,2,High,type1
			11,2,High,type1
			12,2,High,type2
			13,2,High,type2
			14,2,High,type2
			15,2,Low,type1
			16,2,Low,type1
			17,2,Low,type2
			18,2,Low,type2
			19,2,Low,type2
		 */
		
		Map<AbstractAttribute, AttributeValue> oneHighMap = new HashMap<AbstractAttribute, AttributeValue>(); // 1,High
		oneHighMap.put(householdSizeAttr, size1);
		oneHighMap.put(householdIncomeAttr, highIncome);
		
		Map<AbstractAttribute, AttributeValue> oneLowMap = new HashMap<AbstractAttribute, AttributeValue>(); // 1,Low
		oneLowMap.put(householdSizeAttr, size1);
		oneLowMap.put(householdIncomeAttr, lowIncome);
		
		Map<AbstractAttribute, AttributeValue> twoHighMap = new HashMap<AbstractAttribute, AttributeValue>(); // 2,High
		twoHighMap.put(householdSizeAttr, size2);
		twoHighMap.put(householdIncomeAttr, highIncome);
		
		Map<AbstractAttribute, AttributeValue> twoLowMap = new HashMap<AbstractAttribute, AttributeValue>(); // 2,Low
		twoLowMap.put(householdSizeAttr, size2);
		twoLowMap.put(householdIncomeAttr, lowIncome);
		
		
		Map<AbstractAttribute, AttributeValue> oneType1Map = new HashMap<AbstractAttribute, AttributeValue>(); // 1,type1
		oneType1Map.put(householdSizeAttr, size1);
		oneType1Map.put(householdTypeAttr, type1);
		
		Map<AbstractAttribute, AttributeValue> oneType2Map = new HashMap<AbstractAttribute, AttributeValue>(); // 1, type2
		oneType2Map.put(householdSizeAttr, size1);
		oneType2Map.put(householdTypeAttr, type2);
		
		Map<AbstractAttribute, AttributeValue> twoType1Map = new HashMap<AbstractAttribute, AttributeValue>(); // 2,type1 = 4
		twoType1Map.put(householdSizeAttr, size2);
		twoType1Map.put(householdTypeAttr, type1);
		
		Map<AbstractAttribute, AttributeValue> twoType2Map = new HashMap<AbstractAttribute, AttributeValue>(); // 2,type2 = 6
		twoType2Map.put(householdSizeAttr, size2);
		twoType2Map.put(householdTypeAttr, type2);
		
		
		Map<AbstractAttribute, AttributeValue> type1HighMap = new HashMap<AbstractAttribute, AttributeValue>(); // type1,High
		type1HighMap.put(householdTypeAttr, type1);
		type1HighMap.put(householdIncomeAttr, highIncome);
		
		Map<AbstractAttribute, AttributeValue> type1LowMap = new HashMap<AbstractAttribute, AttributeValue>(); // type1,Low
		type1LowMap.put(householdTypeAttr, type1);
		type1LowMap.put(householdIncomeAttr, lowIncome);
		
		Map<AbstractAttribute, AttributeValue> type2HighMap = new HashMap<AbstractAttribute, AttributeValue>(); // type2,High
		type2HighMap.put(householdTypeAttr, type2);
		type2HighMap.put(householdIncomeAttr, highIncome);
		
		Map<AbstractAttribute, AttributeValue> type2LowMap = new HashMap<AbstractAttribute, AttributeValue>(); // type2,Low = 6
		type2LowMap.put(householdTypeAttr, type2);
		type2LowMap.put(householdIncomeAttr, lowIncome);
		
		
		for (Map.Entry<AttributeValuesFrequency, List<Entity>> eCategory : entityCategories2.entrySet()) {
			AttributeValuesFrequency avf = eCategory.getKey(); 
			assertTrue(avf.getAttributes().size() == 2);
			
			if (avf.matchAttributeValuesOnData(oneHighMap)) { assertTrue(avf.getFrequency() == 5); continue; } // 1,High = 5
			if (avf.matchAttributeValuesOnData(oneLowMap)) { assertTrue(avf.getFrequency() == 5); continue; } // 1,Low = 5
			if (avf.matchAttributeValuesOnData(twoHighMap)) { assertTrue(avf.getFrequency() == 5); continue; } // 2,High = 5
			if (avf.matchAttributeValuesOnData(twoLowMap)) { assertTrue(avf.getFrequency() == 5); continue; } // 2,Low = 5
			
			if (avf.matchAttributeValuesOnData(oneType1Map)) { assertTrue(avf.getFrequency() == 5); continue; } // 1,type1 = 5
			if (avf.matchAttributeValuesOnData(oneType2Map)) { assertTrue(avf.getFrequency() == 5); continue; } // 1,type2 = 5
			if (avf.matchAttributeValuesOnData(twoType1Map)) { assertTrue(avf.getFrequency() == 4); continue; } // 2,type1 = 4
			if (avf.matchAttributeValuesOnData(twoType2Map)) { assertTrue(avf.getFrequency() == 6); continue; } // 2,type2 = 6
			
			if (avf.matchAttributeValuesOnData(type1HighMap)) { assertTrue(avf.getFrequency() == 5); continue; } // type1,High = 5
			if (avf.matchAttributeValuesOnData(type1LowMap)) { assertTrue(avf.getFrequency() == 4); continue; } // type1,Low = 4
			if (avf.matchAttributeValuesOnData(type2HighMap)) { assertTrue(avf.getFrequency() == 5); continue; } // type2,High = 5
			if (avf.matchAttributeValuesOnData(type2LowMap)) { assertTrue(avf.getFrequency() == 6); continue; } // type2,Low = 6
		}
	}
	
	
	@Test public void testExtractIpfSinglePopulation() throws GenstarException {
		
		String basePath = "test_data/ummisco/genstar/util/IpfUtils/testExtractIpfSinglePopulation/";
		String populationName = "household";
		
		GenstarCsvFile attributesFileWithID = new GenstarCsvFile(basePath + "group_attributes_with_ID.csv", true);
		String idAttributeNameOnData = "Household ID";
		int minEntitiesOfEachAttributeValuesSet = 15;
		int maxEntitiesOfEachAttributeValuesSet = 15;
		
		IPopulation populationWithID = GenstarUtils.generateRandomSinglePopulation(populationName, attributesFileWithID, idAttributeNameOnData, minEntitiesOfEachAttributeValuesSet, maxEntitiesOfEachAttributeValuesSet);
		assertTrue(populationWithID.getNbOfEntities() == 120);
		
		AbstractAttribute householdSizeAttr = populationWithID.getAttributeByNameOnData("Household Size");
		AbstractAttribute householdIncomeAttr = populationWithID.getAttributeByNameOnData("Household Income");
		AbstractAttribute householdTypeAttr = populationWithID.getAttributeByNameOnData("Household Type");
		
		Set<AbstractAttribute> ipfControlledAttributes = new HashSet<AbstractAttribute>();
		ipfControlledAttributes.add(householdSizeAttr);
		ipfControlledAttributes.add(householdIncomeAttr);
		ipfControlledAttributes.add(householdTypeAttr);
		
		UniqueValuesAttributeWithRangeInput idAttribute = (UniqueValuesAttributeWithRangeInput)populationWithID.getAttributeByNameOnData(idAttributeNameOnData);
		
		// extract 1% of the original population then do the verifications
		float percentage = 0.1f;
		IPopulation extractedPopulation1 = IpfUtils.extractIpfSinglePopulation(populationWithID, percentage, ipfControlledAttributes, idAttribute);
		assertTrue(extractedPopulation1.getNbOfEntities() == 12);
		int idValue = 0;
		for (Entity groupEntity : extractedPopulation1.getEntities()) { // ensure that entities are correctly recoded
			assertTrue(((UniqueValue)groupEntity.getEntityAttributeValue(idAttribute).getAttributeValueOnEntity()).getIntValue() == idValue);
			idValue++;
		}
		// TODO further verifications
		
		// extract 10% of the original population then do the verifications
		percentage = 10;
		IPopulation extractedPopulation2 = IpfUtils.extractIpfSinglePopulation(populationWithID, percentage, ipfControlledAttributes, idAttribute);
		assertTrue(extractedPopulation2.getNbOfEntities() == 12);
		idValue = 0;
		for (Entity groupEntity : extractedPopulation2.getEntities()) { // ensure that entities are correctly recoded
			assertTrue(((UniqueValue)groupEntity.getEntityAttributeValue(idAttribute).getAttributeValueOnEntity()).getIntValue() == idValue);
			idValue++;
		}
		
		// extract 30% of the original population then do the verifications
		percentage = 30;
		IPopulation extractedPopulation3 = IpfUtils.extractIpfSinglePopulation(populationWithID, percentage, ipfControlledAttributes, idAttribute);
		assertTrue(extractedPopulation3.getNbOfEntities() == 36);
		idValue = 0;
		for (Entity groupEntity : extractedPopulation3.getEntities()) { // ensure that entities are correctly recoded
			assertTrue(((UniqueValue)groupEntity.getEntityAttributeValue(idAttribute).getAttributeValueOnEntity()).getIntValue() == idValue);
			idValue++;
		}

		// group_attributes_with_ID
		
		// group_attributes_without_ID
		GenstarCsvFile attributesFileWithoutID = new GenstarCsvFile(basePath + "group_attributes_without_ID.csv", true);
		IPopulation populationWithoutID = GenstarUtils.generateRandomSinglePopulation(populationName, attributesFileWithoutID, null, minEntitiesOfEachAttributeValuesSet, maxEntitiesOfEachAttributeValuesSet);
		assertTrue(populationWithoutID.getNbOfEntities() == 120);
		
		
		AbstractAttribute householdSizeAttr_withoutID = populationWithoutID.getAttributeByNameOnData("Household Size");
		AbstractAttribute householdIncomeAttr_withoutID = populationWithoutID.getAttributeByNameOnData("Household Income");
		AbstractAttribute householdTypeAttr_withoutID = populationWithoutID.getAttributeByNameOnData("Household Type");
		
		Set<AbstractAttribute> ipfControlledAttributes_withoutID = new HashSet<AbstractAttribute>();
		ipfControlledAttributes_withoutID.add(householdSizeAttr_withoutID);
		ipfControlledAttributes_withoutID.add(householdIncomeAttr_withoutID);
		ipfControlledAttributes_withoutID.add(householdTypeAttr_withoutID);
		
		// extract 1% of the original population then do the verifications
		percentage = 0.1f;
		IPopulation extractedPopulation4 = IpfUtils.extractIpfSinglePopulation(populationWithoutID, percentage, ipfControlledAttributes_withoutID, null);
		assertTrue(extractedPopulation4.getNbOfEntities() == 12);
		// TODO further verifications
		
		// extract 10% of the original population then do the verifications
		percentage = 10;
		IPopulation extractedPopulation5 = IpfUtils.extractIpfSinglePopulation(populationWithoutID, percentage, ipfControlledAttributes_withoutID, null);
		assertTrue(extractedPopulation5.getNbOfEntities() == 12);
		
		// extract 30% of the original population then do the verifications
		percentage = 30;
		IPopulation extractedPopulation6 = IpfUtils.extractIpfSinglePopulation(populationWithoutID, percentage, ipfControlledAttributes_withoutID, null);
		assertTrue(extractedPopulation6.getNbOfEntities() == 36);
	}
	
	
	@Test public void testExtractIpfCompoundPopulation() throws GenstarException {
		
		
		String base_path = "test_data/ummisco/genstar/util/IpfUtils/testExtractIpfCompoundPopulation/";
		
		String groupPopulationName = "household";
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(base_path + "group_attributes.csv", true);
		
		String componentPopulationName = "people";
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(base_path + "component_attributes.csv", true);

		String groupIdAttributeNameOnGroupEntity = "Household ID";
		String groupIdAttributeNameOnComponentEntity = "Household ID";
		String groupSizeAttributeNameOnData = "Household Size";
		
		String componentReferenceOnGroup = "inhabitants";
		String groupReferenceOnComponent = "household";

		// 0. generate an original population with 120 entities
		int minGroupEntitiesOfEachAttributeValuesSet1 = 15;
		int maxGroupEntitiesOfEachAttributeValuesSet1 = 15;
		IPopulation generatedCompoundPopulation = GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, 
				componentAttributesFile, groupIdAttributeNameOnGroupEntity, groupIdAttributeNameOnComponentEntity, groupSizeAttributeNameOnData, 
				minGroupEntitiesOfEachAttributeValuesSet1, maxGroupEntitiesOfEachAttributeValuesSet1, componentReferenceOnGroup, groupReferenceOnComponent);
		
		AbstractAttribute householdSizeAttr = generatedCompoundPopulation.getAttributeByNameOnData("Household Size");
		AbstractAttribute householdIncomeAttr = generatedCompoundPopulation.getAttributeByNameOnData("Household Income");
		AbstractAttribute householdTypeAttr = generatedCompoundPopulation.getAttributeByNameOnData("Household Type");
		
		Set<AbstractAttribute> ipfControlledAttributes = new HashSet<AbstractAttribute>();
		ipfControlledAttributes.add(householdSizeAttr);
		ipfControlledAttributes.add(householdIncomeAttr);
		ipfControlledAttributes.add(householdTypeAttr);
		
		UniqueValuesAttributeWithRangeInput groupIdAttributeOnGroupEntity = (UniqueValuesAttributeWithRangeInput) generatedCompoundPopulation.getAttributeByNameOnData("Household ID");
		UniqueValuesAttributeWithRangeInput groupIdAttributeOnComponentEntity = null;
		for (Entity groupEntity : generatedCompoundPopulation.getEntities()) {
			IPopulation componentPopulation = groupEntity.getComponentPopulation(componentPopulationName);
			if (componentPopulation != null) {
				groupIdAttributeOnComponentEntity = (UniqueValuesAttributeWithRangeInput) componentPopulation.getAttributeByNameOnData("Household ID");
				break;
			}
		}
		
		
		// extract 1% of the original population then do the verifications
		float percentage = 0.1f;
		IPopulation extractedPopulation1 = IpfUtils.extractIpfCompoundPopulation(generatedCompoundPopulation, percentage, ipfControlledAttributes, 
				groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity, componentPopulationName);
		assertTrue(extractedPopulation1.getNbOfEntities() == 12);
		int idValue = 0;
		for (Entity groupEntity : extractedPopulation1.getEntities()) { // ensure that entities are correctly recoded
			assertTrue(((UniqueValue)groupEntity.getEntityAttributeValue(groupIdAttributeOnGroupEntity).getAttributeValueOnEntity()).getIntValue() == idValue);
			idValue++;
		}
		// TODO further verifications
		
		// extract 10% of the original population then do the verifications
		percentage = 10;
		IPopulation extractedPopulation2 = IpuUtils.extractIpuPopulation(generatedCompoundPopulation, percentage, ipfControlledAttributes, 
				groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity, componentPopulationName);
		assertTrue(extractedPopulation2.getNbOfEntities() == 12);
		idValue = 0;
		for (Entity groupEntity : extractedPopulation2.getEntities()) { // ensure that entities are correctly recoded
			assertTrue(((UniqueValue)groupEntity.getEntityAttributeValue(groupIdAttributeOnGroupEntity).getAttributeValueOnEntity()).getIntValue() == idValue);
			idValue++;
		}
		
		// extract 30% of the original population then do the verifications
		percentage = 30;
		IPopulation extractedPopulation3 = IpfUtils.extractIpfCompoundPopulation(generatedCompoundPopulation, percentage, ipfControlledAttributes, 
				groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity, componentPopulationName);
		assertTrue(extractedPopulation3.getNbOfEntities() == 36);
		idValue = 0;
		for (Entity groupEntity : extractedPopulation3.getEntities()) { // ensure that entities are correctly recoded
			assertTrue(((UniqueValue)groupEntity.getEntityAttributeValue(groupIdAttributeOnGroupEntity).getAttributeValueOnEntity()).getIntValue() == idValue);
			idValue++;
		}
	}
}
