package ummisco.genstar.ipf;

import static org.junit.Assert.*;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;

@RunWith(JMockit.class)
public class ThreeWayIterationTest {

	@Test public void testInitializeObjectSuccessfully(@Mocked final ThreeWayIPF ipf) throws GenstarException {
		
		// data[2][3][4]
		final double[][][] data = {
			{
				{ 1, 2, 3 },
				{ 4, 5, 6 }
			},
			{
				{ 7, 8, 9 },
				{ 10, 11, 12 }
			},
			{
				{ 13, 14, 15 },
				{ 16, 17, 18 }
			},
			{
				{ 19, 20, 21 },
				{ 22, 23, 24 }
			}
		};
		
		final int[][] rowControls = { // [3][4]
				{ 10, 20, 30 },
				{ 40, 50, 60 },
				{ 70, 80, 90 }
		};
		
		final int[][] columnControls = { // [2][4] 
			{ 10, 20 },
			{ 30, 40 },
			{ 50, 60 },
			{ 70, 80 }
		};
		
		final int[][] layerControls = { // [2][3]
			{ 10, 20 },
			{ 30, 40 },
			{ 50, 60 }
		};
		
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
			ipf.getControls(2); result = layerControls;
		}};
		
		ThreeWayIteration iteration = new ThreeWayIteration(ipf);
		assertTrue(iteration.getIteration() == 0);
		
		double[][][] iterationData = iteration.getData();
		for (int row=0; row<data.length; row++) {
			for (int column=0; column<data[0].length; column++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					assertTrue(iterationData[row][column][layer] == data[row][column][layer]);
				}
			}
		}
	}
	
	
	@Test public void testNextIteration(@Mocked final ThreeWayIPF ipf) throws GenstarException {
		
		// data[2][3][4]
		final double[][][] data = {
			{
				{ 1, 2, 3, 5 },
				{ 5, 6, 7, 8 },
				{ 9, 10, 11, 12 }
			},
			{
				{ 13, 14, 15, 16 },
				{ 17, 18, 19, 20 },
				{ 21, 22, 23, 24 }
			}
		};
		
		final int[][] rowControls = { // [3][4]
			{ 10, 20, 30, 40 },
			{ 50, 60, 70, 80 },
			{ 90, 100, 110, 120 },
		};
		
		final int[][] columnControls = { // [2][4] 
			{ 10, 20, 30, 40 },
			{ 50, 60, 70, 80 }
		};
		
		final int[][] layerControls = { // [2][3]
			{ 10, 20, 30 },
			{ 40, 50, 60 }
		};
		
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
			ipf.getControls(2); result = layerControls;
		}};
		
		ThreeWayIteration iteration0 = new ThreeWayIteration(ipf);
		ThreeWayIteration iteration1 = iteration0.nextIteration();
		
		
		double[][][] data0 = iteration0.getData();
		double[][][] data1 = iteration1.getData();
		
		
		// 1. compute row adjustments on data0 then use row adjustments to adjust data0
		for (int col=0; col<data0[0].length; col++) {
			for (int layer=0; layer<data0[0][0].length; layer++) {
				double rowMarginal = 0;
				for (int row=0; row<data0.length; row++) { rowMarginal += data0[row][col][layer]; }
				double rowAdjustment = ((double)rowControls[col][layer]) / rowMarginal; 
				
				for (int row=0; row<data0.length; row++) { data0[row][col][layer] = data0[row][col][layer] * rowAdjustment; }
			}
		}

		
		// 2. compute column adjustments on data0 then use column adjustments to adjust data0
		for (int row=0; row<data0.length; row++) {
			for (int layer=0; layer<data0[0][0].length; layer++) {
				double columnMarginal = 0;
				for (int col=0; col<data0[0].length; col++) { columnMarginal += data0[row][col][layer]; }
				double columnAdjustment = ((double) columnControls[row][layer]) / columnMarginal;
				
				for (int col=0; col<data0[0].length; col++) { data0[row][col][layer] = data0[row][col][layer] * columnAdjustment; }
			}
		}
		

		// 3. compute layer adjustments on data0 then use layer adjustments to adjust data0
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				double layerMarginal = 0;
				for (int layer=0; layer<data[0][0].length; layer++) { layerMarginal += data0[row][col][layer]; }
				double layerAdjustment = ((double) layerControls[row][col]) / layerMarginal;
				
				for (int layer=0; layer<data[0][0].length; layer++) { data0[row][col][layer] = data0[row][col][layer] * layerAdjustment; }
			}
		}
		
		
		// verify that data1 is correctly computed from data0
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) { data0[row][col][layer] = data1[row][col][layer];}
			}
		}
	}
	
	
	@Test public void testGetNbOfEntitiesToGenerate(@Mocked final ThreeWayIPF ipf) throws GenstarException {
		// data[2][3][4]
		final double[][][] data = {
			{
				{ 1, 2, 3, 5 },
				{ 5, 6, 7, 8 },
				{ 9, 10, 11, 12 }
			},
			{
				{ 13, 14, 15, 16 },
				{ 17, 18, 19, 20 },
				{ 21, 22, 23, 24 }
			}
		};
		
		final int[][] rowControls = { // [3][4]
			{ 10, 20, 30, 40 },
			{ 50, 60, 70, 80 },
			{ 90, 100, 110, 120 },
		};
		
		final int[][] columnControls = { // [2][4] 
			{ 10, 20, 30, 40 },
			{ 50, 60, 70, 80 }
		};
		
		final int[][] layerControls = { // [2][3]
			{ 10, 20, 30 },
			{ 40, 50, 60 }
		};
		
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
			ipf.getControls(2); result = layerControls;
		}};
		
		ThreeWayIteration iteration0 = new ThreeWayIteration(ipf);
		ThreeWayIteration iteration1 = iteration0.nextIteration();
		
		
		// data0
		double[][][] data0 = iteration0.getData();
		int sumData0 = 0;
		for (int row=0; row<data0.length; row++) {
			for (int column=0; column<data0[0].length; column++) {
				for (int layer=0; layer<data0[0][0].length; layer++) {
					sumData0 += Math.round(data0[row][column][layer]);
				}
			}
		}
		assertTrue(sumData0 == iteration0.getNbOfEntitiesToGenerate());
		
		
		// data1
		double[][][] data1 = iteration1.getData();
		int sumData1 = 0;
		for (int row=0; row<data0.length; row++) {
			for (int column=0; column<data1[0].length; column++) {
				for (int layer=0; layer<data1[0][0].length; layer++) {
					sumData1 += Math.round(data1[row][column][layer]);
				}
			}
		}
		assertTrue(sumData1 == iteration1.getNbOfEntitiesToGenerate());
	}
}
