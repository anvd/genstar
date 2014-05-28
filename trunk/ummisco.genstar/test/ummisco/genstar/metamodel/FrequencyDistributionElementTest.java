package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import ummisco.genstar.exception.AttributeException;
import ummisco.genstar.exception.GenstarException;

public class FrequencyDistributionElementTest {

	@Test public void testIsMatchDataSet() throws GenstarException {
		SyntheticPopulationGenerator bondyPopulationGenerator = new SyntheticPopulationGenerator("Population of Bondy", 100);
		
		EnumerationOfRangesAttribute ageRangesAttr1 = new EnumerationOfRangesAttribute(bondyPopulationGenerator, "age_range_1", "age", ValueType.INTEGER);
		RangeValue ageRange1 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		RangeValue ageRange2 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[1][0]), Integer.toString(BondyData.age_ranges_1[1][1]));
		ageRangesAttr1.add(ageRange1);
		ageRangesAttr1.add(ageRange2);
		bondyPopulationGenerator.addAttribute(ageRangesAttr1);
		
		RangeValue copyAgeRange1 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		
		
		EnumerationOfValuesAttribute sexAttr = new EnumerationOfValuesAttribute(bondyPopulationGenerator, "sex", ValueType.BOOL);
		UniqueValue maleValueOrigin = new UniqueValue(ValueType.BOOL, Boolean.toString(BondyData.sexes[0]));
		UniqueValue maleValueCopy = new UniqueValue(ValueType.BOOL, Boolean.toString(BondyData.sexes[0]));
		UniqueValue femaleValue = new UniqueValue(ValueType.BOOL, Boolean.toString(BondyData.sexes[1]));
		sexAttr.add(maleValueOrigin);
		bondyPopulationGenerator.addAttribute(sexAttr);
		
		Map<EnumerationValueAttribute, AttributeValue> catersian1 = new HashMap<EnumerationValueAttribute, AttributeValue>();
		catersian1.put(ageRangesAttr1, ageRange1);
		catersian1.put(sexAttr, maleValueOrigin);
		FrequencyDistributionElement element = new FrequencyDistributionElement(catersian1);
		
		
		Map<EnumerationValueAttribute, AttributeValue> dataSet1 = new HashMap<EnumerationValueAttribute, AttributeValue>();
		dataSet1.put(ageRangesAttr1, ageRange1);
		assertTrue(element.isMatchDataSet(dataSet1));
		
		dataSet1.put(sexAttr, maleValueOrigin);
		assertTrue(element.isMatchDataSet(dataSet1));
		
		
		Map<EnumerationValueAttribute, AttributeValue> dataSet2 = new HashMap<EnumerationValueAttribute, AttributeValue>();
		dataSet2.put(ageRangesAttr1, ageRange2);
		assertFalse(element.isMatchDataSet(dataSet2));
		
		
		Map<EnumerationValueAttribute, AttributeValue> dataSet3 = new HashMap<EnumerationValueAttribute, AttributeValue>();
		dataSet3.put(ageRangesAttr1, copyAgeRange1);
		assertTrue(element.isMatchDataSet(dataSet3));
		
		
		Map<EnumerationValueAttribute, AttributeValue> dataSet4 = new HashMap<EnumerationValueAttribute, AttributeValue>();
		dataSet4.put(ageRangesAttr1, copyAgeRange1);
		dataSet4.put(sexAttr, maleValueCopy);
		assertTrue(element.isMatchDataSet(dataSet4));
		
		
		Map<EnumerationValueAttribute, AttributeValue> dataSet5 = new HashMap<EnumerationValueAttribute, AttributeValue>();
		dataSet5.put(ageRangesAttr1, copyAgeRange1);
		dataSet5.put(sexAttr, femaleValue);
		assertFalse(element.isMatchDataSet(dataSet5));
	}
	
	@Test public void testIsMatchEntity() throws GenstarException {
		ISyntheticPopulationGenerator bondyPopulationGenerator = new SyntheticPopulationGenerator("Population of Bondy", 100);
		
		EnumerationOfRangesAttribute ageRangesAttr1 = new EnumerationOfRangesAttribute(bondyPopulationGenerator, "age_range_1", "age", ValueType.INTEGER);
		RangeValue ageRange1 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		RangeValue ageRange2 = new RangeValue(ValueType.INTEGER, Integer.toString(BondyData.age_ranges_1[1][0]), Integer.toString(BondyData.age_ranges_1[1][1]));
		ageRangesAttr1.add(ageRange1);
		ageRangesAttr1.add(ageRange2);
		bondyPopulationGenerator.addAttribute(ageRangesAttr1);
		
		
		EnumerationOfValuesAttribute sexAttr = new EnumerationOfValuesAttribute(bondyPopulationGenerator, "sex", ValueType.BOOL);
		UniqueValue maleValueOrigin = new UniqueValue(ValueType.BOOL, Boolean.toString(BondyData.sexes[0]));
		sexAttr.add(maleValueOrigin);
		bondyPopulationGenerator.addAttribute(sexAttr);

		
		Entity entity1 = new Entity(bondyPopulationGenerator.generate());
		entity1.putAttributeValue(ageRangesAttr1, ageRange1);
		entity1.putAttributeValue(sexAttr, maleValueOrigin);
		
		Map<EnumerationValueAttribute, AttributeValue> attributeValues = new HashMap<EnumerationValueAttribute, AttributeValue>();
		attributeValues.put(ageRangesAttr1, ageRange1);
		attributeValues.put(sexAttr, maleValueOrigin);
		FrequencyDistributionElement element = new FrequencyDistributionElement(attributeValues);
		
		List<AbstractAttribute> inputAttributes1 = new ArrayList<AbstractAttribute>();
		assertTrue(element.isMatchEntity(inputAttributes1, entity1));
		
		inputAttributes1.add(ageRangesAttr1);
		assertTrue(element.isMatchEntity(inputAttributes1, entity1));
		
		inputAttributes1.add(sexAttr);
		assertTrue(element.isMatchEntity(inputAttributes1, entity1));
		
		
		Map<EnumerationValueAttribute, AttributeValue> attributeValues1 = new HashMap<EnumerationValueAttribute, AttributeValue>();
		attributeValues1.put(ageRangesAttr1, ageRange2);
		attributeValues1.put(sexAttr, maleValueOrigin);
		FrequencyDistributionElement element1 = new FrequencyDistributionElement(attributeValues1);
		
		assertFalse(element1.isMatchEntity(inputAttributes1, entity1));
		

		
		Map<EnumerationValueAttribute, AttributeValue> attributeValues2 = new HashMap<EnumerationValueAttribute, AttributeValue>();
		attributeValues2.put(ageRangesAttr1, ageRange2);
		FrequencyDistributionElement element2 = new FrequencyDistributionElement(attributeValues2);
		 
		assertFalse(element2.isMatchEntity(inputAttributes1, entity1));
		
	}
}
