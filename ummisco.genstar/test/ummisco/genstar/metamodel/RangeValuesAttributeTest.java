package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ummisco.genstar.exception.GenstarException;

public class RangeValuesAttributeTest {

	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testNullPopulationParamConstructor() throws GenstarException {
		exception.expect(GenstarException.class);
		new RangeValuesAttribute(null, "data var name", "entity var name", DataType.BOOL);
	}
	
	@Test
	public void testNullNameParamConstructor() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		
		exception.expect(GenstarException.class);
		new RangeValuesAttribute(p, null, null, DataType.BOOL);
	}
	
	@Test
	public void testNullValueTypeConstructor() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);

		exception.expect(GenstarException.class);
		new RangeValuesAttribute(p, null, null, (Class)null);
	}
	
	@Test
	public void testAddRangeValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		assertTrue(attr.values().isEmpty());
		
		assertTrue(attr.add(new RangeValue(DataType.INTEGER, "1", "2")));
		assertTrue(attr.values().size() == 1);
		
		assertFalse(attr.add(new RangeValue(DataType.INTEGER, "1", "2")));
	}
	
	@Test
	public void testAddNullRangeValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);

		exception.expect(GenstarException.class);
		attr.add(null);
	}
	
	@Test
	public void testAddNullRangeValues() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		exception.expect(GenstarException.class);
		attr.addAll(null);
	}
	
	@Test public void testConstainsInstanceOfAttributeValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		List<String> list1 = new ArrayList<String>();
		list1.add("1");
		list1.add("2");
		AttributeValue rangeValue = new RangeValue(DataType.INTEGER, "1", "2");
		
		assertTrue(attr.containsInstanceOfAttributeValue(rangeValue) == false);
		
		attr.add(rangeValue);
		assertTrue(attr.containsInstanceOfAttributeValue(rangeValue));
	}

	@Test public void testCastDefaultValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER, UniqueValue.class);
		
		assertTrue(attr.getDefaultValue() instanceof UniqueValue);
		assertTrue(attr.getDefaultValue().isValueMatch(new UniqueValue(DataType.INTEGER)));
	}
	
	@Test public void testFindCorrespondingAttributeValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		List<String> list1 = new ArrayList<String>();
		list1.add("1");
		list1.add("2");
		assertTrue(attr.findCorrespondingAttributeValue(list1) == null);

		attr.add(new RangeValue(DataType.INTEGER, "1", "2"));
		assertTrue(attr.findCorrespondingAttributeValue(list1) != null);
		
		
		List<String> list2 = new ArrayList<String>();
		list2.add("0");
		assertTrue(attr.findCorrespondingAttributeValue(list2) == null);
		
		
		List<String> list3 = new ArrayList<String>();
		list3.add("1");
		assertTrue(attr.findCorrespondingAttributeValue(list3) != null);
		
		
		List<String> list4 = new ArrayList<String>();
		list4.add("2");
		assertTrue(attr.findCorrespondingAttributeValue(list4) != null);
		
		
		List<String> list5 = new ArrayList<String>();
		list5.add("3");
		assertTrue(attr.findCorrespondingAttributeValue(list5) == null);
	}

}
