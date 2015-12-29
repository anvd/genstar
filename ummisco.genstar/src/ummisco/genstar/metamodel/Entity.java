package ummisco.genstar.metamodel;

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

	private ISyntheticPopulation population;

	private Map<String, EntityAttributeValue> entityAttributeValues = Collections.EMPTY_MAP; // <attribute name on data, entity attribute value>; lazy initialization
	
	private Map<String, ISyntheticPopulation> componentPopulations = Collections.EMPTY_MAP; // <population name, population>
	
	
	public Entity(final ISyntheticPopulation population) {
		if (population == null) { throw new IllegalArgumentException("Parameter 'population' can not be null"); }
		
		this.population = population;
	}
	
	public ISyntheticPopulation getPopulation() {
		return population;
	}
	
	public ISyntheticPopulation getComponentPopulation(final String componentPopulationName) {
		return componentPopulations.get(componentPopulationName);
	}
	
	public ISyntheticPopulation createComponentPopulation(final String populationName, final List<AbstractAttribute> attributes) throws GenstarException {
		if (populationName == null || populationName.isEmpty()) { throw new GenstarException("Parameter populationName can neither be null nor empty"); }
		if (attributes == null || attributes.isEmpty()) { throw new GenstarException("Parameter attributes can neither be null nor empty"); }
		
		if (componentPopulations == Collections.EMPTY_MAP) {
			componentPopulations = new HashMap<String, ISyntheticPopulation>();
		}
		
		if (componentPopulations.get(populationName) != null) {
			throw new GenstarException("Sample Entity Population " + populationName + " has already existed");
		}
		
		ISyntheticPopulation population = new SyntheticPopulation(populationName, attributes);
		componentPopulations.put(populationName, population);
		
		return population;
	}
	
	public Map<String, EntityAttributeValue> getEntityAttributeValues() {
		return new HashMap<String, EntityAttributeValue>(entityAttributeValues);
	}
	
	public EntityAttributeValue getEntityAttributeValueByNameOnData(final String attributeNameOnData) throws GenstarException {
		if (attributeNameOnData == null) { throw new GenstarException("'attributeNameOnData' parameter can not be null"); }
		
		return entityAttributeValues.get(attributeNameOnData);
	}
	
	public EntityAttributeValue getEntityAttributeValueByNameOnEntity(final String attributeNameOnEntity) throws GenstarException {
		if (attributeNameOnEntity == null) { throw new GenstarException("'attributeNameOnEntity' parameter can not be null"); }
		
		AbstractAttribute attribute = population.getAttributebyNameOnEntity(attributeNameOnEntity);
		if (attribute == null) { return null; }
		
		return entityAttributeValues.get(attribute.getNameOnData());
	}
	
	public void setEntityAttributeValues(final List<EntityAttributeValue> eAttributeValues) throws GenstarException {
		if (eAttributeValues == null) { throw new GenstarException("Parameter entityAttributeValues can not be null"); }
		
		for (EntityAttributeValue eav : eAttributeValues) { 
			if (population.getAttributeByNameOnData(eav.getAttribute().getNameOnData()) == null) {
				throw new GenstarException(eav.getAttribute().getNameOnData() + " atribute is not found on entity's population");
			}
		}
		
		
		if (this.entityAttributeValues == Collections.EMPTY_MAP) {
			this.entityAttributeValues = new HashMap<String, EntityAttributeValue>();
		}
		
		for (EntityAttributeValue eav : eAttributeValues) { 
			this.entityAttributeValues.put(eav.getAttribute().getNameOnEntity(), eav);
		}
	}
	
	public void setAttributeValuesOnData(final Map<String, AttributeValue> attributeValuesOnData) throws GenstarException {
		if (attributeValuesOnData == null) { throw new GenstarException("Parameter values can not be null"); }
		
		if (entityAttributeValues == Collections.EMPTY_MAP) {
			entityAttributeValues = new HashMap<String, EntityAttributeValue>();
			for (AbstractAttribute attr : population.getAttributes()) { entityAttributeValues.put(attr.getNameOnData(), null); }
		}
		
		for (String attrNameOnData : attributeValuesOnData.keySet()) {
			// TODO verify valid AttributeValue
			AbstractAttribute attribute = population.getAttributeByNameOnData(attrNameOnData);
			if (attribute == null) { throw new GenstarException(attrNameOnData + " attribute is not found on entity"); }
			
			EntityAttributeValue eav = new EntityAttributeValue(attribute, attributeValuesOnData.get(attrNameOnData));
			if (entityAttributeValues.containsKey(attrNameOnData)) { entityAttributeValues.put(attrNameOnData, eav); }
		}
	}
	
	public void setAttributeValueOnData(final String attributeNameOnData, final AttributeValue attributeValueOnData) throws GenstarException {
		if (attributeNameOnData == null) { throw new GenstarException("Parameter attributeNameOnData can not be null"); }
		if (attributeValueOnData == null) { throw new GenstarException("Parameter attributeValueOnData can not be null"); }
		
		AbstractAttribute attribute = population.getAttributeByNameOnData(attributeNameOnData);
		if (attribute == null) { throw new GenstarException(attributeNameOnData + " attribute is not found on " + population.getName() + " entity"); }
		
		if (entityAttributeValues == Collections.EMPTY_MAP) {
			entityAttributeValues = new HashMap<String, EntityAttributeValue>();
			for (AbstractAttribute attr : population.getAttributes()) { entityAttributeValues.put(attr.getNameOnData(), null); }
		}

		EntityAttributeValue eav = new EntityAttributeValue(attribute, attributeValueOnData);
		entityAttributeValues.put(attributeNameOnData, eav);
	}
	
	public void setAttributeValueOnEntity(final String attributeNameOnEntity, final AttributeValue attributeValueOnEntity) throws GenstarException {
		if (attributeNameOnEntity == null || attributeValueOnEntity == null) {
			throw new GenstarException("Parameters attributeNameOnData, attributevalueOnEntity can not be null");
		}
		
		AbstractAttribute attribute = population.getAttributebyNameOnEntity(attributeNameOnEntity);
		if (attribute == null) { throw new GenstarException("No attribute found with " + attributeNameOnEntity + " as name on " + population.getName() + " entity"); }
		
		AttributeValue attributeValueOnData = null;
		
		if (!attribute.isIdentity()) {
			attributeValueOnData = attribute.findMatchingAttributeValue(attributeValueOnEntity);
			if (attributeValueOnData == null) { throw new GenstarException("No matching attribute found for attribute on entity: " + attributeValueOnEntity + " on attribute " + attribute.getNameOnData()); }
		} else {
			// attributeValueOnEntity of an identity attribute doesn't have a corresponding attributeValueOnData, i.e., attributeOnData == null
			if (!attributeValueOnEntity.getDataType().equals(attribute.getDataType())) {
				throw new GenstarException("Incompatible data type between attribute (" + attribute.getNameOnData() + ") and attribute value on entity");
			}
		}
		
		
		if (entityAttributeValues == Collections.EMPTY_MAP) {
			entityAttributeValues = new HashMap<String, EntityAttributeValue>();
			for (AbstractAttribute attr : population.getAttributes()) { entityAttributeValues.put(attr.getNameOnData(), null); }
		}
		
		EntityAttributeValue eav = new EntityAttributeValue(attribute, attributeValueOnData, attributeValueOnEntity);
		entityAttributeValues.put(attribute.getNameOnData(), eav);
	}
	
	public void setAttributeValuesOnEntity(Map<String, AttributeValue> attributeValuesOnEntity) throws GenstarException {
		if (attributeValuesOnEntity == null) {
			throw new GenstarException("attributeValuesOnEntity parameter can not be null");
		}
		
		for (String attributeNameOnEntity : attributeValuesOnEntity.keySet()) {
			this.setAttributeValueOnEntity(attributeNameOnEntity, attributeValuesOnEntity.get(attributeNameOnEntity));
		}
	}

	public boolean containAttributeWithNameOnData(final String attributeNameOnData) {
		return entityAttributeValues.get(attributeNameOnData) != null;
	}
	
	public boolean areValuesOnEntityMatched(final Map<String, AttributeValue> matchingAttributeValuesOnEntity) throws GenstarException { // <attribute name on entity, attribute value on entity>
		if (matchingAttributeValuesOnEntity == null || matchingAttributeValuesOnEntity.isEmpty()) { return true; }
		
		AbstractAttribute attribute;
		EntityAttributeValue entityAttrValue;
		for (String attributeNameOnEntity : matchingAttributeValuesOnEntity.keySet()) {
			attribute = population.getAttributebyNameOnEntity(attributeNameOnEntity);
			if (attribute == null) { throw new GenstarException("Attribute with " + attributeNameOnEntity + " as name on entity doesn't exist"); }
			
			entityAttrValue = entityAttributeValues.get(attribute.getNameOnData());
			
			if (entityAttrValue == null) { return false; }
			if (!entityAttrValue.isAttributeValueOnEntityMatched(matchingAttributeValuesOnEntity.get(attributeNameOnEntity))) { return false; }
		}
		
		return true;
	}
	
	public List<ISyntheticPopulation> getComponentPopulations() {
		return new ArrayList<ISyntheticPopulation>(componentPopulations.values());
	}

}
