/**
 *  household_population_generator
 *  Author: voducan
 *  Description: 
 */

model household_population_generator

global {
	
	string population_properties;
	string population_name;
	string population_output_file;

	map<string, string> generatedCompoundPopulationFilePaths;
	map<string, string> populationAttributesFilePaths;
	string attributesCSVFilePath;

	
	init {
		do generate_population;
	}
	
	action generate_population {
		write 'Generate ' +  population_name + ' population';
		
		// generate the population
		float beginning_machine_time <- machine_time;
		write 'Generation starts...';
		list generated_population <- frequency_distribution_population(population_properties);
		float finishing_machine_time <- machine_time;
		write 'Generation finishes. Duration: ' + (finishing_machine_time - beginning_machine_time) + ' miliseconds.' ;

		
		// write the generated population to the CSV file
		put population_output_file at: population_name in: generatedCompoundPopulationFilePaths;
		put attributesCSVFilePath at: population_name in: populationAttributesFilePaths;

		write 'Start writing the generated population to CSV file';
		beginning_machine_time <- machine_time;
		map<string, string> resultCompoundFilePaths <- population_to_csv(generated_population, 
			generatedCompoundPopulationFilePaths, populationAttributesFilePaths);
		
		write 'Finished writing the generated population to CSV file. Duration: ' + (machine_time - beginning_machine_time) + ' miliseconds';
		write 'Open ' + population_output_file + ' to observe the generated populations';
		
		// TODO calculate the number of people to generate basing on the sum of "householdSize" attribute
		if (population_name = 'household') {
			int nbOfPeople <- 0;
			loop i from: 3 to: (length(generated_population) - 1) step: 1 {
				map generated_agent <- map(generated_population at i);
				nbOfPeople <- nbOfPeople + int( generated_agent at 'householdSize' );
			}
			
			write 'Number of people to generate: ' + nbOfPeople;
		}
	}
	
}

experiment household_generation type: gui {
	parameter 'Population properties' var: population_properties <- '../../includes/frequency_distribution_population/reference_population_configuration/household_population/Household_Population.properties';
	parameter 'Population name' var: population_name <- 'household';
	parameter 'Population output file' var: population_output_file <- '../../includes/frequency_distribution_population/generated_populations/generated_household_population.csv';
	parameter 'Attributes CSV file path' var: attributesCSVFilePath <- '../../includes/frequency_distribution_population/reference_population_configuration/household_population/household_attributes.csv';

	output {
		
	}
}

experiment people_generation type: gui {
	parameter 'Population properties' var: population_properties <- '../../includes/frequency_distribution_population/reference_population_configuration/people_population/People_Population.properties';
	parameter 'Population name' var: population_name <- 'people';
	parameter 'Population output file' var: population_output_file <- '../../includes/frequency_distribution_population/generated_populations/generated_people_population.csv';
	parameter 'Attributes CSV file path' var: attributesCSVFilePath <- '../../includes/frequency_distribution_population/reference_population_configuration/people_population/people_attributes.csv';
	
	output {
		
	}
}
