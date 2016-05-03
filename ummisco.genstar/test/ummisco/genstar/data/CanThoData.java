package ummisco.genstar.data;

import java.util.HashMap;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SampleFreeGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;

public final class CanThoData {
	

	private static final boolean[] sexes = { true, false };
	
	public static final class Scenario1 {
		
		// "Inhabitant" population +
		// Rule 1 : Inhabitant population by sex and age group
		/**
		 * Type : Frequency distribution
		 * Attributes : 
		 * 	age : enumeration of integer ranges
		 * 	sex : enumeration of boolean values (true : male, false : female)
		 * 
		 * Input attribute :
		 * 	{}
		 * 
		 * Output attributes :
		 * 	{ age, sex } 
		 * 
		 * Note :
		 * 	an age_ranges_1 element = { min age, max age, male individuals, female individuals }
		 */
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
		// "Inhabitant" population -
		

		// "Household" population +
		// Rule 1 : Household by size (number of members) and type (permanent, semi-permanent, less-permanent, simple, not stated)
		/**
		 * Type : Frequency distribution
		 * Attributes : 
		 * 	size : enumeration of integers (1..9)
		 * 	type : enumeration of string { "permanent", "semi-permanent", "less-permanent", "simple", "not-stated" }
		 * 
		 * Input attribute :
		 * 	{}
		 * 
		 * Output attributes :
		 * 	{ type, size }
		 */
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
		
		public static final String[] household_type1_values = { "permanent", "semi-permanent", "less-permanent", "simple", "not-stated" };
		// "Household" population -
		
	}
	
	
	public static final class Scenario2 {
		
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
		 *   an age_ranges_2 element = { min age, max age, male urban individuals, female urban individuals, male rural individuals, female rural individuals }
		 */
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
		
		public static final String[] living_place_values = { "urban", "rural" };
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
		public static final int[][] household_size_by_types_2 = {
			{ 1,  696,  8751, 1150, 1420,  4,  124,  1380,  651,  1399, 1 }, // { size, urban-permanent, urban-semi-permanent, urban-less-permanent, urban-simple, urban-not-stated, rural-permanent, rural-semi-permanent, rural-less-permanent, rural-simple, rural-not-stated }
			{ 2, 1357, 17798, 2128, 2150, 10,  367,  3392, 1505,  2533, 1 },
			{ 3, 2482, 30598, 4812, 5136, 15,  671,  6995, 4130,  6910, 0 },
			{ 4, 3320, 35442, 6599, 6568,  7, 1164, 10932, 6548, 10211, 2 },
			{ 5, 2234, 18642, 3418, 3043,  6, 1149,  7408, 3608,  5064, 2 },
			{ 6, 1706, 12903, 1935, 1535,  2,  983,  5029, 2228,  2521, 2 },
			{ 7,  703,  4628,  775,  549,  3,  424,  1850,  748,   831, 2 },
			{ 8,  413,  2865,  426,  328,  0,  235,   984,  386,   459, 0 },
			{ 9,  445,  3546,  465,  331,  0,  196,   878,  397,   432, 0 },
		};

		public static final String[] household_type2_values = { "permanent", "semi-permanent", "less-permanent", "simple", "not-stated" };
		// "Household" population -
		
	}
	
	
	public static final class Scenario3 {
		// "Inhabitant" population +
		// Rule 1 : Inhabitant population by sex and age group
		/**
		 * Type : Frequency distribution
		 * Attributes : 
		 * 	age : enumeration of integer ranges
		 * 	sex : enumeration of boolean values (true : male, false : female)
		 * 
		 * Input attribute :
		 * 	{}
		 * 
		 * Output attributes :
		 * 	{ age, sex } 
		 * 
		 * Note :
		 * 	an age_ranges_1 element = { min age, max age, male individuals, female individuals }
		 */
		public static final int[][] age_ranges_3 = {
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
		 * 	a locations_3 element = { male individuals, female individuals }
		 */
		public static final int[][] locations_3 = {
			{ 116700, 127365 },
			{  65232,  65042 },
			{  54314,  56992 },
			{  42617,  43711 },
			{  79932,  79529 },
			{  57153,  55735 },
			{  62996,  61269 },
			{  61870,  59451 },
			{  49928,  49739 },
		};
		
		public static final String[] district_names = { "Ninh Kieu", "O Mon", "Binh Thuy", "Cai Rang", "Thot Not", "Vinh Thanh", "Co Do", "Thoi Lai", "Phong Dien" };
		
		public static int getDistrictIndex(final String districtName) {
			for (int index=0; index<district_names.length; index++) {
				if (districtName.equals(district_names[index])) { return index;}
			}
			
			return -1;
		}
		
		// "Inhabitant" population -
		

		// "Household" population +
		// Rule 3 : Household by size (number of members) and type (permanent, semi-permanent, less-permanent, simple, not stated)
		/**
		 * Type : Frequency distribution
		 * Attributes : 
		 * 	size : enumeration of integers (1..9)
		 * 	type : enumeration of string { "permanent", "semi-permanent", "less-permanent", "simple", "not-stated" }
		 * 
		 * Input attribute :
		 * 	{}
		 * 
		 * Output attributes :
		 * 	{ type, size }
		 */
		public static final int[][] household_size_by_types_3 = {
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
		
		public static final String[] household_type3_values = { "permanent", "semi-permanent", "less-permanent", "simple", "not-stated" };
		// "Household" population -		
		
		
	}
	
	

	public static final class Scenario4 {
		// "Inhabitant" population +
		// Rule 1 : Inhabitant population by sex and age group
		/**
		 * Type : Frequency distribution
		 * Attributes : 
		 * 	age : enumeration of integer ranges
		 * 	sex : enumeration of boolean values (true : male, false : female)
		 * 
		 * Input attribute :
		 * 	{}
		 * 
		 * Output attributes :
		 * 	{ age, sex } 
		 * 
		 * Note :
		 * 	an age_ranges_1 element = { min age, max age, male individuals, female individuals }
		 */
		public static final int[][] age_ranges_4 = {
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
		 * 	a locations_5 element = { male individuals, female individuals }
		 */
		public static final int[][] locations_4 = {
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
		
		public static final String[] district_names = { "Ninh Kieu", "O Mon", "Binh Thuy", "Cai Rang", "Thot Not", "Vinh Thanh", "Co Do", "Thoi Lai", "Phong Dien" };
		
		public static int getDistrictIndex(final String districtName) {
			for (int index=0; index<district_names.length; index++) {
				if (districtName.equals(district_names[index])) { return index;}
			}
			
			return -1;
		}
		
		// "Inhabitant" population -
		
	}

	
	public static final class Scenario5 {
		
		// "Inhabitant" population +
		// Rule 1 : Inhabitant population by sex and location (i.e., district)
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
		 * 	a locations_5 element = { male individuals, female individuals }
		 */
		public static final int[][] locations_5 = {
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
		
		public static final String[] district_names = { "Ninh Kieu", "O Mon", "Binh Thuy", "Cai Rang", "Thot Not", "Vinh Thanh", "Co Do", "Thoi Lai", "Phong Dien" };
		
		public static int getDistrictIndex(final String districtName) {
			for (int index=0; index<district_names.length; index++) {
				if (districtName.equals(district_names[index])) { return index;}
			}
			
			return -1;
		}
		
		
		// Rule 2 : Inhabitant population by sex and age group
		/**
		 * Type : Frequency distribution
		 * Attributes : 
		 * 	age : enumeration of integer ranges
		 * 	sex : enumeration of boolean values (true : male, false : female)
		 * 
		 * Input attribute :
		 * 	{}
		 * 
		 * Output attributes :
		 * 	{ age, sex } 
		 * 
		 * Note :
		 * 	an age_ranges_5 element = { min age, max age, male individuals, female individuals }
		 */
		public static final int[][] age_ranges_5 = {
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
		
		
		// "Inhabitant" population -
		
	}	
	
	
	
	public static final String SCENARIO1_RULE1_NAME = "Inhabitant population by age and sex (Scenario 1)";
	
	public static final String SCENARIO1_RULE2_NAME = "Household population by size and type (Scenario 1)";

	public static final String SCENARIO2_RULE1_NAME = "Inhabitant population by age and sex (Scenario 2)";
	
	public static final String SCENARIO2_RULE2_NAME = "Household population by size and type (Scenario 2)";
	
	public static final String SCENARIO3_RULE1_NAME = "Inhabitant population by age and sex (Scenario 3)";

	public static final String SCENARIO3_RULE2_NAME = "Inhabitant population by sex and district/location (Scenario 3)";
	
	public static final String SCENARIO3_RULE3_NAME = "Household population by size and type (Scenario 3)";

	public static final String SCENARIO4_RULE1_NAME = "Inhabitant population by age and sex (Scenario 4)";

	public static final String SCENARIO4_RULE2_NAME = "Inhabitant population by sex and district/location (Scenario 4)";

	public static final String SCENARIO5_RULE1_NAME = "Inhabitant population by sex and district/location (Scenario 5)";

	public static final String SCENARIO5_RULE2_NAME = "Inhabitant population by age and sex (Scenario 5)";
	
	

	private SampleFreeGenerator scenario1InhabitantPopGenerator;
	private FrequencyDistributionGenerationRule scenario1Rule1;
	
	private SampleFreeGenerator scenario1HouseholdPopGenerator;
	private FrequencyDistributionGenerationRule scenario1Rule2;
	
	private SampleFreeGenerator scenario2InhabitantPopGenerator;
	private FrequencyDistributionGenerationRule scenario2Rule1;
	
	private SampleFreeGenerator scenario2HouseholdPopGenerator;
	private FrequencyDistributionGenerationRule scenario2Rule2;
	
	private SampleFreeGenerator scenario3InhabitantPopGenerator;
	private FrequencyDistributionGenerationRule scenario3Rule1, scenario3Rule2;
	
	private SampleFreeGenerator scenario3HouseholdPopGenerator;
	private FrequencyDistributionGenerationRule scenario3Rule3;
	
	private SampleFreeGenerator scenario4InhabitantPopGenerator;
	private FrequencyDistributionGenerationRule scenario4Rule1, scenario4Rule2;
	
	private SampleFreeGenerator scenario5InhabitantPopGenerator;
	private FrequencyDistributionGenerationRule scenario5Rule1, scenario5Rule2;
	
	
	
	public CanThoData() throws GenstarException {
		initializeScenario1Data();
		initializeScenario2Data();
		initializeScenario3Data();
		initializeScenario4Data();
		initializeScenario5Data();
	}
	
	public ISyntheticPopulationGenerator getScenario1InhabitantGenerator() {
		return scenario1InhabitantPopGenerator;
	}
	
	public ISyntheticPopulationGenerator getScenario1HouseholdGenerator() {
		return scenario1HouseholdPopGenerator;
	}
	
	public ISyntheticPopulationGenerator getScenario2InhabitantGenerator() {
		return scenario2InhabitantPopGenerator;
	}
	
	public ISyntheticPopulationGenerator getScenario2HouseholdGenerator() {
		return scenario2HouseholdPopGenerator;
	}
	
	public ISyntheticPopulationGenerator getScenario3InhabitantGenerator() {
		return scenario3InhabitantPopGenerator;
	}
	
	public ISyntheticPopulationGenerator getScenario3HouseholdGenerator() {
		return scenario3HouseholdPopGenerator;
	}
	
	public ISyntheticPopulationGenerator getScenario4InhabitantGenerator() {
		return scenario4InhabitantPopGenerator;
	}
	
	public ISyntheticPopulationGenerator getScenario5InhabitantGenerator() {
		return scenario5InhabitantPopGenerator;
	}
	

	private void initializeScenario1Data() throws GenstarException {
		scenario1InhabitantPopGenerator = new SampleFreeGenerator("Scenario1's inhabitant population generator", 1188435);
		
		// create attributes +
		
		RangeValuesAttribute ageRangesAttr1 = new RangeValuesAttribute(scenario1InhabitantPopGenerator, "age", DataType.INTEGER, UniqueValue.class);
		for (int[] range : Scenario1.age_ranges_1) {
			ageRangesAttr1.add(new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
		}
		scenario1InhabitantPopGenerator.addAttribute(ageRangesAttr1);
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(scenario1InhabitantPopGenerator, "sex", DataType.BOOL);
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0])));
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1])));
		scenario1InhabitantPopGenerator.addAttribute(sexAttr);
		
		// create attributes -
		
		// create generation rules +
		
		// scenario1Rule1
		scenario1Rule1 = new FrequencyDistributionGenerationRule(scenario1InhabitantPopGenerator, SCENARIO1_RULE1_NAME);
		scenario1Rule1.appendOutputAttribute(ageRangesAttr1);
		scenario1Rule1.appendOutputAttribute(sexAttr);
		scenario1Rule1.generateAttributeValuesFrequencies();
		scenario1InhabitantPopGenerator.appendGenerationRule(scenario1Rule1);
		
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		AttributeValue sexAttrValue;
		for (int[] range : Scenario1.age_ranges_1) {
			attributeValues.clear();
			
			attributeValues.put(ageRangesAttr1, new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
			sexAttrValue = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0]));
			attributeValues.put(sexAttr, sexAttrValue);
			
			// male
			scenario1Rule1.setFrequency(attributeValues, range[2]);
			
			// female
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[1])));
			scenario1Rule1.setFrequency(attributeValues, range[3]);
		}
		 
		// create generation rules -
		
		
		scenario1HouseholdPopGenerator = new SampleFreeGenerator("Scenario1's household population generator", 286057);

		// create attributes +
		
		UniqueValuesAttribute householdSizeAttr = new UniqueValuesAttribute(scenario1HouseholdPopGenerator, "size", DataType.INTEGER);
		for (int size = 1; size < (Scenario1.household_size_by_types_1.length + 1); size++) {
			householdSizeAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(size)));
		}
		scenario1HouseholdPopGenerator.addAttribute(householdSizeAttr);
		
		UniqueValuesAttribute householdTypeAttr = new UniqueValuesAttribute(scenario1HouseholdPopGenerator, "type", DataType.STRING);
		for (String type : Scenario1.household_type1_values) {
			householdTypeAttr.add(new UniqueValue(DataType.STRING, type));
		}
		scenario1HouseholdPopGenerator.addAttribute(householdTypeAttr);
		
		// create attributes -
		
		// create generation rules +
		// scenario1Rule2
		scenario1Rule2 = new FrequencyDistributionGenerationRule(scenario1HouseholdPopGenerator, SCENARIO1_RULE2_NAME);
		scenario1Rule2.appendOutputAttribute(householdSizeAttr);
		scenario1Rule2.appendOutputAttribute(householdTypeAttr);
		scenario1Rule2.generateAttributeValuesFrequencies();
		scenario1HouseholdPopGenerator.appendGenerationRule(scenario1Rule2);
		
		UniqueValue hhTypeValue;
		for (int[] types : Scenario1.household_size_by_types_1) {
			attributeValues.clear();
			
			attributeValues.put(householdSizeAttr, new UniqueValue(DataType.INTEGER, Integer.toString(types[0])));
			for (int typeIndex=0; typeIndex<Scenario1.household_type1_values.length; typeIndex++) {
				hhTypeValue = new UniqueValue(DataType.STRING, Scenario1.household_type1_values[typeIndex]);
				attributeValues.put(householdTypeAttr, hhTypeValue);
				
				scenario1Rule2.setFrequency(attributeValues, types[typeIndex+1]);
			}
		}
		
		// create generation rules -
	}
	
	private void initializeScenario2Data() throws GenstarException {
		scenario2InhabitantPopGenerator = new SampleFreeGenerator("Scenario2's inhabitant population generator", 1188435);
		
		// create attributes +
		
		RangeValuesAttribute ageRangesAttr2 = new RangeValuesAttribute(scenario2InhabitantPopGenerator, "age", DataType.INTEGER, UniqueValue.class);
		for (int[] range : Scenario2.age_ranges_2) {
			ageRangesAttr2.add(new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
		}
		scenario2InhabitantPopGenerator.addAttribute(ageRangesAttr2);
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(scenario2InhabitantPopGenerator, "sex", DataType.BOOL);
		UniqueValue maleValue = new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0]));
		sexAttr.add(maleValue);
		UniqueValue femaleValue = new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1]));
		sexAttr.add(femaleValue);
		scenario2InhabitantPopGenerator.addAttribute(sexAttr);
		
		UniqueValuesAttribute inhabitantLivingPlaceAttr = new UniqueValuesAttribute(scenario2InhabitantPopGenerator, "living_place", DataType.STRING);
		UniqueValue urbanValue = new UniqueValue(DataType.STRING, Scenario2.living_place_values[0]);
		inhabitantLivingPlaceAttr.add(urbanValue);
		UniqueValue ruralValue = new UniqueValue(DataType.STRING, Scenario2.living_place_values[1]);
		inhabitantLivingPlaceAttr.add(ruralValue);
		scenario2InhabitantPopGenerator.addAttribute(inhabitantLivingPlaceAttr);
		
		// create attributes -
		
		// create generation rules +
		// scenario2Rule1
		scenario2Rule1 = new FrequencyDistributionGenerationRule(scenario2InhabitantPopGenerator, SCENARIO2_RULE1_NAME);
		scenario2Rule1.appendOutputAttribute(ageRangesAttr2);
		scenario2Rule1.appendOutputAttribute(sexAttr);
		scenario2Rule1.appendOutputAttribute(inhabitantLivingPlaceAttr);
		scenario2Rule1.generateAttributeValuesFrequencies();
		scenario2InhabitantPopGenerator.appendGenerationRule(scenario2Rule1);
		
		
		//  an age_ranges_2 element = { min age, max age, male urban individuals, female urban individuals, male rural individuals, female rural individuals }
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (int[] range : Scenario2.age_ranges_2) {
			attributeValues.clear();
			
			attributeValues.put(ageRangesAttr2, new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));

			attributeValues.put(inhabitantLivingPlaceAttr, urbanValue); // urban 
			attributeValues.put(sexAttr, maleValue); // male
			scenario2Rule1.setFrequency(attributeValues, range[2]); // urban male
			
			attributeValues.put(sexAttr, femaleValue); // female
			scenario2Rule1.setFrequency(attributeValues, range[3]); // urban female
			
			attributeValues.put(inhabitantLivingPlaceAttr, ruralValue); // rural 
			attributeValues.put(sexAttr, maleValue); // male
			scenario2Rule1.setFrequency(attributeValues, range[4]); // rural male
			
			attributeValues.put(sexAttr, femaleValue); // female
			scenario2Rule1.setFrequency(attributeValues, range[5]); // rural female
		}
		// create generation rules -
		
		
//		scenario2HouseholdPopGenerator = new SyntheticPopulationGenerator("Scenario2's household population generator", 286057);
		scenario2HouseholdPopGenerator = new SampleFreeGenerator("Scenario2's household population generator", 286076); // data inconsistency?

		// create attributes +
		
		UniqueValuesAttribute householdSizeAttr = new UniqueValuesAttribute(scenario2HouseholdPopGenerator, "size", DataType.INTEGER);
		for (int size = 1; size < (Scenario2.household_size_by_types_2.length + 1); size++) {
			householdSizeAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(size)));
		}
		scenario2HouseholdPopGenerator.addAttribute(householdSizeAttr);
		
		UniqueValuesAttribute householdTypeAttr = new UniqueValuesAttribute(scenario2HouseholdPopGenerator, "type", DataType.STRING);
		for (String type : Scenario2.household_type2_values) {
			householdTypeAttr.add(new UniqueValue(DataType.STRING, type));
		}
		scenario2HouseholdPopGenerator.addAttribute(householdTypeAttr);
		
		UniqueValuesAttribute householdLivingPlaceAttr = new UniqueValuesAttribute(scenario2HouseholdPopGenerator, "living_place", DataType.STRING);
		householdLivingPlaceAttr.add(urbanValue);
		householdLivingPlaceAttr.add(ruralValue);
		scenario2HouseholdPopGenerator.addAttribute(householdLivingPlaceAttr);
		 
		// create attributes -
		
		// create generation rules +
		// scenario2Rule2
		scenario2Rule2 = new FrequencyDistributionGenerationRule(scenario2HouseholdPopGenerator, SCENARIO1_RULE2_NAME);
		scenario2Rule2.appendOutputAttribute(householdSizeAttr);
		scenario2Rule2.appendOutputAttribute(householdTypeAttr);
		scenario2Rule2.appendOutputAttribute(householdLivingPlaceAttr); 
		scenario2Rule2.generateAttributeValuesFrequencies();
		scenario2HouseholdPopGenerator.appendGenerationRule(scenario2Rule2);
		
		// an element of household_size_by_types_2 : 
		//		{ size, urban-permanent, urban-semi-permanent, urban-less-permanent, urban-simple, urban-not-stated, 
		//			rural-permanent, rural-semi-permanent, rural-less-permanent, rural-simple, rural-not-stated }
		for (int[] types : Scenario2.household_size_by_types_2) {
			attributeValues.clear();
			
			attributeValues.put(householdSizeAttr, new UniqueValue(DataType.INTEGER, Integer.toString(types[0])));
			
			// urban living places
			attributeValues.put(householdLivingPlaceAttr, urbanValue);
			for (int typeIndex=0; typeIndex<Scenario2.household_type2_values.length; typeIndex++) {
				attributeValues.put(householdTypeAttr, new UniqueValue(DataType.STRING, Scenario2.household_type2_values[typeIndex]));
				
				scenario2Rule2.setFrequency(attributeValues, types[typeIndex+1]);
			}
			 
			// rural living places
			attributeValues.put(householdLivingPlaceAttr, ruralValue);
			for (int typeIndex=0; typeIndex<Scenario2.household_type2_values.length; typeIndex++) {
				attributeValues.put(householdTypeAttr, new UniqueValue(DataType.STRING, Scenario2.household_type2_values[typeIndex]));

				scenario2Rule2.setFrequency(attributeValues, types[typeIndex+6]);
			}
		}
		
		// create generation rules -
	}

	private void initializeScenario3Data() throws GenstarException {
		scenario3InhabitantPopGenerator = new SampleFreeGenerator("Scenario3's inhabitant population generator", 1188435);
		
		// create attributes +
		
		RangeValuesAttribute ageRangesAttr3 = new RangeValuesAttribute(scenario3InhabitantPopGenerator, "age", DataType.INTEGER, UniqueValue.class);
		for (int[] range : Scenario3.age_ranges_3) {
			ageRangesAttr3.add(new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
		}
		scenario3InhabitantPopGenerator.addAttribute(ageRangesAttr3);
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(scenario3InhabitantPopGenerator, "sex", DataType.BOOL);
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0])));
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1])));
		scenario3InhabitantPopGenerator.addAttribute(sexAttr);
		
		UniqueValuesAttribute districtAttr = new UniqueValuesAttribute(scenario3InhabitantPopGenerator, "district", DataType.STRING);
		for (String district : Scenario3.district_names) { districtAttr.add(new UniqueValue(DataType.STRING, district)); }
		scenario3InhabitantPopGenerator.addAttribute(districtAttr);
		
		// create attributes -
		
		
		
		// create generation rules +
		
		// scenario3Rule1
		scenario3Rule1 = new FrequencyDistributionGenerationRule(scenario3InhabitantPopGenerator, SCENARIO3_RULE1_NAME);
		scenario3Rule1.appendOutputAttribute(ageRangesAttr3);
		scenario3Rule1.appendOutputAttribute(sexAttr);
		scenario3Rule1.generateAttributeValuesFrequencies();
		scenario3InhabitantPopGenerator.appendGenerationRule(scenario3Rule1);
		
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		AttributeValue sexAttrValue;
		for (int[] range : Scenario3.age_ranges_3) {
			attributeValues.clear();
			
			attributeValues.put(ageRangesAttr3, new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
			sexAttrValue = new UniqueValue(DataType.BOOL, Boolean.toString(CanThoData.sexes[0]));
			attributeValues.put(sexAttr, sexAttrValue);
			
			// male
			scenario3Rule1.setFrequency(attributeValues, range[2]);
			
			// female
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(CanThoData.sexes[1])));
			scenario3Rule1.setFrequency(attributeValues, range[3]);
		}
		
		
		// scenario3Rule2
		scenario3Rule2 = new FrequencyDistributionGenerationRule(scenario3InhabitantPopGenerator, SCENARIO3_RULE2_NAME);
		scenario3Rule2.appendInputAttribute(sexAttr);
		scenario3Rule2.appendOutputAttribute(districtAttr);
		scenario3Rule2.generateAttributeValuesFrequencies();
		scenario3InhabitantPopGenerator.appendGenerationRule(scenario3Rule2);
		
		// set frequencies
		AttributeValue districtValue;
		int districtIndex = 0;
		for (int location[] : Scenario3.locations_3) {
			attributeValues.clear();
			
			districtValue = new UniqueValue(DataType.STRING, Scenario3.district_names[districtIndex]);
			attributeValues.put(districtAttr, districtValue);
			
			// male
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(CanThoData.sexes[0])));
			scenario3Rule2.setFrequency(attributeValues, location[0]);
			
			// female
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(CanThoData.sexes[1])));
			scenario3Rule2.setFrequency(attributeValues, location[1]);
		
			
			districtIndex++;
		}		 
		// create generation rules -
		 
		
	
		scenario3HouseholdPopGenerator = new SampleFreeGenerator("Scenario3's household population generator", 286057);

		// create attributes +
		
		UniqueValuesAttribute householdSizeAttr = new UniqueValuesAttribute(scenario3HouseholdPopGenerator, "size", DataType.INTEGER);
		for (int size = 1; size < (Scenario3.household_size_by_types_3.length + 1); size++) {
			householdSizeAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(size)));
		}
		scenario3HouseholdPopGenerator.addAttribute(householdSizeAttr);
		
		UniqueValuesAttribute householdTypeAttr = new UniqueValuesAttribute(scenario3HouseholdPopGenerator, "type", DataType.STRING);
		for (String type : Scenario3.household_type3_values) {
			householdTypeAttr.add(new UniqueValue(DataType.STRING, type));
		}
		scenario3HouseholdPopGenerator.addAttribute(householdTypeAttr);
		
		// create attributes -
		
		// create generation rules +
		// scenario3Rule3
		scenario3Rule3 = new FrequencyDistributionGenerationRule(scenario3HouseholdPopGenerator, SCENARIO3_RULE3_NAME);
		scenario3Rule3.appendOutputAttribute(householdSizeAttr);
		scenario3Rule3.appendOutputAttribute(householdTypeAttr);
		scenario3Rule3.generateAttributeValuesFrequencies();
		scenario3HouseholdPopGenerator.appendGenerationRule(scenario3Rule3);
		
		UniqueValue hhTypeValue;
		for (int[] types : Scenario3.household_size_by_types_3) {
			attributeValues.clear();
			
			attributeValues.put(householdSizeAttr, new UniqueValue(DataType.INTEGER, Integer.toString(types[0])));
			for (int typeIndex=0; typeIndex<Scenario3.household_type3_values.length; typeIndex++) {
				hhTypeValue = new UniqueValue(DataType.STRING, Scenario3.household_type3_values[typeIndex]);
				attributeValues.put(householdTypeAttr, hhTypeValue);
				
				scenario3Rule3.setFrequency(attributeValues, types[typeIndex+1]);
			}
		}
		
		// create generation rules -
	}

	private void initializeScenario4Data() throws GenstarException {
		scenario4InhabitantPopGenerator = new SampleFreeGenerator("Scenario4's inhabitant population generator", 1188435);
		
		// create attributes +
		
		RangeValuesAttribute ageRangesAttr4 = new RangeValuesAttribute(scenario4InhabitantPopGenerator, "age", DataType.INTEGER, UniqueValue.class);
		for (int[] range : Scenario4.age_ranges_4) {
			ageRangesAttr4.add(new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
		}
		scenario4InhabitantPopGenerator.addAttribute(ageRangesAttr4);
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(scenario4InhabitantPopGenerator, "sex", DataType.BOOL);
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0])));
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1])));
		scenario4InhabitantPopGenerator.addAttribute(sexAttr);
		
		UniqueValuesAttribute districtAttr = new UniqueValuesAttribute(scenario4InhabitantPopGenerator, "district", DataType.STRING);
		for (String district : Scenario4.district_names) { districtAttr.add(new UniqueValue(DataType.STRING, district)); }
		scenario4InhabitantPopGenerator.addAttribute(districtAttr);
		
		// create attributes -
		
		
		
		// create generation rules +
		
		// scenario4Rule1
		scenario4Rule1 = new FrequencyDistributionGenerationRule(scenario4InhabitantPopGenerator, SCENARIO4_RULE1_NAME);
		scenario4Rule1.appendOutputAttribute(ageRangesAttr4);
		scenario4Rule1.appendOutputAttribute(sexAttr);
		scenario4Rule1.generateAttributeValuesFrequencies();
		scenario4InhabitantPopGenerator.appendGenerationRule(scenario4Rule1);
		
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		AttributeValue sexAttrValue;
		for (int[] range : Scenario4.age_ranges_4) {
			attributeValues.clear();
			
			attributeValues.put(ageRangesAttr4, new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
			sexAttrValue = new UniqueValue(DataType.BOOL, Boolean.toString(CanThoData.sexes[0]));
			attributeValues.put(sexAttr, sexAttrValue);
			
			// male
			scenario4Rule1.setFrequency(attributeValues, range[2]);
			
			// female
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(CanThoData.sexes[1])));
			scenario4Rule1.setFrequency(attributeValues, range[3]);
		}
		
		
		// scenario4Rule2
		scenario4Rule2 = new FrequencyDistributionGenerationRule(scenario4InhabitantPopGenerator, SCENARIO4_RULE2_NAME);
		scenario4Rule2.appendInputAttribute(sexAttr);
		scenario4Rule2.appendOutputAttribute(districtAttr);
		scenario4Rule2.generateAttributeValuesFrequencies();
		scenario4InhabitantPopGenerator.appendGenerationRule(scenario4Rule2);
		
		// set frequencies
		AttributeValue districtValue;
		int districtIndex = 0;
		for (int location[] : Scenario4.locations_4) {
			attributeValues.clear();
			
			districtValue = new UniqueValue(DataType.STRING, Scenario4.district_names[districtIndex]);
			attributeValues.put(districtAttr, districtValue);
			
			// male
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(CanThoData.sexes[0])));
			scenario4Rule2.setFrequency(attributeValues, location[0]);
			
			// female
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(CanThoData.sexes[1])));
			scenario4Rule2.setFrequency(attributeValues, location[1]);
		
			
			districtIndex++;
		}		 
		// create generation rules -
	}


	private void initializeScenario5Data() throws GenstarException {
		scenario5InhabitantPopGenerator = new SampleFreeGenerator("Scenario5's inhabitant population generator", 1188435);
		
		// create attributes +
		
		RangeValuesAttribute ageRangesAttr5 = new RangeValuesAttribute(scenario5InhabitantPopGenerator, "age", DataType.INTEGER, UniqueValue.class);
		for (int[] range : Scenario5.age_ranges_5) {
			ageRangesAttr5.add(new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
		}
		scenario5InhabitantPopGenerator.addAttribute(ageRangesAttr5);
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(scenario5InhabitantPopGenerator, "sex", DataType.BOOL);
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0])));
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1])));
		scenario5InhabitantPopGenerator.addAttribute(sexAttr);
		
		UniqueValuesAttribute districtAttr = new UniqueValuesAttribute(scenario5InhabitantPopGenerator, "district", DataType.STRING);
		for (String district : Scenario5.district_names) { districtAttr.add(new UniqueValue(DataType.STRING, district)); }
		scenario5InhabitantPopGenerator.addAttribute(districtAttr);
		
		// create attributes -
		
		
		// create generation rules +
		
		// scenario5Rule1
		scenario5Rule1 = new FrequencyDistributionGenerationRule(scenario5InhabitantPopGenerator, SCENARIO5_RULE1_NAME);
		scenario5Rule1.appendOutputAttribute(sexAttr);
		scenario5Rule1.appendOutputAttribute(districtAttr);
		scenario5Rule1.generateAttributeValuesFrequencies();
		scenario5InhabitantPopGenerator.appendGenerationRule(scenario5Rule1);
		
		// set frequencies
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		AttributeValue districtValue;
		int districtIndex = 0;
		for (int location[] : Scenario5.locations_5) {
			attributeValues.clear();
			
			districtValue = new UniqueValue(DataType.STRING, Scenario5.district_names[districtIndex]);
			attributeValues.put(districtAttr, districtValue);
			
			// male
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(CanThoData.sexes[0])));
			scenario5Rule1.setFrequency(attributeValues, location[0]);
			
			// female
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(CanThoData.sexes[1])));
			scenario5Rule1.setFrequency(attributeValues, location[1]);
		
			
			districtIndex++;
		}		 
		
		
		// scenario5Rule2
		scenario5Rule2 = new FrequencyDistributionGenerationRule(scenario5InhabitantPopGenerator, SCENARIO5_RULE2_NAME);
		scenario5Rule2.appendInputAttribute(sexAttr);
		scenario5Rule2.appendOutputAttribute(ageRangesAttr5);
		scenario5Rule2.generateAttributeValuesFrequencies();
		scenario5InhabitantPopGenerator.appendGenerationRule(scenario5Rule2);
		
		AttributeValue sexAttrValue;
		for (int[] range : Scenario5.age_ranges_5) {
			attributeValues.clear();
			
			attributeValues.put(ageRangesAttr5, new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
			sexAttrValue = new UniqueValue(DataType.BOOL, Boolean.toString(CanThoData.sexes[0]));
			attributeValues.put(sexAttr, sexAttrValue);
			
			// male
			scenario5Rule2.setFrequency(attributeValues, range[2]);
			
			// female
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(CanThoData.sexes[1])));
			scenario5Rule2.setFrequency(attributeValues, range[3]);
		}
		
		// create generation rules -
	}
}
