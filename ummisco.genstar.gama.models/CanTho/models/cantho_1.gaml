/**
 *  cantho_1
 *  Author: voducan
 *  Description: 
 */

model cantho_1

global {

	init {
		
		// household population
		float beginning_time <- machine_time;
		write 'Start generating household population...';
		list household_population <- frequency_distribution_population('../includes/population/household_population.properties');
		write 'Finished generating household population (' + (length(household_population) - 3) + ' entities). Duration: ' + (machine_time - beginning_time) + ' miliseconds\n';
		
		beginning_time <- machine_time;
		write 'Start creating household agents in GAMA';
		genstar_create synthetic_population: household_population returns: generated_households;
		write 'Finished creating household agents in GAMA. Duration: ' + (machine_time - beginning_time) + ' miliseconds.\n';
		
		// people population
		beginning_time <- machine_time;
		write 'Start generating people population...';
		list people_population <- frequency_distribution_population('../includes/population/people_population.properties');
		write 'Finished generating people population (' + (length(people_population) - 3) + ' entities). Duration: ' + (machine_time - beginning_time) + ' miliseconds\n';

		beginning_time <- machine_time;
		write 'Start creating people agents in GAMA';
		genstar_create synthetic_population: people_population returns: generated_people;
		write 'Finished creating people agents in GAMA. Duration: ' + (machine_time - beginning_time) + ' miliseconds.\n';
	}	
}

species household {
	int householdSize;
	int householdType;
	string livingPlace;
	
	list<people> my_members;
}

species people {
	bool gender;
	int age;
	string livingPlace;
	string district;
	
	household my_household;
}

experiment cantho_expr type: gui {
	output {
		
	}
}