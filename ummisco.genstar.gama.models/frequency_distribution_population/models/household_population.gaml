/**
 *  household_population
 *  Author: voducan
 *  Description: An example of using 'frequency_distribution_population' operator (sample-free approach) to generate a synthetic population.
 */

model household_population

global {
	
	string experiment_name;
	string population_properties_file_path;
	
	init {
		write experiment_name + ' : generate household synthetic population defined in ' + population_properties_file_path;
		list household_population <- frequency_distribution_population(population_properties_file_path);
		
		if (experiment_name = 'experiment 4') {
			write 'Open CSV files in \'../includes/household_population/experiment_4/analysisResult\' folder to observe the analysis result';
		}

		write 'create GAMA agents from the synthetic population';		
		genstar_create synthetic_population: household_population; 
	}
}

species household {
	int householdSize;
	string householdIncome;
	int numberOfCars;
}

experiment experiment_1 type: gui {
	
	parameter 'Experiment name' var: experiment_name <- 'experiment 1';
	parameter 'Population properties file path' var: population_properties_file_path <- '../includes/household_population/experiment_1/HouseholdPopulation_Experiment_1.properties';
	
	output {
		
	}
}

experiment experiment_2 type: gui {
	
	parameter 'Experiment name' var: experiment_name <- 'experiment 2';
	parameter 'Population properties file path' var: population_properties_file_path <- '../includes/household_population/experiment_2/HouseholdPopulation_Experiment_2.properties';
	
	output {
		
	}
}

experiment experiment_3 type: gui {
	
	parameter 'Experiment name' var: experiment_name <- 'experiment 3';
	parameter 'Population properties file path' var: population_properties_file_path <- '../includes/household_population/experiment_3/HouseholdPopulation_Experiment_3.properties';
	
	output {
		
	}
}

experiment experiment_4 type: gui {
	
	parameter 'Experiment name' var: experiment_name <- 'experiment 4';
	parameter 'Population properties file path' var: population_properties_file_path <- '../includes/household_population/experiment_4/HouseholdPopulation_Experiment_4.properties';
	
	output {
		
	}
}