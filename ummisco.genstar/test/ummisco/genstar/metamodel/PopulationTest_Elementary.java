package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;

@RunWith(JUnit4.class)
public class PopulationTest_Elementary {
	@Rule public ExpectedException exception = ExpectedException.none();

	@Test
	public void testInvalidParameterConstructor() throws GenstarException {
		exception.expect(GenstarException.class);

		new MultipleRulesGenerator(null, 1);
		new MultipleRulesGenerator("test population", 0);
		new MultipleRulesGenerator(null, -1);
	}
	
	@Test
	public void testValueParameterConstructor() throws GenstarException {
		new MultipleRulesGenerator("test population", 1);
	}
	
	@Test
	public void testGetNameAndNbOfEntities() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);
		
		Assert.assertEquals(population.getGeneratorName(), "test population");
		Assert.assertEquals(population.getNbOfEntities(), 1);
		
	}
	
	@Test
	public void testAddNullAttribute() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);

		exception.expect(GenstarException.class);
		population.addAttribute(null);
	}
	
	@Test
	public void testAddAttribute() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);
		
		assertTrue(population.getAttributes().size() == 0);

		AbstractAttribute attr = new UniqueValuesAttribute(population, "dummy attribute", DataType.STRING);
		population.addAttribute(attr);
		assertTrue(population.getAttributes().size() == 1);

		AbstractAttribute attr1 = new UniqueValuesAttribute(population, "dummy attribute1", DataType.STRING);
		population.addAttribute(attr1);
		assertTrue(population.getAttributes().size() == 2);
	}
	
	@Test
	public void testDuplicatedAttributeName() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);

		AbstractAttribute attr = new UniqueValuesAttribute(population, "dummy attribute", DataType.STRING);
		population.addAttribute(attr);

		AbstractAttribute attr1 = new UniqueValuesAttribute(population, "dummy attribute", DataType.STRING);
		exception.expect(GenstarException.class);
		population.addAttribute(attr1);
	}
	
	@Test public void testAddAttributeInvalidPopulation() throws GenstarException {
		MultipleRulesGenerator population1 = new MultipleRulesGenerator("test population1", 1);
		MultipleRulesGenerator population2 = new MultipleRulesGenerator("test population2", 1);

		AbstractAttribute attr = new UniqueValuesAttribute(population1, "dummy attribute", DataType.STRING);
		exception.expect(GenstarException.class);
		population2.addAttribute(attr);
	}
	
	@Test public void testGetAttributes() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);

		AbstractAttribute attr = new UniqueValuesAttribute(population, "dummy attribute", DataType.STRING);
		population.addAttribute(attr);
		assertTrue(population.getAttributes().size() == 1);

		AbstractAttribute attr1 = new UniqueValuesAttribute(population, "dummy attribute1", DataType.STRING);
		population.addAttribute(attr1);
		assertTrue(population.getAttributes().size() == 2);
		
		Collection<AbstractAttribute> attributes = population.getAttributes();
		assertTrue(attributes.contains(attr) && attributes.contains(attr1));
	}
	
	@Test public void testContainAttributeName() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);
		
		assertFalse(population.containAttribute("dummy attribute"));

		AbstractAttribute attr = new UniqueValuesAttribute(population, "dummy attribute", DataType.STRING);
		population.addAttribute(attr);
		assertTrue(population.containAttribute("dummy attribute"));

		AbstractAttribute attr1 = new UniqueValuesAttribute(population, "dummy attribute1", DataType.STRING);
		population.addAttribute(attr1);
		assertTrue(population.containAttribute("dummy attribute1"));

		assertFalse(population.containAttribute(new String()));
	}
	
	@Test public void testAppendValidFrequencyGenerationRules() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);
		
		assertTrue(population.getGenerationRules().size() == 0);

		FrequencyDistributionGenerationRule dis1 = new FrequencyDistributionGenerationRule(population, "distribution 1");
		population.appendGenerationRule(dis1);
		assertTrue(population.getGenerationRules().size() == 1);
		assertTrue(population.getGenerationRuleOrder(dis1) == 0);
		
		FrequencyDistributionGenerationRule dis2 = new FrequencyDistributionGenerationRule(population, "distribution 2");
		population.appendGenerationRule(dis2);
		assertTrue(population.getGenerationRules().size() == 2);
		assertTrue(population.getGenerationRuleOrder(dis2) == 1);
	}
	
	@Test public void testAppendInvalidGenerationRule1() throws GenstarException {
		MultipleRulesGenerator population1 = new MultipleRulesGenerator("test population1", 1);
		MultipleRulesGenerator population2 = new MultipleRulesGenerator("test population2", 1);
		
		FrequencyDistributionGenerationRule dis1 = new FrequencyDistributionGenerationRule(population1, "distribution 1");
		exception.expect(IllegalArgumentException.class);
		population2.appendGenerationRule(dis1);
	}
	
	@Test public void testAppendInvalidGenerationRule2() throws GenstarException {
		MultipleRulesGenerator population1 = new MultipleRulesGenerator("test population1", 1);
		
		FrequencyDistributionGenerationRule dis1 = new FrequencyDistributionGenerationRule(population1, "distribution 1");
		population1.appendGenerationRule(dis1);
		exception.expect(IllegalArgumentException.class);
		population1.appendGenerationRule(dis1);
	}
	
	@Test public void testAppendInvalidGenerationRule3() throws GenstarException {
		MultipleRulesGenerator population1 = new MultipleRulesGenerator("test population1", 1);
		
		FrequencyDistributionGenerationRule dis1 = new FrequencyDistributionGenerationRule(population1, "distribution 1");
		FrequencyDistributionGenerationRule dis2 = new FrequencyDistributionGenerationRule(population1, "distribution 1");
		population1.appendGenerationRule(dis1);
		exception.expect(IllegalArgumentException.class);
		population1.appendGenerationRule(dis2);
	}
	
	@Test public void testInsertValidGenerationRules() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);
		
		assertTrue(population.getGenerationRules().size() == 0);

		FrequencyDistributionGenerationRule dis1 = new FrequencyDistributionGenerationRule(population, "distribution 1");
		population.insertGenerationRule(dis1, 0);
		assertTrue(population.getGenerationRules().size() == 1);
		assertTrue(population.getGenerationRuleOrder(dis1) == 0);
		
		FrequencyDistributionGenerationRule dis2 = new FrequencyDistributionGenerationRule(population, "distribution 2");
		population.insertGenerationRule(dis2, 0);
		assertTrue(population.getGenerationRules().size() == 2);
		assertTrue(population.getGenerationRuleOrder(dis2) == 0);
		assertTrue(population.getGenerationRuleOrder(dis1) == 1);

		FrequencyDistributionGenerationRule dis3 = new FrequencyDistributionGenerationRule(population, "distribution 3");
		population.insertGenerationRule(dis3, 1);
		assertTrue(population.getGenerationRules().size() == 3);
		assertTrue(population.getGenerationRuleOrder(dis3) == 1);
		assertTrue(population.getGenerationRuleOrder(dis2) == 0);
		assertTrue(population.getGenerationRuleOrder(dis1) == 2);

		FrequencyDistributionGenerationRule dis4 = new FrequencyDistributionGenerationRule(population, "distribution 4");
		population.insertGenerationRule(dis4, 3);
		assertTrue(population.getGenerationRules().size() == 4);
		assertTrue(population.getGenerationRuleOrder(dis4) == 3);
	}
	
	@Test public void testInsertInvalidPositionGenerationRules() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);
		
		FrequencyDistributionGenerationRule dis1 = new FrequencyDistributionGenerationRule(population, "distribution 1");
		exception.expect(IllegalArgumentException.class);
		population.insertGenerationRule(dis1, -1);
		
		population.insertGenerationRule(dis1, 1);
		assertTrue(population.getNbOfRules() == 0);
	}
	
	@Test public void testRemoveValidGenerationRule() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);
		
		assertTrue(population.getNbOfRules() == 0);
		population.removeGenerationRule(null);
		assertTrue(population.getNbOfRules() == 0);

		FrequencyDistributionGenerationRule dis1 = new FrequencyDistributionGenerationRule(population, "distribution 1");
		population.appendGenerationRule(dis1);
		
		FrequencyDistributionGenerationRule dis2 = new FrequencyDistributionGenerationRule(population, "distribution 2");
		population.appendGenerationRule(dis2);

		FrequencyDistributionGenerationRule dis3 = new FrequencyDistributionGenerationRule(population, "distribution 3");
		population.appendGenerationRule(dis3);

		FrequencyDistributionGenerationRule dis4 = new FrequencyDistributionGenerationRule(population, "distribution 4");
		population.appendGenerationRule(dis4);
		assertTrue(population.getNbOfRules() == 4);
		
		population.removeGenerationRule(dis4);
		assertTrue(population.getNbOfRules() == 3);
		
		FrequencyDistributionGenerationRule dis5 = new FrequencyDistributionGenerationRule(population, "distribution 5");
		population.removeGenerationRule(dis5);
		assertTrue(population.getNbOfRules() == 3);
		
		population.removeGenerationRule(dis1);
		assertTrue(population.getNbOfRules() == 2);
		
		population.removeGenerationRule(dis2);
		assertTrue(population.getNbOfRules() == 1);
		
		population.removeGenerationRule(dis3);
		assertTrue(population.getNbOfRules() == 0);
	}
	
	@Test public void testChangeGenerationRuleOrder() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);
		
		FrequencyDistributionGenerationRule dis1 = new FrequencyDistributionGenerationRule(population, "distribution 1");
		population.appendGenerationRule(dis1);
		
		FrequencyDistributionGenerationRule dis2 = new FrequencyDistributionGenerationRule(population, "distribution 2");
		population.appendGenerationRule(dis2);

		FrequencyDistributionGenerationRule dis3 = new FrequencyDistributionGenerationRule(population, "distribution 3");
		population.appendGenerationRule(dis3);

		FrequencyDistributionGenerationRule dis4 = new FrequencyDistributionGenerationRule(population, "distribution 4");
		population.appendGenerationRule(dis4);
		assertTrue(population.getGenerationRuleOrder(dis4) == 3);

		population.changeGenerationRuleOrder(dis4, 0);
		assertTrue(population.getGenerationRuleOrder(dis4) == 0);
		assertTrue(population.getGenerationRuleOrder(dis1) == 1);
		assertTrue(population.getGenerationRuleOrder(dis2) == 2);
		assertTrue(population.getGenerationRuleOrder(dis3) == 3);
		
		population.changeGenerationRuleOrder(dis4, 2);
		assertTrue(population.getGenerationRuleOrder(dis4) == 2);
		assertTrue(population.getGenerationRuleOrder(dis1) == 0);
		assertTrue(population.getGenerationRuleOrder(dis2) == 1);
		assertTrue(population.getGenerationRuleOrder(dis3) == 3);
	}
	
	@Test public void testGetGenerationRuleAtOrder() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);
		
		FrequencyDistributionGenerationRule dis1 = new FrequencyDistributionGenerationRule(population, "distribution 1");
		population.appendGenerationRule(dis1);
		
		FrequencyDistributionGenerationRule dis2 = new FrequencyDistributionGenerationRule(population, "distribution 2");
		population.appendGenerationRule(dis2);

		FrequencyDistributionGenerationRule dis3 = new FrequencyDistributionGenerationRule(population, "distribution 3");
		population.appendGenerationRule(dis3);

		FrequencyDistributionGenerationRule dis4 = new FrequencyDistributionGenerationRule(population, "distribution 4");
		population.appendGenerationRule(dis4);
		
		
		assertTrue(dis4.equals(population.getGenerationRuleAtOrder(3)));
		assertTrue(dis1.equals(population.getGenerationRuleAtOrder(0)));
		
		exception.expect(IllegalArgumentException.class);
		population.getGenerationRuleAtOrder(10);
	}
	
	@Test public void testContainGenerationRule() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);
		
		FrequencyDistributionGenerationRule dis1 = new FrequencyDistributionGenerationRule(population, "distribution 1");
		assertFalse(population.containGenerationRule(dis1));
		
		population.appendGenerationRule(dis1);
		assertTrue(population.containGenerationRule(dis1));
	}
	
	@Test public void testContainGenerationRuleName() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);
		
		FrequencyDistributionGenerationRule dis1 = new FrequencyDistributionGenerationRule(population, "distribution 1");
		assertFalse(population.containGenerationRuleName("distribution 1"));
		
		population.appendGenerationRule(dis1);
		assertTrue(population.containGenerationRuleName("distribution 1"));
	}
	
	@Test public void testGetGenerationRules() throws GenstarException {
		MultipleRulesGenerator population = new MultipleRulesGenerator("test population", 1);
		
		FrequencyDistributionGenerationRule dis1 = new FrequencyDistributionGenerationRule(population, "distribution 1");
		population.appendGenerationRule(dis1);
		
		FrequencyDistributionGenerationRule dis2 = new FrequencyDistributionGenerationRule(population, "distribution 2");
		population.appendGenerationRule(dis2);

		FrequencyDistributionGenerationRule dis3 = new FrequencyDistributionGenerationRule(population, "distribution 3");
		population.appendGenerationRule(dis3);

		FrequencyDistributionGenerationRule dis4 = new FrequencyDistributionGenerationRule(population, "distribution 4");
		population.appendGenerationRule(dis4);

		GenerationRule[] ruleArray = new GenerationRule[4];
		ruleArray[0] = dis1;
		ruleArray[1] = dis2;
		ruleArray[2] = dis3;
		ruleArray[3] = dis4;
		
		
		NavigableSet<GenerationRule> rules = population.getGenerationRules();
		assertTrue(rules.size() == 4);
		
		int ruleIndex = 0;
		for (GenerationRule rule : rules) {
			rule.equals(ruleArray[ruleIndex]);
			ruleIndex++;
		}
	}
}
