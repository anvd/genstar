package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;

public class TwoWayIPF extends IPF<double[][], int[], double[]> {
	
//	private AbstractAttribute rowAttribute, columnAttribute;
	
//	private List<AttributeValue> rowAttributeValues;
	
//	private List<AttributeValue> columnAttributeValues;
	
//	private double[][] data;
	
//	private int[] rowControls;
	
//	private int[] columnControls;
	
	
	public TwoWayIPF(final SampleDataGenerationRule generationRule) throws GenstarException {
		super(generationRule);
		
//		// input parameters validation
//		List<AbstractAttribute> controlledAttributes = generationRule.getControlledAttributes();
//		
//		if (controlledAttributes.size() != 2) { throw new GenstarException("TwoWayIPF only accepts two controlled attributes."); }
//		Set<AbstractAttribute> attributeSet = new HashSet<AbstractAttribute>(controlledAttributes);
//		if (attributeSet.size() != 2) { throw new GenstarException("Some controlled attributes are duplicated"); }
//		
//		this.controlledAttributes.addAll(controlledAttributes);
//		
//		this.rowAttribute = controlledAttributes.get(0);
//		this.columnAttribute = controlledAttributes.get(1);
//		
//		this.rowAttributeValues = new ArrayList<AttributeValue>(rowAttribute.values());
//		this.columnAttributeValues = new ArrayList<AttributeValue>(columnAttribute.values());
//		
//		initializeData();
//		computeControls();
	}

	@Override
	protected int getNbOfControlledAttributes() { return 2; }
	
	@Override
	protected void initializeData() throws GenstarException {
		ISampleData sampleData = generationRule.getSampleData();
		
		AbstractAttribute rowAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		List<AttributeValue> rowAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		AbstractAttribute columnAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		List<AttributeValue> columnAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);

		data = new double[rowAttributeValues.size()][columnAttributeValues.size()];

		Map<String, AttributeValue> matchingCondition = new HashMap<String, AttributeValue>();
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCondition.put(rowAttribute.getNameOnEntity(), rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCondition.put(columnAttribute.getNameOnEntity(), columnAttributeValues.get(col));
				
				data[row][col] = sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCondition);
			}
		}
	}
	
	@Override
	protected void computeControls() throws GenstarException {
		ControlTotals controlTotals = generationRule.getControlTotals();
		
		// 1. compute row controls
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		AbstractAttribute rowAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		List<AttributeValue> rowAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		int[] rowControls = new int[rowAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			rowControls[row] = 0;
			
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
			for (AttributeValuesFrequency f : matchingFrequencies) { rowControls[row] += f.getFrequency(); }
		}
		controls.add(rowControls);
				
		// 2. compute column controls
		matchingCriteria.clear();
		AbstractAttribute columnAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		List<AttributeValue> columnAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		int[] columnControls = new int[columnAttributeValues.size()];
		for (int column=0; column<columnAttributeValues.size(); column++) {
			columnControls[column] = 0;
			
			matchingCriteria.put(columnAttribute, columnAttributeValues.get(column));
			List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
			for (AttributeValuesFrequency f : matchingFrequencies) { columnControls[column] += f.getFrequency(); }
		}
		controls.add(columnControls);

		// TODO ensure that sum(rowControls) == sum(columnControls) ELSE raise exception
	}
	
	@Override
	protected TwoWayIteration createIPFIteration() throws GenstarException {
		return new TwoWayIteration(this);
	}
	
//	@SuppressWarnings("unchecked")
//	@Override
//	public double[][] getData() {
//		double[][] copy = new double[data.length][data[0].length];
//		for (int row=0; row<copy.length; row++) { copy[row] = Arrays.copyOf(data[row], data[row].length); }
//		
//		return copy;
//	}
	
//	@SuppressWarnings("unchecked")
//	@Override
//	public int[] getControls(final int dimension) throws GenstarException {
//		if (dimension == 0) { return rowControls; }
//		if (dimension == 1) { return columnControls; }
//		throw new GenstarException("Invalid 'dimension' value (valid values: 0, 1)");
//	}
	
//	@Override
//	public AbstractAttribute getControlledAttribute(final int dimension) throws GenstarException {
//		if (dimension == 0) { return rowAttribute; }
//		if (dimension == 1) { return columnAttribute; }
//		
//		throw new GenstarException("Invalid dimension value (accepted values: 0, 1).");
//	}
	
	
	// TODO move to superclass
//	@Override
//	public List<AttributeValue> getAttributeValues(final int dimension) throws GenstarException {
//		if (dimension == 0) { 
//			List<AttributeValue> copy = new ArrayList<AttributeValue>(rowAttributeValues);
//			return copy;
//		}
//		
//		if (dimension == 1) {
//			List<AttributeValue> copy = new ArrayList<AttributeValue>(columnAttributeValues);
//			return copy;
//		}
//		
//		throw new GenstarException("Invalid dimension value (accepted values: 0, 1).");
//	}


	@Override
	public List<AttributeValuesFrequency> getSelectionProbabilitiesOfLastIPFIteration() throws GenstarException {
		if (iterations == null) { fit(); }
		
		if (selectionProbabilities == null) {
			selectionProbabilities = new ArrayList<AttributeValuesFrequency>();
			Map<AbstractAttribute, AttributeValue> attributeValues;
			TwoWayIteration lastIpfIteration = (TwoWayIteration)iterations.get(iterations.size() - 1);
			double[][] iterationData = lastIpfIteration.getCopyData();
			
			for (int row=0; row<iterationData.length; row++) {
				attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
				attributeValues.put(controlledAttributes.get(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX), controlledAttributeValues.get(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).get(row));
				
				for (int column=0; column<iterationData[0].length; column++) {
					attributeValues.put(controlledAttributes.get(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX), controlledAttributeValues.get(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).get(column));
					
					int selectionProba = (int) Math.round(iterationData[row][column]);
					selectionProbabilities.add(new AttributeValuesFrequency(attributeValues, selectionProba));
				}
			}
		}

		List<AttributeValuesFrequency> copy = new ArrayList<AttributeValuesFrequency>(selectionProbabilities);
		return copy;
	}
	
	@Override
	public void printDebug() throws GenstarException {
		if (iterations == null) { fit(); }
		
		System.out.println("TwoWayIPF with");
		System.out.println("\tNumber of entities to generate = " + this.getNbOfEntitiesToGenerate());
		System.out.println("\trowAttributeValues.size() = " + controlledAttributeValues.get(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX).size());
		System.out.println("\tcolumnAttributeValues.size() = " + controlledAttributeValues.get(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX).size());
		
		// 1. rowControls
		int[] rowControls = controls.get(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		System.out.print("\trowControls: ");
		for (int row=0; row<rowControls.length; row++) {
			System.out.print(rowControls[row]);
			if (row < rowControls.length - 1) System.out.print(", ");
		}
		System.out.println();
		
		// 2. columnControls
		int[] columnControls = controls.get(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		System.out.print("\tcolumnControls: ");
		for (int column=0; column<columnControls.length; column++) {
			System.out.print(columnControls[column]);
			if (column < columnControls.length - 1) System.out.print(", ");
		}
		System.out.println();
		System.out.println();
		
		// IPFIterations
		int iterationNo = 0;
		System.out.println("\tTwoWayIterations: ");
		for (IPFIteration<double[][], int[], double[]> iter : iterations) {
			
			System.out.println("\t\tIteration: " + iterationNo);
			
			// data
			double[][] iterationData = iter.getCopyData();
			System.out.println("\t\t\tData:");
			for (int row=0; row<rowControls.length; row++) {
				for (int column=0; column<columnControls.length; column++) {
					System.out.print((column == 0 ? "\t\t\t\t" : "") + iterationData[row][column]);
					if (column < columnControls.length - 1) System.out.print(", ");
				}
				
				System.out.println();
			}
			
			// rowMarginals
			System.out.print("\t\t\trowMarginals: ");
			double[] rowMarginals = iter.getMarginals(0);
			for (int row=0; row<rowMarginals.length; row++) {
				System.out.print(rowMarginals[row]);
				if (row < rowControls.length - 1) System.out.print(", ");
			}
			System.out.println();
			
			// columnMarginals
			System.out.print("\t\t\tcolumnMarginals: ");
			double[] columnMarginals = iter.getMarginals(1);
			for (int column=0; column<columnMarginals.length; column++) {
				System.out.print(columnMarginals[column]);
				if (column < columnControls.length - 1) System.out.print(", ");
			}
			System.out.println();	
			
			iterationNo++;
		}
	}
}
