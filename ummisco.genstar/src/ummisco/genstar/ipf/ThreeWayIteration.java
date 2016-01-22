package ummisco.genstar.ipf;

import java.util.Arrays;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class ThreeWayIteration extends IPFIteration {
	
	private static final int ROW_ATTRIBUTE_INDEX = 0;
	
	private static final int COLUMN_ATTRIBUTE_INDEX = 1;
	
	private static final int LAYER_ATTRIBUTE_INDEX = 2;
	

	private double[][][] data;
	
	private int[][] rowControls;
	
	private int[][] columnControls;
	
	private int[][] layerControls;
	
	private double[][] rowMarginals;
	
	private double[][] columnMarginals;
	
	private double[][] layerMarginals;
		
	
	public ThreeWayIteration(final ThreeWayIPF ipf) throws GenstarException {
		super(ipf, 0);
		
		this.data = ipf.getData();
		this.rowControls = ipf.getControls(0);
		this.columnControls = ipf.getControls(1);
		this.layerControls = ipf.getControls(2);
		
		computeMarginals();
	}
	
	private ThreeWayIteration(final ThreeWayIteration previousIteration, final double[][][] data) throws GenstarException {
		super(previousIteration.getIPF(), previousIteration.getIteration() + 1);
		
		this.data = data;
		this.rowControls = (int[][]) previousIteration.getIPF().getControls(ROW_ATTRIBUTE_INDEX);
		this.columnControls = (int[][]) previousIteration.getIPF().getControls(COLUMN_ATTRIBUTE_INDEX);
		this.layerControls = (int[][]) previousIteration.getIPF().getControls(LAYER_ATTRIBUTE_INDEX);
		
		computeMarginals();
	}
	
	private void computeMarginals() throws GenstarException {
		// rowMarginals
		rowMarginals = new double[data[0].length][data[0][0].length];
		for (int col=0; col<data[0].length; col++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				double rowMarginal = 0;
				for (int row=0; row<data.length; row++) { rowMarginal += data[row][col][layer]; }
				rowMarginals[col][layer] = rowMarginal;
				
				if (rowMarginals[col][layer] == 0) {
					AbstractAttribute rowAttribute = ipf.getControlledAttribute(0);
					AbstractAttribute columnAttribute = ipf.getControlledAttribute(1);
					AbstractAttribute layerAttribute = ipf.getControlledAttribute(2);
					
					AttributeValue columnValue = ipf.getAttributeValues(1).get(col);
					AttributeValue layerValue = ipf.getAttributeValues(2).get(layer);
					
					throw new GenstarException("Zero marginal total on row attribute: " + rowAttribute.getNameOnData() 
							+ ", columnValue: " + columnValue.toCSVString() + "(" + columnAttribute.getNameOnData() + ")"
							+ ", layerValue: " + layerValue.toCSVString() + "(" + layerAttribute.getNameOnData() + ")");
				}
			}
		}		
		
		// columnMarginals
		columnMarginals = new double[data.length][data[0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				double columnMarginal = 0;
				for (int col=0; col<data[0].length; col++) { columnMarginal += data[row][col][layer]; }
				columnMarginals[row][layer] = columnMarginal;
				
				if (columnMarginal == 0) {
					AbstractAttribute rowAttribute = ipf.getControlledAttribute(0);
					AbstractAttribute columnAttribute = ipf.getControlledAttribute(1);
					AbstractAttribute layerAttribute = ipf.getControlledAttribute(2);
					
					AttributeValue rowValue = ipf.getAttributeValues(0).get(row);
					AttributeValue layerValue = ipf.getAttributeValues(2).get(layer);
					
					throw new GenstarException("Zero marginal total on column attribute: " + columnAttribute.getNameOnData() 
							+ ", rowValue: " + rowValue.toCSVString() + "(" + rowAttribute.getNameOnData() + ")"
							+ ", layerValue: " + layerValue.toCSVString() + "(" + layerAttribute.getNameOnData() + ")");
				}
			}
		}
		
		// layerMarginals
		layerMarginals = new double[data.length][data[0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				double layerMarginal = 0;
				for (int layer=0; layer<data[0][0].length; layer++) { layerMarginal += data[row][col][layer]; }
				layerMarginals[row][col] = layerMarginal; 
				
				if (layerMarginal == 0) {
					AbstractAttribute rowAttribute = ipf.getControlledAttribute(0);
					AbstractAttribute columnAttribute = ipf.getControlledAttribute(1);
					AbstractAttribute layerAttribute = ipf.getControlledAttribute(2);
					
					AttributeValue rowValue = ipf.getAttributeValues(0).get(row);
					AttributeValue columnValue = ipf.getAttributeValues(1).get(col);
					
					throw new GenstarException("Zero marginal total on layer attribute: " + layerAttribute.getNameOnData() 
							+ ", rowValue: " + rowValue.toCSVString() + "(" + rowAttribute.getNameOnData() + ")"
							+ ", columnValue: " + columnValue.toCSVString() + "(" + columnAttribute.getNameOnData() + ")");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public double[][][] getData() {
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
		double[][][] copyData = new double[data.length][data[0].length][data[0][0].length];
		for (int row=0; row<copyData.length; row++) {
			for (int column=0; column<copyData[0].length; column++) {
				copyData[row][column] = Arrays.copyOf(data[row][column], data[row][column].length);
			}
		}

		
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
	
	@SuppressWarnings("unchecked")
	@Override
	public double[][] getMarginals(final int dimension) throws GenstarException {
		if (dimension == 0) {
			double[][] copyRowMarginals = new double[rowMarginals.length][rowMarginals[0].length];
			for (int row=0; row<rowMarginals.length; row++) { copyRowMarginals[row] = Arrays.copyOf(rowMarginals[row], rowMarginals[row].length); }
					
			return copyRowMarginals;
		}
		
		if (dimension == 1) {
			double[][] copyColumnMarginals = new double[columnMarginals.length][columnMarginals[0].length];
			for (int row=0; row<columnMarginals.length; row++) { copyColumnMarginals[row] = Arrays.copyOf(columnMarginals[row], columnMarginals[row].length); }
					
			return copyColumnMarginals;
		}
		
		if (dimension == 2) {
			double[][] copyLayerMarginals = new double[layerMarginals.length][layerMarginals[0].length];
			for (int row=0; row<layerMarginals.length; row++) { copyLayerMarginals[row] = Arrays.copyOf(layerMarginals[row], layerMarginals[row].length); }
			
			return copyLayerMarginals;
		}
		
		throw new GenstarException("Invalid 'dimension' value (accepted values: 0, 1, 2)");
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
