/**
 *  classeperson
 *  Author: Piou
 *  Description: 
 */

model classeperson

import "classe_parameters.gaml"
import "classe_point_destination.gaml"
import "classe_foyer.gaml"
import "classe_cultural_space.gaml"
import "classe_market.gaml"
import "classe_parcel.gaml"
import "../model_1.gaml"

species person skills:[moving]{
	string profession;
	int identity;
	int age;
	string categorie_age;
	string sex;
	//parametre pour macro
	int id_myFoyer;
	foyer myFoyer;
	//pt_destination myVillage;
	point point_myVillage;
	pt_destination target_pt_destination;
	point target_destination;
	float frequency;
	float size <- 5#m;
	float speed_outside <- 1000#km/#h;
	float speed <- 500#km/#h;
	bool is_moved_outside <-false;
	bool already_moves <- false;
	int nb_move_realised_week <-0;
	bool go_to_destination <- false;
	int day_period_moves <-0;
	int type <-IS_MOVED_INSIDE;//type 1: il se deplace au niveau locale, 2: il se deplace a l'exterieur
	
	int count_period <-0;//periode en jour: l'individu doit faire un deplacement pendant cette periode
	int nb_last_day <-0;
	bool is_arrived_destination <-false;
	bool is_leaved <- false;//pour determiner si l'individu a deja quitte son lieu de destination
	
	//parametre pour micro
	foyer myResidence;
	Parcel working_place;
	hospital working_hospital;
	market working_market;
	//Parcel mySchool;
	Parcel myActivityPlace;
	market myMarket;
	culturalSpace myCulturalSpace;
	foyer myFriendFoyer;

	point the_target_home <- nil;
	point the_target_work <- nil;
	point the_target_work_temp <- the_target_work;
	point the_target_other <- nil;

	//duree de faire des activites
	float duration_path <-0.0;
	float duration_work <-0.0;
	float duration_at_home <-0.0;
	float duration_activity<-0.0;

	//indice des autres activites comme visite amis, sports, rester a la maison,...
	//int other_activity;
	
	//nombre de pas de simulation actuel
	//int actual_step <- 0;
	//temps pour aller au travail
	int myTime_to_go_work<-0;
	int myTime_to_leave_work <-0;
	//bool isGoToWork <-true;//il part au travail ou non
	bool is_arrived_at_work <-false;
	bool is_arrived_at_activity <-false;
	bool isAvailable <-false;
	int counter_duration_activity <- 0;
	
	//parametre de sante
	string state <-'S';
	int nb_piqure_infected_mosquito <-0;
	
	//int latence_exposed <-5*24*hour;
	int latence_exposed_human <- (min_latence_human_exposed+rnd(max_latence_human_exposed - min_latence_human_exposed));
	int cpt_latence_exposed<-0;
	int cpt_latence_infected<-0;
	int last_time_update_state_E<-0;
	int last_time_update_state_I<-0;
	bool set_update_health <- false;
	int check_state <- rnd(5)+2;
	
	//determiner le profession defini par le modele a partir des professions dans les donnees en entrees
	action linkProfession{
		switch(profession){
			match_one ['sans_activite','retraite']{
				profession <- 'other';
				break;
			}
			match'enseignant'{
				profession <- 'teacher';
				break;
			}
			match_one ['militaire', 'ouvrier_batiment','artisant','chiffonier','employe_ONG','employe_prive','ouvrier']{
				profession <- 'employee';
				break;
			}
			match'ecolier'{
				profession <- 'student';
				break;
			}
			match'conducteur'{
				profession <- 'transporter';
				break;
			}
			match_one ['agriculteur','ouvrier_agricole']{
				profession <- 'farmer';
				break;
			}

			match'commercant'{
				profession <- 'trader';
				break;
			}
			default{
				profession <- 'other';
				break;
			}
		}
	}
	
	//chaque periode de deplacement, l'individu doit faire un deplacement
	//tant qu'il n'a pas encore fait un deplacement pendant cette periode il verifie tous les jours s'il se deplace ou non
	reflex checkMove when: nb_last_day <nb_day and is_moved_outside{
		nb_last_day <-nb_day;
		count_period <-count_period+1;//le compteur du jour augmente a chaque  verification
		if(count_period = day_period_moves){
			//reinitialisation
			if(!already_moves){//s'il n'est pas encore partie mais la periode est atteinte, il doit y aller
				//comptage du nombre des personnes qui se deplacent a l'exterieur selon leurs etats de sante
				switch(state){
					match 'S'{
						susceptible_persons_move <- susceptible_persons_move+1;
					}
					match 'E'{
						exposed_persons_move <- exposed_persons_move+1;
					}
					match 'I'{
						infected_persons_move <- infected_persons_move+1;
					}
				}
				go_to_destination <-true;
				nb_move_realised_week <- nb_move_realised_week+1;
			}
			count_period <-0;
			already_moves <-false;
		}
		else if(flip(frequency/7) and !already_moves){
			//comptage du nombre des personnes qui se deplacent a l'exterieur selon leurs etats de sante
			switch(state){
				match 'S'{
					susceptible_persons_move <- susceptible_persons_move+1;
				}
				match 'E'{
					exposed_persons_move <- exposed_persons_move+1;
				}
				match 'I'{
					infected_persons_move <- infected_persons_move+1;
				}
			}
			go_to_destination <-true;
			already_moves <-true;
			nb_move_realised_week <- nb_move_realised_week+1;
		}
		if(nb_move_realised_week >= frequency){
			is_moved_outside <-false;
		}
	}
	//partir vers d'autre ville le matin et retourner a la maison le soir
	reflex moveToTarget when:go_to_destination and isDay and !is_arrived_destination and is_moved_outside{
		if(location != target_destination){
			do goto target: target_destination speed :speed_outside;
		}
		else{
			is_arrived_destination <-true;
			go_to_destination <-false;
			ask(target_pt_destination){
				//add myself to:myVisitors;
				//write myCategory;
				/*if(myCategory='marche'){
					write "here";
				}*/
				switch(myself.state){
					match 'S'{
						add myself to:visitorsS;
					}
					match 'E'{
						add myself to:visitorsE;
					}
					match 'I'{
						add myself to:visitorsI;
					}
					match 'R'{
						add myself to:visitorsR;
					}
				}
				myself.is_leaved <-false;
			}
		}
	}
	//retourner au village d'origine
	reflex goBack when:!isDay and is_arrived_destination and is_moved_outside{
		if(!is_leaved){
			switch(state){
					match 'S'{
						remove self from:target_pt_destination.visitorsS;
					}
					match 'E'{
						remove self from:target_pt_destination.visitorsE;
					}
					match 'I'{
						remove self from:target_pt_destination.visitorsI;
					}
					match 'R'{
						remove self from:target_pt_destination.visitorsR;
					}
				}
			
			is_leaved <-true;
		}
		if((location != point_myVillage)){
			do goto target: point_myVillage speed :speed_outside;
		}
		else{
			is_arrived_destination <-false;
		}	
	}
	reflex reInitialiseMoveWeek when: (nb_day mod 7)=0 and type=IS_MOVED_OUTSIDE{
		nb_move_realised_week <- 0;
		is_moved_outside <-true;
	}
	
	//deplacemenet a l'interieur du quartier
	//determiner a quelle heure l'individu part au travail : ceci se fait chaque jour
	/*reflex identifyTimeGoWork when:!is_moved_outside and isTime_to_go_work and myTime_to_go_work=0{
		//float marge_time <-0.0;
		//marge_time <- hour_latest_go_work -hour_early_go_work -duration_path;//unite en heure
		//int my_marge_time <- rnd (int(marge_time*hour));
		//myTime_to_go_work <- int(hour_early_go_work*hour)+my_marge_time;
		//write "schools "+closure_local_schools;
		/*if(profession = 'student' and closure_local_schools){
			the_target_work <- nil;
		}
		else{
			the_target_work <- the_target_work_temp;
		}
		
		myTime_to_go_work <- int(hour_early_go_work*hour)+hour;
	}*/
	//partir a l'ecole ou au travail s'il est entre 7 heure à 18 heure
	reflex moveToWork when: !is_moved_outside and the_target_work!=nil and isDay
	and actual_hour>=hour_early_go_work and !is_arrived_at_work{
			if([self]inside(working_place) = [self] or [self]inside(working_hospital) = [self] or [self]inside(working_market) = [self]){
				is_arrived_at_work <-true;
				//determiner l'heure pour rester au travail
				duration_work <- (min_duration_work*hour) +(rnd(int(max_duration_work - min_duration_work)*hour));
				myTime_to_leave_work <- int(actual_hour + duration_work);
				//write 'time leaving work = '+myTime_to_leave_work/hour;
			}
			else{
				do wander amplitude:30  speed: speed;
				do goto target: the_target_work;
			}
	}
	 
	//determiner l'action a faire apres le travail
	action chooseActivity{
		//0:continuer le travail, 1:faire des achats, 2: faire des activités culturelles, 3: visiter des amis, 4: rentrer
		int index_activity <-nil;
		if(isDay){
			set index_activity <-rnd(4);
		}
		else{//s'il fait nuit il faut rentrer
			if(flip(0.4)){//ils font d'autres activites
				set index_activity <-rnd(2)+1;//la plupart ne reste pas au travail (!= 0)
			}
			else{//il rentre tout de suite
				set index_activity <-4;
				the_target_other <- the_target_home;
			}
		}
		//write 'ind ='+index_activity;
		switch (index_activity){
				match 0{
					//rester a sa place actuelle
					the_target_other <-nil;
					is_arrived_at_activity <-true;
					isAvailable <- false;
				}
				match 1{
					//faire des achats
					myMarket <- one_of(market);
					the_target_other <- any_location_in(myMarket);
				}
				match 2{
					//faire des activites culturelles: sport, rester a la bibliotheque, cinema,...
					myCulturalSpace <- one_of(culturalSpace);
					the_target_other <- any_location_in(myCulturalSpace);
				}
				match 3{
					//visiter des amis
					myFriendFoyer <- one_of(foyer);
					the_target_other <- any_location_in(myFriendFoyer);
				}
				match 4{
					//rentrer
					the_target_other <- the_target_home;
				}
		}

		//determination de la duree de l'activité à faire
		duration_activity <- 5.0*hour;//(min_duration_activity*hour)+ rnd(int(max_duration_activity-min_duration_activity)*hour);
	}
	
	reflex updateState when: !is_moved_outside and ((profession !='other' and actual_hour=myTime_to_leave_work)or profession ='other'){
		isAvailable <-true;
	}
	//quitter le travail et choisir les activités à faire
	reflex leavePlace when: !is_moved_outside  and (!isDay or actual_hour >=myTime_to_leave_work) and isAvailable and !isMidNight{
		do chooseActivity();
		isAvailable <-false;
	}
	reflex moveToOtherPlace when: !is_moved_outside  and !isAvailable and the_target_other!=nil and !isMidNight{
		if([self]inside(myMarket) = [self] or [self]inside(myCulturalSpace) = [self] or [self]inside(myFriendFoyer) = [self]){
			isAvailable <-false;
			is_arrived_at_activity <-true;
			myActivityPlace <-nil;
		}
		else{
			do wander amplitude:30  speed: speed;
			do goto target: the_target_other;
		}
	}
	reflex incrementDurationActivity when: !is_moved_outside  and !isAvailable and is_arrived_at_activity{
		if(counter_duration_activity<duration_activity){
			counter_duration_activity <- counter_duration_activity+1;
		}
		else{
			isAvailable <-true;
			the_target_other <-nil;
			counter_duration_activity<-0;
			is_arrived_at_activity <-false;
		}
	}
	//s'il arrive a la maison il ne sort plus
	reflex stayAtHome when:!is_moved_outside   and !isDay and ([self]inside(myFoyer) = [self]){
		isAvailable <-false;
		the_target_other <-nil;
		is_arrived_at_work <-false;
		myActivityPlace <-nil;
	}
	reflex goBackHome when: !is_moved_outside and [self]inside(myFoyer) != [self] and isMidNight{
		isAvailable <-false;
		do wander amplitude:45  speed: speed;
		do goto target: the_target_home;	
	}
	//mise a jour de l'etat de sante
	reflex become_infected when:state='E' and every(2*hour){
			if(cpt_latence_exposed < latence_exposed_human*24*hour){
				cpt_latence_exposed <-cpt_latence_exposed+(counter - last_time_update_state_E);
				last_time_update_state_E <- counter;
			}
			else{
				state <-'I';
				nb_human_exposed <- nb_human_exposed-1;
				nb_human_infected <- nb_human_infected+1;
			}
	}
	reflex become_recovery when:state='I' and every(check_state*hour){
			if flip(risk_mortality) {
				nb_human_died <- nb_human_died+1;
				nb_human_infected <- nb_human_infected-1;
				do die;
			}
			if(cpt_latence_infected < latence_human_infection*24*hour){
				cpt_latence_infected <-cpt_latence_infected+(counter - last_time_update_state_I);
				last_time_update_state_I <- counter;
			}
			else{
				state <-'R';
				nb_human_infected <- nb_human_infected-1;
				nb_human_recovered <- nb_human_recovered+1;	
			}
	}
	aspect geom{
		draw circle(size) color : #cyan;
	}
}

global {
	/** Insert the global definitions, variables and actions here */
}
