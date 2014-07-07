package ummisco.genstar.metamodel;

import java.util.ArrayList;
import java.util.List;

import ummisco.genstar.exception.GenstarException;


public abstract class AbstractAttribute implements AttributeValueSet {
	
	
	protected int attributeID = -1;
	
	protected ISyntheticPopulationGenerator populationGenerator;
	
	protected String nameOnData;
	
	protected String nameOnEntity;
	
	protected DataType dataType;
	
	protected List<AttributeChangedListener> attributeChangeListeners;
	
	protected Class<? extends AttributeValue> valueClassOnData;
	
	protected Class<? extends AttributeValue> valueClassOnEntity;
	
	private AttributeValue defaultValue;
	
	private AttributeValue castDefaultValue;
	
	
	public AbstractAttribute(final ISyntheticPopulationGenerator populationGenerator, final String attributeNameOnData, final String attributeNameOnEntity, 
			final DataType dataType, final Class<? extends AttributeValue> valueClassOnEntity) throws GenstarException {
		if (populationGenerator == null) { throw new GenstarException("'population' parameter can not be null"); }
		if (attributeNameOnData == null || attributeNameOnData.trim().length() == 0) { throw new GenstarException("'attributeNameOnData' parameter can not be null or empty"); }
		if (attributeNameOnEntity == null || attributeNameOnEntity.trim().length() == 0) { throw new GenstarException("'attributeNameOnEntity' parameter can not be null or empty"); }
		if (dataType == null) { throw new GenstarException("'dataType' parameter can not be null"); }
		if (valueClassOnEntity == null) { throw new GenstarException("'valueClassOnEntity' can not be null"); }
		
		this.populationGenerator = populationGenerator;
		this.nameOnData = attributeNameOnData;
		this.nameOnEntity = attributeNameOnEntity;
		this.dataType = dataType;
		this.attributeChangeListeners = new ArrayList<AttributeChangedListener>();
		this.valueClassOnEntity = valueClassOnEntity;
	}
	
	public String getNameOnData() {
		return nameOnData;
	}
	
	public String getNameOnEntity() {
		return nameOnEntity;
	}

	public DataType getDataType() {
		return dataType;
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
	
	public abstract AttributeValue valueFromString(final List<String> stringValue) throws GenstarException;

	@Override public String toString() {
		return this.getClass().getSimpleName() + " with dataType : " + dataType.getName() + "; nameOnData : " + this.nameOnData + "; nameOnEntity : " + this.nameOnEntity;
	}
	

	// TODO revise: should move this method to sub-class?
	public void setDefaultValue(final AttributeValue defaultValue) throws GenstarException {
		if (defaultValue == null) { throw new GenstarException("'defaultValue' parameter can not be null"); }
		if (!defaultValue.getClass().equals(this.valueClassOnData)) { throw new GenstarException("'defaultValue' parameter doesn't belong to the appropriate class"); }
		if (!defaultValue.dataType.equals(this.dataType)) { throw new GenstarException("valueType of 'defaultValue' is not appropriate"); }
		
		this.defaultValue = defaultValue;
		this.castDefaultValue = defaultValue.cast(valueClassOnEntity);
	}
	
	public AttributeValue getDefaultValue() {
		return castDefaultValue;
	}
	
	public int getAttributeID() {
		return attributeID;
	}
	
	public void setAttributeID(final int attributeID) {
		this.attributeID = attributeID;
	}
}
