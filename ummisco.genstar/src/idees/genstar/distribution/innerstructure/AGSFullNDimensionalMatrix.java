package idees.genstar.distribution.innerstructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import idees.genstar.configuration.GSMetaDataType;
import idees.genstar.control.AControl;
import idees.genstar.distribution.GSCoordinate;
import idees.genstar.distribution.exception.IllegalNDimensionalMatrixAccess;
import idees.genstar.distribution.exception.MatrixCoordinateException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

/**
 * TODO: javadoc
 * 
 * @author kevinchapuis
 *
 * @param <T>
 */
public abstract class AGSFullNDimensionalMatrix<T extends Number> implements InDimensionalMatrix<AbstractAttribute, AttributeValue, T> {

	private GSMetaDataType dataType; 

	private final Map<AbstractAttribute, Set<AttributeValue>> dimensions;
	protected final Map<ACoordinate<AbstractAttribute, AttributeValue>, AControl<T>> matrix;

	private ACoordinate<AbstractAttribute, AttributeValue> emptyCoordinate = null;

	// ----------------------- CONSTRUCTORS ----------------------- //

	public AGSFullNDimensionalMatrix(Map<AbstractAttribute, Set<AttributeValue>> dimensionAspectMap, GSMetaDataType metaDataType) 
			throws MatrixCoordinateException {
		this.dimensions = new HashMap<>(dimensionAspectMap);
		this.matrix = new HashMap<>(getTheoreticalSpaceSize() / 4);
		this.dataType = metaDataType;
		this.emptyCoordinate = new GSCoordinate(Collections.<AttributeValue>emptySet());
	}
		
	// ------------------------- META DATA ------------------------ //

	@Override
	public boolean isIndividualFrameOfReference(){
		return this.getMetaDataType().equals(GSMetaDataType.IndivFrequenceTable);
	}

	@Override
	public GSMetaDataType getMetaDataType() {
		return dataType;
	}

	@Override
	public boolean setMetaDataType(GSMetaDataType metaDataType) {
		if(dataType == null || !dataType.equals(metaDataType))
			dataType = metaDataType;
		else 
			return false;
		return true;
	}

	// ---------------------- GLOBAL ACCESSORS ---------------------- //

	@Override
	public int getTheoreticalSpaceSize(){
		return dimensions.entrySet().stream()
				.mapToInt(d -> d.getValue().size())
				.reduce(1, (ir, dimSize) -> ir * dimSize);
	}

	@Override
	public int getConcretSize(){
		return matrix.size();
	}

	@Override
	public Set<AbstractAttribute> getDimensions(){
		return Collections.unmodifiableSet(dimensions.keySet());
	}

	@Override
	public AbstractAttribute getDimension(AttributeValue aspect) throws IllegalNDimensionalMatrixAccess{
		if(!dimensions.values().contains(aspect))
			throw new IllegalNDimensionalMatrixAccess("aspect "+aspect+ " does not fit any known dimension");
		return dimensions.entrySet()
				.stream().filter(e -> e.getValue().contains(aspect))
				.findFirst().get().getKey();
	}

	@Override
	public Set<AttributeValue> getAspects(){
		return Collections.unmodifiableSet(dimensions.values().stream().flatMap(Set::stream).collect(Collectors.toSet()));
	}

	@Override
	public Set<AttributeValue> getAspects(AbstractAttribute dimension) throws IllegalNDimensionalMatrixAccess{
		if(!dimensions.containsKey(dimension))
			throw new IllegalNDimensionalMatrixAccess("dimension "+dimension+" is not present in the joint distribution");
		return Collections.unmodifiableSet(dimensions.get(dimension));
	}

	@Override
	public Map<ACoordinate<AbstractAttribute, AttributeValue>, AControl<T>> getMatrix(){
		return matrix;
	}

	///////////////////////////////////////////////////////////////////
	// -------------------------- GETTERS -------------------------- //
	///////////////////////////////////////////////////////////////////

	@Override
	public AControl<T> getVal(ACoordinate<AbstractAttribute, AttributeValue> coordinate) throws IllegalNDimensionalMatrixAccess{
		if(!matrix.containsKey(coordinate))
			throw new IllegalNDimensionalMatrixAccess("Coordinate "+coordinate+" is absent from this control table ("+this.hashCode()+")");
		return this.matrix.get(coordinate);
	}

	@Override
	public AControl<T> getVal(AttributeValue aspect) throws IllegalNDimensionalMatrixAccess{
		if(!matrix.keySet().stream().anyMatch(coord -> coord.contains(aspect)))
			throw new IllegalNDimensionalMatrixAccess("Aspect "+aspect+" is absent from this control table ("+this.hashCode()+")");
		AControl<T> result = getNulVal();
		for(AControl<T> control : this.matrix.entrySet().parallelStream()
				.filter(e -> e.getKey().values().contains(aspect))
				.map(Entry::getValue).collect(Collectors.toSet()))
			getSummedControl(result, control);
		return result;
	}

	@Override
	public AControl<T> getVal(Collection<AttributeValue> aspects) throws IllegalNDimensionalMatrixAccess{
		if(aspects.stream().allMatch(a -> !matrix.keySet().stream().anyMatch(coord -> coord.contains(a))))
			throw new IllegalNDimensionalMatrixAccess("Aspect "+aspects+" is absent from this control table ("+this.hashCode()+")");

		Map<AbstractAttribute, Set<AttributeValue>> attAsp = new HashMap<>();
		for(AttributeValue val : aspects){
			AbstractAttribute att = val.getAttribute();
			if(attAsp.containsKey(att))
				attAsp.get(att).add(val);
			else {
				Set<AttributeValue> valSet = new HashSet<>();
				valSet.add(val);
				attAsp.put(att, valSet);
			}
		}

		AControl<T> result = getNulVal();
		for(AControl<T> control : this.matrix.entrySet().parallelStream()
				.filter(e -> attAsp.entrySet()
						.stream().allMatch(aa -> aa.getValue()
								.stream().anyMatch(a -> e.getKey().contains(a))))
				.map(Entry::getValue).collect(Collectors.toSet()))
			getSummedControl(result, control);
		return result;
	}

	///////////////////////////////////////////////////////////////////////////
	// ----------------------- COORDINATE MANAGEMENT ----------------------- //
	///////////////////////////////////////////////////////////////////////////

	@Override
	public boolean isCoordinateCompliant(ACoordinate<AbstractAttribute, AttributeValue> coordinate) {
		List<AbstractAttribute> dimensionsAspects = new ArrayList<>();
		for(AttributeValue aspect : coordinate.values())
			dimensionsAspects.addAll(dimensions.keySet()
					.stream().filter(d -> dimensions.get(d).contains(aspect))
					.collect(Collectors.toList()));
		Set<AbstractAttribute> dimSet = new HashSet<>(dimensionsAspects);
		if(dimensionsAspects.size() == dimSet.size())
			return true;
		return false;
	}

	@Override
	public ACoordinate<AbstractAttribute, AttributeValue> getEmptyCoordinate(){
		return emptyCoordinate;
	}

	///////////////////////////////////////////////////////////////////////////
	//   ----------------------- VAlUES MANAGEMENT -----------------------   //
	///////////////////////////////////////////////////////////////////////////

	@Override
	public boolean removeValue(ACoordinate<AbstractAttribute, AttributeValue> coordinate){
		return matrix.remove(coordinate, matrix.get(coordinate));
	}

	private AControl<T> getSummedControl(AControl<T> controlOne, AControl<T> controlTwo){
		return controlOne.add(controlTwo);
	}

	// -------------------------- UTILITY -------------------------- //

	@Override
	public String toString(){
		String s = "-- Matrix: "+dimensions.size()+" dimensions and "+dimensions.values().stream().mapToInt(Collection::size).sum()+" aspects (theoretical size:"+this.getTheoreticalSpaceSize()+")--\n";
		AControl<T> empty = getNulVal();
		for(AbstractAttribute dimension : dimensions.keySet()){
			s += " -- dimension: "+dimension.getNameOnData()+" with "+dimensions.get(dimension).size()+" aspects -- \n";
			for(AttributeValue aspect : dimensions.get(dimension))
				try {
					s += "| "+aspect+": "+getVal(aspect)+"\n";
				} catch (IllegalNDimensionalMatrixAccess e) {
					e.printStackTrace();
					s += "| "+aspect+": "+empty+"\n";
				}
		}
		s += " ----------------------------------- ";
		return s;
	}

	@Override
	public String toCsv(char csvSeparator) {
		List<AbstractAttribute> atts = new ArrayList<>(getDimensions());
		AControl<T> emptyVal = getNulVal();
		String csv = "";
		for(AbstractAttribute att :atts){
			if(!csv.isEmpty())
				csv += csvSeparator;
			csv+=att.getNameOnData();
		}
		csv += csvSeparator+"value\n";
		for(ACoordinate<AbstractAttribute, AttributeValue> coordVal : matrix.keySet()){
			String csvLine = "";
			for(AbstractAttribute att :atts){
				if(!csvLine.isEmpty())
					csvLine += csvSeparator;
				if(!coordVal.values()
						.stream().anyMatch(asp -> asp.getAttribute().equals(att)))
					csvLine += " ";
				else
					csvLine += coordVal.values()
					.stream().filter(asp -> asp.getAttribute().equals(att))
					.findFirst().get().toCsvString();
			}
			try {
				csv += csvLine+csvSeparator+getVal(coordVal).getValue()+"\n";
			} catch (IllegalNDimensionalMatrixAccess e) {
				e.printStackTrace();
				csv += csvLine+csvSeparator+emptyVal+"\n";
			}
		}
		return csv;
	}

}
