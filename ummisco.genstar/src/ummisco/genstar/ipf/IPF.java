package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;

public abstract class IPF<D, C, M> {
		
	protected D data;
	
	protected List<C> controls;
	
	protected List<AbstractAttribute> controlledAttributes;
	
	protected Map<Integer, List<AttributeValue>> controlledAttributeValues;
	
	protected SampleDataGenerationRule generationRule = null;
	
	protected List<IPFIteration<D, C, M>> iterations;
	
	protected List<AttributeValuesFrequency> selectionProbabilities;

	
	protected IPF(final SampleDataGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' can not be null"); }
		this.generationRule = generationRule;
		this.controls = new ArrayList<C>();
		this.controlledAttributes = new ArrayList<AbstractAttribute>();
		this.controlledAttributeValues = new HashMap<Integer, List<AttributeValue>>();
		
		// input parameters validation
		List<AbstractAttribute> controlledAttributes = generationRule.getControlledAttributes();
		
		if (controlledAttributes.size() != getNbOfControlledAttributes()) { throw new GenstarException("Invalid number of controlled attributes. Only accept " + getNbOfControlledAttributes() + " controlled attributes"); }
		Set<AbstractAttribute> attributeSet = new HashSet<AbstractAttribute>(controlledAttributes);
		if (attributeSet.size() != getNbOfControlledAttributes()) { throw new GenstarException("Some controlled attributes are duplicated"); }
		
		this.controlledAttributes.addAll(controlledAttributes);
		
		for (int i=0; i<this.controlledAttributes.size(); i++) {
			controlledAttributeValues.put(i, new ArrayList<AttributeValue>(this.controlledAttributes.get(i).valuesOnData()));
		}
		
		initializeData();
		computeControls();
	}
	
	protected abstract int getNbOfControlledAttributes();
	
	protected abstract void initializeData() throws GenstarException;
	
	protected abstract void computeControls() throws GenstarException;
	
	
	public SampleDataGenerationRule getGenerationRule() {
		return generationRule;
	}
	
	public D getData() {
		return data;
	}
	
	public C getControls(final int dimension) throws GenstarException {
		if (dimension < 0 || dimension > (controls.size() - 1)) { throw new GenstarException("Invalid dimension value (" + dimension  + "). Accepted value : (0.." + (controls.size() - 1) + ")"); }
		return controls.get(dimension);
	}
	
	public AbstractAttribute getControlledAttribute(final int dimension) throws GenstarException {
		if (dimension < 0 || dimension > (controlledAttributes.size() - 1)) { throw new GenstarException("Invalid dimension value (" + dimension  + "). Accepted value : (0.." + (controls.size() - 1) + ")"); }
		return controlledAttributes.get(dimension);
	}
		
	public List<AttributeValue> getAttributeValues(final int dimension) throws GenstarException {
		if (dimension < 0 || dimension > (controlledAttributes.size() - 1)) { throw new GenstarException("Invalid dimension value (" + dimension  + "). Accepted value : (0.." + (controls.size() - 1) + ")"); }
		return new ArrayList<AttributeValue>(controlledAttributeValues.get(dimension));
	}
	
	public void fit() throws GenstarException {
		if (iterations != null) {
			iterations.clear();
		} else {
			iterations = new ArrayList<IPFIteration<D, C, M>>();
		}

		if (selectionProbabilities != null) {
			selectionProbabilities.clear();
			selectionProbabilities = null;
		}
		
		IPFIteration<D, C, M> iteration = createIPFIteration();
		iterations.add(iteration);
		int maxIterations = generationRule.getMaxIterations();
		for (int iter=0; iter<maxIterations; iter++) {
			iteration = iteration.nextIteration();
			iterations.add(iteration);
		}		
	}
	
	protected abstract IPFIteration<D, C, M> createIPFIteration() throws GenstarException;

	public abstract List<AttributeValuesFrequency> getSelectionProbabilitiesOfLastIPFIteration() throws GenstarException;
	
	public int getNbOfEntitiesToGenerate() throws GenstarException {
		if (iterations == null) { fit(); }
		return iterations.get(iterations.size() - 1).getNbOfEntitiesToGenerate();
	}

	public abstract void printDebug() throws GenstarException;
	
}
