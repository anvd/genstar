package ummisco.genstar.gama;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import msi.gama.common.util.FileUtils;
import msi.gama.common.util.GuiUtils;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.file.IGamaFile;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.extensions.genstar.IGamaPopulationsLinker;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.util.CSV_FILE_FORMATS;
import ummisco.genstar.util.CsvWriter;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.GenstarUtils;
import ummisco.genstar.util.IpfUtils;

/**
 * A set of Genstar-related operators.
 */
public abstract class Genstars {
	
	
	public abstract static class SampleFree {
		
		@operator(value = "frequency_distribution_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
		@doc(value = "generates a synthetic population from a set of frequency distributions (sample-free approach). The generated population can be passed to the 'genstar_create' statement to create agents.",
			returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
			special_cases = { "" },
			comment = "",
			examples = { @example(value = "list synthetic_population <- frequency_distribution_population(population_configuration.properties)",
				equals = "",
				test = false) }, see = { "frequency_distribution", "link_populations" })
		public static IList generatePopulationFromFrequencyDistribution(final IScope scope, final String populationPropertiesFilePath) {

			try {
				// 0. Load the property file
				Properties populationProperties = null;
				File propertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, populationPropertiesFilePath, true));
				try {
					FileInputStream propertyInputStream = new FileInputStream(propertiesFile);
					populationProperties = new Properties();
					populationProperties.load(propertyInputStream);
				} catch (FileNotFoundException e) {
					throw new GenstarException(e);
				} catch (IOException e) {
					throw new GenstarException(e);
				}
				
				// 1. Generate the Gen* population
				IPopulation generatedPopulation = GamaGenstarUtils.generateFrequencyDistributionPopulation(scope, populationProperties);
				
				// 2. Convert the Gen* population to GAMA population
				return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(generatedPopulation);
			} catch (final GenstarException e) {
				throw GamaRuntimeException.error(e.getMessage(), scope);
			}
		}
		
		
		@operator(value = "frequency_distribution", type = IType.FILE, category = { IOperatorCategory.GENSTAR })
		@doc(value = "generates a frequency distribution generation rule from a sample data or a population then saves the resulting generation rule to a CSV file",
			returns = "a boolean value, indicating where the operator is successful or not",
			special_cases = { "" },
			comment = "",
			examples = { @example(value = "file result_file <- frequency_distribution_from_sample('Attributes.csv', 'SampleData.csv', 'DistributionFormat.csv', 'ResultingDistribution.csv')",
				equals = "a file containing the resulting frequency distribution generation rule and locating at the resultDistributionCSVFilePath path",
				test = false) }, see = { "population_from_csv", "link_populations" })
		public static String createFrequencyDistributionFromSample(final IScope scope, final String attributesFilePath, 
				final String sampleDataFilePath, final String distributionFormatFilePath, final String resultDistributionFilePath) {
			
			try {
				return GamaGenstarUtils.createFrequencyDistributionFromSampleDataOrPopulationFile(scope, attributesFilePath, sampleDataFilePath, distributionFormatFilePath, resultDistributionFilePath);
			} catch (final Exception e) {
				if (e instanceof GamaRuntimeException) { throw (GamaRuntimeException) e; }
				else { throw GamaRuntimeException.create(e, scope); }
			}
		}
		
		
		/*
		@operator(value = "analyse_frequency_distribution_population_to_file", type = IType.MAP, content_type = IType.INT, category = { IOperatorCategory.GENSTAR })
		@doc(value = "analyze a synthetic population with respect to the frequency distributions then write analysis result to files",
		returns = "",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "map<string, list> analysisResult <- analyse_frequency_distribution_population_to_file(gamaPopulation, populationPropertiesFilePath, outputFolderPath)",
			equals = "",
			test = false) }, see = { "" })
		public static GamaMap<String, IList<Integer>> analyseFrequencyDistributionPopulation_ToFile(final IScope scope, final IList population, final String attributesFilePath, List<String> frequencyDistributionFilesPath, final String outputFolderPath) {
			
			try {

				GamaGenstarUtils.analyseFrequencyDistributionPopulation(scope, population, attributesFilePath, frequencyDistributionFilesPath);
				
				return null;
				
			} catch (final Exception e) {
				throw GamaRuntimeException.create(e, scope);
			}
			
		}
		
		
		@operator(value = "analyse_frequency_distribution_population_to_console", type = IType.MAP, content_type = IType.INT, category = { IOperatorCategory.GENSTAR })
		@doc(value = "analyze a synthetic population with respect to the frequency distributions then write analysis result to GAMA console",
		returns = "",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "map<string, list> analysisResult <- analyse_frequency_distribution_population_to_console(gamaPopulation, populationPropertiesFilePath)",
			equals = "",
			test = false) }, see = { "" })
		public static GamaMap<String, IList<Integer>> analyseFrequencyDistributionPopulation_ToConsole(final IScope scope, final IList population, final String attributesFilePath, List<String> frequencyDistributionFilesPath) {
			return null;
		}
		*/
	}
	
	
	public static abstract class Ipf {
		
		@operator(value = "ipf_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
		@doc(value = "generates a synthetic population from the input data provided by the CVS files using the IPF algorithm. The generated population can be passed to the 'create' statement to create agents.",
		returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list synthetic_population <- ipf_population('single_population_configuration.properties')",
			equals = "",
			test = false) }, see = { "ipf_compound_population, ipu_population, frequency_distribution_single_population" })
		public static IList generateIpfSinglePopulation(final IScope scope, final String ipfSinglePopulationPropertiesFilePath) {
			try {
				
				// 0. Load the properties file
				Properties ipfSinglePopulationPropeties = null;
				File populationPropertyFile = new File(FileUtils.constructAbsoluteFilePath(scope, ipfSinglePopulationPropertiesFilePath, true));
				try {
					FileInputStream propertyInputStream = new FileInputStream(populationPropertyFile);
					ipfSinglePopulationPropeties = new Properties();
					ipfSinglePopulationPropeties.load(propertyInputStream);
				} catch (FileNotFoundException e) {
					throw new GenstarException(e);
				} catch (IOException e) {
					throw new GenstarException(e);
				}
				
				// 1. Generate the Gen* population
				IPopulation genstarIpfSinglePopulation = GamaGenstarUtils.generateIpfPopulation(scope, ipfSinglePopulationPropeties);

				// 2. Convert the Gen* population to GAMA population
				return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(genstarIpfSinglePopulation);
			} catch (GenstarException e) {
				throw GamaRuntimeException.error(e.getMessage(), scope);
			}
		}
		
		
		@operator(value = "ipf_compound_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
		@doc(value = "generates a synthetic population from the input data provided by the CVS files using the IPF algorithm. The generated population can be passed to the 'create' statement to create agents.",
		returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list synthetic_population <- ipf_compound_population('compound_population_configuration.properties')",
			equals = "",
			test = false) }, see = { "ipf_population, ipu_population, frequency_distribution_population" })
		public static IList generateIpfCompoundPopulation(final IScope scope, final String ipfCompoundPopulationPropertiesFilePath) {
			
			try {
				// 0. Load the properties file
				Properties ipfCompoundPopulationProperties = null;
				File ipfCompoundPopulationPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, ipfCompoundPopulationPropertiesFilePath, true));
				try {
					FileInputStream propertyInputStream = new FileInputStream(ipfCompoundPopulationPropertiesFile);
					ipfCompoundPopulationProperties = new Properties();
					ipfCompoundPopulationProperties.load(propertyInputStream);
				} catch (FileNotFoundException e) {
					throw new GenstarException(e);
				} catch (IOException e) {
					throw new GenstarException(e);
				}
				
				
				// 1. Generate the Gen* population
				IPopulation genstarIpfCompoundPopulation = GamaGenstarUtils.generateIpfPopulation(scope, ipfCompoundPopulationProperties);
				
				// 2. Convert the Gen* population to GAMA population
				return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(genstarIpfCompoundPopulation);
			} catch (GenstarException e) {
				throw GamaRuntimeException.error(e.getMessage(), scope);
			}
		}
		

		// TODO change to "ipf_control_totals"
		@operator(value = "control_totals", type = IType.FILE, category = { IOperatorCategory.GENSTAR })
		@doc(value = "generates the control totals then save them to a CSV file",
			returns = "a reference to the file containing the resulting control totals",
			special_cases = { "" },
			comment = "",
			examples = { @example(value = "file result_file <- control_totals('controlTotalPropertiesFilePath.properties', 'resultControlsTotalFilePath.csv')",
				equals = "a file containing the resulting control totals and locating at the path specified by resultControlsTotalFilePath.csv",
				test = false) }, see = { "population_from_csv", "link_populations" })
		public static IGamaFile generateControlTotals(final IScope scope, final String controlTotalPropertiesFilePath, final String resultControlsTotalFilePath) {
			try {
				String exportFileName = GamaGenstarUtils.generateControlTotals(scope, controlTotalPropertiesFilePath, resultControlsTotalFilePath);
				return new GamaCSVFile(scope, exportFileName, CSV_FILE_FORMATS.ATTRIBUTES.FIELD_DELIMITER, Types.STRING, false);
			} catch (GenstarException e) {
				throw GamaRuntimeException.create(e, scope);
			}
			
		}
		

		@operator(value = "analyse_ipf_population_to_console", type = IType.LIST, content_type = IType.INT, category = { IOperatorCategory.GENSTAR })
		@doc(value = "analyze a synthetic population with respect to the IPF control totals then write analysis result to the GAMA console if necessary",
		returns = "",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list<int> analysisResult <- analyse_ipf_population_to_console(gamaPopulation, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath, writeResultToConsole)",
			equals = "",
			test = false) }, see = { "" })
		public static List<Integer> analyseIpfPopulation_ToConsole(final IScope scope, final IList gamaPopulation, final String attributesFilePath, 
				final String controlledAttributesListFilePath, final String controlTotalsFilePath) {
			
			try {
				
				// TODO analyseIpfPopulation need the information of ID attribute -> option 1: analyse compound Ipf population
				GenstarCsvFile controlTotalsFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlTotalsFilePath, true), false);
				List<Integer> generatedFrequencies = analyseIpfPopulation(scope, gamaPopulation, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath);
				
				// write analysis result to GAMA console
				GuiUtils.informConsole("Row format: (attribute name, attribute value)+, control total, generated total");
				int line = 0;
				for (List<String> controlTotalsRow : controlTotalsFile.getContent()) {
					StringBuffer aRow = new StringBuffer();
					for (String e : controlTotalsRow) { aRow.append(e); aRow.append(","); }
					aRow.append(generatedFrequencies.get(line));
					line++;
					
					GuiUtils.informConsole(aRow.toString());				
				}
				
				return generatedFrequencies;
			} catch (GenstarException e){
				throw GamaRuntimeException.error(e.getMessage(), scope);
			}
		}
		
		
		@operator(value = "analyse_ipf_population_to_file", type = IType.LIST, content_type = IType.INT, category = { IOperatorCategory.GENSTAR })
		@doc(value = "analyze a synthetic population with respect to the IPF control totals then write analysis result to the GAMA console if necessary",
		returns = "",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list<int> analysisResult <- analyse_ipf_population_to_file(gamaPopulation, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath, outputFilePath)",
			equals = "",
			test = false) }, see = { "" })
		public static List<Integer> analyseIpfPopulation_ToFile(final IScope scope, final IList gamaPopulation, final String attributesFilePath, 
				final String controlledAttributesListFilePath, final String controlTotalsFilePath, final String outputFilePath) {
			
			try {
				
				GenstarCsvFile controlTotalsFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlTotalsFilePath, true), false);
				List<Integer> generatedFrequencies = analyseIpfPopulation(scope, gamaPopulation, attributesFilePath, controlledAttributesListFilePath, controlTotalsFilePath);

				// output file
				CsvWriter outputFileWriter = new CsvWriter(FileUtils.constructAbsoluteFilePath(scope, outputFilePath, false));
				
				// write analysis result to output file
				int line = 0;
				for (List<String> controlTotalsRow : controlTotalsFile.getContent()) {
					List<String> aRow = new ArrayList<String>();
					for (String e : controlTotalsRow) { aRow.add(e); }
					aRow.add(Integer.toString(generatedFrequencies.get(line)));
					line++;
					
					outputFileWriter.writeRecord(aRow.toArray(new String[0]));			
				}
				outputFileWriter.flush();
				outputFileWriter.close();
				
				return generatedFrequencies;
			} catch (GenstarException e){
				throw GamaRuntimeException.error(e.getMessage(), scope);
			} catch (IOException ioe) {
				throw GamaRuntimeException.error(ioe.getMessage(), scope);
			}
			
		}
		
		
		private static List<Integer> analyseIpfPopulation(final IScope scope, final IList gamaPopulation, final String attributesFilePath, 
				final String controlledAttributesListFilePath, final String ipfControlTotalsFilePath) throws GenstarException {
			// convert GAMA population to Gen* population
			String populationName = (String)gamaPopulation.get(0); // first element is the population name
			Map<String, String> populationsAttributes = new HashMap<String, String>();
			populationsAttributes.put(populationName, attributesFilePath);

			IPopulation genstarPopulation = GamaGenstarUtils.convertGamaPopulationToGenstarPopulation(scope, gamaPopulation, populationsAttributes);
			
			// do the analysis
			GenstarCsvFile controlTotalsFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, ipfControlTotalsFilePath, true), false);
			GenstarCsvFile controlledAttributesListFile = new GenstarCsvFile(FileUtils.constructAbsoluteFilePath(scope, controlledAttributesListFilePath, true), false);
			return IpfUtils.analyseIpfPopulation(genstarPopulation, controlledAttributesListFile, controlTotalsFile);
		}

		// TODO analyseIpfCompoundPopulation
	}
	
	
	public static abstract class Ipu {
		
		@operator(value = "ipu_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
		@doc(value = "generates a synthetic population using the IPU algorithm. The generated population can be passed to the 'create' statement to create agents.",
		returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list synthetic_population <- ipu_population('ipu_population_configuration.properties')",
			equals = "",
			test = false) }, see = { "ipf_population, ipf_compound_population, frequency_distribution_population" })
		public static IList generateIpuPopulation(final IScope scope, final String ipuPopulationPropertiesFilePath) {
			
			try {
				// 0. Load the properties file
				Properties ipuPopulationProperties = null;
				File ipuPopulationPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, ipuPopulationPropertiesFilePath, true));
				try {
					FileInputStream propertyInputStream = new FileInputStream(ipuPopulationPropertiesFile);
					ipuPopulationProperties = new Properties();
					ipuPopulationProperties.load(propertyInputStream);
				} catch (FileNotFoundException e) {
					throw new GenstarException(e);
				} catch (IOException e) {
					throw new GenstarException(e);
				}
				
				// 1. generate the Gen* population
				IPopulation genstarIpuPopulation = GamaGenstarUtils.generateIpuPopulation(scope, ipuPopulationProperties);
				
				// 2. convert the Gen* population to GAMA population
				return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(genstarIpuPopulation);
			} catch (GenstarException ge) {
				throw GamaRuntimeException.error(ge.getMessage(), scope);
			}
		}
		
		
		@operator(value = "extract_ipu_population", type = IType.MAP, category = { IOperatorCategory.GENSTAR })
		@doc(value = "Extracts certain percentage of an IPU population",
		returns = "The extracted population",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list extracted_population <- extract_ipu_population('ipu_population_configuration.properties')",
			equals = "",
			test = false) }, see = { "ipf_population, ipf_compound_population, frequency_distribution_population" })
		public static IList extractIpuPopulation(final IScope scope, final String ipuSourcePopulationPropertiesFilePath) {
			
			//TODO test this method
			
			try {
				// 0. Load the properties file
				Properties ipuSourcePopulationProperties = null;
				File ipuPopulationPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, ipuSourcePopulationPropertiesFilePath, true));
				try {
					FileInputStream propertyInputStream = new FileInputStream(ipuPopulationPropertiesFile);
					ipuSourcePopulationProperties = new Properties();
					ipuSourcePopulationProperties.load(propertyInputStream);
				} catch (FileNotFoundException e) {
					throw new GenstarException(e);
				} catch (IOException e) {
					throw new GenstarException(e);
				}
				
				// 1. extract the population
				IPopulation extractedIpuPopulation = GamaGenstarUtils.extractIpuPopulation(scope, ipuSourcePopulationProperties);
				
				// 2. convert the extracted population from Gen* format to GAMA format
				return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(extractedIpuPopulation);
				
			} catch (GenstarException e) {
				throw GamaRuntimeException.error(e.getMessage(), scope);
			}
		}
		
	}
	
	
	public abstract static class Utils {
		
		@operator(value = "link_populations", category = { IOperatorCategory.GENSTAR })
		@doc(value = "Links populations",
		returns = "",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "bool linking_result <- link_populations('household_inhabitant_linker', household_inhabitant_populations)",
			equals = "a boolean value indicating where the linking process is successful or not",
			test = false) }, see = { "population_from_csv", "frequency_distribution_from_sample" })
		public static boolean linkPopulations(final IScope scope, final String linkerName, final IList<IList<IMacroAgent>> populations) {
			
			// 1. search for the linker on the AbstractGamlAdditions
			IGamaPopulationsLinker populationsLinker = AbstractGamlAdditions.POPULATIONS_LINKERS.get(linkerName);
			
			if (populationsLinker == null) { throw GamaRuntimeException.error("Populations linker : " + linkerName + " does not exist.", scope); }
			
			// 2. ask the linker to do the job
			populationsLinker.establishRelationship(scope, populations);
			
			return true;
		}
		
		
		@operator(value = "random_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
		@doc(value = "generates a random (single) synthetic population.",
		returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
		special_cases = { "" },
		comment = "The property file contains the following properties:",
		examples = { @example(value = "list synthetic_population <- random_population('single_population_configuration.properties')",
			equals = "a Gen* (single) population represented as a GAML list",
			test = false) }, see = { "random_compound_population" })
		public static IList generateRandomSinglePopulation(final IScope scope, final String populationPropertiesFilePath) {
			try {
				// 0. Load the properties file
				Properties populationProperties = null;
				File populationPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, populationPropertiesFilePath, true));
				try {
					FileInputStream propertyInputStream = new FileInputStream(populationPropertiesFile);
					populationProperties = new Properties();
					populationProperties.load(propertyInputStream);
				} catch (FileNotFoundException e) {
					throw new GenstarException(e);
				} catch (IOException e) {
					throw new GenstarException(e);
				}
				

				// 2. Generate the population
				IPopulation generatedPopulation = GamaGenstarUtils.generateRandomSinglePopulation(scope, populationProperties);
				
				// 3. Convert the population to IList
				return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(generatedPopulation);
			} catch (GenstarException e) {
				throw GamaRuntimeException.error(e.getMessage(), scope);
			}
		}
		
		
		@operator(value = "random_compound_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
		@doc(value = "generates a random compound synthetic population. A compound synthetic population is a population in which there is a relationship between a group agent and several component agent.",
		returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
		special_cases = { "" },
		comment = "The property file contains the following properties:",
		examples = { @example(value = "list synthetic_population <- random_compound_population('compound_population_configuration.properties')",
			equals = "a Gen* compound synthetic population represented as a GAML list",
			test = false) }, see = { "random_population" })
		public static IList generateRandomCompoundPopulation(final IScope scope, final String populationConfigurationFile) {
			
			try {
				// 0. Load the properties file
				Properties populationProperties = null;
				File populationPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, populationConfigurationFile, true));
				try {
					FileInputStream propertyInputStream = new FileInputStream(populationPropertiesFile);
					populationProperties = new Properties();
					populationProperties.load(propertyInputStream);
				} catch (FileNotFoundException e) {
					throw new GenstarException(e);
				} catch (IOException e) {
					throw new GenstarException(e);
				}

				// 1. Generate the random compound population
				IPopulation generatedPopulation = GamaGenstarUtils.generateRandomCompoundPopulation(scope, populationProperties);
				
				// 2. Convert the Gen* population to GAMA population
				return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(generatedPopulation);			
			} catch (GenstarException e) {
				throw GamaRuntimeException.error(e.getMessage(), scope);
			}
		}

		
		@operator(value = {"population_to_csv", "save_population" }, type = IType.LIST, category = { IOperatorCategory.GENSTAR })
		@doc(value = "Writes a synthetic population to CSV file(s).",
		returns = "A map in which keys are population names and values are absolute file paths to the CSV files of the corresponding populations.",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "map<string, string> outputFilePaths <- population_to_csv(gamaPopulation, populationOutputFilePaths, populationAttributesFilePaths)",
			equals = "",
			test = false) }, see = { "" })
		public static final Map<String, String> writePopulationsToCsvFiles(final IScope scope, final IList gamaPopulation, final Map<String, String> populationOutputFilePaths, 
				final Map<String, String> populationAttributesFilePaths) {
			
			try {
				
				// 0. convert GAMA synthetic populations to Gen* synthetic populations
				IPopulation genstarPopulation = GamaGenstarUtils.convertGamaPopulationToGenstarPopulation(scope, gamaPopulation, populationAttributesFilePaths);
				
				// 1. re-build populationOutputFilePaths
				Map<String, String> rebuiltPopulationOutputFilePaths = new HashMap<String, String>();
				for (Map.Entry<String, String> populationOutputFilePathsEntry : populationOutputFilePaths.entrySet()) {
					rebuiltPopulationOutputFilePaths.put(populationOutputFilePathsEntry.getKey(), FileUtils.constructAbsoluteFilePath(scope, populationOutputFilePathsEntry.getValue(), false));
				}

				// 2. write Gen* synthetic populations to CSV files
				return GenstarUtils.writePopulationToCsvFile(genstarPopulation, rebuiltPopulationOutputFilePaths);
			} catch (GenstarException e) {
				throw GamaRuntimeException.error(e.getMessage(), scope);
			}
		}
		
		
		@operator(value = { "load_population", "population_from_csv" }, type = IType.LIST, category = { IOperatorCategory.GENSTAR })
		@doc(value = "Load a single population from CSV file(s).",
		returns = "The loaded population in list format understood by genstar_create statement",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list loaded_single_population <- load_population(loaded_single_population.properties)",
			equals = "",
			test = false) }, see = { "" })
		public static IList loadSinglePopulation(final IScope scope, final String singlePopulationPropertiesFilePath) {
			
			try {
				// 0. Load the properties file
				Properties singlePopulationProperties = null;
				File singlePopulationPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, singlePopulationPropertiesFilePath, true));
				try {
					FileInputStream propertyInputStream = new FileInputStream(singlePopulationPropertiesFile);
					singlePopulationProperties = new Properties();
					singlePopulationProperties.load(propertyInputStream);
				} catch (FileNotFoundException e) {
					throw new GenstarException(e);
				} catch (IOException e) {
					throw new GenstarException(e);
				}

				// 1. Load the random single population
				IPopulation loadedSinglePopulation = GamaGenstarUtils.loadSinglePopulation(scope, singlePopulationProperties);
				
				// 2. Convert the Gen* loaded population to GAMA population
				return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(loadedSinglePopulation);			
			} catch (GenstarException e) {
				throw GamaRuntimeException.error(e.getMessage(), scope);
			}
		}
		
		
		@operator(value = { "load_compound_population", "compound_population_from_csv" }, type = IType.LIST, category = { IOperatorCategory.GENSTAR })
		@doc(value = "Load a compound population from CSV file(s).",
		returns = "The loaded compound population in list format understood by genstar_create statement",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list loaded_compound_population <- load_compound_population(loaded_compound_population.properties)",
			equals = "",
			test = false) }, see = { "" })
		public static IList loadCompoundPopulation(final IScope scope, final String compoundPopulationPropertiesFilePath) {
			
			try {
				// 0. Load the properties file
				Properties compoundPopulationProperties = null;
				File compoundPopulationPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, compoundPopulationPropertiesFilePath, true));
				try {
					FileInputStream propertyInputStream = new FileInputStream(compoundPopulationPropertiesFile);
					compoundPopulationProperties = new Properties();
					compoundPopulationProperties.load(propertyInputStream);
				} catch (FileNotFoundException e) {
					throw new GenstarException(e);
				} catch (IOException e) {
					throw new GenstarException(e);
				}

				// 1. Load the random compound population
				IPopulation loadedCompoundPopulation = GamaGenstarUtils.loadCompoundPopulation(scope, compoundPopulationProperties);
				
				// 2. Convert the Gen* loaded population to GAMA population
				return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(loadedCompoundPopulation);			
			} catch (GenstarException e) {
				throw GamaRuntimeException.error(e.getMessage(), scope);
			}

		}
	}
}
