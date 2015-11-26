package ummisco.genstar.data;

import java.util.HashMap;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.IMultipleRulesGenerator;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.MultipleRulesGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;

public final class BondyData {
	
	
	// "Inhabitant" population +
	// Rule 1 : Inhabitant population by sex and age group
	/**
	 * Type : Frequency distribution
	 * Attributes : age, sex
	 * 
	 * Output attributes :
	 * 	age : enumeration of integer ranges
	 * 	sex : enumeration of boolean values (true : male, false : female)
	 * 
	 * Note :
	 * 	an age_ranges_1 element = { min age, max age, male individuals, female individuals }
	 */
	public static final int[][] age_ranges_1 = {
			{ 0, 2, 1374, 1418 },
			{ 3, 5, 1352, 1375 },
			{ 6, 10, 2076, 1879 },
			{ 11, 17, 2594, 2568 },
			{ 18, 24, 2680, 2529 },
			{ 25, 39, 5418, 5979 },
			{ 40, 54, 5149, 5591 },
			{ 55, 64, 2746, 2741 },
			{ 65, 79, 2052, 2208 },
			{ 80, 100, 579, 1220 },
	};
	

	public static final boolean[] sexes = { true, false };
	
	
	// Rule 2 : Inhabitants from 15 years old by sex, age and socio-profession category
	/**
	 * Type : Frequency distribution
	 * Attributes : sex, age, socio-profession category (pcs)
	 * 
	 * Input attributes :
	 * 	sex : enumeration of boolean values
	 * 	age : enumeration of integer ranges
	 * 
	 * Output attributes :
	 * 	pcs : enumeration of integers [1..8]
	 * 
	 * Note :
	 * 	element = { min age, max age, <male pcs>, <female pcs> }
	 */
	public static final int[][] age_ranges_2 = {
		{ 15, 19, 3, 3, 3, 21, 69, 129, 0, 1687, 		0, 13, 0, 34, 86, 11, 0, 1644 },
		{ 20, 24, 0, 10, 71, 331, 413, 438, 0, 657, 	0, 0, 41, 306, 699, 33, 0, 753 },
		{ 25, 39, 0, 293, 654, 1164, 1067, 1822, 7, 398, 0, 71, 397, 1361, 2496, 317, 6, 1338 },
		{ 40, 54, 7, 432, 505, 1010, 810, 2023, 27, 318, 0, 144, 342, 1007, 2508, 532, 39, 1015 },
		{ 55, 64, 3, 168, 172, 280, 242, 658, 913, 323, 	0, 43, 137, 286, 681, 152, 860, 590 },
		{ 65, 100, 0, 24, 16, 16, 51, 48, 5662, 254, 	0, 7, 0, 3, 41, 9, 3176, 206 }
	};
	
	public static final int[] pcs_values = { 1, 2, 3, 4, 5, 6, 7, 8 };
	
	
	// Rule 3 : Net hourly wage by socio-profession category of inhabitants
	/**
	 * Type : Attribute inference
	 * Attributes : Hourly net wage, pcs
	 * 
	 * Input attributes :
	 * 	pcs : enumeration of integers
	 * 
	 * Output attributes :
	 * 	hourly net wage : enumeration of double ranges
	 */
	public static final double[][] hourly_net_wages = {
		{ 1, 	8, 15 },
		{ 2,	12, 20 },
		{ 3,	18.7, 21.8 },
		{ 4,	12.9, 14.6 },
		{ 5,	8.9, 10.5 },
		{ 6,	8.4, 11.4 },
		{ 7,	6, 9 },
		{ 8,	5, 7 },
	};
	// "Inhabitant" population -

	
	// "Household" population +
	// Rule 4 : Household by size, sex and age of household's head
	/**
	 * Type : Frequency distribution
	 * Attributes : household size, sex of household head, age of household head
	 *  
	 *  Output attributes :
	 *  	size : household size
	 *  	sex : sex of household head
	 *  	age : age of household head
	 */
	public static final int[][] household_sizes = {
		{ 15, 19, 6, 4, 0, 0, 0, 0,					3, 0, 3, 0, 3, 0 },
		{ 20, 24, 44, 83, 34, 19, 9, 15, 			36, 36, 15, 0, 6, 0 },
		{ 25, 39, 753, 832, 832, 878, 448, 159, 	423, 324, 295, 101, 46, 22},
		{ 40, 54, 730, 632, 831, 1080, 810, 613,	534, 464, 413, 200, 101, 57 },
		{ 55, 64, 357, 813, 485, 403, 253, 165,		615, 205, 82, 28, 9, 3 },
		{ 65, 79, 253, 1031, 273, 95, 59, 58,		811, 130, 20, 5, 0, 0 },
		{ 80, 100, 134, 291, 47, 3, 3, 6, 			588, 93, 4, 0, 0, 0 }
	};
	
	// Rule 5 : Household by type and age of household's head
	/**
	 * Type : Frequency distribution
	 * Attributes : age of household head, household's type
	 * 
	 * Input attributes :
	 * 		age of household head
	 * 
	 * Output attribute :
	 * 		household type
	 */
	public static final int[][] household_types = {
		{ 15, 19, 1, 3, 6, 9 }, // { head age range, couple, mono-parental, colocation, one-person }
		{ 20, 24, 140, 48, 28, 80 },
		{ 25, 39, 2949, 766, 213, 1176 },
		{ 40, 54, 3683, 1323, 196, 1264 },
		{ 55, 64, 2034, 332, 79, 973 },
		{ 65, 79, 1469, 167, 35, 1064 },
		{ 80, 100, 335, 76, 36, 722 }
	};
	
	public static final int[] household_type_values = { 1, 2, 3, 4 };
	
	// Rule 6 : Housing by number of rooms and household size
	/**
	 * Type : Frequency distribution
	 * Attributes : household size, housing's number of rooms
	 * 
	 * Input attributes :
	 * 		household size
	 * 
	 * Output attributes :
	 * 		housing's number of rooms
	 */
	public static final int[][] housing_rooms = {
		{ 1, 741, 1923, 1565, 760, 210, 93 },
		{ 2, 179, 976, 1982, 1142, 494, 165 },
		{ 3, 62, 445, 1397, 852, 405, 165 },
		{ 4, 37, 173, 881, 948, 587, 184 },
		{ 5, 15, 84, 442, 597, 376, 135 },
		{ 6, 2, 27, 187, 372, 312, 197 }
	};
	
	// Rule 7 : Housing by number of rooms and area
	/**
	 * Type : Frequency distribution
	 * Attributes : housing's number of rooms, housing's area
	 * 
	 * Input attributes :
	 * 		housing's number of rooms
	 * 
	 * Output attributes :
	 * 		housing's area
	 */
	public static final int[][] housing_areas = {
		{ 1, 969, 68, 0 },
		{ 2, 1401, 2226, 0 },
		{ 3, 139, 6176, 138 },
		{ 4, 39, 4046, 658 },
		{ 5, 0, 1484, 900 },
		{ 6, 0, 249, 690 }
	};
	
	public static final int[][] housing_area_types = {
		{ 0, 39 }, 
		{ 40, 100 }, 
		{ 101, 200 } 
	};
	
	// Rule 8 : Number of housings by type 
	/**
	 * Type : Frequency distribution
	 * Attribute : housing type
	 * 
	 * Output attribute :
	 * 		housing type
	 */
	public static final int[] housing_types = { 6027, 13067, 118 };
	
	// "Household" population -
	

	private IMultipleRulesGenerator bondyInhabitantPopGenerator;
	private FrequencyDistributionGenerationRule generationRule1, generationRule2;
	private AttributeInferenceGenerationRule generationRule3;
	
	private IMultipleRulesGenerator bondyHouseholdPopGenerator;
	private FrequencyDistributionGenerationRule generationRule4, generationRule5,
		generationRule6, generationRule7, generationRule8; 

	public BondyData() throws GenstarException {
		createInhabitantPopGenerator();
		createHouseholdPopGenerator();
	}
	
	
	public static final String RULE1_NAME = "Total population by sex and age group";
	
	public static final String RULE2_NAME = "Population from 15 years old by sex, age and socio-profession category";
	
	public static final String RULE3_NAME = "Hourly net wage by socio-profession category";
	
	public static final String RULE4_NAME = "Household by size, sex and age of household's head";
	
	public static final String RULE5_NAME = "Household by type and age of household's head";
	
	public static final String RULE6_NAME = "Housing by number of rooms and household size";
	
	public static final String RULE7_NAME = "Housing by number of rooms and area";
	
	public static final String RULE8_NAME = "Number of housings by type";

	
	
	private void createInhabitantPopGenerator() throws GenstarException {

		bondyInhabitantPopGenerator = new MultipleRulesGenerator("Population of Bondy's Inhabitants", 51000);
		
		// create attributes +
		RangeValuesAttribute ageRangesAttr1 = new RangeValuesAttribute(bondyInhabitantPopGenerator, "age_range_1", "age", DataType.INTEGER, UniqueValue.class);
		for (int[] range : age_ranges_1) {
			ageRangesAttr1.add(new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
		}
		bondyInhabitantPopGenerator.addAttribute(ageRangesAttr1);
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(bondyInhabitantPopGenerator, "sex", DataType.BOOL);
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0])));
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1])));
		bondyInhabitantPopGenerator.addAttribute(sexAttr);
		
		
		RangeValuesAttribute ageRangesAttr2 = new RangeValuesAttribute(bondyInhabitantPopGenerator, "age_range_2", "age", DataType.INTEGER);
		for (int[] range : age_ranges_2) {
			ageRangesAttr2.add(new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
		}
		bondyInhabitantPopGenerator.addAttribute(ageRangesAttr2);
		
		UniqueValuesAttribute pcsAttr = new UniqueValuesAttribute(bondyInhabitantPopGenerator, "pcs", DataType.INTEGER);
		for (int v : pcs_values) { pcsAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(v))); }
		pcsAttr.setDefaultValue(new UniqueValue(DataType.INTEGER, "8"));
		bondyInhabitantPopGenerator.addAttribute(pcsAttr);
		
		RangeValuesAttribute hourlyNetWageAttr = new RangeValuesAttribute(bondyInhabitantPopGenerator, "hourlyNetWage", DataType.DOUBLE, RangeValue.class);
		for (double[] wage_range : hourly_net_wages) {
			hourlyNetWageAttr.add(new RangeValue(DataType.DOUBLE, Double.toString(wage_range[1]), Double.toString(wage_range[2])));
		}
		bondyInhabitantPopGenerator.addAttribute(hourlyNetWageAttr);
		// create attributes -

		
		// create generation rules +
		
		// rule 1
		generationRule1 = new FrequencyDistributionGenerationRule(bondyInhabitantPopGenerator, RULE1_NAME);
		generationRule1.appendOutputAttribute(ageRangesAttr1);
		generationRule1.appendOutputAttribute(sexAttr);
		
		generationRule1.generateAttributeValuesFrequencies();
		
		bondyInhabitantPopGenerator.appendGenerationRule(generationRule1);
		
		AbstractAttribute ageRangeAttrBiz = generationRule1.getAttribute("age_range_1");
		AbstractAttribute sexAttrBiz = generationRule1.getAttribute("sex");
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		AttributeValue sexAttrValueBiz;
		for (int[] range : BondyData.age_ranges_1) {
			attributeValues.clear();
			
			attributeValues.put(ageRangeAttrBiz, new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
			sexAttrValueBiz = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[1]));
			attributeValues.put(sexAttrBiz, sexAttrValueBiz);
			
			// male
			generationRule1.setFrequency(attributeValues, range[2]);
			
			// female
			attributeValues.put(sexAttrBiz, new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0])));
			generationRule1.setFrequency(attributeValues, range[3]);
		}
		
		
		// rule 2
		generationRule2 = new FrequencyDistributionGenerationRule(bondyInhabitantPopGenerator, RULE2_NAME);
		generationRule2.appendInputAttribute(ageRangesAttr2);
		generationRule2.appendInputAttribute(sexAttr);
		generationRule2.appendOutputAttribute(pcsAttr);
		
		generationRule2.generateAttributeValuesFrequencies();

		bondyInhabitantPopGenerator.appendGenerationRule(generationRule2);
		
		AbstractAttribute ageRangeAttrBiz1 = generationRule2.getAttribute("age_range_2");
		AbstractAttribute sexAttrBiz1 = generationRule2.getAttribute("sex");
		AbstractAttribute pcsAttrBiz1 = generationRule2.getAttribute("pcs");
		AttributeValue sexAttrBiz1Value, malePcsValue, femalePcsValue;
		for (int[] range : age_ranges_2) {
			attributeValues.clear();
			
			attributeValues.put(ageRangeAttrBiz1, new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
			
			// male pcs
			sexAttrBiz1Value = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[1]));
			attributeValues.put(sexAttrBiz1, sexAttrBiz1Value);
			for (int i=0; i<8; i++) {
				malePcsValue = new UniqueValue(DataType.INTEGER, Integer.toString(i+1));
				attributeValues.put(pcsAttrBiz1, malePcsValue);
				
				generationRule2.setFrequency(attributeValues, range[i+2]);
			}
			
			// female pcs
			attributeValues.put(sexAttrBiz1, new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0])));
			for (int i=0; i<8; i++) {
				femalePcsValue = new UniqueValue(DataType.INTEGER, Integer.toString(i+1));
				attributeValues.put(pcsAttrBiz1, femalePcsValue);
				
				generationRule2.setFrequency(attributeValues, range[i+2+8]);
			}
		}
		

		// rule 3
		generationRule3 = new AttributeInferenceGenerationRule(bondyInhabitantPopGenerator, RULE3_NAME, pcsAttr, hourlyNetWageAttr);
		bondyInhabitantPopGenerator.appendGenerationRule(generationRule3);
		
		Map<AttributeValue, AttributeValue> pcsInferenceData = new HashMap<AttributeValue, AttributeValue>();
		AttributeValue inferringValue, inferredValue;
		for (double[] net_wage : hourly_net_wages) {
			
			inferringValue = pcsAttr.getInstanceOfAttributeValue(new UniqueValue(DataType.INTEGER, Integer.toString((int) net_wage[0])));
			inferredValue = hourlyNetWageAttr.getInstanceOfAttributeValue(new RangeValue(DataType.DOUBLE, Double.toString(net_wage[1]), Double.toString(net_wage[2])));
			
			if (inferringValue == null || inferredValue == null) {
				throw new GenstarException("Some attribute values are not contained in either inferring attribute or inferred attribute");
			}
			
			pcsInferenceData.put(inferringValue, inferredValue);
		}
		generationRule3.setInferenceData(pcsInferenceData);
		
		// create generation rule -
	}
	
	private void createHouseholdPopGenerator() throws GenstarException {
		bondyHouseholdPopGenerator = new MultipleRulesGenerator("Population of Bondy's Households", 21000);
		
		// create attributes +
		// Rule 4 : Household by size, sex and age of household's head
		RangeValuesAttribute ageRangeAttr3 =  new RangeValuesAttribute(bondyHouseholdPopGenerator, "age_range_3", "age", DataType.INTEGER, UniqueValue.class);
		for (int[] range : household_sizes) {
			ageRangeAttr3.add(new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
		}
		bondyHouseholdPopGenerator.addAttribute(ageRangeAttr3);
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(bondyHouseholdPopGenerator, "sex", DataType.BOOL);
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0])));
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1])));
		bondyHouseholdPopGenerator.addAttribute(sexAttr);
		
		UniqueValuesAttribute householdSizeAttr = new UniqueValuesAttribute(bondyHouseholdPopGenerator, "size", DataType.INTEGER);
		for (int size = 1; size < 7; size++) {
			householdSizeAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(size)));
		}
		bondyHouseholdPopGenerator.addAttribute(householdSizeAttr);
		
		
		// Rule 5 : Household by type and age of household's head
		// reuse ageRangeAttr3
		UniqueValuesAttribute householdTypeAttr = new UniqueValuesAttribute(bondyHouseholdPopGenerator, "household_type", DataType.INTEGER);
		for (int hhType = 1; hhType <= 4; hhType++) {
			householdTypeAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(hhType)));
		}
		bondyHouseholdPopGenerator.addAttribute(householdTypeAttr);
		
		
		// Rule 6 : Housing by number of rooms and household size
		UniqueValuesAttribute roomsAttr = new UniqueValuesAttribute(bondyHouseholdPopGenerator, "rooms", DataType.INTEGER);
		for (int r=1; r<=6; r++) {
			roomsAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(r)));
		}
		bondyHouseholdPopGenerator.addAttribute(roomsAttr);
		
		// reuse householdSizeAttr
		
		
		// Rule 7 : Housing by number of rooms and area
		// reuse roomsAttr
		RangeValuesAttribute housingAreaAttr = new RangeValuesAttribute(bondyHouseholdPopGenerator, "area", DataType.INTEGER, UniqueValue.class);
		for (int[] area_range : housing_area_types) {
			housingAreaAttr.add(new RangeValue(DataType.INTEGER, Integer.toString(area_range[0]), Integer.toString(area_range[1])));
		}
		bondyHouseholdPopGenerator.addAttribute(housingAreaAttr);
		
		
		// Rule 8 : Number of housings by type
		UniqueValuesAttribute housingTypeAttr = new UniqueValuesAttribute(bondyHouseholdPopGenerator, "housing_type", DataType.INTEGER);
		for (int type=1; type<=3; type++) {
			housingTypeAttr.add(new UniqueValue(DataType.INTEGER, Integer.toString(type)));
		}
		bondyHouseholdPopGenerator.addAttribute(housingTypeAttr);
		// create attributes -
		
		
		// create generation rules +
		// Rule 4
		generationRule4 = new FrequencyDistributionGenerationRule(bondyHouseholdPopGenerator, RULE4_NAME);
		generationRule4.appendOutputAttribute(householdSizeAttr);
		generationRule4.appendOutputAttribute(sexAttr);
		generationRule4.appendOutputAttribute(ageRangeAttr3);

		generationRule4.generateAttributeValuesFrequencies();

		bondyHouseholdPopGenerator.appendGenerationRule(generationRule4);
		
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		UniqueValue sexValue;
		UniqueValue hhSizeValue;
		for (int[] sizes : household_sizes) {
			attributeValues.clear();
			
			attributeValues.put(ageRangeAttr3, new RangeValue(DataType.INTEGER, Integer.toString(sizes[0]), Integer.toString(sizes[1])));
			
			// male
			sexValue = new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0]));
			attributeValues.put(sexAttr, sexValue);
			for (int hhSize = 0; hhSize < 6; hhSize++) {
				hhSizeValue = new UniqueValue(DataType.INTEGER, Integer.toString(hhSize +1));
				attributeValues.put(householdSizeAttr, hhSizeValue);
				
				generationRule4.setFrequency(attributeValues, sizes[hhSize + 2]);
			}
			
			// female
			sexValue = new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1]));
			attributeValues.put(sexAttr, sexValue);
			for (int hhSize = 0; hhSize < 6; hhSize++) {
				hhSizeValue = new UniqueValue(DataType.INTEGER, Integer.toString(hhSize +1));
				attributeValues.put(householdSizeAttr, hhSizeValue);
				
				generationRule4.setFrequency(attributeValues, sizes[hhSize + 6 + 2]);
			}
		}
		
		
		// Rule 5 : Household by type and age of household's head
		generationRule5 = new FrequencyDistributionGenerationRule(bondyHouseholdPopGenerator, RULE5_NAME);
		generationRule5.appendInputAttribute(ageRangeAttr3);
		generationRule5.appendOutputAttribute(householdTypeAttr);

		generationRule5.generateAttributeValuesFrequencies();

		bondyHouseholdPopGenerator.appendGenerationRule(generationRule5);
		
		UniqueValue hhTypeValue;
		for (int[] types : household_types) {
			attributeValues.clear();
			
			attributeValues.put(ageRangeAttr3, new RangeValue(DataType.INTEGER, Integer.toString(types[0]), Integer.toString(types[1])));
			for (int typeIndex=2; typeIndex<types.length; typeIndex++) {
				hhTypeValue = new UniqueValue(DataType.INTEGER, Integer.toString(typeIndex - 1));
				attributeValues.put(householdTypeAttr, hhTypeValue);
				
				generationRule5.setFrequency(attributeValues, types[typeIndex]);
			}
		}
		

		// Rule 6 : Housing by number of rooms and household size
		generationRule6 = new FrequencyDistributionGenerationRule(bondyHouseholdPopGenerator, RULE6_NAME);
		generationRule6.appendInputAttribute(householdSizeAttr);
		generationRule6.appendOutputAttribute(roomsAttr);

		generationRule6.generateAttributeValuesFrequencies();

		bondyHouseholdPopGenerator.appendGenerationRule(generationRule6);
		
		UniqueValue roomAttrValue;
		for (int[] r : housing_rooms) {
			attributeValues.clear();
			
			attributeValues.put(householdSizeAttr, new UniqueValue(DataType.INTEGER, Integer.toString(r[0])));
			for (int i=1; i<r.length; i++) {
				roomAttrValue = new UniqueValue(DataType.INTEGER, Integer.toString(i));
				attributeValues.put(roomsAttr, roomAttrValue);
				
				generationRule6.setFrequency(attributeValues, r[i]);
			}
		}
		
		
		// Rule 7 : Housing by number of rooms and area
		generationRule7 = new FrequencyDistributionGenerationRule(bondyHouseholdPopGenerator, RULE7_NAME);
		generationRule7.appendInputAttribute(roomsAttr);
		generationRule7.appendOutputAttribute(housingAreaAttr);

		generationRule7.generateAttributeValuesFrequencies();

		bondyHouseholdPopGenerator.appendGenerationRule(generationRule7);
		
		RangeValue housingAreaTypeValue;
		int area_index;
		for (int[] areas : housing_areas) {
			attributeValues.clear();
			
			attributeValues.put(roomsAttr, new UniqueValue(DataType.INTEGER, Integer.toString(areas[0])));
			area_index = 1;
			for (int[] housing_area : housing_area_types) {
				housingAreaTypeValue = new RangeValue(DataType.INTEGER, Integer.toString(housing_area[0]), Integer.toString(housing_area[1]));
				attributeValues.put(housingAreaAttr, housingAreaTypeValue);
				
				generationRule7.setFrequency(attributeValues, areas[area_index]);
				area_index++;
			}
		}
		
		
		// Rule 8 : Number of housings by type
		generationRule8 = new FrequencyDistributionGenerationRule(bondyHouseholdPopGenerator, RULE8_NAME);
		generationRule8.appendOutputAttribute(housingTypeAttr);

		generationRule8.generateAttributeValuesFrequencies();

		bondyHouseholdPopGenerator.appendGenerationRule(generationRule8);

		for (int type=1; type<=3; type++) {
			attributeValues.clear();
			attributeValues.put(housingTypeAttr, new UniqueValue(DataType.INTEGER, Integer.toString(type)));
			generationRule8.setFrequency(attributeValues, housing_types[type-1]);
		}

		
		// create generation rule -
	}
	
	public ISyntheticPopulationGenerator getInhabitantPopGenerator() {
		return bondyInhabitantPopGenerator;
	}
	
	public IMultipleRulesGenerator getHouseholdPopGenerator() {
		return bondyHouseholdPopGenerator;
	}
	
	public GenerationRule getRule1() {
		return generationRule1;
	}
	
	public GenerationRule getRule2() {
		return generationRule2;
	}
	
	public GenerationRule getRule3() {
		return generationRule3;
	}
	
	public GenerationRule getRule4() {
		return generationRule4;
	}
	
	public GenerationRule getRule5() {
		return generationRule5;
	}
	
	public GenerationRule getRule6() {
		return generationRule6;
	}
	
	public GenerationRule getRule7() {
		return generationRule7;
	}
	
	public GenerationRule getRule8() {
		return generationRule8;
	}
}