package ummisco.genstar.ipf;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mockit.Deencapsulation;

import org.junit.Test;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValuesFrequency;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SyntheticPopulation;
import ummisco.genstar.metamodel.SyntheticPopulationGenerator;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarFactoryUtils;

public class SampleDataGenerationRuleTest {

	@Test public void initializeSampleDataGenerationRuleSuccessfully() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("generator", 100); 
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCSVFile sampleDataFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/people_sample.csv", true);
		GenstarCSVFile controlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/controlled_attributes.csv", false);
		GenstarCSVFile controlsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/control_totals.csv", false);
		GenstarCSVFile supplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/supplementary_attributes.csv", false);
		
		SampleDataGenerationRule rule = new SampleDataGenerationRule(generator, "sample data generation rule", sampleDataFile,
				controlledAttributesFile, controlsFile, supplementaryAttributesFile);
		assertTrue(rule.getSampleData() != null);
		assertTrue(rule.getControlledAttributes().size() == 3);
		assertTrue(rule.getSupplementaryAttributes().size() == 1);
		assertTrue(rule.getControlTotals() != null);
	}
	
	@Test public void testGenerate() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("generator"); 
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCSVFile sampleDataFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/people_sample.csv", true);
		GenstarCSVFile controlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/controlled_attributes.csv", false);
		GenstarCSVFile controlsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/control_totals.csv", false);
		GenstarCSVFile supplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/supplementary_attributes.csv", false);
		
		SampleDataGenerationRule rule = new SampleDataGenerationRule(generator, "sample data generation rule", sampleDataFile,
				controlledAttributesFile, controlsFile, supplementaryAttributesFile);
		generator.setNbOfEntities(rule.getIPF().getNbOfEntitiesToGenerate());
		
		SyntheticPopulation population = new SyntheticPopulation(generator, "people population", rule.getIPF().getNbOfEntitiesToGenerate());
		for (Entity entity : population.getEntities()) { rule.generate(entity); }
		
		List<AttributeValuesFrequency> originalSelectionProbabilities = Deencapsulation.getField(rule, "selectionProbabilities");
		List<AttributeValuesFrequency> copySelectionProbabilities = new ArrayList<AttributeValuesFrequency>();
		for (AttributeValuesFrequency origin : originalSelectionProbabilities) {
			copySelectionProbabilities.add(new AttributeValuesFrequency(origin.getAttributeValues(), origin.getFrequency()));
		}
		
		// verify that the number of generated entities is correct
		assertTrue(population.getEntities().size() == rule.getIPF().getNbOfEntitiesToGenerate());
		
		// verify that the generated entities "conform" to the "selectionProbabilities"
		List<AbstractAttribute> controlledAttributes = rule.getControlledAttributes();
		for (Entity entity : population.getEntities()) {
			for (AttributeValuesFrequency copy : copySelectionProbabilities) {
				if (copy.isMatchEntity(controlledAttributes, entity)) {
					copy.setFrequency(copy.getFrequency() - 1);
					break;
				}
			}
		}
		
		for (AttributeValuesFrequency copy : copySelectionProbabilities) {
			assertTrue(copy.getFrequency() == 0);
		}
	}
	
	@Test public void testBuildSampleEntityCategories() throws GenstarException {
		
		ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("generator"); 
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCSVFile sampleDataFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/people_sample.csv", true);
		GenstarCSVFile controlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/controlled_attributes.csv", false);
		GenstarCSVFile controlsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/control_totals.csv", false);
		GenstarCSVFile supplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_data_generation_rule_data/supplementary_attributes.csv", false);
		
		SampleDataGenerationRule rule = new SampleDataGenerationRule(generator, "sample data generation rule", sampleDataFile,
				controlledAttributesFile, controlsFile, supplementaryAttributesFile);
		
		// 1. verify sampleEntityCategories.size()
		Map<AttributeValuesFrequency, List<SampleEntity>> sampleEntityCategories = Deencapsulation.getField(rule, "sampleEntityCategories");
		assertTrue(sampleEntityCategories == null);
		rule.getIPF().fit();
		
		List<AttributeValuesFrequency> selectionProbabilities = rule.getIPF().getSelectionProbabilities();
		Deencapsulation.setField(rule, "selectionProbabilities", selectionProbabilities);
		
		Deencapsulation.invoke(rule, "buildSampleEntityCategories");
		
		sampleEntityCategories = Deencapsulation.getField(rule, "sampleEntityCategories");
		assertTrue(sampleEntityCategories.size() == selectionProbabilities.size());
		
		for (AttributeValuesFrequency avf : sampleEntityCategories.keySet()) {
			assertTrue(selectionProbabilities.contains(avf));
		}
	}
}
