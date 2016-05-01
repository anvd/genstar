package ummisco.genstar.ipf;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

@RunWith(JMockit.class)
public class ThreeWayIterationTest {

	@Test public void testInitializeObjectSuccessfully(@Mocked final ThreeWayIpf ipf) throws GenstarException {
		
		// data[4][2][3]
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
		
		
		new Expectations() {{
			ipf.getData(); result = data;
		}};
		
		ThreeWayIteration iteration = new ThreeWayIteration(ipf);
		assertTrue(iteration.getIteration() == 0);
		
		double[][][] iterationData = iteration.getCopyData();
		for (int row=0; row<data.length; row++) {
			for (int column=0; column<data[0].length; column++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					assertTrue(iterationData[row][column][layer] == data[row][column][layer]);
				}
			}
		}
		
//		assertTrue(data.length == 4);
//		assertTrue(data[0].length == 2);
//		assertTrue(data[0][0].length == 3);
	}
	
	@Test(expected = GenstarException.class) public void testInitializeObjectWithZeroMarginals(@Mocked final ThreeWayIpf ipf, @Mocked final AbstractAttribute attribute, 
			@Mocked final AttributeValue attributeValue) throws GenstarException {
		// data[4][2][3]
		final double[][][] data = {
			{
				{ 0, 0, 0 },
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
		
		final int[][] rowControls = { // [2][3]
			{ 10, 20, 30 },
			{ 40, 50, 60 }
		};
		
		final int[][] columnControls = { // [4][3] 
			{ 10,   20,  30 },
			{ 40,   50,  60 },
			{ 70,   80,  90 },
			{ 100, 110, 120 }
		};
		
		final int[][] layerControls = { // [4][2]
			{ 10, 20 },
			{ 30, 40 },
			{ 50, 60 },
			{ 70, 80 }
		};

	
		final List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();
		attributeValues.add(attributeValue);
		
		new NonStrictExpectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
			ipf.getControls(2); result = layerControls;
			
			ipf.getAttributeValues(anyInt); result = attributeValues;
			attributeValues.get(anyInt); result = attributeValue;
			attribute.getNameOnData(); result = "dummy attribute name";
			attributeValue.toCsvString(); result = "dummy CSV string";
		}};
		
		new ThreeWayIteration(ipf);
		
//		assertTrue(rowControls.length == 2);
//		assertTrue(rowControls[0].length == 3);
//		assertTrue(columnControls.length == 4);
//		assertTrue(columnControls[0].length == 3);
//		assertTrue(layerControls.length == 4);
//		assertTrue(layerControls[0].length == 2);
	}

		
	@Test public void testNextIteration(@Mocked final ThreeWayIpf ipf) throws GenstarException {
		
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
		
		
		double[][][] data0 = iteration0.getCopyData();
		double[][][] data1 = iteration1.getCopyData();
		
		
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
				for (int layer=0; layer<data[0][0].length; layer++) { assertTrue(data0[row][col][layer] == data1[row][col][layer]); }
			}
		}
	}
	
	
	@Test public void testGetNbOfEntitiesToGenerate(@Mocked final ThreeWayIpf ipf) throws GenstarException {
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
		double[][][] data0 = iteration0.getCopyData();
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
		double[][][] data1 = iteration1.getCopyData();
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
