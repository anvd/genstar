package ummisco.genstar.ipf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;

public abstract class AbstractSampleData implements ISampleData {
	
	protected String populationName;

	private Map<String, String> groupReferences = Collections.EMPTY_MAP; // population_name :: GAMA_attribute_name
	
	private Map<String, String> componentReferences = Collections.EMPTY_MAP; // population_name :: GAMA_attribute_name

	
	public void addGroupReferences(final Map<String, String> groupReferences) throws GenstarException {
		if (groupReferences == null) { throw new GenstarException("Parameter groupReferences can not be null"); }
		
		if (this.groupReferences == Collections.EMPTY_MAP) { this.groupReferences = new HashMap<String, String>(); }
		this.groupReferences.putAll(groupReferences);
	}
	
	public Map<String, String> getGroupReferences() {
		return new HashMap<String, String>(groupReferences);
	}
	
	public String getGroupReference(final String populationName) {
		return groupReferences.get(populationName);
	}
	
	public void addGroupReference(final String populationName, final String referenceAttribute) throws GenstarException {
		if (populationName == null || referenceAttribute == null) { throw new GenstarException("Parameters populationName, referenceAttribute can not be null"); }
		
		if (groupReferences == Collections.EMPTY_MAP) { groupReferences = new HashMap<String, String>(); }
		groupReferences.put(populationName, referenceAttribute);
	}

	public void addComponentReferences(final Map<String, String> componentReferences) throws GenstarException {
		if (componentReferences == null) { throw new GenstarException("Parameter componentReferences can not be null"); }
		
		if (this.componentReferences == Collections.EMPTY_MAP) { this.componentReferences = new HashMap<String, String>(); }
		this.componentReferences.putAll(componentReferences);
	}

	public Map<String, String> getComponentReferences() {
		return new HashMap<String, String>(componentReferences);
	}

	public String getComponentReference(final String populationName) {
		return componentReferences.get(populationName);
	}

	public void addComponentReference(final String populationName, final String referenceAttribute) throws GenstarException {
		if (populationName == null || referenceAttribute == null) { throw new GenstarException("Parameters populationName, referenceAttribute can not be null"); }
		
		if (componentReferences == Collections.EMPTY_MAP) { componentReferences = new HashMap<String, String>(); }
		componentReferences.put(populationName, referenceAttribute);
	}

	@Override
	public String getPopulationName() {
		return populationName;
	}
}
