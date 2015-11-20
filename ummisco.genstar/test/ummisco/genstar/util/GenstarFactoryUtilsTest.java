package ummisco.genstar.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.Assert;
import org.junit.Test;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.MultipleRulesGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;

public class GenstarFactoryUtilsTest {

	@Test
	public final void testCreateUniqueValueAttribut(@Mocked final UniqueValuesAttribute attribute, @Mocked final UniqueValue uValue, @Mocked final ISyntheticPopulationGenerator mockedGenerator) throws GenstarException {
		
		new Expectations() {{
			new UniqueValuesAttribute(mockedGenerator, "Category", "category", DataType.STRING, UniqueValue.class);
			times = 1;
			
			new UniqueValue((DataType) any, anyString);
			times = 8;
			
			attribute.add((UniqueValue) any);
			times = 8;
			
			mockedGenerator.addAttribute((AbstractAttribute) any);
			times = 1;
		}};
		
		GenstarFactoryUtils.createUniqueValueAttribute(mockedGenerator, "Category", "category", DataType.STRING, "C0; C1; C2; C3; C4; C5; C6; C7", UniqueValue.class);
	}


	@Test
	public final void testCreateRangeValueAttribute(@Mocked final ISyntheticPopulationGenerator mockedGenerator,
			@Mocked final RangeValuesAttribute mockedRangeValuesAttribute, @Mocked final RangeValue mockedRangeValue) throws GenstarException {
		
		// record
		new Expectations() {{
			new RangeValuesAttribute(mockedGenerator, "Age", "age", DataType.INTEGER, UniqueValue.class);
			times = 1;
			
			new RangeValue(DataType.INTEGER, (String) withNotNull(), (String) withNotNull());
			times = 7;
			
			mockedRangeValuesAttribute.add((RangeValue) any);
			times = 7;
			
			mockedGenerator.addAttribute((AbstractAttribute) any);
			times = 1;
		}};
		
		// replay
		GenstarFactoryUtils.createRangeValueAttribute(mockedGenerator, "Age", "age", DataType.INTEGER, "0:4; 5:17; 18:24; 25:34; 35:49; 50:64; 65:100", 
				UniqueValue.class);
	}


	@Test
	public final void testCreateAttributesFromCSVFileExpectationAproach(@Mocked final ISyntheticPopulationGenerator generator, @Mocked final GenstarCSVFile mockedAttributesCSVFile) throws GenstarException {
		
		final List<String> headers = new ArrayList<String>();
		headers.add("Name On Data");
		headers.add("Name On Entity");
		headers.add("Data Type");
		headers.add("Value Type On Data");
		headers.add("Values");
		headers.add("Value Type On Entity");

		final List<String> line0 = new ArrayList<String>();
		line0.add("Category");
		line0.add("category");
		line0.add("string");
		line0.add("Unique");
		line0.add("C0; C1; C2; C3; C4; C5; C6; C7");
		line0.add("Unique");
		
		final List<String> line1 = new ArrayList<String>();
		line1.add("Gender");
		line1.add("gender");
		line1.add("bool");
		line1.add("Unique");
		line1.add("true; false");
		line1.add("Unique");
		
		final List<String> line2 = new ArrayList<String>();
		line2.add("Age");
		line2.add("age");
		line2.add("int");
		line2.add("Range");
		line2.add("0:4; 5:17; 18:24; 25:34; 35:49; 50:64; 65:100");
		line2.add("Unique");
		
		final List<List<String>> content = new ArrayList<List<String>>();
		content.add(line0);
		content.add(line1);
		content.add(line2);
		
		new Expectations() {{
			mockedAttributesCSVFile.getContent();
			times = 1;
			result = content;

			mockedAttributesCSVFile.getHeaders();
			times = 1;
			result = headers;
			
			mockedAttributesCSVFile.getPath();
			result = "Dummy CSV File";
			
			mockedAttributesCSVFile.getColumns();
			result = GenstarFactoryUtils.CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS;

			generator.addAttribute((AbstractAttribute) any);
			times = 3;
		}};
		
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, mockedAttributesCSVFile);
	}	
	
	
	@Test public void testCreateFrequencyDistributionGenerationFromSampleData() throws GenstarException {
		// input: distributionFormatCSVFile & sampleDataCSVFile
		// output: the newly created FrequencyDistributionGenerationRule
		
		ISyntheticPopulationGenerator generator = new MultipleRulesGenerator("dummy generator", 10);
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateFrequencyDistributionGenerationFromSampleData/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		
		GenstarCSVFile distributionFormatCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateFrequencyDistributionGenerationFromSampleData/distributionFormat.csv", true);
		GenstarCSVFile sampleDataCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateFrequencyDistributionGenerationFromSampleData/sampleData.csv", true);
		
		FrequencyDistributionGenerationRule rule = GenstarFactoryUtils.createFrequencyDistributionFromSampleData(generator, distributionFormatCSVFile, sampleDataCSVFile);
		assertTrue(rule.getAttributes().size() == 2);
		assertTrue(rule.getInputAttributeAtOrder(0) == null);
		assertTrue(rule.getOutputAttributeAtOrder(0).getNameOnData().equals("Category"));
		assertTrue(rule.getOutputAttributeAtOrder(1).getNameOnData().equals("Age"));
		int totalNbOfAttributeValues = 1;
		for (AbstractAttribute a : rule.getAttributes()) { totalNbOfAttributeValues *= a.values().size(); }
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
		map1.put(generator.getAttribute("Category"), c0Value);
		
		RangeValuesAttribute ageAttribute = (RangeValuesAttribute) generator.getAttribute("Age");
		Set<AttributeValue> ageValues = ageAttribute.values();
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
			if (f.isMatch(map1)) {
				if (isMatchMap1) { Assert.fail("Attributevalue is matched several times (map1)"); }
				
				assertTrue(f.getFrequency() == 1);
				isMatchMap1 = true;
			}
		}
		if (!isMatchMap1) { Assert.fail("No attributeValue is matched (map1)"); }
		
		// C3, 5 (frequency == 2)
		Map<AbstractAttribute, AttributeValue> map2 = new HashMap<AbstractAttribute, AttributeValue>();
		AttributeValue c5Value = new UniqueValue(DataType.STRING, "C3");
		map2.put(generator.getAttribute("Category"), c5Value);
		map2.put(ageAttribute, age5RangeValue);
		
		boolean isMatchMap2 = false;
		for (AttributeValuesFrequency f : attributeValuesFrequencies) {
			if (f.isMatch(map2)) {
				if (isMatchMap2) { Assert.fail("Attributevalue is matched several times (map2)"); }
				
				assertTrue(f.getFrequency() == 2);
				isMatchMap2 = true;
			}
		}
		if (!isMatchMap2) { Assert.fail("No attributeValue is matched (map2)"); }
	}		
	
}
