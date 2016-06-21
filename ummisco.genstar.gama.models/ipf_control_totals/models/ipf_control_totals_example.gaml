/**
 *  ipf_control_totals_example
 *  Author: voducan
 *  Description: This is an example of using the "ipf_control_totals" operator to generate Ipf control totals.
 * 				The generated control totals can be use as one of the inputs to generate the synthetic population using Ipf approach (i.e., ipf_population, ipf_compound_population)
 */

model ipf_control_totals_example

global {
	string control_totals_properties_file_path;
	string generated_control_totals_file_path;
	
	init {
		if (file_exists(generated_control_totals_file_path)) {
			write 'Generated Ipf control totals file already existed. Please delete \'' + generated_control_totals_file_path + '\' file before relaunching the experiment.';
		} else {
			string result_control_totals_file_path <- ipf_control_totals(control_totals_properties_file_path);
			write 'Generated Ipf control totals to \'' + result_control_totals_file_path + '\'. Please open the file to observe the result.';
		}
	}
}

experiment household_1_control_totals_generator type: gui {
	
	parameter 'Control totals properties file path' var: control_totals_properties_file_path <- '../includes/household_1/household_1_control_totals.properties';
	parameter 'Generated control totals file path' var: generated_control_totals_file_path <- '../includes/household_1/household_1_generated_control_totals.csv';
	
	output {}
}

experiment household_2_control_totals_generator type: gui {
	
	parameter 'Control totals properties file path' var: control_totals_properties_file_path <- '../includes/household_2/household_2_control_totals.properties';
	parameter 'Generated control totals file path' var: generated_control_totals_file_path <- '../includes/household_2/household_2_generated_control_totals.csv';

	output {}
}

experiment people_1_control_totals_generator type: gui {

	parameter 'Control totals properties file path' var: control_totals_properties_file_path <- '../includes/people_1/people_1_control_totals.properties';
	parameter 'Generated control totals file path' var: generated_control_totals_file_path <- '../includes/people_1/people_1_generated_control_totals.csv';
	
	output {}
}

experiment people_2_control_totals_generator type: gui {

	parameter 'Control totals properties file path' var: control_totals_properties_file_path <- '../includes/people_2/people_2_control_totals.properties';
	parameter 'Generated control totals file path' var: generated_control_totals_file_path <- '../includes/people_2/people_2_generated_control_totals.csv';

	output {}
}