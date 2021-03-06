package ummisco.genstar.ipf;

import static org.junit.Assert.assertTrue;

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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.sample_data.ISampleData;
import ummisco.genstar.metamodel.sample_data.SampleData;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;

@RunWith(JMockit.class)
public class TwoWayIpfTest {

	@Mocked IpfGenerationRule generationRule;
	ISyntheticPopulationGenerator generator;
	final List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
	AbstractAttribute rowAttr, colAttr;
	List<AttributeValue> rowAttributeValues, colAttributeValues;

	TwoWayIpf ipf;
	
	@Before public void init() throws GenstarException {
		
		if (ipf != null) {  return; }
		
		generator = new SampleBasedGenerator("generator");
		GenstarCsvFile attributesCSVFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/two_way/attributes.csv", true);
		AttributeUtils.createAttributesFromCsvFile(generator, attributesCSVFile);
		
		GenstarCsvFile controlAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/two_way/controlled_attributes.csv", false);
		for (List<String> row : controlAttributesFile.getContent()) { controlledAttributes.add(generator.getAttributeByNameOnData(row.get(0))); }	
		
		final GenstarCsvFile sampleDataFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/two_way/people_sample.csv", true);
		final GenstarCsvFile controlTotalsFile = new GenstarCsvFile("test_data/ummisco/genstar/ipf/two_way/control_totals.csv", false);

		new NonStrictExpectations() {{
			generationRule.getGenerator(); result = generator;
			generationRule.getControlledAttributes(); result = controlledAttributes;
			
			generationRule.getAttributeByNameOnData(anyString);
			result = new Delegate() {
				AbstractAttribute delegateMethod(final String attributeName) throws GenstarException {
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
			result = controlTotalsFile;
			
			generationRule.getControlTotals();
			result = new Delegate() {
				IpfControlTotals getControlTotalsDelegate() throws GenstarException {
					return new IpfControlTotals(generationRule);
				}
			};
			
			generationRule.getMaxIterations();
			result = IpfGenerationRule.DEFAULT_MAX_ITERATIONS;
		}};
		
		ipf = new TwoWayIpf(generationRule);
		
		rowAttr = controlledAttributes.get(0);
		assertTrue(rowAttr.getNameOnData().equals("Household Size"));
		rowAttributeValues = ipf.getAttributeValues(0);
		
		colAttr = controlledAttributes.get(1);
		assertTrue(colAttr.getNameOnData().equals("Household Income"));
		colAttributeValues = ipf.getAttributeValues(1);
	}
	
	
	@Test(expected = GenstarException.class) public void initializeIPFWithInvalidControlledAttributes(@Mocked final IpfGenerationRule myGenerationRule) throws GenstarException {
		
		new Expectations() {{
			myGenerationRule.getControlledAttributes(); result = new ArrayList<AbstractAttribute>();
		}};
		
		new TwoWayIpf(myGenerationRule);
	}
	
	// "data" verification
	@Test public void testDataIsInitializedCorrectly() throws GenstarException {
		double[][] data = ipf.getData();
				
		assertTrue(data.length == rowAttributeValues.size());
		assertTrue(data[0].length == colAttributeValues.size());
		
		ISampleData sampleData = generationRule.getSampleData();
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		int row=0, col=0;
		for (AttributeValue rowValue : ipf.getAttributeValues(0)) {
			matchingCriteria.put(rowAttr, rowValue);
			
			for (AttributeValue colValue : ipf.getAttributeValues(1)) {
				matchingCriteria.put(colAttr, colValue);
				assertTrue(data[row][col] == sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria));
				
				col++;
			}
			
			col=0;
			row++;
		}
	}
	
	
	// "controls" verification
	@Test public void testControlsAreInitializedCorrectly() throws GenstarException {	
		int[] rowControls = ipf.getControls(0);
		int[] columnControls = ipf.getControls(1);
		
		assertTrue(rowControls.length == rowAttributeValues.size());
		assertTrue(columnControls.length == colAttributeValues.size());
		
		/*
		 control_totals.csv
			Household Size,1,20
			Household Size,2,50
			Household Size,3,30
			Household Income,High,40
			Household Income,Low,60		 
		 */
		
		IpfControlTotals controls = generationRule.getControlTotals();
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		int row=0;
		for (AttributeValue rowValue : rowAttributeValues) {
			matchingCriteria.put(rowAttr, rowValue);
			List<AttributeValuesFrequency> matches = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
			
			assertTrue(matches.size() == 1);
			assertTrue(matches.get(0).getFrequency() == rowControls[row]);
			
			row++;
		}
		
		matchingCriteria.clear();
		int col=0;
		for (AttributeValue colValue : colAttributeValues) {
			matchingCriteria.put(colAttr, colValue);
			List<AttributeValuesFrequency> matches = controls.getMatchingAttributeValuesFrequencies(matchingCriteria);
			
			assertTrue(matches.size() == 1);
			assertTrue(matches.get(0).getFrequency() == columnControls[col]);
			
			col++;
		}
	}
	
	@Test public void testFit() throws GenstarException {
		TwoWayIpf ipf1 = new TwoWayIpf(generationRule);
		
		List<IpfIteration> iterations0 = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations0 == null);
		ipf1.fit();
		iterations0 = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations0.size() == generationRule.getMaxIterations() + 1);

		// DEBUG
		//ipf1.printDebug();
	}
	
	@Test public void testGetSelectionProbabilities() throws GenstarException {
		final TwoWayIpf ipf1 = new TwoWayIpf(generationRule);
		
		List<IpfIteration> iterations = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations == null);
		
		List<AttributeValuesFrequency> selectionProbabilities = ipf1.getSelectionProbabilitiesOfLastIPFIteration();
		iterations = Deencapsulation.getField(ipf1, "iterations");
		assertTrue(iterations.size() == generationRule.getMaxIterations() + 1);
		assertTrue(selectionProbabilities.size() == rowAttributeValues.size() * colAttributeValues.size());
		
		
		// verify that selection probabilities "contain" all attribute values
		List<Map<AbstractAttribute, AttributeValue>> allAttributeValues = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
		for (AttributeValue rowValue : rowAttributeValues) {

			for (AttributeValue colValue : colAttributeValues) {
				Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
				attributeValues.put(rowAttr, rowValue);
				attributeValues.put(colAttr, colValue);
				
				allAttributeValues.add(attributeValues);
			}
		}
		
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
			assertTrue(allAttributeValues.contains(selectProba.getAttributeValuesOnData()));
		}
	}
}
