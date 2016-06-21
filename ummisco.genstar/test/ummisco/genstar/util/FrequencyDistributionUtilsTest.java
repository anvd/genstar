package ummisco.genstar.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mockit.Deencapsulation;
import mockit.integration.junit4.JMockit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.sample_free.FrequencyDistributionGenerationRule;


@RunWith(JMockit.class)
public class FrequencyDistributionUtilsTest {

	@Test public void testCreateFrequencyDistributionGenerationRuleFromSampleDataOrPopulationFile() throws GenstarException {
		// input: distributionFormatCSVFile & sampleDataCSVFile
		// output: the newly created FrequencyDistributionGenerationRule
		
		SampleFreeGenerator generator = new SampleFreeGenerator("dummy generator", 10);
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/util/FrequencyDistributionUtils/testCreateFrequencyDistributionGenerationFromSampleDataOrPopulationFile/attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
		
		
		GenstarCsvFile distributionFormatCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/util/FrequencyDistributionUtils/testCreateFrequencyDistributionGenerationFromSampleDataOrPopulationFile/distributionFormat.csv", true);
		GenstarCsvFile sampleDataCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/util/FrequencyDistributionUtils/testCreateFrequencyDistributionGenerationFromSampleDataOrPopulationFile/sampleData.csv", true);
		
		FrequencyDistributionGenerationRule rule = FrequencyDistributionUtils.createFrequencyDistributionGenerationRuleFromSampleDataOrPopulationFile(generator, distributionFormatCSVFile, sampleDataCSVFile);
		assertTrue(rule.getAttributes().size() == 2);
		assertTrue(rule.getInputAttributeAtOrder(0) == null);
		assertTrue(rule.getOutputAttributeAtOrder(0).getNameOnData().equals("Category"));
		assertTrue(rule.getOutputAttributeAtOrder(1).getNameOnData().equals("Age"));
		int totalNbOfAttributeValues = 1;
		for (AbstractAttribute a : rule.getAttributes()) { totalNbOfAttributeValues *= a.valuesOnData().size(); }
		assertTrue(rule.getAttributeValuesFrequencies().size() == totalNbOfAttributeValues);
		
		
		/*
		 * sampleData.csv
				Category,Gender,Age
				C0,false,5
				C3,true,5
				C3,false,5
				C5,true,5
				C7,false,5
		 */
		Set<AttributeValuesFrequency> attributeValuesFrequencies = rule.getAttributeValuesFrequencies();
		
		// C0, 5
		Map<AbstractAttribute, AttributeValue> map1 = new HashMap<AbstractAttribute, AttributeValue>();
		
		AttributeValue c0Value = new UniqueValue(DataType.STRING, "C0");
		map1.put(generator.getAttributeByNameOnData("Category"), c0Value);
		
		RangeValuesAttribute ageAttribute = (RangeValuesAttribute) generator.getAttributeByNameOnData("Age");
		Set<AttributeValue> ageValues = ageAttribute.valuesOnData();
		UniqueValue age5UniqueValue = new UniqueValue(DataType.INTEGER, "5");
		RangeValue age5RangeValue = null;
		for (AttributeValue a : ageValues) {
			if ( ((RangeValue)a).cover(age5UniqueValue)) {
				age5RangeValue = (RangeValue)a;
				break;
			}
		}
		if (age5RangeValue == null) { Assert.fail("No age value is matched"); }
		map1.put(ageAttribute, age5RangeValue);

		boolean isMatchMap1 = false;
		for (AttributeValuesFrequency f : attributeValuesFrequencies) {
			if (f.matchAttributeValuesOnData(map1)) {
				if (isMatchMap1) { Assert.fail("Attributevalue is matched several times (map1)"); }
				
				assertTrue(f.getFrequency() == 1);
				isMatchMap1 = true;
			}
		}
		if (!isMatchMap1) { Assert.fail("No attributeValue is matched (map1)"); }
		
		// C3, 5 (frequency == 2)
		Map<AbstractAttribute, AttributeValue> map2 = new HashMap<AbstractAttribute, AttributeValue>();
		AttributeValue c5Value = new UniqueValue(DataType.STRING, "C3");
		map2.put(generator.getAttributeByNameOnData("Category"), c5Value);
		map2.put(ageAttribute, age5RangeValue);
		
		boolean isMatchMap2 = false;
		for (AttributeValuesFrequency f : attributeValuesFrequencies) {
			if (f.matchAttributeValuesOnData(map2)) {
				if (isMatchMap2) { Assert.fail("Attributevalue is matched several times (map2)"); }
				
				assertTrue(f.getFrequency() == 2);
				isMatchMap2 = true;
			}
		}
		if (!isMatchMap2) { Assert.fail("No attributeValue is matched (map2)"); }
	}
	
	
	@Test public void testParseFrequencyDistributionFileHeader() throws GenstarException {
		
		String basePath = "test_data/ummisco/genstar/util/FrequencyDistributionUtils/testParseFrequencyDistributionFileHeader/";
		
		SampleFreeGenerator generator = new SampleFreeGenerator("generator", 1000);
		
		GenstarCsvFile attributesFile = new GenstarCsvFile(basePath + "attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		GenstarCsvFile distributionFile1 = new GenstarCsvFile(basePath + "People_GenerationRule1_Data.csv", true);
		List<AbstractAttribute> headerAttributes1 = Deencapsulation.invoke(FrequencyDistributionUtils.class, "parseFrequencyDistributionFileHeader", generator, distributionFile1);
		assertTrue(headerAttributes1.size() == 2);
		assertTrue(headerAttributes1.get(0).getNameOnData().equals("Category"));
		assertTrue(headerAttributes1.get(1).getNameOnData().equals("Age"));
		
		GenstarCsvFile distributionFile2 = new GenstarCsvFile(basePath + "People_GenerationRule2_Data.csv", true);
		List<AbstractAttribute> headerAttributes2 = Deencapsulation.invoke(FrequencyDistributionUtils.class, "parseFrequencyDistributionFileHeader", generator, distributionFile2);
		assertTrue(headerAttributes2.size() == 2);
		assertTrue(headerAttributes2.get(0).getNameOnData().equals("Category"));
		assertTrue(headerAttributes2.get(1).getNameOnData().equals("Gender"));
	}
	
	
	@Test public void testParseFrequencyDistributionFileRow() throws GenstarException {

		String basePath = "test_data/ummisco/genstar/util/FrequencyDistributionUtils/testParseFrequencyDistributionFileHeader/";
		
		SampleFreeGenerator generator = new SampleFreeGenerator("generator", 1000);
		
		GenstarCsvFile attributesFile = new GenstarCsvFile(basePath + "attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		GenstarCsvFile distributionFile1 = new GenstarCsvFile(basePath + "People_GenerationRule1_Data.csv", true);
		List<AbstractAttribute> headerAttributes1 = Deencapsulation.invoke(FrequencyDistributionUtils.class, "parseFrequencyDistributionFileHeader", generator, distributionFile1);
		
		/*
			Category:Output,Age:Output,Frequency
			C0,0:4,100
			C1,0:4,200
			C0,5:17,300
			C1,5:17,400
		 */
		UniqueValue c0Value = new UniqueValue(DataType.STRING, "C0");
		UniqueValue c1Value = new UniqueValue(DataType.STRING, "C1");
		
		RangeValue value0_4 = new RangeValue(DataType.INTEGER, "0", "4");
		RangeValue value5_17 = new RangeValue(DataType.INTEGER, "5", "17");
		
		AbstractAttribute categoryAttribute = generator.getAttributeByNameOnData("Category");
		AbstractAttribute ageAttribute = generator.getAttributeByNameOnData("Age");
		
		List<List<String>> fileContent = distributionFile1.getContent();
		
		List<String> row1 = fileContent.get(0);
		Map<AbstractAttribute, AttributeValue> attributeValues1 = Deencapsulation.invoke(FrequencyDistributionUtils.class, "parseFrequencyDistributionFileRow", headerAttributes1, row1);
		assertTrue(attributeValues1.get(categoryAttribute).compareTo(c0Value) == 0);
		assertTrue(attributeValues1.get(ageAttribute).compareTo(value0_4) == 0);

	}
	
	
	@Test public void testAnalyseFrequencyDistributionPopulation() throws GenstarException {
	
		String basePath = "test_data/ummisco/genstar/util/FrequencyDistributionUtils/testAnalyseFrequencyDistributionPopulation/";
		
		String analysisResultOutputFolderPath = basePath + "analysisResultOutputFolderPath/";
		
		// delete all files in "analysis result output folder"
		File outputFolder = new File(analysisResultOutputFolderPath);
		File[] files = outputFolder.listFiles();
		if (files.length > 0) { for (File f : files) { f.delete(); } }
		
		
		GenstarCsvFile attributesFile = new GenstarCsvFile(basePath + "attributes.csv", true);
		GenstarCsvFile singlePopulationFile = new GenstarCsvFile(basePath + "sampleData.csv", true);

		IPopulation population = GenstarUtils.loadSinglePopulation(PopulationType.SYNTHETIC_POPULATION, "people", attributesFile, singlePopulationFile);
		
		List<GenstarCsvFile> generationRuleFiles = new ArrayList<GenstarCsvFile>();
		
		GenstarCsvFile rule1File = new GenstarCsvFile(basePath + "People_GenerationRule1_Data.csv", true);
		GenstarCsvFile rule2File = new GenstarCsvFile(basePath + "People_GenerationRule2_Data.csv", true);
		
		generationRuleFiles.add(rule1File);
		generationRuleFiles.add(rule2File);
		
		
		files = outputFolder.listFiles();
		assertTrue(files.length == 0);
		
		FrequencyDistributionUtils.analyseFrequencyDistributionPopulation(population, generationRuleFiles, analysisResultOutputFolderPath);

	
		files = outputFolder.listFiles();
		assertTrue(files.length == 2);
		
		// verify CSV output file size (i.e., row, column) 
		for (File f : files) {
			GenstarCsvFile outputFile = new GenstarCsvFile(f.getAbsolutePath(), true);
			
			if (f.getAbsolutePath().contains("People_GenerationRule1_Data")) {
				assertTrue(outputFile.getColumns() == rule1File.getColumns() + 1);
				assertTrue(outputFile.getRows() == rule1File.getRows());
			}
			
			if (f.getAbsolutePath().contains("People_GenerationRule2_Data")) {
				assertTrue(outputFile.getColumns() == rule2File.getColumns() + 1);
				assertTrue(outputFile.getRows() == rule2File.getRows());
			}
		}
	}
	
	
	@Test public void testGenerateAndSaveFrequencyDistributions() throws GenstarException {
		/*
	public static List<String> generateAndSaveFrequencyDistributions(final SampleFreeGenerator generator, final GenstarCsvFile sampleDataOrPopulationFile, 
			final List<GenstarCsvFile> distributionFormatCsvFiles, final List<String> resultDistributionCsvFilePaths) {
		 */
		
		String basePath = "test_data/ummisco/genstar/util/FrequencyDistributionUtils/testGenerateAndSaveFrequencyDistributions/";
		
		SampleFreeGenerator generator = new SampleFreeGenerator("generator");
		GenstarCsvFile attributesFile = new GenstarCsvFile(basePath + "attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);

		String distribution1FilePath = basePath + "distributionFormat1.csv";
		String distribution2FilePath = basePath + "distributionFormat2.csv";
		
		String resultDistribution1FilePath = basePath + "resultDistribution1.csv";
		String resultDistribution2FilePath = basePath + "resultDistribution2.csv";
		
		File resultDistribution1File = new File(resultDistribution1FilePath);
		if (resultDistribution1File.exists()) { resultDistribution1File.delete(); }
		
		File resultDistribution2File = new File(resultDistribution2FilePath);
		if (resultDistribution2File.exists()) { resultDistribution2File.delete(); }
		
		
		GenstarCsvFile distribution1CsvFile = new GenstarCsvFile(distribution1FilePath, true);
		GenstarCsvFile distribution2CsvFile = new GenstarCsvFile(distribution2FilePath, true);
		List<GenstarCsvFile> distributionFormatCsvFiles = new ArrayList<GenstarCsvFile>();
		distributionFormatCsvFiles.add(distribution1CsvFile);
		distributionFormatCsvFiles.add(distribution2CsvFile);
		
		
		List<String> resultDistributionCsvFilePaths = new ArrayList<String>();
		resultDistributionCsvFilePaths.add(resultDistribution1FilePath);
		resultDistributionCsvFilePaths.add(resultDistribution2FilePath);
		
		
		GenstarCsvFile sampleDataOrPopulationFile = new GenstarCsvFile(basePath + "sampleData.csv", true);
		
		List<String> results = FrequencyDistributionUtils.generateAndSaveFrequencyDistributions(generator, sampleDataOrPopulationFile, 
				distributionFormatCsvFiles, resultDistributionCsvFilePaths);
		assertTrue(results.size() == 2);
		
		File recreatedResultDistribution1File = new File(resultDistribution1FilePath);
		assertTrue(recreatedResultDistribution1File.exists());
		
		File recreatedCesultDistribution2File = new File(resultDistribution2FilePath);
		assertTrue(recreatedCesultDistribution2File.exists());
	}

	
	@Test public void testGenerateAndSaveFrequencyDistribution() throws GenstarException {
		/*
	public static String generateAndSaveFrequencyDistribution(final SampleFreeGenerator generator, final GenstarCsvFile sampleDataOrPopulationFile, 
			final GenstarCsvFile distributionFormatCsvFile, final String resultDistributionCsvFilePath) throws GenstarException {
		 */
		
		String basePath = "test_data/ummisco/genstar/util/FrequencyDistributionUtils/testGenerateAndSaveFrequencyDistribution/";
		
		SampleFreeGenerator generator = new SampleFreeGenerator("generator");
		GenstarCsvFile attributesFile = new GenstarCsvFile(basePath + "attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		/*
		 sampleData.csv
		category,gender,age
		C0,false,5
		C0,true,5
		C1,false,5
		C1,true,5
		C1,false,10
		 */
		GenstarCsvFile sampleDataOrPopulationFile = new GenstarCsvFile(basePath + "sampleData.csv", true);
		
		GenstarCsvFile distributionFormatCsvFile1 = new GenstarCsvFile(basePath + "distributionFormat1.csv", true);
		
		String resultDistributionCsvFilePath1 = basePath + "resultDistribution1.csv";
		File resultFile1 = new File(resultDistributionCsvFilePath1);
		if (resultFile1.exists()) { resultFile1.delete(); }
		
		File deletedResultFile1 = new File(resultDistributionCsvFilePath1);
		assertFalse(deletedResultFile1.exists());
		
		FrequencyDistributionUtils.generateAndSaveFrequencyDistribution(generator, sampleDataOrPopulationFile, distributionFormatCsvFile1, resultDistributionCsvFilePath1);
		
		File recreatedResultFile1 = new File(resultDistributionCsvFilePath1);
		assertTrue(recreatedResultFile1.exists());
		
		
		/*
		 distributionFormat1.csv
		 	Category:Output,Age:Output
		 */
		GenstarCsvFile recreatedResultCsvFile1 = new GenstarCsvFile(resultDistributionCsvFilePath1, true);
		assertTrue(recreatedResultCsvFile1.getColumns() == 3);
		assertTrue(recreatedResultCsvFile1.getRows() == 5);
		
		/*
		 resultDistribution1.csv
		 	Category:Output,Age:Output,Frequency
		 	C0,0:4,0
		 	C0,5:17,2
		 	C1,0:4,0
		 	C1,5:17,3
		 */
		List<String> resultDistribution1Header = recreatedResultCsvFile1.getHeaders();
		assertTrue(resultDistribution1Header.size() == 3);
		assertTrue(resultDistribution1Header.get(0).equals("Category:Output"));
		assertTrue(resultDistribution1Header.get(1).equals("Age:Output"));
		assertTrue(resultDistribution1Header.get(2).equals("Frequency"));
		
		
		FrequencyDistributionGenerationRule rule1 = FrequencyDistributionUtils.createFrequencyDistributionGenerationRuleFromRuleDataFile(generator, "rule 1", recreatedResultCsvFile1);
		
		AbstractAttribute categoryAttribute = generator.getAttributeByNameOnData("Category");
		AbstractAttribute ageAttribute = generator.getAttributeByNameOnData("Age");
		
		AttributeValue c0Value = new UniqueValue(DataType.STRING, "C0"); 
		AttributeValue c1Value = new UniqueValue(DataType.STRING, "C1");
		
		AttributeValue age_0_4 = new RangeValue(DataType.INTEGER, "0", "4");
		AttributeValue age_5_17 = new RangeValue(DataType.INTEGER, "5", "17");
		
		Map<AbstractAttribute, AttributeValue> attributeValues1 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues1.put(categoryAttribute, c0Value);
		attributeValues1.put(ageAttribute, age_0_4);
		
		List<AttributeValuesFrequency> avfs1 = rule1.findAttributeValuesFrequencies(attributeValues1);
		assertTrue(avfs1.size() == 1);
		assertTrue(avfs1.get(0).getFrequency() == 0);

		Map<AbstractAttribute, AttributeValue> attributeValues2 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues2.put(categoryAttribute, c0Value);
		attributeValues2.put(ageAttribute, age_5_17);
		
		List<AttributeValuesFrequency> avfs2 = rule1.findAttributeValuesFrequencies(attributeValues2);
		assertTrue(avfs2.size() == 1);
		assertTrue(avfs2.get(0).getFrequency() == 2);
		
		
		Map<AbstractAttribute, AttributeValue> attributeValues3 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues3.put(categoryAttribute, c1Value);
		attributeValues3.put(ageAttribute, age_0_4);
		
		List<AttributeValuesFrequency> avfs3 = rule1.findAttributeValuesFrequencies(attributeValues3);
		assertTrue(avfs3.size() == 1);
		assertTrue(avfs3.get(0).getFrequency() == 0);

		
		Map<AbstractAttribute, AttributeValue> attributeValues4 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues4.put(categoryAttribute, c1Value);
		attributeValues4.put(ageAttribute, age_5_17);
		
		List<AttributeValuesFrequency> avfs4 = rule1.findAttributeValuesFrequencies(attributeValues4);
		assertTrue(avfs4.size() == 1);
		assertTrue(avfs4.get(0).getFrequency() == 3);
		
		
		/*
		 distributionFormat2.csv
		 	Category:Output,Gender:Output,Age:Output
		 */
		GenstarCsvFile distributionFormatCsvFile2 = new GenstarCsvFile(basePath + "distributionFormat2.csv", true);
		
		String resultDistributionCsvFilePath2 = basePath + "resultDistribution2.csv";
		File resultFile2 = new File(resultDistributionCsvFilePath2);
		if (resultFile2.exists()) { resultFile2.delete(); }
		
		File deletedResultFile2 = new File(resultDistributionCsvFilePath2);
		assertFalse(deletedResultFile2.exists());
		
		FrequencyDistributionUtils.generateAndSaveFrequencyDistribution(generator, sampleDataOrPopulationFile, distributionFormatCsvFile2, resultDistributionCsvFilePath2);
		
		File recreatedResultFile2 = new File(resultDistributionCsvFilePath2);
		assertTrue(recreatedResultFile2.exists());
		 
		/*
		 sampleData.csv
		category,gender,age
		C0,false,5
		C0,true,5
		C1,false,5
		C1,true,5
		C1,false,10
		 */
		/*
		 resultDistribution2.csv
		 	Category:Output,Gender:Output,Age:Output,Frequency
		 	C0,false,0:4,0
		 	C0,false,5:17,1
		 	C0,true,0:4,0
		 	C0,true,5:17,1
		 	C1,false,0:4,0
		 	C1,false,5:17,2
		 	C1,true,0:4,0
		 	C1,true,5:17,1
		 */
		
		GenstarCsvFile recreatedResultCsvFile2 = new GenstarCsvFile(resultDistributionCsvFilePath2, true);
		assertTrue(recreatedResultCsvFile2.getColumns() == 4);
		assertTrue(recreatedResultCsvFile2.getRows() == 9);
		
		FrequencyDistributionGenerationRule distributionRule2 = FrequencyDistributionUtils.createFrequencyDistributionGenerationRuleFromRuleDataFile(generator, "rule 2", recreatedResultCsvFile2);

		AbstractAttribute genderAttribute = generator.getAttributeByNameOnData("Gender");
		AttributeValue falseValue = new UniqueValue(DataType.BOOL, "false");
		AttributeValue trueValue = new UniqueValue(DataType.BOOL, "true");
		
		Map<AbstractAttribute, AttributeValue> attributeValues5 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues5.put(categoryAttribute, c0Value);
		attributeValues5.put(ageAttribute, age_0_4);
		attributeValues5.put(genderAttribute, falseValue);
		
		List<AttributeValuesFrequency> avfs5 = distributionRule2.findAttributeValuesFrequencies(attributeValues5);
		assertTrue(avfs5.size() == 1);
		assertTrue(avfs5.get(0).getFrequency() == 0);
		

		Map<AbstractAttribute, AttributeValue> attributeValues6 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues6.put(categoryAttribute, c1Value);
		attributeValues6.put(ageAttribute, age_5_17);
		attributeValues6.put(genderAttribute, trueValue);

		List<AttributeValuesFrequency> avfs6 = distributionRule2.findAttributeValuesFrequencies(attributeValues6);
		assertTrue(avfs6.size() == 1);
		assertTrue(avfs6.get(0).getFrequency() == 1);
		 
	}
	
	
	@Test public void testCreateFrequencyDistributionGenerationRuleFromRuleDataFile() throws GenstarException {
		
		/*
	public static FrequencyDistributionGenerationRule createFrequencyDistributionGenerationRuleFromRuleDataFile(final SampleFreeGenerator generator, final String ruleName, final GenstarCsvFile ruleFile) throws GenstarException {
		 */
		
		String basePath = "test_data/ummisco/genstar/util/FrequencyDistributionUtils/testCreateFrequencyDistributionGenerationRuleFromRuleDataFile/";
		
		SampleFreeGenerator generator = new SampleFreeGenerator("generator");
		GenstarCsvFile attributesFile = new GenstarCsvFile(basePath + "attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		String ruleName1 = "rule 1";
		
		/*
		 ruleData1.csv
		 	Category:Output,Age:Output,Frequency
			C0,0:4,0
			C0,5:17,2
			C1,0:4,0
			C1,5:17,3
		 */
		GenstarCsvFile ruleFile1 = new GenstarCsvFile(basePath + "ruleData1.csv", true);
		FrequencyDistributionGenerationRule distributionRule1 = FrequencyDistributionUtils.createFrequencyDistributionGenerationRuleFromRuleDataFile(generator, ruleName1, ruleFile1);
		
		AbstractAttribute categoryAttribute = generator.getAttributeByNameOnData("Category");
		AbstractAttribute ageAttribute = generator.getAttributeByNameOnData("Age");
		
		AttributeValue c0Value = new UniqueValue(DataType.STRING, "C0"); 
		AttributeValue c1Value = new UniqueValue(DataType.STRING, "C1");
		
		AttributeValue age_0_4 = new RangeValue(DataType.INTEGER, "0", "4");
		AttributeValue age_5_17 = new RangeValue(DataType.INTEGER, "5", "17");
		
		Map<AbstractAttribute, AttributeValue> attributeValues1 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues1.put(categoryAttribute, c0Value);
		attributeValues1.put(ageAttribute, age_0_4);
		
		List<AttributeValuesFrequency> avfs1 = distributionRule1.findAttributeValuesFrequencies(attributeValues1);
		assertTrue(avfs1.size() == 1);
		assertTrue(avfs1.get(0).getFrequency() == 0);
		

		Map<AbstractAttribute, AttributeValue> attributeValues2 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues2.put(categoryAttribute, c0Value);
		attributeValues2.put(ageAttribute, age_5_17);
		
		List<AttributeValuesFrequency> avfs2 = distributionRule1.findAttributeValuesFrequencies(attributeValues2);
		assertTrue(avfs2.size() == 1);
		assertTrue(avfs2.get(0).getFrequency() == 2);
		
		
		Map<AbstractAttribute, AttributeValue> attributeValues3 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues3.put(categoryAttribute, c1Value);
		attributeValues3.put(ageAttribute, age_0_4);
		
		List<AttributeValuesFrequency> avfs3 = distributionRule1.findAttributeValuesFrequencies(attributeValues3);
		assertTrue(avfs3.size() == 1);
		assertTrue(avfs3.get(0).getFrequency() == 0);

		
		Map<AbstractAttribute, AttributeValue> attributeValues4 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues4.put(categoryAttribute, c1Value);
		attributeValues4.put(ageAttribute, age_5_17);
		
		List<AttributeValuesFrequency> avfs4 = distributionRule1.findAttributeValuesFrequencies(attributeValues4);
		assertTrue(avfs4.size() == 1);
		assertTrue(avfs4.get(0).getFrequency() == 3);
		

		/*
		 ruleData2.csv
		 	Category:Output,Age:Output,Gender:Output,Frequency
			C0,0:4,false,1
			C0,0:4,true,2
			C0,5:17,false,3
			C0,5:17,true,4
			C1,0:4,false,5
			C1,0:4,true,6
			C1,5:17,false,7
			C1,5:17,true,8
		 */
		GenstarCsvFile ruleFile2 = new GenstarCsvFile(basePath + "ruleData2.csv", true);
		FrequencyDistributionGenerationRule distributionRule2 = FrequencyDistributionUtils.createFrequencyDistributionGenerationRuleFromRuleDataFile(generator, "rule 2", ruleFile2);
		assertTrue(distributionRule2.getAttributeValuesFrequencies().size() == 8);
		
		AbstractAttribute genderAttribute = generator.getAttributeByNameOnData("Gender");
		AttributeValue falseValue = new UniqueValue(DataType.BOOL, "false");
		AttributeValue trueValue = new UniqueValue(DataType.BOOL, "true");
		
		Map<AbstractAttribute, AttributeValue> attributeValues5 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues5.put(categoryAttribute, c0Value);
		attributeValues5.put(ageAttribute, age_0_4);
		attributeValues5.put(genderAttribute, falseValue);
		
		List<AttributeValuesFrequency> avfs5 = distributionRule2.findAttributeValuesFrequencies(attributeValues5);
		assertTrue(avfs5.size() == 1);
		assertTrue(avfs5.get(0).getFrequency() == 1);
		

		Map<AbstractAttribute, AttributeValue> attributeValues6 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues6.put(categoryAttribute, c1Value);
		attributeValues6.put(ageAttribute, age_5_17);
		attributeValues6.put(genderAttribute, trueValue);

		List<AttributeValuesFrequency> avfs6 = distributionRule2.findAttributeValuesFrequencies(attributeValues6);
		assertTrue(avfs6.size() == 1);
		assertTrue(avfs6.get(0).getFrequency() == 8);
	}
}
