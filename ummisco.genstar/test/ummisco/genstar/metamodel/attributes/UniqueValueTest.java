package ummisco.genstar.metamodel.attributes;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;

public class UniqueValueTest {

	@Rule public ExpectedException exception = ExpectedException.none();

	@Test public void testInvalidParamConstructor1() throws GenstarException {
		exception.expect(GenstarException.class);
		new UniqueValue(DataType.INTEGER, null, null);
	}
	
	@Test public void testInvalidParamConstructor3() throws GenstarException {
		exception.expect(GenstarException.class);
		new UniqueValue(DataType.INTEGER, "a", null);
	}
	
	@Test public void testValidParamConstructor() throws GenstarException {
		new UniqueValue(DataType.INTEGER, "1", null);
	}
	
	@Test public void testSetInvalidStringValue() throws GenstarException {
		UniqueValue v = new UniqueValue(DataType.INTEGER, "0", null);
		exception.expect(GenstarException.class);
		v.setStringValue("A");
	}
	
	@Test public void testSetValidStringValue() throws GenstarException {
		UniqueValue v = new UniqueValue(DataType.INTEGER, "0", null);
		v.setStringValue("1");
		assertTrue(v.getStringValue().equals("1"));
	}
	
	@Test public void testCompareTo() throws GenstarException {
		UniqueValue v0 = new UniqueValue(DataType.INTEGER, "0", null);
		UniqueValue v1 = new UniqueValue(DataType.INTEGER, "0", null);
		UniqueValue v2 = new UniqueValue(DataType.INTEGER, "1", null);
		
		assertTrue(v0.compareTo(v0) == 0);
		assertTrue(v0.compareTo(v1) == 0);
		assertTrue(v0.compareTo(v2) == -1);
		assertTrue(v2.compareTo(v0) == 1);
	}
	
	
	@Test public void testCast() throws GenstarException {
		UniqueValue v0 = new UniqueValue(DataType.INTEGER, "0", null);
		
		AttributeValue result1 = v0.cast(RangeValue.class);
		assertTrue(result1 instanceof RangeValue);
		
		AttributeValue result2 = v0.cast(UniqueValue.class);
		assertTrue(result2 == v0);
	}
	
	@Test public void testIsValueMatched() throws GenstarException {
		UniqueValue v1 = new UniqueValue(DataType.INTEGER, "1", null);
		UniqueValue v2 = new UniqueValue(DataType.INTEGER, "1", null);
		UniqueValue v3 = new UniqueValue(DataType.INTEGER, "2", null);
		
		
		UniqueValue v4 = new UniqueValue(DataType.INTEGER, "1", null);
		UniqueValue v5 = new UniqueValue(DataType.INTEGER, "1", null);
		UniqueValue v6 = new UniqueValue(DataType.INTEGER, "2", null);
		
		
		RangeValue v7 = new RangeValue(DataType.INTEGER, "1", "2", null);
		RangeValue v8 = new RangeValue(DataType.INTEGER, "1", "2", null);
		RangeValue v9 = new RangeValue(DataType.INTEGER, "2", "3", null);
		
		
		RangeValue v10 = new RangeValue(DataType.FLOAT, "1", "2", null);
		RangeValue v11 = new RangeValue(DataType.FLOAT, "1", "2", null);
		RangeValue v12 = new RangeValue(DataType.FLOAT, "2", "3", null);
		
		
		UniqueValue v13 = new UniqueValue(DataType.STRING, "1", null);
		UniqueValue v14 = new UniqueValue(DataType.STRING, "1", null);
		UniqueValue v15 = new UniqueValue(DataType.STRING, "2", null);

		
		// assertions
		assertTrue(v1.isValueMatched(v2));
		assertFalse(v1.isValueMatched(v3));
		
		assertTrue(v1.isValueMatched(v4));
		assertTrue(v4.isValueMatched(v5));
		assertFalse(v4.isValueMatched(v6));
		
		assertTrue(v1.isValueMatched(v7));
		assertFalse(v1.isValueMatched(v9));
		assertTrue(v7.isValueMatched(v8));
		assertFalse(v8.isValueMatched(v9));
		
		assertTrue(v4.isValueMatched(v7));
		assertFalse(v4.isValueMatched(v9));
		
		assertTrue(v7.isValueMatched(v10));
		assertFalse(v7.isValueMatched(v12));
		
		assertTrue(v10.isValueMatched(v11));
		assertFalse(v10.isValueMatched(v12));
	
		assertFalse(v1.isValueMatched(v13));
		assertTrue(v13.isValueMatched(v14));
		assertFalse(v13.isValueMatched(v15));
	}

	@Test public void testFindMatchingAttributeValue() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("test population", 100);
		UniqueValuesAttribute attr = new UniqueValuesAttribute("data var name", "entity var name", DataType.INTEGER);
		
		List<String> list1 = new ArrayList<String>();
		list1.add("1");
		UniqueValue uniqueValue = new UniqueValue(DataType.INTEGER, "1", attr);
		attr.add(uniqueValue);

		AttributeValue rangeValue1 = new RangeValue(DataType.INTEGER, "1", "3", attr);
		AttributeValue matchingValue1 = attr.findMatchingAttributeValueOnData(rangeValue1);
		assertTrue(matchingValue1 instanceof UniqueValue);
		assertTrue(matchingValue1.equals(uniqueValue));
		
		AttributeValue rangeValue2 = new RangeValue(DataType.INTEGER, "2", "3", attr);
		AttributeValue matchingValue2 = attr.findMatchingAttributeValueOnData(rangeValue2);
		assertTrue(matchingValue2 == null);
		
		AttributeValue uniqueValue1 = new UniqueValue(DataType.INTEGER, "1", attr);
		AttributeValue matchingValue3 = attr.findMatchingAttributeValueOnData(uniqueValue1);
		assertTrue(matchingValue3.equals(uniqueValue));
		
		AttributeValue uniqueValue2 = new UniqueValue(DataType.INTEGER, "4", attr);
		AttributeValue matchingValue4 = attr.findMatchingAttributeValueOnData(uniqueValue2);
		assertTrue(matchingValue4 == null);
	}
}
