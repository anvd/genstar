package ummisco.genstar.ipu;

import java.util.ArrayList;
import java.util.List;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.util.IpuUtils;

public class IpuControlTotals {

	private List<AttributeValuesFrequency> groupAvFrequencies;
	
	private List<AttributeValuesFrequency> componentAvFrequencies;

	
	public IpuControlTotals(final IpuGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' parameter can not be null"); }
		
		groupAvFrequencies = IpuUtils.parseIpuControlTotalsFile(generationRule.getGenerator(), generationRule.getGroupControlledAttributes(), generationRule.getGroupControlTotalsFile());
		componentAvFrequencies = IpuUtils.parseIpuControlTotalsFile(generationRule.getComponentGenerator(), generationRule.getComponentControlledAttributes(), generationRule.getComponentControlTotalsFile());
	}
	
	public int getGroupTypes() {
		return groupAvFrequencies.size();
	}
	
	public int getComponentTypes() {
		return componentAvFrequencies.size();
	}
	
	public List<AttributeValuesFrequency> getGroupAttributesFrequencies() {
		return new ArrayList<AttributeValuesFrequency>(groupAvFrequencies);
	}
	
	public List<AttributeValuesFrequency> getComponentAttributesFrequencies() {
		return new ArrayList<AttributeValuesFrequency>(componentAvFrequencies);
	}
	
/*
	public List<AttributeValuesFrequency> getMatchingAttributeValuesFrequencies(final Map<AbstractAttribute, AttributeValue> matchingCriteria) {
		List<AttributeValuesFrequency> matchings = new ArrayList<AttributeValuesFrequency>();
		
		for (AttributeValuesFrequency f : avFrequencies) {
			if (f.matchAttributeValues(matchingCriteria)) { matchings.add(f); }
		}
		
		return matchings;
	}
 */
	
	
}
