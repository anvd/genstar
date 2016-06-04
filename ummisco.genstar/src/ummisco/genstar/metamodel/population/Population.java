package ummisco.genstar.metamodel.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;

public class Population implements IPopulation {
	
	private PopulationType type;
	
	private List<AbstractAttribute> attributes;
	
	private String name;
	
	private List<Entity> entities = Collections.EMPTY_LIST;

	private Map<String, String> groupReferences = Collections.EMPTY_MAP; // population_name :: GAMA_attribute_name
	
	private Map<String, String> componentReferences = Collections.EMPTY_MAP; // population_name :: GAMA_attribute_name
	
//	private AbstractAttribute idAttribute;
	
//	private List<Integer> entityIds = Collections.EMPTY_LIST; 
	
//	private int currentMaxIdValue = 0;
	

	public Population(final PopulationType type, final String name, final List<AbstractAttribute> attributes) throws GenstarException {
		if (type == null) { throw new GenstarException("Parameter type can not be null"); }
		if (name == null) { throw new GenstarException("Parameter name can not be null"); }
		if (attributes == null) { throw new GenstarException("Parameter attributes can not be null"); }
		
		Set<AbstractAttribute> attributesSet = new HashSet<AbstractAttribute>(attributes);
		if (attributesSet.size() < attributes.size()) { throw new GenstarException("Some attributes are duplicated"); }
		
		this.type = type;
		this.name = name;
		this.attributes = new ArrayList<AbstractAttribute>();
		this.attributes.addAll(attributes);
		
		// save the reference to identity attribute
//		for (AbstractAttribute attr : attributes) {
//			if (attr.isIdentity()) {
//				if (idAttribute != null) {
//					throw new GenstarException("Not support more than one identity attribute in a population. Both " + idAttribute.getNameOnData() + " and " + attr.getNameOnData() + " are identity attributes.");
//				}
//				
//				idAttribute = attr;
//			}
//		}
	}
	
	@Override public int getNbOfEntities() {
		return entities.size();
	}
	
	@Override public List<Entity> getEntities() {
		return new ArrayList<Entity>(entities);
	}
	
	@Override public List<Entity> getMatchingEntitiesByAttributeValuesOnData(final Map<AbstractAttribute, AttributeValue> attributeValuesOnData) throws GenstarException {
		if (attributeValuesOnData == null) { throw new GenstarException("Parameter attributeValuesOnData can not be null"); }

		List<Entity> matchings = new ArrayList<Entity>();
		for (Entity e : entities) { if (e.matchAttributeValuesOnData(attributeValuesOnData)) { matchings.add(e); } }
		
		return matchings;
	}

	@Override public List<Entity> getMatchingEntitiesByAttributeValuesOnEntity(final Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity) throws GenstarException {
		if (attributeValuesOnEntity == null) { throw new GenstarException("Parameter attributeValuesOnEntity can not be null"); }
		
		List<Entity> matchings = new ArrayList<Entity>();
		for (Entity e : entities) { if (e.matchAttributeValuesOnEntity(attributeValuesOnEntity)) { matchings.add(e); } }
		
		return matchings;
	}
	
	@Override public int countMatchingEntitiesByAttributeValuesOnEntity(final Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity) throws GenstarException {
		if (attributeValuesOnEntity == null) { throw new GenstarException("Paramter matchingCriteria can not be null"); }

		int count = 0;
		for (Entity e : entities) { if (e.matchAttributeValuesOnEntity(attributeValuesOnEntity)) { count++; } }
		
		return count;
	}
	

	@Override public String getName() {
		return name;
	}

	@Override public List<AbstractAttribute> getAttributes() {
		return new ArrayList<AbstractAttribute>(attributes);
	}	
	
	@Override public boolean containAttribute(final AbstractAttribute attribute) throws GenstarException {
		return attributes.contains(attribute);
	}
	
	@Override public AbstractAttribute getAttributeByNameOnData(final String attributeNameOnData) throws GenstarException {
		if (attributeNameOnData  == null) { throw new GenstarException("Parameter attributeNameOnData can not be null"); }
		
		for (AbstractAttribute attr : attributes) {
			if (attr.getNameOnData().equals(attributeNameOnData)) { return attr; }
		}
		
		return null;
	}
	
	@Override public AbstractAttribute getAttributeByNameOnEntity(final String attributeNameOnEntity) throws GenstarException {
		if (attributeNameOnEntity  == null) { throw new GenstarException("Parameter attributeNameOnEntity can not be null"); }
		
		for (AbstractAttribute attr : attributes) {
			if (attr.getNameOnEntity().equals(attributeNameOnEntity)) { return attr; }
		}
		
		return null;
	}
	
	@Override public boolean isCompatible(final IPopulation otherPopulation) {
		if (otherPopulation == null) return false;
		if (!name.equals(otherPopulation.getName())) { return false; }
		
		List<AbstractAttribute> otherAttributes = otherPopulation.getAttributes();
		
		if (attributes.size() != otherAttributes.size()) { return false; }
		
		for (AbstractAttribute attr : attributes) { 
			if (!otherAttributes.contains(attr)) { return false; } 
		}
		
		return true;
	}
	
	@Override public List<Entity> createEntities(final int number) throws GenstarException {
		if (number < 0) throw new GenstarException("Parameter number can not be negative");
		
		if (entities == Collections.EMPTY_LIST) { entities = new ArrayList<Entity>(); }
		List<Entity> newlyCreated = new ArrayList<Entity>();
		for (int i=0; i<number; i++) { 
			Entity e = new Entity(this);
			internalAddEntity(e);
			newlyCreated.add(e);
		}
		
		return newlyCreated;
	}
	
	@Override public Entity createEntity(final List<EntityAttributeValue> entityAttributeValues) throws GenstarException {
		Entity e = new Entity(this);
		e.setEntityAttributeValues(entityAttributeValues);
		
		internalAddEntity(e);
		
		return e;
	}
	
	@Override public Entity createEntity(final Entity sourceEntity) throws GenstarException {
		return replicateEntity(sourceEntity, this);
	}
	
	
	private Entity replicateEntity(final Entity sourceEntity, final IPopulation targetPopulation) throws GenstarException {
		Entity replicatedEntity = targetPopulation.createEntity(sourceEntity.getEntityAttributeValues());
		
		List<IPopulation> sourceComponentPopulations = sourceEntity.getComponentPopulations();
		for (IPopulation sourceComponentPop : sourceComponentPopulations) {
			IPopulation tagetComponentSamplePopulation = replicatedEntity.createComponentPopulation(sourceComponentPop.getName(), sourceComponentPop.getAttributes());
			tagetComponentSamplePopulation.addGroupReferences(sourceComponentPop.getGroupReferences());
			tagetComponentSamplePopulation.addComponentReferences(sourceComponentPop.getComponentReferences());
			
			for (Entity sourceComponentEntity : sourceComponentPop.getEntities()) {
				replicateEntity(sourceComponentEntity, tagetComponentSamplePopulation);
			}
		}
		
		return replicatedEntity;
	}
	
	
	@Override public List<Entity> createEntities(final List<List<EntityAttributeValue>> entityAttributeValuesList) throws GenstarException {
		List<Entity> createdEntities = new ArrayList<Entity>();
		for (List<EntityAttributeValue> eavs : entityAttributeValuesList) { 
			createdEntities.add(this.createEntity(eavs));
		}
		
		return createdEntities;
	}

	
	@Override public Entity createEntityWithAttributeValuesOnEntity(final Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity) throws GenstarException {
		Entity e = new Entity(this);
		e.setAttributeValuesOnEntity(attributeValuesOnEntity);
		
		internalAddEntity(e);
		
		return e;
	}
	
	@Override public List<Entity> createEntitiesWithAttributeValuesOnEntities(final List<Map<AbstractAttribute, AttributeValue>> attributeValuesAttributeValueOnEntities) throws GenstarException {
		List<Entity> createdEntities = new ArrayList<Entity>();
		
		for (Map<AbstractAttribute, AttributeValue> av : attributeValuesAttributeValueOnEntities) {
			createdEntities.add(createEntityWithAttributeValuesOnEntity(av));
		}
		
		return createdEntities;
	}
	
	private void internalAddEntity(final Entity entity) throws GenstarException {
		if (entities == Collections.EMPTY_LIST) { entities = new ArrayList<Entity>(); }
		
		entities.add(entity);
		/*
		if (idAttribute != null) {
			if (!idAttribute.isIdentity()) {
				throw new GenstarException(idAttribute.getNameOnData() + " is no longer an identity attribute. This population object is no longer correctly functional.");
			}
			
			
			if (entityIds == Collections.EMPTY_LIST) { entityIds = new ArrayList<Integer>(); }
			
			EntityAttributeValue idEav = entity.getEntityAttributeValue(idAttribute);
			if (idEav == null) { throw new GenstarException("entity doesn't contain identity attribute value"); }

			int id = ((UniqueValue) idEav.getAttributeValueOnEntity()).getIntValue();
			if (entityIds.contains(id)) {
				throw new GenstarException("Duplicated identity attribute value: " + id + ". Population: " + name + ", identity attribute: " + idAttribute.getNameOnData());
			}
			
			entities.add(entity);
			entityIds.add(id);
		} else {
			entities.add(entity);
		}
		*/
	}

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
	
	@Override public PopulationType getPopulationType() {
		return type;
	}

//	@Override public boolean isIdValueAlreadyInUsed(final int idValue) throws GenstarException {
//		if (idAttribute == null) { throw new GenstarException("This population has no identity attribute"); }
//		if (!idAttribute.isIdentity()) {
//			throw new GenstarException(idAttribute.getNameOnData() + " is no longer an identity attribute. This population object is no longer correctly functional.");
//		}
//
//		if (idValue < 0) { throw new GenstarException("Not support negative identity value"); }
//		
//		return (entityIds.contains(idValue));
//	}
//
//	@Override public int nextIdValue() throws GenstarException {
//		if (idAttribute == null) { throw new GenstarException("This population has no identity attribute"); }
//		if (!idAttribute.isIdentity()) {
//			throw new GenstarException(idAttribute.getNameOnData() + " is no longer an identity attribute. This population object is no longer correctly functional.");
//		}
//		
//		while (entityIds.contains(currentMaxIdValue)) { currentMaxIdValue++; }
//		
//		return currentMaxIdValue;
//	}
//	
//	@Override public AbstractAttribute getIdentityAttribute() throws GenstarException {
//		if (idAttribute != null && !idAttribute.isIdentity()) {
//			throw new GenstarException(idAttribute.getNameOnData() + " is no longer an identity attribute. This population object is no longer correctly functional.");
//		}
//		
//		return idAttribute;
//	}
}
