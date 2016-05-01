package ummisco.genstar.metamodel.attributes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.MultipleRulesGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;

public class AttributeValuesFrequencyTest {

	@Test public void testMatchAttributeValues() throws GenstarException {
		MultipleRulesGenerator bondyPopulationGenerator = new MultipleRulesGenerator("Population of Bondy", 100);
		
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
		AttributeValuesFrequency element = new AttributeValuesFrequency(catersian1);
		
		
		Map<AbstractAttribute, AttributeValue> dataSet1 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet1.put(ageRangesAttr1, ageRange1);
		assertTrue(element.matchAttributeValuesOnData(dataSet1));
		
		dataSet1.put(sexAttr, maleValueOrigin);
		assertTrue(element.matchAttributeValuesOnData(dataSet1));
		
		
		Map<AbstractAttribute, AttributeValue> dataSet2 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet2.put(ageRangesAttr1, ageRange2);
		assertFalse(element.matchAttributeValuesOnData(dataSet2));
		
		
		Map<AbstractAttribute, AttributeValue> dataSet3 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet3.put(ageRangesAttr1, copyAgeRange1);
		assertTrue(element.matchAttributeValuesOnData(dataSet3));
		
		
		Map<AbstractAttribute, AttributeValue> dataSet4 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet4.put(ageRangesAttr1, copyAgeRange1);
		dataSet4.put(sexAttr, maleValueCopy);
		assertTrue(element.matchAttributeValuesOnData(dataSet4));
		
		
		Map<AbstractAttribute, AttributeValue> dataSet5 = new HashMap<AbstractAttribute, AttributeValue>();
		dataSet5.put(ageRangesAttr1, copyAgeRange1);
		dataSet5.put(sexAttr, femaleValue);
		assertFalse(element.matchAttributeValuesOnData(dataSet5));
	}
	
	@Test public void testMatchEntity() throws GenstarException {
		ISyntheticPopulationGenerator bondyPopulationGenerator = new MultipleRulesGenerator("Population of Bondy", 100);
		
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
		
		
		Entity entity1 = new Entity(bondyPopulationGenerator.generate());
		entity1.setAttributeValueOnData(ageRangesAttr1, ageRange1);
		entity1.setAttributeValueOnData(sexAttr, maleValueOrigin);
		
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues.put(ageRangesAttr1, ageRange1);
		attributeValues.put(sexAttr, maleValueOrigin);
		AttributeValuesFrequency avf = new AttributeValuesFrequency(attributeValues);
		
		List<AbstractAttribute> inputAttributes1 = new ArrayList<AbstractAttribute>();
		assertTrue(avf.matchEntity(inputAttributes1, entity1));
		
		inputAttributes1.add(ageRangesAttr1);
		assertTrue(avf.matchEntity(inputAttributes1, entity1));
		
		inputAttributes1.add(sexAttr);
		assertTrue(avf.matchEntity(inputAttributes1, entity1));
		
		
		Map<AbstractAttribute, AttributeValue> attributeValues1 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues1.put(ageRangesAttr1, ageRange2);
		attributeValues1.put(sexAttr, maleValueOrigin);
		AttributeValuesFrequency avf1 = new AttributeValuesFrequency(attributeValues1);
		
		assertFalse(avf1.matchEntity(inputAttributes1, entity1));
		

		Map<AbstractAttribute, AttributeValue> attributeValues2 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues2.put(ageRangesAttr1, ageRange2);
		AttributeValuesFrequency avf2 = new AttributeValuesFrequency(attributeValues2);
		 
		assertFalse(avf2.matchEntity(inputAttributes1, entity1));
	}
	
	
	@Test public void testMatchEntity1() throws GenstarException {
		ISyntheticPopulationGenerator bondyPopulationGenerator = new MultipleRulesGenerator("Population of Bondy", 100);
		
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

		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues.put(ageRangesAttr1, ageRange1);
		attributeValues.put(sexAttr, maleValueOrigin);
		AttributeValuesFrequency avf1 = new AttributeValuesFrequency(attributeValues);

		Entity entity1 = new Entity(bondyPopulationGenerator.generate());
		assertFalse(avf1.matchEntity(entity1));
		
		entity1.setAttributeValueOnData(ageRangesAttr1, ageRange1);
		assertFalse(avf1.matchEntity(entity1));

		entity1.setAttributeValueOnData(sexAttr, maleValueOrigin);
		assertTrue(avf1.matchEntity(entity1));
		
		
		AttributeValuesFrequency avf2 = new AttributeValuesFrequency(attributeValues);
		assertTrue(avf2.matchEntity(entity1));
		
		
		Map<AbstractAttribute, AttributeValue> attributeValues1 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues1.put(ageRangesAttr1, ageRange1);
		AttributeValuesFrequency avf3 = new AttributeValuesFrequency(attributeValues1);
		assertTrue(avf3.matchEntity(entity1));
	}
	
	
	@Test public void testGetAttributes() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SingleRuleGenerator("generator");
		
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/GenstarUtils/testGenerateAttributeValuesFrequencies/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);

		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (AbstractAttribute attr : generator.getAttributes()) {
			attributeValues.put(attr, attr.valuesOnData().iterator().next());
		}
		
		new AttributeValuesFrequency(attributeValues);
	}
	
}
