/**
 *  classecoudofmosquitoes
 *  Author: Piou
 *  Description: 
 */

model classecloudofmosquitoes

import "classe_parameters.gaml"
import "classe_person.gaml"
import "classe_stagnant_water.gaml"
import "classe_package_eggs.gaml"
import "classe_foyer.gaml"

species cloud_of_mosquitoes{
	int age <-0;
	//int nb_moskito_members <-0;
	int nb_male;// <- nb_mosquito_in_cloud/2;
	int nb_female;// <- nb_mosquito_in_cloud/2;
	int age_limit <- nb_age_limit_mosquito*7*24*hour;//4 semaines 
	bool is_full <- false;//les femelles ont pris du sang pour pondre
	bool laid <-false;
	int lay_period <- 2*24*hour;//tous les 2 jours
	int cpt_period <-0;
	int nb_memberS <-nb_female;
	int nb_memberE <-0;
	int nb_memberI <-0;
	bool can_bite <- true;
	int last_time_age <- 0;
	int last_time <- 0;
	int latence_exposed <-latence_mosquito_exposed*24*hour;
	int cpt_latence_exposed <-0;
	list<int> list_mosquito_E_to_I;//le nombre des moustiques qui sont exposés et deviennent infectes
	list<int> time_to_update_E_I;//le temps ou les moustiques exposés deviennent infectés	
	int check_research <- rnd(5)+1;
	int update_state_period <- rnd(12)+12;
	
	foyer our_foyer;
	Parcel our_parcel;
	
	reflex increaseAge when:every(check_research*hour){
			age <- age+(counter - last_time_age);
			last_time_age <-counter;
			if(age >= age_limit){
				if(flip(0.9)){//les moustiques dans les paquets vont mourir
					nb_mosquito_susceptible <- nb_mosquito_susceptible-nb_memberS;
					nb_mosquito_exposed <- nb_mosquito_exposed-nb_memberE;
					nb_mosquito_infected <- nb_mosquito_infected-nb_memberI;
					do die;
				}
			}	
	}
	//chercher des personnes a piquer
	reflex search_to_bite when:!is_full and can_bite and every(check_research*hour){
			list<person> person_closed <- person at_distance (10#m);
			if(length(person_closed)=0){//le nuage de moustique se deplace pour chercher des personnes a piquer
				list<point> new_locations <- 1 points_at(10#m);
				location <- one_of(new_locations);
			}
			else{
				//determiner la liste des personnes susceptibles d'etre piquer par les femmelles
				int new_memberE <-0;
				//les femelles susceptible
				list<person> infected_person_closed <- list(person where(each.state ='I')) at_distance (10#m);
				list<person> susceptible_person_closed <- list(person where(each.state ='S')) at_distance (10#m);
				//succes c'est de piquer une personne infectee avec une proba p
				//nombre des moustiques qui sont nouvellement exposees
				if(length(infected_person_closed) >0 and nb_memberS>0){
					float proba_bite_infected_human <- float(length(infected_person_closed)/length(person_closed));
					if(proba_bite_infected_human!=1){
						new_memberE <- binomial({nb_memberS,proba_bite_infected_human});
					}
					else {
						new_memberE <-nb_memberS; //si p=1 toute les personnes a piquer sont infectes
					}
				}
				if(new_memberE>0){
					nb_memberS <- nb_memberS-new_memberE;
					nb_memberE <- nb_memberE+new_memberE;
					list_mosquito_E_to_I  <- list_mosquito_E_to_I+nb_memberE;
			 		time_to_update_E_I <- time_to_update_E_I+(counter+latence_exposed);
				}
				int nb_person_become_infected <-0;
				if(length(susceptible_person_closed)>0 and nb_memberI>0){
					float proba_become_infected <- nb_memberI/ (nb_memberS+nb_memberE+nb_memberI);
					if(proba_become_infected!=1){
						nb_person_become_infected <- binomial({length(susceptible_person_closed),proba_become_infected});
					}
					else{
						nb_person_become_infected <- length(susceptible_person_closed);
					}
				}
				loop index_person from:0 to:nb_person_become_infected-1{
					int index_choosed <- rnd(length(susceptible_person_closed)-1);
					person person_become_infected <- susceptible_person_closed[index_choosed];
					ask(person_become_infected){
						//set_update_health <- true;//mettre l'etat de sante de la personne en expose
						state <-'E';
						nb_human_susceptible <- nb_human_susceptible-1;
						nb_human_exposed <- nb_human_exposed+1;
					}
					susceptible_person_closed <- susceptible_person_closed - person_become_infected;
				}
				nb_mosquito_susceptible <- nb_mosquito_susceptible-new_memberE;
				nb_mosquito_exposed <- nb_mosquito_exposed+new_memberE;
				is_full <- true;
				can_bite <- false;
			}
	}
	//periode a attendre avant de piquer prochainement
	reflex count_period when:!can_bite and every(check_research*hour){
			if(cpt_period <lay_period){
				cpt_period <- cpt_period+(counter -last_time);
				last_time <- counter;
			}
			else{
				cpt_period <- 0;
				can_bite <-true;
				is_full <- false;
			}
	}
	reflex lay when: is_full {
			//list<stagnant_water> water_closed <- stagnant_water at_distance (10#m);
			list<stagnant_water> water_closed <- list(stagnant_water at_distance (10#m) where(each.nb_eggs <=nb_eggs_max_in_water)
				sort_by(self distance_to(each))
			);
			if(length(water_closed) =0){//se deplacer pour chercher
				list<point> new_locations <- 1 points_at(10#m);
				location <- one_of(new_locations);
			}
			else{
				stagnant_water myWaterPlace <- first(water_closed);
				if(nb_female>0){
					create package_eggs {
//						if(myself.nb_female > myself.nb_memberI){
//							nb_eggs_members <- abs(myself.nb_female-myself.nb_memberI)*nb_egg_laid;
//						}
//						if(myself.nb_female < myself.nb_memberI){
//							nb_eggs_members <- abs(myself.nb_memberI - myself.nb_female)*nb_egg_laid;
//						}
//						if(myself.nb_female = myself.nb_memberI){
//							nb_eggs_members <- abs(myself.nb_memberI)*nb_egg_laid;
//						}
//						if(nb_eggs_members<0) {write"here"+nb_eggs_members+" == "+myself.nb_female+"== "+myself.nb_memberI;
//							
//						}
						//write "ici = "+myself.nb_female+" == "+nb_egg_laid;
						nb_eggs_members <- myself.nb_female*nb_egg_laid;
						if(nb_eggs_members<0) {write"here"+(myself.nb_female*nb_egg_laid)+" == "+myself.nb_female+"== "+myself.nb_memberI+"=="+nb_egg_laid;							
						}
						our_water <- myWaterPlace;
						our_water.nb_eggs <- our_water.nb_eggs +nb_eggs_members;
						location <- any_location_in(our_water);
					}
				}
				if(nb_memberI>0){
					create package_eggs {
						nb_eggs_members <- myself.nb_memberI*nb_egg_laid;
						our_water <- myWaterPlace;
						state <- 'I';
						our_water.nb_eggs <- our_water.nb_eggs +nb_eggs_members;
						location <- any_location_in(our_water);
					}
				}
				
			}
	}
	reflex become_infected when: every(update_state_period*hour) and length(time_to_update_E_I)>0 and counter>=int(time_to_update_E_I[0]){
		// 
		// int nb_index doing
		//write 'traitement propagation' +time_to_update_E_I[0] ;
		int last_index_done <- -1;
		loop index from:0 to: length(list_mosquito_E_to_I)-1{
			//write "infected index"+index;
			if(counter>=time_to_update_E_I[index]){
				nb_memberE <- nb_memberE -list_mosquito_E_to_I[index];
				nb_memberI <- nb_memberI+list_mosquito_E_to_I[index];
				nb_mosquito_exposed <- nb_mosquito_exposed - list_mosquito_E_to_I[index];
				nb_mosquito_infected <- nb_mosquito_infected + list_mosquito_E_to_I[index];
				last_index_done <- last_index_done+1;
			}
			else{
				break;
			}
		}
		//write 'traite = '+last_index_done;
		loop index_move from:0 to:last_index_done{
			//mettre a jour la liste: enlever ce qui est deja traiter
			list_mosquito_E_to_I <-list_mosquito_E_to_I - list_mosquito_E_to_I[0];
			time_to_update_E_I <- time_to_update_E_I -time_to_update_E_I[0];
		}
	}
}

global {
	/** Insert the global definitions, variables and actions here */
}
