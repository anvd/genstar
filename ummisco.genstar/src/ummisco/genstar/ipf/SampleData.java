package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.util.GenstarCSVFile;

public class SampleData implements ISampleData { // TODO change to CSVSampleData
	
	private List<AbstractAttribute> attributes;
	
	private GenstarCSVFile data = null;
	
	private SortedMap<Integer, AbstractAttribute> attributeIndexes;
	
	private String populationName;
	
	private SampleEntityPopulation sampleEntityPopulation;

	
	public SampleData(final String populationName, final List<AbstractAttribute> attributes, GenstarCSVFile data) throws GenstarException {
		if (populationName == null || populationName.isEmpty()) { throw new GenstarException("Parameter populationName can neither be null nor empty"); }
		if (attributes == null) { throw new GenstarException("Parameter attributes can not be null"); }
		if (data == null) { throw new GenstarException("Parameter data can not be null"); }
		
		this.populationName = populationName;
		this.attributes = new ArrayList<AbstractAttribute>();
		this.attributes.addAll(attributes);
		this.data = data;
		
		initializeSampleEntities();
	}
	
	private void initializeSampleEntities() throws GenstarException {
		this.sampleEntityPopulation = new SampleEntityPopulation(populationName, attributes);
		
		Map<String, AbstractAttribute> attributeMap = new HashMap<String, AbstractAttribute>();
		for (AbstractAttribute attr : attributes) { attributeMap.put(attr.getNameOnData(), attr); }
		
		// 1. parse CSV file header
		List<String> sampleDataHeaders = data.getHeaders();
		attributeIndexes = new TreeMap<Integer, AbstractAttribute>();
		for (int col=0; col<sampleDataHeaders.size(); col++) {
			String attributeNameOnSample = sampleDataHeaders.get(col);
			AbstractAttribute attribute = attributeMap.get(attributeNameOnSample);
			if (attribute != null) { attributeIndexes.put(col, attribute); }
		}
		
		// 2. initialize sample entities
		List<String> rowContent;
		AbstractAttribute attribute;
		AttributeValue value;
		String valueStr;
		List<Map<String, AttributeValue>> sampleEntitiesAttributeValues = new ArrayList<Map<String, AttributeValue>>();
		Map<String, AttributeValue> sampleAttributes;
		for (int row=0; row<(data.getRows()-1); row++) { // first line is the header
			rowContent = data.getRow(row);
			
			sampleAttributes = new HashMap<String, AttributeValue>();
			for (int attributeColumn : attributeIndexes.keySet()) { // only care about attributes "recognized" by the SampleDataGenerationRule
				attribute = attributeIndexes.get(attributeColumn);
				valueStr = rowContent.get(attributeColumn);
				List<String> valueStrList = new ArrayList<String>();
				valueStrList.add(valueStr);
				value = attribute.findCorrespondingAttributeValue(valueStrList);
				
				if (value == null) { 
					value = attribute.findCorrespondingAttributeValue(valueStrList);
					throw new GenstarException("'" + valueStr + "' defined in the sample data is not recognized. File: " + data.getPath() + " at row: " + (row + 1) + ", column: " + (attributeColumn) + "."); 
				}
				sampleAttributes.put(attributeIndexes.get(attributeColumn).getNameOnData(), value);
			}
			sampleEntitiesAttributeValues.add(sampleAttributes);
		}
		
		sampleEntityPopulation.createSampleEntities(sampleEntitiesAttributeValues);
	}
	
	@Override
	public SampleEntityPopulation getSampleEntityPopulation() {
		return sampleEntityPopulation;
	}
}
