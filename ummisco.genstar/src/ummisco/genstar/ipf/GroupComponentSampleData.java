package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class GroupComponentSampleData implements ISampleData {
	
	private ISampleData groupSampleData;
	
	private ISampleData componentSampleData;

	private AbstractAttribute groupIdAttributeOnGroupEntity;
	
	private AbstractAttribute groupIdAttributeOnComponentEntity;
	
	private List<SampleEntity> complexSampleEntities;
	
	
	public GroupComponentSampleData(final ISampleData groupSampleData, final ISampleData componentSampleData, 
			final AbstractAttribute groupIdAttributeOnGroupEntity, final AbstractAttribute groupIdAttributeOnComponentEntity) throws GenstarException {
		
		// parameters validation
		if (groupSampleData == null) { throw new GenstarException("Parameter groupSampleData can not be null"); }
		if (componentSampleData == null) { throw new GenstarException("Parameter componentSampleData can not be null"); }
		if (groupIdAttributeOnGroupEntity == null) { throw new GenstarException("Parameter groupIdAttributeOnGroupEntity can not be null"); }
		if (groupIdAttributeOnComponentEntity == null) { throw new GenstarException("Parameter groupIdAttributeOnComponentEntity can not be null"); }
		
		
		this.groupSampleData = groupSampleData;
		this.componentSampleData = componentSampleData;
		
		this.groupIdAttributeOnGroupEntity = groupIdAttributeOnGroupEntity;
		this.groupIdAttributeOnComponentEntity = groupIdAttributeOnComponentEntity;
		
		buildGroupComponentSampleEntities();
	}
	
	private void buildGroupComponentSampleEntities() throws GenstarException {
		
		List<SampleEntity> groupSampleEntities = groupSampleData.getSampleEntities();
		List<SampleEntity> componentSampleEntities = componentSampleData.getSampleEntities();
		
		SampleEntity complexEntity;
		AttributeValue groupIdAttributeValueOnGroupEntity;
		complexSampleEntities = new ArrayList<SampleEntity>();
		
		List<SampleEntity> copyComponentSampleEntities = new ArrayList<SampleEntity>(componentSampleEntities);
		
		for (SampleEntity groupEntity : groupSampleEntities) {
			complexEntity = new SampleEntity(groupEntity.getAttributeValues());
			complexSampleEntities.add(complexEntity);
			
			groupIdAttributeValueOnGroupEntity = complexEntity.getAttributeValue(groupIdAttributeOnGroupEntity);
			if (groupIdAttributeValueOnGroupEntity == null) { throw new GenstarException("groupEntity doesn't contain " + groupIdAttributeOnGroupEntity.getNameOnData() + " as ID attribute"); }
			
			Map<AbstractAttribute, AttributeValue> componentMatchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
			componentMatchingCriteria.put(groupIdAttributeOnComponentEntity, groupIdAttributeValueOnGroupEntity);
			
			List<SampleEntity> matchedComponentEntities = new ArrayList<SampleEntity>();
			for (SampleEntity componentEntity : copyComponentSampleEntities) {
				if (componentEntity.isMatch(componentMatchingCriteria)) { matchedComponentEntities.add(componentEntity);  }
			}
			
			complexEntity.addMembers(matchedComponentEntities);
			copyComponentSampleEntities.removeAll(matchedComponentEntities);
		}
	}
	
	@Override
	public int countMatchingEntities(final Map<AbstractAttribute, AttributeValue> matchingCriteria) {
		int matchingIndividuals = 0;
		for (SampleEntity se : complexSampleEntities) { if (se.isMatch(matchingCriteria)) { matchingIndividuals++; } }
		
		return matchingIndividuals;
	}
	
	@Override
	public List<SampleEntity> getMatchingEntities(final Map<AbstractAttribute, AttributeValue> matchingCriteria) {
		List<SampleEntity> matchings = new ArrayList<SampleEntity>();
		for (SampleEntity se : complexSampleEntities) { if (se.isMatch(matchingCriteria)) { matchings.add(se); } }
		
		return matchings;
	}

	@Override
	public List<SampleEntity> getSampleEntities() {
		return complexSampleEntities;
	}
}
