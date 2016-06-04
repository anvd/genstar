/**
 *  generator_from_frequency_distribution
 *  Author: voducan
 *  Description: 
 */

model generator_from_frequency_distribution

import "../utils.gaml"

global {
	
	init {
		do regenerate_and_save_household_population;
		do regenerate_and_save_people_population;
	}
	
	action regenerate_and_save_household_population {
		string base_folder <- '../../includes/random_compound_population/frequency_distribution_configuration/household/scenario';
		string regenerated_file_path <- '/regenerated_population/';
		
		string properties_file_name <- 'RegeneratedPopulation.properties';
		
		// parameters for saving population +
		string groupPopulationName <- "household";
		string componentPopulationName <- nil;
		string population_output_partial_file_name <- 'regenerated_household_scenario';
		string componentPopulationOutputFile <- nil;
		
		string attributesCSVFilePath <- '/household_attributes.csv';
		
		string componentAttributesCSVFile <- nil;
		string groupIdOnGroup <- nil;
		string groupIdOnComponent <- nil;
		// parameters for saving population -
		
		// parameters for generating and saving frequency distribution +
		string base_resulting_distribution_file <- 'regenerated_household_distribution_scenario';
		// parameters for generating and saving frequency distribution -
		

		loop scenario_number from: 1 to: 3 step: 1 {
			write '(Re-)Generate the household population from the frequency distribution, scenario ' + scenario_number;
			list regenerated_household_population <- frequency_distribution_population(base_folder + scenario_number + regenerated_file_path + properties_file_name);
			
			// write the re-generated population to CSV File
			string groupAttributesCSVFilePath <- base_folder + scenario_number + attributesCSVFilePath;
			string groupPopulationOutputFile <- base_folder + scenario_number + regenerated_file_path + population_output_partial_file_name + scenario_number + '.csv';
			
			write 'Save the re-generated household population, scenario ' + scenario_number;
			do save_population(regenerated_household_population, groupPopulationName, componentPopulationName,
				groupPopulationOutputFile, componentPopulationOutputFile, groupAttributesCSVFilePath,
				componentAttributesCSVFile, groupIdOnGroup, groupIdOnComponent);
			
			// compute frequency distribution of the re-generated population
			write 'Compute the frequency distribution of the re-generated household population, scenario ' + scenario_number;
			string resulting_distribution <- base_folder + scenario_number + regenerated_file_path + base_resulting_distribution_file + scenario_number + '.csv';
			string distribution_format <- base_folder + scenario_number + '/distributionFormat.csv';
			string distribution_result_file <- frequency_distribution(groupAttributesCSVFilePath, groupPopulationOutputFile, distribution_format, resulting_distribution);
		}
	}
	
	action regenerate_and_save_people_population {
		string people_base_folder <- '../../includes/random_compound_population/frequency_distribution_configuration/people/scenario';
		string people_regenerated_file_path <- '/regenerated_population/';
		
		string properties_file_name <- 'RegeneratedPopulation.properties';
		
		// parameters for saving population +
		string groupPopulationName <- "people";
		string componentPopulationName <- nil;
		string population_output_partial_file_name <- 'regenerated_people_scenario';
		string componentPopulationOutputFile <- nil;
		
		string attributesCSVFilePath <- '/people_attributes.csv';
		
		string componentAttributesCSVFile <- nil;
		string groupIdOnGroup <- nil;
		string groupIdOnComponent <- nil;
		// parameters for saving population -
		
		// parameters for generating and saving frequency distribution +
		string base_resulting_distribution_file <- 'regenerated_people_distribution_scenario';
		// parameters for generating and saving frequency distribution -


		loop scenario_number from: 1 to: 3 step: 1 {
			write '(Re-)Generate the people population from the frequency distribution, scenario ' + scenario_number;
			list regenerated_people_population <- frequency_distribution_population(people_base_folder + scenario_number + people_regenerated_file_path + properties_file_name);
			
			// write the re-generated population to CSV File
			string groupAttributesCSVFilePath <- people_base_folder + scenario_number + attributesCSVFilePath;
			string groupPopulationOutputFile <- people_base_folder + scenario_number + people_regenerated_file_path + population_output_partial_file_name + scenario_number + '.csv';
			
			write 'Save the re-generated people population, scenario ' + scenario_number;
			do save_population(regenerated_people_population, groupPopulationName, componentPopulationName,
				groupPopulationOutputFile, componentPopulationOutputFile, groupAttributesCSVFilePath,
				componentAttributesCSVFile, groupIdOnGroup, groupIdOnComponent);
			
			// compute frequency distribution of the re-generated population
			write 'Compute the frequency distribution of the re-generated people population, scenario ' + scenario_number;
			string resulting_distribution <- people_base_folder + scenario_number + people_regenerated_file_path + base_resulting_distribution_file + scenario_number + '.csv';
			string distribution_format <- people_base_folder + scenario_number + '/distributionFormat.csv';
			string distribution_result_file <- frequency_distribution(groupAttributesCSVFilePath, groupPopulationOutputFile, distribution_format, resulting_distribution);
		}
	}
}

experiment regenerate_population_from_frequency_distribution type: gui {
	output {
		
	}
}