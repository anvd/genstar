/**
 *  extract_ipf_population
 *  Author: voducan
 *  Description: This is an example of using the "extract_ipf_population" operator to extract a sample from a population.
 * 				The extracted sample can then be used as input sample of the IPF algorithm (i.e., "ipf_population" operator)
 */

model extract_ipf_population_without_ID

global {

	string extracted_population_properties; 
	
	init {
		
		// 1. extract the generated Gen* population
		list extracted_population <- extract_ipf_population(extracted_population_properties);
		write 'Extracted ' + get_percentage() + ' percent(s) of the population  (without household ID)';
		
		// 2. create GAMA agents from the extracted Gen* population
		genstar_create synthetic_population: extracted_population;
		write 'Created GAMA agents from the extracted population';
		
		write 'To assess the extracted population with respect to the original population, use the population browser to observe the \'household\' species and open the CSV file containing the original population.';
	}
	
	int get_percentage {
		if (extracted_population_properties = '../includes/household_without_ID/ExtractedIpfPopulationProperties_without_ID_OnePercent.properties') {
			return 1;
		} else if (extracted_population_properties = '../includes/household_without_ID/ExtractedIpfPopulationProperties_without_ID_TenPercents.properties') {
			return 10;
		} else if (extracted_population_properties = '../includes/household_without_ID/ExtractedIpfPopulationProperties_without_ID_ThirtyPercents.properties') {
			return 30;
		}
		
		return -1;
	}
}


species household {
	int householdSize;
	string householdIncome;
	string householdType;
}


experiment extract1_percent_ipf_population type: gui {

	parameter "extracted_population_properties" var: extracted_population_properties <- '../includes/household_without_ID/ExtractedIpfPopulationProperties_without_ID_OnePercent.properties'; 

	output {
	}
}

experiment extract_10_percents_ipf_population type: gui {

	parameter "extracted_population_properties" var: extracted_population_properties <- '../includes/household_without_ID/ExtractedIpfPopulationProperties_without_ID_TenPercents.properties'; 

	output {
		
	}
}

experiment extract_30_percents_ipf_population type: gui {

	parameter "extracted_population_properties" var: extracted_population_properties <- '../includes/household_without_ID/ExtractedIpfPopulationProperties_without_ID_ThirtyPercents.properties'; 

	output {
		
	}
}