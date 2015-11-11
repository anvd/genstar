package ummisco.genstar.ipf;

import java.util.Arrays;

import ummisco.genstar.exception.GenstarException;

public class TwoWayIteration extends IPFIteration {
	
	private double[][] data;
	
	private int[] rowControls;
	
	private int[] columnControls;
	
	private double[] rowMarginals;
	
	private double[] columnMarginals;
	
	
	public TwoWayIteration(final TwoWayIPF ipf) throws GenstarException {
		super(ipf, 0);
		
		this.data = ipf.getData();
		this.rowControls = ipf.getControls(0);
		this.columnControls = ipf.getControls(1);
		
		if (data == null) { throw new GenstarException("'ipf' parameter contains null data"); }
		if (rowControls == null) { throw new GenstarException("'ipf' parameter contains null row controls"); }
		if (columnControls == null) { throw new GenstarException("'ipf' parameter contains null column controls"); }
		
		computeMarginals();
	}
	
	private TwoWayIteration(final TwoWayIteration previousIteration, final double[][] data) throws GenstarException {
		super(previousIteration.getIPF(), previousIteration.getIteration() + 1);
		
		this.data = data;
		this.rowControls = (int[]) previousIteration.getIPF().getControls(0);
		this.columnControls = (int[]) previousIteration.getIPF().getControls(1);
		
		computeMarginals();
	}	
	
	private void computeMarginals() throws GenstarException {
		rowMarginals = new double[rowControls.length];
		for (int row=0; row<rowControls.length; row++) {
			double rowTotal = 0;
			for (int column=0; column<columnControls.length; column++) { rowTotal += data[row][column]; }
			rowMarginals[row] = rowTotal;
		}
		
		columnMarginals = new double[columnControls.length];
		for (int column=0; column<columnControls.length; column++) {
			double columnTotal = 0;
			for (int row=0; row<rowControls.length; row++) { columnTotal += data[row][column]; }
			columnMarginals[column] = columnTotal;
		}
	}

	@Override
	public TwoWayIteration nextIteration() throws GenstarException {
		double[][] copyData = new double[data.length][data[0].length];
		for (int row=0; row<data.length; row++) { copyData[row] = Arrays.copyOf(data[row], data[row].length); }
		
		// 1. compute row marginals, rowAdjustments then adjust rows
		double[] copyDataRowMarginals = new double[rowControls.length];
		double[] rowAdjustments = new double[copyDataRowMarginals.length];
		for (int row=0; row<copyDataRowMarginals.length; row++) {
			double rowTotal = 0;
			for (int column=0; column<columnControls.length; column++) { rowTotal += copyData[row][column]; }
			copyDataRowMarginals[row] = rowTotal;
			rowAdjustments[row] = ((double)rowControls[row]) / copyDataRowMarginals[row];
			for (int column=0; column<columnControls.length; column++) { copyData[row][column] = copyData[row][column] * rowAdjustments[row]; }
		}
		
		// 2. compute column marginals, columnAdjustments then adjust columns
		double[] copyDataColumnMarginals = new double[columnControls.length];
		double[] columnAdjustments = new double[columnControls.length];
		for (int column=0; column<copyDataColumnMarginals.length; column++) {
			double colTotal = 0;
			for (int row=0; row<rowControls.length; row++) { colTotal += copyData[row][column]; }
			copyDataColumnMarginals[column] = colTotal;
			columnAdjustments[column] = ((double)columnControls[column]) / copyDataColumnMarginals[column];
			for (int row=0; row<rowControls.length; row++) { copyData[row][column] = columnAdjustments[column] * copyData[row][column]; }
		}
		
		return new TwoWayIteration(this, copyData);
	}

	@SuppressWarnings("unchecked")
	@Override
	public double[][] getData() {
		double[][] copy = new double[data.length][data[0].length];
		for (int row=0; row<data.length; row++) { copy[row] = Arrays.copyOf(data[row], data[row].length); }
		
		return copy;
	}

	@SuppressWarnings("unchecked")
	@Override
	public double[] getMarginals(final int dimension) throws GenstarException {
		if (dimension == 0) {
			double[] copyRowMarginals = Arrays.copyOf(rowMarginals, rowMarginals.length);
			return copyRowMarginals;
		}
		
		if (dimension == 1) {
			double[] copyColumnMarginals = Arrays.copyOf(columnMarginals, columnMarginals.length);
			return copyColumnMarginals;
		}
		
		throw new GenstarException("Invalid 'dimension' value (accepted values: 0, 1)");
	}
	
	@Override
	public int getNbOfEntitiesToGenerate() {
		if (entitiesToGenerate == -1) {
			entitiesToGenerate = 0;
			
			for (int row=0; row<data.length; row++) {
				for (int column=0; column<data[0].length; column++) {
					entitiesToGenerate += Math.round(data[row][column]);
				}
			}
		}
		
		return entitiesToGenerate;
	}
}
