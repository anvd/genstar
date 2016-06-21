/**
 *  extract_ipu_population_example
 *  Author: voducan
 *  Description: This is an example of using the "extract_ipf_compound_population" operator to extract a sample from a population.
 * 				The extracted sample can then be used as input sample of the IPF algorithm to generate a compound synthetic population (i.e., "ipf_compound_population" operator)
 */

model extract_ipf_population_example

global {
	
	string extracted_population_properties; 
	
	init {

		// 1. extract the generated Gen* population
		list extracted_population <- extract_ipf_compound_population(extracted_population_properties);
		write 'Extracted ' + get_percentage() + ' percent(s) of the compound population';
				
		// 2. create GAMA agents from the extracted Gen* population
		genstar_create synthetic_population: extracted_population;
		write 'Created GAMA agents from the extracted population';
		
		write 'To further assess the extracting result, open original population in \'../includes/household_population.csv\' and browse the \'household\' population';
	}
	
	
	float get_percentage {
		if (extracted_population_properties = '../includes/ExtractedIpfCompoundPopulationProperties_OnePercent.properties') {
			return 1;
		} else if (extracted_population_properties = '../includes/ExtractedIpfCompoundPopulationProperties_TenPercents.properties') {
			return 10;
		} else if (extracted_population_properties = '../includes/ExtractedIpfCompoundPopulationProperties_ThirtyPercents.properties') {
			return 30;
		}
		
		return -1;
	}
}

species household {
	int householdID;
	int householdSize;
	string householdIncome;
	string householdType;
	int numberOfCars;
	
	list<people> inhabitants;
}

species people {
	int age;
	bool gender;
	string work;
	int householdID;
	
	household my_household;
}

experiment extract_1_percent_ipf_compound_population type: gui {
	parameter "extracted_population_properties" var: extracted_population_properties <- '../includes/ExtractedIpfCompoundPopulationProperties_OnePercent.properties'; 

	output {
	}
}

experiment extract_10_percents_ipf_compound_population type: gui {
	parameter "extracted_population_properties" var: extracted_population_properties <- '../includes/ExtractedIpfCompoundPopulationProperties_TenPercents.properties'; 

	output {
		
	}
}

experiment extract_30_percents_ipf_compound_population type: gui {
	parameter "extracted_population_properties" var: extracted_population_properties <- '../includes/ExtractedIpfCompoundPopulationProperties_ThirtyPercents.properties'; 

	output {
		
	}
}