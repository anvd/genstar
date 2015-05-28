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
	
	init {
		create building from: shape_file_buildings with: [type::string(read ("NATURE"))] {
			if type="Industrial" {
				color <- #blue ;
			}
		}
		create road from: shape_file_roads ;
		

		list miro_people_population <- population_from_csv('../includes/population/people/People_Attributes.csv', '../includes/population/people/People_GenerationRules.csv', nb_of_people);
		create people from: miro_people_population {
			location <- any_location_in(one_of(building)); 
		}
		
		write 'Quick analysis of the generated people population:';
		write 'Number of people: ' + string(length(miro_people_population) - 1);
		
		list<string> categories <- [ 'C0', 'C1', 'C2', 'C3', 'C4', 'C5', 'C6', 'C7' ];
		map<int, int> age_ranges <- [ 0::4, 5::17, 18::24, 25::34, 35::49, 50::64, 65::100 ];
		list<bool> genders <- [ true, false ];
		
		matrix category_age_input_data <- csv_file('../includes/population/people/People_GenerationRule1_Data.csv');
//		file category_age_input_data <- csv_file('../includes/population/people/People_GenerationRule1_Data.csv');

//		loop aRow over: rows_list(category_age_input_data) {
//			string str <- '';
//			loop st over: aRow { str <- str + st + ', '; }
//			
//			write str;
//		}

		list category_age_input_frequency <- category_age_input_data column_at 2;
		int category_age_index <- 0;
		write '\tClassification of category and age';
		write '\t\tCategory, Age, Input Frequency, Generated Frequency';
		loop ar_key over: age_ranges.keys {
			loop c over: categories {
				write '\t\t' + c + ', [' + ar_key + '::' + age_ranges[ar_key] + '], ' + string(category_age_input_frequency at category_age_index) + ", "
					+ length(people where ( (each.category = c) and (each.age >= ar_key ) and (each.age <= age_ranges[ar_key]) ) );
				category_age_index <- category_age_index + 1;
			}
		}
		
		
		matrix category_gender_input_data <- csv_file('../includes/population/people/People_GenerationRule2_Data.csv');
		list category_gender_input_frequency <- category_gender_input_data column_at 2;
		int category_index <- 0;
		write '\n\tClassfication of category and gender';
		write '\t\tCategory, Gender, Input Frequency, Generated Frequency';
		loop g over: genders {
			loop c over: categories {
				write '\t\t' + c + ", " + g + ", " + (category_gender_input_frequency at category_index) + ', '
					+ length (people where ( (each.gender = g) and (each.category = c) ));
				category_index <- category_index + 1;
			}
		}
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
	}
}