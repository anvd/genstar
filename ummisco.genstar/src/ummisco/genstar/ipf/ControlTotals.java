package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarUtils;


public class ControlTotals {
	
	private GenstarCSVFile data;
	
	private List<AbstractAttribute> controlledAttributes;
	
	private List<AttributeValuesFrequency> avFrequencies;

	
	public ControlTotals(final SampleDataGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' parameter can not be null"); }
		
		this.data = generationRule.getControlTotalsFile();
		this.controlledAttributes = generationRule.getControlledAttributes();
		this.avFrequencies = GenstarUtils.readAttributeValuesFrequenciesFromControlTotalsFile(this.data, this.controlledAttributes);
		
//		parseAttributeValuesFrequency();
	}
	
	
//	private void parseAttributeValuesFrequency() throws GenstarException {
//		
//		TODO move this code to GenstarFactoryUtils.readAttributeValuesFrequenciesFromControlTotalsFile
//		
//		avFrequencies = new ArrayList<AttributeValuesFrequency>();
//		
//		AbstractAttribute attribute;
//		AttributeValue attributeValue;
//		List<String> valueList = new ArrayList<String>();
//		int line = 1;
//		Map<AbstractAttribute, AttributeValue> attributeValues;
//		for (List<String> aRow : data.getContent()) {
//			if (aRow.size() < 3 && (aRow.size() %2 != 1)) throw new GenstarException("Invalid attribute values frequency format. File: " + data.getPath() + ", line: " + line);
//			line++;
//			
//			attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
//			for (int col=0; col<(aRow.size() - 1); col+=2) { // Parse each line of the file
//				// 1. parse the attribute name column
//				attribute = generator.getAttributeByNameOnData(aRow.get(col));
//				if (attribute == null) { throw new GenstarException("'" + aRow.get(col) + "' is not a valid attribute. File: " + data.getPath() + ", line: " + line + "."); }
//				if (!controlledAttributes.contains(attribute)) { throw new GenstarException("'" + aRow.get(col) + "' is not a controlled attribute."); }
//				if (attributeValues.containsKey(attribute)) { throw new GenstarException("Duplicated attribute : '" + attribute.getNameOnData() + "'"); }
//				
//				// 2. parse the attribute value column
//				valueList.clear();
//				String attributeValueString = aRow.get(col+1);
//				if (attributeValueString.contains(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER)) { // range value
//					StringTokenizer rangeValueToken = new StringTokenizer(attributeValueString, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
//					if (rangeValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value: '" + attributeValueString + "'. File: " + data.getPath()); }
//					valueList.add(rangeValueToken.nextToken());
//					valueList.add(rangeValueToken.nextToken());
//				} else { // unique value
//					valueList.add(attributeValueString);
//				}
//				
//				
//				attributeValue = attribute.findCorrespondingAttributeValue(valueList);
//				if (attributeValue == null) { throw new GenstarException("Attribute value '" + aRow.get(col+1) + "' not found in valid attribute value of " + attribute.getNameOnData()
//						+ ". File: " + data.getPath() + ", line: " + line); }
//				
//				attributeValues.put(attribute, attributeValue);
//			}
//			
//			// "frequency" is the last column
//			int frequency = Integer.parseInt(aRow.get(aRow.size() - 1));
//			avFrequencies.add(new AttributeValuesFrequency(attributeValues, frequency));
//		}
//	}
	
	public List<AttributeValuesFrequency> getMatchingAttributeValuesFrequencies(final Map<AbstractAttribute, AttributeValue> matchingCriteria) {
		List<AttributeValuesFrequency> matchings = new ArrayList<AttributeValuesFrequency>();
		
		for (AttributeValuesFrequency f : avFrequencies) {
			if (f.matchAttributeValues(matchingCriteria)) { matchings.add(f); }
		}
		
		return matchings;
	}
	
	
	// TODO verify the "total"
	
}
