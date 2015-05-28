/**
 *  classefoyer
 *  Author: Piou
 *  Description: 
 */

model classefoyer

import "classe_person.gaml"

species foyer{
	bool water;
	bool toilet;
	//string type_water_quantity;
	int water_quantity;
	int nb_stagnant_water;
	//list<cloud_of_mosquitoes> packages_mosquito;
	int nb_mosquito;
	float size <- 40#m;
	list<person> persons;
	string type_foyer;
	
	aspect geometry{
		draw square(size) color : #red;
	}
}

global {
	/** Insert the global definitions, variables and actions here */
}
