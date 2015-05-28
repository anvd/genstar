/**
 *  classestagnantwater
 *  Author: Piou
 *  Description: 
 */

model classestagnantwater

import "classe_foyer.gaml"

species stagnant_water{
	float width <- 0.5#m;
	float length <- 1#m;
	int nb_eggs <-0;
	foyer myFoyer;
	Parcel myParcel;
	aspect geom{
		draw rectangle(width,length) color : #blue;
	}
}

global {
	/** Insert the global definitions, variables and actions here */
}
