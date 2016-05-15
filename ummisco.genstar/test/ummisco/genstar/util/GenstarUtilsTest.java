package ummisco.genstar.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import ummisco.genstar.ipf.IpfGenerationRule;
import ummisco.genstar.ipu.IpuGenerationRule;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.metamodel.sample_data.CompoundSampleData;
import ummisco.genstar.metamodel.sample_data.ISampleData;
import ummisco.genstar.metamodel.sample_data.SampleData;
import ummisco.genstar.sample_free.FrequencyDistributionGenerationRule;

@RunWith(JMockit.class)
public class GenstarUtilsTest {


	@Test public void testCreateFrequencyDistributionGenerationFromSampleData() throws GenstarException {
		// input: distributionFormatCSVFile & sampleDataCSVFile
		// output: the newly created FrequencyDistributionGenerationRule
		
		SampleFreeGenerator generator = new SampleFreeGenerator("dummy generator", 10);
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateFrequencyDistributionGenerationFromSampleData/attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
		
		
		GenstarCsvFile distributionFormatCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateFrequencyDistributionGenerationFromSampleData/distributionFormat.csv", true);
		GenstarCsvFile sampleDataCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testCreateFrequencyDistributionGenerationFromSampleData/sampleData.csv", true);
		
		FrequencyDistributionGenerationRule rule = GenstarUtils.createFrequencyDistributionGenerationRuleFromSampleData(generator, distributionFormatCSVFile, sampleDataCSVFile);
		assertTrue(rule.getAttributes().size() == 2);
		assertTrue(rule.getInputAttributeAtOrder(0) == null);
		assertTrue(rule.getOutputAttributeAtOrder(0).getNameOnData().equals("Category"));
		assertTrue(rule.getOutputAttributeAtOrder(1).getNameOnData().equals("Age"));
		int totalNbOfAttributeValues = 1;
		for (AbstractAttribute a : rule.getAttributes()) { totalNbOfAttributeValues *= a.valuesOnData().size(); }
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
		Set<AttributeValue> ageValues = ageAttribute.valuesOnData();
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
	
	@Test(expected = GenstarException.class) public void testGenerateRandomSinglePopulationWithNegativeEntities(@Mocked final GenstarCsvFile attributesFile) throws GenstarException {
		GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile, 0);
	}
	
	
	@Test(expected = GenstarException.class) public void testWriteContentToCsvFileWithNullContent() throws GenstarException {
		GenstarUtils.writeContentToCsvFile(null, "");
	}
	
	@Test(expected = GenstarException.class) public void testWriteContentTotalsToCsvFileWithNullCsvFilePath() throws GenstarException {
		GenstarUtils.writeContentToCsvFile(new ArrayList<List<String>>(), null);
	}
	
	@Test public void testWriteContentToCsvFile() throws GenstarException {
		GenstarCsvFile controlledAttributesFile1 = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testWriteControlTotalsToCsvFile/controlled_attributes1.csv", true);
		List<List<String>> controlTotals = IpfUtils.generateIpfControlTotals(controlledAttributesFile1, 500);
		
		String controlTotalsFilePath = "test_data/ummisco/genstar/util/GenstarUtils/testWriteControlTotalsToCsvFile/control_totals1.csv";
		File controlTotalsFile = new File(controlTotalsFilePath);
		if (controlTotalsFile.exists()) { controlTotalsFile.delete(); }
		controlTotalsFile = null;
		
		GenstarUtils.writeContentToCsvFile(controlTotals, controlTotalsFilePath);

		GenstarCsvFile controlTotalsCsvFile = new GenstarCsvFile(controlTotalsFilePath, false);
		assertTrue(controlTotalsCsvFile.getRows() == controlTotals.size()); // number of rows
		assertTrue(controlTotalsCsvFile.getColumns() == 7);  // number of columns (3 attributes + frequency)
	}
	
	@Test public void testGenerateRandomSinglePopulationSuccessfully1() throws GenstarException {
		GenstarCsvFile attributesFile1 = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateRandomSinglePopulation1/attributes1.csv", true);
		IPopulation generatedPopulation = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile1, 1, 1);
		
		int nbOfEntities1 = 1;
		for (AbstractAttribute attribute : generatedPopulation.getAttributes()) { nbOfEntities1 *= attribute.valuesOnData().size(); }
		assertTrue(generatedPopulation.getEntities().size() == nbOfEntities1);
		
		generatedPopulation = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile1, 2, 2);
		assertTrue(generatedPopulation.getEntities().size() == 2 * nbOfEntities1);
		
		generatedPopulation = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile1, 1, 2);
		assertTrue((generatedPopulation.getEntities().size() <= 2 * nbOfEntities1) && (generatedPopulation.getEntities().size() >= nbOfEntities1));

	
		GenstarCsvFile attributesFile2 = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateRandomSinglePopulation1/attributes2.csv", true);
		IPopulation generatedPopulation2 = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile2, 1, 1);
		
		int nbOfEntities2 = 1;
		for (AbstractAttribute attribute : generatedPopulation2.getAttributes()) { nbOfEntities2 *= attribute.valuesOnData().size(); }
		assertTrue(generatedPopulation2.getEntities().size() == nbOfEntities2);
	}
	
	@Test public void testGenerateRandomSinglePopulationSuccessfully() throws GenstarException {
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateRandomSinglePopulation/attributes.csv", true);
		int nbEntities = 100 + SharedInstances.RandomNumberGenerator.nextInt(100);
		
		IPopulation generatedPopulation = GenstarUtils.generateRandomSinglePopulation("dummy population", attributesFile, nbEntities);
		
		assertTrue(generatedPopulation.getEntities().size() == nbEntities);
		assertTrue(generatedPopulation.getEntities().get(0).getEntityAttributeValues().size() == generatedPopulation.getAttributes().size());
	}
	
	@Test public void testGenerateGroupPopulationSuccessfully() throws GenstarException {
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateGroupPopulation/group_attributes.csv", true);
		
		String populationName = "household";
		
		SampleBasedGenerator groupGenerator = new SampleBasedGenerator("group dummy generator");
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesFile);
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
		
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateComponentPopulation/group_attributes.csv", true);

		SampleBasedGenerator groupGenerator = new SampleBasedGenerator("group dummy generator");
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesFile);
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
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateComponentPopulation/component_attributes.csv", true);
		
		SampleBasedGenerator componentGenerator = new SampleBasedGenerator("component dummy generator");
		AttributeUtils.createAttributesFromCsvFile(componentGenerator, componentAttributesFile);
		List<AbstractAttribute> componentAttributes = new ArrayList<AbstractAttribute>(componentGenerator.getAttributes());
		AbstractAttribute groupIdAttributeOnComponentEntity = componentGenerator.getAttributeByNameOnData("Household ID"); 
		groupIdAttributeOnComponentEntity.setIdentity(true);
		
		// generate component entities
		Deencapsulation.invoke(GenstarUtils.class, "generateComponentPopulation",  groupPopulation, componentPopulationName, 
				componentAttributes, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity, groupSizeAttribute, String.class);		
		
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
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateRandomCompoundPopulation/group_attributes.csv", true);
		
		String componentPopulationName = "people";
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateRandomCompoundPopulation/component_attributes.csv", true);

		String groupIdAttributeNameOnGroupEntity = "Household ID";
		String groupIdAttributeNameOnComponentEntity = "Household ID";
		String groupSizeAttributeNameOnData = "Household Size";
		int nbOfGroupEntities = 100;
		
		String componentReferenceOnGroup = "inhabitants";
		String groupReferenceOnComponent = "household";
		
		IPopulation generatedCompoundPopulation = GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, componentAttributesFile, 
				groupIdAttributeNameOnGroupEntity, groupIdAttributeNameOnComponentEntity, groupSizeAttributeNameOnData, nbOfGroupEntities, componentReferenceOnGroup, groupReferenceOnComponent);
		
		assertTrue(generatedCompoundPopulation.getEntities().size() == nbOfGroupEntities);
		assertTrue(generatedCompoundPopulation.getEntities().get(0).getEntityAttributeValues().size() == groupAttributesFile.getRows() - 1);
		
		Map<String, String> componentReferences = generatedCompoundPopulation.getComponentReferences();
		assertTrue(componentReferences.size() == 1);
		assertTrue(componentReferences.get(componentPopulationName).equals(componentReferenceOnGroup));

		Entity groupEntityWithComponents = null;
		for (Entity groupEntity : generatedCompoundPopulation.getEntities()) {
			if (!groupEntity.getComponentPopulations().isEmpty()) {
				groupEntityWithComponents = groupEntity;
				break;
			}
		}
		
		IPopulation peoplePopulation = groupEntityWithComponents.getComponentPopulation(componentPopulationName);
		assertTrue(peoplePopulation.getEntities().get(0).getEntityAttributeValues().size() == componentAttributesFile.getRows() - 1);
		
		Map<String, String> groupReferences = peoplePopulation.getGroupReferences();
		assertTrue(groupReferences.size() == 1);
		assertTrue(groupReferences.get(groupPopulationName).equals(groupReferenceOnComponent));
		
		// the number of generated component entities equal to the group size attribute value
		AttributeValue sizeAttributeValueOnGroupEntity = groupEntityWithComponents.getEntityAttributeValue(generatedCompoundPopulation.getAttributeByNameOnData(groupSizeAttributeNameOnData)).getAttributeValueOnEntity();
		assertTrue(Integer.parseInt(((UniqueValue)sizeAttributeValueOnGroupEntity).getStringValue()) == peoplePopulation.getEntities().size());
	}
	
	
	@Test public void testGenerateRandomCompoundPopulation1() throws GenstarException {

		String groupPopulationName = "household";
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateRandomCompoundPopulation1/group_attributes.csv", true);
		
		String componentPopulationName = "people";
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateRandomCompoundPopulation1/component_attributes.csv", true);

		String groupIdAttributeNameOnDataOfGroupEntity = "Household ID";
		String groupIdAttributeNameOnDataOfComponentEntity = "Household ID";
		String groupSizeAttributeNameOnData = "Household Size";
		
		String componentReferenceOnGroup = "inhabitants";
		String groupReferenceOnComponent = "household";

		int minGroupEntitiesOfEachAttributeValuesSet1 = 2;
		int maxGroupEntitiesOfEachAttributeValuesSet1 = 2;
		IPopulation generatedCompoundPopulation1 = GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, 
				componentAttributesFile, groupIdAttributeNameOnDataOfGroupEntity, groupIdAttributeNameOnDataOfComponentEntity, groupSizeAttributeNameOnData, 
				minGroupEntitiesOfEachAttributeValuesSet1, maxGroupEntitiesOfEachAttributeValuesSet1, componentReferenceOnGroup, groupReferenceOnComponent);
		
		Map<String, String> componentReferences = generatedCompoundPopulation1.getComponentReferences();
		assertTrue(componentReferences.size() == 1);
		assertTrue(componentReferences.get(componentPopulationName).equals(componentReferenceOnGroup));

		// 1. verify the number of generated group entities
		int nbOfEntities1 = 1;
		List<AbstractAttribute> compoundPopulation1AttributesWithoutID = generatedCompoundPopulation1.getAttributes();
		compoundPopulation1AttributesWithoutID.remove(generatedCompoundPopulation1.getAttributeByNameOnData(groupIdAttributeNameOnDataOfGroupEntity));
//		compoundPopulation1AttributesWithoutID.remove(generatedCompoundPopulation1.getIdentityAttribute());
		for (AbstractAttribute attribute : compoundPopulation1AttributesWithoutID) { nbOfEntities1 *= attribute.valuesOnData().size(); }
		assertTrue(generatedCompoundPopulation1.getNbOfEntities() == 2 * nbOfEntities1);
		
		// 2. verify the number of generated component entities
		AbstractAttribute groupSizeAttribute = generatedCompoundPopulation1.getAttributeByNameOnData(groupSizeAttributeNameOnData);
		for (Entity groupEntity : generatedCompoundPopulation1.getEntities()) {
			int groupSizeValue =  Integer.parseInt(((UniqueValue)groupEntity.getEntityAttributeValue(groupSizeAttribute).getAttributeValueOnEntity()).getStringValue());
			
			if (groupSizeValue > 0) {
				IPopulation componentPopulation = groupEntity.getComponentPopulation(componentPopulationName);
				assertTrue(componentPopulation.getNbOfEntities() == groupSizeValue);

				Map<String, String> groupReferences = componentPopulation.getGroupReferences();
				assertTrue(groupReferences.size() == 1);
				assertTrue(groupReferences.get(groupPopulationName).equals(groupReferenceOnComponent));
			} else {
				assertTrue(groupEntity.getComponentPopulation(componentPopulationName) == null);
			}
		}
	}
	
	
	@Test public void testLoadSinglePopulation() throws GenstarException {
		SampleBasedGenerator generator = new SampleBasedGenerator("generator");
		
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testLoadSinglePopulation/attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		GenstarCsvFile populationFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testLoadSinglePopulation/sample_data.csv", true);
		
		IPopulation population = GenstarUtils.loadSinglePopulation(PopulationType.SYNTHETIC_POPULATION, "household", generator.getAttributes(), populationFile);

		assertTrue(population.getNbOfEntities() == populationFile.getRows() - 1);

		// verify file entities' attribute values on entity with respect to populationFile content
		List<String> header = populationFile.getHeaders();
		List<Entity> entities = population.getEntities();
		List<List<String>> fileContent = populationFile.getContent();
		assertTrue(entities.size() == fileContent.size());
		for (int row=0; row<entities.size(); row++) {
			Entity e = entities.get(row);
			
			List<String> rowContent = populationFile.getRow(row);
			for (int col=0; col<rowContent.size(); col++) {
				assertTrue(((UniqueValue)e.getEntityAttributeValueByNameOnEntity(header.get(col)).getAttributeValueOnEntity()).getStringValue().equals(rowContent.get(col)));
			}
		}
	}
	
	@Test public void testLoadSinglePopulation1() throws GenstarException {
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testLoadSinglePopulation/attributes.csv", true);
		GenstarCsvFile populationFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testLoadSinglePopulation/sample_data.csv", true);
		
		IPopulation population = GenstarUtils.loadSinglePopulation(PopulationType.SYNTHETIC_POPULATION, "household", attributesFile, populationFile);

		assertTrue(population.getNbOfEntities() == populationFile.getRows() - 1);

		// verify file entities' attribute values on entity with respect to populationFile content
		List<String> header = populationFile.getHeaders();
		List<Entity> entities = population.getEntities();
		List<List<String>> fileContent = populationFile.getContent();
		assertTrue(entities.size() == fileContent.size());
		for (int row=0; row<entities.size(); row++) {
			Entity e = entities.get(row);
			
			List<String> rowContent = populationFile.getRow(row);
			for (int col=0; col<rowContent.size(); col++) {
				assertTrue(((UniqueValue)e.getEntityAttributeValueByNameOnEntity(header.get(col)).getAttributeValueOnEntity()).getStringValue().equals(rowContent.get(col)));
			}
		}
	}
	
	
	@Test public void testLoadCompoundPopulation() throws GenstarException {
		
		// 1. load (single) group population
		SampleBasedGenerator groupGenerator = new SampleBasedGenerator("group generator");
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testLoadCompoundPopulation/group_attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesFile);
		GenstarCsvFile groupPopulationFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testLoadCompoundPopulation/group_sample.csv", true);
		
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		
		IPopulation groupPopulation = GenstarUtils.loadSinglePopulation(PopulationType.SYNTHETIC_POPULATION, "household", groupGenerator.getAttributes(), groupPopulationFile);
		
		
		// 2. load (single) component population
		SampleBasedGenerator componentGenerator = new SampleBasedGenerator("component generator");
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testLoadCompoundPopulation/component_attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(componentGenerator, componentAttributesFile);
		GenstarCsvFile componentPopulationFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testLoadCompoundPopulation/component_sample.csv", true);
		
		AbstractAttribute groupIdAttributeOnComponentEntity = componentGenerator.getAttributeByNameOnData("Household ID");
		
		IPopulation componentPopulation = GenstarUtils.loadSinglePopulation(PopulationType.SYNTHETIC_POPULATION, "people", componentGenerator.getAttributes(), componentPopulationFile);
		
		// 3. load (build) compound population from group population and component population
		IPopulation compoundPopulation = GenstarUtils.loadCompoundPopulation(PopulationType.SYNTHETIC_POPULATION, groupPopulation, componentPopulation, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity);
		
		// 4. verify the number of compound entities
		assertTrue(compoundPopulation.getNbOfEntities() == groupPopulation.getNbOfEntities());
		
		// 5. verify the number of component entities
		int numberOfComponentEntities = 0;
		for (Entity compoundEntity : compoundPopulation.getEntities()) {
			IPopulation _componentPop = compoundEntity.getComponentPopulation("people");
			if (_componentPop != null) { numberOfComponentEntities += _componentPop.getNbOfEntities(); }
		}
		assertTrue(numberOfComponentEntities == componentPopulation.getNbOfEntities());
	}
	
	
	@Test public void testLoadCompoundPopulation1() throws GenstarException {
		
		final String groupPopulationName = "household";
		final GenstarCsvFile groupAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testLoadCompoundPopulation/group_attributes.csv", true);
		final GenstarCsvFile groupPopulationFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testLoadCompoundPopulation/group_sample.csv", true);
		
		final String componentPopulationName = "people";
		final GenstarCsvFile componentAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testLoadCompoundPopulation/component_attributes.csv", true);
		final GenstarCsvFile componentPopulationFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testLoadCompoundPopulation/component_sample.csv", true);
		
		final String groupIdAttributeNameOnDataOnGroupEntity = "Household ID";
		final String groupIdAttributeNameOnDataOnComponentEntity = "Household ID";
		
	 	IPopulation compoundPopulation = GenstarUtils.loadCompoundPopulation(PopulationType.SYNTHETIC_POPULATION, 
			groupPopulationName, groupAttributesFile, groupPopulationFile,
			componentPopulationName, componentAttributesFile, componentPopulationFile, 
			groupIdAttributeNameOnDataOnGroupEntity, groupIdAttributeNameOnDataOnComponentEntity, null, null);

		// verify the number of compound entities
		assertTrue(compoundPopulation.getNbOfEntities() == groupPopulationFile.getRows() - 1);
		
		// verify the number of component entities
		int numberOfComponentEntities = 0;
		for (Entity compoundEntity : compoundPopulation.getEntities()) {
			IPopulation _componentPop = compoundEntity.getComponentPopulation("people");
			if (_componentPop != null) { numberOfComponentEntities += _componentPop.getNbOfEntities(); }
		}
		assertTrue(numberOfComponentEntities == componentPopulationFile.getRows() - 1);
	}	
	

	@Test public void testWriteSinglePopulationToCsvFile(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
		// test write single population
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testWritePopulationToCsvFile/singlePopulation/attributes.csv", true);
		int nbEntities = 100 + SharedInstances.RandomNumberGenerator.nextInt(100);
		
		Map<String, String> generatedSinglePopulationFilePaths = new HashMap<String, String>();
		String singlePopulationName = "single population";
		final String singlePopulationOutputFile = "test_data/ummisco/genstar/util/GenstarUtils/testWritePopulationToCsvFile/singlePopulation/single_population.csv";
		generatedSinglePopulationFilePaths.put(singlePopulationName, singlePopulationOutputFile);
		
		
		IPopulation generatedSinglePopulation = GenstarUtils.generateRandomSinglePopulation(singlePopulationName, attributesFile, nbEntities);
		Map<String, String> resultSingleFilePaths = GenstarUtils.writePopulationToCsvFile(generatedSinglePopulation, generatedSinglePopulationFilePaths);
		
		assertTrue(resultSingleFilePaths.size() == 1);
		assertTrue(resultSingleFilePaths.get(singlePopulationName).equals(singlePopulationOutputFile));
		
		GenstarCsvFile singlePopOutputFile = new GenstarCsvFile(resultSingleFilePaths.get(singlePopulationName), true);
		
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
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testWritePopulationToCsvFile/compoundPopulation/group_attributes.csv", true);
		
		String componentPopulationName = "people";
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testWritePopulationToCsvFile/compoundPopulation/component_attributes.csv", true);

		String groupIdAttributeNameOnGroupEntity = "Household ID";
		String groupIdAttributeNameOnComponentEntity = "Household ID";
		String groupSizeAttributeName = "Household Size";
		int nbOfGroupEntities = 100;
		
		IPopulation generatedCompoundPopulation = GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, componentAttributesFile, 
				groupIdAttributeNameOnGroupEntity, groupIdAttributeNameOnComponentEntity, groupSizeAttributeName, nbOfGroupEntities, null, null);
		
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
		
		final String groupPopulationOutputFile = "test_data/ummisco/genstar/util/GenstarUtils/testWritePopulationToCsvFile/compoundPopulation/group_population.csv";
		final String componentPopulationOutputFile = "test_data/ummisco/genstar/util/GenstarUtils/testWritePopulationToCsvFile/compoundPopulation/component_population.csv";
		Map<String, String> generatedCompoundPopulationFilePaths = new HashMap<String, String>();
		generatedCompoundPopulationFilePaths.put(groupPopulationName, groupPopulationOutputFile);
		generatedCompoundPopulationFilePaths.put(componentPopulationName, componentPopulationOutputFile);
		

		Map<String, String> resultCompoundFilePaths = GenstarUtils.writePopulationToCsvFile(generatedCompoundPopulation, generatedCompoundPopulationFilePaths);
		
		assertTrue(resultCompoundFilePaths.size() == 2);
		assertTrue(resultCompoundFilePaths.get(groupPopulationName).equals(groupPopulationOutputFile));
		assertTrue(resultCompoundFilePaths.get(componentPopulationName).equals(componentPopulationOutputFile));
		
	
		// verify group population
		// verify that the header contains attribute names on entity
		GenstarCsvFile groupPopOutputFile = new GenstarCsvFile(resultCompoundFilePaths.get(groupPopulationName), true);
		List<String> groupPopHeader = groupPopOutputFile.getHeaders();
		List<AbstractAttribute> groupPopulationAttributes = generatedCompoundPopulation.getAttributes();
		assertTrue(groupPopHeader.size() == groupPopulationAttributes.size());
		for (int i=0; i<groupPopHeader.size(); i++) {
			assertTrue(groupPopHeader.get(i).equals(groupPopulationAttributes.get(i).getNameOnEntity()));
		}
		
		assertTrue(groupPopOutputFile.getRows() == nbOfGroupEntities + 1);
		

		// verify component population
		GenstarCsvFile componentPopOutputFile = new GenstarCsvFile(resultCompoundFilePaths.get(componentPopulationName), true);
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
		GenstarCsvFile controlTotalsFile = new GenstarCsvFile(controlTotalsFilePath, false);
		
		GenstarCsvFile resultingFile = IpfUtils.writeAnalysisResultToFile(controlTotalsFile, analysisResult, csvOutputFilePath);
		
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
		
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("generator");
		
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateAttributeValuesFrequencies/attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		Set<AttributeValuesFrequency> avfs = GenstarUtils.generateAttributeValuesFrequencies(new HashSet<AbstractAttribute>(generator.getAttributes()));
		
		assertTrue(avfs.size() == 72);
	}
}
