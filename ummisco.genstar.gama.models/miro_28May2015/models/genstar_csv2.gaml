/**
 *  miro_genstar_csv
 *  Author: voducan
 *  Description: 
 */

model miro_genstar_csv

/* Insert your model definition here */

global {
	file shape_file_buildings <- file("../includes/grenoble/bati/bati_zones_elargies.shp");
	file shape_file_roads <- file("../includes/grenoble/reseau_grenoble/ROUTE_ADRESSE.shp");
	//file shape_file_bounds <- file("../includes/gis/bounds.shp");
	matrix<int> fichier_liste <- matrix<int> (csv_file ("../includes/LISTE_ZONES_HORS_CHAMP.csv"));
	//geometry shape <- envelope(shape_file_bounds);

	graph the_graph;
	list<people> liste_to_generate <- [] ;
	int nb_of_people <- 100;
	list<building> living_list ;
	list<building>  working_list;
	
	init {
			
			create species: building from:shape_file_buildings with: [buildingUsage::string(read('fonction')), buildingId::int(read('ID_bati')),ao1::int(read('Ao1')),ao2::int(read('Ao2')),ac1::int(read('Ac1')),ac2::int(read('Ac2')), zoneB::int(string(read('2010'))) ]
		{
			if condition: buildingUsage in [ "Industriel ou commercial", "Industrielle", "Gestion des eaux"]  {
				color <- rgb('blue') ;
				buildingType <- 2;
				buildingActivity <- "T";
			} else
				if condition: buildingUsage ="Administratif" {
				color <- rgb('green') ;
				buildingType <- 2;
				buildingActivity <- "C";
			} else	
			if condition: buildingUsage ="Commerciale" {
				color <- rgb('green') ;
				buildingType <- 2;
				buildingActivity <- "A";
			} else			
			if condition: buildingUsage ="Culture et loisirs" {
				color <- rgb('green') ;
				buildingType <- 3;
				buildingActivity <- "L";
			} else
			if condition: buildingUsage="Enseignement" {
				color <- rgb('red') ;
				buildingType <- 3;
				buildingActivity <- "E";
			} else
			if condition: buildingUsage in ["Sant" ] {
				color <- rgb('pink') ;
				buildingType <- 3;
				buildingActivity <- "S";
			} else
			if condition: buildingUsage in ["Sport", "Sportive" ] {
				color <- rgb('red') ;
				buildingType <- 2;
				buildingActivity <- "L";
			} else
			if condition: buildingUsage in ["Service" ] {
				color <- rgb('red') ;
				buildingType <- 2;
				buildingActivity <- "R";
			} else
			 {
				color <- rgb('gray') ;
				buildingType <- 1;
				buildingActivity <- "W";
			} 
			}
		
			create road from: shape_file_roads ;

			list miro_population <- list<unknown>(population_from_csv('../includes/population_VP/MIRO_Attributes_MetaData.csv', '../includes/population_VP/MIRO_GenerationRules_MetaData.csv', nb_of_people));
			living_list <-(building as list) where (each.buildingType = 1);
			working_list <-(building as list) where (each.buildingType != 1);
			
			create people from: miro_population {
			int compteur <- 0;
			ID_habitant <- list(people) index_of self;
			
			// Modification de la zone : correction par rapport aux données d'enquête *************
			loop temp_col over: columns_list(fichier_liste) {
			if ((zone in (temp_col)) and (compteur != 0))
			{
				if compteur = 1
				{zone <- 513;}
				if compteur =2
				{zone <- 302;}
				if compteur =3
				{zone <- 403;}
				if compteur =4
				{zone <- 802;}
			}
			compteur <- compteur + 1 ;
			}
			// *************************************************************************************
			
			living_place <- one_of((living_list) where (each.zoneB = zone));
			if actp = "W"
			{working_place <-one_of(working_list where (each.buildingActivity = "L" or "E" or "S")) ;}
			else
			{working_place <- one_of(working_list where (each.buildingActivity = actp)) ;}
			
			location <- (living_place.shape).location;
			homeID <- string(living_place.buildingId);
			Activite_ID <- string(working_place.buildingId);
			
			}
		
				
			write 'Quick analyse of the generation population:';
			write 'Number of people: ' + string(length(miro_population) - 1);
			
			write '\tClassification of gender: ';
			write '\t\tmale: ' + length(people where (each.gender=1));
			write '\t\tfemale: ' + length(people where (each.gender = 2));
			
			write '\tClassification of category: ';
			write '\t\tC0: ' + length(people where (each.category = 0));
			write '\t\tC1: ' + length(people where (each.category = 1));
			write '\t\tC2: ' + length(people where (each.category = 2));
			write '\t\tC3: ' + length(people where (each.category = 3));
			write '\t\tC4: ' + length(people where (each.category = 4));
			write '\t\tC5: ' + length(people where (each.category = 5));
			write '\t\tC6: ' + length(people where (each.category = 6));
			write '\t\tC7: ' + length(people where (each.category = 7));
			
			write '\tClassification of age:';
			write '\t\t[0:4]: ' + length(people where ((each.age >= 0) and (each.age <= 4)));
			write '\t\t[5:17]: ' + length(people where ((each.age >= 5) and (each.age <= 17)));
			write '\t\t[18:24]: ' + length(people where ((each.age >= 18) and (each.age <= 24)));
			write '\t\t[25:34]: ' + length(people where ((each.age >= 25) and (each.age <= 34)));
			write '\t\t[34:49]: ' + length(people where ((each.age >= 34) and (each.age <= 49)));
			write '\t\t[50:64]: ' + length(people where ((each.age >= 50) and (each.age <= 64)));
			write '\t\t[65:100]: ' + length(people where (each.age >= 65));

//			
//			set liste_to_generate <- (people as list) where (each.zone in(liste_zones)) ;
//			
//			ask liste_to_generate {
//				save [gender, category, age, zone, actp] to: "../includes/population_validation/pop100000inside_validation.csv" type: "csv" ;
//				
//			}
			
		   	ask people {
         		save [ID_habitant, gender, category, age, zone, actp, homeID, Activite_ID, vtype ] to: "../includes/pop100.csv" type: "csv";
  				}
      
		}
	
	
}

species building {
	string type; 
	rgb color <- #gray  ;
	int buildingType ;
	string buildingUsage;
	string buildingActivity;
	int zoneB;
	int buildingId;
	aspect base {
		draw shape color: color ;
	}
}

species road  {
	float destruction_coeff <- 1 + ((rnd(100))/ 100.0) max: 2.0;
	int colorValue <- int(255*(destruction_coeff - 1)) update: int(255*(destruction_coeff - 1));
	rgb color <- rgb(min([255, colorValue]),max ([0, 255 - colorValue]),0)  update: rgb(min([255, colorValue]),max ([0, 255 - colorValue]),0) ;
	
	aspect base {
		draw shape color: color ;
	}
}

species people {
	
	int category;
	int gender;
	int age;
	int zone;
	string actp;
	int ID_habitant <- 0;
	building living_place <- nil ;
	building working_place <- nil ;
	string homeID <- " " ;
	string Activite_ID <- " " ;
	int vtype;
	
	
	
	aspect base {
		draw circle(10) color: #yellow;
	}
}
environment  bounds: shape_file_roads; 
experiment test_miro type: gui {
	output {
		display miro_display {
			species road aspect: base;
			species building aspect:base;
			species people aspect: base;
		}
//		file fichier_test type : text data: ("Quick analyse of the generated population: 
//\t Number of people: " + string(length(people) - 1) + "
//\t Classification of gender:" + '
//
//\t\tmale: ' + length(people where each.gender) + '
//\t\tfemale: ' + length(people where != each.gender) + '
//
//\t Pyramide des âges :' + '
//
//\t\t\t\t maleX[0-4] ' + length (people where ((each.gender) and (each.age >= 0) and (each.age <= 4))) + '
//\t\t\t\t maleX[5-17] ' + length (people where ((each.gender) and (each.age >= 5) and (each.age <= 17))) + '
//\t\t\t\t maleX[18-24] ' + length (people where ((each.gender) and (each.age >= 18) and (each.age <= 24))) + '
//\t\t\t\t maleX[25-34] ' + length (people where ((each.gender) and (each.age >= 25) and (each.age <= 34))) + '
//\t\t\t\t maleX[34-49] ' + length (people where ((each.gender) and (each.age >= 34) and (each.age <= 49))) + '
//\t\t\t\t maleX[50-64] ' + length (people where ((each.gender) and (each.age >= 50) and (each.age <= 64))) + '
//\t\t\t\t maleX[65[ ' + length (people where ((each.gender) and (each.age >= 65))) + '
//
//\t\t\t\t femaleX[0-4] ' + length (people where ((!each.gender) and (each.age >= 0) and (each.age <= 4))) + '
//\t\t\t\t femaleX[5-17] ' + length (people where ((!each.gender) and (each.age >= 5) and (each.age <= 17))) + '
//\t\t\t\t femaleX[18-24] ' + length (people where ((!each.gender) and (each.age >= 18) and (each.age <= 24))) + '
//\t\t\t\t femaleX[25-34] ' + length (people where ((!each.gender) and (each.age >= 25) and (each.age <= 34))) + '
//\t\t\t\t femaleX[34-49] ' + length (people where ((!each.gender) and (each.age >= 34) and (each.age <= 49))) + '
//\t\t\t\t femaleX[50-64] ' + length (people where ((!each.gender) and (each.age >= 50) and (each.age <= 64))) + '
//\t\t\t\t femaleX[65[ ' + length (people where ((!each.gender) and (each.age >= 65))) + '
//
//\t Classification of categories :' + '
//
//\t\tC0: ' + length(people where (each.category = 0)) + '
//\t\t\t\t C0Xmale ' + length (people where ((each.category = 0) and (each.gender))) + '
//\t\t\t\t C0Xfemale ' + length (people where ((each.category = 0) and (!each.gender))) + '
//\t\t\t\t C0X[0-4] ' + length (people where ((each.category = 0) and (each.age >= 0) and (each.age <= 4))) + '
//\t\t\t\t C0X[5-17] ' + length (people where ((each.category = 0) and (each.age >= 5) and (each.age <= 17))) + '
//\t\t\t\t C0X[18-24] ' + length (people where ((each.category = 0) and (each.age >= 18) and (each.age <= 24))) + '
//\t\t\t\t C0X[25-34] ' + length (people where ((each.category = 0) and (each.age >= 25) and (each.age <= 34))) + '
//\t\t\t\t C0X[34-49] ' + length (people where ((each.category = 0) and (each.age >= 34) and (each.age <= 49))) + '
//\t\t\t\t C0X[50-64] ' + length (people where ((each.category = 0) and (each.age >= 50) and (each.age <= 64))) + '
//\t\t\t\t C0X[65[ ' + length (people where ((each.category = 0) and (each.age >= 65))) + '
//
//\t\tC1: ' + length(people where (each.category = 1)) + '
//\t\t\t\t C1Xmale ' + length (people where ((each.category = 1) and (each.gender))) + '
//\t\t\t\t C1Xfemale ' + length (people where ((each.category = 1) and (!each.gender))) + '
//\t\t\t\t C1X[0-4] ' + length (people where ((each.category = 1) and (each.age >= 0) and (each.age <= 4))) + '
//\t\t\t\t C1X[5-17] ' + length (people where ((each.category = 1) and (each.age >= 5) and (each.age <= 17))) + '
//\t\t\t\t C1X[18-24] ' + length (people where ((each.category = 1) and (each.age >= 18) and (each.age <= 24))) + '
//\t\t\t\t C1X[25-34] ' + length (people where ((each.category = 1) and (each.age >= 25) and (each.age <= 34))) + '
//\t\t\t\t C1X[34-49] ' + length (people where ((each.category = 1) and (each.age >= 34) and (each.age <= 49))) + '
//\t\t\t\t C1X[50-64] ' + length (people where ((each.category = 1) and (each.age >= 50) and (each.age <= 64))) + '
//\t\t\t\t C1X[65[ ' + length (people where ((each.category = 1) and (each.age >= 65))) + '
//
//
//\t\tC2: ' + length(people where (each.category = 2)) + '
//\t\t\t\t C2Xmale ' + length (people where ((each.category = 2) and (each.gender))) + '
//\t\t\t\t C2Xfemale ' + length (people where ((each.category = 2) and (!each.gender))) + '
//\t\t\t\t C2X[0-4] ' + length (people where ((each.category = 2) and (each.age >= 0) and (each.age <= 4))) + '
//\t\t\t\t C2X[5-17] ' + length (people where ((each.category = 2) and (each.age >= 5) and (each.age <= 17))) + '
//\t\t\t\t C2X[18-24] ' + length (people where ((each.category = 2) and (each.age >= 18) and (each.age <= 24))) + '
//\t\t\t\t C2X[25-34] ' + length (people where ((each.category = 2) and (each.age >= 25) and (each.age <= 34))) + '
//\t\t\t\t C2X[34-49] ' + length (people where ((each.category = 2) and (each.age >= 34) and (each.age <= 49))) + '
//\t\t\t\t C2X[50-64] ' + length (people where ((each.category = 2) and (each.age >= 50) and (each.age <= 64))) + '
//\t\t\t\t C2X[65[ ' + length (people where ((each.category = 2) and (each.age >= 65))) + '
//
//
//\t\tC3: ' + length(people where (each.category = 3)) + '
//\t\t\t\t C3Xmale ' + length (people where ((each.category = 3) and (each.gender))) + '
//\t\t\t\t C3Xfemale ' + length (people where ((each.category = 3) and (!each.gender))) + '
//\t\t\t\t C3X[0-4] ' + length (people where ((each.category = 3) and (each.age >= 0) and (each.age <= 4))) + '
//\t\t\t\t C3X[5-17] ' + length (people where ((each.category = 3) and (each.age >= 5) and (each.age <= 17))) + '
//\t\t\t\t C3X[18-24] ' + length (people where ((each.category = 3) and (each.age >= 18) and (each.age <= 24))) + '
//\t\t\t\t C3X[25-34] ' + length (people where ((each.category = 3) and (each.age >= 25) and (each.age <= 34))) + '
//\t\t\t\t C3X[34-49] ' + length (people where ((each.category = 3) and (each.age >= 34) and (each.age <= 49))) + '
//\t\t\t\t C3X[50-64] ' + length (people where ((each.category = 3) and (each.age >= 50) and (each.age <= 64))) + '
//\t\t\t\t C3X[65[ ' + length (people where ((each.category = 3) and (each.age >= 65))) + '
//
//\t\tC4: ' + length(people where (each.category = 4)) + '
//\t\t\t\t C4Xmale ' + length (people where ((each.category = 4) and (each.gender))) + '
//\t\t\t\t C4Xfemale ' + length (people where ((each.category = 4) and (!each.gender))) + '
//\t\t\t\t C4X[0-4] ' + length (people where ((each.category = 4) and (each.age >= 0) and (each.age <= 4))) + '
//\t\t\t\t C4X[5-17] ' + length (people where ((each.category = 4) and (each.age >= 5) and (each.age <= 17))) + '
//\t\t\t\t C4X[18-24] ' + length (people where ((each.category = 4) and (each.age >= 18) and (each.age <= 24))) + '
//\t\t\t\t C4X[25-34] ' + length (people where ((each.category = 4) and (each.age >= 25) and (each.age <= 34))) + '
//\t\t\t\t C4X[34-49] ' + length (people where ((each.category = 4) and (each.age >= 34) and (each.age <= 49))) + '
//\t\t\t\t C4X[50-64] ' + length (people where ((each.category = 4) and (each.age >= 50) and (each.age <= 64))) + '
//\t\t\t\t C4X[65[ ' + length (people where ((each.category = 4) and (each.age >= 65))) + '
//
//\t\tC5: ' + length(people where (each.category = 5)) + '
//\t\t\t\t C5Xmale ' + length (people where ((each.category = 5) and (each.gender))) + '
//\t\t\t\t C5Xfemale ' + length (people where ((each.category = 5) and (!each.gender))) + '
//\t\t\t\t C5X[0-4] ' + length (people where ((each.category = 5) and (each.age >= 0) and (each.age <= 4))) + '
//\t\t\t\t C5X[5-17] ' + length (people where ((each.category = 5) and (each.age >= 5) and (each.age <= 17))) + '
//\t\t\t\t C5X[18-24] ' + length (people where ((each.category = 5) and (each.age >= 18) and (each.age <= 24))) + '
//\t\t\t\t C5X[25-34] ' + length (people where ((each.category = 5) and (each.age >= 25) and (each.age <= 34))) + '
//\t\t\t\t C5X[34-49] ' + length (people where ((each.category = 5) and (each.age >= 34) and (each.age <= 49))) + '
//\t\t\t\t C5X[50-64] ' + length (people where ((each.category = 5) and (each.age >= 50) and (each.age <= 64))) + '
//\t\t\t\t C5X[65[ ' + length (people where ((each.category = 5) and (each.age >= 65))) + '
//
//\t\tC6: ' + length(people where (each.category = 6)) + '
//\t\t\t\t C6Xmale ' + length (people where ((each.category = 6) and (each.gender))) + '
//\t\t\t\t C6Xfemale ' + length (people where ((each.category = 6) and (!each.gender))) + '
//\t\t\t\t C6X[0-4] ' + length (people where ((each.category = 6) and (each.age >= 0) and (each.age <= 4))) + '
//\t\t\t\t C6X[5-17] ' + length (people where ((each.category = 6) and (each.age >= 5) and (each.age <= 17))) + '
//\t\t\t\t C6X[18-24] ' + length (people where ((each.category = 6) and (each.age >= 18) and (each.age <= 24))) + '
//\t\t\t\t C6X[25-34] ' + length (people where ((each.category = 6) and (each.age >= 25) and (each.age <= 34))) + '
//\t\t\t\t C6X[34-49] ' + length (people where ((each.category = 6) and (each.age >= 34) and (each.age <= 49))) + '
//\t\t\t\t C6X[50-64] ' + length (people where ((each.category = 6) and (each.age >= 50) and (each.age <= 64))) + '
//\t\t\t\t C6X[65[ ' + length (people where ((each.category = 6) and (each.age >= 65))) + '
//
//\t\tC7: ' + length(people where (each.category = 7)) + '
//\t\t\t\t C7Xmale ' + length (people where ((each.category = 7) and (each.gender))) + '
//\t\t\t\t C7Xfemale ' + length (people where ((each.category = 7) and (!each.gender))) + '
//\t\t\t\t C7X[0-4] ' + length (people where ((each.category = 7) and (each.age >= 0) and (each.age <= 4))) + '
//\t\t\t\t C7X[5-17] ' + length (people where ((each.category = 7) and (each.age >= 5) and (each.age <= 17))) + '
//\t\t\t\t C7X[18-24] ' + length (people where ((each.category = 7) and (each.age >= 18) and (each.age <= 24))) + '
//\t\t\t\t C7X[25-34] ' + length (people where ((each.category = 7) and (each.age >= 25) and (each.age <= 34))) + '
//\t\t\t\t C7X[34-49] ' + length (people where ((each.category = 7) and (each.age >= 34) and (each.age <= 49))) + '
//\t\t\t\t C7X[50-64] ' + length (people where ((each.category = 7) and (each.age >= 50) and (each.age <= 64))) + '
//\t\t\t\t C7X[65[ ' + length (people where ((each.category = 7) and (each.age >= 65))) 
//
//
// );
		
	}
}