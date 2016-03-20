/**
 *  household
 *  Author: voducan
 *  Description: 
 */

model household_6

global {
	
	init {
	 	write 'Start generating the population ... ';
	 	float begining_time <- machine_time;
		list household_population <- ipf_population('../includes/household_population/ipf_configuration.properties');
		write 'Finished generating the population. Duration: ' + (machine_time - begining_time) + ' miliseconds';
		
		// analyze the generated population 
		string attributesFilePath <- '../includes/household_population/attributes.csv';
		string controlledAttributesListFilePath <- '../includes/household_population/controlled_attributes_list.csv';
		string controlTotalsFilePath <- '../includes/household_population/control_totals.csv';
		bool writeResultToConsole <- true;
		list<int> analysisResult <- analyse_ipf_population(household_population, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath, writeResultToConsole);
	}
}

species household {
	 int householdSize;
	 string householdIncome;
	 string householdType;
	 int numberOfCars;
	 int numberOfBicycles;
	 int averageAge;
}

experiment household_6_population_expr type: gui {
	output {
		
	}
}