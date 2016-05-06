package ummisco.genstar.ipu;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.sample_data.CompoundSampleData;
import ummisco.genstar.metamodel.sample_data.ISampleData;
import ummisco.genstar.metamodel.sample_data.SampleData;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.GenstarUtils;
import ummisco.genstar.util.IpuUtils;

@RunWith(JMockit.class)
public class IpuTest {
	
	/* @Test */ @BeforeClass
	public static void generateSamplePopulations() throws GenstarException {
		// generate the compound population (i.e., household-people) if necessary
		String base_folder_path = "test_data/ummisco/genstar/ipu/Ipu/generated_populations/";
		
		// if the population exists already then returns
		File groupPopulationFile = new File(base_folder_path + "group_population.csv");
		if (groupPopulationFile.exists()) {
			System.out.println("population already existed");
			return; 
		}
		
		String groupPopulationName = "household";
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(base_folder_path + "group_attributes.csv", true);
		
		String componentPopulationName = "people";
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(base_folder_path + "component_attributes.csv", true);

		String groupIdAttributeNameOnGroupEntity = "Household ID";
		String groupIdAttributeNameOnComponentEntity = "Household ID";
		String groupSizeAttributeName = "Household Size";
		
		int minGroupEntitiesOfEachAttributeValuesSet = 24;
		int maxGroupEntitiesOfEachAttributeValuesSet = 36;
		
		IPopulation generatedCompoundPopulation = GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, 
				componentPopulationName, componentAttributesFile, groupIdAttributeNameOnGroupEntity, 
				groupIdAttributeNameOnComponentEntity, groupSizeAttributeName, minGroupEntitiesOfEachAttributeValuesSet, 
				maxGroupEntitiesOfEachAttributeValuesSet);
		
		
		// write the generated population to CSV files
		final String groupPopulationOutputFile = base_folder_path + "group_population.csv";
		final String componentPopulationOutputFile = base_folder_path + "component_population.csv";
		Map<String, String> generatedCompoundPopulationFilePaths = new HashMap<String, String>();
		generatedCompoundPopulationFilePaths.put(groupPopulationName, groupPopulationOutputFile);
		generatedCompoundPopulationFilePaths.put(componentPopulationName, componentPopulationOutputFile);

		GenstarUtils.writePopulationToCsvFile(generatedCompoundPopulation, generatedCompoundPopulationFilePaths);
		
		
		// extract control totals of the generated compound population
		GenstarCsvFile groupControlledAttributesListFile = new GenstarCsvFile(base_folder_path + "group_controlled_attributes.csv", false);
		GenstarCsvFile componentControlledAttributesListFile = new GenstarCsvFile(base_folder_path + "component_controlled_attributes.csv", false);
		List<AttributeValuesFrequency> groupControlTotalsToBeBuilt = new ArrayList<AttributeValuesFrequency>();
		List<AttributeValuesFrequency> componentControlTotalsToBeBuilt = new ArrayList<AttributeValuesFrequency>();
		IpuUtils.buildIpuControlTotalsOfCompoundPopulation(generatedCompoundPopulation, componentPopulationName, groupControlledAttributesListFile, 
				componentControlledAttributesListFile, groupControlTotalsToBeBuilt, componentControlTotalsToBeBuilt);
		
		
		// write control totals to CSV files
		String groupControlTotalsOutputFilePath = base_folder_path + "group_control_totals.csv";
		IpuUtils.writeIpuControlTotalsToCsvFile(groupControlTotalsToBeBuilt, groupControlTotalsOutputFilePath);
		
		String componentControlTotalsOutputFilePath = base_folder_path + "component_control_totals.csv";
		IpuUtils.writeIpuControlTotalsToCsvFile(componentControlTotalsToBeBuilt, componentControlTotalsOutputFilePath);
		
		// TODO extract sample population from generatedCompoundPopulation
		String based_extracted_populations_folder = base_folder_path + "extracted_populations/";
		
		
		AbstractAttribute householdSizeAttr = generatedCompoundPopulation.getAttributeByNameOnData("Household Size");
		AbstractAttribute householdIncomeAttr = generatedCompoundPopulation.getAttributeByNameOnData("Household Income");
		AbstractAttribute householdTypeAttr = generatedCompoundPopulation.getAttributeByNameOnData("Household Type");
		
		Set<AbstractAttribute> ipuControlledAttributes = new HashSet<AbstractAttribute>();
		ipuControlledAttributes.add(householdSizeAttr);
		ipuControlledAttributes.add(householdIncomeAttr);
		ipuControlledAttributes.add(householdTypeAttr);
		 
		
		// extract 1%
		float percentage = 1f;
		IPopulation extractedPopulation1 = IpuUtils.extractIpuSamplePopulation(generatedCompoundPopulation, generatedCompoundPopulation.getName(), percentage, ipuControlledAttributes);

		final String groupPopulationOutputFile1 = based_extracted_populations_folder + "1_percent/extracted_group_population_1_percent.csv";
		final String componentPopulationOutputFile1 = based_extracted_populations_folder + "1_percent/extracted_component_population_1_percent.csv";
		Map<String, String> extractedPopulationFilePaths1 = new HashMap<String, String>();
		extractedPopulationFilePaths1.put(groupPopulationName, groupPopulationOutputFile1);
		extractedPopulationFilePaths1.put(componentPopulationName, componentPopulationOutputFile1);
		
		GenstarUtils.writePopulationToCsvFile(extractedPopulation1, extractedPopulationFilePaths1);
		
		
		// extract 5%
		percentage = 5;
		IPopulation extractedPopulation2 = IpuUtils.extractIpuSamplePopulation(generatedCompoundPopulation, generatedCompoundPopulation.getName(), percentage, ipuControlledAttributes);
		
		final String groupPopulationOutputFile2 = based_extracted_populations_folder + "5_percent/extracted_group_population_5_percent.csv";
		final String componentPopulationOutputFile2 = based_extracted_populations_folder + "5_percent/extracted_component_population_5_percent.csv";
		Map<String, String> extractedPopulationFilePaths2 = new HashMap<String, String>();
		extractedPopulationFilePaths2.put(groupPopulationName, groupPopulationOutputFile2);
		extractedPopulationFilePaths2.put(componentPopulationName, componentPopulationOutputFile2);
		
		GenstarUtils.writePopulationToCsvFile(extractedPopulation2, extractedPopulationFilePaths2);
		
		
		// extract 10%
		percentage = 10;
		IPopulation extractedPopulation3 = IpuUtils.extractIpuSamplePopulation(generatedCompoundPopulation, generatedCompoundPopulation.getName(), percentage, ipuControlledAttributes);
		
		final String groupPopulationOutputFile3 = based_extracted_populations_folder + "10_percent/extracted_group_population_10_percent.csv";
		final String componentPopulationOutputFile3 = based_extracted_populations_folder + "10_percent/extracted_component_population_10_percent.csv";
		Map<String, String> extractedPopulationFilePaths3 = new HashMap<String, String>();
		extractedPopulationFilePaths3.put(groupPopulationName, groupPopulationOutputFile3);
		extractedPopulationFilePaths3.put(componentPopulationName, componentPopulationOutputFile3);
		
		GenstarUtils.writePopulationToCsvFile(extractedPopulation3, extractedPopulationFilePaths3);
		 
		
		// extract 20%
		percentage = 20;
		IPopulation extractedPopulation4 = IpuUtils.extractIpuSamplePopulation(generatedCompoundPopulation, generatedCompoundPopulation.getName(), percentage, ipuControlledAttributes);
		
		final String groupPopulationOutputFile4 = based_extracted_populations_folder + "20_percent/extracted_group_population_20_percent.csv";
		final String componentPopulationOutputFile4 = based_extracted_populations_folder + "20_percent/extracted_component_population_20_percent.csv";
		Map<String, String> extractedPopulationFilePaths4 = new HashMap<String, String>();
		extractedPopulationFilePaths4.put(groupPopulationName, groupPopulationOutputFile4);
		extractedPopulationFilePaths4.put(componentPopulationName, componentPopulationOutputFile4);
		
		GenstarUtils.writePopulationToCsvFile(extractedPopulation4, extractedPopulationFilePaths4);
		
		
		
		// extract 30%
		percentage = 30;
		IPopulation extractedPopulation5 = IpuUtils.extractIpuSamplePopulation(generatedCompoundPopulation, generatedCompoundPopulation.getName(), percentage, ipuControlledAttributes);
		
		final String groupPopulationOutputFile5 = based_extracted_populations_folder + "30_percent/extracted_group_population_30_percent.csv";
		final String componentPopulationOutputFile5 = based_extracted_populations_folder + "30_percent/extracted_component_population_30_percent.csv";
		Map<String, String> extractedPopulationFilePaths5 = new HashMap<String, String>();
		extractedPopulationFilePaths5.put(groupPopulationName, groupPopulationOutputFile5);
		extractedPopulationFilePaths5.put(componentPopulationName, componentPopulationOutputFile5);
		
		GenstarUtils.writePopulationToCsvFile(extractedPopulation5, extractedPopulationFilePaths5);
		 
	}

	@Test public void testInitializeIpuSuccessfully(@Mocked final IpuGenerationRule generationRule) throws GenstarException {
		
		// 1. initialize ??? 
		final SampleBasedGenerator groupGenerator = new SampleBasedGenerator("group generator");
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/Ipu/success/group_attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesFile);
		
		// Household Size, Household Income
		final List<AbstractAttribute> groupControlledAttributes = new ArrayList<AbstractAttribute>();
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Household Size"));
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Household Income"));

		final GenstarCsvFile groupControlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/Ipu/success/group_ipu_control_totals.csv", false);
		
		final SampleBasedGenerator componentGenerator = new SampleBasedGenerator("componnent generator");
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/Ipu/success/component_attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(componentGenerator, componentAttributesFile);
		
		// Gender, Work
		final List<AbstractAttribute> componentControlledAttributes = new ArrayList<AbstractAttribute>();
		componentControlledAttributes.add(componentGenerator.getAttributeByNameOnData("Gender"));
		componentControlledAttributes.add(componentGenerator.getAttributeByNameOnData("Work"));

		final GenstarCsvFile componentControlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/Ipu/success/component_ipu_control_totals.csv", false);
		
		
		// 3. initialize sample data
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		GenstarCsvFile groupSampleDataFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/Ipu/success/group_sample.csv", true);
		ISampleData groupSample = new SampleData("household", groupGenerator.getAttributes(), groupSampleDataFile);
		
		AbstractAttribute groupIdAttributeOnComponentEntity = componentGenerator.getAttributeByNameOnData("Household ID");
		GenstarCsvFile componentSampleFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/Ipu/success/component_sample.csv", true);
		groupIdAttributeOnComponentEntity.setIdentity(true);
		ISampleData componentSample = new SampleData("people", componentGenerator.getAttributes(), componentSampleFile);
		
		final CompoundSampleData sampleData = new CompoundSampleData(groupSample, componentSample, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity);
		

		// data for ipuControlTotals
		new Expectations() {{
//			generationRule.getGenerator(); result = groupGenerator;
			generationRule.getGroupControlledAttributes(); result = groupControlledAttributes;
			generationRule.getGroupControlTotalsFile(); result = groupControlTotalsFile;
			
//			generationRule.getComponentGenerator(); result = componentGenerator;
			generationRule.getComponentControlledAttributes(); result = componentControlledAttributes;
			generationRule.getComponentControlTotalsFile(); result = componentControlTotalsFile;
		}};
		
		
		// 2. initialize IpuControlTotals
		final IpuControlTotals ipuControlTotals = new IpuControlTotals(generationRule);

		
		// data for Ipu
		new Expectations() {{
			generationRule.getControlTotals(); result = ipuControlTotals;
			generationRule.getSampleData(); result = sampleData;
		}};
	
		
		// verification
		Ipu ipu = new Ipu(generationRule);
		int[][] ipuMatrix = Deencapsulation.getField(ipu, "ipuMatrix");
		assertTrue(ipuMatrix.length == sampleData.getSampleEntityPopulation().getNbOfEntities()); // rows: 8
		assertTrue(ipuMatrix.length == 8); // rows: 8
		assertTrue(ipuMatrix[0].length == 8); // columns: 8
		assertTrue(ipuMatrix[0].length == ipuControlTotals.getGroupTypes() + ipuControlTotals.getComponentTypes()); // columns: 8
		
		List<Integer> constraints = Deencapsulation.getField(ipu, "constraints");
		assertTrue(constraints.size() == 8);
		
		List<List<Double>> weightedSums = Deencapsulation.getField(ipu, "weightedSums");
		assertTrue(weightedSums.size() == 1);
		assertTrue(weightedSums.get(0).size() == 8);
		
		List<List<Double>> weights = Deencapsulation.getField(ipu, "weights");
		assertTrue(weights.size() == 1);
		assertTrue(weights.get(0).size() == ipuMatrix.length);
		
		List<List<Double>> deltas = Deencapsulation.getField(ipu, "deltas");
		assertTrue(deltas.size() == 1);
		assertTrue(deltas.get(0).size() == 8);
		
		List<Double> sumDeltas = Deencapsulation.getField(ipu, "sumDeltas");
		assertTrue(sumDeltas.size() == 1);
		
		List<List<Integer>> tobeComputedWeightsRowsByColumns = Deencapsulation.getField(ipu, "tobeComputedWeightsRowsByColumns");
		List<List<Integer>> notTobeComputedWeightsRowsByColumns = Deencapsulation.getField(ipu, "notTobeComputedWeightsRowsByColumns");
		
		int totalNumberOfCells = 0;
		int column=0;
		for (List<Integer> rows : tobeComputedWeightsRowsByColumns) {
			totalNumberOfCells += rows.size();
			for (Integer row : rows) { assertTrue(ipuMatrix[row][column] > 0); }
			column++;
		}
		column=0;
		for (List<Integer> rows : notTobeComputedWeightsRowsByColumns) {
			totalNumberOfCells += rows.size();
			for (Integer row : rows) { assertTrue(ipuMatrix[row][column] == 0); }
			column++;
		}
		assertTrue(totalNumberOfCells == ipuMatrix.length * ipuMatrix[0].length);
		
		// ipu.printDebug();
	}
	
	
	@Test public void testFit(@Mocked final IpuGenerationRule generationRule) throws GenstarException {
		// initialize generators and controlled attributes 
		final SampleBasedGenerator groupGenerator = new SampleBasedGenerator("group generator");
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/Ipu/fit/group_attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesFile);
		
		// group controlled attributes: Household Size, Household Income
		final List<AbstractAttribute> groupControlledAttributes = new ArrayList<AbstractAttribute>();
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Household Size"));
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Household Income"));

		final GenstarCsvFile groupControlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/Ipu/fit/group_ipu_control_totals.csv", false);
		
		final SampleBasedGenerator componentGenerator = new SampleBasedGenerator("componnent generator");
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/Ipu/fit/component_attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(componentGenerator, componentAttributesFile);
		
		// component controlled attributes: Gender, Work
		final List<AbstractAttribute> componentControlledAttributes = new ArrayList<AbstractAttribute>();
		componentControlledAttributes.add(componentGenerator.getAttributeByNameOnData("Gender"));
		componentControlledAttributes.add(componentGenerator.getAttributeByNameOnData("Work"));

		final GenstarCsvFile componentControlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/Ipu/fit/component_ipu_control_totals.csv", false);
		
		
		// initialize sample data
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		GenstarCsvFile groupSampleDataFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/Ipu/fit/group_sample.csv", true);
		ISampleData groupSample = new SampleData("household", groupGenerator.getAttributes(), groupSampleDataFile);
		
		AbstractAttribute groupIdAttributeOnComponentEntity = componentGenerator.getAttributeByNameOnData("Household ID");
		GenstarCsvFile componentSampleFile = new GenstarCsvFile("test_data/ummisco/genstar/ipu/Ipu/fit/component_sample.csv", true);
		groupIdAttributeOnComponentEntity.setIdentity(true);
		ISampleData componentSample = new SampleData("people", componentGenerator.getAttributes(), componentSampleFile);
		
		final CompoundSampleData sampleData = new CompoundSampleData(groupSample, componentSample, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity);
		
		final int MAX_ITERATIONS = 100;
		
		// data for ipuControlTotals
		new Expectations() {{
//			generationRule.getGenerator(); result = groupGenerator;
			generationRule.getGroupControlledAttributes(); result = groupControlledAttributes;
			generationRule.getGroupControlTotalsFile(); result = groupControlTotalsFile;
			
//			generationRule.getComponentGenerator(); result = componentGenerator;
			generationRule.getComponentControlledAttributes(); result = componentControlledAttributes;
			generationRule.getComponentControlTotalsFile(); result = componentControlTotalsFile;
			
			generationRule.getMaxIterations(); result = MAX_ITERATIONS;
		}};
		
		
		// initialize IpuControlTotals
		final IpuControlTotals ipuControlTotals = new IpuControlTotals(generationRule);

		
		// data for Ipu
		new Expectations() {{
			generationRule.getControlTotals(); result = ipuControlTotals;
			generationRule.getSampleData(); result = sampleData;
		}};
	
		
		Ipu ipu = new Ipu(generationRule);
		ipu.fit();
		
		int numberOfColumns = Deencapsulation.getField(ipu, "numberOfColumns");
		
		List<List<Double>> weightedSums = Deencapsulation.getField(ipu, "weightedSums");
		assertTrue(weightedSums.size() == (numberOfColumns * MAX_ITERATIONS) + 1);
		
		List<List<Double>> weights = Deencapsulation.getField(ipu, "weights");
		assertTrue(weights.size() == (numberOfColumns * MAX_ITERATIONS) + 1);
		
		List<List<Double>> deltas = Deencapsulation.getField(ipu, "deltas");
		assertTrue(deltas.size() == MAX_ITERATIONS + 1);
		
		List<Double> sumDeltas = Deencapsulation.getField(ipu, "sumDeltas");
		assertTrue(sumDeltas.size() == MAX_ITERATIONS + 1);
		
		//ipu.printGoodnessOfFit();
	}
	
	@Test public void testFit1(@Mocked final IpuGenerationRule generationRule) throws GenstarException {
		
		// 0. initialize IpuGenerationRule
		String base_path = "test_data/ummisco/genstar/ipu/Ipu/success1/";
		
		// 1. initialize generators and controlled attributes 
		final SampleBasedGenerator groupGenerator = new SampleBasedGenerator("group generator");
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile(base_path + "group_attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesFile);
		
		// group controlled attributes: Household Type
		final List<AbstractAttribute> groupControlledAttributes = new ArrayList<AbstractAttribute>();
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Household Type"));

		final GenstarCsvFile groupControlTotalsFile = new GenstarCsvFile(base_path + "group_ipu_control_totals.csv", false);
		
		final SampleBasedGenerator componentGenerator = new SampleBasedGenerator("componnent generator");
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile(base_path + "component_attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(componentGenerator, componentAttributesFile);
		
		// component controlled attributes: Person Type
		final List<AbstractAttribute> componentControlledAttributes = new ArrayList<AbstractAttribute>();
		componentControlledAttributes.add(componentGenerator.getAttributeByNameOnData("Person Type"));

		final GenstarCsvFile componentControlTotalsFile = new GenstarCsvFile(base_path + "component_ipu_control_totals.csv", false);

		
		// 2. initialize sample data
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		GenstarCsvFile groupSampleDataFile = new GenstarCsvFile(base_path + "group_sample.csv", true);
		ISampleData groupSample = new SampleData("household", groupGenerator.getAttributes(), groupSampleDataFile);
		
		AbstractAttribute groupIdAttributeOnComponentEntity = componentGenerator.getAttributeByNameOnData("Household ID");
		GenstarCsvFile componentSampleFile = new GenstarCsvFile(base_path + "component_sample.csv", true);
		groupIdAttributeOnComponentEntity.setIdentity(true);
		ISampleData componentSample = new SampleData("people", componentGenerator.getAttributes(), componentSampleFile);
		
		final CompoundSampleData sampleData = new CompoundSampleData(groupSample, componentSample, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity);

		
		// data for ipuControlTotals
		new Expectations() {{
//			generationRule.getGenerator(); result = groupGenerator;
			generationRule.getGroupControlledAttributes(); result = groupControlledAttributes;
			generationRule.getGroupControlTotalsFile(); result = groupControlTotalsFile;
			
//			generationRule.getComponentGenerator(); result = componentGenerator;
			generationRule.getComponentControlledAttributes(); result = componentControlledAttributes;
			generationRule.getComponentControlTotalsFile(); result = componentControlTotalsFile;
			
			generationRule.getMaxIterations(); result = 1; // 3 iterations ?
		}};
		
		
		// initialize IpuControlTotals
		final IpuControlTotals ipuControlTotals = new IpuControlTotals(generationRule);

		
		// data for Ipu
		new Expectations() {{
			generationRule.getControlTotals(); result = ipuControlTotals;
			generationRule.getSampleData(); result = sampleData;
		}};
	
		
		Ipu ipu = new Ipu(generationRule);
		
		
		// verifications 1
		int numberOfColumns1 = Deencapsulation.getField(ipu, "numberOfColumns");
		assertTrue(numberOfColumns1 == 5);
		
		int numberOfRows1 = Deencapsulation.getField(ipu, "numberOfRows");
		assertTrue(numberOfRows1 == 8);
		
		List<List<Double>> weightedSums1 = Deencapsulation.getField(ipu, "weightedSums");
		assertTrue(weightedSums1.size() == 1);
		List<Double> weightedSums1Content0 = weightedSums1.get(0);
		assertTrue(weightedSums1Content0.get(0) == 3);
		assertTrue(weightedSums1Content0.get(1) == 5);
		assertTrue(weightedSums1Content0.get(2) == 9);
		assertTrue(weightedSums1Content0.get(3) == 7);
		assertTrue(weightedSums1Content0.get(4) == 7);
		assertTrue(weightedSums1Content0.size() == 5);
		
		List<List<Double>> weights1 = Deencapsulation.getField(ipu, "weights");
		assertTrue(weights1.size() == 1);
		List<Double> weights1Content0 = weights1.get(0);
		for (Double d : weights1Content0) { assertTrue(d == 1); }
		
		List<Integer> constraints1 = Deencapsulation.getField(ipu, "constraints");
		assertTrue(constraints1.size() == 5);
		assertTrue(constraints1.get(0) == 35);
		assertTrue(constraints1.get(1) == 65);
		assertTrue(constraints1.get(2) == 91);
		assertTrue(constraints1.get(3) == 65);
		assertTrue(constraints1.get(4) == 104);
		

		DecimalFormat df1 = new DecimalFormat("#.####");      
		
		List<List<Double>> deltas1 = Deencapsulation.getField(ipu, "deltas");
		assertTrue(deltas1.size() == 1);
		List<Double> deltas1Content0 = deltas1.get(0);
		assertTrue(deltas1Content0.size() == 5);
		assertTrue(Double.valueOf(df1.format(deltas1Content0.get(0))) == 0.9143);
		assertTrue(Double.valueOf(df1.format(deltas1Content0.get(1))) == 0.9231);
		assertTrue(Double.valueOf(df1.format(deltas1Content0.get(2))) == 0.9011);
		assertTrue(Double.valueOf(df1.format(deltas1Content0.get(3))) == 0.8923);
		assertTrue(Double.valueOf(df1.format(deltas1Content0.get(4))) == 0.9327);
		
		
		List<Double> sumDeltas1 = Deencapsulation.getField(ipu, "sumDeltas");
		assertTrue(sumDeltas1.size() == 1);
		assertTrue(sumDeltas1.get(0) == deltas1Content0.get(0) + deltas1Content0.get(1) + deltas1Content0.get(2) + deltas1Content0.get(3) + deltas1Content0.get(4));
		
		Map<Entity, Integer> selectionProbabilities1 = Deencapsulation.getField(ipu, "selectionProbabilities");
		assertTrue(selectionProbabilities1 == null);
		
		
		ipu.fit();
		
		
		// verifications 2
		int numberOfColumns2 = Deencapsulation.getField(ipu, "numberOfColumns");
		assertTrue(numberOfColumns2 == 5);
		
		int numberOfRows2 = Deencapsulation.getField(ipu, "numberOfRows");
		assertTrue(numberOfRows2 == 8);
		
		List<List<Double>> weightedSums2 = Deencapsulation.getField(ipu, "weightedSums");
		assertTrue(weightedSums2.size() == numberOfColumns1 + 1);
		
		DecimalFormat df2 = new DecimalFormat("#.##");      
		
		// weightedSums21: 35.00, 5.00, 51.67, 28.33, 28.33
		List<Double> weightedSums21 = weightedSums2.get(1);
		assertTrue(Double.valueOf(df2.format(weightedSums21.get(0))) == 35.00);
		assertTrue(Double.valueOf(df2.format(weightedSums21.get(1))) == 5.00);
		assertTrue(Double.valueOf(df2.format(weightedSums21.get(2))) == 51.67);
		assertTrue(Double.valueOf(df2.format(weightedSums21.get(3))) == 28.33);
		assertTrue(Double.valueOf(df2.format(weightedSums21.get(4))) == 28.33);
		
		// weightedSums22: 35.00, 65.00, 111.67, 88.33, 88.33
		List<Double> weightedSums22 = weightedSums2.get(2);
		assertTrue(Double.valueOf(df2.format(weightedSums22.get(0))) == 35.00);
		assertTrue(Double.valueOf(df2.format(weightedSums22.get(1))) == 65.00);
		assertTrue(Double.valueOf(df2.format(weightedSums22.get(2))) == 111.67);
		assertTrue(Double.valueOf(df2.format(weightedSums22.get(3))) == 88.33);
		assertTrue(Double.valueOf(df2.format(weightedSums22.get(4))) == 88.33);
		
		// weightedSums23: 28.52, 55.38, 91.00, 76.80, 74.39
		List<Double> weightedSums23 = weightedSums2.get(3);
		assertTrue(Double.valueOf(df2.format(weightedSums23.get(0))) == 28.52);
		assertTrue(Double.valueOf(df2.format(weightedSums23.get(1))) == 55.38);
		assertTrue(Double.valueOf(df2.format(weightedSums23.get(2))) == 91.00);
		assertTrue(Double.valueOf(df2.format(weightedSums23.get(3))) == 76.80);
		assertTrue(Double.valueOf(df2.format(weightedSums23.get(4))) == 74.39);
		
		// weightedSums24: 25.60, 48.50, 80.11, 65.00, 67.68
		List<Double> weightedSums24 = weightedSums2.get(4);
		assertTrue(Double.valueOf(df2.format(weightedSums24.get(0))) == 25.60);
		assertTrue(Double.valueOf(df2.format(weightedSums24.get(1))) == 48.50);
		assertTrue(Double.valueOf(df2.format(weightedSums24.get(2))) == 80.11);
		assertTrue(Double.valueOf(df2.format(weightedSums24.get(3))) == 65.00);
		assertTrue(Double.valueOf(df2.format(weightedSums24.get(4))) == 67.68);
		
		// weightedSums25: 35.02, 64.90, 104.84, 85.94, 104.00
		List<Double> weightedSums25 = weightedSums2.get(5);
		assertTrue(Double.valueOf(df2.format(weightedSums25.get(0))) == 35.02);
		assertTrue(Double.valueOf(df2.format(weightedSums25.get(1))) == 64.90);
		assertTrue(Double.valueOf(df2.format(weightedSums25.get(2))) == 104.84);
		assertTrue(Double.valueOf(df2.format(weightedSums25.get(3))) == 85.94);
		assertTrue(Double.valueOf(df2.format(weightedSums25.get(4))) == 104.00);
		

		List<List<Double>> weights2 = Deencapsulation.getField(ipu, "weights");
		assertTrue(weights2.size() == numberOfColumns2 + 1);
		
		// weights21: 11.67, 11.67, 11.67, 1.00, 1.00, 1.00, 1.00, 1.00
		List<Double> weights21 = weights2.get(1);
		assertTrue(Double.valueOf(df2.format(weights21.get(0))) == 11.67);
		assertTrue(Double.valueOf(df2.format(weights21.get(1))) == 11.67);
		assertTrue(Double.valueOf(df2.format(weights21.get(2))) == 11.67);
		assertTrue(Double.valueOf(df2.format(weights21.get(3))) == 1.00);
		assertTrue(Double.valueOf(df2.format(weights21.get(4))) == 1.00);
		assertTrue(Double.valueOf(df2.format(weights21.get(5))) == 1.00);
		assertTrue(Double.valueOf(df2.format(weights21.get(6))) == 1.00);
		assertTrue(Double.valueOf(df2.format(weights21.get(7))) == 1.00);
		
		// weights22: 11.67, 11.67, 11.67, 13.00, 13.00, 13.00, 13.00, 13.00
		List<Double> weights22 = weights2.get(2);
		assertTrue(Double.valueOf(df2.format(weights22.get(0))) == 11.67);
		assertTrue(Double.valueOf(df2.format(weights22.get(1))) == 11.67);
		assertTrue(Double.valueOf(df2.format(weights22.get(2))) == 11.67);
		assertTrue(Double.valueOf(df2.format(weights22.get(3))) == 13.00);
		assertTrue(Double.valueOf(df2.format(weights22.get(4))) == 13.00);
		assertTrue(Double.valueOf(df2.format(weights22.get(5))) == 13.00);
		assertTrue(Double.valueOf(df2.format(weights22.get(6))) == 13.00);
		assertTrue(Double.valueOf(df2.format(weights22.get(7))) == 13.00);
		
		// weights23: 9.51, 9.51, 9.51, 10.59, 13.00, 10.59, 10.59, 10.59
		List<Double> weights23 = weights2.get(3);
		assertTrue(Double.valueOf(df2.format(weights23.get(0))) == 9.51);
		assertTrue(Double.valueOf(df2.format(weights23.get(1))) == 9.51);
		assertTrue(Double.valueOf(df2.format(weights23.get(2))) == 9.51);
		assertTrue(Double.valueOf(df2.format(weights23.get(3))) == 10.59);
		assertTrue(Double.valueOf(df2.format(weights23.get(4))) == 13.00);
		assertTrue(Double.valueOf(df2.format(weights23.get(5))) == 10.59);
		assertTrue(Double.valueOf(df2.format(weights23.get(6))) == 10.59);
		assertTrue(Double.valueOf(df2.format(weights23.get(7))) == 10.59);
		
		// weights24: 8.05, 9.51, 8.05, 10.59, 11.00, 8.97, 8.97, 8.97 
		List<Double> weights24 = weights2.get(4);
		assertTrue(Double.valueOf(df2.format(weights24.get(0))) == 8.05);
		assertTrue(Double.valueOf(df2.format(weights24.get(1))) == 9.51);
		assertTrue(Double.valueOf(df2.format(weights24.get(2))) == 8.05);
		assertTrue(Double.valueOf(df2.format(weights24.get(3))) == 10.59);
		assertTrue(Double.valueOf(df2.format(weights24.get(4))) == 11.00);
		assertTrue(Double.valueOf(df2.format(weights24.get(5))) == 8.97);
		assertTrue(Double.valueOf(df2.format(weights24.get(6))) == 8.97);
		assertTrue(Double.valueOf(df2.format(weights24.get(7))) == 8.97);
		
		// weights25: 12.37, 14.61, 8.05, 16.28, 16.91, 8.97, 13.78, 8.97
		List<Double> weights25 = weights2.get(5);
		assertTrue(Double.valueOf(df2.format(weights25.get(0))) == 12.37);
		assertTrue(Double.valueOf(df2.format(weights25.get(1))) == 14.61);
		assertTrue(Double.valueOf(df2.format(weights25.get(2))) == 8.05);
		assertTrue(Double.valueOf(df2.format(weights25.get(3))) == 16.28);
		assertTrue(Double.valueOf(df2.format(weights25.get(4))) == 16.91);
		assertTrue(Double.valueOf(df2.format(weights25.get(5))) == 8.97);
		assertTrue(Double.valueOf(df2.format(weights25.get(6))) == 13.78);
		assertTrue(Double.valueOf(df2.format(weights25.get(7))) == 8.97);

		
		List<List<Double>> deltas2 = Deencapsulation.getField(ipu, "deltas");
		assertTrue(deltas2.size() == 2);
		List<Double> deltas2Content1 = deltas1.get(1);
		assertTrue(deltas2Content1.size() == 5);
		assertTrue(Double.valueOf(df1.format(deltas2Content1.get(0))) == 0.0006);
		assertTrue(Double.valueOf(df1.format(deltas2Content1.get(1))) == 0.0015);
		assertTrue(Double.valueOf(df1.format(deltas2Content1.get(2))) == 0.1521);
		assertTrue(Double.valueOf(df1.format(deltas2Content1.get(3))) == 0.3222);
		assertTrue(Double.valueOf(df1.format(deltas2Content1.get(4))) == 0.0000);
		
		
		List<Double> sumDeltas2 = Deencapsulation.getField(ipu, "sumDeltas");
		assertTrue(sumDeltas2.size() == 2);
		assertTrue(sumDeltas2.get(1) == deltas2Content1.get(0) + deltas2Content1.get(1) + deltas2Content1.get(2) + deltas2Content1.get(3) + deltas2Content1.get(4));
		
		
		Map<Entity, Integer> selectionProbabilities2 = Deencapsulation.getField(ipu, "selectionProbabilities");
		assertTrue(selectionProbabilities2.size() == 8);
		
		List<Entity> groupEntities = sampleData.getSampleEntityPopulation().getEntities();
		
		// weights5: 12.37, 14.61, 8.05, 16.28, 16.91, 8.97, 13.78, 8.97
		assertTrue(selectionProbabilities2.get(groupEntities.get(0)) == Math.round(12.37));
		assertTrue(selectionProbabilities2.get(groupEntities.get(1)) == Math.round(14.61));
		assertTrue(selectionProbabilities2.get(groupEntities.get(2)) == Math.round(8.05));
		assertTrue(selectionProbabilities2.get(groupEntities.get(3)) == Math.round(16.28));
		assertTrue(selectionProbabilities2.get(groupEntities.get(4)) == Math.round(16.91));
		assertTrue(selectionProbabilities2.get(groupEntities.get(5)) == Math.round(8.97));
		assertTrue(selectionProbabilities2.get(groupEntities.get(6)) == Math.round(13.78));
		assertTrue(selectionProbabilities2.get(groupEntities.get(7)) == Math.round(8.97));
	}
}
