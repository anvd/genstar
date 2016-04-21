package ummisco.genstar.gama;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;

public class GenstarGamaTypesConverter {

	/*
	 * GAML types :
	 *  primtive types :
	 *  	bool
	 *  	float
	 *  	int
	 *  	string
	 * 
	 * 	complex types :
	 * 		color
	 * 		file
	 * 		graph
	 * 		list
	 * 		map
	 * 		matrix
	 * 		pair
	 * 		path
	 */
	
	/*
	 * Genstar types :
	 *  value types :
	 *  	unique value
	 *  	range value
	 *  
	 *  data types :
	 *  	bool
	 *  	double
	 *  	float
	 *  	int
	 *  	string
	 */
	
	
	/*
	 * Genstar to GAMA Conversion specification
	 * 		unique value (bool) -> GAML bool
	 * 		unique value (double) -> GAML float
	 * 		unique value (float) -> GAML float
	 * 		unique value(int) ) -> GAML int
	 * 		unique value(string) -> GAML string
	 * 
	 * 		range value (double) -> cast range value (double) TO unique value (double) -> GAML (float)
	 * 		range value (float) -> cast range value (float) TO unique value (float) -> GAML (float)
	 * 		range value (int) -> cast range value (int) TO unique value (int) -> GAML (int)
	 */
	
	public static Object convertGenstar2GamaType(final AttributeValue genstarAttributeValue) throws GenstarException {
		UniqueValue genstarUniqueValue = (UniqueValue) genstarAttributeValue.cast(UniqueValue.class);
		
		switch (genstarUniqueValue.getDataType()) {
			case BOOL:
				return new Boolean(genstarUniqueValue.getStringValue());
				
			case DOUBLE:
				return new Double(genstarUniqueValue.getStringValue());
				
			case FLOAT:
				return new Float(genstarUniqueValue.getStringValue());
			
			case INTEGER:
				return new Integer(genstarUniqueValue.getStringValue());
				
			case STRING:
				return new String(genstarUniqueValue.getStringValue());
			
			default:
				return null;	
		}
	}
	
	
	/*
	 * GAMA to Genstar Conversion specification
	 * 		gamaStringRepresentation + genstarAttribute.dataType -> UniqueValue
	 * 
	 * 		if (AbstractAttribute.valueClassOnEntity == UniqueValue.class) then return UniqueValue
	 * 		else return a RangeValue (make the casting if necessary)
	 */
	
	public static AttributeValue convertGama2GenstarType(final AbstractAttribute genstarAttribute, final String gamaStringRepresentation) throws GenstarException {
		if (genstarAttribute == null || gamaStringRepresentation == null) { throw new GenstarException("Parameters genstarAttribute, gamaStringRepresentation can not be null"); }
		
		UniqueValue genstarValue = new UniqueValue(genstarAttribute.getDataType(), gamaStringRepresentation);
		
		if (genstarAttribute.getNameOnData().equals("Household_ID")) {
			System.out.println("genstarAttribute.getPopulationGenerator().getPopulationName(): " + genstarAttribute.getPopulationGenerator().getPopulationName() + ", genstarAttribute.isIdentity(): " + genstarAttribute.isIdentity());
		}
		
		if (genstarAttribute.isIdentity()) {
			if (genstarAttribute.getValueClassOnEntity().equals(UniqueValue.class)) { return genstarValue; }
			else { // valueClassOnEntity == RangeValue.class
				return genstarValue.cast(RangeValue.class);
			}
		}
		
		// Not an identity attribute then ensure the validity of the value
		AttributeValue matchingGenstarValue = genstarAttribute.findMatchingAttributeValueOnData(genstarValue);
		if (matchingGenstarValue == null) { throw new GenstarException(genstarAttribute.getNameOnEntity() + " isn't accepted " + gamaStringRepresentation + " as a valid value."); }
		
		if (genstarAttribute.getValueClassOnEntity().equals(UniqueValue.class)) { return genstarValue; }
		else { // valueClassOnEntity == RangeValue.class
			if (genstarAttribute instanceof RangeValuesAttribute) {  
				return matchingGenstarValue; // RangeValue already
			} else { // genstarAttribute instanceof UniqueValuesAttribute
				return matchingGenstarValue.cast(RangeValue.class); // cast UniqueValue to RangeValue
			}
		}
	}
	
	
	public static String convertGamaAttributeValueToString(final Object gamaAttributeValue) throws GenstarException {
		
		if (gamaAttributeValue instanceof String) { return (String)gamaAttributeValue; }
		if (gamaAttributeValue instanceof Integer) { return Integer.toString((Integer)gamaAttributeValue); }
		if (gamaAttributeValue instanceof Float) { return Float.toString((Float)gamaAttributeValue); }
		if (gamaAttributeValue instanceof Double) { return Double.toString((Double)gamaAttributeValue); }
		if (gamaAttributeValue instanceof Boolean) { return Boolean.toString((Boolean)gamaAttributeValue); }
		
		throw new GenstarException("Can not convert " + gamaAttributeValue.getClass().getName() + " to String.");
	}
}
