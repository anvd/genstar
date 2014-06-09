package ummisco.genstar.metamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;

public class Entity {

	private ISyntheticPopulation population;

	private Map<String, EntityAttributeValue> attributes; // <attribute name on entity, entity attribute value>
	
	private List<Entity> members;
	
	public Entity(final ISyntheticPopulation population) {
		if (population == null) { throw new IllegalArgumentException("Input parameter 'population' can not be null"); }
		
		this.population = population;
		this.attributes = new HashMap<String, EntityAttributeValue>();
		this.members = new ArrayList<Entity>();
	}
	
	public ISyntheticPopulation getPopulation() {
		return population;
	}
	
	public List<Entity> getMembers() {
		return members;
	}
	
	public Map<String, EntityAttributeValue> getAttributes() {
		return attributes;
	}
	
	public EntityAttributeValue getEntityAttributeValue(final String attributeNameOnEntity) {
		if (attributeNameOnEntity == null) { throw new IllegalArgumentException("'attributeNameOnEntity' parameter can not be null"); }
		
		return attributes.get(attributeNameOnEntity);
	}
	
	public boolean containAttribute(final String attributeNameOnEntity) {
		return attributes.get(attributeNameOnEntity) != null;
	}
	
	public void putAttributeValue(final AbstractAttribute attribute, final AttributeValue attributeValue) throws GenstarException {
		this.putAttributeValue(new EntityAttributeValue(attribute, attributeValue));
	}

	public void putAttributeValue(final EntityAttributeValue entityAttributeValue) throws GenstarException {
		
		if (entityAttributeValue == null) { throw new IllegalArgumentException("'attributeValue' parameter can not be null"); }
		
		String attributeNameOnEntity = entityAttributeValue.getAttribute().getNameOnEntity();
		if (containAttribute(attributeNameOnEntity)) { throw new GenstarException("Entity " + population.getName() + " already contains '" + attributeNameOnEntity + "' attribute."); }
		
		attributes.put(entityAttributeValue.getAttribute().getNameOnEntity(), entityAttributeValue);
	}
	
	public void replaceAttributeValue(final AbstractAttribute attribute, final AttributeValue attributeValue) throws GenstarException {
		this.replaceAttributeValue(new EntityAttributeValue(attribute, attributeValue));
	}

	public void replaceAttributeValue(final EntityAttributeValue entityAttributeValue) throws GenstarException {
		if (entityAttributeValue == null) { throw new IllegalArgumentException("'attributeValue' parameter can not be null"); }
		
		attributes.put(entityAttributeValue.getAttribute().getNameOnEntity(), entityAttributeValue);
	}
	
	public boolean isMatch(final Map<String, AttributeValue> matchingAttributeValues) { // <attribute name on entity, attribute value on entity>
		if (matchingAttributeValues == null || matchingAttributeValues.isEmpty()) { return true; }

		EntityAttributeValue entityAttrValue;
		for (String attributeNameOnEntity : matchingAttributeValues.keySet()) {
			entityAttrValue = attributes.get(attributeNameOnEntity);
			
			if (entityAttrValue == null) { return false; }
			if (!entityAttrValue.isValueMatch(matchingAttributeValues.get(attributeNameOnEntity))) { return false; }
		}
		
		return true;
	}
	
	public void addMember(final Entity m) {
		if (m == null) { throw new IllegalArgumentException("'member' parameter can not be null"); }
		if (!members.contains(m)) { members.add(m); }
	}
	
	public void addMembers(final List<Entity> members) {
		if (members == null) { throw new IllegalArgumentException("'members' parameter can not be null"); }
		for (Entity m : members) { this.addMember(m); }
	}
}
