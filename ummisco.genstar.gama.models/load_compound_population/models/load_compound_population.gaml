/**
 *  load_compound_population
 *  Author: voducan
 *  Description: An example of using the 'load_compound_population' operator to load a (synthetic) compound population saved in CSV files then create GAMA agents.
 */

model load_compound_population

global {
	string properties_file_path <- '../includes/CompoundPopulationProperties.properties';
	
	init {
		list loaded_compound_population <- load_compound_population(properties_file_path);
		genstar_create synthetic_population: loaded_compound_population;
		
		write 'Please open the \'../includes/group_sample.csv\', \'../includes/component_sample.csv\' and browse \'household\', \'people\' population to assess the loaded compound population';
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

experiment load_compound_population_experiment type: gui {
	
	output {}
}

