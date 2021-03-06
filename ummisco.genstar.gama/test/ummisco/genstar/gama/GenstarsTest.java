package ummisco.genstar.gama;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;
import msi.gama.common.util.FileUtils;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaFont;
import msi.gama.util.IList;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.GamaObjectMatrix;
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
import msi.gaml.types.Types;

import org.junit.Test;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.IpfControlTotals;
import ummisco.genstar.ipf.IpfGenerationRule;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.CsvWriter;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.CSV_FILE_FORMATS;

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
		
		IList generatedPopulation = Genstars.SampleFree.generatePopulationFromFrequencyDistribution(scope, populationPropertiesFilePath);
		
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
	
	
	@Test public void testGenerateFrequencyDistributionsFromSampleDataOrPopulationFile(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws IOException {
		
		String basePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateFrequencyDistributionsFromSampleDataOrPopulationFile/";
		final String propertiesFilePath = basePath + "frequency_distributions.properties";
		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, anyBoolean);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("frequency_distributions.properties")) { return propertiesFilePath; }
					
					return null;
				}
			};
		}};
		

		// clean up if necessary
		String resultFilePath1 = basePath + "resultDistribution1.csv";
		String resultFilePath2 = basePath + "resultDistribution2.csv";
		
		File resultFile1 = new File(resultFilePath1);
		if (resultFile1.exists()) { resultFile1.delete(); }
		
		File resultFile2 = new File(resultFilePath2);
		if (resultFile2.exists()) { resultFile2.delete(); }
		
		Genstars.SampleFree.generateFrequencyDistributionsFromSampleDataOrPopulationFile(scope, propertiesFilePath);
		
		File recreatedResultFile1 = new File(resultFilePath1);
		assertTrue(recreatedResultFile1.exists());
		
		File recreatedResultFile2 = new File(resultFilePath2);
		assertTrue(recreatedResultFile2.exists());
		
	}
	
	
	@Test public void testGenerateIpfSinglePopulation(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) {
		
		final String populationPropertiesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFSinglePopulation/IpfSinglePopulationProperties.properties";
		final String attributesCSVFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFSinglePopulation/attributes.csv";
		final String sampleDataFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFSinglePopulation/PICURS_People_SampleData.csv";
		final String controlledAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFSinglePopulation/controlled_attributes.csv";
		final String controlTotalFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFSinglePopulation/control_totals.csv";
		final String supplementaryAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFSinglePopulation/supplementary_attributes.csv";
		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("IpfSinglePopulationProperties.properties")) { return populationPropertiesFilePath; }
					if (filePath.endsWith("/attributes.csv")) { return attributesCSVFilePath; }
					if (filePath.endsWith("PICURS_People_SampleData.csv")) { return sampleDataFilePath; }
					if (filePath.endsWith("controlled_attributes.csv")) { return controlledAttributesFilePath; }
					if (filePath.endsWith("control_totals.csv")) { return controlTotalFilePath; }
					if (filePath.endsWith("supplementary_attributes.csv")) { return supplementaryAttributesFilePath; }
					
					return null;
				}
			};
		}};
		
		IList generatedPopulation = Genstars.Ipf.generateIpfSinglePopulation(scope, populationPropertiesFilePath);
		
		// verify the first three elements of the generated population
		assertTrue(((String)generatedPopulation.get(0)).equals("people"));
		assertTrue(((Map)generatedPopulation.get(1)).isEmpty());
		assertTrue(((Map)generatedPopulation.get(2)).isEmpty());
		
		// verify that the fourth element of the generated population is a map
		assertTrue(generatedPopulation.get(3) instanceof Map);
		
		// TODO verify that the generated population is "single"
	}
	
	@Test public void testGenerateIpfCompoundPopulation(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) {
		
		/*
		ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_attributes.csv
		SAMPLE_DATA=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_sample.csv
		CONTROLLED_ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_component/group_controlled_attributes.csv
		CONTROL_TOTALS=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_component/group_control_totals.csv
		SUPPLEMENTARY_ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_supplementary_attributes.csv
		COMPONENT_POPULATION_NAME=people
		COMPONENT_SAMPLE_DATA=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/component_sample.csv
		COMPONENT_ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/component_attributes.csv
		 */
		
		final String ipfCompoundPopulationPropertiesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/IpfCompoundPopulationProperties.properties";
		
		final String groupAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_attributes.csv";
		final String groupSampleDataFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_sample.csv";
		final String groupControlledAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_controlled_attributes.csv";
		final String groupControlTotalsFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_control_totals.csv";
		final String groupSupplementaryAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/group_supplementary_attributes.csv";
		
		final String componentAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/component_attributes.csv";
		final String componentSampleDataFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIPFCompoundPopulation/component_sample.csv";
		
		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("/IpfCompoundPopulationProperties.properties")) { return ipfCompoundPopulationPropertiesFilePath; }
					if (filePath.endsWith("/group_attributes.csv")) { return groupAttributesFilePath; }
					if (filePath.endsWith("/group_sample.csv")) { return groupSampleDataFilePath; }
					if (filePath.endsWith("/group_controlled_attributes.csv")) { return groupControlledAttributesFilePath; }
					if (filePath.endsWith("/group_control_totals.csv")) { return groupControlTotalsFilePath; }
					if (filePath.endsWith("/group_supplementary_attributes.csv")) { return groupSupplementaryAttributesFilePath; }
					
					if (filePath.endsWith("/component_attributes.csv")) { return componentAttributesFilePath; }
					if (filePath.endsWith("/component_sample.csv")) { return componentSampleDataFilePath; }

					return null;
				}
			};
		}};
		
		
		IList generatedPopulation = Genstars.Ipf.generateIpfCompoundPopulation(scope, ipfCompoundPopulationPropertiesFilePath);
		
		
		// verify the first three elements of a GAMA synthetic population
		assertTrue(((String)generatedPopulation.get(0)).equals("household")); // first element is the population name
		assertTrue(((Map)generatedPopulation.get(1)).isEmpty() ); // second element contains references to "group" agents
		assertTrue( ((Map)generatedPopulation.get(2)).get("people").equals("inhabitants") ); // third element contains references to "component" agents
		
		
		// verify component populations
		for (int i=3; i<generatedPopulation.size(); i++) {
			Map householdEntity = (Map)generatedPopulation.get(i);
			
			IList componentPopulations = (IList) householdEntity.get(IPopulation.class);
			for (Object o : componentPopulations) {
				IList componentPopulation = (IList) o;
				
				// verify the first three elements of a GAMA synthetic population
				assertTrue(((String)componentPopulation.get(0)).equals("people")); // first element is the population name
				assertTrue(((Map)componentPopulation.get(1)).get("household").equals("my_household") ); // second element contains references to "group" agents
				assertTrue( ((Map)componentPopulation.get(2)).isEmpty() ); // third element contains references to "component" agents
			}
		}
	}
	
	@Test public void testGenerateIpuPopulation(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		/*
	public static IList generateIpuPopulation(final IScope scope, final String ipuPopulationPropertiesFilePath) {
		 */
		
		/*
			GROUP_ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIpuPopulation/group_attributes.csv
			GROUP_POPULATION_NAME=household
			GROUP_SAMPLE_DATA=test_data/ummisco/genstar/gama/Genstars/testGenerateIpuPopulation/group_sample.csv
			GROUP_ID_ATTRIBUTE_ON_GROUP=HouseholdID
			GROUP_CONTROLLED_ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIpuPopulation/group_controlled_attributes.csv
			GROUP_CONTROL_TOTALS=test_data/ummisco/genstar/gama/Genstars/testGenerateIpuPopulation/group_ipu_control_totals.csv
			GROUP_SUPPLEMENTARY_ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIpuPopulation/group_supplementary_attributes.csv
			COMPONENT_ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIpuPopulation/component_attributes.csv
			COMPONENT_POPULATION_NAME=people
			COMPONENT_SAMPLE_DATA=test_data/ummisco/genstar/gama/Genstars/testGenerateIpuPopulation/component_sample.csv
			GROUP_ID_ATTRIBUTE_ON_COMPONENT=HouseholdID
			COMPONENT_CONTROLLED_ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIpuPopulation/component_controlled_attributes.csv
			COMPONENT_CONTROL_TOTALS=test_data/ummisco/genstar/gama/Genstars/testGenerateIpuPopulation/component_ipu_control_totals.csv
			COMPONENT_SUPPLEMENTARY_ATTRIBUTES=test_data/ummisco/genstar/gama/Genstars/testGenerateIpuPopulation/component_supplementary_attributes.csv
			COMPONENT_REFERENCE_ON_GROUP=inhabitans
			GROUP_REFERENCE_ON_COMPONENT=my_household
			MAX_ITERATIONS=4
		 */
		
		final String base_path = "test_data/ummisco/genstar/gama/Genstars/testGenerateIpuPopulation/";
		
		final String ipuPopulationPropertiesFilePath = base_path + "IpuPopulationProperties.properties";

		final String groupAttributesFilePath = base_path + "group_attributes.csv";
		final String groupPopulationName = "household";
		final String groupSampleDataFile = base_path + "group_sample.csv";
		final String groupIdOnGroup = "HouseholdID";
		final String groupControlledAttributesFile = base_path + "group_controlled_attributes.csv";
		final String groupControlTotalsFile = base_path + "group_ipu_control_totals.csv";
		final String groupSupplementaryAttributesFile = base_path + "group_supplementary_attributes.csv";
		
		final String componentAttributesFile = base_path + "component_attributes.csv";
		final String componentPopulationName = "household";
		final String componentSampleDataFile = base_path + "component_sample.csv";
		final String componentIdOnGroup = "HouseholdID";
		final String componentControlledAttributesFile = base_path + "component_controlled_attributes.csv";
		final String componentControlTotalsFile = base_path + "component_ipu_control_totals.csv";
		final String componentSupplementaryAttributesFile = base_path + "component_supplementary_attributes.csv";
		
		
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, true);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					
					if (filePath.endsWith("/IpuPopulationProperties.properties")) { return ipuPopulationPropertiesFilePath; }
					
					// group population
					if (filePath.endsWith("/group_attributes.csv")) { return groupAttributesFilePath; }
					if (filePath.endsWith("/group_sample.csv")) { return groupSampleDataFile; }
					if (filePath.endsWith("/group_controlled_attributes.csv")) { return groupControlledAttributesFile; }
					if (filePath.endsWith("/group_ipu_control_totals.csv")) { return groupControlTotalsFile; }
					if (filePath.endsWith("/group_supplementary_attributes.csv")) { return groupSupplementaryAttributesFile; }
					
					// component population
					if (filePath.endsWith("/component_attributes.csv")) { return componentAttributesFile; }
					if (filePath.endsWith("/component_sample.csv")) { return componentSampleDataFile; }
					if (filePath.endsWith("/component_controlled_attributes.csv")) { return componentControlledAttributesFile; }
					if (filePath.endsWith("/component_ipu_control_totals.csv")) { return componentControlTotalsFile; }
					if (filePath.endsWith("/component_supplementary_attributes.csv")) { return componentSupplementaryAttributesFile; }
					
					
					return null;
				}
			};
		}};

	
		IList generatedIpuPopulation = Genstars.Ipu.generateIpuPopulation(scope, ipuPopulationPropertiesFilePath);
		
		
		// verify the first three elements of a GAMA synthetic population
		assertTrue(((String)generatedIpuPopulation.get(0)).equals("household")); // first element is the population name
		assertTrue(((Map)generatedIpuPopulation.get(1)).isEmpty() ); // second element contains references to "group" agents
		assertTrue( ((Map)generatedIpuPopulation.get(2)).get("people").equals("inhabitants") ); // third element contains references to "component" agents
		
		
		// verify component populations
		for (int i=3; i<generatedIpuPopulation.size(); i++) {
			Map householdEntity = (Map)generatedIpuPopulation.get(i);
			
			IList componentPopulations = (IList) householdEntity.get(IPopulation.class);
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
		
		
		IList generatedPopulation = Genstars.Utils.generateRandomSinglePopulation(scope, populationPropertiesFilePath);
		 
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
		
		
		IList generatedPopulation = Genstars.Utils.generateRandomCompoundPopulation(scope, populationPropertiesFilePath);


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
			
			IList componentPopulations = (IList) householdEntity.get(IPopulation.class);
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
		
		
		IList groupPopulation = Genstars.Utils.generateRandomCompoundPopulation(scope, populationPropertiesFilePath);
		
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
		
		Map<String, String> resultCompoundFilePaths = Genstars.Utils.writePopulationsToCsvFiles(scope, groupPopulation, 
				generatedCompoundPopulationFilePaths, populationAttributesFilePaths);
		
		assertTrue(resultCompoundFilePaths.size() == 2);
		assertTrue(resultCompoundFilePaths.get(groupPopulationName).equals(groupPopulationOutputFile));
		assertTrue(resultCompoundFilePaths.get(componentPopulationName).equals(componentPopulationOutputFile));
	}
	
	
	@Test public void testWriteSinglePopulationToCsvFile(@Mocked final IScope scope, @Mocked final FileUtils fileUtils) throws GenstarException {
		
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
		
		IList singlePopulation = Genstars.Utils.generateRandomSinglePopulation(scope, populationPropertiesFilePath);
		
		
		String populationName = "people";
		String peoplePopulationOutputFile = "test_data/ummisco/genstar/gama/Genstars/testWritePopulationsToCsvFiles/single_population/people_population.csv";
		Map<String, String> populationFilePaths = new HashMap<String, String>();
		populationFilePaths.put(populationName, peoplePopulationOutputFile);
		
		Map<String, String> populationAttributesFilePaths = new HashMap<String, String>();
		populationAttributesFilePaths.put(populationName, attributesCSVFilePath);
		
		
		Map<String, String> resultFilePaths = Genstars.Utils.writePopulationsToCsvFiles(scope, singlePopulation, 
				populationFilePaths, populationAttributesFilePaths);

		
		assertTrue(resultFilePaths.size() == 1);
		assertTrue(resultFilePaths.get(populationName).equals(peoplePopulationOutputFile));
	}
	
	
	@Test public void testGenerateIpfControlTotalsDataSet1(@Mocked final IScope scope, @Mocked final FileUtils fileUtils, @Mocked final IpfGenerationRule generationRule) throws GenstarException {
		
		// dataSet1
		final String dataSet1AttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIpfControlTotals/dataSet1/group_attributes.csv";
		final String dataSet1ControlledAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIpfControlTotals/dataSet1/group_controlled_attributes.csv";
		final String dataSet1PopulationFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIpfControlTotals/dataSet1/group_sample.csv";
		final String dataSet1ControlTotalFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIpfControlTotals/dataSet1/generated_control_totals.csv";
		
		final String controlTotalPropertiesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIpfControlTotals/dataSet1/DataSet1_ControlTotals.properties";
		
		// /dataSet1/group_controlled_attributes.csv
		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, anyBoolean);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("/dataSet1/group_attributes.csv")) { return dataSet1AttributesFilePath; }
					if (filePath.endsWith("/dataSet1/group_controlled_attributes.csv")) { return dataSet1ControlledAttributesFilePath; }
					if (filePath.endsWith("/dataSet1/group_sample.csv")) { return dataSet1PopulationFilePath; }
					if (filePath.endsWith("/dataSet1/generated_control_totals.csv")) { return dataSet1ControlTotalFilePath; }
					if (filePath.endsWith("/dataSet1/DataSet1_ControlTotals.properties")) { return controlTotalPropertiesFilePath; }
					
					return null;
				}
			};
		}};
		
		
		// dataSet1
		/*
		 * group_controlled_attributes.csv
			Household Size
			Household Income
			Number Of Cars
		 */
		
		/*
		 * group_sample.csv
			householdID,householdSize,householdIncome,householdType,numberOfCars
			0,2,High,type3,2
			1,3,High,type1,1
			2,3,High,type1,2
			3,1,High,type2,3
			4,3,High,type1,3
			5,1,High,type3,3
			6,1,High,type3,1
			7,1,Low,type1,1
			8,1,Low,type2,2
			
			==> generated_control_totals.csv
				Household Income,High,Household Size,1,5
				Household Income,High,Household Size,2,2
				Household Income,High,Household Size,3,4
				Household Income,Low,Household Size,1,3
				Household Income,Low,Household Size,2,4
				Household Income,Low,Household Size,3,1
				Number Of Cars,0,Household Size,1,2
				Number Of Cars,1,Household Size,1,2
				Number Of Cars,2,Household Size,1,1
				Number Of Cars,3,Household Size,1,3
				Number Of Cars,0,Household Size,2,2
				Number Of Cars,1,Household Size,2,2
				Number Of Cars,2,Household Size,2,1
				Number Of Cars,3,Household Size,2,1
				Number Of Cars,0,Household Size,3,1
				Number Of Cars,1,Household Size,3,2
				Number Of Cars,2,Household Size,3,1
				Number Of Cars,3,Household Size,3,1
				Household Income,High,Number Of Cars,0,3
				Household Income,High,Number Of Cars,1,3
				Household Income,High,Number Of Cars,2,2
				Household Income,High,Number Of Cars,3,3
				Household Income,Low,Number Of Cars,0,2
				Household Income,Low,Number Of Cars,1,3
				Household Income,Low,Number Of Cars,2,1
				Household Income,Low,Number Of Cars,3,2
		 */
		
		// delete the resulting file if exists
		File resultingFile = new File(dataSet1ControlTotalFilePath);
		if (resultingFile.exists()) { resultingFile.delete(); }
		
		Genstars.Ipf.generateIpfControlTotals(scope, controlTotalPropertiesFilePath);
		final GenstarCsvFile dataSet1controlTotalFile = new GenstarCsvFile(dataSet1ControlTotalFilePath, false);
		assertTrue(dataSet1controlTotalFile.getColumns() == 5);
		assertTrue(dataSet1controlTotalFile.getRows() == 26);

		// dataSet1
		final ISyntheticPopulationGenerator dataSet1Generator = new SampleBasedGenerator("dummy generator");
		GenstarCsvFile dataSet1AttributesFile = new GenstarCsvFile(dataSet1AttributesFilePath, true);
		AttributeUtils.createAttributesFromCsvFile(dataSet1Generator, dataSet1AttributesFile);
		
		AbstractAttribute householdSizeAttr = dataSet1Generator.getAttributeByNameOnData("Household Size");
		AttributeValue size1 = new UniqueValue(DataType.INTEGER, "1");
		AttributeValue size2 = new UniqueValue(DataType.INTEGER, "2");
		AttributeValue size3 = new UniqueValue(DataType.INTEGER, "3");
		
		AbstractAttribute householdIncomeAttr = dataSet1Generator.getAttributeByNameOnData("Household Income");
		AttributeValue incomeLow = new UniqueValue(DataType.STRING, "Low");
		AttributeValue incomeHigh = new UniqueValue(DataType.STRING, "High");
		
		AbstractAttribute nbOfCarsAttr = dataSet1Generator.getAttributeByNameOnData("Number Of Cars");
		AttributeValue zeroCar = new UniqueValue(DataType.INTEGER, "0");
		AttributeValue oneCar = new UniqueValue(DataType.INTEGER, "1");
		AttributeValue twoCars = new UniqueValue(DataType.INTEGER, "2");
		AttributeValue threeCars = new UniqueValue(DataType.INTEGER, "3");
		
		final List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
		controlledAttributes.add(householdSizeAttr);
		controlledAttributes.add(householdIncomeAttr);
		controlledAttributes.add(nbOfCarsAttr);
		

		new Expectations() {{
			generationRule.getControlTotalsFile();
			result = dataSet1controlTotalFile;
			
			generationRule.getControlledAttributes();
			result = controlledAttributes;
		}};
		
		
		IpfControlTotals dataSet1ControlTotals = new IpfControlTotals(generationRule);
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		
		// Household Size,1,Household Income,High,5
		matchingCriteria.put(householdSizeAttr, size1);
		matchingCriteria.put(householdIncomeAttr, incomeHigh);
		List<AttributeValuesFrequency> avfs = dataSet1ControlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(avfs.size() == 1);
		assertTrue(avfs.get(0).getFrequency() == 5);
		
		// Household Size,2,Household Income,Low,4
		matchingCriteria.put(householdSizeAttr, size2);
		matchingCriteria.put(householdIncomeAttr, incomeLow);
		avfs = dataSet1ControlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(avfs.size() == 1);
		assertTrue(avfs.get(0).getFrequency() == 4);
		
		// Household Size,1,Number Of Cars,1,2
		matchingCriteria.clear();
		matchingCriteria.put(householdSizeAttr, size1);
		matchingCriteria.put(nbOfCarsAttr, oneCar);
		avfs = dataSet1ControlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(avfs.size() == 1);
		assertTrue(avfs.get(0).getFrequency() == 2);
		
		// Household Size,2,Number Of Cars,0,2
		matchingCriteria.put(householdSizeAttr, size2);
		matchingCriteria.put(nbOfCarsAttr, zeroCar);
		avfs = dataSet1ControlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(avfs.size() == 1);
		assertTrue(avfs.get(0).getFrequency() == 2);
		
		// Household Income,High,Number Of Cars,3,3
		matchingCriteria.clear();
		matchingCriteria.put(householdIncomeAttr, incomeHigh);
		matchingCriteria.put(nbOfCarsAttr, threeCars);
		avfs = dataSet1ControlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(avfs.size() == 1);
		assertTrue(avfs.get(0).getFrequency() == 3);
		
		// Household Income,Low,Number Of Cars,3,2
		matchingCriteria.put(householdIncomeAttr, incomeLow);
		avfs = dataSet1ControlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(avfs.size() == 1);
		assertTrue(avfs.get(0).getFrequency() == 2);
	}
	
	
	@Test public void testGenerateIpfControlTotalsDataSet2(@Mocked final IScope scope, @Mocked final FileUtils fileUtils, @Mocked final IpfGenerationRule generationRule) throws GenstarException {
		
		// dataSet2
		final String dataSet2AttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIpfControlTotals/dataSet2/attributes.csv";
		final String dataSet2ControlledAttributesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIpfControlTotals/dataSet2/controlled_attributes.csv";
		final String dataSet2PopulationFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIpfControlTotals/dataSet2/people_sample.csv";
		final String dataSet2ControlTotalFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIpfControlTotals/dataSet2/generated_control_totals.csv";
		
		final String controlTotalPropertiesFilePath = "test_data/ummisco/genstar/gama/Genstars/testGenerateIpfControlTotals/dataSet2/DataSet2_ControlTotals.properties";
		

		new Expectations() {{
			FileUtils.constructAbsoluteFilePath(scope, anyString, anyBoolean);
			result = new Delegate() {
				String delegate(IScope scope, String filePath, boolean mustExist) {
					if (filePath.endsWith("/dataSet2/attributes.csv")) { return dataSet2AttributesFilePath; }
					if (filePath.endsWith("/dataSet2/controlled_attributes.csv")) { return dataSet2ControlledAttributesFilePath; }
					if (filePath.endsWith("/dataSet2/people_sample.csv")) { return dataSet2PopulationFilePath; }
					if (filePath.endsWith("/dataSet2/generated_control_totals.csv")) { return dataSet2ControlTotalFilePath; }
					if (filePath.endsWith("/dataSet2/DataSet2_ControlTotals.properties")) { return controlTotalPropertiesFilePath; }
					
					return null;
				}
			};
		}};
		
		
		// dataSet2
		/*
		 * attributes.csv
				Name On Data,Name On Entity,Data Type,Value Type On Data,Values,Value Type On Entity
				Age,age,int,Range,0:15; 16:20,Unique
				Gender,gender,bool,Unique,true; false,Unique
				Work,work,string,Unique,agriculteur; artisant,Unique
		 */
		/*
		 * controlled_attributes.csv
				Age
				Gender
		 */
		/*
		 * people_sample.csv
			age,gender,work
			1,true,agriculteur
			2,true,agriculteur
			3,false,agriculteur
			4,false,agriculteur
			16,true,artisant
			17,true,artisant
			18,false,artisant
			19,false,artisant			
			
			==>
				Age,0:15,4
				Age,16:20,4
				Gender,true,4
				Gender,false,4
		 */
		
		// delete the resulting file if exists
		File resultingFile = new File(dataSet2ControlTotalFilePath);
		if (resultingFile.exists()) { resultingFile.delete(); }
		
		Genstars.Ipf.generateIpfControlTotals(scope, controlTotalPropertiesFilePath);
		final GenstarCsvFile dataSet2controlTotalFile = new GenstarCsvFile(dataSet2ControlTotalFilePath, false);
		assertTrue(dataSet2controlTotalFile.getColumns() == 3);
		assertTrue(dataSet2controlTotalFile.getRows() == 4);
		
		// dataSet2
		final ISyntheticPopulationGenerator dataSet2Generator = new SampleBasedGenerator("dummy generator");
		GenstarCsvFile dataSet2AttributesFile = new GenstarCsvFile(dataSet2AttributesFilePath, true);
		AttributeUtils.createAttributesFromCsvFile(dataSet2Generator, dataSet2AttributesFile);
		
		AbstractAttribute ageAttr = dataSet2Generator.getAttributeByNameOnData("Age");
		// 0:15; 16:20
		RangeValue zeroValue = new RangeValue(DataType.INTEGER, "0", "15");
		RangeValue sixteenValue = new RangeValue(DataType.INTEGER, "16", "20");
		
		AbstractAttribute genderAttr = dataSet2Generator.getAttributeByNameOnData("Gender");
		AttributeValue maleValue = new UniqueValue(DataType.BOOL, "true");
		AttributeValue femaleValue = new UniqueValue(DataType.BOOL, "false");

		final List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
		controlledAttributes.add(ageAttr);
		controlledAttributes.add(genderAttr);
		

		new Expectations() {{
			generationRule.getControlTotalsFile();
			result = dataSet2controlTotalFile;
			
			generationRule.getControlledAttributes();
			result = controlledAttributes;
		}};
		
		
		IpfControlTotals dataSet2ControlTotals = new IpfControlTotals(generationRule);
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		
		// Age,0:15,4
		matchingCriteria.put(ageAttr, zeroValue);
		List<AttributeValuesFrequency> avfs = dataSet2ControlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(avfs.size() == 1);
		assertTrue(avfs.get(0).getFrequency() == 4);
		
		// Age,16:20,4
		matchingCriteria.put(ageAttr, sixteenValue);
		avfs = dataSet2ControlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(avfs.size() == 1);
		assertTrue(avfs.get(0).getFrequency() == 4);
		
		// Gender,true,4
		matchingCriteria.clear();
		matchingCriteria.put(genderAttr, maleValue);
		avfs = dataSet2ControlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(avfs.size() == 1);
		assertTrue(avfs.get(0).getFrequency() == 4);
		
		// Gender,false,4
		matchingCriteria.put(genderAttr, femaleValue);
		avfs = dataSet2ControlTotals.getMatchingAttributeValuesFrequencies(matchingCriteria);
		assertTrue(avfs.size() == 1);
		assertTrue(avfs.get(0).getFrequency() == 4);
		
	}
	
	
	@Test public void testExtractIpuPopulation() {
		fail("not yet implemented");
	}
	
	
	@Test public void testExtractIpfSinglePopulation() {
		fail("not yet implemented");
	}
	
	
	@Test public void testExtractIpfCompoundPopulation() {
		fail("not yet implemented");
	}

}
