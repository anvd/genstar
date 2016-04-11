package ummisco.genstar.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.IPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.UniqueValue;

@RunWith(JMockit.class)
public class IpuUtilsTest {

	@Test public void testParseIpuControlTotalsFile() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SingleRuleGenerator("generator");
		
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/util/IpuUtils/parseIpuControlTotalsFile/success/group_attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		/*
			Household Size
			Household Income
			Number Of Cars
		 */
		List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
		AbstractAttribute sizeAttribute = generator.getAttributeByNameOnData("Household Size");
		controlledAttributes.add(sizeAttribute);
		AbstractAttribute incomeAttribute = generator.getAttributeByNameOnData("Household Income");
		controlledAttributes.add(incomeAttribute);
		AbstractAttribute carsAttribute = generator.getAttributeByNameOnData("Number Of Cars");
		controlledAttributes.add(carsAttribute);
		
		GenstarCSVFile ipuControlTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/util/IpuUtils/parseIpuControlTotalsFile/success/group_ipu_control_totals.csv", false);
		
		List<AttributeValuesFrequency> avfs = IpuUtils.parseIpuControlTotalsFile(generator, controlledAttributes, ipuControlTotalsFile);
		
		assertTrue(avfs.size() == 24); // verify size
		
		// verify order
		// 		Household Size: 1,2,3
		//		Household Income: High, Low
		// 		Number Of Cars: 0,1,2,3
		AttributeValue householdSize1 = new UniqueValue(DataType.INTEGER, "1");
		AttributeValue householdSize2 = new UniqueValue(DataType.INTEGER, "2");
		AttributeValue householdSize3 = new UniqueValue(DataType.INTEGER, "3");
		List<AttributeValue> householdSizeValues = new ArrayList<AttributeValue>();
		householdSizeValues.add(householdSize1);
		householdSizeValues.add(householdSize2);
		householdSizeValues.add(householdSize3);
		
		AttributeValue householdIncomeHigh = new UniqueValue(DataType.STRING, "High");
		AttributeValue householdIncomeLow = new UniqueValue(DataType.STRING, "Low");
		List<AttributeValue> householdIncomeValues = new ArrayList<AttributeValue>();
		householdIncomeValues.add(householdIncomeHigh);
		householdIncomeValues.add(householdIncomeLow);
		
		AttributeValue cars0 = new UniqueValue(DataType.INTEGER, "0");
		AttributeValue cars1 = new UniqueValue(DataType.INTEGER, "1");
		AttributeValue cars2 = new UniqueValue(DataType.INTEGER, "2");
		AttributeValue cars3 = new UniqueValue(DataType.INTEGER, "3");
		List<AttributeValue> carValues = new ArrayList<AttributeValue>();
		carValues.add(cars0);
		carValues.add(cars1);
		carValues.add(cars2);
		carValues.add(cars3);
		
		int[] attributeValueFrequencies = { 50, 150, 100, 200, 250,
					50, 50, 100, 150, 100, 100, 150, 50, 150, 100,
					200, 125, 125, 125, 125, 100, 100, 100, 200 };
		
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		int avfsIndex = 0;
		for (AttributeValue size : householdSizeValues) {
			attributeValues.put(sizeAttribute, size);
			
			for (AttributeValue income : householdIncomeValues) {
				attributeValues.put(incomeAttribute, income);
				
				for (AttributeValue car : carValues) {
					attributeValues.put(carsAttribute, car);
					
					AttributeValuesFrequency avf = avfs.get(avfsIndex); 
					assertTrue(avf.matchAttributeValuesOnData(attributeValues));
					assertTrue(avf.getFrequency() == attributeValueFrequencies[avfsIndex]);
					avfsIndex++;
				}
			}
		}
	}
	
	
	@Test public void testBuildIpuControlTotalsOfCompoundPopulation() throws GenstarException {
		/*
	public void buildIpuControlTotalsOfCompoundPopulation(ISyntheticPopulation compoundPopulation, final String componentPopulationName, 
	final GenstarCSVFile groupControlledAttributesListFile, 
			final GenstarCSVFile componentControlledAttributesListFile, final List<List<String>> groupControlTotalsToBeBuilt, 
			final List<List<String>> componentControlTotalsToBeBuilt) throws GenstarException {
		 */
		
		
	}
}
