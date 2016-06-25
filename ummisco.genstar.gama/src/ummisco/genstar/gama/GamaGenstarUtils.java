package ummisco.genstar.gama;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import msi.gama.common.util.FileUtils;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.IpfGenerationRule;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.Population;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.sample_free.AttributeInferenceGenerationRule;
import ummisco.genstar.sample_free.CustomSampleFreeGenerationRule;
import ummisco.genstar.sample_free.FrequencyDistributionGenerationRule;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.CSV_FILE_FORMATS;
import ummisco.genstar.util.FrequencyDistributionUtils;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.GenstarService;
import ummisco.genstar.util.GenstarUtils;
import ummisco.genstar.util.PROPERTY_FILES;

public class GamaGenstarUtils {

	public static IPopulation generateIpfPopulation(final IScope scope, final Properties ipfPopulationProperties) throws GenstarException {
		
		// re-build attributes file path
		String attributesCsvFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesCsvFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, attributesCsvFilePath, false)); }
		
		// re-build file paths for IpfGenerationRule +
		// SAMPLE_DATA_PROPERTY
		String sampleDataFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SAMPLE_DATA_PROPERTY);
		if (sampleDataFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SAMPLE_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, sampleDataFilePath, true)); }
		
		// CONTROLLED_ATTRIBUTES_PROPERTY 
		String controlledAttributesFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (controlledAttributesFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, controlledAttributesFilePath, true)); }

		// CONTROL_TOTALS_PROPERTY
		String controlledTotalsFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROL_TOTALS_PROPERTY);
		if (controlledTotalsFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROL_TOTALS_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, controlledTotalsFilePath, true)); }

		// SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String supplementaryAttributesFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (supplementaryAttributesFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SUPPLEMENTARY_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, supplementaryAttributesFilePath, true)); }
		
		// COMPONENT_POPULATION_PROPERTY.
		String componentPopulationName = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);
		if (componentPopulationName != null) { // compound population (with component populations)
			
			// COMPONENT_SAMPLE_DATA_PROPERTY
			String componentSampleDataFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY);
			if (componentSampleDataFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentSampleDataFilePath, true)); }

			// COMPONENT_ATTRIBUTES_PROPERTY
			String componentAttributesFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
			if (componentAttributesFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true)); }
		}
		// re-build file paths for IpfGenerationRule -


		String analysisOutputFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.ANALYSIS_OUTPUT_PROPERTY);
		if (analysisOutputFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.ANALYSIS_OUTPUT_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, analysisOutputFilePath, false)); }
		
		
		return GenstarService.generateIpfPopulation(ipfPopulationProperties);
	}
	
	
	public static IPopulation generateIpuPopulation(final IScope scope, final Properties ipuPopulationProperties) throws GenstarException {
		
		// re-build file paths
		// GROUP_ATTRIBUTES_PROPERTY
		String groupAttributesCsvFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesCsvFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupAttributesCsvFilePath, true)); }
		
		
		// re-build file paths for IpuGenerationRule +
		// GROUP_SAMPLE_DATA_PROPERTY
		String groupSampleDataFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SAMPLE_DATA_PROPERTY);
		if (groupSampleDataFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SAMPLE_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupSampleDataFilePath, true)); }
		
		// GROUP_CONTROLLED_ATTRIBUTES_PROPERTY 
		String groupControlledAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (groupControlledAttributesFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupControlledAttributesFilePath, true)); }

		// GROUP_CONTROL_TOTALS_PROPERTY
		String groupControlTotalsFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROL_TOTALS_PROPERTY);
		if (groupControlTotalsFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROL_TOTALS_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupControlTotalsFilePath, true)); }
		
		// GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String groupSupplementaryAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (groupSupplementaryAttributesFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupSupplementaryAttributesFilePath, true)); }
		
		// COMPONENT_SAMPLE_DATA_PROPERTY
		String componentSampleDataFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY);
		if (componentSampleDataFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentSampleDataFilePath, true)); }

		// COMPONENT_ATTRIBUTES_PROPERTY
		String componentAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true)); }
		
		// COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY
		String componentControlledAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (componentControlledAttributesFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentControlledAttributesFilePath, true)); }
		
		// COMPONENT_CONTROL_TOTALS_PROPERTY
		String componentControlTotalsFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROL_TOTALS_PROPERTY);
		if (componentControlTotalsFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROL_TOTALS_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentControlTotalsFilePath, true)); }
		
		// COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String componentSupplementaryAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (componentSupplementaryAttributesFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentSupplementaryAttributesFilePath, true)); }
		// re-build file paths for IpuGenerationRule -
		
		
		// GROUP_ANALYSIS_OUTPUT_PROPERTY
		String groupAnalysisOutput = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ANALYSIS_OUTPUT_PROPERTY);
		if (groupAnalysisOutput != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_ANALYSIS_OUTPUT_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupAnalysisOutput, false)); }
		
		// COMPONENT_ANALYSIS_OUTPUT_PROPERTY
		String componentAnalysisOutput = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_ANALYSIS_OUTPUT_PROPERTY);
		if (componentAnalysisOutput != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_ANALYSIS_OUTPUT_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentAnalysisOutput, false)); }

		
		return GenstarService.generateIpuPopulation(ipuPopulationProperties);
	}
	
	
	public static IPopulation generateFrequencyDistributionPopulation(final IScope scope, final Properties frequencyDistributionPopulationProperties) throws GenstarException {
		
		String attributesCSVFilePath = frequencyDistributionPopulationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesCSVFilePath != null) { frequencyDistributionPopulationProperties.put(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true)); }
		
		String generationRulesCSVFilePath = frequencyDistributionPopulationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.GENERATION_RULES_PROPERTY);
		if (generationRulesCSVFilePath != null) { frequencyDistributionPopulationProperties.put(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.GENERATION_RULES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, generationRulesCSVFilePath, true)); }
		
		String analysisResultOutputFolderPath = frequencyDistributionPopulationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.ANALYSIS_OUTPUT_FOLDER_PROPERTY);
		if (analysisResultOutputFolderPath != null) { frequencyDistributionPopulationProperties.put(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.ANALYSIS_OUTPUT_FOLDER_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, analysisResultOutputFolderPath, true)); }
		
		
		String generationRulesListFilePath = frequencyDistributionPopulationProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.GENERATION_RULES_PROPERTY);
		if (generationRulesListFilePath == null) { throw new GenstarException(PROPERTY_FILES.FREQUENCY_DISTRIBUTION_POPULATION_PROPERTIES.GENERATION_RULES_PROPERTY + " property not found"); }
		GenstarCsvFile generationRulesListFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, generationRulesListFilePath, true), true);
		List<String> fdGenerationRuleFilePaths = accumulateFrequencyDistributionGenerationRuleFilePaths(scope, generationRulesListFile);
		

		return GenstarService.generateFrequencyDistributionPopulation(frequencyDistributionPopulationProperties, fdGenerationRuleFilePaths);
	}
	
	
	private static List<String> accumulateFrequencyDistributionGenerationRuleFilePaths(final IScope scope, final GenstarCsvFile generationRulesListFile) throws GenstarException {
		
		// 1. Parse the header
		List<String> header = generationRulesListFile.getHeaders();
		for (int i=0; i<header.size(); i++) {
			if (!header.get(i).equals(CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.HEADERS[i])) {
				throw new GenstarException("Invalid Generation Rule file header. Header must be " + CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.HEADER_STR + " (file: " + generationRulesListFile.getPath() + ")");
			}
		}
		
		
		// 2. Parse and initialize distributions
		List<String> frequencyDistributionGenerationRuleDataFilePaths = new ArrayList<String>();
		List<List<String>> fileContent = generationRulesListFile.getContent();
		int rows = fileContent.size();
		for ( int rowIndex = 0; rowIndex < rows; rowIndex++ ) {
			
			final List<String> generationRuleInfo = fileContent.get(rowIndex);
			if (generationRuleInfo.size() != CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.NB_OF_COLS) { throw new GenstarException("Invalid Generation Rule file format: each row must have " +  CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.NB_OF_COLS + " columns, (file: " + generationRulesListFile.getPath() + ")"); }
			
			String ruleName = (String)generationRuleInfo.get(0);
			String ruleDataFilePath = (String)generationRuleInfo.get(1);
			String ruleTypeName = (String)generationRuleInfo.get(2);
			if (!ruleTypeName.equals(FrequencyDistributionGenerationRule.RULE_TYPE_NAME)) { throw new GenstarException("Only support " + FrequencyDistributionGenerationRule.RULE_TYPE_NAME); }
			
			frequencyDistributionGenerationRuleDataFilePaths.add(FileUtils.constructAbsoluteFilePath(scope, ruleDataFilePath, true));
		}
		 
		
		return frequencyDistributionGenerationRuleDataFilePaths;
	}
	
	
	public static List<String> generateFrequencyDistributionsFromSampleOrPopulationData(final IScope scope, final Properties frequencyDistributionsProperties) throws GenstarException {
		
		String attributesCSVFilePath = frequencyDistributionsProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTIONS_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesCSVFilePath != null) { frequencyDistributionsProperties.put(PROPERTY_FILES.FREQUENCY_DISTRIBUTIONS_PROPERTIES.ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, attributesCSVFilePath, true)); }
		
		String sampleOrPopulationDataFilePath = frequencyDistributionsProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTIONS_PROPERTIES.POPULATION_DATA_PROPERTY);
		if (sampleOrPopulationDataFilePath != null) { frequencyDistributionsProperties.put(PROPERTY_FILES.FREQUENCY_DISTRIBUTIONS_PROPERTIES.POPULATION_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, sampleOrPopulationDataFilePath, true)); }

		String frequencyDistributionFormatsListFilePath = frequencyDistributionsProperties.getProperty(PROPERTY_FILES.FREQUENCY_DISTRIBUTIONS_PROPERTIES.FREQUENCY_DISTRIBUTION_FORMATS_PROPERTY);
		if (frequencyDistributionFormatsListFilePath != null) { frequencyDistributionsProperties.put(PROPERTY_FILES.FREQUENCY_DISTRIBUTIONS_PROPERTIES.FREQUENCY_DISTRIBUTION_FORMATS_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, frequencyDistributionFormatsListFilePath, true)); }
		
		
		// 2. parse the frequencyDistributionFormatsListFile
		
		// 2.1. header verification
		GenstarCsvFile frequencyDistributionFormatsListFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, frequencyDistributionFormatsListFilePath, true), true);
		List<String> frequencyDistributionFormatsListFileHeader = frequencyDistributionFormatsListFile.getHeaders();
		if (frequencyDistributionFormatsListFileHeader.size() != CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_FORMATS_LIST.NB_OF_COLS) { // header length verification
			throw new GenstarException("Invalid frequency distribution formats list file header: file header must have " 
											+ CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_FORMATS_LIST.NB_OF_COLS + " columns."
											+ " File: " + frequencyDistributionFormatsListFile.getPath());
		}
		
		if (!frequencyDistributionFormatsListFileHeader.get(0).equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_FORMATS_LIST.HEADERS[0])) { // header's first column
			throw new GenstarException("Invalid header content. First element of the header must be \'" + CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_FORMATS_LIST.HEADERS[0]
					+ "\'. File: " + frequencyDistributionFormatsListFile.getPath());
		}
		
		if (!frequencyDistributionFormatsListFileHeader.get(1).equals(CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_FORMATS_LIST.HEADERS[1])) { // header's second column
			throw new GenstarException("Invalid header content. Second element of the header must be \'" + CSV_FILE_FORMATS.FREQUENCY_DISTRIBUTION_FORMATS_LIST.HEADERS[1] 
					+ "\'. File: " + frequencyDistributionFormatsListFile.getPath());
		}
		
		// 2.2. read distribution format lists file content (first column: path to format file, second column: corresponding output file)
		List<String> distributionFormatCsvFilePaths = new ArrayList<String>();
		List<String> resultDistributionCsvFilePaths = new ArrayList<String>();
		for (List<String> row : frequencyDistributionFormatsListFile.getContent()) {
			
			// first column: Format File
			String distributionFormatFilePath = row.get(0);
			String constractedDistributionFormatFilePath = FileUtils.constructAbsoluteFilePath(scope, distributionFormatFilePath, true);
			distributionFormatCsvFilePaths.add(constractedDistributionFormatFilePath);
			
			// second column: Output File
			String outputFilePath = row.get(1);
			String constructedOutputFilePath = FileUtils.constructAbsoluteFilePath(scope, outputFilePath, false);
			resultDistributionCsvFilePaths.add(constructedOutputFilePath);
		}
		
		
		return GenstarService.generateFrequencyDistributionsFromSampleOrPopulationData(frequencyDistributionsProperties, distributionFormatCsvFilePaths, resultDistributionCsvFilePaths);
	}
	
	
	public static IPopulation generateRandomSinglePopulation(final IScope scope, final Properties populationProperties) throws GenstarException {
		
		String attributesFileProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesFileProperty != null) { populationProperties.put(PROPERTY_FILES.RANDOM_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, attributesFileProperty, true)); }
		
		return GenstarService.generateRandomSinglePopulation(populationProperties);
	}
	
	
	public static IPopulation generateRandomCompoundPopulation(final IScope scope, final Properties populationProperties) throws GenstarException {
		
		String groupAttributesFileProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesFileProperty != null) { populationProperties.put(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupAttributesFileProperty, true)); }
		
		String componentAttributesFileProperty = populationProperties.getProperty(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFileProperty != null) { populationProperties.put(PROPERTY_FILES.RANDOM_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentAttributesFileProperty, true)); }
		
		return GenstarService.generateRandomCompoundPopulation(populationProperties);
	}
	
	
	public static String generateIpfControlTotalsFromPopulationData(final IScope scope, final Properties ipfControlTotalsProperties) throws GenstarException {
		
		String attributesFilePath = ipfControlTotalsProperties.getProperty(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesFilePath != null) { ipfControlTotalsProperties.put(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, attributesFilePath, true)); }
		
		String controlledAttributesFilePath = ipfControlTotalsProperties.getProperty(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (controlledAttributesFilePath != null) { ipfControlTotalsProperties.put(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, controlledAttributesFilePath, true)); }
		
		String populationFilePath = ipfControlTotalsProperties.getProperty(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.POPULATION_DATA_PROPERTY);
		if (populationFilePath != null) { ipfControlTotalsProperties.put(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.POPULATION_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, populationFilePath, true)); }
		
		String controlTotalsOuputFilePath = ipfControlTotalsProperties.getProperty(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.OUTPUT_FILE_PROPERTY);
		if (controlTotalsOuputFilePath != null) { ipfControlTotalsProperties.put(PROPERTY_FILES.IPF_CONTROL_TOTALS_PROPERTIES.OUTPUT_FILE_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, controlTotalsOuputFilePath, false)); }
		
		
		return GenstarService.generateIpfControlTotalsFromPopulationData(ipfControlTotalsProperties);
	}
	
	
	public static Map<String, String> generateIpuControlTotalsFromPopulationData(final IScope scope, final Properties ipuControlTotalsProperties) throws GenstarException {
		
		// GROUP_ATTRIBUTES_PROPERTY
		String groupAttributesFilePath = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesFilePath != null) { ipuControlTotalsProperties.put(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupAttributesFilePath, true)); }
		
		// GROUP_POPULATION_DATA_PROPERTY
		String groupPopulationFilePath = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY);
		if (groupPopulationFilePath != null) { ipuControlTotalsProperties.put(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupPopulationFilePath, true)); }
		
		// COMPONENT_ATTRIBUTES_PROPERTY
		String componentAttributesFilePath = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFilePath != null) { ipuControlTotalsProperties.put(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true)); }
		
		// COMPONENT_POPULATION_DATA_PROPERTY
		String componentPopulationFilePath = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY);
		if (componentPopulationFilePath != null) { ipuControlTotalsProperties.put(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentPopulationFilePath, true)); }
		
		
		// GROUP_CONTROLLED_ATTRIBUTES_PROPERTY 
		String groupControlledAttributesFilePath = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (groupControlledAttributesFilePath != null) { ipuControlTotalsProperties.put(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupControlledAttributesFilePath, true)); }

		// COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY
		String componentControlledAttributesFilePath = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (componentControlledAttributesFilePath != null) { ipuControlTotalsProperties.put(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentControlledAttributesFilePath, true)); }
		
		// GROUP_OUTPUT_FILE_PROPERTY
		String groupControlTotalsOuputFilePath = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.GROUP_OUTPUT_FILE_PROPERTY);
		if (groupControlTotalsOuputFilePath != null) { ipuControlTotalsProperties.put(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.GROUP_OUTPUT_FILE_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupControlTotalsOuputFilePath, false)); }

		// COMPONENT_OUTPUT_FILE_PROPERTY
		String componentControlTotalsOuputFilePath = ipuControlTotalsProperties.getProperty(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.COMPONENT_OUTPUT_FILE_PROPERTY);
		if (componentControlTotalsOuputFilePath != null) { ipuControlTotalsProperties.put(PROPERTY_FILES.IPU_CONTROL_TOTALS_PROPERTIES.COMPONENT_OUTPUT_FILE_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentControlTotalsOuputFilePath, false)); }
		
		return GenstarService.generateIpuControlTotalsFromPopulationData(ipuControlTotalsProperties);
	}
	
	
	public static void createIpfGenerationRule(final IScope scope, final SampleBasedGenerator generator, final String ruleName, final Properties ipfPopulationProperties) throws GenstarException {
		
		// SAMPLE_DATA_PROPERTY
		String sampleDataFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SAMPLE_DATA_PROPERTY);
		if (sampleDataFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SAMPLE_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, sampleDataFilePath, true)); }
		
		// CONTROLLED_ATTRIBUTES_PROPERTY 
		String controlledAttributesFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (controlledAttributesFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, controlledAttributesFilePath, true)); }
		
		// CONTROL_TOTALS_PROPERTY
		String controlledTotalsFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROL_TOTALS_PROPERTY);
		if (controlledTotalsFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.CONTROL_TOTALS_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, controlledTotalsFilePath, true)); }
		
		// SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String supplementaryAttributesFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (supplementaryAttributesFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.SUPPLEMENTARY_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, supplementaryAttributesFilePath, true)); }
		
		// COMPONENT_POPULATION_PROPERTY.
		String componentPopulationName = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_POPULATION_NAME_PROPERTY);

		if (componentPopulationName != null) { // If COMPONENT_POPULATION_PROPERTY exists, then this is a compound sample data
			
			// COMPONENT_SAMPLE_DATA_PROPERTY
			String componentSampleDataFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY);
			if (componentSampleDataFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentSampleDataFilePath, true)); }
		
			// COMPONENT_ATTRIBUTES_PROPERTY
			String componentAttributesFilePath = ipfPopulationProperties.getProperty(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
			if (componentAttributesFilePath != null) { ipfPopulationProperties.put(PROPERTY_FILES.IPF_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true)); }
		}
		
		
		GenstarService.createIpfGenerationRule(generator, ruleName, ipfPopulationProperties);
	}

	
	public static void createIpuGenerationRule(final IScope scope, final SampleBasedGenerator groupGenerator, final String ruleName, final Properties ipuPopulationProperties) throws GenstarException {

		// GROUP_SAMPLE_DATA_PROPERTY
		String groupSampleDataFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SAMPLE_DATA_PROPERTY);
		if (groupSampleDataFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SAMPLE_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupSampleDataFilePath, true)); }
		
		// GROUP_CONTROLLED_ATTRIBUTES_PROPERTY 
		String groupControlledAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (groupControlledAttributesFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupControlledAttributesFilePath, true)); }
		
		// GROUP_CONTROL_TOTALS_PROPERTY
		String groupControlTotalsFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROL_TOTALS_PROPERTY);
		if (groupControlTotalsFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_CONTROL_TOTALS_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupControlTotalsFilePath, true)); }
		
		// GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String groupSupplementaryAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (groupSupplementaryAttributesFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.GROUP_SUPPLEMENTARY_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupSupplementaryAttributesFilePath, true)); }

		// COMPONENT_SAMPLE_DATA_PROPERTY
		String componentSampleDataFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY);
		if (componentSampleDataFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SAMPLE_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentSampleDataFilePath, true)); }
		
		// COMPONENT_ATTRIBUTES_PROPERTY
		String componentAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true)); }

		// COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY
		String componentControlledAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (componentControlledAttributesFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROLLED_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentControlledAttributesFilePath, true)); }
		
		// COMPONENT_CONTROL_TOTALS_PROPERTY
		String componentControlTotalsFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROL_TOTALS_PROPERTY);
		if (componentControlTotalsFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_CONTROL_TOTALS_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentControlTotalsFilePath, true)); }
		
		// COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY
		String componentSupplementaryAttributesFilePath = ipuPopulationProperties.getProperty(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY);
		if (componentSupplementaryAttributesFilePath != null) { ipuPopulationProperties.put(PROPERTY_FILES.IPU_POPULATION_PROPERTIES.COMPONENT_SUPPLEMENTARY_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentSupplementaryAttributesFilePath, true)); }
	
		
		GenstarService.createIpuGenerationRule(groupGenerator, ruleName, ipuPopulationProperties);
	}
	
	
	public static List<GenstarCsvFile> createSampleFreeGenerationRules(final IScope scope, final SampleFreeGenerator generator, final GenstarCsvFile sampleFreeGenerationRulesListFile) throws GenstarException {
		
		List<GenstarCsvFile> generationRuleFiles = new ArrayList<GenstarCsvFile>();
		
		List<List<String>> fileContent = sampleFreeGenerationRulesListFile.getContent();
		if ( fileContent == null || fileContent.isEmpty() ) { throw new GenstarException("Invalid Generation Rule file: content is empty (file: " + sampleFreeGenerationRulesListFile.getPath()  + ")"); }
		int rows = fileContent.size();

		if (sampleFreeGenerationRulesListFile.getColumns() != CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.NB_OF_COLS) { throw new GenstarException("CVS file must have " + CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.NB_OF_COLS + " columns, (file: " + sampleFreeGenerationRulesListFile.getPath() + ")"); }

		// 1. Parse the header
		List<String> header = sampleFreeGenerationRulesListFile.getHeaders();
		for (int i=0; i<header.size(); i++) {
			if (!header.get(i).equals(CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.HEADERS[i])) {
				throw new GenstarException("Invalid Generation Rule file header. Header must be " + CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.HEADER_STR + " (file: " + sampleFreeGenerationRulesListFile.getPath() + ")");
			}
		}
		
		
		// 2. Parse and initialize distributions
		for ( int rowIndex = 0; rowIndex < rows; rowIndex++ ) {
			
			final List<String> generationRuleInfo = fileContent.get(rowIndex);
			if (generationRuleInfo.size() != CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.NB_OF_COLS) { throw new GenstarException("Invalid Generation Rule file format: each row must have " +  CSV_FILE_FORMATS.SAMPLE_FREE_GENERATION_RULES_LIST.NB_OF_COLS + " columns, (file: " + sampleFreeGenerationRulesListFile.getPath() + ")"); }
			
			String ruleName = (String)generationRuleInfo.get(0);
			String ruleDataFilePathOrJavaClass = (String)generationRuleInfo.get(1);
			String ruleTypeName = (String)generationRuleInfo.get(2);
			GenstarCsvFile ruleDataFile = null;
			
			// initialize the (sample free) data file
			if (!ruleTypeName.equals(CustomSampleFreeGenerationRule.RULE_TYPE_NAME)) {
				
				if (ruleTypeName.equals(IpfGenerationRule.RULE_TYPE_NAME)) { throw new GenstarException("Unsupported generation rule type: " + IpfGenerationRule.RULE_TYPE_NAME); }
				
				ruleDataFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, ruleDataFilePathOrJavaClass, true), true); // Frequency Distribution or Attribute Inference
			}
			
			
			if (ruleTypeName.equals(FrequencyDistributionGenerationRule.RULE_TYPE_NAME)) {
				FrequencyDistributionUtils.createFrequencyDistributionGenerationRuleFromRuleDataFile(generator, ruleName, ruleDataFile);
				generationRuleFiles.add(ruleDataFile); // ATTENTION: only save FrequencyDistributionGenerationRule file
			} else if (ruleTypeName.equals(AttributeInferenceGenerationRule.RULE_TYPE_NAME)) {
				GenstarUtils.createAttributeInferenceGenerationRule(generator, ruleName, ruleDataFile);
			} else if (ruleTypeName.equals(CustomSampleFreeGenerationRule.RULE_TYPE_NAME)) { 
				// ATTENTION: not "save" custom generation rule
				GenstarUtils.createCustomGenerationRule(generator, ruleName, ruleDataFilePathOrJavaClass);
			} else {
				throw new GenstarException("Unsupported generation rule (" + ruleTypeName + "), file: " + sampleFreeGenerationRulesListFile.getPath());
			}
		}
		
		return generationRuleFiles;
	}
	
	
	public static IPopulation extractIpuPopulation(final IScope scope, final Properties ipuSourcePopulationProperties) throws GenstarException {
		
		// GROUP_ATTRIBUTES_PROPERTY
		String groupAttributesFilePath = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesFilePath != null) { ipuSourcePopulationProperties.put(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupAttributesFilePath, true)); }
		
		// GROUP_POPULATION_DATA_PROPERTY
		String groupPopulationFilePath = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY);
		if (groupPopulationFilePath != null) { ipuSourcePopulationProperties.put(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupPopulationFilePath, true)); }

		// COMPONENT_ATTRIBUTES_PROPERTY
		String componentAttributesFilePath =  ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFilePath != null) { ipuSourcePopulationProperties.put(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true)); }

		// COMPONENT_POPULATION_DATA_PROPERTY
		String componentPopulationFilePath = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY);
		if (componentPopulationFilePath != null) { ipuSourcePopulationProperties.put(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentPopulationFilePath, true)); }
		
		// GROUP_CONTROLLED_ATTRIBUTES_PROPERTY
		String ipuGroupControlledFilePath = ipuSourcePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY);
		if (ipuGroupControlledFilePath != null) { ipuSourcePopulationProperties.put(PROPERTY_FILES.EXTRACT_IPU_POPULATION_PROPERTIES.GROUP_CONTROLLED_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, ipuGroupControlledFilePath, true)); }
		
		return GenstarService.extractIpuPopulation(ipuSourcePopulationProperties);
	}
	
	
	public static IPopulation extractIpfSinglePopulation(final IScope scope, final Properties ipfSinglePopulationProperties) throws GenstarException {
		
		String attributesFilePath = ipfSinglePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesFilePath != null) { ipfSinglePopulationProperties.put(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, attributesFilePath, true)); }
		
		// POPULATION_DATA_PROPERTY
		String populationFilePath = ipfSinglePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.POPULATION_DATA_PROPERTY);
		if (populationFilePath != null) { ipfSinglePopulationProperties.put(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.POPULATION_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, populationFilePath, true)); }
		
		// CONTROLLED_ATTRIBUTES_PROPERTY
		String ipfControlledAttributesFilePath = ipfSinglePopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (ipfControlledAttributesFilePath != null) { ipfSinglePopulationProperties.put(PROPERTY_FILES.EXTRACT_IPF_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, ipfControlledAttributesFilePath, true)); }

		
		return GenstarService.extractIpfSinglePopulation(ipfSinglePopulationProperties);
	}
	
	
	public static IPopulation extractIpfCompoundPopulation(final IScope scope, final Properties ipfCompoundPopulationProperties) throws GenstarException {
		
		// GROUP_ATTRIBUTES_PROPERTY
		String groupAttributesFilePath = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesFilePath != null) { ipfCompoundPopulationProperties.put(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupAttributesFilePath, true)); }
		
		// GROUP_POPULATION_DATA_PROPERTY
		String groupPopulationFilePath = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY);
		if (groupPopulationFilePath != null) { ipfCompoundPopulationProperties.put(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupPopulationFilePath, true)); }
		
		// CONTROLLED_ATTRIBUTES_PROPERTY
		String ipfGroupControlledAttributesFilePath = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY);
		if (ipfGroupControlledAttributesFilePath != null) { ipfCompoundPopulationProperties.put(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.CONTROLLED_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, ipfGroupControlledAttributesFilePath, true)); }
		
		// COMPONENT_ATTRIBUTES_PROPERTY
		String componentAttributesFilePath =  ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFilePath != null) { ipfCompoundPopulationProperties.put(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentAttributesFilePath, true)); }
		
		// COMPONENT_POPULATION_DATA_PROPERTY
		String componentPopulationFilePath = ipfCompoundPopulationProperties.getProperty(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY);
		if (componentPopulationFilePath != null) { ipfCompoundPopulationProperties.put(PROPERTY_FILES.EXTRACT_IPF_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentPopulationFilePath, true)); }
		
		
		return GenstarService.extractIpfCompoundPopulation(ipfCompoundPopulationProperties);
	}
	
	
	public static IPopulation loadSinglePopulation(final IScope scope, final Properties singlePopulationProperties) throws GenstarException {
		
		String attributesFileProperty = singlePopulationProperties.getProperty(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY);
		if (attributesFileProperty != null) { singlePopulationProperties.put(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, attributesFileProperty, true)); }
		
		String populationCSVFilePath = singlePopulationProperties.getProperty(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.POPULATION_DATA_PROPERTY);
		if (populationCSVFilePath != null) { singlePopulationProperties.put(PROPERTY_FILES.LOAD_POPULATION_PROPERTIES.POPULATION_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, populationCSVFilePath, true)); }
		
		return GenstarService.loadSinglePopulation(singlePopulationProperties);
	}
	
	
	public static IPopulation loadCompoundPopulation(final IScope scope, final Properties compoundPopulationProperties) throws GenstarException {
		
		String groupAttributesFileProperty = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY);
		if (groupAttributesFileProperty != null) { compoundPopulationProperties.put(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupAttributesFileProperty, true)); }

		String groupPopulationCSVFilePath = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY);
		if (groupPopulationCSVFilePath != null) { compoundPopulationProperties.put(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.GROUP_POPULATION_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, groupPopulationCSVFilePath, true)); }
		
		String componentAttributesFileProperty = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY);
		if (componentAttributesFileProperty != null) { compoundPopulationProperties.put(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_ATTRIBUTES_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentAttributesFileProperty, true)); }
		
		String componentPopulationCSVFilePath = compoundPopulationProperties.getProperty(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY);
		if (componentPopulationCSVFilePath != null) { compoundPopulationProperties.put(PROPERTY_FILES.LOAD_COMPOUND_POPULATION_PROPERTIES.COMPONENT_POPULATION_DATA_PROPERTY, FileUtils.constructAbsoluteFilePath(scope, componentPopulationCSVFilePath, true)); }

		
		return GenstarService.loadCompoundPopulation(compoundPopulationProperties);
	}


	public static IPopulation convertGamaPopulationToGenstarPopulation(final IScope scope, final IList gamaPopulation, final Map<String, String> populationAttributesFilePathsByPopulationNames) throws GenstarException {
		
		// build population attributes
		Map<String, List<AbstractAttribute>> populationAttributes = new HashMap<String, List<AbstractAttribute>>();
		for (String populationName : populationAttributesFilePathsByPopulationNames.keySet()) {
			ISyntheticPopulationGenerator generator = new SampleBasedGenerator("dummy generator");
			
			GenstarCsvFile attributesFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, populationAttributesFilePathsByPopulationNames.get(populationName), true), true);
			AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
			
			populationAttributes.put(populationName, generator.getAttributes());
		}
		
		return convertGamaPopulationToGenstarPopulation(null, gamaPopulation, populationAttributes);
	}

	
	private static IPopulation convertGamaPopulationToGenstarPopulation(final Entity host, final IList gamaPopulation, 
			final Map<String, List<AbstractAttribute>> populationsAttributes) throws GenstarException {
		if (gamaPopulation == null) { throw new GenstarException("Parameter gamaPopulation can not be null"); }
		if (gamaPopulation.size() < 3) { throw new GenstarException("gamaPopulation is not a valid gama synthetic population format"); }
		if (populationsAttributes == null) { throw new GenstarException("Parameter populationsAttributes can not be null"); }
		
		// 1. First three elements of a GAMA synthetic population
		String populationName =  (String)gamaPopulation.get(0); // first element is the population name
		Map<String, String> groupReferences = (Map<String, String>)gamaPopulation.get(1); // second element contains references to "group" agents
		Map<String, String> componentReferences = (Map<String, String>)gamaPopulation.get(2); // third element contains references to "component" agents
		// what to do with group and component references?
		
		IPopulation genstarPopulation = null; // 2. create the genstar population appropriately
		if (host == null) {
			genstarPopulation = new Population(PopulationType.SYNTHETIC_POPULATION, populationName, populationsAttributes.get(populationName));
		} else {
			genstarPopulation = host.createComponentPopulation(populationName, populationsAttributes.get(populationName));
		}
		
		// 3. extract GAMA init values
		IList<GamaMap> gamaInits = GamaListFactory.create();
		gamaInits.addAll(gamaPopulation.subList(3, gamaPopulation.size()));

		// 4. convert entities (each entity is a map)
		for (GamaMap gamaEntityInitValues : gamaInits) {
			
			// extract the initial values
			GamaMap<String, String> mirrorGamaEntityInitValues = null;
			IList<IList> gamaComponentPopulations = (IList<IList>) gamaEntityInitValues.get(IPopulation.class);
			if (gamaComponentPopulations == null) { // without Genstar component populations
				mirrorGamaEntityInitValues = gamaEntityInitValues;
			} else {
				mirrorGamaEntityInitValues = GamaMapFactory.create();
				mirrorGamaEntityInitValues.putAll(gamaEntityInitValues);
				mirrorGamaEntityInitValues.remove(IPopulation.class);
			}
			
			// convert string representation (of the initial values) to AttributeValue
			Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>();
			for (String attributeNameOnEntity : mirrorGamaEntityInitValues.keySet()) {
				attributeValuesOnEntity.put(genstarPopulation.getAttributeByNameOnEntity(attributeNameOnEntity), 
						GenstarGamaTypesConverter.convertGama2GenstarType(genstarPopulation.getAttributeByNameOnEntity(attributeNameOnEntity), 
								GenstarGamaTypesConverter.convertGamaAttributeValueToString(mirrorGamaEntityInitValues.get(attributeNameOnEntity) ) ) );
			}
			
			Entity genstarEntity = genstarPopulation.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity); // create the entity
			if (gamaComponentPopulations != null) { // recursively convert component populations
				for (IList gamaComponentPopulation : gamaComponentPopulations) {
					convertGamaPopulationToGenstarPopulation(genstarEntity, gamaComponentPopulation, populationsAttributes);
				}
			}
		}
		
		return genstarPopulation;
	}
	
	
	public static IList convertGenstarPopulationToGamaPopulation(final IPopulation genstarPopulation) throws GenstarException {
		IList gamaPopulation = GamaListFactory.create();
		
		// First three elements of a GAMA synthetic population
		gamaPopulation.add(genstarPopulation.getName()); // first element is the population name
		gamaPopulation.add(genstarPopulation.getGroupReferences()); // second element contains references to "group" agents
		gamaPopulation.add(genstarPopulation.getComponentReferences()); // third element contains references to "component" agents
		
		// Convert the genstar population to the format understood by GAML "genstar_create" statement
		GamaMap map;
		for (Entity entity : genstarPopulation.getEntities()) {
			map = GamaMapFactory.create();
			for (EntityAttributeValue eav : entity.getEntityAttributeValues()) {
				map.put(eav.getAttribute().getNameOnEntity(), GenstarGamaTypesConverter.convertGenstar2GamaType(eav.getAttributeValueOnEntity()));
			}

			gamaPopulation.add(map);
			
			// Recursively convert genstar component populations
			IList componentPopulations = GamaListFactory.create();
			for (IPopulation componentPopulation : entity.getComponentPopulations()) {
				componentPopulations.add(convertGenstarPopulationToGamaPopulation(componentPopulation));
			}
			
			if (!componentPopulations.isEmpty()) { map.put(IPopulation.class, componentPopulations); }
		}

		return gamaPopulation;
	}
}
