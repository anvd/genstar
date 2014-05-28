package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ummisco.genstar.exception.AttributeException;
import ummisco.genstar.exception.GenstarException;

@RunWith(JUnit4.class)
public class RangeValueTest {
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test public void testNullValueConstructor1() throws GenstarException {
		exception.expect(AttributeException.class);
		new RangeValue(null, "", "");
	}
	
	@Test public void testNullValueConstructor2() throws GenstarException {
		exception.expect(AttributeException.class);
		new RangeValue(ValueType.INTEGER, null, "");
	}
	
	@Test public void testNullValueConstructor3() throws GenstarException {
		exception.expect(AttributeException.class);
		new RangeValue(null, null, null);
	}

	@Test
	public void testNotNullValueConstructor() throws GenstarException {
		new RangeValue(ValueType.INTEGER, "1", "1");
	}
	
	@Test
	public void testInvalidMinMaxRange() throws GenstarException {
		exception.expect(AttributeException.class);
		new RangeValue(ValueType.INTEGER, "2", "1");
	}
	
	@Test
	public void testEquals() throws GenstarException {
		RangeValue rangeValue1 = new RangeValue(ValueType.INTEGER, "1", "2");
		RangeValue rangeValue2 = new RangeValue(ValueType.DOUBLE, "1", "2");
		RangeValue rangeValue3 = new RangeValue(ValueType.FLOAT, "1", "2");
		RangeValue rangeValue4 = new RangeValue(ValueType.INTEGER, "1", "3");
		RangeValue rangeValue5 = new RangeValue(ValueType.INTEGER, "1", "2");
		
		assertFalse(rangeValue1.equals(new Object()));
		assertFalse(rangeValue1.equals(rangeValue2));
		assertFalse(rangeValue1.equals(rangeValue3));
		assertFalse(rangeValue1.equals(rangeValue4));
		assertTrue(rangeValue1.equals(rangeValue5));
		assertTrue(rangeValue1.equals(rangeValue1));
	}
	
	@Test public void testCompareTo1() throws GenstarException {
		RangeValue rangeValue1 = new RangeValue(ValueType.INTEGER, "1", "2");
		RangeValue rangeValue2 = new RangeValue(ValueType.INTEGER, "1", "2");
		RangeValue rangeValue3 = new RangeValue(ValueType.INTEGER, "1", "3");
		
		assertTrue(rangeValue1.compareTo(rangeValue2) == 0);
		assertTrue(rangeValue1.compareTo(rangeValue3) < 0);
		assertTrue(rangeValue3.compareTo(rangeValue1) > 0);
		assertTrue(rangeValue3.compareTo(rangeValue3) == 0);
	}
	
	@Test public void testCast() throws GenstarException {
		fail("not yet implemented");
	}
}
