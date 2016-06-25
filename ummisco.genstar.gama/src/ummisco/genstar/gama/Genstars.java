package ummisco.genstar.gama;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.IList;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.extensions.genstar.IGamaPopulationsLinker;
import msi.gaml.types.IType;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.util.GenstarUtils;

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
				test = false) }, see = { "frequency_distribution", "link_populations", "ipf_population", "ipf_compound_population", "ipu_population" })
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
		
		
		@operator(value = "frequency_distributions", type = IType.LIST, content_type = IType.STRING, category = { IOperatorCategory.GENSTAR })
		@doc(value = "generates frequency distribution(s) from sample data or population data file following the distribution format file(s) then saves the result to CSV file(s)",
			returns = "a list of strings, representing the path to the resulting frequency distribution files",
			special_cases = { "" },
			comment = "",
			examples = { @example(value = "list<string> result_file_paths <- frequency_distributions('frequency_distributions.properties')",
				equals = "",
				test = false) }, see = { "population_from_csv", "link_populations", "ipf_control_totals", "ipu_control_totals" })
		public static List<String> generateFrequencyDistributionsFromSampleDataOrPopulationFile(final IScope scope, final String frequencyDistributionsPropertiesFilePath) {
			
			try {
				
				// 0. Load the property file
				Properties frequencyDistributionsProperties = null;
				File propertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, frequencyDistributionsPropertiesFilePath, true));
				try {
					FileInputStream propertyInputStream = new FileInputStream(propertiesFile);
					frequencyDistributionsProperties = new Properties();
					frequencyDistributionsProperties.load(propertyInputStream);
				} catch (FileNotFoundException e) {
					throw new GenstarException(e);
				} catch (IOException e) {
					throw new GenstarException(e);
				}
				
				// 1. generates frequency distributions then saves results to files
				return GamaGenstarUtils.generateFrequencyDistributionsFromSampleOrPopulationData(scope, frequencyDistributionsProperties);
			} catch (final Exception e) {
				if (e instanceof GamaRuntimeException) { throw (GamaRuntimeException) e; }
				else { throw GamaRuntimeException.create(e, scope); }
			}
		}
		
	}
	
	
	public static abstract class Ipf {
		
		@operator(value = "ipf_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
		@doc(value = "generates a synthetic population from the input data provided by the CVS files using the IPF algorithm. The generated population can be passed to the 'create' statement to create agents.",
		returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list synthetic_population <- ipf_population('single_population_configuration.properties')",
			equals = "",
			test = false) }, see = { "ipf_compound_population", "ipu_population", "frequency_distribution_single_population" })
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
		
		
		@operator(value = "extract_ipf_population", type = IType.MAP, category = { IOperatorCategory.GENSTAR })
		@doc(value = "Extracts certain percentage of a population so that the result population can be used as sample data to generate a synthetic population using IPF approach."
				+ "This operator tries to ensure that there is at least one individual of each attribute value set of the controlled attributes in the extracted population.",
		returns = "The extracted population",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list extracted_population <- extract_ipf_population('ipf_population_configuration.properties')",
			equals = "",
			test = false) }, see = { "extract_ipu_population", "extract_ipf_compound_population", "ipf_population" })
		public static IList extractIpfSinglePopulation(final IScope scope, final String ipfSinglePopulationPropertiesFilePath) {

			try {
				// 0. Load the properties file
				Properties ipfSinglePopulationProperties = null;
				File ipfSinglePopulationPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, ipfSinglePopulationPropertiesFilePath, true));
				try {
					FileInputStream propertyInputStream = new FileInputStream(ipfSinglePopulationPropertiesFile);
					ipfSinglePopulationProperties = new Properties();
					ipfSinglePopulationProperties.load(propertyInputStream);
				} catch (FileNotFoundException e) {
					throw new GenstarException(e);
				} catch (IOException e) {
					throw new GenstarException(e);
				}
				
				
				// 1. extract the population
				IPopulation extractedSingledIpfPopulation = GamaGenstarUtils.extractIpfSinglePopulation(scope, ipfSinglePopulationProperties);
				
				// 2. convert the extracted population from Gen* format to GAMA format
				return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(extractedSingledIpfPopulation);
			} catch (GenstarException e) {
				throw GamaRuntimeException.error(e.getMessage(), scope);
			}
		}
		
		
		@operator(value = "extract_ipf_compound_population", type = IType.MAP, category = { IOperatorCategory.GENSTAR })
		@doc(value = "Extracts certain percentage of a compound population (group-component) so that the result population can be used as sample data to generate a synthetic population using IPF approach."
				+ "This operator tries to ensure that there is at least one individual of each attribute value set of the controlled attributes (of group entity) in the extracted population.",
		returns = "The extracted population",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list extracted_population <- extract_ipf_compound_population('ipf_compound_population_configuration.properties')",
			equals = "",
			test = false) }, see = { "extract_ipu_population", "extract_ipf_population", "ipf_compound_population" })
		public static IList extractIpfCompoundPopulation(final IScope scope, final String ipfCompoundPopulationPropertiesFilePath) {
			
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
				
				
				// 1. extract the population
				IPopulation extractedCompoundIpfPopulation = GamaGenstarUtils.extractIpfCompoundPopulation(scope, ipfCompoundPopulationProperties);
				
				// 2. convert the extracted population from Gen* format to GAMA format
				return GamaGenstarUtils.convertGenstarPopulationToGamaPopulation(extractedCompoundIpfPopulation);
			} catch (GenstarException e) {
				throw GamaRuntimeException.error(e.getMessage(), scope);
			}
		}
		

		@operator(value = "ipf_control_totals", type = IType.STRING, category = { IOperatorCategory.GENSTAR })
		@doc(value = "generates the IPF control totals then save them to a CSV file",
			returns = "the file name path of the resulting IPF control totals",
			special_cases = { "" },
			comment = "",
			examples = { @example(value = "string control_totals_file_path <- ipf_control_totals('controlTotalPropertiesFilePath.properties')",
				equals = "the file name path of the resulting IPF control totals",
				test = false) }, see = { "ipu_control_totals", "ipf_population", "ipf_compound_population" })
		public static String generateIpfControlTotals(final IScope scope, final String ipfControlTotalsPropertiesFilePath) {
			try {
				// 0. Load the properties file
				Properties ipfControlTotalsProperties = null;
				File ipfControlTotalsPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, ipfControlTotalsPropertiesFilePath, true));
				try {
					FileInputStream propertyInputStream = new FileInputStream(ipfControlTotalsPropertiesFile);
					ipfControlTotalsProperties = new Properties();
					ipfControlTotalsProperties.load(propertyInputStream);
				} catch (FileNotFoundException e) {
					throw new GenstarException(e);
				} catch (IOException e) {
					throw new GenstarException(e);
				}
				
				
				String controlTotalsFilePath = GamaGenstarUtils.generateIpfControlTotalsFromPopulationData(scope, ipfControlTotalsProperties);
				return controlTotalsFilePath;
			} catch (GenstarException e) {
				throw GamaRuntimeException.create(e, scope);
			}
		}
	}
	
	
	public static abstract class Ipu {
		
		@operator(value = "ipu_population", type = IType.LIST, category = { IOperatorCategory.GENSTAR })
		@doc(value = "generates a synthetic population using the IPU algorithm. The generated population can be passed to the 'create' statement to create agents.",
		returns = "a list of maps in which each map represents the information (i.e., pairs of [attribute name : attribute value]) of a generated agent",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list synthetic_population <- ipu_population('ipu_population_configuration.properties')",
			equals = "",
			test = false) }, see = { "ipu_control_totals", "extract_ipu_population", "ipf_population", "ipf_compound_population", "frequency_distribution_population" })
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
		@doc(value = "Extracts certain percentage of a population so that the extracted population can be used as sample data to generate the synthetic population using IPU approach."
				+ " This operator tries to ensure that there is at least one individual of each attribute value set of the controlled attributes in the extracted population.",
		returns = "The extracted population",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "list extracted_population <- extract_ipu_population('ipu_population_configuration.properties')",
			equals = "",
			test = false) }, see = { "ipu_population", "extract_ipf_population", "extract_ipf_compound_population" })
		public static IList extractIpuPopulation(final IScope scope, final String ipuSourcePopulationPropertiesFilePath) {
			
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
		
		
		@operator(value = "ipu_control_totals", type = IType.MAP, content_type = IType.STRING,category = { IOperatorCategory.GENSTAR })
		@doc(value = "generates the IPU control totals then save them to CSV files",
			returns = "a map of two elements in which keys are two population names (group, component) and values are two file paths of the resulting IPU control totals",
			special_cases = { "" },
			comment = "",
			examples = { @example(value = "map<string,string> results <- ips_control_totals('ipuControlTotalsPropertiesFilePath.properties')",
				equals = "",
				test = false) }, see = { "ipf_control_totals", "ipu_population" })
		public static Map<String, String> generateIpuControlTotals(final IScope scope,  final String ipuControlTotalsPropertiesFilePath) {
			
			try {
				
				Properties ipuControlTotalsProperties = null;
				File ipuControlTotalsPropertiesFile = new File(FileUtils.constructAbsoluteFilePath(scope, ipuControlTotalsPropertiesFilePath, true));
				try {
					FileInputStream propertyInputStream = new FileInputStream(ipuControlTotalsPropertiesFile);
					ipuControlTotalsProperties = new Properties();
					ipuControlTotalsProperties.load(propertyInputStream);
				} catch (FileNotFoundException e) {
					throw new GenstarException(e);
				} catch (IOException e) {
					throw new GenstarException(e);
				}
				
				
				return GamaGenstarUtils.generateIpuControlTotalsFromPopulationData(scope, ipuControlTotalsProperties);
			} catch (GenstarException e) {
				throw GamaRuntimeException.create(e, scope);
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
		@doc(value = "Saves a synthetic population to CSV file(s).",
		returns = "A map in which keys are population names and values are absolute file paths to the CSV files of the corresponding populations.",
		special_cases = { "" },
		comment = "",
		examples = { @example(value = "map<string, string> outputFilePaths <- population_to_csv(gamaPopulation, populationOutputFilePaths, populationAttributesFilePaths)",
			equals = "",
			test = false) }, see = { "load_population", "population_from_csv" })
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
			test = false) }, see = { "population_to_csv", "save_population" })
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
			test = false) }, see = { "population_to_csv", "save_population" })
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
