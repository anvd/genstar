/**
 *  ipf_max_iterations_demo
 *  Author: voducan
 *  Description: 
 */

model people_2_max_iterations_demo

global {
	
	init {
	 	write 'Start generating \'people\' population (1 iteration) ... ';
	 	float begining_time <- machine_time;
		list people_population_1iteration <- ipf_population('../includes/people/ipf_configuration_1iteration.properties');
		write 'Finished generating the population (1 iteration). Duration: ' + (machine_time - begining_time) + ' miliseconds';

		write '\nStart generating \'people\' population (10 iterations) ... ';
	 	begining_time <- machine_time;
		list people_population_10iterations <- ipf_population('../includes/people/ipf_configuration_10iterations.properties');
		write 'Finished generating the population (10 iterations). Duration: ' + (machine_time - begining_time) + ' miliseconds';
	}
}

experiment people_2_max_iterations_demo_expr type: gui {
	output {
		
	}
}