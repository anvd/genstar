package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	}
	
	
	public List<AttributeValuesFrequency> getMatchingAttributeValuesFrequencies(final Map<AbstractAttribute, AttributeValue> matchingCriteria) {
		List<AttributeValuesFrequency> matchings = new ArrayList<AttributeValuesFrequency>();
		
		for (AttributeValuesFrequency f : avFrequencies) {
			if (f.matchAttributeValues(matchingCriteria)) { matchings.add(f); }
		}
		
		return matchings;
	}
	
	
	// TODO verify the "total"
	
}
