/**
 *  Author: voducan
 *  Description: 
 */

model household

global {
	int nb_of_households <- 6420;
	
	init {
		list miro_household_population <- population_from_csv('../includes/population/Household_Attributes.csv', '../includes/population/Household_GenerationRules.csv', nb_of_households);
		create household from: miro_household_population;
		
		write 'Quick analyse of the generated household population:';
		write 'Number of households: ' + string(length(miro_household_population) - 1);

		write '\tClassification of household size: ';
		loop size from: 1 to: 9 {
			write '\t\tNumber of household with size = ' + size + ' is ' + length(household where (each.householdSize = size));
		}
		
		
	}

}

species household {
	int householdSize;
}

experiment miro_household type: gui {
	output {
		
	}
}