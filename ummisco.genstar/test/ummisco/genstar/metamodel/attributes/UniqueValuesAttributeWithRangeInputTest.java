package ummisco.genstar.metamodel.attributes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;

@RunWith(JMockit.class)
public class UniqueValuesAttributeWithRangeInputTest {

	@Test public void testInitializeUniqueValuesAttributeWithRangeInputSuccessfully(@Mocked final ISyntheticPopulationGenerator generator) throws GenstarException {
		UniqueValue minValue = new UniqueValue(DataType.INTEGER, "1");
		UniqueValue maxValue = new UniqueValue(DataType.INTEGER, "10");
		UniqueValuesAttributeWithRangeInput attribute = new UniqueValuesAttributeWithRangeInput(generator, "dummy attribute", "dummy attribute", minValue, maxValue);
		
		List<AttributeValue> internalValuesOnData = Deencapsulation.getField(attribute, "internalValuesOnData");
		assertTrue(internalValuesOnData.size() == 10);
		
		UniqueValue _minValue = Deencapsulation.getField(attribute, "minValue");
		assertTrue(_minValue.compareTo(minValue) == 0);
		
		UniqueValue _maxValue = Deencapsulation.getField(attribute, "maxValue");
		assertTrue(_maxValue.compareTo(maxValue) == 0);
	}
	
	@Test public void testContainInstanceOfAttributeValue(@Mocked final ISyntheticPopulationGenerator generator) throws GenstarException {
		UniqueValue minValue = new UniqueValue(DataType.INTEGER, "1");
		UniqueValue maxValue = new UniqueValue(DataType.INTEGER, "10");
		UniqueValuesAttributeWithRangeInput attribute = new UniqueValuesAttributeWithRangeInput(generator, "dummy attribute", "dummy attribute", minValue, maxValue);

		UniqueValue value1 = new UniqueValue(DataType.INTEGER, "0");
		assertFalse(attribute.containInstanceOfAttributeValue(value1));
		
		UniqueValue value2 = new UniqueValue(DataType.INTEGER, "1");
		assertFalse(attribute.containInstanceOfAttributeValue(value2));
		
		for (AttributeValue value : attribute.valuesOnData()) { assertTrue(attribute.containInstanceOfAttributeValue(value)); }
		
		
		/*
		AttributeValue _value2 = attribute.containsInstanceOfAttributeValue(value2);
		assertTrue(value2.compareTo(_value2) == 0);
		assertTrue(!value2.equals(_value2));
		
		AttributeValue __value2 = attribute.containsInstanceOfAttributeValue(value2);
		assertTrue(_value2.equals(__value2));
		*/
	}
	
	@Test public void testFindCorrespondingAttributeValueOnData(@Mocked final ISyntheticPopulationGenerator generator) throws GenstarException {
		
		UniqueValue minValue = new UniqueValue(DataType.INTEGER, "1");
		UniqueValue maxValue = new UniqueValue(DataType.INTEGER, "10");
		UniqueValuesAttributeWithRangeInput attribute = new UniqueValuesAttributeWithRangeInput(generator, "dummy attribute", "dummy attribute", minValue, maxValue);
		
		List<String> stringValue0 = new ArrayList<String>();
		stringValue0.add("0");
		
		AttributeValue value0 = attribute.getMatchingAttributeValueOnData(stringValue0);
		assertTrue(value0 == null);
		
		List<String> stringValue1 = new ArrayList<String>();
		stringValue1.add("1");
		
		AttributeValue value1 = attribute.getMatchingAttributeValueOnData(stringValue1);
		assertTrue(value1 != null);
		
		AttributeValue _value1 = attribute.getMatchingAttributeValueOnData(stringValue1);
		assertTrue(value1.equals(_value1));
	}
	
	@Test public void testGetMatchingAttributeValueOnData(@Mocked final ISyntheticPopulationGenerator generator) throws GenstarException {
		
		UniqueValue minValue = new UniqueValue(DataType.INTEGER, "1");
		UniqueValue maxValue = new UniqueValue(DataType.INTEGER, "10");
		UniqueValuesAttributeWithRangeInput attribute = new UniqueValuesAttributeWithRangeInput(generator, "dummy attribute", "dummy attribute", minValue, maxValue);

		UniqueValue value1 = new UniqueValue(DataType.INTEGER, "0");
		assertTrue(attribute.getMatchingAttributeValueOnData(value1) == null);
		
		UniqueValue value2 = new UniqueValue(DataType.INTEGER, "1");
		AttributeValue _value2 = attribute.getMatchingAttributeValueOnData(value2);
		assertTrue(value2.compareTo(_value2) == 0);
		assertTrue(!value2.equals(_value2));
		
		AttributeValue __value2 = attribute.getMatchingAttributeValueOnData(value2);
		assertTrue(_value2.equals(__value2));
		
		UniqueValue value3 = new UniqueValue(DataType.INTEGER, "10");
		AttributeValue _value3 = attribute.getMatchingAttributeValueOnData(value3);
		assertTrue(value3.compareTo(_value3) == 0);
		assertTrue(!value3.equals(_value3));

		AttributeValue __value3 = attribute.getMatchingAttributeValueOnData(value3);
		assertTrue(_value3.equals(__value3));
	}
	
	@Test public void testValuesOnData(@Mocked final ISyntheticPopulationGenerator generator) throws GenstarException {
		UniqueValue minValue = new UniqueValue(DataType.INTEGER, "1");
		UniqueValue maxValue = new UniqueValue(DataType.INTEGER, "100");
		UniqueValuesAttributeWithRangeInput attribute = new UniqueValuesAttributeWithRangeInput(generator, "dummy attribute", "dummy attribute", minValue, maxValue);
		
		List<AttributeValue> values = new ArrayList<AttributeValue>(attribute.valuesOnData());
		assertTrue(values.size() == 100);
		
		Collections.sort(values);

		AttributeValue value1 = values.get(0);
		AttributeValue value2 = values.get(99);
		
		assertTrue(value1.compareTo(value2) != 0);
		assertTrue(value1.compareTo(minValue) == 0 && value2.compareTo(maxValue) == 0);
	}
	
	/*
	@Test public void testContainsValueOfAttributeValue(@Mocked final ISyntheticPopulationGenerator generator) throws GenstarException {
		
		UniqueValue minValue = new UniqueValue(DataType.INTEGER, "1");
		UniqueValue maxValue = new UniqueValue(DataType.INTEGER, "10000");
		UniqueValuesAttributeWithRangeInput attribute = new UniqueValuesAttributeWithRangeInput(generator, "dummy attribute", "dummy attribute", minValue, maxValue);
		
		UniqueValue value1 = new UniqueValue(DataType.INTEGER, "1");
		assertTrue(attribute.containsValueOfAttributeValue(value1));
		
		UniqueValue value2 = new UniqueValue(DataType.INTEGER, "0");
		assertFalse(attribute.containsValueOfAttributeValue(value2));
	}
	*/
}
