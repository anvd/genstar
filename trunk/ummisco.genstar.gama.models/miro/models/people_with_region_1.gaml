/**
 *  Author: voducan
 *  Description: 
 */

model people_with_region_1

global {
	int nb_of_people <- 14821;
	file bordeaux_shape_file <- file('../includes/gis/bordeaux/Tirage_2010_L93_region.shp');
	file bordeaux_bounds <- file('../includes/gis/bordeaux/Bordeaux_Bounds.shp');
	
	geometry shape <- envelope(bordeaux_bounds);
	
	init {
		create region from: bordeaux_shape_file with: [ regionID::int(read("2010")) ];

		list miro_people_population <- population_from_csv('../includes/population/people_with_region_1/People_With_Region_Attributes.csv', '../includes/population/people_with_region_1/People_With_Region_GenerationRules.csv', nb_of_people);
		create people from: miro_people_population;
		
		// Analysis
		// 		criteria : Category & Zone
		list<string> categories <- [ 'C0', 'C1', 'C2', 'C3', 'C4', 'C5', 'C6', 'C7' ];
		list<int> zones <- [ // Total : 97 zones
			101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, // 20 zones
			121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, // 20 zones 
			141, 142, 143, 201, 202, 203, 204, 205, 301, 302, 303, 304, 305, 306, 307, 308, 309, 310, 311, 312, // 20 zones 
			313, 314, 401, 402, 403, 501, 502, 503, 504, 505, 506, 507, 508, 509, 510, 511, 512, 513, 514, 515, // 20 zones 
			516, 517, 518, 519, 601, 602, 603, 701, 801, 802, 803, 804, 805, 806, 901, 902, 903 // 17 zones
		];
		
		matrix spatial_input_data <- csv_file('../includes/population/people_with_region_1/Bordeaux_Spatial_Data.csv');
		list frequency_data <- spatial_input_data column_at 2;
		int data_index <- 1;
		write 'Category,Zone,Original Frequency, Generated Frequency';
		loop z over: zones {
			loop c over: categories {
				int nb_agents <- length(people where ((each.category = c) and (each.regionID = z)));
				write c + "," + z + "," + (frequency_data at data_index) + ","+ nb_agents;
				data_index <- data_index + 1;
			}
		}
		
		// TODO print results to graph?
	}
}

species region {
	int regionID;
	
	aspect default {
		draw shape color: rgb('green');
	}
}

species people {
	string category;
	bool gender;
	int age;
	int regionID;	
	
	init {
		// distribute people to the appropriate regionID/zone
//		region target_region <- one_of(region where (each.regionID = regionID));
//		if (target_region != nil) { location <- any_location_in(target_region.shape); }

	}

	aspect base {
		draw circle(2) color: #yellow;
	}
}

experiment people_with_zone type: gui {
	output {
		display region_display {
			species region aspect: default;
			species people aspect: base;
		}
	}
}