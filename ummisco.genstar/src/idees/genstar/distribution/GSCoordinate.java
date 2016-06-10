package idees.genstar.distribution;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import idees.genstar.distribution.innerstructure.ACoordinate;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class GSCoordinate extends ACoordinate<AbstractAttribute, AttributeValue> {

	public GSCoordinate(Set<AttributeValue> coordinate) {
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

}
