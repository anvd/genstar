package ummisco.genstar.ipf;

import ummisco.genstar.exception.GenstarException;

public abstract class IPFIteration<T> {

	protected int iteration;
	
	protected IPF ipf;
	
	
	protected IPFIteration(final IPF ipf, final int iteration) throws GenstarException {
		if (ipf == null) { throw new GenstarException("'ipf' parameter can not be null"); }
		if (iteration < 0) { throw new GenstarException("'iteration' can not be negative"); }
		
		this.ipf = ipf;
		this.iteration = iteration;
	}
	
	public IPF getIPF() {
		return ipf;
	}
	
	public int getIteration() {
		return iteration;
	}
	
	public abstract <T> T getData();
	
	public abstract IPFIteration nextIteration() throws GenstarException;
}
