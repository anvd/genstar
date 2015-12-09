package ummisco.genstar.metamodel;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;

public class SyntheticPopulationTest_Bondy {


	private BondyData bondyData;
	
	public SyntheticPopulationTest_Bondy() throws GenstarException {
		bondyData = new BondyData();
	}

	@Test public void testGetMatchingEntitiesByAttributeValuesOnEntity1() throws GenstarException {
		ISyntheticPopulationGenerator inhabitantPopulationGenerator = bondyData.getInhabitantPopGenerator();
		inhabitantPopulationGenerator.setNbOfEntities(10);
		
		ISyntheticPopulation population = inhabitantPopulationGenerator.generate();
		Entity firstInhabitant = population.getEntities().get(0);
		
		int entitiesBefore = population.getEntities().size();
		
		EntityAttributeValue entityAgeAttrValue = firstInhabitant.getEntityAttributeValueByNameOnEntity("age");
		AttributeValue ageAttrValue = entityAgeAttrValue.getAttributeValueOnEntity();
		Map<String, AttributeValue> attributeValues = new HashMap<String, AttributeValue>();
		attributeValues.put("age", ageAttrValue);
		List<Entity> pickedEntities = population.getMatchingEntitiesByAttributeValuesOnEntity(attributeValues);
		
		assertTrue(firstInhabitant.equals(pickedEntities.get(0)));
		
		int entitiesAfter = population.getEntities().size();
		assertTrue(entitiesBefore == entitiesAfter);
	}
	
	@Test public void testGetMatchingEntitiesByAttributeValuesOnEntity2() throws GenstarException {
		ISyntheticPopulationGenerator inhabitantPopulationGenerator = bondyData.getInhabitantPopGenerator();
		inhabitantPopulationGenerator.setNbOfEntities(10);
		
		ISyntheticPopulation population = inhabitantPopulationGenerator.generate();
		Entity firstInhabitant = population.getEntities().get(0);
		
		int entitiesBefore = population.getEntities().size();
		
		EntityAttributeValue entityAgeAttrValue = firstInhabitant.getEntityAttributeValueByNameOnEntity("age");
		UniqueValue ageAttrValue = (UniqueValue) entityAgeAttrValue.getAttributeValueOnEntity();
		UniqueValue queryAgeAttributeValue = new UniqueValue(DataType.INTEGER, ageAttrValue.getStringValue());
		
		Map<String, AttributeValue> attributeValues = new HashMap<String, AttributeValue>();
		attributeValues.put("age", queryAgeAttributeValue);
		List<Entity> pickedEntities = population.getMatchingEntitiesByAttributeValuesOnEntity(attributeValues);
		
		assertTrue(firstInhabitant.equals(pickedEntities.get(0)));
		
		int entitiesAfter = population.getEntities().size();
		assertTrue(entitiesBefore == entitiesAfter);
	}
	
}
