package ummisco.genstar.ipf;

import java.util.Map;

import ummisco.genstar.exception.GenstarException;

public interface ISampleData {
	public abstract SampleEntityPopulation getSampleEntityPopulation();

	public abstract void addGroupReferences(final Map<String, String> groupReferences) throws GenstarException;
	
	public abstract Map<String, String> getGroupReferences();
	
	public abstract String getGroupReference(final String populationName);
	
	public abstract void addGroupReference(final String populationName, final String referenceAttribute) throws GenstarException;

	public abstract void addComponentReferences(final Map<String, String> componentReferences) throws GenstarException;

	public abstract Map<String, String> getComponentReferences();

	public abstract String getComponentReference(final String populationName);

	public abstract void addComponentReference(final String populationName, final String referenceAttribute) throws GenstarException;

	public abstract void recodeIdAttributes(final SampleEntity targetEntity) throws GenstarException;
	
	public abstract String getPopulationName();
}
