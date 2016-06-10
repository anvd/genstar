package idees.genstar;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import idees.genstar.distribution.innerstructure.InDimensionalMatrix;
import idees.genstar.util.GSPerformanceUtil;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.Population;
import ummisco.genstar.metamodel.population.PopulationType;

public class DistributionBasedGenerator implements ISyntheticPopulationGenerator {
	
	private static boolean LOGSYSO = false;
	
	private InDimensionalMatrix<AbstractAttribute, AttributeValue, Double> distribution;
	
	/*
	 *  TODO: based on distribution generator should provide a mean to synthesis more limited population
	 *  --> a subset of attribute could be used to fill the agent with
	 *  --> a solution could be to reset sampler to draw only from a limited part of the distribution
	 *  --> another one could be to change the distribution itself in the constructor
	 *  	This is the most elegant way: TODO a method in distribution that return a subset of the distribution
	 *  --> last dirty solution will be to draw and then remove unwanted attributes (not time saver)
	 */
	private Set<AbstractAttribute> attributes;
	private int popSize;
	
	public DistributionBasedGenerator(InDimensionalMatrix<AbstractAttribute, AttributeValue, Double> distribution, int popSize) {
		this.distribution = distribution;
		this.attributes = distribution.getDimensions();
		this.popSize = popSize;
	}
	
	@Override
	public boolean containAttribute(AbstractAttribute attribute) throws GenstarException {
		return distribution.getDimensions().contains(attribute);
	}

	@Override
	public Set<AbstractAttribute> getAttributes() {
		return Collections.unmodifiableSet(attributes);
	}

	@Override
	public AbstractAttribute getAttributeByNameOnData(String attributeNameOnData) throws GenstarException {
		List<AbstractAttribute> attList = distribution.getDimensions()
				.stream().filter(d -> d.getNameOnData().equals(attributeNameOnData)).collect(Collectors.toList());
		if(attList.size() != 1)
			throw new GenstarException("More than one or none attribute named: "+attributeNameOnData);
		return attList.get(0);
	}

	@Override
	public AbstractAttribute getAttributeByNameOnEntity(String attributeNameOnEntity) throws GenstarException {
		List<AbstractAttribute> attList = distribution.getDimensions()
				.stream().filter(d -> d.getNameOnEntity().equals(attributeNameOnEntity)).collect(Collectors.toList());
		if(attList.size() != 1)
			throw new GenstarException("More than one or none attribute named: "+attributeNameOnEntity);
		return attList.get(0);
	}

	@Override
	public boolean addAttribute(AbstractAttribute attribute) throws GenstarException {
		return this.attributes.add(attribute);
	}

	@Override
	public boolean removeAttribute(AbstractAttribute attribute) {
		return this.attributes.remove(attribute);
	}

	@Override
	public IPopulation generate() throws GenstarException {
		GSPerformanceUtil pu = new GSPerformanceUtil("Performance of population generation", LOGSYSO);
		IPopulation population = new Population(PopulationType.SYNTHETIC_POPULATION, getPopulationName(), attributes);
		pu.sysoStempPerformance(0d, this);
		for(int i = 0; i < popSize; i++){
			Map<AbstractAttribute, AttributeValue> indiv = distribution.draw().getMap();
			if(!indiv.keySet().equals(distribution.getDimensions()))
				System.out.println("Sorry An, my bad");
			population.createEntityWithAttributeValuesOnEntity(indiv);
			if((i + 1) % (popSize / 10) == 0)
				pu.sysoStempPerformance((i + 1d) / popSize, this);
		}
		return population;
	}
	
	// ---------------- Unused methods ---------------- //
	// TODO: refactor all the interface in order to be more concise
	// ---------------- ////////////// ---------------- // 
	

	@Override
	public void setID(int id) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getGeneratorName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPopulationName(String populationName) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPopulationName() {
		// TODO Auto-generated method stub
		return "Ceci n'est pas une population";
	}

	@Override
	public int getNbOfEntities() {
		// TODO: move to #generate() parameter method
		return popSize;
	}

	@Override
	public void setNbOfEntities(int nbOfEntities) {
		// TODO: move to #generate() parameter method
		this.popSize = nbOfEntities;
	}

	@Override
	public Map<String, String> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProperty(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperty(String name, String value) throws GenstarException {
		// TODO Auto-generated method stub

	}

}
