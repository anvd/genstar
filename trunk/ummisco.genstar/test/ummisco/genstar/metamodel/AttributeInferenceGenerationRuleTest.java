package ummisco.genstar.metamodel;

import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ummisco.genstar.exception.GenstarException;

@RunWith(JUnit4.class)
public class AttributeInferenceGenerationRuleTest {
	
	@Rule public ExpectedException exception = ExpectedException.none();
	

	@Test public void testInvalidParamConstructor1() throws GenstarException { // null "population"
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "inferring attribute", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferringAttribute);
		
		InferredAttribute inferredAttr = new InferredValueAttribute(dummyPopulation, inferringAttribute, "inferred attribute", "inferred attribute", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferredAttr);

		
		exception.expect(GenstarException.class);
		AttributeInferenceGenerationRule rule1 = new AttributeInferenceGenerationRule(null, "dummy rule", inferredAttr);
		
	}
	
	@Test public void testInvalidParamConstructor2() throws GenstarException { // null "name"
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "inferring attribute", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferringAttribute);
		
		InferredAttribute inferredAttr = new InferredValueAttribute(dummyPopulation, inferringAttribute, "inferred attribute", "inferred attribute", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferredAttr);

		
		exception.expect(GenstarException.class);
		AttributeInferenceGenerationRule rule1 = new AttributeInferenceGenerationRule(dummyPopulation, null, inferredAttr);
	}
	
	@Test public void testInvalidParamConstructor3() throws GenstarException { // null "inferredAttribute"
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "inferring attribute", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferringAttribute);
		
		
		exception.expect(GenstarException.class);
		AttributeInferenceGenerationRule rule1 = new AttributeInferenceGenerationRule(dummyPopulation, "dummy rule", null);
	}
	
	@Test public void testInvalidParamConstructor4() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "inferring attribute", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferringAttribute);
		
		InferredAttribute inferredAttr = new InferredValueAttribute(dummyPopulation, inferringAttribute, "inferred attribute", "inferred attribute", ValueType.INTEGER);
		
		
		exception.expect(GenstarException.class);
		AttributeInferenceGenerationRule rule1 = new AttributeInferenceGenerationRule(dummyPopulation, "dummy rule", inferredAttr);
	}
	
	@Test public void testValidParamConstructor5() throws GenstarException {
		SyntheticPopulationGenerator dummyPopulation = new SyntheticPopulationGenerator("dummy population", 1);
		EnumerationValueAttribute inferringAttribute = new EnumerationOfValuesAttribute(dummyPopulation, "inferring attribute", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferringAttribute);
		
		InferredAttribute inferredAttr = new InferredValueAttribute(dummyPopulation, inferringAttribute, "inferred attribute", "inferred attribute", ValueType.INTEGER);
		dummyPopulation.addAttribute(inferredAttr);
		
		AttributeInferenceGenerationRule rule1 = new AttributeInferenceGenerationRule(dummyPopulation, "dummy rule", inferredAttr);
	}
	
	@Test public void testGenerate() throws GenstarException {
		BondyData bondyData = new BondyData();
		AttributeInferenceGenerationRule rule3 = (AttributeInferenceGenerationRule) bondyData.getRule3();
		InferredAttribute inferredAttribute = rule3.getInferredAttribute();
		EnumerationValueAttribute pcsAttr = inferredAttribute.getInferringAttribute();
		ISyntheticPopulationGenerator generator = bondyData.getInhabitantPopGenerator();
		generator.setNbOfEntities(1);
		
		Entity entity;
		for (double[] wage : BondyData.hourly_net_wages) {
			
			entity = new Entity(generator.generate());
			entity.putAttributeValue(pcsAttr, new UniqueValue(ValueType.INTEGER, Integer.toString((int) wage[0])));
			
			assertTrue(entity.getEntityAttributeValue(inferredAttribute.nameOnEntity) == null);
			rule3.generate(entity);
			
			assertTrue(entity.getEntityAttributeValue(inferredAttribute.nameOnEntity).getAttributeValueOnData().equals(new RangeValue(ValueType.DOUBLE, Double.toString(wage[1]), Double.toString(wage[2]))));
			assertTrue(entity.getEntityAttributeValue(inferredAttribute.nameOnEntity).getAttributeValueOnEntity().isValueMatch(new RangeValue(ValueType.DOUBLE, Double.toString(wage[1]), Double.toString(wage[2]))));
		}
		
		// TODO infer "invalid" value!
	}
}
