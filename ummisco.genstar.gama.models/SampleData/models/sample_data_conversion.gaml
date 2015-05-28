/**
 *  sample_data_conversion
 *  Author: voducan
 *  Description: 
 */

model sample_data_conversion

global {
	init {
		string attribute_data <- '../includes/attributes.csv';
		string sample_data <- '../includes/PICURS_People_SampleData.csv';

		string distribution_format1 <- '../includes/distributionFormat1.csv';
		string resulting_distribution_file1 <- '../results/resultingDistribution1.csv';
		file distribution_result1 <- frequency_distribution_from_sample(attribute_data, sample_data, distribution_format1, resulting_distribution_file1);
		write 'Open ' + distribution_result1.path + ' to observe the result.';

		string distribution_format2 <- '../includes/distributionFormat2.csv';
		string resulting_distribution_file2 <- '../results/resultingDistribution2.csv';
		file distribution_result2 <- frequency_distribution_from_sample(attribute_data, sample_data, distribution_format2, resulting_distribution_file2);
		write 'Open ' + distribution_result2.path + ' to observe the result.';

		string distribution_format3 <- '../includes/distributionFormat3.csv';
		string resulting_distribution_file3 <- '../results/resultingDistribution3.csv';
		file distribution_result3 <- frequency_distribution_from_sample(attribute_data, sample_data, distribution_format3, resulting_distribution_file3);
		write 'Open ' + distribution_result3.path + ' to observe the result.';
	}	
}

experiment test type: gui {
	output {
		
	}
}