package ummisco.genstar.gama;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.GroupComponentSampleData;
import ummisco.genstar.ipf.SampleData;
import ummisco.genstar.ipf.SampleDataGenerationRule;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarFactoryUtils;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import msi.gama.common.util.FileUtils;
import msi.gama.runtime.IScope;

@RunWith(JMockit.class)
public class GamaGenstarFactoryUtilsTest {

	@Test public void testCreateGroupComponentSampleDataGenerationRule(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
		/*
		POPULATION_NAME=household
		ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/group_attributes.csv
		SAMPLE_DATA=test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/group_sample.csv
		CONTROLLED_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/group_controlled_attributes.csv
		CONTROLLED_TOTALS=test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/group_control_totals.csv
		SUPPLEMENTARY_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/group_supplementary_attributes.csv
		COMPONENT_POPULATION_NAME=people
		COMPONENT_SAMPLE_DATA=test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/component_sample.csv
		COMPONENT_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/component_attributes.csv
		GROUP_ID_ATTRIBUTE_ON_GROUP=HouseholdID
		GROUP_ID_ATTRIBUTE_ON_COMPONENT=HouseholdID
		COMPONENT_REFERENCE_ON_GROUP=inhabitans
		GROUP_REFERENCE_ON_COMPONENT=my_household
		 * 
		 */
		
		final String groupAttributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/group_attributes.csv";
		final String groupSampleFilePath = "test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/group_sample.csv";
		final String controlledAttributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/group_controlled_attributes.csv";
		final String controlTotalsFilePath = "test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/group_control_totals.csv";
		final String supplementaryAttributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/group_supplementary_attributes.csv";
		
		final String componentSampleFilePath = "test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/component_sample.csv";
		final String componentAttributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/component_attributes.csv";
		
		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("/group_attributes.csv")) { return groupAttributesFilePath; }
					if (filePath.endsWith("/group_sample.csv")) { return groupSampleFilePath; }
					if (filePath.endsWith("/group_controlled_attributes.csv")) { return controlledAttributesFilePath; }
					if (filePath.endsWith("/group_control_totals.csv")) { return controlTotalsFilePath; }
					if (filePath.endsWith("/group_supplementary_attributes.csv")) { return supplementaryAttributesFilePath; }
					
					if (filePath.endsWith("/component_sample.csv")) { return componentSampleFilePath; }
					if (filePath.endsWith("/component_attributes.csv")) { return componentAttributesFilePath; }
					
					return null;
				}
			};
		}};
		 
		
		String ruleName = "dummy rule";
		
		Properties sampleDataProperties = null;
		File sampleDataPropertyFile = new File("test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/group_component/SampleDataProperties.properties");
		try {
			FileInputStream propertyInputStream = new FileInputStream(sampleDataPropertyFile);
			sampleDataProperties = new Properties();
			sampleDataProperties.load(propertyInputStream);
		} catch (FileNotFoundException e) {
			throw new GenstarException(e);
		} catch (IOException e) {
			throw new GenstarException(e);
		}
		
		
		ISingleRuleGenerator generator = new SingleRuleGenerator("dummy generator");
		
		GenstarCSVFile attributesFile = new GenstarCSVFile(groupAttributesFilePath, true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		GamaGenstarFactoryUtils.createSampleDataGenerationRule(scope, generator, ruleName, sampleDataProperties);
		
		assertTrue(generator.getGenerationRule() instanceof SampleDataGenerationRule);

		SampleDataGenerationRule generationRule = (SampleDataGenerationRule) generator.getGenerationRule();
		assertTrue(generationRule.getSampleData() instanceof GroupComponentSampleData);
	}
	
	
	@Test public void testCreateSampleDataGenerationRule(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
		/*
		POPULATION_NAME=household
		ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/sample_data/attributes.csv
		ID_ATTRIBUTE=HouseholdID
		SAMPLE_DATA=test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/sample_data/sample_data.csv
		CONTROLLED_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/sample_data/controlled_attributes.csv
		CONTROLLED_TOTALS=test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/sample_data/control_totals.csv
		SUPPLEMENTARY_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/sample_data/supplementary_attributes.csv
		 */
		
		
		final String attributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/sample_data/attributes.csv";
		final String sampleFilePath = "test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/sample_data/sample_data.csv";
		final String controlledAttributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/sample_data/controlled_attributes.csv";
		final String controlTotalsFilePath = "test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/sample_data/control_totals.csv";
		final String supplementaryAttributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/sample_data/supplementary_attributes.csv";
		 
		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("/attributes.csv")) { return attributesFilePath; }
					if (filePath.endsWith("/sample_data.csv")) { return sampleFilePath; }
					if (filePath.endsWith("/controlled_attributes.csv")) { return controlledAttributesFilePath; }
					if (filePath.endsWith("/control_totals.csv")) { return controlTotalsFilePath; }
					if (filePath.endsWith("/supplementary_attributes.csv")) { return supplementaryAttributesFilePath; }
					
					return null;
				}
			};
		}};
		
		
		String ruleName = "dummy rule";
		
		Properties sampleDataProperties = null;
		File sampleDataPropertyFile = new File("test_data/ummisco/genstar/gama/GamaGenstarFactoryUtilsTest/testCreateSampleDataGenerationRule/sample_data/SampleDataProperties.properties");
		try {
			FileInputStream propertyInputStream = new FileInputStream(sampleDataPropertyFile);
			sampleDataProperties = new Properties();
			sampleDataProperties.load(propertyInputStream);
		} catch (FileNotFoundException e) {
			throw new GenstarException(e);
		} catch (IOException e) {
			throw new GenstarException(e);
		}

		
		ISingleRuleGenerator generator = new SingleRuleGenerator("dummy generator");
		
		GenstarCSVFile attributesFile = new GenstarCSVFile(attributesFilePath, true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		GamaGenstarFactoryUtils.createSampleDataGenerationRule(scope, generator, ruleName, sampleDataProperties);
		
		assertTrue(generator.getGenerationRule() instanceof SampleDataGenerationRule);

		SampleDataGenerationRule generationRule = (SampleDataGenerationRule) generator.getGenerationRule();
		assertTrue(generationRule.getSampleData() instanceof SampleData);
		
	}
	
	
	@Test public void testCreateGenerationRulesFromCSVFile() {
		fail("not yet implemented");
	}
}
