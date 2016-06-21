package ummisco.genstar.metamodel.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;

public class Entity {

	private IPopulation population;

	private Map<AbstractAttribute, EntityAttributeValue> entityAttributeValues = Collections.EMPTY_MAP;
	
	private Map<String, IPopulation> componentPopulations = Collections.EMPTY_MAP; // <population name, population>
	
	
	public Entity(final IPopulation population) {
		if (population == null) { throw new IllegalArgumentException("Parameter 'population' can not be null"); }

		this.population = population;
	}
	
	public IPopulation getPopulation() {
		return population;
	}
	
	public IPopulation getComponentPopulation(final String componentPopulationName) {
		return componentPopulations.get(componentPopulationName);
	}
	
	public IPopulation createComponentPopulation(final String populationName, final List<AbstractAttribute> attributes) throws GenstarException {
		if (populationName == null || populationName.isEmpty()) { throw new GenstarException("Parameter populationName can neither be null nor empty"); }
		if (attributes == null || attributes.isEmpty()) { throw new GenstarException("Parameter attributes can neither be null nor empty"); }
		
		if (componentPopulations == Collections.EMPTY_MAP) {
			componentPopulations = new HashMap<String, IPopulation>();
		}
		
		if (componentPopulations.get(populationName) != null) {
			throw new GenstarException("Component Population " + populationName + " has already existed");
		}
		
		IPopulation componentPopulation = new Population(this.population.getPopulationType(), populationName, attributes);
		componentPopulations.put(populationName, componentPopulation);
		
		return componentPopulation;
	}
	
	public List<EntityAttributeValue> getEntityAttributeValues() {
		return new ArrayList<EntityAttributeValue>(entityAttributeValues.values());
	}
	
	public EntityAttributeValue getEntityAttributeValue(final AbstractAttribute attribute) throws GenstarException {
		if (attribute == null) { throw new GenstarException("'attribute' parameter can not be null"); }
		
		return entityAttributeValues.get(attribute); // TODO return a copy of identity attribute value
	}
	
	public EntityAttributeValue getEntityAttributeValueByNameOnData(final String attributeNameOnData) throws GenstarException {
		if (attributeNameOnData == null) { throw new GenstarException("'attributeNameOnData' parameter can not be null"); }
		AbstractAttribute attribute = population.getAttributeByNameOnData(attributeNameOnData);
		if (attribute == null) { return null; }
		
		return entityAttributeValues.get(attribute); // TODO return a copy of identity attribute value
	}
	
	public EntityAttributeValue getEntityAttributeValueByNameOnEntity(final String attributeNameOnEntity) throws GenstarException {
		if (attributeNameOnEntity == null) { throw new GenstarException("'attributeNameOnEntity' parameter can not be null"); }
		
		AbstractAttribute attribute = population.getAttributeByNameOnEntity(attributeNameOnEntity);
		if (attribute == null) { return null; }
		
		return entityAttributeValues.get(attribute); // TODO return a copy identity attribute value
	}
	
	public void setEntityAttributeValues(final List<EntityAttributeValue> eAttributeValues) throws GenstarException {
		if (eAttributeValues == null) { throw new GenstarException("Parameter entityAttributeValues can not be null"); }
		
		for (EntityAttributeValue eav : eAttributeValues) { 
			if (!population.containAttribute(eav.getAttribute())) {
				throw new GenstarException(eav.getAttribute().getNameOnData() + " attribute is not found on entity's population");
			}
		}
		
		// TODO call internalSetEntityAttributeValue
		if (this.entityAttributeValues == Collections.EMPTY_MAP) {
			this.entityAttributeValues = new HashMap<AbstractAttribute, EntityAttributeValue>();
		}
		
		for (EntityAttributeValue eav : eAttributeValues) { 
			this.entityAttributeValues.put(eav.getAttribute(), eav);
		}
	}
	

	public void setAttributeValueOnData(final AbstractAttribute attribute, final AttributeValue attributeValueOnData) throws GenstarException {
		if (attribute == null) { throw new GenstarException("Parameter attribute can not be null"); }
		if (attributeValueOnData == null) { throw new GenstarException("Parameter attributeValueOnData can not be null"); }
		
		if (!population.containAttribute(attribute)) { throw new GenstarException(attribute.getNameOnData() + " attribute is not found on " + population.getName() + " entity"); }
		
		// TODO call internalSetEntityAttributeValue
		
		if (entityAttributeValues == Collections.EMPTY_MAP) {
			entityAttributeValues = new HashMap<AbstractAttribute, EntityAttributeValue>();
			for (AbstractAttribute attr : population.getAttributes()) { entityAttributeValues.put(attr, null); }
		}

		EntityAttributeValue eav = new EntityAttributeValue(attribute, attributeValueOnData);
		entityAttributeValues.put(attribute, eav);
	}
	
	public void setAttributeValuesOnData(final Map<AbstractAttribute, AttributeValue> attributeValuesOnData) throws GenstarException {
		if (attributeValuesOnData == null) { throw new GenstarException("'attributeValuesOnData' parameter can not be null"); }
		
		// TODO call internalSetEntityAttributeValue
		
		for (AbstractAttribute attr : attributeValuesOnData.keySet()) { this.setAttributeValueOnData(attr, attributeValuesOnData.get(attr)); }
	}
	
	public void setAttributeValueOnEntity(final AbstractAttribute attribute, final AttributeValue attributeValueOnEntity) throws GenstarException {
		if (attribute == null || attributeValueOnEntity == null) {
			throw new GenstarException("Parameters attribute, attributevalueOnEntity can not be null");
		}
		
		// TODO call internalSetEntityAttributeValue
		
		if (!population.containAttribute(attribute)) { throw new GenstarException("No attribute found with " + attribute.getNameOnData() + " as name on data of " + population.getName() + " entity"); }
		
		AttributeValue attributeValueOnData = null;
		
		// validation of attributeValueOnEntity
		attributeValueOnData = attribute.getMatchingAttributeValueOnData(attributeValueOnEntity);
		if (attributeValueOnData == null) {  throw new GenstarException(attributeValueOnEntity + " is not a valid value of " + attribute.getNameOnData()  + " attribute."); }
		
		
		if (entityAttributeValues == Collections.EMPTY_MAP) {
			entityAttributeValues = new HashMap<AbstractAttribute, EntityAttributeValue>();
			for (AbstractAttribute attr : population.getAttributes()) { entityAttributeValues.put(attr, null); }
		}
		
		EntityAttributeValue eav = new EntityAttributeValue(attribute, attributeValueOnData, attributeValueOnEntity);
		entityAttributeValues.put(attribute, eav);
		
		// TODO call internalSetEntityAttributeValue
	}
	
	public void setAttributeValuesOnEntity(Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity) throws GenstarException {
		if (attributeValuesOnEntity == null) {
			throw new GenstarException("attributeValuesOnEntity parameter can not be null");
		}
		
		// TODO call internalSetEntityAttributeValue
		
		for (AbstractAttribute attribute : attributeValuesOnEntity.keySet()) {
			this.setAttributeValueOnEntity(attribute, attributeValuesOnEntity.get(attribute));
		}
	}

	public boolean matchAttributeValuesOnEntity(final Map<AbstractAttribute, AttributeValue> matchingAttributeValuesOnEntity) throws GenstarException { // <attribute name on entity, attribute value on entity>
		if (matchingAttributeValuesOnEntity == null || matchingAttributeValuesOnEntity.isEmpty()) { return true; }
		
		EntityAttributeValue entityAttrValue;
		for (AbstractAttribute attribute : matchingAttributeValuesOnEntity.keySet()) {
			if (!population.containAttribute(attribute)) { throw new GenstarException("Attribute with " + attribute.getNameOnEntity() + " as name on entity doesn't exist"); }
			
			entityAttrValue = entityAttributeValues.get(attribute);
			if (entityAttrValue == null) { return false; }
			if (!entityAttrValue.isAttributeValueOnEntityMatched(matchingAttributeValuesOnEntity.get(attribute))) { return false; }
		}
		
		return true;
	}
	
	public boolean matchAttributeValuesOnData(final Map<AbstractAttribute, AttributeValue> matchingAttributeValuesOnData) throws GenstarException {
		if (matchingAttributeValuesOnData == null || matchingAttributeValuesOnData.isEmpty()) { return true; }
		
		EntityAttributeValue entityAttrValue;
		for (AbstractAttribute attribute : matchingAttributeValuesOnData.keySet()) {
			if (!population.containAttribute(attribute)) { throw new GenstarException("Attribute with " + attribute.getNameOnData() + " as name on data doesn't exist"); }
			
			entityAttrValue = entityAttributeValues.get(attribute);
			if (entityAttrValue == null) { return false; }
			if (!entityAttrValue.isAttributeValueOnDataMatched(matchingAttributeValuesOnData.get(attribute))) { return false; }
		}
		
		return true;
	}
	
	public Map<AbstractAttribute, AttributeValue> getAttributesValuesOnData(final List<AbstractAttribute> attributes) throws GenstarException {
		if (attributes == null) { throw new GenstarException("Parameter attributes can not be null"); }
		if (!population.getAttributes().containsAll(attributes)) { throw new GenstarException("One or several attributes are not valid"); }
		
		Map<AbstractAttribute, AttributeValue> controlledAttributesValuesOnData = new HashMap<AbstractAttribute, AttributeValue>();
		for (AbstractAttribute attr : attributes) {
			EntityAttributeValue entityAttributeValue = entityAttributeValues.get(attr);
			if (entityAttributeValue == null) { throw new GenstarException("Attribute '" + attr.getNameOnData() + "' not found on the entity"); }

			AttributeValue valueOnData = entityAttributeValue.getAttributeValueOnData();
			controlledAttributesValuesOnData.put(attr, valueOnData);
		}
		
		return controlledAttributesValuesOnData;
	}
	
	
	public List<IPopulation> getComponentPopulations() {
		return new ArrayList<IPopulation>(componentPopulations.values());
	}

}
