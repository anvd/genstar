package ummisco.genstar.ipf;

import java.util.List;
import java.util.Map;

import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public interface ISampleData {

	public abstract int countMatchingEntities(final Map<AbstractAttribute, AttributeValue> matchingCriteria);
	
	public abstract List<SampleEntity> getMatchingEntities(final Map<AbstractAttribute, AttributeValue> matchingCriteria);
	
	public abstract List<SampleEntity> getSampleEntities();
}
