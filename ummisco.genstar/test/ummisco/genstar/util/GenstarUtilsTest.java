package ummisco.genstar.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mockit.Deencapsulation;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import msi.gama.common.util.FileUtils;
import msi.gama.runtime.IScope;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.GroupComponentSampleData;
import ummisco.genstar.ipf.SampleData;
import ummisco.genstar.ipf.SampleDataGenerationRule;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.IPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.MultipleRulesGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;

@RunWith(JMockit.class)
public class GenstarUtilsTest {


	@Test public void testCreateFrequencyDistributionGenerationFromSampleData() throws GenstarException {
		// input: distributionFormatCSVFile & sampleDataCSVFile
		// output: the newly created FrequencyDistributionGenerationRule
		
		ISyntheticPopulationGenerator generator = new MultipleRulesGenerator("dummy generator", 10);
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateFrequencyDistributionGenerationFromSampleData/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		
		GenstarCSVFile distributionFormatCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateFrequencyDistributionGenerationFromSampleData/distributionFormat.csv", true);
		GenstarCSVFile sampleDataCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateFrequencyDistributionGenerationFromSampleData/sampleData.csv", true);
		
		FrequencyDistributionGenerationRule rule = GenstarUtils.createFrequencyDistributionFromSampleData(generator, distributionFormatCSVFile, sampleDataCSVFile);
		assertTrue(rule.getAttributes().size() == 2);
		assertTrue(rule.getInputAttributeAtOrder(0) == null);
		assertTrue(rule.getOutputAttributeAtOrder(0).getNameOnData().equals("Category"));
		assertTrue(rule.getOutputAttributeAtOrder(1).getNameOnData().equals("Age"));
		int totalNbOfAttributeValues = 1;
		for (AbstractAttribute a : rule.getAttributes()) { totalNbOfAttributeValues *= a.values().size(); }
		assertTrue(rule.getAttributeValuesFrequencies().size() == totalNbOfAttributeValues);
		
		
		/*
		 * sampleData.csv
				Category,Gender,Age
				C0,false,5
				C3,true,5
				C3,false,5
				C5,true,5
				C7,false,5
		 */
		Set<AttributeValuesFrequency> attributeValuesFrequencies = rule.getAttributeValuesFrequencies();
		
		// C0, 5
		Map<AbstractAttribute, AttributeValue> map1 = new HashMap<AbstractAttribute, AttributeValue>();
		
		AttributeValue c0Value = new UniqueValue(DataType.STRING, "C0");
		map1.put(generator.getAttributeByNameOnData("Category"), c0Value);
		
		RangeValuesAttribute ageAttribute = (RangeValuesAttribute) generator.getAttributeByNameOnData("Age");
		Set<AttributeValue> ageValues = ageAttribute.values();
		UniqueValue age5UniqueValue = new UniqueValue(DataType.INTEGER, "5");
		RangeValue age5RangeValue = null;
		for (AttributeValue a : ageValues) {
			if ( ((RangeValue)a).cover(age5UniqueValue)) {
				age5RangeValue = (RangeValue)a;
				break;
			}
		}
		if (age5RangeValue == null) { Assert.fail("No age value is matched"); }
		map1.put(ageAttribute, age5RangeValue);

		boolean isMatchMap1 = false;
		for (AttributeValuesFrequency f : attributeValuesFrequencies) {
			if (f.matchAttributeValuesOnData(map1)) {
				if (isMatchMap1) { Assert.fail("Attributevalue is matched several times (map1)"); }
				
				assertTrue(f.getFrequency() == 1);
				isMatchMap1 = true;
			}
		}
		if (!isMatchMap1) { Assert.fail("No attributeValue is matched (map1)"); }
		
		// C3, 5 (frequency == 2)
		Map<AbstractAttribute, AttributeValue> map2 = new HashMap<AbstractAttribute, AttributeValue>();
		AttributeValue c5Value = new UniqueValue(DataType.STRING, "C3");
		map2.put(generator.getAttributeByNameOnData("Category"), c5Value);
		map2.put(ageAttribute, age5RangeValue);
		
		boolean isMatchMap2 = false;
		for (AttributeValuesFrequency f : attributeValuesFrequencies) {
			if (f.matchAttributeValuesOnData(map2)) {
				if (isMatchMap2) { Assert.fail("Attributevalue is matched several times (map2)"); }
				
				assertTrue(f.getFrequency() == 2);
				isMatchMap2 = true;
			}
		}
		if (!isMatchMap2) { Assert.fail("No attributeValue is matched (map2)"); }
	}		
	
	@Test(expected = GenstarException.class) public void testGenerateRandomSinglePopulationWithNullAttributesFile() throws GenstarException {
		GenstarUtils.generateRandomSinglePopulation("dummy population", null, 1);
	}
	
	@Test(expected = GenstarException.class) public void testGenerateRandomSinglePopulationWithNegativeEntities(@Mocked final GenstarCSVFile attributesFile) throws GenstarException {
		GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile, 0);
	}
	
	
	@Test(expected = GenstarException.class) public void testWriteControlTotalsToCsvFileWithNullControlTotals() throws GenstarException {
		GenstarUtils.writeContentToCsvFile(null, "");
	}
	
	@Test(expected = GenstarException.class) public void testWriteControlTotalsToCsvFileWithNullCsvFilePath() throws GenstarException {
		GenstarUtils.writeContentToCsvFile(new ArrayList<List<String>>(), null);
	}
	
	@Test public void testWriteControlTotalsToCsvFile() throws GenstarException {
		GenstarCSVFile controlledAttributesFile1 = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testWriteControlTotalsToCsvFile/controlled_attributes1.csv", true);
		List<List<String>> controlTotals = IpfUtils.generateIpfControlTotals(controlledAttributesFile1, 500);
		
		String controlTotalsFilePath = "test_data/ummisco/genstar/util/GenstarUtils/testWriteControlTotalsToCsvFile/control_totals1.csv";
		File controlTotalsFile = new File(controlTotalsFilePath);
		if (controlTotalsFile.exists()) { controlTotalsFile.delete(); }
		controlTotalsFile = null;
		
		GenstarUtils.writeContentToCsvFile(controlTotals, controlTotalsFilePath);

		GenstarCSVFile controlTotalsCsvFile = new GenstarCSVFile(controlTotalsFilePath, false);
		assertTrue(controlTotalsCsvFile.getRows() == controlTotals.size()); // number of rows
		assertTrue(controlTotalsCsvFile.getColumns() == 7);  // number of columns (3 attributes + frequency)
	}
	
	@Test public void testGenerateRandomSinglePopulationSuccessfully1() throws GenstarException {
		GenstarCSVFile attributesFile1 = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateRandomSinglePopulation1/attributes1.csv", true);
		IPopulation generatedPopulation = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile1, 1, 1);
		
		int nbOfEntities1 = 1;
		for (AbstractAttribute attribute : generatedPopulation.getAttributes()) { nbOfEntities1 *= attribute.values().size(); }
		assertTrue(generatedPopulation.getEntities().size() == nbOfEntities1);
		
		generatedPopulation = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile1, 2, 2);
		assertTrue(generatedPopulation.getEntities().size() == 2 * nbOfEntities1);
		
		generatedPopulation = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile1, 1, 2);
		assertTrue((generatedPopulation.getEntities().size() <= 2 * nbOfEntities1) && (generatedPopulation.getEntities().size() >= nbOfEntities1));

	
		GenstarCSVFile attributesFile2 = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateRandomSinglePopulation1/attributes2.csv", true);
		IPopulation generatedPopulation2 = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile2, 1, 1);
		
		int nbOfEntities2 = 1;
		for (AbstractAttribute attribute : generatedPopulation2.getAttributes()) { nbOfEntities2 *= attribute.values().size(); }
		assertTrue(generatedPopulation2.getEntities().size() == nbOfEntities2);
	}
	
	@Test public void testGenerateRandomSinglePopulationSuccessfully() throws GenstarException {
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateRandomSinglePopulation/attributes.csv", true);
		int nbEntities = 100 + SharedInstances.RandomNumberGenerator.nextInt(100);
		
		IPopulation generatedPopulation = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile, nbEntities);
		
		assertTrue(generatedPopulation.getEntities().size() == nbEntities);
		assertTrue(generatedPopulation.getEntities().get(0).getEntityAttributeValues().size() == generatedPopulation.getAttributes().size());
	}
	
	@Test public void testGenerateGroupPopulationSuccessfully() throws GenstarException {
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateGroupPopulation/group_attributes.csv", true);
		
		String populationName = "household";
		
		ISingleRuleGenerator groupGenerator = new SingleRuleGenerator("group dummy generator");
		AttributeUtils.createAttributesFromCSVFile(groupGenerator, groupAttributesFile);
		List<AbstractAttribute> groupAttributes = new ArrayList<AbstractAttribute>(groupGenerator.getAttributes());
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		
		int nbOfGroups = 100;
		
		IPopulation groupPopulation = Deencapsulation.invoke(GenstarUtils.class, "generateGroupPopulation", populationName, groupAttributes, 
				groupIdAttributeOnGroupEntity, nbOfGroups);
		
		assertTrue(groupPopulation.getEntities().size() == nbOfGroups);
		assertTrue(groupPopulation.getEntities().get(0).getEntityAttributeValues().size() == groupAttributesFile.getRows() - 1);
	}
	
	@Test public void testGenerateComponentPopulation() throws GenstarException {
		
		// generate group population
		String groupPopulationName = "household";
		
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateComponentPopulation/group_attributes.csv", true);

		ISingleRuleGenerator groupGenerator = new SingleRuleGenerator("group dummy generator");
		AttributeUtils.createAttributesFromCSVFile(groupGenerator, groupAttributesFile);
		List<AbstractAttribute> groupAttributes = new ArrayList<AbstractAttribute>(groupGenerator.getAttributes());
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		AbstractAttribute groupSizeAttribute = groupGenerator.getAttributeByNameOnData("Household Size");
		
		int nbOfGroups = 100;
		IPopulation groupPopulation = Deencapsulation.invoke(GenstarUtils.class, "generateGroupPopulation", groupPopulationName, groupAttributes, 
				groupIdAttributeOnGroupEntity, nbOfGroups);
		
		// assert empty component populations
		UniqueValue groupSizeValue;
		int nbOfComponentsToGenerate = 0;
		for (Entity groupEntity : groupPopulation.getEntities()) {
			assertTrue(groupEntity.getComponentPopulations().isEmpty());
			
			groupSizeValue = (UniqueValue) groupEntity.getEntityAttributeValueByNameOnData(groupSizeAttribute.getNameOnData()).getAttributeValueOnEntity();
			nbOfComponentsToGenerate += groupSizeValue.getIntValue(); 
		}
		
		// generate component entities
		String componentPopulationName = "people";
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateComponentPopulation/component_attributes.csv", true);
		
		ISingleRuleGenerator componentGenerator = new SingleRuleGenerator("component dummy generator");
		AttributeUtils.createAttributesFromCSVFile(componentGenerator, componentAttributesFile);
		List<AbstractAttribute> componentAttributes = new ArrayList<AbstractAttribute>(componentGenerator.getAttributes());
		AbstractAttribute groupIdAttributeOnComponentEntity = componentGenerator.getAttributeByNameOnData("Household ID"); 
		groupIdAttributeOnComponentEntity.setIdentity(true);
		
		// generate component entities
		Deencapsulation.invoke(GenstarUtils.class, "generateComponentPopulation",  groupPopulation, componentPopulationName, 
				componentAttributes, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity, groupSizeAttribute);		
		
		// assert that the number of generated component entities is correct
		int nbOfGeneratedComponents = 0;
		for (Entity groupEntity : groupPopulation.getEntities()) {
			for (IPopulation componentPopulation : groupEntity.getComponentPopulations()) {
				nbOfGeneratedComponents += componentPopulation.getEntities().size();
			}
		}
		
		assertTrue(nbOfComponentsToGenerate == nbOfGeneratedComponents);
	}
	
	
	@Test public void testGenerateRandomCompoundPopulation() throws GenstarException {

		String groupPopulationName = "household";
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateRandomCompoundPopulation/group_attributes.csv", true);
		
		String componentPopulationName = "people";
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateRandomCompoundPopulation/component_attributes.csv", true);

		String groupIdAttributeNameOnGroupEntity = "Household ID";
		String groupIdAttributeNameOnComponentEntity = "Household ID";
		String groupSizeAttributeName = "Household Size";
		int nbOfGroupEntities = 100;
		
		IPopulation generatedCompoundPopulation = GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, componentAttributesFile, 
				groupIdAttributeNameOnGroupEntity, groupIdAttributeNameOnComponentEntity, groupSizeAttributeName, nbOfGroupEntities);
		
		assertTrue(generatedCompoundPopulation.getEntities().size() == nbOfGroupEntities);
		assertTrue(generatedCompoundPopulation.getEntities().get(0).getEntityAttributeValues().size() == groupAttributesFile.getRows() - 1);
		
		Entity groupEntityWithComponents = null;
		for (Entity groupEntity : generatedCompoundPopulation.getEntities()) {
			if (!groupEntity.getComponentPopulations().isEmpty()) {
				groupEntityWithComponents = groupEntity;
				break;
			}
		}
		
		IPopulation peoplePopulation = groupEntityWithComponents.getComponentPopulation(componentPopulationName);
		assertTrue(peoplePopulation.getEntities().get(0).getEntityAttributeValues().size() == componentAttributesFile.getRows() - 1);
	}
	
	
	@Test public void testGenerateRandomCompoundPopulation1() throws GenstarException {
		fail("not yet implemented");
	}
	
	
	@Test public void testLoadSinglePopulation() throws GenstarException {
		fail("not yet implemented");
	}
	
	
	@Test public void testLoadCompoundPopulation() throws GenstarException {
		fail("not yet implemented");
	}
	
	
	@Test public void testCreateSampleDataGenerationRule() throws GenstarException {
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateSampleDataGenerationRule/attributes.csv", true);
		
		ISingleRuleGenerator generator = new SingleRuleGenerator("single rule generator");
		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		GenstarCSVFile sampleFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateSampleDataGenerationRule/sample_data.csv", true);
		GenstarCSVFile controlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateSampleDataGenerationRule/controlled_attributes.csv", false);
		GenstarCSVFile controlledTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateSampleDataGenerationRule/control_totals.csv", false);
		GenstarCSVFile supplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateSampleDataGenerationRule/supplementary_attributes.csv", false);
		
		
		AbstractAttribute householdIdAttribute = generator.getAttributeByNameOnEntity("householdID");
		
		GenstarUtils.createSampleDataGenerationRule(generator, "sample data generation rule", sampleFile, controlledAttributesFile, controlledTotalsFile, supplementaryAttributesFile, householdIdAttribute, SampleDataGenerationRule.DEFAULT_MAX_ITERATIONS);
		
		SampleDataGenerationRule rule = (SampleDataGenerationRule) generator.getGenerationRule();
		assertTrue(rule.getSampleData() instanceof SampleData);
		assertTrue(generator.getNbOfEntities() == rule.getIPF().getNbOfEntitiesToGenerate());
	}
	
	
	@Test public void testCreateGroupComponentSampleDataGenerationRule() throws GenstarException {
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateGroupComponentSampleDataGenerationRule/group_attributes.csv", true);
		
		ISingleRuleGenerator groupGenerator = new SingleRuleGenerator("group rule generator");
		AttributeUtils.createAttributesFromCSVFile(groupGenerator, attributesFile);
		
		
		GenstarCSVFile groupSampleFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateGroupComponentSampleDataGenerationRule/group_sample.csv", true);
		GenstarCSVFile groupControlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateGroupComponentSampleDataGenerationRule/group_controlled_attributes.csv", false);
		GenstarCSVFile groupControlledTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateGroupComponentSampleDataGenerationRule/group_control_totals.csv", false);
		GenstarCSVFile groupSupplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateGroupComponentSampleDataGenerationRule/group_supplementary_attributes.csv", false);
		
		GenstarCSVFile componentSampleFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateGroupComponentSampleDataGenerationRule/component_sample.csv", true);
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateGroupComponentSampleDataGenerationRule/component_attributes.csv", true);
		
		String groupIdAttributeNameOnGroup = "Household ID";
		String groupIdAttributeNameOnComponent = "Household ID";
		String componentPopulationName = "people";
		
		// optional/supplementary properties (COMPONENT_REFERENCE_ON_GROUP, GROUP_REFERENCE_ON_COMPONENT)
		Map<String, String> supplementaryProperties = new HashMap<String, String>();
		supplementaryProperties.put(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY, groupIdAttributeNameOnGroup);
		supplementaryProperties.put(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY, groupIdAttributeNameOnComponent);
		 
		
		GenstarUtils.createGroupComponentSampleDataGenerationRule(groupGenerator, "group component sample data generation rule", groupSampleFile, groupControlledAttributesFile, groupControlledTotalsFile, 
				groupSupplementaryAttributesFile, componentSampleFile, componentAttributesFile, componentPopulationName, SampleDataGenerationRule.DEFAULT_MAX_ITERATIONS, supplementaryProperties);
		
		SampleDataGenerationRule rule = (SampleDataGenerationRule)groupGenerator.getGenerationRule();
		assertTrue(rule.getSampleData() instanceof GroupComponentSampleData);
		assertTrue(rule.getIPF().getNbOfEntitiesToGenerate() == groupGenerator.getNbOfEntities());
	}
	

	@Test public void testWriteSinglePopulationToCSVFile(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
		// test write single population
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testWritePopulationToCSVFile/singlePopulation/attributes.csv", true);
		int nbEntities = 100 + SharedInstances.RandomNumberGenerator.nextInt(100);
		
		Map<String, String> generatedSinglePopulationFilePaths = new HashMap<String, String>();
		String singlePopulationName = "single population";
		final String singlePopulationOutputFile = "test_data/ummisco/genstar/util/GenstarUtils/testWritePopulationToCSVFile/singlePopulation/single_population.csv";
		generatedSinglePopulationFilePaths.put(singlePopulationName, singlePopulationOutputFile);
		
		
		IPopulation generatedSinglePopulation = GenstarUtils.generateRandomSinglePopulation(singlePopulationName, attributesFile, nbEntities);
		Map<String, String> resultSingleFilePaths = GenstarUtils.writePopulationToCSVFile(generatedSinglePopulation, generatedSinglePopulationFilePaths);
		
		assertTrue(resultSingleFilePaths.size() == 1);
		assertTrue(resultSingleFilePaths.get(singlePopulationName).equals(singlePopulationOutputFile));
		
		GenstarCSVFile singlePopOutputFile = new GenstarCSVFile(resultSingleFilePaths.get(singlePopulationName), true);
		
		// verify that the header contains attribute names on entity
		List<String> singlePopHeader = singlePopOutputFile.getHeaders();
		List<AbstractAttribute> attributes = generatedSinglePopulation.getAttributes();
		for (int i=0; i<singlePopHeader.size(); i++) {
			assertTrue(singlePopHeader.get(i).equals(attributes.get(i).getNameOnEntity()));
		}
		
		assertTrue(singlePopOutputFile.getRows() == nbEntities + 1);
	}
	
	
	@Test public void testWriteCompoundPopulation() throws GenstarException {
		// test write compound population
		String groupPopulationName = "household";
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testWritePopulationToCSVFile/compoundPopulation/group_attributes.csv", true);
		
		String componentPopulationName = "people";
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/GenstarUtils/testWritePopulationToCSVFile/compoundPopulation/component_attributes.csv", true);

		String groupIdAttributeNameOnGroupEntity = "Household ID";
		String groupIdAttributeNameOnComponentEntity = "Household ID";
		String groupSizeAttributeName = "Household Size";
		int nbOfGroupEntities = 100;
		
		IPopulation generatedCompoundPopulation = GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, componentAttributesFile, 
				groupIdAttributeNameOnGroupEntity, groupIdAttributeNameOnComponentEntity, groupSizeAttributeName, nbOfGroupEntities);
		
		List<AbstractAttribute> componentPopulationAttributes = null;
		int nbOfComponentEntities = 0;
		for (Entity groupEntity : generatedCompoundPopulation.getEntities()) {
			for (IPopulation componentPopulation : groupEntity.getComponentPopulations()) {
				nbOfComponentEntities += componentPopulation.getNbOfEntities();
				if (componentPopulationAttributes == null) {
					componentPopulationAttributes = componentPopulation.getAttributes();
				}
			}
		}
		
		final String groupPopulationOutputFile = "test_data/ummisco/genstar/util/GenstarUtils/testWritePopulationToCSVFile/compoundPopulation/group_population.csv";
		final String componentPopulationOutputFile = "test_data/ummisco/genstar/util/GenstarUtils/testWritePopulationToCSVFile/compoundPopulation/component_population.csv";
		Map<String, String> generatedCompoundPopulationFilePaths = new HashMap<String, String>();
		generatedCompoundPopulationFilePaths.put(groupPopulationName, groupPopulationOutputFile);
		generatedCompoundPopulationFilePaths.put(componentPopulationName, componentPopulationOutputFile);
		

		Map<String, String> resultCompoundFilePaths = GenstarUtils.writePopulationToCSVFile(generatedCompoundPopulation, generatedCompoundPopulationFilePaths);
		
		assertTrue(resultCompoundFilePaths.size() == 2);
		assertTrue(resultCompoundFilePaths.get(groupPopulationName).equals(groupPopulationOutputFile));
		assertTrue(resultCompoundFilePaths.get(componentPopulationName).equals(componentPopulationOutputFile));
		
	
		// verify group population
		// verify that the header contains attribute names on entity
		GenstarCSVFile groupPopOutputFile = new GenstarCSVFile(resultCompoundFilePaths.get(groupPopulationName), true);
		List<String> groupPopHeader = groupPopOutputFile.getHeaders();
		List<AbstractAttribute> groupPopulationAttributes = generatedCompoundPopulation.getAttributes();
		assertTrue(groupPopHeader.size() == groupPopulationAttributes.size());
		for (int i=0; i<groupPopHeader.size(); i++) {
			assertTrue(groupPopHeader.get(i).equals(groupPopulationAttributes.get(i).getNameOnEntity()));
		}
		
		assertTrue(groupPopOutputFile.getRows() == nbOfGroupEntities + 1);
		

		// verify component population
		GenstarCSVFile componentPopOutputFile = new GenstarCSVFile(resultCompoundFilePaths.get(componentPopulationName), true);
		List<String> componentPopHeader = componentPopOutputFile.getHeaders();
		assertTrue(componentPopHeader.size() == componentPopulationAttributes.size());
		for (int i=0; i<componentPopHeader.size(); i++) {
			assertTrue(componentPopHeader.get(i).equals(componentPopulationAttributes.get(i).getNameOnEntity()));
		}
		
		assertTrue(componentPopOutputFile.getRows() == nbOfComponentEntities + 1);
	}
	
	
	@Test public void testWriteAnalysisResultToFile() throws GenstarException {
		
		// delete the result file if necessary
		String csvOutputFilePath = "test_data/ummisco/genstar/util/GenstarUtils/testWriteAnalysisResultToFile/analysis_result.csv";
		File resultFile = new File(csvOutputFilePath);
		if (resultFile.exists()) { resultFile.delete(); }

		
		List<Integer> analysisResult = new ArrayList<Integer>();
		analysisResult.add(1);
		analysisResult.add(2);
		analysisResult.add(3);
		
		String controlTotalsFilePath = "test_data/ummisco/genstar/util/GenstarUtils/testWriteAnalysisResultToFile/control_totals.csv";
		GenstarCSVFile controlTotalsFile = new GenstarCSVFile(controlTotalsFilePath, false);
		
		GenstarCSVFile resultingFile = IpfUtils.writeAnalysisResultToFile(controlTotalsFile, analysisResult, csvOutputFilePath);
		
		assertTrue(resultingFile.getRows() == 3);
		assertTrue(resultingFile.getColumns() == 3);

		// row1 verification
		List<String> resultingRow1 = resultingFile.getRow(0);
		assertTrue(resultingRow1.get(0).equals("A"));
		assertTrue(resultingRow1.get(1).equals("1"));
		assertTrue(resultingRow1.get(2).equals("1"));

	
		// row2 verification
		List<String> resultingRow2 = resultingFile.getRow(1);
		assertTrue(resultingRow2.get(0).equals("B"));
		assertTrue(resultingRow2.get(1).equals("2"));
		assertTrue(resultingRow2.get(2).equals("2"));

	
		// row3 verification
		List<String> resultingRow3 = resultingFile.getRow(2);
		assertTrue(resultingRow3.get(0).equals("C"));
		assertTrue(resultingRow3.get(1).equals("3"));
		assertTrue(resultingRow3.get(2).equals("3"));
	}
	
	
	@Test public void testGenerateAttributeValuesFrequencies() throws GenstarException {
		fail("not yet implemented");
	}
}
