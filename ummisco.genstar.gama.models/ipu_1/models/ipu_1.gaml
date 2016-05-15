/**
 *  ipu_1
 *  Author: voducan
 *  Description: 
 */

model ipu_1


global {
	string ipu_population_properties <- '../includes/population/IpuPopulationProperties.properties';
	
	init {
		list household_people_population <- ipu_population(ipu_population_properties);
		
		genstar_create synthetic_population: household_people_population;
	}
}

species household {
	int householdID;
	string householdType;
	list<people> inhabitants;
}

species people {
	string personType;
	household my_household;
}

experiment ipu_1_expr type: gui {
	output {
		monitor generated_household_type1 value: length(household where (each.householdType = 'Household Type 1'));
		monitor control_total_household_type1 value: 35;
		
		monitor generated_household_type2 value: length(household where (each.householdType = 'Household Type 2'));
		monitor control_total_household_type2 value: 65;


		monitor generated_people_type1 value: length(people where (each.personType = 'Person Type 1'));
		monitor control_total_people_type1 value: 91;
		
		monitor generated_people_type2 value: length(people where (each.personType = 'Person Type 2'));
		monitor control_total_people_type2 value: 65;
		
		monitor generated_people_type3 value: length(people where (each.personType = 'Person Type 3'));
		monitor control_total_people_type3 value: 104;
		
	}
	
}