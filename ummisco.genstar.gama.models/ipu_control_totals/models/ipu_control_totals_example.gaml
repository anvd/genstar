/**
 *  ipu_control_totals_example
 *  Author: voducan
 *  Description: This is an example of using the "ipu_control_totals" operator to generate Ipu control totals.
 * 				The generated control totals can be use as one of the inputs to generate the synthetic population using Ipu approach (i.e., ipu_population)
 */

model ipu_control_totals_example

global {
	string base_path <- '../includes/';
	list<string> ipu_control_totals_properties_file_paths <- [
		base_path + 'scenario_1/ipu_control_totals_1.properties',
		base_path + 'scenario_2/ipu_control_totals_2.properties',
		base_path + 'scenario_3/ipu_control_totals_3.properties'
	];
	
	init {
		
		write 'Started generating IPU control totals ...';
		loop i from: 0 to: (length(ipu_control_totals_properties_file_paths) - 1) {
			write '\tGenerating IPU control totals for ' + (ipu_control_totals_properties_file_paths at i);
			map<string,string> result <- ipu_control_totals(ipu_control_totals_properties_file_paths at i);
		}
		write 'Finished generating IPU control totals. Open the resulting IPU control totals file to observe the results.';
		
	}
}

experiment ipu_control_totals_generator type: gui {
	output {}
}
