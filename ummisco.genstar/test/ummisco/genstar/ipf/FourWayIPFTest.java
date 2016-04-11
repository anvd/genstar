package ummisco.genstar.ipf;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.Deencapsulation;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.IPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarUtils;
import ummisco.genstar.util.IpfUtils;

@RunWith(JMockit.class)
public class FourWayIPFTest {

	@Mocked SampleDataGenerationRule generationRule;
	ISyntheticPopulationGenerator generator;
	final List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
	AbstractAttribute rowAttribute, columnAttribute, layerAttribute, stackAttribute;
	List<AttributeValue> rowAttributeValues, columnAttributeValues, layerAttributeValues, stackAttributeValues;

	FourWayIPF ipf;
	
	@Before public void init() throws GenstarException {
		
		String _attributesFilePath = "test_data/ummisco/genstar/ipf/four_way/attributes.csv";
		String _sampleDataFilePath = "test_data/ummisco/genstar/ipf/four_way/household_sample.csv";
		String controlTotalsFilePath = "test_data/ummisco/genstar/ipf/four_way/control_totals.csv";

		GenstarCSVFile _attributesFile = new GenstarCSVFile(_attributesFilePath, true);
		GenstarCSVFile controlAttributesFile = _attributesFile; 
		
		String controlAttributesListFilePath = "test_data/ummisco/genstar/ipf/four_way/controlled_attributes_list.csv";
		
		// generate household control totals if necessary
		File controlTotalsFile = new File(controlTotalsFilePath);
		if (!controlTotalsFile.exists()) {
			int numberOfHouseholds = 1000;
			List<List<String>> householdControlTotals = IpfUtils.generateIpfControlTotals(controlAttributesFile, numberOfHouseholds);
			GenstarUtils.writeContentToCsvFile(householdControlTotals, controlTotalsFilePath);
		}
		
		// generate sample data if necessary
		File _sampleDataFile = new File(_sampleDataFilePath);
		if (!_sampleDataFile.exists()) {
			String populationName = "dummy population";
			int minEntitiesOfEachAttributeValuesSet = 1;
			int maxEntitiesOfEachAttributeValuesSet = 3;
			
			IPopulation generatedSamplePopulation = GenstarUtils.generateRandomSinglePopulation(populationName, _attributesFile, minEntitiesOfEachAttributeValuesSet, maxEntitiesOfEachAttributeValuesSet);
			
			Map<String, String> csvFilePaths = new HashMap<String, String>();
			csvFilePaths.put(populationName, _sampleDataFilePath);
			
			GenstarUtils.writePopulationToCSVFile(generatedSamplePopulation, csvFilePaths);
		}
		
		generator = new SingleRuleGenerator("generator");
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/four_way/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCSVFile controlAttributesListFile = new GenstarCSVFile(controlAttributesListFilePath, false);
		for (List<String> row : controlAttributesListFile.getContent()) { controlledAttributes.add(generator.getAttributeByNameOnData(row.get(0))); }	
		
		final GenstarCSVFile sampleDataFile = new GenstarCSVFile(_sampleDataFilePath, true);
		final GenstarCSVFile controlTotalsCSVFile = new GenstarCSVFile(controlTotalsFilePath, false);
		
		new NonStrictExpectations() {{
			generationRule.getGenerator(); result = generator;
			generationRule.getControlledAttributes(); result = controlledAttributes;
			
			generationRule.getAttributeByNameOnData(anyString);
			result = new Delegate() {
				AbstractAttribute delegateMethod(final String attributeName) {
					return generator.getAttributeByNameOnData(attributeName);
				}
			};
			
			generationRule.getSampleData();
			result = new Delegate() {
				SampleData getSampleDataDelegate() throws GenstarException {
					return new SampleData("people", generator.getAttributes(), sampleDataFile);
				}
			};
			
			generationRule.getControlTotalsFile();
			result = controlTotalsCSVFile;
			
			generationRule.getControlTotals();
			result = new Delegate() {
				IpfControlTotals getControlTotalsDelegate() throws GenstarException {
					return new IpfControlTotals(generationRule);
				}
			};
			
			generationRule.getMaxIterations();
			result = SampleDataGenerationRule.DEFAULT_MAX_ITERATIONS;
		}};
		
		ipf = new FourWayIPF(generationRule);
		
		/*
		 controlled_attributes.csv
			Household Size
			Household Income
			Number Of Cars
			Household Type
		 */
		rowAttribute = controlledAttributes.get(0);
		assertTrue(rowAttribute.getNameOnData().equals("Household Size"));
		rowAttributeValues = ipf.getAttributeValues(0);
		
		columnAttribute = controlledAttributes.get(1);
		assertTrue(columnAttribute.getNameOnData().equals("Household Income"));
		columnAttributeValues = ipf.getAttributeValues(1);
		
		layerAttribute = controlledAttributes.get(2);
		assertTrue(layerAttribute.getNameOnData().equals("Number Of Cars"));
		layerAttributeValues = ipf.getAttributeValues(2);

		stackAttribute = controlledAttributes.get(3);
		assertTrue(stackAttribute.getNameOnData().equals("Household Type"));
		stackAttributeValues = ipf.getAttributeValues(3);
	}
	

	@Test(expected = GenstarException.class) public void testInitializeFourWayIPFWithInvalidControlledAttributes(@Mocked final SampleDataGenerationRule fourWayGenerationRule) throws GenstarException {
		new Expectations() {{
			fourWayGenerationRule.getControlledAttributes(); result = new ArrayList<AbstractAttribute>();
		}};
		new FourWayIPF(fourWayGenerationRule);
	}
	
	// "data" verification
	@Test public void testDataIsInitializedCorrectly() throws GenstarException {
		
		double[][][][] data = ipf.getData();
				
		assertTrue(data.length == rowAttributeValues.size());
		assertTrue(data[0].length == columnAttributeValues.size());
		assertTrue(data[0][0].length == layerAttributeValues.size());
		assertTrue(data[0][0][0].length == stackAttributeValues.size());
		
		
		ISampleData sampleData = generationRule.getSampleData();
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
					
					for (int stack=0; stack<stackAttributeValues.size(); stack++) {
						matchingCriteria.put(stackAttribute, stackAttributeValues.get(stack));

						assertTrue(data[row][col][layer][stack] == sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria));
					}
				}
			}
		}
	}
	
	// "controls" verification
	@Test public void testControlsAreInitializedCorrectly() throws GenstarException {
		int[][][] rowControls = ipf.getControls(0);
		int[][][] columnControls = ipf.getControls(1);
		int[][][] layerControls = ipf.getControls(2);
		int[][][] stackControls = ipf.getControls(3);
		
		assertTrue(rowControls.length == columnAttributeValues.size());
		assertTrue(rowControls[0].length == layerAttributeValues.size());
		assertTrue(rowControls[0][0].length == stackAttributeValues.size());
		
		assertTrue(columnControls.length == rowAttributeValues.size());
		assertTrue(columnControls[0].length == layerAttributeValues.size());
		assertTrue(columnControls[0][0].length == stackAttributeValues.size());
		
		assertTrue(layerControls.length == rowAttributeValues.size());
		assertTrue(layerControls[0].length == columnAttributeValues.size());
		assertTrue(layerControls[0][0].length == stackAttributeValues.size());
		
		assertTrue(stackControls.length == rowAttributeValues.size());
		assertTrue(stackControls[0].length == columnAttributeValues.size());
		assertTrue(stackControls[0][0].length == layerAttributeValues.size());
		
		/*
		 control_totals.csv
			Household Size,1,Household Income,High,Number Of Cars,0,25
			Household Size,1,Household Income,High,Number Of Cars,1,25
			Household Size,1,Household Income,High,Number Of Cars,2,25
			Household Size,1,Household Income,High,Number Of Cars,3,25
			Household Size,1,Household Income,Low,Number Of Cars,0,25
			Household Size,1,Household Income,Low,Number Of Cars,1,25
			Household Size,1,Household Income,Low,Number Of Cars,2,25
			Household Size,1,Household Income,Low,Number Of Cars,3,25
			Household Size,2,Household Income,High,Number Of Cars,0,25
			Household Size,2,Household Income,High,Number Of Cars,1,25
			Household Size,2,Household Income,High,Number Of Cars,2,25
			Household Size,2,Household Income,High,Number Of Cars,3,25
			Household Size,2,Household Income,Low,Number Of Cars,0,25
			Household Size,2,Household Income,Low,Number Of Cars,1,25
			Household Size,2,Household Income,Low,Number Of Cars,2,25
			Household Size,2,Household Income,Low,Number Of Cars,3,25
			Household Size,3,Household Income,High,Number Of Cars,0,25
			Household Size,3,Household Income,High,Number Of Cars,1,25
			Household Size,3,Household Income,High,Number Of Cars,2,25
			Household Size,3,Household Income,High,Number Of Cars,3,25
			Household Size,3,Household Income,Low,Number Of Cars,0,25
			Household Size,3,Household Income,Low,Number Of Cars,1,25
			Household Size,3,Household Income,Low,Number Of Cars,2,25
			Household Size,3,Household Income,Low,Number Of Cars,3,25
			Household Size,1,Household Income,High,Household Type,type1,30
			Household Size,1,Household Income,High,Household Type,type2,30
			Household Size,1,Household Income,High,Household Type,type3,40
			Household Size,1,Household Income,Low,Household Type,type1,30
			Household Size,1,Household Income,Low,Household Type,type2,30
			Household Size,1,Household Income,Low,Household Type,type3,40
			Household Size,2,Household Income,High,Household Type,type1,30
			Household Size,2,Household Income,High,Household Type,type2,30
			Household Size,2,Household Income,High,Household Type,type3,40
			Household Size,2,Household Income,Low,Household Type,type1,30
			Household Size,2,Household Income,Low,Household Type,type2,30
			Household Size,2,Household Income,Low,Household Type,type3,40
			Household Size,3,Household Income,High,Household Type,type1,30
			Household Size,3,Household Income,High,Household Type,type2,30
			Household Size,3,Household Income,High,Household Type,type3,40
			Household Size,3,Household Income,Low,Household Type,type1,30
			Household Size,3,Household Income,Low,Household Type,type2,30
			Household Size,3,Household Income,Low,Household Type,type3,40
			Household Size,1,Number Of Cars,0,Household Type,type1,20
			Household Size,1,Number Of Cars,0,Household Type,type2,20
			Household Size,1,Number Of Cars,0,Household Type,type3,10
			Household Size,1,Number Of Cars,1,Household Type,type1,20
			Household Size,1,Number Of Cars,1,Household Type,type2,20
			Household Size,1,Number Of Cars,1,Household Type,type3,10
			Household Size,1,Number Of Cars,2,Household Type,type1,20
			Household Size,1,Number Of Cars,2,Household Type,type2,20
			Household Size,1,Number Of Cars,2,Household Type,type3,10
			Household Size,1,Number Of Cars,3,Household Type,type1,20
			Household Size,1,Number Of Cars,3,Household Type,type2,20
			Household Size,1,Number Of Cars,3,Household Type,type3,10
			Household Size,2,Number Of Cars,0,Household Type,type1,20
			Household Size,2,Number Of Cars,0,Household Type,type2,20
			Household Size,2,Number Of Cars,0,Household Type,type3,10
			Household Size,2,Number Of Cars,1,Household Type,type1,20
			Household Size,2,Number Of Cars,1,Household Type,type2,20
			Household Size,2,Number Of Cars,1,Household Type,type3,10
			Household Size,2,Number Of Cars,2,Household Type,type1,20
			Household Size,2,Number Of Cars,2,Household Type,type2,20
			Household Size,2,Number Of Cars,2,Household Type,type3,10
			Household Size,2,Number Of Cars,3,Household Type,type1,20
			Household Size,2,Number Of Cars,3,Household Type,type2,20
			Household Size,2,Number Of Cars,3,Household Type,type3,10
			Household Size,3,Number Of Cars,0,Household Type,type1,20
			Household Size,3,Number Of Cars,0,Household Type,type2,20
			Household Size,3,Number Of Cars,0,Household Type,type3,10
			Household Size,3,Number Of Cars,1,Household Type,type1,20
			Household Size,3,Number Of Cars,1,Household Type,type2,20
			Household Size,3,Number Of Cars,1,Household Type,type3,10
			Household Size,3,Number Of Cars,2,Household Type,type1,20
			Household Size,3,Number Of Cars,2,Household Type,type2,20
			Household Size,3,Number Of Cars,2,Household Type,type3,10
			Household Size,3,Number Of Cars,3,Household Type,type1,20
			Household Size,3,Number Of Cars,3,Household Type,type2,20
			Household Size,3,Number Of Cars,3,Household Type,type3,10
			Household Income,Low,Number Of Cars,0,Household Type,type1,25
			Household Income,Low,Number Of Cars,0,Household Type,type2,25
			Household Income,Low,Number Of Cars,0,Household Type,type3,25
			Household Income,Low,Number Of Cars,1,Household Type,type1,25
			Household Income,Low,Number Of Cars,1,Household Type,type2,25
			Household Income,Low,Number Of Cars,1,Household Type,type3,25
			Household Income,Low,Number Of Cars,2,Household Type,type1,25
			Household Income,Low,Number Of Cars,2,Household Type,type2,25
			Household Income,Low,Number Of Cars,2,Household Type,type3,25
			Household Income,Low,Number Of Cars,3,Household Type,type1,25
			Household Income,Low,Number Of Cars,3,Household Type,type2,25
			Household Income,Low,Number Of Cars,3,Household Type,type3,25
			Household Income,High,Number Of Cars,0,Household Type,type1,25
			Household Income,High,Number Of Cars,0,Household Type,type2,25
			Household Income,High,Number Of Cars,0,Household Type,type3,25
			Household Income,High,Number Of Cars,1,Household Type,type1,25
			Household Income,High,Number Of Cars,1,Household Type,type2,25
			Household Income,High,Number Of Cars,1,Household Type,type3,25
			Household Income,High,Number Of Cars,2,Household Type,type1,25
			Household Income,High,Number Of Cars,2,Household Type,type2,25
			Household Income,High,Number Of Cars,2,Household Type,type3,25
			Household Income,High,Number Of Cars,3,Household Type,type1,25
			Household Income,High,Number Of Cars,3,Household Type,type2,25
			Household Income,High,Number Of Cars,3,Household Type,type3,25
		 */

		
		IpfControlTotals controls = generationRule.getControlTotals();
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();

		// 1. row controls verification
		for (int col=0; col<columnAttributeValues.size(); col++) {
			matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				
				for (int stack=0; stack<stackAttributeValues.size(); stack++) {
					matchingCriteria.put(stackAttribute, stackAttributeValues.get(stack));
				
					int rowControlTotal = 0;
					List<AttributeValuesFrequency> matchingFrequencies = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
					for (AttributeValuesFrequency f : matchingFrequencies) { rowControlTotal += f.getFrequency(); }
					
					assertTrue(rowControls[col][layer][stack] == rowControlTotal);
				}
			}
		}

		
		// 2. column controls verification
		matchingCriteria.clear();
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				
				for (int stack=0; stack<stackAttributeValues.size(); stack++) {
					matchingCriteria.put(stackAttribute, stackAttributeValues.get(stack));

					int columnControlTotal = 0;
					List<AttributeValuesFrequency> matchingFrequencies = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
					for (AttributeValuesFrequency f : matchingFrequencies) { columnControlTotal += f.getFrequency(); }
					
					assertTrue(columnControlTotal == columnControls[row][layer][stack]);
				}
			}
		}

		
		// 3. layer controls verification
		matchingCriteria.clear();
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int stack=0; stack<stackAttributeValues.size(); stack++) {
					matchingCriteria.put(stackAttribute, stackAttributeValues.get(stack));
				
					int layerControlTotal = 0;
					List<AttributeValuesFrequency> matchingFrequencies = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
					for (AttributeValuesFrequency f : matchingFrequencies) { layerControlTotal += f.getFrequency(); }
					
					assertTrue(layerControlTotal == layerControls[row][col][stack]);
				}
			}
		}

		
		// 4. stack controls verification
		matchingCriteria.clear();
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				
					int stackControlTotal = 0;
					List<AttributeValuesFrequency> matchingFrequencies = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
					for (AttributeValuesFrequency f : matchingFrequencies) { stackControlTotal += f.getFrequency(); }
					
					assertTrue(stackControlTotal == stackControls[row][col][layer]);
				}
			}
		}
		
	}

	@Test public void testFit() throws GenstarException {
		FourWayIPF ipf1 = new FourWayIPF(generationRule);
		
		List<FourWayIteration> iterations0 = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations0 == null);
		ipf1.fit();
		iterations0 = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations0.size() == generationRule.getMaxIterations() + 1);
	}

	@Test public void testGetSelectionProbabilities() throws GenstarException {
		final FourWayIPF ipf1 = new FourWayIPF(generationRule);
		
		List<FourWayIteration> iterations = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations == null);
		
		List<AttributeValuesFrequency> selectionProbabilities = ipf1.getSelectionProbabilitiesOfLastIPFIteration();
		iterations = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations.size() == generationRule.getMaxIterations() + 1);
		assertTrue(selectionProbabilities.size() == rowAttributeValues.size() * columnAttributeValues.size() * layerAttributeValues.size() * stackAttributeValues.size());
		
		
		// verify that selection probabilities "contain" all attribute values
		List<Map<AbstractAttribute, AttributeValue>> allAttributeValues = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
		for (AttributeValue rowValue : rowAttributeValues) {
			for (AttributeValue colValue : columnAttributeValues) {
				for (AttributeValue layerValue : layerAttributeValues) {
					for (AttributeValue stackValue : stackAttributeValues) {
						Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
						attributeValues.put(rowAttribute, rowValue);
						attributeValues.put(columnAttribute, colValue);
						attributeValues.put(layerAttribute, layerValue);
						attributeValues.put(stackAttribute, stackValue);
						
						allAttributeValues.add(attributeValues);
					}
				}				
			}
		}
		
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
			assertTrue(allAttributeValues.contains(selectProba.getAttributeValuesOnData()));
		}
	}
}
