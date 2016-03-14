package ummisco.genstar.ipf;

import java.util.Arrays;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class ThreeWayIteration extends IPFIteration<double[][][], int[][], double[][]> {
	

	public ThreeWayIteration(final ThreeWayIPF ipf) throws GenstarException {
		super(ipf, 0, ipf.getData());
	}
	
	private ThreeWayIteration(final ThreeWayIteration previousIteration, final double[][][] data) throws GenstarException {
		super(previousIteration.getIPF(), previousIteration.getIteration() + 1, data);
	}
	
	@Override
	protected void computeMarginals() throws GenstarException {
		AbstractAttribute rowAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		AbstractAttribute columnAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		AbstractAttribute layerAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
		
		// rowMarginals
		double[][] rowMarginals = new double[data[0].length][data[0][0].length];
		for (int col=0; col<data[0].length; col++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				double rowMarginal = 0;
				for (int row=0; row<data.length; row++) { rowMarginal += data[row][col][layer]; }
				rowMarginals[col][layer] = rowMarginal;
				
				if (rowMarginals[col][layer] == 0) {
					AttributeValue columnValue = ipf.getAttributeValues(1).get(col);
					AttributeValue layerValue = ipf.getAttributeValues(2).get(layer);
					
					throw new GenstarException("Zero marginal total on row attribute: " + rowAttribute.getNameOnData() 
							+ ", columnValue: " + columnValue.toCsvString() + " (" + columnAttribute.getNameOnData() + ")"
							+ ", layerValue: " + layerValue.toCsvString() + " (" + layerAttribute.getNameOnData() + ")");
				}
			}
		}	
		marginals.add(rowMarginals);
		
		// columnMarginals
		double[][] columnMarginals = new double[data.length][data[0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				double columnMarginal = 0;
				for (int col=0; col<data[0].length; col++) { columnMarginal += data[row][col][layer]; }
				columnMarginals[row][layer] = columnMarginal;
				
				if (columnMarginal == 0) {
					AttributeValue rowValue = ipf.getAttributeValues(0).get(row);
					AttributeValue layerValue = ipf.getAttributeValues(2).get(layer);
					
					throw new GenstarException("Zero marginal total on column attribute: " + columnAttribute.getNameOnData() 
							+ ", rowValue: " + rowValue.toCsvString() + " (" + rowAttribute.getNameOnData() + ")"
							+ ", layerValue: " + layerValue.toCsvString() + " (" + layerAttribute.getNameOnData() + ")");
				}
			}
		}
		marginals.add(columnMarginals);
		
		// layerMarginals
		double[][] layerMarginals = new double[data.length][data[0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				double layerMarginal = 0;
				for (int layer=0; layer<data[0][0].length; layer++) { layerMarginal += data[row][col][layer]; }
				layerMarginals[row][col] = layerMarginal; 
				
				if (layerMarginal == 0) {
					AttributeValue rowValue = ipf.getAttributeValues(0).get(row);
					AttributeValue columnValue = ipf.getAttributeValues(1).get(col);
					
					throw new GenstarException("Zero marginal total on layer attribute: " + layerAttribute.getNameOnData() 
							+ ", rowValue: " + rowValue.toCsvString() + " (" + rowAttribute.getNameOnData() + ")"
							+ ", columnValue: " + columnValue.toCsvString() + " (" + columnAttribute.getNameOnData() + ")");
				}
			}
		}
		marginals.add(layerMarginals);
	}

	@Override
	public double[][][] getCopyData() {
		double[][][] copy = new double[data.length][data[0].length][data[0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int column=0; column<data[0].length; column++) {
				copy[row][column] = Arrays.copyOf(data[row][column], data[row][column].length);
			}
		}
		
		return copy;
	}

	@Override
	public ThreeWayIteration nextIteration() throws GenstarException {
		int[][] rowControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		int[][] columnControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		int[][] layerControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
		
		double[][][] copyData = this.getCopyData();
		
		// 1. compute row marginals, rowAdjustments then adjust rows
		double[][] copyDataRowMarginals = new double[data[0].length][data[0][0].length];
		double[][] rowAdjustments = new double[data[0].length][data[0][0].length];
		for (int col=0; col<data[0].length; col++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				double rowMarginal = 0;
				for (int row=0; row<data.length; row++) { rowMarginal += copyData[row][col][layer]; }
				copyDataRowMarginals[col][layer] = rowMarginal;
				rowAdjustments[col][layer] = ((double)rowControls[col][layer]) / copyDataRowMarginals[col][layer];
				for (int row=0; row<data.length; row++) { copyData[row][col][layer] = copyData[row][col][layer] * rowAdjustments[col][layer]; }
			}
		}
		
		
		// 2. compute column marginals, columnAdjustments then adjust columns
		double[][] copyDataColumnMarginals = new double[data.length][data[0][0].length];
		double[][] columnAdjustments = new double[data.length][data[0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				double columnMarginal = 0;
				for (int col=0; col<data[0].length; col++) { columnMarginal += copyData[row][col][layer]; }
				copyDataColumnMarginals[row][layer] = columnMarginal;
				columnAdjustments[row][layer] = ((double) columnControls[row][layer]) / copyDataColumnMarginals[row][layer];
				for (int col=0; col<data[0].length; col++) { copyData[row][col][layer] = copyData[row][col][layer] * columnAdjustments[row][layer]; }
			}
		}

		
		// 3. compute layer marginals, layerAdjustments then adjust layers
		double[][] copyDataLayerMarginals = new double[data.length][data[0].length];
		double[][] layerAdjustments = new double[data.length][data[0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				double layerMarginal = 0;
				for (int layer=0; layer<data[0][0].length; layer++) { layerMarginal += copyData[row][col][layer]; }
				copyDataLayerMarginals[row][col] = layerMarginal; 
				layerAdjustments[row][col] = ((double) layerControls[row][col]) / copyDataLayerMarginals[row][col];
				for (int layer=0; layer<data[0][0].length; layer++) { copyData[row][col][layer] = copyData[row][col][layer] * layerAdjustments[row][col]; }
			}
		}
		
		
		return new ThreeWayIteration(this, copyData);
	}
	

	@Override
	public int getNbOfEntitiesToGenerate() {
		if (entitiesToGenerate == -1) {
			entitiesToGenerate = 0;
			
			for (int row=0; row<data.length; row++) {
				for (int column=0; column<data[0].length; column++) {
					for (int layer=0; layer<data[0][0].length; layer++) {
						entitiesToGenerate += Math.round(data[row][column][layer]);
					}
				}
			}
		}
		
		return entitiesToGenerate;
	}
}
