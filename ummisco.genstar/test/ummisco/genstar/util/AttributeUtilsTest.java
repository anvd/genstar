package ummisco.genstar.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttributeWithRangeInput;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;

@RunWith(JMockit.class)
public class AttributeUtilsTest {

	@Test
	public final void testCreateUniqueValueAttribut(@Mocked final UniqueValuesAttribute attribute, @Mocked final UniqueValue uValue, @Mocked final ISyntheticPopulationGenerator mockedGenerator) throws GenstarException {
		
		new Expectations() {{
			new UniqueValuesAttribute("Category", "category", DataType.STRING, UniqueValue.class);
			times = 1;
			
			new UniqueValue((DataType) any, anyString, attribute);
			times = 8;
			
			attribute.add((UniqueValue) any);
			times = 8;
			
			mockedGenerator.addAttribute((AbstractAttribute) any);
			times = 1;
		}};
		
		AttributeUtils.createUniqueValueAttribute(mockedGenerator, "Category", "category", DataType.STRING, "C0; C1; C2; C3; C4; C5; C6; C7", UniqueValue.class);
	}


	@Test
	public final void testCreateRangeValueAttribute(@Mocked final ISyntheticPopulationGenerator mockedGenerator,
			@Mocked final RangeValuesAttribute mockedRangeValuesAttribute, @Mocked final RangeValue mockedRangeValue) throws GenstarException {
		
		// record
		new Expectations() {{
			new RangeValuesAttribute("Age", "age", DataType.INTEGER, UniqueValue.class);
			times = 1;
			
			new RangeValue(DataType.INTEGER, (String) withNotNull(), (String) withNotNull(), mockedRangeValuesAttribute);
			times = 7;
			
			mockedRangeValuesAttribute.add((RangeValue) any);
			times = 7;
			
			mockedGenerator.addAttribute((AbstractAttribute) any);
			times = 1;
		}};
		
		// replay
		AttributeUtils.createRangeValueAttribute(mockedGenerator, "Age", "age", DataType.INTEGER, "0:4; 5:17; 18:24; 25:34; 35:49; 50:64; 65:100", 
				UniqueValue.class);
	}


	@Test public final void testCreateAttributesFromCsvFileExpectationApproach(@Mocked final ISyntheticPopulationGenerator generator, @Mocked final GenstarCsvFile mockedAttributesCSVFile) throws GenstarException {
		
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
			result = CSV_FILE_FORMATS.ATTRIBUTES.NB_OF_COLS;

			generator.addAttribute((AbstractAttribute) any);
			times = 3;
		}};
		
		AttributeUtils.createAttributesFromCsvFile(generator, mockedAttributesCSVFile);
	}	
	
	@Test public void testCreateAttributesFromCsvFile() throws GenstarException {
		fail("not yet implemented");
	}
	
	@Test public void testCreateUniqueValueAttributeWithRangeInput() throws GenstarException {
		
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("generator");
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/AttributeUtils/testCreateUniqueValueAttributeWithRangeInput/attributes1.csv", true);
		
		assertTrue(generator.getAttributes().isEmpty());
		
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		List<AbstractAttribute> attributes = new ArrayList<>(generator.getAttributes());
		assertTrue(attributes.size() == 2);
		
		assertTrue(attributes.get(0) instanceof UniqueValuesAttributeWithRangeInput);
		assertTrue(attributes.get(1) instanceof UniqueValuesAttributeWithRangeInput);
	}
}
