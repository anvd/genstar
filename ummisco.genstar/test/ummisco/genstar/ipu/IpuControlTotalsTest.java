package ummisco.genstar.ipu;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

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
public class IpuControlTotalsTest {

	@Test public void testGetAvFrequencies(@Mocked final IpuGenerationRule generationRule) throws GenstarException {
		
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/IpuControlTotals/group_attributes.csv", true);
		final ISyntheticPopulationGenerator groupGenerator = new SingleRuleGenerator("Group generator");
		AttributeUtils.createAttributesFromCSVFile(groupGenerator, groupAttributesFile);
		
		// Household Size, Household Income, Number Of Cars
		final List<AbstractAttribute> groupControlledAttributes = new ArrayList<AbstractAttribute>();
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Household Size"));
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Household Income"));
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Number Of Cars"));
		
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/IpuControlTotals/component_attributes.csv", true);
		final ISyntheticPopulationGenerator componentGenerator = new SingleRuleGenerator("Component generator");
		AttributeUtils.createAttributesFromCSVFile(componentGenerator, componentAttributesFile);
		
		final GenstarCSVFile groupControlTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/IpuControlTotals/group_ipu_control_totals.csv", false);
		
		// Age, Gender
		final List<AbstractAttribute> componentControlledAttributes = new ArrayList<AbstractAttribute>();
		componentControlledAttributes.add(componentGenerator.getAttributeByNameOnData("Age"));
		componentControlledAttributes.add(componentGenerator.getAttributeByNameOnData("Gender"));
		
		final GenstarCSVFile componentControlTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/IpuControlTotals/component_ipu_control_totals.csv", false);
		
		new Expectations() {{
			generationRule.getGenerator(); result = groupGenerator;
			generationRule.getGroupControlledAttributes(); result = groupControlledAttributes;
			generationRule.getGroupControlTotalsFile(); result = groupControlTotalsFile;
			
			generationRule.getComponentGenerator(); result = componentGenerator;
			generationRule.getComponentControlledAttributes(); result = componentControlledAttributes;
			generationRule.getComponentControlTotalsFile(); result = componentControlTotalsFile;
		}};
		
		IpuControlTotals ipuControlTotals = new IpuControlTotals(generationRule);
		
		assertTrue(ipuControlTotals.getGroupTypes() == 24);
		assertTrue(ipuControlTotals.getGroupAttributesFrequencies().size() == 24);

		assertTrue(ipuControlTotals.getComponentTypes() == 18);
		assertTrue(ipuControlTotals.getComponentAttributesFrequencies().size() == 18);
	}
}
