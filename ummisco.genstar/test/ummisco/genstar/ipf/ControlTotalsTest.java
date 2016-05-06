package ummisco.genstar.ipf;

import static mockit.Deencapsulation.getField;
import static org.junit.Assert.assertTrue;

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
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;

@RunWith(JMockit.class)
public class ControlTotalsTest { // TODO change name to IpfControlTotalsTest

	@Test public void testGetMatchingAttributeValuesFrequenciesForUniqueValue(@Mocked final IpfGenerationRule rule1, @Mocked final IpfGenerationRule rule2) throws GenstarException {
		final ISyntheticPopulationGenerator generator = new SampleBasedGenerator("generator");
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/ControlTotals/testGetMatchingAttributeValuesFrequenciesForUniqueValue/attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
		
		final GenstarCsvFile frequencyFile1 = new GenstarCsvFile("test_data/ummisco/genstar/ipf/ControlTotals/testGetMatchingAttributeValuesFrequenciesForUniqueValue/control_totals1.csv", false);

		AbstractAttribute householdSizeAttr = generator.getAttributeByNameOnData("Household Size");
		AbstractAttribute householdIncomeAttr = generator.getAttributeByNameOnData("Household Income");
		
		final List<AbstractAttribute> rule1ControlledAttributes = new ArrayList<AbstractAttribute>();
		rule1ControlledAttributes.add(householdSizeAttr);
		rule1ControlledAttributes.add(householdIncomeAttr);

		new Expectations() {{
			onInstance(rule1).getControlledAttributes(); result = rule1ControlledAttributes;
			onInstance(rule1).getControlTotalsFile(); result = frequencyFile1;
		}};

		IpfControlTotals avfCSVFile1 = new IpfControlTotals(rule1);
		
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		
		/*
		control_totals1.csv
			Household Size,1,20
			Household Size,2,50
			Household Size,3,30
			Household Income,High,40
			Household Income,Low,60		 
		 */
		AbstractAttribute householdSize = generator.getAttributeByNameOnData("Household Size");
		AbstractAttribute householdIncome = generator.getAttributeByNameOnData("Household Income");
		
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
		
		final GenstarCsvFile frequencyFile2 = new GenstarCsvFile("test_data/ummisco/genstar/ipf/ControlTotals/testGetMatchingAttributeValuesFrequenciesForUniqueValue/control_totals2.csv", false);
		
		AbstractAttribute nbOfCarAttr = generator.getAttributeByNameOnData("Number Of Cars");
		
		final List<AbstractAttribute> rule2ControlledAttributes = new ArrayList<AbstractAttribute>();
		rule2ControlledAttributes.add(householdSizeAttr);
		rule2ControlledAttributes.add(householdIncomeAttr);
		rule2ControlledAttributes.add(nbOfCarAttr);
		
		
		new Expectations() {{
			onInstance(rule2).getControlledAttributes(); result = rule2ControlledAttributes;
			onInstance(rule2).getControlTotalsFile(); result = frequencyFile2;
		}};
		
		IpfControlTotals avfCSVFile2 = new IpfControlTotals(rule2);
		
		/*
		control_totals2.csv
			...
			Household Size,2,Household Income,High,250
			...
			Household Size,3,Number Of Cars,3,150
			...
			Household Income,Low,Number Of Cars,0,150
			...
		 */
		matchingCriteria.put(householdSize, householdSizeTwo);
		matchingCriteria.put(householdIncome, householdIncomeHigh);
		List<AttributeValuesFrequency> result6 = avfCSVFile2.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(result6.size() == 1);
		assertTrue(result6.get(0).getFrequency() == 250);
		
		
		AbstractAttribute nbOfCars = generator.getAttributeByNameOnData("Number Of Cars");
		AttributeValue nbOfCarsThree = new UniqueValue(nbOfCars.getDataType(), "3");
		AttributeValue nbOfCarsZero =  new UniqueValue(nbOfCars.getDataType(), "0");
		
		matchingCriteria.clear();
		matchingCriteria.put(householdSize, householdSizeThree);
		matchingCriteria.put(nbOfCars, nbOfCarsThree);
		List<AttributeValuesFrequency> result7 = avfCSVFile2.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(result7.size() == 1);
		assertTrue(result7.get(0).getFrequency() == 150);
		
		AttributeValue householdIncomeLow = new UniqueValue(householdIncome.getDataType(), "Low");
		matchingCriteria.clear();
		matchingCriteria.put(householdIncome, householdIncomeLow);
		matchingCriteria.put(nbOfCars, nbOfCarsZero);
		List<AttributeValuesFrequency> result8 = avfCSVFile2.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(result8.size() == 1);
		assertTrue(result8.get(0).getFrequency() == 150);
		
		
		matchingCriteria.clear();
		matchingCriteria.put(householdSize, householdSizeTwo);
		List<AttributeValuesFrequency> result9 = avfCSVFile2.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(result9.size() == 6);
	}
	
	@Test public void testParseAttributeValuesFrequencyForUniqueValue(@Mocked final IpfGenerationRule rule1, @Mocked final IpfGenerationRule rule2) throws GenstarException {
		
		final ISyntheticPopulationGenerator generator = new SampleBasedGenerator("generator");
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/ControlTotals/testParseAttributeValuesFrequencyForUniqueValue/attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);

		AbstractAttribute householdSizeAttr = generator.getAttributeByNameOnData("Household Size");
		AbstractAttribute householdIncomeAttr = generator.getAttributeByNameOnData("Household Income");

		final List<AbstractAttribute> rule1ControlledAttributes = new ArrayList<AbstractAttribute>();
		rule1ControlledAttributes.add(householdSizeAttr);
		rule1ControlledAttributes.add(householdIncomeAttr);

		final GenstarCsvFile frequencyFile1 = new GenstarCsvFile("test_data/ummisco/genstar/ipf/ControlTotals/testParseAttributeValuesFrequencyForUniqueValue/control_totals1.csv", false);

		new Expectations() {{
			onInstance(rule1).getControlledAttributes(); result = rule1ControlledAttributes;
			onInstance(rule1).getControlTotalsFile(); result = frequencyFile1;
		}};
		
		IpfControlTotals avfCSVFile1 = new IpfControlTotals(rule1);
		List<AttributeValuesFrequency> avFrequencies1 = getField(avfCSVFile1, "avFrequencies");
		assertTrue(avFrequencies1.size() == 5);
		
		AbstractAttribute nbOfCarsAttr = generator.getAttributeByNameOnData("Number Of Cars");
		
		final List<AbstractAttribute> rule2ControlledAttributes = new ArrayList<AbstractAttribute>();
		rule2ControlledAttributes.add(householdSizeAttr);
		rule2ControlledAttributes.add(householdIncomeAttr);
		rule2ControlledAttributes.add(nbOfCarsAttr);

		final GenstarCsvFile frequencyFile2 = new GenstarCsvFile("test_data/ummisco/genstar/ipf/ControlTotals/testParseAttributeValuesFrequencyForUniqueValue/control_totals2.csv", false);

		new Expectations() {{
			onInstance(rule2).getControlledAttributes(); result = rule2ControlledAttributes;
			onInstance(rule2).getControlTotalsFile(); result = frequencyFile2;
		}};
		
		IpfControlTotals avfCSVFile2 = new IpfControlTotals(rule2);
		List<AttributeValuesFrequency> avFrequencies2 = getField(avfCSVFile2, "avFrequencies");
		assertTrue(avFrequencies2.size() == 26);
	}
	
	@Test public void testGetMatchingAttributeValuesFrequenciesForRangeValue(@Mocked final IpfGenerationRule rule) throws GenstarException {
		final ISyntheticPopulationGenerator generator = new SampleBasedGenerator("generator");
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/ControlTotals/testGetMatchingAttributeValuesFrequenciesForRangeValue/attributes1.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
		
		final GenstarCsvFile frequencyFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/ControlTotals/testGetMatchingAttributeValuesFrequenciesForRangeValue/control_totals3.csv", false);
		
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age");
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");
		
		final List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
		controlledAttributes.add(ageAttr);
		controlledAttributes.add(genderAttr);
				
		new Expectations() {{
			rule.getControlledAttributes(); result = controlledAttributes;
			rule.getControlTotalsFile(); result = frequencyFile;
		}};
		
		
		/*
		 control_totals3.csv
			Age,0:15,100
			Age,16:20,150
			Age,21:25,250
			Age,26:30,50
			Age,31:50,50
			Age,51:60,150
			Age,61:75,50
			Age,76:80,100
			Age,81:100,100
			Gender,true,400
			Gender,false,600
		 */
		IpfControlTotals avfCSVFile = new IpfControlTotals(rule);
		
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		
		AttributeValue age_0_15 = new RangeValue(DataType.INTEGER, "0", "15");
		AttributeValue male = new UniqueValue(DataType.BOOL, "true");
		AttributeValue female = new UniqueValue(DataType.BOOL, "false");
		
		matchingCriteria.put(ageAttr, age_0_15);
		
		List<AttributeValuesFrequency> matching1 = avfCSVFile.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(matching1.size() == 1);
		
		matchingCriteria.clear();
		matchingCriteria.put(genderAttr, male);
		matching1 = avfCSVFile.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(matching1.size() == 1);
		assertTrue(matching1.get(0).getFrequency() == 400);
		
		matchingCriteria.clear();
		matchingCriteria.put(genderAttr, female);
		matching1 = avfCSVFile.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(matching1.size() == 1);
		assertTrue(matching1.get(0).getFrequency() == 600);
		
	}
	
	@Test(expected = GenstarException.class) public void testAttributeValuesFrequenciesFileContainsInvalidAttribute(@Mocked final IpfGenerationRule rule) throws GenstarException {
		final ISyntheticPopulationGenerator generator = new SampleBasedGenerator("generator");
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/ControlTotals/testAttributeValuesFrequenciesFileContainsInvalidAttribute/attributes1.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
		
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age");
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");

		final List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
		controlledAttributes.add(ageAttr);
		controlledAttributes.add(genderAttr);

		final GenstarCsvFile frequencyFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/ControlTotals/testAttributeValuesFrequenciesFileContainsInvalidAttribute/control_totals4.csv", false);
		
		new Expectations() {{
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
		new IpfControlTotals(rule);	
	}
	
	@Test(expected = GenstarException.class) public void testAttributeValuesFrequenciesFileContainsInvalidControlledAttribute(@Mocked final IpfGenerationRule rule) throws GenstarException {
		final ISyntheticPopulationGenerator generator = new SampleBasedGenerator("generator");
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/ControlTotals/testAttributeValuesFrequenciesFileContainsInvalidControlledAttribute/attributes1.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
		
		AbstractAttribute ageAttr = generator.getAttributeByNameOnData("Age");
		AbstractAttribute genderAttr = generator.getAttributeByNameOnData("Gender");

		final List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
		controlledAttributes.add(ageAttr);
		controlledAttributes.add(genderAttr);

		final GenstarCsvFile frequencyFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/ControlTotals/testAttributeValuesFrequenciesFileContainsInvalidControlledAttribute/control_totals5.csv", false);
		
		new Expectations() {{
			rule.getControlledAttributes(); result = controlledAttributes;
			rule.getControlTotalsFile(); result = frequencyFile;
		}};
		
		
		/*
		 control_totals5.csv
		 	Gender,true,Work,agriculteur,10
		 */
		new IpfControlTotals(rule);		
	}

}
