/**
 *  pointdestination
 *  Author: Piou
 *  Description: 
 */

model pointdestination

import "classe_parameters.gaml"
import "classe_cloud_of_mosquitoes.gaml"

species pt_destination
{
	int identity_point;
	string name_destination;
	string type;
	string myCategory;
	bool is_inside_village;
	//village myVillage_parent;
	float size <-30#m;
	//list<person> myVisitors;
	bool already_check_night<-false;
	string s_distance_between_source;
	float f_distance_between_source;
	int update_time <- rnd(12)+24;
	list<cloud_of_mosquitoes> my_cloud_mosquitos;
	list<stagnant_water> my_stagnant_water;
	float my_risk <-0.0;
	//repartition des etres humains selon leurs etats de sante
	list<person> visitorsS <- [];// update: myVisitors where(each.state = 'S');
	list<person> visitorsE <- [];// update: myVisitors where(each.state = 'E');
	list<person> visitorsI <- [];// update: myVisitors where(each.state = 'I');
	list<person> visitorsR <- [];// update: myVisitors where(each.state = 'R');
	
	//nombre des moustiques selon leurs etats de sante
	int nb_mosquitosS <- sum (my_cloud_mosquitos collect each.nb_memberS) update: sum (my_cloud_mosquitos collect each.nb_memberS);
	int nb_mosquitosE <- sum (my_cloud_mosquitos collect each.nb_memberS) update: sum (my_cloud_mosquitos collect each.nb_memberE);
	int nb_mosquitosI <- sum (my_cloud_mosquitos collect each.nb_memberS) update: sum (my_cloud_mosquitos collect each.nb_memberI);
	/*reflex updatevaleur{
		
	 visitorsS <-  (myVisitors where (each != nil))where (each.state = 'S');
	visitorsE <-  (myVisitors where (each != nil))where(each.state = 'E');
	visitorsI <- (myVisitors where (each != nil))where(each.state = 'I');
	visitorsR <- (myVisitors where (each != nil))where(each.state = 'R');
	}*/
	action identifyCategory_and_risk{
		switch(type){
			match'quartier'{
				myCategory <- "quartier"; 
				my_risk <- risk_in_street;
			}
			match_one ['marche', 'supermarche']{
				myCategory <- "marche";
				my_risk <- risk_in_market;
			}
			match_one ['parc','lieu public','pagode']{
				myCategory <- "lieu public";
				my_risk <- risk_in_public_space;
			}
			match_one ['zone rurale','site de relocal','Enquete','destination']{
				myCategory <- "zone rurale";
				my_risk <- risk_in_other;
			}
			match_one ['universite','hopital','caserne militai']{
				myCategory <- "administration";
				my_risk <- risk_in_enterprise;
			}
			match_one ['lycee','ecole']{
				myCategory <- "ecole";
				my_risk <- risk_in_education;
			}
			match 'Enquete'{
				myCategory <- "village";
				//my_risk <- risk_in_education;
			}
		}
	}
	reflex update_checking when:isDay and already_check_night{
		already_check_night <- false;
	}
	/*reflex emigration when:!isDay and !already_check_night and length(myVisitors)!=0{
		loop index_visitor from:0 to:length(myVisitors)-1{
			myVisitors[index_visitor].target_pt_destination <-first(list(pt_destination where(each.myVillage_parent = myVisitors[index_visitor].myVillage)));
			if(myVillage_parent!=nil){//si le point de destination appartient dans un village
				//write " "+myVillage_parent.name+" = "+ length(myVillage_parent.inhabitants);
				remove myVisitors[index_visitor] from: myVillage_parent.inhabitants;
			}
			
		}
		myVisitors <-nil;
		//already_check_day <-false;
		already_check_night<-true;
	}*/
	reflex create_stagnante_water when: every(update_time*hour) and flip(my_risk) and take_account_risk_destination and dynamic_creation_water{	
		create cloud_of_mosquitoes number:1{
			location <- any_location_in(myself);
			nb_mosquito_susceptible <- nb_mosquito_susceptible+nb_memberS;
		}
		create stagnant_water number:1{
			location <- any_location_in(myself);
		}		
	}
	
	aspect geom {
		if(nb_mosquitosI >0.01*nb_mosquitosS){
				draw rectangle(size,size) color:#red;  //rectangle(1#km,1#km);
		}
		else{
		switch(type){
			match'quartier'{
				draw circle(size) color:#cyan; 
			}
			match_one ['marché', 'supermarché']{
				draw circle(size) color:#magenta; 
			}
			match_one ['parc','lieu public','pagode']{
				draw circle(size) color:#green;
			}
			match_one ['zone rurale','site de relocal','Enquêté','destination']{
				draw circle(size) color:#yellow;
			}
			match_one ['université','hopital','caserne militai']{
				draw circle(size) color:#orange; 
			}
			match_one ['lycée','école']{
				draw circle(size) color:#blue;
			}
			match 'Enquêté'{
				draw circle(size) color:#white;
			}
		}
		}
	}
}

global {
	/** Insert the global definitions, variables and actions here */
}
