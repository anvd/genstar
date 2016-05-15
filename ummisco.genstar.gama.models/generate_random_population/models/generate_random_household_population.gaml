/**
 * An example of using the 'random_population' operator to generator a random (single) population.
 */

model generate_random_people_population

global {
	string properties_file_path <- "../includes/household_population/RandomHouseholdPopulationProperties.properties";
	
	list genstar_household_population <- random_population(properties_file_path);
	
	init {
		genstar_create synthetic_population: genstar_household_population;
		
		write 'Please browse the \'household\' populations to observe the generation result.';
	}
	
}

species household {
	int householdID;
	int householdSize;
	string householdIncome;
	string householdType;
	int numberOfCars;
}

experiment generate_random_household_population_experiment type: gui {
	output {
		
	}
}
