/**
 * An example of using the 'random_compound_population' operator to generate a compound synthetic population.
 * A compound synthetic population is a population in which there is a relationship between a group agent and several component agent.
 * In this example, 'household' agent is the group agent and 'people' agent is the component agent.
 */

model generate_random_compound_population

global {
	string properties_file_path;
	
	list genstar_household_people_compound_population <- random_compound_population(properties_file_path);
	
	init {
		genstar_create synthetic_population: genstar_household_people_compound_population;
		
		write 'Please browse the \'household\' and \'people\' populations to observe the generation result.';
	}
}

species household {
	int householdID;
	int householdSize;
	string householdIncome;
	string householdType;
	int numberOfCars;
	
	list<people> inhabitants;
}

species people {
	int age;
	bool gender;
	string work;
	int householdID;
	
	household my_household;
}

experiment generate_random_compound_population_experiment_1 type: gui {
	
	parameter "Properties file" var: properties_file_path <-  "../includes/RandomCompoundPopulationProperties1.properties";
	
	output {}
}

experiment generate_random_compound_population_experiment_2 type: gui {
	
	parameter "Properties file" var: properties_file_path <-  "../includes/RandomCompoundPopulationProperties2.properties";
	
	output {}
}
