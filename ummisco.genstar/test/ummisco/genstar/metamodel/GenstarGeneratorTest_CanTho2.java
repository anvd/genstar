package ummisco.genstar.metamodel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ummisco.genstar.data.CanThoData;
import ummisco.genstar.data.CanThoData.Scenario2;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;
import ummisco.genstar.util.GenstarFileUtils;

public class GenstarGeneratorTest_CanTho2 {

	
	private static class HouseholdData {

		public static final String[] living_place_values = { "urban", "rural" };
		
		// "Household" population +
		// Note : "city" in data is represented as "urban" here!
		// Rule 1 : Household by size (number of members) and type (urban-permanent, urban-semi-permanent, urban-less-permanent, urban-simple, urban-not-stated, rural-permanent, rural-semi-permanent, rural-less-permanent, rural-simple, rural-not-stated)
		/**
		 * Type : Frequency distribution
		 * Attributes : 
		 * 	size : enumeration of integers (1..9)
		 * 	type : enumeration of strings { permanent, semi-permanent, less-permanent, simple, not-stated }
		 *  living_place : enumeration of strings { urban, rural }
		 * 
		 * Input attribute :
		 * 	{}
		 * 
		 * Output attributes :
		 * 	{ size, type, living_place }
		 */
		public static final int[][] household_size_by_types = {
			{ 1,  696,  8751, 1150, 1420,  4,  124,  1380,  651,  1399, 1 }, // { size, urban-permanent, urban-semi-permanent, urban-less-permanent, urban-simple, urban-not-stated, rural-permanent, rural-semi-permanent, rural-less-permanent, rural-simple, rural-not-stated }
			{ 2, 1357, 17798, 2128, 2150, 10,  367,  3392, 1505,  2533, 1 },
			{ 3, 2482, 30598, 4812, 5136, 15,  671,  6995, 4130,  6910, 0 },
			{ 4, 3302, 35442, 6599, 6568,  7, 1164, 10932, 6548, 10211, 2 },
			{ 5, 2234, 18642, 3418, 3043,  6, 1149,  7408, 3608,  5064, 2 },
			{ 6, 1706, 12903, 1935, 1535,  2,  983,  5029, 2228,  2521, 2 },
			{ 7,  703,  4628,  775,  549,  3,  424,  1850,  748,   831, 2 },
			{ 8,  413,  2865,  426,  328,  0,  235,   984,  386,   459, 0 },
			{ 9,  445,  3546,  465,  331,  0,  196,   878,  397,   432, 0 },
		};

		public static final String[] household_type_values = { "permanent", "semi-permanent", "less-permanent", "simple", "not-stated" };
		// "Household" population -
				
	}
	
	
	private SampleFreeGenerator householdPopGenerator1;
	private FrequencyDistributionGenerationRule generator1Rule1;
	
	public static final String GENERATOR1_RULE1_NAME = "Household population by size, type and living_place";

	// percentages of household population to generate
	private float[] percentages = { 0.01f, 0.05f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1, 1.25f, 1.5f, 1.75f, 2 };
	
	private int[] nbOfEntitiesToGenerate = new int[percentages.length];

	
	public GenstarGeneratorTest_CanTho2() throws GenstarException {
		for (int i=0; i<percentages.length; i++) {
			nbOfEntitiesToGenerate[i] = Math.round(286058f * percentages[i]);

//			System.out.println(percentages[i]*100 + " percent with " + nbOfEntitiesToGenerate[i] + " entities.");
		}
		

		initializeHouseholdPopGenerator1();
	}
	
	private void initializeHouseholdPopGenerator1() throws GenstarException {
		
		householdPopGenerator1 = new SampleFreeGenerator("Household population generator 1", 286058); // data inconsistency?

		// create attributes +
		
		UniqueValuesAttribute householdSizeAttr = new UniqueValuesAttribute(householdPopGenerator1, "size", DataType.INTEGER);
		for (int size = 1; size < (HouseholdData.household_size_by_types.length + 1); size++) {
			householdSizeAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(size)));
		}
		householdPopGenerator1.addAttribute(householdSizeAttr);
		
		UniqueValuesAttribute householdTypeAttr = new UniqueValuesAttribute(householdPopGenerator1, "type", DataType.STRING);
		for (String type : HouseholdData.household_type_values) {
			householdTypeAttr.add(new UniqueValue(DataType.STRING, type));
		}
		householdPopGenerator1.addAttribute(householdTypeAttr);
		
		
		UniqueValue urbanValue = new UniqueValue(DataType.STRING, HouseholdData.living_place_values[0]);
		UniqueValue ruralValue = new UniqueValue(DataType.STRING, HouseholdData.living_place_values[1]);
		 
		UniqueValuesAttribute householdLivingPlaceAttr = new UniqueValuesAttribute(householdPopGenerator1, "living_place", DataType.STRING);
		householdLivingPlaceAttr.add(urbanValue);
		householdLivingPlaceAttr.add(ruralValue);
		householdPopGenerator1.addAttribute(householdLivingPlaceAttr);
		 
		// create attributes -
		
		// create generation rules +
		// generator1Rule1
		generator1Rule1 = new FrequencyDistributionGenerationRule(householdPopGenerator1, GENERATOR1_RULE1_NAME);
		generator1Rule1.appendOutputAttribute(householdSizeAttr);
		generator1Rule1.appendOutputAttribute(householdTypeAttr);
		generator1Rule1.appendOutputAttribute(householdLivingPlaceAttr); 
		generator1Rule1.generateAttributeValuesFrequencies();
		householdPopGenerator1.appendGenerationRule(generator1Rule1);
		
		// an element of household_size_by_types_2 : 
		//		{ size, urban-permanent, urban-semi-permanent, urban-less-permanent, urban-simple, urban-not-stated, 
		//			rural-permanent, rural-semi-permanent, rural-less-permanent, rural-simple, rural-not-stated }
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (int[] types : HouseholdData.household_size_by_types) {
			attributeValues.clear();
			
			attributeValues.put(householdSizeAttr, new UniqueValue(DataType.INTEGER, Integer.toString(types[0])));
			
			// urban living places
			attributeValues.put(householdLivingPlaceAttr, urbanValue);
			for (int typeIndex=0; typeIndex<Scenario2.household_type2_values.length; typeIndex++) {
				attributeValues.put(householdTypeAttr, new UniqueValue(DataType.STRING, HouseholdData.household_type_values[typeIndex]));
				
				generator1Rule1.setFrequency(attributeValues, types[typeIndex+1]);
			}
			 
			// rural living places
			attributeValues.put(householdLivingPlaceAttr, ruralValue);
			for (int typeIndex=0; typeIndex<Scenario2.household_type2_values.length; typeIndex++) {
				attributeValues.put(householdTypeAttr, new UniqueValue(DataType.STRING, HouseholdData.household_type_values[typeIndex]));

				generator1Rule1.setFrequency(attributeValues, types[typeIndex+6]);
			}
		}
		
		// create generation rules -
		 
	}


	@Test public void testGenerateHouseholdPopulation() throws GenstarException {

		System.out.println("Beginning testGenerateHouseholdPopulation");
		
		String basePath = "./household";
		File baseDir = new File(basePath);
		baseDir.mkdir();
		
		int times = 30;
		for (int percentageIndex=0; percentageIndex<percentages.length; percentageIndex++) {
			
			int nbOfEntities = nbOfEntitiesToGenerate[percentageIndex];
			String percentageName = Integer.toString( (int)(percentages[percentageIndex] * 100) ) + "Percent";
			
			System.out.println("Started generating " + Integer.toString( (int)(percentages[percentageIndex] * 100) ) + " percent of household population with " + nbOfEntities + " inhabitants.");
			
			// set name nbOfEntities for the generator
			householdPopGenerator1.setNbOfEntities(nbOfEntities);
			
			String subFolderName = percentageName;
			File path = new File(basePath + "/" + subFolderName);
			path.mkdir();
			
			
			for (int currentIteration=0; currentIteration<times; currentIteration++) {
				
				System.out.println("Started iteration : " + currentIteration);

				
				int[][] household_size_by_types_copy = new int[HouseholdData.household_size_by_types.length][11];
				for (int rowIndex = 0; rowIndex < HouseholdData.household_size_by_types.length; rowIndex++) {
					for (int colIndex = 0; colIndex < HouseholdData.household_size_by_types[rowIndex].length; colIndex++) {
						household_size_by_types_copy[rowIndex][colIndex] = HouseholdData.household_size_by_types[rowIndex][colIndex];
					}
				}
				
				
				int[][] generated_household_size_by_types = new int[HouseholdData.household_size_by_types.length][11];
				for (int rowIndex = 0; rowIndex < HouseholdData.household_size_by_types.length; rowIndex++) {
					generated_household_size_by_types[rowIndex][0] =  HouseholdData.household_size_by_types[rowIndex][0];
					
					generated_household_size_by_types[rowIndex][1] = generated_household_size_by_types[rowIndex][2] 
						= generated_household_size_by_types[rowIndex][3] = generated_household_size_by_types[rowIndex][4] 
						= generated_household_size_by_types[rowIndex][5] = generated_household_size_by_types[rowIndex][6] 
						= generated_household_size_by_types[rowIndex][7] = generated_household_size_by_types[rowIndex][8] 
						= generated_household_size_by_types[rowIndex][9] = generated_household_size_by_types[rowIndex][10] = 0;
				}
				
		
				Map<String, Integer> householdTypes = new HashMap<String, Integer>();
				for (int i=0; i<CanThoData.Scenario2.household_type2_values.length; i++) { householdTypes.put(CanThoData.Scenario2.household_type2_values[i], i + 1); }
				
				
				IPopulation householdPopulation = householdPopGenerator1.generate();
				for (Entity household : householdPopulation.getEntities()) {
					int size = ((UniqueValue) household.getEntityAttributeValueByNameOnData("size").getAttributeValueOnEntity()).getIntValue();
					
					String livingPlace = ( (UniqueValue) household.getEntityAttributeValueByNameOnData("living_place").getAttributeValueOnEntity() ).getStringValue();
		
					String type = ( (UniqueValue) household.getEntityAttributeValueByNameOnData("type").getAttributeValueOnEntity()).getStringValue();
					int typeIndex = householdTypes.get(type);
					if (livingPlace.equals("rural")) { typeIndex += 5; }
					
					
					generated_household_size_by_types[size-1][typeIndex] = ++generated_household_size_by_types[size-1][typeIndex]; 
				}
				
						
				// header : Household size, Permanent, Semi-permanent, Less-permanent, Simple, Not-Stated
				String[] header = new String[6];
				header[0] = "Household size";
				header[1] = "Permanent";
				header[2] = "Semi-permanent";
				header[3] = "Less-permanent";
				header[4] = "Simple";
				header[5] = "Not-Stated";
				String[] urbanLine, ruralLine;

				// urban
				List<String[]> urbanLines = new ArrayList<String[]>();
				urbanLines.add(header);
				
				// rural
				List<String[]> ruralLines = new ArrayList<String[]>();
				ruralLines.add(header);
				
				for (int rowIndex = 0; rowIndex < generated_household_size_by_types.length; rowIndex++) {
				
					urbanLine = new String[6];
					urbanLine[0] = Integer.toString(rowIndex + 1); // household size
					for (int urbanIndex = 1; urbanIndex <= 5; urbanIndex++) {
						urbanLine[urbanIndex] = Integer.toString(generated_household_size_by_types[rowIndex][urbanIndex]);
					}
					urbanLines.add(urbanLine);
					
					ruralLine = new String[6];
					ruralLine[0] = Integer.toString(rowIndex + 1); // household size
					for (int ruralIndex = 6; ruralIndex <= 10; ruralIndex++) {
						ruralLine[ruralIndex - 5] = Integer.toString(generated_household_size_by_types[rowIndex][ruralIndex]);
					}
					ruralLines.add(ruralLine);
				}
				
				// urban file
				GenstarFileUtils.writeCSVFile(basePath + "/" + subFolderName + "/" + percentageName + "_urban_household_size_type_Iteration" + Integer.toString(currentIteration) + ".csv", urbanLines);
				
				// rural file
				GenstarFileUtils.writeCSVFile(basePath + "/" + subFolderName + "/" + percentageName + "_rural_household_size_type_Iteration" + Integer.toString(currentIteration) + ".csv", ruralLines);
				
				
				System.out.println("Finished iteration : " + currentIteration);
			}			
			
			
			
			System.out.println("Finished generating " + Integer.toString( (int)(percentages[percentageIndex] * 100) ) + " percent of household population with " + nbOfEntities + " inhabitants.");
			System.out.println("---------------\n");
		}		

		System.out.println("Finished testGenerateHouseholdPopulation");
				 
	}
}
