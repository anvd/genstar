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
		

		list miro_people_population <- population_from_csv('../includes/population/People_Attributes.csv', '../includes/population/People_GenerationRules.csv', nb_of_people);
		create people from: miro_people_population {
			location <- any_location_in(one_of(building)); 
		}
		
		write 'Quick analyse of the generated people population:';
		write 'Number of people: ' + string(length(miro_people_population) - 1);
		
		write '\tClassification of gender: ';
		write '\t\tmale: ' + length(people where each.gender);
		write '\t\tfemale: ' + length(people where !each.gender);
		
		write '\tClassification of category: ';
		write '\t\tC0: ' + length(people where (each.category = 'C0'));
		write '\t\tC1: ' + length(people where (each.category = 'C1'));
		write '\t\tC2: ' + length(people where (each.category = 'C2'));
		write '\t\tC3: ' + length(people where (each.category = 'C3'));
		write '\t\tC4: ' + length(people where (each.category = 'C4'));
		write '\t\tC5: ' + length(people where (each.category = 'C5'));
		write '\t\tC6: ' + length(people where (each.category = 'C6'));
		write '\t\tC7: ' + length(people where (each.category = 'C7'));
		
		write '\tClassification of age:';
		write '\t\t[0:4]: ' + length(people where ((each.age >= 0) and (each.age <= 4)));
		write '\t\t[5:17]: ' + length(people where ((each.age >= 5) and (each.age <= 17)));
		write '\t\t[18:24]: ' + length(people where ((each.age >= 18) and (each.age <= 24)));
		write '\t\t[25:34]: ' + length(people where ((each.age >= 25) and (each.age <= 34)));
		write '\t\t[34:49]: ' + length(people where ((each.age >= 34) and (each.age <= 49)));
		write '\t\t[50:64]: ' + length(people where ((each.age >= 50) and (each.age <= 64)));
		write '\t\t[65:100]: ' + length(people where (each.age >= 65));
		
		// save [name, location, host] to: "save_data.csv" type: "csv";
//		ask people {
//			save [gender, category, age] to: "people_detail_data.csv" type: "csv";
//		}
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