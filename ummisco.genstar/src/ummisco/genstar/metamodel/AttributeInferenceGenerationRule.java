package ummisco.genstar.metamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;

public class AttributeInferenceGenerationRule extends GenerationRule implements AttributeChangedListener {
	
	public static final int ATTRIBUTE_INFERENCE_GENERATION_RULE_ID = 1;
	public static final String RULE_TYPE_NAME = "Attribute Inference";
	
	private AbstractAttribute inferringAttribute;
	private AbstractAttribute inferredAttribute;
	
	private Map<AttributeValue, AttributeValue> inferenceData;

	
	public AttributeInferenceGenerationRule(final ISyntheticPopulationGenerator populationGenerator, final String name, 
			final AbstractAttribute inferringAttribute, final AbstractAttribute inferredAttribute) throws GenstarException {
		super(populationGenerator, name);

		this.setInferenceAttributes(inferringAttribute, inferredAttribute);
	}
	
	private void setInferenceAttributes(final AbstractAttribute inferringAttribute, final AbstractAttribute inferredAttribute) throws GenstarException {
		if (inferringAttribute == null || inferredAttribute == null) { throw new GenstarException("Neither 'inferringAttribute' nor 'inferredAttribute' can be null"); }
		if (inferringAttribute.equals(inferredAttribute)) { throw new GenstarException("'inferringAttribute' and 'inferredAttribute' can not be identical"); }
		if (inferringAttribute.values().size() != inferredAttribute.values().size()) { throw new GenstarException("'inferringAttribute' and 'inferredAttribute' must contains the same number of attribute values"); }
		
		// FIXME more validation!
		
		this.inferringAttribute = inferringAttribute;
		this.inferredAttribute = inferredAttribute;
		
		setDefaultAttributeValuesCorrespondence();
	}
	
	private void setDefaultAttributeValuesCorrespondence() {
		List<AttributeValue> inferringAttributeValues = new ArrayList<AttributeValue>(inferringAttribute.values());
		List<AttributeValue> inferredAttributeValues = new ArrayList<AttributeValue>(inferredAttribute.values());
		
		inferenceData = new HashMap<AttributeValue, AttributeValue>();
		for (int i=0; i<inferringAttributeValues.size(); i++) { inferenceData.put(inferringAttributeValues.get(i), inferredAttributeValues.get(i)); }
	}
	
	public void setInferenceData(final Map<AttributeValue, AttributeValue> inferenceData) throws GenstarException { // FIXME find a better way to set the inference data!
		if (inferenceData == null) { throw new GenstarException("'inferenceData' parameter can not be null"); }
		
		if (inferenceData.size() != inferringAttribute.values().size()) { throw new GenstarException("'inferenceData' contains different number of members than inferringAttribute.values"); }
		
		Set<AttributeValue> inferringAttributeValues = inferringAttribute.values();
		for (AttributeValue inferenceDataKey : new ArrayList<AttributeValue>(inferenceData.keySet())) {
			if (!inferringAttributeValues.contains(inferenceDataKey)) {
				throw new GenstarException("Some keys of inferenceData are not values of inferringAttribute");
			}
		}
		 
		Set<AttributeValue> inferredAttributeValues = inferredAttribute.values();
		for (AttributeValue inferenceDataValue : new ArrayList<AttributeValue>(inferenceData.values())) {
			if (!inferredAttributeValues.contains(inferenceDataValue)) {
				throw new GenstarException("Some values of inferenceData are not values of inferredAttribute");
			}
		}
		
		this.inferenceData = new HashMap<AttributeValue, AttributeValue>(inferenceData);
	}
	
	public Map<AttributeValue, AttributeValue> getInferenceData() {
		return new HashMap<AttributeValue, AttributeValue>(inferenceData);
	}

	@Override
	public void generate(final Entity entity) throws GenstarException {
		
		if (entity == null) { throw new GenstarException("'entity' parameter can not be null"); }
		
		String inferringAttributeName = inferringAttribute.getNameOnEntity();
		EntityAttributeValue inferringAttrValueOnEntity = entity.getEntityAttributeValue(inferringAttributeName);
		
		if (inferringAttrValueOnEntity != null) {
			for (AttributeValue inferringAttrValueOnData : inferenceData.keySet()) {
				if (inferringAttrValueOnEntity.isValueMatch(inferringAttrValueOnData)) {
					entity.putAttributeValue(new EntityAttributeValue(inferredAttribute, inferenceData.get(inferringAttrValueOnData)));
					
					break;
				}
				// TODO use default value if nothing matches?
			}
		} else {
			// ? default value!!
		}
		 
	}
	
	public AbstractAttribute getInferringAttribute() {
		return inferringAttribute;
	}
	
	public AbstractAttribute getInferredAttribute() {
		return inferredAttribute;
	}

	@Override
	public void attributeChanged(final AttributeChangedEvent event) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public int getRuleTypeID() {
		return ATTRIBUTE_INFERENCE_GENERATION_RULE_ID;
	}

	@Override
	public List<AbstractAttribute> getAttributes() {
		List<AbstractAttribute> retVal = new ArrayList<AbstractAttribute>();
		retVal.add(inferringAttribute);
		retVal.add(inferredAttribute);
		
		return retVal;
	}

	@Override
	public AbstractAttribute findAttributeByNameOnData(final String attributeNameOnData) {
		if (inferringAttribute.getNameOnData().equals(attributeNameOnData)) { return inferringAttribute; }
		if (inferredAttribute.getNameOnData().equals(attributeNameOnData)) { return inferredAttribute; }
		
		return null;
	}

	@Override
	public String getRuleTypeName() {
		return RULE_TYPE_NAME;
	}
	
}
