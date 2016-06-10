package idees.genstar.distribution;

import java.util.Map;
import java.util.Set;

import idees.genstar.configuration.GSMetaDataType;
import idees.genstar.control.AControl;
import idees.genstar.control.ControlContingency;
import idees.genstar.datareader.GSDataParser;
import idees.genstar.distribution.innerstructure.ACoordinate;
import idees.genstar.distribution.innerstructure.AGSFullNDimensionalMatrix;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;

/**
 * 
 * TODO: javadoc 
 * 
 * @author kevinchapuis
 *
 */
public class GSContingencyTable extends AGSFullNDimensionalMatrix<Integer> {
	
	public GSContingencyTable(Map<AbstractAttribute, Set<AttributeValue>> dimensionAspectMap) {
		super(dimensionAspectMap, GSMetaDataType.ContingenceTable);
	}
	
	@Override
	public AControl<Integer> getNulVal() {
		return new ControlContingency(0);
	}
	
	@Override
	public AControl<Integer> getIdentityProductVal() {
		return new ControlContingency(1);
	}
	
	@Override
	public AControl<Integer> parseVal(GSDataParser parser, String val){
		if(!parser.getValueType(val).equals(DataType.INTEGER))
			return getNulVal();
		return new ControlContingency(Integer.valueOf(val));
	}

	@Override
	public ACoordinate<AbstractAttribute, AttributeValue> draw() {
		// TODO Auto-generated method stub
		return null;
	}
	

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
			matrix.put(coordinate, new ControlContingency(value.getValue().intValue()));
			return true;
		}
		return false;
	}
			
}
