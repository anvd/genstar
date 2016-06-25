/**
 *  setup
 *  Author: voducan
 *  Description: 
 * 		This model generates a reference population (of household-people) using the frequency distribution approach then writes the generated population to CSV files.
 * 		The reference population is then used as an input to evaluate the populations generated using frequency distribution, ipf and ipu approaches. 		
 */

model setup

global {
	
	init {
		write 'Generate reference populations ... ';
		
		
		// generate the reference population
		string properties_file_path <- '../includes/setup/RandomCompoundPopulationProperties.properties';
		list genstar_household_people_compound_population <- random_compound_population(properties_file_path);
		
		
		// write the reference population to CSV files		
		string household_population_file_path <- '../includes/setup/household_population.csv';
		string people_population_file_path <- '../includes/setup/people_population.csv';
		map<string, string> population_file_paths;
		put household_population_file_path at: 'household' in: population_file_paths;
		put people_population_file_path at: 'people' in: population_file_paths;
		
		map<string, string> population_attributes_file_paths;
		string household_population_attributes_file_path <- '../includes/setup/household_attributes.csv';
		string people_population_attributes_file_path <- '../includes/setup/people_attributes.csv';
		put household_population_attributes_file_path at: 'household' in: population_attributes_file_paths;
		put people_population_attributes_file_path at: 'people' in: population_attributes_file_paths;
		
		
//		if (self frequency_distribution_files_exist()) {
//			write 'Please delete all the generated frequency distribution files before proceeding with the generation of the reference population';
//		} else { // generate the reference population
			map<string, string> resulting_population_file_paths <- population_to_csv(genstar_household_people_compound_population, population_file_paths, population_attributes_file_paths);
			write 'Save generated populations to ' + household_population_attributes_file_path + ' and ' + people_population_attributes_file_path;
//		}
	}


	/*
	bool frequency_distribution_files_exist {
		bool distribution_files_exist <- false;
		
		string base_path <- '../includes/frequency_distribution_evaluation/generated_frequency_distributions/';
		
		// scenario 1's frequency distribution files
		string scenario_1_base_path <- base_path + 'scenario_1/';
		list<string> scenario_1_distribution_files <- [ scenario_1_base_path + 'generated_household_distribution.csv' ];

		// scenario 2's frequency distribution files
		string scenario_2_base_path <- base_path + 'scenario_2/';
		list<string> scenario_2_distribution_files <- [ scenario_2_base_path + 'generated_household_distribution1.csv',
			scenario_2_base_path + 'generated_household_distribution2.csv' ];
		
		// scenario 3's frequency distribution files
		string scenario_3_base_path <- base_path + 'scenario_3/';
		list<string> scenario_3_distribution_files <- [ scenario_3_base_path + 'generated_household_distribution1.csv',
			scenario_3_base_path + 'generated_household_distribution2.csv', scenario_3_base_path + 'generated_household_distribution3.csv'];
			
			
		list<string> distribution_file_paths;
		add scenario_1_distribution_files all: true to: distribution_file_paths;
		add scenario_2_distribution_files all: true to: distribution_file_paths;
		add scenario_3_distribution_files all: true to: distribution_file_paths;
		
		
		loop i from: 0 to: length(distribution_file_paths) - 1 {
			if (file_exists(distribution_file_paths[i])) {
				distribution_files_exist <- true;
				write '\'' + distribution_file_paths[i] + '\' already existed';
			}
		}		
		
		return distribution_files_exist;
	}
	*/
}

experiment setup type: gui {
	output {
		
	}
}