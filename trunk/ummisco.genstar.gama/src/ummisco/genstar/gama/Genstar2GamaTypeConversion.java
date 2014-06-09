package ummisco.genstar.gama;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.RangeValue;
import ummisco.genstar.metamodel.UniqueValue;
import ummisco.genstar.metamodel.DataType;

public class Genstar2GamaTypeConversion {

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
	 * Conversion specification : Genstar -> GAML
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
	
	public static Object convertGenstar2GamaType(final AttributeValue genstarAttributeValue) {
		try {
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
			}
		} catch (GenstarException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
