package ummisco.genstar.metamodel;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ummisco.genstar.data.CanThoData;
import ummisco.genstar.data.CanThoData.Scenario4;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.sample_free.FrequencyDistributionGenerationRule;
import ummisco.genstar.util.GenstarFileUtils;

public class GenstarGeneratorTest_CanTho1 {
	
	
	private static final boolean[] sexes = { true, false };

	private static class InhabitantData {
		
		
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
		 * 	an age_ranges element = { min age, max age, male individuals, female individuals }
		 */
		static final int[][] age_ranges = {
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
		 * 	a locations element = { male individuals, female individuals }
		 */
		static final int[][] locations = {
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
	}

	
	public static final String ORDER1_GENERATOR_RULE1_NAME = "Inhabitant population by age and sex (Order 1 Generator)";

	public static final String ORDER1_GENERATOR_RULE2_NAME = "Inhabitant population by sex and district/location (Order 1 Generator)";

	public static final String ORDER2_GENERATOR_RULE1_NAME = "Inhabitant population by sex and district/location (Order 1 Generator)";

	public static final String ORDER2_GENERATOR_RULE2_NAME = "Inhabitant population by age and sex (Order 1 Generator)";
	
	
	private SampleFreeGenerator rulesOrder1InhabitantPopGenerator;
	private FrequencyDistributionGenerationRule order1Rule1, order1Rule2;
	
	private SampleFreeGenerator rulesOrder2InhabitantPopGenerator;
	private FrequencyDistributionGenerationRule order2Rule1, order2Rule2;
	
	
	// percentages of inhabitant population to generate
	private float[] percentages = { 0.01f, 0.05f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1, 1.25f, 1.5f, 1.75f, 2 };
	
	private int[] nbOfEntitiesToGenerate = new int[percentages.length];
	
	public GenstarGeneratorTest_CanTho1() throws GenstarException {
		for (int i=0; i<percentages.length; i++) {
			nbOfEntitiesToGenerate[i] = Math.round(1188435f * percentages[i]);
			
//			System.out.println(p + " percent of 1188435 = " + (1188435f * p) + 
//					"; with 'int' casting = " + Integer.toString( (int)(1188435f * p)) + 
//					"; and with Math.round = " + Math.round(1188435f * p));
		}
		
		initializeRulesOrder1Generator();
		initializeRulesOrder2Generator();
		
	}
	
	private void initializeRulesOrder1Generator() throws GenstarException {
		
		rulesOrder1InhabitantPopGenerator = new SampleFreeGenerator("Inhabitant population generator (Rules Order 1)", 1188435);
		
		// create attributes +
		
		RangeValuesAttribute ageRangesAttr = new RangeValuesAttribute(rulesOrder1InhabitantPopGenerator, "age", DataType.INTEGER, UniqueValue.class);
		for (int[] range : InhabitantData.age_ranges) {
			ageRangesAttr.add(new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
		}
		rulesOrder1InhabitantPopGenerator.addAttribute(ageRangesAttr);
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(rulesOrder1InhabitantPopGenerator, "sex", DataType.BOOL);
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0])));
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1])));
		rulesOrder1InhabitantPopGenerator.addAttribute(sexAttr);
		
		UniqueValuesAttribute districtAttr = new UniqueValuesAttribute(rulesOrder1InhabitantPopGenerator, "district", DataType.STRING);
		for (String district : InhabitantData.district_names) { districtAttr.add(new UniqueValue(DataType.STRING, district)); }
		rulesOrder1InhabitantPopGenerator.addAttribute(districtAttr);
		
		// create attributes -
		
		
		// create generation rules +
		
		// order1Rule1
		order1Rule1 = new FrequencyDistributionGenerationRule(rulesOrder1InhabitantPopGenerator, ORDER1_GENERATOR_RULE1_NAME);
		order1Rule1.appendOutputAttribute(ageRangesAttr);
		order1Rule1.appendOutputAttribute(sexAttr);
		order1Rule1.generateAttributeValuesFrequencies();
		rulesOrder1InhabitantPopGenerator.appendGenerationRule(order1Rule1);
		
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		AttributeValue sexAttrValue;
		for (int[] range : InhabitantData.age_ranges) {
			attributeValues.clear();
			
			attributeValues.put(ageRangesAttr, new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
			sexAttrValue = new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0]));
			attributeValues.put(sexAttr, sexAttrValue);
			
			// male
			order1Rule1.setFrequency(attributeValues, range[2]);
			
			// female
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1])));
			order1Rule1.setFrequency(attributeValues, range[3]);
		}
		
		
		// order1Rule2
		order1Rule2 = new FrequencyDistributionGenerationRule(rulesOrder1InhabitantPopGenerator, ORDER1_GENERATOR_RULE2_NAME);
		order1Rule2.appendInputAttribute(sexAttr);
		order1Rule2.appendOutputAttribute(districtAttr);
		order1Rule2.generateAttributeValuesFrequencies();
		rulesOrder1InhabitantPopGenerator.appendGenerationRule(order1Rule2);
		
		// set frequencies
		AttributeValue districtValue;
		int districtIndex = 0;
		for (int location[] : InhabitantData.locations) {
			attributeValues.clear();
			
			districtValue = new UniqueValue(DataType.STRING, Scenario4.district_names[districtIndex]);
			attributeValues.put(districtAttr, districtValue);
			
			// male
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0])));
			order1Rule2.setFrequency(attributeValues, location[0]);
			
			// female
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1])));
			order1Rule2.setFrequency(attributeValues, location[1]);
		
			
			districtIndex++;
		}		 
		// create generation rules -
		
	}
	
	
	private void initializeRulesOrder2Generator() throws GenstarException {
		
		rulesOrder2InhabitantPopGenerator = new SampleFreeGenerator("Inhabitant population generator (Rules Order 2)", 1188435);
		
		// create attributes +
		
		RangeValuesAttribute ageRangesAttr = new RangeValuesAttribute(rulesOrder2InhabitantPopGenerator, "age", DataType.INTEGER, UniqueValue.class);
		for (int[] range : InhabitantData.age_ranges) {
			ageRangesAttr.add(new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
		}
		rulesOrder2InhabitantPopGenerator.addAttribute(ageRangesAttr);
		
		UniqueValuesAttribute sexAttr = new UniqueValuesAttribute(rulesOrder2InhabitantPopGenerator, "sex", DataType.BOOL);
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0])));
		sexAttr.add(new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1])));
		rulesOrder2InhabitantPopGenerator.addAttribute(sexAttr);
		
		UniqueValuesAttribute districtAttr = new UniqueValuesAttribute(rulesOrder2InhabitantPopGenerator, "district", DataType.STRING);
		for (String district : InhabitantData.district_names) { districtAttr.add(new UniqueValue(DataType.STRING, district)); }
		rulesOrder2InhabitantPopGenerator.addAttribute(districtAttr);
		
		// create attributes -
		 
		
		// create generation rules +
		
		// order2Rule1
		order2Rule1 = new FrequencyDistributionGenerationRule(rulesOrder2InhabitantPopGenerator, ORDER2_GENERATOR_RULE1_NAME);
		order2Rule1.appendOutputAttribute(sexAttr);
		order2Rule1.appendOutputAttribute(districtAttr);
		order2Rule1.generateAttributeValuesFrequencies();
		rulesOrder2InhabitantPopGenerator.appendGenerationRule(order2Rule1);
		
		// set frequencies
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		AttributeValue districtValue;
		int districtIndex = 0;
		for (int location[] : InhabitantData.locations) {
			attributeValues.clear();
			
			districtValue = new UniqueValue(DataType.STRING, InhabitantData.district_names[districtIndex]);
			attributeValues.put(districtAttr, districtValue);
			
			// male
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0])));
			order2Rule1.setFrequency(attributeValues, location[0]);
			
			// female
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1])));
			order2Rule1.setFrequency(attributeValues, location[1]);
		
			
			districtIndex++;
		}		 
		
		
		// order2Rule2
		order2Rule2 = new FrequencyDistributionGenerationRule(rulesOrder2InhabitantPopGenerator, ORDER2_GENERATOR_RULE2_NAME);
		order2Rule2.appendInputAttribute(sexAttr);
		order2Rule2.appendOutputAttribute(ageRangesAttr);
		order2Rule2.generateAttributeValuesFrequencies();
		rulesOrder2InhabitantPopGenerator.appendGenerationRule(order2Rule2);
		
		AttributeValue sexAttrValue;
		for (int[] range : InhabitantData.age_ranges) {
			attributeValues.clear();
			
			attributeValues.put(ageRangesAttr, new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
			sexAttrValue = new UniqueValue(DataType.BOOL, Boolean.toString(sexes[0]));
			attributeValues.put(sexAttr, sexAttrValue);
			
			// male
			order2Rule2.setFrequency(attributeValues, range[2]);
			
			// female
			attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(sexes[1])));
			order2Rule2.setFrequency(attributeValues, range[3]);
		}
		
		// create generation rules -
		
	}
	

//	@Test public void testGenerateInhabitantPopulation_RulesOrder1() throws GenstarException {
	public void testGenerateInhabitantPopulation_RulesOrder1() throws GenstarException {
		
		System.out.println("Beginning testGenerateInhabitantPopulation_RulesOrder1");
		
		String basePath = "./Scenario1";
		File baseDir = new File(basePath);
		baseDir.mkdir();
		
		int times = 30;
		
		for (int percentageIndex=0; percentageIndex<percentages.length; percentageIndex++) {
			
			int nbOfEntities = nbOfEntitiesToGenerate[percentageIndex];
			String percentageName = Integer.toString( (int)(percentages[percentageIndex] * 100) ) + "Percent";
			
			System.out.println("Started generating " + Integer.toString( (int)(percentages[percentageIndex] * 100) ) + " percent of inhabitant population with " + nbOfEntities + " inhabitants.");
			
			// set name nbOfEntities for the generator
			rulesOrder1InhabitantPopGenerator.setNbOfEntities(nbOfEntities);
			
			String subFolderName = percentageName;
			File path = new File(basePath + "/" + subFolderName);
			path.mkdir();
			
			
			for (int currentIteration=0; currentIteration<times; currentIteration++) {
				
				System.out.println("Started iteration : " + currentIteration);

				int[][] generated_districts = new int[InhabitantData.locations.length][2];
				for (int rowIndex = 0; rowIndex < InhabitantData.locations.length; rowIndex++) {
					generated_districts[rowIndex][0] = generated_districts[rowIndex][1] = 0; 
				}

				int[][] age_ranges_copy = new int[InhabitantData.age_ranges.length][4];
				for (int rowIndex = 0; rowIndex < InhabitantData.age_ranges.length; rowIndex++) {
					for (int colIndex = 0; colIndex < InhabitantData.age_ranges[rowIndex].length; colIndex++) {
						age_ranges_copy[rowIndex][colIndex] = InhabitantData.age_ranges[rowIndex][colIndex];
					}
				}
				
				int[][] generated_age_ranges = new int[InhabitantData.age_ranges.length][4];
				for (int rowIndex = 0; rowIndex < InhabitantData.age_ranges.length; rowIndex++) {
					generated_age_ranges[rowIndex][0] = InhabitantData.age_ranges[rowIndex][0];
					generated_age_ranges[rowIndex][1] = InhabitantData.age_ranges[rowIndex][1];
					generated_age_ranges[rowIndex][2] = generated_age_ranges[rowIndex][3] = 0;
				}
				 
				
				IPopulation inhabitantPopulation = rulesOrder1InhabitantPopGenerator.generate();
				for (Entity inhabitant : inhabitantPopulation.getEntities()) {
					int age = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity())).getIntValue();
					boolean isMale = ( (UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("sex").getAttributeValueOnEntity()) ).getBooleanValue();

					for (int rowIndex = 0; rowIndex < age_ranges_copy.length; rowIndex++) {
						if (age >= age_ranges_copy[rowIndex][0] && age <= age_ranges_copy[rowIndex][1]) {
							if (isMale) {
								age_ranges_copy[rowIndex][2] = --age_ranges_copy[rowIndex][2];
								generated_age_ranges[rowIndex][2] = ++generated_age_ranges[rowIndex][2];
							} else {
								age_ranges_copy[rowIndex][3] = --age_ranges_copy[rowIndex][3];
								generated_age_ranges[rowIndex][3] = ++generated_age_ranges[rowIndex][3];
							}
							
							break;
						}
					}
					
					// district
					String district = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("district").getAttributeValueOnEntity())).getStringValue();
					int districtIndex = InhabitantData.getDistrictIndex(district);
					if (isMale) {
						generated_districts[districtIndex][0] = ++generated_districts[districtIndex][0];
					} else {
						generated_districts[districtIndex][1] = ++generated_districts[districtIndex][1];
					}
				}
				
//				int sumZero = 0;
//				for (int rowIndex = 0; rowIndex < age_ranges_copy.length; rowIndex++) {
//					sumZero += age_ranges_copy[rowIndex][2];
//					sumZero += age_ranges_copy[rowIndex][3];
//				}
//				assertTrue(sumZero == 0);
				 
				
				List<String[]> lines1 = new ArrayList<String[]>();
				String[] header1 = new String[3];
				header1[0] = "Age range";
				header1[1] = "Male";
				header1[2] = "Female";
				lines1.add(header1);
				
				String[] line;
				for (int rowIndex = 0; rowIndex < generated_age_ranges.length; rowIndex++) {
					line = new String[3];
					
					line[0] = new String("[" + generated_age_ranges[rowIndex][0] + " : " + generated_age_ranges[rowIndex][1] + "]");  // age range
					line[1] = new String(Integer.toString(generated_age_ranges[rowIndex][2])); // male
					line[2] = new String(Integer.toString(generated_age_ranges[rowIndex][3])); // female
		
					lines1.add(line);
				} 
				GenstarFileUtils.writeCSVFile(basePath + "/" + subFolderName + "/" + percentageName + "_age_sex_Iteration" + Integer.toString(currentIteration) + ".csv", lines1);
				 
				
				
				List<String[]> lines2 = new ArrayList<String[]>();
				String[] header2 = new String[3];
				header2[0] = "District";
				header2[1] = "Male";
				header2[2] = "Female";
				lines2.add(header2);
				
				for (int rowIndex = 0; rowIndex < generated_districts.length; rowIndex++) {
					line = new String[3];
					
					line[0] = new String(InhabitantData.district_names[rowIndex]);  // district
					line[1] = new String(Integer.toString(generated_districts[rowIndex][0])); // male
					line[2] = new String(Integer.toString(generated_districts[rowIndex][1])); // female
		
					lines2.add(line);
				} 
				GenstarFileUtils.writeCSVFile(basePath + "/" + subFolderName + "/" + percentageName + "_district_sex_Iteration" + Integer.toString(currentIteration) + ".csv", lines2);
				
				
				System.out.println("Finished iteration : " + currentIteration);
			}			
			
			
			
			System.out.println("Finished generating " + Integer.toString( (int)(percentages[percentageIndex] * 100) ) + " percent of inhabitant population with " + nbOfEntities + " inhabitants.");
			System.out.println("---------------\n");
		}		

		System.out.println("Finished testGenerateInhabitantPopulation_RulesOrder1");
		
	}
	
	@Test public void testGenerateInhabitantPopulation_RulesOrder2() throws GenstarException {
//	public void testGenerateInhabitantPopulation_RulesOrder2() throws GenstarException {
		System.out.println("Beginning testGenerateInhabitantPopulation_RulesOrder2");
		

		String basePath = "./Scenario2";
		File baseDir = new File(basePath);
		baseDir.mkdir();
		
		int times = 30;
		
		for (int percentageIndex=0; percentageIndex<percentages.length; percentageIndex++) {
			
			int nbOfEntities = nbOfEntitiesToGenerate[percentageIndex];
			String percentageName = Integer.toString( (int)(percentages[percentageIndex] * 100) ) + "Percent";
			
			System.out.println("Started generating " + Integer.toString( (int)(percentages[percentageIndex] * 100) ) + " percent of inhabitant population with " + nbOfEntities + " inhabitants.");
			
			// set name nbOfEntities for the generator
			rulesOrder2InhabitantPopGenerator.setNbOfEntities(nbOfEntities);
			
			// create folder
			String subFolderName = percentageName;
			File path = new File(basePath + "/" + subFolderName);
			path.mkdir();
			

			for (int currentIteration=0; currentIteration<times; currentIteration++) {
				
				System.out.println("Started iteration : " + currentIteration);

				int[][] generated_districts = new int[InhabitantData.locations.length][2];
				for (int rowIndex = 0; rowIndex < InhabitantData.locations.length; rowIndex++) {
					generated_districts[rowIndex][0] = generated_districts[rowIndex][1] = 0; 
				}

				int[][] age_ranges_copy = new int[InhabitantData.age_ranges.length][4];
				for (int rowIndex = 0; rowIndex < InhabitantData.age_ranges.length; rowIndex++) {
					for (int colIndex = 0; colIndex < InhabitantData.age_ranges[rowIndex].length; colIndex++) {
						age_ranges_copy[rowIndex][colIndex] = InhabitantData.age_ranges[rowIndex][colIndex];
					}
				}
				
				int[][] generated_age_ranges = new int[InhabitantData.age_ranges.length][4];
				for (int rowIndex = 0; rowIndex < InhabitantData.age_ranges.length; rowIndex++) {
					generated_age_ranges[rowIndex][0] = InhabitantData.age_ranges[rowIndex][0];
					generated_age_ranges[rowIndex][1] = InhabitantData.age_ranges[rowIndex][1];
					generated_age_ranges[rowIndex][2] = generated_age_ranges[rowIndex][3] = 0;
				}
				 
				
				IPopulation inhabitantPopulation = rulesOrder2InhabitantPopGenerator.generate();
				for (Entity inhabitant : inhabitantPopulation.getEntities()) {
					int age = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity())).getIntValue();
					boolean isMale = ( (UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("sex").getAttributeValueOnEntity()) ).getBooleanValue();

					for (int rowIndex = 0; rowIndex < age_ranges_copy.length; rowIndex++) {
						if (age >= age_ranges_copy[rowIndex][0] && age <= age_ranges_copy[rowIndex][1]) {
							if (isMale) {
								age_ranges_copy[rowIndex][2] = --age_ranges_copy[rowIndex][2];
								generated_age_ranges[rowIndex][2] = ++generated_age_ranges[rowIndex][2];
							} else {
								age_ranges_copy[rowIndex][3] = --age_ranges_copy[rowIndex][3];
								generated_age_ranges[rowIndex][3] = ++generated_age_ranges[rowIndex][3];
							}
							
							break;
						}
					}
					
					// district
					String district = ((UniqueValue) (inhabitant.getEntityAttributeValueByNameOnData("district").getAttributeValueOnEntity())).getStringValue();
					int districtIndex = InhabitantData.getDistrictIndex(district);
					if (isMale) {
						generated_districts[districtIndex][0] = ++generated_districts[districtIndex][0];
					} else {
						generated_districts[districtIndex][1] = ++generated_districts[districtIndex][1];
					}
				}
				
//				int sumZero = 0;
//				for (int rowIndex = 0; rowIndex < age_ranges_copy.length; rowIndex++) {
//					sumZero += age_ranges_copy[rowIndex][2];
//					sumZero += age_ranges_copy[rowIndex][3];
//				}
//				assertTrue(sumZero == 0);
				 
				
				List<String[]> lines1 = new ArrayList<String[]>();
				String[] header1 = new String[3];
				header1[0] = "Age range";
				header1[1] = "Male";
				header1[2] = "Female";
				lines1.add(header1);
				
				String[] line;
				for (int rowIndex = 0; rowIndex < generated_age_ranges.length; rowIndex++) {
					line = new String[3];
					
					line[0] = new String("[" + generated_age_ranges[rowIndex][0] + " : " + generated_age_ranges[rowIndex][1] + "]");  // age range
					line[1] = new String(Integer.toString(generated_age_ranges[rowIndex][2])); // male
					line[2] = new String(Integer.toString(generated_age_ranges[rowIndex][3])); // female
		
					lines1.add(line);
				} 
				GenstarFileUtils.writeCSVFile(basePath + "/" + subFolderName + "/" + percentageName + "_age_sex_Iteration" + Integer.toString(currentIteration) + ".csv", lines1);
				 
				
				
				List<String[]> lines2 = new ArrayList<String[]>();
				String[] header2 = new String[3];
				header2[0] = "District";
				header2[1] = "Male";
				header2[2] = "Female";
				lines2.add(header2);
				
				for (int rowIndex = 0; rowIndex < generated_districts.length; rowIndex++) {
					line = new String[3];
					
					line[0] = new String(InhabitantData.district_names[rowIndex]);  // district
					line[1] = new String(Integer.toString(generated_districts[rowIndex][0])); // male
					line[2] = new String(Integer.toString(generated_districts[rowIndex][1])); // female
		
					lines2.add(line);
				} 
				GenstarFileUtils.writeCSVFile(basePath + "/" + subFolderName + "/" + percentageName + "_district_sex_Iteration" + Integer.toString(currentIteration) + ".csv", lines2);
				
				
				System.out.println("Finished iteration : " + currentIteration);
			}			
			
			
			System.out.println("Finished generating " + Integer.toString( (int)(percentages[percentageIndex] * 100) ) + " percent of inhabitant population with " + nbOfEntities + " inhabitants.");
			System.out.println("---------------\n");
		}		

		System.out.println("Finished testGenerateInhabitantPopulation_RulesOrder2");
	}
}
