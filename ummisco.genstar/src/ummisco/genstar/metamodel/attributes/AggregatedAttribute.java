package ummisco.genstar.metamodel.attributes;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import ummisco.genstar.exception.GenstarException;

public class AggregatedAttribute extends AbstractAttribute {
	
	private final Map<String, Set<String>> mappedValuesOnData;
	private Set<AttributeValue> valuesOnData;

	protected AggregatedAttribute(String attributeNameOnData, String attributeNameOnEntity, DataType dataType,
			AbstractAttribute referentAttribute, Map<String, Set<String>> mappedValuesOnData)
			throws GenstarException {
		super(attributeNameOnData, attributeNameOnEntity, dataType, referentAttribute.getValueClassOnEntity(), referentAttribute);
		this.mappedValuesOnData = mappedValuesOnData;
		this.valuesOnData = new HashSet<>();
		this.valueClassOnData = referentAttribute.getValueClassOnEntity();
	}

	@Override
	public AttributeValue findCorrespondingAttributeValueOnData(List<String> stringValue) throws GenstarException {
		Set<String> valueSet = getReferentAttribute().valuesOnData()
				.stream().map(av -> av.toCsvString()).filter(s -> stringValue.contains(s))
				.collect(Collectors.toSet());
		Optional<String> value = mappedValuesOnData.entrySet()
				.stream().filter(e -> e.getValue().containsAll(valueSet))
				.map(e -> e.getKey()).findFirst();
		if(value.isPresent())
			return valuesOnData().stream().filter(av -> av.toCsvString().equals(value.get())).findFirst().get();
		return null;
	}

	@Override
	public AttributeValue findMatchingAttributeValueOnData(AttributeValue attributeValue) throws GenstarException {
		if(mappedValuesOnData.values().stream().flatMap(set -> set.stream()).anyMatch(s -> s.equals(attributeValue.toCsvString())))
			return mappedValuesOnData.entrySet().stream().filter(e -> e.getValue()
					.stream().anyMatch(s -> s.equals(attributeValue.toCsvString())))
					.map(e -> valuesOnData().stream().filter(av -> av.toCsvString().equals(e.getKey())).findFirst().get())
					.findFirst().get();
		if(mappedValuesOnData.keySet().stream().anyMatch(s -> s.equals(attributeValue.toCsvString())))
			return mappedValuesOnData.keySet().stream().filter(s -> s.equals(attributeValue.toCsvString()))
					.map(s -> valuesOnData().stream().filter(av -> av.toCsvString().equals(s)).findFirst().get())
					.findFirst().get();
		return null;
	}

	@Override
	public Set<AttributeValue> valuesOnData() {
		return Collections.unmodifiableSet(valuesOnData);
	}

	@Override
	public boolean add(AttributeValue value) throws GenstarException {
		if (value == null) { throw new GenstarException("'value' parameter can not be null"); }
		if (!(value.getClass().equals(valueClassOnData))) { throw new GenstarException("value must be an instance of " + valueClassOnData.getName()); }
		if (!this.dataType.equals(value.dataType)) { throw new GenstarException("Incompatible valueType"); }
		if(mappedValuesOnData.containsKey(value.toCsvString()) && value.getAttribute().equals(this))
			return valuesOnData.add(value);
		return false;
	}

	@Override
	public boolean addAll(Set<AttributeValue> values) throws GenstarException {
		if (values == null) { throw new GenstarException("'values' parameter can not be null"); }
		boolean res = true;
		for(AttributeValue val : values)
			if(!add(val))
				res = false;
		return res;
	}

	@Override
	public boolean remove(AttributeValue value) {
		if(valuesOnData.contains(value) && mappedValuesOnData.containsKey(value.toCsvString())){
			valuesOnData.remove(value);
			mappedValuesOnData.remove(value);
			return true;
		}
		return false;
	}

	@Override
	public AttributeValue getInstanceOfAttributeValue(AttributeValue value) {
		if(valuesOnData.contains(value))
			return value;
		if(getReferentAttribute().valuesOnData().contains(value)){
			Set<String> attString = mappedValuesOnData.entrySet()
					.stream().filter(e -> e.getValue().contains(value.toCsvString()))
					.map(e -> e.getKey()).collect(Collectors.toSet());
			return valuesOnData.stream().filter(av -> attString.contains(av.toCsvString())).findFirst().get();
		}
		return null;
	}

	@Override
	public void clear() {
		valuesOnData.clear();
		mappedValuesOnData.clear();
	}

}
