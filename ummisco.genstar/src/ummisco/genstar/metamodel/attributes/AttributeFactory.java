package ummisco.genstar.metamodel.attributes;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import idees.genstar.configuration.GSAttDataType;
import idees.genstar.datareader.GSDataParser;
import idees.genstar.datareader.exception.GenstarIllegalRangedData;
import ummisco.genstar.exception.GenstarException;

/**
 * This class provide mean to initialize {@link AbstractAttribute}
 * 
 * TODO: implement methods to initialize empty {@link AttributeValue} -> value with no attribute
 * 
 * @author kevinchapuis
 *
 */
public class AttributeFactory {

	private final GSDataParser parser;

	public AttributeFactory(){
		this.parser = new GSDataParser();
	}

	/**
	 * Main method to instantiate {@link AbstractAttribute}. Concrete type depend on {@link GSAttDataType} passed in argument.
	 * <p>
	 * If {@code valueType.equals({@link GSAttDataType#range})} then return a {@link RangeValuesAttribute} <br/>
	 * Else if {@code valueType.equals({@link GSAttDataType#unique})} then return a {@link UniqueValuesAttribute} <br/>
	 *   
	 * 
	 * @param nameOnData
	 * @param nameOnEntity
	 * @param dataType
	 * @param values
	 * @param valueType
	 * @return an {@link AbstractAttribute}
	 * @throws GenstarException
	 * @throws GenstarIllegalRangedData
	 */
	public AbstractAttribute createAttribute(String nameOnData, String nameOnEntity, DataType dataType, List<String> values, 
			GSAttDataType valueType) throws GenstarException, GenstarIllegalRangedData{
		return createAttribute(nameOnData, nameOnEntity, dataType, values, valueType, null, Collections.emptyMap());
	}

	/**
	 * Method that permit to instantiate specific case of {@link AbstractAttribute}: {@link RecorderAttribute} and {@link AggregatedAttribute}
	 * <p>
	 * TODO: explain how
	 * 
	 * @param nameOnData
	 * @param nameOnEntity
	 * @param dataType
	 * @param values
	 * @param valueType
	 * @param referentAttribute
	 * @return
	 * @throws GenstarException
	 * @throws GenstarIllegalRangedData
	 */
	public AbstractAttribute createAttribute(String nameOnData, String nameOnEntity, DataType dataType, List<String> values, 
			GSAttDataType valueType, AbstractAttribute referentAttribute, Map<String, Set<String>> mapper) 
					throws GenstarException, GenstarIllegalRangedData{
		AbstractAttribute att = null;
		switch (valueType) {
		case unique:
			if(referentAttribute == null)
				att = new UniqueValuesAttribute(nameOnData, nameOnEntity, dataType);
			else if (values.size() == 1)
				att = new RecorderAttribute(nameOnData, nameOnEntity, dataType, referentAttribute);
			else if (!mapper.isEmpty())
				att = new AggregatedAttribute(nameOnData, nameOnEntity, dataType, referentAttribute, mapper);
			else
				throw new GenstarException("cannot instantiate aggregated value without mapper");
			break;
		case range:
			if(mapper.isEmpty())
				att = new RangeValuesAttribute(nameOnData, nameOnEntity, dataType);
			else if(referentAttribute != null)
				att = new AggregatedAttribute(nameOnData, nameOnEntity, dataType, referentAttribute, mapper);
			else
				throw new GenstarException("cannot instantiate aggregated value with "+referentAttribute+" referent attribute");
			break;
		default:
			throw new GenstarException("The attribute meta data type "+valueType+" is not applicable !");
		}
		att.addAll(this.getValues(valueType, dataType, values, att));
		att.setEmptyValue(this.getEmptyValue(valueType, dataType, att));
		return att;
	}
	
	/**
	 * create a value with {@code valueType}, concrete value type {@code dataType} and the given {@code attribute}.
	 * <p>
	 * {@code values} can represent a unlimited number of string value, only the first one for unique type and the two first
	 * ones for range will be used for {@link AttributeValue} creation. if {@code values} is empty, then returned {@link AttributeValue}
	 * will be empty
	 * 
	 * @param {@link GSAttDataType} valueType
	 * @param {@link DataType} dataType
	 * @param {@code List<String>} values
	 * @param {@link AbstractAttribute} attribute
	 * @return a value with {@link AttributeValue} type
	 * @throws GenstarException
	 * @throws GenstarIllegalRangedData
	 */
	public AttributeValue createValue(GSAttDataType valueType, DataType dataType, List<String> values, AbstractAttribute attribute) 
			throws GenstarException, GenstarIllegalRangedData{
		if(values.isEmpty())
			return getEmptyValue(valueType, dataType, attribute);
		return getValues(valueType, dataType, values, attribute).iterator().next();
	}
	
	// ----------------------------- Back office ----------------------------- //
	
	private Set<AttributeValue> getValues(GSAttDataType valueType, DataType dataType, List<String> values, AbstractAttribute attribute) 
			throws GenstarException, GenstarIllegalRangedData{
		Set<AttributeValue> vals = new HashSet<>();
		switch (valueType) {
		case unique:
			for(String value : values)
				if(dataType.isNumericValue())
					vals.add(new UniqueValue(dataType, parser.getNumber(value.trim()).get(0), attribute));
				else
					vals.add(new UniqueValue(dataType, value.trim(), attribute));
			return vals;
		case range:
			for(String val : values){
				if(dataType.equals(DataType.INTEGER)){
					List<Integer> intVal = parser.getRangedIntegerData(val, false, 0, 150);
					vals.add(new RangeValue(dataType, intVal.get(0).toString(), intVal.get(1).toString(), val, attribute));
				} else if(dataType.equals(DataType.DOUBLE) || dataType.equals(DataType.FLOAT)){
					List<Double> doublVal = parser.getRangedDoubleData(val, false, 0d, 150d);
					vals.add(new RangeValue(dataType, doublVal.get(0).toString(), doublVal.get(1).toString(), val, attribute));
				}
			}
			return vals;
		default:
			throw new GenstarException("cannot get values out of type "+GSAttDataType.unique+" or "+GSAttDataType.range+" but was "+valueType);
		}
	}
	
	private AttributeValue getEmptyValue(GSAttDataType valueType, DataType dataType, AbstractAttribute attribute) 
			throws GenstarException{
		switch (valueType) {
		case unique:
			return new UniqueValue(dataType, attribute);
		case range:
			return new RangeValue(dataType, attribute);
		default:
			return new UniqueValue(dataType, attribute);
		}
	}

}
