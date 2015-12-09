package ummisco.genstar.metamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class SyntheticPopulation implements ISyntheticPopulation {
	
	private List<AbstractAttribute> attributes;
	
	private String name;
	
	private List<Entity> entities = Collections.EMPTY_LIST;
	

	public SyntheticPopulation(final String name, final List<AbstractAttribute> attributes) {
		if ( name == null ) { throw new IllegalArgumentException("'name' parameter can not be null"); }
		if (attributes == null) { throw new IllegalArgumentException("'attributes' parameter can not be null"); }
		
		this.name = name;
		this.attributes = new ArrayList<AbstractAttribute>();
		this.attributes.addAll(attributes);
	}
	
	@Override public int getNbOfEntities() {
		return entities.size();
	}
	
	@Override public List<Entity> getEntities() {
		return new ArrayList<Entity>(entities);
	}

	@Override public List<Entity> getMatchingEntitiesByAttributeValuesOnEntity(final Map<String, AttributeValue> matchingCriteria) throws GenstarException {
		List<Entity> matchings = new ArrayList<Entity>();
		for (Entity e : entities) { if (e.areValuesOnEntityMatched(matchingCriteria)) { matchings.add(e); } }
		
		return matchings;
	}

	@Override public String getName() {
		return name;
	}

	@Override public List<AbstractAttribute> getAttributes() {
		List<AbstractAttribute> copy = new ArrayList<AbstractAttribute>();
		copy.addAll(attributes);
		
		return copy;
	}	
	
	@Override public AbstractAttribute getAttributeByNameOnData(final String attributeNameOnData) throws GenstarException {
		if (attributeNameOnData  == null) { throw new GenstarException("Parameter attributeNameOnData can not be null"); }
		
		for (AbstractAttribute attr : attributes) {
			if (attr.getNameOnData().equals(attributeNameOnData)) { return attr; }
		}
		
		return null;
	}
	
	@Override public AbstractAttribute getAttributebyNameOnEntity(final String attributeNameOnEntity) throws GenstarException {
		if (attributeNameOnEntity  == null) { throw new GenstarException("Parameter attributeNameOnEntity can not be null"); }
		
		for (AbstractAttribute attr : attributes) {
			if (attr.getNameOnEntity().equals(attributeNameOnEntity)) { return attr; }
		}
		
		return null;
	}
	
	@Override public boolean isCompatible(final ISyntheticPopulation otherPopulation) {
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
			entities.add(e);
			newlyCreated.add(e);
		}
		
		return newlyCreated;
	}
	
	@Override public Entity createEntityWithAttributeValuesOnEntity(final Map<String, AttributeValue> attributeValuesOnEntity) throws GenstarException {
		Entity e = new Entity(this);
		e.setAttributeValuesOnData(attributeValuesOnEntity);
		
		if (entities == Collections.EMPTY_LIST) { entities = new ArrayList<Entity>(); }
		entities.add(e);
		
		return e;
	}
	
	@Override public List<Entity> createEntitiesWithAttributeValuesOnEntities(final List<Map<String, AttributeValue>> attributeValuesAttributeValueOnEntities) throws GenstarException {
		List<Entity> createdEntities = new ArrayList<Entity>();
		
		for (Map<String, AttributeValue> av : attributeValuesAttributeValueOnEntities) {
			createdEntities.add(createEntityWithAttributeValuesOnEntity(av));
		}
		
		return createdEntities;
	}

	
}
