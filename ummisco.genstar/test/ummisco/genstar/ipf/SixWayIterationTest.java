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
public class SixWayIterationTest {

	
	@Test public void testInitializeObjectSuccessfully(@Mocked final SixWayIpf ipf) throws GenstarException {
		// data[2][2][2][2][2][2]
		final double[][][][][][] data = {
			{
				{
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					},
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					}
				}
			},

			{
				{
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					},
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					}
				}
			}
		};
		
		
		new Expectations() {{
			ipf.getData(); result = data;
		}};
		
		SixWayIteration iteration = new SixWayIteration(ipf);
		assertTrue(iteration.getIteration() == 0);
		
		double[][][][][][] iterationData = iteration.getCopyData();
		for (int row=0; row<data.length; row++) {
			for (int column=0; column<data[0].length; column++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					for (int stack=0; stack<data[0][0][0].length; stack++) {
						for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
							for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) {
								assertTrue(iterationData[row][column][layer][stack][fifthDim][sixthDim] == data[row][column][layer][stack][fifthDim][sixthDim]);
							}
						}
					}
				}
			}
		}
		
		assertTrue(iterationData.length == data.length);
		assertTrue(iterationData[0].length == data[0].length);
		assertTrue(iterationData[0][0].length == data[0][0].length);
		assertTrue(iterationData[0][0][0].length == data[0][0][0].length);
		assertTrue(iterationData[0][0][0][0].length == data[0][0][0][0].length);
		assertTrue(iterationData[0][0][0][0][0].length == data[0][0][0][0][0].length);
	}


	@Test(expected = GenstarException.class) public void testInitializeObjectWithZeroMarginals(@Mocked final SixWayIpf ipf, @Mocked final AbstractAttribute attribute, 
			@Mocked final AttributeValue attributeValue) throws GenstarException {
		// data[2][2][2][2][2][2]
		final double[][][][][][] data = {
			{
				{
					{
						{
							{ 0, 0 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					},
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					}
				}
			},

			{
				{
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					},
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					}
				}
			}
		};

		
		final int[][][][][] rowControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};

	
		final int[][][][][] columnControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};

	
		final int[][][][][] layerControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};

	
		final int[][][][][] stackControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};

	
		final int[][][][][] fifthDimControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};


		final int[][][][][] sixthDimControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};
		
		
		final List<AttributeValue> attributeValues = new ArrayList<AttributeValue>();
		attributeValues.add(attributeValue);
		
		new NonStrictExpectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
			ipf.getControls(2); result = layerControls;
			ipf.getControls(3); result = stackControls;
			ipf.getControls(4); result = fifthDimControls;
			ipf.getControls(5); result = sixthDimControls;
			
			ipf.getAttributeValues(anyInt); result = attributeValues;
			attributeValues.get(anyInt); result = attributeValue;
			attribute.getNameOnData(); result = "dummy attribute name";
			attributeValue.toCsvString(); result = "dummy CSV string";
		}};
		
		new SixWayIteration(ipf); 
	}


	@Test public void testNextIteration(@Mocked final SixWayIpf ipf) throws GenstarException {
		// data[2][2][2][2][2][2]
		final double[][][][][][] data = {
			{
				{
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					},
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					}
				}
			},

			{
				{
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					},
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					}
				}
			}
		};

		
		final int[][][][][] rowControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};

	
		final int[][][][][] columnControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};

	
		final int[][][][][] layerControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};

	
		final int[][][][][] stackControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};

	
		final int[][][][][] fifthDimControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};


		final int[][][][][] sixthDimControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};
		
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
			ipf.getControls(2); result = layerControls;
			ipf.getControls(3); result = stackControls;
			ipf.getControls(4); result = fifthDimControls;
			ipf.getControls(5); result = sixthDimControls;
		}};
		
		SixWayIteration iteration0 = new SixWayIteration(ipf);
		SixWayIteration iteration1 = iteration0.nextIteration();
		
		
		double[][][][][][] data0 = iteration0.getCopyData();
		double[][][][][][] data1 = iteration1.getCopyData();
		
		
		// 1. compute row adjustments on data0 then use row adjustments to adjust data0
		for (int col=0; col<data0[0].length; col++) {
			for (int layer=0; layer<data0[0][0].length; layer++) {
				for (int stack=0; stack<data0[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data0[0][0][0][0].length; fifthDim++) {
						for (int sixthDim=0; sixthDim<data0[0][0][0][0][0].length; sixthDim++) {
							double rowMarginal = 0;
							for (int row=0; row<data0.length; row++) { rowMarginal += data0[row][col][layer][stack][fifthDim][sixthDim]; }
							double rowAdjustment = ((double)rowControls[col][layer][stack][fifthDim][sixthDim]) / rowMarginal; 
							
							for (int row=0; row<data0.length; row++) { data0[row][col][layer][stack][fifthDim][sixthDim] = data0[row][col][layer][stack][fifthDim][sixthDim] * rowAdjustment; }
						}
					}
				}
			}
		}

		
		// 2. compute column adjustments on data0 then use column adjustments to adjust data0
		for (int row=0; row<data0.length; row++) {
			for (int layer=0; layer<data0[0][0].length; layer++) {
				for (int stack=0; stack<data0[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data0[0][0][0][0].length; fifthDim++) {
						for (int sixthDim=0; sixthDim<data0[0][0][0][0][0].length; sixthDim++) {
							double columnMarginal = 0;
							for (int col=0; col<data0[0].length; col++) { columnMarginal += data0[row][col][layer][stack][fifthDim][sixthDim]; }
							double columnAdjustment = ((double) columnControls[row][layer][stack][fifthDim][sixthDim]) / columnMarginal;
							
							for (int col=0; col<data0[0].length; col++) { data0[row][col][layer][stack][fifthDim][sixthDim] = data0[row][col][layer][stack][fifthDim][sixthDim] * columnAdjustment; }
						}
					}
				}
			}
		}
		

		// 3. compute layer adjustments on data0 then use layer adjustments to adjust data0
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int stack=0; stack<data0[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data0[0][0][0][0].length; fifthDim++) {
						for (int sixthDim=0; sixthDim<data0[0][0][0][0][0].length; sixthDim++) {
							double layerMarginal = 0;
							for (int layer=0; layer<data[0][0].length; layer++) { layerMarginal += data0[row][col][layer][stack][fifthDim][sixthDim]; }
							double layerAdjustment = ((double) layerControls[row][col][stack][fifthDim][sixthDim]) / layerMarginal;
							
							for (int layer=0; layer<data[0][0].length; layer++) { data0[row][col][layer][stack][fifthDim][sixthDim] = data0[row][col][layer][stack][fifthDim][sixthDim] * layerAdjustment; }
						}
					}
				}
			}
		}
		

		// 4. compute stack adjustments on data0 then use stack adjustments to adjust data0
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data0[0][0].length; layer++) {
					for (int fifthDim=0; fifthDim<data0[0][0][0][0].length; fifthDim++) {
						for (int sixthDim=0; sixthDim<data0[0][0][0][0][0].length; sixthDim++) {
							double stackMarginal = 0;
							for (int stack=0; stack<data[0][0][0].length; stack++) { stackMarginal += data0[row][col][layer][stack][fifthDim][sixthDim]; }
							double stackAdjustment = ((double) stackControls[row][col][layer][fifthDim][sixthDim]) / stackMarginal;
							
							for (int stack=0; stack<data[0][0][0].length; stack++) { data0[row][col][layer][stack][fifthDim][sixthDim] = data0[row][col][layer][stack][fifthDim][sixthDim] * stackAdjustment; }
						}
					}
				}
			}
		}
		
		
		// 5. compute fifthDim adjustments on data0 then use fifthDim adjustments to adjust data0
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data0[0][0].length; layer++) {
					for (int stack=0; stack<data0[0][0][0].length; stack++) {
						for (int sixthDim=0; sixthDim<data0[0][0][0][0][0].length; sixthDim++) {
							double fifthDimMarginal = 0;
							for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) { fifthDimMarginal += data0[row][col][layer][stack][fifthDim][sixthDim]; }
							double fifthDimAdjustment = ((double) fifthDimControls[row][col][layer][stack][sixthDim]) / fifthDimMarginal;
							
							for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) { data0[row][col][layer][stack][fifthDim][sixthDim] = data0[row][col][layer][stack][fifthDim][sixthDim] * fifthDimAdjustment; }
						}
					}
				}
			}
		}
		
		
		// 6. compute sixthDim adjustments on data0 then use sixthDim adjustments to adjust data0
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data0[0][0].length; layer++) {
					for (int stack=0; stack<data0[0][0][0].length; stack++) {
						for (int fifthDim=0; fifthDim<data0[0][0][0][0].length; fifthDim++) {
							double sixthDimMarginal = 0;
							for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) { sixthDimMarginal += data0[row][col][layer][stack][fifthDim][sixthDim]; }
							double sixthDimAdjustment = ((double) sixthDimControls[row][col][layer][stack][fifthDim]) / sixthDimMarginal;
							
							for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) { data0[row][col][layer][stack][fifthDim][sixthDim] = data0[row][col][layer][stack][fifthDim][sixthDim] * sixthDimAdjustment; }
						}
					}
				}
			}
		}


		// verify that data1 is correctly computed from data0
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
				 	for (int stack=0; stack<data[0][0][0].length; stack++) {
						for (int fifthDim=0; fifthDim<data0[0][0][0][0].length; fifthDim++) {
							for (int sixthDim=0; sixthDim<data0[0][0][0][0][0].length; sixthDim++) {
								assertTrue(data0[row][col][layer][stack][fifthDim][sixthDim] == data1[row][col][layer][stack][fifthDim][sixthDim]);
							}
						
						}
				 	}
				}
			}
		}
	}


	@Test public void testGetNbOfEntitiesToGenerate(@Mocked final SixWayIpf ipf) throws GenstarException {
		// data[2][2][2][2][2][2]
		final double[][][][][][] data = {
			{
				{
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					},
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					}
				}
			},

			{
				{
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					},
					{
						{
							{ 1, 2 },
							{ 1, 2 }
						},
						{
							{ 1, 2 },
							{ 1, 2 }
						}
					}
				}
			}
		};

		
		final int[][][][][] rowControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};

	
		final int[][][][][] columnControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};

	
		final int[][][][][] layerControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};

	
		final int[][][][][] stackControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};

	
		final int[][][][][] fifthDimControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};


		final int[][][][][] sixthDimControls = { // [2][2][2][2][2]
			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			},

			{
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				},
				{
					{
						{ 10, 20 },
						{ 10, 20 }
					},
					{
						{ 10, 20 },
						{ 10, 20 }
					}
				}
			}
		};
		
		
		new Expectations() {{
			ipf.getData(); result = data;
			ipf.getControls(0); result = rowControls;
			ipf.getControls(1); result = columnControls;
			ipf.getControls(2); result = layerControls;
			ipf.getControls(3); result = stackControls;
			ipf.getControls(4); result = fifthDimControls;
			ipf.getControls(5); result = sixthDimControls;
		}};
		
		SixWayIteration iteration0 = new SixWayIteration(ipf);
		SixWayIteration iteration1 = iteration0.nextIteration();
		
		
		// data0
		double[][][][][][] data0 = iteration0.getCopyData();
		int sumData0 = 0;
		for (int row=0; row<data0.length; row++) {
			for (int column=0; column<data0[0].length; column++) {
				for (int layer=0; layer<data0[0][0].length; layer++) {
					for (int stack=0; stack<data0[0][0][0].length; stack++) {
						for (int fifthDim=0; fifthDim<data0[0][0][0][0].length; fifthDim++) {
							for (int sixthDim=0; sixthDim<data0[0][0][0][0][0].length; sixthDim++) {
								sumData0 += Math.round(data0[row][column][layer][stack][fifthDim][sixthDim]);
							}
						}
					}
				}
			}
		}
		assertTrue(sumData0 == iteration0.getNbOfEntitiesToGenerate());
		
		
		// data1
		double[][][][][][] data1 = iteration1.getCopyData();
		int sumData1 = 0;
		for (int row=0; row<data0.length; row++) {
			for (int column=0; column<data1[0].length; column++) {
				for (int layer=0; layer<data1[0][0].length; layer++) {
					for (int stack=0; stack<data1[0][0][0].length; stack++) {
						for (int fifthDim=0; fifthDim<data1[0][0][0][0].length; fifthDim++) {
							for (int sixthDim=0; sixthDim<data0[0][0][0][0][0].length; sixthDim++) {
								sumData1 += Math.round(data1[row][column][layer][stack][fifthDim][sixthDim]);
							}
						}
					}
				}
			}
		}
		
		assertTrue(sumData1 == iteration1.getNbOfEntitiesToGenerate());
	}
}
