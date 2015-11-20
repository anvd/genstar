package ummisco.genstar.metamodel;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.util.PersistentObject;

public abstract class AbstractSyntheticPopulationGenerator implements ISyntheticPopulationGenerator {

	protected int id = PersistentObject.NEW_OBJECT_ID;
	
	protected String generatorName;
	
	protected String populationName = "no-name population";
	
	protected SortedMap<String, AbstractAttribute> attributes; // <attribute name on data, attribute>

	protected int nbOfEntities = 100;


	public AbstractSyntheticPopulationGenerator(final String generatorName) throws GenstarException {
		if (generatorName == null || generatorName.trim().length() == 0) { throw new GenstarException("'generatorName' parameter can neither be null nor empty"); }
		
		this.generatorName = generatorName;
		this.attributes = new TreeMap<String, AbstractAttribute>();
	}

	@Override public void setID(final int id) {
		this.id = id;
	}
	
	@Override public int getID() {
		return id;
	}
	
	@Override public String getGeneratorName() {
		return generatorName;
	}
	
	@Override public void setPopulationName(final String populationName) {
		if (populationName == null || populationName.trim().length() == 0) { throw new IllegalArgumentException("'populationName' parameter can neither be null nor empty"); }
		this.populationName = populationName;
	}
	
	@Override public String getPopulationName() {
		return populationName;
	}

	@Override public int getNbOfEntities() {
		return nbOfEntities;
	}
	
	@Override public void setNbOfEntities(final int nbOfEntities) {
		if (nbOfEntities <= 0) { throw new IllegalArgumentException("'nbOfEntities' must be a positive integer"); }
		this.nbOfEntities = nbOfEntities;
	}

	@Override public Set<AbstractAttribute> getAttributes() {
		return new HashSet<AbstractAttribute>(attributes.values());
	}
	
	@Override public boolean containAttribute(final AbstractAttribute attribute) {
		if (attribute == null) { return false; }
		
		return attributes.containsValue(attribute);
	}
	
	@Override public boolean containAttribute(final String dataAttributeName) {
		if (dataAttributeName == null) { return false; }
		
		return attributes.keySet().contains(dataAttributeName);
	}
	
	@Override public AbstractAttribute getAttribute(final String attributeNameOnData) {
		if (attributeNameOnData == null) { throw new IllegalArgumentException("'dataAttributeName' parameter can not be null"); }
		return attributes.get(attributeNameOnData);
	}
	
	@Override public void addAttribute(final AbstractAttribute attribute) throws GenstarException {
		if (attribute == null) { throw new GenstarException("'attribute' parameter can not be null"); }
		if (containAttribute(attribute.getNameOnData())) { throw new GenstarException("'" + generatorName + "' population already contains '" + attribute.getNameOnData() + "' attribute."); }
		if (!attribute.getPopulationGenerator().equals(this)) { throw new GenstarException("Can not add '" + attribute.getNameOnData() + "' attribute to '" + this.getGeneratorName() + 
				"' population. Because attribute's population is " + attribute.getPopulationGenerator() + " (different from " + this + " population)."); }
		
		
		attributes.put(attribute.getNameOnData(), attribute);
	}
	
	@Override public void removeAttribute(final AbstractAttribute attribute) {
		throw new UnsupportedOperationException("Not yet implemented");
		// TODO implement it!
	}
	
}
