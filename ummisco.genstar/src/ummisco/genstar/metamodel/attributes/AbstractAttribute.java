package ummisco.genstar.metamodel.attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.util.PersistentObject;


public abstract class AbstractAttribute {
	
	
	protected int attributeID = PersistentObject.NEW_OBJECT_ID;
	
	protected ISyntheticPopulationGenerator populationGenerator;
	
	protected String nameOnData;
	
	protected String nameOnEntity;
	
	protected DataType dataType;
	
	protected List<AttributeChangedListener> attributeChangeListeners;
	
	protected Class<? extends AttributeValue> valueClassOnData;
	
	protected Class<? extends AttributeValue> valueClassOnEntity;
	
	private AttributeValue defaultValue;
	
	private AttributeValue castDefaultValue;
	
//	protected boolean isIdentity; // TODO remove this property
	
	
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
	
	public AttributeValue getDefaultValueOnEntity() {
		return castDefaultValue;
	}
	
	public AttributeValue getDefaultValueOnData() {
		return defaultValue;
	}
	
	public int getAttributeID() {
		return attributeID;
	}
	
	public void setAttributeID(final int attributeID) {
		this.attributeID = attributeID;
	}
	
	public abstract Set<AttributeValue> valuesOnData();
	
	public abstract boolean add(final AttributeValue value) throws GenstarException;
	
	public abstract boolean addAll(final Set<AttributeValue> values) throws GenstarException;
	
	public abstract boolean remove(final AttributeValue value);

	public abstract void clear();
	
	public abstract AttributeValue getInstanceOfAttributeValue(final AttributeValue value);
	
	public abstract AttributeValue getMatchingAttributeValueOnData(final List<String> stringRepresentationOfValue) throws GenstarException;
	
	public abstract AttributeValue getMatchingAttributeValueOnData(final AttributeValue attributeValue); // throws GenstarException;
}
