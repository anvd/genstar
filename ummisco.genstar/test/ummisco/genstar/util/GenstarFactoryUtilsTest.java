package ummisco.genstar.util;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import mockit.Deencapsulation;
import mockit.Expectations;
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
import ummisco.genstar.metamodel.ISyntheticPopulation;
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
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;
import ummisco.genstar.util.GenstarUtils.CSV_FILE_FORMATS;

@RunWith(JMockit.class)
public class GenstarFactoryUtilsTest {

	@Test
	public final void testCreateUniqueValueAttribut(@Mocked final UniqueValuesAttribute attribute, @Mocked final UniqueValue uValue, @Mocked final ISyntheticPopulationGenerator mockedGenerator) throws GenstarException {
		
		new Expectations() {{
			new UniqueValuesAttribute(mockedGenerator, "Category", "category", DataType.STRING, UniqueValue.class);
			times = 1;
			
			new UniqueValue((DataType) any, anyString);
			times = 8;
			
			attribute.add((UniqueValue) any);
			times = 8;
			
			mockedGenerator.addAttribute((AbstractAttribute) any);
			times = 1;
		}};
		
		GenstarUtils.createUniqueValueAttribute(mockedGenerator, "Category", "category", DataType.STRING, "C0; C1; C2; C3; C4; C5; C6; C7", UniqueValue.class);
	}


	@Test
	public final void testCreateRangeValueAttribute(@Mocked final ISyntheticPopulationGenerator mockedGenerator,
			@Mocked final RangeValuesAttribute mockedRangeValuesAttribute, @Mocked final RangeValue mockedRangeValue) throws GenstarException {
		
		// record
		new Expectations() {{
			new RangeValuesAttribute(mockedGenerator, "Age", "age", DataType.INTEGER, UniqueValue.class);
			times = 1;
			
			new RangeValue(DataType.INTEGER, (String) withNotNull(), (String) withNotNull());
			times = 7;
			
			mockedRangeValuesAttribute.add((RangeValue) any);
			times = 7;
			
			mockedGenerator.addAttribute((AbstractAttribute) any);
			times = 1;
		}};
		
		// replay
		GenstarUtils.createRangeValueAttribute(mockedGenerator, "Age", "age", DataType.INTEGER, "0:4; 5:17; 18:24; 25:34; 35:49; 50:64; 65:100", 
				UniqueValue.class);
	}


	@Test
	public final void testCreateAttributesFromCSVFileExpectationAproach(@Mocked final ISyntheticPopulationGenerator generator, @Mocked final GenstarCSVFile mockedAttributesCSVFile) throws GenstarException {
		
		final List<String> headers = new ArrayList<String>();
		headers.add("Name On Data");
		headers.add("Name On Entity");
		headers.add("Data Type");
		headers.add("Value Type On Data");
		headers.add("Values");
		headers.add("Value Type On Entity");

		final List<String> line0 = new ArrayList<String>();
		line0.add("Category");
		line0.add("category");
		line0.add("string");
		line0.add("Unique");
		line0.add("C0; C1; C2; C3; C4; C5; C6; C7");
		line0.add("Unique");
		
		final List<String> line1 = new ArrayList<String>();
		line1.add("Gender");
		line1.add("gender");
		line1.add("bool");
		line1.add("Unique");
		line1.add("true; false");
		line1.add("Unique");
		
		final List<String> line2 = new ArrayList<String>();
		line2.add("Age");
		line2.add("age");
		line2.add("int");
		line2.add("Range");
		line2.add("0:4; 5:17; 18:24; 25:34; 35:49; 50:64; 65:100");
		line2.add("Unique");
		
		final List<List<String>> content = new ArrayList<List<String>>();
		content.add(line0);
		content.add(line1);
		content.add(line2);
		
		new Expectations() {{
			mockedAttributesCSVFile.getContent();
			times = 1;
			result = content;

			mockedAttributesCSVFile.getHeaders();
			times = 1;
			result = headers;
			
			mockedAttributesCSVFile.getPath();
			result = "Dummy CSV File";
			
			mockedAttributesCSVFile.getColumns();
			result = GenstarUtils.CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS;

			generator.addAttribute((AbstractAttribute) any);
			times = 3;
		}};
		
		GenstarUtils.createAttributesFromCSVFile(generator, mockedAttributesCSVFile);
	}	
	
	
	@Test public void testCreateFrequencyDistributionGenerationFromSampleData() throws GenstarException {
		// input: distributionFormatCSVFile & sampleDataCSVFile
		// output: the newly created FrequencyDistributionGenerationRule
		
		ISyntheticPopulationGenerator generator = new MultipleRulesGenerator("dummy generator", 10);
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateFrequencyDistributionGenerationFromSampleData/attributes.csv", true);
		GenstarUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		
		GenstarCSVFile distributionFormatCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateFrequencyDistributionGenerationFromSampleData/distributionFormat.csv", true);
		GenstarCSVFile sampleDataCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateFrequencyDistributionGenerationFromSampleData/sampleData.csv", true);
		
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
			if (f.matchAttributeValues(map1)) {
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
			if (f.matchAttributeValues(map2)) {
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
	
	@Test public void testBuildControlledAttributesValuesSubsets() throws GenstarException {
		// test_data/ummisco/genstar/util/testBuildControlledAttributesValuesSubsets/controlled_attributes1.csv
		GenstarCSVFile controlledAttributesFile1 = new GenstarCSVFile("test_data/ummisco/genstar/util/testBuildControlledAttributesValuesSubsets/controlled_attributes1.csv", true);
		ISingleRuleGenerator generator1 = new SingleRuleGenerator("dummy single rule generator");
		GenstarUtils.createAttributesFromCSVFile(generator1, controlledAttributesFile1);
		
		// generate frequencies / control totals
		List<List<Map<AbstractAttribute, AttributeValue>>> controlledAttributesValuesSubsets1 = GenstarUtils.buildControlledAttributesValuesSubsets(new HashSet<AbstractAttribute>(generator1.getAttributes()));
		
		// 4 controlled attributes
		assertTrue(controlledAttributesValuesSubsets1.size() == 4);
		for (List<Map<AbstractAttribute, AttributeValue>> subset : controlledAttributesValuesSubsets1) {
			int nbPossibilities = 1;
			for (AbstractAttribute attribute : subset.get(0).keySet()) { nbPossibilities *= attribute.values().size(); }
			assertTrue(nbPossibilities == subset.size());
			
			Set<AbstractAttribute> attributeSet = subset.get(0).keySet();
			for (Map<AbstractAttribute, AttributeValue> entry : subset) {
				assertTrue(attributeSet.size() == entry.size() && attributeSet.containsAll(entry.keySet()));
			}
		}
	
	
		// test_data/ummisco/genstar/util/testBuildControlledAttributesValuesSubsets/controlled_attributes2.csv
		GenstarCSVFile controlledAttributesFile2 = new GenstarCSVFile("test_data/ummisco/genstar/util/testBuildControlledAttributesValuesSubsets/controlled_attributes2.csv", true);
		ISingleRuleGenerator generator2 = new SingleRuleGenerator("dummy single rule generator");
		GenstarUtils.createAttributesFromCSVFile(generator2, controlledAttributesFile2);
		
		// generate frequencies / control totals
		List<List<Map<AbstractAttribute, AttributeValue>>> controlledAttributesValuesSubsets2 = GenstarUtils.buildControlledAttributesValuesSubsets(new HashSet<AbstractAttribute>(generator2.getAttributes()));

		// 3 controlled attributes
		assertTrue(controlledAttributesValuesSubsets2.size() == 3);
	}
	
	@Test(expected = GenstarException.class) public void testGenerateControlTotalsWithNullControlledAttributesFile() throws GenstarException {
		GenstarUtils.generateControlTotals(null, 1);
	}
	
	@Test(expected = GenstarException.class) public void testGenerateControlTotalsWithNonPositiveTotal(@Mocked final GenstarCSVFile controlledAttributesFile) throws GenstarException {
		GenstarUtils.generateControlTotals(controlledAttributesFile, 0);
	}
	
	@Test public void testGenerateControlTotals() throws GenstarException {
		GenstarCSVFile controlledAttributesFile1 = new GenstarCSVFile("test_data/ummisco/genstar/util/testGenerateControlTotals/controlled_attributes1.csv", true);
		List<List<String>> result1 = GenstarUtils.generateControlTotals(controlledAttributesFile1, 1000);
		
		/*
			Household Size, Household Income, Household Type: 3*2*3 = 18
			Household Size, Household Income, Number Of Cars: 3*2*4 = 24
			Household Size,Household Type, Number Of Cars: 3*3*4 = 36
			Household Income, Household Type, Number Of Cars: 2*3*4 = 24
			--> 18 + 24 + 36 + 24 = 102		
		*/
		assertTrue(result1.size() == 102); 
		for (List<String> row1 : result1) { assertTrue(row1.size() == 7); }

		GenstarCSVFile controlledAttributesFile2 = new GenstarCSVFile("test_data/ummisco/genstar/util/testGenerateControlTotals/controlled_attributes2.csv", true);
		List<List<String>> result3 = GenstarUtils.generateControlTotals(controlledAttributesFile2, 10000);
		
		/*
			Household Size, Household Income: 3*2 = 6
			Household Size, Household Type: 3*3 = 9
			Household Income, Household Type: 2*3 = 6	
			6 + 9 + 6 = 21	 
		*/
		assertTrue(result3.size() == 21);
		for (List<String> row3 : result3) { assertTrue(row3.size() == 5); }
	}
	
	@Test(expected = GenstarException.class) public void testWriteControlTotalsToCsvFileWithNullControlTotals() throws GenstarException {
		GenstarUtils.writeControlTotalsToCsvFile(null, "");
	}
	
	@Test(expected = GenstarException.class) public void testWriteControlTotalsToCsvFileWithNullCsvFilePath() throws GenstarException {
		GenstarUtils.writeControlTotalsToCsvFile(new ArrayList<List<String>>(), null);
	}
	
	@Test public void testWriteControlTotalsToCsvFile() throws GenstarException {
		GenstarCSVFile controlledAttributesFile1 = new GenstarCSVFile("test_data/ummisco/genstar/util/testWriteControlTotalsToCsvFile/controlled_attributes1.csv", true);
		List<List<String>> controlTotals = GenstarUtils.generateControlTotals(controlledAttributesFile1, 500);
		
		String controlTotalsFilePath = "test_data/ummisco/genstar/util/testWriteControlTotalsToCsvFile/control_totals1.csv";
		File controlTotalsFile = new File(controlTotalsFilePath);
		if (controlTotalsFile.exists()) { controlTotalsFile.delete(); }
		controlTotalsFile = null;
		
		GenstarUtils.writeControlTotalsToCsvFile(controlTotals, controlTotalsFilePath);

		GenstarCSVFile controlTotalsCsvFile = new GenstarCSVFile(controlTotalsFilePath, false);
		assertTrue(controlTotalsCsvFile.getRows() == controlTotals.size()); // number of rows
		assertTrue(controlTotalsCsvFile.getColumns() == 7);  // number of columns (3 attributes + frequency)
	}
	
	@Test public void testGenerateRandomSinglePopulationSuccessfully1() throws GenstarException {
		GenstarCSVFile attributesFile1 = new GenstarCSVFile("test_data/ummisco/genstar/util/testGenerateRandomSinglePopulation1/attributes1.csv", true);
		ISyntheticPopulation generatedPopulation = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile1, 1, 1);
		
		int nbOfEntities1 = 1;
		for (AbstractAttribute attribute : generatedPopulation.getAttributes()) { nbOfEntities1 *= attribute.values().size(); }
		assertTrue(generatedPopulation.getEntities().size() == nbOfEntities1);
		
		generatedPopulation = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile1, 2, 2);
		assertTrue(generatedPopulation.getEntities().size() == 2 * nbOfEntities1);
		
		generatedPopulation = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile1, 1, 2);
		assertTrue((generatedPopulation.getEntities().size() <= 2 * nbOfEntities1) && (generatedPopulation.getEntities().size() >= nbOfEntities1));

	
		GenstarCSVFile attributesFile2 = new GenstarCSVFile("test_data/ummisco/genstar/util/testGenerateRandomSinglePopulation1/attributes2.csv", true);
		ISyntheticPopulation generatedPopulation2 = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile2, 1, 1);
		
		int nbOfEntities2 = 1;
		for (AbstractAttribute attribute : generatedPopulation2.getAttributes()) { nbOfEntities2 *= attribute.values().size(); }
		assertTrue(generatedPopulation2.getEntities().size() == nbOfEntities2);
	}
	
	@Test public void testGenerateRandomSinglePopulationSuccessfully() throws GenstarException {
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testGenerateRandomSinglePopulation/attributes.csv", true);
		int nbEntities = 100 + SharedInstances.RandomNumberGenerator.nextInt(100);
		
		ISyntheticPopulation generatedPopulation = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile, nbEntities);
		
		assertTrue(generatedPopulation.getEntities().size() == nbEntities);
		assertTrue(generatedPopulation.getEntities().get(0).getEntityAttributeValues().size() == generatedPopulation.getAttributes().size());
	}
	
	@Test public void testGenerateGroupPopulationSuccessfully() throws GenstarException {
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testGenerateGroupPopulation/group_attributes.csv", true);
		
		String populationName = "household";
		
		ISingleRuleGenerator groupGenerator = new SingleRuleGenerator("group dummy generator");
		GenstarUtils.createAttributesFromCSVFile(groupGenerator, groupAttributesFile);
		List<AbstractAttribute> groupAttributes = new ArrayList<AbstractAttribute>(groupGenerator.getAttributes());
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		
		int nbOfGroups = 100;
		
		ISyntheticPopulation groupPopulation = Deencapsulation.invoke(GenstarUtils.class, "generateGroupPopulation", populationName, groupAttributes, 
				groupIdAttributeOnGroupEntity, nbOfGroups);
		
		assertTrue(groupPopulation.getEntities().size() == nbOfGroups);
		assertTrue(groupPopulation.getEntities().get(0).getEntityAttributeValues().size() == groupAttributesFile.getRows() - 1);
	}
	
	@Test public void testGenerateComponentPopulation() throws GenstarException {
		
		// generate group population
		String groupPopulationName = "household";
		
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testGenerateComponentPopulation/group_attributes.csv", true);

		ISingleRuleGenerator groupGenerator = new SingleRuleGenerator("group dummy generator");
		GenstarUtils.createAttributesFromCSVFile(groupGenerator, groupAttributesFile);
		List<AbstractAttribute> groupAttributes = new ArrayList<AbstractAttribute>(groupGenerator.getAttributes());
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		AbstractAttribute groupSizeAttribute = groupGenerator.getAttributeByNameOnData("Household Size");
		
		int nbOfGroups = 100;
		ISyntheticPopulation groupPopulation = Deencapsulation.invoke(GenstarUtils.class, "generateGroupPopulation", groupPopulationName, groupAttributes, 
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
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testGenerateComponentPopulation/component_attributes.csv", true);
		
		ISingleRuleGenerator componentGenerator = new SingleRuleGenerator("component dummy generator");
		GenstarUtils.createAttributesFromCSVFile(componentGenerator, componentAttributesFile);
		List<AbstractAttribute> componentAttributes = new ArrayList<AbstractAttribute>(componentGenerator.getAttributes());
		AbstractAttribute groupIdAttributeOnComponentEntity = componentGenerator.getAttributeByNameOnData("Household ID"); 
		groupIdAttributeOnComponentEntity.setIdentity(true);
		
		// generate component entities
		Deencapsulation.invoke(GenstarUtils.class, "generateComponentPopulation",  groupPopulation, componentPopulationName, 
				componentAttributes, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity, groupSizeAttribute);		
		
		// assert that the number of generated component entities is correct
		int nbOfGeneratedComponents = 0;
		for (Entity groupEntity : groupPopulation.getEntities()) {
			for (ISyntheticPopulation componentPopulation : groupEntity.getComponentPopulations()) {
				nbOfGeneratedComponents += componentPopulation.getEntities().size();
			}
		}
		
		assertTrue(nbOfComponentsToGenerate == nbOfGeneratedComponents);
	}
	
	
	@Test public void testGenerateRandomCompoundPopulation() throws GenstarException {

		String groupPopulationName = "household";
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testGenerateRandomCompoundPopulation/group_attributes.csv", true);
		
		String componentPopulationName = "people";
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testGenerateRandomCompoundPopulation/component_attributes.csv", true);

		String groupIdAttributeNameOnGroupEntity = "Household ID";
		String groupIdAttributeNameOnComponentEntity = "Household ID";
		String groupSizeAttributeName = "Household Size";
		int nbOfGroupEntities = 100;
		
		ISyntheticPopulation generatedCompoundPopulation = GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, componentAttributesFile, 
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
		
		ISyntheticPopulation peoplePopulation = groupEntityWithComponents.getComponentPopulation(componentPopulationName);
		assertTrue(peoplePopulation.getEntities().get(0).getEntityAttributeValues().size() == componentAttributesFile.getRows() - 1);
	}
	
	
	@Test public void testCreateSampleDataGenerationRule() throws GenstarException {
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateSampleDataGenerationRule/attributes.csv", true);
		
		ISingleRuleGenerator generator = new SingleRuleGenerator("single rule generator");
		GenstarUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		GenstarCSVFile sampleFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateSampleDataGenerationRule/sample_data.csv", true);
		GenstarCSVFile controlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateSampleDataGenerationRule/controlled_attributes.csv", false);
		GenstarCSVFile controlledTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateSampleDataGenerationRule/control_totals.csv", false);
		GenstarCSVFile supplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateSampleDataGenerationRule/supplementary_attributes.csv", false);
		
		
		AbstractAttribute householdIdAttribute = generator.getAttributeByNameOnEntity("householdID");
		
		GenstarUtils.createSampleDataGenerationRule(generator, "sample data generation rule", sampleFile, controlledAttributesFile, controlledTotalsFile, supplementaryAttributesFile, householdIdAttribute);
		
		SampleDataGenerationRule rule = (SampleDataGenerationRule) generator.getGenerationRule();
		assertTrue(rule.getSampleData() instanceof SampleData);
		assertTrue(generator.getNbOfEntities() == rule.getIPF().getNbOfEntitiesToGenerate());
	}
	
	
	@Test public void testCreateGroupComponentSampleDataGenerationRule() throws GenstarException {
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateGroupComponentSampleDataGenerationRule/group_attributes.csv", true);
		
		ISingleRuleGenerator groupGenerator = new SingleRuleGenerator("group rule generator");
		GenstarUtils.createAttributesFromCSVFile(groupGenerator, attributesFile);
		
		
		GenstarCSVFile groupSampleFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateGroupComponentSampleDataGenerationRule/group_sample.csv", true);
		GenstarCSVFile groupControlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateGroupComponentSampleDataGenerationRule/group_controlled_attributes.csv", false);
		GenstarCSVFile groupControlledTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateGroupComponentSampleDataGenerationRule/group_control_totals.csv", false);
		GenstarCSVFile groupSupplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateGroupComponentSampleDataGenerationRule/group_supplementary_attributes.csv", false);
		
		GenstarCSVFile componentSampleFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateGroupComponentSampleDataGenerationRule/component_sample.csv", true);
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateGroupComponentSampleDataGenerationRule/component_attributes.csv", true);
		
		String groupIdAttributeNameOnGroup = "Household ID";
		String groupIdAttributeNameOnComponent = "Household ID";
		String componentPopulationName = "people";
		
		// optional/supplementary properties (COMPONENT_REFERENCE_ON_GROUP, GROUP_REFERENCE_ON_COMPONENT)
		Map<String, String> supplementaryProperties = new HashMap<String, String>();
		supplementaryProperties.put(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY, groupIdAttributeNameOnGroup);
		supplementaryProperties.put(GenstarUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY, groupIdAttributeNameOnComponent);
		 
		
		GenstarUtils.createGroupComponentSampleDataGenerationRule(groupGenerator, "group component sample data generation rule", groupSampleFile, groupControlledAttributesFile, groupControlledTotalsFile, 
				groupSupplementaryAttributesFile, componentSampleFile, componentAttributesFile, componentPopulationName, supplementaryProperties);
		
		SampleDataGenerationRule rule = (SampleDataGenerationRule)groupGenerator.getGenerationRule();
		assertTrue(rule.getSampleData() instanceof GroupComponentSampleData);
		assertTrue(rule.getIPF().getNbOfEntitiesToGenerate() == groupGenerator.getNbOfEntities());
	}
	

	@Test public void testWriteSinglePopulationToCSVFile(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
		// test write single population
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testWritePopulationToCSVFile/singlePopulation/attributes.csv", true);
		int nbEntities = 100 + SharedInstances.RandomNumberGenerator.nextInt(100);
		
		Map<String, String> generatedSinglePopulationFilePaths = new HashMap<String, String>();
		String singlePopulationName = "single population";
		final String singlePopulationOutputFile = "test_data/ummisco/genstar/util/testWritePopulationToCSVFile/singlePopulation/single_population.csv";
		generatedSinglePopulationFilePaths.put(singlePopulationName, singlePopulationOutputFile);
		
		
		ISyntheticPopulation generatedSinglePopulation = GenstarUtils.generateRandomSinglePopulation(singlePopulationName, attributesFile, nbEntities);
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
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testWritePopulationToCSVFile/compoundPopulation/group_attributes.csv", true);
		
		String componentPopulationName = "people";
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testWritePopulationToCSVFile/compoundPopulation/component_attributes.csv", true);

		String groupIdAttributeNameOnGroupEntity = "Household ID";
		String groupIdAttributeNameOnComponentEntity = "Household ID";
		String groupSizeAttributeName = "Household Size";
		int nbOfGroupEntities = 100;
		
		ISyntheticPopulation generatedCompoundPopulation = GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, componentAttributesFile, 
				groupIdAttributeNameOnGroupEntity, groupIdAttributeNameOnComponentEntity, groupSizeAttributeName, nbOfGroupEntities);
		
		List<AbstractAttribute> componentPopulationAttributes = null;
		int nbOfComponentEntities = 0;
		for (Entity groupEntity : generatedCompoundPopulation.getEntities()) {
			for (ISyntheticPopulation componentPopulation : groupEntity.getComponentPopulations()) {
				nbOfComponentEntities += componentPopulation.getNbOfEntities();
				if (componentPopulationAttributes == null) {
					componentPopulationAttributes = componentPopulation.getAttributes();
				}
			}
		}
		
		final String groupPopulationOutputFile = "test_data/ummisco/genstar/util/testWritePopulationToCSVFile/compoundPopulation/group_population.csv";
		final String componentPopulationOutputFile = "test_data/ummisco/genstar/util/testWritePopulationToCSVFile/compoundPopulation/component_population.csv";
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
	
	
	@Test(expected = GenstarException.class) public void testFindSubsetSumWithNonPositiveTotal() throws GenstarException {
		GenstarUtils.findSubsetSum(0, 10);
	}
	
	
	@Test(expected = GenstarException.class) public void testFindSubsetSumWithNonPositiveN() throws GenstarException {
		GenstarUtils.findSubsetSum(10, 0);
	}
	
	
	@Test(expected = GenstarException.class) public void testFindSubsetSumWithTotalSmallerThanN() throws GenstarException {
		GenstarUtils.findSubsetSum(9, 10);
	}
	
	
	@Test public void testFindSubsetSum() throws GenstarException {
		List<Integer> subset1 = GenstarUtils.findSubsetSum(100000, 10);
		assertTrue(subset1.size() == 10);
		int sumSubset1 = 0;
		for (int i=0; i<subset1.size(); i++) { 
			assertTrue(subset1.get(i) >= 1);
			sumSubset1 += subset1.get(i); 
		}
		assertTrue(sumSubset1 == 100000);
		
		
		List<Integer> subset2 = GenstarUtils.findSubsetSum(1000000, 15);
		assertTrue(subset2.size() == 15);
		int sumSubset2 = 0;
		for (int i=0; i<subset2.size(); i++) {
			assertTrue(subset2.get(i) >= 1);
			sumSubset2 += subset2.get(i);
		}
		assertTrue(sumSubset2 == 1000000);
		
		List<Integer> subset3 = GenstarUtils.findSubsetSum(2, 1);
		assertTrue(subset3.size() == 1);
		assertTrue(subset3.get(subset3.size() - 1) == 2);
		
		List<Integer> subset4 = GenstarUtils.findSubsetSum(2, 2);
		assertTrue(subset4.size() == 2);
		assertTrue(subset4.get(subset4.size() - 1) == 1);
		assertTrue(subset4.get(0) == 1);
	}
	
	
	@Test public void testReadAttributeValuesFrequenciesFromControlTotalsFile() throws GenstarException {
		
		ISyntheticPopulationGenerator generator = new SingleRuleGenerator("dummy generator");
		GenstarCSVFile controlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testReadAttributeValuesFrequenciesFromControlTotalsFile/controlled_attributes1.csv", true);
		GenstarUtils.createAttributesFromCSVFile(generator, controlledAttributesFile);
		
		GenstarCSVFile controlTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testReadAttributeValuesFrequenciesFromControlTotalsFile/control_totals1.csv", false);
		
		List<AttributeValuesFrequency> avfs = GenstarUtils.readAttributeValuesFrequenciesFromControlTotalsFile(controlTotalsFile, generator.getAttributes());
		
		assertTrue(avfs.size() == controlTotalsFile.getRows());
		
		// verify order
		int line = 0;
		AbstractAttribute attribute;
		AttributeValue attributeValue;
		List<String> valueList = new ArrayList<String>();
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>(); ;
		for (List<String> aRow : controlTotalsFile.getContent()) {
			
			attributeValues.clear();
			for (int col=0; col<(aRow.size() - 1); col+=2) { // Parse each line of the file
				// 1. parse the attribute name column
				attribute = generator.getAttributeByNameOnData(aRow.get(col));
				
				// 2. parse the attribute value column
				valueList.clear();
				String attributeValueString = aRow.get(col+1);
				if (attributeValueString.contains(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER)) { // range value
					StringTokenizer rangeValueToken = new StringTokenizer(attributeValueString, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
					valueList.add(rangeValueToken.nextToken());
					valueList.add(rangeValueToken.nextToken());
				} else { // unique value
					valueList.add(attributeValueString);
				}
				
				attributeValue = attribute.findCorrespondingAttributeValue(valueList);
				attributeValues.put(attribute, attributeValue);
			}
			
			assertTrue(avfs.get(line).matchAttributeValues(attributeValues));
			
			// "frequency" is the last column
			assertTrue(avfs.get(line).getFrequency() == Integer.parseInt(aRow.get(aRow.size() - 1)));
			line++;
		}		
	}
	
	
	@Test public void testAnalyseIpfPopulation() throws GenstarException {
		
		// delete the csvOutputFile if is exists
		String csvOutputFilePath = "test_data/ummisco/genstar/util/testAnalyseIpfPopulation/analysis_result.csv";
		File outputFile = new File(csvOutputFilePath);
		if (outputFile.exists()) { outputFile.delete(); }
		
		// generate the sample data if necessary
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testAnalyseIpfPopulation/attributes.csv", true);
		String sampleFilePath = "test_data/ummisco/genstar/util/testAnalyseIpfPopulation/sample_data.csv";
		File sampleFile = new File(sampleFilePath);
		if (!sampleFile.exists()) {
			String populationName = "household population";
			ISyntheticPopulation generatedPopulation = GenstarUtils.generateRandomSinglePopulation(populationName, attributesFile, 1, 3);
			
			Map<String, String> csvFilePathsByPopulationNames = new HashMap<String, String>();
			csvFilePathsByPopulationNames.put(populationName, sampleFilePath);
			GenstarUtils.writePopulationToCSVFile(generatedPopulation, csvFilePathsByPopulationNames);
		}
		
		
		// 1. create the generator and the attributes
		ISingleRuleGenerator generator = new SingleRuleGenerator("dummy generator");
		GenstarUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		// 2. create sample data generation rule (GenstarFactoryUtils.createSampleDataGenerationRule)
		GenstarCSVFile sampleCSVFile = new GenstarCSVFile(sampleFilePath, true);
		GenstarCSVFile controlledAttributesListFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testAnalyseIpfPopulation/controlled_attributes_list.csv", false);
		GenstarCSVFile controlTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testAnalyseIpfPopulation/control_totals.csv", false);
		GenstarCSVFile supplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testAnalyseIpfPopulation/supplementary_attributes_list.csv", false);
		GenstarUtils.createSampleDataGenerationRule(generator, "dummy rule", sampleCSVFile, controlledAttributesListFile, controlTotalsFile, supplementaryAttributesFile, null);
		
		// 3. generate the population
		ISyntheticPopulation population = generator.generate();
		
		// 4. do the analysis
		List<Integer> analsysisResult = GenstarUtils.analyseIpfPopulation(population, controlledAttributesListFile, controlTotalsFile);
		
		assertTrue(analsysisResult.size() == controlTotalsFile.getRows());
	}
	
	
	@Test public void testWriteAnalsysisResultToFile() throws GenstarException {
		
		// delete the result file if necessary
		String csvOutputFilePath = "test_data/ummisco/genstar/util/testWriteAnalsysisResultToFile/analysis_result.csv";
		File resultFile = new File(csvOutputFilePath);
		if (resultFile.exists()) { resultFile.delete(); }

		
		List<Integer> analysisResult = new ArrayList<Integer>();
		analysisResult.add(1);
		analysisResult.add(2);
		analysisResult.add(3);
		
		String controlTotalsFilePath = "test_data/ummisco/genstar/util/testWriteAnalsysisResultToFile/control_totals.csv";
		GenstarCSVFile controlTotalsFile = new GenstarCSVFile(controlTotalsFilePath, false);
		
		GenstarCSVFile resultingFile = GenstarUtils.writeAnalsysisResultToFile(controlTotalsFile, analysisResult, csvOutputFilePath);
		
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
}
