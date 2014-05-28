package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ummisco.genstar.exception.AttributeException;
import ummisco.genstar.exception.GenstarException;

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
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		
		assertEquals(d.getName(), "Distribution of household size, sex and age of head");
		assertEquals(d.getGenerator(), p);
		assertTrue(d.getDistributionElements().isEmpty());
		assertTrue(d.getInputAttributes().isEmpty());
		assertTrue(d.getOutputAttributes().isEmpty());
	}

	@Test public void testAppendInvalidInputAttributes1() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);
		
		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "data var name", "entity var name", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		d.appendInputAttribute(attr1);
		
		assertTrue(d.getInputAttributes().size() == 0);
	}
	
	@Test public void testAppendValidInputAttributes() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);
		assertTrue(d.getInputAttributes().size() == 0);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		d.appendInputAttribute(attr1);
		
		assertTrue(d.getInputAttributes().size() == 1);
		
		EnumerationOfRangesAttribute attr2 = new EnumerationOfRangesAttribute(p, "attribute 2", ValueType.INTEGER);
		p.addAttribute(attr2);
		d.appendInputAttribute(attr2);

		assertTrue(d.getInputAttributes().size() == 2);
	}
	
	@Test public void testAppendInvalidInputAttribute2() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);
		
		assertTrue(d.getInputAttributes().size() == 0);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		d.appendInputAttribute(attr1);
		
		assertTrue(d.getInputAttributes().size() == 1);
		
		exception.expect(AttributeException.class);
		d.appendInputAttribute(attr1);
	}
	
	@Test public void testInsertInvalidInputAttribute1() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		exception.expect(AttributeException.class);
		d.insertInputAttribute(attr1, 0);
	}
	
	@Test public void testInsertInvalidInputAttribute2() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		exception.expect(AttributeException.class);
		d.insertInputAttribute(attr1, 1);
	}
	
	@Test public void testInsertValidInputAttributes() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		d.insertInputAttribute(attr1, 0);
		assertTrue(d.getInputAttributes().size() == 1);
		assertTrue(d.getInputAttributeAtOrder(0).equals(attr1));

		EnumerationOfRangesAttribute attr2 = new EnumerationOfRangesAttribute(p, "attribute 2", ValueType.INTEGER);
		p.addAttribute(attr2);
		d.insertInputAttribute(attr2, 0);
		assertTrue(d.getInputAttributes().size() == 2);
		assertTrue(d.getInputAttributeAtOrder(0).equals(attr2));
	}
	
	@Test public void testGetInputAttributes() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		d.insertInputAttribute(attr1, 0);

		EnumerationOfRangesAttribute attr2 = new EnumerationOfRangesAttribute(p, "attribute 2", ValueType.INTEGER);
		p.addAttribute(attr2);
		d.insertInputAttribute(attr2, 0);

		EnumerationOfRangesAttribute attr3 = new EnumerationOfRangesAttribute(p, "attribute 3", ValueType.INTEGER);
		p.addAttribute(attr3);
		d.insertInputAttribute(attr3, 0);
		
		
		List<AbstractAttribute> inputAttributes = d.getInputAttributes();
		assertTrue(inputAttributes.size() == 3);
		assertTrue(inputAttributes.get(0).equals(attr3));
		assertTrue(inputAttributes.get(1).equals(attr2));
		assertTrue(inputAttributes.get(2).equals(attr1));
		
	}
	
	@Test public void testRemoveInputAttributes() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		d.appendInputAttribute(attr1);

		EnumerationOfRangesAttribute attr2 = new EnumerationOfRangesAttribute(p, "attribute 2", ValueType.INTEGER);
		p.addAttribute(attr2);
		d.appendInputAttribute(attr2);

		EnumerationOfRangesAttribute attr3 = new EnumerationOfRangesAttribute(p, "attribute 3", ValueType.INTEGER);
		p.addAttribute(attr3);
		d.appendInputAttribute(attr3);
		
		
		assertTrue(d.getInputAttributes().size() == 3);
		d.removeInputAttribute(attr1);

		assertTrue(d.getInputAttributes().size() == 2);
		d.removeInputAttribute(attr1);
		assertTrue(d.getInputAttributes().size() == 2);
		assertTrue(d.getInputAttributeAtOrder(0).equals(attr2));
		assertTrue(d.getInputAttributeAtOrder(1).equals(attr3));
	
		d.removeInputAttribute(attr2);
		assertTrue(d.getInputAttributes().size() == 1);

		d.removeInputAttribute(null);
		assertTrue(d.getInputAttributes().size() == 1);

		d.removeInputAttribute(attr3);
		assertTrue(d.getInputAttributes().size() == 0);
	
	}
	
	@Test public void testChangeInputAttributeOrder() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		d.appendInputAttribute(attr1);

		EnumerationOfRangesAttribute attr2 = new EnumerationOfRangesAttribute(p, "attribute 2", ValueType.INTEGER);
		p.addAttribute(attr2);
		d.appendInputAttribute(attr2);

		EnumerationOfRangesAttribute attr3 = new EnumerationOfRangesAttribute(p, "attribute 3", ValueType.INTEGER);
		p.addAttribute(attr3);
		d.appendInputAttribute(attr3);
		
		EnumerationOfRangesAttribute attr4 = new EnumerationOfRangesAttribute(p, "attribute 4", ValueType.INTEGER);
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
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);
		
		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		
		exception.expect(AttributeException.class);
		d.appendOutputAttribute(attr1);
		
		assertTrue(d.getOutputAttributes().size() == 0);
	}
	
	@Test public void testAppendInvalidOutputAttribute2() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);
		
		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		d.appendOutputAttribute(attr1);
		
		assertTrue(d.getOutputAttributes().size() == 1);
		
		exception.expect(AttributeException.class);
		d.appendOutputAttribute(attr1);		
	}
	
	@Test public void testAppendValidOutputAttributes() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);
		
		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		d.appendOutputAttribute(attr1);
		assertTrue(d.getOutputAttributes().size() == 1);
		
		EnumerationOfRangesAttribute attr2 = new EnumerationOfRangesAttribute(p, "attribute 2", ValueType.INTEGER);
		p.addAttribute(attr2);
		d.appendOutputAttribute(attr2);
		assertTrue(d.getOutputAttributes().size() == 2);
	}
	
	@Test public void testInsertInvalidOutputAttribute1() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		exception.expect(AttributeException.class);
		d.insertOutputAttribute(attr1, 0);
	}
	
	@Test public void testInsertInvalidOutputAttribute2() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		exception.expect(AttributeException.class);
		d.insertOutputAttribute(attr1, 1);
	}
	
	@Test public void testInsertValidOutputAttributes() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		d.insertOutputAttribute(attr1, 0);
		assertTrue(d.getOutputAttributes().size() == 1);
		assertTrue(d.getOutputAttributeAtOrder(0).equals(attr1));

		EnumerationOfRangesAttribute attr2 = new EnumerationOfRangesAttribute(p, "attribute 2", ValueType.INTEGER);
		p.addAttribute(attr2);
		d.insertOutputAttribute(attr2, 0);
		assertTrue(d.getOutputAttributes().size() == 2);
		assertTrue(d.getOutputAttributeAtOrder(0).equals(attr2));
	}
	
	@Test public void testGetOutputAttributes() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		d.insertOutputAttribute(attr1, 0);

		EnumerationOfRangesAttribute attr2 = new EnumerationOfRangesAttribute(p, "attribute 2", ValueType.INTEGER);
		p.addAttribute(attr2);
		d.insertOutputAttribute(attr2, 0);

		EnumerationOfRangesAttribute attr3 = new EnumerationOfRangesAttribute(p, "attribute 3", ValueType.INTEGER);
		p.addAttribute(attr3);
		d.insertOutputAttribute(attr3, 0);
		
		
		List<AbstractAttribute> outputAttributes = d.getOutputAttributes();
		assertTrue(outputAttributes.size() == 3);
		assertTrue(outputAttributes.get(0).equals(attr3));
		assertTrue(outputAttributes.get(1).equals(attr2));
		assertTrue(outputAttributes.get(2).equals(attr1));
	}
	
	@Test public void testRemoveOutputAttributes() throws GenstarException {
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		d.appendOutputAttribute(attr1);

		EnumerationOfRangesAttribute attr2 = new EnumerationOfRangesAttribute(p, "attribute 2", ValueType.INTEGER);
		p.addAttribute(attr2);
		d.appendOutputAttribute(attr2);

		EnumerationOfRangesAttribute attr3 = new EnumerationOfRangesAttribute(p, "attribute 3", ValueType.INTEGER);
		p.addAttribute(attr3);
		d.appendOutputAttribute(attr3);
		
		
		assertTrue(d.getOutputAttributes().size() == 3);
		d.removeOutputAttribute(attr1);

		assertTrue(d.getOutputAttributes().size() == 2);
		d.removeOutputAttribute(attr1);
		assertTrue(d.getOutputAttributes().size() == 2);
		assertTrue(d.getOutputAttributeAtOrder(0).equals(attr2));
		assertTrue(d.getOutputAttributeAtOrder(1).equals(attr3));
	
		d.removeOutputAttribute(attr2);
		assertTrue(d.getOutputAttributes().size() == 1);

		d.removeOutputAttribute(null);
		assertTrue(d.getOutputAttributes().size() == 1);

		d.removeOutputAttribute(attr3);
		assertTrue(d.getOutputAttributes().size() == 0);
	}
	
	@Test public void testChangeOutputAttributeOrder() throws GenstarException {
		
		SyntheticPopulationGenerator p = new SyntheticPopulationGenerator("Household population", 10);
		FrequencyDistributionGenerationRule d = new FrequencyDistributionGenerationRule(p, "Distribution of household size, sex and age of head");
		p.appendGenerationRule(d);

		EnumerationOfRangesAttribute attr1 = new EnumerationOfRangesAttribute(p, "attribute 1", ValueType.INTEGER);
		p.addAttribute(attr1);
		d.appendOutputAttribute(attr1);

		EnumerationOfRangesAttribute attr2 = new EnumerationOfRangesAttribute(p, "attribute 2", ValueType.INTEGER);
		p.addAttribute(attr2);
		d.appendOutputAttribute(attr2);

		EnumerationOfRangesAttribute attr3 = new EnumerationOfRangesAttribute(p, "attribute 3", ValueType.INTEGER);
		p.addAttribute(attr3);
		d.appendOutputAttribute(attr3);
		
		EnumerationOfRangesAttribute attr4 = new EnumerationOfRangesAttribute(p, "attribute 4", ValueType.INTEGER);
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
		distribution1.generateFrequencyElements();
		assertTrue(distribution1.getDistributionElements().size() == 20);
	}
	
	@Test public void testGenerateDistributionElements2() throws GenstarException {
		FrequencyDistributionGenerationRule distribution2 = (FrequencyDistributionGenerationRule) new BondyData().getRule2();
		distribution2.generateFrequencyElements();
		assertTrue(distribution2.getDistributionElements().size() == 96);
	}
	
	private void printDistributionElements(final FrequencyDistributionGenerationRule d) {
		System.out.println("Distribution elements of '" + d.getName() + "' distribution : ");
		for (FrequencyDistributionElement e : d.getDistributionElements()) { System.out.println(e); }
	}
	
	@Test public void testSetFrequencyValue() throws GenstarException {
		SyntheticPopulationGenerator bondyPopulation = new SyntheticPopulationGenerator("Population of Bondy", 100);
		
		EnumerationOfRangesAttribute ageRangesAttr1 = new EnumerationOfRangesAttribute(bondyPopulation, "age_range_1", "age", ValueType.INTEGER);
		for (int[] range : BondyData.age_ranges_1) {
			ageRangesAttr1.add(new RangeValue(ValueType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
		}
		bondyPopulation.addAttribute(ageRangesAttr1);
		
		EnumerationOfValuesAttribute sexAttr = new EnumerationOfValuesAttribute(bondyPopulation, "sex", ValueType.BOOL);
		sexAttr.add(new UniqueValue(ValueType.BOOL, Boolean.toString(BondyData.sexes[0])));
		sexAttr.add(new UniqueValue(ValueType.BOOL, Boolean.toString(BondyData.sexes[1])));
		bondyPopulation.addAttribute(sexAttr);
				
		
		FrequencyDistributionGenerationRule distribution1 = new FrequencyDistributionGenerationRule(bondyPopulation, "Total population by sex and age group");
		distribution1.appendOutputAttribute(ageRangesAttr1);
		distribution1.appendOutputAttribute(sexAttr);
		bondyPopulation.appendGenerationRule(distribution1);
		
		distribution1.generateFrequencyElements();
		
		AbstractAttribute ageRangeAttrBiz = distribution1.getAttributeByDataAttributeName("age_range_1");
		AbstractAttribute sexAttrBiz = distribution1.getAttributeByDataAttributeName("sex");
		Map<EnumerationValueAttribute, AttributeValue> attributeValues = new HashMap<EnumerationValueAttribute, AttributeValue>();
		AttributeValue sexAttrValueBiz;
		for (int[] range : BondyData.age_ranges_1) {
			attributeValues.clear();
			
			attributeValues.put((EnumerationValueAttribute) ageRangeAttrBiz, new RangeValue(ValueType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
			sexAttrValueBiz = new UniqueValue(ValueType.BOOL, Boolean.toString(BondyData.sexes[1]));
			attributeValues.put((EnumerationValueAttribute) sexAttrBiz, sexAttrValueBiz);
			
			// male
			distribution1.setFrequency(attributeValues, range[2]);
			
			
			// female
//			attributeValues.remove(sexAttrValueBiz);
			attributeValues.put((EnumerationValueAttribute) sexAttrBiz, new UniqueValue(ValueType.BOOL, Boolean.toString(BondyData.sexes[0])));
			distribution1.setFrequency(attributeValues, range[3]);
		}

		
		List<FrequencyDistributionElement> matches;
		AttributeValue ageRangeAttrValueBiz;
		for (int[] range : BondyData.age_ranges_1) {
			attributeValues.clear();
			
			ageRangeAttrValueBiz = new RangeValue(ValueType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1]));
			attributeValues.put((EnumerationValueAttribute) ageRangeAttrBiz, ageRangeAttrValueBiz);
			assertTrue(distribution1.findDistributionElements(attributeValues).size() == 2);
			
			// male
			sexAttrValueBiz = new UniqueValue(ValueType.BOOL, Boolean.toString(BondyData.sexes[1]));
			attributeValues.put((EnumerationValueAttribute) sexAttrBiz, sexAttrValueBiz);

			// assertion
			matches = distribution1.findDistributionElements(attributeValues);
			assertTrue(distribution1.findDistributionElements(attributeValues).size() == 1);
			assertTrue(matches.get(0).getFrequency() == range[2]);
			
			// female
			attributeValues.put((EnumerationValueAttribute) sexAttrBiz, new UniqueValue(ValueType.BOOL, Boolean.toString(BondyData.sexes[0])));

			// assertion
			matches = distribution1.findDistributionElements(attributeValues);
			assertTrue(distribution1.findDistributionElements(attributeValues).size() == 1);
			assertTrue(matches.get(0).getFrequency() == range[3]);
		}
	}
	
	@Test public void testGenerate1() throws GenstarException {
		BondyData bondyData = new BondyData();
		ISyntheticPopulationGenerator bondyPopulationGenerator = bondyData.getInhabitantPopGenerator();
		bondyPopulationGenerator.setNbOfEntities(1);
		Entity entity = new Entity(bondyPopulationGenerator.generate());
		
		
		// rule1
		FrequencyDistributionGenerationRule rule1 = (FrequencyDistributionGenerationRule) bondyData.getRule1();
		
		for (AbstractAttribute attr : rule1.getOutputAttributes()) {
			assertFalse(entity.containAttribute(attr.getNameOnEntity()));
		}
		rule1.generate(entity);
		for (AbstractAttribute attr : rule1.getOutputAttributes()) {
			assertTrue(entity.containAttribute(attr.getNameOnEntity()));
		}
		
		EntityAttributeValue entityAgeAttributeValue = entity.getEntityAttributeValue("age");
		AttributeValue originalAgeAttributeValue = entityAgeAttributeValue.getAttributeValueOnData();
		assertTrue(originalAgeAttributeValue.getValueType().equals(ValueType.INTEGER));
		assertTrue(originalAgeAttributeValue instanceof RangeValue);
		AttributeValue castedAgeAttributeValue = entityAgeAttributeValue.getAttributeValueOnEntity();
		assertTrue(castedAgeAttributeValue instanceof UniqueValue);
		assertTrue(originalAgeAttributeValue.isValueMatch(castedAgeAttributeValue));
		
		AttributeValue sexAttributeValue = entity.getEntityAttributeValue("sex").getAttributeValueOnData();
		assertTrue(sexAttributeValue.getValueType().equals(ValueType.BOOL));
		assertTrue(sexAttributeValue instanceof UniqueValue);
		
		
		// rule2
		FrequencyDistributionGenerationRule rule2 = (FrequencyDistributionGenerationRule) bondyData.getRule2();
		
		assertFalse(entity.containAttribute("pcs"));
		rule2.generate(entity);
		assertTrue(entity.containAttribute("pcs"));
		
	}
	
	@Test public void testGenerate2() throws GenstarException {
		BondyData bondyData = new BondyData();
		ISyntheticPopulationGenerator bondyPopulationGenerator = bondyData.getInhabitantPopGenerator();
		bondyPopulationGenerator.setNbOfEntities(1);
		Entity entity = new Entity(bondyPopulationGenerator.generate());
		
		// rule2
		FrequencyDistributionGenerationRule rule2 = (FrequencyDistributionGenerationRule) bondyData.getRule2();
		
		assertFalse(entity.containAttribute("pcs"));
		rule2.generate(entity);
		assertTrue(entity.containAttribute("pcs"));
		
		EntityAttributeValue pcsEntityAttrValue = entity.getEntityAttributeValue("pcs");
		assertTrue(pcsEntityAttrValue.getAttributeValueOnData().isValueMatch(new UniqueValue(ValueType.INTEGER, "8")));
		assertTrue(pcsEntityAttrValue.getAttributeValueOnData().equals(pcsEntityAttrValue.getAttributeValueOnEntity()));
	}
}
