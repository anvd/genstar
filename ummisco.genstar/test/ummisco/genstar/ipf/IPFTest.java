package ummisco.genstar.ipf;

import static org.junit.Assert.assertTrue;

import java.util.List;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;

@RunWith(JMockit.class)
public class IPFTest {

	private class MockIPF extends IPF {
		public MockIPF(SampleDataGenerationRule generationRule) throws GenstarException {
			super(generationRule);
		}

		public void fit() {}

		@Override
		public Object getData() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object getControls(int dimension) throws GenstarException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List getAttributeValues(int dimension) throws GenstarException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List getSelectionProbabilitiesOfLastIPFIteration() throws GenstarException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void printDebug() throws GenstarException {
			// TODO Auto-generated method stub
			
		}
	}

	@Test(expected = GenstarException.class)
	public void testInitializeIPFWithNullGenerationRule() throws GenstarException {
		new MockIPF(null);
	}
	
	@Test public void testMaxIterationIsSetCorrectly(@Mocked SampleDataGenerationRule generationRule) throws GenstarException {
		
		IPF ipf = new MockIPF(generationRule);
		
		assertTrue(ipf.getMaxIteration() == 3);
		ipf.setMaxIteration(5);
		assertTrue(ipf.getMaxIteration() == 5);
	}
}
