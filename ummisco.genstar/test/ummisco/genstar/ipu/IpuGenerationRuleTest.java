package ummisco.genstar.ipu;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.GroupComponentSampleData;
import ummisco.genstar.ipf.ISampleData;
import ummisco.genstar.ipf.SampleData;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.IPopulation;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.Population;
import ummisco.genstar.metamodel.PopulationType;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;
import mockit.Deencapsulation;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class IpuGenerationRuleTest {

	static ISingleRuleGenerator groupPopulationGenerator;
	static GenstarCsvFile groupAttributesFile;
	
	static ISingleRuleGenerator componentPopulationGenerator;
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
		
		groupPopulationGenerator = new SingleRuleGenerator("group generator");
		groupAttributesFile = new GenstarCsvFile(base_path + "group_attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(groupPopulationGenerator, groupAttributesFile);
		
		componentPopulationGenerator = new SingleRuleGenerator("component generator");
		componentAttributesFile = new GenstarCsvFile(base_path + "component_attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(componentPopulationGenerator, componentAttributesFile);

		groupControlledAttributesFile = new GenstarCsvFile(base_path + "group_controlled_attributes.csv", false);
		groupControlTotalsFile = new GenstarCsvFile(base_path + "group_ipu_control_totals.csv", false);
		groupSupplementaryAttributesFile = new GenstarCsvFile(base_path + "group_supplementary_attributes.csv", false);
		
		componentControlledAttributesFile = new GenstarCsvFile(base_path + "component_controlled_attributes.csv", false);
		componentControlTotalsFile = new GenstarCsvFile(base_path + "component_ipu_control_totals.csv", false);
		componentSupplementaryAttributesFile = new GenstarCsvFile(base_path + "component_supplementary_attributes.csv", false);
		
		sharedRule = new IpuGenerationRule(groupPopulationGenerator, componentPopulationGenerator, "Ipu generation rule", 
				groupControlledAttributesFile, groupControlTotalsFile, groupSupplementaryAttributesFile,
				componentControlledAttributesFile, componentControlTotalsFile, componentSupplementaryAttributesFile, 3);
	}

	@Test public void testInitializeIpuGenerationRuleSuccessfully() throws GenstarException {
		IpuGenerationRule rule = new IpuGenerationRule(groupPopulationGenerator, componentPopulationGenerator, "Ipu generation rule", 
				groupControlledAttributesFile, groupControlTotalsFile, groupSupplementaryAttributesFile,
				componentControlledAttributesFile, componentControlTotalsFile, componentSupplementaryAttributesFile, 3);
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
	
	@Test public void testIpuGenerationRuleWithNullComponentPopulationGenerator() throws GenstarException {
		fail("not yet implemented");
	}
	
	
	@Test public void testSetSampleData() throws GenstarException {
		IpuGenerationRule rule = new IpuGenerationRule(groupPopulationGenerator, componentPopulationGenerator, "Ipu generation rule", 
				groupControlledAttributesFile, groupControlTotalsFile, groupSupplementaryAttributesFile,
				componentControlledAttributesFile, componentControlTotalsFile, componentSupplementaryAttributesFile, 3);
		
		
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
		
		final GroupComponentSampleData sampleData = new GroupComponentSampleData(groupSample, componentSample, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity);
		 
		
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
		IpuGenerationRule rule = new IpuGenerationRule(groupPopulationGenerator, componentPopulationGenerator, "Ipu generation rule", 
				groupControlledAttributesFile, groupControlTotalsFile, groupSupplementaryAttributesFile,
				componentControlledAttributesFile, componentControlTotalsFile, componentSupplementaryAttributesFile, 3);
		
		
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
		
		final GroupComponentSampleData sampleData = new GroupComponentSampleData(groupSample, componentSample, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity);
		 
		rule.setSampleData(sampleData);
		
		// verifications 0
		boolean ipuRun = Deencapsulation.getField(rule, "ipuRun");
		assertFalse(ipuRun);
		
		List<Entity> internalSampleEntities = Deencapsulation.getField(rule, "internalSampleEntities");
		assertNull(internalSampleEntities);
		
		int currentEntityIndex = Deencapsulation.getField(rule, "currentEntityIndex");
		assertTrue(currentEntityIndex == -1);
		
		
		IPopulation population = new Population(PopulationType.SYNTHETIC_POPULATION, "dummy population", rule.getGenerator().getAttributes());
		Entity e = population.createEntities(1).get(0);
		rule.generate(e);
		
		
		// verifications 1
		ipuRun = Deencapsulation.getField(rule, "ipuRun");
		assertTrue(ipuRun);
		
		internalSampleEntities = Deencapsulation.getField(rule, "internalSampleEntities");
		
		Ipu ipu = Deencapsulation.getField(rule, "ipu");
		Map<Entity, Integer> selectionProbabilities = ipu.getSelectionProbabilities();
		int totalGeneratedEntities = 0;
		for (Integer i : selectionProbabilities.values()) { totalGeneratedEntities += i; }
		
		assertTrue(internalSampleEntities.size() == totalGeneratedEntities);
	}
}
