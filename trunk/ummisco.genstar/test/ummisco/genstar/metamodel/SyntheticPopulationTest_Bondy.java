package ummisco.genstar.metamodel;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarException;

public class SyntheticPopulationTest_Bondy {


	private BondyData bondyData;
	
	public SyntheticPopulationTest_Bondy() throws GenstarException {
		bondyData = new BondyData();
	}

	@Test public void testPick1() throws GenstarException {
		ISyntheticPopulationGenerator inhabitantPopulationGenerator = bondyData.getInhabitantPopGenerator();
		inhabitantPopulationGenerator.setNbOfEntities(10);
		
		ISyntheticPopulation population = inhabitantPopulationGenerator.generate();
		Entity firstInhabitant = population.getEntities().get(0);
		
		int entitiesBefore = population.getEntities().size();
		
		EntityAttributeValue entityAgeAttrValue = firstInhabitant.getEntityAttributeValue("age");
		AttributeValue ageAttrValue = entityAgeAttrValue.getAttributeValueOnEntity();
		Map<String, AttributeValue> attributeValues = new HashMap<String, AttributeValue>();
		attributeValues.put("age", ageAttrValue);
		Entity pickedEntity = population.pick(attributeValues);
		
		assertTrue(firstInhabitant.equals(pickedEntity));
		
		int entitiesAfter = population.getEntities().size();
		assertTrue(entitiesBefore == entitiesAfter + 1);
	}
	
	@Test public void testPick2() throws GenstarException {
		ISyntheticPopulationGenerator inhabitantPopulationGenerator = bondyData.getInhabitantPopGenerator();
		inhabitantPopulationGenerator.setNbOfEntities(10);
		
		ISyntheticPopulation population = inhabitantPopulationGenerator.generate();
		Entity firstInhabitant = population.getEntities().get(0);
		
		int entitiesBefore = population.getEntities().size();
		
		EntityAttributeValue entityAgeAttrValue = firstInhabitant.getEntityAttributeValue("age");
		UniqueValue ageAttrValue = (UniqueValue) entityAgeAttrValue.getAttributeValueOnEntity();
		UniqueValue queryAgeAttributeValue = new UniqueValue(DataType.INTEGER, ageAttrValue.getStringValue());
		
		Map<String, AttributeValue> attributeValues = new HashMap<String, AttributeValue>();
		attributeValues.put("age", queryAgeAttributeValue);
		Entity pickedEntity = population.pick(attributeValues);
		
		assertTrue(firstInhabitant.equals(pickedEntity));
		
		int entitiesAfter = population.getEntities().size();
		assertTrue(entitiesBefore == entitiesAfter + 1);
	}
	
	@Test public void testPick3() throws GenstarException {
		ISyntheticPopulationGenerator inhabitantPopulationGenerator = bondyData.getInhabitantPopGenerator();
		inhabitantPopulationGenerator.setNbOfEntities(10);
		
		ISyntheticPopulation inhabitantPopulation = inhabitantPopulationGenerator.generate();
		
		
		// 1. pick by age attribute value on data
		Entity concernedHouseholdHead1 = inhabitantPopulation.getEntities().get(1);
		EntityAttributeValue ageEAValue1 = concernedHouseholdHead1.getEntityAttributeValue("age");
		RangeValue ageRangeValue1 = new RangeValue((RangeValue) ageEAValue1.getAttributeValueOnData());

		int entitiesBefore = inhabitantPopulation.getEntities().size();

		Map<String, AttributeValue> headAgeAttributeValue = new HashMap<String, AttributeValue>();
		headAgeAttributeValue.put("age", ageRangeValue1);
		
		Entity householdHead1 = inhabitantPopulation.pick(headAgeAttributeValue);
		
		int entitiesAfter = inhabitantPopulation.getEntities().size();
		assertTrue(entitiesBefore == entitiesAfter + 1);
		
		
		// 2. pick by age attribute value on entity
		Entity concernedHouseholdHead2 = inhabitantPopulation.getEntities().get(2);
		EntityAttributeValue ageEAValue2 = concernedHouseholdHead2.getEntityAttributeValue("age");
		UniqueValue ageUniqueValue2 = new UniqueValue((UniqueValue) ageEAValue2.getAttributeValueOnEntity());
		
		entitiesBefore = inhabitantPopulation.getEntities().size();
		
		headAgeAttributeValue.put("age", ageUniqueValue2);
		
		Entity householdHead2 = inhabitantPopulation.pick(headAgeAttributeValue);
		entitiesAfter = inhabitantPopulation.getEntities().size();
		assertTrue(entitiesBefore == entitiesAfter + 1);
	}	
	
}
