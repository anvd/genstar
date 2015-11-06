package ummisco.genstar.ipf;

import java.util.Arrays;

import ummisco.genstar.exception.GenstarException;

public class ThreeWayIteration extends IPFIteration<double[][][]> {

	private double[][][] data;
	
	private double[][] rowMarginals;
	
	private double[][] columnMarginals;
	
	private double[][] layerMarginals;
	
	private int[][] rowControls;
	
	private int[][] columnControls;
	
	private int[][] layerControls;
	
	private double[][] rowAdjustments;
	
	private double[][] columnAdjustments;
	
	private double[][] layerAdjustments;
	
	
	public ThreeWayIteration(final ThreeWayIPF ipf) throws GenstarException {
		super(ipf, 0);
		
		this.data = ipf.getData();
		this.rowControls = ipf.getControls(0);
		this.columnControls = ipf.getControls(1);
		this.layerControls = ipf.getControls(2);
	}
	
	private ThreeWayIteration(final ThreeWayIteration previousIteration, final double[][][] data) throws GenstarException {
		super(previousIteration.getIPF(), previousIteration.getIteration() + 1);
		
		this.data = data;
		this.rowControls = (int[][]) previousIteration.getIPF().getControls(0);
		this.columnControls = (int[][]) previousIteration.getIPF().getControls(1);
		this.layerControls = (int[][]) previousIteration.getIPF().getControls(2);
	}

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
		rowMarginals = new double[data[0].length][data[0][0].length];
		rowAdjustments = new double[data[0].length][data[0][0].length];
		for (int col=0; col<data[0].length; col++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				double rowMarginal = 0;
				for (int row=0; row<data.length; row++) { rowMarginal += copyData[row][col][layer]; }
				rowMarginals[col][layer] = rowMarginal;
				rowAdjustments[col][layer] = ((double)rowControls[col][layer]) / rowMarginals[col][layer];
				for (int row=0; row<data.length; row++) { copyData[row][col][layer] = copyData[row][col][layer] * rowAdjustments[col][layer]; }
			}
		}
		
		
		// 2. compute column marginals, columnAdjustments then adjust columns
		columnMarginals = new double[data.length][data[0][0].length];
		columnAdjustments = new double[data.length][data[0][0].length];
		for (int row=0; row<data.length; row++) {
			for (int layer=0; layer<data[0][0].length; layer++) {
				double columnMarginal = 0;
				for (int col=0; col<data[0].length; col++) { columnMarginal += copyData[row][col][layer]; }
				columnMarginals[row][layer] = columnMarginal;
				columnAdjustments[row][layer] = ((double) columnControls[row][layer]) / columnMarginals[row][layer];
				for (int col=0; col<data[0].length; col++) { copyData[row][col][layer] = copyData[row][col][layer] * columnAdjustments[row][layer]; }
			}
		}

		
		// 3. compute layer marginals, layerAdjustments then adjust layers
		layerMarginals = new double[data.length][data[0].length];
		layerAdjustments = new double[data.length][data[0].length];
		for (int row=0; row<data.length; row++) {
			for (int col=0; col<data[0].length; col++) {
				double layerMarginal = 0;
				for (int layer=0; layer<data[0][0].length; layer++) { layerMarginal += copyData[row][col][layer]; }
				layerMarginals[row][col] = layerMarginal; 
				layerAdjustments[row][col] = ((double) layerControls[row][col]) / layerMarginals[row][col];
				for (int layer=0; layer<data[0][0].length; layer++) { copyData[row][col][layer] = copyData[row][col][layer] * layerAdjustments[row][col]; }
			}
		}
		
		
		return new ThreeWayIteration(this, copyData);
	}
}
