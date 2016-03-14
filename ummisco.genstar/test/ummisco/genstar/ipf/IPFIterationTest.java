package ummisco.genstar.ipf;

import static org.junit.Assert.*;

import java.util.List;

import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;

@RunWith(JMockit.class)
public class IPFIterationTest {

	private class MockIPF extends IPF<double[][], int[], double[]> {
		public MockIPF(SampleDataGenerationRule generationRule) throws GenstarException {
			super(generationRule);
		}

		public void fit() {}

		@Override
		public double[][] getData() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int[] getControls(int dimension) throws GenstarException {
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

		@Override
		public AbstractAttribute getControlledAttribute(int dimension)
				throws GenstarException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected int getNbOfControlledAttributes() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		protected void initializeData() throws GenstarException {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void computeControls() throws GenstarException {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected IPFIteration<double[][], int[], double[]> createIPFIteration()
				throws GenstarException {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private class MockedIPFIteration extends IPFIteration<double[][], int[], double[]> {
		
		public MockedIPFIteration(MockIPF ipf) throws GenstarException {
			super(ipf, 0, ipf.getData()); // TODO ipf.getData() == null
		}

		@Override
		public IPFIteration nextIteration() {
			return null;
		}

		@Override
		public double[][] getCopyData() {
			return null;
		}

		@Override
		public double[] getMarginals(int dimension) throws GenstarException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getNbOfEntitiesToGenerate() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		protected void computeMarginals() throws GenstarException {
			// TODO Auto-generated method stub
			
		}
	}
	
	
	@Test public void testInitializeSucessfullyIPFIteration(@Mocked MockIPF ipf) throws GenstarException {
		MockedIPFIteration ipfIteration = new MockedIPFIteration(ipf);
		
		assertTrue(ipfIteration.getIteration() == 0);
		assertTrue(ipfIteration.getIPF().equals(ipf));
	}
	
	@Test(expected = NullPointerException.class) public void testInitializeIPFIterationWithNullIPF() throws GenstarException {
		new MockedIPFIteration(null);
	}
}
