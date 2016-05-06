package ummisco.genstar.metamodel;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.sample_free.AttributeInferenceGenerationRule;

@RunWith(JUnit4.class)
public class AttributeInferenceGenerationRuleTest {
	
	@Rule public ExpectedException exception = ExpectedException.none();
	

	@Test public void testInvalidParamConstructor1() throws GenstarException { // null "population"
		exception.expect(GenstarException.class);
		new AttributeInferenceGenerationRule(null, "dummy rule", null, null);
	}
	
	@Test public void testInvalidParamConstructor2() throws GenstarException { // null "name"
		SampleFreeGenerator dummyPopulation = new SampleFreeGenerator("dummy population", 1);

		exception.expect(GenstarException.class);
		new AttributeInferenceGenerationRule(dummyPopulation, null, null, null);
	}
	
	@Test public void testInvalidParamConstructor3() throws GenstarException {
		SampleFreeGenerator dummyPopulation = new SampleFreeGenerator("dummy population", 1);

		exception.expect(GenstarException.class);
		new AttributeInferenceGenerationRule(dummyPopulation, "attribute inference generation rule", null, null);
	}
	
	@Test public void testValidParamConstructor1() throws GenstarException {
		SampleFreeGenerator bondyInhabitantPopGenerator = new SampleFreeGenerator("Population of Bondy's Inhabitants", 51000);
		
		// create attributes +
		UniqueValuesAttribute pcsAttr = new UniqueValuesAttribute(bondyInhabitantPopGenerator, "pcs", DataType.INTEGER);
		for (int v : BondyData.pcs_values) { pcsAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(v))); }
		pcsAttr.setDefaultValue(new UniqueValue(DataType.INTEGER, "8"));
		bondyInhabitantPopGenerator.addAttribute(pcsAttr);
		
		RangeValuesAttribute hourlyNetWageAttr = new RangeValuesAttribute(bondyInhabitantPopGenerator, "hourlyNetWage", DataType.DOUBLE, RangeValue.class);
		for (double[] wage_range : BondyData.hourly_net_wages) {
			hourlyNetWageAttr.add(new RangeValue(DataType.DOUBLE, Double.toString(wage_range[1]), Double.toString(wage_range[2])));
		}
		bondyInhabitantPopGenerator.addAttribute(hourlyNetWageAttr);
		// create attributes -
		
		
		new AttributeInferenceGenerationRule(bondyInhabitantPopGenerator, "Hourly net wage by socio-profession category", pcsAttr, hourlyNetWageAttr);
	}
	
	@Test public void testSetValidInferenceData1() throws GenstarException {
		SampleFreeGenerator bondyInhabitantPopGenerator = new SampleFreeGenerator("Population of Bondy's Inhabitants", 51000);
		
		// create attributes +
		UniqueValuesAttribute pcsAttr = new UniqueValuesAttribute(bondyInhabitantPopGenerator, "pcs", DataType.INTEGER);
		for (int v : BondyData.pcs_values) { pcsAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(v))); }
		pcsAttr.setDefaultValue(new UniqueValue(DataType.INTEGER, "8"));
		bondyInhabitantPopGenerator.addAttribute(pcsAttr);
		
		RangeValuesAttribute hourlyNetWageAttr = new RangeValuesAttribute(bondyInhabitantPopGenerator, "hourlyNetWage", DataType.DOUBLE, RangeValue.class);
		for (double[] wage_range : BondyData.hourly_net_wages) {
			hourlyNetWageAttr.add(new RangeValue(DataType.DOUBLE, Double.toString(wage_range[1]), Double.toString(wage_range[2])));
		}
		bondyInhabitantPopGenerator.addAttribute(hourlyNetWageAttr);
		// create attributes -
		
		
		AttributeInferenceGenerationRule generationRule3 = new AttributeInferenceGenerationRule(bondyInhabitantPopGenerator, "Hourly net wage by socio-profession category",
				pcsAttr, hourlyNetWageAttr);
		
		
		Map<AttributeValue, AttributeValue> pcsInferenceData = new HashMap<AttributeValue, AttributeValue>();
		for (double[] net_wage : BondyData.hourly_net_wages) {
			pcsInferenceData.put(new UniqueValue(DataType.INTEGER, Integer.toString((int) net_wage[0])), 
					new RangeValue(DataType.DOUBLE, Double.toString(net_wage[1]), Double.toString(net_wage[2])));
		}
		generationRule3.setInferenceData(pcsInferenceData);
		
	}
	
	@Test public void testSetInvalidInferenceData1() throws GenstarException { // null inference data
		SampleFreeGenerator bondyInhabitantPopGenerator = new SampleFreeGenerator("Population of Bondy's Inhabitants", 51000);
		
		// create attributes +
		UniqueValuesAttribute pcsAttr = new UniqueValuesAttribute(bondyInhabitantPopGenerator, "pcs", DataType.INTEGER);
		for (int v : BondyData.pcs_values) { pcsAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(v))); }
		pcsAttr.setDefaultValue(new UniqueValue(DataType.INTEGER, "8"));
		bondyInhabitantPopGenerator.addAttribute(pcsAttr);
		
		RangeValuesAttribute hourlyNetWageAttr = new RangeValuesAttribute(bondyInhabitantPopGenerator, "hourlyNetWage", DataType.DOUBLE, RangeValue.class);
		for (double[] wage_range : BondyData.hourly_net_wages) {
			hourlyNetWageAttr.add(new RangeValue(DataType.DOUBLE, Double.toString(wage_range[1]), Double.toString(wage_range[2])));
		}
		bondyInhabitantPopGenerator.addAttribute(hourlyNetWageAttr);
		// create attributes -
		
		
		AttributeInferenceGenerationRule generationRule3 = new AttributeInferenceGenerationRule(bondyInhabitantPopGenerator, "Hourly net wage by socio-profession category",
				pcsAttr, hourlyNetWageAttr);

		exception.expect(GenstarException.class);
		generationRule3.setInferenceData(null);
	}
	
	@Test public void testInvalidSetInferenceData2() throws GenstarException { // length(inferring attribute values) != length(rule.inferringAttribute.values)
		SampleFreeGenerator bondyInhabitantPopGenerator = new SampleFreeGenerator("Population of Bondy's Inhabitants", 51000);
		
		// create attributes +
		UniqueValuesAttribute pcsAttr = new UniqueValuesAttribute(bondyInhabitantPopGenerator, "pcs", DataType.INTEGER);
		for (int v : BondyData.pcs_values) { pcsAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(v))); }
		pcsAttr.setDefaultValue(new UniqueValue(DataType.INTEGER, "8"));
		bondyInhabitantPopGenerator.addAttribute(pcsAttr);
		
		RangeValuesAttribute hourlyNetWageAttr = new RangeValuesAttribute(bondyInhabitantPopGenerator, "hourlyNetWage", DataType.DOUBLE, RangeValue.class);
		for (double[] wage_range : BondyData.hourly_net_wages) {
			hourlyNetWageAttr.add(new RangeValue(DataType.DOUBLE, Double.toString(wage_range[1]), Double.toString(wage_range[2])));
		}
		bondyInhabitantPopGenerator.addAttribute(hourlyNetWageAttr);
		// create attributes -
		
		
		AttributeInferenceGenerationRule generationRule3 = new AttributeInferenceGenerationRule(bondyInhabitantPopGenerator, "Hourly net wage by socio-profession category",
				pcsAttr, hourlyNetWageAttr);

		exception.expect(GenstarException.class);
		generationRule3.setInferenceData(new HashMap<AttributeValue, AttributeValue>());
	}

	@Test public void testInvalidSetInferenceData3() throws GenstarException { // some inferring attributes values don't belong to (rule.inferringAttribute.values)
		SampleFreeGenerator bondyInhabitantPopGenerator = new SampleFreeGenerator("Population of Bondy's Inhabitants", 51000);
		
		// create attributes +
		UniqueValuesAttribute pcsAttr = new UniqueValuesAttribute(bondyInhabitantPopGenerator, "pcs", DataType.INTEGER);
		for (int v : BondyData.pcs_values) { pcsAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(v))); }
		pcsAttr.setDefaultValue(new UniqueValue(DataType.INTEGER, "8"));
		bondyInhabitantPopGenerator.addAttribute(pcsAttr);
		
		RangeValuesAttribute hourlyNetWageAttr = new RangeValuesAttribute(bondyInhabitantPopGenerator, "hourlyNetWage", DataType.DOUBLE, RangeValue.class);
		for (double[] wage_range : BondyData.hourly_net_wages) {
			hourlyNetWageAttr.add(new RangeValue(DataType.DOUBLE, Double.toString(wage_range[1]), Double.toString(wage_range[2])));
		}
		bondyInhabitantPopGenerator.addAttribute(hourlyNetWageAttr);
		// create attributes -
		
		
		AttributeInferenceGenerationRule generationRule3 = new AttributeInferenceGenerationRule(bondyInhabitantPopGenerator, "Hourly net wage by socio-profession category",
				pcsAttr, hourlyNetWageAttr);

		Map<AttributeValue, AttributeValue> pcsInferenceData = new HashMap<AttributeValue, AttributeValue>();
		for (double[] net_wage : BondyData.hourly_net_wages) {
			pcsInferenceData.put(new UniqueValue(DataType.INTEGER, Integer.toString((int) net_wage[0]) + 1), 
					new RangeValue(DataType.DOUBLE, Double.toString(net_wage[1]), Double.toString(net_wage[2])));
		}
		
		exception.expect(GenstarException.class);
		generationRule3.setInferenceData(pcsInferenceData);
		
	}

	@Test public void testInvalidSetInferenceData4() throws GenstarException { // some inferred attributes values don't belong to (rule.inferredAttribute.values)
		SampleFreeGenerator bondyInhabitantPopGenerator = new SampleFreeGenerator("Population of Bondy's Inhabitants", 51000);
		
		// create attributes +
		UniqueValuesAttribute pcsAttr = new UniqueValuesAttribute(bondyInhabitantPopGenerator, "pcs", DataType.INTEGER);
		for (int v : BondyData.pcs_values) { pcsAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(v))); }
		pcsAttr.setDefaultValue(new UniqueValue(DataType.INTEGER, "8"));
		bondyInhabitantPopGenerator.addAttribute(pcsAttr);
		
		RangeValuesAttribute hourlyNetWageAttr = new RangeValuesAttribute(bondyInhabitantPopGenerator, "hourlyNetWage", DataType.DOUBLE, RangeValue.class);
		for (double[] wage_range : BondyData.hourly_net_wages) {
			hourlyNetWageAttr.add(new RangeValue(DataType.DOUBLE, Double.toString(wage_range[1]), Double.toString(wage_range[2])));
		}
		bondyInhabitantPopGenerator.addAttribute(hourlyNetWageAttr);
		// create attributes -
		
		
		AttributeInferenceGenerationRule generationRule3 = new AttributeInferenceGenerationRule(bondyInhabitantPopGenerator, "Hourly net wage by socio-profession category",
				pcsAttr, hourlyNetWageAttr);

		Map<AttributeValue, AttributeValue> pcsInferenceData = new HashMap<AttributeValue, AttributeValue>();
		for (double[] net_wage : BondyData.hourly_net_wages) {
			pcsInferenceData.put(new UniqueValue(DataType.INTEGER, Integer.toString((int) net_wage[0])), 
					new RangeValue(DataType.DOUBLE, Double.toString(net_wage[1]), Double.toString(net_wage[2] + 1)));
		}
		
		exception.expect(GenstarException.class);
		generationRule3.setInferenceData(pcsInferenceData);
		
	}

	@Test public void testGetInferenceData() throws GenstarException {
		SampleFreeGenerator bondyInhabitantPopGenerator = new SampleFreeGenerator("Population of Bondy's Inhabitants", 51000);
		
		// create attributes +
		UniqueValuesAttribute pcsAttr = new UniqueValuesAttribute(bondyInhabitantPopGenerator, "pcs", DataType.INTEGER);
		for (int v : BondyData.pcs_values) { pcsAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(v))); }
		pcsAttr.setDefaultValue(new UniqueValue(DataType.INTEGER, "8"));
		bondyInhabitantPopGenerator.addAttribute(pcsAttr);
		
		RangeValuesAttribute hourlyNetWageAttr = new RangeValuesAttribute(bondyInhabitantPopGenerator, "hourlyNetWage", DataType.DOUBLE, RangeValue.class);
		for (double[] wage_range : BondyData.hourly_net_wages) {
			hourlyNetWageAttr.add(new RangeValue(DataType.DOUBLE, Double.toString(wage_range[1]), Double.toString(wage_range[2])));
		}
		bondyInhabitantPopGenerator.addAttribute(hourlyNetWageAttr);
		// create attributes -
		
		
		AttributeInferenceGenerationRule generationRule3 = new AttributeInferenceGenerationRule(bondyInhabitantPopGenerator, "Hourly net wage by socio-profession category",
				pcsAttr, hourlyNetWageAttr);
		
		Map<AttributeValue, AttributeValue> inferenceData = generationRule3.getInferenceData();
		assertTrue(inferenceData.size() == pcsAttr.valuesOnData().size());
		
		AttributeValue firstKey = (new ArrayList<AttributeValue>(inferenceData.keySet())).get(0);
		inferenceData.remove(firstKey);
		assertTrue(inferenceData.size() == generationRule3.getInferenceData().size() - 1);
	}
	
	@Test public void testSetInferenceData() throws GenstarException {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Test public void testGenerate() throws GenstarException {
		BondyData bondyData = new BondyData();
		AttributeInferenceGenerationRule rule3 = (AttributeInferenceGenerationRule) bondyData.getRule3();
		AbstractAttribute inferredAttribute = rule3.getInferredAttribute();
		AbstractAttribute inferringAttribute = rule3.getInferringAttribute();
		ISyntheticPopulationGenerator generator = bondyData.getInhabitantPopGenerator();
		generator.setNbOfEntities(1);
		
		Entity entity;
		for (double[] wage : BondyData.hourly_net_wages) {
			
			entity = new Entity(generator.generate());
			entity.setAttributeValueOnData(inferringAttribute, new UniqueValue(DataType.INTEGER, Integer.toString((int) wage[0])));
			
			assertTrue(entity.getEntityAttributeValueByNameOnData(inferredAttribute.getNameOnEntity()) == null);
			rule3.generate(entity);
			
			assertTrue(entity.getEntityAttributeValueByNameOnData(inferredAttribute.getNameOnEntity()).getAttributeValueOnData().equals(new RangeValue(DataType.DOUBLE, Double.toString(wage[1]), Double.toString(wage[2]))));
			assertTrue(entity.getEntityAttributeValueByNameOnData(inferredAttribute.getNameOnEntity()).getAttributeValueOnEntity().isValueMatched(new RangeValue(DataType.DOUBLE, Double.toString(wage[1]), Double.toString(wage[2]))));
		}
	}
}
