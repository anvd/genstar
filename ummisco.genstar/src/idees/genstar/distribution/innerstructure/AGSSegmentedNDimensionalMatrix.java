package idees.genstar.distribution.innerstructure;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import idees.genstar.control.AControl;
import idees.genstar.control.ControlFrequency;
import idees.genstar.distribution.exception.IllegalDistributionCreation;
import idees.genstar.distribution.exception.IllegalNDimensionalMatrixAccess;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public abstract class AGSSegmentedNDimensionalMatrix<T extends Number> implements
		InDimensionalMatrix<AbstractAttribute, AttributeValue, T> {

	protected final Set<AGSFullNDimensionalMatrix<T>> jointDistributionSet;
	
	private AGSSegmentedNDimensionalMatrix(){
		jointDistributionSet = new HashSet<>();
	}
	
	public AGSSegmentedNDimensionalMatrix(Set<AGSFullNDimensionalMatrix<T>> jointDistributionSet) throws IllegalDistributionCreation{
		this();
		if(jointDistributionSet.isEmpty())
			throw new IllegalDistributionCreation("Not distributions to fill in the conditional distribution", jointDistributionSet); 
		this.jointDistributionSet.addAll(jointDistributionSet);
		if(jointDistributionSet.stream().map(jd -> jd.isIndividualFrameOfReference()).collect(Collectors.toSet()).size() > 1)
			throw new IllegalDistributionCreation("Divergent frame of reference among sub joint distribution", jointDistributionSet);
		this.alignDistributions(jointDistributionSet.stream().sorted((jd1, jd2) -> jd2.getConcretSize() - jd1.getConcretSize()).findFirst().get());
	}

	private void alignDistributions(AGSFullNDimensionalMatrix<T> gsJointDistribution) {
		// TODO Auto-generated method stub
		
	}
	
	// ---------------- Getters ---------------- //
	

	@Override
	public Set<AbstractAttribute> getDimensions() {
		return jointDistributionSet.stream().flatMap(jd -> jd.getDimensions().stream()).collect(Collectors.toSet());
	}
	
	@Override
	public AbstractAttribute getDimension(AttributeValue aspect) throws IllegalNDimensionalMatrixAccess {
		return getDimensions().stream().filter(d -> d.valuesOnData().contains(aspect)).findFirst().get();
	}
	
	@Override
	public Set<AttributeValue> getAspects() {
		return getDimensions().stream().flatMap(d -> d.valuesOnData().stream()).collect(Collectors.toSet());
	}

	@Override
	public Set<AttributeValue> getAspects(AbstractAttribute dimension) throws IllegalNDimensionalMatrixAccess {
		return Collections.unmodifiableSet(dimension.valuesOnData());
	}

	@Override
	public int getTheoreticalSpaceSize() {
		return this.getDimensions().stream().mapToInt(d -> d.valuesOnData().size()).reduce(1, (s1, s2) -> s1 * s2);
	}

	@Override
	public int getConcretSize() {
		return jointDistributionSet.stream().mapToInt(AGSFullNDimensionalMatrix::getConcretSize).sum();
	}
	
	@Override
	public ACoordinate<AbstractAttribute, AttributeValue> getEmptyCoordinate() {
		return jointDistributionSet.iterator().next().getEmptyCoordinate();
	}
	
	// ---------------------- Getters ---------------------- //
	
	@Override
	public Map<ACoordinate<AbstractAttribute, AttributeValue>, AControl<T>> getMatrix(){
		Map<ACoordinate<AbstractAttribute, AttributeValue>, AControl<T>> matrix = new HashMap<>();
		for(AGSFullNDimensionalMatrix<T> jd : jointDistributionSet)
			matrix.putAll(jd.getMatrix());
		return matrix;
	}
	
	@Override
	public AControl<T> getVal(ACoordinate<AbstractAttribute, AttributeValue> coordinate) throws IllegalNDimensionalMatrixAccess {
		return getVal(coordinate.values());
	}

	@Override
	public AControl<T> getVal(AttributeValue aspect) throws IllegalNDimensionalMatrixAccess {
		AControl<T> val = null;
		for(AGSFullNDimensionalMatrix<T> distribution : jointDistributionSet
				.stream().filter(jd -> jd.getDimensions().contains(aspect.getAttribute())).collect(Collectors.toList()))
			if(val == null)
				val = distribution.getVal(aspect);
			else if(!val.getValue().equals(distribution.getVal(aspect).getValue()))
				throw new IllegalNDimensionalMatrixAccess("Incongruent probability in underlying distributions");
		return val;
	}

	@Override
	public AControl<T> getVal(Collection<AttributeValue> aspects) throws IllegalNDimensionalMatrixAccess {
		Map<AbstractAttribute, Collection<AttributeValue>> coordinates = new HashMap<>();
		for(AttributeValue val : aspects){
			if(coordinates.containsKey(val.getAttribute()))
				coordinates.get(val.getAttribute()).add(val);
			else
				coordinates.put(val.getAttribute(), new HashSet<>(Arrays.asList(val)));
		}
		AControl<T> conditionalProba = getIdentityProductVal();
		Set<AttributeValue> includedProbaDimension = new HashSet<>();
		for(AbstractAttribute att : coordinates.keySet()){
			AControl<T> localProba = getNulVal();
			for(AGSFullNDimensionalMatrix<T> distribution : jointDistributionSet
					.stream().filter(jd -> jd.getDimensions().contains(att)).collect(Collectors.toList())){
				Set<AbstractAttribute> hookAtt = distribution.getDimensions()
						.stream().filter(d -> includedProbaDimension.contains(d)).collect(Collectors.toSet());
				if(hookAtt.isEmpty()){
					localProba = distribution.getVal(coordinates.get(att));  
				} else {
					Set<AttributeValue> hookVals = hookAtt.stream().flatMap(a -> a.valuesOnData().stream()).collect(Collectors.toSet());
					Set<AttributeValue> localVals = new HashSet<>(hookVals);
					localVals.addAll(coordinates.get(att));
					localProba.multiply(distribution.getVal(localVals)
							.getRowProduct(new ControlFrequency(1d / distribution.getVal(hookVals).getValue().doubleValue())));
				}
			}	
			includedProbaDimension.addAll(coordinates.get(att));
			conditionalProba.multiply(localProba);
		}
		return conditionalProba;
	}
	
}
