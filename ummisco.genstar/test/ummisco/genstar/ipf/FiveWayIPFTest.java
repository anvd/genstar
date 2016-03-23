package ummisco.genstar.ipf;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import mockit.Deencapsulation;
import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarUtils;

@RunWith(JMockit.class)
public class FiveWayIPFTest {

	// household population
	@Mocked SampleDataGenerationRule householdGenerationRule;
	ISyntheticPopulationGenerator householdGenerator;
	final List<AbstractAttribute> householdControlledAttributes = new ArrayList<AbstractAttribute>();
	AbstractAttribute householdRowAttribute, householdColumnAttribute, householdLayerAttribute, householdStackAttribute, householdFifthAttribute;
	List<AttributeValue> householdRowAttributeValues, householdColumnAttributeValues, householdLayerAttributeValues, householdStackAttributeValues, householdFifthAttributeValues;

	FiveWayIPF householdIPF;

	
	@Before public void init() throws GenstarException {
		
		// household population
		String householdAttributesFilePath = "test_data/ummisco/genstar/ipf/five_way/household_attributes.csv";
		String _householdSampleDataFileNamePath = "test_data/ummisco/genstar/ipf/five_way/household_sample.csv";
		String householdControlTotalsFilePath = "test_data/ummisco/genstar/ipf/five_way/household_control_totals.csv";
		GenstarCSVFile householdControlAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/five_way/household_controlled_attributes.csv", true);
		
		String householdControlAttributesListFilePath = "test_data/ummisco/genstar/ipf/five_way/household_controlled_attributes_list.csv";
		GenstarCSVFile householdControlAttributesListFile = new GenstarCSVFile(householdControlAttributesListFilePath, false);
		
		
		// generate household control totals if necessary
		File householdControlTotalsFile = new File(householdControlTotalsFilePath);
		if (!householdControlTotalsFile.exists()) {
			int numberOfHouseholds = 1000;
			List<List<String>> householdControlTotals = GenstarUtils.generateControlTotals(householdControlAttributesFile, numberOfHouseholds);
			GenstarUtils.writeControlTotalsToCsvFile(householdControlTotals, householdControlTotalsFilePath);
		}
		
		
		// generate household sample data if necessary
		File _sampleDataFile = new File(_householdSampleDataFileNamePath);
		if (!_sampleDataFile.exists()) {
			String householdPopulationName = "household population";
			GenstarCSVFile _householdAttributesFile = new GenstarCSVFile(householdAttributesFilePath, true);
			int minEntitiesOfEachAttributeValuesSet = 1;
			int maxEntitiesOfEachAttributeValuesSet = 3;
			
			ISyntheticPopulation generatedSamplePopulation = GenstarUtils.generateRandomSinglePopulation(householdPopulationName, _householdAttributesFile, minEntitiesOfEachAttributeValuesSet, maxEntitiesOfEachAttributeValuesSet);
			
			Map<String, String> csvFilePaths = new HashMap<String, String>();
			csvFilePaths.put(householdPopulationName, _householdSampleDataFileNamePath);
			
			GenstarUtils.writePopulationToCSVFile(generatedSamplePopulation, csvFilePaths);
		}
		 
		householdGenerator = new SingleRuleGenerator("household generator");
		GenstarCSVFile householdAttributesCSVFile = new GenstarCSVFile(householdAttributesFilePath, true);
		GenstarUtils.createAttributesFromCSVFile(householdGenerator, householdAttributesCSVFile);
		
		for (List<String> row : householdControlAttributesListFile.getContent()) { householdControlledAttributes.add(householdGenerator.getAttributeByNameOnData(row.get(0))); }	
		
		final GenstarCSVFile sampleDataFile = new GenstarCSVFile(_householdSampleDataFileNamePath, true);
		final GenstarCSVFile controlTotalsFile = new GenstarCSVFile(householdControlTotalsFilePath, false);
		
		new NonStrictExpectations() {{
			onInstance(householdGenerationRule).getGenerator(); result = householdGenerator;
			onInstance(householdGenerationRule).getControlledAttributes(); result = householdControlledAttributes;
			
			onInstance(householdGenerationRule).getAttributeByNameOnData(anyString);
			result = new Delegate() {
				AbstractAttribute delegateMethod(final String attributeName) {
					return householdGenerator.getAttributeByNameOnData(attributeName);
				}
			};
			
			onInstance(householdGenerationRule).getSampleData();
			result = new Delegate() {
				SampleData getSampleDataDelegate() throws GenstarException {
					return new SampleData("household", householdGenerator.getAttributes(), sampleDataFile);
				}
			};
			
			onInstance(householdGenerationRule).getControlTotalsFile();
			result = controlTotalsFile;
			
			onInstance(householdGenerationRule).getControlTotals();
			result = new Delegate() {
				ControlTotals getControlTotalsDelegate() throws GenstarException {
					return new ControlTotals(householdGenerationRule);
				}
			};
			
			onInstance(householdGenerationRule).getMaxIterations();
			result = SampleDataGenerationRule.DEFAULT_MAX_ITERATIONS;
		}};
		
		householdIPF = new FiveWayIPF(householdGenerationRule);
		
		/*
		 controlled_attributes_list.csv
			Household Size
			Household Income
			Number Of Cars
			Household Type
			Number Of Bicycles
		 */
		householdRowAttribute = householdControlledAttributes.get(0);
		assertTrue(householdRowAttribute.getNameOnData().equals("Household Size"));
		householdRowAttributeValues = householdIPF.getAttributeValues(0);
		
		householdColumnAttribute = householdControlledAttributes.get(1);
		assertTrue(householdColumnAttribute.getNameOnData().equals("Household Income"));
		householdColumnAttributeValues = householdIPF.getAttributeValues(1);
		
		householdLayerAttribute = householdControlledAttributes.get(2);
		assertTrue(householdLayerAttribute.getNameOnData().equals("Number Of Cars"));
		householdLayerAttributeValues = householdIPF.getAttributeValues(2);

		householdStackAttribute = householdControlledAttributes.get(3);
		assertTrue(householdStackAttribute.getNameOnData().equals("Household Type"));
		householdStackAttributeValues = householdIPF.getAttributeValues(3);
		 
		householdFifthAttribute = householdControlledAttributes.get(4);
		assertTrue(householdFifthAttribute.getNameOnData().equals("Number Of Bicycles"));
		householdFifthAttributeValues = householdIPF.getAttributeValues(4);
	}

	@Test(expected = GenstarException.class) public void testInitializeFiveWayIPFWithInvalidControlledAttributes(@Mocked final SampleDataGenerationRule fiveWayGenerationRule) throws GenstarException {
		new Expectations() {{
			fiveWayGenerationRule.getControlledAttributes(); result = new ArrayList<AbstractAttribute>();
		}};
		new FiveWayIPF(fiveWayGenerationRule);
	}

	// "data" verification
	@Test public void testDataIsInitializedCorrectly() throws GenstarException {
		double[][][][][] data = householdIPF.getData();
				
		assertTrue(data.length == householdRowAttributeValues.size());
		assertTrue(data[0].length == householdColumnAttributeValues.size());
		assertTrue(data[0][0].length == householdLayerAttributeValues.size());
		assertTrue(data[0][0][0].length == householdStackAttributeValues.size());
		assertTrue(data[0][0][0][0].length == householdFifthAttributeValues.size());
		
		
		ISampleData sampleData = householdGenerationRule.getSampleData();
		Map<String, AttributeValue> matchingCriteria = new HashMap<String, AttributeValue>();
		
		for (int row=0; row<householdRowAttributeValues.size(); row++) {
			matchingCriteria.put(householdRowAttribute.getNameOnEntity(), householdRowAttributeValues.get(row));
			
			for (int col=0; col<householdColumnAttributeValues.size(); col++) {
				matchingCriteria.put(householdColumnAttribute.getNameOnEntity(), householdColumnAttributeValues.get(col));
				
				for (int layer=0; layer<householdLayerAttributeValues.size(); layer++) {
					matchingCriteria.put(householdLayerAttribute.getNameOnEntity(), householdLayerAttributeValues.get(layer));
					
					for (int stack=0; stack<householdStackAttributeValues.size(); stack++) {
						matchingCriteria.put(householdStackAttribute.getNameOnEntity(), householdStackAttributeValues.get(stack));
						
						for (int fifthDim=0; fifthDim<householdFifthAttributeValues.size(); fifthDim++) {
							matchingCriteria.put(householdFifthAttribute.getNameOnEntity(), householdFifthAttributeValues.get(fifthDim));
						
							assertTrue(data[row][col][layer][stack][fifthDim] == sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCriteria));
						}
					}
				}
			}
		}
	}

	// "controls" verification
	@Test public void testControlsAreInitializedCorrectly() throws GenstarException {
		int[][][][] rowControls = householdIPF.getControls(0);
		int[][][][] columnControls = householdIPF.getControls(1);
		int[][][][] layerControls = householdIPF.getControls(2);
		int[][][][] stackControls = householdIPF.getControls(3);
		int[][][][] fifthAttributeControls = householdIPF.getControls(4);
		
		assertTrue(rowControls.length == householdColumnAttributeValues.size());
		assertTrue(rowControls[0].length == householdLayerAttributeValues.size());
		assertTrue(rowControls[0][0].length == householdStackAttributeValues.size());
		assertTrue(rowControls[0][0][0].length == householdFifthAttributeValues.size());
		
		assertTrue(columnControls.length == householdRowAttributeValues.size());
		assertTrue(columnControls[0].length == householdLayerAttributeValues.size());
		assertTrue(columnControls[0][0].length == householdStackAttributeValues.size());
		assertTrue(columnControls[0][0][0].length == householdFifthAttributeValues.size());

		assertTrue(layerControls.length == householdRowAttributeValues.size());
		assertTrue(layerControls[0].length == householdColumnAttributeValues.size());
		assertTrue(layerControls[0][0].length == householdStackAttributeValues.size());
		assertTrue(layerControls[0][0][0].length == householdFifthAttributeValues.size());
		
		assertTrue(stackControls.length == householdRowAttributeValues.size());
		assertTrue(stackControls[0].length == householdColumnAttributeValues.size());
		assertTrue(stackControls[0][0].length == householdLayerAttributeValues.size());
		assertTrue(stackControls[0][0][0].length == householdFifthAttributeValues.size());
		
		assertTrue(fifthAttributeControls.length == householdRowAttributeValues.size());
		assertTrue(fifthAttributeControls[0].length == householdColumnAttributeValues.size());
		assertTrue(fifthAttributeControls[0][0].length == householdLayerAttributeValues.size());
		assertTrue(fifthAttributeControls[0][0][0].length == householdStackAttributeValues.size());
		
		
		ControlTotals controls = householdGenerationRule.getControlTotals();
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		
		// 1. row controls verification
		for (int col=0; col<householdColumnAttributeValues.size(); col++) {
			matchingCriteria.put(householdColumnAttribute, householdColumnAttributeValues.get(col));
			
			for (int layer=0; layer<householdLayerAttributeValues.size(); layer++) {
				matchingCriteria.put(householdLayerAttribute, householdLayerAttributeValues.get(layer));
				
				for (int stack=0; stack<householdStackAttributeValues.size(); stack++) {
					matchingCriteria.put(householdStackAttribute, householdStackAttributeValues.get(stack));
					
					for (int fifthDim=0; fifthDim<householdFifthAttributeValues.size(); fifthDim++) {
						matchingCriteria.put(householdFifthAttribute, householdFifthAttributeValues.get(fifthDim));
					
						int rowControlTotal = 0;
						List<AttributeValuesFrequency> matchingFrequencies = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
						for (AttributeValuesFrequency f : matchingFrequencies) { rowControlTotal += f.getFrequency(); }
						
						assertTrue(rowControls[col][layer][stack][fifthDim] == rowControlTotal);
					}
				}
			}
		}

	
		// 2. column controls verification
		matchingCriteria.clear();
		for (int row=0; row<householdRowAttributeValues.size(); row++) {
			matchingCriteria.put(householdRowAttribute, householdRowAttributeValues.get(row));
			
			for (int layer=0; layer<householdLayerAttributeValues.size(); layer++) {
				matchingCriteria.put(householdLayerAttribute, householdLayerAttributeValues.get(layer));
				
				for (int stack=0; stack<householdStackAttributeValues.size(); stack++) {
					matchingCriteria.put(householdStackAttribute, householdStackAttributeValues.get(stack));
					
					for (int fifthDim=0; fifthDim<householdFifthAttributeValues.size(); fifthDim++) {
						matchingCriteria.put(householdFifthAttribute, householdFifthAttributeValues.get(fifthDim));

						int columnControlTotal = 0;
						List<AttributeValuesFrequency> matchingFrequencies = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
						for (AttributeValuesFrequency f : matchingFrequencies) { columnControlTotal += f.getFrequency(); }

						assertTrue(columnControlTotal == columnControls[row][layer][stack][fifthDim]);
					}
				}
			}
		}
	
	
		// 3. layer controls verification
		matchingCriteria.clear();
		for (int row=0; row<householdRowAttributeValues.size(); row++) {
			matchingCriteria.put(householdRowAttribute, householdRowAttributeValues.get(row));
			
			for (int col=0; col<householdColumnAttributeValues.size(); col++) {
				matchingCriteria.put(householdColumnAttribute, householdColumnAttributeValues.get(col));
				
				for (int stack=0; stack<householdStackAttributeValues.size(); stack++) {
					matchingCriteria.put(householdStackAttribute, householdStackAttributeValues.get(stack));
					
					for (int fifthDim=0; fifthDim<householdFifthAttributeValues.size(); fifthDim++) {
						matchingCriteria.put(householdFifthAttribute, householdFifthAttributeValues.get(fifthDim));

						int layerControlTotal = 0;
						List<AttributeValuesFrequency> matchingFrequencies = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
						for (AttributeValuesFrequency f : matchingFrequencies) { layerControlTotal += f.getFrequency(); }
						
						assertTrue(layerControlTotal == layerControls[row][col][stack][fifthDim]);
					}
				}
			}
		}

	
		// 4. stack controls verification
		matchingCriteria.clear();
		for (int row=0; row<householdRowAttributeValues.size(); row++) {
			matchingCriteria.put(householdRowAttribute, householdRowAttributeValues.get(row));
			
			for (int col=0; col<householdColumnAttributeValues.size(); col++) {
				matchingCriteria.put(householdColumnAttribute, householdColumnAttributeValues.get(col));
				
				for (int layer=0; layer<householdLayerAttributeValues.size(); layer++) {
					matchingCriteria.put(householdLayerAttribute, householdLayerAttributeValues.get(layer));
				
					for (int fifthDim=0; fifthDim<householdFifthAttributeValues.size(); fifthDim++) {
						matchingCriteria.put(householdFifthAttribute, householdFifthAttributeValues.get(fifthDim));

							int stackControlTotal = 0;
							List<AttributeValuesFrequency> matchingFrequencies = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
							for (AttributeValuesFrequency f : matchingFrequencies) { stackControlTotal += f.getFrequency(); }
							
							assertTrue(stackControlTotal == stackControls[row][col][layer][fifthDim]);
					}
				}
			}
		}

	
		// 5. fifthDim controls verification
		matchingCriteria.clear();
		for (int row=0; row<householdRowAttributeValues.size(); row++) {
			matchingCriteria.put(householdRowAttribute, householdRowAttributeValues.get(row));
			
			for (int col=0; col<householdColumnAttributeValues.size(); col++) {
				matchingCriteria.put(householdColumnAttribute, householdColumnAttributeValues.get(col));
				
				for (int layer=0; layer<householdLayerAttributeValues.size(); layer++) {
					matchingCriteria.put(householdLayerAttribute, householdLayerAttributeValues.get(layer));
				
					for (int stack=0; stack<householdStackAttributeValues.size(); stack++) {
						matchingCriteria.put(householdStackAttribute, householdStackAttributeValues.get(stack));

						int fifthDimControlTotal = 0;
						List<AttributeValuesFrequency> matchingFrequencies = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
						for (AttributeValuesFrequency f : matchingFrequencies) { fifthDimControlTotal += f.getFrequency(); }
						
						assertTrue(fifthDimControlTotal == fifthAttributeControls[row][col][layer][stack]);
					}
				}
			}
		}
	}

	@Test public void testFit() throws GenstarException {
		FiveWayIPF ipf1 = new FiveWayIPF(householdGenerationRule);
		
		List<FiveWayIteration> iterations0 = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations0 == null);
		ipf1.fit();
		iterations0 = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations0.size() == householdGenerationRule.getMaxIterations() + 1);
	}

	@Test public void testGetSelectionProbabilities() throws GenstarException {
		final FiveWayIPF ipf1 = new FiveWayIPF(householdGenerationRule);
		
		List<FiveWayIteration> iterations = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations == null);
		
		List<AttributeValuesFrequency> selectionProbabilities = ipf1.getSelectionProbabilitiesOfLastIPFIteration();
		iterations = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations.size() == householdGenerationRule.getMaxIterations() + 1);
		assertTrue(selectionProbabilities.size() == householdRowAttributeValues.size() * householdColumnAttributeValues.size() * householdLayerAttributeValues.size() * householdStackAttributeValues.size() * householdFifthAttributeValues.size());
		
		
		// verify that selection probabilities "contain" all attribute values
		List<Map<AbstractAttribute, AttributeValue>> allAttributeValues = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
		for (AttributeValue rowValue : householdRowAttributeValues) {
			for (AttributeValue colValue : householdColumnAttributeValues) {
				for (AttributeValue layerValue : householdLayerAttributeValues) {
					for (AttributeValue stackValue : householdStackAttributeValues) {
						for (AttributeValue fifthDimAttributeValue : householdFifthAttributeValues) {
							Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
							attributeValues.put(householdRowAttribute, rowValue);
							attributeValues.put(householdColumnAttribute, colValue);
							attributeValues.put(householdLayerAttribute, layerValue);
							attributeValues.put(householdStackAttribute, stackValue);
							attributeValues.put(householdFifthAttribute, fifthDimAttributeValue);
							
							allAttributeValues.add(attributeValues);
						}
					}
				}				
			}
		}
		
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
			assertTrue(allAttributeValues.contains(selectProba.getAttributeValues()));
		}
	}
}
