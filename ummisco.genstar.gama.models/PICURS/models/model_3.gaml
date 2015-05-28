/**
 *  model3
 *  Author: Piou
 *  Description: 
 */

model model3

import "classe_model_3/parameters.gaml"
import "classe_model_3/parcel.gaml"


global {
	/** Insert the global definitions, variables and actions here */
	reflex dayToNight {
		if(isStart){
			write('====== Debut de la simmulation=======');
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
			write "=================Fin soiree & Debut journee=====================";
			set isDay<-true;
			set counter_periodic<-0;
			nb_day <- nb_day+1;
			write "--- "+nb_day+" eme jour de la simulation";
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
		}
	}
	
	
	int nb_of_people <- 30349; // ATTENTION: see documents/data_demographic_cantho_2010.csv:population_ninhkieu_2010
	
	init{
		create bondary from:environment_file;	
		create river from:river_file;
		create all_river from:all_river_file; 
		create sewer from:sewer_file; 
		create mornitoring from: mornitoring_file;

		create parcel from: village_file with: [landuse::string(read('landuse_na')), area::float(read('area_m2')), identity::int(read('idendity'))] {
			do set_color_risk_parcel;
		}
		
		// ATTENTION: age_sex_generation_rules.csv is formulated from data_gemographic_cantho_2010.xls:age_group_ct_2009
		list AnHoa_people_population <- list<unknown>(population_from_csv('../includes/population/An Hoa/people_attributes.csv', '../includes/population/An Hoa/people_generation_rules.csv', nb_of_people));
		create people from: AnHoa_people_population { 
			location <- any_location_in(one_of(parcel where(each.landuse = 'Residential')));
		}
		
		
	}
}
/*
 * Les species. 
 */
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

species sewer
{
	aspect geom{
		draw shape color: rgb(50,137,48);
	}
}

species mornitoring
{
	aspect geom{
		//draw shape color: rgb(166,37,19);
		draw hexagon(10) empty: false color: rgb("red");
	}
}

///////////////////////////

species people schedules: [] {
	
	string category;
	bool gender;
	int age;
	int identity;
	
	aspect circle {
		draw circle(2) color: #yellow;
	}
}

///////////////////////////

experiment model3 type: gui {
	/** Insert here the definition of the input and output of the model */
	parameter "Shapefile for village:" var: village_file category: "GIS";
	parameter "Shapefile for the environnement:" var: environment_file category: "GIS";
	parameter "Shapefile for river:" var: river_file category: "GIS";
	parameter "Shapefile for all river:" var: all_river_file category: "GIS";
	parameter "Shapefile for sewer:" var: sewer_file category: "GIS";
	parameter "Shapefile for mornitoring:" var: mornitoring_file category: "GIS";
	
	output {
		display city{
			species bondary aspect: geom refresh: false;
			species parcel aspect: base refresh: false;
			species river aspect: geom refresh: false; 
			species all_river aspect:geom refresh: false;
			species sewer aspect: geom refresh: false;
			species mornitoring aspect: geom refresh: false;  
			
			species people aspect: circle refresh: false;
		}
	}
}
