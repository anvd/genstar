package ummisco.genstar.metamodel.attributes;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;

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

		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		new UniqueValuesAttribute(p, null, "entity var name", DataType.BOOL);
	}
	
	@Test
	public void testNullEntityVarNameConstructor() throws GenstarException {
		exception.expect(GenstarException.class);

		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		new UniqueValuesAttribute(p, "data var name", null, DataType.BOOL);
	}
	
	@Test
	public void testNullValueTypeConstructor() throws GenstarException {
		exception.expect(GenstarException.class);
		
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		new UniqueValuesAttribute(p, "data var name", "entity var name", null);
	}
	
	@Test
	public void testNullParamsConstructor() throws GenstarException {
		exception.expect(GenstarException.class);
		
		new UniqueValuesAttribute(null, null, null, (Class)null);
	}
	
	@Test
	public void testValidParamsConstructor() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.BOOL);
	}

	@Test
	public void testAddNullValue() throws GenstarException {
		exception.expect(GenstarException.class);

		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		attr.add(null);
	}
	
	@Test
	public void testAddValidValue() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		attr.add(new UniqueValue(DataType.INTEGER, "10"));
	}
	
	@Test
	public void testAddNullValues() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);

		exception.expect(GenstarException.class);
		attr.addAll(null);
	}
	
	@Test
	public void testGetValues() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);

		assertTrue(attr.valuesOnData().isEmpty());
		
		attr.add(new UniqueValue(DataType.INTEGER, "1"));
		assertTrue(attr.valuesOnData().size() == 1);
	}
	
	@Test public void testMatchingAttributeValueOnData() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		List<String> list1 = new ArrayList<String>();
		list1.add("1");
		AttributeValue oneValue = attr.getMatchingAttributeValueOnData(list1);
		assertTrue(oneValue == null);

		UniqueValue uniqueValue = new UniqueValue(DataType.INTEGER, "1");
		attr.add(uniqueValue);

		oneValue = attr.getMatchingAttributeValueOnData(list1);
		assertTrue( ((UniqueValue) oneValue).getStringValue().equals("1"));
	}
	
	
	@Test public void testAddDuplicatedValues1() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		UniqueValue testValue = new UniqueValue(DataType.INTEGER, "1");
		assertTrue(attr.add(testValue));
		
		assertFalse(attr.add(testValue));
		
		UniqueValue testValue1 = new UniqueValue(DataType.INTEGER, "1");
		assertFalse(attr.add(testValue1));
	}
	
	@Test public void testGetInstanceOfValue() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute(p, "data var name", "entity var name", DataType.INTEGER);
		
		UniqueValue testValue = new UniqueValue(DataType.INTEGER, "1");
		
		assertTrue(attr.getInstanceOfAttributeValue(testValue) == null);
		
		attr.add(testValue);
		assertTrue(attr.getInstanceOfAttributeValue(testValue) != null);
		
		UniqueValue testValue1 = new UniqueValue(DataType.INTEGER, "1");
		assertTrue(attr.getInstanceOfAttributeValue(testValue1).equals(testValue));
	}
	
	@Test public void testGetDefaultValueOnEntity() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);

		UniqueValuesAttribute attribute1 = new UniqueValuesAttribute(p, "dummy var", DataType.INTEGER);
		assertTrue(attribute1.getDefaultValueOnEntity() instanceof UniqueValue);
		assertTrue(attribute1.getDefaultValueOnEntity().isValueMatched(new UniqueValue(DataType.INTEGER)));

		UniqueValuesAttribute attribute2 = new UniqueValuesAttribute(p, "dummy var", DataType.INTEGER, RangeValue.class);
		assertTrue(attribute2.getDefaultValueOnEntity() instanceof RangeValue);
		assertTrue(attribute2.getDefaultValueOnEntity().isValueMatched(new RangeValue(DataType.INTEGER)));
	}
	
	@Test public void testGetDefaultValueOnData() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);

		UniqueValuesAttribute attribute1 = new UniqueValuesAttribute(p, "dummy var", DataType.INTEGER);
		assertTrue(attribute1.getDefaultValueOnData() instanceof UniqueValue);
		assertTrue(attribute1.getDefaultValueOnData().isValueMatched(new UniqueValue(DataType.INTEGER)));

		UniqueValuesAttribute attribute2 = new UniqueValuesAttribute(p, "dummy var", DataType.INTEGER, RangeValue.class);
		assertTrue(attribute2.getDefaultValueOnData() instanceof UniqueValue);
		assertTrue(attribute2.getDefaultValueOnData().isValueMatched(new UniqueValue(DataType.INTEGER)));
	}
	
	@Test public void testSetDefaultValue() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		
		UniqueValuesAttribute attribute1 = new UniqueValuesAttribute(p, "dummy var", DataType.INTEGER);

		AttributeValue defaultValue1 = new UniqueValue(DataType.INTEGER, "1");
		attribute1.setDefaultValue(defaultValue1);
		assertTrue(attribute1.getDefaultValueOnEntity().equals(defaultValue1));
		assertTrue(attribute1.getDefaultValueOnData().equals(defaultValue1));

		AttributeValue defaultValue2 = new RangeValue(DataType.INTEGER, "1", "2");
		exception.expect(GenstarException.class);
		attribute1.setDefaultValue(defaultValue2);
	}
	
}
