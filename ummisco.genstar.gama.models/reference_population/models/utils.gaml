/**
 *  utils
 *  Author: voducan
 *  Description: 
 */

model utils

global {
	
	// save the population(s) to CSV file(s)
	action save_population(list populationToSave, string groupPopulationName, string componentPopulationName,
		string groupPopulationOutputFile, string componentPopulationOutputFile, string groupAttributesCSVFilePath,
		string componentAttributesCSVFile, string groupIdOnGroup, string groupIdOnComponent) {
		
		map<string, string> generatedCompoundPopulationFilePaths;
		put groupPopulationOutputFile at: groupPopulationName in: generatedCompoundPopulationFilePaths;
		if (componentPopulationName != nil) {
			put componentPopulationOutputFile at: componentPopulationName in:  generatedCompoundPopulationFilePaths;
		}
		
		map<string, string> populationAttributesFilePaths;
		put groupAttributesCSVFilePath at: groupPopulationName in: populationAttributesFilePaths;
		if (componentPopulationName != nil) {
			put componentAttributesCSVFile at: componentPopulationName in: populationAttributesFilePaths;
		}
		
		map<string, string> populationIdAttributes;
		if (groupIdOnGroup != nil) {
			put groupIdOnGroup at: groupPopulationName in: populationIdAttributes;
		}
		if (componentPopulationName != nil and groupIdOnComponent != nil) {
			put groupIdOnComponent at:  componentPopulationName in: populationIdAttributes;
		}

		float beginning_machine_time <- machine_time;
		write 'Saving population starts...';

		map<string, string> resultCompoundFilePaths <- population_to_csv(populationToSave, 
			generatedCompoundPopulationFilePaths, populationAttributesFilePaths, populationIdAttributes);
		
		float finishing_machine_time <- machine_time;
		write 'Saving finished. Duration: ' + (finishing_machine_time - beginning_machine_time) + ' miliseconds.' ;
		write 'Open ' + groupPopulationOutputFile + ' and ' + componentPopulationOutputFile + ' to observe the generated populations';
	}
	
}