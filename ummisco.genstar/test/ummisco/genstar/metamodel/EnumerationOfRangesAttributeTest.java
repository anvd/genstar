package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ummisco.genstar.exception.GenstarException;

public class EnumerationOfRangesAttributeTest {

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
	
	@Test public void testCreateValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		List<String> list1 = new ArrayList<String>();
		list1.add("1");
		list1.add("2");
		AttributeValue rangeValue = attr.valueFromString(list1);
		
		RangeValue anotherRange = new RangeValue(DataType.INTEGER, "1", "2");
		
		assertTrue(rangeValue.equals(anotherRange));
		
		exception.expect(IllegalArgumentException.class);
		list1.remove(0);
		attr.valueFromString(list1);
	}

	@Test public void testCastDefaultValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		RangeValuesAttribute attr = new RangeValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER, UniqueValue.class);
		
		assertTrue(attr.getDefaultValue() instanceof UniqueValue);
		assertTrue(attr.getDefaultValue().isValueMatch(new UniqueValue(DataType.INTEGER)));

	}
}
