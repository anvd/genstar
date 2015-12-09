/**
 *  people
 *  Author: voducan
 *  Description: 
 */

model people

global {
	
	init {
		int nb_of_people <- 270;
		list people_population <- list<unknown>(ipf_population('../includes/SampleData_GenerationRule_Config.properties'));
		create people from: people_population { 
			// location <- any_location_in(one_of(parcel where(each.landuse = 'Residential')));
		}
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