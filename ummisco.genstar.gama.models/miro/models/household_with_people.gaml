/**
 *  Author: voducan
 *  Description: 
 */

model household_with_people

global {

	init {
		list miro_household_population <- frequency_distribution_population('../includes/population/household_with_people/Household_Population.properties');
		genstar_create synthetic_population: miro_household_population returns: generated_households;
		
		list miro_people_population <- frequency_distribution_population('../includes/population/household_with_people/People_Population.properties');
		genstar_create people synthetic_population: miro_people_population returns: generated_people;
		
		list<list> populations_to_link <- [ generated_households, generated_people ];
		let linker_result <- link_populations('miro_household_size_linker', populations_to_link);
		
		
		// TODO: open the "household" population browser and have a look at the "member" list to verify the linker result
		
	}
}

species people {
	
	string category;
	bool gender;
	int age;
	
	
	aspect base {
		draw circle(2) color: #yellow;
	}
}

species household {
	int householdSize;
	list<people> member_people;
}

experiment miro_household_with_people type: gui {
	output {
		
	}
}