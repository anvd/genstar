package ummisco.genstar.ipu;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class IpuControlledAndSupplementaryAttributesTest {
	
	GenstarCsvFile groupAttributesFile;
	GenstarCsvFile groupControlledAttributesFile;
	GenstarCsvFile groupSupplementaryAttributesFile;
	
	GenstarCsvFile componentAttributesFile;
	GenstarCsvFile componentControlledAttributesFile;
	GenstarCsvFile componentSupplementaryAttributesFile;

	@Before public void setup() throws GenstarException {
		groupAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/IpuControlledAndSupplementaryAttributes/group_attributes.csv", true);
		groupControlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/IpuControlledAndSupplementaryAttributes/group_controlled_attributes.csv", false);
		groupSupplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/IpuControlledAndSupplementaryAttributes/group_supplementary_attributes.csv", false);
		
		componentAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/IpuControlledAndSupplementaryAttributes/component_attributes.csv", true);
		componentControlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/IpuControlledAndSupplementaryAttributes/component_controlled_attributes.csv", false);
		componentSupplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/IpuControlledAndSupplementaryAttributes/component_supplementary_attributes.csv", false);
	}

	@Test(expected = GenstarException.class) public void testInitializeIpuControlledAndSupplementaryAttributesWithNullGenerationRule() throws GenstarException {
		new IpuControlledAndSupplementaryAttributes(null);
	}
	
	@Test public void testInitializeIpuControlledAndSupplementaryAttributesSuccessfully(@Mocked final IpuGenerationRule generationRule) throws GenstarException {

		new Expectations() {{
			generationRule.getGroupControlledAttributesFile(); result = groupControlledAttributesFile;
			generationRule.getGroupSupplementaryAttributesFile(); result = groupSupplementaryAttributesFile;
			
			generationRule.getComponentControlledAttributesFile(); result = componentControlledAttributesFile;
			generationRule.getComponentSupplementaryAttributesFile(); result = componentSupplementaryAttributesFile;
		}};
		
		new IpuControlledAndSupplementaryAttributes(generationRule);
	}
	
	@Test public void testGetGroupAndComponentControlledAttributes(@Mocked final IpuGenerationRule generationRule) throws GenstarException {
		
		final ISyntheticPopulationGenerator groupGenerator = new SampleBasedGenerator("group generator");
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesFile);
		
		final ISyntheticPopulationGenerator componentGenerator = new SampleBasedGenerator("component generator");
		AttributeUtils.createAttributesFromCsvFile(componentGenerator, componentAttributesFile);
		
		
		new Expectations() {{
			generationRule.getGroupControlledAttributesFile(); result = groupControlledAttributesFile;
			generationRule.getGroupSupplementaryAttributesFile(); result = groupSupplementaryAttributesFile;
			
			generationRule.getComponentControlledAttributesFile(); result = componentControlledAttributesFile;
			generationRule.getComponentSupplementaryAttributesFile(); result = componentSupplementaryAttributesFile;
			
			generationRule.getGenerator(); result = groupGenerator;
			generationRule.getComponentGenerator(); result = componentGenerator;
		}};
		
		IpuControlledAndSupplementaryAttributes ipuAttributes = new IpuControlledAndSupplementaryAttributes(generationRule);
		
		List<AbstractAttribute> groupControlledAttributes = ipuAttributes.getGroupControlledAttributes();
		assertTrue(groupControlledAttributes.size() == 3);
		assertTrue(groupControlledAttributes.get(0).getNameOnData().equals("Household Size"));
		assertTrue(groupControlledAttributes.get(1).getNameOnData().equals("Household Income"));
		assertTrue(groupControlledAttributes.get(2).getNameOnData().equals("Number Of Cars"));
		
		List<AbstractAttribute> componentControlledAttributes = ipuAttributes.getComponentControlledAttributes();
		assertTrue(componentControlledAttributes.size() == 3);
		assertTrue(componentControlledAttributes.get(0).getNameOnData().equals("Age"));
		assertTrue(componentControlledAttributes.get(1).getNameOnData().equals("Gender"));
		assertTrue(componentControlledAttributes.get(2).getNameOnData().equals("Work"));
	}
}
