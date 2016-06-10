package idees.genstar.distribution.innerstructure;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import idees.genstar.configuration.GSMetaDataType;
import idees.genstar.control.AControl;
import idees.genstar.datareader.GSDataParser;
import idees.genstar.distribution.exception.IllegalNDimensionalMatrixAccess;

/**
 * Main interface that forces n dimensional matrix to specify: <br/>
 * 1) {@code <D>} the type of dimension to be used <br/>
 * 2) {@code <A>} the type of aspect dimensions contain <br/>
 * 3) {@code <T>} the type of value the matrix contains <br/>
 * <br/>
 * There is also several methods to access and set the matrix
 * 
 * @author kevinchapuis
 *
 * @param <D> Type of random variables
 * @param <A> Type of variables' values
 * @param <T> Type of values the matrix is made of
 */
public interface InDimensionalMatrix<D, A, T extends Number> {
	
	// --------------------------- MAIN CONTRACT --------------------------- //
	
	/**
	 * Draw a particular {@link ACoordinate} using the mutli-dimensional distribution. This drawing
	 * should return a complete coordinate: each dimension of the distribution should be represented
	 * 
	 * @return {@link ACoordinate}
	 */
	public ACoordinate<D, A> draw();
		
	// ------------------------- Matrix description ------------------------- // 

	/**
	 * Gives the null value for this matrix
	 * 
	 * @return {@link AControl} that represent null value
	 */
	public AControl<T> getNulVal();
	
	/**
	 * Gives the value for which this {@link AControl} multiply identify value return this. More formally:
	 * 
	 * {@code 
	 * AControl control = new AControl();
	 * control.multiply(this#getIdentityProductVal()) == control;
	 * }
	 * 
	 * @return {@link AControl} that represent identity value
	 */
	public AControl<T> getIdentityProductVal();
	
	/**
	 * Gives the singleton like empty coordinate 
	 * 
	 * @return the empty {@link ACoordinate} 
	 */
	public ACoordinate<D, A> getEmptyCoordinate();
	
	/**
	 * Gives the dimensions (variables) of this matrix
	 * 
	 * @return {@link Set} of {@code D} dimensions
	 */
	public Set<D> getDimensions();

	/**
	 * Gives the dimension (variable) associated with the aspect (variable value) in argument
	 * 
	 * @param aspect
	 * @return {@code D} dimension
	 * @throws IllegalNDimensionalMatrixAccess
	 */
	public D getDimension(A aspect) throws IllegalNDimensionalMatrixAccess;
	
	/**
	 * Gives all aspects (variable values) that characterize matrix coordinates
	 * 
	 * @return {@link Set} of {@code A} aspects
	 */
	public Set<A> getAspects();

	/**
	 * Gives the aspects (variable values) associated with dimension (variable) in argument
	 * 
	 * @param dimension
	 * @return {@link Set} of {@code A} aspects
	 * @throws IllegalNDimensionalMatrixAccess
	 */
	public Set<A> getAspects(D dimension) throws IllegalNDimensionalMatrixAccess;

	/**
	 * Gives the theoretical size of the matrix: 
	 * {@code
	 * 	int theoreticalSize = 1;
	 *  for(D dimension : #getDimensions()){theoreticalSize *= dimension.size()}
	 * }
	 * 
	 * @return the theoretical size of the matrix
	 */
	public int getTheoreticalSpaceSize();

	/**
	 * Gives the actual size of the matrix. Due to optimization, matrix could retain only
	 * significant value
	 * 
	 * @return the actual size of the matrix
	 */
	public int getConcretSize();

	@Override
	public String toString();

	// --------------------------- Matrix access --------------------------- //

	/**
	 * Get the underlying matrix
	 * 
	 * @return {@link Map} with coordinate {@link ACoordinate} and associated {@link AControl}
	 */
	public Map<ACoordinate<D, A>, AControl<T>> getMatrix();
	
	/**
	 * Get the value corresponding to the coordinate
	 * 
	 * @param coordinate
	 * @return T extends {@link Number}
	 * @throws IllegalNDimensionalMatrixAccess 
	 * @throws IllegalDimMatrixRequest 
	 */
	public AControl<T> getVal(ACoordinate<D, A> coordinate) throws IllegalNDimensionalMatrixAccess;

	/**
	 * Get the sum value of all matrix space characterized by the given {@code aspect}. <br/>
	 * Concretely, the function sums all matrix values with coordinates that contains the {@code aspect} given in parameter. 
	 * 
	 * @param aspect
	 * @return T extends {@link Number}
	 * @throws IllegalNDimensionalMatrixAccess 
	 */
	public AControl<T> getVal(A aspect) throws IllegalNDimensionalMatrixAccess;

	/**
	 * Get the sum value of all matrix space characterized by the given combination of {@code aspects}. <br/>
	 * Concretely, the function sums all matrix values with coordinates that contains any valid combination of <br/> 
	 * the {@code aspects} given in parameter 
	 * 
	 * @param aspects
	 * @return T extends {@link Number}
	 * @throws IllegalNDimensionalMatrixAccess
	 */
	public AControl<T> getVal(Collection<A> aspects) throws IllegalNDimensionalMatrixAccess;

	// --------------------------- Matrix settup --------------------------- //

	/**
	 * Safe add to the underlying matrix: this means, if {@code coordinates} have been <br/>
	 * already binding to a given value, the call of {@link #addValue(Map, Number)} will <br/>
	 * result in no change
	 * 
	 * @param coordinates
	 * @param value
	 * @return <code>true</code> if added to the matrix or false otherwise
	 */
	public boolean addValue(ACoordinate<D, A> coordinates, AControl<? extends Number> value);

	/**
	 * Always bind {@code coordinates} to {@code value}: either replace or
	 * add the corresponding value at given coordinates
	 * 
	 * @param coordinates
	 * @param value
	 */
	public boolean setValue(ACoordinate<D, A> coordinates, AControl<? extends Number> value);
	
	/**
	 * remove the value associated to the {@code coordinate} passed in argument. This also leads
	 * to a removal of the coordinate.
	 * 
	 * @param coordinates
	 * @return
	 */
	public boolean removeValue(ACoordinate<D, A> coordinate); 

	// ------------------------- matrix frame of reference ------------------------- //

	/**
	 * It's a way to know the basic tag associated with data. It is either a sample data set,
	 * a contingent data set or a frequency data set; the last could be framed by two different
	 * referential, i.e. individual (frequencies refer to particular aspects of the data) 
	 * or complet (frequencies refer to the overall data set)
	 * 
	 * @return {@link GSMetaDataType} the meta data type
	 */
	public GSMetaDataType getMetaDataType();
	
	/**
	 * Change the {@link GSMetaDataType} of this matrix
	 * 
	 * @see #getMetaDataType()
	 * 
	 * @param metaDataType
	 * @return <code>true</code> if the meta data type has change, false otherwise
	 */
	public boolean setMetaDataType(GSMetaDataType metaDataType);
	
	/**
	 * Gives true if inner data values are related to individual attribute or to complete set of attributes.
	 * 
	 * TODO: be more precise
	 * 
	 * @return
	 */
	public boolean isIndividualFrameOfReference();

	// ----------------------------------------------- Utilities

	/**
	 * Coordinate should satisfy the criteria: "at most one aspect per dimension"
	 * 
	 * @param coordinate
	 * @return <code>true</code> if {@link ACoordinate} is compliant, <code>false</code> otherwise
	 */
	public boolean isCoordinateCompliant(ACoordinate<D, A> coordinate);
	
	/**
	 * Csv convenient output of the matrix
	 * 
	 * @param csvSeparator
	 * @return {@link String} formated as a csv with {@code csvSeparator} separator
	 */
	public String toCsv(char csvSeparator);
	
	/**
	 * The method will return a version of the string that correspond to concrete {@link Number} type
	 * that fill the matrix
	 * 
	 * @param parser
	 * @param val
	 * @return
	 */
	public AControl<T> parseVal(GSDataParser parser, String val);

}
