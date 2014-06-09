package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ummisco.genstar.exception.GenstarException;

public class UniqueValueTest {

	@Rule public ExpectedException exception = ExpectedException.none();

	@Test public void testInvalidParamConstructor1() throws GenstarException {
		exception.expect(GenstarException.class);
		new UniqueValue(DataType.INTEGER, null);
	}
	
	@Test public void testInvalidParamConstructor3() throws GenstarException {
		exception.expect(GenstarException.class);
		new UniqueValue(DataType.INTEGER, "a");
	}
	
	@Test public void testValidParamConstructor() throws GenstarException {
		new UniqueValue(DataType.INTEGER, "1");
	}
	
	@Test public void testSetInvalidStringValue() throws GenstarException {
		UniqueValue v = new UniqueValue(DataType.INTEGER, "0");
		exception.expect(GenstarException.class);
		v.setStringValue("A");
	}
	
	@Test public void testSetValidStringValue() throws GenstarException {
		UniqueValue v = new UniqueValue(DataType.INTEGER, "0");
		v.setStringValue("1");
		assertTrue(v.getStringValue().equals("1"));
	}
	
	@Test public void testCompareTo() throws GenstarException {
		UniqueValue v0 = new UniqueValue(DataType.INTEGER, "0");
		UniqueValue v1 = new UniqueValue(DataType.INTEGER, "0");
		UniqueValue v2 = new UniqueValue(DataType.INTEGER, "1");
		
		assertTrue(v0.compareTo(v0) == 0);
		assertTrue(v0.compareTo(v1) == 0);
		assertTrue(v0.compareTo(v2) == -1);
		assertTrue(v2.compareTo(v0) == 1);
	}
	
	@Test public void testEquals() throws GenstarException {
		UniqueValue v0 = new UniqueValue(DataType.INTEGER, "0");
		UniqueValue v1 = new UniqueValue(DataType.INTEGER, "0");
		UniqueValue v2 = new UniqueValue(DataType.INTEGER, "1");
		
		assertTrue(v0.equals(v0));
		assertTrue(v0.equals(v1));
		assertFalse(v0.equals(v2));
		
		Map<UniqueValue, Integer> mapData = new HashMap<UniqueValue, Integer>();
		mapData.put(v0, 1);
		assertTrue(mapData.get(v1) == 1);
	}
	
	@Test public void testCast() throws GenstarException {
		UniqueValue v0 = new UniqueValue(DataType.INTEGER, "0");
		
		AttributeValue result1 = v0.cast(RangeValue.class);
		assertTrue(result1 instanceof RangeValue);
		
		AttributeValue result2 = v0.cast(UniqueValue.class);
		assertTrue(result2 == v0);
	}
	
	@Test public void testIsMatchValue() throws GenstarException {
		UniqueValue v1 = new UniqueValue(DataType.INTEGER, "1");
		UniqueValue v2 = new UniqueValue(DataType.INTEGER, "1");
		UniqueValue v3 = new UniqueValue(DataType.INTEGER, "2");
		
		
		UniqueValue v4 = new UniqueValue(DataType.INTEGER, "1");
		UniqueValue v5 = new UniqueValue(DataType.INTEGER, "1");
		UniqueValue v6 = new UniqueValue(DataType.INTEGER, "2");
		
		
		RangeValue v7 = new RangeValue(DataType.INTEGER, "1", "2");
		RangeValue v8 = new RangeValue(DataType.INTEGER, "1", "2");
		RangeValue v9 = new RangeValue(DataType.INTEGER, "2", "3");
		
		
		RangeValue v10 = new RangeValue(DataType.FLOAT, "1", "2");
		RangeValue v11 = new RangeValue(DataType.FLOAT, "1", "2");
		RangeValue v12 = new RangeValue(DataType.FLOAT, "2", "3");
		
		
		UniqueValue v13 = new UniqueValue(DataType.STRING, "1");
		UniqueValue v14 = new UniqueValue(DataType.STRING, "1");
		UniqueValue v15 = new UniqueValue(DataType.STRING, "2");

		
		// assertions
		assertTrue(v1.isValueMatch(v2));
		assertFalse(v1.isValueMatch(v3));
		
		assertTrue(v1.isValueMatch(v4));
		assertTrue(v4.isValueMatch(v5));
		assertFalse(v4.isValueMatch(v6));
		
		assertTrue(v1.isValueMatch(v7));
		assertFalse(v1.isValueMatch(v9));
		assertTrue(v7.isValueMatch(v8));
		assertFalse(v8.isValueMatch(v9));
		
		assertTrue(v4.isValueMatch(v7));
		assertFalse(v4.isValueMatch(v9));
		
		assertTrue(v7.isValueMatch(v10));
		assertFalse(v7.isValueMatch(v12));
		
		assertTrue(v10.isValueMatch(v11));
		assertFalse(v10.isValueMatch(v12));
	
		assertFalse(v1.isValueMatch(v13));
		assertTrue(v13.isValueMatch(v14));
		assertFalse(v13.isValueMatch(v15));
	}
}
