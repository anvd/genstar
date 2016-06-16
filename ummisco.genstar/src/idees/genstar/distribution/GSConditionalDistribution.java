package idees.genstar.distribution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import idees.genstar.configuration.GSMetaDataType;
import idees.genstar.control.AControl;
import idees.genstar.control.ControlFrequency;
import idees.genstar.datareader.GSDataParser;
import idees.genstar.distribution.exception.IllegalDistributionCreation;
import idees.genstar.distribution.exception.MatrixCoordinateException;
import idees.genstar.distribution.innerstructure.ACoordinate;
import idees.genstar.distribution.innerstructure.AGSFullNDimensionalMatrix;
import idees.genstar.distribution.innerstructure.AGSSegmentedNDimensionalMatrix;
import idees.genstar.distribution.sampler.GSStreamSample;
import idees.genstar.distribution.sampler.IGSSampler;
import idees.genstar.util.GSPerformanceUtil;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;

public class GSConditionalDistribution extends AGSSegmentedNDimensionalMatrix<Double> {

	private static boolean DEBUG_SYSO = false;
	
	private IGSSampler<ACoordinate<AbstractAttribute, AttributeValue>> sampler;

	public GSConditionalDistribution(Set<AGSFullNDimensionalMatrix<Double>> jointDistributionSet)
			throws IllegalDistributionCreation {
		super(jointDistributionSet);
		this.sampler = createSampler();
	}


	// -------------------- Main contract -------------------- //

	@Override
	public ACoordinate<AbstractAttribute, AttributeValue> draw() {
		return this.sampler.draw();
	}

	// --------------- data frame of reference --------------- //

	@Override
	public boolean isIndividualFrameOfReference() {
		return jointDistributionSet.iterator().next().isIndividualFrameOfReference();
	}

	@Override
	public GSMetaDataType getMetaDataType() {
		return jointDistributionSet.iterator().next().getMetaDataType();
	}

	@Override
	public boolean setMetaDataType(GSMetaDataType metaDataType) {
		return false;
	}

	// ------------------------- Accessors ------------------------- //

	@Override
	public AControl<Double> getNulVal() {
		return new ControlFrequency(0d);
	}

	@Override
	public AControl<Double> getIdentityProductVal() {
		return new ControlFrequency(1d);
	}

	// ------------------ Setters ------------------ //

	@Override
	public boolean addValue(ACoordinate<AbstractAttribute, AttributeValue> coordinates, AControl<? extends Number> value) {
		Set<AGSFullNDimensionalMatrix<Double>> jds = jointDistributionSet
				.stream().filter(jd -> jd.getDimensions().equals(coordinates.getDimensions())).collect(Collectors.toSet());
		return jds.iterator().next().addValue(coordinates, value);
	}

	@Override
	public boolean setValue(ACoordinate<AbstractAttribute, AttributeValue> coordinates, AControl<? extends Number> value) {
		Set<AGSFullNDimensionalMatrix<Double>> jds = jointDistributionSet
				.stream().filter(jd -> jd.getDimensions().equals(coordinates.getDimensions())).collect(Collectors.toSet());
		if(jds.size() != 1)
			return false;
		return jds.iterator().next().setValue(coordinates, value);
	}

	@Override
	public boolean removeValue(ACoordinate<AbstractAttribute, AttributeValue> coordinate) {
		Set<AGSFullNDimensionalMatrix<Double>> jds = jointDistributionSet
				.stream().filter(jd -> jd.getDimensions().equals(coordinate.getDimensions())).collect(Collectors.toSet());
		if(jds.size() != 1)
			return false;
		return jds.iterator().next().removeValue(coordinate);
	}

	// ------------------ Utilities ------------------ //

	@Override
	public boolean isCoordinateCompliant(ACoordinate<AbstractAttribute, AttributeValue> coordinate) {
		return jointDistributionSet.stream().anyMatch(jd -> jd.isCoordinateCompliant(coordinate));
	}

	@Override
	public AControl<Double> parseVal(GSDataParser parser, String val) {
		if(parser.getValueType(val).equals(DataType.STRING) || parser.getValueType(val).equals(DataType.BOOL))
			return getNulVal();
		return new ControlFrequency(Double.valueOf(val));
	}

	@Override
	public String toCsv(char csvSeparator) {
		// TODO Auto-generated method stub
		return null;
	}

	// -------------- Back office -------------- //

	private IGSSampler<ACoordinate<AbstractAttribute, AttributeValue>> createSampler() 
			throws IllegalDistributionCreation, MatrixCoordinateException{
		GSPerformanceUtil gspu = new GSPerformanceUtil("Compute independant-hypothesis-joint-distribution from conditional distribution\nTheoretical size = "+
				this.getTheoreticalSpaceSize(), DEBUG_SYSO);
		Map<Set<AttributeValue>, Double> sampleDistribution = new HashMap<>();
		// Store the attributes that have been allocated
		Set<AbstractAttribute> allocatedAttribut = new HashSet<>();
		// Begin the algorithm
		gspu.getStempPerformance(0);
		for(AGSFullNDimensionalMatrix<Double> jd : jointDistributionSet.stream()
				.sorted((jd1, jd2) -> jd2.getConcretSize() - jd1.getConcretSize()).collect(Collectors.toList())){
			// Collect attribute in the schema for which a probability have already been calculated
			Set<AbstractAttribute> hookAtt = jd.getDimensions()
					.stream().filter(att -> allocatedAttribut.contains(att)).collect(Collectors.toSet());

			gspu.sysoStempPerformance(0d, this);
			// If "hookAtt" is empty fill the proxy distribution with conditional probability of this joint distribution
			if(sampleDistribution.isEmpty()){
				sampleDistribution.putAll(jd.getMatrix().entrySet()
						.parallelStream().collect(Collectors.toMap(e -> new HashSet<>(e.getKey().values()), e -> e.getValue().getValue())));

				gspu.sysoStempPerformance(1d, this);
				System.out.println(sampleDistribution.keySet().parallelStream().mapToInt(c -> c.size()).average().getAsDouble()+" attributs ("+
						Arrays.toString(jd.getDimensions().stream().map(d -> d.getNameOnData()).toArray())+")");

				// Else, take into account known conditional probabilities in order to add new attributes
			} else {
				int j = 1;
				Map<Set<AttributeValue>, Double> newSampleDistribution = new HashMap<>();
				for(Set<AttributeValue> indiv : sampleDistribution.keySet()){
					Set<AttributeValue> hookVal = indiv.stream().filter(val -> hookAtt.contains(val.getAttribute())).collect(Collectors.toSet());
					Map<ACoordinate<AbstractAttribute, AttributeValue>, AControl<Double>> coordsHooked = jd.getMatrix().entrySet()
							.parallelStream().filter(e -> e.getKey().containsAll(hookVal))
							.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
					double summedProba = coordsHooked.values().stream().reduce(getNulVal(), (v1, v2) -> v1.add(v2)).getValue();
					for(ACoordinate<AbstractAttribute, AttributeValue> newIndivVal : coordsHooked.keySet()){
						Set<AttributeValue> newIndiv = new HashSet<>(indiv);
						newIndiv.addAll(newIndivVal.values());
						double newProba = sampleDistribution.get(indiv) * coordsHooked.get(newIndivVal).getValue() / summedProba;
						if(newProba > 0d)
							newSampleDistribution.put(newIndiv, newProba);
					}
					if(j++ % (sampleDistribution.size() / 10) == 0)
						gspu.sysoStempPerformance(j * 1d / sampleDistribution.size(), this);
				}
				if(sampleDistribution.size() >= newSampleDistribution.size())
					throw new IllegalDistributionCreation(Arrays.toString(jd.getDimensions().toArray()), jointDistributionSet);
				System.out.println("-------------------------\nFrom "+sampleDistribution.keySet().parallelStream().mapToInt(c -> c.size()).average().getAsDouble()+
						"To "+newSampleDistribution.keySet().parallelStream().mapToInt(c -> c.size()).average().getAsDouble()+" attributs ("+
						Arrays.toString(jd.getDimensions().stream().map(d -> d.getNameOnData()).toArray())+")"
						+ "\n-------------------------");
				sampleDistribution = newSampleDistribution;
			}
			allocatedAttribut.addAll(jd.getDimensions());
			gspu.resetStempProp();
			gspu.sysoStempPerformance(1, this);
		}
		long wrongPr = sampleDistribution.keySet().parallelStream().filter(c -> c.size() != getDimensions().size()).count();
		double avrSize =  sampleDistribution.keySet().parallelStream().mapToInt(c -> c.size()).average().getAsDouble();
		if(wrongPr != 0)
			throw new IllegalDistributionCreation("Some sample indiv ("+( Math.round(Math.round(wrongPr * 1d / sampleDistribution.size() * 100)))+"%) have not all attributs (average attributs nb = "+avrSize+")", jointDistributionSet);
		return new GSStreamSample(sampleDistribution.entrySet().parallelStream().collect(Collectors.toMap(e -> new GSCoordinate(e.getKey()), e -> e.getValue())));
	}

}
