/**
 *  find_region_IDs
 *  Author: voducan
 *  Description: 
 */

model find_region_IDs

/* Insert your model definition here */

global {
	matrix bordeaux_spatial_data <- matrix(csv_file('../../includes/population/Bordeaux_Spatial_Data.csv'));
	
	init {
		write remove_duplicates(bordeaux_spatial_data column_at 1);
	}
}

experiment find_region_IDs type: gui {
	output {
		
	}
}