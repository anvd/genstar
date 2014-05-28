package ummisco.genstar.metamodel;

import java.util.HashMap;
import java.util.Map;

import ummisco.genstar.exception.AttributeException;

// TODO experimentation : write one common InferredAttribute class
public abstract class InferredAttribute extends AbstractAttribute {
	
	// TODO should accept a set of "inferringAttributes"? necessary?
	protected EnumerationValueAttribute inferringAttribute;
	
	protected Map<AttributeValue, AttributeValue> inferenceData; // <inferring attribute value, inferred attribute value>

	
	public InferredAttribute(final ISyntheticPopulationGenerator populationGenerator, final EnumerationValueAttribute inferringAttribute, 
			final String attributeNameOnData, final ValueType valueType, final Class<? extends AttributeValue> entityAttributeValueClass) throws AttributeException {
		this(populationGenerator, inferringAttribute, attributeNameOnData, attributeNameOnData, valueType, entityAttributeValueClass);
	}
	
	public InferredAttribute(final ISyntheticPopulationGenerator populationGenerator, final EnumerationValueAttribute inferringAttribute, 
			final String attributeNameOnData, final String attributeNameOnEntity, final ValueType valueType, final Class<? extends AttributeValue> entityAttributeValueClass) throws AttributeException {
		super(populationGenerator, attributeNameOnData, attributeNameOnEntity, valueType, entityAttributeValueClass);
		
		if (inferringAttribute == null) { throw new AttributeException("'inferringAttribute' parameter can not be null"); }
		if (!inferringAttribute.populationGenerator.equals(populationGenerator)) {
			throw new AttributeException("Incoherence of populations between inferred attribute's population (" + populationGenerator.getName() + ") and inferring attribute's population ("
					+ inferringAttribute.getPopulationGenerator().getName() + ")");
		}
		
		if (!populationGenerator.containAttribute(inferringAttribute)) { throw new AttributeException("inferringAttribute has not been added to the population yet!"); }
		
		this.inferringAttribute = inferringAttribute;
		initializeInferenceData();
	}
	
	public EnumerationValueAttribute getInferringAttribute() {
		return inferringAttribute;
	}
	
	protected void validateInferenceData1(final Map<AttributeValue, AttributeValue> inferenceData) throws AttributeException {
		if (inferenceData == null) { throw new AttributeException("'inferenceData' parameter can not be null"); }

		for (AttributeValue inferringValue : inferenceData.keySet()) {
			if (!inferringAttribute.contains(inferringValue)) {
				throw new AttributeException("keys of 'inferenceData' map must be values of inferringAttribute");
			}
		}
		
		
		// TODO further validation : inferenceData.size should be equal to inferredAttribute.inferingAttribute.<number of values/ranges>!
	}
	
	protected abstract void validateInferenceData2(final Map<AttributeValue, AttributeValue> inferenceData) throws AttributeException;

	protected abstract void initializeInferenceData() throws AttributeException;

	public void setInferenceData(final Map<AttributeValue, AttributeValue> inferenceData) throws AttributeException {
		validateInferenceData1(inferenceData);
		validateInferenceData2(inferenceData);
		
		this.inferenceData.clear();
		this.inferenceData.putAll(inferenceData);
	}
	
	public Map<AttributeValue, AttributeValue> getInferenceData() {
		Map<AttributeValue, AttributeValue> retVal = new HashMap<AttributeValue, AttributeValue>();
		retVal.putAll(inferenceData);
		
		return retVal;
	}

	public void setInferredAttributeValue(final AttributeValue inferringAttributeValue, final AttributeValue inferredAttributeValue) throws AttributeException {
		if (inferringAttributeValue == null || inferredAttributeValue == null) {
			throw new AttributeException("Neither 'inferringAttributeValue' parameter nor 'inferredAttributeValue' parameter can be null");
		}
		
		if (!inferringAttribute.contains(inferringAttributeValue)) {
			throw new AttributeException("'inferringAttributeValue' is not a value of inferringAttribute");
		}
		
		inferenceData.put(inferringAttributeValue, inferredAttributeValue);
	}
	
	public AttributeValue getInferredAttributeValue(final AttributeValue inferringAttributeValue) throws AttributeException {
		if (inferringAttributeValue == null) { throw new AttributeException("'inferringAttributeValue' can not be null"); }
		
		return inferenceData.get(inferringAttributeValue);
	}

}
