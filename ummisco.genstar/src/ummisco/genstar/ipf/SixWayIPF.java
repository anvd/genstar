package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;

public class SixWayIPF extends IPF<double[][][][][][], int[][][][][], double[][][][][]> {

	protected SixWayIPF(final SampleDataGenerationRule generationRule) throws GenstarException {
		super(generationRule);
	}

	@Override
	protected int getNbOfControlledAttributes() { return 6; }

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
		AbstractAttribute sixthAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.SIXTH_ATTRIBUTE_INDEX);
		List<AttributeValue> sixthAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.SIXTH_ATTRIBUTE_INDEX);

		data = new double[rowAttributeValues.size()][columnAttributeValues.size()][layerAttributeValues.size()][stackAttributeValues.size()][fifthAttributeValues.size()][sixthAttributeValues.size()];
		
		Map<AbstractAttribute, AttributeValue> matchingCondition = new HashMap<AbstractAttribute, AttributeValue>();
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCondition.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCondition.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCondition.put(layerAttribute, layerAttributeValues.get(layer));
					
					for (int stack=0; stack<stackAttributeValues.size(); stack++) {
						matchingCondition.put(stackAttribute, stackAttributeValues.get(stack));
						
						for (int fifthDim=0; fifthDim<fifthAttributeValues.size(); fifthDim++) {
							matchingCondition.put(fifthAttribute, fifthAttributeValues.get(fifthDim));
						
							for (int sixthDim=0; sixthDim<sixthAttributeValues.size(); sixthDim++) {
								matchingCondition.put(sixthAttribute, sixthAttributeValues.get(sixthDim));
								data[row][col][layer][stack][fifthDim][sixthDim] = sampleData.getSampleEntityPopulation().countMatchingEntitiesByAttributeValuesOnEntity(matchingCondition);
								// TODO if (data[row][col][layer][stack][fifthDim][sixthDim] == 0) raise warning
							}
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
		AbstractAttribute sixthAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.SIXTH_ATTRIBUTE_INDEX);
		List<AttributeValue> sixthAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.SIXTH_ATTRIBUTE_INDEX);

		
		// 1. compute row controls
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		int[][][][][] rowControls = new int[columnAttributeValues.size()][layerAttributeValues.size()][stackAttributeValues.size()][fifthAttributeValues.size()][sixthAttributeValues.size()];
		for (int col=0; col<columnAttributeValues.size(); col++) {
			matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				
				for (int stack=0; stack<stackAttributeValues.size(); stack++) {
					matchingCriteria.put(stackAttribute,  stackAttributeValues.get(stack));
					
					for (int fifthDim=0; fifthDim<fifthAttributeValues.size(); fifthDim++) {
						matchingCriteria.put(fifthAttribute, fifthAttributeValues.get(fifthDim));
						
						for (int sixthDim=0; sixthDim<sixthAttributeValues.size(); sixthDim++) {
							matchingCriteria.put(sixthAttribute, sixthAttributeValues.get(sixthDim));
							
							rowControls[col][layer][stack][fifthDim][sixthDim] = 0;
							List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
							for (AttributeValuesFrequency f : matchingFrequencies) { rowControls[col][layer][stack][fifthDim][sixthDim] += f.getFrequency(); }
						}
					}
				}
			}
		}
		controls.add(rowControls);

	
		// 2. compute column controls
		matchingCriteria.clear();
		int[][][][][] columnControls = new int[rowAttributeValues.size()][layerAttributeValues.size()][stackAttributeValues.size()][fifthAttributeValues.size()][sixthAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int layer=0; layer<layerAttributeValues.size(); layer++) {
				matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));
				
				for (int stack=0; stack<stackAttributeValues.size(); stack++) {
					matchingCriteria.put(stackAttribute, stackAttributeValues.get(stack));
					
					for (int fifthDim=0; fifthDim<fifthAttributeValues.size(); fifthDim++) {
						matchingCriteria.put(fifthAttribute, fifthAttributeValues.get(fifthDim));
						
						for (int sixthDim=0; sixthDim<sixthAttributeValues.size(); sixthDim++) {
							matchingCriteria.put(sixthAttribute, sixthAttributeValues.get(sixthDim));
							
							columnControls[row][layer][stack][fifthDim][sixthDim] = 0;
							List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
							for (AttributeValuesFrequency f : matchingFrequencies) { columnControls[row][layer][stack][fifthDim][sixthDim] += f.getFrequency(); }
						}
					}
				}
			}
		}
		controls.add(columnControls);

	
		// 3. compute layer controls
		matchingCriteria.clear();
		int[][][][][] layerControls = new int[rowAttributeValues.size()][columnAttributeValues.size()][stackAttributeValues.size()][fifthAttributeValues.size()][sixthAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int stack=0; stack<stackAttributeValues.size(); stack++) {
					matchingCriteria.put(stackAttribute, stackAttributeValues.get(stack));
					
					for (int fifthDim=0; fifthDim<fifthAttributeValues.size(); fifthDim++) {
						matchingCriteria.put(fifthAttribute, fifthAttributeValues.get(fifthDim));
						
						for (int sixthDim=0; sixthDim<sixthAttributeValues.size(); sixthDim++) {
							matchingCriteria.put(sixthAttribute, sixthAttributeValues.get(sixthDim));
							
							layerControls[row][col][stack][fifthDim][sixthDim] = 0;
							List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
							for (AttributeValuesFrequency f : matchingFrequencies) { layerControls[row][col][stack][fifthDim][sixthDim] += f.getFrequency(); }
						}
					}
				}
			}
		}
		controls.add(layerControls);

	
		// 4. compute stack controls
		matchingCriteria.clear();
		int[][][][][] stackControls = new int[rowAttributeValues.size()][columnAttributeValues.size()][layerAttributeValues.size()][fifthAttributeValues.size()][sixthAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));

					for (int fifthDim=0; fifthDim<fifthAttributeValues.size(); fifthDim++) {
						matchingCriteria.put(fifthAttribute, fifthAttributeValues.get(fifthDim));

						for (int sixthDim=0; sixthDim<sixthAttributeValues.size(); sixthDim++) {
							matchingCriteria.put(sixthAttribute, sixthAttributeValues.get(sixthDim));
							
							stackControls[row][col][layer][fifthDim][sixthDim] = 0;
							List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
							for (AttributeValuesFrequency f : matchingFrequencies) { stackControls[row][col][layer][fifthDim][sixthDim] += f.getFrequency(); }
						}
					}
				}
			}
		}
		controls.add(stackControls);

	
		// 5. compute fifth dimension/attribute controls
		matchingCriteria.clear();
		int[][][][][] fifthAttributeControls = new int[rowAttributeValues.size()][columnAttributeValues.size()][layerAttributeValues.size()][stackAttributeValues.size()][sixthAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));

					for (int stack=0; stack<stackAttributeValues.size(); stack++) {
						matchingCriteria.put(stackAttribute, stackAttributeValues.get(stack));

						for (int sixthDim=0; sixthDim<sixthAttributeValues.size(); sixthDim++) {
							matchingCriteria.put(sixthAttribute, sixthAttributeValues.get(sixthDim));

							fifthAttributeControls[row][col][layer][stack][sixthDim] = 0;
							List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
							for (AttributeValuesFrequency f : matchingFrequencies) { fifthAttributeControls[row][col][layer][stack][sixthDim] += f.getFrequency(); }
						}
					}
				}
			}
		}
		controls.add(fifthAttributeControls);

	
		// 6. compute sixth dimension/attribute controls
		matchingCriteria.clear();
		int[][][][][] sixthAttributeControls = new int[rowAttributeValues.size()][columnAttributeValues.size()][layerAttributeValues.size()][stackAttributeValues.size()][fifthAttributeValues.size()];
		for (int row=0; row<rowAttributeValues.size(); row++) {
			matchingCriteria.put(rowAttribute, rowAttributeValues.get(row));
			
			for (int col=0; col<columnAttributeValues.size(); col++) {
				matchingCriteria.put(columnAttribute, columnAttributeValues.get(col));
				
				for (int layer=0; layer<layerAttributeValues.size(); layer++) {
					matchingCriteria.put(layerAttribute, layerAttributeValues.get(layer));

					for (int stack=0; stack<stackAttributeValues.size(); stack++) {
						matchingCriteria.put(stackAttribute, stackAttributeValues.get(stack));

						for (int fifthDim=0; fifthDim<fifthAttributeValues.size(); fifthDim++) {
							matchingCriteria.put(fifthAttribute, fifthAttributeValues.get(fifthDim));

							sixthAttributeControls[row][col][layer][stack][fifthDim] = 0;
							List<AttributeValuesFrequency> matchingFrequencies = controlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
							for (AttributeValuesFrequency f : matchingFrequencies) { sixthAttributeControls[row][col][layer][stack][fifthDim] += f.getFrequency(); }
						}
					}
				}
			}
		}
		controls.add(sixthAttributeControls);
		
		
		// TODO ensure that sum(rowControls) == sum(columnControls) == sum(layerControls) == sum(stackControls) == sum(fifthAttributeControls) == sum(sixthAttributeControls) ELSE raise exception
	}

	@Override
	protected SixWayIteration createIPFIteration() throws GenstarException {
		return new SixWayIteration(this);
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
			AbstractAttribute sixthAttribute = getControlledAttribute(IPF_ATTRIBUTE_INDEXES.SIXTH_ATTRIBUTE_INDEX);
			List<AttributeValue> sixthAttributeValues = getAttributeValues(IPF_ATTRIBUTE_INDEXES.SIXTH_ATTRIBUTE_INDEX);

			selectionProbabilities = new ArrayList<AttributeValuesFrequency>();
			Map<AbstractAttribute, AttributeValue> attributeValues;
			SixWayIteration lastIpfIteration = (SixWayIteration)iterations.get(iterations.size() - 1);
			double[][][][][][] iterationData = lastIpfIteration.getCopyData();
			
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
							
								for (int sixthDim=0; sixthDim<iterationData[0][0][0][0][0].length; sixthDim++) {
									attributeValues.put(sixthAttribute, sixthAttributeValues.get(sixthDim));

									int selectionProba = (int) Math.round(iterationData[row][column][layer][stack][fifthDim][sixthDim]);
									selectionProbabilities.add(new AttributeValuesFrequency(attributeValues, selectionProba));
								}
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
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
