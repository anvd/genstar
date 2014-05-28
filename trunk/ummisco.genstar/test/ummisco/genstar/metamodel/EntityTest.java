package ummisco.genstar.metamodel;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ummisco.genstar.exception.AttributeException;
import ummisco.genstar.exception.GenstarException;

public class EntityTest {

	@Rule public ExpectedException exception = ExpectedException.none();

	@Test public void testValidParamConstructor() throws GenstarException {
		ISyntheticPopulationGenerator bondyPopulation = new SyntheticPopulationGenerator("Population of Bondy", 100);
		bondyPopulation.setNbOfEntities(1);
		new Entity(bondyPopulation.generate());
	}
	
	@Test public void testInvalidParamConstructor() {
		exception.expect(IllegalArgumentException.class);
		new Entity(null);
	}
	
	@Test public void testPutAttributeValue() throws GenstarException {
		
		ISyntheticPopulationGenerator bondyPopulation = new SyntheticPopulationGenerator("Population of Bondy", 100);
		bondyPopulation.setNbOfEntities(1);
		
		EnumerationOfRangesAttribute ageRangesAttr1 = new EnumerationOfRangesAttribute(bondyPopulation, "age_range_1", "age", ValueType.INTEGER);
		RangeValue ageRange1 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		RangeValue ageRange2 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[1][0]), Integer.toString(BondyData.age_ranges_1[1][1]));
		ageRangesAttr1.add(ageRange1);
		ageRangesAttr1.add(ageRange2);
		bondyPopulation.addAttribute(ageRangesAttr1);
		
		
		EnumerationOfValuesAttribute sexAttr = new EnumerationOfValuesAttribute(bondyPopulation, "sex", ValueType.BOOL);
		UniqueValue maleValueOrigin = new UniqueValue(ValueType.BOOL, Boolean.toString(BondyData.sexes[0]));
		sexAttr.add(maleValueOrigin);
		bondyPopulation.addAttribute(sexAttr);

		
		Entity entity1 = new Entity(bondyPopulation.generate());
		assertFalse(entity1.containAttribute(ageRangesAttr1.getNameOnEntity()));
		entity1.putAttributeValue(ageRangesAttr1, ageRange1);
		assertTrue(entity1.containAttribute(ageRangesAttr1.getNameOnEntity()));
		
		
		assertFalse(entity1.containAttribute(sexAttr.getNameOnEntity()));
		entity1.putAttributeValue(sexAttr, maleValueOrigin);
		assertTrue(entity1.containAttribute(sexAttr.getNameOnEntity()));
		
	}
	
	@Test public void testPutDuplicatedAttribute() throws GenstarException  {

		SyntheticPopulationGenerator bondyPopulation = new SyntheticPopulationGenerator("Population of Bondy", 100);
		bondyPopulation.setNbOfEntities(1);
		
		EnumerationOfRangesAttribute ageRangesAttr1 = new EnumerationOfRangesAttribute(bondyPopulation, "age_range_1", "age", ValueType.INTEGER);
		RangeValue ageRange1 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		RangeValue ageRange2 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[1][0]), Integer.toString(BondyData.age_ranges_1[1][1]));
		ageRangesAttr1.add(ageRange1);
		ageRangesAttr1.add(ageRange2);
		bondyPopulation.addAttribute(ageRangesAttr1);
		
		
		Entity entity1 = new Entity(bondyPopulation.generate());
		assertFalse(entity1.containAttribute(ageRangesAttr1.getNameOnEntity()));

		entity1.putAttributeValue(ageRangesAttr1, ageRange1);
		assertTrue(entity1.containAttribute(ageRangesAttr1.getNameOnEntity()));
		
		exception.expect(AttributeException.class);
		entity1.putAttributeValue(ageRangesAttr1, ageRange1);
	}
	
	@Test public void testReplaceAttributeValue() throws GenstarException {
		SyntheticPopulationGenerator bondyPopulation = new SyntheticPopulationGenerator("Population of Bondy", 100);
		bondyPopulation.setNbOfEntities(1);
		
		EnumerationOfRangesAttribute ageRangesAttr1 = new EnumerationOfRangesAttribute(bondyPopulation, "age_range_1", "age", ValueType.INTEGER);
		RangeValue ageRange1 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		RangeValue ageRange2 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[1][0]), Integer.toString(BondyData.age_ranges_1[1][1]));
		ageRangesAttr1.add(ageRange1);
		ageRangesAttr1.add(ageRange2);
		bondyPopulation.addAttribute(ageRangesAttr1);
		
		
		Entity entity1 = new Entity(bondyPopulation.generate());
		assertFalse(entity1.containAttribute(ageRangesAttr1.getNameOnEntity()));

		entity1.putAttributeValue(ageRangesAttr1, ageRange1);
		assertTrue(entity1.containAttribute(ageRangesAttr1.getNameOnEntity()));
		
		entity1.replaceAttributeValue(ageRangesAttr1, ageRange2);
		assertFalse(entity1.getEntityAttributeValue("age").getAttributeValueOnEntity().equals(ageRange1));
		assertTrue(entity1.getEntityAttributeValue("age").getAttributeValueOnEntity().equals(ageRange2));
	}
	
	@Test public void testIsMatch() throws GenstarException {
		SyntheticPopulationGenerator bondyPopulation = new SyntheticPopulationGenerator("Population of Bondy", 100);
		bondyPopulation.setNbOfEntities(1);
		
		EnumerationOfRangesAttribute ageRangesAttr1 = new EnumerationOfRangesAttribute(bondyPopulation, "age_range_1", "age", ValueType.INTEGER);
		RangeValue ageRange1 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		ageRangesAttr1.add(ageRange1);
		bondyPopulation.addAttribute(ageRangesAttr1);

		Entity entity1 = new Entity(bondyPopulation.generate());
		entity1.putAttributeValue(ageRangesAttr1, ageRange1);
		
		Map<String, AttributeValue> set1 = new HashMap<String, AttributeValue>();
		
		assertTrue(entity1.isMatch(set1));
		assertTrue(entity1.isMatch(null));
		
		
		UniqueValue ageRangeMatchAttrValue1 = new UniqueValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]));
		set1.put("age", ageRangeMatchAttrValue1);
		assertTrue(entity1.isMatch(set1));
		
		
		set1.clear();
		UniqueValue ageRangeMatchAttrValue2 = new UniqueValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0] - 1));
		set1.put("age", ageRangeMatchAttrValue2);
		assertFalse(entity1.isMatch(set1));
		
		
		set1.clear();
		RangeValue ageRangeMatchAttrValue3 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		set1.put("age", ageRangeMatchAttrValue3);
		assertTrue(entity1.isMatch(set1));
		
		
		set1.clear();
		RangeValue ageRangeMatchAttrValue4 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][1] + 1), Integer.toString(BondyData.age_ranges_1[0][1] + 2));
		set1.put("age", ageRangeMatchAttrValue4);
		assertFalse(entity1.isMatch(set1));
		
	}
}
