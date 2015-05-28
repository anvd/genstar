/**
 *  model1
 *  Author: Piou
 *  Description: 
 */

model model1

import "classe/classe_parameters.gaml"
import "classe/classe_cloud_of_mosquitoes.gaml"
import "classe/classe_foyer.gaml"
import "classe/classe_package_eggs.gaml"
import "classe/classe_person.gaml"
import "classe/classe_point_destination.gaml"
import "classe/classe_parcel.gaml"
import "classe/classe_cultural_space.gaml"
import "classe/classe_market.gaml"

global {
	/** Insert the global definitions, variables and actions here */
	
	reflex dayToNight {
		//write sum(cloud_of_mosquitoes where(each.our_parcel!=nil and [market] inside self=self) collect (each.nb_memberS));
		if(isStart){
			write('====== Debut de la simmulation=======');
			//write('------Il est '+ (actual_hour/hour)+'h actuellement');
			isStart <- false;
		} 
		counter <- counter+1;
		counter_periodic <- counter_periodic+1;
		
		if(isDay and counter_periodic=day_duration){
			write "=================Fin Journee & Debut Soiree=====================";
			set isDay<-false;
			set counter_periodic<-0;
		}
		if(!isDay and counter_periodic=night_duration){
			//recuperation des nombres des personnes qui se deplacent le jour j-1
			//mosquito
			//
			//personnes qui se deplacent a l'exterieur
			person_S_moves_chart <- person_S_moves_chart+{nb_day,susceptible_persons_move};
			person_E_moves_chart <- person_E_moves_chart+{nb_day,exposed_persons_move};
			person_I_moves_chart <- person_I_moves_chart+{nb_day,infected_persons_move};
			
			write "=================Fin soiree & Debut journee=====================";
			set isDay<-true;
			set counter_periodic<-0;
			nb_day <- nb_day+1;
			
			write "--- "+nb_day+" eme jour de la simulation";
			susceptible_persons_move <-0;
			exposed_persons_move <-0;
			infected_persons_move <-0;
		}
	}
		
	reflex affichage_horloge{
		set actual_hour<- actual_hour+1;
		//50 correspond a 1 heure
		if(actual_hour=24*hour){
			set actual_hour<-0;
		}
		if((actual_hour mod hour)=0){
			float actual_hour_ <- (actual_hour/hour);
			write("----> il est "+actual_hour_+"h actuellement");
			if(actual_hour_=0.0){
				isMidNight <- true;
			}
			if(actual_hour_=2.0){
				isMidNight <- false;
			}
			if(actual_hour>=hour_early_go_work and actual_hour<=hour_latest_go_work){
				isTime_to_go_work <-true;
			}
			else{
				isTime_to_go_work <-false;
			} 
		}
	}

	//int nb_of_people <- 30349;
	int nb_of_people <- 14821;
	int nb_of_households <- 6420;

	init{
		create bondary from:environment_file;
			
		create river from:river_file;
		create all_river from:all_river_file; 
		create sewer from:sewer_file; 
		create mornitoring from: mornitoring_file;
		
		// LANDY
		create center from:center_file;
		create Parcel from: village_file with: [landuse::string(read('landuse_na')), area::float(read('area_m2')),identity::int(read('monid'))] {
			do set_color_risk_parcel;
		}
		create hospital from: hospital_file{
			if(flip(risk_in_other)){
				create cloud_of_mosquitoes number:1{
					location <- any_location_in(myself);
					nb_mosquito_susceptible <- nb_mosquito_susceptible+nb_memberS;
				}
				create stagnant_water number:1{
					location <- any_location_in(myself);
				}
			}
		}
		/*create culturalSpace from: cultural_file{
			if(flip(risk_in_public_space)){
				create cloud_of_mosquitoes number:1{
					location <- any_location_in(myself);
					nb_mosquito_susceptible <- nb_mosquito_susceptible+nb_memberS;
				}
				create stagnant_water number:1{
					location <- any_location_in(myself);
				}
			}
		}*/
		/*create market from: market_file{
			if(flip(risk_in_market)){
				create cloud_of_mosquitoes number:1{
					location <- any_location_in(myself);
					nb_mosquito_susceptible <- nb_mosquito_susceptible+nb_memberS;
				}
				create stagnant_water number:1{
					location <- any_location_in(myself);
				}
			}
		}*/
		//calcul de la surface moyenne des residences
		list listResidences <- list(Parcel where(each.landuse = "Residential"));
		
		//creation des points de destination à l'exterieur du village à étudier
/*		create pt_destination from: pt_destination_csv_file header: true with: [name_destination::string(read('quartiers')), identity_point::int(read('id')),
			type::string(read('type')),s_distance_between_source::string(read('Distance'))
		]{
			if(s_distance_between_source='NA'){//attribuer une valeur aleatoire pour la distance entre le quartier et le point de destination
				f_distance_between_source <- float(5000+rnd(10000));//minimum 5km
			}
			else{
				f_distance_between_source <- float(s_distance_between_source);
			}
			center centre_village <- one_of(list(center));//un petit point au centre du village
			point point_leader_destination <-nil;//point qui indique la position du point de destination
			ask(centre_village){
				list<point> list_point_around <- 1 points_at(myself.f_distance_between_source/scale_destination);//1 point qui se trouve a une distance D au alentour du centre
				point_leader_destination <- first(list_point_around);
			}
			 location <- point_leader_destination.location;
			 do identifyCategory_and_risk;//identifier la categorie du point de destination et son risque selon sa categorie
		} 
		*/
		
	// NOUVEAU - population
		list miro_people_population <- population_from_csv('../includes/population/people/People_Attributes.csv', '../includes/population/people/People_GenerationRules.csv', nb_of_people);
		create people from: miro_people_population {
			location <- any_location_in(one_of(Parcel where(each.landuse = "Economic activiity"))); 
		}
		
		write 'Quick analysis of the generated people population:';
		write 'Number of people: ' + string(length(miro_people_population) - 1);
		
		list<string> categories <- [ 'C0', 'C1', 'C2', 'C3', 'C4', 'C5', 'C6', 'C7' ];
		//list<string> categories <- [ 'Agri', 'Fish', 'Miming', 'Manufac', 'Elect', 'Const', 'Trade', 'Storage', 'Other', 'Housekeeping', 'Pupil', 'Worktable', 'Chomeur', 'No_need' ];
		map<int, int> age_ranges <- [ 0::4, 5::17, 18::24, 25::34, 35::49, 50::64, 65::100 ];
		list<bool> genders <- [ true, false ];
		
		matrix category_age_input_data <- csv_file('../includes/population/people/People_GenerationRule1_Data.csv');
		list category_age_input_frequency <- category_age_input_data column_at 2;
		int category_age_index <- 1;
		write '\tClassification of category and age';
		write '\t\tCategory, Age, Input Frequency, Generated Frequency';
		loop ar_key over: age_ranges.keys {
			loop c over: categories {
				write '\t\t' + c + ', [' + ar_key + '::' + age_ranges[ar_key] + '], ' + (category_age_input_frequency at category_age_index) + ", "
					+ length(people where ( (each.category = c) and (each.age >= ar_key ) and (each.age <= age_ranges[ar_key])) );
				category_age_index <- category_age_index + 1;
			}
		}
		
		matrix category_gender_input_data <- csv_file('../includes/population/people/People_GenerationRule2_Data.csv');
		list category_gender_input_frequency <- category_gender_input_data column_at 2;
		int category_index <- 1;
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

// NOUVEAU
species people {
	
	string category;
	bool gender;
	int age;
	
	
	aspect base {
		draw circle(2) color: #yellow;
	}
}

species household {
	int householdSize;
	list<people> member_people;
}


/////////////////////////////////:


species bondary
{
	aspect geom {
		draw shape color: rgb(186,139,45);
	}
}
 
species river
{
	aspect geom{
		draw shape empty: false color: rgb(57,219,257);
	}
}

species all_river
{
	aspect geom{
		draw shape empty: false color: rgb(57,219,257);
	}
}

species mornitoring
{
	aspect geom{
		//draw shape color: rgb(166,37,19);
		draw hexagon(10) empty: false color: rgb("red");
	}
}

species sewer
{
	aspect geom{
		draw shape color: rgb(50,137,48);
	}
}

species outside
{
	string name;
	string type;
	aspect geom {
		draw shape color: rgb(186,139,45);
	}
}

species hospital
{
	aspect geom {
		draw shape color: #orange;
	}
}
species center//reperer le centre du quartier afin de positionner les points de destination
{
}



experiment model1 type: gui {
	/** Insert here the definition of the input and output of the model */
	parameter "Shapefile for village:" var: village_file category: "GIS";
	parameter "Shapefile for the environnement:" var: environment_file category: "GIS";
	parameter "Shapefile for river:" var: river_file category: "GIS";
	parameter "Shapefile for all river:" var: all_river_file category: "GIS";
	parameter "Shapefile for sewer:" var: sewer_file category: "GIS";
	parameter "Shapefile for mornitoring:" var: mornitoring_file category: "GIS";
	
	output {
		//display city type: opengl ambient_light: 100  {
		display city{
			species bondary aspect: geom;
			//species outside aspect: geom;
			species Parcel aspect: base;
			species hospital aspect: geom;
			species culturalSpace aspect: geom;
			species market aspect: geom;
			species pt_destination aspect: geom;
			species stagnant_water aspect: geom;
			species person aspect: geom;
			//species vaccin aspect: geom;
			//species controller aspect: geom;
			//event [mouse_down] action: make_action ;
			
			// new display
			species river aspect: geom; 
			species all_river aspect:geom;
			species sewer aspect: geom;
			species mornitoring aspect: geom;  
			
			// NOUVEAU - population
			species people aspect: base;
		}
	}
}
