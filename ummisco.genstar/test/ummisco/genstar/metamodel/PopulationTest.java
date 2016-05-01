package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class PopulationTest {
	
	private GenstarCsvFile attributesFile;
	
	private ISingleRuleGenerator generator;
	
	
	@Before public void init() throws GenstarException {
		attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/metamodel/population/initialize_population_successfully/attributes.csv", true);
		generator = new SingleRuleGenerator("dummy generator");
		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
	}
	
	@Test public void testInitializePopulationSuccessfully() throws GenstarException {
		Population population1 = new Population(PopulationType.SYNTHETIC_POPULATION, "population1", generator.getAttributes());
		assertTrue(population1.getPopulationType().equals(PopulationType.SYNTHETIC_POPULATION));
		assertTrue(population1.getAttributes().size() == generator.getAttributes().size());
		assertTrue(population1.getName().equals("population1"));
		assertTrue(population1.getEntities().equals(Collections.EMPTY_LIST));
		
		Population population2 = new Population(PopulationType.SAMPLE_DATA_POPULATION, "population2", generator.getAttributes());
		assertTrue(population2.getPopulationType().equals(PopulationType.SAMPLE_DATA_POPULATION));
	}
	
	@Test(expected = GenstarException.class) public void testInitializePopulationWithDuplicatedAttributes() throws GenstarException {
		List<AbstractAttribute> duplicatedAttributes = generator.getAttributes();
		duplicatedAttributes.add(duplicatedAttributes.get(0));
		
		new Population(PopulationType.SAMPLE_DATA_POPULATION, "population2", duplicatedAttributes);
	}
	
	@Test public void testInitializePopulationWithAttributeFromDifferentGenerators() throws GenstarException {
		fail("not yet implemented");
	}
	
	@Test public void testGetAttributeByNameOnData() throws GenstarException {
		Population population1 = new Population(PopulationType.SYNTHETIC_POPULATION, "population1", generator.getAttributes());
		assertTrue(population1.getAttributeByNameOnData("Age") != null);
		assertTrue(population1.getAttributeByNameOnData("age") == null);
	}
	
	@Test public void testGetAttributeByNameOnEntity() throws GenstarException {
		Population population1 = new Population(PopulationType.SYNTHETIC_POPULATION, "population1", generator.getAttributes());
		assertTrue(population1.getAttributeByNameOnEntity("Age") == null);
		assertTrue(population1.getAttributeByNameOnEntity("age") != null);
	}
	
	@Test public void testCreateEntitiesWithIntParameter() throws GenstarException {
		Population population1 = new Population(PopulationType.SYNTHETIC_POPULATION, "population1", generator.getAttributes());
		
		assertTrue(population1.getEntities().isEmpty());
		
		List<Entity> entities1 = population1.createEntities(2);
		assertTrue(entities1.size() == 2);
		assertTrue(population1.getEntities().size() == 2);
		
		List<Entity> entities2 = population1.createEntities(3);
		assertTrue(entities2.size() == 3);
		assertTrue(population1.getEntities().size() == 5);
	}
	
	@Test public void testCreateEntityWithEntityAttributeValues() throws GenstarException {
		Population population = new Population(PopulationType.SYNTHETIC_POPULATION, "population 1", generator.getAttributes());
		
		// Category
		AbstractAttribute categoryAttr = generator.getAttributeByNameOnData("Category");
		AttributeValue[] categoryValues = categoryAttr.valuesOnData().toArray(new AttributeValue[0]);
		EntityAttributeValue categoryEAV = new EntityAttributeValue(categoryAttr, categoryValues[0], categoryValues[0]);
		
		// Age
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age"); 
		AttributeValue[] ageValues = ageAttr.valuesOnData().toArray(new AttributeValue[0]);
		EntityAttributeValue ageEVA = new EntityAttributeValue(ageAttr, ageValues[0], ageValues[0].cast(ageAttr.getValueClassOnEntity()));
		
		// Gender
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");
		AttributeValue[] genderValues = genderAttr.valuesOnData().toArray(new AttributeValue[0]);
		EntityAttributeValue genderEAV = new EntityAttributeValue(genderAttr, genderValues[0], genderValues[0]);
		
		List<EntityAttributeValue> entityAttributeValues = new ArrayList<EntityAttributeValue>();
		entityAttributeValues.add(categoryEAV);
		entityAttributeValues.add(ageEVA);
		entityAttributeValues.add(genderEAV);
		
		assertTrue(population.getEntities().size() == 0);
		Entity e = population.createEntity(entityAttributeValues);
		assertTrue(population.getEntities().size() == 1);
	}
	
	@Test public void testCreateEntitiesWithEntityAttributeValuesList() throws GenstarException {
		Population population = new Population(PopulationType.SYNTHETIC_POPULATION, "population 1", generator.getAttributes());
		
		// Category
		AbstractAttribute categoryAttr = generator.getAttributeByNameOnData("Category");
		AttributeValue[] categoryValues = categoryAttr.valuesOnData().toArray(new AttributeValue[0]);
		EntityAttributeValue categoryEAV = new EntityAttributeValue(categoryAttr, categoryValues[0], categoryValues[0]);
		
		// Age
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age"); 
		AttributeValue[] ageValues = ageAttr.valuesOnData().toArray(new AttributeValue[0]);
		EntityAttributeValue ageEVA = new EntityAttributeValue(ageAttr, ageValues[0], ageValues[0].cast(ageAttr.getValueClassOnEntity()));
		
		// Gender
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");
		AttributeValue[] genderValues = genderAttr.valuesOnData().toArray(new AttributeValue[0]);
		EntityAttributeValue genderEAV = new EntityAttributeValue(genderAttr, genderValues[0], genderValues[0]);
		
		List<EntityAttributeValue> entityAttributeValues1 = new ArrayList<EntityAttributeValue>();
		entityAttributeValues1.add(categoryEAV);
		entityAttributeValues1.add(ageEVA);
		entityAttributeValues1.add(genderEAV);

		List<EntityAttributeValue> entityAttributeValues2 = new ArrayList<EntityAttributeValue>();
		entityAttributeValues2.add(categoryEAV);
		entityAttributeValues2.add(ageEVA);
		entityAttributeValues2.add(genderEAV);
		
		List<List<EntityAttributeValue>>entityAttributeValuesList = new ArrayList<List<EntityAttributeValue>>();
		entityAttributeValuesList.add(entityAttributeValues1);
		entityAttributeValuesList.add(entityAttributeValues2);
		
		assertTrue(population.getEntities().size() == 0);
		List<Entity> entities = population.createEntities(entityAttributeValuesList);
		assertTrue(entities.size() == 2);
		assertTrue(population.getEntities().size() == 2);
		
		population.createEntities(entityAttributeValuesList);
		assertTrue(population.getEntities().size() == 4);
	}
	
	@Test public void testCreateEntityWithAttributeValuesOnEntity() throws GenstarException {
		Population population = new Population(PopulationType.SYNTHETIC_POPULATION, "population 1", generator.getAttributes());
		
		// Category
		AbstractAttribute categoryAttr = generator.getAttributeByNameOnData("Category");
		AttributeValue[] categoryValues = categoryAttr.valuesOnData().toArray(new AttributeValue[0]);
		
		// Age
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age"); 
		AttributeValue[] ageValues = ageAttr.valuesOnData().toArray(new AttributeValue[0]);
		
		// Gender
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");
		AttributeValue[] genderValues = genderAttr.valuesOnData().toArray(new AttributeValue[0]);
		
		Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValuesOnEntity.put(categoryAttr, categoryValues[0].cast(categoryAttr.getValueClassOnEntity()));
		attributeValuesOnEntity.put(ageAttr, ageValues[0].cast(ageAttr.getValueClassOnEntity()));
		attributeValuesOnEntity.put(genderAttr, genderValues[0].cast(genderAttr.getValueClassOnEntity()));
		
		assertTrue(population.getEntities().size() == 0);
		Entity e = population.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity);
		assertTrue(population.getEntities().size() == 1);
	}
	
	@Test public void testCreateEntitiesWithAttributeValuesOnEntities() throws GenstarException {
		Population population = new Population(PopulationType.SYNTHETIC_POPULATION, "population 1", generator.getAttributes());
		
		// Category
		AbstractAttribute categoryAttr = generator.getAttributeByNameOnData("Category");
		AttributeValue[] categoryValues = categoryAttr.valuesOnData().toArray(new AttributeValue[0]);
		
		// Age
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age"); 
		AttributeValue[] ageValues = ageAttr.valuesOnData().toArray(new AttributeValue[0]);
		
		// Gender
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");
		AttributeValue[] genderValues = genderAttr.valuesOnData().toArray(new AttributeValue[0]);
		
		Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity1 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValuesOnEntity1.put(categoryAttr, categoryValues[0].cast(categoryAttr.getValueClassOnEntity()));
		attributeValuesOnEntity1.put(ageAttr, ageValues[0].cast(ageAttr.getValueClassOnEntity()));
		attributeValuesOnEntity1.put(genderAttr, genderValues[0].cast(genderAttr.getValueClassOnEntity()));
		
		Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity2 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValuesOnEntity2.put(categoryAttr, categoryValues[0].cast(categoryAttr.getValueClassOnEntity()));
		attributeValuesOnEntity2.put(ageAttr, ageValues[0].cast(ageAttr.getValueClassOnEntity()));
		attributeValuesOnEntity2.put(genderAttr, genderValues[0].cast(genderAttr.getValueClassOnEntity()));
		
		List<Map<AbstractAttribute, AttributeValue>> attributeValuesAttributeValueOnEntities = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
		attributeValuesAttributeValueOnEntities.add(attributeValuesOnEntity1);
		attributeValuesAttributeValueOnEntities.add(attributeValuesOnEntity2);

		assertTrue(population.getEntities().size() == 0);
		List<Entity> entities = population.createEntitiesWithAttributeValuesOnEntities(attributeValuesAttributeValueOnEntities);
		assertTrue(entities.size() == 2);
		assertTrue(population.getEntities().size() == 2);
	}
	
	@Test public void testCreateEntityFromAnotherEntity() throws GenstarException { // createEntity(final Entity sourceEntity)
		fail("not yet implemented");
	}
	
	private void createEntities(final IPopulation population) throws GenstarException {
		// Category
		AbstractAttribute categoryAttr = generator.getAttributeByNameOnData("Category");
		AttributeValue[] categoryValues = categoryAttr.valuesOnData().toArray(new AttributeValue[0]);
		EntityAttributeValue categoryEAV0 = new EntityAttributeValue(categoryAttr, categoryValues[0], categoryValues[0]);
		EntityAttributeValue categoryEAV1 = new EntityAttributeValue(categoryAttr, categoryValues[1], categoryValues[1]);
		
		// Age
		List<AttributeValue> ageValuesOnEntity = new ArrayList<AttributeValue>();
		ageValuesOnEntity.add(new UniqueValue(DataType.INTEGER, "0"));
		ageValuesOnEntity.add(new UniqueValue(DataType.INTEGER, "1"));
		ageValuesOnEntity.add(new UniqueValue(DataType.INTEGER, "2"));
		ageValuesOnEntity.add(new UniqueValue(DataType.INTEGER, "3"));
		ageValuesOnEntity.add(new UniqueValue(DataType.INTEGER, "4"));
		
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age"); 
		AttributeValue[] ageValues = ageAttr.valuesOnData().toArray(new AttributeValue[0]);
		List<EntityAttributeValue> ageEVAs = new ArrayList<EntityAttributeValue>();
		for (int i=0; i<ageValuesOnEntity.size(); i++) {
			ageEVAs.add(new EntityAttributeValue(ageAttr, ageValues[0], ageValuesOnEntity.get(i)));
		}
		
		// Gender
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");
		AttributeValue[] genderValues = genderAttr.valuesOnData().toArray(new AttributeValue[0]);
		EntityAttributeValue genderEAV = new EntityAttributeValue(genderAttr, genderValues[0], genderValues[0]);

		// create 10 entities
		
		// create 5 entities with entityAttributeValues0
		List<EntityAttributeValue> entityAttributeValues0 = new ArrayList<EntityAttributeValue>();
		entityAttributeValues0.add(categoryEAV0);
		entityAttributeValues0.add(genderEAV);
		for (int i=0; i<5; i++) {
			if (entityAttributeValues0.size() == 3) { entityAttributeValues0.remove(2); }
			entityAttributeValues0.add(ageEVAs.get(i));
			population.createEntity(entityAttributeValues0); 
		}
		
		// create 5 entities with entityAttributeValues1
		List<EntityAttributeValue> entityAttributeValues1 = new ArrayList<EntityAttributeValue>();
		entityAttributeValues1.add(categoryEAV1);
		entityAttributeValues1.add(genderEAV);
		for (int i=0; i<5; i++) { 
			if (entityAttributeValues1.size() == 3) { entityAttributeValues1.remove(2); }
			entityAttributeValues1.add(ageEVAs.get(i));
			population.createEntity(entityAttributeValues1); 
		}
	}
	
	@Test public void testGetMatchingEntitiesByAttributeValuesOnData() throws GenstarException {
		Population population = new Population(PopulationType.SYNTHETIC_POPULATION, "population 1", generator.getAttributes());
		createEntities(population);
		
		// Category
		AbstractAttribute categoryAttr = generator.getAttributeByNameOnData("Category");
		AttributeValue[] categoryValues = categoryAttr.valuesOnData().toArray(new AttributeValue[0]);
		AttributeValue categoryValues0 = categoryValues[0]; 
		AttributeValue categoryValues1 = categoryValues[1]; 
		
		// Age
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age"); 
		AttributeValue[] ageValues = ageAttr.valuesOnData().toArray(new AttributeValue[0]);
		AttributeValue ageValues0 = ageValues[0]; 
		AttributeValue ageValues1 = ageValues[1]; 
		
		// Gender
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");
		AttributeValue[] genderValues = genderAttr.valuesOnData().toArray(new AttributeValue[0]);
		AttributeValue genderValues0 = genderValues[0]; 
		AttributeValue genderValues1 = genderValues[1]; 
		
		
		Map<AbstractAttribute, AttributeValue> attributeValuesOnData = new HashMap<AbstractAttribute, AttributeValue>();
		
		// 1. query with categoryValues[0] -> 5 entities
		attributeValuesOnData.put(categoryAttr, categoryValues0);
		List<Entity> entities1 = population.getMatchingEntitiesByAttributeValuesOnData(attributeValuesOnData);
		assertTrue(entities1.size() == 5);
		
		
		// 2. query with categoryValues[1] -> 5 entities
		attributeValuesOnData.put(categoryAttr, categoryValues1);
		List<Entity> entities2 = population.getMatchingEntitiesByAttributeValuesOnData(attributeValuesOnData);
		assertTrue(entities2.size() == 5);
		
		
		// 3. query with ageValues[0] -> 10 entities
		attributeValuesOnData.clear();
		attributeValuesOnData.put(ageAttr, ageValues0);
		List<Entity> entities3 = population.getMatchingEntitiesByAttributeValuesOnData(attributeValuesOnData);
		assertTrue(entities3.size() == 10);
		
		
		// 4. query with categoryValues[0] & ageValues[0] -> 5 entities
		attributeValuesOnData.put(categoryAttr, categoryValues0);
		List<Entity> entities4 = population.getMatchingEntitiesByAttributeValuesOnData(attributeValuesOnData);
		assertTrue(entities4.size() == 5);
		
		
		// 5. query with ageValues[1] -> 0 entities
		attributeValuesOnData.clear();
		attributeValuesOnData.put(ageAttr, ageValues1);
		List<Entity> entities5 = population.getMatchingEntitiesByAttributeValuesOnData(attributeValuesOnData);
		assertTrue(entities5.isEmpty());
		
		
		// 6. query with categoryValues[0] & genderValues[0] -> 5 entities
		attributeValuesOnData.clear();
		attributeValuesOnData.put(categoryAttr, categoryValues0);
		attributeValuesOnData.put(genderAttr, genderValues0);
		List<Entity> entities6 = population.getMatchingEntitiesByAttributeValuesOnData(attributeValuesOnData);
		assertTrue(entities6.size() == 5);
		
		
		// 7. query with categoryValues[1] & ageValues[0] & genderValues[0] -> 5 entities
		attributeValuesOnData.put(categoryAttr, categoryValues1);
		attributeValuesOnData.put(ageAttr, ageValues0);
		attributeValuesOnData.put(genderAttr, genderValues0);
		List<Entity> entities7 = population.getMatchingEntitiesByAttributeValuesOnData(attributeValuesOnData);
		assertTrue(entities7.size() == 5);
		
		
		// 8. query with genderValues[1] -> 0 entities
		attributeValuesOnData.clear();
		attributeValuesOnData.put(genderAttr, genderValues1);
		List<Entity> entities8 = population.getMatchingEntitiesByAttributeValuesOnData(attributeValuesOnData);
		assertTrue(entities8.isEmpty());
	}
	
	@Test public void testGetMatchingEntitiesByAttributeValuesOnEntity() throws GenstarException {
		Population population = new Population(PopulationType.SYNTHETIC_POPULATION, "population 1", generator.getAttributes());
		createEntities(population);
		
		// Category
		AbstractAttribute categoryAttr = generator.getAttributeByNameOnData("Category");
		AttributeValue[] categoryValues = categoryAttr.valuesOnData().toArray(new AttributeValue[0]);
		AttributeValue categoryValues0 = categoryValues[0]; 
		
		// Age
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age"); 
		
		// Gender
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");
		AttributeValue[] genderValues = genderAttr.valuesOnData().toArray(new AttributeValue[0]);
		AttributeValue genderValues0 = genderValues[0]; 
		AttributeValue genderValues1 = genderValues[1]; 
		

		Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>();
		
		// 1. query with age = 0 -> 2 entities
		attributeValuesOnEntity.put(ageAttr, new UniqueValue(DataType.INTEGER, "0"));
		List<Entity> entities1 = population.getMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity);
		assertTrue(entities1.size() == 2);
		
		
		// 2. query with age = 5 -> 0 entity
		attributeValuesOnEntity.put(ageAttr, new UniqueValue(DataType.INTEGER, "5"));
		List<Entity> entities2 = population.getMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity);
		assertTrue(entities2.size() == 0);
		
		
		// 3. query with category0 -> 5 entities
		attributeValuesOnEntity.clear();
		attributeValuesOnEntity.put(categoryAttr, categoryValues0);
		List<Entity> entities3 = population.getMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity);
		assertTrue(entities3.size() == 5);
		
		
		// 4. query with gender0 -> 10 entities
		attributeValuesOnEntity.clear();
		attributeValuesOnEntity.put(genderAttr, genderValues0);
		List<Entity> entities4 = population.getMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity);
		assertTrue(entities4.size() == 10);

		
		// 5. query with age = 0 & category0 -> 2 entities
		attributeValuesOnEntity.clear();
		attributeValuesOnEntity.put(ageAttr, new UniqueValue(DataType.INTEGER, "0"));
		attributeValuesOnEntity.put(categoryAttr, categoryValues0);
		List<Entity> entities5 = population.getMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity);
		assertTrue(entities5.size() == 1);
		
		
		// 6. query with age = 0 & category0 & gender0 -> 2 entities
		attributeValuesOnEntity.put(genderAttr, genderValues0);
		List<Entity> entities6 = population.getMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity);
		assertTrue(entities6.size() == 1);

		
		// 7. query with gender1 -> 0 entity
		attributeValuesOnEntity.clear();
		attributeValuesOnEntity.put(genderAttr, genderValues1);
		List<Entity> entities7 = population.getMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity);
		assertTrue(entities7.isEmpty());
	}
	
	@Test public void testCountMatchingEntitiesByAttributeValuesOnEntity() throws GenstarException {
		Population population = new Population(PopulationType.SYNTHETIC_POPULATION, "population 1", generator.getAttributes());
		createEntities(population);
		
		// Category
		AbstractAttribute categoryAttr = generator.getAttributeByNameOnData("Category");
		AttributeValue[] categoryValues = categoryAttr.valuesOnData().toArray(new AttributeValue[0]);
		AttributeValue categoryValues0 = categoryValues[0]; 
		
		// Age
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age"); 
		
		// Gender
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");
		AttributeValue[] genderValues = genderAttr.valuesOnData().toArray(new AttributeValue[0]);
		AttributeValue genderValues0 = genderValues[0]; 
		AttributeValue genderValues1 = genderValues[1]; 
		

		Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>();
		
		// 1. query with age = 0 -> 2 entities
		attributeValuesOnEntity.put(ageAttr, new UniqueValue(DataType.INTEGER, "0"));
		assertTrue(population.countMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity) == 2);
		
		
		// 2. query with age = 5 -> 0 entity
		attributeValuesOnEntity.put(ageAttr, new UniqueValue(DataType.INTEGER, "5"));
		assertTrue(population.countMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity) == 0);
		
		
		// 3. query with category0 -> 5 entities
		attributeValuesOnEntity.clear();
		attributeValuesOnEntity.put(categoryAttr, categoryValues0);
		assertTrue(population.countMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity) == 5);
		
		
		// 4. query with gender0 -> 10 entities
		attributeValuesOnEntity.clear();
		attributeValuesOnEntity.put(genderAttr, genderValues0);
		assertTrue(population.countMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity) == 10);

		
		// 5. query with age = 0 & category0 -> 2 entities
		attributeValuesOnEntity.clear();
		attributeValuesOnEntity.put(ageAttr, new UniqueValue(DataType.INTEGER, "0"));
		attributeValuesOnEntity.put(categoryAttr, categoryValues0);
		assertTrue(population.countMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity) == 1);
		
		
		// 6. query with age = 0 & category0 & gender0 -> 2 entities
		attributeValuesOnEntity.put(genderAttr, genderValues0);
		assertTrue(population.countMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity) == 1);

		
		// 7. query with gender1 -> 0 entity
		attributeValuesOnEntity.clear();
		attributeValuesOnEntity.put(genderAttr, genderValues1);
		assertTrue(population.countMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity) == 0);
	}

	@Test public void testContainAttribute() throws GenstarException {
		Population population = new Population(PopulationType.SYNTHETIC_POPULATION, "population 1", generator.getAttributes());
		
		AbstractAttribute dummyAttribute = new UniqueValuesAttribute(generator, "dummy_attribute", DataType.INTEGER);
		assertFalse(population.containAttribute(dummyAttribute));
		
		for (AbstractAttribute attr : generator.getAttributes()) { assertTrue(population.containAttribute(attr)); }
	}
	
	@Test public void testIsIdValueAlreadyInUsed() throws GenstarException {
		fail("not yet implemented");
	}
	
	@Test public void testNextIdValue() throws GenstarException {
		fail("not yet implemented");
	}
	
	@Test public void testGetIdentityAttribute() throws GenstarException {
		fail("not yet implemented");
	}
	
	@Test public void testGetIdentityAttributeWhenIdentityAttributeChanged() throws GenstarException {
		fail("not yet implemented");
	}
	
	@Test public void testIsCompatible() throws GenstarException {
		fail("not yet implemented");
	}
}
