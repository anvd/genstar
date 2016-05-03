package ummisco.genstar.ipf;

import java.util.List;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;

public class IpfFactory {
	
	public static Ipf createIPF(final IpfGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' can not be null"); }
		
		// initialize nWayIPF basing on the number of controlled attributes
		List<AbstractAttribute> controlledAttributes = generationRule.getControlledAttributes();
		int nbOfControlledAttributes = controlledAttributes.size();
		
		switch (nbOfControlledAttributes) {
			case 2: { return new TwoWayIpf(generationRule); }
			
			case 3: { return new ThreeWayIpf(generationRule); }
			
			case 4: { return new FourWayIpf(generationRule); }
			
			case 5: { return new FiveWayIpf(generationRule); }
			
			case 6: { return new SixWayIpf(generationRule); }
			
			default: { throw new GenstarException("Can not handle " + nbOfControlledAttributes + " controlled attributes."); }
		}
	}
}
