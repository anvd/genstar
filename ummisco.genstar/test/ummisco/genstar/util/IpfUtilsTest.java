package ummisco.genstar.util;

import static org.junit.Assert.assertTrue;

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
import ummisco.genstar.metamodel.IPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SampleBasedGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;

@RunWith(JMockit.class)
public class IpfUtilsTest {

	@Test public void testBuildControlledAttributesValuesSubsets() throws GenstarException {
		// test_data/ummisco/genstar/util/IpfUtils/testBuildControlledAttributesValuesSubsets/controlled_attributes1.csv
		GenstarCsvFile controlledAttributesFile1 = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testBuildControlledAttributesValuesSubsets/controlled_attributes1.csv", true);
		SampleBasedGenerator generator1 = new SampleBasedGenerator("dummy single rule generator");
		AttributeUtils.createAttributesFromCSVFile(generator1, controlledAttributesFile1);
		
		// generate frequencies / control totals
		List<List<Map<AbstractAttribute, AttributeValue>>> controlledAttributesValuesSubsets1 = IpfUtils.buildControlledAttributesValuesSubsets(new HashSet<AbstractAttribute>(generator1.getAttributes()));
		
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
	
	
		// test_data/ummisco/genstar/util/IpfUtils/testBuildControlledAttributesValuesSubsets/controlled_attributes2.csv
		GenstarCsvFile controlledAttributesFile2 = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testBuildControlledAttributesValuesSubsets/controlled_attributes2.csv", true);
		SampleBasedGenerator generator2 = new SampleBasedGenerator("dummy single rule generator");
		AttributeUtils.createAttributesFromCSVFile(generator2, controlledAttributesFile2);
		
		// generate frequencies / control totals
		List<List<Map<AbstractAttribute, AttributeValue>>> controlledAttributesValuesSubsets2 = IpfUtils.buildControlledAttributesValuesSubsets(new HashSet<AbstractAttribute>(generator2.getAttributes()));

		// 3 controlled attributes
		assertTrue(controlledAttributesValuesSubsets2.size() == 3);
	}
	
	@Test(expected = GenstarException.class) public void testGenerateControlTotalsWithNullControlledAttributesFile() throws GenstarException {
		IpfUtils.generateIpfControlTotals(null, 1);
	}
	
	@Test(expected = GenstarException.class) public void testGenerateControlTotalsWithNonPositiveTotal(@Mocked final GenstarCsvFile controlledAttributesFile) throws GenstarException {
		IpfUtils.generateIpfControlTotals(controlledAttributesFile, 0);
	}
	
	@Test public void testGenerateIpfControlTotals() throws GenstarException {
		GenstarCsvFile controlledAttributesFile1 = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testGenerateIpfControlTotals/controlled_attributes1.csv", true);
		List<List<String>> result1 = IpfUtils.generateIpfControlTotals(controlledAttributesFile1, 1000);
		
		/*
			Household Size, Household Income, Household Type: 3*2*3 = 18
			Household Size, Household Income, Number Of Cars: 3*2*4 = 24
			Household Size,Household Type, Number Of Cars: 3*3*4 = 36
			Household Income, Household Type, Number Of Cars: 2*3*4 = 24
			--> 18 + 24 + 36 + 24 = 102		
		*/
		assertTrue(result1.size() == 102); 
		for (List<String> row1 : result1) { assertTrue(row1.size() == 7); }

		GenstarCsvFile controlledAttributesFile2 = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testGenerateIpfControlTotals/controlled_attributes2.csv", true);
		List<List<String>> result3 = IpfUtils.generateIpfControlTotals(controlledAttributesFile2, 10000);
		
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
		AttributeUtils.createAttributesFromCSVFile(generator, controlledAttributesFile);
		
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
		AttributeUtils.createAttributesFromCSVFile(generator1, controlledAttributesFile);
		
		ISyntheticPopulationGenerator generator2 = new SampleBasedGenerator("dummy generator");
		AttributeUtils.createAttributesFromCSVFile(generator2, controlledAttributesFile);

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
		AttributeUtils.createAttributesFromCSVFile(generator, controlledAttributesFile);
		
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFileWithMismatchedControlTotalsFileContentAndAttributesValuesSizes/control_totals1.csv", false);
		
		IpfUtils.parseAttributeValuesFrequenciesFromIpfControlTotalsFile(controlTotalsFile, generator.getAttributes());
	}

	@Test(expected = GenstarException.class) public void testReadAttributeValuesFrequenciesFromControlTotalsFileWithInvalidControlTotalFormat() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/IpfUtils/util/testReadAttributeValuesFrequenciesFromControlTotalsFileWithInvalidControlTotalFormat/controlled_attributes1.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, controlledAttributesFile);
		
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFileWithInvalidControlTotalFormat/control_totals1.csv", false);
		
		IpfUtils.parseAttributeValuesFrequenciesFromIpfControlTotalsFile(controlTotalsFile, generator.getAttributes());
	}

	@Test(expected = GenstarException.class) public void testReadAttributeValuesFrequenciesFromControlTotalsFileWithUnregconizedAttribute() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFileWithUnregconizedAttribute/controlled_attributes1.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, controlledAttributesFile);
		
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFileWithUnregconizedAttribute/control_totals1.csv", false);
		
		IpfUtils.parseAttributeValuesFrequenciesFromIpfControlTotalsFile(controlTotalsFile, generator.getAttributes());
	}

	@Test(expected = GenstarException.class) public void testReadAttributeValuesFrequenciesFromControlTotalsFileWithDuplicatedControlTotals() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFileWithDuplicatedControlTotals/controlled_attributes1.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, controlledAttributesFile);
		
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFileWithDuplicatedControlTotals/control_totals1.csv", false);
		
		IpfUtils.parseAttributeValuesFrequenciesFromIpfControlTotalsFile(controlTotalsFile, generator.getAttributes());
		
	}
	
	
	@Test public void testReadAttributeValuesFrequenciesFromControlTotalsFile() throws GenstarException {
		
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testReadAttributeValuesFrequenciesFromControlTotalsFile/controlled_attributes1.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, controlledAttributesFile);
		
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
				if (attributeValueString.contains(INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER)) { // range value
					StringTokenizer rangeValueToken = new StringTokenizer(attributeValueString, INPUT_DATA_FORMATS.CSV_FILES.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER);
					valueList.add(rangeValueToken.nextToken());
					valueList.add(rangeValueToken.nextToken());
				} else { // unique value
					valueList.add(attributeValueString);
				}
				
				attributeValue = attribute.findCorrespondingAttributeValueOnData(valueList);
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
			IPopulation generatedPopulation = GenstarUtils.generateRandomSinglePopulation(populationName, attributesFile, 1, 3);
			
			Map<String, String> csvFilePathsByPopulationNames = new HashMap<String, String>();
			csvFilePathsByPopulationNames.put(populationName, sampleFilePath);
			GenstarUtils.writePopulationToCsvFile(generatedPopulation, csvFilePathsByPopulationNames);
		}
		
		
		// 1. create the generator and the attributes
		SampleBasedGenerator generator = new SampleBasedGenerator("dummy generator");
		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		// 2. create sample data generation rule (GenstarFactoryUtils.createSampleDataGenerationRule)
		GenstarCsvFile sampleCSVFile = new GenstarCsvFile(sampleFilePath, true);
		GenstarCsvFile controlledAttributesListFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testAnalyseIpfPopulation/controlled_attributes_list.csv", false);
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testAnalyseIpfPopulation/control_totals.csv", false);
		GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpfUtils/testAnalyseIpfPopulation/supplementary_attributes_list.csv", false);
		GenstarUtils.createSampleDataGenerationRule(generator, "dummy rule", sampleCSVFile, controlledAttributesListFile, controlTotalsFile, supplementaryAttributesFile, null, IpfGenerationRule.DEFAULT_MAX_ITERATIONS);
		
		// 3. generate the population
		IPopulation population = generator.generate();
		
		// 4. do the analysis
		List<Integer> analsysisResult = IpfUtils.analyseIpfPopulation(population, controlledAttributesListFile, controlTotalsFile);
		
		assertTrue(analsysisResult.size() == controlTotalsFile.getRows());
	}
}
