/**
 *  classparcel
 *  Author: Piou
 *  Description: 
 */

model classparcel

import "classe_parameters.gaml"
import "classe_cloud_of_mosquitoes.gaml"
import "classe_stagnant_water.gaml"

species Parcel {
	int identity;
	string landuse;
	float area ;
	rgb color;
	int height;
	float my_risk <-0.0;
	float update_time <- rnd(2)+12.0;
	string landuse_categ;
	
	action set_color_risk_parcel {
		switch(landuse){
			match_one['Rice','Annual crops','Aquaculture']{
				color <- rgb(0,179,0); // vert un peu foncé
				my_risk <- risk_in_paddy_field;
				landuse_categ <- "paddy_field";
			}
			match 'Fruit'{
				color <- rgb(128,255,128); // vert claire
				my_risk <-risk_in_fruit;
				landuse_categ <- "fruit";
			}
			match 'Economic activity'{
				color <- rgb(0,84,168); // bleu foncé
				my_risk <- risk_in_enterprise;
				landuse_categ <- "enterprise";
				//set height <-50+rnd(90);
			}
			match 'Education'{
				color <- rgb(215,107,0); // orange - marron 
				my_risk <- risk_in_education;
				landuse_categ <- "education";
			}
			match_one(['Residential','Religion']){
				color <- rgb(190,125,255); // violet claire 
				my_risk <- risk_in_other;
				landuse_categ <- "construction";
			}
			match 'River - canal'{
				color <- rgb(176,255,255); // bleu clair 
				my_risk <-risk_in_river;
				landuse_categ <- "river";
			}
			match 'Military' {
				color <- rgb(255,66,66); // rouge un peu clair 
				my_risk <- risk_in_military; 
				landuse_categ <- "construction";
			}
			match 'Street' {
				color <- #white;
				my_risk <- risk_in_street; 
				landuse_categ <- "construction";
			}
			match 'Cemetery' {
				color <- rgb(134,134,134); // gris foncé
				my_risk <- risk_in_other; 
				landuse_categ <- "construction";
			}
			match 'Cultural' {
				color <- rgb(128,0,0); // rouge foncé
				my_risk <- risk_in_public_space; 
				landuse_categ <- "public_space";
			}
			match 'Fallow' {
				color <- rgb(255,225,215); // beige - blanc
				my_risk <- risk_in_other; 
				landuse_categ <- "fruit";
			}
			match 'Utilities' {
				color <- rgb(130,0,130); // violet foncé
				my_risk <- risk_in_public_space; 
				landuse_categ <- "public_space";
			}
			match 'Market' {
				color <- rgb(240,240,0); // jaune
				my_risk <- risk_in_market; 
				landuse_categ <- "public_space";
			}
		}
	}
	/*reflex create_stagnante_water when: every(update_time*hour) and flip(my_risk) and dynamic_creation_water{	
		create cloud_of_mosquitoes number:1{
			location <- any_location_in(myself);
			our_parcel <- myself;
			nb_mosquito_susceptible <- nb_mosquito_susceptible+nb_memberS;
		}
		create stagnant_water number:1{
			location <- any_location_in(myself);
			myParcel <-myself;
		}		
	}*/
	aspect base {
			draw shape color: color depth: height;
	}
	
}

global {
	/** Insert the global definitions, variables and actions here */
}
