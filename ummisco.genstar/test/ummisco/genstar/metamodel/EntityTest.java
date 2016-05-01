package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;

public class EntityTest {

	@Rule public ExpectedException exception = ExpectedException.none();

	@Test public void testValidParamConstructor() throws GenstarException {
		ISyntheticPopulationGenerator bondyPopulation = new MultipleRulesGenerator("Population of Bondy", 100);
		bondyPopulation.setNbOfEntities(1);
		new Entity(bondyPopulation.generate());
	}
	
	@Test public void testInvalidParamConstructor() {
		exception.expect(IllegalArgumentException.class);
		new Entity(null);
	}
	
	@Test public void testSetAttributeValueOnData() throws GenstarException {
		
		ISyntheticPopulationGenerator bondyPopulation = new MultipleRulesGenerator("Population of Bondy", 100);
		bondyPopulation.setNbOfEntities(1);
		
		RangeValuesAttribute ageRangesAttr1 = new RangeValuesAttribute(bondyPopulation, "age_range_1", "age", DataType.INTEGER, UniqueValue.class);
		RangeValue ageRange1 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		RangeValue ageRange2 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[1][0]), Integer.toString(BondyData.age_ranges_1[1][1]));
		ageRangesAttr1.add(ageRange1);
		ageRangesAttr1.add(ageRange2);
		bondyPopulation.addAttribute(ageRangesAttr1);
		
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(bondyPopulation, "sex", DataType.BOOL);
		UniqueValue maleValueOrigin = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0]));
		sexAttr.add(maleValueOrigin);
		bondyPopulation.addAttribute(sexAttr);

		
		Entity entity1 = new Entity(bondyPopulation.generate());
		assertFalse(entity1.getEntityAttributeValueByNameOnData(ageRangesAttr1.getNameOnData()) != null);
		entity1.setAttributeValueOnData(ageRangesAttr1, ageRange1);
		assertTrue(entity1.getEntityAttributeValueByNameOnData(ageRangesAttr1.getNameOnData()) != null);
		
		EntityAttributeValue entityAgeValue = entity1.getEntityAttributeValueByNameOnData("age_range_1");
		assertTrue(entityAgeValue.getAttributeValueOnEntity() instanceof UniqueValue);
		assertTrue(ageRange1.cover( (UniqueValue) entityAgeValue.getAttributeValueOnEntity()));
		
		
		assertFalse(entity1.getEntityAttributeValueByNameOnData(sexAttr.getNameOnData()) != null);
		entity1.setAttributeValueOnData(sexAttr, maleValueOrigin);
		assertTrue(entity1.getEntityAttributeValueByNameOnData(sexAttr.getNameOnEntity()) != null);
	}
	
	@Test public void testSetAttributeValuesOnData() throws GenstarException {
		ISyntheticPopulationGenerator bondyPopulation = new MultipleRulesGenerator("Population of Bondy", 100);
		bondyPopulation.setNbOfEntities(1);
		
		RangeValuesAttribute ageRangesAttr1 = new RangeValuesAttribute(bondyPopulation, "age_range_1", "age", DataType.INTEGER);
		RangeValue ageRange1 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		RangeValue ageRange2 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[1][0]), Integer.toString(BondyData.age_ranges_1[1][1]));
		ageRangesAttr1.add(ageRange1);
		ageRangesAttr1.add(ageRange2);
		bondyPopulation.addAttribute(ageRangesAttr1);
		
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(bondyPopulation, "sex", DataType.BOOL);
		UniqueValue maleValueOrigin = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0]));
		sexAttr.add(maleValueOrigin);
		bondyPopulation.addAttribute(sexAttr);

		
		Entity entity1 = new Entity(bondyPopulation.generate());
		
		assertFalse(entity1.getEntityAttributeValue(ageRangesAttr1) != null);
		assertFalse(entity1.getEntityAttributeValue(sexAttr) != null);

		Map<AbstractAttribute, AttributeValue> attributeValuesOnData = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValuesOnData.put(ageRangesAttr1, ageRange1);
		attributeValuesOnData.put(sexAttr, maleValueOrigin);
		
		entity1.setAttributeValuesOnData(attributeValuesOnData);
		
		assertTrue(entity1.getEntityAttributeValue(ageRangesAttr1) != null);
		assertTrue(entity1.getEntityAttributeValue(sexAttr) != null);
	}
	
	@Test public void testSetAttributeValueOnEntity() throws GenstarException {
		ISyntheticPopulationGenerator bondyPopulation = new MultipleRulesGenerator("Population of Bondy", 100);
		bondyPopulation.setNbOfEntities(1);
		
		RangeValuesAttribute ageRangesAttr1 = new RangeValuesAttribute(bondyPopulation, "age_range_1", "age", DataType.INTEGER, UniqueValue.class);
		RangeValue ageRange1 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1])); // [0, 2]
		RangeValue ageRange2 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[1][0]), Integer.toString(BondyData.age_ranges_1[1][1])); // [3, 5]
		ageRangesAttr1.add(ageRange1);
		ageRangesAttr1.add(ageRange2);
		bondyPopulation.addAttribute(ageRangesAttr1);
		
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(bondyPopulation, "sex", DataType.BOOL);
		UniqueValue maleValueOrigin = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0]));
		sexAttr.add(maleValueOrigin);
		bondyPopulation.addAttribute(sexAttr);

		
		Entity entity = new Entity(bondyPopulation.generate());
		
		AttributeValue ageValue1 = new UniqueValue(DataType.INTEGER, "1");
		entity.setAttributeValueOnEntity(ageRangesAttr1, ageValue1);
		
		EntityAttributeValue eav1 = entity.getEntityAttributeValueByNameOnData("age_range_1");
		assertTrue(eav1.getAttribute().equals(ageRangesAttr1));
		assertTrue(eav1.getAttributeValueOnData().equals(ageRange1));
		assertTrue(eav1.getAttributeValueOnEntity().equals(ageValue1));
		
		EntityAttributeValue eav2 = entity.getEntityAttributeValueByNameOnEntity("age");
		assertTrue(eav2.getAttribute().equals(ageRangesAttr1));
		assertTrue(eav2.getAttributeValueOnData().equals(ageRange1));
		assertTrue(eav2.getAttributeValueOnEntity().equals(ageValue1));
		
	}
	
	@Test public void testSetAttributeValuesOnEntity() throws GenstarException {
		ISyntheticPopulationGenerator bondyPopulation = new MultipleRulesGenerator("Population of Bondy", 100);
		bondyPopulation.setNbOfEntities(1);
		
		RangeValuesAttribute ageRangesAttr1 = new RangeValuesAttribute(bondyPopulation, "age_range_1", "age", DataType.INTEGER, UniqueValue.class);
		RangeValue ageRange1 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1])); // [0, 2]
		RangeValue ageRange2 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[1][0]), Integer.toString(BondyData.age_ranges_1[1][1])); // [3, 5]
		ageRangesAttr1.add(ageRange1);
		ageRangesAttr1.add(ageRange2);
		bondyPopulation.addAttribute(ageRangesAttr1);
		
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(bondyPopulation, "sex", DataType.BOOL);
		UniqueValue maleValueOrigin = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0]));
		sexAttr.add(maleValueOrigin);
		bondyPopulation.addAttribute(sexAttr);

		
		Entity entity = new Entity(bondyPopulation.generate());

		Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>();
		
		AttributeValue ageValue1 = new UniqueValue(DataType.INTEGER, "1");
		attributeValuesOnEntity.put(ageRangesAttr1, ageValue1);
		
		attributeValuesOnEntity.put(sexAttr, maleValueOrigin);
		
		entity.setAttributeValuesOnEntity(attributeValuesOnEntity);
		
		
		EntityAttributeValue eav1 = entity.getEntityAttributeValueByNameOnData("age_range_1");
		assertTrue(eav1.getAttribute().equals(ageRangesAttr1));
		assertTrue(eav1.getAttributeValueOnData().equals(ageRange1));
		assertTrue(eav1.getAttributeValueOnEntity().equals(ageValue1));
		
		EntityAttributeValue eav2 = entity.getEntityAttributeValueByNameOnEntity("age");
		assertTrue(eav2.getAttribute().equals(ageRangesAttr1));
		assertTrue(eav2.getAttributeValueOnData().equals(ageRange1));
		assertTrue(eav2.getAttributeValueOnEntity().equals(ageValue1));

		EntityAttributeValue eav3 = entity.getEntityAttributeValueByNameOnData("sex");
		assertTrue(eav3.getAttribute().equals(sexAttr));
		assertTrue(eav3.getAttributeValueOnData().equals(maleValueOrigin));
		assertTrue(eav3.getAttributeValueOnEntity().equals(maleValueOrigin));
		
		EntityAttributeValue eav4 = entity.getEntityAttributeValueByNameOnEntity("sex");
		assertTrue(eav4.getAttribute().equals(sexAttr));
		assertTrue(eav4.getAttributeValueOnData().equals(maleValueOrigin));
		assertTrue(eav4.getAttributeValueOnEntity().equals(maleValueOrigin));
	}
	
	
	@Test public void testMatchAttributeValuesOnEntity() throws GenstarException {
		MultipleRulesGenerator bondyPopulationGenerator = new MultipleRulesGenerator("Population of Bondy", 100);
		bondyPopulationGenerator.setNbOfEntities(1);
		
		RangeValuesAttribute ageRangesAttr1 = new RangeValuesAttribute(bondyPopulationGenerator, "age_range_1", "age", DataType.INTEGER);
		RangeValue ageRange1 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		ageRangesAttr1.add(ageRange1);
		bondyPopulationGenerator.addAttribute(ageRangesAttr1);

		Entity entity1 = new Entity(bondyPopulationGenerator.generate());
		entity1.setAttributeValueOnData(ageRangesAttr1, ageRange1);
		
		Map<AbstractAttribute, AttributeValue> set1 = new HashMap<AbstractAttribute, AttributeValue>();
		
		assertTrue(entity1.matchAttributeValuesOnEntity(set1));
		assertTrue(entity1.matchAttributeValuesOnEntity(null));
		
		
		UniqueValue ageRangeMatchAttrValue1 = new UniqueValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]));
		set1.put(ageRangesAttr1, ageRangeMatchAttrValue1);
		assertTrue(entity1.matchAttributeValuesOnEntity(set1));
		
		
		set1.clear();
		UniqueValue ageRangeMatchAttrValue2 = new UniqueValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0] - 1));
		set1.put(ageRangesAttr1, ageRangeMatchAttrValue2);
		assertFalse(entity1.matchAttributeValuesOnEntity(set1));
		
		
		set1.clear();
		RangeValue ageRangeMatchAttrValue3 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		set1.put(ageRangesAttr1, ageRangeMatchAttrValue3);
		assertTrue(entity1.matchAttributeValuesOnEntity(set1));
		
		
		set1.clear();
		RangeValue ageRangeMatchAttrValue4 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][1] + 1), Integer.toString(BondyData.age_ranges_1[0][1] + 2));
		set1.put(ageRangesAttr1, ageRangeMatchAttrValue4);
		assertFalse(entity1.matchAttributeValuesOnEntity(set1));
		
	}
	
	
	@Test public void testSetEntityAttributeValues() throws GenstarException {
		fail("not yet implemented");
	}
	
	
	@Test public void testSetEntityAttributeValuesWithInvalidAttribute() throws GenstarException {
		fail("not yet implemented");
	}
}
