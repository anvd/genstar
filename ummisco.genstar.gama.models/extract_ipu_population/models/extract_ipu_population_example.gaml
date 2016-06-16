/**
 *  extract_ipu_population_example
 *  Author: voducan
 *  Description: 
 */

model extract_ipu_population_example

global {
	
	string extracted_population_properties; 
	
	init {
		// 0. generate a Gen* compound population (household - people) if necessary
		string group_population_data_file_path <- '../includes/generated_population/household_population.csv';
		
		if (!file_exists(group_population_data_file_path)) {
			string properties_file_path <- '../includes/RandomCompoundPopulationProperties.properties';
			
			list genstar_household_people_compound_population <- random_compound_population(properties_file_path);

			
			// 1 write genstar_household_people_compound_population to CSV files
			map<string, string> populationOutputFilePaths;
			put '../includes/generated_people_population.csv' at: 'people' in: populationOutputFilePaths;
			put '../includes/generated_household_population.csv' at: 'household' in: populationOutputFilePaths;
			
			map<string, string> populationAttributesFilePaths;
			put '../includes/people_attributes.csv' at: 'people' in: populationAttributesFilePaths;
			put '../includes/household_attributes.csv' at: 'household' in: populationAttributesFilePaths;
			
			map<string, string> results <- population_to_csv(genstar_household_people_compound_population, populationOutputFilePaths, populationAttributesFilePaths);
			
			loop population_name over: results.keys {
				write 'write ' + population_name + ' population to ' + (results at population_name);
			}
		}


		// 1. extract the generated Gen* population
		list extracted_population <- extract_ipu_population(extracted_population_properties);
		
		// 2. verify the extracted Gen* population
		
		// 3. create GAMA agents from the extracted Gen* population
		genstar_create synthetic_population: extracted_population;
		
		// TODO load_compound_population
		
		write 'To further assess the extracting result, open original population in ../includes/generated_household_population.csv and browse the \'household\' population';
	}
	
	
	action elaborate_extracting_result(list original_population, list extracted_population) {
		
	}
	
}

species household {
	int householdID;
	int householdSize;
	string householdIncome;
	string householdType;
	int numberOfCars;
	
	list<people> inhabitants;
}

species people {
	int age;
	bool gender;
	string work;
	int householdID;
	
	household my_household;
}

experiment extract_0_point_1_percent_ipu_population type: gui {
	parameter "extracted_population_properties" var: extracted_population_properties <- '../includes/ExtractedIpuPopulationProperties_ZeroPointOnePercent.properties'; 

	output {
	}
}

experiment extract_10_percents_ipu_population type: gui {
	parameter "extracted_population_properties" var: extracted_population_properties <- '../includes/ExtractedIpuPopulationProperties_TenPercents.properties'; 

	output {
		
	}
}

experiment extract_30_percents_ipu_population type: gui {
	parameter "extracted_population_properties" var: extracted_population_properties <- '../includes/ExtractedIpuPopulationProperties_ThirtyPercents.properties'; 

	output {
		
	}
}