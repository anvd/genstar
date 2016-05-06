package ummisco.genstar.gama;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import mockit.Deencapsulation;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import msi.gama.common.util.FileUtils;
import msi.gama.runtime.IScope;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.IpfGenerationRule;
import ummisco.genstar.ipu.IpuGenerationRule;
import ummisco.genstar.metamodel.generation_rules.SampleBasedGenerationRule;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.sample_data.CompoundSampleData;
import ummisco.genstar.metamodel.sample_data.SampleData;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;

@RunWith(JMockit.class)
public class GamaGenstarUtilsTest {

	@Test public void testCreateCompoundIpfGenerationRule(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
		/*
			POPULATION_NAME=household
			ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/group_attributes.csv
			SAMPLE_DATA=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/group_sample.csv
			CONTROLLED_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/group_controlled_attributes.csv
			CONTROL_TOTALS=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/group_control_totals.csv
			SUPPLEMENTARY_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/group_supplementary_attributes.csv
			COMPONENT_POPULATION_NAME=people
			COMPONENT_SAMPLE_DATA=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/component_sample.csv
			COMPONENT_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/component_attributes.csv
			GROUP_ID_ATTRIBUTE_ON_GROUP=HouseholdID
			GROUP_ID_ATTRIBUTE_ON_COMPONENT=HouseholdID
			COMPONENT_REFERENCE_ON_GROUP=inhabitans
			GROUP_REFERENCE_ON_COMPONENT=my_household
		 * 
		 */
		
		final String groupAttributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/group_attributes.csv";
		final String groupSampleFilePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/group_sample.csv";
		final String controlledAttributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/group_controlled_attributes.csv";
		final String controlTotalsFilePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/group_control_totals.csv";
		final String supplementaryAttributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/group_supplementary_attributes.csv";
		
		final String componentSampleFilePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/component_sample.csv";
		final String componentAttributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/component_attributes.csv";
		
		
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
		
		Properties ipfPopulationProperties = null;
		File ipfPopulationPropertiesFile = new File("test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateCompoundIpfGenerationRule/IpfPopulationProperties.properties");
		try {
			FileInputStream propertyInputStream = new FileInputStream(ipfPopulationPropertiesFile);
			ipfPopulationProperties = new Properties();
			ipfPopulationProperties.load(propertyInputStream);
		} catch (FileNotFoundException e) {
			throw new GenstarException(e);
		} catch (IOException e) {
			throw new GenstarException(e);
		}
		
		
		SampleBasedGenerator generator = new SampleBasedGenerator("dummy generator");
		
		GenstarCsvFile attributesFile = new GenstarCsvFile(groupAttributesFilePath, true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		GamaGenstarUtils.createIpfGenerationRule(scope, generator, ruleName, ipfPopulationProperties);
		
		assertTrue(generator.getGenerationRule() instanceof IpfGenerationRule);

		IpfGenerationRule generationRule = (IpfGenerationRule) generator.getGenerationRule();
		assertTrue(generationRule.getSampleData() instanceof CompoundSampleData);
	}
	
	
	@Test public void testCreateIpfGenerationRule(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
		/*
			POPULATION_NAME=household
			ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpfGenerationRule/attributes.csv
			ID_ATTRIBUTE=HouseholdID
			SAMPLE_DATA=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpfGenerationRule/sample_data.csv
			CONTROLLED_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpfGenerationRule/controlled_attributes.csv
			CONTROL_TOTALS=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpfGenerationRule/control_totals.csv
			SUPPLEMENTARY_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpfGenerationRule/supplementary_attributes.csv
		 */
		
		
		final String attributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpfGenerationRule/attributes.csv";
		final String sampleFilePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpfGenerationRule/sample_data.csv";
		final String controlledAttributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpfGenerationRule/controlled_attributes.csv";
		final String controlTotalsFilePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpfGenerationRule/control_totals.csv";
		final String supplementaryAttributesFilePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpfGenerationRule/supplementary_attributes.csv";
		 
		
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
		
		Properties ipfPopulationProperties = null;
		File ipfPopulationPropertiesFile = new File("test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpfGenerationRule/IpfPopulationProperties.properties");
		try {
			FileInputStream propertyInputStream = new FileInputStream(ipfPopulationPropertiesFile);
			ipfPopulationProperties = new Properties();
			ipfPopulationProperties.load(propertyInputStream);
		} catch (FileNotFoundException e) {
			throw new GenstarException(e);
		} catch (IOException e) {
			throw new GenstarException(e);
		}

		
		SampleBasedGenerator generator = new SampleBasedGenerator("dummy generator");
		
		GenstarCsvFile attributesFile = new GenstarCsvFile(attributesFilePath, true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		GamaGenstarUtils.createIpfGenerationRule(scope, generator, ruleName, ipfPopulationProperties);
		
		assertTrue(generator.getGenerationRule() instanceof IpfGenerationRule);

		IpfGenerationRule generationRule = (IpfGenerationRule) generator.getGenerationRule();
		assertTrue(generationRule.getSampleData() instanceof SampleData);
	}
	
	
	@Test public void testCreateSampleFreeGenerationRules() {
		fail("not yet implemented");
	}
	
	
	@Test public void testCreateIpuGenerationRule(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
		/*
			GROUP_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpUGenerationRule/group_attributes.csv
			GROUP_POPULATION_NAME=household
			GROUP_SAMPLE_DATA=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpUGenerationRule/group_sample.csv
			GROUP_ID_ATTRIBUTE_ON_GROUP=HouseholdID
			GROUP_CONTROLLED_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpUGenerationRule/group_ipu_controlled_attributes.csv
			GROUP_CONTROL_TOTALS=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpUGenerationRule/group_control_totals.csv
			GROUP_SUPPLEMENTARY_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpUGenerationRule/group_supplementary_attributes.csv
			COMPONENT_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpUGenerationRule/component_attributes.csv
			COMPONENT_POPULATION_NAME=people
			COMPONENT_SAMPLE_DATA=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpUGenerationRule/component_sample.csv
			GROUP_ID_ATTRIBUTE_ON_COMPONENT=HouseholdID
			COMPONENT_CONTROLLED_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpUGenerationRule/component_controlled_attributes.csv
			COMPONENT_CONTROL_TOTALS=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpUGenerationRule/component_ipu_control_totals.csv
			COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpUGenerationRule/component_supplementary_attributes.csv
			COMPONENT_REFERENCE_ON_GROUP=inhabitans
			GROUP_REFERENCE_ON_COMPONENT=my_household
			MAX_ITERATIONS=4 
		 */
		
		final String base_path = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateIpuGenerationRule/";
		
		final String groupAttributesFilePath = base_path + "group_attributes.csv";
		final String groupPopulationName = "household";
		final String groupSampleDataFile = base_path + "group_sample.csv";
		final String groupIdOnGroup = "HouseholdID";
		final String groupControlledAttributesFile = base_path + "group_controlled_attributes.csv";
		final String groupControlTotalsFile = base_path + "group_ipu_control_totals.csv";
		final String groupSupplementaryAttributesFile = base_path + "group_supplementary_attributes.csv";
		
		final String componentAttributesFile = base_path + "component_attributes.csv";
		final String componentPopulationName = "household";
		final String componentSampleDataFile = base_path + "component_sample.csv";
		final String componentIdOnGroup = "HouseholdID";
		final String componentControlledAttributesFile = base_path + "component_controlled_attributes.csv";
		final String componentControlTotalsFile = base_path + "component_ipu_control_totals.csv";
		final String componentSupplementaryAttributesFile = base_path + "component_supplementary_attributes.csv";

		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					// group population
					if (filePath.endsWith("/group_attributes.csv")) { return groupAttributesFilePath; }
					if (filePath.endsWith("/group_sample.csv")) { return groupSampleDataFile; }
					if (filePath.endsWith("/group_controlled_attributes.csv")) { return groupControlledAttributesFile; }
					if (filePath.endsWith("/group_ipu_control_totals.csv")) { return groupControlTotalsFile; }
					if (filePath.endsWith("/group_supplementary_attributes.csv")) { return groupSupplementaryAttributesFile; }
					
					// component population
					if (filePath.endsWith("/component_attributes.csv")) { return componentAttributesFile; }
					if (filePath.endsWith("/component_sample.csv")) { return componentSampleDataFile; }
					if (filePath.endsWith("/component_controlled_attributes.csv")) { return componentControlledAttributesFile; }
					if (filePath.endsWith("/component_ipu_control_totals.csv")) { return componentControlTotalsFile; }
					if (filePath.endsWith("/component_supplementary_attributes.csv")) { return componentSupplementaryAttributesFile; }
					
					
					return null;
				}
			};
		}};
		
		
		String ipuPopulationPropertiesFilePath = base_path + "IpuPopulationProperties.properties";
		String ruleName = "dummy rule";
		
		Properties ipuPopulationProperties = null;
		File ipuPopulationPropertiesFile = new File(ipuPopulationPropertiesFilePath);
		try {
			FileInputStream propertyInputStream = new FileInputStream(ipuPopulationPropertiesFile);
			ipuPopulationProperties = new Properties();
			ipuPopulationProperties.load(propertyInputStream);
		} catch (FileNotFoundException e) {
			throw new GenstarException(e);
		} catch (IOException e) {
			throw new GenstarException(e);
		}
		
		SampleBasedGenerator groupGenerator = new SampleBasedGenerator("group generator");
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(groupAttributesFilePath, true);
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesFile);
		
		GamaGenstarUtils.createIpuGenerationRule(scope, groupGenerator, ruleName, ipuPopulationProperties);
		
		IpuGenerationRule generationRule = (IpuGenerationRule) groupGenerator.getGenerationRule();
		
		
		GenstarCsvFile _groupControlledAttributesFile = Deencapsulation.getField(generationRule, "groupControlledAttributesFile");
		assertTrue(_groupControlledAttributesFile.getPath().equals(groupControlledAttributesFile));
		
		GenstarCsvFile _groupControlTotalsFile = Deencapsulation.getField(generationRule, "groupControlTotalsFile");
		assertTrue(_groupControlTotalsFile.getPath().equals(groupControlTotalsFile));
		
		GenstarCsvFile _groupSupplementaryAttributesFile = Deencapsulation.getField(generationRule, "groupSupplementaryAttributesFile");
		assertTrue(_groupSupplementaryAttributesFile.getPath().equals(groupSupplementaryAttributesFile));
		
		
		GenstarCsvFile _componentControlledAttributesFile = Deencapsulation.getField(generationRule, "componentControlledAttributesFile");
		assertTrue(_componentControlledAttributesFile.getPath().equals(componentControlledAttributesFile));
		
		GenstarCsvFile _componentControlTotalsFile = Deencapsulation.getField(generationRule, "componentControlTotalsFile");
		assertTrue(_componentControlTotalsFile.getPath().equals(componentControlTotalsFile));
		
		GenstarCsvFile _componentSupplementaryAttributesFile = Deencapsulation.getField(generationRule, "componentSupplementaryAttributesFile");
		assertTrue(_componentSupplementaryAttributesFile.getPath().equals(componentSupplementaryAttributesFile));
	}
}
