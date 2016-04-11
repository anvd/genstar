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
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCSVFile;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class PopulationTest {
	
	private GenstarCSVFile attributesFile;
	
	private ISingleRuleGenerator generator;
	
	@Before public void init() throws GenstarException {
		attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/metamodel/population/initialize_population_successfully/attributes.csv", true);
		generator = new SingleRuleGenerator("dummy generator");
		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
	}
	
	@Test public void testInitializePopulationSuccessfully() throws GenstarException {
//		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/metamodel/population/initialize_population_successfully/attributes.csv", true);
//		ISingleRuleGenerator generator = new SingleRuleGenerator("dummy generator");
//		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		Population population1 = new Population(PopulationType.SYNTHETIC_POPULATION, "population1", generator.getAttributes());
		assertTrue(population1.getPopulationType().equals(PopulationType.SYNTHETIC_POPULATION));
		assertTrue(population1.getAttributes().size() == generator.getAttributes().size());
		assertTrue(population1.getName().equals("population1"));
		assertTrue(population1.getEntities().equals(Collections.EMPTY_LIST));
		
		Population population2 = new Population(PopulationType.SAMPLE_DATA_POPULATION, "population2", generator.getAttributes());
		assertTrue(population2.getPopulationType().equals(PopulationType.SAMPLE_DATA_POPULATION));
	}
	
	@Test public void testGetAttributeByNameOnData() throws GenstarException {
//		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/metamodel/population/initialize_population_successfully/attributes.csv", true);
//		ISingleRuleGenerator generator = new SingleRuleGenerator("dummy generator");
//		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		Population population1 = new Population(PopulationType.SYNTHETIC_POPULATION, "population1", generator.getAttributes());
		assertTrue(population1.getAttributeByNameOnData("Age") != null);
		assertTrue(population1.getAttributeByNameOnData("age") == null);
	}
	
	@Test public void testGetAttributeByNameOnEntity() throws GenstarException {
//		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/metamodel/population/initialize_population_successfully/attributes.csv", true);
//		ISingleRuleGenerator generator = new SingleRuleGenerator("dummy generator");
//		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
		
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
		AttributeValue[] categoryValues = categoryAttr.values().toArray(new AttributeValue[0]);
		EntityAttributeValue categoryEAV = new EntityAttributeValue(categoryAttr, categoryValues[0], categoryValues[0]);
		
		// Age
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age"); 
		AttributeValue[] ageValues = ageAttr.values().toArray(new AttributeValue[0]);
		EntityAttributeValue ageEVA = new EntityAttributeValue(ageAttr, ageValues[0], ageValues[0].cast(ageAttr.getValueClassOnEntity()));
		
		// Gender
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");
		AttributeValue[] genderValues = genderAttr.values().toArray(new AttributeValue[0]);
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
		/*
	@Override public List<Entity> createEntities(final List<List<EntityAttributeValue>> entityAttributeValuesList) throws GenstarException {
		 */
		
		Population population = new Population(PopulationType.SYNTHETIC_POPULATION, "population 1", generator.getAttributes());
		
		// Category
		AbstractAttribute categoryAttr = generator.getAttributeByNameOnData("Category");
		AttributeValue[] categoryValues = categoryAttr.values().toArray(new AttributeValue[0]);
		EntityAttributeValue categoryEAV = new EntityAttributeValue(categoryAttr, categoryValues[0], categoryValues[0]);
		
		// Age
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age"); 
		AttributeValue[] ageValues = ageAttr.values().toArray(new AttributeValue[0]);
		EntityAttributeValue ageEVA = new EntityAttributeValue(ageAttr, ageValues[0], ageValues[0].cast(ageAttr.getValueClassOnEntity()));
		
		// Gender
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");
		AttributeValue[] genderValues = genderAttr.values().toArray(new AttributeValue[0]);
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
		/*
	@Override public Entity createEntityWithAttributeValuesOnEntity(final Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity) throws GenstarException {
		 */
		
		Population population = new Population(PopulationType.SYNTHETIC_POPULATION, "population 1", generator.getAttributes());
		
		// Category
		AbstractAttribute categoryAttr = generator.getAttributeByNameOnData("Category");
		AttributeValue[] categoryValues = categoryAttr.values().toArray(new AttributeValue[0]);
		
		// Age
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age"); 
		AttributeValue[] ageValues = ageAttr.values().toArray(new AttributeValue[0]);
		
		// Gender
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");
		AttributeValue[] genderValues = genderAttr.values().toArray(new AttributeValue[0]);
		
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
		AttributeValue[] categoryValues = categoryAttr.values().toArray(new AttributeValue[0]);
		
		// Age
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age"); 
		AttributeValue[] ageValues = ageAttr.values().toArray(new AttributeValue[0]);
		
		// Gender
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");
		AttributeValue[] genderValues = genderAttr.values().toArray(new AttributeValue[0]);
		
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

	@Test public void testContainAttribute() throws GenstarException {
		fail("not yet implemented");
	}
}
