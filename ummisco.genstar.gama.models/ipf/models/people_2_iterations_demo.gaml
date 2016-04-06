/**
 *  ipf_max_iterations_demo
 *  Author: voducan
 *  Description: 
 */

model people_2_max_iterations_demo

global {
	
	init {

		string attributesFilePath <- '../includes/people/attributes.csv';
		string controlledAttributesListFilePath <- '../includes/people/controlled_attributes.csv';
		string controlTotalsFilePath <- '../includes/people/controlled_totals.csv';
		string outputFilePath_1iteration <- '../outputs/people_2_iterations_demo/1iteration.csv';

	 	write 'Start generating \'people\' population (1 iteration) ... ';
	 	float begining_time <- machine_time;
		list people_population_1iteration <- ipf_population('../includes/people/ipf_configuration_1iteration.properties');
		write 'Finished generating the population (1 iteration). Duration: ' + (machine_time - begining_time) + ' miliseconds';

		// analyze the generated population 
		write 'Start writing analyzing result to ' + outputFilePath_1iteration;
		list<int> analysisResult <- analyse_ipf_population_to_file(people_population_1iteration, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath, outputFilePath_1iteration);
		write 'Finished writing analyzing result ';


		write '\nStart generating \'people\' population (10 iterations) ... ';
	 	begining_time <- machine_time;
		list people_population_10iterations <- ipf_population('../includes/people/ipf_configuration_10iterations.properties');
		write 'Finished generating the population (10 iterations). Duration: ' + (machine_time - begining_time) + ' miliseconds';

		// analyze the generated population 
		string outputFilePath_10iterations <- '../outputs/people_2_iterations_demo/10iterations.csv';
		write 'Start writing analyzing result to ' + outputFilePath_10iterations;
		analysisResult <- analyse_ipf_population_to_file(people_population_10iterations, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath, outputFilePath_10iterations);
		write 'Finished writing analyzing result ';
	}
}

experiment people_2_max_iterations_demo_expr type: gui {
	output {
		
	}
}