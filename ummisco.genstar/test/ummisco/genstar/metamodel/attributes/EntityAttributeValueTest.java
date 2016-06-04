package ummisco.genstar.metamodel.attributes;

import static org.junit.Assert.*;

import org.junit.Test;

import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;

public class EntityAttributeValueTest {

	
	@Test public void testCreateEntityAttributeValue1() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SampleFreeGenerator("Population of Bondy", 100);
		generator.setNbOfEntities(1);
		
		RangeValuesAttribute ageRangesAttr1 = new RangeValuesAttribute(generator, "age_range_1", "age", DataType.INTEGER, UniqueValue.class);
		RangeValue ageRange1 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]));
		RangeValue ageRange2 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[1][0]), Integer.toString(BondyData.age_ranges_1[1][1]));
		ageRangesAttr1.add(ageRange1);
		ageRangesAttr1.add(ageRange2);
		generator.addAttribute(ageRangesAttr1);
		
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(generator, "sex", DataType.BOOL);
		UniqueValue maleValueOrigin = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0]));
		sexAttr.add(maleValueOrigin);
		generator.addAttribute(sexAttr);

		EntityAttributeValue entityAgeValue1 = new EntityAttributeValue(ageRangesAttr1, ageRange1);
		assertTrue(ageRange1.equals(entityAgeValue1.getAttributeValueOnData()));
		assertTrue(ageRange1.cover((UniqueValue) entityAgeValue1.getAttributeValueOnEntity()));
		
		EntityAttributeValue entitySexValue = new EntityAttributeValue(sexAttr, maleValueOrigin);
		assertTrue(entitySexValue.getAttributeValueOnData().equals(maleValueOrigin));
		assertTrue(entitySexValue.getAttributeValueOnEntity().equals(maleValueOrigin));
	}
	
	@Test public void testCreateEntityAttributeValueWithInvalidParameters() throws GenstarException {
		fail("Not yet implemented");
	}
	
	@Test(expected = GenstarException.class) public void testCreateEntityAttributeValueWithInvalidAttributeValueOnData() throws GenstarException {
		String basePath = "test_data/ummisco/genstar/metamodel/attributes/EntityAttributeValue/testCreateEntityAttributeValueWithInvalidAttributeValueOnData/";
		
		GenstarCsvFile householdAttributesFile = new GenstarCsvFile(basePath + "household_attributes.csv", true);
		
		ISyntheticPopulationGenerator generator = new SampleFreeGenerator("generator", 100);
		AttributeUtils.createAttributesFromCsvFile(generator, householdAttributesFile);
		
		AbstractAttribute householdSizeAttr = generator.getAttributeByNameOnData("Household Size");

		UniqueValue invalidHouseholdSizeValueOnData = new UniqueValue(DataType.INTEGER, "4");
		UniqueValue valueOnEntity = invalidHouseholdSizeValueOnData;
		new EntityAttributeValue(householdSizeAttr, invalidHouseholdSizeValueOnData, valueOnEntity);
	}
	
	@Test(expected = GenstarException.class) public void testCreateEntityAttributeValueWithInvalidAttributeValueOnData1() throws GenstarException {
		String basePath = "test_data/ummisco/genstar/metamodel/attributes/EntityAttributeValue/testCreateEntityAttributeValueWithInvalidAttributeValueOnData1/";
		
		GenstarCsvFile householdAttributesFile = new GenstarCsvFile(basePath + "household_attributes.csv", true);
		
		ISyntheticPopulationGenerator generator = new SampleFreeGenerator("generator", 100);
		AttributeUtils.createAttributesFromCsvFile(generator, householdAttributesFile);
		
		AbstractAttribute householdSizeAttr = generator.getAttributeByNameOnData("Household Size");

		UniqueValue invalidHouseholdSizeValueOnData = new UniqueValue(DataType.INTEGER, "4");
		new EntityAttributeValue(householdSizeAttr, invalidHouseholdSizeValueOnData);
	}
	
	@Test(expected = GenstarException.class) public void testCreateEntityAttributeValueWithInvalidAttributeValueOnData2() throws GenstarException {
		String basePath = "test_data/ummisco/genstar/metamodel/attributes/EntityAttributeValue/testCreateEntityAttributeValueWithInvalidAttributeValueOnData2/";
		
		GenstarCsvFile peopleAttributesFile = new GenstarCsvFile(basePath + "people_attributes.csv", true);
		
		ISyntheticPopulationGenerator generator = new SampleFreeGenerator("generator", 100);
		AttributeUtils.createAttributesFromCsvFile(generator, peopleAttributesFile);
		
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age");

		RangeValue invalidAgeValueOnData = new RangeValue(DataType.INTEGER, "0", "1");
		new EntityAttributeValue(ageAttr, invalidAgeValueOnData);
	}

	@Test(expected = GenstarException.class) public void testCreateEntityAttributeValueWithInvalidAttributeValueOnData3() throws GenstarException {
		String basePath = "test_data/ummisco/genstar/metamodel/attributes/EntityAttributeValue/testCreateEntityAttributeValueWithInvalidAttributeValueOnData3/";
		
		GenstarCsvFile peopleAttributesFile = new GenstarCsvFile(basePath + "people_attributes.csv", true);
		
		ISyntheticPopulationGenerator generator = new SampleFreeGenerator("generator", 100);
		AttributeUtils.createAttributesFromCsvFile(generator, peopleAttributesFile);
		
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age");

		RangeValue invalidAgeValueOnData = new RangeValue(DataType.INTEGER, "0", "1");
		UniqueValue ageValueOnEntity = new UniqueValue(DataType.INTEGER, "1");
		new EntityAttributeValue(ageAttr, invalidAgeValueOnData, ageValueOnEntity);
	}
	
	
	@Test(expected = GenstarException.class) public void testCreateEntityAttributeValueWithMismatchedAttributeValues() throws GenstarException {
		String basePath = "test_data/ummisco/genstar/metamodel/attributes/EntityAttributeValue/testCreateEntityAttributeValueWithMismatchedAttributeValues/";
		
		GenstarCsvFile householdAttributesFile = new GenstarCsvFile(basePath + "household_attributes.csv", true);
		
		ISyntheticPopulationGenerator generator = new SampleFreeGenerator("generator", 100);
		AttributeUtils.createAttributesFromCsvFile(generator, householdAttributesFile);
		
		AbstractAttribute householdSizeAttr = generator.getAttributeByNameOnData("Household Size");

		UniqueValue valueOnData = new UniqueValue(DataType.INTEGER, "3");
		UniqueValue valueOnEntity = new UniqueValue(DataType.INTEGER, "2");
		new EntityAttributeValue(householdSizeAttr, valueOnData, valueOnEntity);
	}

	@Test(expected = GenstarException.class) public void testCreateEntityAttributeValueWithMismatchedAttributeValues1() throws GenstarException {
		String basePath = "test_data/ummisco/genstar/metamodel/attributes/EntityAttributeValue/testCreateEntityAttributeValueWithMismatchedAttributeValues1/";
		
		GenstarCsvFile peopleAttributesFile = new GenstarCsvFile(basePath + "people_attributes.csv", true);
		
		ISyntheticPopulationGenerator generator = new SampleFreeGenerator("generator", 100);
		AttributeUtils.createAttributesFromCsvFile(generator, peopleAttributesFile);
		
		AbstractAttribute householdSizeAttr = generator.getAttributeByNameOnData("Age");

		RangeValue valueOnData = new RangeValue(DataType.INTEGER, "0", "15");
		UniqueValue valueOnEntity = new UniqueValue(DataType.INTEGER, "16");
		new EntityAttributeValue(householdSizeAttr, valueOnData, valueOnEntity);
	}
}
