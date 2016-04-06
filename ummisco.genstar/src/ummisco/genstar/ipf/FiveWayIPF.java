package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;

public class FiveWayIPF extends IPF<double[][][][][], int[][][][], double[][][][]> {


	protected FiveWayIPF(SampleDataGenerationRule generationRule) throws GenstarException {
		super(generationRule);
	}
	
	@Override
	protected int getNbOfControlledAttributes() { return 5; }

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
		AbstractAttribute fifthAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX);
		List<AttributeValue> fifthAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX);

		data = new double[rowAttributeValues.size()][columnAttributeValues.size()][layerAttributeValues.size()][stackAttributeValues.size()][fifthAttributeValues.size()];
		
		Map<String, AttributeValue> matchingCondition = new HashMap<String, AttributeValue>();
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCondition.put(rowAttribute.getNameOnEntity(), rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCondition.put(columnAttribute.getNameOnEntity(), columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCondition.put(layerAttribute.getNameOnEntity(), layerAttributeValues.get(layer));
					
					for (int stack=0; stack<stackAttributeValues.size(); stack++) {
						matchingCondition.put(stackAttribute.getNameOnEntity(), stackAttributeValues.get(stack));
						
						for (int fifthDim=0; fifthDim<fifthAttributeValues.size(); fifthDim++) {
							matchingCondition.put(fifthAttribute.getNameOnEntity(), fifthAttributeValues.get(fifthDim));
							data[row][col][layer][stack][fifthDim] = sampleData.getSampleEntityPopulation().countMatchingEntities(matchingCondition);
							// TODO if (data[row][col][layer][stack][fifthDim] == 0) raise warning
						}
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
		AbstractAttribute fifthAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX);
		List<AttributeValue> fifthAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX);

		// 1. compute row controls
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		int[][][][] rowControls = new int[columnAttributeValues.size()][layerAttributeValues.size()][stackAttributeValues.size()][fifthAttributeValues.size()];
		for (int col=0; col<columnAttributeValues.size(); col++) {
			matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				
				for (int stack=0; stack<stackAttributeValues.size(); stack++) {
					matchingCriteria.put(stackAttribute,  stackAttributeValues.get(stack));
					
					for (int fifthDim=0; fifthDim<fifthAttributeValues.size(); fifthDim++) {
						matchingCriteria.put(fifthAttribute, fifthAttributeValues.get(fifthDim));

						rowControls[col][layer][stack][fifthDim] = 0;
						List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
						for (AttributeValuesFrequency f : matchingFrequencies) { rowControls[col][layer][stack][fifthDim] += f.getFrequency(); }
					}
				}
			}
		}
		controls.add(rowControls);
		

		// 2. compute column controls
		matchingCriteria.clear();
		int[][][][] columnControls = new int[rowAttributeValues.size()][layerAttributeValues.size()][stackAttributeValues.size()][fifthAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				
				for (int stack=0; stack<stackAttributeValues.size(); stack++) {
					matchingCriteria.put(stackAttribute, stackAttributeValues.get(stack));
					
					for (int fifthDim=0; fifthDim<fifthAttributeValues.size(); fifthDim++) {
						matchingCriteria.put(fifthAttribute, fifthAttributeValues.get(fifthDim));
											
						columnControls[row][layer][stack][fifthDim] = 0;
						List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
						for (AttributeValuesFrequency f : matchingFrequencies) { columnControls[row][layer][stack][fifthDim] += f.getFrequency(); }
					}
				}
			}
		}
		controls.add(columnControls);
		
	
		// 3. compute layer controls
		matchingCriteria.clear();
		int[][][][] layerControls = new int[rowAttributeValues.size()][columnAttributeValues.size()][stackAttributeValues.size()][fifthAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int stack=0; stack<stackAttributeValues.size(); stack++) {
					matchingCriteria.put(stackAttribute, stackAttributeValues.get(stack));
					
					for (int fifthDim=0; fifthDim<fifthAttributeValues.size(); fifthDim++) {
						matchingCriteria.put(fifthAttribute, fifthAttributeValues.get(fifthDim));

						layerControls[row][col][stack][fifthDim] = 0;
						List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
						for (AttributeValuesFrequency f : matchingFrequencies) { layerControls[row][col][stack][fifthDim] += f.getFrequency(); }
					}
				}
			}
		}
		controls.add(layerControls);

	
		// 4. compute stack controls
		matchingCriteria.clear();
		int[][][][] stackControls = new int[rowAttributeValues.size()][columnAttributeValues.size()][layerAttributeValues.size()][fifthAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));

					for (int fifthDim=0; fifthDim<fifthAttributeValues.size(); fifthDim++) {
						matchingCriteria.put(fifthAttribute, fifthAttributeValues.get(fifthDim));

						stackControls[row][col][layer][fifthDim] = 0;
						List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
						for (AttributeValuesFrequency f : matchingFrequencies) { stackControls[row][col][layer][fifthDim] += f.getFrequency(); }
					}
				}
			}
		}
		controls.add(stackControls);

	
		// 5. compute fifth dimension/attribute controls
		matchingCriteria.clear();
		int[][][][] fifthAttributeControls = new int[rowAttributeValues.size()][columnAttributeValues.size()][layerAttributeValues.size()][stackAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));

					for (int stack=0; stack<stackAttributeValues.size(); stack++) {
						matchingCriteria.put(stackAttribute, stackAttributeValues.get(stack));

						fifthAttributeControls[row][col][layer][stack] = 0;
						List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
						for (AttributeValuesFrequency f : matchingFrequencies) { fifthAttributeControls[row][col][layer][stack] += f.getFrequency(); }
					}
				}
			}
		}
		controls.add(fifthAttributeControls);

	
		// TODO ensure that sum(rowControls) == sum(columnControls) == sum(layerControls) == sum(stackControls) == sum(fifthAttributeControls) ELSE raise exception
	}
	

	@Override
	protected FiveWayIteration createIPFIteration() throws GenstarException {
		return new FiveWayIteration(this);
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
			AbstractAttribute fifthAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX);
			List<AttributeValue> fifthAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.FIFTH_ATTRIBUTE_INDEX);

			selectionProbabilities = new ArrayList<AttributeValuesFrequency>();
			Map<AbstractAttribute, AttributeValue> attributeValues;
			FiveWayIteration lastIpfIteration = (FiveWayIteration)iterations.get(iterations.size() - 1);
			double[][][][][] iterationData = lastIpfIteration.getCopyData();
			
			for (int row=0; row<iterationData.length; row++) {
				attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
				attributeValues.put(rowAttribute, rowAttributeValues.get(row));
				
				for (int column=0; column<iterationData[0].length; column++) {
					attributeValues.put(columnAttribute, columnAttributeValues.get(column));
					
					for (int layer=0; layer<iterationData[0][0].length; layer++) {
						attributeValues.put(layerAttribute,  layerAttributeValues.get(layer));
						
						for (int stack=0; stack<iterationData[0][0][0].length; stack++) {
							attributeValues.put(stackAttribute, stackAttributeValues.get(stack));
							
							for (int fifthDim=0; fifthDim<iterationData[0][0][0][0].length; fifthDim++) {
								attributeValues.put(fifthAttribute, fifthAttributeValues.get(fifthDim));
								int selectionProba = (int) Math.round(iterationData[row][column][layer][stack][fifthDim]);
								selectionProbabilities.add(new AttributeValuesFrequency(attributeValues, selectionProba));
							}
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
