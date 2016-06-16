package ummisco.genstar.metamodel.sample_data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import idees.genstar.configuration.GSAttDataType;
import idees.genstar.datareader.exception.GenstarIllegalRangedData;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeFactory;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.util.GenstarUtils;

public class CompoundSampleData extends AbstractSampleData implements ICompoundSampleData {
	
	private ISampleData originalGroupSampleData;
	
	private ISampleData originalComponentSampleData;

	private AbstractAttribute groupIdAttributeOnGroupEntity;
	
	private AbstractAttribute groupIdAttributeOnComponentEntity;
	
	private Map<String, Integer> populationIDs;
	
	
	public CompoundSampleData(final ISampleData originalGroupSampleData, final ISampleData originalComponentSampleData, 
			final AbstractAttribute groupIdAttributeOnGroupEntity, final AbstractAttribute groupIdAttributeOnComponentEntity) throws GenstarException {
		
		// parameters validation
		if (originalGroupSampleData == null) { throw new GenstarException("Parameter groupSampleData can not be null"); }
		if (originalComponentSampleData == null) { throw new GenstarException("Parameter originalComponentSampleData can not be null"); }
		if (groupIdAttributeOnGroupEntity == null) { throw new GenstarException("Parameter groupIdAttributeOnGroupEntity can not be null"); }
		if (groupIdAttributeOnComponentEntity == null) { throw new GenstarException("Parameter groupIdAttributeOnComponentEntity can not be null"); }
		
		
		this.originalGroupSampleData = originalGroupSampleData;
		this.originalComponentSampleData = originalComponentSampleData;
		
		// TODO verify that groupIdAttributeOnGroupEntity and groupIdAttributeOnComponentEntity belong to the corresponding sample data
		
		this.groupIdAttributeOnGroupEntity = groupIdAttributeOnGroupEntity;
		this.groupIdAttributeOnComponentEntity = groupIdAttributeOnComponentEntity;
		
		sampleEntityPopulation = GenstarUtils.loadCompoundPopulation(PopulationType.SAMPLE_DATA_POPULATION, originalGroupSampleData.getSampleEntityPopulation(), 
				originalComponentSampleData.getSampleEntityPopulation(), groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity);
	}
	
	public void recodeIdAttributes(final Entity targetGroupEntity) 
			throws GenstarException, GenstarIllegalRangedData {
		String targetEntityPopulationName = targetGroupEntity.getPopulation().getName();
		AttributeFactory af = new AttributeFactory();
		
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		if (targetEntityPopulationName.equals(originalGroupSampleData.getSampleEntityPopulation().getName())) { // targetEntity.getPopulation().isCompatible(groupSampleData.getSampleEntityPopulation())
			EntityAttributeValue groupIdOnGroupEav = targetGroupEntity.getEntityAttributeValue(groupIdAttributeOnGroupEntity);
			
			if (groupIdOnGroupEav == null) { throw new GenstarException("No entity attribute value found for " + groupIdAttributeOnGroupEntity.getNameOnEntity() + " attribute on " + originalGroupSampleData.getSampleEntityPopulation().getName() + " population."); }
			
			AttributeValue groupIdValueOnGroup = groupIdOnGroupEav.getAttributeValueOnEntity(); // what to to with this value?
			
			int recodedID = nextIdValue(targetEntityPopulationName);
			AttributeValue recodedIdValue = af.createValue(GSAttDataType.unique, DataType.INTEGER, Arrays.asList(Integer.toString(recodedID)), groupIdAttributeOnGroupEntity);
			attributeValues.clear();
			attributeValues.put(groupIdAttributeOnGroupEntity, recodedIdValue);
			
			targetGroupEntity.setAttributeValuesOnEntity(attributeValues); // recode ID of group entity
			
			// recode ID of component entities
			IPopulation componentPopulation = targetGroupEntity.getComponentPopulation(originalComponentSampleData.getSampleEntityPopulation().getName());
			if (componentPopulation != null) {
				for (Entity componentEntity : componentPopulation.getEntities()) {
					EntityAttributeValue groupIdOnComponentEav = componentEntity.getEntityAttributeValue(groupIdAttributeOnComponentEntity);
					if (groupIdOnComponentEav == null) { throw new GenstarException("No entity attribute value found for " + groupIdAttributeOnComponentEntity.getNameOnData() + " attribute on " + originalComponentSampleData.getSampleEntityPopulation().getName() + " population."); }
					
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
	
	@Override
	public ISampleData getOriginalGroupSampleData() {
		return originalGroupSampleData;
	}

	@Override
	public ISampleData getOriginalComponentSampleData() {
		return originalComponentSampleData;
	}

	@Override
	public AbstractAttribute getGroupIdAttributeOnGroupEntity() {
		return groupIdAttributeOnGroupEntity;
	}

	@Override
	public AbstractAttribute getGroupIdAttributeOnComponentEntity() {
		return groupIdAttributeOnComponentEntity;
	}
}
