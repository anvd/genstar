/**
 *  compound_population_generator
 *  Author: voducan
 *  Description: Generate a compound population (household - people) and save the generated population to CSV files
 */

model compound_population_generator

import "../utils.gaml"

global {

	init {
		// 1. generate the reference population
		float start_time <- machine_time;
		write 'Generation starts...';
		list generated_population <- generate_and_save_reference_population();
		write 'Generation finished. Duration: ' + (machine_time - start_time) + ' miliseconds.';
		
		// 2. extract samples from the reference population
		do extract_and_save_samples(generated_population);

	}
	
	list generate_and_save_reference_population {
		list reference_population <- random_compound_population('../../includes/random_compound_population/reference_population_configuration/ReferencePopulation.properties'); 
		
		// save the generated population
		// parameters config ...
		string groupPopulationName <- "household";
		string componentPopulationName <- "people";
		string groupPopulationOutputFile <- '../../includes/random_compound_population/generated_populations/generated_household_population.csv';
		string componentPopulationOutputFile <- '../../includes/random_compound_population/generated_populations/generated_people_population.csv';
		string groupAttributesCSVFilePath <- '../../includes/random_compound_population/reference_population_configuration/household_attributes.csv';
		string componentAttributesCSVFile <- '../../includes/random_compound_population/reference_population_configuration/people_attributes.csv';
		string groupIdOnGroup <- 'HouseholdID';
		string groupIdOnComponent <- 'HouseholdID';
		
		do save_population(reference_population, groupPopulationName, componentPopulationName,
			groupPopulationOutputFile, componentPopulationOutputFile, groupAttributesCSVFilePath,
			componentAttributesCSVFile, groupIdOnGroup, groupIdOnComponent);
		
		return reference_population;
	}
	

	// extract the samples from the generated reference population then save the samples to CSV files 
	action extract_and_save_samples(list generated_population) {
		string base_sample_folder <- '../../includes/random_compound_population/generated_populations/extracted_sample/';
		string sample_household_population_base_name <- '_household_population.csv';
		string sample_people_population_base_name <- '_people_population.csv';
		list<string> percent_names <- [ '1_percent', '5_percent', '10_percent', '30_percent', '50_percent' ];
		list<int> percentages <- [ 1, 5, 10, 30, 50 ];
		
		list population_header <- copy_between(generated_population, 0, 3);
		list core_population <- copy_between(generated_population, 3, length(generated_population));
		int core_population_size <- length(core_population);
		
		// parameters for saving...
		string groupPopulationName <- "household";
		string componentPopulationName <- "people";
		string groupPopulationOutputFile <- '';
		string componentPopulationOutputFile <- '';
		string groupAttributesCSVFilePath <- '../../includes/random_compound_population/reference_population_configuration/household_attributes.csv';
		string componentAttributesCSVFile <- '../../includes/random_compound_population/reference_population_configuration/people_attributes.csv';
		string groupIdOnGroup <- 'HouseholdID';
		string groupIdOnComponent <- 'HouseholdID';
		
		
		loop i from: 0 to: (length(percentages) - 1) step: 1 {
			string percent_name <- percent_names[i];
			int percentage <- percentages[i];
			int nbOfSampleEntity <- int(float(core_population_size) * float(percentage / 100));

			write 'Starts extracting ' + percentage + ' percents sample, ' + nbOfSampleEntity + ' entities out of ' + core_population_size;
			
			list sample_population <- [];
			add all: population_header to: sample_population;
			add all: (nbOfSampleEntity among core_population) to: sample_population;
			
			// parameters
			groupPopulationOutputFile <- base_sample_folder + percent_name + sample_household_population_base_name;
			componentPopulationOutputFile <- base_sample_folder + percent_name + sample_people_population_base_name;
			
			// write the sample population to CSV file
			do save_population(sample_population, groupPopulationName, componentPopulationName,
				groupPopulationOutputFile, componentPopulationOutputFile, groupAttributesCSVFilePath,
				componentAttributesCSVFile, groupIdOnGroup, groupIdOnComponent);
			 
			write 'Finished extracting ' + percentage + ' percents sample.';
		}
	}
}

experiment generate_compound_population type: gui {
	output {
		
	}
} 