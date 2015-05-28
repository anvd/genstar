/**
 *  test_csv
 *  Author: voducan
 *  Description: 
 */

model test_csv

global {
	init {
		// csv_file("../includes/iris.csv",",");
		matrix<string, string> csv_file_content <- matrix<string, string> (csv_file("../includes/test.csv", ","));
		
		write length(csv_file_content at {0, 0});
	}
}

experiment test type: gui {
	output {}
}