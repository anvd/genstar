package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ummisco.genstar.exception.GenstarException;

@RunWith(JUnit4.class)
public class UniqueValuesAttributeTest {
	
	@Rule public ExpectedException exception = ExpectedException.none();
	

	@Test
	public void testNullPopulationParamConstructor() throws GenstarException {
		exception.expect(GenstarException.class);
		new UniqueValuesAttribute(null, "data var name", "entity var name", DataType.BOOL);
	}
	
	@Test
	public void testNullDataVarNameParamConstructor() throws GenstarException {
		exception.expect(GenstarException.class);

		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		new UniqueValuesAttribute(p, null, "entity var name", DataType.BOOL);
	}
	
	@Test
	public void testNullEntityVarNameConstructor() throws GenstarException {
		exception.expect(GenstarException.class);

		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		new UniqueValuesAttribute(p, "data var name", null, DataType.BOOL);
	}
	
	@Test
	public void testNullValueTypeConstructor() throws GenstarException {
		exception.expect(GenstarException.class);
		
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		new UniqueValuesAttribute(p, "data var name", "entity var name", null);
	}
	
	@Test
	public void testNullParamsConstructor() throws GenstarException {
		exception.expect(GenstarException.class);
		
		new UniqueValuesAttribute(null, null, null, (Class)null);
	}
	
	@Test
	public void testValidParamsConstructor() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.BOOL);
	}

	@Test
	public void testAddNullValue() throws GenstarException {
		exception.expect(GenstarException.class);

		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		attr.add(null);
	}
	
	@Test
	public void testAddValidValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		attr.add(new UniqueValue(DataType.INTEGER, "10"));
	}
	
	@Test
	public void testAddNullValues() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);

		exception.expect(GenstarException.class);
		attr.addAll(null);
	}
	
	@Test
	public void testGetValues() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);

		assertTrue(attr.values().isEmpty());
		
		attr.add(new UniqueValue(DataType.INTEGER, "1"));
		assertTrue(attr.values().size() == 1);
	}
	
	@Test public void testFindCorrespondingAttributeValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		List<String> list1 = new ArrayList<String>();
		list1.add("1");
		AttributeValue oneValue = attr.findCorrespondingAttributeValue(list1);
		assertTrue(oneValue == null);

		UniqueValue uniqueValue = new UniqueValue(DataType.INTEGER, "1");
		attr.add(uniqueValue);

		oneValue = attr.findCorrespondingAttributeValue(list1);
		assertTrue( ((UniqueValue) oneValue).getStringValue().equals("1"));
	}
	
	@Test public void testContainsValueOfAttributeValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		attr.add(new UniqueValue(DataType.INTEGER, "1"));
		
		assertTrue(attr.containsValueOfAttributeValue(new UniqueValue(DataType.INTEGER, "1")));
		
		UniqueValue testValue = new UniqueValue(DataType.INTEGER, "1");
		assertTrue(attr.containsValueOfAttributeValue(testValue));
	}
	
	@Test public void testConstainsInstanceOfAttributeValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		UniqueValue testValue = new UniqueValue(DataType.INTEGER, "1");
		attr.add(testValue);
		
		assertFalse(attr.containsInstanceOfAttributeValue(new UniqueValue(DataType.INTEGER, "1")));		
		assertTrue(attr.containsInstanceOfAttributeValue(testValue));
	}
	
	@Test public void testAddDuplicatedValues1() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		UniqueValue testValue = new UniqueValue(DataType.INTEGER, "1");
		assertTrue(attr.add(testValue));
		
		assertFalse(attr.add(testValue));
		
		UniqueValue testValue1 = new UniqueValue(DataType.INTEGER, "1");
		assertFalse(attr.add(testValue1));
	}
	
	@Test public void testGetInstanceOfValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		UniqueValue testValue = new UniqueValue(DataType.INTEGER, "1");
		
		assertTrue(attr.getInstanceOfAttributeValue(testValue) == null);
		
		attr.add(testValue);
		assertTrue(attr.getInstanceOfAttributeValue(testValue) != null);
		
		UniqueValue testValue1 = new UniqueValue(DataType.INTEGER, "1");
		assertTrue(attr.getInstanceOfAttributeValue(testValue1).equals(testValue));
	}
	
	
	@Test public void testDefaultAttributeValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);

		UniqueValuesAttribute attribute1 = new UniqueValuesAttribute(p, "dummy var", DataType.INTEGER);
		assertTrue(attribute1.getDefaultValue() instanceof UniqueValue);
		assertTrue(attribute1.getDefaultValue().isValueMatch(new UniqueValue(DataType.INTEGER)));
		
		AttributeValue defaultValue1 = new UniqueValue(DataType.INTEGER, "1");
		attribute1.setDefaultValue(defaultValue1);
		assertTrue(attribute1.getDefaultValue().equals(defaultValue1));
		
		AttributeValue defaultValue2 = new RangeValue(DataType.INTEGER, "1", "2");
		
		exception.expect(GenstarException.class);

		attribute1.setDefaultValue(defaultValue2);
		assertTrue(attribute1.getDefaultValue().equals(defaultValue2));
		
		attribute1.setDefaultValue(null);
	}
	
	@Test public void testCastDefaultValue() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("test population", 100);

		UniqueValuesAttribute attribute1 = new UniqueValuesAttribute(p, "dummy var", DataType.INTEGER, RangeValue.class);
		assertTrue(attribute1.getDefaultValue() instanceof RangeValue);
		assertTrue(attribute1.getDefaultValue().isValueMatch(new RangeValue(DataType.INTEGER)));
	}
}
