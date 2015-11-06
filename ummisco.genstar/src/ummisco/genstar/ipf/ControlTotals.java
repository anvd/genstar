package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.AttributeValuesFrequency;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.util.GenstarCSVFile;


public class ControlTotals {
	
	private ISyntheticPopulationGenerator generator;
	
	private GenstarCSVFile data;
	
	private List<AttributeValuesFrequency> avFrequencies;
	
	private int total = 0;

	
	public ControlTotals(final SampleDataGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' parameter can not be null"); }
		
		this.generator = generationRule.getGenerator();
		this.data = generationRule.getControlTotalsFile();
		parseAttributeValuesFrequency();
	}
	
	
	private void parseAttributeValuesFrequency() throws GenstarException {
		avFrequencies = new ArrayList<AttributeValuesFrequency>();
		
		AbstractAttribute attribute;
		AttributeValue attributeValue;
		List<String> avStrings = new ArrayList<String>();
		int line = 1;
		Map<AbstractAttribute, AttributeValue> attributeValues;
		for (List<String> aRow : data.getContent()) {
			if (aRow.size() < 3 && (aRow.size() %2 != 1)) throw new GenstarException("Invalid attribute values frequency format. File: " + data.getFilePath() + ", line: " + line);
			line++;
			
			attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
			for (int col=0; col<(aRow.size() - 1); col+=2) { // Parse each line of the file
				// 1. parse the attribute name column
				attribute = generator.getAttribute(aRow.get(col));
				if (attribute == null) { throw new GenstarException("'" + aRow.get(col) + "' is not a valid attribute. File: " + data.getFilePath() + ", line: " + line + "."); }
				//if (!controlledAttributes.contains(attribute)) { throw new GenstarException("'" + aRow.get(col) + "' is not a controlled attribute."); }
				if (attributeValues.containsKey(attribute)) { throw new GenstarException("Duplicated attribute : '" + attribute.getNameOnData() + "'"); }
				
				// 2. parse the attribute value column
				avStrings.clear();
				avStrings.add(aRow.get(col+1));
				attributeValue = attribute.findCorrespondingAttributeValue(avStrings);
				if (attributeValue == null) { throw new GenstarException("Attribute value '" + aRow.get(col+1) + "' not found."); }
				
				attributeValues.put(attribute, attributeValue);
			}
			
			// "frequency" is the last column
			int frequency = Integer.parseInt(aRow.get(aRow.size() - 1));
			avFrequencies.add(new AttributeValuesFrequency(attributeValues, frequency));
			total += frequency;
		}
	}
	
	public List<AttributeValuesFrequency> getMatchingAttributeValuesFrequencies(final Map<AbstractAttribute, AttributeValue> matchingCriteria) {
		List<AttributeValuesFrequency> matchings = new ArrayList<AttributeValuesFrequency>();
		
		for (AttributeValuesFrequency f : avFrequencies) {
			if (f.isMatch(matchingCriteria)) { matchings.add(f); }
		}
		
		return matchings;
	}
	
	public int getTotal() {
		return total;
	}
}
