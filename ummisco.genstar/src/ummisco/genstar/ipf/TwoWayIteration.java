package ummisco.genstar.ipf;

import java.util.Arrays;

import ummisco.genstar.exception.GenstarException;

public class TwoWayIteration extends IPFIteration<double[][]> {
	
	private double[][] data;
	
	private double[] rowMarginals;
	
	private double[] columnMarginals;
	
	private int[] rowControls;
	
	private int[] columnControls;
	
	private double[] rowAdjustments;
	
	private double[] columnAdjustments;
	
	
	public TwoWayIteration(final TwoWayIPF ipf) throws GenstarException {
		super(ipf, 0);
		
		this.data = ipf.getData();
		this.rowControls = ipf.getControls(0);
		this.columnControls = ipf.getControls(1);
		
		if (data == null) { throw new GenstarException("'ipf' parameter contains null data"); }
		if (rowControls == null) { throw new GenstarException("'ipf' parameter contains null row controls"); }
		if (columnControls == null) { throw new GenstarException("'ipf' parameter contains null column controls"); }
	}
	
	private TwoWayIteration(final TwoWayIteration previousIteration, final double[][] data) throws GenstarException {
		super(previousIteration.getIPF(), previousIteration.getIteration() + 1);
		
		this.data = data;
		this.rowControls = (int[]) previousIteration.getIPF().getControls(0);
		this.columnControls = (int[]) previousIteration.getIPF().getControls(1);
	}	


	@Override
	public TwoWayIteration nextIteration() throws GenstarException {
		double[][] copyData = new double[data.length][data[0].length];
		for (int row=0; row<data.length; row++) { copyData[row] = Arrays.copyOf(data[row], data[row].length); }
		
		// 1. compute row marginals, rowAdjustments then adjust rows
		rowMarginals = new double[rowControls.length];
		rowAdjustments = new double[rowMarginals.length];
		for (int row=0; row<rowMarginals.length; row++) {
			double rowTotal = 0;
			for (int column=0; column<columnControls.length; column++) { rowTotal += copyData[row][column]; }
			rowMarginals[row] = rowTotal;
			rowAdjustments[row] = ((double)rowControls[row]) / rowMarginals[row];
			for (int column=0; column<columnControls.length; column++) { copyData[row][column] = copyData[row][column] * rowAdjustments[row]; }
		}
		
		// 2. compute column marginals, columnAdjustments then adjust columns
		columnMarginals = new double[columnControls.length];
		columnAdjustments = new double[columnControls.length];
		for (int column=0; column<columnMarginals.length; column++) {
			double colTotal = 0;
			for (int row=0; row<rowControls.length; row++) { colTotal += copyData[row][column]; }
			columnMarginals[column] = colTotal;
			columnAdjustments[column] = ((double)columnControls[column]) / columnMarginals[column];
			for (int row=0; row<rowControls.length; row++) { copyData[row][column] = columnAdjustments[column] * copyData[row][column]; }
		}
		
		return new TwoWayIteration(this, copyData);
	}
	

	@Override
	public double[][] getData() {
		double[][] copy = new double[data.length][data[0].length];
		for (int row=0; row<data.length; row++) { copy[row] = Arrays.copyOf(data[row], data[row].length); }
		
		return copy;
	}
}
