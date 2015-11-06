package ummisco.genstar.gama;

import mockit.Delegate;
import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import msi.gama.common.util.GuiUtils;
import msi.gama.kernel.experiment.IExperimentAgent;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaFont;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.IFileMetaDataProvider;
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
import msi.gaml.types.IType;
import msi.gaml.types.Types;

import org.junit.Test;

import ummisco.genstar.exception.GenstarException;

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
	

	@Test(expected = GamaRuntimeException.class)
	public final void whenAttributeDescriptionFileNotExistThenExceptionIsThrownMockUpApproach() {
		new MockUp<GamaFile>() {
			@Mock
			public void $init(final IScope scope, final String pathName) throws GamaRuntimeException {
				// nothing to do
			}
		};
		
		new MockUp<GamaCSVFile>() {
			@Mock
			public void $init(final IScope scope, final String pathName, final String separator, final IType type,
					final Boolean withHeader) {
				if (pathName.equals("NotExistAttributeFile.csv"))  { throw GamaRuntimeException.error(pathName + " not found", null); }
			}
		};
				
		Genstars.generateFrequencyDistribution(null, "NotExistAttributeFile.csv", "", "", "");
	}
	
	@Test(expected = GamaRuntimeException.class)
	public final void whenAttributeDescriptionFileNotExistThenExceptionIsThrownExpectationsApproach(@Mocked final GamaCSVFile anyCSVFile, @Mocked final IScope anyScope) {

		new Expectations() {{
			new GamaCSVFile(anyScope, anyString, ",", Types.STRING, true);
			result = new Delegate() {
				void delegate(final IScope scope, final String pathName, final String separator, final IType type, final Boolean withHeader) {
					if (pathName.equals("NotExistAttributeFile.csv")) { throw GamaRuntimeException.error("NotExistAttributeFile.csv not found", scope); }
				}
			};
		}};

		Genstars.generateFrequencyDistribution(anyScope, "", "", "NotExistAttributeFile.csv", "");
	}
	
	@Test(expected = GamaRuntimeException.class)
	public final void whenSampleDataCSVFileNotExistThenExceptionIsThrownMockUpApproach() {
		new MockUp<GamaFile>() {
			@Mock
			public void $init(final IScope scope, final String pathName) throws GamaRuntimeException {
				// nothing to do
			}
		};

		new MockUp<GamaCSVFile>() {
			@Mock
			public void $init(final IScope scope, final String pathName, final String separator, final IType type,
					final Boolean withHeader) {
				if (pathName.equals("NotExistSampleDataFile.csv")) { throw GamaRuntimeException.error(pathName + " not found", null); }
			}
		};
		
		Genstars.generateFrequencyDistribution(null, "", "NotExistSampleDataFile.csv", "", "");
	}
	
	@Test(expected = GamaRuntimeException.class)
	public final void whenSampleDataCSVFileNotExistThenExceptionIsThrownExpectationsApproach(@Mocked GamaCSVFile anyCSVFile, @Mocked final IScope anyScope) {
		
		new Expectations() {{
			new GamaCSVFile(anyScope, anyString, ",", Types.STRING, true);
			result = new Delegate() {
				void delegate(final IScope scope, final String pathName, final String separator, final IType type, final Boolean withHeader) {
					if (pathName.equals("NotExistSampleDataFile.csv")) { throw GamaRuntimeException.error("NotExistSampleDataFile.csv not found", scope); }
				}
			};
		}};

		Genstars.generateFrequencyDistribution(anyScope, "", "", "NotExistSampleDataFile.csv", "");
	}

	@Test(expected = GamaRuntimeException.class)
	public final void whenDistributionFormatCSVFileNotExistThenExceptionIsThrown() {
		new MockUp<GamaFile>() {
			@Mock
			public void $init(final IScope scope, final String pathName) throws GamaRuntimeException {
				// nothing to do
			}
		};

		new MockUp<GamaCSVFile>() {
			@Mock
			public void $init(final IScope scope, final String pathName, final String separator, final IType type,
					final Boolean withHeader) {
				if (pathName.equals("NotExistDistributionFormatFile.csv")) { throw GamaRuntimeException.error(pathName + " not found", null); }
			}
		};
		
		Genstars.generateFrequencyDistribution(null, "", "", "NotExistDistributionFormatFile.csv", "");
	}
	
	@Test(expected = GamaRuntimeException.class)
	public final void whenDistributionFormatCSVFileNotExistThenExceptionIsThrownExpectationsApproach(@Mocked final GamaCSVFile anyCSVFile, @Mocked final IScope scope) {
		
		new Expectations() {{
			new GamaCSVFile(scope, anyString, ",", Types.STRING, true);
			result = new Delegate() {
				void delegate(final IScope scope, final String pathName, final String separator, final IType type, final Boolean withHeader) {
					if (pathName.equals("NotExistDistributionFormatFile.csv")) { throw GamaRuntimeException.error("NotExistDistributionFormatFile.csv not found", scope); }
				}
			};
		}};

		Genstars.generateFrequencyDistribution(scope, "", "", "NotExistDistributionFormatFile.csv", "");
	}
	
		
	@Test public final void testGenerateFrequencyDistributionCreateAttributes(@Mocked final IScope scope,
			@Mocked final IExperimentAgent experiment) throws GenstarException {
		
		new MockUp<GuiUtils>() {
			@Mock IFileMetaDataProvider getMetaDataProvider() { return null; }
			
			@Mock void updateSubStatusCompletion(final double n) {}
		};
		
		new Expectations() {{
			experiment.getWorkingPath();
			result = "./";
			times = 5;
		}};
		
		Genstars.generateFrequencyDistribution(scope, "test/data/attributes.csv", "test/data/sampleData.csv", "test/data/distributionFormat.csv", "test/data/resultDistribution.csv");
		
		GamaCSVFile resultDistributionFile = new GamaCSVFile(scope, "./test/data/resultDistribution.csv", ",", Types.STRING, true);
	}
	
}
