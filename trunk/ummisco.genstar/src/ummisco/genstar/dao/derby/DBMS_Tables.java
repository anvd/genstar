package ummisco.genstar.dao.derby;

public class DBMS_Tables {

	static final class ATTRIBUTE_TABLE {
		static final String TABLE_NAME = "ATTRIBUTE";
		
		
		// column indexes
		static final int ATTRIBUTE_ID_COLUMN_INDEX = 1;
		
		static final int POPULATION_GENERATOR_ID_COLUMN_INDEX = 2;
		
		static final int NAME_ON_DATA_COLUMN_INDEX = 3;
		
		static final int NAME_ON_ENTITY_COLUMN_INDEX = 4;
		
		static final int DATA_TYPE_COLUMN_INDEX = 5;
		
		static final int VALUE_TYPE_ON_DATA_COLUMN_INDEX = 6;
		
		static final int VALUE_TYPE_ON_ENTITY_COLUMN_INDEX = 7;
		
		
		// column names
		static final String ATTRIBUTE_ID_COLUMN_NAME = "ATTRIBUTE_ID";
		
		static final String POPULATION_GENERATOR_ID_COLUMN_NAME = "POPULATION_GENERATOR_ID";
		
		static final String NAME_ON_DATA_COLUMN_NAME = "NAME_ON_DATA";
		
		static final String NAME_ON_ENTITY_COLUMN_NAME = "NAME_ON_ENTITY";
		
		static final String DATA_TYPE_COLUMN_NAME = "DATA_TYPE";
		
		static final String VALUE_TYPE_ON_DATA_COLUMN_NAME = "VALUE_TYPE_ON_DATA";
		
		static final String VALUE_TYPE_ON_ENTITY_COLUMN_NAME = "VALUE_TYPE_ON_ENTITY";
		
	}
	
	static final class ATTRIBUTE_INFERENCE_GENERATION_RULE_TABLE {
		static final String TABLE_NAME = "ATTRIBUTE_INFERENCE_GENERATION_RULE";
		
		
		// column indexes
		static final int GENERATION_RULE_ID_COLUMN_INDEX = 1;
		
		static final int INFERRING_ATTRIBUTE_ID_COLUMN_INDEX = 2;
		
		static final int INFERRED_ATTRIBUTE_ID_COLUMN_INDEX = 3;
		
		
		// column names
		static final String GENERATION_RULE_ID_COLUMN_NAME = "GENERATION_RULE_ID";
		
		static final String INFERRING_ATTRIBUTE_ID_COLUMN_NAME = "INFERRING_ATTRIBUTE_ID";
		
		static final String INFERRED_ATTRIBUTE_ID_COLUMN_NAME = "INFERRED_ATTRIBUTE_ID";
	}
	
	static final class ATTRIBUTE_VALUES_FREQUENCY_TABLE {
		static final String TABLE_NAME = "ATTRIBUTE_VALUES_FREQUENCY";
		
		
		// column indexes
		static final int ATTRIBUTE_VALUES_FREQUENCY_ID_COLUMN_INDEX = 1;
		
		static final int GENERATION_RULE_ID_COLUMN_INDEX = 2;
		
		static final int FREQUENCY_COLUMN_INDEX = 3;
		
		
		// column names
		static final String ATTRIBUTE_VALUES_FREQUENCY_ID_COLUMN_NAME = "ATTRIBUTE_VALUES_FREQUENCY_ID";
		
		static final String GENERATION_RULE_ID_COLUMN_NAME = "GENERATION_RULE_ID";
		
		static final String FREQUENCY_COLUMN_NAME = "FREQUENCY";
	}
	
	static final class ATTRIBUTE_VALUES_FREQUENCY_DATA_TABLE {
		static final String TABLE_NAME = "ATTRIBUTE_VALUES_FREQUENCY_DATA";
		
		
		// column indexes
		static final int ATTRIBUTE_VALUES_FREQUENCY_ID_COLUMN_INDEX = 1;
		
		static final int ATTRIBUTE_ID_COLUMN_INDEX = 2;
		
		static final int UNIQUE_VALUE_ID_COLUMN_INDEX = 3;
		
		static final int RANGE_VALUE_ID_COLUMN_INDEX = 4;
		
		
		// column names
		static final String ATTRIBUTE_VALUES_FREQUENCY_ID_COLUMN_NAME = "ATTRIBUTE_VALUES_FREQUENCY_ID";
		
		static final String ATTRIBUTE_ID_COLUMN_NAME = "ATTRIBUTE_ID";
		
		static final String UNIQUE_VALUE_ID_COLUMN_NAME = "UNIQUE_VALUE_ID";
		
		static final String RANGE_VALUE_ID_COLUMN_NAME = "RANGE_VALUE_ID";
	}
	
	static final class ENTITY_TABLE {
		static final String TABLE_NAME = "ENTITY";
		
		
		// column indexes
		static final int ENTITY_ID_COLUMN_INDEX = 1;
		
		static final int POPULATION_ID_COLUMN_INDEX = 2;
		
		
		// column names
		static final String ENTITY_ID_COLUMN_NAME = "ENTITY_ID";
		
		static final String POPULATION_ID_COLUMN_NAME = "POPULATION_ID";		
		
	}
	
	static final class ENTITY_ATTRIBUTE_TABLE {
		static final String TABLE_NAME = "ENTITY_ATTRIBUTE";
		
		
		// column indexes
		static final int ENTITY_ATTRIBUTE_ID_COLUMN_INDEX = 1;
		
		static final int ENTITY_ID_COLUMN_INDEX = 2;
		
		static final int NAME_COLUMN_INDEX = 3;
		
		static final int TYPE_COLUMN_INDEX = 4;
		
		static final int VALUE_TYPE_COLUMN_INDEX = 5;
		
		
		// column names
		static final String ENTITY_ATTRIBUTE_ID_COLUMN_NAME = "ENTITY_ATTRIBUTE_ID";
		
		static final String ENTITY_ID_COLUMN_NAME = "ENTITY_ID";
		
		static final String NAME_COLUMN_NAME = "NAME";
		
		static final String TYPE_COLUMN_NAME = "TYPE";
		
		static final String VALUE_TYPE_COLUMN_NAME = "VALUE_TYPE";
	}
	
	static final class ENTITY_ATTRIBUTE_RANGE_VALUE_TABLE {
		static final String TABLE_NAME = "ENTITY_ATTRIBUTE_RANGE_VALUE";
		
		
		// column indexes
		static final int ENTITY_ATTRIBUTE_VALUE_ID_COLUMN_INDEX = 1;
		
		static final int ENTITY_ATTRIBUTE_ID_COLUMN_INDEX = 2;
		
		static final int MIN_STRING_VALUE_COLUMN_INDEX = 3;
		
		static final int MAX_STRING_VALUE_COLUMN_INDEX = 4;
		
		
		// column names
		static final String ENTITY_ATTRIBUTE_VALUE_ID_COLUMN_NAME = "ENTITY_ATTRIBUTE_VALUE_ID";
		
		static final String ENTITY_ATTRIBUTE_ID_COLUMN_NAME = "ENTITY_ATTRIBUTE_ID";
		
		static final String MIN_STRING_VALUE_COLUMN_NAME = "MIN_STRING_VALUE";
		
		static final String MAX_STRING_VALUE_COLUMN_NAME = "MAX_STRING_VALUE";
	}
	
	static final class ENTITY_ATTRIBUTE_UNIQUE_VALUE_TABLE {
		static final String TABLE_NAME = "ENTITY_ATTRIBUTE_UNIQUE_VALUE";
		
		
		// column indexes
		static final int ENTITY_ATTRIBUTE_UNIQUE_VALUE_ID_COLUMN_INDEX = 1;
		
		static final int ENTITY_ATTRIBUTE_ID_COLUMN_INDEX = 2;
		
		static final int STRING_VALUE_COLUMN_INDEX = 3;
		
		
		// column names
		static final String ENTITY_ATTRIBUTE_UNIQUE_VALUE_ID_COLUMN_NAME = "ENTITY_ATTRIBUTE_UNIQUE_VALUE_ID";
		
		static final String ENTITY_ATTRIBUTE_ID_COLUMN_NAME = "ENTITY_ATTRIBUTE_ID";
		
		static final String STRING_VALUE_COLUMN_NAME = "STRING_VALUE";
		
	}
	
	
	static final class GENERATION_RULE_TABLE {
		static final String TABLE_NAME = "GENERATION_RULE";
		
		
		// column indexes
		static final int GENERATION_RULE_ID_COLUMN_INDEX = 1;
		
		static final int POPULATION_GENERATOR_ID_COLUMN_INDEX = 2;
		
		static final int NAME_COLUMN_INDEX = 3;
		
		static final int RULE_ORDER_COLUMN_INDEX = 4;
		
		static final int RULE_TYPE_COLUMN_INDEX = 5;
		
		
		// column names
		static final String GENERATION_RULE_ID_COLUMN_NAME = "GENERATION_RULE_ID";
		
		static final String POPULATION_GENERATOR_ID_COLUMN_NAME = "POPULATION_GENERATOR_ID";
		
		static final String NAME_COLUMN_NAME = "NAME";
		
		static final String RULE_ORDER_COLUMN_NAME = "RULE_ORDER";
		
		static final String RULE_TYPE_COLUMN_NAME = "RULE_TYPE";
	}
	
	static final class INFERENCE_DATA_UNIQUE_INFER_RANGE_TABLE {
		static final String TABLE_NAME = "INFERENCE_DATA_UNIQUE_INFER_RANGE";
		
		
		// column indexes
		static final int GENERATION_RULE_ID_COLUMN_INDEX = 1;
		
		static final int INFERRING_UNIQUE_VALUE_ID_COLUMN_INDEX = 2;
		
		static final int INFERRED_RANGE_VALUE_ID_COLUMN_INDEX = 3;
		
		
		// column names
		static final String GENERATION_RULE_ID_COLUMN_NAME = "GENERATION_RULE_ID";
		
		static final String INFERRING_UNIQUE_VALUE_ID_COLUMN_NAME = "INFERRING_UNIQUE_VALUE_ID";
		
		static final String INFERRED_RANGE_VALUE_ID_COLUMN_NAME = "INFERRED_RANGE_VALUE_ID";
	}
	
	static final class INFERENCE_DATA_RANGE_INFER_RANGE_TABLE {
		static final String TABLE_NAME = "INFERENCE_DATA_RANGE_INFER_RANGE";
		
		
		// column indexes
		static final int GENERATION_RULE_ID_COLUMN_INDEX = 1;
		
		static final int INFERRING_RANGE_VALUE_ID_COLUMN_INDEX = 2;
		
		static final int INFERRED_RANGE_VALUE_ID_COLUMN_INDEX = 3;
		
		
		// column names
		static final String GENERATION_RULE_ID_COLUMN_NAME = "GENERATION_RULE_ID";
		
		static final String INFERRING_RANGE_VALUE_ID_COLUMN_NAME = "INFERRING_RANGE_VALUE_ID";
		
		static final String INFERRED_RANGE_VALUE_ID_COLUMN_NAME = "INFERRED_RANGE_VALUE_ID";
	}
	
	static final class INFERENCE_DATA_UNIQUE_INFER_UNIQUE_TABLE {
		static final String TABLE_NAME = "INFERENCE_DATA_UNIQUE_INFER_UNIQUE";
		
		
		// column indexes
		static final int ATTRIBUTE_INFERENCE_GENERATION_RULE_ID_COLUMN_INDEX = 1;
		
		static final int INFERRING_UNIQUE_VALUE_ID_COLUMN_INDEX = 2;
		
		static final int INFERRED_UNIQUE_VALUE_ID_COLUMN_INDEX = 3;
		
		
		// column names
		static final String GENERATION_RULE_ID_COLUMN_NAME = "GENERATION_RULE_ID";
		
		static final String INFERRING_UNIQUE_VALUE_ID_COLUMN_NAME = "INFERRING_UNIQUE_VALUE_ID";
		
		static final String INFERRED_UNIQUE_VALUE_ID_COLUMN_NAME = "INFERRED_UNIQUE_VALUE_ID";
	}
	
	static final class INFERENCE_DATA_RANGE_INFER_UNIQUE_TABLE {
		static final String TABLE_NAME = "INFERENCE_DATA_RANGE_INFER_UNIQUE";
		
		
		// column indexes
		static final int GENERATION_RULE_ID_COLUMN_INDEX = 1;
		
		static final int INFERRING_RANGE_VALUE_ID_COLUMN_INDEX = 2;
		
		static final int INFERRED_UNIQUE_VALUE_ID_COLUMN_INDEX = 3;
		
		
		// column names
		static final String GENERATION_RULE_ID_COLUMN_NAME = "GENERATION_RULE_ID";
		
		static final String INFERRING_RANGE_VALUE_ID_COLUMN_NAME = "INFERRING_RANGE_VALUE_ID";
		
		static final String INFERRED_UNIQUE_VALUE_ID_COLUMN_NAME = "INFERRED_UNIQUE_VALUE_ID";
	}
	
	static final class INPUT_OUTPUT_ATTRIBUTE_TABLE {
		static final String TABLE_NAME = "INPUT_OUTPUT_ATTRIBUTE";
		
		
		// column indexes
		static final int ATTRIBUTE_ID_COLUMN_INDEX = 1;
		
		static final int GENERATION_RULE_ID_COLUMN_INDEX = 2;
		
		static final int ATTRIBUTE_ORDER_COLUMN_INDEX = 3;
		
		static final int IS_INPUT_ATTRIBUTE_COLUMN_INDEX = 4;
		
		
		// column names
		static final String ATTRIBUTE_ID_COLUMN_NAME = "ATTRIBUTE_ID";
		
		static final String GENERATION_RULE_ID_COLUMN_NAME = "GENERATION_RULE_ID";
		
		static final String ATTRIBUTE_ORDER_COLUMN_NAME = "ATTRIBUTE_ORDER";
		
		static final String IS_INPUT_ATTRIBUTE_COLUMN_NAME = "IS_INPUT_ATTRIBUTE";
	}
	
	static final class SYNTHETIC_POPULATION_GENERATOR_TABLE {
		static final String TABLE_NAME = "POPULATION_GENERATOR";
		
		
		// column indexes
		static final int POPULATION_GENERATOR_ID_COLUMN_INDEX = 1;
		
		static final int NAME_COLUMN_INDEX = 2;
		
		static final int INITIAL_NUMBER_OF_ENTITIES_COLUMN_INDEX = 3;
		
		
		// column names
		static final String POPULATION_GENERATOR_ID_COLUMN_NAME = "POPULATION_GENERATOR_ID";
		
		static final String NAME_COLUMN_NAME = "NAME";
		
		static final String INITIAL_NUMBER_OF_ENTITIES_COLUMN_NAME = "INITIAL_NUMBER_OF_ENTITIES";
	}
	
	static final class RANGE_VALUE_TABLE {
		static final String TABLE_NAME = "RANGE_VALUE";
		
		
		// column indexes
		static final int RANGE_VALUE_ID_COLUMN_INDEX = 1;
		
		static final int ATTRIBUTE_ID_COLUMN_INDEX = 2;
		
		static final int MIN_STRING_VALUE_COLUMN_INDEX = 3;
		
		static final int MAX_STRING_VALUE_COLUMN_INDEX = 4;
		
		
		// column names
		static final String RANGE_VALUE_ID_COLUMN_NAME = "RANGE_VALUE_ID";
		
		static final String ATTRIBUTE_ID_COLUMN_NAME = "ATTRIBUTE_ID";
		
		static final String MIN_STRING_VALUE_COLUMN_NAME = "MIN_STRING_VALUE";
		
		static final String MAX_STRING_VALUE_COLUMN_NAME = "MAX_STRING_VALUE";
	}
	
	static final class SYNTHETIC_POPULATION_TABLE {
		static final String TABLE_NAME = "SYNTHETIC_POPULATION";
		
		
		// column indexes
		static final int POPULATION_ID_COLUMN_INDEX = 1;
		
		static final int NAME_COLUMN_INDEX = 2;
		
		static final int NUMBER_OF_ENTITIES_COLUMN_INDEX = 3;
		
		
		// column names
		static final String POPULATION_ID_COLUMN_NAME = "POPULATION_ID";
		
		static final String NAME_COLUMN_NAME = "NAME";
		
		static final String NUMBER_OF_ENTITIES_COLUMN_NAME = "NUMBER_OF_ENTITIES"; // TODO remove? redundant
	}
	
	static final class UNIQUE_VALUE_TABLE {
		static final String TABLE_NAME = "UNIQUE_VALUE";
		
		
		// column indexes
		static final int UNIQUE_VALUE_ID_COLUMN_INDEX = 1;
		
		static final int ATTRIBUTE_ID_COLUMN_INDEX = 2;
		
		static final int STRING_VALUE_COLUMN_INDEX = 3;
		
		
		// column names
		static final String UNIQUE_VALUE_ID_COLUMN_NAME = "UNIQUE_VALUE_ID";
		
		static final String ATTRIBUTE_ID_COLUMN_NAME = "ATTRIBUTE_ID";
		
		static final String STRING_VALUE_COLUMN_NAME = "STRING_VALUE";
	}
}
