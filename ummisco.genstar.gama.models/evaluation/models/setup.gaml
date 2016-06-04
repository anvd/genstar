/**
 *  setup
 *  Author: voducan
 *  Description: 
 * 		This model generates a reference population (of household-people) using the frequency distribution approach then writes the generated population to CSV files.
 * 		The reference population is used as an input to evaluate the populations generated using frequency distribution, ipf and ipu approaches. 		
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
		
		map<string, string> resulting_population_file_paths <- population_to_csv(genstar_household_people_compound_population, population_file_paths, population_attributes_file_paths);
		
		
		write 'Save generated populations to ' + household_population_attributes_file_path + ' and ' + people_population_attributes_file_path;
		 
	}
}

experiment generate_reference_population type: gui {
	output {
		
	}
}