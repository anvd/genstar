/**
 *  save_household_population
 *  Author: voducan
 *  Description: This model illustrates how to save a (single) population to CSV file.
 */

model save_household_population

global {
	
	init {
		// if 'household_population.csv' exists then returns
		string household_population_file_path <- '../includes/household_population/household_population.csv';
		if (file_exists(household_population_file_path)) {
			write '\'household\' population already exists in ' + household_population_file_path + '. Please delete the file and re-run the experiment to see the effect';
		} else {

			// generate the 'household' population
			write 'generate the household population';
			string properties_file_path <- "../includes/household_population/RandomHouseholdPopulationProperties.properties";
			list household_population <- random_population(properties_file_path);

		
			// save the 'household' population to CSV file
			write 'save the population to CSV file ' + household_population_file_path + '. Please open the file to observe the result';
			
			map<string, string> population_file_paths;
			put household_population_file_path at: 'household' in: population_file_paths;
			
			map<string, string> population_attributes_file_paths;
			string household_population_attributes_file_path <- '../includes/household_population/household_attributes.csv';
			put household_population_attributes_file_path at: 'household' in: population_attributes_file_paths;
			
			map<string, string> resulting_population_file_paths <- population_to_csv(household_population, population_file_paths, population_attributes_file_paths);
		}
	}
}

experiment save_household_population_experiment type: gui {
	output {
		
	}
}