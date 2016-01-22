package ummisco.genstar.ipf;

import static org.junit.Assert.*;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;

@RunWith(JMockit.class)
public class TwoWayIterationTest {
	
	@Test public void testInitializeObjectSuccessfully(@Mocked final TwoWayIPF ipf) throws GenstarException {
		
		final double[][] data = {
				{ 1, 2 },
				{ 3, 4 },
				{ 5, 6 }
		};
		
		final int[] rowControls = { 10, 20, 30 };
		
		final int[] columnControls = { 30, 40 };
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
		}};
		
		TwoWayIteration iteration = new TwoWayIteration(ipf);
		assertTrue(iteration.getIteration() == 0);
		
		double[][] iterationData = iteration.getData();
		for (int row=0; row<data.length; row++) {
			for (int column=0; column<data[0].length; column++) {
				assertTrue(data[row][column] == iterationData[row][column]);
			}
		}
	}
	
	@Test(expected = GenstarException.class) public void testInitializeObjectWithNullData(@Mocked final TwoWayIPF ipf) throws GenstarException {
		final int[] rowControls = { 10, 20, 30 };
		
		final int[] columnControls = { 30, 40 };
		
		new Expectations() {{
			ipf.getData(); result = null;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
		}};
		
		new TwoWayIteration(ipf);
	}
	
	@Test public void testInitializeObjectWithZeroMarginals(@Mocked final ThreeWayIPF ipf) throws GenstarException {
		fail("Not yet implemented");
	}

	@Test(expected = GenstarException.class) public void testInitializeObjectWithNullRowControls(@Mocked final TwoWayIPF ipf) throws GenstarException {
		final double[][] data = {
				{ 1, 2 },
				{ 3, 4 },
				{ 5, 6 }
		};
		
		final int[] columnControls = { 30, 40 };
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = null;
			ipf.getControls(1); result = columnControls;
		}};
		
		new TwoWayIteration(ipf);
	}


	@Test(expected = GenstarException.class) public void testInitializeObjectWithNullColumnControls(@Mocked final TwoWayIPF ipf) throws GenstarException {
		final double[][] data = {
				{ 1, 2 },
				{ 3, 4 },
				{ 5, 6 }
		};
		
		final int[] rowControls = { 10, 20, 30 };
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = null;
		}};
		
		new TwoWayIteration(ipf);
	}
	

	@Test public void testNextIteration(@Mocked final TwoWayIPF ipf) throws GenstarException {
		final double[][] data = {
				{ 1, 2 },
				{ 3, 4 },
				{ 5, 6 }
		};
		
		final int[] rowControls = { 10, 20, 30 };
		
		final int[] columnControls = { 30, 40 };
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
		}};
		
		TwoWayIteration iteration0 = new TwoWayIteration(ipf);
		TwoWayIteration iteration1 = iteration0.nextIteration();
		
		double[][] data0 = iteration0.getData();
		double[][] data1 = iteration1.getData();
		
		// 1. compute row adjustments on data0 then use row adjustments to adjust data0
		for (int row=0; row<data.length; row++) {
			double rowMarginal = 0;
			for (int column=0; column<data[0].length; column++) { rowMarginal += data0[row][column]; }			
			double rowAdjustment = ((double)rowControls[row]) / rowMarginal;
			
			for (int column=0; column<data[0].length; column++) { data0[row][column] = data0[row][column] * rowAdjustment; }
		}
		
		
		// 2. compute column adjustments on data0 then use column adjustments to adjust data0
		for (int column=0; column<data[0].length; column++) {
			double columnMarginal = 0;
			for (int row=0; row<data.length; row++) { columnMarginal += data0[row][column]; }			
			double columnAdjustment = ((double)columnControls[column]) / columnMarginal;
			
			for (int row=0; row<data.length; row++) { data0[row][column] = data0[row][column] * columnAdjustment; }
		}
		
		
		// verify that data1 is correctly computed from data0
		for (int row=0; row<data0.length; row++) {
			for (int column=0; column<data0[0].length; column++) {
				assertTrue(data0[row][column] == data1[row][column]);
			}
		}
	}

	@Test public void testGetNbOfEntitiesToGenerate(@Mocked final TwoWayIPF ipf) throws GenstarException {
		final double[][] data = {
				{ 1, 2 },
				{ 3, 4 },
				{ 5, 6 }
		};
		
		final int[] rowControls = { 10, 20, 30 };
		
		final int[] columnControls = { 30, 40 };
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
		}};
		
		TwoWayIteration iteration0 = new TwoWayIteration(ipf);
		TwoWayIteration iteration1 = iteration0.nextIteration();

		
		// data0
		double[][] data0 = iteration0.getData();
		int sumData0 = 0;
		for (int row=0; row<data0.length; row++) {
			for (int column=0; column<data0[0].length; column++) {
				sumData0 += Math.round(data0[row][column]);
			}
		}
		assertTrue(sumData0 == iteration0.getNbOfEntitiesToGenerate());
		
		
		// data1
		double[][] data1 = iteration1.getData();
		int sumData1 = 0;
		for (int row=0; row<data1.length; row++) {
			for (int column=0; column<data1[0].length; column++) {
				sumData1 += Math.round(data1[row][column]);
			}
		}
		assertTrue(sumData1 == iteration1.getNbOfEntitiesToGenerate());
	}
}
