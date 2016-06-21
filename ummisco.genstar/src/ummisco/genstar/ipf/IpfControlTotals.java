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
	
	private GenstarCsvFile ipfControlTotalsFile;
	
	private List<AbstractAttribute> controlledAttributes;
	
	private List<AttributeValuesFrequency> avFrequencies;

	
	public IpfControlTotals(final IpfGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' parameter can not be null"); }
		
		this.ipfControlTotalsFile = generationRule.getControlTotalsFile();
		this.controlledAttributes = new ArrayList<AbstractAttribute>(generationRule.getControlledAttributes());
		this.avFrequencies = IpfUtils.parseAttributeValuesFrequenciesFromIpfControlTotalsFile(this.ipfControlTotalsFile, this.controlledAttributes);
	}
	
	
	public List<AttributeValuesFrequency> getMatchingAttributeValuesFrequencies(final Map<AbstractAttribute, AttributeValue> matchingCriteria) {
		List<AttributeValuesFrequency> matchings = new ArrayList<AttributeValuesFrequency>();
		
		for (AttributeValuesFrequency f : avFrequencies) {
			if (f.matchAttributeValuesOnData(matchingCriteria)) { matchings.add(f); }
		}
		
		return matchings;
	}
	
}
