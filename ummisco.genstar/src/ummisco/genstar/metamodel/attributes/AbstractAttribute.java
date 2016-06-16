package ummisco.genstar.metamodel.attributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.util.PersistentObject;


public abstract class AbstractAttribute {
	
	// link to DB -> TODO: erase ? 
	protected int attributeID = PersistentObject.NEW_OBJECT_ID;
	
	protected String nameOnData;
	
	protected String nameOnEntity;
	
	protected DataType dataType;
	
	protected List<AttributeChangedListener> attributeChangeListeners;
	
	protected Class<? extends AttributeValue> valueClassOnData;
	
	protected Class<? extends AttributeValue> valueClassOnEntity;
	
	private AttributeValue defaultValue;
	
	private AttributeValue castDefaultValue;
	
	private AttributeValue emptyValue;
	
	private AbstractAttribute referentAttribute;
	
	
	protected AbstractAttribute(final String attributeNameOnData, final String attributeNameOnEntity, 
			final DataType dataType, final Class<? extends AttributeValue> valueClassOnEntity, AbstractAttribute referentAttribute) throws GenstarException {
		if (attributeNameOnData == null || attributeNameOnData.trim().length() == 0) { throw new GenstarException("'attributeNameOnData' parameter can not be null or empty"); }
		if (attributeNameOnEntity == null || attributeNameOnEntity.trim().length() == 0) { throw new GenstarException("'attributeNameOnEntity' parameter can not be null or empty"); }
		if (dataType == null) { throw new GenstarException("'dataType' parameter can not be null"); }
		if (valueClassOnEntity == null) { throw new GenstarException("'valueClassOnEntity' can not be null"); }
	
		this.nameOnData = attributeNameOnData;
		this.nameOnEntity = attributeNameOnEntity;
		this.dataType = dataType;
		this.attributeChangeListeners = new ArrayList<AttributeChangedListener>();
		this.valueClassOnEntity = valueClassOnEntity;
		this.referentAttribute = (referentAttribute == null ? this : referentAttribute);
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
	
	public AbstractAttribute getReferentAttribute(){
		return referentAttribute;
	}
	
	public Class<? extends AttributeValue> getValueClassOnEntity() {
		return valueClassOnEntity;
	}
	
	public Class<? extends AttributeValue> getValueClassOnData() {
		return valueClassOnData;
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
	
	public abstract AttributeValue findCorrespondingAttributeValueOnData(final List<String> stringValue) throws GenstarException;
	
	public abstract AttributeValue findMatchingAttributeValueOnData(final AttributeValue attributeValue) throws GenstarException; // TODO remove as of duplicate with getInstanceOfAttributeValue

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
	
	public void setEmptyValue(AttributeValue emptyValue) {
		this.emptyValue = emptyValue;
	}
	
	public AttributeValue getEmptyValue(){
		return emptyValue;
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
	
	public abstract AttributeValue getInstanceOfAttributeValue(final AttributeValue value);

	public abstract void clear();

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeChangeListeners == null) ? 0 : attributeChangeListeners.hashCode());
		result = prime * result + attributeID;
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result + ((nameOnData == null) ? 0 : nameOnData.hashCode());
		result = prime * result + ((nameOnEntity == null) ? 0 : nameOnEntity.hashCode());
		result = prime * result + ((valueClassOnData == null) ? 0 : valueClassOnData.hashCode());
		result = prime * result + ((valueClassOnEntity == null) ? 0 : valueClassOnEntity.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractAttribute other = (AbstractAttribute) obj;
		if (attributeChangeListeners == null) {
			if (other.attributeChangeListeners != null)
				return false;
		} else if (!attributeChangeListeners.equals(other.attributeChangeListeners))
			return false;
		if (attributeID != other.attributeID)
			return false;
		if (dataType != other.dataType)
			return false;
		if (nameOnData == null) {
			if (other.nameOnData != null)
				return false;
		} else if (!nameOnData.equals(other.nameOnData))
			return false;
		if (nameOnEntity == null) {
			if (other.nameOnEntity != null)
				return false;
		} else if (!nameOnEntity.equals(other.nameOnEntity))
			return false;
		if (valueClassOnData == null) {
			if (other.valueClassOnData != null)
				return false;
		} else if (!valueClassOnData.equals(other.valueClassOnData))
			return false;
		if (valueClassOnEntity == null) {
			if (other.valueClassOnEntity != null)
				return false;
		} else if (!valueClassOnEntity.equals(other.valueClassOnEntity))
			return false;
		return true;
	}


}
