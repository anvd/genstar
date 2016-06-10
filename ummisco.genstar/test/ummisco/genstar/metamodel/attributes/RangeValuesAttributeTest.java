package ummisco.genstar.metamodel.attributes;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;

public class RangeValuesAttributeTest {

	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testNullPopulationParamConstructor() throws GenstarException {
		exception.expect(GenstarException.class);
		new RangeValuesAttribute("data var name", "entity var name", DataType.BOOL);
	}
	
	@Test
	public void testNullNameParamConstructor() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		
		exception.expect(GenstarException.class);
		new RangeValuesAttribute(null, null, DataType.BOOL);
	}
	
	@Test
	public void testNullValueTypeConstructor() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);

		exception.expect(GenstarException.class);
		new RangeValuesAttribute(null, null, (Class)null);
	}
	
	@Test
	public void testAddRangeValue() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute("data var name", "entity var name", DataType.INTEGER);
		
		assertTrue(attr.valuesOnData().isEmpty());
		
		assertTrue(attr.add(new RangeValue(DataType.INTEGER, "1", "2", attr)));
		assertTrue(attr.valuesOnData().size() == 1);
		
		assertFalse(attr.add(new RangeValue(DataType.INTEGER, "1", "2", attr)));
	}
	
	@Test
	public void testAddNullRangeValue() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute("data var name", "entity var name", DataType.INTEGER);

		exception.expect(GenstarException.class);
		attr.add(null);
	}
	
	@Test
	public void testAddNullRangeValues() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute("data var name", "entity var name", DataType.INTEGER);
		
		exception.expect(GenstarException.class);
		attr.addAll(null);
	}
	
	/*
	@Test public void testConstainsInstanceOfAttributeValue() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		List<String> list1 = new ArrayList<String>();
		list1.add("1");
		list1.add("2");
		AttributeValue rangeValue = new RangeValue(DataType.INTEGER, "1", "2");
		
		assertTrue(attr.containsInstanceOfAttributeValue(rangeValue) == false);
		
		attr.add(rangeValue);
		assertTrue(attr.containsInstanceOfAttributeValue(rangeValue));
	}
	*/

	@Test public void testDefaultValueOnEntity() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute("data var name", "entity var name", DataType.INTEGER, UniqueValue.class);
		
		assertTrue(attr.getDefaultValueOnEntity() instanceof UniqueValue);
		assertTrue(attr.getDefaultValueOnEntity().isValueMatched(new UniqueValue(DataType.INTEGER, attr)));
	}
	
	@Test public void testDefaultValueOnData() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute("data var name", "entity var name", DataType.INTEGER, UniqueValue.class);
		
		assertTrue(attr.getDefaultValueOnData() instanceof RangeValue);
		assertTrue(attr.getDefaultValueOnData().isValueMatched(new RangeValue(DataType.INTEGER, "0", "0", attr)));
	}
	
	
	@Test public void testFindCorrespondingAttributeValue() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute("data var name", "entity var name", DataType.INTEGER);
		
		List<String> list1 = new ArrayList<String>();
		list1.add("1");
		list1.add("2");
		assertTrue(attr.findCorrespondingAttributeValueOnData(list1) == null);

		attr.add(new RangeValue(DataType.INTEGER, "1", "2", attr));
		assertTrue(attr.findCorrespondingAttributeValueOnData(list1) != null);
		
		
		List<String> list2 = new ArrayList<String>();
		list2.add("0");
		assertTrue(attr.findCorrespondingAttributeValueOnData(list2) == null);
		
		
		List<String> list3 = new ArrayList<String>();
		list3.add("1");
		assertTrue(attr.findCorrespondingAttributeValueOnData(list3) != null);
		
		
		List<String> list4 = new ArrayList<String>();
		list4.add("2");
		assertTrue(attr.findCorrespondingAttributeValueOnData(list4) != null);
		
		
		List<String> list5 = new ArrayList<String>();
		list5.add("3");
		assertTrue(attr.findCorrespondingAttributeValueOnData(list5) == null);
	}
	
	@Test public void testFindMatchingAttributeValue() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute("data var name", "entity var name", DataType.INTEGER);
		
		List<String> list1 = new ArrayList<String>();
		list1.add("1");
		list1.add("3");
		AttributeValue rangeValue = new RangeValue(DataType.INTEGER, "1", "3", attr);
		attr.add(rangeValue);

		AttributeValue rangeValue1 = new RangeValue(DataType.INTEGER, "1", "3", attr);
		AttributeValue matchingValue1 = attr.findMatchingAttributeValueOnData(rangeValue1);
		assertTrue(matchingValue1 instanceof RangeValue);
		assertTrue(matchingValue1.equals(rangeValue));
		
		AttributeValue rangeValue2 = new RangeValue(DataType.INTEGER, "0", "3", attr);
		AttributeValue matchingValue2 = attr.findMatchingAttributeValueOnData(rangeValue2);
		assertTrue(matchingValue2 == null);
		
		AttributeValue uniqueValue1 = new UniqueValue(DataType.INTEGER, "1", attr);
		AttributeValue matchingValue3 = attr.findMatchingAttributeValueOnData(uniqueValue1);
		assertTrue(matchingValue3.equals(rangeValue));
		
		AttributeValue uniqueValue2 = new UniqueValue(DataType.INTEGER, "4", attr);
		AttributeValue matchingValue4 = attr.findMatchingAttributeValueOnData(uniqueValue2);
		assertTrue(matchingValue4 == null);
	}

}
