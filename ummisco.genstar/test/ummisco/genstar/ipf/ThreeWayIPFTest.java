package ummisco.genstar.ipf;

import static org.junit.Assert.*;

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
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.AttributeValuesFrequency;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SyntheticPopulationGenerator;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarFactoryUtils;

@RunWith(JMockit.class)
public class ThreeWayIPFTest {
	
	@Mocked SampleDataGenerationRule generationRule;
	ISyntheticPopulationGenerator generator;
	final List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
	AbstractAttribute rowAttribute, columnAttribute, layerAttribute;
	List<AttributeValue> rowAttributeValues, columnAttributeValues, layerAttributeValues;

	ThreeWayIPF ipf;

	
	@Before public void init() throws GenstarException {
		
		generator = new SyntheticPopulationGenerator("generator", 100);
		GenstarCSVFile attributesCSVFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/three_way/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesCSVFile);
		
		GenstarCSVFile controlAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/three_way/controlled_attributes.csv", false);
		for (List<String> row : controlAttributesFile.getContent()) { controlledAttributes.add(generator.getAttribute(row.get(0))); }	
		
		final GenstarCSVFile sampleDataFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/three_way/people_sample.csv", true);
		final GenstarCSVFile controlTotalsFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/three_way/control_totals.csv", false);

		new NonStrictExpectations() {{
			generationRule.getGenerator(); result = generator;
			generationRule.getControlledAttributes(); result = controlledAttributes;
			
			generationRule.findAttributeByNameOnData(anyString);
			result = new Delegate() {
				AbstractAttribute delegateMethod(final String attributeName) {
					return generator.getAttribute(attributeName);
				}
			};
			
			generationRule.getSampleDataFile();
			result = sampleDataFile;
			
			generationRule.getSampleData();
			result = new Delegate() {
				SampleData getSampleDataDelegate() throws GenstarException {
					return new SampleData(generationRule);
				}
			};
			
			generationRule.getControlTotalsFile();
			result = controlTotalsFile;
			
			generationRule.getControlTotals();
			result = new Delegate() {
				ControlTotals getControlTotalsDelegate() throws GenstarException {
					return new ControlTotals(generationRule);
				}
			};
		}};
		
		ipf = new ThreeWayIPF(generationRule);
		
		
		/*
		 controlled_attributes.csv
			Household Size
			Household Income
			Number Of Cars
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
	}
	
	@Test(expected = GenstarException.class) public void testInitializeThreeWayIPFWithInvalidControlledAttributes(@Mocked final SampleDataGenerationRule threeWayGenerationRule) throws GenstarException {
		
		new Expectations() {{
			threeWayGenerationRule.getControlledAttributes(); result = new ArrayList<AbstractAttribute>();
		}};
		new ThreeWayIPF(threeWayGenerationRule);
	}
	
	// "data" verification
	@Test public void testDataIsInitializedCorrectly() throws GenstarException {
		
		// double[][][] data = Deencapsulation.getField(ipf, "data");
		double[][][] data = ipf.getData();
				
		assertTrue(data.length == rowAttributeValues.size());
		assertTrue(data[0].length == columnAttributeValues.size());
		assertTrue(data[0][0].length == layerAttributeValues.size());
		
		
		SampleData sampleData = generationRule.getSampleData();
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
					assertTrue(data[row][col][layer] == sampleData.countMatchingEntities(matchingCriteria));
				}
			}
		}
	}
	

	// "controls" verification
	@Test public void testControlsAreInitializedCorrectly() {
		int[][] rowControls = Deencapsulation.getField(ipf, "rowControls");
		int[][] columnControls = Deencapsulation.getField(ipf, "columnControls");
		int[][] layerControls = Deencapsulation.getField(ipf, "layerControls");
		
		assertTrue(rowControls.length == columnAttributeValues.size());
		assertTrue(rowControls[0].length == layerAttributeValues.size());
		assertTrue(columnControls.length == rowAttributeValues.size());
		assertTrue(columnControls[0].length == layerAttributeValues.size());
		assertTrue(layerControls.length == rowAttributeValues.size());
		assertTrue(layerControls[0].length == columnAttributeValues.size());
		
		
		/*
		 control_totals.csv
			Household Size,1,Household Income,High,20
			Household Size,1,Household Income,Low,20
			Household Size,2,Household Income,High,50
			Household Size,2,Household Income,Low,30
			Household Size,3,Household Income,High,70
			Household Size,3,Household Income,Low,25
			Household Size,1,Number Of Cars,0,10
			Household Size,1,Number Of Cars,1,15
			Household Size,1,Number Of Cars,2,8
			Household Size,1,Number Of Cars,3,18
			Household Size,2,Number Of Cars,0,15
			Household Size,2,Number Of Cars,1,17
			Household Size,2,Number Of Cars,2,12
			Household Size,2,Number Of Cars,3,21
			Household Size,3,Number Of Cars,0,34
			Household Size,3,Number Of Cars,1,28
			Household Size,3,Number Of Cars,2,24
			Household Size,3,Number Of Cars,3,14
			Household Income,Low,Number Of Cars,0,15
			Household Income,Low,Number Of Cars,1,25
			Household Income,Low,Number Of Cars,2,5
			Household Income,Low,Number Of Cars,3,45
			Household Income,High,Number Of Cars,0,22
			Household Income,High,Number Of Cars,1,12
			Household Income,High,Number Of Cars,2,32
			Household Income,High,Number Of Cars,3,42		 
		 */
		
		
		ControlTotals controls = generationRule.getControlTotals();
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();

		// 1. row controls verification
		for (int col=0; col<columnAttributeValues.size(); col++) {
			matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				int rowControlTotal = 0;
				List<AttributeValuesFrequency> matchingFrequencies = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
				for (AttributeValuesFrequency f : matchingFrequencies) { rowControlTotal += f.getFrequency(); }
				
				assertTrue(rowControls[col][layer] == rowControlTotal);
			}
		}
		
		
		// 2. column controls verification
		matchingCriteria.clear();
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				int columnControlTotal = 0;
				List<AttributeValuesFrequency> matchingFrequencies = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
				for (AttributeValuesFrequency f : matchingFrequencies) { columnControlTotal += f.getFrequency(); }
				
				assertTrue(columnControlTotal == columnControls[row][layer]);
			}
		}
		
		
		// 3. layer controls verification
		matchingCriteria.clear();
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				int layerControlTotal = 0;
				List<AttributeValuesFrequency> matchingFrequencies = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
				for (AttributeValuesFrequency f : matchingFrequencies) { layerControlTotal += f.getFrequency(); }
				
				assertTrue(layerControlTotal == layerControls[row][col]);
			}
		}
	}
	
	@Test public void testFit() throws GenstarException {
		ThreeWayIPF ipf1 = new ThreeWayIPF(generationRule);
		
		List<IPFIteration> iterations0 = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations0 == null);
		ipf1.fit();
		iterations0 = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations0.size() == ipf1.getMaxIteration() + 1);
		
		
		// DEBUG
		ipf1.printDebug();
	}
	
	@Test public void testGetSelectionProbabilities() throws GenstarException {
		final ThreeWayIPF ipf1 = new ThreeWayIPF(generationRule);
		
		List<IPFIteration> iterations = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations == null);
		
		List<AttributeValuesFrequency> selectionProbabilities = ipf1.getSelectionProbabilitiesOfLastIPFIteration();
		iterations = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations.size() == ipf1.getMaxIteration() + 1);
		assertTrue(selectionProbabilities.size() == rowAttributeValues.size() * columnAttributeValues.size() * layerAttributeValues.size());
		
		
		// verify that selection probabilities "contain" all attribute values
		List<Map<AbstractAttribute, AttributeValue>> allAttributeValues = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
		for (AttributeValue rowValue : rowAttributeValues) {

			for (AttributeValue colValue : columnAttributeValues) {
				for (AttributeValue layerValue : layerAttributeValues) {
					Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
					attributeValues.put(rowAttribute, rowValue);
					attributeValues.put(columnAttribute, colValue);
					attributeValues.put(layerAttribute, layerValue);
					
					allAttributeValues.add(attributeValues);
				}
				
			}
		}
		
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
			assertTrue(allAttributeValues.contains(selectProba.getAttributeValues()));
		}
	}
	
	
}
