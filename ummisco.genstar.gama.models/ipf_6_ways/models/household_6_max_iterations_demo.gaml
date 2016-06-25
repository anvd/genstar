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


	 	write 'Start generating the population (3 iterations)... ';
	 	begining_time <- machine_time;
		list household_population_3iterations <- ipf_population('../includes/household_population/ipf_configuration.properties');
		write 'Finished generating the population (3 iterations). Duration: ' + (machine_time - begining_time) + ' miliseconds';



		
	 	write 'Start generating the population (10 iterations)... ';
	 	begining_time <- machine_time;
		list household_population_10iterations <- ipf_population('../includes/household_population/ipf_configuration_10iterations.properties');
		write 'Finished generating the population (10 iterations). Duration: ' + (machine_time - begining_time) + ' miliseconds';
	}
}

experiment household_6_population_expr type: gui {
	output {
		
	}
}