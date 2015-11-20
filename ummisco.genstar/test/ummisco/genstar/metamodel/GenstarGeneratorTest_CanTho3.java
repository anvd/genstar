package ummisco.genstar.metamodel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;
import ummisco.genstar.util.GenstarFileUtils;

public class GenstarGeneratorTest_CanTho3 {

	private static final boolean[] sexes = { true, false };
	private static final String[] living_place_values = { "urban", "rural" };

	private static final class Data {
		// "Inhabitant" population +
		// Rule 1 : Inhabitant population by sex_and_living_place and age group
		/**
		 * Type : Frequency distribution
		 * Attributes : 
		 * 	age : enumeration of integer ranges
		 *  sex : enumeration of boolean values  ( true : male, false : female )
		 *  living_place : enumeration of string values ( urban, rural )
		 *  
		 *  Input attributes :
		 *  	{}
		 *  
		 *  Output attributes :
		 *  	{ age, sex, living_place }
		 *  
		 *  Note:
		 *   an age_ranges element = { min age, max age, male urban individuals, female urban individuals, male rural individuals, female rural individuals }
		 */
		public static final int[][] age_ranges = { // Total: 1188435 inhabitants
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
		
		
		// Rule 2 : Inhabitant population by sex and location (i.e., district)
		/**
		 * Type : Frequency distribution
		 * Attributes :
		 * 	sex : enumeration of boolean values (true : male, false : female)
		 * 	district : enumeration of string values
		 * 
		 * Input attributes :
		 * 	{ sex }
		 * 
		 * Output attributes :
		 * 	{ district }
		 * 
		 * Note :
		 * 	a locations element = { male individuals, female individuals }
		 */
		static final int[][] locations = { // Total : 1188435
			{ 115564, 127381 },
			{  65232,  65042 },
			{  54314,  56992 },
			{  42617,  43711 },
			{  79932,  79529 },
			{  57153,  55735 },
			{  62996,  61269 },
			{  61870,  59451 },
			{  49928,  49739 },
		};
		
		static final String[] district_names = { "Ninh Kieu", "O Mon", "Binh Thuy", "Cai Rang", "Thot Not", "Vinh Thanh", "Co Do", "Thoi Lai", "Phong Dien" };
		
		static int getDistrictIndex(final String districtName) {
			for (int index=0; index<district_names.length; index++) {
				if (districtName.equals(district_names[index])) { return index;}
			}
			
			return -1;
		}		
		// "Inhabitant" population -

		
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
		public static final int[][] household_size_by_types = { // total households : 286058 -> Calculated Inhabitants : 1164908 ( < 1188435 )
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

	
	public static final String INHABITANT_GENERATOR_RULE1_NAME = "Inhabitant population by age, sex and living_place";

	public static final String INHABITANT_GENERATOR_RULE2_NAME = "Inhabitant population by sex and district/location";
	
	public static final String INHABITANT_POPULATION_NAME = "Inhabitant population";
	
	public static final String HOUSEHOLD_GENERATOR_RULE1_NAME = "Household population by size, type and living_place";
	
	public static final String HOUSEHOLD_POPULATION_NAME = "Household population";
	
	private IMultipleRulesGenerator inhabitantPopGenerator;
	private FrequencyDistributionGenerationRule inhabitantPopRule1, inhabitantPopRule2;
	
	private IMultipleRulesGenerator householdPopGenerator;
	private FrequencyDistributionGenerationRule householdPopRule1;
	
	// percentages of inhabitant population to generate
//	private float[] percentages = { 0.01f, 0.05f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1, 1.25f, 1.5f, 1.75f, 2 };
	private float[] percentages = { 1, 1.25f, 1.5f, 1.75f, 2 };
	
	private final float TOTAL_INHABITANTS = 1188435f;
	
	private final float TOTAL_HOUSEHOLDS = 286058f;
	
	private int[] nbOfInhabitantsToGenerate = new int[percentages.length];

	private int[] nbOfHouseholdsToGenerate = new int[percentages.length];
	

	public GenstarGeneratorTest_CanTho3() throws GenstarException {
		initializeGenerators();
		
		for (int i=0; i<percentages.length; i++) {
			nbOfInhabitantsToGenerate[i] = Math.round(TOTAL_INHABITANTS * percentages[i]);
			nbOfHouseholdsToGenerate[i] = Math.round(TOTAL_HOUSEHOLDS * percentages[i]);
		}
	}
	
	private void initializeGenerators() throws GenstarException {
		// inhabitantGenerator +
		
		
		inhabitantPopGenerator = new MultipleRulesGenerator("Inhabitant population generator", (int)TOTAL_INHABITANTS);
		inhabitantPopGenerator.setPopulationName(INHABITANT_POPULATION_NAME);
		
		// create attributes +
		RangeValuesAttribute ageRangesAttr = new RangeValuesAttribute(inhabitantPopGenerator, "age", DataType.INTEGER, UniqueValue.class);
		for (int[] range : Data.age_ranges) {
			ageRangesAttr.add(new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
		}
		inhabitantPopGenerator.addAttribute(ageRangesAttr);
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(inhabitantPopGenerator, "sex", DataType.BOOL);
		UniqueValue maleValue = new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0]));
		sexAttr.add(maleValue);
		UniqueValue femaleValue = new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1]));
		sexAttr.add(femaleValue);
		inhabitantPopGenerator.addAttribute(sexAttr);
		
		UniqueValuesAttribute inhabitantLivingPlaceAttr = new UniqueValuesAttribute(inhabitantPopGenerator, "living_place", DataType.STRING);
		UniqueValue urbanValue = new UniqueValue(DataType.STRING, living_place_values[0]);
		inhabitantLivingPlaceAttr.add(urbanValue);
		UniqueValue ruralValue = new UniqueValue(DataType.STRING, living_place_values[1]);
		inhabitantLivingPlaceAttr.add(ruralValue);
		inhabitantPopGenerator.addAttribute(inhabitantLivingPlaceAttr);
		
		UniqueValuesAttribute districtAttr = new UniqueValuesAttribute(inhabitantPopGenerator, "district", DataType.STRING);
		for (String district : Data.district_names) { districtAttr.add(new UniqueValue(DataType.STRING, district)); }
		inhabitantPopGenerator.addAttribute(districtAttr);
		// create attributes -

		
		// create generation rules +
		// inhabitantPopRule1
		inhabitantPopRule1 = new FrequencyDistributionGenerationRule(inhabitantPopGenerator, INHABITANT_GENERATOR_RULE1_NAME);
		inhabitantPopRule1.appendOutputAttribute(ageRangesAttr);
		inhabitantPopRule1.appendOutputAttribute(sexAttr);
		inhabitantPopRule1.appendOutputAttribute(inhabitantLivingPlaceAttr);
		inhabitantPopRule1.generateAttributeValuesFrequencies();
		inhabitantPopGenerator.appendGenerationRule(inhabitantPopRule1);
		
		//  an age_ranges element = { min age, max age, male urban individuals, female urban individuals, male rural individuals, female rural individuals }
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (int[] range : Data.age_ranges) {
			attributeValues.clear();
			
			attributeValues.put(ageRangesAttr, new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));

			attributeValues.put(inhabitantLivingPlaceAttr, urbanValue); // urban 
			attributeValues.put(sexAttr, maleValue); // male
			inhabitantPopRule1.setFrequency(attributeValues, range[2]); // urban male
			
			attributeValues.put(sexAttr, femaleValue); // female
			inhabitantPopRule1.setFrequency(attributeValues, range[3]); // urban female
			
			attributeValues.put(inhabitantLivingPlaceAttr, ruralValue); // rural 
			attributeValues.put(sexAttr, maleValue); // male
			inhabitantPopRule1.setFrequency(attributeValues, range[4]); // rural male
			
			attributeValues.put(sexAttr, femaleValue); // female
			inhabitantPopRule1.setFrequency(attributeValues, range[5]); // rural female
		}
		

		// inhabitantPopRule2
		inhabitantPopRule2 = new FrequencyDistributionGenerationRule(inhabitantPopGenerator, INHABITANT_GENERATOR_RULE2_NAME);
		inhabitantPopRule2.appendInputAttribute(sexAttr);
		inhabitantPopRule2.appendOutputAttribute(districtAttr);
		inhabitantPopRule2.generateAttributeValuesFrequencies();
		inhabitantPopGenerator.appendGenerationRule(inhabitantPopRule2);
		
		// set frequencies
		AttributeValue districtValue;
		int districtIndex = 0;
		for (int location[] : Data.locations) {
			attributeValues.clear();
			
			districtValue = new UniqueValue(DataType.STRING, Data.district_names[districtIndex]);
			attributeValues.put(districtAttr, districtValue);
			
			// male
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0])));
			inhabitantPopRule2.setFrequency(attributeValues, location[0]);
			
			// female
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1])));
			inhabitantPopRule2.setFrequency(attributeValues, location[1]);
		
			
			districtIndex++;
		}
		// create generation rules -
		// inhabitantGenerator -
		
		
		// householdGenerator +
		householdPopGenerator = new MultipleRulesGenerator("Household population generator", (int)TOTAL_HOUSEHOLDS); // data inconsistency?
		householdPopGenerator.setPopulationName(HOUSEHOLD_POPULATION_NAME);

		// create attributes +
		UniqueValuesAttribute householdSizeAttr = new UniqueValuesAttribute(householdPopGenerator, "size", DataType.INTEGER);
		for (int size = 1; size < (Data.household_size_by_types.length + 1); size++) {
			householdSizeAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(size)));
		}
		householdPopGenerator.addAttribute(householdSizeAttr);
		
		UniqueValuesAttribute householdTypeAttr = new UniqueValuesAttribute(householdPopGenerator, "type", DataType.STRING);
		for (String type : Data.household_type_values) {
			householdTypeAttr.add(new UniqueValue(DataType.STRING, type));
		}
		householdPopGenerator.addAttribute(householdTypeAttr);
		
		
		UniqueValuesAttribute householdLivingPlaceAttr = new UniqueValuesAttribute(householdPopGenerator, "living_place", DataType.STRING);
		householdLivingPlaceAttr.add(urbanValue);
		householdLivingPlaceAttr.add(ruralValue);
		householdPopGenerator.addAttribute(householdLivingPlaceAttr);
		// create attributes -
		
		// create generation rules +
		// generator1Rule1
		householdPopRule1 = new FrequencyDistributionGenerationRule(householdPopGenerator, HOUSEHOLD_GENERATOR_RULE1_NAME);
		householdPopRule1.appendOutputAttribute(householdSizeAttr);
		householdPopRule1.appendOutputAttribute(householdTypeAttr);
		householdPopRule1.appendOutputAttribute(householdLivingPlaceAttr); 
		householdPopRule1.generateAttributeValuesFrequencies();
		householdPopGenerator.appendGenerationRule(householdPopRule1);
		
		// an element of household_size_by_types : 
		//		{ size, urban-permanent, urban-semi-permanent, urban-less-permanent, urban-simple, urban-not-stated, 
		//			rural-permanent, rural-semi-permanent, rural-less-permanent, rural-simple, rural-not-stated }
		for (int[] types : Data.household_size_by_types) {
			attributeValues.clear();
			
			attributeValues.put(householdSizeAttr, new UniqueValue(DataType.INTEGER, Integer.toString(types[0])));
			
			// urban living places
			attributeValues.put(householdLivingPlaceAttr, urbanValue);
			for (int typeIndex=0; typeIndex<Data.household_type_values.length; typeIndex++) {
				attributeValues.put(householdTypeAttr, new UniqueValue(DataType.STRING, Data.household_type_values[typeIndex]));
				
				householdPopRule1.setFrequency(attributeValues, types[typeIndex+1]);
			}
			 
			// rural living places
			attributeValues.put(householdLivingPlaceAttr, ruralValue);
			for (int typeIndex=0; typeIndex<Data.household_type_values.length; typeIndex++) {
				attributeValues.put(householdTypeAttr, new UniqueValue(DataType.STRING, Data.household_type_values[typeIndex]));

				householdPopRule1.setFrequency(attributeValues, types[typeIndex+6]);
			}
		}
		// create generation rules -
		// householdGenerator -
	}

	private String basePath = "./test_6_October_2014";
	private String inhabitantPath = basePath + "/inhabitant";
	private String householdPath = basePath + "/household";
	private int times = 30;
	@Test public void test() throws GenstarException {
		System.out.println("Started test");
		
//		File baseDir = new File(basePath);
//		baseDir.mkdir();
		
		// "inhabitant" folder
//		File inhabitantBaseDir = new File(inhabitantPath);
//		inhabitantBaseDir.mkdir();
		
		// "household" folder
//		File householdBaseDir = new File(householdPath);
//		householdBaseDir.mkdir();
		
		
		for (int percentageIndex=0; percentageIndex<percentages.length; percentageIndex++) {
			
			
			// create "XPercent" folders
			String percentageName = Integer.toString( (int)(percentages[percentageIndex] * 100) ) + "Percent";
			String inhabitantPercentagePath = inhabitantPath + "/" + percentageName;
			String householdPercentagePath = householdPath + "/" + percentageName;
			
			File inhabitantPercentageDir = new File(inhabitantPercentagePath);
			inhabitantPercentageDir.mkdir();
			
			File householdPercentageDir = new File(householdPercentagePath);
			householdPercentageDir.mkdir();
			
			inhabitantPopGenerator.setNbOfEntities(nbOfInhabitantsToGenerate[percentageIndex]);
			householdPopGenerator.setNbOfEntities(nbOfHouseholdsToGenerate[percentageIndex]);
			
			System.out.println("Started generating " + (int)(percentages[percentageIndex] * 100) + " percent of populations with ");
			System.out.println("\t" + nbOfInhabitantsToGenerate[percentageIndex] + " of inhabitants");
			System.out.println("\t" + nbOfHouseholdsToGenerate[percentageIndex] + " of households");

			for (int iteration=0; iteration<times; iteration++) {
				System.out.println("\t\tStarted iteration : " + iteration);
				
				System.out.println("\t\t\tStarted generating 'inhabitant' population");
				ISyntheticPopulation inhabitantPopulation = testInhabitantGenerator(iteration, percentageName, inhabitantPercentagePath);
				System.out.println("\t\t\tFinished generating 'inhabitant' population");
								
				System.out.println("\t\t\tStarted generating 'household' population");
				ISyntheticPopulation householdPopulation = testHouseholdGenerator(iteration, percentageName, householdPercentagePath);
				System.out.println("\t\t\tFinished generating 'household' population");
				
				// only test the linkers on one iteration of 100% percent generation
				if ( percentages[percentageIndex] == 1f && ( (iteration == 0) || (iteration == 1) ) ) {
					MyPopLinker linker = null;
					if (iteration == 0) { linker = new PopLinker1(); }
					if (iteration == 1) { linker = new PopLinker2(); }
					
					List<ISyntheticPopulation> populations = new ArrayList<ISyntheticPopulation>();
					populations.add(inhabitantPopulation);
					populations.add(householdPopulation);
					testLinker(linker, populations);
				}
				
				System.out.println("\t\tFinished iteration : " + iteration);
			}
			
			System.out.println();
		}
		
		
		System.out.println("Finished test");
	}
	
	private ISyntheticPopulation testInhabitantGenerator(final int iteration, final String percentageName, final String basePath) throws GenstarException {

		int[][] age_ranges_copy = new int[Data.age_ranges.length][6];
		for (int rowIndex = 0; rowIndex < Data.age_ranges.length; rowIndex++) {
			for (int colIndex = 0; colIndex < Data.age_ranges[rowIndex].length; colIndex++) {
				age_ranges_copy[rowIndex][colIndex] = Data.age_ranges[rowIndex][colIndex];
			}
		}
		
		int[][] generated_age_ranges = new int[Data.age_ranges.length][6];
		for (int rowIndex = 0; rowIndex < Data.age_ranges.length; rowIndex++) {
			generated_age_ranges[rowIndex][0] =  Data.age_ranges[rowIndex][0];
			generated_age_ranges[rowIndex][1] =  Data.age_ranges[rowIndex][1];
			generated_age_ranges[rowIndex][2] = generated_age_ranges[rowIndex][3] 
				= generated_age_ranges[rowIndex][4] = generated_age_ranges[rowIndex][5] = 0;
		}
		
		int[][] generated_districts = new int[Data.locations.length][2];
		for (int rowIndex = 0; rowIndex < Data.locations.length; rowIndex++) {
			generated_districts[rowIndex][0] = generated_districts[rowIndex][1] = 0; 
		}
		
		
		ISyntheticPopulation inhabitantPopulation = inhabitantPopGenerator.generate();
		for (Entity inhabitant : inhabitantPopulation.getEntities()) {
			int age = ((UniqueValue) (inhabitant.getEntityAttributeValue("age").getAttributeValueOnEntity())).getIntValue();
			boolean isMale = ( (UniqueValue) (inhabitant.getEntityAttributeValue("sex").getAttributeValueOnEntity()) ).getBooleanValue();
			String livingPlace = ( (UniqueValue) (inhabitant.getEntityAttributeValue("living_place").getAttributeValueOnEntity()) ).getStringValue();

			// age, sex, living_place
			for (int rowIndex = 0; rowIndex < age_ranges_copy.length; rowIndex++) {
				if (age >= age_ranges_copy[rowIndex][0] && age <= age_ranges_copy[rowIndex][1]) {
					if (isMale) {
						if (livingPlace.equals("urban")) {
							age_ranges_copy[rowIndex][2] = --age_ranges_copy[rowIndex][2]; 
							generated_age_ranges[rowIndex][2] = ++generated_age_ranges[rowIndex][2];
						} else { // rural
							age_ranges_copy[rowIndex][4] = --age_ranges_copy[rowIndex][4]; 
							generated_age_ranges[rowIndex][4] = ++generated_age_ranges[rowIndex][4];
						}
					} else {
						if (livingPlace.equals("urban")) {
							age_ranges_copy[rowIndex][3] = --age_ranges_copy[rowIndex][3]; 
							generated_age_ranges[rowIndex][3] = ++generated_age_ranges[rowIndex][3];
						} else { // rural
							age_ranges_copy[rowIndex][5] = --age_ranges_copy[rowIndex][5]; 
							generated_age_ranges[rowIndex][5] = ++generated_age_ranges[rowIndex][5];
						}
					}
					
					break;
				}
			}
			
			// district
			String district = ((UniqueValue) (inhabitant.getEntityAttributeValue("district").getAttributeValueOnEntity())).getStringValue();
			int districtIndex = Data.getDistrictIndex(district);
			if (isMale) {
				generated_districts[districtIndex][0] = ++generated_districts[districtIndex][0];
			} else {
				generated_districts[districtIndex][1] = ++generated_districts[districtIndex][1];
			}
			
		}
				 
		
		// write result to file
		// { min age, max age, male urban individuals, female urban individuals, male rural individuals, female rural individuals }
		List<String[]> lines1 = new ArrayList<String[]>();
		String[] header1 = new String[5];
		header1[0] = "Age range";
		header1[1] = "Urban Male";
		header1[2] = "Urban Female";
		header1[3] = "Rural Male";
		header1[4] = "Rural Female";
		lines1.add(header1);
		
		String[] line;
		for (int rowIndex = 0; rowIndex < generated_age_ranges.length; rowIndex++) {
			line = new String[5];
			
			line[0] = new String("[" + generated_age_ranges[rowIndex][0] + " : " + generated_age_ranges[rowIndex][1] + "]");  // age range
			line[1] = Integer.toString(generated_age_ranges[rowIndex][2]); // urban male
			line[2] = Integer.toString(generated_age_ranges[rowIndex][3]); // urban female
			line[3] = Integer.toString(generated_age_ranges[rowIndex][4]); // rural male
			line[4] = Integer.toString(generated_age_ranges[rowIndex][5]); // rural female

			lines1.add(line);
		} 
		GenstarFileUtils.writeCSVFile(basePath + "/Inhabitant_age_sex_livingPlace_" + percentageName +" _Iteration" + Integer.toString(iteration) + ".csv", lines1);
		 
		
		
		List<String[]> lines2 = new ArrayList<String[]>();
		String[] header2 = new String[3];
		header2[0] = "District";
		header2[1] = "Male";
		header2[2] = "Female";
		lines2.add(header2);
		
		for (int rowIndex = 0; rowIndex < generated_districts.length; rowIndex++) {
			line = new String[3];
			
			line[0] = new String(Data.district_names[rowIndex]);  // district
			line[1] = new String(Integer.toString(generated_districts[rowIndex][0])); // male
			line[2] = new String(Integer.toString(generated_districts[rowIndex][1])); // female

			lines2.add(line);
		} 
		GenstarFileUtils.writeCSVFile(basePath + "/Inhabitant_district_sex_" + percentageName + "_Iteration" + Integer.toString(iteration) + ".csv", lines2);
		 
		return inhabitantPopulation;
	}
	
	private ISyntheticPopulation testHouseholdGenerator( final int iteration, final String percentageName, final String basePath) throws GenstarException {
		
		int[][] household_size_by_types_copy = new int[Data.household_size_by_types.length][11];
		for (int rowIndex = 0; rowIndex < Data.household_size_by_types.length; rowIndex++) {
			for (int colIndex = 0; colIndex < Data.household_size_by_types[rowIndex].length; colIndex++) {
				household_size_by_types_copy[rowIndex][colIndex] = Data.household_size_by_types[rowIndex][colIndex];
			}
		}
		
		
		int[][] generated_household_size_by_types = new int[Data.household_size_by_types.length][11];
		for (int rowIndex = 0; rowIndex < Data.household_size_by_types.length; rowIndex++) {
			generated_household_size_by_types[rowIndex][0] =  Data.household_size_by_types[rowIndex][0];
			
			generated_household_size_by_types[rowIndex][1] = generated_household_size_by_types[rowIndex][2] 
				= generated_household_size_by_types[rowIndex][3] = generated_household_size_by_types[rowIndex][4] 
				= generated_household_size_by_types[rowIndex][5] = generated_household_size_by_types[rowIndex][6] 
				= generated_household_size_by_types[rowIndex][7] = generated_household_size_by_types[rowIndex][8] 
				= generated_household_size_by_types[rowIndex][9] = generated_household_size_by_types[rowIndex][10] = 0;
		}
		

		Map<String, Integer> householdTypes = new HashMap<String, Integer>();
		for (int i=0; i<Data.household_type_values.length; i++) { householdTypes.put(Data.household_type_values[i], i + 1); }
		
		
		ISyntheticPopulation householdPopulation = householdPopGenerator.generate();
		for (Entity household : householdPopulation.getEntities()) {
			int size = ((UniqueValue) household.getEntityAttributeValue("size").getAttributeValueOnEntity()).getIntValue();
			
			String livingPlace = ( (UniqueValue) household.getEntityAttributeValue("living_place").getAttributeValueOnEntity() ).getStringValue();

			String type = ( (UniqueValue) household.getEntityAttributeValue("type").getAttributeValueOnEntity()).getStringValue();
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
		GenstarFileUtils.writeCSVFile(basePath + "/Household_urban_size_type_" + percentageName + "_Iteration" + Integer.toString(iteration) + ".csv", urbanLines);
		
		// rural file
		GenstarFileUtils.writeCSVFile(basePath + "/Household_rural_size_type_" + percentageName + "_Iteration" + Integer.toString(iteration) + ".csv", ruralLines);
		
		return householdPopulation;
	}
	
	
	private void testLinker(final MyPopLinker linker, final List<ISyntheticPopulation> populations) throws GenstarException {
		
		// test and evaluate 2 linkers
		// criteria :
		//		+ number of Households successfully/unsuccessfully filled with Inhabitants
		//		+ explain the reason for this from input data
		
		System.out.println("Started establishing relationship between entities with " + linker.getClass().getName() + " as population linker");
		System.out.println("Concerning populations : ");
		for (ISyntheticPopulation p : populations) { System.out.println("\t+ " + p.getName() + " with " + p.getInitialNbOfEntities() + " entities"); }
		
		linker.establishRelationship(populations);
				
		System.out.println("Finished establishing relationship between entities");
		System.out.println("Picked households : " + linker.getPickedHouseholds().size() + "; picked inhabitants : " + linker.getPickedInhabitants().size());
		System.out.println();
	}
	
	
	private abstract class MyPopLinker extends AbstractPopulationsLinker {
		
		protected ISyntheticPopulation inhabitantPopulation, householdPopulation;

		// internal data
		protected List<Entity> pickedHouseholds = null;
		protected List<Entity> pickedInhabitants = null;

		public List<Entity> getPickedHouseholds() {
			return pickedHouseholds;
		}
		
		public List<Entity> getPickedInhabitants() {
			return pickedInhabitants;
		}
	}
	
	private class PopLinker1 extends MyPopLinker {
		
		@Override
		public void establishRelationship(final List<ISyntheticPopulation> populations) throws GenstarException {
			// populations:
			// 		1. population 1: Inhabitant population
			//		2. population 2: Household population
			
			if (populations == null) { throw new IllegalArgumentException("'populations' parameter can not be null"); }
			if (populations.size() != 2) { throw new IllegalArgumentException("'populations' must contain 2 populations"); }
			
			ISyntheticPopulation tmpPop1 = populations.get(0);
			ISyntheticPopulation tmpPop2 = populations.get(1);
			
			if (tmpPop1.getName().equals(INHABITANT_POPULATION_NAME)) { 
				inhabitantPopulation = tmpPop1; 
				householdPopulation = tmpPop2;
			} else {
				inhabitantPopulation = tmpPop2; 
				householdPopulation = tmpPop1;
			}
			
			if (householdPopulation == null || inhabitantPopulation == null) {
				throw new IllegalArgumentException("'populations' doesn't contain required populations");
			}
			
			this.populations = new ArrayList<ISyntheticPopulation>(populations);
			
			// pick inhabitants into households
			EntityAttributeValue hhSizeEntityAttrValue;
			UniqueValue hhSizeAttrValue;
			int hhSizeIntValue;
			pickedHouseholds = new ArrayList<Entity>();
			pickedInhabitants = new ArrayList<Entity>();
			List<Entity> availableHouseholds;
			
			for (int iteration=0; iteration<totalRound; iteration++) {
				availableHouseholds = new ArrayList<Entity>(householdPopulation.getEntities());
				
				for (Entity householdEntity : availableHouseholds) {
					hhSizeEntityAttrValue = householdEntity.getEntityAttributeValue("size");
					hhSizeAttrValue = (UniqueValue) hhSizeEntityAttrValue.getAttributeValueOnEntity();
					hhSizeIntValue = Integer.parseInt(hhSizeAttrValue.getStringValue());
					
					buildHousehold(householdEntity, hhSizeIntValue);
				}
			}
		}
		
		// put inhabitants into household basing on household's size
		private void buildHousehold(final Entity householdEntity, final int size) {
			List<Entity> members = new ArrayList<Entity>();
			for (int i=0; i<size; i++) {
				Entity inhabitant = inhabitantPopulation.pick();
				
				if (inhabitant == null) { 
					inhabitantPopulation.putBack(members);
					return; 
				}
				
				members.add(inhabitant);
			}
			
			householdEntity.addMembers(members);
			householdPopulation.pick(householdEntity);
			
			pickedInhabitants.addAll(members);
			pickedHouseholds.add(householdEntity);
		}
		
	}
	
	public class PopLinker2 extends MyPopLinker {

		@Override
		public void establishRelationship(final List<ISyntheticPopulation> populations) throws GenstarException {
			// populations:
			// 		1. population 1: Inhabitant population
			//		2. population 2: Household population
			
			if (populations == null) { throw new IllegalArgumentException("'populations' parameter can not be null"); }
			if (populations.size() != 2) { throw new IllegalArgumentException("'populations' must contain 2 populations"); }
			
			ISyntheticPopulation tmpPop1 = populations.get(0);
			ISyntheticPopulation tmpPop2 = populations.get(1);
			
			if (tmpPop1.getName().equals(INHABITANT_POPULATION_NAME)) { 
				inhabitantPopulation = tmpPop1; 
				householdPopulation = tmpPop2;
			} else {
				inhabitantPopulation = tmpPop2; 
				householdPopulation = tmpPop1;
			}
			
			if (householdPopulation == null || inhabitantPopulation == null) {
				throw new IllegalArgumentException("'populations' doesn't contain required populations");
			}
			
			this.populations = new ArrayList<ISyntheticPopulation>(populations);
			
			// pick inhabitants into households
			int hhSizeIntValue;
			String livingPlace;
			
			pickedHouseholds = new ArrayList<Entity>();
			pickedInhabitants = new ArrayList<Entity>();
			List<Entity> availableHouseholds;
			
			for (int iteration=0; iteration<totalRound; iteration++) {
				availableHouseholds = new ArrayList<Entity>(householdPopulation.getEntities());
				
				for (Entity householdEntity : availableHouseholds) {
					hhSizeIntValue = ((UniqueValue) householdEntity.getEntityAttributeValue("size").getAttributeValueOnEntity()).getIntValue();
					livingPlace = ((UniqueValue) householdEntity.getEntityAttributeValue("living_place").getAttributeValueOnEntity()).getStringValue();
					
					buildHousehold(householdEntity, hhSizeIntValue, livingPlace);
				}
			}
		}
		
		// put inhabitants into household basing on household's size and living_place
		private void buildHousehold(final Entity householdEntity, final int size, final String livingPlace) throws GenstarException {
			Map<String, AttributeValue> livingPlaceMap = new HashMap<String, AttributeValue>();
			UniqueValue livingPlaceAttrValue = new UniqueValue(DataType.STRING, livingPlace);
			livingPlaceMap.put("living_place", livingPlaceAttrValue);

			List<Entity> members = new ArrayList<Entity>();
			for (int i=0; i<size; i++) {
				
				Entity inhabitant = inhabitantPopulation.pick(livingPlaceMap);
				if (inhabitant == null) {
					inhabitantPopulation.putBack(members);
					return;
				}
				
				members.add(inhabitant);
			}
			
			householdEntity.addMembers(members);
			householdPopulation.pick(householdEntity);
			
			pickedInhabitants.addAll(members);
			pickedHouseholds.add(householdEntity);
		}
	}
		
}
