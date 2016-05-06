package ummisco.genstar;

import java.util.ArrayList;
import java.util.List;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.IPopulationsLinker;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.population.IPopulation;

public class GenstarGenerator {
	
	private int totalRound = 1;
	
	private IPopulationsLinker linker;
	
	private List<ISyntheticPopulationGenerator> populationGenerators;
	
	
	public GenstarGenerator() {
		populationGenerators = new ArrayList<ISyntheticPopulationGenerator>();
	}
	
	public void setTotalRound(final int totalRound) {
		if (totalRound <= 0) { throw new IllegalArgumentException("'totalRound' must be a positive integer"); }
		this.totalRound = totalRound;
	}
	
	public int getTotalRound() {
		return totalRound;
	}
	
	public void setPopulationsLinker(final IPopulationsLinker populationsLinker) {
		this.linker = populationsLinker;
	}
	
	public void addPopulationGenerator(final ISyntheticPopulationGenerator populationGenerator) {
		if (populationGenerator == null) { throw new IllegalArgumentException("'population' parameter can not be null"); }
		
		populationGenerators.add(populationGenerator);
	}
	
	public void run() throws GenstarException {
		List<IPopulation> populations = new ArrayList<IPopulation>(); 
		for (ISyntheticPopulationGenerator generator : populationGenerators) { populations.add(generator.generate()); }
		
		if (linker != null) { 
			linker.setTotalRound(totalRound);
			linker.establishRelationship(populations); 
		}
	}
}
