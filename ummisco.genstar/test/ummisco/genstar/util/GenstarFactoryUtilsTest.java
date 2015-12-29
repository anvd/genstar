package ummisco.genstar.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

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
		
		GenstarFactoryUtils.createUniqueValueAttribute(mockedGenerator, "Category", "category", DataType.STRING, "C0; C1; C2; C3; C4; C5; C6; C7", UniqueValue.class);
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
		GenstarFactoryUtils.createRangeValueAttribute(mockedGenerator, "Age", "age", DataType.INTEGER, "0:4; 5:17; 18:24; 25:34; 35:49; 50:64; 65:100", 
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
			result = GenstarFactoryUtils.CSV_FILE_FORMATS.ATTRIBUTE_METADATA.NB_OF_COLS;

			generator.addAttribute((AbstractAttribute) any);
			times = 3;
		}};
		
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, mockedAttributesCSVFile);
	}	
	
	
	@Test public void testCreateFrequencyDistributionGenerationFromSampleData() throws GenstarException {
		// input: distributionFormatCSVFile & sampleDataCSVFile
		// output: the newly created FrequencyDistributionGenerationRule
		
		ISyntheticPopulationGenerator generator = new MultipleRulesGenerator("dummy generator", 10);
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateFrequencyDistributionGenerationFromSampleData/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		
		GenstarCSVFile distributionFormatCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateFrequencyDistributionGenerationFromSampleData/distributionFormat.csv", true);
		GenstarCSVFile sampleDataCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateFrequencyDistributionGenerationFromSampleData/sampleData.csv", true);
		
		FrequencyDistributionGenerationRule rule = GenstarFactoryUtils.createFrequencyDistributionFromSampleData(generator, distributionFormatCSVFile, sampleDataCSVFile);
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
			if (f.isMatch(map1)) {
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
			if (f.isMatch(map2)) {
				if (isMatchMap2) { Assert.fail("Attributevalue is matched several times (map2)"); }
				
				assertTrue(f.getFrequency() == 2);
				isMatchMap2 = true;
			}
		}
		if (!isMatchMap2) { Assert.fail("No attributeValue is matched (map2)"); }
	}		
	
	@Test(expected = GenstarException.class) public void testGenerateRandomSinglePopulationWithNullAttributesFile() throws GenstarException {
		GenstarFactoryUtils.generateRandomSinglePopulation("dummy population", null, 1);
	}
	
	@Test(expected = GenstarException.class) public void testGenerateRandomSinglePopulationWithNegativeEntities(@Mocked final GenstarCSVFile attributesFile) throws GenstarException {
		GenstarFactoryUtils.generateRandomSinglePopulation("dummy population", attributesFile, 0);
	}
	
	@Test public void testGenerateRandomSinglePopulationSuccessfully() throws GenstarException {
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testGenerateRandomSinglePopulation/attributes.csv", true);
		int nbEntities = 100 + SharedInstances.RandomNumberGenerator.nextInt(100);
		
		ISyntheticPopulation generatedPopulation = GenstarFactoryUtils.generateRandomSinglePopulation("dummy population", attributesFile, nbEntities);
		
		assertTrue(generatedPopulation.getEntities().size() == nbEntities);
		assertTrue(generatedPopulation.getEntities().get(0).getEntityAttributeValues().size() == generatedPopulation.getAttributes().size());
	}
	
	@Test public void testGenerateGroupPopulationSuccessfully() throws GenstarException {
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testGenerateGroupPopulation/group_attributes.csv", true);
		
		String populationName = "household";
		
		ISingleRuleGenerator groupGenerator = new SingleRuleGenerator("group dummy generator");
		GenstarFactoryUtils.createAttributesFromCSVFile(groupGenerator, groupAttributesFile);
		List<AbstractAttribute> groupAttributes = new ArrayList<AbstractAttribute>(groupGenerator.getAttributes());
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		
		int nbOfGroups = 100;
		
		ISyntheticPopulation groupPopulation = Deencapsulation.invoke(GenstarFactoryUtils.class, "generateGroupPopulation", populationName, groupAttributes, 
				groupIdAttributeOnGroupEntity, nbOfGroups);
		
		assertTrue(groupPopulation.getEntities().size() == nbOfGroups);
		assertTrue(groupPopulation.getEntities().get(0).getEntityAttributeValues().size() == groupAttributesFile.getRows() - 1);
	}
	
	@Test public void testGenerateComponentPopulation() throws GenstarException {
		
		// generate group population
		String groupPopulationName = "household";
		
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testGenerateComponentPopulation/group_attributes.csv", true);

		ISingleRuleGenerator groupGenerator = new SingleRuleGenerator("group dummy generator");
		GenstarFactoryUtils.createAttributesFromCSVFile(groupGenerator, groupAttributesFile);
		List<AbstractAttribute> groupAttributes = new ArrayList<AbstractAttribute>(groupGenerator.getAttributes());
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		AbstractAttribute groupSizeAttribute = groupGenerator.getAttributeByNameOnData("Household Size");
		
		int nbOfGroups = 100;
		ISyntheticPopulation groupPopulation = Deencapsulation.invoke(GenstarFactoryUtils.class, "generateGroupPopulation", groupPopulationName, groupAttributes, 
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
		GenstarFactoryUtils.createAttributesFromCSVFile(componentGenerator, componentAttributesFile);
		List<AbstractAttribute> componentAttributes = new ArrayList<AbstractAttribute>(componentGenerator.getAttributes());
		AbstractAttribute groupIdAttributeOnComponentEntity = componentGenerator.getAttributeByNameOnData("Household ID"); 
		groupIdAttributeOnComponentEntity.setIdentity(true);
		
		// generate component entities
		Deencapsulation.invoke(GenstarFactoryUtils.class, "generateComponentPopulation",  groupPopulation, componentPopulationName, 
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
		
		ISyntheticPopulation generatedCompoundPopulation = GenstarFactoryUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, componentAttributesFile, 
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
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		GenstarCSVFile sampleFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateSampleDataGenerationRule/sample_data.csv", true);
		GenstarCSVFile controlledAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateSampleDataGenerationRule/controlled_attributes.csv", false);
		GenstarCSVFile controlledTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateSampleDataGenerationRule/control_totals.csv", false);
		GenstarCSVFile supplementaryAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateSampleDataGenerationRule/supplementary_attributes.csv", false);
		
		
		AbstractAttribute householdIdAttribute = generator.getAttributeByNameOnEntity("householdID");
		
		GenstarFactoryUtils.createSampleDataGenerationRule(generator, "sample data generation rule", sampleFile, controlledAttributesFile, controlledTotalsFile, supplementaryAttributesFile, householdIdAttribute);
		
		SampleDataGenerationRule rule = (SampleDataGenerationRule) generator.getGenerationRule();
		assertTrue(rule.getSampleData() instanceof SampleData);
		assertTrue(generator.getNbOfEntities() == rule.getIPF().getNbOfEntitiesToGenerate());
	}
	
	
	@Test public void testCreateGroupComponentSampleDataGenerationRule() throws GenstarException {
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testCreateGroupComponentSampleDataGenerationRule/group_attributes.csv", true);
		
		ISingleRuleGenerator groupGenerator = new SingleRuleGenerator("group rule generator");
		GenstarFactoryUtils.createAttributesFromCSVFile(groupGenerator, attributesFile);
		
		
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
		supplementaryProperties.put(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_GROUP_PROPERTY, groupIdAttributeNameOnGroup);
		supplementaryProperties.put(GenstarFactoryUtils.SAMPLE_DATA_POPULATION_PROPERTIES.GROUP_ID_ATTRIBUTE_ON_COMPONENT_PROPERTY, groupIdAttributeNameOnComponent);
		 
		
		GenstarFactoryUtils.createGroupComponentSampleDataGenerationRule(groupGenerator, "group component sample data generation rule", groupSampleFile, groupControlledAttributesFile, groupControlledTotalsFile, 
				groupSupplementaryAttributesFile, componentSampleFile, componentAttributesFile, componentPopulationName, supplementaryProperties);
		
		SampleDataGenerationRule rule = (SampleDataGenerationRule)groupGenerator.getGenerationRule();
		assertTrue(rule.getSampleData() instanceof GroupComponentSampleData);
		assertTrue(rule.getIPF().getNbOfEntitiesToGenerate() == groupGenerator.getNbOfEntities());
	}
	

	@Test public void testWritePopulationToCSVFile() throws GenstarException {
		
		// test write single population
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testWritePopulationToCSVFile/singlePopulation/attributes.csv", true);
		int nbEntities = 100 + SharedInstances.RandomNumberGenerator.nextInt(100);
		
		Map<String, String> generatedSinglePopulationFilePaths = new HashMap<String, String>();
		String singlePopulationName = "single population";
		String singlePopulationOutputFile = "test_data/ummisco/genstar/util/testWritePopulationToCSVFile/singlePopulation/single_population.csv";
		generatedSinglePopulationFilePaths.put(singlePopulationName, singlePopulationOutputFile);
		
		ISyntheticPopulation generatedSinglePopulation = GenstarFactoryUtils.generateRandomSinglePopulation(singlePopulationName, attributesFile, nbEntities);
		Map<String, String> resultSingleFilePaths = GenstarFactoryUtils.writePopulationToCSVFile(generatedSinglePopulation, generatedSinglePopulationFilePaths);
		
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
		
		
		// test write compound population
		String groupPopulationName = "household";
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testWritePopulationToCSVFile/compoundPopulation/group_attributes.csv", true);
		
		String componentPopulationName = "people";
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/testWritePopulationToCSVFile/compoundPopulation/component_attributes.csv", true);

		String groupIdAttributeNameOnGroupEntity = "Household ID";
		String groupIdAttributeNameOnComponentEntity = "Household ID";
		String groupSizeAttributeName = "Household Size";
		int nbOfGroupEntities = 100;
		
		ISyntheticPopulation generatedCompoundPopulation = GenstarFactoryUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, componentAttributesFile, 
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
		
		String groupPopulationOutputFile = "test_data/ummisco/genstar/util/testWritePopulationToCSVFile/compoundPopulation/group_population.csv";
		String componentPopulationOutputFile = "test_data/ummisco/genstar/util/testWritePopulationToCSVFile/compoundPopulation/component_population.csv";
		Map<String, String> generatedCompoundPopulationFilePaths = new HashMap<String, String>();
		generatedCompoundPopulationFilePaths.put(groupPopulationName, groupPopulationOutputFile);
		generatedCompoundPopulationFilePaths.put(componentPopulationName, componentPopulationOutputFile);
		
		Map<String, String> resultCompoundFilePaths = GenstarFactoryUtils.writePopulationToCSVFile(generatedCompoundPopulation, generatedCompoundPopulationFilePaths);
		
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
}
