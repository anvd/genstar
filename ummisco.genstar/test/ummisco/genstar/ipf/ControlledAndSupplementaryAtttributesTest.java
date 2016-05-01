package ummisco.genstar.ipf;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;

@RunWith(JMockit.class)
public class ControlledAndSupplementaryAtttributesTest {
	
	private static ISyntheticPopulationGenerator generator = null;
	static {
		try {
			generator = new SingleRuleGenerator("generator");
			GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/controlled_supplementary_attributes_data/attributes.csv", true);
			AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		} catch (final GenstarException e) {
			e.printStackTrace();
		} 
	}
	
	@Test public void testInitializeObjectSuccessfully(@Mocked final SampleDataGenerationRule generationRule) throws GenstarException {
		final GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/controlled_supplementary_attributes_data/controlled_attributes1.csv", false);
		final GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/controlled_supplementary_attributes_data/supplementary_attributes1.csv", false);
		
		new Expectations() {{
			generationRule.getGenerator(); result = generator;
			generationRule.getControlledAttributesFile(); result = controlledAttributesFile;
			generationRule.getSupplementaryAttributesFile(); result = supplementaryAttributesFile;
		}};
		
		ControlledAndSupplementaryAttributes tested = new ControlledAndSupplementaryAttributes(generationRule);
		
		List<AbstractAttribute> controlledAttributes = tested.getControlledAttributes();
		List<AbstractAttribute> supplementaryAttributes = tested.getSupplementaryAttributes();
		
		assertTrue(controlledAttributes.size() == 2);
		assertTrue(supplementaryAttributes.size() == 2);
		
		List<String> controlledAttrNames = new ArrayList<String>();
		for (List<String> row : controlledAttributesFile.getContent()) { controlledAttrNames.add(row.get(0)); }
		for (AbstractAttribute cAttr : controlledAttributes) { controlledAttrNames.contains(cAttr.getNameOnData()); }
		
		List<String> supplementaryAttrNames = new ArrayList<String>();
		for (List<String> row : supplementaryAttributesFile.getContent()) { supplementaryAttrNames.add(row.get(0)); }
		for (AbstractAttribute sAttr : supplementaryAttributes) { supplementaryAttrNames.contains(sAttr.getNameOnData()); }
	}
	
	@Test(expected = GenstarException.class) 
	public void testSupplementaryFileContainsControlledAttr(@Mocked final SampleDataGenerationRule generationRule) throws GenstarException {
		final GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/controlled_supplementary_attributes_data/controlled_attributes2.csv", false);
		final GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/controlled_supplementary_attributes_data/supplementary_attributes2.csv", false);
		
		new Expectations() {{
			generationRule.getGenerator(); result = generator;
			generationRule.getControlledAttributesFile(); result = controlledAttributesFile;
			generationRule.getSupplementaryAttributesFile(); result = supplementaryAttributesFile;
		}};
		
		ControlledAndSupplementaryAttributes tested = new ControlledAndSupplementaryAttributes(generationRule);
	}
	
	@Test(expected = GenstarException.class) public void testEmptyControlledAttributesFile(@Mocked final SampleDataGenerationRule generationRule) throws GenstarException {
		final GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/controlled_supplementary_attributes_data/controlled_attributes3.csv", false);
		final GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/controlled_supplementary_attributes_data/supplementary_attributes3.csv", false);
		
		new Expectations() {{
			generationRule.getControlledAttributesFile(); result = controlledAttributesFile;
			generationRule.getSupplementaryAttributesFile(); result = supplementaryAttributesFile;
		}};
		
		ControlledAndSupplementaryAttributes tested = new ControlledAndSupplementaryAttributes(generationRule);
	}
	
	@Test public void testEmptySupplementaryAttributesFile(@Mocked final SampleDataGenerationRule generationRule) throws GenstarException {
		final GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/controlled_supplementary_attributes_data/controlled_attributes1.csv", false);
		final GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/controlled_supplementary_attributes_data/supplementary_attributes3.csv", false);
		
		new Expectations() {{
			generationRule.getGenerator(); result = generator;
			generationRule.getControlledAttributesFile(); result = controlledAttributesFile;
			generationRule.getSupplementaryAttributesFile(); result = supplementaryAttributesFile;
		}};
		
		ControlledAndSupplementaryAttributes tested = new ControlledAndSupplementaryAttributes(generationRule);
		assertTrue(tested.getSupplementaryAttributes().isEmpty());
	}
	
	@Test(expected = GenstarException.class) public void testControlledAttributesFileContainsInvalidAttr(@Mocked final SampleDataGenerationRule generationRule) throws GenstarException {
		final GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/controlled_supplementary_attributes_data/controlled_attributes4.csv", false);
		final GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/controlled_supplementary_attributes_data/supplementary_attributes3.csv", false);
		
		new Expectations() {{
			generationRule.getGenerator(); result = generator;
			generationRule.getControlledAttributesFile(); result = controlledAttributesFile;
			generationRule.getSupplementaryAttributesFile(); result = supplementaryAttributesFile;
		}};
		
		ControlledAndSupplementaryAttributes tested = new ControlledAndSupplementaryAttributes(generationRule);
	}
	
	@Test(expected = GenstarException.class) public void testSupplementaryAttributesFileContainsInvalidAttr(@Mocked final SampleDataGenerationRule generationRule) throws GenstarException {
		final GenstarCsvFile controlledAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/controlled_supplementary_attributes_data/controlled_attributes1.csv", false);
		final GenstarCsvFile supplementaryAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/controlled_supplementary_attributes_data/supplementary_attributes4.csv", false);
		
		new Expectations() {{
			generationRule.getGenerator(); result = generator;
			generationRule.getControlledAttributesFile(); result = controlledAttributesFile;
			generationRule.getSupplementaryAttributesFile(); result = supplementaryAttributesFile;
		}};
		
		ControlledAndSupplementaryAttributes tested = new ControlledAndSupplementaryAttributes(generationRule);
	}
	
}
