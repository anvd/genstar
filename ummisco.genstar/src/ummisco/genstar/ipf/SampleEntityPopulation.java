package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class SampleEntityPopulation {

	private List<AbstractAttribute> attributes;
	
	private String populationName;
	
	private List<SampleEntity> sampleEntities = Collections.EMPTY_LIST;
	
	private Map<String, String> groupReferences = Collections.EMPTY_MAP; // population_name :: GAMA_attribute_name
	
	private Map<String, String> componentReferences = Collections.EMPTY_MAP; // population_name :: GAMA_attribute_name
	
	// TODO establish the relationship between group and component agents


	public SampleEntityPopulation(final String populationName, final List<AbstractAttribute> attributes) throws GenstarException {
		if (populationName == null) { throw new GenstarException("'populationName' parameter can not be null"); }
		if (attributes == null) { throw new GenstarException("'attributes' parameter can not be null"); }
		
		this.populationName = populationName;
		this.attributes = new ArrayList<AbstractAttribute>(attributes);
	}
	
	public int getNbOfEntities() {
		return sampleEntities.size();
	}
	
	public List<SampleEntity> getSampleEntities() {
		return new ArrayList<SampleEntity>(sampleEntities);
	}
 
	
	public List<SampleEntity> getMatchingEntities(final Map<String, AttributeValue> matchingCriteria) throws GenstarException {
		List<SampleEntity> matchings = new ArrayList<SampleEntity>();
		for (SampleEntity e : sampleEntities) { if (e.isMatched(matchingCriteria)) { matchings.add(e); } }
		
		return matchings;
	}
	
	public int countMatchingEntities(final Map<String, AttributeValue> matchingCriteria) throws GenstarException {
		return getMatchingEntities(matchingCriteria).size();
	}

	public String getName() {
		return populationName;
	}

	public List<AbstractAttribute> getAttributes() {
		List<AbstractAttribute> copy = new ArrayList<AbstractAttribute>();
		copy.addAll(attributes);
		
		return copy;
	}
	
	public AbstractAttribute getAttributeByNameOnData(final String attributeNameOnData) throws GenstarException {
		for (AbstractAttribute attr : attributes) {
			if (attr.getNameOnData().equals(attributeNameOnData)) { return attr; }
		}
		
		return null;
	}
	
	public AbstractAttribute getAttributebyNameOnEntity(final String attributeNameOnEntity) throws GenstarException {
		for (AbstractAttribute attr : attributes) {
			if (attr.getNameOnEntity().equals(attributeNameOnEntity)) { return attr; }
		}
		
		return null;
	}

	public List<SampleEntity> createSampleEntities(final List<Map<String, AttributeValue>> attributeValues) throws GenstarException {
		if (attributeValues == null) { throw new GenstarException("Parameter attributeValues can not be null"); }
		
		List<SampleEntity> newlyCreated = new ArrayList<SampleEntity>();
		for (Map<String, AttributeValue> values : attributeValues) {
			SampleEntity se = createSampleEntity(values);
			newlyCreated.add(se);
		}
		
		return newlyCreated;
	}
	
	public SampleEntity createSampleEntity(final Map<String, AttributeValue> attributeValues) throws GenstarException {
		if (attributeValues == null) { throw new GenstarException("Parameter attributeValues can not be null"); }
		
		if (sampleEntities == Collections.EMPTY_LIST) { sampleEntities = new ArrayList<SampleEntity>(); }
		
		SampleEntity se = new SampleEntity(this);
		se.setAttributeValuesOnEntity(attributeValues);
		sampleEntities.add(se);
		
		return se;
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
}
