package ummisco.genstar.metamodel;

import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public interface ISyntheticPopulation {
	
	public abstract List<AbstractAttribute> getAttributes();
	
	public abstract AbstractAttribute getAttributeByNameOnData(final String attributeNameOnData) throws GenstarException;
	
	public abstract AbstractAttribute getAttributebyNameOnEntity(final String attributeNameOnEntity) throws GenstarException;
	
	public abstract String getName();
	
	public abstract int getNbOfEntities();
	
	public abstract List<Entity> getEntities();

	public abstract List<Entity> getMatchingEntitiesByAttributeValuesOnEntity(final Map<String, AttributeValue> matchingCriteria) throws GenstarException;

	public abstract boolean isCompatible(final ISyntheticPopulation otherPopulation);
	
	public abstract List<Entity> createEntities(final int number) throws GenstarException;
	
	public abstract Entity createEntityWithAttributeValuesOnEntity(final Map<String, AttributeValue> attributeValuesOnEntity) throws GenstarException;

	public abstract List<Entity> createEntitiesWithAttributeValuesOnEntities(final List<Map<String, AttributeValue>> attributeValuesOnEntities) throws GenstarException;

	public abstract void addGroupReferences(final Map<String, String> groupReferences) throws GenstarException;
	
	public abstract Map<String, String> getGroupReferences();
	
	public abstract String getGroupReference(final String populationName);
	
	public abstract void addGroupReference(final String populationName, final String referenceAttribute) throws GenstarException;

	public abstract void addComponentReferences(final Map<String, String> componentReferences) throws GenstarException;

	public abstract Map<String, String> getComponentReferences();

	public abstract String getComponentReference(final String populationName);

	public abstract void addComponentReference(final String populationName, final String referenceAttribute) throws GenstarException;
}
