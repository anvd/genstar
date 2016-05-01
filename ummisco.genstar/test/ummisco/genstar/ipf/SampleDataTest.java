package ummisco.genstar.ipf;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.MultipleRulesGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;

@RunWith(JMockit.class)
public class SampleDataTest {

	@Test(expected = GenstarException.class) public void testInitializeSampleDataWithDuplicatedAttributes() throws GenstarException {
		final ISyntheticPopulationGenerator generator = new SingleRuleGenerator("generator");
		
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/sample_data/testGetSamtestInitializeSampleDataWithDuplicatedAttributespleEntities/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		List<AbstractAttribute> duplicatedAttributes = generator.getAttributes();
		duplicatedAttributes.add(duplicatedAttributes.get(0));
		
		final GenstarCsvFile sampleDataCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/sample_data/testGetSampleEntities/people_sample.csv", true);
		
		new SampleData("people", duplicatedAttributes, sampleDataCSVFile);
	}
	
	@Test public void testGetSampleEntities() throws GenstarException {
		final ISyntheticPopulationGenerator generator = new SingleRuleGenerator("generator");
		
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/sample_data/testGetSampleEntities/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		final GenstarCsvFile sampleDataCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/sample_data/testGetSampleEntities/people_sample.csv", true);
		
		SampleData sampleData = new SampleData("people", generator.getAttributes(), sampleDataCSVFile);
		assertTrue(sampleData.getSampleEntityPopulation().getEntities().size() == 4);
	}
	
	@Test public void testCountMatchingEntitiesByAttributeValuesOnEntity() throws GenstarException {
		final ISyntheticPopulationGenerator generator = new MultipleRulesGenerator("generator", 10);
		
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/sample_data/testCountMatchingEntitiesByAttributeValuesOnEntity/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		final GenstarCsvFile sampleDataCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/sample_data/testCountMatchingEntitiesByAttributeValuesOnEntity/people_sample.csv", true);
		
		SampleData sampleData = new SampleData("people", generator.getAttributes(), sampleDataCSVFile);
		/*
			Household Size,Household Income,Household Type,Number Of Cars
			1,High,type1,1
			2,Low,type2,2
			3,High,type3,1
			2,High,type1,3 
		 */
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		
		AbstractAttribute householdSizeAttr = generator.getAttributeByNameOnEntity("householdSize");
		
		AttributeValue householdSizeOne = new UniqueValue(householdSizeAttr.getDataType(), "1");
		matchingCriteria.put(householdSizeAttr, householdSizeOne);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria) == 1);
		
		AttributeValue householdSizeTwo = new UniqueValue(householdSizeAttr.getDataType(), "2");
		matchingCriteria.put(householdSizeAttr, householdSizeTwo);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria) == 2);

		AttributeValue householdFour = new UniqueValue(householdSizeAttr.getDataType(), "4");
		matchingCriteria.put(householdSizeAttr, householdFour);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria) == 0);
		
		
		matchingCriteria.clear();
		AbstractAttribute typeAttr = generator.getAttributeByNameOnData("Household Type");
		
		AttributeValue typeOne = new UniqueValue(typeAttr.getDataType(), "type1");
		matchingCriteria.put(typeAttr, typeOne);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria) == 2);
		
		AttributeValue typeThree = new UniqueValue(typeAttr.getDataType(), "type3");
		matchingCriteria.put(typeAttr, typeThree);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria) == 1);
		
		
		matchingCriteria.clear();
		AbstractAttribute incomeAttr = generator.getAttributeByNameOnData("Household Income");
		AttributeValue incomeHigh = new UniqueValue(incomeAttr.getDataType(), "High");
		matchingCriteria.put(incomeAttr, incomeHigh);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria) == 3);
		
		
		matchingCriteria.clear();
		matchingCriteria.put(householdSizeAttr, householdSizeTwo);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria) == 2);
		matchingCriteria.put(incomeAttr, incomeHigh);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria) == 1);
		matchingCriteria.put(typeAttr, typeOne);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria) == 1);
		
		
		AbstractAttribute nbOfCarsAttr = generator.getAttributeByNameOnData("Number Of Cars");
		
		AttributeValue carsOne = new UniqueValue(nbOfCarsAttr.getDataType(), "1");
		matchingCriteria.put(nbOfCarsAttr, carsOne);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria) == 0);
		
		AttributeValue carsThree = new UniqueValue(nbOfCarsAttr.getDataType(), "3");
		matchingCriteria.put(nbOfCarsAttr, carsThree);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria) == 1);
	}
	
	@Test(expected = GenstarException.class) public void testInitializeSampleEntitiesWithMismatchedAttributes() throws GenstarException {
		ISingleRuleGenerator generator = new SingleRuleGenerator("generator");
		
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/sample_data/testInitializeSampleEntitiesWithMismatchedAttributes/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		GenstarCsvFile data = new GenstarCsvFile("test_data/ummisco/genstar/ipf/sample_data/testInitializeSampleEntitiesWithMismatchedAttributes/PICURS_People_SampleData.csv", true);
		
		new SampleData("dummy population", generator.getAttributes(), data);
	}
}
