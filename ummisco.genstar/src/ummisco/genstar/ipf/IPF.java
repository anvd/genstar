package ummisco.genstar.ipf;

import java.util.List;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;

public abstract class IPF {
	
	protected int maxIteration = 3;
	
	protected SampleDataGenerationRule generationRule = null;
	
	protected List<IPFIteration> iterations;
	
	protected List<AttributeValuesFrequency> selectionProbabilities;

	
	protected IPF(final SampleDataGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' can not be null"); }
		this.generationRule = generationRule;
	}
	
	public void setMaxIteration(final int maxIteration) {
		if (maxIteration <= 0) { throw new IllegalArgumentException("'maxIteration' must be a positive integer."); }
		
		this.maxIteration = maxIteration;
	}
	
	public int getMaxIteration() {
		return maxIteration;
	}
	
	public SampleDataGenerationRule getGenerationRule() {
		return generationRule;
	}
	
	public abstract <T> T getData();
	
	public abstract <K> K getControls(final int dimension) throws GenstarException;
		
	public abstract List<AttributeValue> getAttributeValues(final int dimension) throws GenstarException;
	
	public abstract void fit() throws GenstarException;

	public abstract List<AttributeValuesFrequency> getSelectionProbabilitiesOfLastIPFIteration() throws GenstarException;
	
	public int getNbOfEntitiesToGenerate() throws GenstarException {
		if (iterations == null) { fit(); }
		return iterations.get(iterations.size() - 1).getNbOfEntitiesToGenerate();
	}

	public abstract void printDebug() throws GenstarException;
	
}
