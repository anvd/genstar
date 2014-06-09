package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import ummisco.genstar.exception.GenstarException;

public class FrequencyDistributionElementTest {

	@Test public void testIsMatchDataSet() throws GenstarException {
		SyntheticPopulationGenerator bondyPopulationGenerator = new SyntheticPopulationGenerator("Population of Bondy", 100);
		
		RangeValuesAttribute ageRangesAttr1 = new RangeValuesAttribute(bondyPopulationGenerator, "age_range_1", "age", DataType.INTEGER);
		RangeValue ageRange1 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		RangeValue ageRange2 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[1][0]), Integer.toString(BondyData.age_ranges_1[1][1]));
		ageRangesAttr1.add(ageRange1);
		ageRangesAttr1.add(ageRange2);
		bondyPopulationGenerator.addAttribute(ageRangesAttr1);
		
		RangeValue copyAgeRange1 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(bondyPopulationGenerator, "sex", DataType.BOOL);
		UniqueValue maleValueOrigin = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0]));
		UniqueValue maleValueCopy = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0]));
		UniqueValue femaleValue = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[1]));
		sexAttr.add(maleValueOrigin);
		bondyPopulationGenerator.addAttribute(sexAttr);
		
		
		FrequencyDistributionGenerationRule generationRule = new FrequencyDistributionGenerationRule(bondyPopulationGenerator, "age - sex inference rule");
		generationRule.appendInputAttribute(ageRangesAttr1);
		generationRule.appendOutputAttribute(sexAttr);

		Map<AbstractAttribute, AttributeValue> catersian1 = new HashMap<AbstractAttribute, AttributeValue>();
		catersian1.put(ageRangesAttr1, ageRange1);
		catersian1.put(sexAttr, maleValueOrigin);
		FrequencyDistributionElement element = new FrequencyDistributionElement(generationRule, catersian1);
		
		
		Map<AbstractAttribute, AttributeValue> dataSet1 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet1.put(ageRangesAttr1, ageRange1);
		assertTrue(element.isMatchDataSet(dataSet1));
		
		dataSet1.put(sexAttr, maleValueOrigin);
		assertTrue(element.isMatchDataSet(dataSet1));
		
		
		Map<AbstractAttribute, AttributeValue> dataSet2 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet2.put(ageRangesAttr1, ageRange2);
		assertFalse(element.isMatchDataSet(dataSet2));
		
		
		Map<AbstractAttribute, AttributeValue> dataSet3 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet3.put(ageRangesAttr1, copyAgeRange1);
		assertTrue(element.isMatchDataSet(dataSet3));
		
		
		Map<AbstractAttribute, AttributeValue> dataSet4 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet4.put(ageRangesAttr1, copyAgeRange1);
		dataSet4.put(sexAttr, maleValueCopy);
		assertTrue(element.isMatchDataSet(dataSet4));
		
		
		Map<AbstractAttribute, AttributeValue> dataSet5 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet5.put(ageRangesAttr1, copyAgeRange1);
		dataSet5.put(sexAttr, femaleValue);
		assertFalse(element.isMatchDataSet(dataSet5));
	}
	
	@Test public void testIsMatchEntity() throws GenstarException {
		ISyntheticPopulationGenerator bondyPopulationGenerator = new SyntheticPopulationGenerator("Population of Bondy", 100);
		
		RangeValuesAttribute ageRangesAttr1 = new RangeValuesAttribute(bondyPopulationGenerator, "age_range_1", "age", DataType.INTEGER);
		RangeValue ageRange1 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		RangeValue ageRange2 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[1][0]), Integer.toString(BondyData.age_ranges_1[1][1]));
		ageRangesAttr1.add(ageRange1);
		ageRangesAttr1.add(ageRange2);
		bondyPopulationGenerator.addAttribute(ageRangesAttr1);
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(bondyPopulationGenerator, "sex", DataType.BOOL);
		UniqueValue maleValueOrigin = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0]));
		sexAttr.add(maleValueOrigin);
		bondyPopulationGenerator.addAttribute(sexAttr);
		
		
		FrequencyDistributionGenerationRule generationRule = new FrequencyDistributionGenerationRule(bondyPopulationGenerator, "age - sex inference rule");
		generationRule.appendInputAttribute(ageRangesAttr1);
		generationRule.appendOutputAttribute(sexAttr);

		
		Entity entity1 = new Entity(bondyPopulationGenerator.generate());
		entity1.putAttributeValue(ageRangesAttr1, ageRange1);
		entity1.putAttributeValue(sexAttr, maleValueOrigin);
		
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues.put(ageRangesAttr1, ageRange1);
		attributeValues.put(sexAttr, maleValueOrigin);
		FrequencyDistributionElement element = new FrequencyDistributionElement(generationRule, attributeValues);
		
		List<AbstractAttribute> inputAttributes1 = new ArrayList<AbstractAttribute>();
		assertTrue(element.isMatchEntity(inputAttributes1, entity1));
		
		inputAttributes1.add(ageRangesAttr1);
		assertTrue(element.isMatchEntity(inputAttributes1, entity1));
		
		inputAttributes1.add(sexAttr);
		assertTrue(element.isMatchEntity(inputAttributes1, entity1));
		
		
		Map<AbstractAttribute, AttributeValue> attributeValues1 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues1.put(ageRangesAttr1, ageRange2);
		attributeValues1.put(sexAttr, maleValueOrigin);
		FrequencyDistributionElement element1 = new FrequencyDistributionElement(generationRule, attributeValues1);
		
		assertFalse(element1.isMatchEntity(inputAttributes1, entity1));
		

		
		Map<AbstractAttribute, AttributeValue> attributeValues2 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues2.put(ageRangesAttr1, ageRange2);
		FrequencyDistributionElement element2 = new FrequencyDistributionElement(generationRule, attributeValues2);
		 
		assertFalse(element2.isMatchEntity(inputAttributes1, entity1));
		
	}
}
