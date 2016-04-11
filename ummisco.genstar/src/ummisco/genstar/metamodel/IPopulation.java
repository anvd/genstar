package ummisco.genstar.metamodel;

import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;

public interface IPopulation {
	
	
	public abstract List<AbstractAttribute> getAttributes();
	
	public abstract boolean containAttribute(final AbstractAttribute attribute) throws GenstarException;
	
	public abstract AbstractAttribute getAttributeByNameOnData(final String attributeNameOnData) throws GenstarException;
	
	public abstract AbstractAttribute getAttributeByNameOnEntity(final String attributeNameOnEntity) throws GenstarException;
	
	public abstract String getName();
	
	public abstract int getNbOfEntities();
	
	public abstract List<Entity> getEntities();
	
	public abstract List<Entity> getMatchingEntitiesByAttributeValuesOnData(final Map<AbstractAttribute, AttributeValue> attributeValuesOnData) throws GenstarException;

	public abstract List<Entity> getMatchingEntitiesByAttributeValuesOnEntity(final Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity) throws GenstarException;
	
	public abstract int countMatchingEntitiesByAttributeValuesOnEntity(final Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity) throws GenstarException;

	public abstract boolean isCompatible(final IPopulation otherPopulation);
	
	public abstract List<Entity> createEntities(final int number) throws GenstarException;
	
	public abstract Entity createEntity(final List<EntityAttributeValue> entityAttributeValues) throws GenstarException;
	
	public abstract List<Entity> createEntities(final List<List<EntityAttributeValue>> entityAttributeValuesList) throws GenstarException;
	
	public abstract Entity createEntityWithAttributeValuesOnEntity(final Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity) throws GenstarException;

	public abstract List<Entity> createEntitiesWithAttributeValuesOnEntities(final List<Map<AbstractAttribute, AttributeValue>> attributeValuesOnEntities) throws GenstarException;

	public abstract void addGroupReferences(final Map<String, String> groupReferences) throws GenstarException;
	
	public abstract Map<String, String> getGroupReferences();
	
	public abstract String getGroupReference(final String populationName);
	
	public abstract void addGroupReference(final String populationName, final String referenceAttribute) throws GenstarException;

	public abstract void addComponentReferences(final Map<String, String> componentReferences) throws GenstarException;

	public abstract Map<String, String> getComponentReferences();

	public abstract String getComponentReference(final String populationName);

	public abstract void addComponentReference(final String populationName, final String referenceAttribute) throws GenstarException;
	
	public PopulationType getPopulationType();
}
