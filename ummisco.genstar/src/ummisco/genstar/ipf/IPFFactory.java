package ummisco.genstar.ipf;

import java.util.List;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;

public class IPFFactory {
	
	public static IPF createIPF(final SampleDataGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' can not be null"); }
		
		// initialize nWayIPF basing on the number of controlled attributes
		List<AbstractAttribute> controlledAttributes = generationRule.getControlledAttributes();
		int nbOfControlledAttributes = controlledAttributes.size();
		
		switch (nbOfControlledAttributes) {
			case 2: { return new TwoWayIPF(generationRule); }
			
			case 3: { return new ThreeWayIPF(generationRule); }
			
			case 4: { return new FourWayIPF(generationRule); }
			
			case 5: { return new FiveWayIPF(generationRule); }
			
			case 6: { return new SixWayIPF(generationRule); }
			
			default: { throw new GenstarException("Can not handle " + nbOfControlledAttributes + " controlled attributes."); }
		}
	}
}
