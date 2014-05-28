package ummisco.genstar.metamodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ummisco.genstar.util.SharedInstances;

public class SyntheticPopulation implements ISyntheticPopulation {
	
	private String name;
	
	private List<Entity> entities;
	
	private int initialNbOfEntities;
	

	public SyntheticPopulation(final String name, final int initialNbOfEntities) {
		if ( name == null ) { throw new IllegalArgumentException("'name' parameter can not be null"); }
		if (initialNbOfEntities <= 0) { throw new IllegalArgumentException("'initialNbOfEntities' must be a positive integer"); }
		
		this.name = name;
		this.initialNbOfEntities = initialNbOfEntities;
		
		this.entities = new ArrayList<Entity>();
		for (int i=0; i<initialNbOfEntities; i++) { entities.add(new Entity(this)); }
	}
	
	@Override public int getInitialNbOfEntities() {
		return initialNbOfEntities;
	}
	
	@Override public List<Entity> getEntities() {
		return entities;
	}

	@Override public Entity pick(final Map<String, AttributeValue> matchingAttributeValues) {
		Entity picked = null;
		for (Entity e : entities) {
			if (e.isMatch(matchingAttributeValues)) {
				picked = e;
				break;
			}
		}
		
		if (picked != null) { entities.remove(picked); }
		return picked;
	}
	
	@Override public Entity pick() {
		if (entities.isEmpty()) { return null; }
		
		int randomIndex = SharedInstances.RandomNumberGenerator.nextInt(entities.size());
		Entity pickedMember = entities.get(randomIndex);
		entities.remove(pickedMember);
		return pickedMember;
	}
	
	@Override public Entity pick(final Entity entity) {
		if (entities.contains(entity)) {
			entities.remove(entity);
			return entity;
		}
		
		return null;
	}

	@Override public void putBack(final Entity entity) {
		if (entity == null) { throw new IllegalArgumentException("'entity' parameter can not be null"); }
		if (!entity.getPopulation().equals(this)) { throw new IllegalArgumentException("'entity' belongs to a different population from this one"); }
		
		if (!entities.contains(entity)) { entities.add(entity); }
	}
	
	@Override public void putBack(final List<Entity> entities) {
		if (entities == null) { throw new IllegalArgumentException("'entities' can not be null"); }
		for (Entity e : entities) { this.putBack(e); }
	}

	@Override public String getName() {
		return name;
	}	
	
}
