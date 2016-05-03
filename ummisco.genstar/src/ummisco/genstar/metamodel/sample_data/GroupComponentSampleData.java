package ummisco.genstar.metamodel.sample_data;

import java.util.HashMap;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.AbstractSampleData;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.IPopulation;
import ummisco.genstar.metamodel.PopulationType;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.util.GenstarUtils;

public class GroupComponentSampleData extends AbstractSampleData implements ISampleData {
	
	private ISampleData groupSampleData;
	
	private ISampleData componentSampleData;

	private AbstractAttribute groupIdAttributeOnGroupEntity;
	
	private AbstractAttribute groupIdAttributeOnComponentEntity;
	
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
		
		// TODO verify that groupIdAttributeOnGroupEntity and groupIdAttributeOnComponentEntity belong to the corresponding sample data
		
		// ?? duplicate with group-component references
		this.groupIdAttributeOnGroupEntity = groupIdAttributeOnGroupEntity;
		this.groupIdAttributeOnComponentEntity = groupIdAttributeOnComponentEntity;
		
		sampleEntityPopulation = GenstarUtils.loadCompoundPopulation(PopulationType.SAMPLE_DATA_POPULATION, groupSampleData.getSampleEntityPopulation(), 
				componentSampleData.getSampleEntityPopulation(), groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity);
	}
	
		
	@Override
	public IPopulation getSampleEntityPopulation() {
		return sampleEntityPopulation;
	}

	public void recodeIdAttributes(final Entity targetGroupEntity) throws GenstarException {
		String targetEntityPopulationName = targetGroupEntity.getPopulation().getName();
		
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		if (targetEntityPopulationName.equals(groupSampleData.getSampleEntityPopulation().getName())) { // targetEntity.getPopulation().isCompatible(groupSampleData.getSampleEntityPopulation())
			EntityAttributeValue groupIdOnGroupEav = targetGroupEntity.getEntityAttributeValue(groupIdAttributeOnGroupEntity);
			
			if (groupIdOnGroupEav == null) { throw new GenstarException("No entity attribute value found for " + groupIdAttributeOnGroupEntity.getNameOnEntity() + " attribute on " + groupSampleData.getSampleEntityPopulation().getName() + " population."); }
			
			AttributeValue groupIdValueOnGroup = groupIdOnGroupEav.getAttributeValueOnEntity(); // what to to with this value?
			
			int recodedID = nextIdValue(targetEntityPopulationName);
			AttributeValue recodedIdValue = new UniqueValue(DataType.INTEGER, Integer.toString(recodedID));
			attributeValues.clear();
			attributeValues.put(groupIdAttributeOnGroupEntity, recodedIdValue);
			
			targetGroupEntity.setAttributeValuesOnEntity(attributeValues); // recode ID of group entity
			
			// recode ID of component entities
			IPopulation componentPopulation = targetGroupEntity.getComponentPopulation(componentSampleData.getSampleEntityPopulation().getName());
			if (componentPopulation != null) {
				for (Entity componentEntity : componentPopulation.getEntities()) {
					EntityAttributeValue groupIdOnComponentEav = componentEntity.getEntityAttributeValue(groupIdAttributeOnComponentEntity);
					if (groupIdOnComponentEav == null) { throw new GenstarException("No entity attribute value found for " + groupIdAttributeOnComponentEntity.getNameOnData() + " attribute on " + componentSampleData.getSampleEntityPopulation().getName() + " population."); }
					
					AttributeValue groupIdValueOnComponent = groupIdOnComponentEav.getAttributeValueOnEntity(); // what to to with this value?
					
					attributeValues.clear();
					attributeValues.put(groupIdAttributeOnComponentEntity, recodedIdValue);
					
					componentEntity.setAttributeValuesOnEntity(attributeValues);
				}
			}
			
		} else {
			throw new GenstarException("Unrecognized population of targetEntity: " + targetGroupEntity.getPopulation().getName());
		}		
	}

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
