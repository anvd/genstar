package ummisco.genstar.ipf;

import java.util.List;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;

public class IPFFactory {
	
	public static IPF createIPF(final SampleDataGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' can not be null"); }
		
		// create TwoWayIPF or ThreeWayIPF basing on the number of controlled attributes
		List<AbstractAttribute> controlledAttributes = generationRule.getControlledAttributes();
		
		if (controlledAttributes.size() == 2) {
			return new TwoWayIPF(generationRule);
		} else if (controlledAttributes.size() == 3) {
			return new ThreeWayIPF(generationRule);
		} else {
			throw new GenstarException("Can not handle " + controlledAttributes.size() + " controlled attributes.");
		}
	}
}
