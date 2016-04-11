package ummisco.genstar.ipf;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mockit.Deencapsulation;

import org.junit.Test;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.PopulationType;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.Population;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarUtils;

public class SampleDataGenerationRuleTest {

	@Test public void initializeSampleDataGenerationRuleSuccessfully() throws GenstarException {
		ISingleRuleGenerator generator = new SingleRuleGenerator("generator"); 
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCSVFile sampleDataFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/people_sample.csv", true);
		GenstarCSVFile controlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/controlled_attributes.csv", false);
		GenstarCSVFile controlsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/control_totals.csv", false);
		GenstarCSVFile supplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/supplementary_attributes.csv", false);
		
		SampleDataGenerationRule rule = new SampleDataGenerationRule(generator, "sample data generation rule",
				controlledAttributesFile, controlsFile, supplementaryAttributesFile, SampleDataGenerationRule.DEFAULT_MAX_ITERATIONS);
		ISampleData sampleData = new SampleData("people", generator.getAttributes(), sampleDataFile);
		rule.setSampleData(sampleData);
		
		assertTrue(rule.getSampleData() != null);
		assertTrue(rule.getControlledAttributes().size() == 3);
		assertTrue(rule.getSupplementaryAttributes().size() == 1);
		assertTrue(rule.getControlTotals() != null);
	}
	
	@Test public void testGenerate() throws GenstarException {
		ISingleRuleGenerator generator = new SingleRuleGenerator("generator"); 
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCSVFile sampleDataFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/people_sample.csv", true);
		GenstarCSVFile controlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/controlled_attributes.csv", false);
		GenstarCSVFile controlsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/control_totals.csv", false);
		GenstarCSVFile supplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/supplementary_attributes.csv", false);
		
		SampleDataGenerationRule rule = new SampleDataGenerationRule(generator, "sample data generation rule", 
				controlledAttributesFile, controlsFile, supplementaryAttributesFile, SampleDataGenerationRule.DEFAULT_MAX_ITERATIONS);
		ISampleData sampleData = new SampleData("people", generator.getAttributes(), sampleDataFile);
		rule.setSampleData(sampleData);
		generator.setNbOfEntities(rule.getIPF().getNbOfEntitiesToGenerate());
		
		List<Entity> internalSampleEntities = Deencapsulation.getField(rule, "internalSampleEntities"); 
		assertTrue(internalSampleEntities == null);
		
		Population population = new Population(PopulationType.SYNTHETIC_POPULATION, "people", generator.getAttributes());
		for (Entity entity : population.createEntities(generator.getNbOfEntities())) { 
			rule.generate(entity);
		}
		
		List<AttributeValuesFrequency> originalSelectionProbabilities = Deencapsulation.getField(rule, "selectionProbabilities");
		List<AttributeValuesFrequency> copySelectionProbabilities = new ArrayList<AttributeValuesFrequency>();
		for (AttributeValuesFrequency origin : originalSelectionProbabilities) {
			copySelectionProbabilities.add(new AttributeValuesFrequency(origin.getAttributeValuesOnData(), origin.getFrequency()));
		}
		
		// verify that the number of generated entities is correct
		assertTrue(population.getEntities().size() == rule.getIPF().getNbOfEntitiesToGenerate());
		
		// verify that the generated entities "conform" to the "selectionProbabilities"
		List<AbstractAttribute> controlledAttributes = rule.getControlledAttributes();
		for (Entity entity : population.getEntities()) {
			for (AttributeValuesFrequency copy : copySelectionProbabilities) {
				if (copy.matchEntity(controlledAttributes, entity)) {
					copy.setFrequency(copy.getFrequency() - 1);
					break;
				}
			}
		}
		
		for (AttributeValuesFrequency copy : copySelectionProbabilities) { assertTrue(copy.getFrequency() == 0); }
		
		internalSampleEntities = Deencapsulation.getField(rule, "internalSampleEntities"); 
		assertTrue(internalSampleEntities.size() == generator.getNbOfEntities());
	}
	
	@Test(expected = Exception.class) public void testGenerateFailed() throws GenstarException {
		ISingleRuleGenerator generator = new SingleRuleGenerator("generator"); 
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCSVFile sampleDataFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/people_sample.csv", true);
		GenstarCSVFile controlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/controlled_attributes.csv", false);
		GenstarCSVFile controlsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/control_totals.csv", false);
		GenstarCSVFile supplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/supplementary_attributes.csv", false);
		
		SampleDataGenerationRule rule = new SampleDataGenerationRule(generator, "sample data generation rule",
				controlledAttributesFile, controlsFile, supplementaryAttributesFile, SampleDataGenerationRule.DEFAULT_MAX_ITERATIONS);
		ISampleData sampleData = new SampleData("people", generator.getAttributes(), sampleDataFile);
		rule.setSampleData(sampleData);
		generator.setNbOfEntities(rule.getIPF().getNbOfEntitiesToGenerate());
		
		Population population = new Population(PopulationType.SYNTHETIC_POPULATION, "people", generator.getAttributes());
		for (Entity entity : population.createEntities(1)) { rule.generate(entity); }

		rule.generate(population.getEntities().get(0));
	}
	
	@Test public void testBuildSampleEntityCategories() throws GenstarException {
		
		ISingleRuleGenerator generator = new SingleRuleGenerator("generator"); 
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCSVFile sampleDataFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/people_sample.csv", true);
		GenstarCSVFile controlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/controlled_attributes.csv", false);
		GenstarCSVFile controlsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/control_totals.csv", false);
		GenstarCSVFile supplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/supplementary_attributes.csv", false);
		
		SampleDataGenerationRule rule = new SampleDataGenerationRule(generator, "sample data generation rule",
				controlledAttributesFile, controlsFile, supplementaryAttributesFile, SampleDataGenerationRule.DEFAULT_MAX_ITERATIONS);
		ISampleData sampleData = new SampleData("people", generator.getAttributes(), sampleDataFile);
		rule.setSampleData(sampleData);
		
		// 1. verify sampleEntityCategories.size()
		Map<AttributeValuesFrequency, List<SampleEntity>> sampleEntityCategories = Deencapsulation.getField(rule, "sampleEntityCategories");
		assertTrue(sampleEntityCategories == null);
		rule.getIPF().fit();
		
		List<AttributeValuesFrequency> selectionProbabilities = rule.getIPF().getSelectionProbabilitiesOfLastIPFIteration();
		Deencapsulation.setField(rule, "selectionProbabilities", selectionProbabilities);
		
		Deencapsulation.invoke(rule, "buildSampleEntityCategories");
		
		sampleEntityCategories = Deencapsulation.getField(rule, "sampleEntityCategories");
		assertTrue(sampleEntityCategories.size() == selectionProbabilities.size());
		
		for (AttributeValuesFrequency avf : sampleEntityCategories.keySet()) {
			assertTrue(selectionProbabilities.contains(avf));
		}
	}


	@Test public void testMaxIterationIsSetCorrectly() throws GenstarException {
		
		ISingleRuleGenerator generator = new SingleRuleGenerator("generator"); 
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCSVFile sampleDataFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/people_sample.csv", true);
		GenstarCSVFile controlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/controlled_attributes.csv", false);
		GenstarCSVFile controlsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/control_totals.csv", false);
		GenstarCSVFile supplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/supplementary_attributes.csv", false);
		
		SampleDataGenerationRule rule = new SampleDataGenerationRule(generator, "sample data generation rule",
				controlledAttributesFile, controlsFile, supplementaryAttributesFile, SampleDataGenerationRule.DEFAULT_MAX_ITERATIONS);

		assertTrue(rule.getMaxIterations() == SampleDataGenerationRule.DEFAULT_MAX_ITERATIONS);
		
		rule.setMaxIterations(5);
		assertTrue(rule.getMaxIterations() == 5);
	}
}
