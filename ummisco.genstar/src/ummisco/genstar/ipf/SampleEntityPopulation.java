package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class SampleEntityPopulation {

	private List<AbstractAttribute> attributes;
	
	private String populationName;
	
	private List<SampleEntity> sampleEntities;


	public SampleEntityPopulation(final String populationName, final List<AbstractAttribute> attributes) {
		if ( populationName == null ) { throw new IllegalArgumentException("'populationName' parameter can not be null"); }
		if (attributes == null) { throw new IllegalArgumentException("'attributes' parameter can not be null"); }
		
		this.populationName = populationName;
		this.attributes = new ArrayList<AbstractAttribute>(attributes);
	}
	
	public int getNbOfEntities() {
		return sampleEntities.size();
	}
	
	public List<SampleEntity> getSampleEntities() {
		return new ArrayList<SampleEntity>(sampleEntities);
	}
 
	
	public List<SampleEntity> getMatchingEntities(final Map<String, AttributeValue> matchingCriteria) {
		List<SampleEntity> matchings = new ArrayList<SampleEntity>();
		for (SampleEntity e : sampleEntities) { if (e.isMatched(matchingCriteria)) { matchings.add(e); } }
		
		return matchings;
	}
	
	public int countMatchingEntities(final Map<String, AttributeValue> matchingCriteria) {
		return getMatchingEntities(matchingCriteria).size();
	}

	public String getPopulationName() {
		return populationName;
	}

	public List<AbstractAttribute> getAttributes() {
		List<AbstractAttribute> copy = new ArrayList<AbstractAttribute>();
		copy.addAll(attributes);
		
		return copy;
	}
	
	public List<SampleEntity> createSampleEntities(final List<Map<String, AttributeValue>> attributeValues) throws GenstarException {
		if (attributeValues == null) { throw new GenstarException("Parameter attributeValues can not be null"); }
		
		if (sampleEntities == null) { sampleEntities = new ArrayList<SampleEntity>(); }
		
		List<SampleEntity> newlyCreated = new ArrayList<SampleEntity>();
		for (Map<String, AttributeValue> values : attributeValues) {
			SampleEntity se = createSampleEntity(values);
			
			sampleEntities.add(se);
			newlyCreated.add(se);
		}
		
		return newlyCreated;
	}
	
	public SampleEntity createSampleEntity(final Map<String, AttributeValue> attributeValues) throws GenstarException {
		if (attributeValues == null) { throw new GenstarException("Parameter attributeValues can not be null"); }
		
		if (sampleEntities == null) { sampleEntities = new ArrayList<SampleEntity>(); }
		
		SampleEntity se = new SampleEntity(this);
		se.setAttributeValues(attributeValues);
		sampleEntities.add(se);
		
		return se;
	}
}
