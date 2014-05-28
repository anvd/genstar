package ummisco.genstar.metamodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.AttributeException;

public class InferredValueAttribute extends InferredAttribute { // TODO implements AttributeChangedListener

	
	public InferredValueAttribute(final SyntheticPopulationGenerator populationGenerator, final EnumerationValueAttribute inferringAttribute, 
			final String dataAttributeName, final ValueType valueType) throws AttributeException {
		this(populationGenerator, inferringAttribute, dataAttributeName, dataAttributeName, valueType, UniqueValue.class);
	}

	public InferredValueAttribute(final SyntheticPopulationGenerator populationGenerator, final EnumerationValueAttribute inferringAttribute, 
			final String dataAttributeName, final ValueType valueType, final Class<? extends AttributeValue> entityAttributeValueClass) throws AttributeException {
		this(populationGenerator, inferringAttribute, dataAttributeName, dataAttributeName, valueType, entityAttributeValueClass);
	}
	
	public InferredValueAttribute(final SyntheticPopulationGenerator populationGenerator,
			final EnumerationValueAttribute inferringAttribute, final String dataAttributeName, 
			final String entityAttributeName, final ValueType valueType) throws AttributeException {
		super(populationGenerator, inferringAttribute, dataAttributeName, entityAttributeName, valueType, UniqueValue.class);
	}

	public InferredValueAttribute(final SyntheticPopulationGenerator populationGenerator,
			final EnumerationValueAttribute inferringAttribute, final String dataAttributeName, 
			final String entityAttributeName, final ValueType valueType, final Class<? extends AttributeValue> entityAttributeValueClass) throws AttributeException {
		super(populationGenerator, inferringAttribute, dataAttributeName, entityAttributeName, valueType, entityAttributeValueClass);
	}

	@Override
	protected void initializeInferenceData() throws AttributeException {
		inferenceData = new HashMap<AttributeValue, AttributeValue>();
		for (AttributeValue inferringAttributeValue : inferringAttribute.values()) {
			inferenceData.put(inferringAttributeValue, new UniqueValue(valueType));
		}
	}

	@Override
	public UniqueValue valueFromString(final List<String> stringValue) throws AttributeException {
		if (stringValue == null || stringValue.size() < 1) { throw new AttributeException("'stringValue' parameter must contain at least 1 element."); }
		
		return new UniqueValue(valueType, stringValue.get(0));
	}

	@Override
	protected void validateInferenceData2(final Map<AttributeValue, AttributeValue> inferenceData) throws AttributeException {
		for (AttributeValue inferredAttributeValue : inferenceData.values()) { 
			if (!(inferredAttributeValue instanceof UniqueValue)) {
				throw new AttributeException("values of inferenceData must be instances of " + UniqueValue.class.getSimpleName() + " class");
			}
		}
	}
}
