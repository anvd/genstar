package ummisco.genstar.metamodel;

import java.util.ArrayList;
import java.util.List;

import ummisco.genstar.exception.AttributeException;


public abstract class AbstractAttribute {
	
	protected ISyntheticPopulationGenerator populationGenerator;
	
	protected String nameOnData;
	
	protected String nameOnEntity;
	
	protected ValueType valueType;
	
	protected List<AttributeChangedListener> attributeChangeListeners;
	
	protected Class<? extends AttributeValue> valueClassOnData;
	
	protected Class<? extends AttributeValue> valueClassOnEntity;
	
	private AttributeValue defaultValue;
	
	private AttributeValue castDefaultValue;
	
	
	public AbstractAttribute(final ISyntheticPopulationGenerator populationGenerator, final String attributeNameOnData, final String attributeNameOnEntity, 
			final ValueType valueType, final Class<? extends AttributeValue> valueClassOnEntity) throws AttributeException {
		if (populationGenerator == null) { throw new AttributeException("'population' parameter can not be null"); }
		if (attributeNameOnData == null || attributeNameOnData.trim().length() == 0) { throw new AttributeException("'attributeNameOnData' parameter can not be null or empty"); }
		if (attributeNameOnEntity == null || attributeNameOnEntity.trim().length() == 0) { throw new AttributeException("'attributeNameOnEntity' parameter can not be null or empty"); }
		if (valueType == null) { throw new AttributeException("'valueType' parameter can not be null"); }
		if (valueClassOnEntity == null) { throw new AttributeException("'valueClassOnEntity' can not be null"); }
		
		this.populationGenerator = populationGenerator;
		this.nameOnData = attributeNameOnData;
		this.nameOnEntity = attributeNameOnEntity;
		this.valueType = valueType;
		this.attributeChangeListeners = new ArrayList<AttributeChangedListener>();
		this.valueClassOnEntity = valueClassOnEntity;
	}
	
	public String getNameOnData() {
		return nameOnData;
	}
	
	public String getNameOnEntity() {
		return nameOnEntity;
	}

	public ValueType getValueType() {
		return valueType;
	}
	
	public Class<? extends AttributeValue> getValueClassOnEntity() {
		return valueClassOnEntity;
	}
	
	public Class<? extends AttributeValue> getValueClassOnData() {
		return valueClassOnData;
	}
	
	public ISyntheticPopulationGenerator getPopulationGenerator() {
		return populationGenerator;
	}
	
	public void addAttributeChangedListener(final AttributeChangedListener l) {
		if (l == null) { return; }
		attributeChangeListeners.add(l);
	}
	
	public void removeAttributeChangedListener(final AttributeChangedListener l) {
		if (l == null) { return; }
		attributeChangeListeners.remove(l);
	}

	// utility method
	protected void internalFireEvent() {
		// fire event
		AttributeChangedEvent event = new AttributeChangedEvent(this);
		fireAttributeChangedEvent(event);
	}
	
	protected void fireAttributeChangedEvent(final AttributeChangedEvent event) {
		for (AttributeChangedListener l : attributeChangeListeners) { l.attributeChanged(event); }
	}
	
	public abstract AttributeValue valueFromString(final List<String> stringValue) throws AttributeException;

	@Override public String toString() {
		return this.getClass().getSimpleName() + " with valueType : " + valueType.getName() + "; dataAttributeName : " + this.nameOnData + "; entityAttributeName : " + this.nameOnEntity;
	}
	

	// TODO revise: should move this method to sub-class?
	public void setDefaultValue(final AttributeValue defaultValue) throws AttributeException {
		if (defaultValue == null) { throw new AttributeException("'defaultValue' parameter can not be null"); }
		if (!defaultValue.getClass().equals(this.valueClassOnData)) { throw new AttributeException("'defaultValue' parameter doesn't belong to the appropriate class"); }
		if (!defaultValue.valueType.equals(this.valueType)) { throw new AttributeException("valueType of 'defaultValue' is not appropriate"); }
		
		this.defaultValue = defaultValue;
		this.castDefaultValue = defaultValue.cast(valueClassOnEntity);
	}
	
	public AttributeValue getDefaultValue() {
		return castDefaultValue;
	}
}
