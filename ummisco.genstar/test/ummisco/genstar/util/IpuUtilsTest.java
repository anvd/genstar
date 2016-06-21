package ummisco.genstar.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipu.IpuGenerationRule;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttributeWithRangeInput;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.metamodel.sample_data.CompoundSampleData;
import ummisco.genstar.metamodel.sample_data.ISampleData;

@RunWith(JMockit.class)
public class IpuUtilsTest {
	
	
	@Test public void testParseIpuControlTotalsFile() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("generator");
		
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpuUtils/parseIpuControlTotalsFile/success/group_attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
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
		
		GenstarCsvFile ipuControlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpuUtils/parseIpuControlTotalsFile/success/group_ipu_control_totals.csv", false);
		
		List<AttributeValuesFrequency> avfs = IpuUtils.parseAttributeValuesFrequenciesFromIpuControlTotalsFile(controlledAttributes, ipuControlTotalsFile);
		
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
	
	
	@Test public void testParseIpuControlTotalsFileWithUniqueValuesAttributeWithRangeInput() throws GenstarException {
		fail("not yet implement");
	}
	
	
	@Test public void testBuildIpuControlTotalsOfCompoundPopulation() throws GenstarException {

		// 0. load a compound population from files
		String base_folder_path1 = "test_data/ummisco/genstar/util/IpuUtils/buildIpuControlTotalsOfCompoundPopulation/generated_populations/";
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(base_folder_path1 + "group_attributes.csv", true);
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(base_folder_path1 + "component_attributes.csv", true);
		GenstarCsvFile groupPopulationFile = new GenstarCsvFile(base_folder_path1 + "group_population.csv", true);
		GenstarCsvFile componentPopulationFile = new GenstarCsvFile(base_folder_path1 + "component_population.csv", true);
		IPopulation compoundPopulation = GenstarUtils.loadCompoundPopulation(PopulationType.SYNTHETIC_POPULATION, "household", groupAttributesFile, 
				groupPopulationFile, "people", componentAttributesFile, componentPopulationFile, "Household ID", "Household ID", null, null);
		
		// 1. build IPU control totals
		String base_folder_path2 = "test_data/ummisco/genstar/util/IpuUtils/buildIpuControlTotalsOfCompoundPopulation/";
		GenstarCsvFile groupControlledAttributesListFile = new GenstarCsvFile(base_folder_path2 + "group_controlled_attributes.csv", false);
		GenstarCsvFile componentControlledAttributesListFile = new GenstarCsvFile(base_folder_path2 + "component_controlled_attributes.csv", false);
		List<AttributeValuesFrequency> groupControlTotalsToBeBuilt = new ArrayList<AttributeValuesFrequency>();
		List<AttributeValuesFrequency> componentControlTotalsToBeBuilt = new ArrayList<AttributeValuesFrequency>();
		IpuUtils.buildIpuControlTotalsOfCompoundPopulation(compoundPopulation, "people", 
				groupControlledAttributesListFile, componentControlledAttributesListFile, groupControlTotalsToBeBuilt, componentControlTotalsToBeBuilt);
		
		// 2. do the necessary verifications/assertions of the built group and component control totals
		
		//		2.1. group control totals
		//			size of List<AttributeValuesFrequency>
		//			frequency of each AttributeValuesFrequency
		/*
		group_population.csv
			householdID,householdSize,householdIncome
			0,1,High
			1,1,Low
			2,2,High
			3,2,Low
			4,1,High
			5,1,Low
			6,2,High
			7,2,Low
		 */
		assertTrue(groupControlTotalsToBeBuilt.size() == 4);
		
		AbstractAttribute householdSizeAttr = compoundPopulation.getAttributeByNameOnData("Household Size");
		AbstractAttribute householdIncomeAttr = compoundPopulation.getAttributeByNameOnData("Household Income");
		
		assertTrue(householdSizeAttr != null);
		assertTrue(householdIncomeAttr != null);
		
		AttributeValue size1 = new UniqueValue(DataType.INTEGER, "1");
		AttributeValue size2 = new UniqueValue(DataType.INTEGER, "2");
		AttributeValue incomeLow = new UniqueValue(DataType.STRING, "Low");
		AttributeValue incomeHigh = new UniqueValue(DataType.STRING, "High");
		
		// householdSizeAttr : 1, householdIncomeAttr : Low, Frequency : 2 
		Map<AbstractAttribute, AttributeValue> valueSet1 = new HashMap<AbstractAttribute, AttributeValue>();
		valueSet1.put(householdSizeAttr, size1);
		valueSet1.put(householdIncomeAttr, incomeLow);
		boolean valueSet1Matched = false;
		
		// householdSizeAttr : 1, householdIncomeAttr : High, Frequency : 2
		Map<AbstractAttribute, AttributeValue> valueSet2 = new HashMap<AbstractAttribute, AttributeValue>();
		valueSet2.put(householdSizeAttr, size1);
		valueSet2.put(householdIncomeAttr, incomeHigh);
		boolean valueSet2Matched = false;

		// householdSizeAttr : 2, householdIncomeAttr : Low, Frequency : 2
		Map<AbstractAttribute, AttributeValue> valueSet3 = new HashMap<AbstractAttribute, AttributeValue>();
		valueSet3.put(householdSizeAttr, size2);
		valueSet3.put(householdIncomeAttr, incomeLow);
		boolean valueSet3Matched = false;
		
		// householdSizeAttr : 2, householdIncomeAttr : High, Frequency : 2
		Map<AbstractAttribute, AttributeValue> valueSet4 = new HashMap<AbstractAttribute, AttributeValue>();
		valueSet4.put(householdSizeAttr, size2);
		valueSet4.put(householdIncomeAttr, incomeHigh);
		boolean valueSet4Matched = false;
		
		
		for (AttributeValuesFrequency avf : groupControlTotalsToBeBuilt) {
			if (avf.matchAttributeValuesOnData(valueSet1)) {
				assertTrue(avf.getFrequency() == 2);
				valueSet1Matched = true;
			} else if (avf.matchAttributeValuesOnData(valueSet2)) {
				assertTrue(avf.getFrequency() == 2);
				valueSet2Matched = true;
			} else if (avf.matchAttributeValuesOnData(valueSet3)) {
				assertTrue(avf.getFrequency() == 2);
				valueSet3Matched = true;
			} else if (avf.matchAttributeValuesOnData(valueSet4)) {
				assertTrue(avf.getFrequency() == 2);
				valueSet4Matched = true;
			}
		}
		
		assertTrue(valueSet1Matched == true);
		assertTrue(valueSet2Matched == true);
		assertTrue(valueSet3Matched == true);
		assertTrue(valueSet4Matched == true);
		
		
		
		//		2.2. component control totals
		//			size of List<AttributeValuesFrequency>
		//			frequency of each AttributeValuesFrequency
		/*
		component_population.csv
			gender,work,householdID
			true,work1,0
			true,work2,1
			false,work1,2
			true,work2,2
			true,work1,3
			false,work2,3
			false,work2,4
			false,work1,5
			true,work1,6
			false,work2,6
			false,work1,7
			true,work2,7
		 */
		assertTrue(componentControlTotalsToBeBuilt.size() == 4);
		
		AttributeValuesFrequency componentAvf = componentControlTotalsToBeBuilt.get(0);
		AbstractAttribute peopleGenderAttr = null;
		AbstractAttribute peopleWorkAttr = null;
		for (AbstractAttribute attr : componentAvf.getAttributes()) {
			if (attr.getNameOnData().equals("Gender")) { peopleGenderAttr = attr; }
			else if (attr.getNameOnData().equals("Work")) { peopleWorkAttr = attr; }
		}
		
		assertTrue(peopleGenderAttr != null);
		assertTrue(peopleWorkAttr != null);
		
		AttributeValue genderTrue = new UniqueValue(DataType.BOOL, "true");
		AttributeValue genderFalse = new UniqueValue(DataType.BOOL, "false");
		AttributeValue work1 = new UniqueValue(DataType.STRING, "work1");
		AttributeValue work2 = new UniqueValue(DataType.STRING, "work2");
		
		// Gender : true, Work : work1, Frequency : 3
		Map<AbstractAttribute, AttributeValue> peopleValueSet1 = new HashMap<AbstractAttribute, AttributeValue>();
		peopleValueSet1.put(peopleGenderAttr, genderTrue);
		peopleValueSet1.put(peopleWorkAttr, work1);
		boolean peopleValueSet1Matched = false;

		// Gender : true, Work : work2, Frequency : 3
		Map<AbstractAttribute, AttributeValue> peopleValueSet2 = new HashMap<AbstractAttribute, AttributeValue>();
		peopleValueSet2.put(peopleGenderAttr, genderTrue);
		peopleValueSet2.put(peopleWorkAttr, work2);
		boolean peopleValueSet2Matched = false;

		// Gender : false, Work : work1, Frequency : 3
		Map<AbstractAttribute, AttributeValue> peopleValueSet3 = new HashMap<AbstractAttribute, AttributeValue>();
		peopleValueSet3.put(peopleGenderAttr, genderFalse);
		peopleValueSet3.put(peopleWorkAttr, work1);
		boolean peopleValueSet3Matched = false;

		// Gender : false, Work : work2, Frequency : 3
		Map<AbstractAttribute, AttributeValue> peopleValueSet4 = new HashMap<AbstractAttribute, AttributeValue>();
		peopleValueSet4.put(peopleGenderAttr, genderFalse);
		peopleValueSet4.put(peopleWorkAttr, work2);
		boolean peopleValueSet4Matched = false;

		for (AttributeValuesFrequency avf : componentControlTotalsToBeBuilt) {
			if (avf.matchAttributeValuesOnData(peopleValueSet1)) {
				assertTrue(avf.getFrequency() == 3);
				peopleValueSet1Matched = true;
			} else if (avf.matchAttributeValuesOnData(peopleValueSet2)) {
				assertTrue(avf.getFrequency() == 3);
				peopleValueSet2Matched = true;
			} else if (avf.matchAttributeValuesOnData(peopleValueSet3)) {
				assertTrue(avf.getFrequency() == 3);
				peopleValueSet3Matched = true;
			} else if (avf.matchAttributeValuesOnData(peopleValueSet4)) {
				assertTrue(avf.getFrequency() == 3);
				peopleValueSet4Matched = true;
			}
		}
		
		assertTrue(peopleValueSet1Matched == true);
		assertTrue(peopleValueSet2Matched == true);
		assertTrue(peopleValueSet3Matched == true);
		assertTrue(peopleValueSet4Matched == true);
	}
	
	
	@Test public void testWriteIpuControlTotalsToCsvFile() throws GenstarException {
		
		String csvOutputFilePath = "test_data/ummisco/genstar/util/IpuUtils/writeIpuControlTotalsToCsvFile/ipu_control_totals.csv";
		File outputFile = new File(csvOutputFilePath);
		if (outputFile.exists()) { outputFile.delete(); }

		
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/util/IpuUtils/writeIpuControlTotalsToCsvFile/group_attributes.csv", true);
		ISyntheticPopulationGenerator generator = new SampleBasedGenerator("generator");
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		List<AttributeValuesFrequency> ipuControlTotals = new ArrayList<AttributeValuesFrequency>();
		
		
		AbstractAttribute householdSizeAttr = generator.getAttributeByNameOnData("Household Size");
		AbstractAttribute householdIncomeAttr = generator.getAttributeByNameOnData("Household Income");
		
		assertTrue(householdSizeAttr != null);
		assertTrue(householdIncomeAttr != null);
		
		/*
		AttributeValue size1 = new UniqueValue(DataType.INTEGER, "1");
		AttributeValue size2 = new UniqueValue(DataType.INTEGER, "2");
		AttributeValue incomeLow = new UniqueValue(DataType.STRING, "Low");
		AttributeValue incomeHigh = new UniqueValue(DataType.STRING, "High");
		*/
		AttributeValue size1 = householdSizeAttr.getMatchingAttributeValueOnData(new UniqueValue(DataType.INTEGER, "1"));
		AttributeValue size2 = householdSizeAttr.getMatchingAttributeValueOnData(new UniqueValue(DataType.INTEGER, "2"));
		AttributeValue incomeLow = householdIncomeAttr.getMatchingAttributeValueOnData(new UniqueValue(DataType.STRING, "Low"));
		AttributeValue incomeHigh = householdIncomeAttr.getMatchingAttributeValueOnData(new UniqueValue(DataType.STRING, "High"));
		
		assertTrue(size1 != null);
		assertTrue(size2 != null);
		assertTrue(incomeLow != null);
		assertTrue(incomeHigh != null);
		
		// householdSizeAttr : 1, householdIncomeAttr : Low, Frequency : 2 
		Map<AbstractAttribute, AttributeValue> valueSet1 = new HashMap<AbstractAttribute, AttributeValue>();
		valueSet1.put(householdSizeAttr, size1);
		valueSet1.put(householdIncomeAttr, incomeLow);
		ipuControlTotals.add(new AttributeValuesFrequency(valueSet1, 5));
		
		// householdSizeAttr : 1, householdIncomeAttr : High, Frequency : 2
		Map<AbstractAttribute, AttributeValue> valueSet2 = new HashMap<AbstractAttribute, AttributeValue>();
		valueSet2.put(householdSizeAttr, size1);
		valueSet2.put(householdIncomeAttr, incomeHigh);
		ipuControlTotals.add(new AttributeValuesFrequency(valueSet2, 10));

		// householdSizeAttr : 2, householdIncomeAttr : Low, Frequency : 2
		Map<AbstractAttribute, AttributeValue> valueSet3 = new HashMap<AbstractAttribute, AttributeValue>();
		valueSet3.put(householdSizeAttr, size2);
		valueSet3.put(householdIncomeAttr, incomeLow);
		ipuControlTotals.add(new AttributeValuesFrequency(valueSet3, 15));
		
		// householdSizeAttr : 2, householdIncomeAttr : High, Frequency : 2
		Map<AbstractAttribute, AttributeValue> valueSet4 = new HashMap<AbstractAttribute, AttributeValue>();
		valueSet4.put(householdSizeAttr, size2);
		valueSet4.put(householdIncomeAttr, incomeHigh);
		ipuControlTotals.add(new AttributeValuesFrequency(valueSet4, 20));
		
		
		IpuUtils.writeIpuControlTotalsToCsvFile(ipuControlTotals, csvOutputFilePath);
		
		// verifications
		GenstarCsvFile csvOutputFile = new GenstarCsvFile(csvOutputFilePath, false);
		assertTrue(csvOutputFile.getRows() == 4);
		
		for (List<String> row : csvOutputFile.getContent()) {
			assertTrue(row.size() == 5);
		}
	}

	
	@Test public void testBuildIpuEntityCategories() throws GenstarException {
		// TODO Map<AttributeValuesFrequency, List<Entity>> buildIpuEntityCategories(final IPopulation population, final Set<AbstractAttribute> ipuControlledAttributes)
		
		String base_path = "test_data/ummisco/genstar/util/IpuUtils/buildIpuEntityCategories/";
		String groupPopulationName = "household";
		String groupAttributesFileName = "group_attributes.csv";
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(base_path + groupAttributesFileName, true);
		String componentPopulationName = "people";
		String componentAttributesFileName = "component_attributes.csv";
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(base_path + componentAttributesFileName, true);
		String groupIdAttributeNameOnDataOfGroupEntity = "Household ID";
		String groupIdAttributeNameOnDataOfComponentEntity = "Household ID";
		
		String groupPopulationFileName = "group_population.csv";
		String componentPopulationFileName = "component_population.csv"; 
		
		// 0. load the population from files
		GenstarCsvFile groupPopulationFile = new GenstarCsvFile(base_path + groupPopulationFileName, true);
		GenstarCsvFile componentPopulationFile = new GenstarCsvFile(base_path + componentPopulationFileName, true);
		IPopulation loadedCompoundPopulation = GenstarUtils.loadCompoundPopulation(PopulationType.SYNTHETIC_POPULATION, groupPopulationName, groupAttributesFile, 
				groupPopulationFile, componentPopulationName, componentAttributesFile, 
				componentPopulationFile, groupIdAttributeNameOnDataOfGroupEntity, groupIdAttributeNameOnDataOfComponentEntity, null, null);
		
		
		// 1. build entity categories
		AbstractAttribute householdSizeAttr = loadedCompoundPopulation.getAttributeByNameOnData("Household Size");
		AbstractAttribute householdIncomeAttr = loadedCompoundPopulation.getAttributeByNameOnData("Household Income");
		AbstractAttribute householdTypeAttr = loadedCompoundPopulation.getAttributeByNameOnData("Household Type");
		
		Set<AbstractAttribute> ipuControlledAttributes = new HashSet<AbstractAttribute>();
		ipuControlledAttributes.add(householdSizeAttr);
		ipuControlledAttributes.add(householdIncomeAttr);
		ipuControlledAttributes.add(householdTypeAttr);

		Map<AttributeValuesFrequency, List<Entity>> householdCategories = IpuUtils.buildIpuEntityCategories(loadedCompoundPopulation, ipuControlledAttributes);
		Set<AttributeValuesFrequency> categoryAvfs = householdCategories.keySet();
		
		
		// 2. do the verification
		assertTrue(householdCategories.size() == 8);
		
		// further verifications
		/*
			householdID,householdSize,householdIncome,householdType
			0,1,High,type1
			1,1,High,type1
			2,1,High,type1
			3,1,High,type2
			4,1,High,type2
			5,1,Low,type1
			6,1,Low,type1
			7,1,Low,type2
			8,1,Low,type2
			9,1,Low,type2
			10,2,High,type1
			11,2,High,type1
			12,2,High,type2
			13,2,High,type2
			14,2,High,type2
			15,2,Low,type1
			16,2,Low,type1
			17,2,Low,type2
			18,2,Low,type2
			19,2,Low,type2 
		 */
		
		AttributeValue highIncome = new UniqueValue(DataType.STRING, "High");
		AttributeValue lowIncome = new UniqueValue(DataType.STRING, "Low");
		
		AttributeValue type1 = new UniqueValue(DataType.STRING, "type1");
		AttributeValue type2 = new UniqueValue(DataType.STRING, "type2");
		
		AttributeValue size1 = new UniqueValue(DataType.INTEGER, "1");
		AttributeValue size2 = new UniqueValue(DataType.INTEGER, "2");
		
		
		// 1,High,type1 == 3
		Map<AbstractAttribute, AttributeValue> attributeValues1 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues1.put(householdSizeAttr, size1);
		attributeValues1.put(householdIncomeAttr, highIncome);
		attributeValues1.put(householdTypeAttr, type1);
		AttributeValuesFrequency avf1 = findMatchingAVF(categoryAvfs, attributeValues1);
		assertTrue(avf1.getFrequency() == 3);
		assertTrue(householdCategories.get(avf1).size() == 3);
		
		
		// 1,High,type2 == 2
		Map<AbstractAttribute, AttributeValue> attributeValues2 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues2.put(householdSizeAttr, size1);
		attributeValues2.put(householdIncomeAttr, highIncome);
		attributeValues2.put(householdTypeAttr, type2);
		AttributeValuesFrequency avf2 = findMatchingAVF(categoryAvfs, attributeValues2);
		assertTrue(avf2.getFrequency() == 2);
		assertTrue(householdCategories.get(avf2).size() == 2);
		
		
		// 1,Low,type1 = 2
		Map<AbstractAttribute, AttributeValue> attributeValues3 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues3.put(householdSizeAttr, size1);
		attributeValues3.put(householdIncomeAttr, lowIncome);
		attributeValues3.put(householdTypeAttr, type1);
		AttributeValuesFrequency avf3 = findMatchingAVF(categoryAvfs, attributeValues3);
		assertTrue(avf3.getFrequency() == 2);
		assertTrue(householdCategories.get(avf3).size() == 2);
		
		
		// 1,Low,type2 = 3
		Map<AbstractAttribute, AttributeValue> attributeValues4 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues4.put(householdSizeAttr, size1);
		attributeValues4.put(householdIncomeAttr, lowIncome);
		attributeValues4.put(householdTypeAttr, type2);
		AttributeValuesFrequency avf4 = findMatchingAVF(categoryAvfs, attributeValues4);
		assertTrue(avf4.getFrequency() == 3);
		assertTrue(householdCategories.get(avf4).size() == 3);
		
		
		// 2,High,type1 = 2
		Map<AbstractAttribute, AttributeValue> attributeValues5 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues5.put(householdSizeAttr, size2);
		attributeValues5.put(householdIncomeAttr, highIncome);
		attributeValues5.put(householdTypeAttr, type1);
		AttributeValuesFrequency avf5 = findMatchingAVF(categoryAvfs, attributeValues5);
		assertTrue(avf5.getFrequency() == 2);
		assertTrue(householdCategories.get(avf5).size() == 2);
		
		
		// 2,High,type2 = 3
		Map<AbstractAttribute, AttributeValue> attributeValues6 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues6.put(householdSizeAttr, size2);
		attributeValues6.put(householdIncomeAttr, highIncome);
		attributeValues6.put(householdTypeAttr, type2);
		AttributeValuesFrequency avf6 = findMatchingAVF(categoryAvfs, attributeValues6);
		assertTrue(avf6.getFrequency() == 3);
		assertTrue(householdCategories.get(avf6).size() == 3);
		
		
		// 2,Low,type1 = 2
		Map<AbstractAttribute, AttributeValue> attributeValues7 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues7.put(householdSizeAttr, size2);
		attributeValues7.put(householdIncomeAttr, lowIncome);
		attributeValues7.put(householdTypeAttr, type1);
		AttributeValuesFrequency avf7 = findMatchingAVF(categoryAvfs, attributeValues7);
		assertTrue(avf7.getFrequency() == 2);
		assertTrue(householdCategories.get(avf7).size() == 2);
		
		
		// 2,Low,type2 = 3
		Map<AbstractAttribute, AttributeValue> attributeValues8 = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValues8.put(householdSizeAttr, size2);
		attributeValues8.put(householdIncomeAttr, lowIncome);
		attributeValues8.put(householdTypeAttr, type2);
		AttributeValuesFrequency avf8 = findMatchingAVF(categoryAvfs, attributeValues8);
		assertTrue(avf8.getFrequency() == 3);
		assertTrue(householdCategories.get(avf8).size() == 3);
		
	}
	
	private AttributeValuesFrequency findMatchingAVF(final Set<AttributeValuesFrequency> candidates, Map<AbstractAttribute, AttributeValue> attributeValuesOnData) {
		
		for (AttributeValuesFrequency avf : candidates) {
			if (avf.matchAttributeValuesOnData(attributeValuesOnData)) { return avf; }
		}
		
		return null;
	}
	

	@Test public void testExtractIpuPopulation() throws GenstarException {
		// extractIpuSamplePopulation(final IPopulation originalPopulation, final String samplePopulationName, final float percentage, final Set<AbstractAttribute> ipuControlledAttributes)
		
		String base_path = "test_data/ummisco/genstar/util/IpuUtils/extractIpuPopulation/";
		
		String groupPopulationName = "household";
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(base_path + "group_attributes.csv", true);
		
		String componentPopulationName = "people";
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(base_path + "component_attributes.csv", true);

		String groupIdAttributeNameOnGroupEntity = "Household ID";
		String groupIdAttributeNameOnComponentEntity = "Household ID";
		String groupSizeAttributeNameOnData = "Household Size";
		
		String componentReferenceOnGroup = "inhabitants";
		String groupReferenceOnComponent = "household";

		// 0. generate an original population with 120 entities
		int minGroupEntitiesOfEachAttributeValuesSet1 = 15;
		int maxGroupEntitiesOfEachAttributeValuesSet1 = 15;
		IPopulation generatedCompoundPopulation = GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, 
				componentAttributesFile, groupIdAttributeNameOnGroupEntity, groupIdAttributeNameOnComponentEntity, groupSizeAttributeNameOnData, 
				minGroupEntitiesOfEachAttributeValuesSet1, maxGroupEntitiesOfEachAttributeValuesSet1, componentReferenceOnGroup, groupReferenceOnComponent);
		
		AbstractAttribute householdSizeAttr = generatedCompoundPopulation.getAttributeByNameOnData("Household Size");
		AbstractAttribute householdIncomeAttr = generatedCompoundPopulation.getAttributeByNameOnData("Household Income");
		AbstractAttribute householdTypeAttr = generatedCompoundPopulation.getAttributeByNameOnData("Household Type");
		
		Set<AbstractAttribute> ipuControlledAttributes = new HashSet<AbstractAttribute>();
		ipuControlledAttributes.add(householdSizeAttr);
		ipuControlledAttributes.add(householdIncomeAttr);
		ipuControlledAttributes.add(householdTypeAttr);
		
		UniqueValuesAttributeWithRangeInput groupIdAttributeOnGroupEntity = (UniqueValuesAttributeWithRangeInput) generatedCompoundPopulation.getAttributeByNameOnData("Household ID");
		UniqueValuesAttributeWithRangeInput groupIdAttributeOnComponentEntity = null;
		for (Entity groupEntity : generatedCompoundPopulation.getEntities()) {
			IPopulation componentPopulation = groupEntity.getComponentPopulation(componentPopulationName);
			if (componentPopulation != null) {
				groupIdAttributeOnComponentEntity = (UniqueValuesAttributeWithRangeInput) componentPopulation.getAttributeByNameOnData("Household ID");
				break;
			}
		}
		
		
		// extract 1% of the original population then do the verifications
		float percentage = 0.1f;
		IPopulation extractedPopulation1 = IpuUtils.extractIpuPopulation(generatedCompoundPopulation, percentage, ipuControlledAttributes, 
				groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity, componentPopulationName);
		assertTrue(extractedPopulation1.getNbOfEntities() == 8);
		int idValue = 0;
		for (Entity groupEntity : extractedPopulation1.getEntities()) { // ensure that entities are correctly recoded
			assertTrue(((UniqueValue)groupEntity.getEntityAttributeValue(groupIdAttributeOnGroupEntity).getAttributeValueOnEntity()).getIntValue() == idValue);
			idValue++;
		}
		// TODO further verifications
		
		// extract 10% of the original population then do the verifications
		percentage = 10;
		IPopulation extractedPopulation2 = IpuUtils.extractIpuPopulation(generatedCompoundPopulation, percentage, ipuControlledAttributes, 
				groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity, componentPopulationName);
		assertTrue(extractedPopulation2.getNbOfEntities() == 12);
		idValue = 0;
		for (Entity groupEntity : extractedPopulation2.getEntities()) { // ensure that entities are correctly recoded
			assertTrue(((UniqueValue)groupEntity.getEntityAttributeValue(groupIdAttributeOnGroupEntity).getAttributeValueOnEntity()).getIntValue() == idValue);
			idValue++;
		}
		
		// extract 30% of the original population then do the verifications
		percentage = 30;
		IPopulation extractedPopulation3 = IpuUtils.extractIpuPopulation(generatedCompoundPopulation, percentage, ipuControlledAttributes, 
				groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity, componentPopulationName);
		assertTrue(extractedPopulation3.getNbOfEntities() == 36);
		idValue = 0;
		for (Entity groupEntity : extractedPopulation3.getEntities()) { // ensure that entities are correctly recoded
			assertTrue(((UniqueValue)groupEntity.getEntityAttributeValue(groupIdAttributeOnGroupEntity).getAttributeValueOnEntity()).getIntValue() == idValue);
			idValue++;
		}
	}
	
	
	@Test public void testCreateIpuGenerationRule() throws GenstarException {
		
		String base_path = "test_data/ummisco/genstar/util/IpuUtils/testCreateIpuGenerationRule/";
		
		String groupPopulationName = "household";
		SampleBasedGenerator groupPopulationGenerator = new SampleBasedGenerator("group generator");
		groupPopulationGenerator.setPopulationName(groupPopulationName);
		
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(base_path + "group_attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(groupPopulationGenerator, groupAttributesFile);
		
		GenstarCsvFile groupSampleDataFile = new GenstarCsvFile(base_path + "group_sample.csv", true);
		String groupIdAttributeNameOnGroup = "Household ID";
		GenstarCsvFile groupControlledAttributesFile = new GenstarCsvFile(base_path + "group_controlled_attributes.csv", false);
		GenstarCsvFile groupControlTotalsFile = new GenstarCsvFile(base_path + "group_ipu_control_totals.csv", false);
		GenstarCsvFile groupSupplementaryAttributesFile = new GenstarCsvFile(base_path + "group_supplementary_attributes.csv", false);
		
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(base_path + "component_attributes.csv", true);
		String componentPopulationName = "people";
		GenstarCsvFile componentSampleDataFile = new GenstarCsvFile(base_path + "component_sample.csv", true);
		String groupIdAttributeNameOnComponent = "Household ID";
		GenstarCsvFile componentControlledAttributesFile = new GenstarCsvFile(base_path + "component_controlled_attributes.csv", false);
		GenstarCsvFile componentControlTotalsFile = new GenstarCsvFile(base_path + "component_ipu_control_totals.csv", false);
		GenstarCsvFile componentSupplementaryAttributesFile = new GenstarCsvFile(base_path + "component_supplementary_attributes.csv", false);
		
		int maxIterations = 3;
		
		assertTrue(groupPopulationGenerator.getGenerationRule() == null);
		
		IpuUtils.createIpuGenerationRule(groupPopulationGenerator, groupSampleDataFile, groupIdAttributeNameOnGroup, 
				groupControlledAttributesFile, groupControlTotalsFile, groupSupplementaryAttributesFile, null,
				componentAttributesFile, componentPopulationName, componentSampleDataFile, groupIdAttributeNameOnComponent, 
				componentControlledAttributesFile, componentControlTotalsFile, componentSupplementaryAttributesFile, null, 
				maxIterations);

		IpuGenerationRule generationRule = (IpuGenerationRule) groupPopulationGenerator.getGenerationRule();
		assertTrue(generationRule != null);
		
		ISampleData sampleData = generationRule.getSampleData();
		assertTrue(sampleData instanceof CompoundSampleData);
	}

	
	@Test public void testAttributeValueSetsWithZeroMatchingEntity() throws GenstarException {
		fail("not yet implemented");
	}
}
