package ummisco.genstar.ipf;

import static org.junit.Assert.*;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;

@RunWith(JMockit.class)
public class IPFIterationTest {

	private class MockedIPFIteration extends IPFIteration {

		protected MockedIPFIteration(IPF ipf, int iteration) throws GenstarException {
			super(ipf, iteration);
		}

		@Override
		public IPFIteration nextIteration() {
			return null;
		}

		@Override
		public double[][] getData() {
			return null;
		}
	}
	
	
	@Test public void testInitializeSucessfullyIPFIteration(@Mocked IPF ipf) throws GenstarException {
		MockedIPFIteration ipfIteration = new MockedIPFIteration(ipf, 1);
		
		assertTrue(ipfIteration.getIteration() == 1);
		assertTrue(ipfIteration.getIPF().equals(ipf));
	}
	
	@Test(expected = GenstarException.class) public void testInitializeIPFIterationWithNullIPF() throws GenstarException {
		new MockedIPFIteration(null, 1);
	}
	
	@Test(expected = GenstarException.class) public void testInitializeIPFIterationWithNegativeIteration(@Mocked IPF ipf) throws GenstarException {
		new MockedIPFIteration(ipf, -1);
	}
}
