/**
 *  save_compound_population
 *  Author: voducan
 *  Description: This model illustrates how to save a compound population to CSV files.
 */

model save_compound_population

global {
	
	init {
		string household_population_file_path <- '../includes/compound_population/household_population.csv';
		string people_population_file_path <- '../includes/compound_population/people_population.csv';

		if (file_exists(household_population_file_path) or file_exists(people_population_file_path)) {
			write '\'household\' and/or \'people\' population already exists in ' + household_population_file_path + ' and/or ' + people_population_file_path + '. Please delete the file(s) and re-run the experiment to see the effect';
		} else {
			// generate the compound population (household - people)
			string properties_file_path <- "../includes/compound_population/RandomCompoundPopulationProperties.properties";
			list genstar_household_people_compound_population <- random_compound_population(properties_file_path);
			
			
			// save the compound population
			write 'save the compound population';
			write 'save household population to ' + household_population_file_path;
			write 'save people population to ' + people_population_file_path;
			write 'Please open the files to see the effect';
			
			map<string, string> population_file_paths;
			put household_population_file_path at: 'household' in: population_file_paths;
			put people_population_file_path at: 'people' in: population_file_paths;
			
			map<string, string> population_attributes_file_paths;
			string household_population_attributes_file_path <- '../includes/compound_population/household_attributes.csv';
			string people_population_attributes_file_path <- '../includes/compound_population/people_attributes.csv';
			put household_population_attributes_file_path at: 'household' in: population_attributes_file_paths;
			put people_population_attributes_file_path at: 'people' in: population_attributes_file_paths;
			
			map<string, string> resulting_population_file_paths <- population_to_csv(genstar_household_people_compound_population, population_file_paths, population_attributes_file_paths);
		}
	}
}

experiment save_compound_population_experiment type: gui {
	output {
		
	}
}