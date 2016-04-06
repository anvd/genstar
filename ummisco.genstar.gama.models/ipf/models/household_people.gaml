/**
 *  household_people
 *  Author: voducan
 *  Description: 
 */

model household_people

global {
	
	init {
		list household_population <- ipf_compound_population('../includes/household_people/ipf_configuration.properties');
		
		// analyze the generated population 
		write 'Analyze generation result ...';
		string attributesFilePath <- '../includes/household_people/group_attributes.csv';
		string controlledAttributesListFilePath <- '../includes/household_people/group_controlled_attributes.csv';
		string controlTotalsFilePath <- '../includes/household_people/group_control_totals.csv';
		list<int> analysisResult <- analyse_ipf_population_to_console(household_population, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath);
		// TODO analyse_ipf_compound_population_to_console 

		// create GAMA agents from genstar generated population
		genstar_create synthetic_population: household_population;

	}
}

species people {
	int age;
	bool gender;
	string work;
	int householdID;
	
	household my_household;
}

species household {
	int householdID;
	int householdSize;
	string householdIncome;
	string householdType;
	int numberOfCars;
	
	list<people> inhabitants;
}

experiment test type: gui {
	output {
		
	}
}