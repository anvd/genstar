package ummisco.genstar.metamodel.sample_data;

import ummisco.genstar.metamodel.attributes.AbstractAttribute;

public interface ICompoundSampleData {

	public abstract ISampleData getOriginalGroupSampleData();
	
	public abstract ISampleData getOriginalComponentSampleData();
	
	public AbstractAttribute getGroupIdAttributeOnGroupEntity();
	
	public AbstractAttribute getGroupIdAttributeOnComponentEntity();
}
