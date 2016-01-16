/**
 *  frequency_distribution_generator
 *  Author: voducan
 *  Description: 
 */

model frequency_distribution_generator

import "../utils.gaml"

global {
	
	string attribute_data;
	string sample_data;
	string distribution_format;
	string resulting_distribution;

	init {
		do generate_household_frequencies; // household
		do generate_people_frequencies; // people
	}	
	
	action generate_household_frequencies {
		string base_folder <- '../../includes/random_compound_population/frequency_distribution_configuration/household/';
		string base_scenario_folder <- 'scenario';
		sample_data <- '../../includes/random_compound_population/generated_populations/generated_household_population.csv';
		
		string attributes_file <- 'household_attributes.csv';
		string distribution_format_file <- 'distributionFormat.csv';
		string base_resulting_distribution_file <- 'household_distribution_scenario';
		
		loop scenario_number from: 1 to: 3 step: 1 {
			attribute_data <- base_folder + base_scenario_folder + scenario_number + '/' + attributes_file;
			distribution_format <- base_folder + base_scenario_folder + scenario_number + '/' + distribution_format_file;
			resulting_distribution <- base_folder + base_scenario_folder + scenario_number + '/' + base_resulting_distribution_file + scenario_number + '.csv';
			
			do report_parameters;
			write '\nGeneration starts...';
			float starting_time <- machine_time;
			
			string distribution_result_file <- frequency_distribution_from_sample(attribute_data, sample_data, distribution_format, resulting_distribution);

			write 'Generation finishes. Duration: ' + (machine_time - starting_time) + ' miliseconds.';
		} 
	}
	
	action generate_people_frequencies {
		string base_folder <- '../../includes/random_compound_population/frequency_distribution_configuration/people/';
		string base_scenario_folder <- 'scenario';
		sample_data <- '../../includes/random_compound_population/generated_populations/generated_people_population.csv';
		
		string attributes_file <- 'people_attributes.csv';
		string distribution_format_file <- 'distributionFormat.csv';
		string base_resulting_distribution_file <- 'people_distribution_scenario';

		loop scenario_number from: 1 to: 3 step: 1 {
			attribute_data <- base_folder + base_scenario_folder + scenario_number + '/' + attributes_file;
			distribution_format <- base_folder + base_scenario_folder + scenario_number + '/' + distribution_format_file;
			resulting_distribution <- base_folder + base_scenario_folder + scenario_number + '/' + base_resulting_distribution_file + scenario_number + '.csv';
			
			do report_parameters;
			write '\nGeneration starts...';
			float starting_time <- machine_time;

			string distribution_result_file <- frequency_distribution_from_sample(attribute_data, sample_data, distribution_format, resulting_distribution);

			write 'Generation finishes. Duration: ' + (machine_time - starting_time) + ' miliseconds.';
		} 
	}
	
	action report_parameters {
		write 'Generate frequency distribution with the following parameters:';
		write '\tattribute_data: ' + attribute_data;
		write '\tsample_data: ' + sample_data;
		write '\tdistribution_format: ' + distribution_format;
		write '\tresulting_distribution: ' + resulting_distribution;
	}
}

experiment generate_frequency_distributions type: gui {
	output {
		
	}
}