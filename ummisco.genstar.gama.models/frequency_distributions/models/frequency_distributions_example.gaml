/**
 *  frequency_distributions_example
 *  Author: voducan
 *  Description: This is an example of using the "frequency_distributions" operator to generate frequency distribution(s) then save the result to CSV file(s)
 */

model frequency_distributions_example

global {
	int scenario;
	string properties_file_path;
	
	init {
		
		if (scenario = 1) {
			string resultFilePath <- '../includes/scenario_1/resultDistribution1.csv'; 
			if (file_exists(resultFilePath)) {
				write 'Result file already existed. Please remove \'' + resultFilePath + '\' file then re-run the experiment' ;
			} else {
				list<string> resultingFilePaths <- frequency_distributions(properties_file_path);
				write 'Please open \'' + resultFilePath + '\' to observe the outcome.';
			}
		}
		
		if (scenario = 2) {
			string resultFilePath1 <- '../includes/scenario_2/resultDistribution1.csv'; 
			string resultFilePath2 <- '../includes/scenario_2/resultDistribution2.csv'; 
			if (file_exists(resultFilePath1) or file_exists(resultFilePath2)) {
				write 'Result files already existed. Please remove \'' + resultFilePath1 + '\' and/or ' + resultFilePath2 + ' file(s) then re-run the experiment' ;
			} else {
				list<string> resultingFilePaths <- frequency_distributions(properties_file_path);
				write 'Please open \'' + resultFilePath1 + '\' and ' + resultFilePath2 + ' to observe the outcome.';
			}
		}
		
		if (scenario = 3) {
			string resultFilePath1 <- '../includes/scenario_3/resultDistribution1.csv'; 
			string resultFilePath2 <- '../includes/scenario_3/resultDistribution2.csv'; 
			string resultFilePath3 <- '../includes/scenario_3/resultDistribution3.csv'; 
			if (file_exists(resultFilePath1) or file_exists(resultFilePath2) or file_exists(resultFilePath3)) {
				write 'Result files already existed. Please remove \'' + resultFilePath1 + '\' and/or ' + resultFilePath2 + '\' and/or ' + resultFilePath3 + ' file(s) then re-run the experiment' ;
			} else {
				list<string> resultingFilePaths <- frequency_distributions(properties_file_path);
				write 'Please open \'' + resultFilePath1 + '\' and ' + resultFilePath2 + '\' and ' + resultFilePath3 + ' to observe the outcome.';
			}
		}

	}
}

experiment scenario_1 type: gui {
	
	parameter 'Scenario' var: scenario <- 1;
	parameter 'Properties file path' var: properties_file_path <- '../includes/scenario_1/frequency_distributions.properties';
	
	output {
		
	}
}

experiment scenario_2 type: gui {

	parameter 'Scenario' var: scenario <- 2;
	parameter 'Properties file path' var: properties_file_path <- '../includes/scenario_2/frequency_distributions.properties';

	output {
		
	}
}

experiment scenario_3 type: gui {

	parameter 'Scenario' var: scenario <- 3;
	parameter 'Properties file path' var: properties_file_path <- '../includes/scenario_3/frequency_distributions.properties';

	output {
		
	}
}