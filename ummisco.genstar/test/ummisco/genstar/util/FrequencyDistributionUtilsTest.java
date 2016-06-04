package ummisco.genstar.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
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

	@Test public void testCreateFrequencyDistributionGenerationFromSampleDataOrPopulationFile() throws GenstarException {
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

}
