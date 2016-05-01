package ummisco.genstar.ipf;

import java.util.Arrays;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class TwoWayIteration extends IpfIteration<double[][], int[], double[]> {
	

	public TwoWayIteration(final TwoWayIpf ipf) throws GenstarException {
		super(ipf, 0, ipf.getData());
	}
	
	private TwoWayIteration(final TwoWayIteration previousIteration, final double[][] data) throws GenstarException {
		super(previousIteration.getIPF(), previousIteration.getIteration() + 1, data);
	}	
	
	@Override
	protected void computeMarginals() throws GenstarException {
		int[] rowControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		int[] columnControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);

		double[] rowMarginals = new double[rowControls.length];
		for (int row=0; row<rowControls.length; row++) {
			double rowTotal = 0;
			for (int column=0; column<columnControls.length; column++) { rowTotal += data[row][column]; }
			rowMarginals[row] = rowTotal;
			
			if (rowMarginals[row] == 0) {
				AbstractAttribute rowAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
				AttributeValue rowValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row);
				
				throw new GenstarException("Zero marginal total on row attribute: " + rowAttribute.getNameOnData() + " rowValue: " + rowValue.toCsvString());
			}
		}
		
		double[] columnMarginals = new double[columnControls.length];
		for (int column=0; column<columnControls.length; column++) {
			double columnTotal = 0;
			for (int row=0; row<rowControls.length; row++) { columnTotal += data[row][column]; }
			columnMarginals[column] = columnTotal;
			
			if (columnMarginals[column] == 0) {
				AbstractAttribute columnAttribute = ipf.getControlledAttribute(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
				AttributeValue columnValue = ipf.getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(column);

				throw new GenstarException("Zero marginal total on column attribute: " + columnAttribute.getNameOnData() + " columnValue: " + columnValue.toCsvString());
			}
		}
	}

	@Override
	public TwoWayIteration nextIteration() throws GenstarException {
		int[] rowControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		int[] columnControls = ipf.getControls(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);

		double[][] copyData = getCopyData();
		
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

	@Override
	public double[][] getCopyData() {
		double[][] copy = new double[data.length][data[0].length];
		for (int row=0; row<data.length; row++) { copy[row] = Arrays.copyOf(data[row], data[row].length); }
		
		return copy;
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
