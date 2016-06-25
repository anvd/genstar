/**
 *  household_people
 *  Author: voducan
 *  Description: 
 */

model household_people

global {
	
	init {
		list household_population <- ipf_compound_population('../includes/household_people/ipf_configuration.properties');

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

experiment generate_ipf_population type: gui {
	output {
		
	}
}