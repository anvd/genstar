package ummisco.genstar.ipu;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SampleBasedGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class IpuControlTotalsTest {

	@Test public void testGetTypeConstraints(@Mocked final IpuGenerationRule generationRule) throws GenstarException {
		
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/IpuControlTotals/group_attributes.csv", true);
		final ISyntheticPopulationGenerator groupGenerator = new SampleBasedGenerator("Group generator");
		AttributeUtils.createAttributesFromCSVFile(groupGenerator, groupAttributesFile);
		
		// Household Size, Household Income, Number Of Cars
		final List<AbstractAttribute> groupControlledAttributes = new ArrayList<AbstractAttribute>();
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Household Size"));
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Household Income"));
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Number Of Cars"));
		
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/IpuControlTotals/component_attributes.csv", true);
		final ISyntheticPopulationGenerator componentGenerator = new SampleBasedGenerator("Component generator");
		AttributeUtils.createAttributesFromCSVFile(componentGenerator, componentAttributesFile);
		
		final GenstarCsvFile groupControlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/IpuControlTotals/group_ipu_control_totals.csv", false);
		
		// Age, Gender
		final List<AbstractAttribute> componentControlledAttributes = new ArrayList<AbstractAttribute>();
		componentControlledAttributes.add(componentGenerator.getAttributeByNameOnData("Age"));
		componentControlledAttributes.add(componentGenerator.getAttributeByNameOnData("Gender"));
		
		final GenstarCsvFile componentControlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/IpuControlTotals/component_ipu_control_totals.csv", false);
		
		new Expectations() {{
//			generationRule.getGenerator(); result = groupGenerator;
			generationRule.getGroupControlledAttributes(); result = groupControlledAttributes;
			generationRule.getGroupControlTotalsFile(); result = groupControlTotalsFile;
			
//			generationRule.getComponentGenerator(); result = componentGenerator;
			generationRule.getComponentControlledAttributes(); result = componentControlledAttributes;
			generationRule.getComponentControlTotalsFile(); result = componentControlTotalsFile;
		}};
		
		IpuControlTotals ipuControlTotals = new IpuControlTotals(generationRule);
		
		assertTrue(ipuControlTotals.getGroupTypes() == 24);
		assertTrue(ipuControlTotals.getGroupTypeConstraints().size() == 24);

		assertTrue(ipuControlTotals.getComponentTypes() == 18);
		assertTrue(ipuControlTotals.getComponentTypeConstraints().size() == 18);
	}
}
