package ummisco.genstar.gama;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.generation_rules.SampleFreeGenerationRule;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.sample_data.CompoundSampleData;
import ummisco.genstar.metamodel.sample_data.SampleData;
import ummisco.genstar.sample_free.FrequencyDistributionGenerationRule;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.GenstarUtils;

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
		
		DeprecatedGamaGenstarUtils.createIpfGenerationRule(scope, generator, ruleName, ipfPopulationProperties);
		
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
		
		DeprecatedGamaGenstarUtils.createIpfGenerationRule(scope, generator, ruleName, ipfPopulationProperties);
		
		assertTrue(generator.getGenerationRule() instanceof IpfGenerationRule);

		IpfGenerationRule generationRule = (IpfGenerationRule) generator.getGenerationRule();
		assertTrue(generationRule.getSampleData() instanceof SampleData);
	}
	
	
	@Test public void testCreateSampleFreeGenerationRules(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
		/*
	List<GenstarCsvFile> createSampleFreeGenerationRules(final IScope scope, final SampleFreeGenerator generator, final GenstarCsvFile sampleFreeGenerationRulesListFile) throws GenstarException {
		 */
		
		final String basePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testCreateSampleFreeGenerationRules/";
		final String attributesFilePath = basePath + "people_attributes.csv";
		final String generationRulesFilePath = basePath + "People_GenerationRules.csv";
		final String rule1FilePath = basePath + "people_distribution_1.csv";
		final String rule2FilePath = basePath + "people_distribution_2.csv";
		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("people_distribution_1.csv")) { return rule1FilePath; }
					if (filePath.endsWith("people_distribution_2.csv")) { return rule2FilePath; }
										
					return null;
				}
			};
		}};		
		
		SampleFreeGenerator generator = new SampleFreeGenerator("generator", 100);
		GenstarCsvFile attributesFile = new GenstarCsvFile(attributesFilePath, true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		GenstarCsvFile sampleFreeGenerationRulesListFile = new GenstarCsvFile(generationRulesFilePath, true);
		
		assertTrue(generator.getGenerationRules().size() == 0);

		List<GenstarCsvFile> ruleFiles = DeprecatedGamaGenstarUtils.createSampleFreeGenerationRules(scope, generator, sampleFreeGenerationRulesListFile);
		
		assertTrue(ruleFiles.size() == 2);
		assertTrue(ruleFiles.get(0).getPath().endsWith("people_distribution_1.csv"));
		assertTrue(ruleFiles.get(1).getPath().endsWith("people_distribution_2.csv"));
		
		List<SampleFreeGenerationRule> rules = new ArrayList<SampleFreeGenerationRule>(generator.getGenerationRules()); 
		assertTrue(rules.size() == 2);
		assertTrue(rules.get(0) instanceof FrequencyDistributionGenerationRule);
		assertTrue(rules.get(1) instanceof FrequencyDistributionGenerationRule);
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
		
		DeprecatedGamaGenstarUtils.createIpuGenerationRule(scope, groupGenerator, ruleName, ipuPopulationProperties);
		
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
	
	
	@Test public void testExtractIpuPopulation(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {

		String base_path = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testExtractIpuPopulation/";
		
		// 0. generate an original population with 120 entities if necessary
		String groupPopulationName = "household";
		
		File groupPopulationFile = new File(base_path + "household_population.csv");
		if (!groupPopulationFile.exists()) {
			GenstarCsvFile groupAttributesFile = new GenstarCsvFile(base_path + "group_attributes.csv", true);
			
			String componentPopulationName = "people";
			GenstarCsvFile componentAttributesFile = new GenstarCsvFile(base_path + "component_attributes.csv", true);

			String groupIdAttributeNameOnGroupEntity = "Household ID";
			String groupIdAttributeNameOnComponentEntity = "Household ID";
			String groupSizeAttributeNameOnData = "Household Size";
			
			// 0.1. generate an original population with 120 entities
			int minGroupEntitiesOfEachAttributeValuesSet1 = 15;
			int maxGroupEntitiesOfEachAttributeValuesSet1 = 15;
			IPopulation generatedCompoundPopulation = GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, 
					componentAttributesFile, groupIdAttributeNameOnGroupEntity, groupIdAttributeNameOnComponentEntity, groupSizeAttributeNameOnData, 
					minGroupEntitiesOfEachAttributeValuesSet1, maxGroupEntitiesOfEachAttributeValuesSet1, null, null);
			
			AbstractAttribute householdSizeAttr = generatedCompoundPopulation.getAttributeByNameOnData("Household Size");
			AbstractAttribute householdIncomeAttr = generatedCompoundPopulation.getAttributeByNameOnData("Household Income");
			AbstractAttribute householdTypeAttr = generatedCompoundPopulation.getAttributeByNameOnData("Household Type");
			
			Set<AbstractAttribute> ipuControlledAttributes = new HashSet<AbstractAttribute>();
			ipuControlledAttributes.add(householdSizeAttr);
			ipuControlledAttributes.add(householdIncomeAttr);
			ipuControlledAttributes.add(householdTypeAttr);
			
			// 0.2. save the generated population to CSV files
			Map<String, String> csvFilePathsByPopulationNames = new HashMap<String, String>();
			csvFilePathsByPopulationNames.put("household", base_path + "household_population.csv");
			csvFilePathsByPopulationNames.put("people", base_path + "people_population.csv");
			GenstarUtils.writePopulationToCsvFile(generatedCompoundPopulation, csvFilePathsByPopulationNames);
		}
		
		
		// 1. prepare data
		/* IpuPopulationProperties_ZeroPointOne.properties
		 * 
			GROUP_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testExtractIpuPopulation/group_attributes.csv
			GROUP_POPULATION_NAME=household
			GROUP_POPULATION_DATA=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testExtractIpuPopulation/household_population.csv
			GROUP_ID_ATTRIBUTE_ON_GROUP=HouseholdID
			GROUP_CONTROLLED_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testExtractIpuPopulation/group_controlled_attributes.csv
			COMPONENT_ATTRIBUTES=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testExtractIpuPopulation/component_attributes.csv
			COMPONENT_POPULATION_NAME=people
			COMPONENT_POPULATION_DATA=test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testExtractIpuPopulation/people_population.csv
			GROUP_ID_ATTRIBUTE_ON_COMPONENT=HouseholdID
			PERCENTAGE=0,1 
		 */
	
		final String groupAttributesFilePath = base_path + "group_attributes.csv";
//		final String groupPopulationName = "household";
		final String groupPopulationDataFile = base_path + "household_population.csv";
		final String groupIdOnGroup = "HouseholdID";
		final String groupControlledAttributesFile = base_path + "group_controlled_attributes.csv";
		
		final String componentAttributesFile = base_path + "component_attributes.csv";
		final String componentPopulationName = "people";
		final String componentPopulationFile = base_path + "people_population.csv";
		final String componentIdOnGroup = "HouseholdID";

		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					// group population
					if (filePath.endsWith("/group_attributes.csv")) {  return groupAttributesFilePath;  }
					if (filePath.endsWith("/household_population.csv")) {  return groupPopulationDataFile; }
					if (filePath.endsWith("/group_controlled_attributes.csv")) { return groupControlledAttributesFile; }
					
					// component population
					if (filePath.endsWith("/component_attributes.csv")) { return componentAttributesFile; }
					if (filePath.endsWith("/people_population.csv")) { return componentPopulationFile; }					
					
					return null;
				}
			};
		}};
		
		
		String ipuPopulationPropertiesFilePath_ZeroPointOne = base_path + "IpuPopulationProperties_ZeroPointOne.properties";
		
		Properties ipuPopulationProperties_ZeroPointOne = null;
		File ipuPopulationPropertiesFile_ZeroPointOne = new File(ipuPopulationPropertiesFilePath_ZeroPointOne);
		try {
			FileInputStream propertyInputStream = new FileInputStream(ipuPopulationPropertiesFile_ZeroPointOne);
			ipuPopulationProperties_ZeroPointOne = new Properties();
			ipuPopulationProperties_ZeroPointOne.load(propertyInputStream);
		} catch (FileNotFoundException e) {
			throw new GenstarException(e);
		} catch (IOException e) {
			throw new GenstarException(e);
		}
		
		
		// 2. extract the generated population and verify the extracted populations
		
		IPopulation extractedPopulation_ZeroPointOne = DeprecatedGamaGenstarUtils.extractIpuPopulation(scope, ipuPopulationProperties_ZeroPointOne);
		
		
		// 3. Verifications of the extracted population
		//		extracted population's number of entities
		assertTrue(extractedPopulation_ZeroPointOne.getNbOfEntities() == 8);
		
		
		String ipuPopulationPropertiesFilePath_Ten = base_path + "IpuPopulationProperties_Ten.properties";
		
		Properties ipuPopulationProperties_Ten = null;
		File ipuPopulationPropertiesFile_Ten = new File(ipuPopulationPropertiesFilePath_Ten);
		try {
			FileInputStream propertyInputStream = new FileInputStream(ipuPopulationPropertiesFile_Ten);
			ipuPopulationProperties_Ten = new Properties();
			ipuPopulationProperties_Ten.load(propertyInputStream);
		} catch (FileNotFoundException e) {
			throw new GenstarException(e);
		} catch (IOException e) {
			throw new GenstarException(e);
		}
		 
		IPopulation extractedPopulation_Ten = DeprecatedGamaGenstarUtils.extractIpuPopulation(scope, ipuPopulationProperties_Ten);
		assertTrue(extractedPopulation_Ten.getNbOfEntities() == 12);
		
		
		String ipuPopulationPropertiesFilePath_Thirty = base_path + "IpuPopulationProperties_Thirty.properties";
		
		Properties ipuPopulationProperties_Thirty = null;
		File ipuPopulationPropertiesFile_Thirty = new File(ipuPopulationPropertiesFilePath_Thirty);
		try {
			FileInputStream propertyInputStream = new FileInputStream(ipuPopulationPropertiesFile_Thirty);
			ipuPopulationProperties_Thirty = new Properties();
			ipuPopulationProperties_Thirty.load(propertyInputStream);
		} catch (FileNotFoundException e) {
			throw new GenstarException(e);
		} catch (IOException e) {
			throw new GenstarException(e);
		}
		 
		IPopulation extractedPopulation_Thirty = DeprecatedGamaGenstarUtils.extractIpuPopulation(scope, ipuPopulationProperties_Thirty);
		assertTrue(extractedPopulation_Thirty.getNbOfEntities() == 36);
		 
	}
	
	
	@Test public void testGenerateRandomPopulation() throws GenstarException {
		fail("not yet implemented");
	}
	
	
	@Test public void testGenerateRandomCompoundPopulation() throws GenstarException {
		fail("not yet implemented");
	}
	
	
	@Test public void testLoadSinglePopulation(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		/*
	public static IPopulation loadSinglePopulation(final IScope scope, final Properties singlePopulationProperties) throws GenstarException {
		 */
		
		String base_path = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testLoadSinglePopulation/";
		
		final String attributesFilePath = base_path + "attributes.csv";
		final String populationDataFile = base_path + "sample_data.csv";
		

		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					// group population
					if (filePath.endsWith("/attributes.csv")) {  return attributesFilePath;  }
					if (filePath.endsWith("/sample_data.csv")) {  return populationDataFile; }
					
					return null;
				}
			};
		}};

	
		final String propertiesFilePath = base_path + "SinglePopulationProperties.properties";
		
		Properties properties = null;
		File propertiesFile = new File(propertiesFilePath);
		try {
			FileInputStream propertyInputStream = new FileInputStream(propertiesFile);
			properties = new Properties();
			properties.load(propertyInputStream);
		} catch (FileNotFoundException e) {
			throw new GenstarException(e);
		} catch (IOException e) {
			throw new GenstarException(e);
		}
		 
		IPopulation loadedSinglePopulation = DeprecatedGamaGenstarUtils.loadSinglePopulation(scope, properties);

	
		// verify file entities' attribute values on entity with respect to populationFile content
		GenstarCsvFile populationFile = new GenstarCsvFile(populationDataFile, true);
		List<String> header = populationFile.getHeaders();
		List<Entity> entities = loadedSinglePopulation.getEntities();
		List<List<String>> fileContent = populationFile.getContent();
		assertTrue(entities.size() == fileContent.size());
		for (int row=0; row<entities.size(); row++) {
			Entity e = entities.get(row);
			
			List<String> rowContent = populationFile.getRow(row);
			for (int col=0; col<rowContent.size(); col++) {
				assertTrue(((UniqueValue)e.getEntityAttributeValueByNameOnEntity(header.get(col)).getAttributeValueOnEntity()).getStringValue().equals(rowContent.get(col)));
			}
		}
		
		assertTrue(loadedSinglePopulation.getGroupReferences().isEmpty());
		assertTrue(loadedSinglePopulation.getComponentReferences().isEmpty());
	}
	
	
	@Test public void testLoadCompoundPopulation(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {

		String base_path = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testLoadCompoundPopulation/";
		
		final String groupAttributesFilePath = base_path + "group_attributes.csv";
		final String groupPopulationDataFile = base_path + "group_sample.csv";
		
		final String componentAttributesFile = base_path + "component_attributes.csv";
		final String componentPopulationFilePath = base_path + "component_sample.csv";
		
	
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					// group population
					if (filePath.endsWith("/group_attributes.csv")) {  return groupAttributesFilePath;  }
					if (filePath.endsWith("/group_sample.csv")) {  return groupPopulationDataFile; }
					
					// component population
					if (filePath.endsWith("/component_attributes.csv")) { return componentAttributesFile; }
					if (filePath.endsWith("/component_sample.csv")) { return componentPopulationFilePath; }					
					
					return null;
				}
			};
		}};
		
		
		final String propertiesFilePath = base_path + "CompoundPopulationProperties.properties";
		
		Properties properties = null;
		File propertiesFile = new File(propertiesFilePath);
		try {
			FileInputStream propertyInputStream = new FileInputStream(propertiesFile);
			properties = new Properties();
			properties.load(propertyInputStream);
		} catch (FileNotFoundException e) {
			throw new GenstarException(e);
		} catch (IOException e) {
			throw new GenstarException(e);
		}
		 
		IPopulation loadedCompoundPopulation = DeprecatedGamaGenstarUtils.loadCompoundPopulation(scope, properties);
		 
		
		// verifications		
		assertTrue(loadedCompoundPopulation.getName().equals("household"));
		
		// verify the number of compound entities
		final GenstarCsvFile groupPopulationFile = new GenstarCsvFile(groupPopulationDataFile, true);
		assertTrue(loadedCompoundPopulation.getNbOfEntities() == groupPopulationFile.getRows() - 1);
		
		// verify the number of component entities
		int numberOfComponentEntities = 0;
		for (Entity compoundEntity : loadedCompoundPopulation.getEntities()) {
			IPopulation _componentPop = compoundEntity.getComponentPopulation("people");
			if (_componentPop != null) {
				numberOfComponentEntities += _componentPop.getNbOfEntities();
				assertTrue(_componentPop.getGroupReferences().size() == 1);
				assertTrue(_componentPop.getGroupReference("household").equals("my_household"));
			}
		}
		final GenstarCsvFile componentPopulationFile = new GenstarCsvFile(componentPopulationFilePath, true);
		assertTrue(numberOfComponentEntities == componentPopulationFile.getRows() - 1);
		 
		
		assertTrue(loadedCompoundPopulation.getComponentReferences().size() == 1);
		assertTrue(loadedCompoundPopulation.getComponentReference("people").equals("inhabitants"));
		
		assertTrue(loadedCompoundPopulation.getGroupReferences().size() == 0);
		
	}
	
	
	@Test public void testGenerateFrequencyDistributionPopulation(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
	
		String basePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testGenerateFrequencyDistributionPopulation/";
		
		/*
	static IPopulation generateFrequencyDistributionPopulation(final IScope scope, final Properties frequencyDistributionPopulationProperties) throws GenstarException {
		 */
		
		final String peopleAttributesFilePath = basePath + "people_attributes.csv";
		final String peopleDistribution1FilePath = basePath + "people_distribution_1.csv";
		final String peopleDistribution2FilePath = basePath + "people_distribution_2.csv";
		final String generationRulesFilePaths = basePath + "People_GenerationRules.csv";
		final String outputFolderPath = basePath + "analysisResult";
		
		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("/people_attributes.csv")) {  return peopleAttributesFilePath;  }
					if (filePath.endsWith("/people_distribution_1.csv")) {  return peopleDistribution1FilePath; }
					if (filePath.endsWith("/people_distribution_2.csv")) {  return peopleDistribution2FilePath; }
					if (filePath.endsWith("/People_GenerationRules.csv")) { return generationRulesFilePaths; }
					if (filePath.endsWith("analysisResult")) { return outputFolderPath; }
					
					return null;
				}
			};
		}};
		
		
		// do the clean up if necessary (remove "analysis output files")
		File outputFolder = new File(outputFolderPath);
		File[] outputFiles = outputFolder.listFiles();
		for (File f : outputFiles) { f.delete(); }
		
		
		// load the property file
		String propertiesFilePath = basePath + "PeoplePopulation.properties";
		Properties frequencyDistributionPopulationProperties = null;
		File propertiesFile = new File(propertiesFilePath);
		try {
			FileInputStream propertyInputStream = new FileInputStream(propertiesFile);
			frequencyDistributionPopulationProperties = new Properties();
			frequencyDistributionPopulationProperties.load(propertyInputStream);
		} catch (FileNotFoundException e) {
			throw new GenstarException(e);
		} catch (IOException e) {
			throw new GenstarException(e);
		}

		IPopulation population = DeprecatedGamaGenstarUtils.generateFrequencyDistributionPopulation(scope, frequencyDistributionPopulationProperties);
		
		// verify population name
		assertTrue(population.getName().equals("people"));
		
		// verify number of entity
		assertTrue(population.getEntities().size() == 5000);
		
		// verify the existence of "analysis output files"
		outputFiles = outputFolder.listFiles();
		assertTrue(outputFiles.length == 2);
	}
	
	
	@Test public void testGenerateFrequencyDistributionsFromSampleDataOrPopulationFile(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
		/*
	static List<String> generateFrequencyDistributionsFromSampleDataOrPopulationFile(final IScope scope, final Properties frequencyDistributionsProperties) throws GenstarException {
		 */
		
		String basePath = "test_data/ummisco/genstar/gama/GamaGenstarUtilsTest/testGenerateFrequencyDistributionsFromSampleDataOrPopulationFile/";
		
		String propertiesFilePath = basePath + "frequency_distributions.properties";
		Properties frequencyDistributionsProperties = null;
		File propertiesFile = new File(propertiesFilePath);
		try {
			FileInputStream propertyInputStream = new FileInputStream(propertiesFile);
			frequencyDistributionsProperties = new Properties();
			frequencyDistributionsProperties.load(propertyInputStream);
		} catch (FileNotFoundException e) {
			throw new GenstarException(e);
		} catch (IOException e) {
			throw new GenstarException(e);
		}
		
		
		
		
		final String attributesFilePath = basePath + "attributes.csv";
		final String populationDataFilePath = basePath + "sampleData.csv";
		final String distributionFormatsListFilePath = basePath + "frequency_distribution_formats_list.csv";
		final String distributionFormat1FilePath = basePath + "distributionFormat1.csv";
		final String distributionFormat2FilePath = basePath + "distributionFormat2.csv";
		final String resultDistribution1FilePath = basePath + "resultDistribution1.csv";
		final String resultDistribution2FilePath = basePath + "resultDistribution2.csv";
		
		File resultFile1 = new File(resultDistribution1FilePath);
		if (resultFile1.exists()) { resultFile1.delete(); }

		File resultFile2 = new File(resultDistribution2FilePath);
		if (resultFile2.exists()) { resultFile2.delete(); }
		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, anyBoolean);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("/attributes.csv")) {  return attributesFilePath;  }
					if (filePath.endsWith("/sampleData.csv")) {  return populationDataFilePath; }					
					if (filePath.endsWith("/frequency_distribution_formats_list.csv")) {  return distributionFormatsListFilePath; }					
					if (filePath.endsWith("/distributionFormat1.csv")) {  return distributionFormat1FilePath; }
					if (filePath.endsWith("/distributionFormat2.csv")) {  return distributionFormat2FilePath; }
					if (filePath.endsWith("/resultDistribution1.csv")) { return resultDistribution1FilePath; }
					if (filePath.endsWith("/resultDistribution2.csv")) { return resultDistribution2FilePath; }
					
					
					return null;
				}
			};
		}};
		 
		
		DeprecatedGamaGenstarUtils.generateFrequencyDistributionsFromSampleOrPopulationData(scope, frequencyDistributionsProperties);
		
		
		File recreatedResultFile1 = new File(resultDistribution1FilePath);
		assertTrue(recreatedResultFile1.exists());

		File recreatedRile2 = new File(resultDistribution2FilePath);
		assertTrue (recreatedRile2.exists());
	}
}

