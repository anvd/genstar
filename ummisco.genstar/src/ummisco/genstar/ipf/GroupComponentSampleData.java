package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;

public class GroupComponentSampleData extends AbstractSampleData implements ISampleData {
	
	private ISampleData groupSampleData;
	
	private ISampleData componentSampleData;

	private AbstractAttribute groupIdAttributeOnGroupEntity;
	
	private AbstractAttribute groupIdAttributeOnComponentEntity;
	
	private SampleEntityPopulation sampleEntityPopulation;
	
	
	public GroupComponentSampleData(final ISampleData groupSampleData, final ISampleData componentSampleData, 
			final AbstractAttribute groupIdAttributeOnGroupEntity, final AbstractAttribute groupIdAttributeOnComponentEntity) throws GenstarException {
		
		// parameters validation
		if (groupSampleData == null) { throw new GenstarException("Parameter groupSampleData can not be null"); }
		if (componentSampleData == null) { throw new GenstarException("Parameter componentSampleData can not be null"); }
		if (groupIdAttributeOnGroupEntity == null) { throw new GenstarException("Parameter groupIdAttributeOnGroupEntity can not be null"); }
		if (groupIdAttributeOnComponentEntity == null) { throw new GenstarException("Parameter groupIdAttributeOnComponentEntity can not be null"); }
		
		
		this.groupSampleData = groupSampleData;
		this.componentSampleData = componentSampleData;
		
		// ?? duplicate with group-component references
		this.groupIdAttributeOnGroupEntity = groupIdAttributeOnGroupEntity;
		this.groupIdAttributeOnComponentEntity = groupIdAttributeOnComponentEntity;
		
		buildGroupComponentSampleEntities();
	}
	
	private void buildGroupComponentSampleEntities() throws GenstarException {
		
		sampleEntityPopulation = new SampleEntityPopulation(groupSampleData.getSampleEntityPopulation().getName(), groupSampleData.getSampleEntityPopulation().getAttributes());
		sampleEntityPopulation.addGroupReferences(groupSampleData.getGroupReferences());
		sampleEntityPopulation.addComponentReferences(groupSampleData.getComponentReferences());
		
		List<SampleEntity> groupSampleEntities = groupSampleData.getSampleEntityPopulation().getSampleEntities();
		List<SampleEntity> componentSampleEntities = componentSampleData.getSampleEntityPopulation().getSampleEntities();
		
		SampleEntity complexEntity;
		AttributeValue groupIdAttributeValueOnGroupEntity;
		
		List<SampleEntity> copyComponentSampleEntities = new ArrayList<SampleEntity>(componentSampleEntities);
		
		for (SampleEntity groupEntity : groupSampleEntities) {
			complexEntity = sampleEntityPopulation.createSampleEntity(groupEntity.getAttributeValuesOnEntity());
			
			groupIdAttributeValueOnGroupEntity = complexEntity.getAttributeValueOnEntity(groupIdAttributeOnGroupEntity.getNameOnData());
			if (groupIdAttributeValueOnGroupEntity == null) { throw new GenstarException("groupEntity doesn't contain " + groupIdAttributeOnGroupEntity.getNameOnData() + " as ID attribute"); }
			
			Map<String, AttributeValue> componentMatchingCriteria = new HashMap<String, AttributeValue>();
			componentMatchingCriteria.put(groupIdAttributeOnComponentEntity.getNameOnData(), groupIdAttributeValueOnGroupEntity);
			
			List<SampleEntity> matchedComponentEntities = new ArrayList<SampleEntity>();
			for (SampleEntity componentEntity : copyComponentSampleEntities) {
				if (componentEntity.isMatched(componentMatchingCriteria)) { matchedComponentEntities.add(componentEntity);  }
			}
			
			// create component sample entities
			if (!matchedComponentEntities.isEmpty()) {
				List<Map<String, AttributeValue>> componentSampleEntityAVs = new ArrayList<Map<String, AttributeValue>>();
				for (SampleEntity matchedComEntity : matchedComponentEntities) { componentSampleEntityAVs.add(matchedComEntity.getAttributeValuesOnEntity()); }
				
				SampleEntityPopulation componentPopulation = complexEntity.createComponentPopulation(componentSampleData.getSampleEntityPopulation().getName(), componentSampleData.getSampleEntityPopulation().getAttributes());
				componentPopulation.addGroupReferences(componentSampleData.getGroupReferences());
				componentPopulation.addComponentReferences(componentSampleData.getComponentReferences());
				
				componentPopulation.createSampleEntities(componentSampleEntityAVs);
			}
			
			copyComponentSampleEntities.removeAll(matchedComponentEntities);
		}
	}
		
	@Override
	public SampleEntityPopulation getSampleEntityPopulation() {
		return sampleEntityPopulation;
	}
}
