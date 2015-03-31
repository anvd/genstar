package ummisco.genstar.metamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;

public class Entity {

	private ISyntheticPopulation population;

	private Map<String, EntityAttributeValue> attributeValues = Collections.EMPTY_MAP; // <attribute name on entity, entity attribute value>; lazy initialization
	
	private List<Entity> members = Collections.EMPTY_LIST; // lazy initialization
	
	
	public Entity(final ISyntheticPopulation population) {
		if (population == null) { throw new IllegalArgumentException("Input parameter 'population' can not be null"); }
		
		this.population = population;
	}
	
	public ISyntheticPopulation getPopulation() {
		return population;
	}
	
	public List<Entity> getMembers() {
		return members;
	}
	
	public Map<String, EntityAttributeValue> getAttributeValues() {
		return attributeValues;
	}
	
	public EntityAttributeValue getEntityAttributeValue(final String attributeNameOnEntity) {
		if (attributeNameOnEntity == null) { throw new IllegalArgumentException("'attributeNameOnEntity' parameter can not be null"); }
		
		return attributeValues.get(attributeNameOnEntity);
	}
	
	public boolean containAttribute(final String attributeNameOnEntity) {
		return attributeValues.get(attributeNameOnEntity) != null;
	}
	
	public void putAttributeValue(final AbstractAttribute attribute, final AttributeValue attributeValue) throws GenstarException {
		this.putAttributeValue(new EntityAttributeValue(attribute, attributeValue));
	}

	public void putAttributeValue(final EntityAttributeValue entityAttributeValue) throws GenstarException {
		
		if (entityAttributeValue == null) { throw new GenstarException("'attributeValue' parameter can not be null"); }
		
		String attributeNameOnEntity = entityAttributeValue.getAttribute().getNameOnEntity();
		if (containAttribute(attributeNameOnEntity)) {  throw new GenstarException("Entity " + population.getName() + " has already contained '" + attributeNameOnEntity + "' attribute."); }
		// TODO improve the clarity of the above error message (when invoked from GAMA, the "population.getName()" has no sense)
		
		if (attributeValues == Collections.EMPTY_MAP) { attributeValues = new HashMap<String, EntityAttributeValue>(); }
		attributeValues.put(entityAttributeValue.getAttribute().getNameOnEntity(), entityAttributeValue);
	}
	
	public void replaceAttributeValue(final AbstractAttribute attribute, final AttributeValue attributeValue) throws GenstarException {
		this.replaceAttributeValue(new EntityAttributeValue(attribute, attributeValue));
	}

	public void replaceAttributeValue(final EntityAttributeValue entityAttributeValue) throws GenstarException {
		if (entityAttributeValue == null) { throw new IllegalArgumentException("'attributeValue' parameter can not be null"); }
		if (attributeValues == Collections.EMPTY_MAP) { attributeValues = new HashMap<String, EntityAttributeValue>(); }
		
		attributeValues.put(entityAttributeValue.getAttribute().getNameOnEntity(), entityAttributeValue);
	}
	
	public boolean isMatch(final Map<String, AttributeValue> matchingAttributeValues) { // <attribute name on entity, attribute value on entity>
		if (matchingAttributeValues == null || matchingAttributeValues.isEmpty()) { return true; }

		EntityAttributeValue entityAttrValue;
		for (String attributeNameOnEntity : matchingAttributeValues.keySet()) {
			entityAttrValue = attributeValues.get(attributeNameOnEntity);
			
			if (entityAttrValue == null) { return false; }
			if (!entityAttrValue.isValueMatch(matchingAttributeValues.get(attributeNameOnEntity))) { return false; }
		}
		
		return true;
	}
	
	public void addMember(final Entity member) {
		if (member == null) { throw new IllegalArgumentException("'member' parameter can not be null"); }
		if (members == Collections.EMPTY_LIST) { members = new ArrayList<Entity>(5); }
		if (!members.contains(member)) { members.add(member); }
	}
	
	public void addMembers(final List<Entity> members) {
		if (members == null) { throw new IllegalArgumentException("'members' parameter can not be null"); }
		if (this.members == Collections.EMPTY_LIST) { this.members = new ArrayList<Entity>(members.size()); }
		for (Entity m : members) { this.addMember(m); }
	}
}
