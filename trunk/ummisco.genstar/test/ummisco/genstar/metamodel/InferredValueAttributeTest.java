package ummisco.genstar.metamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ummisco.genstar.exception.AttributeException;
import ummisco.genstar.exception.GenstarException;

public class InferredValueAttributeTest {

	@Rule public ExpectedException exception = ExpectedException.none();
	
	
	@Test public void testNullParamConstructor1() throws GenstarException { // null "population"
		
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredValueAttribute(null, inferringAttribute, "inferred attribute", ValueType.INTEGER);
	}
	
	@Test public void testNullParamConstructor2() throws GenstarException { // null "inferringAttribute"
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		
		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredValueAttribute(dummyPopulation, null, "inferred attribute", ValueType.INTEGER);
	}
	
	@Test public void testNullParamConstructor3() throws GenstarException { // null "dataAttributeName"
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredValueAttribute(dummyPopulation, inferringAttribute, null, ValueType.INTEGER);
	}
	
	@Test public void testNullParamConstructor4() throws GenstarException { // null "entityAttributeName"
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredValueAttribute(dummyPopulation, inferringAttribute, "dummy var", null, ValueType.INTEGER);
	}
	
	@Test public void testNullParamConstructor5() throws GenstarException { // null "valueType"
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredValueAttribute(dummyPopulation, inferringAttribute, "dummy var", "dummy var", null);
	}
	
	@Test public void testInvalidParamConstructor1() throws GenstarException { // incoherence of populations between inferred attribute and inferring attribute
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		
		SyntheticPopulationGenerator anotherDummyPopulation = new SyntheticPopulationGenerator("another dummy population", 1);

		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredValueAttribute(anotherDummyPopulation, inferringAttribute, "dummy var", "dummy var", ValueType.INTEGER);
	}
	
	@Test public void testInvalidParamConstructor2() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredValueAttribute(dummyPopulation, inferringAttribute, "dummy var", "dummy var", ValueType.INTEGER);
	}
	
	@Test public void testValidParamConstructor() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferringAttribute);
		
		InferredAttribute iAttr = new InferredValueAttribute(dummyPopulation, inferringAttribute, "dummy var", "dummy var", ValueType.INTEGER);
	}
	
	@Test public void testCreateValueInvalidParam1() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferringAttribute);
		
		InferredAttribute iAttr = new InferredValueAttribute(dummyPopulation, inferringAttribute, "dummy var", "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		iAttr.valueFromString(null);
	}
	
	@Test public void testCreateValueInvalidParam2() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferringAttribute);
		
		InferredAttribute iAttr = new InferredValueAttribute(dummyPopulation, inferringAttribute, "dummy var", "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		iAttr.valueFromString(new ArrayList());
	}
	
	@Test public void testCreateValueValidParam1() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferringAttribute);
		
		InferredAttribute iAttr = new InferredValueAttribute(dummyPopulation, inferringAttribute, "dummy var", "dummy var", ValueType.INTEGER);
		
		List<String> stringValue = new ArrayList<String>();
		stringValue.add("1");
		AttributeValue v1 = iAttr.valueFromString(stringValue);
		
		assertTrue(v1 instanceof UniqueValue);
		
		
		stringValue.add("2");
		AttributeValue v2 = iAttr.valueFromString(stringValue);
		
		assertFalse(v2 instanceof RangeValue);
	}
	
	@Test public void testInitializeInferenceData() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationOfValuesAttribute inferringValueAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		inferringValueAttribute.add(new UniqueValue(ValueType.INTEGER, "1"));
		inferringValueAttribute.add(new UniqueValue(ValueType.INTEGER, "2"));
		dummyPopulation.addAttribute(inferringValueAttribute);
		
		InferredAttribute iAttr = new InferredValueAttribute(dummyPopulation, inferringValueAttribute, "dummy var", "dummy var", ValueType.INTEGER);
		
		Map<AttributeValue, AttributeValue> inferenceData = iAttr.getInferenceData(); 
		assertTrue(inferenceData.size() == 2);
		UniqueValue zeroIntValue = new UniqueValue(ValueType.INTEGER, "0");
		for (AttributeValue av : inferenceData.values()) {  assertTrue(zeroIntValue.equals(av)); }
	}
	
	
	@Test public void testGetInferenceData() {
		fail("not yet implemented");
	}
	
}
