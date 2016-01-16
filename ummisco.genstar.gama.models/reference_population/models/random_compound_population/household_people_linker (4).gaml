/**
 *  household_people_linker
 *  Author: voducan
 *  Description: 
 */

model household_people_linker

global {
	
	init {
		string base_folder <- '../../includes/random_compound_population/frequency_distribution_configuration/';
		string household_population_properties_file_path <- 'household/scenario1/regenerated_population/RegeneratedPopulation.properties';
		string people_population_properties_file_path <- 'people/scenario1/regenerated_population/RegeneratedPopulation.properties';

		list household_population <- frequency_distribution_population(base_folder + household_population_properties_file_path);
		genstar_create synthetic_population: household_population returns: generated_households;
		
		list people_population <- frequency_distribution_population(base_folder + people_population_properties_file_path);
		genstar_create synthetic_population: people_population returns: generated_people;
		
		write 'Number of households: ' + length(household_population) + ', total householdSize: ' + sum(list(household) collect each.householdSize);
		write 'Number of people: ' + length(list(people));
		write 'Total member people in households before running the linker: ' + sum(list(household) collect length(each.member_people));
		
		list<list> populations_to_link <- [ generated_households, generated_people ];
		let linker_result <- link_populations('reference_pop_household_size_linker', populations_to_link);
		
		write 'Total member people in households after running the linker : ' + sum(list(household) collect length(each.member_people));
		 
	}
	
}

species household {
	
	int householdSize;
	int householdIncome;
	int householdType;
	int numberOfCars;
	
	list<people> member_people;
}

species people {
	int age;
	bool gender;
	string work;
	
}

experiment test_linker type: gui {
	output {
		
	}
}