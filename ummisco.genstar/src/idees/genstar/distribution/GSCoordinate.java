package idees.genstar.distribution;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import idees.genstar.distribution.exception.MatrixCoordinateException;
import idees.genstar.distribution.innerstructure.ACoordinate;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class GSCoordinate extends ACoordinate<AbstractAttribute, AttributeValue> {

	public GSCoordinate(Set<AttributeValue> coordinate) throws MatrixCoordinateException {
		super(coordinate);
	}

	@Override
	public Set<AbstractAttribute> getDimensions() {
		return values().stream().map(AttributeValue::getAttribute).collect(Collectors.toSet());
	}

	@Override
	public Map<AbstractAttribute, AttributeValue> getMap() {
		return values().stream().collect(Collectors.toMap(v -> v.getAttribute(), v -> v));
	}

	@Override
	protected boolean isCoordinateSetComplient(Set<AttributeValue> coordinateSet) {
		Set<AbstractAttribute> attributeSet = coordinateSet.stream().map(av -> av.getAttribute()).collect(Collectors.toSet());
		if(attributeSet.size() == coordinateSet.size())
			return true;
		return false;
	}

}
