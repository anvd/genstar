/**
 *  Author: voducan
 *  Description: 
 */

model household_with_people

global {
	
	int nb_of_people <- 14821;
	int nb_of_households <- 6420;

	init {
		list miro_household_population <- population_from_csv('../includes/population/Household_Attributes.csv', '../includes/population/Household_GenerationRules.csv', nb_of_households);
		create household from: miro_household_population returns: generated_households;
		
		list miro_people_population <- population_from_csv('../includes/population/People_Attributes.csv', '../includes/population/People_GenerationRules.csv', nb_of_people);
		create people from: miro_people_population returns: generated_people;
		
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