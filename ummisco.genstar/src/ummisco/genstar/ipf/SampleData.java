package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.IWithAttributes;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.util.GenstarCSVFile;

public class SampleData implements ISampleData {
	
	private IWithAttributes withAttributes;
	
	private GenstarCSVFile data = null;
	
	private SortedMap<Integer, AbstractAttribute> attributeIndexes;
	
	private List<SampleEntity> sampleEntities;

	
	public SampleData(final IWithAttributes withAttributes, GenstarCSVFile data) throws GenstarException {
		if (withAttributes == null) { throw new GenstarException("Parameter withAttributes can not be null"); }
		if (data == null) { throw new GenstarException("Parameter data can not be null"); }
		
		this.withAttributes = withAttributes;
		this.data = data;
		
		initializeSampleEntities();
	}
	
	private void initializeSampleEntities() throws GenstarException {
		
		// 1. parse CSV file header
		List<String> sampleDataHeaders = data.getHeaders();
		attributeIndexes = new TreeMap<Integer, AbstractAttribute>();
		for (int col=0; col<sampleDataHeaders.size(); col++) {
			String attributeNameOnSample = sampleDataHeaders.get(col);
			AbstractAttribute attribute = withAttributes.getAttribute(attributeNameOnSample);
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
				
				if (attribute.getNameOnData().equals("Household ID")) {
					System.out.println("");
				}
				
				value = attribute.findCorrespondingAttributeValue(valueStrList);
				
				if (value == null) { 
					value = attribute.findCorrespondingAttributeValue(valueStrList);
					throw new GenstarException("'" + valueStr + "' defined in the sample data is not recognized. File: " + data.getPath() + " at row: " + (row + 1) + ", column: " + (attributeColumn) + "."); 
				}
				sampleAttributes.put(attributeIndexes.get(attributeColumn), value);
			}
			sampleEntities.add(new SampleEntity(sampleAttributes));
		}
	}
	
	@Override
	public int countMatchingEntities(final Map<AbstractAttribute, AttributeValue> matchingCriteria) {
		int matchingIndividuals = 0;
		for (SampleEntity se : sampleEntities) { if (se.isMatch(matchingCriteria)) { matchingIndividuals++; } }
		
		return matchingIndividuals;
	}
	
	@Override
	public List<SampleEntity> getMatchingEntities(final Map<AbstractAttribute, AttributeValue> matchingCriteria) {
		List<SampleEntity> matchings = new ArrayList<SampleEntity>();
		for (SampleEntity se : sampleEntities) { if (se.isMatch(matchingCriteria)) { matchings.add(se); } }
		
		return matchings;
	}
	
	@Override
	public List<SampleEntity> getSampleEntities() {
		return sampleEntities;
	}
}
