/**
 *  new
 *  Author: Piou
 *  Description: 
 */

model classe_parameters

global {
	//shapefiles 
	// map an hoa (2005 et 2010)
	//file map_file <- shape_file('../includes/anhoa_quang/anhoa_2010.shp', 0);
	
	//environnement de travail
	//file environment_file <- shape_file('../includes/anhoa/anhoa_admin.shp', 0);
	file environment_file <- shape_file('../includes/anhoa_quang/couverture.shp', 0);
	
	//carte du quartier
	file village_file <- shape_file('../includes/anhoa_quang/anhoa_2010.shp', 0);
	
	// LANDY
	//determination du centre du quartier
	file center_file <- shape_file('../includes/anhoa_ancien/anhoa_centre.shp', 0);
	
	//les hopitaux, marches et centre culturel: si le fichier n'existe pas, il faut mettre sa valeur a nil
	file hospital_file <- shape_file('../includes/anhoa_ancien/anhoa_2010_ajuste/anHoa2010_hospital.shp', 0);
	file cultural_file <- shape_file('../includes/anhoa_ancien/anhoa_2010_ajuste/anhoa2010_cultural.shp', 0);
	file market_file <- shape_file('../includes/anhoa_ancien/anhoa_2010_ajuste/anhoa2010_market.shp', 0);
	
	// new shapefiles 
	//file environment_file_2 <- shape_file('../includes/Map_2/An_Hoa.shp', 0);
	file river_file <- shape_file('../includes/map_tri/An_Hoa_River_1.shp', 0);
	file all_river_file <- shape_file('../includes/map_tri/Ninh_Kieu_River_2.shp', 0);
	file sewer_file <- shape_file('../includes/map_tri/An_hoa_Sewer.shp', 0);
	file mornitoring_file <- shape_file('../includes/map_tri/Mornitoring_sites.shp', 0);

	file water_csv_file <- csv_file("../doc/data_generated/water_quality/Turbidity_12.csv",";");//avec attribution de qte d'eau

/*
	file foyers_csv_file <- csv_file("../doc/data_generated/foyers_TC.csv",";");//avec attribution de qte d'eau
	file persons_csv_file <- csv_file("../doc/data_generated/persons_TC.csv",";");//avec attribution de professtion
	file displacement_csv_file <- csv_file("../doc/data_generated/moves_TC.csv",";");
	
	file categorie_destination_csv_file <- csv_file("../doc/data_generated/pt_destination.csv",";");//categorie des points de destination
	
	file pt_destination_csv_file <- csv_file("../doc/data_generated/distance_TA_pt_destination.csv",";");//ne pas changer
	file vaccin_image <- file("../images/vaccin1.jpeg");
	file exteminator_image <- file("../images/exteminateur.jpeg");
*/

	// LANDY 
	file categorie_destination_csv_file <- csv_file("../doc/data_generated/pt_destination.csv",";");//categorie des points de destination
	file pt_destination_csv_file <- csv_file("../doc/data_generated/distance_TA_pt_destination.csv",";");//ne pas changer
	
	//////////////////////////////////////////////:
	
	geometry shape <- envelope(environment_file);
	//geometry shape <- envelope(environment_file_2);
	
	float scale_destination <-10#m ; //echelle utilise pour represente les points de destination par rapport au village etudie
	//comme anHoa est tres loin des points de destination, on considere une echelle de 10m
	//pour les autres quartiers, la plupart des destinations sont proches du quartier, on considere une echelle de 5m
	
	/*
	 * parametre pour le temps
	 */
	// Durée Jour et nuit (5 cycles correspond a 1heure)
	int hour<-3 ;
	int day_duration<-14*hour; 
	int night_duration<-10*hour;

	//initialisation
	bool isDay<-true;
	bool isMidNight <-false;
	int actual_hour<-6*hour; //initialisation : la simulation commence a 06h du matin ;
	
	//int heure_actuel<-0;
	int nb_day<-1;
	bool isStart <- true;
	int counter_periodic<-0;
	int counter<-0;//compteur de nombre de pas de simulation
	
	//variables globales
	int taille_individu <-10;
	int nb_all_people <-0;
	float people_moves_rate <-48.07 ;
	int susceptible_persons_move <- 0;
	int exposed_persons_move <- 0;
	int infected_persons_move <- 0;
	int IS_MOVED_INSIDE <- 1;
	int IS_MOVED_OUTSIDE <-2;

	//donnees de can tho
	/*float rate_employee <- 65.60;
	float rate_student <- 17.13;
	float rate_teacher <- 0.9;
	float rate_doctor <- 0.5;
	float rate_trader <- 9.84;
	float rate_transporter <- 2.43;
	float rate_farmer <- 0.7;
	float rate_other <- 2.9;*/

	//verifier si heure pour aller au travail
	bool isTime_to_go_work <-false;
	float hour_early_go_work <-7.0;
	float hour_latest_go_work <-9.0;

	//duree de travail en heure
	float min_duration_work <- 9.0;
	float max_duration_work <- 11.0;
	//duree des autres activites en heure
	float min_duration_activity <- 2.0;
	float max_duration_activity <- 3.0;

	int INDEX_VACCINATION <-1;
	int INDEX_DESINFECTION <-2;
	int index_mouse_action <- -1; 
	float radious_action <- 300#m;
	bool dynamic_creation_water <-true;
	int epidemic_time_step <-2;
	
	/*int nb_students <-0;
	int nb_employees <-0;
	int nb_teachers <-0;
	int nb_doctors <-0;
	int nb_traders <-0;
	int nb_transporters <-0;
	int nb_farmers <-0;
	int nb_others <-0;*/

	int cpt_not_work <-0;
	//parametre pour contamination
	//float succes_rate_egg <- 0.4;
	int nb_egg_laid <- 50;
	int nb_init_cloud_mosquito <- 100;//nombre initial de paquet de moustique
	int nb_mosquito_in_cloud <- 20;//nombre des moustique dans un paquet
	int nb_eggs_max_in_water <- 2000;
	
	int nb_mosquito_susceptible <- 0;//(nb_init_cloud_mosquito*nb_mosquito_in_cloud);// on ne compte que les femelles
	int nb_mosquito_exposed<-0;
	int nb_mosquito_infected<-0;
	int min_latence_human_exposed <-3;//l'etre humain devient infecte dans 3 a 6 jours apres le piqure du moustique infecte
	int max_latence_human_exposed <-6;//
	int latence_human_infection <-5;
	int nb_age_limit_mosquito <-3;//4 semaines;
	int eclosion_duration <-10;
	int latence_mosquito_exposed <- 10;
	int nb_human_susceptible<-0;
	int nb_human_exposed<-0;
	int nb_human_infected<-0;
	int nb_human_recovered <-0;
	int nb_human_died <-0;
	float risk_mortality <-0.01;
	float rate_init_human_infected <-0.2;
	float rate_init_cloud_mosquito_infected <-0.4;
	float rate_init_human_recovered <-0.1;
	int quantity_basin_water <-200;
	
	//parametres modifiables 
	//risque selon le type de parcel
	float risk_in_paddy_field <-0.1;
	float risk_in_fruit <-0.4;
	float risk_in_education <-0.2;
	float risk_in_enterprise <-0.1;
	float risk_in_market <-0.5;
	float risk_in_public_space <-0.5;
	float risk_in_river <-0.05;
	float risk_in_street <-0.01;
	float risk_in_other <-0.01;
	float risk_in_military <-0.01;
	bool take_account_risk_destination <-true;
	bool closure_local_schools <-false;
	bool closure_external_schools <-false;
	int nb_msq_per_destination <- 100;
	int nb_water_per_destination <- 100;
	
	//parametres pour les graphiques
	//moustiques
	list<point> mosquito_S_chart<-nil ;
	list<point> mosquito_E_chart<-nil ;
	list<point> mosquito_I_chart<-nil ;
	
	list<point> person_S_moves_chart<-nil ;
	list<point> person_E_moves_chart<-nil ;
	list<point> person_I_moves_chart<-nil ;
	
	
}
