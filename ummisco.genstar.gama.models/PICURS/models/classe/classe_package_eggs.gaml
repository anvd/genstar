/**
 *  packageegg
 *  Author: Piou
 *  Description: 
 */

model packageeggs

import "classe_parameters.gaml"
import "classe_cloud_of_mosquitoes.gaml"

species package_eggs{
	int age <- 0;
	int age_limit <- eclosion_duration*24*hour;//10 jours (5 a 7 jours aussi)
	stagnant_water our_water;//
	string state <- 'S';
	int check_research <- rnd(5)+5;
	int last_time_age <- 0;
	int nb_eggs_members ;
	reflex increaseAge when:every(check_research*hour){
			age <- age+(counter - last_time_age);
			last_time_age <-counter;
			if(age >= age_limit){
				point pt_closest_water <- one_of(1 points_at(10#m));//1 point a 1m de l'eau 
				create cloud_of_mosquitoes number:1{
					our_foyer <- myself.our_water.myFoyer;
					location <- pt_closest_water;
					//write myself.nb_eggs_members;
					nb_male <- int(myself.nb_eggs_members/2);
					nb_female <- int(myself.nb_eggs_members/2);
					//nb_moskito_members <- myself.nb_eggs_members;
					if(our_foyer !=nil){
						//write'nbmal='+nb_male;
						our_foyer.nb_mosquito <-our_foyer.nb_mosquito+(nb_male+nb_female);
						nb_memberS <- nb_female;
					}
					if(myself.state = 'I' and flip(0.7)){
						//creation des moustiques infectes
						nb_memberI <- nb_female;
						nb_mosquito_infected <- nb_mosquito_infected+nb_memberI;
					}
					else{
						nb_memberS <- nb_female;
						nb_mosquito_susceptible <- nb_mosquito_susceptible+nb_memberS;
					}
				}
				our_water.nb_eggs <- our_water.nb_eggs - nb_eggs_members;
				//write 'egg';
				do die;	
			}	
	}
}

global {
	/** Insert the global definitions, variables and actions here */
}
