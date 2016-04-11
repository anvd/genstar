package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.IPopulation;
import ummisco.genstar.metamodel.Population;
import ummisco.genstar.metamodel.PopulationType;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;

public class GroupComponentSampleData extends AbstractSampleData implements ISampleData {
	
	private ISampleData groupSampleData;
	
	private ISampleData componentSampleData;

	private AbstractAttribute groupIdAttributeOnGroupEntity;
	
	private AbstractAttribute groupIdAttributeOnComponentEntity;
	
//	private SampleEntityPopulation sampleEntityPopulation;
	private IPopulation sampleEntityPopulation;
	
	private Map<String, Integer> populationIDs;
	
	
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
		sampleEntityPopulation = new Population(PopulationType.SAMPLE_DATA_POPULATION, groupSampleData.getSampleEntityPopulation().getName(), groupSampleData.getSampleEntityPopulation().getAttributes());
		sampleEntityPopulation.addGroupReferences(groupSampleData.getGroupReferences());
		sampleEntityPopulation.addComponentReferences(groupSampleData.getComponentReferences());
		
		List<Entity> groupSampleEntities = groupSampleData.getSampleEntityPopulation().getEntities();
		List<Entity> componentSampleEntities = componentSampleData.getSampleEntityPopulation().getEntities();
		
		Entity complexEntity;
		EntityAttributeValue groupIdEntityAttributeValue;
		AttributeValue groupIdAttributeValueOnGroupEntity;
		
		List<Entity> copyComponentSampleEntities = new ArrayList<Entity>(componentSampleEntities);
		
		for (Entity groupEntity : groupSampleEntities) {
//			complexEntity = sampleEntityPopulation.createSampleEntity(groupEntity.getAttributeValuesOnEntity());
			complexEntity = sampleEntityPopulation.createEntity(groupEntity.getEntityAttributeValues());
			
			groupIdEntityAttributeValue = complexEntity.getEntityAttributeValue(groupIdAttributeOnGroupEntity);
			if (groupIdEntityAttributeValue == null) { throw new GenstarException("groupEntity doesn't contain " + groupIdAttributeOnGroupEntity.getNameOnEntity() + " as ID attribute"); }
			
			groupIdAttributeValueOnGroupEntity = groupIdEntityAttributeValue.getAttributeValueOnEntity();
			
			Map<AbstractAttribute, AttributeValue> componentMatchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
			componentMatchingCriteria.put(groupIdAttributeOnComponentEntity, groupIdAttributeValueOnGroupEntity);
			
			List<Entity> matchedComponentEntities = new ArrayList<Entity>();
			for (Entity componentEntity : copyComponentSampleEntities) {
				if (componentEntity.matchAttributeValuesOnEntity(componentMatchingCriteria)) { matchedComponentEntities.add(componentEntity);  }
			}
			
			// create component sample entities
			if (!matchedComponentEntities.isEmpty()) {
				List<List<EntityAttributeValue>> componentSampleEntityAVs = new ArrayList<List<EntityAttributeValue>>();
				for (Entity matchedComEntity : matchedComponentEntities) { componentSampleEntityAVs.add(matchedComEntity.getEntityAttributeValues()); }
				
//				SampleEntityPopulation componentPopulation = complexEntity.createComponentPopulation(componentSampleData.getSampleEntityPopulation().getName(), componentSampleData.getSampleEntityPopulation().getAttributes());
				IPopulation componentPopulation = complexEntity.createComponentPopulation(componentSampleData.getSampleEntityPopulation().getName(), componentSampleData.getSampleEntityPopulation().getAttributes());
				componentPopulation.addGroupReferences(componentSampleData.getGroupReferences());
				componentPopulation.addComponentReferences(componentSampleData.getComponentReferences());
				
//				componentPopulation.createSampleEntities(componentSampleEntityAVs);
				componentPopulation.createEntities(componentSampleEntityAVs);
			}
			
			copyComponentSampleEntities.removeAll(matchedComponentEntities);
		}
	}

		
	/*
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
			
			groupIdAttributeValueOnGroupEntity = complexEntity.getAttributeValueOnEntity(groupIdAttributeOnGroupEntity.getNameOnEntity());
			if (groupIdAttributeValueOnGroupEntity == null) { throw new GenstarException("groupEntity doesn't contain " + groupIdAttributeOnGroupEntity.getNameOnEntity() + " as ID attribute"); }
			
			Map<String, AttributeValue> componentMatchingCriteria = new HashMap<String, AttributeValue>();
			componentMatchingCriteria.put(groupIdAttributeOnComponentEntity.getNameOnEntity(), groupIdAttributeValueOnGroupEntity);
			
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
	*/
		
	@Override
	public IPopulation getSampleEntityPopulation() {
		return sampleEntityPopulation;
	}
//	@Override
//	public SampleEntityPopulation getSampleEntityPopulation() {
//		return sampleEntityPopulation;
//	}

	public void recodeIdAttributes(final Entity targetEntity) throws GenstarException {
		String targetEntityPopulationName = targetEntity.getPopulation().getName();
		
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		if (targetEntityPopulationName.equals(groupSampleData.getSampleEntityPopulation().getName())) {
			EntityAttributeValue eav = targetEntity.getEntityAttributeValue(groupIdAttributeOnGroupEntity);
			
			if (eav == null) { throw new GenstarException("No entity attribute value found for " + groupIdAttributeOnGroupEntity.getNameOnEntity() + " attribute on " + groupSampleData.getSampleEntityPopulation().getName() + " population."); }
			
			AttributeValue groupIdValueOnGroup = eav.getAttributeValueOnEntity(); // what to to with this value?
			
			int recodedID = nextIdValue(targetEntityPopulationName);
			AttributeValue recodedIdValue = new UniqueValue(DataType.INTEGER, Integer.toString(recodedID));
			attributeValues.clear();
			attributeValues.put(groupIdAttributeOnGroupEntity, recodedIdValue);
			
			targetEntity.setAttributeValuesOnEntity(attributeValues); // recode ID of group entity
			
			// recode ID of component entities
			IPopulation componentPopulation = targetEntity.getComponentPopulation(componentSampleData.getSampleEntityPopulation().getName());
			if (componentPopulation != null) {
				for (Entity componentEntity : componentPopulation.getEntities()) {
					EntityAttributeValue componentEav = componentEntity.getEntityAttributeValue(groupIdAttributeOnComponentEntity);
					if (componentEav == null) { throw new GenstarException("No entity attribute value found for " + groupIdAttributeOnComponentEntity.getNameOnData() + " attribute on " + componentSampleData.getSampleEntityPopulation().getName() + " population."); }
					
					AttributeValue groupIdValueOnComponent = componentEav.getAttributeValueOnEntity(); // what to to with this value?
					
					attributeValues.clear();
					attributeValues.put(groupIdAttributeOnComponentEntity, recodedIdValue);
					
					componentEntity.setAttributeValuesOnEntity(attributeValues);
				}
			}
			
		} else {
			throw new GenstarException("Unrecognized population of targetEntity: " + targetEntity.getPopulation().getName());
		}
		
	}

		
//	@Override
//	public void recodeIdAttributes(final SampleEntity targetEntity) throws GenstarException {
//		String targetEntityPopulationName = targetEntity.getPopulation().getName();
//		
//		Map<String, AttributeValue> attributeValues = new HashMap<String, AttributeValue>();
//		if (targetEntityPopulationName.equals(groupSampleData.getSampleEntityPopulation().getName())) {
//			AttributeValue groupIdValueOnGroup = targetEntity.getAttributeValueOnEntity(groupIdAttributeOnGroupEntity.getNameOnEntity());
//			if (groupIdValueOnGroup == null) { throw new GenstarException("No attribute value found for " + groupIdAttributeOnGroupEntity.getNameOnEntity() + " attribute on " + groupSampleData.getSampleEntityPopulation().getName() + " name."); }
//			
//			int recodedID = nextIdValue(targetEntityPopulationName);
//			AttributeValue recodedIdValue = new UniqueValue(DataType.INTEGER, Integer.toString(recodedID));
//			attributeValues.clear();
//			attributeValues.put(groupIdAttributeOnGroupEntity.getNameOnEntity(), recodedIdValue);
//			
//			targetEntity.setAttributeValuesOnEntity(attributeValues); // recode ID of group entity
//			
//			// recode ID of component entities
//			SampleEntityPopulation componentPopulation = targetEntity.getComponentPopulation(componentSampleData.getSampleEntityPopulation().getName());
//			if (componentPopulation != null) {
//				for (SampleEntity componentEntity : componentPopulation.getSampleEntities()) {
//					AttributeValue groupIdValueOnComponent = componentEntity.getAttributeValueOnEntity(groupIdAttributeOnComponentEntity.getNameOnEntity());
//					if (groupIdValueOnComponent == null) {}
//					
//					attributeValues.clear();
//					attributeValues.put(groupIdAttributeOnComponentEntity.getNameOnEntity(), recodedIdValue);
//					
//					componentEntity.setAttributeValuesOnEntity(attributeValues);
//				}
//			}
//			
//		} else {
//			throw new GenstarException("Unrecognized population of targetEntity: " + targetEntity.getPopulation().getName());
//		}
//	}
	
	private int nextIdValue(final String populationName) throws GenstarException {
		if (populationName == null || populationName.isEmpty()) { throw new GenstarException("Parameter populationName can be neither null nor empty"); }
		if (populationIDs == null) { populationIDs = new HashMap<String, Integer>(); }
		
		Integer nextId = populationIDs.get(populationName);
		if (nextId == null) { nextId = 0; }
		populationIDs.put(populationName, nextId + 1);

		return nextId;
	}
	
	public String getGroupPopulationName() {
		return groupSampleData.getPopulationName();
	}
	
	public String getComponentPopulationName() {
		return componentSampleData.getPopulationName();
	}
	
	
}
