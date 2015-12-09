/**
 *  household_people
 *  Author: voducan
 *  Description: 
 */

model household_people

global {
	
	init {
		list household_population <- ipf_population('../includes/household_people/ipf_configuration.properties');
		create household from: household_population;
		// TODO how to initialize "people"/component agents in GAMA?
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