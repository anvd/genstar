package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;

/**
 * 
 * @author voducan
 *
 * Reference: http://www.demog.berkeley.edu/~eddieh/IPFDescription/AKDOLWDIPFFOURD.pdf
 */
public class FourWayIPF extends IPF<double[][][][], int[][][], double[][][]> {

	
	protected FourWayIPF(final SampleDataGenerationRule generationRule) throws GenstarException {
		super(generationRule);
	}

	@Override
	protected int getNbOfControlledAttributes() { return 4; }

	@Override
	protected void initializeData() throws GenstarException {
		ISampleData sampleData = generationRule.getSampleData();
		
		AbstractAttribute rowAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		List<AttributeValue> rowAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.ROW_ATTRIBUTE_INDEX);
		AbstractAttribute columnAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		List<AttributeValue> columnAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.COLUMN_ATTRIBUTE_INDEX);
		AbstractAttribute layerAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
		List<AttributeValue> layerAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.LAYER_ATTRIBUTE_INDEX);
		AbstractAttribute stackAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX);
		List<AttributeValue> stackAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX);

		data = new double[rowAttributeValues.size()][columnAttributeValues.size()][layerAttributeValues.size()][stackAttributeValues.size()];
		
		Map<String, AttributeValue> matchingCondition = new HashMap<String, AttributeValue>();
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCondition.put(rowAttribute.getNameOnEntity(), rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCondition.put(columnAttribute.getNameOnEntity(), columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCondition.put(layerAttribute.getNameOnEntity(), layerAttributeValues.get(layer));
					
					for (int stack=0; stack<stackAttributeValues.size(); stack++) {
						matchingCondition.put(stackAttribute.getNameOnEntity(), stackAttributeValues.get(stack));
						data[row][col][layer][stack] = sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCondition);
						// TODO if (data[row][col][layer][stack] == 0) raise warning
					}
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
		AbstractAttribute stackAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX);
		List<AttributeValue> stackAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX);

		// 1. compute row controls
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		int[][][] rowControls = new int[columnAttributeValues.size()][layerAttributeValues.size()][stackAttributeValues.size()];
		for (int col=0; col<columnAttributeValues.size(); col++) {
			matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				
				for (int stack=0; stack<stackAttributeValues.size(); stack++) {
					matchingCriteria.put(stackAttribute,  stackAttributeValues.get(stack));
					
					rowControls[col][layer][stack] = 0;
					List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
					for (AttributeValuesFrequency f : matchingFrequencies) { rowControls[col][layer][stack] += f.getFrequency(); }
				}
			}
		}
		controls.add(rowControls);
		 

		// 2. compute column controls
		matchingCriteria.clear();
		int[][][] columnControls = new int[rowAttributeValues.size()][layerAttributeValues.size()][stackAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				
				for (int stack=0; stack<stackAttributeValues.size(); stack++) {
					matchingCriteria.put(stackAttribute, stackAttributeValues.get(stack));
					
					columnControls[row][layer][stack] = 0;
					List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
					for (AttributeValuesFrequency f : matchingFrequencies) { columnControls[row][layer][stack] += f.getFrequency(); }
				}
			}
		}
		controls.add(columnControls);
		 
		
		// 3. compute layer controls
		matchingCriteria.clear();
		int[][][] layerControls = new int[rowAttributeValues.size()][columnAttributeValues.size()][stackAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int stack=0; stack<stackAttributeValues.size(); stack++) {
					matchingCriteria.put(stackAttribute, stackAttributeValues.get(stack));
					
					layerControls[row][col][stack] = 0;
					List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
					for (AttributeValuesFrequency f : matchingFrequencies) { layerControls[row][col][stack] += f.getFrequency(); }
				}
			}
		}
		controls.add(layerControls);
		 

		// 4. compute stack controls
		matchingCriteria.clear();
		int[][][] stackControls = new int[rowAttributeValues.size()][columnAttributeValues.size()][layerAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
					
					stackControls[row][col][layer] = 0;
					List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
					for (AttributeValuesFrequency f : matchingFrequencies) { stackControls[row][col][layer] += f.getFrequency(); }
				}
			}
		}
		controls.add(stackControls);
		
		
		// TODO ensure that sum(rowControls) == sum(columnControls) == sum(layerControls) == sum(stackControls) ELSE raise exception
	}
	

	@Override
	protected IPFIteration<double[][][][], int[][][], double[][][]> createIPFIteration() throws GenstarException {
		return new FourWayIteration(this);
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
			AbstractAttribute stackAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX);
			List<AttributeValue> stackAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.STACK_ATTRIBUTE_INDEX);

			selectionProbabilities = new ArrayList<AttributeValuesFrequency>();
			Map<AbstractAttribute, AttributeValue> attributeValues;
			FourWayIteration lastIpfIteration = (FourWayIteration)iterations.get(iterations.size() - 1);
			double[][][][] iterationData = lastIpfIteration.getCopyData();
			
			for (int row=0; row<iterationData.length; row++) {
				attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
				attributeValues.put(rowAttribute, rowAttributeValues.get(row));
				
				for (int column=0; column<iterationData[0].length; column++) {
					attributeValues.put(columnAttribute, columnAttributeValues.get(column));
					
					for (int layer=0; layer<iterationData[0][0].length; layer++) {
						attributeValues.put(layerAttribute,  layerAttributeValues.get(layer));
						
						for (int stack=0; stack<iterationData[0][0][0].length; stack++) {
							attributeValues.put(stackAttribute, stackAttributeValues.get(stack));
							int selectionProba = (int) Math.round(iterationData[row][column][layer][stack]);
							selectionProbabilities.add(new AttributeValuesFrequency(attributeValues, selectionProba));
						}
					}
				}
			}
		}

		List<AttributeValuesFrequency> copy = new ArrayList<AttributeValuesFrequency>(selectionProbabilities);
		return copy; 
	}

	@Override
	public void printDebug() throws GenstarException {
		// TODO Auto-generated method stub
		
	}

}
