package ummisco.genstar.metamodel.attributes;

import static org.junit.Assert.*;

import org.junit.Test;

import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;

public class EntityAttributeValueTest {

	
	@Test public void testCreateEntityAttributeValue1() throws GenstarException {
		ISyntheticPopulationGenerator bondyPopulation = new SampleFreeGenerator("Population of Bondy", 100);
		bondyPopulation.setNbOfEntities(1);
		
		RangeValuesAttribute ageRangesAttr1 = new RangeValuesAttribute("age_range_1", "age", DataType.INTEGER, UniqueValue.class);
		RangeValue ageRange1 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[0][0]), Integer.toString(BondyData.age_ranges_1[0][1]), ageRangesAttr1);
		RangeValue ageRange2 = new RangeValue(DataType.INTEGER, Integer.toString(BondyData.age_ranges_1[1][0]), Integer.toString(BondyData.age_ranges_1[1][1]), ageRangesAttr1);
		ageRangesAttr1.add(ageRange1);
		ageRangesAttr1.add(ageRange2);
		bondyPopulation.addAttribute(ageRangesAttr1);
		
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute("sex", DataType.BOOL);
		UniqueValue maleValueOrigin = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0]), sexAttr);
		sexAttr.add(maleValueOrigin);
		bondyPopulation.addAttribute(sexAttr);

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
}
