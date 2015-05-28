/**
 *  parameters
 *  Author: Piou
 *  Description: 
 */

model parameters

/* Insert your model definition here */
global {
	//shapefiles 
	
	//environnement de travail
	//file environment_file <- shape_file('../includes/anhoa_quang/couverture.shp', 0);
	file environment_file <- shape_file('../includes/anhoa_quang_v2/couverture.shp', 0);
	
	//carte du quartier - // map an hoa (2005 et 2010)
	//file village_file <- shape_file('../includes/anhoa_quang/anhoa_2010.shp', 0);
	file village_file <- shape_file('../includes/anhoa_quang_v2/anhoa_2010.shp', 0);
	
	// LANDY
	//les hopitaux, marches et centre culturel: si le fichier n'existe pas, il faut mettre sa valeur a nil
	file hospital_file <- shape_file('../includes/anhoa_ancien/anhoa_2010_ajuste/anHoa2010_hospital.shp', 0);
	file cultural_file <- shape_file('../includes/anhoa_ancien/anhoa_2010_ajuste/anhoa2010_cultural.shp', 0);
	file market_file <- shape_file('../includes/anhoa_ancien/anhoa_2010_ajuste/anhoa2010_market.shp', 0);
	
	file river_file <- shape_file('../includes/map_tri/An_Hoa_River_1.shp', 0);
	file all_river_file <- shape_file('../includes/map_tri/Ninh_Kieu_River_2.shp', 0);
	file sewer_file <- shape_file('../includes/map_tri/An_hoa_Sewer.shp', 0);
	file mornitoring_file <- shape_file('../includes/map_tri/Mornitoring_sites.shp', 0);

	file categorie_destination_csv_file <- csv_file("../doc/data_generated/pt_destination.csv",";");//categorie des points de destination
	file pt_destination_csv_file <- csv_file("../doc/data_generated/distance_TA_pt_destination.csv",";");//ne pas changer
	
	//////////////////////////////////////////////
	
	geometry shape <- envelope(environment_file);
	
	/*
	 * Parameters for timming
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
	
	//////////////////////////////////////////////
	
	 /*
	 * Parameters for parcel
	 */
	// Risque selon le type de parcel
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
}

