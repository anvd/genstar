package ummisco.genstar.gama;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mockit.Deencapsulation;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaFont;
import msi.gama.util.IList;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.file.IFileMetaDataProvider;
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
import msi.gaml.types.Types;

import org.junit.Assert;
import org.junit.Test;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.AttributeValuesFrequency;
import ummisco.genstar.metamodel.DataType;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.RangeValue;
import ummisco.genstar.metamodel.RangeValuesAttribute;
import ummisco.genstar.metamodel.SyntheticPopulationGenerator;
import ummisco.genstar.metamodel.UniqueValue;
import ummisco.genstar.metamodel.UniqueValuesAttribute;

public class GenstarUtilsTest {
	
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

	@Test
	public final void testCreateUniqueValueAttributeExpectationsApproach(@Mocked final ISyntheticPopulationGenerator mockedGenerator,
			@Mocked final UniqueValuesAttribute uAttrValue) throws GenstarException {
		
		// record
		new Expectations() {{
			new UniqueValuesAttribute(mockedGenerator, "Category", "category", (DataType) withNotNull(), (Class) withNotNull());
			result = uAttrValue;
			times = 1;
			
			mockedGenerator.addAttribute(uAttrValue);
			times = 1;
		}};
		
		
		// replay
		GenstarUtils.createUniqueValueAttributePublicProxy(mockedGenerator, "Category", "category", DataType.STRING, "C0; C1; C2; C3; C4; C5; C6; C7", UniqueValue.class, "dummyAttributesFile.csv");
		
		// verify
	}
	
	@Test
	public final <T extends ISyntheticPopulationGenerator> void testCreateUniqueValueAttributeMockUpApproach() throws GenstarException {
		
		new MockUp<T>() {
			Set<AbstractAttribute> attributes = new HashSet<AbstractAttribute>();
			
			@Mock void addAttribute(AbstractAttribute attribute) { attributes.add(attribute); }
			
			@Mock Set<AbstractAttribute> getAttributes() { return attributes; }
		};

		ISyntheticPopulationGenerator mockedGenerator = new SyntheticPopulationGenerator("dummy generator", 10);
		assertTrue(mockedGenerator.getAttributes().isEmpty());
		GenstarUtils.createUniqueValueAttributePublicProxy(mockedGenerator, "Category", "category", DataType.STRING, "C0; C1; C2; C3; C4; C5; C6; C7", UniqueValue.class, "dummyAttributesFile.csv");
		assertTrue(mockedGenerator.getAttributes().size() == 1);
		
		List<AbstractAttribute> attributes = new ArrayList<AbstractAttribute>(mockedGenerator.getAttributes());
		AbstractAttribute createdAttribute = attributes.get(0);
		assertTrue(createdAttribute.getNameOnData().equals("Category"));
	}
	
	@Test
	public final void testCreateRangeValueAttributeExpectationsApproach(@Mocked final ISyntheticPopulationGenerator mockedGenerator,
			@Mocked final RangeValuesAttribute mockedRangeValuesAttribute, @Mocked final RangeValue mockedRangeValue) throws GenstarException {
		
		// record
		new Expectations() {{
			new RangeValuesAttribute(mockedGenerator, "Age", "age", DataType.INTEGER, UniqueValue.class);
			result = mockedRangeValuesAttribute;
			times = 1;
			
			new RangeValue(DataType.INTEGER, (String) withNotNull(), (String) withNotNull());
			result = mockedRangeValue;
			times = 7;
			
			mockedRangeValuesAttribute.add(mockedRangeValue);
			times = 7;
			
			mockedGenerator.addAttribute(mockedRangeValuesAttribute);
			times = 1;
		}};
		
		// replay
		GenstarUtils.createRangeValueAttributePublicProxy(mockedGenerator, "Age", "age", DataType.INTEGER, "0:4; 5:17; 18:24; 25:34; 35:49; 50:64; 65:100", 
				UniqueValue.class, "Dummy CSV attribute file");
	}
	
	@Test
	public final void testCreateAttributesFromCSVFileExpectationAproach(@Mocked final IScope scope, 
			@Mocked final IMatrix mockedMatrix, @Mocked final GamaCSVFile mockedAttributesCSVFile) throws GenstarException {
		
		
		final class MockedIList<String> extends MockUp<IList<String>> {
			List<String> data = new ArrayList<String>();
			
			@Mock boolean add(String e) { return data.add(e); }
			
			@Mock String get(int index) { return data.get(index); }
			
			@Mock int size() { return data.size(); }
		};
				
		
		final IList<String> attributes = (IList<String>) new MockedIList().getMockInstance();
		attributes.add("Name On Data");
		attributes.add("Name On Entity");
		attributes.add("Data Type");
		attributes.add("Value Type On Data");
		attributes.add("Values");
		attributes.add("Value Type On Entity");

		final IList<String> line0 = (IList<String>) new MockedIList().getMockInstance();
		line0.add("Category");
		line0.add("category");
		line0.add("string");
		line0.add("Unique");
		line0.add("C0; C1; C2; C3; C4; C5; C6; C7");
		line0.add("Unique");
		
		final IList<String> line1 = (IList<String>) new MockedIList().getMockInstance();
		line1.add("Gender");
		line1.add("gender");
		line1.add("bool");
		line1.add("Unique");
		line1.add("true; false");
		line1.add("Unique");
		
		final IList<String> line2 = (IList<String>) new MockedIList().getMockInstance();
		line2.add("Age");
		line2.add("age");
		line2.add("int");
		line2.add("Range");
		line2.add("0:4; 5:17; 18:24; 25:34; 35:49; 50:64; 65:100");
		line2.add("Unique");
		
		final ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("dummy generator", 10);
		
		new Expectations() {{
			mockedMatrix.getCols(scope);
			result = 6;
			times = 1;
			
			mockedMatrix.getRows(scope);
			result = 3;
			times = 1;
			
			mockedMatrix.getRow(scope, 0);
			result = line0;
			
			mockedMatrix.getRow(scope, 1);
			result = line1;

			mockedMatrix.getRow(scope, 2);
			result = line2;
			
			mockedAttributesCSVFile.getPath();
			result = "Dummy CSV File";

			mockedAttributesCSVFile.getContents(scope);
			result = mockedMatrix;
			
			mockedAttributesCSVFile.getAttributes(scope);
			result = attributes;
			times = 1;
			
			// TODO expression requirements
			// 	3 Attributes are created: 2 unique and 1 range
			//	"addAttribute" is invoked 3 times on the generator

		}};
		
		assertTrue(generator.getAttributes().size() == 0);
		
		Deencapsulation.invoke(GenstarUtils.class, "createAttributesFromCSVFile", scope, generator, mockedAttributesCSVFile);
		
		assertTrue(generator.getAttributes().size() == 3);
		assertTrue(generator.getAttribute("Category") != null);
		assertTrue(generator.getAttribute("Gender") != null);
		assertTrue(generator.getAttribute("Age") != null);
		
	}	
	
	@Test public void testCreateFrequencyDistributionGenerationFromSampleData(@Mocked final IScope scope,
			@Mocked final IExperimentAgent experiment) throws GenstarException {
		// input: distributionFormatCSVFile & sampleDataCSVFile
		// output: the newly created FrequencyDistributionGenerationRule
		
		new MockUp<GuiUtils>() {
			@Mock IFileMetaDataProvider getMetaDataProvider() { return null; }
			
			@Mock void updateSubStatusCompletion(final double n) {}
		};
		
		new Expectations() {{
			experiment.getWorkingPath();
			result = "./";
		}};
		
		ISyntheticPopulationGenerator generator = new SyntheticPopulationGenerator("dummy generator", 10);
		GamaCSVFile attributesCSVFile = new GamaCSVFile(scope, "test/data/attributes.csv", ",", Types.STRING, true);
		Deencapsulation.invoke(GenstarUtils.class, "createAttributesFromCSVFile", scope, generator, attributesCSVFile);
		
		
		GamaCSVFile distributionFormatCSVFile = new GamaCSVFile(scope, "test/data/distributionFormat.csv", ",", Types.STRING, true);
		GamaCSVFile sampleDataCSVFile = new GamaCSVFile(scope, "test/data/sampleData.csv", ",", Types.STRING, true);
		FrequencyDistributionGenerationRule rule = (FrequencyDistributionGenerationRule) Deencapsulation.invoke(GenstarUtils.class, "createFrequencyDistributionFromSampleData", scope, generator, distributionFormatCSVFile, sampleDataCSVFile);
		assertTrue(rule.getAttributes().size() == 2);
		assertTrue(rule.getInputAttributeAtOrder(0) == null);
		assertTrue(rule.getOutputAttributeAtOrder(0).getNameOnData().equals("Category"));
		assertTrue(rule.getOutputAttributeAtOrder(1).getNameOnData().equals("Age"));
		int totalNbOfAttributeValues = 1;
		for (AbstractAttribute a : rule.getAttributes()) { totalNbOfAttributeValues *= a.values().size(); }
		assertTrue(rule.getAttributeValuesFrequencies().size() == totalNbOfAttributeValues);
		
		
		/*
		 * sampleData.csv
				Category,Gender,Age
				C0,false,5
				C3,true,5
				C3,false,5
				C5,true,5
				C7,false,5
		 */
		Set<AttributeValuesFrequency> attributeValuesFrequencies = rule.getAttributeValuesFrequencies();
		
		// C0, 5
		Map<AbstractAttribute, AttributeValue> map1 = new HashMap<AbstractAttribute, AttributeValue>();
		
		AttributeValue c0Value = new UniqueValue(DataType.STRING, "C0");
		map1.put(generator.getAttribute("Category"), c0Value);
		
		RangeValuesAttribute ageAttribute = (RangeValuesAttribute) generator.getAttribute("Age");
		Set<AttributeValue> ageValues = ageAttribute.values();
		UniqueValue age5UniqueValue = new UniqueValue(DataType.INTEGER, "5");
		RangeValue age5RangeValue = null;
		for (AttributeValue a : ageValues) {
			if ( ((RangeValue)a).cover(age5UniqueValue)) {
				age5RangeValue = (RangeValue)a;
				break;
			}
		}
		if (age5RangeValue == null) { Assert.fail("No age value is matched"); }
		map1.put(ageAttribute, age5RangeValue);

		boolean isMatchMap1 = false;
		for (AttributeValuesFrequency f : attributeValuesFrequencies) {
			if (f.isMatch(map1)) {
				if (isMatchMap1) { Assert.fail("Attributevalue is matched several times (map1)"); }
				
				assertTrue(f.getFrequency() == 1);
				isMatchMap1 = true;
			}
		}
		if (!isMatchMap1) { Assert.fail("No attributeValue is matched (map1)"); }
		
		// C3, 5 (frequency == 2)
		Map<AbstractAttribute, AttributeValue> map2 = new HashMap<AbstractAttribute, AttributeValue>();
		AttributeValue c5Value = new UniqueValue(DataType.STRING, "C3");
		map2.put(generator.getAttribute("Category"), c5Value);
		map2.put(ageAttribute, age5RangeValue);
		
		boolean isMatchMap2 = false;
		for (AttributeValuesFrequency f : attributeValuesFrequencies) {
			if (f.isMatch(map2)) {
				if (isMatchMap2) { Assert.fail("Attributevalue is matched several times (map2)"); }
				
				assertTrue(f.getFrequency() == 2);
				isMatchMap2 = true;
			}
		}
		if (!isMatchMap2) { Assert.fail("No attributeValue is matched (map2)"); }
	}	
	
}
