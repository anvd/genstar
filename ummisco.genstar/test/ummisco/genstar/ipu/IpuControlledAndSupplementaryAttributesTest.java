package ummisco.genstar.ipu;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCSVFile;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class IpuControlledAndSupplementaryAttributesTest {
	
	GenstarCSVFile groupAttributesFile;
	GenstarCSVFile groupControlledAttributesFile;
	GenstarCSVFile groupSupplementaryAttributesFile;
	
	GenstarCSVFile componentAttributesFile;
	GenstarCSVFile componentControlledAttributesFile;
	GenstarCSVFile componentSupplementaryAttributesFile;

	@Before public void setup() throws GenstarException {
		groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/IpuControlledAndSupplementaryAttributes/group_attributes.csv", true);
		groupControlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/IpuControlledAndSupplementaryAttributes/group_controlled_attributes.csv", false);
		groupSupplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/IpuControlledAndSupplementaryAttributes/group_supplementary_attributes.csv", false);
		
		componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/IpuControlledAndSupplementaryAttributes/component_attributes.csv", true);
		componentControlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/IpuControlledAndSupplementaryAttributes/component_controlled_attributes.csv", false);
		componentSupplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/IpuControlledAndSupplementaryAttributes/component_supplementary_attributes.csv", false);
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
		
		final ISyntheticPopulationGenerator groupGenerator = new SingleRuleGenerator("group generator");
		AttributeUtils.createAttributesFromCSVFile(groupGenerator, groupAttributesFile);
		
		final ISyntheticPopulationGenerator componentGenerator = new SingleRuleGenerator("component generator");
		AttributeUtils.createAttributesFromCSVFile(componentGenerator, componentAttributesFile);
		
		
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
