package ummisco.genstar.metamodel;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ummisco.genstar.exception.AttributeException;
import ummisco.genstar.exception.GenstarException;

public class InferredRangeAttributeTest {

	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test public void testNullParamConstructor1() throws GenstarException { // null "population"
		
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredRangeAttribute(null, inferringAttribute, "inferred attribute", ValueType.INTEGER);
	}
	
	@Test public void testNullParamConstructor2() throws GenstarException { // null "inferringAttribute"
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		
		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredRangeAttribute(dummyPopulation, null, "inferred attribute", ValueType.INTEGER);
	}

	@Test public void testNullParamConstructor3() throws GenstarException { // null "dataAttributeName"
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredRangeAttribute(dummyPopulation, inferringAttribute, null, ValueType.INTEGER);
	}

	@Test public void testNullParamConstructor4() throws GenstarException { // null "entityAttributeName"
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredRangeAttribute(dummyPopulation, inferringAttribute, "dummy var", null, ValueType.INTEGER);
	}
	
	@Test public void testNullParamConstructor5() throws GenstarException { // null "valueType"
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredRangeAttribute(dummyPopulation, inferringAttribute, "dummy var", "dummy var", null);
	}
	
	@Test public void testInvalidParamConstructor1() throws GenstarException { // incoherence of populations between inferred attribute and inferring attribute
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		
		SyntheticPopulationGenerator anotherDummyPopulation = new SyntheticPopulationGenerator("another dummy population", 1);

		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredRangeAttribute(anotherDummyPopulation, inferringAttribute, "dummy var", "dummy var", ValueType.INTEGER);
	}
	
	@Test public void testInvalidParamConstructor2() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		InferredAttribute iAttr = new InferredRangeAttribute(dummyPopulation, inferringAttribute, "dummy var", "dummy var", ValueType.INTEGER);
	}
	
	@Test public void testValidParamConstructor() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferringAttribute);
		
		InferredAttribute iAttr = new InferredRangeAttribute(dummyPopulation, inferringAttribute, "dummy var", "dummy var", ValueType.INTEGER);
	}
	
	@Test public void testCreateValueInvalidParam1() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferringAttribute);
		
		InferredAttribute iAttr = new InferredRangeAttribute(dummyPopulation, inferringAttribute, "dummy var", "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		iAttr.valueFromString(null);
	}
	
	@Test public void testCreateValueInvalidParam2() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferringAttribute);
		
		InferredAttribute iAttr = new InferredRangeAttribute(dummyPopulation, inferringAttribute, "dummy var", "dummy var", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		iAttr.valueFromString(new ArrayList());
	}
	
	@Test public void testCreateValueValidParam1() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferringAttribute);
		
		InferredAttribute iAttr = new InferredRangeAttribute(dummyPopulation, inferringAttribute, "dummy var", "dummy var", ValueType.INTEGER);
		
		List<String> stringValue = new ArrayList<String>();
		stringValue.add("1");
		stringValue.add("2");
		AttributeValue v1 = iAttr.valueFromString(stringValue);
		
		assertTrue(v1 instanceof RangeValue);
		assertTrue(v1.equals(new RangeValue(ValueType.INTEGER, "1", "2")));
	}

	@Test public void testInitializeInferenceData() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationOfValuesAttribute inferringValueAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "dummy var", ValueType.INTEGER);
		inferringValueAttribute.add(new UniqueValue(ValueType.INTEGER, "1"));
		inferringValueAttribute.add(new UniqueValue(ValueType.INTEGER, "2"));
		dummyPopulation.addAttribute(inferringValueAttribute);
		
		InferredAttribute inferredAttr = new InferredRangeAttribute(dummyPopulation, inferringValueAttribute, "dummy var", "dummy var", ValueType.INTEGER);
		
		Map<AttributeValue, AttributeValue> inferenceData = inferredAttr.getInferenceData(); 
		assertTrue(inferenceData.size() == 2);
		RangeValue zeroRangeValue = new RangeValue(ValueType.INTEGER, "0", "0");
		for (AttributeValue av : inferenceData.values()) {  assertTrue(zeroRangeValue.equals(av)); }
	}

	@Test public void testSetAndGetInferredValue() throws GenstarException {
		
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		
		EnumerationOfValuesAttribute inferringValueAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "inferring var", ValueType.INTEGER);
		
		for (double[] range : BondyData.hourly_net_wages) {
			inferringValueAttribute.add(new UniqueValue(ValueType.INTEGER, Integer.toString((int) range[0])));
		}
		
		dummyPopulation.addAttribute(inferringValueAttribute);
		
		InferredRangeAttribute inferredRangeAttr = new InferredRangeAttribute(dummyPopulation, inferringValueAttribute, "inferred var", "inferred var", ValueType.DOUBLE);
		for (double[] range : BondyData.hourly_net_wages) {
			inferredRangeAttr.setInferredAttributeValue(new UniqueValue(ValueType.INTEGER, Integer.toString((int) range[0])), new RangeValue(ValueType.DOUBLE, Double.toString(range[1]), Double.toString(range[2])));
		}
		
		for (double[] range : BondyData.hourly_net_wages) {
			assertTrue(inferredRangeAttr.getInferredAttributeValue(new UniqueValue(ValueType.INTEGER, Integer.toString((int) range[0]))).equals(new RangeValue(ValueType.DOUBLE, Double.toString(range[1]), Double.toString(range[2]))));
		}
	}
	
	@Test public void testGetAndSetInferredValue1() throws GenstarException {
		BondyData bondyData = new BondyData();
		
		AttributeInferenceGenerationRule rule3 = (AttributeInferenceGenerationRule) bondyData.getRule3();
		InferredAttribute inferredAttribute = rule3.getInferredAttribute();
		EnumerationValueAttribute inferringAttribute = inferredAttribute.getInferringAttribute();
		
		for (double[] net_wage : BondyData.hourly_net_wages) {
			assertTrue(inferredAttribute.getInferredAttributeValue(new UniqueValue(ValueType.INTEGER, Integer.toString((int) net_wage[0])) ).equals(new RangeValue(ValueType.DOUBLE, Double.toString(net_wage[1]), Double.toString(net_wage[2]))));
		}
		 
	}
}
