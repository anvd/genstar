/**
 *  people
 *  Author: voducan
 *  Description: 
 */

model people

global {
	file shape_file_buildings <- file("../includes/gis/building.shp");
	file shape_file_roads <- file("../includes/gis/road.shp");
	file shape_file_bounds <- file("../includes/gis/bounds.shp");
	geometry shape <- envelope(shape_file_bounds);

	graph the_graph;

	int nb_of_people <- 14821;
	
	// input data
	matrix category_age_input_data <- csv_file('../includes/population/people/People_GenerationRule1_Data.csv');
	list category_age_input_frequency <- category_age_input_data column_at 2;
	matrix category_gender_input_data <- csv_file('../includes/population/people/People_GenerationRule2_Data.csv');
	list category_gender_input_frequency <- category_gender_input_data column_at 2;

	// generation result
	list<int> category_age_result;
	list<int> category_gender_result;
	
	init {
		create building from: shape_file_buildings with: [type::string(read ("NATURE"))] {
			if type="Industrial" {
				color <- #blue ;
			}
		}
		create road from: shape_file_roads ;
		

		list miro_people_population <- frequency_distribution_population('../includes/population/people/Population.properties');
		genstar_create synthetic_population: miro_people_population returns: created_people;
		ask created_people as: people {
			location <- any_location_in(one_of(building));
		}
		 
//		create people {
//			location <- any_location_in(one_of(building));
//		}
//		genstar_create synthetic_population: miro_people_population {
//			location <- any_location_in(one_of(building));
//		}
//		create people from: miro_people_population {
//			location <- any_location_in(one_of(building)); 
//		}
		
		write 'Quick analysis of the generated people population:';
		write 'Number of people: ' + string(length(miro_people_population) - 3);
		
		list<string> categories <- [ 'C0', 'C1', 'C2', 'C3', 'C4', 'C5', 'C6', 'C7' ];
		map<int, int> age_ranges <- [ 0::4, 5::17, 18::24, 25::34, 35::49, 50::64, 65::100 ];
		list<bool> genders <- [ true, false ];
		
		int category_age_index <- 0;
		write '\tClassification of category and age';
		write '\t\tCategory, Age, Input Frequency, Generated Frequency';
		loop ar_key over: age_ranges.keys {
			loop c over: categories {
				add length(people where ( (each.category = c) and (each.age >= ar_key ) and (each.age <= age_ranges[ar_key]) ) ) to: category_age_result; 
				
				write '\t\t' + c + ', [' + ar_key + '::' + age_ranges[ar_key] + '], ' + string(category_age_input_frequency at category_age_index) + ", "
					+ length(people where ( (each.category = c) and (each.age >= ar_key ) and (each.age <= age_ranges[ar_key]) ) );
				category_age_index <- category_age_index + 1;
			}
		}
		
		
		int category_index <- 0;
		write '\n\tClassfication of category and gender';
		write '\t\tCategory, Gender, Input Frequency, Generated Frequency';
		loop g over: genders {
			loop c over: categories {
				add length(people where ( (each.gender = g) and (each.category = c) )) to: category_gender_result;
				
				write '\t\t' + c + ", " + g + ", " + (category_gender_input_frequency at category_index) + ', '
					+ length (people where ( (each.gender = g) and (each.category = c) ));
				category_index <- category_index + 1;
			}
		}
		
		write 'category_age_result: ' + category_age_result;
		write 'category_gender_result: ' + category_gender_result;
	}
}

species building {
	string type; 
	rgb color <- #gray  ;
	
	aspect base {
		draw shape color: color ;
	}
}

species road  {
	float destruction_coeff <- 1 + ((rnd(100))/ 100.0) max: 2.0;
	int colorValue <- int(255*(destruction_coeff - 1)) update: int(255*(destruction_coeff - 1));
	rgb color <- rgb(min([255, colorValue]),max ([0, 255 - colorValue]),0)  update: rgb(min([255, colorValue]),max ([0, 255 - colorValue]),0) ;
	
	aspect base {
		draw shape color: color ;
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

experiment miro_people type: gui {
	output {
		display miro_display {
			species road aspect: base;
			species building aspect:base;
			species people aspect: base;
		}
		
		/*
		display ChartHisto {
			chart "DataBar" type:histogram
			{
				data "empty_ants" value:(list(ant) count (!each.hasFood)) color:°red;
				data "carry_food_ants" value:(list(ant) count (each.hasFood)) color:°green;				
			}
			
			}
		 */
		display generation_result {
			// TODO focus only on selected output
			// the rest should be "processed" by Numbers
			chart "Age statistic" type: histogram {
				//data "test" value: 100;
				
				// age_range: 0::4
//				data "0_4_data" value: int(category_age_input_frequency at 0) color: °red;
				data "0_4_data" value: 100 color: °red;
//				data "0_4_generated" value: category_age_result at 0 color: °green;
				
				// age_range: 5::17
				data "5_17_data" value: int(category_age_input_frequency at 1) color: °red;
//				data "5_17_generated" value: category_age_result at 1 color: °green;
								
				// age_range: 18::24
				data "18_24_data" value: int(category_age_input_frequency at 2) color: °red;
//				data "18_24_generated" value: category_age_result at 2 color: °green;
				
				// age_range: 25_34
				data "25_34_data" value: int(category_age_input_frequency at 3) color: °red;
//				data "25_34_generated" value: category_age_result at 3 color: °green;
				
				// age_range: 35::49
				data "35_49_data" value: int(category_age_input_frequency at 4) color: °red;
//				data "35_49_generated" value: category_age_result at 4 color: °green;
				
				// age_range: 50::64
				data "50_64_data" value: int(category_age_input_frequency at 5) color: °red;
//				data "50_64_generated" value: category_age_result at 5 color: °green;
				
				// age_range: 65::100
				data "65_100_data" value: int(category_age_input_frequency at 6) color: °red;
//				data "65_100_generated" value: category_age_result at 6 color: °green;
			} 
		}
	}
}