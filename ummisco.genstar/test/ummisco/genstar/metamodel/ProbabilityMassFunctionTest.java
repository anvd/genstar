package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ummisco.genstar.data.NationalLevelDistribution;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.ProbabilityMassFunction;
import ummisco.genstar.metamodel.attributes.RangeValue;

public class ProbabilityMassFunctionTest {

	private List<RangeValue> liveBirthOrderAgeRanges;
	
	public ProbabilityMassFunctionTest() throws GenstarException {
		liveBirthOrderAgeRanges = new ArrayList<RangeValue>();
		for (int[] order : NationalLevelDistribution.liveBirthOrder) {
			liveBirthOrderAgeRanges.add(new RangeValue(DataType.INTEGER, Integer.toString(order[0]), Integer.toString(order[1])));
		}
	}
	
	@Test public void testNextValue() {
		ProbabilityMassFunction firstPmf = new ProbabilityMassFunction(NationalLevelDistribution.firstBirthOrder);
		assertTrue(liveBirthOrderAgeRanges.contains(firstPmf.nextValue()));
		
		ProbabilityMassFunction secondPmf = new ProbabilityMassFunction(NationalLevelDistribution.secondBirthOrder);
		assertTrue(liveBirthOrderAgeRanges.contains(secondPmf.nextValue()));
		
		ProbabilityMassFunction thirdPmf = new ProbabilityMassFunction(NationalLevelDistribution.thirdBirthOrder);
		assertTrue(liveBirthOrderAgeRanges.contains(thirdPmf.nextValue()));
		
		ProbabilityMassFunction fourthPmf = new ProbabilityMassFunction(NationalLevelDistribution.fourthBirthOrder);
		assertTrue(liveBirthOrderAgeRanges.contains(fourthPmf.nextValue()));
	}
}
