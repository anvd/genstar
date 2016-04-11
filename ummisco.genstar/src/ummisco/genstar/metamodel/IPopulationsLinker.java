package ummisco.genstar.metamodel;

import java.util.List;

import ummisco.genstar.exception.GenstarException;

public interface IPopulationsLinker { 

	public abstract void setTotalRound(final int totalRound); 
	
	public abstract int getTotalRound();
	
	public abstract int getCurrentRound();
	
	public abstract List<IPopulation> getPopulations();

	public abstract void establishRelationship(final List<IPopulation> populations) throws GenstarException;
}
