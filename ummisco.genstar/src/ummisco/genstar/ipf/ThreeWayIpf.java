package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;

public class ThreeWayIpf extends Ipf<double[][][], int[][], double[][]> {

	
	public ThreeWayIpf(final SampleDataGenerationRule generationRule) throws GenstarException {
		super(generationRule);
	}
	
	@Override
	protected int getNbOfControlledAttributes() { return 3; }
	
	@Override
	protected void initializeData() throws GenstarException {
		ISampleData sampleData = generationRule.getSampleData();
		
		AbstractAttribute rowAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		List<AttributeValue> rowAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		AbstractAttribute columnAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		List<AttributeValue> columnAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		AbstractAttribute layerAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
		List<AttributeValue> layerAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);

		data = new double[rowAttributeValues.size()][columnAttributeValues.size()][layerAttributeValues.size()];

		Map<AbstractAttribute, AttributeValue> matchingCondition = new HashMap<AbstractAttribute, AttributeValue>();
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCondition.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCondition.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCondition.put(layerAttribute, layerAttributeValues.get(layer));
					data[row][col][layer] = sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCondition);
					// TODO if (data[row][col][layer] == 0) raise warning
				}
			}
		}
	}
	
	
	@Override
	protected void computeControls() throws GenstarException {
		IpfControlTotals controlTotals = generationRule.getControlTotals();
		
		AbstractAttribute rowAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		List<AttributeValue> rowAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		AbstractAttribute columnAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		List<AttributeValue> columnAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		AbstractAttribute layerAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
		List<AttributeValue> layerAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);

		// 1. compute row controls
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		int[][] rowControls = new int[columnAttributeValues.size()][layerAttributeValues.size()];
		for (int col=0; col<columnAttributeValues.size(); col++) {
			matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				rowControls[col][layer] = 0;
				List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
				for (AttributeValuesFrequency f : matchingFrequencies) { rowControls[col][layer] += f.getFrequency(); }
			}
		}
		controls.add(rowControls);
		
		// 2. compute column controls
		matchingCriteria.clear();
		int[][] columnControls = new int[rowAttributeValues.size()][layerAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				columnControls[row][layer] = 0;
				List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
				for (AttributeValuesFrequency f : matchingFrequencies) { columnControls[row][layer] += f.getFrequency(); }
			}
		}
		controls.add(columnControls);
		
		// 3. compute layer controls
		matchingCriteria.clear();
		int[][] layerControls = new int[rowAttributeValues.size()][columnAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				layerControls[row][col] = 0;
				List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
				for (AttributeValuesFrequency f : matchingFrequencies) { layerControls[row][col] += f.getFrequency(); }
			}
		}
		controls.add(layerControls);
		
		// TODO ensure that sum(rowControls) == sum(columnControls) == sum(layerControls) ELSE raise exception
	}
	
	@Override
	protected ThreeWayIteration createIPFIteration() throws GenstarException {
		return new ThreeWayIteration(this);
	}


	@Override
	public List<AttributeValuesFrequency> getSelectionProbabilitiesOfLastIPFIteration() throws GenstarException {
		if (iterations == null) { fit(); }
		
		if (selectionProbabilities == null) {
			AbstractAttribute rowAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
			List<AttributeValue> rowAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
			AbstractAttribute columnAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
			List<AttributeValue> columnAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
			AbstractAttribute layerAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
			List<AttributeValue> layerAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);

			selectionProbabilities = new ArrayList<AttributeValuesFrequency>();
			Map<AbstractAttribute, AttributeValue> attributeValues;
			ThreeWayIteration lastIpfIteration = (ThreeWayIteration)iterations.get(iterations.size() - 1);
			double[][][] iterationData = lastIpfIteration.getCopyData();
			
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
		
		List<AttributeValue> rowAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		List<AttributeValue> columnAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		List<AttributeValue> layerAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);

		System.out.println("ThreeWayIPF with");
		System.out.println("\tNumber of entities to generate = " + this.getNbOfEntitiesToGenerate());
		System.out.println("\trowAttributeValues.size() = " + rowAttributeValues.size());
		System.out.println("\tcolumnAttributeValues.size() = " + columnAttributeValues.size());
		System.out.println("\tlayerAttributeValues.size() = " + layerAttributeValues.size());
		System.out.println();

		// 1. rowControls
		int[][] rowControls = controls.get(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
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
		int[][] columnControls = controls.get(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
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
		int[][] layerControls = controls.get(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
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
		for (IpfIteration<double[][][], int[][], double[][]> iter : iterations) {
			System.out.println("\tIteration: " + iterationNo);
			
			// data
			double[][][] iterationData = iter.getCopyData();
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
