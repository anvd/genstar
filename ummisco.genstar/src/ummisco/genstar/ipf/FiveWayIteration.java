package ummisco.genstar.ipf;

import java.util.Arrays;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class FiveWayIteration extends IpfIteration<double[][][][][], int[][][][], double[][][][]> {

	
	public FiveWayIteration(final FiveWayIpf ipf) throws GenstarException {
		super(ipf, 0, ipf.getData());
	}

	private FiveWayIteration(final FiveWayIteration previousIteration, final double[][][][][] data) throws GenstarException {
		super(previousIteration.getIPF(), previousIteration.getIteration() + 1, data);
	}
	
	@Override
	protected void computeMarginals() throws GenstarException {
		AbstractAttribute rowAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		AbstractAttribute columnAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		AbstractAttribute layerAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
		AbstractAttribute stackAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX);
		AbstractAttribute fifthAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX);
		

		// rowMarginals
		double[][][][] rowMarginals = new double[data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length];
		for (int col=0; col<data[0].length; col++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						double rowMarginal = 0;
						for (int row=0; row<data.length; row++) { rowMarginal += data[row][col][layer][stack][fifthDim]; }
						rowMarginals[col][layer][stack][fifthDim] = rowMarginal;
						
						if (rowMarginals[col][layer][stack][fifthDim] == 0) {
							AttributeValue columnValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(col);
							AttributeValue layerValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX).get(layer);
							AttributeValue stackValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX).get(stack);
							AttributeValue fifthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX).get(fifthDim);
							
							throw new GenstarException("Zero marginal total on row attribute: " + rowAttribute.getNameOnData() 
									+ ", columnValue: " + columnValue.toCsvString() + "(" + columnAttribute.getNameOnData() + ")"
									+ ", layerValue: " + layerValue.toCsvString() + "(" + layerAttribute.getNameOnData() + ")"
									+ ", stackValue: " + stackValue.toCsvString() + "(" + stackAttribute.getNameOnData() + ")"
									+ ", fifthAttributeValue: " + fifthAttributeValue.toCsvString() + "(" + fifthAttribute.getNameOnData() + ")");
						}
					}
				}
			}
		}	
		marginals.add(rowMarginals);

	
		// columnMarginals
		double[][][][] columnMarginals = new double[data.length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						double columnMarginal = 0;
						for (int col=0; col<data[0].length; col++) { columnMarginal += data[row][col][layer][stack][fifthDim]; }
						columnMarginals[row][layer][stack][fifthDim] = columnMarginal;
						
						if (columnMarginal == 0) {
							AttributeValue rowValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row);
							AttributeValue layerValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX).get(layer);
							AttributeValue stackValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX).get(stack);
							AttributeValue fifthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX).get(fifthDim);
							
							throw new GenstarException("Zero marginal total on column attribute: " + columnAttribute.getNameOnData() 
									+ ", rowValue: " + rowValue.toCsvString() + "(" + rowAttribute.getNameOnData() + ")"
									+ ", layerValue: " + layerValue.toCsvString() + "(" + layerAttribute.getNameOnData() + ")"
									+ ", stackValue: " + stackValue.toCsvString() + "(" + stackAttribute.getNameOnData() + ")"
									+ ", fifthAttributeValue: " + fifthAttributeValue.toCsvString() + "(" + fifthAttribute.getNameOnData() + ")");
						}
					}
				}
			}
		}
		marginals.add(columnMarginals);

	
		// layerMarginals
		double[][][][] layerMarginals = new double[data.length][data[0].length][data[0][0][0].length][data[0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						double layerMarginal = 0;
						for (int layer=0; layer<data[0][0].length; layer++) { layerMarginal += data[row][col][layer][stack][fifthDim]; }
						layerMarginals[row][col][stack][fifthDim] = layerMarginal; 
						
						if (layerMarginal == 0) {
							AttributeValue rowValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row);
							AttributeValue columnValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(col);
							AttributeValue stackValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX).get(stack);
							AttributeValue fifthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX).get(fifthDim);
							
							throw new GenstarException("Zero marginal total on layer attribute: " + layerAttribute.getNameOnData() 
									+ ", rowValue: " + rowValue.toCsvString() + "(" + rowAttribute.getNameOnData() + ")"
									+ ", columnValue: " + columnValue.toCsvString() + "(" + columnAttribute.getNameOnData() + ")"
									+ ", stackValue: " + stackValue.toCsvString() + "(" + stackAttribute.getNameOnData() + ")"
									+ ", fifthAttributeValue: " + fifthAttributeValue.toCsvString() + "(" + fifthAttribute.getNameOnData() + ")");
						}
					}				
				}
			}
		}
		marginals.add(layerMarginals);

	
		// stackMarginals
		double[][][][] stackMarginals = new double[data.length][data[0].length][data[0][0].length][data[0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						double stackMarginal = 0;
						for (int stack=0; stack<data[0][0][0].length; stack++) { stackMarginal += data[row][col][layer][stack][fifthDim]; }
						stackMarginals[row][col][layer][fifthDim] = stackMarginal; 
						
						if (stackMarginal == 0) {
							AttributeValue rowValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row);
							AttributeValue columnValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(col);
							AttributeValue layerValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX).get(layer);
							AttributeValue fifthAttributeValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX).get(fifthDim);
							
							throw new GenstarException("Zero marginal total on stack attribute: " + stackAttribute.getNameOnData() 
									+ ", rowValue: " + rowValue.toCsvString() + "(" + rowAttribute.getNameOnData() + ")"
									+ ", columnValue: " + columnValue.toCsvString() + "(" + columnAttribute.getNameOnData() + ")"
									+ ", layerValue: " + layerValue.toCsvString() + "(" + layerAttribute.getNameOnData() + ")"
									+ ", fifthAttributeValue: " + fifthAttributeValue.toCsvString() + "(" + fifthAttribute.getNameOnData() + ")");
						}
					}
				}
			}
		}
		marginals.add(stackMarginals);

	
		// fifthAttributeMarginals
		double[][][][] fifthAttributeMarginals = new double[data.length][data[0].length][data[0][0].length][data[0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					for (int stack=0; stack<data[0][0][0].length; stack++) {
						double fifthAttributeMarginal = 0;
						for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) { fifthAttributeMarginal += data[row][col][layer][stack][fifthDim]; }
						fifthAttributeMarginals[row][col][layer][stack] = fifthAttributeMarginal; 
						
						if (fifthAttributeMarginal == 0) {
							AttributeValue rowValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row);
							AttributeValue columnValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(col);
							AttributeValue layerValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX).get(layer);
							AttributeValue stackValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX).get(stack);
							
							throw new GenstarException("Zero marginal total on fifth attribute: " + fifthAttribute.getNameOnData() 
									+ ", rowValue: " + rowValue.toCsvString() + "(" + rowAttribute.getNameOnData() + ")"
									+ ", columnValue: " + columnValue.toCsvString() + "(" + columnAttribute.getNameOnData() + ")"
									+ ", layerValue: " + layerValue.toCsvString() + "(" + layerAttribute.getNameOnData() + ")"
									+ ", stackValue: " + stackValue.toCsvString() + "(" + stackAttribute.getNameOnData() + ")");
						}
					}
				}
			}
		}
		marginals.add(fifthAttributeMarginals);
	}

	@Override
	public double[][][][][] getCopyData() {
		double[][][][][] copy = new double[data.length][data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int column=0; column<data[0].length; column++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					for (int stack=0; stack<data[0][0][0].length; stack++) {
						copy[row][column][layer][stack] = Arrays.copyOf(data[row][column][layer][stack], data[row][column][layer][stack].length);
					}
				}
			}
		}
		
		return copy;
	}

	@Override
	public FiveWayIteration nextIteration() throws GenstarException {
		
		double[][][][][] copyData = getCopyData();
		
		int[][][][] rowControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		int[][][][] columnControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		int[][][][] layerControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
		int[][][][] stackControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX);
		int[][][][] fifthAttributeControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX);

		// 1. compute row marginals, rowAdjustments then adjust rows
		double[][][][] copyDataRowMarginals = new double[data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length];
		double[][][][] rowAdjustments = new double[data[0].length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length];
		for (int col=0; col<data[0].length; col++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						double rowMarginal = 0;
						for (int row=0; row<data.length; row++) { rowMarginal += copyData[row][col][layer][stack][fifthDim]; }
						copyDataRowMarginals[col][layer][stack][fifthDim] = rowMarginal;
						rowAdjustments[col][layer][stack][fifthDim] = ((double)rowControls[col][layer][stack][fifthDim]) / copyDataRowMarginals[col][layer][stack][fifthDim];
						for (int row=0; row<data.length; row++) { copyData[row][col][layer][stack][fifthDim] = copyData[row][col][layer][stack][fifthDim] * rowAdjustments[col][layer][stack][fifthDim]; }
					}
				}
			}
		}

		
		// 2. compute column marginals, columnAdjustments then adjust columns
		double[][][][] copyDataColumnMarginals = new double[data.length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length];
		double[][][][] columnAdjustments = new double[data.length][data[0][0].length][data[0][0][0].length][data[0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						double columnMarginal = 0;
						for (int col=0; col<data[0].length; col++) { columnMarginal += copyData[row][col][layer][stack][fifthDim]; }
						copyDataColumnMarginals[row][layer][stack][fifthDim] = columnMarginal;
						columnAdjustments[row][layer][stack][fifthDim] = ((double) columnControls[row][layer][stack][fifthDim]) / copyDataColumnMarginals[row][layer][stack][fifthDim];
						for (int col=0; col<data[0].length; col++) { copyData[row][col][layer][stack][fifthDim] = copyData[row][col][layer][stack][fifthDim] * columnAdjustments[row][layer][stack][fifthDim]; }
					}
				}
			}
		}
		
		
		// 3. compute layer marginals, layerAdjustments then adjust layers
		double[][][][] copyDataLayerMarginals = new double[data.length][data[0].length][data[0][0][0].length][data[0][0][0][0].length];
		double[][][][] layerAdjustments = new double[data.length][data[0].length][data[0][0][0].length][data[0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
						double layerMarginal = 0;
						for (int layer=0; layer<data[0][0].length; layer++) { layerMarginal += copyData[row][col][layer][stack][fifthDim]; }
						copyDataLayerMarginals[row][col][stack][fifthDim] = layerMarginal; 
						layerAdjustments[row][col][stack][fifthDim] = ((double) layerControls[row][col][stack][fifthDim]) / copyDataLayerMarginals[row][col][stack][fifthDim];
						for (int layer=0; layer<data[0][0].length; layer++) { copyData[row][col][layer][stack][fifthDim] = copyData[row][col][layer][stack][fifthDim] * layerAdjustments[row][col][stack][fifthDim]; }
					}
				}
			}
		}
		
		
		// 4. compute stack marginals, stackAdjusments then adjust stacks
		double[][][][] copyDataStackMarginals = new double[data.length][data[0].length][data[0][0].length][data[0][0][0][0].length];
		double[][][][] stackAdjustments = new double[data.length][data[0].length][data[0][0].length][data[0][0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
				
					for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) {
					
						double stackMarginal = 0;
						for (int stack=0; stack<data[0][0][0].length; stack++) { stackMarginal += copyData[row][col][layer][stack][fifthDim]; }
						copyDataStackMarginals[row][col][layer][fifthDim] = stackMarginal; 
						stackAdjustments[row][col][layer][fifthDim] = ((double) stackControls[row][col][layer][fifthDim]) / copyDataStackMarginals[row][col][layer][fifthDim];
						for (int stack=0; stack<data[0][0][0].length; stack++) { copyData[row][col][layer][stack][fifthDim] = copyData[row][col][layer][stack][fifthDim] * stackAdjustments[row][col][layer][fifthDim]; }
					}
				}
			}
		}
		
		
		// 5. compute fifth attribute marginals, fifthAttributeAdjustments then adjust fifth attribute data
		double[][][][] copyFifthDimMarginals = new double[data.length][data[0].length][data[0][0].length][data[0][0][0].length];
		double[][][][] fifthDimAdjustments = new double[data.length][data[0].length][data[0][0].length][data[0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
				
					for (int stack=0; stack<data[0][0][0].length; stack++) {
					
						double fifthDimMarginal = 0;
						for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) { fifthDimMarginal += copyData[row][col][layer][stack][fifthDim]; }
						copyFifthDimMarginals[row][col][layer][stack] = fifthDimMarginal; 
						fifthDimAdjustments[row][col][layer][stack] = ((double) fifthAttributeControls[row][col][layer][stack]) / copyFifthDimMarginals[row][col][layer][stack];
						for (int fifthDim=0; fifthDim<data[0][0][0][0].length; fifthDim++) { copyData[row][col][layer][stack][fifthDim] = copyData[row][col][layer][stack][fifthDim] * fifthDimAdjustments[row][col][layer][stack]; }
					}
				}
			}
		}
		
		
		return new FiveWayIteration(this, copyData);
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
								entitiesToGenerate += Math.round(data[row][column][layer][stack][fifthDim]);
							}
						}
					}
				}
			}
		}
		
		return entitiesToGenerate;
	}

}
