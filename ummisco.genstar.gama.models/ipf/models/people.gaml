/**
 *  people
 *  Author: voducan
 *  Description: 
 */

model people

global {
	
	init {
		list people_population <- ipf_population('../includes/people/ipf_configuration.properties');
		create people from: people_population;
	}
}

species people {
	int age;
	bool gender;
	string work;
}

experiment test type: gui {
	output {
		
	}
}