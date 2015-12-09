package ummisco.genstar.ipf;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.MultipleRulesGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarFactoryUtils;

@RunWith(JMockit.class)
public class SampleDataTest {

	@Test
	public void testGetSampleEntities(@Mocked final SampleDataGenerationRule generationRule) throws GenstarException {
		final ISyntheticPopulationGenerator generator = new MultipleRulesGenerator("generator", 10);
		
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		final GenstarCSVFile sampleDataCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/people_sample.csv", true);
		
		new Expectations() {{
			generationRule.getAttributeByNameOnData(anyString);
			result = new Delegate() {
				AbstractAttribute delegateMethod(final String attributeName) {
					return generator.getAttributeByNameOnData(attributeName);
				}
			};
		}};
		
		SampleData sampleData = new SampleData("people", generator.getAttributes(), sampleDataCSVFile);
		assertTrue(sampleData.getSampleEntityPopulation().getSampleEntities().size() == 4);
	}
	
	@Test public void testCountMatchingEntities(@Mocked final SampleDataGenerationRule generationRule) throws GenstarException {
		final ISyntheticPopulationGenerator generator = new MultipleRulesGenerator("generator", 10);
		
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		final GenstarCSVFile sampleDataCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/util/people_sample.csv", true);
		
		new Expectations() {{
			generationRule.getAttributeByNameOnData(anyString);
			result = new Delegate() {
				AbstractAttribute delegateMethod(final String attributeName) {
					return generator.getAttributeByNameOnData(attributeName);
				}
			};
		}};
		
		SampleData sampleData = new SampleData("people", generator.getAttributes(), sampleDataCSVFile);
		/*
			Household Size,Household Income,Household Type,Number Of Cars
			1,High,type1,1
			2,Low,type2,2
			3,High,type3,1
			2,High,type1,3 
		 */
		Map<String, AttributeValue> matchingCriteria = new HashMap<String, AttributeValue>();
		
		AbstractAttribute householdSizeAttr = generator.getAttributeByNameOnData("Household Size");
		
		AttributeValue householdSizeOne = new UniqueValue(householdSizeAttr.getDataType(), "1");
		matchingCriteria.put(householdSizeAttr.getNameOnData(), householdSizeOne);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCriteria) == 1);
		
		AttributeValue householdSizeTwo = new UniqueValue(householdSizeAttr.getDataType(), "2");
		matchingCriteria.put(householdSizeAttr.getNameOnData(), householdSizeTwo);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCriteria) == 2);

		AttributeValue householdFour = new UniqueValue(householdSizeAttr.getDataType(), "4");
		matchingCriteria.put(householdSizeAttr.getNameOnData(), householdFour);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCriteria) == 0);
		
		
		matchingCriteria.clear();
		AbstractAttribute typeAttr = generator.getAttributeByNameOnData("Household Type");
		
		AttributeValue typeOne = new UniqueValue(typeAttr.getDataType(), "type1");
		matchingCriteria.put(typeAttr.getNameOnData(), typeOne);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCriteria) == 2);
		
		AttributeValue typeThree = new UniqueValue(typeAttr.getDataType(), "type3");
		matchingCriteria.put(typeAttr.getNameOnData(), typeThree);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCriteria) == 1);
		
		
		matchingCriteria.clear();
		AbstractAttribute incomeAttr = generator.getAttributeByNameOnData("Household Income");
		AttributeValue incomeHigh = new UniqueValue(incomeAttr.getDataType(), "High");
		matchingCriteria.put(incomeAttr.getNameOnData(), incomeHigh);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCriteria) == 3);
		
		
		matchingCriteria.clear();
		matchingCriteria.put(householdSizeAttr.getNameOnData(), householdSizeTwo);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCriteria) == 2);
		matchingCriteria.put(incomeAttr.getNameOnData(), incomeHigh);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCriteria) == 1);
		matchingCriteria.put(typeAttr.getNameOnData(), typeOne);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCriteria) == 1);
		
		
		AbstractAttribute nbOfCarsAttr = generator.getAttributeByNameOnData("Number Of Cars");
		
		AttributeValue carsOne = new UniqueValue(nbOfCarsAttr.getDataType(), "1");
		matchingCriteria.put(nbOfCarsAttr.getNameOnData(), carsOne);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCriteria) == 0);
		
		AttributeValue carsThree = new UniqueValue(nbOfCarsAttr.getDataType(), "3");
		matchingCriteria.put(nbOfCarsAttr.getNameOnData(), carsThree);
		assertTrue(sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCriteria) == 1);
	}
}
