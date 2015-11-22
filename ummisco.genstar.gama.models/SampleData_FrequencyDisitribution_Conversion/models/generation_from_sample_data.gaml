/**
 *  generation_from_sample_data
 *  Author: voducan
 *  Description: 
 */

model generation_from_sample_data

global {
	int scenario <- 0;
	
	init {
		int nb_of_people <- 324;
		string attribute_data <- '../includes/attributes.csv';
		string sample_data <- '../includes/PICURS_People_SampleData.csv';
		file distribution_result_file;
		string generation_rule_file;
		

		switch scenario {
			match 1 {
				write ('Scenario 1');
				string distribution_format1 <- '../includes/distributionFormat1.csv';
				string resulting_distribution_file1 <- '../results/resultingDistribution1.csv';
				distribution_result_file <- frequency_distribution_from_sample(attribute_data, sample_data, distribution_format1, resulting_distribution_file1);
				generation_rule_file <- '../includes/generationRule1.csv';
			}
			
			match 2 {
				write ('Scenario 2');
				string distribution_format2 <- '../includes/distributionFormat2.csv';
				string resulting_distribution_file2 <- '../results/resultingDistribution2.csv';
				distribution_result_file <- frequency_distribution_from_sample(attribute_data, sample_data, distribution_format2, resulting_distribution_file2);
				generation_rule_file <- '../includes/generationRule2.csv';
			}
			
			match 3 {
				write ('Scenario 3');
				string distribution_format3 <- '../includes/distributionFormat3.csv';
				string resulting_distribution_file3 <- '../results/resultingDistribution3.csv';
				distribution_result_file <- frequency_distribution_from_sample(attribute_data, sample_data, distribution_format3, resulting_distribution_file3);
				generation_rule_file <- '../includes/generationRule3.csv';
			}
		}
		
		
		list people_population <- population_from_csv(attribute_data, generation_rule_file, nb_of_people);
		create people from: people_population;
		do analyse_result(distribution_result_file);
	}	
	
	action analyse_result(file result_file) {
		list<string> attributesWithFrequency <- result_file.attributes;
		
		// First attribute: Work
		// Second attribute: Age
		// Third attribute: Gender
		
		matrix<string> contents <- matrix<string>(result_file.contents);
		
		// write attribute names
		string str <- '';
		loop a over: attributesWithFrequency { str <- str + a + ','; }
		str <- str + 'Generated Frequency';
		write str;

		switch length(result_file.attributes) {
			match 2 {
				loop aRow over: rows_list(contents) {
					write string(aRow at 0) + "," + string(aRow at 1) + "," + string ( length(people where (each.work = string(aRow at 0)) ) );
				}
			}
			
			match 3 {
				loop aRow over: rows_list(contents) {
					string ageStr <- string(aRow at 1);
					list<string> minMaxAge <- tokenize(ageStr, ':');
					int minAge <- minMaxAge at 0;
					int maxAge <- minMaxAge at 1;
					
					write string(aRow at 0) + "," + string(aRow at 1) + "," + string(aRow at 2) + "," + string ( length(people where ( (each.work = string(aRow at 0)) and (each.age >= minAge) and (each.age <= maxAge)  ) ) );
				}
			}
			
			match 4 {
				loop aRow over: rows_list(contents) {
					string ageStr <- string(aRow at 1);
					list<string> minMaxAge <- tokenize(ageStr, ':');
					int minAge <- minMaxAge at 0;
					int maxAge <- minMaxAge at 1;
					
					write string(aRow at 0) + "," + string(aRow at 1) + "," + string(aRow at 2) + "," + string(aRow at 3) + "," + string ( length(people where ( (each.work = string(aRow at 0)) and (each.age >= minAge) and (each.age <= maxAge) and (each.gender = bool(string(aRow at 2)))  ) ) );
				}
			}
		}
	}
}

species people {
	string work;
	int age;
	bool gender;
}

experiment Scenario1 type: gui {
	parameter 'Scenario' var: scenario <- 1;
	
	output {
		
	}
}

experiment Scenario2 type: gui {
	parameter 'Scenario' var: scenario <- 2;

	output {
		
	}
}

experiment Scenario3 type: gui {
	parameter 'Scenario' var: scenario <- 3;

	output {
		
	}
}