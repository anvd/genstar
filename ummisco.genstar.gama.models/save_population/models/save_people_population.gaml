/**
 *  save_people_population
 *  Author: voducan
 *  Description: This model illustrates how to save a (single) population to CSV file.
 */

model save_people_population

global {
	
	init {
		// if 'people_population.csv' exists then returns
		string people_population_file_path <- '../includes/people_population/people_population.csv';
		if (file_exists(people_population_file_path)) {
			write '\'people\' population already exists in ' + people_population_file_path + '. Please delete the file and re-run the experiment to see the effect';
		} else {

			// generate the 'people' population
			write 'generate the people population';
			string properties_file_path <- "../includes/people_population/RandomPeoplePopulationProperties.properties";
			list people_population <- random_population(properties_file_path);

		
			// save the 'household' population to CSV file
			write 'save the population to CSV file ' + people_population_file_path + '. Please open the file to observe the result';
			
			map<string, string> population_file_paths;
			put people_population_file_path at: 'people' in: population_file_paths;
			
			map<string, string> population_attributes_file_paths;
			string people_population_attributes_file_path <- '../includes/people_population/people_attributes.csv';
			put people_population_attributes_file_path at: 'people' in: population_attributes_file_paths;
			
			map<string, string> resulting_population_file_paths <- population_to_csv(people_population, population_file_paths, population_attributes_file_paths);
		}
	}
	 
}

experiment save_people_population_experiment type: gui {
	output {
		
	}
}