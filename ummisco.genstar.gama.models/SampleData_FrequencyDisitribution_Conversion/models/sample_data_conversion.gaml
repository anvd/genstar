/**
 *  sample_data_conversion
 *  Author: voducan
 *  Description: 
 */

model sample_data_conversion

global {
	int scenario <- 0;

	init {
		
		string attribute_data;
		string sample_data <- '../includes/PICURS_People_SampleData.csv';
		file distribution_result_file;
		
		switch scenario {
			match 1 {
				write ('Scenario 1');
				attribute_data <- '../includes/scenario1/attributes1.csv';
				string distribution_format1 <- '../includes/scenario1/distributionFormat1.csv';
				string resulting_distribution_file1 <- '../results/resultingDistribution1.csv';
				distribution_result_file <- frequency_distribution_from_sample(attribute_data, sample_data, distribution_format1, resulting_distribution_file1);
			}
			
			match 2 {
				write ('Scenario 2');
				attribute_data <- '../includes/scenario2/attributes2.csv';
				string distribution_format2 <- '../includes/scenario2/distributionFormat2.csv';
				string resulting_distribution_file2 <- '../results/resultingDistribution2.csv';
				distribution_result_file <- frequency_distribution_from_sample(attribute_data, sample_data, distribution_format2, resulting_distribution_file2);
			}
			
			match 3 {
				write ('Scenario 3');
				attribute_data <- '../includes/scenario3/attributes3.csv';
				string distribution_format3 <- '../includes/scenario3/distributionFormat3.csv';
				string resulting_distribution_file3 <- '../results/resultingDistribution3.csv';
				distribution_result_file <- frequency_distribution_from_sample(attribute_data, sample_data, distribution_format3, resulting_distribution_file3);
			}
		}
				
		write 'Open ' + distribution_result_file.path + ' to observe the result.';
	}	
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