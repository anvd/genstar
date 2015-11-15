package ummisco.genstar.ipf;

import static org.junit.Assert.*;
import static mockit.Deencapsulation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.AttributeValuesFrequency;
import ummisco.genstar.metamodel.DataType;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.RangeValue;
import ummisco.genstar.metamodel.SyntheticPopulationGenerator;
import ummisco.genstar.metamodel.UniqueValue;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarFactoryUtils;

@RunWith(JMockit.class)
public class ControlTotalsTest {

	@Test public void testGetMatchingAttributeValuesFrequenciesForUniqueValue(@Mocked final SampleDataGenerationRule rule1, @Mocked final SampleDataGenerationRule rule2) throws GenstarException {
		final ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("generator", 1000);
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		final GenstarCSVFile frequencyFile1 = new GenstarCSVFile("test_data/ummisco/genstar/ipf/control_totals1.csv", false);

		AbstractAttribute householdSizeAttr = generator.getAttribute("Household Size");
		AbstractAttribute householdIncomeAttr = generator.getAttribute("Household Income");
		
		final List<AbstractAttribute> rule1ControlledAttributes = new ArrayList<AbstractAttribute>();
		rule1ControlledAttributes.add(householdSizeAttr);
		rule1ControlledAttributes.add(householdIncomeAttr);

		new Expectations() {{
			onInstance(rule1).getGenerator(); result = generator;
			onInstance(rule1).getControlledAttributes(); result = rule1ControlledAttributes;
			onInstance(rule1).getControlTotalsFile(); result = frequencyFile1;
		}};

		ControlTotals avfCSVFile1 = new ControlTotals(rule1);
		
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		
		/*
		control_totals1.csv
			Household Size,1,20
			Household Size,2,50
			Household Size,3,30
			Household Income,High,40
			Household Income,Low,60		 
		 */
		AbstractAttribute householdSize = generator.getAttribute("Household Size");
		AbstractAttribute householdIncome = generator.getAttribute("Household Income");
		
		AttributeValue householdSizeOne = new UniqueValue(householdSize.getDataType(), "1");
		AttributeValue householdSizeTwo = new UniqueValue(householdSize.getDataType(), "2");
		AttributeValue householdSizeThree = new UniqueValue(householdSize.getDataType(), "3");
		
		matchingCriteria.put(householdSize, householdSizeOne);
		List<AttributeValuesFrequency> result1 = avfCSVFile1.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(result1.size() == 1);
		assertTrue(result1.get(0).getFrequency() == 20);
		
		matchingCriteria.put(householdSize, householdSizeTwo);
		List<AttributeValuesFrequency> result2 = avfCSVFile1.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(result2.size() == 1);
		assertTrue(result2.get(0).getFrequency() == 50);
		
		matchingCriteria.put(householdSize, householdSizeThree);
		List<AttributeValuesFrequency> result3 = avfCSVFile1.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(result3.size() == 1);
		assertTrue(result3.get(0).getFrequency() == 30);

		AttributeValue householdSizeFour = new UniqueValue(householdSize.getDataType(), "4");
		matchingCriteria.put(householdSize, householdSizeFour);
		List<AttributeValuesFrequency> result4 = avfCSVFile1.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(result4.size() == 0);
		
		AttributeValue householdIncomeHigh = new UniqueValue(householdIncome.getDataType(), "High");
		
		matchingCriteria.clear();
		matchingCriteria.put(householdIncome, householdIncomeHigh);
		List<AttributeValuesFrequency> result5 = avfCSVFile1.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(result5.size() == 1);
		
		final GenstarCSVFile frequencyFile2 = new GenstarCSVFile("test_data/ummisco/genstar/ipf/control_totals2.csv", false);
		
		AbstractAttribute nbOfCarAttr = generator.getAttribute("Number Of Cars");
		
		final List<AbstractAttribute> rule2ControlledAttributes = new ArrayList<AbstractAttribute>();
		rule2ControlledAttributes.add(householdSizeAttr);
		rule2ControlledAttributes.add(householdIncomeAttr);
		rule2ControlledAttributes.add(nbOfCarAttr);
		
		
		new Expectations() {{
			onInstance(rule2).getGenerator(); result = generator;
			onInstance(rule2).getControlledAttributes(); result = rule2ControlledAttributes;
			onInstance(rule2).getControlTotalsFile(); result = frequencyFile2;
		}};
		
		ControlTotals avfCSVFile2 = new ControlTotals(rule2);
		
		/*
		control_totals2.csv
			...
			Household Size,2,Household Income,High,50
			...
			Household Size,3,Number Of Cars,3,14
			Household Income,Low,Number Of Cars,0,15
			...
		 */
		matchingCriteria.put(householdSize, householdSizeTwo);
		matchingCriteria.put(householdIncome, householdIncomeHigh);
		List<AttributeValuesFrequency> result6 = avfCSVFile2.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(result6.size() == 1);
		assertTrue(result6.get(0).getFrequency() == 50);
		
		
		AbstractAttribute nbOfCars = generator.getAttribute("Number Of Cars");
		AttributeValue nbOfCarsThree = new UniqueValue(nbOfCars.getDataType(), "3");
		AttributeValue nbOfCarsZero =  new UniqueValue(nbOfCars.getDataType(), "0");
		
		matchingCriteria.clear();
		matchingCriteria.put(householdSize, householdSizeThree);
		matchingCriteria.put(nbOfCars, nbOfCarsThree);
		List<AttributeValuesFrequency> result7 = avfCSVFile2.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(result7.size() == 1);
		assertTrue(result7.get(0).getFrequency() == 14);
		
		AttributeValue householdIncomeLow = new UniqueValue(householdIncome.getDataType(), "Low");
		matchingCriteria.clear();
		matchingCriteria.put(householdIncome, householdIncomeLow);
		matchingCriteria.put(nbOfCars, nbOfCarsZero);
		List<AttributeValuesFrequency> result8 = avfCSVFile2.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(result8.size() == 1);
		assertTrue(result8.get(0).getFrequency() == 15);
		
		
		matchingCriteria.clear();
		matchingCriteria.put(householdSize, householdSizeTwo);
		List<AttributeValuesFrequency> result9 = avfCSVFile2.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(result9.size() == 0);
	}
	
	@Test public void testParseAttributeValuesFrequencyForUniqueValue(@Mocked final SampleDataGenerationRule rule1, @Mocked final SampleDataGenerationRule rule2) throws GenstarException {
		
		final ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("generator", 1000);
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);

		AbstractAttribute householdSizeAttr = generator.getAttribute("Household Size");
		AbstractAttribute householdIncomeAttr = generator.getAttribute("Household Income");

		final List<AbstractAttribute> rule1ControlledAttributes = new ArrayList<AbstractAttribute>();
		rule1ControlledAttributes.add(householdSizeAttr);
		rule1ControlledAttributes.add(householdIncomeAttr);

		final GenstarCSVFile frequencyFile1 = new GenstarCSVFile("test_data/ummisco/genstar/ipf/control_totals1.csv", false);

		new Expectations() {{
			onInstance(rule1).getGenerator(); result = generator;
			onInstance(rule1).getControlledAttributes(); result = rule1ControlledAttributes;
			onInstance(rule1).getControlTotalsFile(); result = frequencyFile1;
		}};
		
		ControlTotals avfCSVFile1 = new ControlTotals(rule1);
		List<AttributeValuesFrequency> avFrequencies1 = getField(avfCSVFile1, "avFrequencies");
		assertTrue(avFrequencies1.size() == 5);
		
		AbstractAttribute nbOfCarsAttr = generator.getAttribute("Number Of Cars");
		
		final List<AbstractAttribute> rule2ControlledAttributes = new ArrayList<AbstractAttribute>();
		rule2ControlledAttributes.add(householdSizeAttr);
		rule2ControlledAttributes.add(householdIncomeAttr);
		rule2ControlledAttributes.add(nbOfCarsAttr);

		final GenstarCSVFile frequencyFile2 = new GenstarCSVFile("test_data/ummisco/genstar/ipf/control_totals2.csv", false);

		new Expectations() {{
			onInstance(rule2).getGenerator(); result = generator;
			onInstance(rule2).getControlledAttributes(); result = rule2ControlledAttributes;
			onInstance(rule2).getControlTotalsFile(); result = frequencyFile2;
		}};
		
		ControlTotals avfCSVFile2 = new ControlTotals(rule2);
		List<AttributeValuesFrequency> avFrequencies2 = getField(avfCSVFile2, "avFrequencies");
		assertTrue(avFrequencies2.size() == 26);
	}
	
	@Test public void testGetMatchingAttributeValuesFrequenciesForRangeValue(@Mocked final SampleDataGenerationRule rule) throws GenstarException {
		final ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("generator", 1000);
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/attributes1.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		final GenstarCSVFile frequencyFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/control_totals3.csv", false);
		
		AbstractAttribute ageAttr = generator.getAttribute("Age");
		AbstractAttribute genderAttr = generator.getAttribute("Gender");
		
		final List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
		controlledAttributes.add(ageAttr);
		controlledAttributes.add(genderAttr);
				
		new Expectations() {{
			rule.getGenerator(); result = generator;
			rule.getControlledAttributes(); result = controlledAttributes;
			rule.getControlTotalsFile(); result = frequencyFile;
		}};
		
		
		/*
		 control_totals3.csv
			Age,0:15,Gender,true,10
			Age,16:20,Gender,true,15
			Age,21:25,Gender,true,25
			Age,26:30,Gender,true,20
			Age,31:50,Gender,true,10
			Age,51:60,Gender,true,15
			Age,61:75,Gender,true,40
			Age,76:80,Gender,true,35
			Age,81:100,Gender,true,5
			Age,0:15,Gender,false,17
			Age,16:20,Gender,false,34
			Age,21:25,Gender,false,21
			Age,26:30,Gender,false,15
			Age,31:50,Gender,false,12
			Age,51:60,Gender,false,9
			Age,61:75,Gender,false,19
			Age,76:80,Gender,false,5
			Age,81:100,Gender,false,3 
		 */
		ControlTotals avfCSVFile = new ControlTotals(rule);
		
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		
		AttributeValue age_0_15 = new RangeValue(DataType.INTEGER, "0", "15");
		AttributeValue male = new UniqueValue(DataType.BOOL, "true");
		AttributeValue female = new UniqueValue(DataType.BOOL, "false");
		
		matchingCriteria.put(ageAttr, age_0_15);
		
		List<AttributeValuesFrequency> matching1 = avfCSVFile.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(matching1.size() == 2);
		
		assertTrue(matching1.get(0).getFrequency() == 10);
		assertTrue(matching1.get(1).getFrequency() == 17);
		
		matchingCriteria.put(genderAttr, male);
		matching1 = avfCSVFile.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(matching1.size() == 1);
		assertTrue(matching1.get(0).getFrequency() == 10);
		
		matchingCriteria.put(genderAttr, female);
		matching1 = avfCSVFile.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(matching1.size() == 1);
		assertTrue(matching1.get(0).getFrequency() == 17);
		
	}
	
	@Test(expected = GenstarException.class) public void testAttributeValuesFrequenciesFileContainsInvalidAttribute(@Mocked final SampleDataGenerationRule rule) throws GenstarException {
		final ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("generator", 1000);
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/attributes1.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		AbstractAttribute ageAttr = generator.getAttribute("Age");
		AbstractAttribute genderAttr = generator.getAttribute("Gender");

		final List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
		controlledAttributes.add(ageAttr);
		controlledAttributes.add(genderAttr);

		final GenstarCSVFile frequencyFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/control_totals4.csv", false);
		
		new Expectations() {{
			rule.getGenerator(); result = generator;
			rule.getControlledAttributes(); result = controlledAttributes;
			rule.getControlTotalsFile(); result = frequencyFile;
		}};
		
		
		/*
		 control_totals4.csv
			Age,0:15,Gender1,true,10
			Age,16:20,Gender,true,15
			Age,21:25,Gender,true,25
			Age,26:30,Gender,true,20
			Age,31:50,Gender,true,10
			Age,51:60,Gender,true,15
			Age,61:75,Gender,true,40
			Age,76:80,Gender,true,35
			Age,81:100,Gender,true,5
			Age,0:15,Gender,false,17
			Age,16:20,Gender,false,34
			Age,21:25,Gender,false,21
			Age,26:30,Gender,false,15
			Age,31:50,Gender,false,12
			Age,51:60,Gender,false,9
			Age,61:75,Gender,false,19
			Age,76:80,Gender,false,5
			Age,81:100,Gender,false,3			
		 */
		new ControlTotals(rule);	
	}
	
	@Test(expected = GenstarException.class) public void testAttributeValuesFrequenciesFileContainsInvalidControlledAttribute(@Mocked final SampleDataGenerationRule rule) throws GenstarException {
		final ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("generator", 1000);
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/attributes1.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		AbstractAttribute ageAttr = generator.getAttribute("Age");
		AbstractAttribute genderAttr = generator.getAttribute("Gender");

		final List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
		controlledAttributes.add(ageAttr);
		controlledAttributes.add(genderAttr);

		final GenstarCSVFile frequencyFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/control_totals5.csv", false);
		
		new Expectations() {{
			rule.getGenerator(); result = generator;
			rule.getControlledAttributes(); result = controlledAttributes;
			rule.getControlTotalsFile(); result = frequencyFile;
		}};
		
		
		/*
		 control_totals5.csv
		 	Gender,true,Work,agriculteur,10
		 */
		new ControlTotals(rule);		
	}
	
	
	@Test public void testGetTotal(@Mocked final SampleDataGenerationRule rule) throws GenstarException {
		final ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("generator", 1000);
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);

		AbstractAttribute householdSizeAttr = generator.getAttribute("Household Size");
		AbstractAttribute householdIncomeAttr = generator.getAttribute("Household Income");
		
		final List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
		controlledAttributes.add(householdSizeAttr);
		controlledAttributes.add(householdIncomeAttr);

		final GenstarCSVFile frequencyFile1 = new GenstarCSVFile("test_data/ummisco/genstar/ipf/control_totals1.csv", false);
		
		new Expectations() {{
			rule.getGenerator(); result = generator;
			rule.getControlledAttributes(); result = controlledAttributes;
			rule.getControlTotalsFile(); result = frequencyFile1;
		}};

		ControlTotals avfCSVFile1 = new ControlTotals(rule);
		assertTrue(avfCSVFile1.getTotal() == 200);
	}
}
