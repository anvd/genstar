package ummisco.genstar.ipf;

import java.util.List;

import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;

@RunWith(JMockit.class)
public class IpfTest {

	private class MockIPF extends Ipf<double[][], int[], double[]> {
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
		protected IpfIteration<double[][], int[], double[]> createIPFIteration()
				throws GenstarException {
			// TODO Auto-generated method stub
			return null;
		}
	}

	@Test(expected = GenstarException.class)
	public void testInitializeIPFWithNullGenerationRule() throws GenstarException {
		new MockIPF(null);
	}
	
}
