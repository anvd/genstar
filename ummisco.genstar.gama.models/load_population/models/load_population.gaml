/**
 *  load_population
 *  Author: voducan
 *  Description: An example of using the 'load_population' operator to load a (synthetic) population saved in CSV files then create GAMA agents.  
 */

model load_population

global {
	
	string properties_file_path <- '../includes/SinglePopulationProperties.properties';
	
	
	init {
		list loaded_single_population <- load_population(properties_file_path);
		genstar_create synthetic_population: loaded_single_population;
		
		write 'Please open the \'../includes/sample_data.csv\' and browse the \'household\' population to assess the loaded population';
	}
}

species household {
	int householdID;
	int householdSize;
	string householdIncome;
	string householdType;
	int numberOfCars;
}

experiment load_population_experiment type: gui {
	output {
		
	}
}