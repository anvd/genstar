/**
 *  Author: voducan
 *  Description: 
 */

model household

global {
	int nb_of_households <- 6420;
	
	init {
		list miro_household_population <- frequency_distribution_population('../includes/population/household/Population.properties');
		genstar_create synthetic_population: miro_household_population;
		
		matrix input_data <- csv_file('../includes/population/household/Household_GenerationRule_Data.csv');
		list input_frequency <- input_data column_at 1;
		
		write 'Quick analysis of the generated household population:';
		write 'Number of households: ' + string(length(miro_household_population) - 1);

		write 'size, input frequency, generated frequency';
		int input_frequency_index <- 0;
		loop size from: 1 to: 9 {
			write "" + size + ", " + (input_frequency at input_frequency_index) + ", " + length(household where (each.householdSize = size));
			input_frequency_index <- input_frequency_index + 1;
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