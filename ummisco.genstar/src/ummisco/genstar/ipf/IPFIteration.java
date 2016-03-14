package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.List;

import ummisco.genstar.exception.GenstarException;

public abstract class IPFIteration<D, C, M> {
	
	protected D data;

	protected int iteration;
	
	protected IPF<D, C, M> ipf;
	
	protected List<M> marginals;
	
	protected int entitiesToGenerate = -1;
	
	
	protected IPFIteration(final IPF<D, C, M> ipf, final int iteration, final D data) throws GenstarException {
		if (ipf == null) { throw new GenstarException("'ipf' parameter can not be null"); }
		if (iteration < 0) { throw new GenstarException("'iteration' can not be negative"); }
		
		this.ipf = ipf;
		this.data = data;
		this.iteration = iteration;
		this.marginals = new ArrayList<M>();
		
		computeMarginals();
	}
	
	protected abstract void computeMarginals() throws GenstarException;
	
	public IPF<D, C, M> getIPF() {
		return ipf;
	}
	
	public int getIteration() {
		return iteration;
	}
	
	public abstract D getCopyData();
	
	public M getMarginals(final int dimension) throws GenstarException {
		if (dimension < 0 || dimension > (marginals.size() - 1)) { throw new GenstarException("Invalid dimension value (" + dimension  + "). Accepted value : (0.." + (marginals.size() - 1) + ")"); }
		return marginals.get(dimension);
	}
	
	public abstract IPFIteration<D, C, M> nextIteration() throws GenstarException;
	
	public abstract int getNbOfEntitiesToGenerate();
}
