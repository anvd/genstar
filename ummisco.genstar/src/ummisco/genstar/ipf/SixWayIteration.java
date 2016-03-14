package ummisco.genstar.ipf;

import java.util.Arrays;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class SixWayIteration extends IPFIteration<double[][][][][][], int[][][][][], double[][][][][]> {

	
	public SixWayIteration(final SixWayIPF ipf) throws GenstarException {
		super(ipf, 0, ipf.getData());
	}
	
	private SixWayIteration(final SixWayIteration previousIteration, final double[][][][][][] data) throws GenstarException {
		super(previousIteration.getIPF(), previousIteration.getIteration() + 1, data);
	}
	
	@Override
	protected void computeMarginals() throws GenstarException {
		AbstractAttribute rowAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		AbstractAttribute columnAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		AbstractAttribute layerAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
		AbstractAttribute stackAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX);
		AbstractAttribute fifthAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX);
		AbstractAttribute sixthAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.SIXTH_ATTRIBUTE_INDEX);

		
		// rowMarginals
		double[][][][][] rowMarginals = new double[data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length][data[0][0][0][0][0].length];
		for (int col=0; col<data[0].length; col++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) {
							double rowMarginal = 0;
							for (int row=0; row<data.length; row++) { rowMarginal += data[row][col][layer][stack][fifthDim][sixthDim]; }
							rowMarginals[col][layer][stack][fifthDim][sixthDim] = rowMarginal;
							
							if (rowMarginals[col][layer][stack][fifthDim][sixthDim] == 0) {
								AttributeValue columnValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(col);
								AttributeValue layerValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX).get(layer);
								AttributeValue stackValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX).get(stack);
								AttributeValue fifthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX).get(fifthDim);
								AttributeValue sixthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.SIXTH_ATTRIBUTE_INDEX).get(sixthDim);
								
								throw new GenstarException("Zero marginal total on row attribute: " + rowAttribute.getNameOnData() 
										+ ", columnValue: " + columnValue.toCsvString() + "(" + columnAttribute.getNameOnData() + ")"
										+ ", layerValue: " + layerValue.toCsvString() + "(" + layerAttribute.getNameOnData() + ")"
										+ ", stackValue: " + stackValue.toCsvString() + "(" + stackAttribute.getNameOnData() + ")"
										+ ", fifthAttributeValue: " + fifthAttributeValue.toCsvString() + "(" + fifthAttribute.getNameOnData() + ")"
										+ ", sixthAttributeValue: " + sixthAttributeValue.toCsvString() + "(" + sixthAttribute.getNameOnData() + ")");
							}
						}
					}
				}
			}
		}	
		marginals.add(rowMarginals);

		
		// columnMarginals
		double[][][][][] columnMarginals = new double[data.length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length][data[0][0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) {
							double columnMarginal = 0;
							for (int col=0; col<data[0].length; col++) { columnMarginal += data[row][col][layer][stack][fifthDim][sixthDim]; }
							columnMarginals[row][layer][stack][fifthDim][sixthDim] = columnMarginal;
							
							if (columnMarginal == 0) {
								AttributeValue rowValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row);
								AttributeValue layerValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX).get(layer);
								AttributeValue stackValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX).get(stack);
								AttributeValue fifthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX).get(fifthDim);
								AttributeValue sixthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.SIXTH_ATTRIBUTE_INDEX).get(sixthDim);
								
								throw new GenstarException("Zero marginal total on column attribute: " + columnAttribute.getNameOnData() 
										+ ", rowValue: " + rowValue.toCsvString() + "(" + rowAttribute.getNameOnData() + ")"
										+ ", layerValue: " + layerValue.toCsvString() + "(" + layerAttribute.getNameOnData() + ")"
										+ ", stackValue: " + stackValue.toCsvString() + "(" + stackAttribute.getNameOnData() + ")"
										+ ", fifthAttributeValue: " + fifthAttributeValue.toCsvString() + "(" + fifthAttribute.getNameOnData() + ")"
										+ ", sixthAttributeValue: " + sixthAttributeValue.toCsvString() + "(" + sixthAttribute.getNameOnData() + ")");
							}
						}
					}
				}
			}
		}
		marginals.add(columnMarginals);

		
		// layerMarginals
		double[][][][][] layerMarginals = new double[data.length][data[0].length][data[0][0][0].length][data[0][0][0][0].length][data[0][0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) {
							double layerMarginal = 0;
							for (int layer=0; layer<data[0][0].length; layer++) { layerMarginal += data[row][col][layer][stack][fifthDim][sixthDim]; }
							layerMarginals[row][col][stack][fifthDim][sixthDim] = layerMarginal; 
							
							if (layerMarginal == 0) {
								AttributeValue rowValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row);
								AttributeValue columnValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(col);
								AttributeValue stackValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX).get(stack);
								AttributeValue fifthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX).get(fifthDim);
								AttributeValue sixthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.SIXTH_ATTRIBUTE_INDEX).get(fifthDim);
								
								throw new GenstarException("Zero marginal total on layer attribute: " + layerAttribute.getNameOnData() 
										+ ", rowValue: " + rowValue.toCsvString() + "(" + rowAttribute.getNameOnData() + ")"
										+ ", columnValue: " + columnValue.toCsvString() + "(" + columnAttribute.getNameOnData() + ")"
										+ ", stackValue: " + stackValue.toCsvString() + "(" + stackAttribute.getNameOnData() + ")"
										+ ", fifthAttributeValue: " + fifthAttributeValue.toCsvString() + "(" + fifthAttribute.getNameOnData() + ")"
										+ ", sixthAttributeValue: " + sixthAttributeValue.toCsvString() + "(" + sixthAttribute.getNameOnData() + ")");
							}
						}
					}				
				}
			}
		}
		marginals.add(layerMarginals);

		
		// stackMarginals
		double[][][][][] stackMarginals = new double[data.length][data[0].length][data[0][0].length][data[0][0][0][0].length][data[0][0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) {
							double stackMarginal = 0;
							for (int stack=0; stack<data[0][0][0].length; stack++) { stackMarginal += data[row][col][layer][stack][fifthDim][sixthDim]; }
							stackMarginals[row][col][layer][fifthDim][sixthDim] = stackMarginal; 
							
							if (stackMarginal == 0) {
								AttributeValue rowValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row);
								AttributeValue columnValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(col);
								AttributeValue layerValue = ipf.getAttributeValues(2).get(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
								AttributeValue fifthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX).get(fifthDim);
								AttributeValue sixthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.SIXTH_ATTRIBUTE_INDEX).get(sixthDim);
								
								throw new GenstarException("Zero marginal total on stack attribute: " + stackAttribute.getNameOnData() 
										+ ", rowValue: " + rowValue.toCsvString() + "(" + rowAttribute.getNameOnData() + ")"
										+ ", columnValue: " + columnValue.toCsvString() + "(" + columnAttribute.getNameOnData() + ")"
										+ ", layerValue: " + layerValue.toCsvString() + "(" +layerAttribute.getNameOnData() + ")"
										+ ", fifthAttributeValue: " + fifthAttributeValue.toCsvString() + "(" + fifthAttribute.getNameOnData() + ")"
										+ ", sixthAttributeValue: " + sixthAttributeValue.toCsvString() + "(" + sixthAttribute.getNameOnData() + ")");
							}
						}
					}
				}
			}
		}
		marginals.add(stackMarginals);

		
		// fifthAttributeMarginals
		double[][][][][] fifthAttributeMarginals = new double[data.length][data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					for (int stack=0; stack<data[0][0][0].length; stack++) {
						for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) {
							double fifthAttributeMarginal = 0;
							for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) { fifthAttributeMarginal += data[row][col][layer][stack][fifthDim][sixthDim]; }
							fifthAttributeMarginals[row][col][layer][stack][sixthDim] = fifthAttributeMarginal; 
							
							if (fifthAttributeMarginal == 0) {
								AttributeValue rowValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row);
								AttributeValue columnValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(col);
								AttributeValue layerValue = ipf.getAttributeValues(2).get(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
								AttributeValue stackValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX).get(stack);
								AttributeValue sixthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.SIXTH_ATTRIBUTE_INDEX).get(sixthDim);
								
								throw new GenstarException("Zero marginal total on fifth attribute: " + fifthAttribute.getNameOnData() 
										+ ", rowValue: " + rowValue.toCsvString() + "(" + rowAttribute.getNameOnData() + ")"
										+ ", columnValue: " + columnValue.toCsvString() + "(" + columnAttribute.getNameOnData() + ")"
										+ ", layerValue: " + layerValue.toCsvString() + "(" +layerAttribute.getNameOnData() + ")"
										+ ", stackValue: " + stackValue.toCsvString() + "(" + stackAttribute.getNameOnData() + ")"
										+ ", sixthAttributeValue: " + sixthAttributeValue.toCsvString() + "(" + sixthAttribute.getNameOnData() + ")");
							}
						}
					}
				}
			}
		}
		marginals.add(fifthAttributeMarginals);

		
		// sixthAttributeMarginals
		double[][][][][] sixthAttributeMarginals = new double[data.length][data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					for (int stack=0; stack<data[0][0][0].length; stack++) {
						for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
							double sixthAttributeMarginal = 0;
							for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) { sixthAttributeMarginal += data[row][col][layer][stack][fifthDim][sixthDim]; }
							sixthAttributeMarginals[row][col][layer][stack][fifthDim] = sixthAttributeMarginal; 
							
							if (sixthAttributeMarginal == 0) {
								AttributeValue rowValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row);
								AttributeValue columnValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(col);
								AttributeValue layerValue = ipf.getAttributeValues(2).get(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
								AttributeValue stackValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX).get(stack);
								AttributeValue fifthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX).get(fifthDim);
								
								throw new GenstarException("Zero marginal total on sixth attribute: " + sixthAttribute.getNameOnData() 
										+ ", rowValue: " + rowValue.toCsvString() + "(" + rowAttribute.getNameOnData() + ")"
										+ ", columnValue: " + columnValue.toCsvString() + "(" + columnAttribute.getNameOnData() + ")"
										+ ", layerValue: " + layerValue.toCsvString() + "(" +layerAttribute.getNameOnData() + ")"
										+ ", stackValue: " + stackValue.toCsvString() + "(" + stackAttribute.getNameOnData() + ")"
										+ ", fifthAttributeValue: " + fifthAttributeValue.toCsvString() + "(" + fifthAttribute.getNameOnData() + ")");
							}
						}
					}
				}
			}
		}
		marginals.add(fifthAttributeMarginals);
		
	}

	@Override
	public double[][][][][][] getCopyData() {
		double[][][][][][] copy = new double[data.length][data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length][data[0][0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int column=0; column<data[0].length; column++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					for (int stack=0; stack<data[0][0][0].length; stack++) {
						for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
							copy[row][column][layer][stack][fifthDim] = Arrays.copyOf(data[row][column][layer][stack][fifthDim], data[row][column][layer][stack][fifthDim].length);
						}
					}
				}
			}
		}
		
		return copy;
	}

	@Override
	public SixWayIteration nextIteration() throws GenstarException {

		double[][][][][][] copyData = getCopyData();
		
		int[][][][][] rowControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		int[][][][][] columnControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		int[][][][][] layerControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
		int[][][][][] stackControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX);
		int[][][][][] fifthAttributeControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX);
		int[][][][][] sixthAttributeControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.SIXTH_ATTRIBUTE_INDEX);
		
		// 1. compute row marginals, rowAdjustments then adjust rows
		double[][][][][] copyDataRowMarginals = new double[data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length][data[0][0][0][0][0].length];
		double[][][][][] rowAdjustments = new double[data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length][data[0][0][0][0][0].length];
		for (int col=0; col<data[0].length; col++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) {
							double rowMarginal = 0;
							for (int row=0; row<data.length; row++) { rowMarginal += copyData[row][col][layer][stack][fifthDim][sixthDim]; }
							copyDataRowMarginals[col][layer][stack][fifthDim][sixthDim] = rowMarginal;
							rowAdjustments[col][layer][stack][fifthDim][sixthDim] = ((double)rowControls[col][layer][stack][fifthDim][sixthDim]) / copyDataRowMarginals[col][layer][stack][fifthDim][sixthDim];
							for (int row=0; row<data.length; row++) { copyData[row][col][layer][stack][fifthDim][sixthDim] = copyData[row][col][layer][stack][fifthDim][sixthDim] * rowAdjustments[col][layer][stack][fifthDim][sixthDim]; }
						}
					}
				}
			}
		}

		
		// 2. compute column marginals, columnAdjustments then adjust columns
		double[][][][][] copyDataColumnMarginals = new double[data.length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length][data[0][0][0][0][0].length];
		double[][][][][] columnAdjustments = new double[data.length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length][data[0][0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) {
							double columnMarginal = 0;
							for (int col=0; col<data[0].length; col++) { columnMarginal += copyData[row][col][layer][stack][fifthDim][sixthDim]; }
							copyDataColumnMarginals[row][layer][stack][fifthDim][sixthDim] = columnMarginal;
							columnAdjustments[row][layer][stack][fifthDim][sixthDim] = ((double) columnControls[row][layer][stack][fifthDim][sixthDim]) / copyDataColumnMarginals[row][layer][stack][fifthDim][sixthDim];
							for (int col=0; col<data[0].length; col++) { copyData[row][col][layer][stack][fifthDim][sixthDim] = copyData[row][col][layer][stack][fifthDim][sixthDim] * columnAdjustments[row][layer][stack][fifthDim][sixthDim]; }
						}
					}
				}
			}
		}

		
		// 3. compute layer marginals, layerAdjustments then adjust layers
		double[][][][][] copyDataLayerMarginals = new double[data.length][data[0].length][data[0][0][0].length][data[0][0][0][0].length][data[0][0][0][0][0].length];
		double[][][][][] layerAdjustments = new double[data.length][data[0].length][data[0][0][0].length][data[0][0][0][0].length][data[0][0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) {
							double layerMarginal = 0;
							for (int layer=0; layer<data[0][0].length; layer++) { layerMarginal += copyData[row][col][layer][stack][fifthDim][sixthDim]; }
							copyDataLayerMarginals[row][col][stack][fifthDim][sixthDim] = layerMarginal; 
							layerAdjustments[row][col][stack][fifthDim][sixthDim] = ((double) layerControls[row][col][stack][fifthDim][sixthDim]) / copyDataLayerMarginals[row][col][stack][fifthDim][sixthDim];
							for (int layer=0; layer<data[0][0].length; layer++) { copyData[row][col][layer][stack][fifthDim][sixthDim] = copyData[row][col][layer][stack][fifthDim][sixthDim] * layerAdjustments[row][col][stack][fifthDim][sixthDim]; }
						}
					}
				}
			}
		}

		
		// 4. compute stack marginals, stackAdjusments then adjust stacks
		double[][][][][] copyDataStackMarginals = new double[data.length][data[0].length][data[0][0].length][data[0][0][0][0].length][data[0][0][0][0][0].length];
		double[][][][][] stackAdjustments = new double[data.length][data[0].length][data[0][0].length][data[0][0][0][0].length][data[0][0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) {
							double stackMarginal = 0;
							for (int stack=0; stack<data[0][0][0].length; stack++) { stackMarginal += copyData[row][col][layer][stack][fifthDim][sixthDim]; }
							copyDataStackMarginals[row][col][layer][fifthDim][sixthDim] = stackMarginal; 
							stackAdjustments[row][col][layer][fifthDim][sixthDim] = ((double) stackControls[row][col][layer][fifthDim][sixthDim]) / copyDataStackMarginals[row][col][layer][fifthDim][sixthDim];
							for (int stack=0; stack<data[0][0][0].length; stack++) { copyData[row][col][layer][stack][fifthDim][sixthDim] = copyData[row][col][layer][stack][fifthDim][sixthDim] * stackAdjustments[row][col][layer][fifthDim][sixthDim]; }
						}
					}
				}
			}
		}

		
		// 5. compute fifth attribute marginals, fifthAttributeAdjustments then adjust fifth attribute data
		double[][][][][] copyFifthDimMarginals = new double[data.length][data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0][0].length];
		double[][][][][] fifthDimAdjustments = new double[data.length][data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					for (int stack=0; stack<data[0][0][0].length; stack++) {
						for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) {
							double fifthDimMarginal = 0;
							for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) { fifthDimMarginal += copyData[row][col][layer][stack][fifthDim][sixthDim]; }
							copyFifthDimMarginals[row][col][layer][stack][sixthDim] = fifthDimMarginal; 
							fifthDimAdjustments[row][col][layer][stack][sixthDim] = ((double) fifthAttributeControls[row][col][layer][stack][sixthDim]) / copyFifthDimMarginals[row][col][layer][stack][sixthDim];
							for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) { copyData[row][col][layer][stack][fifthDim][sixthDim] = copyData[row][col][layer][stack][fifthDim][sixthDim] * fifthDimAdjustments[row][col][layer][stack][sixthDim]; }
						}
					}
				}
			}
		}

		
		// 6. compute sixth attribute marginals, sixthAttributeAdjustments then adjust sixth attribute data
		double[][][][][] copySixthDimMarginals = new double[data.length][data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length];
		double[][][][][] sixthDimAdjustments = new double[data.length][data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					for (int stack=0; stack<data[0][0][0].length; stack++) {
						for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
							double sixthDimMarginal = 0;
							for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) { sixthDimMarginal += copyData[row][col][layer][stack][fifthDim][sixthDim]; }
							copySixthDimMarginals[row][col][layer][stack][fifthDim] = sixthDimMarginal; 
							sixthDimAdjustments[row][col][layer][stack][fifthDim] = ((double) sixthAttributeControls[row][col][layer][stack][fifthDim]) / copySixthDimMarginals[row][col][layer][stack][fifthDim];
							for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) { copyData[row][col][layer][stack][fifthDim][sixthDim] = copyData[row][col][layer][stack][fifthDim][sixthDim] * sixthDimAdjustments[row][col][layer][stack][fifthDim]; }
						}
					}
				}
			}
		}
		
		
		return new SixWayIteration(this, copyData);
	}

	@Override
	public int getNbOfEntitiesToGenerate() {
		if (entitiesToGenerate == -1) {
			entitiesToGenerate = 0;
			
			for (int row=0; row<data.length; row++) {
				for (int column=0; column<data[0].length; column++) {
					for (int layer=0; layer<data[0][0].length; layer++) {
						for (int stack=0; stack<data[0][0][0].length; stack++) {
							for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
								for (int sixthDim=0; sixthDim<data[0][0][0][0][0].length; sixthDim++) {
									entitiesToGenerate += Math.round(data[row][column][layer][stack][fifthDim][sixthDim]);
								}
							}
						}
					}
				}
			}
		}
		
		return entitiesToGenerate;
	}


}
