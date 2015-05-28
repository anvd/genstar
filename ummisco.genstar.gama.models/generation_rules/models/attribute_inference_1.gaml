/**
 *  attribute_inference_1
 *  Author: voducan
 *  Description: 
 */

model attribute_inference_1

global {
	int nb_of_people <- 100;
	
	file salary_data_file <- csv_file("../includes/population/example1/Salary_Data.csv", ",");
	map<string, list<int>> salary_data;
	init {
		list employee_population <- population_from_csv('../includes/population/example1/attributes.csv', '../includes/population/example1/generation_rules.csv', nb_of_people);
		create employee from: employee_population returns: new_employees;
		
		matrix data <- matrix(salary_data_file);
		loop aRow over: rows_list(data) {
			add tokenize( string(aRow at 1), ":" ) at: (aRow at 0) to: salary_data;
		}
		
		loop k over: salary_data.keys {
			list<int> range <- list<int>(salary_data at k);
			list concerning_employees <- new_employees where (each.type = k);
			list violated_salary_range_employees <- concerning_employees where ( (each.salary < (range at 0)) or (each.salary > (range at 1)) );
			write 'All ' + k + 's have correct salary range: ' + empty(violated_salary_range_employees);
		}		
	}
}

species employee {
	string type;
	float salary;
	
	aspect base {
		draw circle(1) color: #green;
		draw text: name + ' works as ' + type + ' with salary ' + string(salary) at: location;
	}
}

experiment test type: gui {
	output {
		display empployee_view {
			species employee aspect: base;
		}
	}
}