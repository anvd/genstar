package ummisco.genstar.ipf;

import static org.junit.Assert.assertTrue;

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
public class FourWayIterationTest {

	@Test public void testInitializeObjectSuccessfully(@Mocked final FourWayIpf ipf) throws GenstarException {
		
		// data[2][3][4][5]
		final double[][][][] data = {
			{
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				}
			},
			
			{
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				}
			},
				
		};
		
		
		new Expectations() {{
			ipf.getData(); result = data;
		}};
		
		FourWayIteration iteration = new FourWayIteration(ipf);
		assertTrue(iteration.getIteration() == 0);
		
		double[][][][] iterationData = iteration.getCopyData();
		for (int row=0; row<data.length; row++) {
			for (int column=0; column<data[0].length; column++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					for (int stack=0; stack<data[0][0][0].length; stack++) {
						assertTrue(iterationData[row][column][layer][stack] == data[row][column][layer][stack]);
					}
				}
			}
		}
		
		assertTrue(iterationData.length == data.length);
		assertTrue(iterationData[0].length == data[0].length);
		assertTrue(iterationData[0][0].length == data[0][0].length);
		assertTrue(iterationData[0][0][0].length == data[0][0][0].length);
	}

	@Test(expected = GenstarException.class) public void testInitializeObjectWithZeroMarginals(@Mocked final FourWayIpf ipf, @Mocked final AbstractAttribute attribute, 
			@Mocked final AttributeValue attributeValue) throws GenstarException {
		
		// data[2][3][4][5]
		final double[][][][] data = {
			{
				{
					{ 0, 0, 0, 0, 0 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				}
			},
			
			{
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				}
			},
				
		};
		
		final int[][][] rowControls = { // [3][4][5]
			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},

			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},

			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},
		};
		
		final int[][][] columnControls = { // [2][4][5]
			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},

			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},
		};
		
		final int[][][] layerControls = { // [2][3][5]
			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},

			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},
		};

		final int[][][] stackControls = { // [2][3][4]
				{
					{ 10, 20, 30, 40 },
					{ 10, 20, 30, 40 },
					{ 10, 20, 30, 40 }
				},

				{
					{ 10, 20, 30, 40 },
					{ 10, 20, 30, 40 },
					{ 10, 20, 30, 40 }
				},
		};

		final List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();
		attributeValues.add(attributeValue);
		
		new NonStrictExpectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
			ipf.getControls(2); result = layerControls;
			ipf.getControls(3); result = stackControls;
			
			ipf.getAttributeValues(anyInt); result = attributeValues;
			attributeValues.get(anyInt); result = attributeValue;
			attribute.getNameOnData(); result = "dummy attribute name";
			attributeValue.toCsvString(); result = "dummy CSV string";
		}};
		
		new FourWayIteration(ipf);
		 
	}

	@Test public void testNextIteration(@Mocked final FourWayIpf ipf) throws GenstarException {
		// data[2][3][4][5]
		final double[][][][] data = {
			{
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				}
			},
			
			{
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				}
			},
				
		};
		
		final int[][][] rowControls = { // [3][4][5]
			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},

			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},

			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},
		};
		
		final int[][][] columnControls = { // [2][4][5]
			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},

			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},
		};
		
		final int[][][] layerControls = { // [2][3][5]
			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},

			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},
		};

		final int[][][] stackControls = { // [2][3][4]
				{
					{ 10, 20, 30, 40 },
					{ 10, 20, 30, 40 },
					{ 10, 20, 30, 40 }
				},

				{
					{ 10, 20, 30, 40 },
					{ 10, 20, 30, 40 },
					{ 10, 20, 30, 40 }
				},
		};

		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
			ipf.getControls(2); result = layerControls;
			ipf.getControls(3); result = stackControls;
		}};
		
		FourWayIteration iteration0 = new FourWayIteration(ipf);
		FourWayIteration iteration1 = iteration0.nextIteration();
		
		
		double[][][][] data0 = iteration0.getCopyData();
		double[][][][] data1 = iteration1.getCopyData();
		
		
		// 1. compute row adjustments on data0 then use row adjustments to adjust data0
		for (int col=0; col<data0[0].length; col++) {
			for (int layer=0; layer<data0[0][0].length; layer++) {
				for (int stack=0; stack<data0[0][0][0].length; stack++) {
					double rowMarginal = 0;
					for (int row=0; row<data0.length; row++) { rowMarginal += data0[row][col][layer][stack]; }
					double rowAdjustment = ((double)rowControls[col][layer][stack]) / rowMarginal; 
					
					for (int row=0; row<data0.length; row++) { data0[row][col][layer][stack] = data0[row][col][layer][stack] * rowAdjustment; }
				}
			}
		}

		
		// 2. compute column adjustments on data0 then use column adjustments to adjust data0
		for (int row=0; row<data0.length; row++) {
			for (int layer=0; layer<data0[0][0].length; layer++) {
				for (int stack=0; stack<data0[0][0][0].length; stack++) {
					double columnMarginal = 0;
					for (int col=0; col<data0[0].length; col++) { columnMarginal += data0[row][col][layer][stack]; }
					double columnAdjustment = ((double) columnControls[row][layer][stack]) / columnMarginal;
					
					for (int col=0; col<data0[0].length; col++) { data0[row][col][layer][stack] = data0[row][col][layer][stack] * columnAdjustment; }
				}
			}
		}
		

		// 3. compute layer adjustments on data0 then use layer adjustments to adjust data0
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int stack=0; stack<data0[0][0][0].length; stack++) {
					double layerMarginal = 0;
					for (int layer=0; layer<data[0][0].length; layer++) { layerMarginal += data0[row][col][layer][stack]; }
					double layerAdjustment = ((double) layerControls[row][col][stack]) / layerMarginal;
					
					for (int layer=0; layer<data[0][0].length; layer++) { data0[row][col][layer][stack] = data0[row][col][layer][stack] * layerAdjustment; }
				}
			}
		}
		

		// 4. compute stack adjustments on data0 then use stack adjustments to adjust data0
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data0[0][0].length; layer++) {
					double stackMarginal = 0;
					for (int stack=0; stack<data[0][0][0].length; stack++) { stackMarginal += data0[row][col][layer][stack]; }
					double stackAdjustment = ((double) stackControls[row][col][layer]) / stackMarginal;
					
					for (int stack=0; stack<data[0][0][0].length; stack++) { data0[row][col][layer][stack] = data0[row][col][layer][stack] * stackAdjustment; }
				}
			}
		}

		
		
		// verify that data1 is correctly computed from data0
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
				 	for (int stack=0; stack<data[0][0][0].length; stack++) {
				 		assertTrue(data0[row][col][layer][stack] == data1[row][col][layer][stack]);
				 	}
				}
			}
		}
	}

	@Test public void testGetNbOfEntitiesToGenerate(@Mocked final FourWayIpf ipf) throws GenstarException {
		// data[2][3][4][5]
		final double[][][][] data = {
			{
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				}
			},
			
			{
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				},
					
				{
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 },
					{ 1, 2, 3, 4, 5 }
				}
			},
				
		};
		
		final int[][][] rowControls = { // [3][4][5]
			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},

			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},

			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},
		};
		
		final int[][][] columnControls = { // [2][4][5]
			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},

			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},
		};
		
		final int[][][] layerControls = { // [2][3][5]
			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},

			{
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 },
				{ 10, 20, 30, 40, 50 }
			},
		};

		final int[][][] stackControls = { // [2][3][4]
				{
					{ 10, 20, 30, 40 },
					{ 10, 20, 30, 40 },
					{ 10, 20, 30, 40 }
				},

				{
					{ 10, 20, 30, 40 },
					{ 10, 20, 30, 40 },
					{ 10, 20, 30, 40 }
				},
		};

		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
			ipf.getControls(2); result = layerControls;
			ipf.getControls(3); result = stackControls;
		}};
		
		FourWayIteration iteration0 = new FourWayIteration(ipf);
		FourWayIteration iteration1 = iteration0.nextIteration();
		
		
		// data0
		double[][][][] data0 = iteration0.getCopyData();
		int sumData0 = 0;
		for (int row=0; row<data0.length; row++) {
			for (int column=0; column<data0[0].length; column++) {
				for (int layer=0; layer<data0[0][0].length; layer++) {
					for (int stack=0; stack<data0[0][0][0].length; stack++) {
						sumData0 += Math.round(data0[row][column][layer][stack]);
					}
				}
			}
		}
		assertTrue(sumData0 == iteration0.getNbOfEntitiesToGenerate());
		
		
		// data1
		double[][][][] data1 = iteration1.getCopyData();
		int sumData1 = 0;
		for (int row=0; row<data0.length; row++) {
			for (int column=0; column<data1[0].length; column++) {
				for (int layer=0; layer<data1[0][0].length; layer++) {
					for (int stack=0; stack<data1[0][0][0].length; stack++) {
						sumData1 += Math.round(data1[row][column][layer][stack]);
					}
				}
			}
		}
		assertTrue(sumData1 == iteration1.getNbOfEntitiesToGenerate());
	}
}
