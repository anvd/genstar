package idees.genstar.configuration;

import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;

public enum GSAttDataType {
	
	unique (UniqueValue.class, UniqueValue.class),
	range (RangeValue.class, RangeValue.class);
	
	private Class<? extends AttributeValue> dataClazz;
	private Class<? extends AttributeValue> entityClazz;
	
	private GSAttDataType(Class<? extends AttributeValue> dataClazz, Class<? extends AttributeValue> entityClazz){
		this.dataClazz = dataClazz;
		this.entityClazz = entityClazz;
	}
	
	public Class<? extends AttributeValue> getAttributeOnDataMetaDataClass(){
		return dataClazz;
	}
	
	public Class<? extends AttributeValue> getAttributeOnEntityMetaDataClass(){
		return entityClazz;
	}
}
