/**
 *  control_totals_generator
 *  Author: voducan
 *  Description: 
 */

model control_totals_generator

global {
	
	string base_path <- '../../includes/random_compound_population/ipf_configuration/';
	string controlled_attributes_base_folder <- '_controlled_attributes/';
	string control_totals_configuration_base_file <- 'control_totals_configuration_';
	string result_control_totals_base_fil <- 'controlled_totals_';
	
	string point_properties <- '.properties';
	string point_csv <- '.csv';
	string slash <- '/';
	
	list<int> nb_controlled_attributes <- [ 2, 3 ];
	
	init {
		// Genstars.generateControlTotals(scope, controlTotalPropertiesFilePath, dataSet2ControlTotalFilePath);
		loop nb_attrs over: nb_controlled_attributes {
			string control_totals_configuration_file <- base_path + nb_attrs + controlled_attributes_base_folder 
					+ slash + control_totals_configuration_base_file + nb_attrs + point_properties;
			string control_totals_file_path <- base_path + nb_attrs + controlled_attributes_base_folder
					+ slash + result_control_totals_base_fil + nb_attrs + point_csv;
					 
			file control_totals_file <- control_totals(control_totals_configuration_file, control_totals_file_path);
		}
	}
}

experiment generate_control_totals type: gui {
	output {
		
	}
}