package ummisco.genstar.metamodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.AttributeException;

public class InferredRangeAttribute extends InferredAttribute {
	
	
	public InferredRangeAttribute(final ISyntheticPopulationGenerator populationGenerator, final EnumerationValueAttribute inferringAttribute, 
			final String dataAttributeName, final ValueType valueType) throws AttributeException {
		this(populationGenerator, inferringAttribute, dataAttributeName, dataAttributeName, valueType, RangeValue.class);
	}

	public InferredRangeAttribute(final ISyntheticPopulationGenerator populationGenerator, final EnumerationValueAttribute inferringAttribute, 
			final String dataAttributeName, final ValueType valueType, final Class<? extends AttributeValue> entityAttributeValueClass) throws AttributeException {
		this(populationGenerator, inferringAttribute, dataAttributeName, dataAttributeName, valueType, entityAttributeValueClass);
	}
	
	public InferredRangeAttribute(final ISyntheticPopulationGenerator populationGenerator,
			final EnumerationValueAttribute inferringAttribute, final String dataAttributeName, 
			final String entityAttributeName, final ValueType valueType) throws AttributeException {
		super(populationGenerator, inferringAttribute, dataAttributeName, entityAttributeName, valueType, RangeValue.class);
	}

	public InferredRangeAttribute(final ISyntheticPopulationGenerator populationGenerator,
			final EnumerationValueAttribute inferringAttribute, final String dataAttributeName, 
			final String entityAttributeName, final ValueType valueType, final Class<? extends AttributeValue> entityAttributeValueClass) throws AttributeException {
		super(populationGenerator, inferringAttribute, dataAttributeName, entityAttributeName, valueType, entityAttributeValueClass);
	}

	@Override
	protected void initializeInferenceData() throws AttributeException {
		inferenceData = new HashMap<AttributeValue, AttributeValue>();
		for (AttributeValue inferringAttributeValue : inferringAttribute.values()) {
			inferenceData.put(inferringAttributeValue, new RangeValue(valueType));
		}
	}

	@Override
	public RangeValue valueFromString(final List<String> stringValue) throws AttributeException {
		if (stringValue == null || stringValue.size() < 2) { throw new AttributeException("'stringValue' parameter must contain at least 2 elements."); }
		
		return new RangeValue(valueType, stringValue.get(0), stringValue.get(1));
	}

	@Override
	protected void validateInferenceData2(final Map<AttributeValue, AttributeValue> inferenceData) throws AttributeException {
		for (AttributeValue av : inferenceData.values()) { 
			if (!(av instanceof RangeValue)) {
				throw new AttributeException("values of inferenceData must be instances of " + RangeValue.class.getSimpleName() + " class");
			}
		}
	}

}
