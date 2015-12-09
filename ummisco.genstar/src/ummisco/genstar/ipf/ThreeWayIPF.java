package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;

public class ThreeWayIPF extends IPF {

	private AbstractAttribute rowAttribute, columnAttribute, layerAttribute;
	
	private List<AttributeValue> rowAttributeValues;
	
	private List<AttributeValue> columnAttributeValues;
	
	private List<AttributeValue> layerAttributeValues;
	
	private double[][][] data;
	
	private int[][] rowControls;
	
	private int[][] columnControls;
	
	private int[][] layerControls;
	
	
	public ThreeWayIPF(final SampleDataGenerationRule generationRule) throws GenstarException {
		super(generationRule);
		
		// input parameters validation
		List<AbstractAttribute> controlledAttributes = generationRule.getControlledAttributes();
		
		if (controlledAttributes.size() != 3) { throw new GenstarException("ThreeWayIPF only accepts three controlled attributes."); }
		Set<AbstractAttribute> attributeSet = new HashSet<AbstractAttribute>(controlledAttributes);
		if (attributeSet.size() != 3) { throw new GenstarException("Some controlled attributes are duplicated"); }

		this.rowAttribute = controlledAttributes.get(0);
		this.columnAttribute = controlledAttributes.get(1);
		this.layerAttribute = controlledAttributes.get(2);
		
		this.rowAttributeValues = new ArrayList<AttributeValue>(rowAttribute.values());
		this.columnAttributeValues = new ArrayList<AttributeValue>(columnAttribute.values());
		this.layerAttributeValues = new ArrayList<AttributeValue>(layerAttribute.values());
		
		initializeData();
		computeControls();
	}
	
	
	private void initializeData() {
		ISampleData sampleData = generationRule.getSampleData();
		
		data = new double[rowAttributeValues.size()][columnAttributeValues.size()][layerAttributeValues.size()];

		Map<String, AttributeValue> matchingCondition = new HashMap<String, AttributeValue>();
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCondition.put(rowAttribute.getNameOnData(), rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCondition.put(columnAttribute.getNameOnData(), columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCondition.put(layerAttribute.getNameOnData(), layerAttributeValues.get(layer));
					data[row][col][layer] = sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCondition);
					// TODO if (data[row][col][layer] == 0) raise warning
				}
			}
		}
	}
	
	
	private void computeControls() {
		ControlTotals controlTotals = generationRule.getControlTotals();
		
		// 1. compute row controls
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		rowControls = new int[columnAttributeValues.size()][layerAttributeValues.size()];
		for (int col=0; col<columnAttributeValues.size(); col++) {
			matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				rowControls[col][layer] = 0;
				List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
				for (AttributeValuesFrequency f : matchingFrequencies) { rowControls[col][layer] += f.getFrequency(); }
			}
		}
		
		// 2. compute column controls
		matchingCriteria.clear();
		columnControls = new int[rowAttributeValues.size()][layerAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				columnControls[row][layer] = 0;
				List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
				for (AttributeValuesFrequency f : matchingFrequencies) { columnControls[row][layer] += f.getFrequency(); }
			}
		}
		
		// 3. compute layer controls
		matchingCriteria.clear();
		layerControls = new int[rowAttributeValues.size()][columnAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				layerControls[row][col] = 0;
				List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
				for (AttributeValuesFrequency f : matchingFrequencies) { layerControls[row][col] += f.getFrequency(); }
			}
		}
	}
	

	@Override
	public List<AttributeValue> getAttributeValues(final int dimension) throws GenstarException {
		if (dimension == 0) { 
			List<AttributeValue> copy = new ArrayList<AttributeValue>(rowAttributeValues);
			return copy;
		}
		
		if (dimension == 1) {
			List<AttributeValue> copy = new ArrayList<AttributeValue>(columnAttributeValues);
			return copy;
		}
		
		if (dimension == 2) {
			List<AttributeValue> copy = new ArrayList<AttributeValue>(layerAttributeValues);
			return copy;
		}
		
		throw new GenstarException("Invalid dimension value (accepted values: 0, 1, 2).");
	}
	
	@Override
	public void fit() throws GenstarException {
		if (iterations != null) {
			iterations.clear();
		} else {
			iterations = new ArrayList<IPFIteration>();
		}

		if (selectionProbabilities != null) {
			selectionProbabilities.clear();
			selectionProbabilities = null;
		}
		
		IPFIteration iteration = new ThreeWayIteration(this);
		iterations.add(iteration);
		for (int iter=0; iter<maxIteration; iter++) {
			iteration = iteration.nextIteration();
			iterations.add(iteration);
		}		
	}


	@SuppressWarnings("unchecked")
	@Override
	public int[][] getControls(int dimension) throws GenstarException {
		if (dimension == 0) { return rowControls; }
		if (dimension == 1) { return columnControls; }
		if (dimension == 2) { return layerControls; }

		throw new GenstarException("Invalid 'dimension' value. Accepted values: 0, 1, 2.");
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
	public List<AttributeValuesFrequency> getSelectionProbabilitiesOfLastIPFIteration() throws GenstarException {
		if (iterations == null) { fit(); }
		
		if (selectionProbabilities == null) {
			selectionProbabilities = new ArrayList<AttributeValuesFrequency>();
			Map<AbstractAttribute, AttributeValue> attributeValues;
			ThreeWayIteration lastIpfIteration = (ThreeWayIteration)iterations.get(iterations.size() - 1);
			double[][][] iterationData = lastIpfIteration.getData();
			
			for (int row=0; row<iterationData.length; row++) {
				attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
				attributeValues.put(rowAttribute, rowAttributeValues.get(row));
				
				for (int column=0; column<iterationData[0].length; column++) {
					attributeValues.put(columnAttribute, columnAttributeValues.get(column));
					
					for (int layer=0; layer<iterationData[0][0].length; layer++) {
						attributeValues.put(layerAttribute,  layerAttributeValues.get(layer));
						
						int selectionProba = (int) Math.round(iterationData[row][column][layer]);
						selectionProbabilities.add(new AttributeValuesFrequency(attributeValues, selectionProba));
					}
				}
			}
		}

		List<AttributeValuesFrequency> copy = new ArrayList<AttributeValuesFrequency>(selectionProbabilities);
		return copy;
	}
	
	@Override
	public void printDebug() throws GenstarException {
		if (iterations == null) { fit(); }
		
		System.out.println("ThreeWayIPF with");
		System.out.println("\tNumber of entities to generate = " + this.getNbOfEntitiesToGenerate());
		System.out.println("\trowAttributeValues.size() = " + rowAttributeValues.size());
		System.out.println("\tcolumnAttributeValues.size() = " + columnAttributeValues.size());
		System.out.println("\tlayerAttributeValues.size() = " + layerAttributeValues.size());
		System.out.println();

		// 1. rowControls
		System.out.println("rowControls: ");
		for (int dim1=0; dim1<rowControls.length; dim1++) {
			for (int dim2=0; dim2<rowControls[0].length; dim2++) {
				System.out.print((dim2 == 0 ? "\t" : "") + rowControls[dim1][dim2]);
				
				if (dim2 < rowControls[0].length - 1) System.out.print(", ");
				else { System.out.println(); }
			}
		}
		System.out.println();
		
		// 2. columnControls
		System.out.println("columnControls: ");
		for (int dim1=0; dim1<columnControls.length; dim1++) {
			for (int dim2=0; dim2<columnControls[0].length; dim2++) {
				System.out.print((dim2 == 0 ? "\t" : "") + columnControls[dim1][dim2]);
				
				if (dim2 < columnControls[0].length - 1) System.out.print(", ");
				else { System.out.println(); }
			}
		}
		System.out.println();
		
		// 3.layerControls
		System.out.println("layerControls: ");
		for (int dim1=0; dim1<layerControls.length; dim1++) {
			for (int dim2=0; dim2<layerControls[0].length; dim2++) {
				System.out.print((dim2 == 0 ? "\t" : "") + layerControls[dim1][dim2]);

				if (dim2 < layerControls[0].length - 1) System.out.print(", ");
				else { System.out.println(); }
			}
		}
		System.out.println();

		// IPFIterations
		int iterationNo = 0;
		System.out.println("Data of ThreeWayIteration: ");
		for (IPFIteration iter : iterations) {
			System.out.println("\tIteration: " + iterationNo);
			
			// data
			double[][][] iterationData = iter.getData();
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				System.out.println("\t\tLayer : " + layer);
				
				for (int row=0; row<rowAttributeValues.size(); row++) {
					for (int column=0; column<columnAttributeValues.size(); column++) {
						System.out.print((column == 0 ? "\t\t\t" : "") + iterationData[row][column][layer]);
						if (column < columnAttributeValues.size() - 1) System.out.print(", ");
					}
					
					System.out.println();
				}
				System.out.println();
			}
			
			
			// rowMarginals
			System.out.println("\t\trowMarginals:");
			double[][] rowMarginals = iter.getMarginals(0);
			for (int dim1=0; dim1<rowMarginals.length; dim1++) {
				for (int dim2=0; dim2<rowMarginals[0].length; dim2++) {
					System.out.print((dim2 == 0 ? "\t\t\t" : "") + rowMarginals[dim1][dim2]);
					
					if (dim2 < rowMarginals[0].length - 1) System.out.print(", ");
					else { System.out.println(); }
				}
			}
			System.out.println();
			
			
			// columnMarginals
			System.out.println("\t\tcolumnMarginals:");
			double[][] columnMarginals = iter.getMarginals(1);
			for (int dim1=0; dim1<columnMarginals.length; dim1++) {
				for (int dim2=0; dim2<columnMarginals[0].length; dim2++) {
					System.out.print((dim2 == 0 ? "\t\t\t" : "") + columnMarginals[dim1][dim2]);
					
					if (dim2 < columnMarginals[0].length - 1) System.out.print(", ");
					else { System.out.println(); }
				}
			}
			System.out.println();
			
			
			// layerMarginals
			System.out.println("\t\tlayerMarginals:");
			double[][] layerMarginals = iter.getMarginals(2);
			for (int dim1=0; dim1<layerMarginals.length; dim1++) {
				for (int dim2=0; dim2<layerMarginals[0].length; dim2++) {
					System.out.print((dim2 == 0 ? "\t\t\t" : "") + layerMarginals[dim1][dim2]);
					
					if (dim2 < layerMarginals[0].length - 1) System.out.print(", ");
					else { System.out.println(); }
				}
			}
			System.out.println();
			
			iterationNo++;
		}
	}
}
