package ummisco.genstar.ipf;

import java.util.Arrays;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class FourWayIteration extends IPFIteration<double[][][][], int[][][], double[][][]> {
	
	
	public FourWayIteration(final FourWayIPF ipf) throws GenstarException {
		super(ipf, 0, ipf.getData());
	}
	
	private FourWayIteration(final FourWayIteration previousIteration, final double[][][][] data) throws GenstarException {
		super(previousIteration.getIPF(), previousIteration.getIteration() + 1, data);
	}

	
	@Override
	protected void computeMarginals() throws GenstarException {
		
		AbstractAttribute rowAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		AbstractAttribute columnAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		AbstractAttribute layerAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
		AbstractAttribute stackAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX);
		

		// rowMarginals
		double[][][] rowMarginals = new double[data[0].length][data[0][0].length][data[0][0][0].length];
		for (int col=0; col<data[0].length; col++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					double rowMarginal = 0;
					for (int row=0; row<data.length; row++) { rowMarginal += data[row][col][layer][stack]; }
					rowMarginals[col][layer][stack] = rowMarginal;
					
					if (rowMarginals[col][layer][stack] == 0) {
						AttributeValue columnValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(col);
						AttributeValue layerValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX).get(layer);
						AttributeValue stackValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX).get(stack);
						
						throw new GenstarException("Zero marginal total on row attribute: " + rowAttribute.getNameOnData() 
								+ ", columnValue: " + columnValue.toCsvString() + "(" + columnAttribute.getNameOnData() + ")"
								+ ", layerValue: " + layerValue.toCsvString() + "(" + layerAttribute.getNameOnData() + ")"
								+ ", stackValue: " + stackValue.toCsvString() + "(" + stackAttribute.getNameOnData() + ")");
					}
				}
			}
		}	
		marginals.add(rowMarginals);
		
		// columnMarginals
		double[][][] columnMarginals = new double[data.length][data[0][0].length][data[0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					double columnMarginal = 0;
					for (int col=0; col<data[0].length; col++) { columnMarginal += data[row][col][layer][stack]; }
					columnMarginals[row][layer][stack] = columnMarginal;
					
					if (columnMarginal == 0) {
						AttributeValue rowValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row);
						AttributeValue layerValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX).get(layer);
						AttributeValue stackValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX).get(stack);
						
						throw new GenstarException("Zero marginal total on column attribute: " + columnAttribute.getNameOnData() 
								+ ", rowValue: " + rowValue.toCsvString() + "(" + rowAttribute.getNameOnData() + ")"
								+ ", layerValue: " + layerValue.toCsvString() + "(" + layerAttribute.getNameOnData() + ")"
								+ ", stackValue: " + stackValue.toCsvString() + "(" + stackAttribute.getNameOnData() + ")");
					}
				}
			}
		}
		marginals.add(columnMarginals);
		
		// layerMarginals
		double[][][] layerMarginals = new double[data.length][data[0].length][data[0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					double layerMarginal = 0;
					for (int layer=0; layer<data[0][0].length; layer++) { layerMarginal += data[row][col][layer][stack]; }
					layerMarginals[row][col][stack] = layerMarginal; 
					
					if (layerMarginal == 0) {
						AttributeValue rowValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row);
						AttributeValue columnValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(col);
						AttributeValue stackValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX).get(stack);
						
						throw new GenstarException("Zero marginal total on layer attribute: " + layerAttribute.getNameOnData() 
								+ ", rowValue: " + rowValue.toCsvString() + "(" + rowAttribute.getNameOnData() + ")"
								+ ", columnValue: " + columnValue.toCsvString() + "(" + columnAttribute.getNameOnData() + ")"
								+ ", stackValue: " + stackValue.toCsvString() + "(" + stackAttribute.getNameOnData() + ")");
					}
				}
			}
		}
		marginals.add(layerMarginals);
		
		// stackMarginals
		double[][][] stackMarginals = new double[data.length][data[0].length][data[0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					double stackMarginal = 0;
					for (int stack=0; stack<data[0][0][0].length; stack++) { stackMarginal += data[row][col][layer][stack]; }
					stackMarginals[row][col][layer] = stackMarginal; 
					
					if (stackMarginal == 0) {
						AttributeValue rowValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row);
						AttributeValue columnValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(col);
						AttributeValue layerValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX).get(layer);
						
						throw new GenstarException("Zero marginal total on stack attribute: " + stackAttribute.getNameOnData() 
								+ ", rowValue: " + rowValue.toCsvString() + "(" + rowAttribute.getNameOnData() + ")"
								+ ", columnValue: " + columnValue.toCsvString() + "(" + columnAttribute.getNameOnData() + ")"
								+ ", layerValue: " + layerValue.toCsvString() + "(" +layerAttribute.getNameOnData() + ")");
					}
				}
			}
		}
		marginals.add(stackMarginals);
	}
	

	@Override
	public double[][][][] getCopyData() {
		double[][][][] copy = new double[data.length][data[0].length][data[0][0].length][data[0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int column=0; column<data[0].length; column++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					copy[row][column][layer] = Arrays.copyOf(data[row][column][layer], data[row][column][layer].length);
				}
			}
		}
		
		return copy;
	}


	@Override
	public FourWayIteration nextIteration() throws GenstarException {
		
		double[][][][] copyData = getCopyData();
		
		int[][][] rowControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		int[][][] columnControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		int[][][] layerControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
		int[][][] stackControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX);

		// 1. compute row marginals, rowAdjustments then adjust rows
		double[][][] copyDataRowMarginals = new double[data[0].length][data[0][0].length][data[0][0][0].length];
		double[][][] rowAdjustments = new double[data[0].length][data[0][0].length][data[0][0][0].length];
		for (int col=0; col<data[0].length; col++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					double rowMarginal = 0;
					for (int row=0; row<data.length; row++) { rowMarginal += copyData[row][col][layer][stack]; }
					copyDataRowMarginals[col][layer][stack] = rowMarginal;
					rowAdjustments[col][layer][stack] = ((double)rowControls[col][layer][stack]) / copyDataRowMarginals[col][layer][stack];
					for (int row=0; row<data.length; row++) { copyData[row][col][layer][stack] = copyData[row][col][layer][stack] * rowAdjustments[col][layer][stack]; }
				}
			}
		}

		
		// 2. compute column marginals, columnAdjustments then adjust columns
		double[][][] copyDataColumnMarginals = new double[data.length][data[0][0].length][data[0][0][0].length];
		double[][][] columnAdjustments = new double[data.length][data[0][0].length][data[0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					double columnMarginal = 0;
					for (int col=0; col<data[0].length; col++) { columnMarginal += copyData[row][col][layer][stack]; }
					copyDataColumnMarginals[row][layer][stack] = columnMarginal;
					columnAdjustments[row][layer][stack] = ((double) columnControls[row][layer][stack]) / copyDataColumnMarginals[row][layer][stack];
					for (int col=0; col<data[0].length; col++) { copyData[row][col][layer][stack] = copyData[row][col][layer][stack] * columnAdjustments[row][layer][stack]; }
				}
			}
		}
		
		
		// 3. compute layer marginals, layerAdjustments then adjust layers
		double[][][] copyDataLayerMarginals = new double[data.length][data[0].length][data[0][0][0].length];
		double[][][] layerAdjustments = new double[data.length][data[0].length][data[0][0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int stack=0; stack<data[0][0][0].length; stack++) {
					double layerMarginal = 0;
					for (int layer=0; layer<data[0][0].length; layer++) { layerMarginal += copyData[row][col][layer][stack]; }
					copyDataLayerMarginals[row][col][stack] = layerMarginal; 
					layerAdjustments[row][col][stack] = ((double) layerControls[row][col][stack]) / copyDataLayerMarginals[row][col][stack];
					for (int layer=0; layer<data[0][0].length; layer++) { copyData[row][col][layer][stack] = copyData[row][col][layer][stack] * layerAdjustments[row][col][stack]; }
				}
			}
		}
		
		
		// 4. compute stack marginals, stackAdjusments then adjust stacks
		double[][][] copyDataStackMarginals = new double[data.length][data[0].length][data[0][0].length];
		double[][][] stackAdjustments = new double[data.length][data[0].length][data[0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				for (int layer=0; layer<data[0][0].length; layer++) {
					double stackMarginal = 0;
					for (int stack=0; stack<data[0][0][0].length; stack++) { stackMarginal += copyData[row][col][layer][stack]; }
					copyDataStackMarginals[row][col][layer] = stackMarginal; 
					stackAdjustments[row][col][layer] = ((double) stackControls[row][col][layer]) / copyDataStackMarginals[row][col][layer];
					for (int stack=0; stack<data[0][0][0].length; stack++) { copyData[row][col][layer][stack] = copyData[row][col][layer][stack] * stackAdjustments[row][col][layer]; }
				}
			}
		}
		
		
		return new FourWayIteration(this, copyData);
	}

	@Override
	public int getNbOfEntitiesToGenerate() {
		if (entitiesToGenerate == -1) {
			entitiesToGenerate = 0;
			
			for (int row=0; row<data.length; row++) {
				for (int column=0; column<data[0].length; column++) {
					for (int layer=0; layer<data[0][0].length; layer++) {
						for (int stack=0; stack<data[0][0][0].length; stack++) {
							entitiesToGenerate += Math.round(data[row][column][layer][stack]);
						}
					}
				}
			}
		}
		
		return entitiesToGenerate;
	}

}
