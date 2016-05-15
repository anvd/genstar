/**
 * An example of using the 'random_population' operator to generator a random (single) population.
 */

model generate_random_people_population

global {
	string properties_file_path <- "../includes/people_population/RandomPeoplePopulationProperties.properties";
	
	list genstar_people_population <- random_population(properties_file_path);
	
	init {
		genstar_create synthetic_population: genstar_people_population;
		
		write 'Please browse the \'people\' populations to observe the generation result.';
	}
	
}

species people {
	int age;
	bool gender;
	string work;
	int householdID;
}

experiment generate_random_people_population_experiment type: gui {
	output {
		
	}
}
