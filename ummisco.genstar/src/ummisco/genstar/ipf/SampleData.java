package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.util.GenstarCSVFile;

public class SampleData {
	
	private SampleDataGenerationRule generationRule;
	
	private GenstarCSVFile data = null; 
	
	private SortedMap<Integer, AbstractAttribute> attributeIndexes;
	
	private List<SampleEntity> sampleEntities;

	
	public SampleData(final SampleDataGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new IllegalArgumentException("'generationRule' can not be null"); }
		
		this.generationRule = generationRule;
		this.data = generationRule.getSampleDataFile();
		if (data == null) { throw new GenstarException("Sample Data Generation Rule contains a null CSV file"); }
		
		initializeSampleEntities();
	}
	
	private void initializeSampleEntities() throws GenstarException {
		
		// 1. parse CSV file header
		List<String> sampleDataHeaders = data.getHeaders();
		attributeIndexes = new TreeMap<Integer, AbstractAttribute>();
		for (int col=0; col<sampleDataHeaders.size(); col++) {
			String attributeNameOnSample = sampleDataHeaders.get(col);
			AbstractAttribute attribute = generationRule.findAttributeByNameOnData(attributeNameOnSample);
			if (attribute != null) { attributeIndexes.put(col, attribute); }
		}
		
		// 2. initialize sample entities
		sampleEntities = new ArrayList<SampleEntity>();
		List<String> rowContent;
		AbstractAttribute attribute;
		AttributeValue value;
		String valueStr;
		Map<AbstractAttribute, AttributeValue> sampleAttributes;
		for (int row=0; row<(data.getRows()-1); row++) { // first line is the header
			rowContent = data.getRow(row);
			
			sampleAttributes = new HashMap<AbstractAttribute, AttributeValue>();
			for (int attributeColumn : attributeIndexes.keySet()) { // only care about attributes "recognized" by the SampleDataGenerationRule
				attribute = attributeIndexes.get(attributeColumn);
				valueStr = rowContent.get(attributeColumn);
				List<String> valueStrList = new ArrayList<String>();
				valueStrList.add(valueStr);
				value = attribute.findCorrespondingAttributeValue(valueStrList);
				
				if (value == null) { throw new GenstarException("'" + valueStr + "' defined in the sample data is not recognized. File: " + data.getFilePath() + " at row: " + (row + 1) + ", column: " + (attributeColumn) + "."); }
				sampleAttributes.put(attributeIndexes.get(attributeColumn), value);
			}
			sampleEntities.add(new SampleEntity(sampleAttributes));
		}
	}

	
	public int countMatchingEntities(final Map<AbstractAttribute, AttributeValue> matchingCriteria) {
		int matchingIndividuals = 0;
		for (SampleEntity se : sampleEntities) { if (se.isMatch(matchingCriteria)) { matchingIndividuals++; } }
		
		return matchingIndividuals;
	}
	
	public List<SampleEntity> getMatchingEntities(final Map<AbstractAttribute, AttributeValue> matchingCriteria) {
		List<SampleEntity> matchings = new ArrayList<SampleEntity>();
		for (SampleEntity se : sampleEntities) { if (se.isMatch(matchingCriteria)) { matchings.add(se); } }
		
		return matchings;
	}
		
	
	public List<SampleEntity> getSampleEntities() {
		return sampleEntities;
	}
}
