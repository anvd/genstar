package idees.genstar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import idees.genstar.configuration.GSConfiguration;
import idees.genstar.configuration.GSDataFile;
import idees.genstar.configuration.GSMetaDataType;
import idees.genstar.configuration.xml.GenstarXmlSerializer;
import idees.genstar.control.AControl;
import idees.genstar.control.ControlContingency;
import idees.genstar.control.ControlFrequency;
import idees.genstar.datareader.GSDataParser;
import idees.genstar.datareader.ISurvey;
import idees.genstar.datareader.exception.InputFileNotSupportedException;
import idees.genstar.distribution.GSConditionalDistribution;
import idees.genstar.distribution.GSContingencyTable;
import idees.genstar.distribution.GSCoordinate;
import idees.genstar.distribution.GSJointDistribution;
import idees.genstar.distribution.exception.IllegalAlignDistributions;
import idees.genstar.distribution.exception.IllegalDistributionCreation;
import idees.genstar.distribution.exception.IllegalNDimensionalMatrixAccess;
import idees.genstar.distribution.innerstructure.ACoordinate;
import idees.genstar.distribution.innerstructure.AGSFullNDimensionalMatrix;
import idees.genstar.distribution.innerstructure.AGSSegmentedNDimensionalMatrix;
import idees.genstar.distribution.innerstructure.InDimensionalMatrix;
import idees.genstar.exception.IncompatibleControlTotalException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;

/**
 * The main class to generate the distribution of attributes based on data !
 * <p>
 *  TODO: make the process being aimed at producing one type of distribution, based on basic data requirement and adapted inference algorithm: 
 * <p><ul>
 * <li> <b> Joint distribution </b>: in case of low dimensionality and with full data knowledge
 * <li> <b> Contingency table </b>: like joint distribution but with contingent - not a distribution per se
 * <li> <b> Conditional distribution </b>: composition of multiple joint distribution (on few dimensions)
 * <li> <b> Bayesian network </b>: kind of conditional distribution but with higher level structure (network) assumptions
 * <li> <b> Markov chain </b>: kind of conditional distribution but with cyclic chained probabilities
 * </ul><p>
 * 
 * TODO: The whole process should be as follow: <p>
 * <ul> 
 *  <li> First -> The builder needs a estimation algorithm / default is no estimation i.e. joint distribution or conditional distribution
 *  <li> Second -> In order to create estimation algorithm you'll need some required basic data, 
 *  e.g. for IPF sample + control totals, for IPU sample + control total for individual and household, and some
 *  algorithm related parameters, e.g. bayesian network will need either a contraint function and / or a network structure
 * </ul> Third -> Obtain a distribution like generator that is able to sample a synthetic population
 * <p>
 * 
 * TODO: alternatively default non estimate joint or conditional distribution could be used 
 * to setup a combinatorial optimization draw with replacement from a sample
 * 
 * @author kevinchapuis
 *
 */
public class DistributionFactory {

	private final GSDataParser dataParser;

	// TODO: move to parameters
	public double epsilon = Math.pow(10, -4);

	private GSConfiguration configuration;

	private Set<InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number>> distributionsSet;

	public DistributionFactory(){
		this.dataParser = new GSDataParser();
	}

	// ----------------------------- BUILDER' SETTER ----------------------------- //

	public void setGenstarConfiguration(GSConfiguration configuration){
		this.configuration = configuration;
	}

	public void setGenstarConfiguration(Path configurationFilePath){
		GenstarXmlSerializer gxs = null;
		try {
			gxs = new GenstarXmlSerializer();
			configuration = gxs.deserializeGSConfig(configurationFilePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// ------------------------------ BUILD TABLE METHODS ------------------------------ //

	/** 
	 * 
	 * Main methods to parse and get control totals from a {@link GSDataFile} file and with the
	 * help of a specified set of {@link AbstractAttribute}
	 * <p>
	 * Method gets all data file from the builder and harmonizes them to one another using line
	 * identifier attributes
	 * 
	 * WARNING: nothing has been done yet for {@link GSMetaDataType#Sample} data
	 * 
	 * @param file
	 * @param attributes
	 * @return {@link AGSControlTable}: with {@link ACoordinate} made of {@link AttributeValue}s associate to {@link Double} value
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws InputFileNotSupportedException
	 * @throws IllegalDistributionCreation 
	 * @throws IllegalDimMatrixRequest 
	 */
	public Set<InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number>> buildDistributions() 
			throws InvalidFormatException, IOException, InputFileNotSupportedException, IllegalNDimensionalMatrixAccess, IllegalDistributionCreation{
		distributionsSet = new HashSet<>();
		for(GSDataFile file : this.configuration.getDataFiles())
			if(!file.getDataFileType().equals(GSMetaDataType.Sample))
				distributionsSet.addAll(getDistributions(file, this.configuration.getAttributes()));
		return distributionsSet;
	}
	
	/**
	 * TODO
	 * 
	 * @return
	 * @throws IncompatibleControlTotalException
	 * @throws IllegalNDimensionalMatrixAccess
	 * @throws IllegalAlignDistributions
	 * @throws IllegalDistributionCreation
	 */
	public InDimensionalMatrix<AbstractAttribute, AttributeValue, Double> getDistribution() 
			throws IncompatibleControlTotalException, IllegalNDimensionalMatrixAccess, IllegalAlignDistributions, IllegalDistributionCreation{
		if(distributionsSet.size() == 1)
			return createDistribution(distributionsSet.iterator().next());
		else {
			for(InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> matrix : distributionsSet
					.stream().filter(c -> c.getMetaDataType().equals(GSMetaDataType.IndivFrequenceTable))
					.collect(Collectors.toList())){
				InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> matrixOfReference = distributionsSet
						.stream().filter(ctFitter -> !ctFitter.isIndividualFrameOfReference()
								&& ctFitter.getDimensions().stream().anyMatch(d -> matrix.getDimensions().contains(d)))
						.sorted((jd1, jd2) -> (int) jd2.getDimensions().stream().filter(d -> matrix.getDimensions().contains(d)).count() 
								- (int) jd1.getDimensions().stream().filter(d -> matrix.getDimensions().contains(d)).count())
						.findFirst().get(); 
				getDistributionsAlign(matrix, matrixOfReference);
			}
			for(InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> matrix : distributionsSet)
				getFrequencyMatrix(matrix);
			return createDistribution(distributionsSet);
		}
	}
	

	//////////////////////////////////////////////////////////////////////////////////////
	// ------------------------------- MAIN BACK OFFICE ------------------------------- //
	//////////////////////////////////////////////////////////////////////////////////////
	
	
	/**
	 * The one line entry to construct a distribution from a control table
	 * 
	 * TODO: Remove
	 * 
	 * @param controlTableSet
	 * @return {@link GSJointDistribution}
	 * @throws IllegalControlSettingForDistribution
	 * @throws IncompatibleControlTotalException 
	 * @throws IllegalNDimensionalMatrixAccess 
	 * @throws IllegalAlignDistributions 
	 * @throws IllegalDimMatrixRequest 
	 */
	private AGSFullNDimensionalMatrix<Double> createDistribution(InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> matrix) 
			throws IncompatibleControlTotalException, IllegalNDimensionalMatrixAccess, IllegalAlignDistributions{
		return getFrequencyMatrix(matrix);
	}

	/**
	 * Create a conditional distribution from various joint distributions
	 * 
	 * TODO: Remove
	 * 
	 * @param matrixSet
	 * @return {@link GSConditionalDistribution}
	 * @throws IllegalControlSettingForDistribution
	 * @throws IllegalDimMatrixRequest
	 * @throws IncompatibleControlTotalException
	 * @throws IllegalConditionalDistributionCreation
	 * @throws IllegalNDimensionalMatrixAccess 
	 * @throws IllegalAlignDistributions 
	 */
	private AGSSegmentedNDimensionalMatrix<Double> createDistribution(Set<InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number>> matrixSet) 
			throws IncompatibleControlTotalException, IllegalDistributionCreation, IllegalNDimensionalMatrixAccess, IllegalAlignDistributions{
		Set<AGSFullNDimensionalMatrix<Double>> jointDistributionSet = new HashSet<>();
		for(InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> controlTable : matrixSet)
			jointDistributionSet.add(createDistribution(controlTable));
		return new GSConditionalDistribution(jointDistributionSet);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	// ----------------------------- TRANSPOSITION METHODS ---------------------------- //
	//////////////////////////////////////////////////////////////////////////////////////	
	
	
	/**
	 * TODO: javadoc
	 * 
	 * @param matrix
	 * @return
	 * @throws IllegalNDimensionalMatrixAccess
	 * @throws IllegalAlignDistributions
	 * @throws IncompatibleControlTotalException
	 */
	private AGSFullNDimensionalMatrix<Double> getFrequencyMatrix(InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> matrix) 
			throws IllegalNDimensionalMatrixAccess, IllegalAlignDistributions, IncompatibleControlTotalException {
		if(matrix.getMetaDataType().equals(GSMetaDataType.IndivFrequenceTable))
			throw new IllegalAlignDistributions("Cannot change individual frequency matrix into a contingency one befor alignement to global frame of reference", matrix.getMetaDataType());
		AGSFullNDimensionalMatrix<Double> freqMatrix = 
				new GSJointDistribution(matrix.getDimensions().stream().collect(Collectors.toMap(d -> d, d -> d.valuesOnData())),
						GSMetaDataType.CompletFrequenceTable);
		AControl<? extends Number> total = checkAndPeakControlTotal(matrix);
		for(ACoordinate<AbstractAttribute, AttributeValue> coord : matrix.getMatrix().keySet())
			freqMatrix.addValue(coord, new ControlFrequency(matrix.getVal(coord).getValue().doubleValue() / total.getValue().doubleValue()));
		return freqMatrix;
	}


	/**
	 * Meant to transpose a frequency control table into a contingency control table with precision (more or less one).
	 * Actually, every {@link AGSControlTable} could be transpose to a {@link GSContingencyTable}.
	 * 
	 * FIXME: move to {@link InDimensionalMatrix}
	 * 
	 * @param matrix
	 * @return
	 * @throws IllegalAlignDistributions
	 * @throws IllegalDimMatrixRequest
	 */
	@SuppressWarnings("unused")
	private AGSFullNDimensionalMatrix<Integer> getContingentMatrix(InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> matrix) 
					throws IllegalNDimensionalMatrixAccess, IllegalAlignDistributions {
		if(matrix.getMetaDataType().equals(GSMetaDataType.IndivFrequenceTable))
			throw new IllegalAlignDistributions("Cannot change individual frequency matrix into a contingency one befor alignement to global frame of reference", matrix.getMetaDataType());		
		AGSFullNDimensionalMatrix<Integer> contingencyMatrix = 
				new GSContingencyTable(matrix.getDimensions().stream().collect(Collectors.toMap(d -> d, d -> d.valuesOnData())));

		int castDiff = 0;
		for(ACoordinate<AbstractAttribute, AttributeValue> coord : matrix.getMatrix().keySet()){
			double matrixControl = matrix.getVal(coord).getValue().doubleValue();
			double decimal = matrixControl - Math.floor(matrixControl);
			if(castDiff >= 0 && decimal > 0.5){
				//TODO: test returned boolean
				contingencyMatrix.addValue(coord, new ControlContingency(Math.round(Math.round(matrixControl))));
				castDiff -= 1 - decimal;
			} else {
				contingencyMatrix.addValue(coord, new ControlContingency((int) matrixControl));
				castDiff += decimal;
			}
		}
		castDiff = Math.round(Math.round(castDiff));
		if(castDiff < 1)
			return contingencyMatrix;

		List<ACoordinate<AbstractAttribute, AttributeValue>> controlKeyList = new ArrayList<>(contingencyMatrix.getMatrix().keySet());
		Collections.shuffle(controlKeyList);
		for(ACoordinate<AbstractAttribute, AttributeValue> randControlKey : controlKeyList.subList(0, castDiff))
			contingencyMatrix.getVal(randControlKey).add(new ControlContingency(1));

		return contingencyMatrix;
	}
	
	/**
	 * Align the former {@link AGSControlTable} passed in argument with the last. 
	 * <p>
	 * Be very careful about which {@link AGSControlTable} should be align with which one. The typical use case will be 
	 * to align a frequency table with {@link GSDataFrameOfReference#Individual} frame of reference to a contingency table 
	 * with {@link GSDataFrameOfReference#Complet}, in order to "scale up" and normalize the data control tables. 
	 * 
	 * @param aligneMatrix {@link AGSControlTable}
	 * @param referenceMatrix {@link AGSControlTable}
	 * @return freqTable {@link AGSControlTable}
	 * @throws IllegalAlignDistributions
	 * @throws IllegalDimMatrixRequest
	 */
	private InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> getDistributionsAlign(
			InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> aligneMatrix,
			InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> referenceMatrix) 
					throws IllegalAlignDistributions, IllegalNDimensionalMatrixAccess {
		if(!referenceMatrix.getDimensions().stream().anyMatch(d -> aligneMatrix.getDimensions().contains(d)))
			throw new IllegalAlignDistributions(referenceMatrix.getDimensions(), aligneMatrix.getDimensions());
		for(ACoordinate<AbstractAttribute, AttributeValue> controlKey : aligneMatrix.getMatrix().keySet()){
			aligneMatrix.getVal(controlKey).multiply(referenceMatrix.getVal(controlKey.values()
					.stream().filter(asp -> referenceMatrix.getDimensions()
							.contains(asp.getAttribute())).collect(Collectors.toSet())));
		}
		aligneMatrix.setMetaDataType(referenceMatrix.getMetaDataType());
		return aligneMatrix;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////
	// ------------------------------ MAIN INPUT METHOD ------------------------------- //
	//////////////////////////////////////////////////////////////////////////////////////

	/**
	 * TODO: javadoc
	 * 
	 * @param file
	 * @param attributes
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 * @throws InputFileNotSupportedException
	 * @throws IllegalDistributionCreation
	 * @throws IllegalNDimensionalMatrixAccess 
	 */
	private Set<InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number>> getDistributions(GSDataFile file, 
			Set<AbstractAttribute> attributes) throws InvalidFormatException, IOException, InputFileNotSupportedException, IllegalDistributionCreation, IllegalNDimensionalMatrixAccess {
		if(file.getDataFileType().equals(GSMetaDataType.Sample))
			throw new IllegalDistributionCreation("Sample to distribution is not yet implemented !!", file);
		Set<InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number>> cTableSet = new HashSet<>();
		ISurvey survey = file.getSurvey();

		Map<Integer, Set<AttributeValue>> rowHeaders = getRowHeaders(file, survey, attributes);
		Map<Integer, Set<AttributeValue>> columnHeaders = getColumnHeaders(file, survey, attributes);

		Set<Set<AbstractAttribute>> columnSchemas = columnHeaders.values()
				.stream().map(head -> head
						.stream().map(v -> v.getAttribute()).collect(Collectors.toSet()))
				.collect(Collectors.toSet());
		Set<Set<AbstractAttribute>> rowSchemas = rowHeaders.values()
				.stream().map(line -> line
						.stream().map(v -> v.getAttribute()).collect(Collectors.toSet()))
				.collect(Collectors.toSet());

		for(Set<AbstractAttribute> rSchema : rowSchemas){
			for(Set<AbstractAttribute> cSchema : columnSchemas){
				InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> jDistribution;
				Map<AbstractAttribute, Set<AttributeValue>> dimTable = Stream.concat(rSchema.stream(), cSchema.stream())
						.collect(Collectors.toMap(a -> a, a -> a.valuesOnData()));
				if(file.getDataFileType().equals(GSMetaDataType.ContingenceTable))
					jDistribution = new GSContingencyTable(dimTable);
				else
					jDistribution = new GSJointDistribution(dimTable, file.getDataFileType());
				for(Integer row : rowHeaders.entrySet()
						.stream().filter(e -> e.getValue()
								.stream().allMatch(v -> rSchema.contains(v.getAttribute())))
						.map(e -> e.getKey()).collect(Collectors.toSet())){
					for(Integer col : columnHeaders.entrySet()
							.stream().filter(e -> e.getValue()
									.stream().allMatch(v -> cSchema.contains(v.getAttribute())))
							.map(e -> e.getKey()).collect(Collectors.toSet())){
						String stringVal = survey.read(row, col);
						DataType dt = dataParser.getValueType(stringVal);
						ACoordinate<AbstractAttribute, AttributeValue> coord = new GSCoordinate(
								Stream.concat(rowHeaders.get(row).stream(), columnHeaders.get(col).stream()).collect(Collectors.toSet()));
						if(dt == DataType.INTEGER || dt == DataType.DOUBLE)
							if(!jDistribution.addValue(coord, jDistribution.parseVal(dataParser, stringVal)))
								jDistribution.getVal(coord).add(jDistribution.parseVal(dataParser, stringVal));
					}
				}
				cTableSet.add(jDistribution);
			}
		}
		return cTableSet;
	}

	/**
	 * TODO: javadoc
	 * 
	 * @param distribution
	 * @return
	 * @throws IllegalNDimensionalMatrixAccess
	 * @throws IncompatibleControlTotalException
	 */
	private AControl<? extends Number> checkAndPeakControlTotal(InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> distribution) 
			throws IllegalNDimensionalMatrixAccess, IncompatibleControlTotalException {
		List<AbstractAttribute> attributes = new ArrayList<>(distribution.getDimensions());
		Collections.shuffle(attributes);
		AControl<? extends Number> control = distribution.getVal(attributes.remove(0).valuesOnData());
		for(AbstractAttribute attribut : attributes){
			AControl<? extends Number> controlAtt = distribution.getVal(attribut.valuesOnData());
			if(Math.abs(controlAtt.getValue().doubleValue() - control.getValue().doubleValue()) / controlAtt.getValue().doubleValue() > this.epsilon)
				throw new IncompatibleControlTotalException(control, controlAtt);
		}
		return control;
	}

	// -------------------------------- INNER UTILITY PARSER -------------------------------- //

	private Map<Integer, Set<AttributeValue>> getRowHeaders(GSDataFile file, ISurvey survey, Set<AbstractAttribute> attributes) {
		Map<Integer, Set<AttributeValue>> rowHeaders = new HashMap<>();
		for(int i = file.getFirstRowDataIndex(); i <= survey.getLastRowIndex(); i++){
			List<String> line = survey.readColumns(0, file.getFirstColumnDataIndex(), i);
			for(int j = 0; j < line.size(); j++){
				String lineVal = line.get(j);
				// WARNING: the original RangeValue#toCsvString() do not get the original string from the data
				// instead it concatenate minVal+":"+maxVal [written in stone] -> put the original string in
				// the constructor
				Set<AttributeValue> vals = attributes.stream().flatMap(att -> att.valuesOnData().stream())
						.filter(asp -> asp.toCsvString().equals(lineVal))
						.collect(Collectors.toSet());
				if(vals.isEmpty())
					continue;
				if(vals.size() > 1){
					Set<AttributeValue> vals2 = new HashSet<>(vals);
					vals = survey.readLines(0, file.getFirstRowDataIndex(), j).stream().flatMap(s -> attributes
							.stream().filter(att -> att.getNameOnData().equals(s)))
							.flatMap(att -> vals2
									.stream().filter(v -> v.getAttribute().equals(att)))
							.collect(Collectors.toSet());
				}
				if(rowHeaders.containsKey(i))
					rowHeaders.get(i).addAll(vals);
				else 
					rowHeaders.put(i, new HashSet<>(vals));				
			}
		}
		return rowHeaders;
	}


	private Map<Integer, Set<AttributeValue>> getColumnHeaders(GSDataFile file, ISurvey survey,
			Set<AbstractAttribute> attributes) {
		Map<Integer, Set<AttributeValue>> columnHeaders = new HashMap<>();
		for(int i = file.getFirstColumnDataIndex(); i <= survey.getLastColumnIndex(); i++){
			List<String> column = survey.readLines(0, file.getFirstRowDataIndex(), i);
			for(int j = 0; j < column.size(); j++){
				String columnVal = column.get(j);
				// WARNING: the original RangeValue#toCsvString() do not get the original string from the data
				// instead it concatenate minVal+":"+maxVal [written in stone] -> put the original string in
				// the constructor
				Set<AttributeValue> vals = attributes.stream().flatMap(att -> att.valuesOnData().stream())
						.filter(asp -> asp.toCsvString().equals(columnVal))
						.collect(Collectors.toSet());
				if(vals.isEmpty())
					continue;
				if(vals.size() > 1){
					Set<AttributeValue> vals2 = new HashSet<>(vals);
					vals = column.stream().flatMap(s -> attributes
							.stream().filter(att -> att.getNameOnData().equals(s)))
							.flatMap(att -> vals2
									.stream().filter(v -> v.getAttribute().equals(att)))
							.collect(Collectors.toSet());
				}
				if(columnHeaders.containsKey(i))
					columnHeaders.get(i).addAll(vals);
				else
					columnHeaders.put(i, new HashSet<>(vals));
			}
		}
		return columnHeaders;
	}

}
