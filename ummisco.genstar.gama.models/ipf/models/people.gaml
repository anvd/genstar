/**
 *  people
 *  Author: voducan
 *  Description: 
 */

model people

global {
	
	init {
	 	write 'Start generating \'people\' population ... ';
	 	float begining_time <- machine_time;
		list people_population <- ipf_population('../includes/people/ipf_configuration.properties');
		write 'Finished generating the population. Duration: ' + (machine_time - begining_time) + ' miliseconds';

		genstar_create synthetic_population: people_population;
		
		// analyze the generated population 
		write 'Analyze generation result ...';
		string attributesFilePath <- '../includes/people/attributes.csv';
		string controlledAttributesListFilePath <- '../includes/people/controlled_attributes.csv';
		string controlTotalsFilePath <- '../includes/people/controlled_totals.csv';
		bool writeResultToConsole <- true;
		list<int> analysisResult <- analyse_ipf_population(people_population, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath, writeResultToConsole);
	}
}

species people {
	int age;
	bool gender;
	string work;
}

experiment people_ipf_2_ways type: gui {
	output {
		
	}
}