package ummisco.genstar.ipf;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mockit.Deencapsulation;

import org.junit.Test;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.IPopulation;
import ummisco.genstar.metamodel.SampleBasedGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.sample_data.ISampleData;
import ummisco.genstar.metamodel.sample_data.SampleData;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;

public class IpfGenerationRuleTest {

	@Test public void initializeSampleDataGenerationRuleSuccessfully() throws GenstarException {
		SampleBasedGenerator generator = new SampleBasedGenerator("generator"); 
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCsvFile sampleDataFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/people_sample.csv", true);
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/controlled_attributes.csv", false);
		GenstarCsvFile controlsFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/control_totals.csv", false);
		GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/supplementary_attributes.csv", false);
		
		IpfGenerationRule rule = new IpfGenerationRule(generator, "sample data generation rule",
				controlledAttributesFile, controlsFile, supplementaryAttributesFile, IpfGenerationRule.DEFAULT_MAX_ITERATIONS);
		ISampleData sampleData = new SampleData("people", generator.getAttributes(), sampleDataFile);
		rule.setSampleData(sampleData);
		
		assertTrue(rule.getSampleData() != null);
		assertTrue(rule.getControlledAttributes().size() == 3);
		assertTrue(rule.getSupplementaryAttributes().size() == 1);
		assertTrue(rule.getControlTotals() != null);
	}
	
	@Test public void testGenerate() throws GenstarException {
		SampleBasedGenerator generator = new SampleBasedGenerator("generator"); 
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCsvFile sampleDataFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/people_sample.csv", true);
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/controlled_attributes.csv", false);
		GenstarCsvFile controlsFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/control_totals.csv", false);
		GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/supplementary_attributes.csv", false);
		
		IpfGenerationRule rule = new IpfGenerationRule(generator, "sample data generation rule", 
				controlledAttributesFile, controlsFile, supplementaryAttributesFile, IpfGenerationRule.DEFAULT_MAX_ITERATIONS);
		ISampleData sampleData = new SampleData("people", generator.getAttributes(), sampleDataFile);
		rule.setSampleData(sampleData);
		generator.setNbOfEntities(rule.getIPF().getNbOfEntitiesToGenerate());
		
		IPopulation population = rule.generate();
		
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
	
	}
	
	/*
	@Test(expected = Exception.class) public void testGenerateFailed() throws GenstarException {
		SampleBasedGenerator generator = new SampleBasedGenerator("generator"); 
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCsvFile sampleDataFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/people_sample.csv", true);
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/controlled_attributes.csv", false);
		GenstarCsvFile controlsFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/control_totals.csv", false);
		GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/supplementary_attributes.csv", false);
		
		IpfGenerationRule rule = new IpfGenerationRule(generator, "sample data generation rule",
				controlledAttributesFile, controlsFile, supplementaryAttributesFile, IpfGenerationRule.DEFAULT_MAX_ITERATIONS);
		ISampleData sampleData = new SampleData("people", generator.getAttributes(), sampleDataFile);
		rule.setSampleData(sampleData);
		generator.setNbOfEntities(rule.getIPF().getNbOfEntitiesToGenerate());
		
		rule.generate();
	}
	*/
	
	@Test public void testBuildSampleEntityCategories() throws GenstarException {
		
		SampleBasedGenerator generator = new SampleBasedGenerator("generator"); 
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCsvFile sampleDataFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/people_sample.csv", true);
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/controlled_attributes.csv", false);
		GenstarCsvFile controlsFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/control_totals.csv", false);
		GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/supplementary_attributes.csv", false);
		
		IpfGenerationRule rule = new IpfGenerationRule(generator, "sample data generation rule",
				controlledAttributesFile, controlsFile, supplementaryAttributesFile, IpfGenerationRule.DEFAULT_MAX_ITERATIONS);
		ISampleData sampleData = new SampleData("people", generator.getAttributes(), sampleDataFile);
		rule.setSampleData(sampleData);
		
		// 1. verify sampleEntityCategories.size()
		Map<AttributeValuesFrequency, List<Entity>> sampleEntityCategories = Deencapsulation.getField(rule, "sampleEntityCategories");
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
		
		SampleBasedGenerator generator = new SampleBasedGenerator("generator"); 
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCsvFile sampleDataFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/people_sample.csv", true);
		GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/controlled_attributes.csv", false);
		GenstarCsvFile controlsFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/control_totals.csv", false);
		GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/IpfGenerationRule/supplementary_attributes.csv", false);
		
		IpfGenerationRule rule = new IpfGenerationRule(generator, "sample data generation rule",
				controlledAttributesFile, controlsFile, supplementaryAttributesFile, IpfGenerationRule.DEFAULT_MAX_ITERATIONS);

		assertTrue(rule.getMaxIterations() == IpfGenerationRule.DEFAULT_MAX_ITERATIONS);
		
		rule.setMaxIterations(5);
		assertTrue(rule.getMaxIterations() == 5);
	}
}
