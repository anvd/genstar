package ummisco.genstar.metamodel;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ummisco.genstar.GenstarGenerator;
import ummisco.genstar.data.CanThoData;
import ummisco.genstar.data.CanThoPopLinkers;
import ummisco.genstar.data.CanThoPopLinkers.Scenario1PopLinker;
import ummisco.genstar.data.CanThoPopLinkers.Scenario2PopLinker;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.util.GenstarFileUtils;

public class GenstarGeneratorTest_CanTho {
	
	private CanThoData data;
	
	private CanThoPopLinkers linkers;
	
	
	public GenstarGeneratorTest_CanTho() throws GenstarException {
		data = new CanThoData();
		linkers = new CanThoPopLinkers();
	}
	
	public void testScenario1NbOfGeneratedInhabitants() throws GenstarException {
//	@Test public void testScenario1NbOfGeneratedInhabitants() throws GenstarException {
		/*
		public static final int[][] age_ranges_1 = {
			{  0,  0,  9354,  8790 },
			{  1,  4, 36413, 34162 },
			{  5,  9, 44112, 41547 },
			{ 10, 14, 43203, 40575 },
			{ 15, 17, 31641, 29527 },
			{ 18, 19, 28150, 27708 },
			{ 20, 24, 66280, 63182 },
			{ 25, 29, 61072, 58324 },
			{ 30, 34, 53169, 51271 },
			{ 35, 39, 50447, 48529 },
			{ 40, 44, 44788, 44767 },
			{ 45, 49, 35478, 38882 },
			{ 50, 54, 26664, 33869 },
			{ 55, 59, 19680, 24671 },
			{ 60, 64, 11479, 13264 },
			{ 65, 69,  7585, 11378 },
			{ 70, 74,  6580, 10474 },
			{ 75, 79,  6949,  8791 },
			{ 80, 84,  4305,  5243 },
			{ 85, 99,  2257,  3875 }
		};
		 */
		
		int[][] age_ranges_1_copy = new int[CanThoData.Scenario1.age_ranges_1.length][4];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario1.age_ranges_1.length; rowIndex++) {
			for (int colIndex = 0; colIndex < CanThoData.Scenario1.age_ranges_1[rowIndex].length; colIndex++) {
				age_ranges_1_copy[rowIndex][colIndex] = CanThoData.Scenario1.age_ranges_1[rowIndex][colIndex];
			}
		}
		
		int[][] generated_age_ranges1 = new int[CanThoData.Scenario1.age_ranges_1.length][4];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario1.age_ranges_1.length; rowIndex++) {
			generated_age_ranges1[rowIndex][0] = CanThoData.Scenario1.age_ranges_1[rowIndex][0];
			generated_age_ranges1[rowIndex][1] = CanThoData.Scenario1.age_ranges_1[rowIndex][1];
			generated_age_ranges1[rowIndex][2] = generated_age_ranges1[rowIndex][3] = 0;
		}
		
		
		
		ISyntheticPopulationGenerator scenario1InhabitantGenerator = data.getScenario1InhabitantGenerator();
		scenario1InhabitantGenerator.setPopulationName("Inhabitant population");
		
		IPopulation inhabitantPopulation = scenario1InhabitantGenerator.generate();
		for (Entity inhabitant : inhabitantPopulation.getEntities()) {
			int age = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity())).getIntValue();
			boolean isMale = ( (UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("sex").getAttributeValueOnEntity()) ).getBooleanValue();

			for (int rowIndex = 0; rowIndex < age_ranges_1_copy.length; rowIndex++) {
				if (age >= age_ranges_1_copy[rowIndex][0] && age <= age_ranges_1_copy[rowIndex][1]) {
					if (isMale) {
						age_ranges_1_copy[rowIndex][2] = --age_ranges_1_copy[rowIndex][2];
						generated_age_ranges1[rowIndex][2] = ++generated_age_ranges1[rowIndex][2];
					} else {
						age_ranges_1_copy[rowIndex][3] = --age_ranges_1_copy[rowIndex][3];
						generated_age_ranges1[rowIndex][3] = ++generated_age_ranges1[rowIndex][3];
					}
					
					break;
				}
			}
		}
		
		int sumZero = 0;
		for (int rowIndex = 0; rowIndex < age_ranges_1_copy.length; rowIndex++) {
			sumZero += age_ranges_1_copy[rowIndex][2];
			sumZero += age_ranges_1_copy[rowIndex][3];
		}
		assertTrue(sumZero == 0);
		
		
		for (int rowIndex = 0; rowIndex < generated_age_ranges1.length; rowIndex++) {
			for (int colIndex = 0; colIndex < generated_age_ranges1[rowIndex].length; colIndex++) {
				System.out.print(generated_age_ranges1[rowIndex][colIndex] + ", ");
			}
			System.out.println();
		}
		
	}
	
	public void testScenario1NbOfGeneratedHouseholds() throws GenstarException {
//	@Test public void testScenario1NbOfGeneratedHouseholds() throws GenstarException {
		/*
		public static final int[][] household_size_by_types_1 = {
			{ 1,  820, 10131,  1801,  2819,  5 }, // { size, permanent, semi-permanent, less-permanent, simple, not-stated }
			{ 2, 1724, 21190,  3633,  4683, 11 },
			{ 3, 3153, 37593,  8942, 12046, 15 },
			{ 4, 4466, 46374, 13147, 16779,  9 },
			{ 5, 3383, 26050,  7026,  8107,  8 },
			{ 6, 2689, 17932,  4163,  4056,  4 },
			{ 7, 1127,  6478,  1523,  1380,  4 },
			{ 8,  648,  3849,   812,   787,  0 },
			{ 9,  641,  4424,   862,   763,  0 },
		};
		 */
		
		
		int[][] household_size_by_types_1_copy = new int[CanThoData.Scenario1.household_size_by_types_1.length][6];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario1.household_size_by_types_1.length; rowIndex++) {
			for (int colIndex = 0; colIndex < CanThoData.Scenario1.household_size_by_types_1[rowIndex].length; colIndex++) {
				household_size_by_types_1_copy[rowIndex][colIndex] = CanThoData.Scenario1.household_size_by_types_1[rowIndex][colIndex];
			}
		}
		
		
		int[][] generated_household_size_by_types_1 = new int[CanThoData.Scenario1.household_size_by_types_1.length][6];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario1.household_size_by_types_1.length; rowIndex++) {
			generated_household_size_by_types_1[rowIndex][0] = CanThoData.Scenario1.household_size_by_types_1[rowIndex][0];
			generated_household_size_by_types_1[rowIndex][1] = generated_household_size_by_types_1[rowIndex][2] = 
				generated_household_size_by_types_1[rowIndex][3] = generated_household_size_by_types_1[rowIndex][4] = generated_household_size_by_types_1[rowIndex][5] = 0;
		}
		
		
//		System.out.println("household_size_by_types_1_copy BEFORE");
//		for (int rowIndex = 0; rowIndex < CanThoData.Scenario1.household_size_by_types_1.length; rowIndex++) {
//			for (int colIndex = 0; colIndex < CanThoData.Scenario1.household_size_by_types_1[rowIndex].length; colIndex++) {
//				System.out.print(household_size_by_types_1_copy[rowIndex][colIndex] + ", ");
//			}
//			System.out.println();
//		}
//		System.out.println("---");

		
		ISyntheticPopulationGenerator scenario1HouseholdGenerator = data.getScenario1HouseholdGenerator();
		scenario1HouseholdGenerator.setPopulationName("Household population");
		
		/*
		public static final String[] household_type1_values = { "permanent", "semi-permanent", "less-permanent", "simple", "not-stated" };
		 */
		Map<String, Integer> householdTypes = new HashMap<String, Integer>();
		for (int i=0; i<CanThoData.Scenario1.household_type1_values.length; i++) { householdTypes.put(CanThoData.Scenario1.household_type1_values[i], i + 1); }
		
		IPopulation householdPopulation = scenario1HouseholdGenerator.generate();
		for (Entity household : householdPopulation.getEntities()) {
			int size = ((UniqueValue) household.getEntityAttributeValueByNameOnData("size").getAttributeValueOnEntity()).getIntValue();

			String type = ( (UniqueValue) household.getEntityAttributeValueByNameOnData("type").getAttributeValueOnEntity()).getStringValue();
			int typeIndex = householdTypes.get(type);
			
			household_size_by_types_1_copy[size-1][typeIndex] = --household_size_by_types_1_copy[size-1][typeIndex]; 
			
			generated_household_size_by_types_1[size-1][typeIndex] = ++generated_household_size_by_types_1[size-1][typeIndex];
		}
		
//		System.out.println("household_size_by_types_1_copy AFTER");
//		for (int rowIndex = 0; rowIndex < CanThoData.Scenario1.household_size_by_types_1.length; rowIndex++) {
//			for (int colIndex = 0; colIndex < CanThoData.Scenario1.household_size_by_types_1[rowIndex].length; colIndex++) {
//				System.out.print(household_size_by_types_1_copy[rowIndex][colIndex] + ", ");
//			}
//			System.out.println();
//		}
		
		
		for (int rowIndex = 0; rowIndex < generated_household_size_by_types_1.length; rowIndex++) {
			for (int colIndex = 0; colIndex < generated_household_size_by_types_1[rowIndex].length; colIndex++) {
				System.out.print(generated_household_size_by_types_1[rowIndex][colIndex] + ", ");
			}
			System.out.println();
		}
		 

		int sumZero = 0;
		for (int rowIndex = 0; rowIndex < household_size_by_types_1_copy.length; rowIndex++) {
			sumZero += household_size_by_types_1_copy[rowIndex][1]; 
			sumZero += household_size_by_types_1_copy[rowIndex][2]; 
			sumZero += household_size_by_types_1_copy[rowIndex][3]; 
			sumZero += household_size_by_types_1_copy[rowIndex][4]; 
			sumZero += household_size_by_types_1_copy[rowIndex][5]; 
		}
		assertTrue(sumZero == 0);
	}
	
	public void testScenario1NbOfGeneratedInhabitants10Percent() throws GenstarException {
//	@Test public void testScenario1NbOfGeneratedInhabitants10Percent() throws GenstarException {
		int[][] age_ranges_1_copy = new int[CanThoData.Scenario1.age_ranges_1.length][4];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario1.age_ranges_1.length; rowIndex++) {
			for (int colIndex = 0; colIndex < CanThoData.Scenario1.age_ranges_1[rowIndex].length; colIndex++) {
				age_ranges_1_copy[rowIndex][colIndex] = CanThoData.Scenario1.age_ranges_1[rowIndex][colIndex];
			}
		}
		
		int[][] generated_age_ranges1 = new int[CanThoData.Scenario1.age_ranges_1.length][4];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario1.age_ranges_1.length; rowIndex++) {
			generated_age_ranges1[rowIndex][0] = CanThoData.Scenario1.age_ranges_1[rowIndex][0];
			generated_age_ranges1[rowIndex][1] = CanThoData.Scenario1.age_ranges_1[rowIndex][1];
			generated_age_ranges1[rowIndex][2] = generated_age_ranges1[rowIndex][3] = 0;
		}
		
		
		
		ISyntheticPopulationGenerator scenario1InhabitantGenerator = data.getScenario1InhabitantGenerator();
		scenario1InhabitantGenerator.setPopulationName("Inhabitant population");
		int initialNbOfEntities = scenario1InhabitantGenerator.getNbOfEntities();
		int tenPercentNbOfEntities = initialNbOfEntities / 10; 
		scenario1InhabitantGenerator.setNbOfEntities(tenPercentNbOfEntities);
		
		IPopulation inhabitantPopulation = scenario1InhabitantGenerator.generate();
		for (Entity inhabitant : inhabitantPopulation.getEntities()) {
			int age = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity())).getIntValue();
			boolean isMale = ( (UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("sex").getAttributeValueOnEntity()) ).getBooleanValue();

			for (int rowIndex = 0; rowIndex < age_ranges_1_copy.length; rowIndex++) {
				if (age >= age_ranges_1_copy[rowIndex][0] && age <= age_ranges_1_copy[rowIndex][1]) {
					if (isMale) {
						age_ranges_1_copy[rowIndex][2] = --age_ranges_1_copy[rowIndex][2];
						generated_age_ranges1[rowIndex][2] = ++generated_age_ranges1[rowIndex][2];
					} else {
						age_ranges_1_copy[rowIndex][3] = --age_ranges_1_copy[rowIndex][3];
						generated_age_ranges1[rowIndex][3] = ++generated_age_ranges1[rowIndex][3];
					}
					
					break;
				}
			}
		}
				
		
		System.out.println("Ten percent number of inhabitants (Scenario 1)");
		for (int rowIndex = 0; rowIndex < generated_age_ranges1.length; rowIndex++) {
			for (int colIndex = 0; colIndex < generated_age_ranges1[rowIndex].length; colIndex++) {
				System.out.print(generated_age_ranges1[rowIndex][colIndex] + ", ");
			}
			System.out.println();
		}		
		
		// reset nbOfEntities
		scenario1InhabitantGenerator.setNbOfEntities(initialNbOfEntities);
	}
	
	public void testScenario1NbOfGeneratedInhabitants40Percent() throws GenstarException {
//	@Test public void testScenario1NbOfGeneratedInhabitants40Percent() throws GenstarException {
		int[][] age_ranges_1_copy = new int[CanThoData.Scenario1.age_ranges_1.length][4];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario1.age_ranges_1.length; rowIndex++) {
			for (int colIndex = 0; colIndex < CanThoData.Scenario1.age_ranges_1[rowIndex].length; colIndex++) {
				age_ranges_1_copy[rowIndex][colIndex] = CanThoData.Scenario1.age_ranges_1[rowIndex][colIndex];
			}
		}
		
		int[][] generated_age_ranges1 = new int[CanThoData.Scenario1.age_ranges_1.length][4];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario1.age_ranges_1.length; rowIndex++) {
			generated_age_ranges1[rowIndex][0] = CanThoData.Scenario1.age_ranges_1[rowIndex][0];
			generated_age_ranges1[rowIndex][1] = CanThoData.Scenario1.age_ranges_1[rowIndex][1];
			generated_age_ranges1[rowIndex][2] = generated_age_ranges1[rowIndex][3] = 0;
		}
		
		
		
		ISyntheticPopulationGenerator scenario1InhabitantGenerator = data.getScenario1InhabitantGenerator();
		scenario1InhabitantGenerator.setPopulationName("Inhabitant population");
		int initialNbOfEntities = scenario1InhabitantGenerator.getNbOfEntities();
		int fortyPercentNbOfEntities = 475374; // (initialNbOfEntities * 4) / 10; 
		scenario1InhabitantGenerator.setNbOfEntities(fortyPercentNbOfEntities);
		
		IPopulation inhabitantPopulation = scenario1InhabitantGenerator.generate();
		for (Entity inhabitant : inhabitantPopulation.getEntities()) {
			int age = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity())).getIntValue();
			boolean isMale = ( (UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("sex").getAttributeValueOnEntity()) ).getBooleanValue();

			for (int rowIndex = 0; rowIndex < age_ranges_1_copy.length; rowIndex++) {
				if (age >= age_ranges_1_copy[rowIndex][0] && age <= age_ranges_1_copy[rowIndex][1]) {
					if (isMale) {
						age_ranges_1_copy[rowIndex][2] = --age_ranges_1_copy[rowIndex][2];
						generated_age_ranges1[rowIndex][2] = ++generated_age_ranges1[rowIndex][2];
					} else {
						age_ranges_1_copy[rowIndex][3] = --age_ranges_1_copy[rowIndex][3];
						generated_age_ranges1[rowIndex][3] = ++generated_age_ranges1[rowIndex][3];
					}
					
					break;
				}
			}
		}
				
		
		System.out.println("Forty percent number of inhabitants (Scenario 1)");
		for (int rowIndex = 0; rowIndex < generated_age_ranges1.length; rowIndex++) {
			for (int colIndex = 0; colIndex < generated_age_ranges1[rowIndex].length; colIndex++) {
				System.out.print(generated_age_ranges1[rowIndex][colIndex] + ", ");
			}
			System.out.println();
		}		
		
		// reset nbOfEntities
		scenario1InhabitantGenerator.setNbOfEntities(initialNbOfEntities);		
		
	}

//	@Test public void testScenario1NbOfGeneratedInhabitants70Percent() throws GenstarException {
	public void testScenario1NbOfGeneratedInhabitants70Percent() throws GenstarException {
		int[][] age_ranges_1_copy = new int[CanThoData.Scenario1.age_ranges_1.length][4];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario1.age_ranges_1.length; rowIndex++) {
			for (int colIndex = 0; colIndex < CanThoData.Scenario1.age_ranges_1[rowIndex].length; colIndex++) {
				age_ranges_1_copy[rowIndex][colIndex] = CanThoData.Scenario1.age_ranges_1[rowIndex][colIndex];
			}
		}
		
		int[][] generated_age_ranges1 = new int[CanThoData.Scenario1.age_ranges_1.length][4];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario1.age_ranges_1.length; rowIndex++) {
			generated_age_ranges1[rowIndex][0] = CanThoData.Scenario1.age_ranges_1[rowIndex][0];
			generated_age_ranges1[rowIndex][1] = CanThoData.Scenario1.age_ranges_1[rowIndex][1];
			generated_age_ranges1[rowIndex][2] = generated_age_ranges1[rowIndex][3] = 0;
		}
		
		
		
		ISyntheticPopulationGenerator scenario1InhabitantGenerator = data.getScenario1InhabitantGenerator();
		scenario1InhabitantGenerator.setPopulationName("Inhabitant population");
		int initialNbOfEntities = scenario1InhabitantGenerator.getNbOfEntities();
		int seventyPercentNbOfEntities = 831904; // (initialNbOfEntities * 7) / 10; 
		scenario1InhabitantGenerator.setNbOfEntities(seventyPercentNbOfEntities);
		
		IPopulation inhabitantPopulation = scenario1InhabitantGenerator.generate();
		for (Entity inhabitant : inhabitantPopulation.getEntities()) {
			int age = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity())).getIntValue();
			boolean isMale = ( (UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("sex").getAttributeValueOnEntity()) ).getBooleanValue();

			for (int rowIndex = 0; rowIndex < age_ranges_1_copy.length; rowIndex++) {
				if (age >= age_ranges_1_copy[rowIndex][0] && age <= age_ranges_1_copy[rowIndex][1]) {
					if (isMale) {
						age_ranges_1_copy[rowIndex][2] = --age_ranges_1_copy[rowIndex][2];
						generated_age_ranges1[rowIndex][2] = ++generated_age_ranges1[rowIndex][2];
					} else {
						age_ranges_1_copy[rowIndex][3] = --age_ranges_1_copy[rowIndex][3];
						generated_age_ranges1[rowIndex][3] = ++generated_age_ranges1[rowIndex][3];
					}
					
					break;
				}
			}
		}
				
		
		System.out.println("Seventy percent number of inhabitants (Scenario 1)");
		for (int rowIndex = 0; rowIndex < generated_age_ranges1.length; rowIndex++) {
			for (int colIndex = 0; colIndex < generated_age_ranges1[rowIndex].length; colIndex++) {
				System.out.print(generated_age_ranges1[rowIndex][colIndex] + ", ");
			}
			System.out.println();
		}		
		
		// reset nbOfEntities
		scenario1InhabitantGenerator.setNbOfEntities(initialNbOfEntities);		
	}

	
//	@Test public void testScenario2NbOfGeneratedInhabitants() throws GenstarException {
	public void testScenario2NbOfGeneratedInhabitants() throws GenstarException {
		/*
		// { min age, max age, male urban individuals, female urban individuals, male rural individuals, female rural individuals }
		public static final int[][] age_ranges_2 = {
			{  0,  0,  6062,  5577,  3292,  3213 },
			{  1,  4, 23231, 21799, 13182, 12363 },
			{  5,  9, 27312, 25612, 16800, 15935 },
			{ 10, 14, 25612, 24373, 17591, 16202 },
			{ 15, 17, 19099, 17958, 12542, 11569 },
			{ 18, 19, 19302, 19806,  8848,  7902 },
			{ 20, 24, 44930, 44882, 21350, 18300 },
			{ 25, 29, 40021, 39683, 21051, 18641 },
			{ 30, 34, 35039, 34207, 18130, 17064 },
			{ 35, 39, 33137, 32504, 17310, 16025 },
			{ 40, 44, 29722, 30583, 15066, 14184 },
			{ 45, 49, 24346, 26696, 11132, 12186 },
			{ 50, 54, 18250, 23134,  8414, 10735 },
			{ 55, 59, 12860, 16487,  6820,  8184 },
			{ 60, 64,  7360,  8903,  4119,  4361 },
			{ 65, 69,  5050,  7629,  2535,  3749 },
			{ 70, 74,  4251,  6916,  2329,  3558 },
			{ 75, 79,  4462,  5881,  2487,  2910 },
			{ 80, 84,  2771,  3615,  1534,  1628 },
			{ 85, 99,  1465,  2595,   792,  1280 }
		};
		 */
		
		int[][] age_ranges_2_copy = new int[CanThoData.Scenario2.age_ranges_2.length][7];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario2.age_ranges_2.length; rowIndex++) {
			for (int colIndex = 0; colIndex < CanThoData.Scenario2.age_ranges_2[rowIndex].length; colIndex++) {
				age_ranges_2_copy[rowIndex][colIndex] = CanThoData.Scenario2.age_ranges_2[rowIndex][colIndex];
			}
		}
		
		int[][] generated_age_ranges_2 = new int[CanThoData.Scenario2.age_ranges_2.length][6];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario2.age_ranges_2.length; rowIndex++) {
			generated_age_ranges_2[rowIndex][0] =  CanThoData.Scenario2.age_ranges_2[rowIndex][0];
			generated_age_ranges_2[rowIndex][1] =  CanThoData.Scenario2.age_ranges_2[rowIndex][1];
			generated_age_ranges_2[rowIndex][2] = generated_age_ranges_2[rowIndex][3] 
				= generated_age_ranges_2[rowIndex][4] = generated_age_ranges_2[rowIndex][5] = 0;
		}
		
		
		ISyntheticPopulationGenerator scenario2InhabitantGenerator = data.getScenario2InhabitantGenerator();
		scenario2InhabitantGenerator.setPopulationName("Inhabitant population");
		
		IPopulation inhabitantPopulation = scenario2InhabitantGenerator.generate();
		for (Entity inhabitant : inhabitantPopulation.getEntities()) {
			int age = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity())).getIntValue();
			boolean isMale = ( (UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("sex").getAttributeValueOnEntity()) ).getBooleanValue();
			String livingPlace = ( (UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("living_place").getAttributeValueOnEntity()) ).getStringValue();

			for (int rowIndex = 0; rowIndex < age_ranges_2_copy.length; rowIndex++) {
				if (age >= age_ranges_2_copy[rowIndex][0] && age <= age_ranges_2_copy[rowIndex][1]) {
					if (isMale) {
						if (livingPlace.equals("urban")) {
							age_ranges_2_copy[rowIndex][2] = --age_ranges_2_copy[rowIndex][2]; 
							generated_age_ranges_2[rowIndex][2] = ++generated_age_ranges_2[rowIndex][2];
						} else { // rural
							age_ranges_2_copy[rowIndex][4] = --age_ranges_2_copy[rowIndex][4]; 
							generated_age_ranges_2[rowIndex][4] = ++generated_age_ranges_2[rowIndex][4];
							
						}
					} else {
						if (livingPlace.equals("urban")) {
							age_ranges_2_copy[rowIndex][3] = --age_ranges_2_copy[rowIndex][3]; 
							generated_age_ranges_2[rowIndex][3] = ++generated_age_ranges_2[rowIndex][3];
						} else { // rural
							age_ranges_2_copy[rowIndex][5] = --age_ranges_2_copy[rowIndex][5]; 
							generated_age_ranges_2[rowIndex][5] = ++generated_age_ranges_2[rowIndex][5];
						}
					}
					
					break;
				}
			}
		}
		
		int sumZero = 0;
		for (int rowIndex = 0; rowIndex < age_ranges_2_copy.length; rowIndex++) {
			sumZero += age_ranges_2_copy[rowIndex][2];
			sumZero += age_ranges_2_copy[rowIndex][3];
			sumZero += age_ranges_2_copy[rowIndex][4];
			sumZero += age_ranges_2_copy[rowIndex][5];
		}
		assertTrue(sumZero == 0);
		
		
		for (int rowIndex = 0; rowIndex < generated_age_ranges_2.length; rowIndex++) {
			for (int colIndex = 0; colIndex < generated_age_ranges_2[rowIndex].length; colIndex++) {
				System.out.print(generated_age_ranges_2[rowIndex][colIndex] + ", ");
			}
			System.out.println();
		}
		
		
	}
	
	public void testScenario2NbOfGeneratedHouseholds() throws GenstarException {
//	@Test public void testScenario2NbOfGeneratedHouseholds() throws GenstarException {
		/*
		// { size, urban-permanent, urban-semi-permanent, urban-less-permanent, urban-simple, urban-not-stated, 
		 	rural-permanent, rural-semi-permanent, rural-less-permanent, rural-simple, rural-not-stated }
			
			{ 1,  696,  8751, 1150, 1420,  4,  124,  1380,  651,  1399, 1 },
			{ 2, 1357, 17798, 2128, 2150, 10,  367,  3392, 1505,  2533, 1 },
			{ 3, 2482, 30598, 4812, 5136, 15,  671,  6995, 4130,  6910, 0 },
			{ 4, 3320, 35442, 6599, 6568,  7, 1164, 10932, 6548, 10211, 2 },
			{ 5, 2234, 18642, 3418, 3043,  6, 1149,  7408, 3608,  5064, 2 },
			{ 6, 1706, 12903, 1935, 1535,  2,  983,  5029, 2228,  2521, 2 },
			{ 7,  703,  4628,  775,  549,  3,  424,  1850,  748,   831, 2 },
			{ 8,  413,  2865,  426,  328,  0,  235,   984,  386,   459, 0 },
			{ 9,  445,  3546,  465,  331,  0,  196,   878,  397,   432, 0 },
		 */
		
		int[][] household_size_by_types_2_copy = new int[CanThoData.Scenario2.household_size_by_types_2.length][11];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario2.household_size_by_types_2.length; rowIndex++) {
			for (int colIndex = 0; colIndex < CanThoData.Scenario2.household_size_by_types_2[rowIndex].length; colIndex++) {
				household_size_by_types_2_copy[rowIndex][colIndex] = CanThoData.Scenario2.household_size_by_types_2[rowIndex][colIndex];
			}
		}
		
		
		int[][] generated_household_size_by_types_2 = new int[CanThoData.Scenario2.household_size_by_types_2.length][11];
		for (int rowIndex = 0; rowIndex < CanThoData.Scenario2.household_size_by_types_2.length; rowIndex++) {
			generated_household_size_by_types_2[rowIndex][0] =  CanThoData.Scenario2.household_size_by_types_2[rowIndex][0];
			
			generated_household_size_by_types_2[rowIndex][1] = generated_household_size_by_types_2[rowIndex][2] 
				= generated_household_size_by_types_2[rowIndex][3] = generated_household_size_by_types_2[rowIndex][4] 
				= generated_household_size_by_types_2[rowIndex][5] = generated_household_size_by_types_2[rowIndex][6] 
				= generated_household_size_by_types_2[rowIndex][7] = generated_household_size_by_types_2[rowIndex][8] 
				= generated_household_size_by_types_2[rowIndex][9] = generated_household_size_by_types_2[rowIndex][10] = 0;
		}
		

		
		ISyntheticPopulationGenerator scenario2HouseholdGenerator = data.getScenario2HouseholdGenerator();
		scenario2HouseholdGenerator.setPopulationName("Household population");
		

		Map<String, Integer> householdTypes = new HashMap<String, Integer>();
		for (int i=0; i<CanThoData.Scenario2.household_type2_values.length; i++) { householdTypes.put(CanThoData.Scenario2.household_type2_values[i], i + 1); }
		
		
		IPopulation householdPopulation = scenario2HouseholdGenerator.generate();
		for (Entity household : householdPopulation.getEntities()) {
			int size = ((UniqueValue) household.getEntityAttributeValueByNameOnData("size").getAttributeValueOnEntity()).getIntValue();
			
			String livingPlace = ( (UniqueValue) household.getEntityAttributeValueByNameOnData("living_place").getAttributeValueOnEntity() ).getStringValue();

			String type = ( (UniqueValue) household.getEntityAttributeValueByNameOnData("type").getAttributeValueOnEntity()).getStringValue();
			int typeIndex = householdTypes.get(type);
			if (livingPlace.equals("rural")) { typeIndex += 5; }
			
			household_size_by_types_2_copy[size-1][typeIndex] = --household_size_by_types_2_copy[size-1][typeIndex]; 
			
			generated_household_size_by_types_2[size-1][typeIndex] = ++generated_household_size_by_types_2[size-1][typeIndex]; 
		}
		
		int sumZero = 0;
		for (int rowIndex = 0; rowIndex < household_size_by_types_2_copy.length; rowIndex++) {
			sumZero += household_size_by_types_2_copy[rowIndex][1]; 
			sumZero += household_size_by_types_2_copy[rowIndex][2]; 
			sumZero += household_size_by_types_2_copy[rowIndex][3]; 
			sumZero += household_size_by_types_2_copy[rowIndex][4]; 
			sumZero += household_size_by_types_2_copy[rowIndex][5]; 
			sumZero += household_size_by_types_2_copy[rowIndex][6]; 
			sumZero += household_size_by_types_2_copy[rowIndex][7]; 
			sumZero += household_size_by_types_2_copy[rowIndex][8]; 
			sumZero += household_size_by_types_2_copy[rowIndex][9]; 
			sumZero += household_size_by_types_2_copy[rowIndex][10]; 
		}
		

		assertTrue(sumZero == 0);
		
		
		for (int rowIndex = 0; rowIndex < generated_household_size_by_types_2.length; rowIndex++) {
			for (int colIndex = 0; colIndex < generated_household_size_by_types_2[rowIndex].length; colIndex++) {
				System.out.print(generated_household_size_by_types_2[rowIndex][colIndex] + ", ");
			}
			System.out.println();
		}
	}

	public void testScenario1Generator() throws GenstarException {
//	@Test public void testScenario1Generator() throws GenstarException {
		
		/*
		public static final int[][] household_size_by_types_1 = {
			{ 1,  820, 10131,  1801,  2819,  5 }, // { size, permanent, semi-permanent, less-permanent, simple, not-stated }
			{ 2, 1724, 21190,  3633,  4683, 11 },
			{ 3, 3153, 37593,  8942, 12046, 15 },
			{ 4, 4466, 46374, 13147, 16779,  9 },
			{ 5, 3383, 26050,  7026,  8107,  8 },
			{ 6, 2689, 17932,  4163,  4056,  4 },
			{ 7, 1127,  6478,  1523,  1380,  4 },
			{ 8,  648,  3849,   812,   787,  0 },
			{ 9,  641,  4424,   862,   763,  0 },
		};
		 */
		
		int[][] before_linking_households = new int[9][6];
		int[][] after_linking_households = new int[9][6];
		for (int rowIndex=0; rowIndex<before_linking_households.length; rowIndex++) {
			before_linking_households[rowIndex][0] = after_linking_households[rowIndex][0] = rowIndex + 1;
			before_linking_households[rowIndex][1] = after_linking_households[rowIndex][1] 
					= before_linking_households[rowIndex][2] = after_linking_households[rowIndex][2] 
					= before_linking_households[rowIndex][3] = after_linking_households[rowIndex][3] 
					= before_linking_households[rowIndex][4] = after_linking_households[rowIndex][4] 
					= before_linking_households[rowIndex][5] = after_linking_households[rowIndex][5] = 0;
		}
		
		
		ISyntheticPopulationGenerator inhabitantPopGenerator = data.getScenario1InhabitantGenerator();
		inhabitantPopGenerator.setPopulationName("Scenario1's inhabitant population");
		
		ISyntheticPopulationGenerator householdPopGenerator = data.getScenario1HouseholdGenerator();
		householdPopGenerator.setPopulationName("Scenario1's household population");
		
		GenstarGenerator generator = new GenstarGenerator();
		generator.addPopulationGenerator(inhabitantPopGenerator);
		generator.addPopulationGenerator(householdPopGenerator);
		
		Scenario1PopLinker linker = linkers.getScenario1PopLinker();
		generator.setPopulationsLinker(linker);
		
		long start = System.currentTimeMillis();
		generator.run();
		long end = System.currentTimeMillis();
		System.out.println("Generation duration : " + ( (end - start) / 1000 ) + " seconds");
		 
		
		List<Entity> pickedInhabitants = new ArrayList<Entity>(linker.getPickedInhabitants());
		IPopulation inhabitantPopulation = null, householdPopulation = null;
		List<IPopulation> concernedPopulations = linker.getPopulations();
		if (concernedPopulations.get(0).getName().equals("Scenario1's inhabitant population")) {
			inhabitantPopulation = concernedPopulations.get(0);
			householdPopulation = concernedPopulations.get(1);
		} else {
			inhabitantPopulation = concernedPopulations.get(1);
			householdPopulation = concernedPopulations.get(0);
		}
		
		/*
		public static final String[] household_type1_values = { "permanent", "semi-permanent", "less-permanent", "simple", "not-stated" };
		 */
		Map<String, Integer> householdTypes = new HashMap<String, Integer>();
		for (int i=0; i<CanThoData.Scenario1.household_type1_values.length; i++) { householdTypes.put(CanThoData.Scenario1.household_type1_values[i], i + 1); }

		for (Entity pickedHousehold : linker.getPickedHouseholds()) {
			int size = ((UniqueValue) pickedHousehold.getEntityAttributeValueByNameOnData("size").getAttributeValueOnEntity()).getIntValue();
			assertTrue(pickedHousehold.getComponentPopulation("Scenario1's inhabitant population").getEntities().size() == size);
			
			
			String type = ( (UniqueValue) pickedHousehold.getEntityAttributeValueByNameOnData("type").getAttributeValueOnEntity()).getStringValue();
			int typeIndex = householdTypes.get(type);
			
			after_linking_households[size-1][typeIndex] = ++after_linking_households[size-1][typeIndex];
			before_linking_households[size-1][typeIndex] = ++before_linking_households[size-1][typeIndex];
		}
		
		for (Entity unpickedHousehold : householdPopulation.getEntities()) {
			assertTrue(unpickedHousehold.getComponentPopulation("Scenario1's inhabitant population") == null);
			
			int size = ((UniqueValue) unpickedHousehold.getEntityAttributeValueByNameOnData("size").getAttributeValueOnEntity()).getIntValue();
			String type = ( (UniqueValue) unpickedHousehold.getEntityAttributeValueByNameOnData("type").getAttributeValueOnEntity()).getStringValue();
			int typeIndex = householdTypes.get(type);
			
			before_linking_households[size-1][typeIndex] = ++before_linking_households[size-1][typeIndex];
		}
		

		System.out.println("Before linking households");
		for (int colIndex=0; colIndex<before_linking_households.length; colIndex++) {
			for (int rowIndex=0; rowIndex<before_linking_households[colIndex].length; rowIndex++) {
				System.out.print(before_linking_households[colIndex][rowIndex] + ", ");
			}
			System.out.println();
		}
		System.out.println("---");
		
		System.out.println("After linking households");
		for (int colIndex=0; colIndex<after_linking_households.length; colIndex++) {
			for (int rowIndex=0; rowIndex<after_linking_households[colIndex].length; rowIndex++) {
				System.out.print(after_linking_households[colIndex][rowIndex] + ", ");
			}
			System.out.println();
		}
		
		
		assertTrue(pickedInhabitants.size() > 0);
//		assertTrue(avaialableInhabitants.size() + pickedInhabitants.size() == inhabitantPopulation.getInitialNbOfEntities());
		

//		System.out.println("pickedInhabitants = " + pickedInhabitants.size() + ", unpickedInhabitants = " + unpickedInhabitants.size());
	}
	
	
	
//	@Test public void testScenario2Generator() throws GenstarException {
	public void testScenario2Generator() throws GenstarException {
		
		ISyntheticPopulationGenerator inhabitantPopGenerator = data.getScenario2InhabitantGenerator();
		inhabitantPopGenerator.setPopulationName("Scenario2's inhabitant population");
		
		ISyntheticPopulationGenerator householdPopGenerator = data.getScenario2HouseholdGenerator();
		householdPopGenerator.setPopulationName("Scenario2's household population");
		
		GenstarGenerator generator = new GenstarGenerator();
		generator.addPopulationGenerator(inhabitantPopGenerator);
		generator.addPopulationGenerator(householdPopGenerator);
		
		Scenario2PopLinker linker = linkers.getScenario2PopLinker();
		generator.setPopulationsLinker(linker);
		
		long start = System.currentTimeMillis();
		generator.run();
		long end = System.currentTimeMillis();
		System.out.println("Generation duration : " + ( (end - start) / 1000 ) + " seconds");

		
		IPopulation inhabitantPopulation = null, householdPopulation = null;
		List<IPopulation> concernedPopulations = linker.getPopulations();
		if (concernedPopulations.get(0).getName().equals("Scenario2's inhabitant population")) {
			inhabitantPopulation = concernedPopulations.get(0);
			householdPopulation = concernedPopulations.get(1);
		} else {
			inhabitantPopulation = concernedPopulations.get(1);
			householdPopulation = concernedPopulations.get(0);
		}
		
		for (Entity pickedHousehold : linker.getPickedHouseholds()) {
			int size = ((UniqueValue) pickedHousehold.getEntityAttributeValueByNameOnData("size").getAttributeValueOnData()).getIntValue();
			assertTrue(pickedHousehold.getComponentPopulation("Scenario2's inhabitant population").getEntities().size() == size);
		}
		
		for (Entity unpickedHousehold : householdPopulation.getEntities()) {
			assertTrue(unpickedHousehold.getComponentPopulation("Scenario2's inhabitant population") == null);
		}
		
		assertTrue(linker.getPickedInhabitants().size() > 0);
//		assertTrue(inhabitantPopulation.getEntities().size() + linker.getPickedInhabitants().size() == inhabitantPopulation.getInitialNbOfEntities());
	}
	
//	@Test public void testScenario3NbOfGeneratedInhabitants() throws GenstarException {
	public void testScenario3NNbOfGeneratedInhabitants() throws GenstarException {
		
		int times = 29;
		for (int currentIteration=0; currentIteration<times; currentIteration++) {
			int[][] age_ranges_3_copy = new int[CanThoData.Scenario3.age_ranges_3.length][4];
			for (int rowIndex = 0; rowIndex < CanThoData.Scenario3.age_ranges_3.length; rowIndex++) {
				for (int colIndex = 0; colIndex < CanThoData.Scenario3.age_ranges_3[rowIndex].length; colIndex++) {
					age_ranges_3_copy[rowIndex][colIndex] = CanThoData.Scenario3.age_ranges_3[rowIndex][colIndex];
				}
			}
			
			int[][] generated_age_ranges3 = new int[CanThoData.Scenario3.age_ranges_3.length][4];
			for (int rowIndex = 0; rowIndex < CanThoData.Scenario3.age_ranges_3.length; rowIndex++) {
				generated_age_ranges3[rowIndex][0] = CanThoData.Scenario3.age_ranges_3[rowIndex][0];
				generated_age_ranges3[rowIndex][1] = CanThoData.Scenario3.age_ranges_3[rowIndex][1];
				generated_age_ranges3[rowIndex][2] = generated_age_ranges3[rowIndex][3] = 0;
			}
			
			int[][] generated_districts_3 = new int[CanThoData.Scenario3.locations_3.length][2];
			for (int rowIndex = 0; rowIndex < generated_districts_3.length; rowIndex++) {
				generated_districts_3[rowIndex][0] = generated_districts_3[rowIndex][1] = 0; 
			}
			
			
			ISyntheticPopulationGenerator scenario3InhabitantGenerator = data.getScenario3InhabitantGenerator();
			scenario3InhabitantGenerator.setPopulationName("Inhabitant population");
			
			IPopulation inhabitantPopulation = scenario3InhabitantGenerator.generate();
			for (Entity inhabitant : inhabitantPopulation.getEntities()) {
				int age = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity())).getIntValue();
				boolean isMale = ( (UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("sex").getAttributeValueOnEntity()) ).getBooleanValue();

				for (int rowIndex = 0; rowIndex < age_ranges_3_copy.length; rowIndex++) {
					if (age >= age_ranges_3_copy[rowIndex][0] && age <= age_ranges_3_copy[rowIndex][1]) {
						if (isMale) {
							age_ranges_3_copy[rowIndex][2] = --age_ranges_3_copy[rowIndex][2];
							generated_age_ranges3[rowIndex][2] = ++generated_age_ranges3[rowIndex][2];
						} else {
							age_ranges_3_copy[rowIndex][3] = --age_ranges_3_copy[rowIndex][3];
							generated_age_ranges3[rowIndex][3] = ++generated_age_ranges3[rowIndex][3];
						}
						
						break;
					}
				}
				
				// district
				String district = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("district").getAttributeValueOnEntity())).getStringValue();
				int districtIndex = CanThoData.Scenario3.getDistrictIndex(district);
				if (isMale) {
					generated_districts_3[districtIndex][0] = ++generated_districts_3[districtIndex][0];
				} else {
					generated_districts_3[districtIndex][1] = ++generated_districts_3[districtIndex][1];
				}
			}
			
			int sumZero = 0;
			for (int rowIndex = 0; rowIndex < age_ranges_3_copy.length; rowIndex++) {
				sumZero += age_ranges_3_copy[rowIndex][2];
				sumZero += age_ranges_3_copy[rowIndex][3];
			}
			assertTrue(sumZero == 0);
			
			
			System.out.println("generated_age_ranges3 at iteration : " + currentIteration);
			System.out.println("Age range, Male, Female");
			for (int rowIndex = 0; rowIndex < generated_age_ranges3.length; rowIndex++) {
				
				System.out.print("[" + generated_age_ranges3[rowIndex][0] + " : " + generated_age_ranges3[rowIndex][1] + "], "); // age range
				System.out.print(generated_age_ranges3[rowIndex][2] + ", "); // male
				System.out.print(generated_age_ranges3[rowIndex][3]); // female
				
				System.out.println();
			} 
			System.out.println("---");
			
			
			System.out.println("generated_districts_3 at iteration : " + currentIteration);
			System.out.println("District, Male, Female");
			for (int rowIndex = 0; rowIndex < generated_districts_3.length; rowIndex++) {
				System.out.print(CanThoData.Scenario3.district_names[rowIndex] + ", "); // district
				System.out.print(generated_districts_3[rowIndex][0] + ", "); // male
				System.out.print(generated_districts_3[rowIndex][1]); // female
				
				System.out.println();
			}			
		}
		

		/*
		generated_districts_3
		116839, 127815, 
		65218, 64783, 
		53923, 56906, 
		42678, 43768, 
		79851, 79617, 
		57015, 55442, 
		62670, 61309, 
		61919, 59442, 
		49623, 49617, 
		 */
	}
	
//	@Test public void testScenario4NbOfGeneratedInhabitants() throws GenstarException {
	public void testScenario4NbOfGeneratedInhabitants() throws GenstarException {
		
		int times = 30;
		for (int currentIteration=0; currentIteration<times; currentIteration++) {
			System.out.println("Started iteration : " + currentIteration);

			int[][] age_ranges_4_copy = new int[CanThoData.Scenario4.age_ranges_4.length][4];
			for (int rowIndex = 0; rowIndex < CanThoData.Scenario4.age_ranges_4.length; rowIndex++) {
				for (int colIndex = 0; colIndex < CanThoData.Scenario4.age_ranges_4[rowIndex].length; colIndex++) {
					age_ranges_4_copy[rowIndex][colIndex] = CanThoData.Scenario4.age_ranges_4[rowIndex][colIndex];
				}
			}
			
			int[][] generated_age_ranges4 = new int[CanThoData.Scenario4.age_ranges_4.length][4];
			for (int rowIndex = 0; rowIndex < CanThoData.Scenario4.age_ranges_4.length; rowIndex++) {
				generated_age_ranges4[rowIndex][0] = CanThoData.Scenario4.age_ranges_4[rowIndex][0];
				generated_age_ranges4[rowIndex][1] = CanThoData.Scenario4.age_ranges_4[rowIndex][1];
				generated_age_ranges4[rowIndex][2] = generated_age_ranges4[rowIndex][3] = 0;
			}
			
			int[][] generated_districts_4 = new int[CanThoData.Scenario4.locations_4.length][2];
			for (int rowIndex = 0; rowIndex < generated_districts_4.length; rowIndex++) {
				generated_districts_4[rowIndex][0] = generated_districts_4[rowIndex][1] = 0; 
			}
			
			
			ISyntheticPopulationGenerator scenario4InhabitantGenerator = data.getScenario4InhabitantGenerator();
			scenario4InhabitantGenerator.setPopulationName("Inhabitant population");
			
			IPopulation inhabitantPopulation = scenario4InhabitantGenerator.generate();
			for (Entity inhabitant : inhabitantPopulation.getEntities()) {
				int age = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity())).getIntValue();
				boolean isMale = ( (UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("sex").getAttributeValueOnEntity()) ).getBooleanValue();

				for (int rowIndex = 0; rowIndex < age_ranges_4_copy.length; rowIndex++) {
					if (age >= age_ranges_4_copy[rowIndex][0] && age <= age_ranges_4_copy[rowIndex][1]) {
						if (isMale) {
							age_ranges_4_copy[rowIndex][2] = --age_ranges_4_copy[rowIndex][2];
							generated_age_ranges4[rowIndex][2] = ++generated_age_ranges4[rowIndex][2];
						} else {
							age_ranges_4_copy[rowIndex][3] = --age_ranges_4_copy[rowIndex][3];
							generated_age_ranges4[rowIndex][3] = ++generated_age_ranges4[rowIndex][3];
						}
						
						break;
					}
				}
				
				// district
				String district = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("district").getAttributeValueOnEntity())).getStringValue();
				int districtIndex = CanThoData.Scenario4.getDistrictIndex(district);
				if (isMale) {
					generated_districts_4[districtIndex][0] = ++generated_districts_4[districtIndex][0];
				} else {
					generated_districts_4[districtIndex][1] = ++generated_districts_4[districtIndex][1];
				}
			}
			
			int sumZero = 0;
			for (int rowIndex = 0; rowIndex < age_ranges_4_copy.length; rowIndex++) {
				sumZero += age_ranges_4_copy[rowIndex][2];
				sumZero += age_ranges_4_copy[rowIndex][3];
			}
			assertTrue(sumZero == 0);
			
			
			
			List<String[]> lines1 = new ArrayList<String[]>();
			String[] header1 = new String[3];
			header1[0] = "Age range";
			header1[1] = "Male";
			header1[2] = "Female";
			lines1.add(header1);
			
			String[] line;
			for (int rowIndex = 0; rowIndex < generated_age_ranges4.length; rowIndex++) {
				line = new String[3];
				
				line[0] = new String("[" + generated_age_ranges4[rowIndex][0] + " : " + generated_age_ranges4[rowIndex][1] + "]");  // age range
				line[1] = new String(Integer.toString(generated_age_ranges4[rowIndex][2])); // male
				line[2] = new String(Integer.toString(generated_age_ranges4[rowIndex][3])); // female
	
				lines1.add(line);
			} 
			GenstarFileUtils.writeCSVFile("Scenario1_test" + Integer.toString(currentIteration) + "_age_sex.csv", lines1);
			 
			
			
			List<String[]> lines2 = new ArrayList<String[]>();
			String[] header2 = new String[3];
			header2[0] = "District";
			header2[1] = "Male";
			header2[2] = "Female";
			lines2.add(header2);
			
			for (int rowIndex = 0; rowIndex < generated_districts_4.length; rowIndex++) {
				line = new String[3];
				
				line[0] = new String(CanThoData.Scenario4.district_names[rowIndex]);  // district
				line[1] = new String(Integer.toString(generated_districts_4[rowIndex][0])); // male
				line[2] = new String(Integer.toString(generated_districts_4[rowIndex][1])); // female
	
				lines2.add(line);
			} 
			GenstarFileUtils.writeCSVFile("Scenario1_test" + Integer.toString(currentIteration) + "_district_sex.csv", lines2);
			 
			
			System.out.println("Finished iteration : " + currentIteration);
			
		}		 
	}
	
	@Test public void testScenario5NbOfGeneratedInhabitants() throws GenstarException {
		System.out.println("Scenario 5");
		
		int times = 30;
		for (int currentIteration=0; currentIteration<times; currentIteration++) {
			System.out.println("Started iteration : " + currentIteration);

			int[][] generated_districts_5 = new int[CanThoData.Scenario5.locations_5.length][2];
			for (int rowIndex = 0; rowIndex < generated_districts_5.length; rowIndex++) {
				generated_districts_5[rowIndex][0] = generated_districts_5[rowIndex][1] = 0; 
			}

			int[][] age_ranges_5_copy = new int[CanThoData.Scenario5.age_ranges_5.length][4];
			for (int rowIndex = 0; rowIndex < CanThoData.Scenario5.age_ranges_5.length; rowIndex++) {
				for (int colIndex = 0; colIndex < CanThoData.Scenario5.age_ranges_5[rowIndex].length; colIndex++) {
					age_ranges_5_copy[rowIndex][colIndex] = CanThoData.Scenario5.age_ranges_5[rowIndex][colIndex];
				}
			}
			
			int[][] generated_age_ranges5 = new int[CanThoData.Scenario5.age_ranges_5.length][4];
			for (int rowIndex = 0; rowIndex < CanThoData.Scenario5.age_ranges_5.length; rowIndex++) {
				generated_age_ranges5[rowIndex][0] = CanThoData.Scenario5.age_ranges_5[rowIndex][0];
				generated_age_ranges5[rowIndex][1] = CanThoData.Scenario5.age_ranges_5[rowIndex][1];
				generated_age_ranges5[rowIndex][2] = generated_age_ranges5[rowIndex][3] = 0;
			}
			
			
			ISyntheticPopulationGenerator scenario5InhabitantGenerator = data.getScenario5InhabitantGenerator();
			scenario5InhabitantGenerator.setPopulationName("Inhabitant population (Scenario 5)");
			
			
			IPopulation inhabitantPopulation = scenario5InhabitantGenerator.generate();
			for (Entity inhabitant : inhabitantPopulation.getEntities()) {
				int age = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity())).getIntValue();
				boolean isMale = ( (UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("sex").getAttributeValueOnEntity()) ).getBooleanValue();

				for (int rowIndex = 0; rowIndex < age_ranges_5_copy.length; rowIndex++) {
					if (age >= age_ranges_5_copy[rowIndex][0] && age <= age_ranges_5_copy[rowIndex][1]) {
						if (isMale) {
							age_ranges_5_copy[rowIndex][2] = --age_ranges_5_copy[rowIndex][2];
							generated_age_ranges5[rowIndex][2] = ++generated_age_ranges5[rowIndex][2];
						} else {
							age_ranges_5_copy[rowIndex][3] = --age_ranges_5_copy[rowIndex][3];
							generated_age_ranges5[rowIndex][3] = ++generated_age_ranges5[rowIndex][3];
						}
						
						break;
					}
				}
				
				// district
				String district = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("district").getAttributeValueOnEntity())).getStringValue();
				int districtIndex = CanThoData.Scenario3.getDistrictIndex(district);
				if (isMale) {
					generated_districts_5[districtIndex][0] = ++generated_districts_5[districtIndex][0];
				} else {
					generated_districts_5[districtIndex][1] = ++generated_districts_5[districtIndex][1];
				}
			}
			
			int sumZero = 0;
			for (int rowIndex = 0; rowIndex < age_ranges_5_copy.length; rowIndex++) {
				sumZero += age_ranges_5_copy[rowIndex][2];
				sumZero += age_ranges_5_copy[rowIndex][3];
			}
			assertTrue(sumZero == 0);
			
			
			List<String[]> lines1 = new ArrayList<String[]>();
			String[] header1 = new String[3];
			header1[0] = "Age range";
			header1[1] = "Male";
			header1[2] = "Female";
			lines1.add(header1);
			
			String[] line;
			for (int rowIndex = 0; rowIndex < generated_age_ranges5.length; rowIndex++) {
				line = new String[3];
				
				line[0] = new String("[" + generated_age_ranges5[rowIndex][0] + " : " + generated_age_ranges5[rowIndex][1] + "]");  // age range
				line[1] = new String(Integer.toString(generated_age_ranges5[rowIndex][2])); // male
				line[2] = new String(Integer.toString(generated_age_ranges5[rowIndex][3])); // female
	
				lines1.add(line);
			} 
			GenstarFileUtils.writeCSVFile("Scenario2_test" + Integer.toString(currentIteration) + "_age_sex.csv", lines1);
			 
			
			
			List<String[]> lines2 = new ArrayList<String[]>();
			String[] header2 = new String[3];
			header2[0] = "District";
			header2[1] = "Male";
			header2[2] = "Female";
			lines2.add(header2);
			
			for (int rowIndex = 0; rowIndex < generated_districts_5.length; rowIndex++) {
				line = new String[3];
				
				line[0] = new String(CanThoData.Scenario5.district_names[rowIndex]);  // district
				line[1] = new String(Integer.toString(generated_districts_5[rowIndex][0])); // male
				line[2] = new String(Integer.toString(generated_districts_5[rowIndex][1])); // female
	
				lines2.add(line);
			} 
			GenstarFileUtils.writeCSVFile("Scenario2_test" + Integer.toString(currentIteration) + "_district_sex.csv", lines2);
			
			
			System.out.println("Finished iteration : " + currentIteration);
		}
		
	}
	
	
	public void testScenario3NNbOfGeneratedInhabitants10Percent() throws GenstarException {
		
	}
	
	public void testScenario3NNbOfGeneratedInhabitants40Percent() throws GenstarException {
		
	}
	
	public void testScenario3NNbOfGeneratedInhabitants70Percent() throws GenstarException {
		
	}
	
}