package ummisco.genstar.ipu;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.GroupComponentSampleData;
import ummisco.genstar.ipf.ISampleData;
import ummisco.genstar.ipf.SampleData;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.IPopulation;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarUtils;
import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class IpuTest {
	@Test /* @Before */ 
	public void generateSamplePopulation() throws GenstarException {
		// generate the compound population (i.e., household-people) if necessary
		String base_folder_path = "test_data/ummisco/genstar/ipu/Ipu/generated_populations/";
		
		// if the population exists already then returns
		File groupPopulationFile = new File(base_folder_path + "group_population.csv");
		if (groupPopulationFile.exists()) { return; }
		
		String groupPopulationName = "household";
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile(base_folder_path + "group_attributes.csv", true);
		
		String componentPopulationName = "people";
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile(base_folder_path + "component_attributes.csv", true);

		String groupIdAttributeNameOnGroupEntity = "Household ID";
		String groupIdAttributeNameOnComponentEntity = "Household ID";
		String groupSizeAttributeName = "Household Size";
		int nbOfGroupEntities = 200;
		
		IPopulation generatedCompoundPopulation = GenstarUtils.generateRandomCompoundPopulation(groupPopulationName, groupAttributesFile, componentPopulationName, componentAttributesFile, 
				groupIdAttributeNameOnGroupEntity, groupIdAttributeNameOnComponentEntity, groupSizeAttributeName, nbOfGroupEntities);
		
		
		// write the generated population to CSV files
		final String groupPopulationOutputFile = base_folder_path + "group_population.csv";
		final String componentPopulationOutputFile = base_folder_path + "component_population.csv";
		Map<String, String> generatedCompoundPopulationFilePaths = new HashMap<String, String>();
		generatedCompoundPopulationFilePaths.put(groupPopulationName, groupPopulationOutputFile);
		generatedCompoundPopulationFilePaths.put(componentPopulationName, componentPopulationOutputFile);

		GenstarUtils.writePopulationToCSVFile(generatedCompoundPopulation, generatedCompoundPopulationFilePaths);
		
		// TODO extract control totals, i.e., buildIpuControlTotalsOfCompoundPopulation
		// write control totals tp CSVFile GenstarUtils.writeContentToCsvFile(fileContent, csvFilePath); 
	}

	@Test public void testInitializeIpuSuccessfully(@Mocked final IpuGenerationRule generationRule) throws GenstarException {
		
		// 1. initialize ??? 
		final ISingleRuleGenerator groupGenerator = new SingleRuleGenerator("group generator");
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/Ipu/success/group_attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(groupGenerator, groupAttributesFile);
		
		// Household Size, Household Income
		final List<AbstractAttribute> groupControlledAttributes = new ArrayList<AbstractAttribute>();
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Household Size"));
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Household Income"));

		final GenstarCSVFile groupControlTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/Ipu/success/group_ipu_control_totals.csv", false);
		
		final ISingleRuleGenerator componentGenerator = new SingleRuleGenerator("componnent generator");
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/Ipu/success/component_attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(componentGenerator, componentAttributesFile);
		
		// Gender, Work
		final List<AbstractAttribute> componentControlledAttributes = new ArrayList<AbstractAttribute>();
		componentControlledAttributes.add(componentGenerator.getAttributeByNameOnData("Gender"));
		componentControlledAttributes.add(componentGenerator.getAttributeByNameOnData("Work"));

		final GenstarCSVFile componentControlTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/Ipu/success/component_ipu_control_totals.csv", false);
		
		
		// 3. initialize sample data
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		GenstarCSVFile groupSampleDataFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/Ipu/success/group_sample.csv", true);
		ISampleData groupSample = new SampleData("household", groupGenerator.getAttributes(), groupSampleDataFile);
		
		AbstractAttribute groupIdAttributeOnComponentEntity = componentGenerator.getAttributeByNameOnData("Household ID");
		GenstarCSVFile componentSampleFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/Ipu/success/component_sample.csv", true);
		groupIdAttributeOnComponentEntity.setIdentity(true);
		ISampleData componentSample = new SampleData("people", componentGenerator.getAttributes(), componentSampleFile);
		
		final GroupComponentSampleData sampleData = new GroupComponentSampleData(groupSample, componentSample, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity);
		

		// data for ipuControlTotals
		new Expectations() {{
			generationRule.getGenerator(); result = groupGenerator;
			generationRule.getGroupControlledAttributes(); result = groupControlledAttributes;
			generationRule.getGroupControlTotalsFile(); result = groupControlTotalsFile;
			
			generationRule.getComponentGenerator(); result = componentGenerator;
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
		// 1. initialize ??? 
		final ISingleRuleGenerator groupGenerator = new SingleRuleGenerator("group generator");
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/Ipu/fit/group_attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(groupGenerator, groupAttributesFile);
		
		// Household Size, Household Income
		final List<AbstractAttribute> groupControlledAttributes = new ArrayList<AbstractAttribute>();
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Household Size"));
		groupControlledAttributes.add(groupGenerator.getAttributeByNameOnData("Household Income"));

		final GenstarCSVFile groupControlTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/Ipu/fit/group_ipu_control_totals.csv", false);
		
		final ISingleRuleGenerator componentGenerator = new SingleRuleGenerator("componnent generator");
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/Ipu/fit/component_attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(componentGenerator, componentAttributesFile);
		
		// Gender, Work
		final List<AbstractAttribute> componentControlledAttributes = new ArrayList<AbstractAttribute>();
		componentControlledAttributes.add(componentGenerator.getAttributeByNameOnData("Gender"));
		componentControlledAttributes.add(componentGenerator.getAttributeByNameOnData("Work"));

		final GenstarCSVFile componentControlTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/Ipu/fit/component_ipu_control_totals.csv", false);
		
		
		// 3. initialize sample data
		AbstractAttribute groupIdAttributeOnGroupEntity = groupGenerator.getAttributeByNameOnData("Household ID");
		groupIdAttributeOnGroupEntity.setIdentity(true);
		GenstarCSVFile groupSampleDataFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/Ipu/fit/group_sample.csv", true);
		ISampleData groupSample = new SampleData("household", groupGenerator.getAttributes(), groupSampleDataFile);
		
		AbstractAttribute groupIdAttributeOnComponentEntity = componentGenerator.getAttributeByNameOnData("Household ID");
		GenstarCSVFile componentSampleFile = new GenstarCSVFile("test_data/ummisco/genstar/ipu/Ipu/fit/component_sample.csv", true);
		groupIdAttributeOnComponentEntity.setIdentity(true);
		ISampleData componentSample = new SampleData("people", componentGenerator.getAttributes(), componentSampleFile);
		
		final GroupComponentSampleData sampleData = new GroupComponentSampleData(groupSample, componentSample, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity);
		
		final int MAX_ITERATIONS = 100;
		
		// data for ipuControlTotals
		new Expectations() {{
			generationRule.getGenerator(); result = groupGenerator;
			generationRule.getGroupControlledAttributes(); result = groupControlledAttributes;
			generationRule.getGroupControlTotalsFile(); result = groupControlTotalsFile;
			
			generationRule.getComponentGenerator(); result = componentGenerator;
			generationRule.getComponentControlledAttributes(); result = componentControlledAttributes;
			generationRule.getComponentControlTotalsFile(); result = componentControlTotalsFile;
			
			generationRule.getMaxIterations(); result = MAX_ITERATIONS;
		}};
		
		
		// 2. initialize IpuControlTotals
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
}
