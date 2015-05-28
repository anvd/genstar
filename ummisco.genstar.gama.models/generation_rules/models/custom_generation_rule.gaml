/**
 *  custom_generation_rule
 *  Author: voducan
 *  Description: 
 */

model custom_generation_rule

/* Insert your model definition here */

global {
	init {
		int nb_of_people <- 7150;
		
//		list miro_people1_population <- population_from_csv('../includes/population/custom_rule/attributes_1.csv', '../includes/population/custom_rule/custom_generation_rules.csv', nb_of_people);
//		create people1 from: miro_people1_population returns: generated_people;

		list miro_people2_population <- population_from_csv('../includes/population/custom_rule/attributes_2.csv', '../includes/population/custom_rule/custom_generation_rules.csv', nb_of_people);
//		create people2 from: miro_people2_population returns: generated_people;
		
		// TODO generation result analysis
		
	}
}

species people1 {
	int age;
	
	aspect default {
		draw shape color: #blue;
	}
}

species people2 {
	int age;

	aspect default {
		draw shape color: #green;
	}
}

experiment test_custom_rule type: gui {
	output {
		display people_display {
			species people1;
			species people2;
		}
	}
}