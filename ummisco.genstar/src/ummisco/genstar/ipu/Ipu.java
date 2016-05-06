package ummisco.genstar.ipu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.sample_data.CompoundSampleData;


public class Ipu {
	
	private IpuGenerationRule generationRule;
	
	private IpuControlTotals controlTotals;
	
	
	private int[][] ipuMatrix;
	
	/** Number of group entities in sample data */
	private int numberOfRows;
	
	/** Number of group and component entities' types */
	private int numberOfColumns;
	
	/** Number of group types */
	private int groupTypes;
	
	/** Number of component types */
	private int componentTypes;
	
	
	private List<AttributeValuesFrequency> groupTypeConstraints;
	
	private List<AttributeValuesFrequency> componentTypeConstraints;
	
	
	/** An element (a list itself) contains weights that group entities are picked/selected in each iteration */
	private List<List<Double>> weights;
	
	/** An element (a list itself) contains sums of weights representing the probabilities that entity types are picked/selected in each iteration */
	private List<List<Double>> weightedSums;
	
	/** The number of entity types that we need to reach (i.e., total constraints) */
	private List<Integer> constraints;
	
	/** An element (a list itself) represents the differences between the weighted sums in each iteration and the constraints (i.e., total constraints)  */
	private List<List<Double>> deltas;
	
	/** An element (a double) is the sum of deltas in each iteration, representing the goodness of fit */
	private List<Double> sumDeltas;
	
	
	/** Elements in each column that we need to compute weights in each iteration */
	private List<List<Integer>> tobeComputedWeightsRowsByColumns;
	
	/** Elements in each column that we don't need to compute weights in each iteration */
	private List<List<Integer>> notTobeComputedWeightsRowsByColumns;
	
	private List<Entity> groupSampleEntities;
	
	private Map<Entity, Integer> selectionProbabilities = null;
	

	public Ipu(final IpuGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("Parameter generationRule can not be null"); }
		
		this.generationRule = generationRule;
		this.controlTotals = generationRule.getControlTotals();
		
		initializeData();
	}
	
	private void initializeData() throws GenstarException {
		// initialize ipuMatrix[][]
		// 		the number of rows is equal to the number of households in the sample data
		//		the number of columns is equal to the sum of household types and person types
		//			household types are determined by household controlled variables such as household size, income, ...
		//			person types are determined by person controlled variables such as age, race, gender, ...
		
		groupSampleEntities = generationRule.getSampleData().getSampleEntityPopulation().getEntities();
		numberOfRows = groupSampleEntities.size();
		groupTypes = controlTotals.getGroupTypes();
		componentTypes = controlTotals.getComponentTypes();
		numberOfColumns = groupTypes + componentTypes; 
		ipuMatrix = new int[numberOfRows][numberOfColumns];
		
		groupTypeConstraints = controlTotals.getGroupTypeConstraints();
		componentTypeConstraints = controlTotals.getComponentTypeConstraints();
		
		fillIpuMatrix();
		
		// initialize constraints
		constraints = new ArrayList<Integer>(numberOfColumns);
		for (AttributeValuesFrequency groupF : groupTypeConstraints) { constraints.add(groupF.getFrequency()); }
		for (AttributeValuesFrequency componentF : componentTypeConstraints) { constraints.add(componentF.getFrequency()); }

		// initialize weightedSums
		weightedSums = new ArrayList<List<Double>>();
		List<Double> weightedSums0 = new ArrayList<Double>();
		for (int column=0; column<numberOfColumns; column++) {
			double weightSumAtColumn = 0;
			for (int row=0; row<numberOfRows; row++) { weightSumAtColumn += ipuMatrix[row][column]; }
			weightedSums0.add(weightSumAtColumn);
		}
		weightedSums.add(weightedSums0);
		
		// initialize weights0
		weights = new ArrayList<List<Double>>();
		List<Double> weights0 = new ArrayList<Double>(numberOfRows);
		for (int row=0; row<numberOfRows; row++) { weights0.add(new Double(1)); }
		weights.add(weights0);
		
		// initialize deltas0
		deltas = new ArrayList<List<Double>>();
		List<Double> deltas0 = computeDeltas();
		deltas.add(deltas0);
		
		// initialize sumDeltas
		sumDeltas = new ArrayList<Double>();
		sumDeltas.add(computeSumDelta());
		
		// with each element of ipuMatrix, decide in advance whether we need to "compute weight" for that element later on or not
		tobeComputedWeightsRowsByColumns = new ArrayList<List<Integer>>();
		notTobeComputedWeightsRowsByColumns = new ArrayList<List<Integer>>();
		for (int column=0; column<numberOfColumns; column++) {
			
			List<Integer> tobeComputedWeightsRows = new ArrayList<Integer>();
			List<Integer> notTobeComputedWeightsRows = new ArrayList<Integer>(); 
			
			for (int row=0; row<numberOfRows; row++) {
				if (ipuMatrix[row][column] != 0) {
					tobeComputedWeightsRows.add(row);
				} else {
					notTobeComputedWeightsRows.add(row);
				}
			}
			
			tobeComputedWeightsRowsByColumns.add(tobeComputedWeightsRows);
			notTobeComputedWeightsRowsByColumns.add(notTobeComputedWeightsRows);
		}
	}
	
	
	private List<Double> computeDeltas() {
		List<Double> computedDeltas = new ArrayList<Double>();
		List<Double> latestWeightedSums = weightedSums.get(weightedSums.size() - 1);
		
		for (int column=0; column<numberOfColumns; column++) {
			computedDeltas.add(Math.abs(latestWeightedSums.get(column) - constraints.get(column)) / constraints.get(column));
		}
		
		return computedDeltas;
	}
	
	
	private double computeSumDelta() {
		double sumDelta = 0;
		for (double d : deltas.get(deltas.size() - 1)) { sumDelta += d; }
		
		return sumDelta;
	}
	
	
	private void fillIpuMatrix() throws GenstarException {
		
		CompoundSampleData compoundSampleData = generationRule.getSampleData();
		String componentPopulationName = compoundSampleData.getSampleEntityPopulation().getName();
		List<AbstractAttribute> groupControlledAttributes = generationRule.getGroupControlledAttributes();
		List<AbstractAttribute> componentControlledAttributes = generationRule.getComponentControlledAttributes();
		for (int row=0; row<numberOfRows; row++) {
			Entity groupEntity = groupSampleEntities.get(row);
			
			Map<AbstractAttribute, AttributeValue> groupControlledAttributesValuesOnData = groupEntity.getAttributesValuesOnData(groupControlledAttributes);
			
			// fill "group" part
			for (int column=0; column<groupTypes; column++) {
				AttributeValuesFrequency groupAvf =  groupTypeConstraints.get(column);
				if (groupAvf.matchAttributeValuesOnData(groupControlledAttributesValuesOnData)) {
					ipuMatrix[row][column] += 1;
					break;
				}
			}
			
			// fill "component" part
			IPopulation componentPopulation = groupEntity.getComponentPopulation(componentPopulationName);
			if (componentPopulation != null) {
				for (Entity componentEntity : componentPopulation.getEntities()) {
					Map<AbstractAttribute, AttributeValue> componentControlledAttributesValuesOnData = componentEntity.getAttributesValuesOnData(componentControlledAttributes);

					for (int column=groupTypes; column<numberOfColumns; column++) {
						AttributeValuesFrequency componentAvf = componentTypeConstraints.get(column - groupTypes);
						if (componentAvf.matchAttributeValuesOnData(componentControlledAttributesValuesOnData)) {
							ipuMatrix[row][column] += 1;
							break;
						}
					}
				}
			}
		}
	}
	
	
	public void fit() throws GenstarException {
		// run "fit" once only
		if (selectionProbabilities == null) {
			selectionProbabilities = new HashMap<Entity, Integer>();
		} else {
			return;
		}
		
		
		for (int iteration=0; iteration<generationRule.getMaxIterations(); iteration++) {
			for (int column=0; column<numberOfColumns; column++) {
				computeWeights(column);
				computeWeightedSums();
			}

			evaluateGoodnessOfFit();
		}
		
		// build selectionProbabilities based on the latest selection weights of group sample entities
		List<Double> latestWeights = weights.get(weights.size() - 1);
		for (int index=0; index<groupSampleEntities.size(); index++) {
			selectionProbabilities.put(groupSampleEntities.get(index), (int) Math.round(latestWeights.get(index)));
		}
	}
	
	
	private void computeWeights(int column) {
		List<Double> previousWeights = weights.get(weights.size() - 1);
		int constraint = constraints.get(column);
		double previousWeightedSum = weightedSums.get(weightedSums.size() - 1).get(column);
		
		List<Integer> tobeComputedWeightsRows = tobeComputedWeightsRowsByColumns.get(column);
		List<Integer> notTobeComputedWeightsRows = notTobeComputedWeightsRowsByColumns.get(column);
		
		double[] newWeightsArray = new double[numberOfRows];
		for (int row : notTobeComputedWeightsRows) { newWeightsArray[row] = previousWeights.get(row); }  // no need to compute weights, the assign the previous weights
		for (int row : tobeComputedWeightsRows) {
			newWeightsArray[row] = (((double)constraint) / previousWeightedSum) * previousWeights.get(row); // newWeight = constraint / previousWeightedSum * previousWeight
		}
		
		List<Double> newWeights = new ArrayList<Double>(newWeightsArray.length);
		for (double newW : newWeightsArray) { newWeights.add(newW); }
		
		weights.add(newWeights);
	}
	
	
	private void computeWeightedSums() {
		List<Double> latestWeights = weights.get(weights.size() - 1);
		
		List<Double> latestWeightedSums = new ArrayList<Double>(numberOfColumns);
		for (int column=0; column<numberOfColumns; column++) {
			
			double latestWeightedSumByColumn = 0;
			for (int row : tobeComputedWeightsRowsByColumns.get(column)) {
				latestWeightedSumByColumn += latestWeights.get(row) * ipuMatrix[row][column];
			}
			
			latestWeightedSums.add(latestWeightedSumByColumn);
		}
		
		weightedSums.add(latestWeightedSums);
	}
	
	
	public Map<Entity, Integer> getSelectionProbabilities() throws GenstarException {
		if (selectionProbabilities == null) { fit(); }
		return new HashMap<Entity, Integer>(selectionProbabilities);
	}
	
	
	private void evaluateGoodnessOfFit() {
		deltas.add(computeDeltas());
		sumDeltas.add(computeSumDelta());
	}
	
	
	public void printDebug() {
		System.out.println("Ipu data");
		System.out.println("Number of rows: " + numberOfRows);
		System.out.println("Number of columns: " + numberOfColumns);
		
		System.out.print("Initial weights: ");
		for (Double w : weights.get(0)) { System.out.print(w + ", "); }
		System.out.println();
		
		System.out.print("Initial weighted sums: ");
		for (Double w : weightedSums.get(0)) { System.out.print(w + ", "); }
		System.out.println();
		
		System.out.print("Constraints: ");
		for (int c : constraints) { System.out.print(c + ", "); }
		System.out.println();
		
		System.out.print("Initial deltas: ");
		for (Double d : deltas.get(0)) { System.out.print(d + ", "); }
		System.out.println();
		
		System.out.println("Initial delta: " + sumDeltas.get(0));
		
		int column = 0;
		System.out.println("groupTypeValues:");
		for (AttributeValuesFrequency avf : groupTypeConstraints) { 
			System.out.println("column " + column + ": "  + avf.toString());
			column++;
		}
		
		System.out.println("componentTypeValues:");
		for (AttributeValuesFrequency avf : componentTypeConstraints) { 
			System.out.println("column " + column + ": "  + avf.toString());
			column++;
		}

		for (int row=0; row<numberOfRows; row++) {
			System.out.print("Row " + row + ": ");
			
			for (int col=0; col<numberOfColumns; col++) {
				System.out.print(ipuMatrix[row][col] + ", ");
			}
			System.out.println();
		}
	}
	
	public void printGoodnessOfFit() {
		System.out.println("Goodness of fit");
		for (int iteration=0; iteration<deltas.size(); iteration++) {
			System.out.print("Iteration " + iteration + ": sumDelta: " + sumDeltas.get(iteration));
			
			System.out.print(" and deltas (");
			for (double d : deltas.get(iteration)) { System.out.print(d + ","); }
			System.out.print(")");
			
			System.out.println();
		}
	}
}
