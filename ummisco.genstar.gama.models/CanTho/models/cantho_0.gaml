/**
 *  cantho_0
 *  Author: voducan
 *  Description: 
 */

model cantho_0

global {
}

species household {
	int householdSize;
	int householdType;
	string livingPlace;
	
	list<people> my_members;
}

species people {
	bool gender;
	int age;
	string livingPlace;
	string district;
	
	household my_household;
}

experiment cantho_expr type: gui {
	output {
		
	}
}