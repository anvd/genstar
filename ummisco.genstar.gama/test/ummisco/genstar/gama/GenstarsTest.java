package ummisco.genstar.gama;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import msi.gama.common.util.FileUtils;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaFont;
import msi.gama.util.IList;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.IFileMetaDataProvider;
import msi.gama.util.file.IGamaFile;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.GamaObjectMatrix;
import msi.gama.util.matrix.IMatrix;
import msi.gama.util.path.GamaPath;
import msi.gaml.compilation.AbstractGamlAdditions;
import msi.gaml.compilation.IGamlAdditions;
import msi.gaml.types.GamaBoolType;
import msi.gaml.types.GamaColorType;
import msi.gaml.types.GamaContainerType;
import msi.gaml.types.GamaFileType;
import msi.gaml.types.GamaFloatType;
import msi.gaml.types.GamaFontType;
import msi.gaml.types.GamaGenericAgentType;
import msi.gaml.types.GamaGeometryType;
import msi.gaml.types.GamaGraphType;
import msi.gaml.types.GamaIntegerType;
import msi.gaml.types.GamaListType;
import msi.gaml.types.GamaMapType;
import msi.gaml.types.GamaMatrixType;
import msi.gaml.types.GamaNoType;
import msi.gaml.types.GamaPairType;
import msi.gaml.types.GamaPathType;
import msi.gaml.types.GamaPointType;
import msi.gaml.types.GamaSpeciesType;
import msi.gaml.types.GamaStringType;
import msi.gaml.types.GamaTopologyType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.util.CsvWriter;
import ummisco.genstar.util.GenstarFactoryUtils;

@RunWith(JMockit.class)
public class GenstarsTest {

	static { // init GAMLTypes
		AbstractGamlAdditions.initType("container",new GamaContainerType(),16,102,IGamlAdditions.IC);
		AbstractGamlAdditions.initType("graph",new GamaGraphType(),15,102,IGamlAdditions.GR);
		AbstractGamlAdditions.initType("agent",new GamaGenericAgentType(),11,104,IGamlAdditions.IA);
		AbstractGamlAdditions.initType("map",new GamaMapType(),10,102,IGamlAdditions.GM);
		AbstractGamlAdditions.initType("list",new GamaListType(),5,102,IGamlAdditions.LI);
		AbstractGamlAdditions.initType("pair",new GamaPairType(),9,104,IGamlAdditions.GP);
		AbstractGamlAdditions.initType("path",new GamaPathType(),17,104,IGamlAdditions.IP,GamaPath.class);
		AbstractGamlAdditions.initType("file",new GamaFileType(),12,102,IGamlAdditions.GF);
		AbstractGamlAdditions.initType("bool",new GamaBoolType(),3,104,IGamlAdditions.B,boolean.class);
		AbstractGamlAdditions.initType("matrix",new GamaMatrixType(),8,102,IGamlAdditions.IM,GamaIntMatrix.class,GamaFloatMatrix.class,GamaObjectMatrix.class);
		AbstractGamlAdditions.initType("unknown",new GamaNoType(),0,104,IGamlAdditions.O);
		AbstractGamlAdditions.initType("rgb",new GamaColorType(),6,104,IGamlAdditions.GC,java.awt.Color.class);
		AbstractGamlAdditions.initType("species",new GamaSpeciesType(),14,104,IGamlAdditions.SP);
		AbstractGamlAdditions.initType("topology",new GamaTopologyType(),18,104,IGamlAdditions.IT);
		AbstractGamlAdditions.initType("string",new GamaStringType(),4,104,IGamlAdditions.S);
		AbstractGamlAdditions.initType("float",new GamaFloatType(),2,101,IGamlAdditions.D,double.class);
		AbstractGamlAdditions.initType("geometry",new GamaGeometryType(),13,104,IGamlAdditions.GS,IGamlAdditions.IS);
		AbstractGamlAdditions.initType("font",new GamaFontType(),19,104,GamaFont.class);
		AbstractGamlAdditions.initType("point",new GamaPointType(),7,104,IGamlAdditions.IL,IGamlAdditions.P);
		AbstractGamlAdditions.initType("int",new GamaIntegerType(),1,101,IGamlAdditions.I,int.class,Long.class);
	}
	
	
	@Test public final void testGeneratePopulationFromFrequencyDistribution(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) {
		
		final String populationPropertiesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGeneratePopulationFromFrequencyDistribution/people/Population.properties";
		
		final String attributesCSVFilePath = "test_data/ummisco/genstar/gama/Genstars/testGeneratePopulationFromFrequencyDistribution/people/People_Attributes.csv";
		final String generationRulesCSVFilePath = "test_data/ummisco/genstar/gama/Genstars/testGeneratePopulationFromFrequencyDistribution/people/People_GenerationRules.csv";
		int nbOfAgents = 14821;
		
		final String rule1FilePath = "test_data/ummisco/genstar/gama/Genstars/testGeneratePopulationFromFrequencyDistribution/people/People_GenerationRule1_Data.csv";
		final String rule2FilePath = "test_data/ummisco/genstar/gama/Genstars/testGeneratePopulationFromFrequencyDistribution/people/People_GenerationRule2_Data.csv";
		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.contains("Population.properties")) { return populationPropertiesFilePath; }
					if (filePath.contains("People_Attributes.csv")) { return attributesCSVFilePath; }
					if (filePath.contains("People_GenerationRules.csv")) { return generationRulesCSVFilePath; }
					if (filePath.contains("People_GenerationRule1_Data.csv")) { return rule1FilePath; }
					if (filePath.contains("People_GenerationRule2_Data.csv")) { return rule2FilePath; }
					
					return null;
				}
			};
		}};
		
		IList generatedPopulation = Genstars.generatePopulationFromFrequencyDistribution(scope, populationPropertiesFilePath);
		
		// verify first three elements of the generatedPopulation
		String populationName = (String) generatedPopulation.get(0);
		assertTrue(populationName.equals("people")); // first element is the population name
		assertTrue(((Map)generatedPopulation.get(1)).isEmpty()); // second element contains references to "group" agents
		assertTrue(((Map)generatedPopulation.get(2)).isEmpty()); // third element contains references to "component" agents
		
		// verify the number of generated agents/entities
		assertTrue(generatedPopulation.size() == nbOfAgents + 3);
	}
	
	
	@Test public void testLinkPopulations() {
		fail("not yet implemented");
	}
	
	
	/*
	public static IGamaFile createFrequencyDistributionFromSample(final IScope scope, final String attributesCSVFilePath, 
			final String sampleDataCSVFilePath, final String distributionFormatCSVFilePath, final String resultDistributionCSVFilePath) {
	 */
	@Test public void testCreateFrequencyDistributionFromSample(@Mocked final IScope scope, @Mocked final FileUtils fileUtils, 
			@Mocked final CsvWriter writer, @Mocked final GamaCSVFile csvFile) throws IOException {
		
		final String attributesCSVFilePath = "test_data/ummisco/genstar/gama/Genstars/testCreateFrequencyDistributionFromSample/attributes.csv";
		final String distributionFormatCSVFilePath = "test_data/ummisco/genstar/gama/Genstars/testCreateFrequencyDistributionFromSample/distributionFormat.csv";
		final String sampleDataCSVFilePath = "test_data/ummisco/genstar/gama/Genstars/testCreateFrequencyDistributionFromSample/sampleData.csv";
		final String resultDistributionCSVFilePath = "test_data/ummisco/genstar/gama/Genstars/testCreateFrequencyDistributionFromSample/resultDistribution.csv";
		// resultDistribution.csv
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, anyBoolean);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("attributes.csv")) { return attributesCSVFilePath; }
					if (filePath.endsWith("distributionFormat.csv")) { return distributionFormatCSVFilePath; }
					if (filePath.endsWith("sampleData.csv")) { return sampleDataCSVFilePath; }
					if (filePath.endsWith("resultDistribution.csv")) { return resultDistributionCSVFilePath; }
					
					return null;
				}
			};
			
			// verify CsvWriter is invovoked 57 times (including 1 for header and 8*7 for file content), see distributionFormat.csv and attributes.csv
			writer.writeRecord((String[])any); times = 57;
			
			// verify that one instance of GamaCSVFile is created
			new GamaCSVFile(scope, resultDistributionCSVFilePath, GenstarFactoryUtils.CSV_FILE_FORMATS.ATTRIBUTE_METADATA.FIELD_DELIMITER, Types.STRING, true);
		}};
		
		
		Genstars.createFrequencyDistributionFromSample(scope, attributesCSVFilePath, sampleDataCSVFilePath, distributionFormatCSVFilePath, resultDistributionCSVFilePath);
	}
	
	
	@Test public void testGenerateIPFSinglePopulation(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) {
		
		final String populationPropertiesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFSinglePopulation/SampleData_GenerationRule_Config.properties";
		final String attributesCSVFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFSinglePopulation/attributes.csv";
		final String sampleDataFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFSinglePopulation/PICURS_People_SampleData.csv";
		final String controlledAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFSinglePopulation/controlled_attributes.csv";
		final String controlledTotalFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFSinglePopulation/controlled_totals.csv";
		final String supplementaryAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFSinglePopulation/supplementary_attributes.csv";
		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("SampleData_GenerationRule_Config.properties")) { return populationPropertiesFilePath; }
					if (filePath.endsWith("/attributes.csv")) { return attributesCSVFilePath; }
					if (filePath.endsWith("PICURS_People_SampleData.csv")) { return sampleDataFilePath; }
					if (filePath.endsWith("controlled_attributes.csv")) { return controlledAttributesFilePath; }
					if (filePath.endsWith("controlled_totals.csv")) { return controlledTotalFilePath; }
					if (filePath.endsWith("supplementary_attributes.csv")) { return supplementaryAttributesFilePath; }
					
					return null;
				}
			};
		}};
		
		IList generatedPopulation = Genstars.generateIPFSinglePopulation(scope, populationPropertiesFilePath);
		
		// verify the first three elements of the generated population
		assertTrue(((String)generatedPopulation.get(0)).equals("people"));
		assertTrue(((Map)generatedPopulation.get(1)).isEmpty());
		assertTrue(((Map)generatedPopulation.get(2)).isEmpty());
		
		// verify that the fourth element of the generated population is a map
		assertTrue(generatedPopulation.get(3) instanceof Map);
		
		// TODO verify that the generated population is "single"
	}
	
	@Test public void testGenerateIPFCompoundPopulation(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) {
		
		/*
		ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_attributes.csv
		SAMPLE_DATA=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_sample.csv
		CONTROLLED_ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_component/group_controlled_attributes.csv
		CONTROLLED_TOTALS=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_component/group_control_totals.csv
		SUPPLEMENTARY_ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_supplementary_attributes.csv
		COMPONENT_POPULATION_NAME=people
		COMPONENT_SAMPLE_DATA=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/component_sample.csv
		COMPONENT_ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/component_attributes.csv
		 */
		
		final String populationPropertiesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/SampleDataProperties.properties";
		
		final String groupAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_attributes.csv";
		final String groupSampleDataFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_sample.csv";
		final String groupControlledAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_controlled_attributes.csv";
		final String groupControlledTotalsFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_control_totals.csv";
		final String groupSupplementaryAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_supplementary_attributes.csv";
		
		final String componentAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/component_attributes.csv";
		final String componentSampleDataFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/component_sample.csv";
		
		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("/SampleDataProperties.properties")) { return populationPropertiesFilePath; }
					if (filePath.endsWith("/group_attributes.csv")) { return groupAttributesFilePath; }
					if (filePath.endsWith("/group_sample.csv")) { return groupSampleDataFilePath; }
					if (filePath.endsWith("/group_controlled_attributes.csv")) { return groupControlledAttributesFilePath; }
					if (filePath.endsWith("/group_control_totals.csv")) { return groupControlledTotalsFilePath; }
					if (filePath.endsWith("/group_supplementary_attributes.csv")) { return groupSupplementaryAttributesFilePath; }
					
					if (filePath.endsWith("/component_attributes.csv")) { return componentAttributesFilePath; }
					if (filePath.endsWith("/component_sample.csv")) { return componentSampleDataFilePath; }

					return null;
				}
			};
		}};
		
		
		IList generatedPopulation = Genstars.generateIPFCompoundPopulation(scope, populationPropertiesFilePath);
		
		
		// verify the first three elements of a GAMA synthetic population
		assertTrue(((String)generatedPopulation.get(0)).equals("household")); // first element is the population name
		assertTrue(((Map)generatedPopulation.get(1)).isEmpty() ); // second element contains references to "group" agents
		assertTrue( ((Map)generatedPopulation.get(2)).get("people").equals("inhabitants") ); // third element contains references to "component" agents
		
		
		// verify component populations
		for (int i=3; i<generatedPopulation.size(); i++) {
			Map householdEntity = (Map)generatedPopulation.get(i);
			
			IList componentPopulations = (IList) householdEntity.get(ISyntheticPopulation.class);
			for (Object o : componentPopulations) {
				IList componentPopulation = (IList) o;
				
				// verify the first three elements of a GAMA synthetic population
				assertTrue(((String)componentPopulation.get(0)).equals("people")); // first element is the population name
				assertTrue(((Map)componentPopulation.get(1)).get("household").equals("my_household") ); // second element contains references to "group" agents
				assertTrue( ((Map)componentPopulation.get(2)).isEmpty() ); // third element contains references to "component" agents
			}
		}
	}
	
	@Test public void testGenerateRandomSinglePopulation(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) {
		
		/*
		POPULATION_NAME=people
		ATTRIBUTES=attributes.csv
		NB_OF_ENTITIES=100		 
		 */
		
		final String populationPropertiesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateRandomSinglePopulation/RandomSinglePopulationProperties.properties";
		final String attributesCSVFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateRandomSinglePopulation/attributes.csv";
		
		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("/RandomSinglePopulationProperties.properties")) { return populationPropertiesFilePath; }
					if (filePath.endsWith("attributes.csv")) { return attributesCSVFilePath; }

					return null;
				}
			};
		}};
		
		
		IList generatedPopulation = Genstars.generateRandomSinglePopulation(scope, populationPropertiesFilePath);
		 
		// verify the first three elements of a GAMA synthetic population
		assertTrue(((String)generatedPopulation.get(0)).equals("people")); // first element is the population name
		assertTrue(((Map)generatedPopulation.get(1)).isEmpty() ); // second element contains references to "group" agents
		assertTrue( ((Map)generatedPopulation.get(2)).isEmpty() ); // third element contains references to "component" agents
		
		assertTrue(generatedPopulation.size() == 100+3);
		
		Map aGeneratedEntity = (Map)generatedPopulation.get(3);
		assertTrue(aGeneratedEntity.get("age") != null);
		assertTrue(aGeneratedEntity.get("work") != null);
		assertTrue(aGeneratedEntity.get("gender") != null);
	}
	
	@Test public void testGenerateRandomCompoundPopulation(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
		/*
			GROUP_POPULATION_NAME=household
			GROUP_ATTRIBUTES=group_attributes.csv
			NB_OF_GROUP_ENTITIES=100
			GROUP_SIZE_ATTRIBUTE=HouseholdSize
			COMPONENT_POPULATION_NAME=people
			COMPONENT_ATTRIBUTES=component_attributes.csv
			GROUP_ID_ATTRIBUTE_ON_GROUP=HouseholdID
			GROUP_ID_ATTRIBUTE_ON_COMPONENT=HouseholdID 
		 */
		
		
		final String populationPropertiesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateRandomCompoundPopulation/RandomCompountPopulationProperties.properties";
		final String groupAttributesCSVFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateRandomCompoundPopulation/group_attributes.csv";
		final String componentAttributesCSVFile = "test_data/ummisco/genstar/gama/Genstars/testGenerateRandomCompoundPopulation/component_attributes.csv";

		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("/RandomCompountPopulationProperties.properties")) { return populationPropertiesFilePath; }
					if (filePath.endsWith("group_attributes.csv")) { return groupAttributesCSVFilePath; }
					if (filePath.endsWith("component_attributes.csv")) { return componentAttributesCSVFile; }

					return null;
				}
			};
		}};
		
		
		IList generatedPopulation = Genstars.generateRandomCompoundPopulation(scope, populationPropertiesFilePath);


		// verify the first three elements of a GAMA synthetic population
		assertTrue(((String)generatedPopulation.get(0)).equals("household")); // first element is the population name
		assertTrue(((Map)generatedPopulation.get(1)).isEmpty() ); // second element contains references to "group" agents
		assertTrue( ((Map)generatedPopulation.get(2)).isEmpty() ); // third element contains references to "component" agents
		
		assertTrue(generatedPopulation.size() == 100+3);
		
		
		// verify the attributes of a generated household
		Map aGeneratedEntity = (Map)generatedPopulation.get(3);
		assertTrue(aGeneratedEntity.get("householdID") != null);
		assertTrue(aGeneratedEntity.get("householdSize") != null);
		assertTrue(aGeneratedEntity.get("householdIncome") != null);
		assertTrue(aGeneratedEntity.get("householdType") != null);
		assertTrue(aGeneratedEntity.get("numberOfCars") != null);
		 
		
		
		// verify component populations
		for (int i=3; i<generatedPopulation.size(); i++) {
			Map householdEntity = (Map)generatedPopulation.get(i);
			
			IList componentPopulations = (IList) householdEntity.get(ISyntheticPopulation.class);
			for (Object o : componentPopulations) {
				IList componentPopulation = (IList) o;
				
				// verify the first three elements of a GAMA synthetic population
				assertTrue(((String)componentPopulation.get(0)).equals("people")); // first element is the population name
				assertTrue(((Map)componentPopulation.get(1)).isEmpty() ); // second element contains references to "group" agents
				assertTrue( ((Map)componentPopulation.get(2)).isEmpty() ); // third element contains references to "component" agents
			}
		}
	}
	
	
	@Test public void testWriteCompoundPopulationsToCsvFiles(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
		final String populationPropertiesFilePath = "test_data/ummisco/genstar/gama/Genstars/testWritePopulationsToCsvFiles/compound_population/RandomCompountPopulationProperties.properties";
		final String groupAttributesCSVFilePath = "test_data/ummisco/genstar/gama/Genstars/testWritePopulationsToCsvFiles/compound_population/group_attributes.csv";
		final String componentAttributesCSVFile = "test_data/ummisco/genstar/gama/Genstars/testWritePopulationsToCsvFiles/compound_population/component_attributes.csv";

		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("/compound_population/RandomCompountPopulationProperties.properties")) { return populationPropertiesFilePath; }
					if (filePath.endsWith("compound_population/group_attributes.csv")) { return groupAttributesCSVFilePath; }
					if (filePath.endsWith("compound_population/component_attributes.csv")) { return componentAttributesCSVFile; }

					return null;
				}
			};
		}};
		
		
		IList groupPopulation = Genstars.generateRandomCompoundPopulation(scope, populationPropertiesFilePath);
		
		String groupPopulationName = "household";
		String componentPopulationName = "people";
		String groupPopulationOutputFile = "test_data/ummisco/genstar/gama/Genstars/testWritePopulationsToCsvFiles/compound_population/group_population.csv";
		String componentPopulationOutputFile = "test_data/ummisco/genstar/gama/Genstars/testWritePopulationsToCsvFiles/compound_population/component_population.csv";
		Map<String, String> generatedCompoundPopulationFilePaths = new HashMap<String, String>();
		generatedCompoundPopulationFilePaths.put(groupPopulationName, groupPopulationOutputFile);
		generatedCompoundPopulationFilePaths.put(componentPopulationName, componentPopulationOutputFile);
		
		Map<String, String> populationAttributesFilePaths = new HashMap<String, String>();
		populationAttributesFilePaths.put(groupPopulationName, groupAttributesCSVFilePath);
		populationAttributesFilePaths.put(componentPopulationName, componentAttributesCSVFile);
		
		Map<String, String> populationIdAttributes = new HashMap<String, String>();
		populationIdAttributes.put(groupPopulationName, "HouseholdID");
		populationIdAttributes.put(componentPopulationName, "HouseholdID");
		
		Map<String, String> resultCompoundFilePaths = Genstars.writePopulationsToCsvFiles(scope, groupPopulation, 
				generatedCompoundPopulationFilePaths, populationAttributesFilePaths, populationIdAttributes);
		
		assertTrue(resultCompoundFilePaths.size() == 2);
		assertTrue(resultCompoundFilePaths.get(groupPopulationName).equals(groupPopulationOutputFile));
		assertTrue(resultCompoundFilePaths.get(componentPopulationName).equals(componentPopulationOutputFile));
	}
	
	
	@Test public void testWriteSinglePopulationToCSVFile(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
		/*
		POPULATION_NAME=people
		ATTRIBUTES=attributes.csv
		NB_OF_ENTITIES=100
		 */
		
		final String populationPropertiesFilePath = "test_data/ummisco/genstar/gama/Genstars/testWritePopulationsToCsvFiles/single_population/RandomSinglePopulationProperties.properties";
		final String attributesCSVFilePath = "test_data/ummisco/genstar/gama/Genstars/testWritePopulationsToCsvFiles/single_population/attributes.csv";

		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("/single_population/RandomSinglePopulationProperties.properties")) { return populationPropertiesFilePath; }
					if (filePath.endsWith("attributes.csv")) { return attributesCSVFilePath; }

					return null;
				}
			};
		}};
		
		IList singlePopulation = Genstars.generateRandomSinglePopulation(scope, populationPropertiesFilePath);
		
		
		String populationName = "people";
		String peoplePopulationOutputFile = "test_data/ummisco/genstar/gama/Genstars/testWritePopulationsToCsvFiles/single_population/people_population.csv";
		Map<String, String> populationFilePaths = new HashMap<String, String>();
		populationFilePaths.put(populationName, peoplePopulationOutputFile);
		
		Map<String, String> populationAttributesFilePaths = new HashMap<String, String>();
		populationAttributesFilePaths.put(populationName, attributesCSVFilePath);
		
		
		Map<String, String> resultFilePaths = Genstars.writePopulationsToCsvFiles(scope, singlePopulation, 
				populationFilePaths, populationAttributesFilePaths, Collections.EMPTY_MAP);

		
		assertTrue(resultFilePaths.size() == 1);
		assertTrue(resultFilePaths.get(populationName).equals(peoplePopulationOutputFile));
	}

}
