package idees.genstar.distribution;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import idees.genstar.configuration.GSMetaDataType;
import idees.genstar.control.AControl;
import idees.genstar.control.ControlFrequency;
import idees.genstar.datareader.GSDataParser;
import idees.genstar.distribution.innerstructure.ACoordinate;
import idees.genstar.distribution.innerstructure.AGSFullNDimensionalMatrix;
import idees.genstar.distribution.sampler.GSStreamSample;
import idees.genstar.distribution.sampler.IGSSampler;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;

/**
 * TODO: javadoc
 * 
 * @author kevinchapuis
 *
 */
public class GSJointDistribution extends AGSFullNDimensionalMatrix<Double> {
	
	private IGSSampler<ACoordinate<AbstractAttribute, AttributeValue>> sampler = null;

	public GSJointDistribution(Map<AbstractAttribute, Set<AttributeValue>> dimensionAspectMap, GSMetaDataType metaDataType) {
		super(dimensionAspectMap, metaDataType);
	}
	
	// ----------------------- MAIN CONTRACT ----------------------- //
	
	@Override
	public ACoordinate<AbstractAttribute, AttributeValue> draw(){
		if(sampler == null)
			this.sampler = new GSStreamSample(matrix.entrySet()
					.parallelStream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().getValue())));
		return this.sampler.draw();
	}
	
	// ----------------------- SETTER CONTRACT ----------------------- //
	
	@Override
	public boolean addValue(ACoordinate<AbstractAttribute, AttributeValue> coordinates, AControl<? extends Number> value){
		if(matrix.containsKey(coordinates))
			return false;
		return setValue(coordinates, value);
	}

	@Override
	public boolean setValue(ACoordinate<AbstractAttribute, AttributeValue> coordinate, AControl<? extends Number> value){
		if(isCoordinateCompliant(coordinate)){
			coordinate.setHashIndex(matrix.size()+1);
			matrix.put(coordinate, new ControlFrequency(value.getValue().doubleValue()));
			return true;
		}
		return false;
	}
	
	// ----------------------- SIDE CONTRACT ----------------------- //
	
	@Override
	public AControl<Double> getNulVal() {
		return new ControlFrequency(0d);
	}
	

	@Override
	public AControl<Double> getIdentityProductVal() {
		return new ControlFrequency(1d);
	}

	@Override
	public AControl<Double> parseVal(GSDataParser parser, String val) {
		if(parser.getValueType(val).equals(DataType.STRING) || parser.getValueType(val).equals(DataType.BOOL))
			return getNulVal();
		return new ControlFrequency(parser.getDouble(val));
	}
	
}
