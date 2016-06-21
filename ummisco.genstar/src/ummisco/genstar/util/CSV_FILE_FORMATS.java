package ummisco.genstar.util;


public final class CSV_FILE_FORMATS {

	public static final class ATTRIBUTES {
		static final String ATTRIBUTE_VALUE_DELIMITER = ";";
		public static final String MIN_MAX_VALUE_DELIMITER = ":";
		public static final String FIELD_DELIMITER = ",";
			
		// Header of attribute meta-data file: 
		//		Name On Data,Name On Entity,Data Type,Value Type On Data,Values,Value Type On Entity
		static final String HEADER_STR = "Name On Data,Name On Entity, Data Type,Value Type,Values,Value Type On Entity";
		static final int NB_OF_COLS = 6;
		static String[] HEADERS = new String[NB_OF_COLS];
		static {
			HEADERS[0] = "Name On Data";
			HEADERS[1] = "Name On Entity";
			HEADERS[2] = "Data Type";
			HEADERS[3] = "Value Type On Data";
			HEADERS[4] = "Values";
			HEADERS[5] = "Value Type On Entity";
		}
		
		// Value Type Names
		static final String UNIQUE_VALUE_NAME = "Unique";
		static final String RANGE_VALUE_NAME = "Range";
		static final String UNIQUE_VALUE_WITH_RANGE_INPUT_NAME = "UniqueWithRangeInput";
		
		public static final class UNIQUE_VALUES_ATTRIBUTE_WITH_RANGE_INPUT {
			
			public static final int MAX_VALUE = Integer.MAX_VALUE;
			
			public static final String MAX_VALUE_STRING = "MAX_VALUE";
			
			public static final int MIN_VALUE = Integer.MIN_VALUE;
			
			public static final String MIN_VALUE_STRING = "MIN_VALUE";
		}
	}
	
	
	public static final class SAMPLE_FREE_GENERATION_RULES_LIST {
		
		// Header of Generation Rule meta-data file:
		//		Name, File, Type
		public static final String HEADER_STR = "Name,File,Rule Type";
		public static final int NB_OF_COLS = 3;
		public static final String[] HEADERS = new String[NB_OF_COLS];
		static {
			HEADERS[0] = "Name";
			HEADERS[1] = "File";
			HEADERS[2] = "Rule Type";
		}
		
		public static final String JAVA_CLASS_PARAMETER_DELIMITER = "?";
	}
	
	
	public static final class FREQUENCY_DISTRIBUTION_GENERATION_RULE {
		
		public static final String ATTRIBUTE_NAME_TYPE_DELIMITER = ":";
		
		public static final String INPUT_ATTRIBUTE = "Input";
		public static final String OUTPUT_ATTRIBUTE = "Output";
		public static final String FREQUENCY = "Frequency";
	}
	
	
	public static final class FREQUENCY_DISTRIBUTION_FORMATS_LIST {
		
		// Header
		//		Format File,Output File
		public static final String HEADER_STR = "Format File,Output File";
		public static final int NB_OF_COLS = 2;
		public static final String[] HEADERS = new String[NB_OF_COLS];
		static {
			HEADERS[0] = "Format File";
			HEADERS[1] = "Output File";
		}
	}
}
