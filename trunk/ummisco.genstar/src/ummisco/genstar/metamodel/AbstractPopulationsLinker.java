package ummisco.genstar.metamodel;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractPopulationsLinker implements IPopulationsLinker {
	
	protected List<ISyntheticPopulation> populations;
	
	protected int totalRound = 1;
	protected int currentRound = 1;
	
	public AbstractPopulationsLinker() {
		populations = new ArrayList<ISyntheticPopulation>();
	}
	
	@Override public List<ISyntheticPopulation> getPopulations() {
		return populations;
	}
	
	@Override
	public void setTotalRound(final int totalRound) {
		if (totalRound <= 0) { throw new IllegalArgumentException("'totalRound' must be positive"); }
		this.totalRound = totalRound;
	}

	@Override
	public int getTotalRound() {
		return totalRound;
	}

	@Override
	public int getCurrentRound() {
		return currentRound;
	}
}
