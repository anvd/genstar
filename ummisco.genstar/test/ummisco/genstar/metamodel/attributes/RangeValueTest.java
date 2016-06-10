package ummisco.genstar.metamodel.attributes;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;

@RunWith(JUnit4.class)
public class RangeValueTest {
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test public void testNullValueConstructor1() throws GenstarException {
		exception.expect(GenstarException.class);
		new RangeValue(null, "", "", null);
	}
	
	@Test public void testNullValueConstructor2() throws GenstarException {
		exception.expect(GenstarException.class);
		new RangeValue(DataType.INTEGER, null, "", null);
	}
	
	@Test public void testNullValueConstructor3() throws GenstarException {
		exception.expect(GenstarException.class);
		new RangeValue(null, null, null, null);
	}

	@Test
	public void testNotNullValueConstructor() throws GenstarException {
		new RangeValue(DataType.INTEGER, "1", "1", null);
	}
	
	@Test
	public void testInvalidMinMaxRange() throws GenstarException {
		exception.expect(GenstarException.class);
		new RangeValue(DataType.INTEGER, "2", "1", null);
	}
	
	@Test
	public void testEquals() throws GenstarException {
		RangeValue rangeValue1 = new RangeValue(DataType.INTEGER, "1", "2", null);
		RangeValue rangeValue2 = new RangeValue(DataType.DOUBLE, "1", "2", null);
		RangeValue rangeValue3 = new RangeValue(DataType.FLOAT, "1", "2", null);
		RangeValue rangeValue4 = new RangeValue(DataType.INTEGER, "1", "3", null);
		RangeValue rangeValue5 = new RangeValue(DataType.INTEGER, "1", "2", null);
		
		assertFalse(rangeValue1.equals(new Object()));
		assertFalse(rangeValue1.equals(rangeValue2));
		assertFalse(rangeValue1.equals(rangeValue3));
		assertFalse(rangeValue1.equals(rangeValue4));
		assertFalse(rangeValue1.equals(rangeValue5));
		assertTrue(rangeValue1.equals(rangeValue1));
	}
	
	@Test public void testCompareTo1() throws GenstarException {
		RangeValue rangeValue1 = new RangeValue(DataType.INTEGER, "1", "2", null);
		RangeValue rangeValue2 = new RangeValue(DataType.INTEGER, "1", "2", null);
		RangeValue rangeValue3 = new RangeValue(DataType.INTEGER, "1", "3", null);
		
		assertTrue(rangeValue1.compareTo(rangeValue2) == 0);
		assertTrue(rangeValue1.compareTo(rangeValue3) < 0);
		assertTrue(rangeValue3.compareTo(rangeValue1) > 0);
		assertTrue(rangeValue3.compareTo(rangeValue3) == 0);
	}
	
	@Test public void testCast() throws GenstarException {
		fail("not yet implemented");
	}
}
