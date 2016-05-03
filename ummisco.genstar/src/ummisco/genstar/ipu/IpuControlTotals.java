package ummisco.genstar.ipu;

import java.util.ArrayList;
import java.util.List;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.util.IpuUtils;

public class IpuControlTotals {

	private List<AttributeValuesFrequency> groupTypeConstraints;
	
	private List<AttributeValuesFrequency> componentTypeConstraints;

	
	public IpuControlTotals(final IpuGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' parameter can not be null"); }
		
		groupTypeConstraints = IpuUtils.parseAttributeValuesFrequenciesFromIpuControlTotalsFile(generationRule.getGroupControlledAttributes(), generationRule.getGroupControlTotalsFile());
		componentTypeConstraints = IpuUtils.parseAttributeValuesFrequenciesFromIpuControlTotalsFile(generationRule.getComponentControlledAttributes(), generationRule.getComponentControlTotalsFile());
	}
	
	public int getGroupTypes() {
		return groupTypeConstraints.size();
	}
	
	public int getComponentTypes() {
		return componentTypeConstraints.size();
	}
	
	public List<AttributeValuesFrequency> getGroupTypeConstraints() {
		return new ArrayList<AttributeValuesFrequency>(groupTypeConstraints);
	}
	
	public List<AttributeValuesFrequency> getComponentTypeConstraints() {
		return new ArrayList<AttributeValuesFrequency>(componentTypeConstraints);
	}
	
}
