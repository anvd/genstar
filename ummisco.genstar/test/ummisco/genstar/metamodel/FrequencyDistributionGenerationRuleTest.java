package ummisco.genstar.metamodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
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
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.sample_free.FrequencyDistributionGenerationRule;

@RunWith(JUnit4.class)
public class FrequencyDistributionGenerationRuleTest {
	@Rule public ExpectedException exception = ExpectedException.none();

	@Test
	public void testInvalidParametersConstructor() throws GenstarException {
		exception.expect(GenstarException.class);

		new FrequencyDistributionGenerationRule(null, null);
	}
	
	@Test
	public void testValidParametersConstructor() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		
		assertEquals(d.getName(), "Distribution of household size, sex and age of head");
		assertEquals(d.getGenerator(), p);
		assertTrue(d.getAttributeValuesFrequencies().isEmpty());
		assertTrue(d.getOrderedInputAttributes().isEmpty());
		assertTrue(d.getOrderedOutputAttributes().isEmpty());
	}

	@Test public void testAppendInvalidInputAttributes1() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);
		
		RangeValuesAttribute attr1 = new RangeValuesAttribute("data var name", "entity var name", DataType.INTEGER);
		
		exception.expect(GenstarException.class);
		d.appendInputAttribute(attr1);
		
		assertTrue(d.getOrderedInputAttributes().size() == 0);
	}
	
	@Test public void testAppendValidInputAttributes() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);
		assertTrue(d.getOrderedInputAttributes().size() == 0);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		d.appendInputAttribute(attr1);
		
		assertTrue(d.getOrderedInputAttributes().size() == 1);
		
		RangeValuesAttribute attr2 = new RangeValuesAttribute("attribute 2", DataType.INTEGER);
		p.addAttribute(attr2);
		d.appendInputAttribute(attr2);

		assertTrue(d.getOrderedInputAttributes().size() == 2);
	}
	
	@Test public void testAppendInvalidInputAttribute2() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);
		
		assertTrue(d.getOrderedInputAttributes().size() == 0);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		d.appendInputAttribute(attr1);
		
		assertTrue(d.getOrderedInputAttributes().size() == 1);
		
		exception.expect(GenstarException.class);
		d.appendInputAttribute(attr1);
	}
	
	@Test public void testInsertInvalidInputAttribute1() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		exception.expect(GenstarException.class);
		d.insertInputAttribute(attr1, 0);
	}
	
	@Test public void testInsertInvalidInputAttribute2() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		exception.expect(GenstarException.class);
		d.insertInputAttribute(attr1, 1);
	}
	
	@Test public void testInsertValidInputAttributes() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		d.insertInputAttribute(attr1, 0);
		assertTrue(d.getOrderedInputAttributes().size() == 1);
		assertTrue(d.getInputAttributeAtOrder(0).equals(attr1));

		RangeValuesAttribute attr2 = new RangeValuesAttribute("attribute 2", DataType.INTEGER);
		p.addAttribute(attr2);
		d.insertInputAttribute(attr2, 0);
		assertTrue(d.getOrderedInputAttributes().size() == 2);
		assertTrue(d.getInputAttributeAtOrder(0).equals(attr2));
	}
	
	@Test public void testGetInputAttributes() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		d.insertInputAttribute(attr1, 0);

		RangeValuesAttribute attr2 = new RangeValuesAttribute("attribute 2", DataType.INTEGER);
		p.addAttribute(attr2);
		d.insertInputAttribute(attr2, 0);

		RangeValuesAttribute attr3 = new RangeValuesAttribute("attribute 3", DataType.INTEGER);
		p.addAttribute(attr3);
		d.insertInputAttribute(attr3, 0);
		
		
		List<AbstractAttribute> inputAttributes = d.getOrderedInputAttributes();
		assertTrue(inputAttributes.size() == 3);
		assertTrue(inputAttributes.get(0).equals(attr3));
		assertTrue(inputAttributes.get(1).equals(attr2));
		assertTrue(inputAttributes.get(2).equals(attr1));
		
	}
	
	@Test public void testRemoveInputAttributes() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		d.appendInputAttribute(attr1);

		RangeValuesAttribute attr2 = new RangeValuesAttribute("attribute 2", DataType.INTEGER);
		p.addAttribute(attr2);
		d.appendInputAttribute(attr2);

		RangeValuesAttribute attr3 = new RangeValuesAttribute("attribute 3", DataType.INTEGER);
		p.addAttribute(attr3);
		d.appendInputAttribute(attr3);
		
		
		assertTrue(d.getOrderedInputAttributes().size() == 3);
		d.removeInputAttribute(attr1);

		assertTrue(d.getOrderedInputAttributes().size() == 2);
		d.removeInputAttribute(attr1);
		assertTrue(d.getOrderedInputAttributes().size() == 2);
		assertTrue(d.getInputAttributeAtOrder(0).equals(attr2));
		assertTrue(d.getInputAttributeAtOrder(1).equals(attr3));
	
		d.removeInputAttribute(attr2);
		assertTrue(d.getOrderedInputAttributes().size() == 1);

		d.removeInputAttribute(null);
		assertTrue(d.getOrderedInputAttributes().size() == 1);

		d.removeInputAttribute(attr3);
		assertTrue(d.getOrderedInputAttributes().size() == 0);
	
	}
	
	@Test public void testChangeInputAttributeOrder() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		d.appendInputAttribute(attr1);

		RangeValuesAttribute attr2 = new RangeValuesAttribute("attribute 2", DataType.INTEGER);
		p.addAttribute(attr2);
		d.appendInputAttribute(attr2);

		RangeValuesAttribute attr3 = new RangeValuesAttribute("attribute 3", DataType.INTEGER);
		p.addAttribute(attr3);
		d.appendInputAttribute(attr3);
		
		RangeValuesAttribute attr4 = new RangeValuesAttribute("attribute 4", DataType.INTEGER);
		p.addAttribute(attr4);
		d.appendInputAttribute(attr4);

		
		assertTrue(d.getInputAttributeOrder(attr4) == 3);
		d.changeInputAttributeOrder(attr4, 1);
		
		assertTrue(d.getInputAttributeOrder(attr1) == 0);
		assertTrue(d.getInputAttributeOrder(attr4) == 1);
		assertTrue(d.getInputAttributeOrder(attr2) == 2);
		assertTrue(d.getInputAttributeOrder(attr3) == 3);
		
		
		d.changeInputAttributeOrder(attr4, 0);
		assertTrue(d.getInputAttributeOrder(attr4) == 0);
		assertTrue(d.getInputAttributeOrder(attr1) == 1);
		assertTrue(d.getInputAttributeOrder(attr2) == 2);
		assertTrue(d.getInputAttributeOrder(attr3) == 3);

		d.changeInputAttributeOrder(attr4, 3);
		assertTrue(d.getInputAttributeOrder(attr1) == 0);
		assertTrue(d.getInputAttributeOrder(attr2) == 1);
		assertTrue(d.getInputAttributeOrder(attr3) == 2);
		assertTrue(d.getInputAttributeOrder(attr4) == 3);
	}
	
	@Test public void testAppendInvalidOutputAttribute1() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);
		
		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		
		exception.expect(GenstarException.class);
		d.appendOutputAttribute(attr1);
		
		assertTrue(d.getOrderedOutputAttributes().size() == 0);
	}
	
	@Test public void testAppendInvalidOutputAttribute2() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);
		
		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		d.appendOutputAttribute(attr1);
		
		assertTrue(d.getOrderedOutputAttributes().size() == 1);
		
		exception.expect(GenstarException.class);
		d.appendOutputAttribute(attr1);		
	}
	
	@Test public void testAppendValidOutputAttributes() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);
		
		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		d.appendOutputAttribute(attr1);
		assertTrue(d.getOrderedOutputAttributes().size() == 1);
		
		RangeValuesAttribute attr2 = new RangeValuesAttribute("attribute 2", DataType.INTEGER);
		p.addAttribute(attr2);
		d.appendOutputAttribute(attr2);
		assertTrue(d.getOrderedOutputAttributes().size() == 2);
	}
	
	@Test public void testInsertInvalidOutputAttribute1() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		exception.expect(GenstarException.class);
		d.insertOutputAttribute(attr1, 0);
	}
	
	@Test public void testInsertInvalidOutputAttribute2() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		exception.expect(GenstarException.class);
		d.insertOutputAttribute(attr1, 1);
	}
	
	@Test public void testInsertValidOutputAttributes() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		d.insertOutputAttribute(attr1, 0);
		assertTrue(d.getOrderedOutputAttributes().size() == 1);
		assertTrue(d.getOutputAttributeAtOrder(0).equals(attr1));

		RangeValuesAttribute attr2 = new RangeValuesAttribute("attribute 2", DataType.INTEGER);
		p.addAttribute(attr2);
		d.insertOutputAttribute(attr2, 0);
		assertTrue(d.getOrderedOutputAttributes().size() == 2);
		assertTrue(d.getOutputAttributeAtOrder(0).equals(attr2));
	}
	
	@Test public void testGetOutputAttributes() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		d.insertOutputAttribute(attr1, 0);

		RangeValuesAttribute attr2 = new RangeValuesAttribute("attribute 2", DataType.INTEGER);
		p.addAttribute(attr2);
		d.insertOutputAttribute(attr2, 0);

		RangeValuesAttribute attr3 = new RangeValuesAttribute("attribute 3", DataType.INTEGER);
		p.addAttribute(attr3);
		d.insertOutputAttribute(attr3, 0);
		
		
		List<AbstractAttribute> outputAttributes = d.getOrderedOutputAttributes();
		assertTrue(outputAttributes.size() == 3);
		assertTrue(outputAttributes.get(0).equals(attr3));
		assertTrue(outputAttributes.get(1).equals(attr2));
		assertTrue(outputAttributes.get(2).equals(attr1));
	}
	
	@Test public void testRemoveOutputAttributes() throws GenstarException {
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		d.appendOutputAttribute(attr1);

		RangeValuesAttribute attr2 = new RangeValuesAttribute("attribute 2", DataType.INTEGER);
		p.addAttribute(attr2);
		d.appendOutputAttribute(attr2);

		RangeValuesAttribute attr3 = new RangeValuesAttribute("attribute 3", DataType.INTEGER);
		p.addAttribute(attr3);
		d.appendOutputAttribute(attr3);
		
		
		assertTrue(d.getOrderedOutputAttributes().size() == 3);
		d.removeOutputAttribute(attr1);

		assertTrue(d.getOrderedOutputAttributes().size() == 2);
		d.removeOutputAttribute(attr1);
		assertTrue(d.getOrderedOutputAttributes().size() == 2);
		assertTrue(d.getOutputAttributeAtOrder(0).equals(attr2));
		assertTrue(d.getOutputAttributeAtOrder(1).equals(attr3));
	
		d.removeOutputAttribute(attr2);
		assertTrue(d.getOrderedOutputAttributes().size() == 1);

		d.removeOutputAttribute(null);
		assertTrue(d.getOrderedOutputAttributes().size() == 1);

		d.removeOutputAttribute(attr3);
		assertTrue(d.getOrderedOutputAttributes().size() == 0);
	}
	
	@Test public void testChangeOutputAttributeOrder() throws GenstarException {
		
		SampleFreeGenerator p = new SampleFreeGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		RangeValuesAttribute attr1 = new RangeValuesAttribute("attribute 1", DataType.INTEGER);
		p.addAttribute(attr1);
		d.appendOutputAttribute(attr1);

		RangeValuesAttribute attr2 = new RangeValuesAttribute("attribute 2", DataType.INTEGER);
		p.addAttribute(attr2);
		d.appendOutputAttribute(attr2);

		RangeValuesAttribute attr3 = new RangeValuesAttribute("attribute 3", DataType.INTEGER);
		p.addAttribute(attr3);
		d.appendOutputAttribute(attr3);
		
		RangeValuesAttribute attr4 = new RangeValuesAttribute("attribute 4", DataType.INTEGER);
		p.addAttribute(attr4);
		d.appendOutputAttribute(attr4);

		
		assertTrue(d.getOutputAttributeOrder(attr4) == 3);
		d.changeOutputAttributeOrder(attr4, 1);
		
		assertTrue(d.getOutputAttributeOrder(attr1) == 0);
		assertTrue(d.getOutputAttributeOrder(attr4) == 1);
		assertTrue(d.getOutputAttributeOrder(attr2) == 2);
		assertTrue(d.getOutputAttributeOrder(attr3) == 3);
		
		
		d.changeOutputAttributeOrder(attr4, 0);
		assertTrue(d.getOutputAttributeOrder(attr4) == 0);
		assertTrue(d.getOutputAttributeOrder(attr1) == 1);
		assertTrue(d.getOutputAttributeOrder(attr2) == 2);
		assertTrue(d.getOutputAttributeOrder(attr3) == 3);

		d.changeOutputAttributeOrder(attr4, 3);
		assertTrue(d.getOutputAttributeOrder(attr1) == 0);
		assertTrue(d.getOutputAttributeOrder(attr2) == 1);
		assertTrue(d.getOutputAttributeOrder(attr3) == 2);
		assertTrue(d.getOutputAttributeOrder(attr4) == 3);
	}
	
	@Test public void testGenerateDistributionElements1() throws GenstarException {
		FrequencyDistributionGenerationRule distribution1 = (FrequencyDistributionGenerationRule) new BondyData().getRule1();
		assertTrue(distribution1.getAttributeValuesFrequencies().size() == 20);
	}
	
	@Test public void testGenerateDistributionElements2() throws GenstarException {
		FrequencyDistributionGenerationRule distribution2 = (FrequencyDistributionGenerationRule) new BondyData().getRule2();
		assertTrue(distribution2.getAttributeValuesFrequencies().size() == 96);
	}
	
	@Test public void testSetFrequencyValue() throws GenstarException {
		SampleFreeGenerator bondyPopulation = new SampleFreeGenerator("Population of Bondy", 100);
		
		RangeValuesAttribute ageRangesAttr1 = new RangeValuesAttribute("age_range_1", "age", DataType.INTEGER);
		for (int[] range : BondyData.age_ranges_1) {
			ageRangesAttr1.add(new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1]), ageRangesAttr1));
		}
		bondyPopulation.addAttribute(ageRangesAttr1);
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute("sex", DataType.BOOL);
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0]), sexAttr));
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[1]), sexAttr));
		bondyPopulation.addAttribute(sexAttr);
				
		
		FrequencyDistributionGenerationRule distribution1 = new FrequencyDistributionGenerationRule(bondyPopulation, "Total population by sex and age group");
		distribution1.appendOutputAttribute(ageRangesAttr1);
		distribution1.appendOutputAttribute(sexAttr);
		bondyPopulation.appendGenerationRule(distribution1);
		
		distribution1.generateAttributeValuesFrequencies();
		
		AbstractAttribute ageRangeAttrBiz = distribution1.getAttributeByNameOnData("age_range_1");
		AbstractAttribute sexAttrBiz = distribution1.getAttributeByNameOnData("sex");
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		AttributeValue sexAttrValueBiz;
		for (int[] range : BondyData.age_ranges_1) {
			attributeValues.clear();
			
			attributeValues.put(ageRangeAttrBiz, new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1]), ageRangeAttrBiz));
			sexAttrValueBiz = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[1]), sexAttrBiz);
			attributeValues.put(sexAttrBiz, sexAttrValueBiz);
			
			// male
			distribution1.setFrequency(attributeValues, range[2]);
			
			
			// female
			attributeValues.put(sexAttrBiz, new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0]), sexAttrBiz));
			distribution1.setFrequency(attributeValues, range[3]);
		}

		
		List<AttributeValuesFrequency> matches;
		AttributeValue ageRangeAttrValueBiz;
		for (int[] range : BondyData.age_ranges_1) {
			attributeValues.clear();
			
			ageRangeAttrValueBiz = new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1]), ageRangeAttrBiz);
			attributeValues.put(ageRangeAttrBiz, ageRangeAttrValueBiz);
			assertTrue(distribution1.findAttributeValuesFrequencies(attributeValues).size() == 2);
			
			// male
			sexAttrValueBiz = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[1]), sexAttrBiz);
			attributeValues.put(sexAttrBiz, sexAttrValueBiz);

			// assertion
			matches = distribution1.findAttributeValuesFrequencies(attributeValues);
			assertTrue(distribution1.findAttributeValuesFrequencies(attributeValues).size() == 1);
			assertTrue(matches.get(0).getFrequency() == range[2]);
			
			// female
			attributeValues.put(sexAttrBiz, new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0]), sexAttrBiz));

			// assertion
			matches = distribution1.findAttributeValuesFrequencies(attributeValues);
			assertTrue(distribution1.findAttributeValuesFrequencies(attributeValues).size() == 1);
			assertTrue(matches.get(0).getFrequency() == range[3]);
		}
	}
	
	@Test public void testGenerate1() throws GenstarException {
		BondyData bondyData = new BondyData();
		ISyntheticPopulationGenerator bondyPopulationGenerator = bondyData.getInhabitantPopGenerator();
		bondyPopulationGenerator.setNbOfEntities(1);
		IPopulation population = bondyPopulationGenerator.generate(); 
		Entity entity = new Entity(population);
		
		
		// rule1
		FrequencyDistributionGenerationRule rule1 = (FrequencyDistributionGenerationRule) bondyData.getRule1();
		
		for (AbstractAttribute attr : rule1.getOrderedOutputAttributes()) {
			assertFalse(entity.getEntityAttributeValue(attr) != null);
		}
		rule1.generate(entity);
		for (AbstractAttribute attr : rule1.getOrderedOutputAttributes()) {
			assertTrue(entity.getEntityAttributeValue(attr) != null);
		}
		
//		EntityAttributeValue entityAgeAttributeValue = entity.getEntityAttributeValueByNameOnData("age");
		EntityAttributeValue entityAgeAttributeValue = entity.getEntityAttributeValueByNameOnData(population.getAttributeByNameOnEntity("age").getNameOnData());
		AttributeValue originalAgeAttributeValue = entityAgeAttributeValue.getAttributeValueOnData();
		assertTrue(originalAgeAttributeValue.getDataType().equals(DataType.INTEGER));
		assertTrue(originalAgeAttributeValue instanceof RangeValue);
		AttributeValue castedAgeAttributeValue = entityAgeAttributeValue.getAttributeValueOnEntity();
		assertTrue(castedAgeAttributeValue instanceof UniqueValue);
		assertTrue(originalAgeAttributeValue.isValueMatched(castedAgeAttributeValue));
		
		AttributeValue sexAttributeValue = entity.getEntityAttributeValueByNameOnData("sex").getAttributeValueOnData();
		assertTrue(sexAttributeValue.getDataType().equals(DataType.BOOL));
		assertTrue(sexAttributeValue instanceof UniqueValue);
		
		
		// rule2
		FrequencyDistributionGenerationRule rule2 = (FrequencyDistributionGenerationRule) bondyData.getRule2();
		
		AbstractAttribute pcsAttr = entity.getPopulation().getAttributeByNameOnData("pcs");
		assertFalse(entity.getEntityAttributeValue(pcsAttr) != null);
		rule2.generate(entity);
		assertTrue(entity.getEntityAttributeValue(pcsAttr) != null);
		
	}
	
	@Test public void testGenerate2() throws GenstarException {
		BondyData bondyData = new BondyData();
		ISyntheticPopulationGenerator bondyPopulationGenerator = bondyData.getInhabitantPopGenerator();
		bondyPopulationGenerator.setNbOfEntities(1);
		Entity entity = new Entity(bondyPopulationGenerator.generate());
		
		// rule2
		FrequencyDistributionGenerationRule rule2 = (FrequencyDistributionGenerationRule) bondyData.getRule2();
		
		AbstractAttribute pcsAttr = entity.getPopulation().getAttributeByNameOnData("pcs");
		assertFalse(entity.getEntityAttributeValue(pcsAttr) != null);
		rule2.generate(entity);
		assertTrue(entity.getEntityAttributeValue(pcsAttr) != null);
		
		EntityAttributeValue pcsEntityAttrValue = entity.getEntityAttributeValueByNameOnData("pcs");
		assertTrue(pcsEntityAttrValue.getAttributeValueOnData().isValueMatched(new UniqueValue(DataType.INTEGER, "8", pcsAttr)));
		assertTrue(pcsEntityAttrValue.getAttributeValueOnData().equals(pcsEntityAttrValue.getAttributeValueOnEntity()));
	}
}
