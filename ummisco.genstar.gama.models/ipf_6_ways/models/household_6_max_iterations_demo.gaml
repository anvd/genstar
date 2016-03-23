/**
 *  household
 *  Author: voducan
 *  Description: 
 */

model household_6

global {
	
	init {
	 	write 'Start generating the population (1 iteration)... ';
	 	float begining_time <- machine_time;
		list household_population_1iteration <- ipf_population('../includes/household_population/ipf_configuration_1iteration.properties');
		write 'Finished generating the population (1 iteration). Duration: ' + (machine_time - begining_time) + ' miliseconds';
		
		// analyze the generated population (1 iteration)
		string attributesFilePath <- '../includes/household_population/attributes.csv';
		string controlledAttributesListFilePath <- '../includes/household_population/controlled_attributes_list.csv';
		string controlTotalsFilePath <- '../includes/household_population/control_totals.csv';
		string outputFilePath_1iteration <- '../outputs/1iteration.csv';
		write 'Start writing analyzing result to ' + outputFilePath_1iteration;
		list<int> analysisResult <- analyse_ipf_population_to_file(household_population_1iteration, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath, outputFilePath_1iteration);
		write 'Finished writing analyzing result ';


	 	write 'Start generating the population (3 iterations)... ';
	 	begining_time <- machine_time;
		list household_population_3iterations <- ipf_population('../includes/household_population/ipf_configuration.properties');
		write 'Finished generating the population (3 iterations). Duration: ' + (machine_time - begining_time) + ' miliseconds';

		// analyze the generated population (3 iterations)
		string outputFilePath_3iterations <- '../outputs/3iterations.csv';
		write 'Start writing analyzing result to ' + outputFilePath_3iterations;
		analysisResult <- analyse_ipf_population_to_file(household_population_3iterations, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath, outputFilePath_3iterations);
		write 'Finished writing analyzing result ';


		
	 	write 'Start generating the population (10 iterations)... ';
	 	begining_time <- machine_time;
		list household_population_10iterations <- ipf_population('../includes/household_population/ipf_configuration_10iterations.properties');
		write 'Finished generating the population (10 iterations). Duration: ' + (machine_time - begining_time) + ' miliseconds';

		// analyze the generated population (10 iterations)
		string outputFilePath_10iterations <- '../outputs/10iterations.csv';
		write 'Start writing analyzing result to ' + outputFilePath_10iterations;
		analysisResult <- analyse_ipf_population_to_file(household_population_10iterations, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath, outputFilePath_10iterations);
		write 'Finished writing analyzing result ';
		 
	}
}

experiment household_6_population_expr type: gui {
	output {
		
	}
}