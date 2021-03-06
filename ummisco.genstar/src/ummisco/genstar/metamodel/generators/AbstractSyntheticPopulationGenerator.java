package ummisco.genstar.metamodel.generators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;

public abstract class AbstractSyntheticPopulationGenerator implements ISyntheticPopulationGenerator {
	
	protected String generatorName;
	
	protected String populationName = "no-name population";
	
	protected List<AbstractAttribute> attributes;

	protected int nbOfEntities = 100;
	
	protected Map<String, String> properties = Collections.EMPTY_MAP;


	public AbstractSyntheticPopulationGenerator(final String generatorName) throws GenstarException {
		if (generatorName == null || generatorName.trim().length() == 0) { throw new GenstarException("'generatorName' parameter can neither be null nor empty"); }
		
		this.generatorName = generatorName;
		this.attributes = new ArrayList<AbstractAttribute>();
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
		if (nbOfEntities <= 0) { 
			throw new IllegalArgumentException("'nbOfEntities' must be a positive integer"); 
		}
		this.nbOfEntities = nbOfEntities;
	}

	@Override public List<AbstractAttribute> getAttributes() {
		return new ArrayList<AbstractAttribute>(attributes);
	}
	
	@Override public boolean containAttribute(final AbstractAttribute attribute) {
		if (attribute == null) { return false; }
		
		return attributes.contains(attribute);
	}
	
	@Override public AbstractAttribute getAttributeByNameOnData(final String attributeNameOnData) {
		if (attributeNameOnData == null) { throw new IllegalArgumentException("'attributeNameOnData' parameter can not be null"); }

		for (AbstractAttribute attr : attributes) { if (attr.getNameOnData().equals(attributeNameOnData)) return attr; }
		return null;
	}
	
	@Override public AbstractAttribute getAttributeByNameOnEntity(final String attributeNameOnEntity) {
		if (attributeNameOnEntity == null) { throw new IllegalArgumentException("'attributeNameOnEntity' parameter can not be null"); }

		for (AbstractAttribute attr : attributes) { if (attr.getNameOnEntity().equals(attributeNameOnEntity)) return attr; }
		return null;
	}

	
	@Override public void addAttribute(final AbstractAttribute attribute) throws GenstarException {
		if (attribute == null) { throw new GenstarException("'attribute' parameter can not be null"); }
		if (getAttributeByNameOnData(attribute.getNameOnData()) != null) { throw new GenstarException("'" + generatorName + "' population already contains '" + attribute.getNameOnData() + "' attribute."); }
		if (!attribute.getPopulationGenerator().equals(this)) { throw new GenstarException("Can not add '" + attribute.getNameOnData() + "' attribute to '" + this.getGeneratorName() + 
				"' population. Because attribute's population is " + attribute.getPopulationGenerator() + " (different from " + this + " population)."); }
		
		// not support more than 1 identity attribute
//		if (attribute.isIdentity()) {
//			for (AbstractAttribute attr : attributes) {
//				if (attr.isIdentity()) {
//					throw new GenstarException("A population generator can not contain more than 2 identity attributes (" + attr.getNameOnData() + ", " + attribute.getNameOnData() + ")");
//				}
//			}
//		}
		
		attributes.add(attribute);
	}
	
	@Override public void removeAttribute(final AbstractAttribute attribute) {
		throw new UnsupportedOperationException("Not yet implemented");
		// TODO implement it!
	}
	
	@Override public Map<String, String> getProperties() {
		return new HashMap<String, String>(properties);
	}
	
	@Override public String getProperty(final String name) {
		return properties.get(name);
	}
	
	@Override public void setProperty(final String name, final String value) throws GenstarException {
		if (name == null || value == null) { throw new GenstarException("Parameters name, value can not be null"); }
		
		if (properties == Collections.EMPTY_MAP) { properties = new HashMap<String, String>(); }
		properties.put(name, value);
	}
}
