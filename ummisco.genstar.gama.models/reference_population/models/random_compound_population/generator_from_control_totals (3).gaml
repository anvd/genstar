/**
 *  generator_from_control_totals
 *  Author: voducan
 *  Description: 
 */

model generator_from_control_totals

import "../utils.gaml"

// TODO recode ID?

global {
	
	string base_folder <- '../../includes/random_compound_population/ipf_configuration/';
	string base_percentage_folder <- '_percent/';
	string ipf_configuration_base_name <- '_percent_ipf_configuration_';
	
	string slash <- '/';
	
	string point_properties <- '.properties';
	string point_csv <- '.csv';
	
	list<int> percentages <- [ 1, 5, 10, 30, 50 ];
	list<int> nb_controlled_attributes <- [ 2, 3 ];
	
	// parameters for saving populations +
	string group_population_name <- 'household';
	string component_population_name <- 'people';
	
	string group_attributes_csv_file_path <- '../../includes/random_compound_population/reference_population_configuration/household_attributes.csv';
	string component_attributes_csv_file_path <- '../../includes/random_compound_population/reference_population_configuration/people_attributes.csv';
//	string group_id_on_group <- 'householdID';
//	string group_id_on_component <- 'householdID';
	string group_id_on_group <- 'HouseholdID';
	string group_id_on_component <- 'HouseholdID';
	
	string group_population_output_base_file <- 'regenerate_household_from_';
	string component_population_output_base_file <- 'regenerate_people_from_';
	
	string percent_sample <- '_percent_sample.csv';
	// parameters for saving populations -
	
	init {
		
		loop percentage over: percentages {
			
			loop nb_controlled_attrs over: nb_controlled_attributes {
				
				string population_properties <- base_folder + percentage + base_percentage_folder 
					+ slash + percentage + ipf_configuration_base_name + nb_controlled_attrs + point_properties;
				list regenerated_populations <- ipf_compound_population(population_properties);
				 
				// write the generated populations to CSV files
				string group_population_output_file <- base_folder + percentage + base_percentage_folder 
				 	+ group_population_output_base_file + percentage + percent_sample;
				string component_population_output_file <- base_folder + percentage + base_percentage_folder 
				 	+ component_population_output_base_file + percentage + percent_sample;
				 
				do save_population(regenerated_populations, group_population_name, component_population_name,
					group_population_output_file, component_population_output_file, group_attributes_csv_file_path,
					component_attributes_csv_file_path, group_id_on_group, group_id_on_component);
				  
			}
		}
		
	}
}

experiment regenerate_population_from_control_totals_and_sample type: gui {
	output {
		
	}
}