package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.IpfUtils;


public class IpfControlTotals {
	
	private GenstarCsvFile data;
	
	private List<AbstractAttribute> controlledAttributes;
	
	private List<AttributeValuesFrequency> avFrequencies;

	
	public IpfControlTotals(final SampleDataGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' parameter can not be null"); }
		
		this.data = generationRule.getControlTotalsFile();
		this.controlledAttributes = generationRule.getControlledAttributes();
		this.avFrequencies = IpfUtils.parseIpfControlTotalsFile(this.data, this.controlledAttributes);
	}
	
	
	public List<AttributeValuesFrequency> getMatchingAttributeValuesFrequencies(final Map<AbstractAttribute, AttributeValue> matchingCriteria) {
		List<AttributeValuesFrequency> matchings = new ArrayList<AttributeValuesFrequency>();
		
		for (AttributeValuesFrequency f : avFrequencies) {
			if (f.matchAttributeValuesOnData(matchingCriteria)) { matchings.add(f); }
		}
		
		return matchings;
	}
	
}
