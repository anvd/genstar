package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarException;

public class AttributeValuesFrequencyTest {

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
		AttributeValuesFrequency element = new AttributeValuesFrequency(generationRule, catersian1);
		
		
		Map<AbstractAttribute, AttributeValue> dataSet1 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet1.put(ageRangesAttr1, ageRange1);
		assertTrue(element.isMatch(dataSet1));
		
		dataSet1.put(sexAttr, maleValueOrigin);
		assertTrue(element.isMatch(dataSet1));
		
		
		Map<AbstractAttribute, AttributeValue> dataSet2 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet2.put(ageRangesAttr1, ageRange2);
		assertFalse(element.isMatch(dataSet2));
		
		
		Map<AbstractAttribute, AttributeValue> dataSet3 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet3.put(ageRangesAttr1, copyAgeRange1);
		assertTrue(element.isMatch(dataSet3));
		
		
		Map<AbstractAttribute, AttributeValue> dataSet4 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet4.put(ageRangesAttr1, copyAgeRange1);
		dataSet4.put(sexAttr, maleValueCopy);
		assertTrue(element.isMatch(dataSet4));
		
		
		Map<AbstractAttribute, AttributeValue> dataSet5 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet5.put(ageRangesAttr1, copyAgeRange1);
		dataSet5.put(sexAttr, femaleValue);
		assertFalse(element.isMatch(dataSet5));
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
		AttributeValuesFrequency element = new AttributeValuesFrequency(generationRule, attributeValues);
		
		List<AbstractAttribute> inputAttributes1 = new ArrayList<AbstractAttribute>();
		assertTrue(element.isMatchEntity(inputAttributes1, entity1));
		
		inputAttributes1.add(ageRangesAttr1);
		assertTrue(element.isMatchEntity(inputAttributes1, entity1));
		
		inputAttributes1.add(sexAttr);
		assertTrue(element.isMatchEntity(inputAttributes1, entity1));
		
		
		Map<AbstractAttribute, AttributeValue> attributeValues1 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues1.put(ageRangesAttr1, ageRange2);
		attributeValues1.put(sexAttr, maleValueOrigin);
		AttributeValuesFrequency element1 = new AttributeValuesFrequency(generationRule, attributeValues1);
		
		assertFalse(element1.isMatchEntity(inputAttributes1, entity1));
		

		
		Map<AbstractAttribute, AttributeValue> attributeValues2 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues2.put(ageRangesAttr1, ageRange2);
		AttributeValuesFrequency element2 = new AttributeValuesFrequency(generationRule, attributeValues2);
		 
		assertFalse(element2.isMatchEntity(inputAttributes1, entity1));
		
	}
}
