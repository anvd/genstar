package ummisco.genstar.ipu;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mockit.Deencapsulation;
import mockit.integration.junit4.JMockit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.sample_data.CompoundSampleData;
import ummisco.genstar.metamodel.sample_data.ISampleData;
import ummisco.genstar.metamodel.sample_data.SampleData;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;

@RunWith(JMockit.class)
public class IpuGenerationRuleTest {

	static SampleBasedGenerator groupPopulationGenerator;
	static GenstarCsvFile groupAttributesFile;
	
	static SampleBasedGenerator componentPopulationGenerator;
	static GenstarCsvFile componentAttributesFile;
	
	static GenstarCsvFile groupControlledAttributesFile;
	static GenstarCsvFile groupControlTotalsFile;
	static GenstarCsvFile groupSupplementaryAttributesFile;
	
	static GenstarCsvFile componentControlledAttributesFile;
	static GenstarCsvFile componentControlTotalsFile;
	static GenstarCsvFile componentSupplementaryAttributesFile;
	
	static IpuGenerationRule sharedRule;
	
	
	@BeforeClass public static void setup() throws GenstarException {
		String base_path = "test_data/ummisco/genstar/ipu/IpuGenerationRule/";
		
		groupPopulationGenerator = new SampleBasedGenerator("group generator");
		groupAttributesFile = new GenstarCsvFile(base_path + "group_attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(groupPopulationGenerator, groupAttributesFile);
		
		componentPopulationGenerator = new SampleBasedGenerator("component generator");
		componentAttributesFile = new GenstarCsvFile(base_path + "component_attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(componentPopulationGenerator, componentAttributesFile);

		groupControlledAttributesFile = new GenstarCsvFile(base_path + "group_controlled_attributes.csv", false);
		groupControlTotalsFile = new GenstarCsvFile(base_path + "group_ipu_control_totals.csv", false);
		groupSupplementaryAttributesFile = new GenstarCsvFile(base_path + "group_supplementary_attributes.csv", false);
		
		componentControlledAttributesFile = new GenstarCsvFile(base_path + "component_controlled_attributes.csv", false);
		componentControlTotalsFile = new GenstarCsvFile(base_path + "component_ipu_control_totals.csv", false);
		componentSupplementaryAttributesFile = new GenstarCsvFile(base_path + "component_supplementary_attributes.csv", false);
		
		sharedRule = new IpuGenerationRule("Ipu generation rule", groupPopulationGenerator, groupControlledAttributesFile, groupControlTotalsFile, groupSupplementaryAttributesFile,
				componentPopulationGenerator, componentControlledAttributesFile, componentControlTotalsFile, componentSupplementaryAttributesFile, 3);
	}

	@Test public void testInitializeIpuGenerationRuleSuccessfully() throws GenstarException {
		IpuGenerationRule rule = new IpuGenerationRule("Ipu generation rule", groupPopulationGenerator, groupControlledAttributesFile, groupControlTotalsFile, groupSupplementaryAttributesFile,
				componentPopulationGenerator, componentControlledAttributesFile, componentControlTotalsFile, componentSupplementaryAttributesFile, 3);
	}
	
	@Test public void testGetAttributes() throws GenstarException {
		List<AbstractAttribute> groupAttributes = sharedRule.getAttributes();
		assertTrue(groupAttributes.size() == 2);
	}
	
	@Test public void testGetGroupControlTotalsFile() throws GenstarException {
		assertTrue(sharedRule.getGroupControlTotalsFile().equals(groupControlTotalsFile));
	}
	
	@Test public void testGetGroupControlledAttributesFile() throws GenstarException {
		assertTrue(sharedRule.getGroupControlledAttributesFile().equals(groupControlledAttributesFile));
	}
	
	@Test public void testGetGroupSupplementaryAttributesFile() throws GenstarException {
		assertTrue(sharedRule.getGroupSupplementaryAttributesFile().equals(groupSupplementaryAttributesFile));
	}
	
	@Test public void testGetComponentControlTotalsFile() throws GenstarException {
		assertTrue(sharedRule.getComponentControlTotalsFile().equals(componentControlTotalsFile));
	}
	
	@Test public void testGetComponentControlledAttributesFile() throws GenstarException {
		assertTrue(sharedRule.getComponentControlledAttributesFile().equals(componentControlledAttributesFile));
	}
	
	@Test public void testGetComponentSupplementaryAttributesFile() throws GenstarException {
		assertTrue(sharedRule.getComponentSupplementaryAttributesFile().equals(componentSupplementaryAttributesFile));
	}
	
	@Test public void testGetComponentControlledAttributes() throws GenstarException {
		assertTrue(sharedRule.getComponentSupplementaryAttributesFile().equals(componentSupplementaryAttributesFile));
	}
	
	@Test(expected = GenstarException.class) public void testIpuGenerationRuleWithNullComponentPopulationGenerator() throws GenstarException {
		new IpuGenerationRule("Ipu generation rule", groupPopulationGenerator, groupControlledAttributesFile, groupControlTotalsFile, groupSupplementaryAttributesFile, 
				null, componentControlledAttributesFile, componentControlTotalsFile, componentSupplementaryAttributesFile, 3);
	}
	
	
	@Test public void testSetSampleData() throws GenstarException {
		IpuGenerationRule rule = new IpuGenerationRule("Ipu generation rule", groupPopulationGenerator, groupControlledAttributesFile, groupControlTotalsFile, groupSupplementaryAttributesFile,
				componentPopulationGenerator, componentControlledAttributesFile, componentControlTotalsFile, componentSupplementaryAttributesFile, 3);
		
		
		// 0. initialize IpuGenerationRule
		String base_path = "test_data/ummisco/genstar/ipu/IpuGenerationRule/";
		
		// group controlled attributes: Household Type
		final List<AbstractAttribute> groupControlledAttributes = new ArrayList<AbstractAttribute>();
		groupControlledAttributes.add(rule.getGenerator().getAttributeByNameOnData("Household Type"));
		
		// component controlled attributes: Person Type
		final List<AbstractAttribute> componentControlledAttributes = new ArrayList<AbstractAttribute>();
		componentControlledAttributes.add(rule.getComponentGenerator().getAttributeByNameOnData("Person Type"));
		
		// 2. initialize sample data
		AbstractAttribute groupIdAttributeOnGroupEntity = rule.getGenerator().getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		GenstarCsvFile groupSampleDataFile = new GenstarCsvFile(base_path + "group_sample.csv", true);
		ISampleData groupSample = new SampleData("household", rule.getGenerator().getAttributes(), groupSampleDataFile);
		
		AbstractAttribute groupIdAttributeOnComponentEntity = rule.getComponentGenerator().getAttributeByNameOnData("Household ID");
		GenstarCsvFile componentSampleFile = new GenstarCsvFile(base_path + "component_sample.csv", true);
		groupIdAttributeOnComponentEntity.setIdentity(true);
		ISampleData componentSample = new SampleData("people", rule.getComponentGenerator().getAttributes(), componentSampleFile);
		
		final CompoundSampleData sampleData = new CompoundSampleData(groupSample, componentSample, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity);
		 
		
		// verifications 0
		assertTrue(rule.getSampleData() == null);
		Ipu ipu = Deencapsulation.getField(rule, "ipu");
		assertTrue(ipu == null);
		

		rule.setSampleData(sampleData);

		
		// verifications 1
		assertTrue(rule.getSampleData().equals(sampleData));
		ipu = Deencapsulation.getField(rule, "ipu");
		assertTrue(ipu != null);
	}
	
	
	@Test public void testGenerate() throws GenstarException {
		IpuGenerationRule rule = new IpuGenerationRule("Ipu generation rule", groupPopulationGenerator, groupControlledAttributesFile, groupControlTotalsFile, groupSupplementaryAttributesFile,
				componentPopulationGenerator, componentControlledAttributesFile, componentControlTotalsFile, componentSupplementaryAttributesFile, 3);
		
		
		// 0. initialize IpuGenerationRule
		String base_path = "test_data/ummisco/genstar/ipu/IpuGenerationRule/";
		
		// group controlled attributes: Household Type
		final List<AbstractAttribute> groupControlledAttributes = new ArrayList<AbstractAttribute>();
		groupControlledAttributes.add(rule.getGenerator().getAttributeByNameOnData("Household Type"));
		
		// component controlled attributes: Person Type
		final List<AbstractAttribute> componentControlledAttributes = new ArrayList<AbstractAttribute>();
		componentControlledAttributes.add(rule.getComponentGenerator().getAttributeByNameOnData("Person Type"));
		
		// 2. initialize sample data
		AbstractAttribute groupIdAttributeOnGroupEntity = rule.getGenerator().getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		GenstarCsvFile groupSampleDataFile = new GenstarCsvFile(base_path + "group_sample.csv", true);
		ISampleData groupSample = new SampleData("household", rule.getGenerator().getAttributes(), groupSampleDataFile);
		
		AbstractAttribute groupIdAttributeOnComponentEntity = rule.getComponentGenerator().getAttributeByNameOnData("Household ID");
		GenstarCsvFile componentSampleFile = new GenstarCsvFile(base_path + "component_sample.csv", true);
		groupIdAttributeOnComponentEntity.setIdentity(true);
		ISampleData componentSample = new SampleData("people", rule.getComponentGenerator().getAttributes(), componentSampleFile);
		
		final CompoundSampleData sampleData = new CompoundSampleData(groupSample, componentSample, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity);
		 
		rule.setSampleData(sampleData);
		
		// verifications 0
		boolean ipuRun = Deencapsulation.getField(rule, "ipuRun");
		assertFalse(ipuRun);
		
		IPopulation population = rule.generate();
		
		// verifications 1
		ipuRun = Deencapsulation.getField(rule, "ipuRun");
		assertTrue(ipuRun);
		
		Ipu ipu = Deencapsulation.getField(rule, "ipu");
		Map<Entity, Integer> selectionProbabilities = ipu.getSelectionProbabilities();
		int totalGeneratedEntities = 0;
		for (Integer i : selectionProbabilities.values()) { totalGeneratedEntities += i; }
		
		assertTrue(population.getNbOfEntities() == totalGeneratedEntities);
		
	}
}
