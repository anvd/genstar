/**
 *  urban_evolution_v3_genstar_v1
 *  Author: voducan
 *  Description: 
 */

model urban_evolution_v3_genstar_v1

/* Insert your model definition here */

global {
	file road_shapefile <- file("../includes/road.shp");
	geometry shape <- envelope(road_shapefile);
	string wc_bd <- "Working class House";
	string uc_bd <- "Upper class House";
	string app_bd <- "Appartment building";
	string indu_bd <- "Industrial zone";
	map<string, rgb> building_color <- map([wc_bd ::rgb(255,220,220),uc_bd ::rgb(255,0,0),app_bd ::rgb("blue"),indu_bd ::rgb("green")]);
	map<string, float> building_size <-map([wc_bd ::20.0,uc_bd ::30.0,app_bd ::50.0,indu_bd::100.0]);
	map<string, float> building_height <-map([wc_bd ::5.0,uc_bd ::8.0,app_bd ::20.0,indu_bd::7.0]);

	geometry city update: union(building collect (each.shape + 20.0));
	list<building_block> suburbs;
	graph road_network;
	init {
		create road from: road_shapefile;
		road_network <-  as_edge_graph(road);

		do building_block_creation;

		create popgen returns: my_generators;
		genstar genstar_agent <- my_generators at 0;

		// generate and create buildings then place buildings appropriately in the city
		create building from: genstar_agent query_genstar_population ( name :: 'Building population' ) {
			location <- any_location_in(city);
			shape <- circle(size);
		}
		
		// ask building_blocks do post_init
		ask list(building_block) { do post_init; }
	}
	
	action building_block_creation {
		geometry global_geom <- copy(shape);
		loop rd over: road {
			global_geom <- global_geom - (rd + 0.1);
		}
		loop g over: global_geom.geometries {
			if g != nil and g.area > 3000 {
				create building_block with: [shape::g];
			}
		}
		suburbs <- building_block where (each overlaps world.shape.contour);
		city <- union(building_block where !(each overlaps world.shape.contour));
	}	

	reflex road_building {
		ask suburbs where(each.density > 0.02) {
			do build_road;
		}
		road_network <- as_edge_graph(road);
	}
}

species popgen parent: genstar {}

species road {
	aspect geom {
		draw shape color: rgb("black");
	}
}

species building {
	string type;
	float height;
	float size;
	rgb color;
	
	aspect geom {
		draw shape color: color depth: height;
	}
}

species building_block {
	rgb color <- rgb(155 + rnd(100),155 + rnd(100),155 + rnd(100));
	list<building> buildings;
	list<road> roads;
	geometry empty_space; 
	map<string, bool> possible_construction <-  map([wc_bd ::true,uc_bd ::true,app_bd ::true,indu_bd ::true]);
	geometry space;
	float distance_to_city_center;
	float density;
	
	action post_init {
		buildings <- building overlapping self;
		do update_empty_space;
		roads <- road where (((each farthest_point_to location) distance_to self) < 1.0);
		density <- sum (buildings collect each.shape.area) / shape.area;
		
//		write name + ' in post_init with length(buildings) = ' + length(buildings) + '; sum (buildings collect each.shape.area) =' + (sum (buildings collect each.shape.area)) + '; density = ' + density;
		
		using topology(road_network) {
			distance_to_city_center <- location distance_to city.location;
		}
	}
	
	action update_empty_space {
		empty_space <- copy(shape); 
		loop bd over: buildings {
			empty_space <- empty_space - (bd +10.0);
		}
		list<geometry> geoms_to_keep <- empty_space.geometries where (each.area > 500);
		if (not empty(geoms_to_keep)) {
			empty_space <- geometry(geoms_to_keep);
		} else {
			empty_space <- nil;
		}
		
	}
	aspect geom {
		draw shape color: color;
		
	}
	reflex update_distance_to_center {
		using topology(road_network) {
			distance_to_city_center <- location distance_to city.location;
		}
	}
	reflex densification when: true in possible_construction.values{
		float coeff_density <- 1.0 + rnd(100) / 10000.0;
		if (distance_to_city_center < 100.0) {
			coeff_density <- coeff_density + 0.1;
		} else if (distance_to_city_center < 300.0) {
			coeff_density <- coeff_density + 0.05;
		} else if (distance_to_city_center < 500.0) {
			coeff_density <- coeff_density + 0.01;
		}
		list<building_block> neigbours_blocks <- building_block at_distance 20.0;
		list<building> neigbours_buildings <- neigbours_blocks accumulate (each.buildings);
		float density_neighbours <-  mean (neigbours_blocks collect each.density);
		
		float target_density <- max([density * coeff_density, density_neighbours /10.0]);
		loop while: density < target_density and true in possible_construction.values {
			int nb <- neigbours_buildings count (possible_construction[each.type] ) + (possible_construction.values count each);
			float val_wh <- possible_construction[wc_bd] ? (1 + neigbours_buildings count (each.type = wc_bd)) / nb : 0.0;
			float val_uh <- possible_construction[uc_bd] ? val_wh + (1 + neigbours_buildings count (each.type =uc_bd)) / nb: val_wh;
			float val_app <- possible_construction[app_bd] ? val_uh + (1 + neigbours_buildings count (each.type = app_bd)) / nb: val_uh;
			float val_iz <- possible_construction[indu_bd] ? val_app + (1 + neigbours_buildings count (each.type =indu_bd)) / nb: val_app;
			float rnb <- rnd(1000) / 1000;
			string type <- indu_bd;
			if (rnb < val_wh) {
				type <- wc_bd;
			} else if (rnb < val_uh) {
				type <- uc_bd;
			} else if (rnb < val_app) {
				type <- app_bd;
			} 
			do building_construction(type);
			if (true in possible_construction.values) {
				do update_empty_space;	
			}
			density <- sum (buildings collect each.shape.area) / shape.area;
		}
	}
	
	action building_construction(string type) {
		float size <- building_size[type];
		geometry new_building <- square(size);
		geometry space2 <- nil;
		space <- empty_space reduced_by size;
		if ((space != nil) and (space.area > 0.0)) {
			road closest_road <- road closest_to space;
			create building with:[type::type, shape:: new_building, location::(point((closest_road closest_points_with space)[1]))] {
				myself.buildings << self;
				color <- building_color[type];
				height <- building_height[type];
			}
		} else {
			possible_construction[type] <- false;
		}	
	}
	
	 action build_road {
	 	geometry possible_area <- ((city + 300.0) - (city + 100.0)) intersection empty_space;
	 	if (possible_area != nil) {
	 		point pt0 <- any_location_in(possible_area);
	 		road the_road <- road closest_to pt0;
	 		if (pt0 distance_to world.shape.contour < 20.0) {
	 			pt0 <- (world.shape.contour closest_points_with pt0)[0];
	 		} 
		 	geometry new_road_geom <- line ([pt0, point((the_road.shape closest_points_with pt0)[0])]);
		 	create road {
		 		shape <- new_road_geom;
		 		myself.roads << self;
		 		myself.shape <- myself.shape - (shape + 0.1);
		 	}
		 	if (length(shape.geometries) > 1) {
				loop g over: shape.geometries {
					if g != nil and g.area > 3000 {
						create building_block with: [shape::g];
					}
				}
				suburbs <- building_block where (each overlaps world.shape.contour);
				remove self from: suburbs;
				do die;
			} else {
		 		do update_empty_space;
			}
	 	}
	 }
}


experiment urban_evolution_genstar type: gui {
	output {
		display "map" type: opengl{
			species building_block aspect: geom;
			species road aspect: geom;
			species building aspect: geom;
		}
	}
}
