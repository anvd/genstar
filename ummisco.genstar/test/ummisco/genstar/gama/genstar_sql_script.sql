DROP TABLE INFERENCE_RANGE_ATTRIBUTE_DATA2;
DROP TABLE INFERENCE_RANGE_ATTRIBUTE_DATA1;
DROP TABLE INFERENCE_VALUE_ATTRIBUTE_DATA2;
DROP TABLE INFERENCE_VALUE_ATTRIBUTE_DATA1;
DROP TABLE ATTRIBUTE_INFERENCE_GENERATION_RULE;
DROP TABLE FREQUENCY_DISTRIBUTION_ELEMENT_ATTRIBUTE_VALUE;
DROP TABLE FREQUENCY_DISTRIBUTION_ELEMENT;
DROP TABLE INPUT_OUTPUT_ATTRIBUTE;
DROP TABLE GENERATION_RULE;
DROP TABLE RANGE_VALUE;
DROP TABLE UNIQUE_VALUE;
DROP TABLE ATTRIBUTE;
DROP TABLE ENTITY_ATTRIBUTE_RANGE_VALUE;
DROP TABLE ENTITY_ATTRIBUTE_UNIQUE_VALUE;
DROP TABLE ENTITY_ATTRIBUTE;
DROP TABLE ENTITY;
DROP TABLE SYNTHETIC_POPULATION;
DROP TABLE POPULATION_GENERATOR;
CREATE TABLE POPULATION_GENERATOR (
	POPULATION_GENERATOR_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	NAME VARCHAR(100) NOT NULL UNIQUE,
	INITIAL_NUMBER_OF_ENTITIES INT NOT NULL,
	CONSTRAINT INITIAL_NUMBER_OF_ENTITIES_CK CHECK (INITIAL_NUMBER_OF_ENTITIES > 0)
);
CREATE TABLE SYNTHETIC_POPULATION (
	POPULATION_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	NAME VARCHAR(100) NOT NULL UNIQUE,
	NUMBER_OF_ENTITIES INT NOT NULL
);
CREATE TABLE ENTITY (
	ENTITY_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	POPULATION_ID INT NOT NULL,
	CONSTRAINT SYNTHETIC_POPULATION_PK FOREIGN KEY (POPULATION_ID) REFERENCES SYNTHETIC_POPULATION(POPULATION_ID) ON DELETE CASCADE	
);
CREATE TABLE ENTITY_ATTRIBUTE (
	ENTITY_ATTRIBUTE_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	ENTITY_ID INT NOT NULL,
	NAME VARCHAR(100) NOT NULL,
	TYPE SMALLINT NOT NULL,
	VALUE_TYPE SMALLINT NOT NULL,
	CONSTRAINT ENTITY_ID_PK FOREIGN KEY (ENTITY_ID) REFERENCES ENTITY(ENTITY_ID) ON DELETE CASCADE	
);
CREATE TABLE ENTITY_ATTRIBUTE_UNIQUE_VALUE (
	ENTITY_ATTRIBUTE_UNIQUE_VALUE_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	ENTITY_ATTRIBUTE_ID INT NOT NULL,
	STRING_VALUE VARCHAR(100) NOT NULL,
	CONSTRAINT ENTITY_ATTRIBUTE_ID_PK_1 FOREIGN KEY (ENTITY_ATTRIBUTE_ID) REFERENCES ENTITY_ATTRIBUTE(ENTITY_ATTRIBUTE_ID) ON DELETE CASCADE	
);
CREATE TABLE ENTITY_ATTRIBUTE_RANGE_VALUE (
	ENTITY_ATTRIBUTE_RANGE_VALUE_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	ENTITY_ATTRIBUTE_ID INT NOT NULL,
	MIN_STRING_VALUE VARCHAR(100) NOT NULL,
	MAX_STRING_VALUE VARCHAR(100) NOT NULL,
	CONSTRAINT ENTITY_ATTRIBUTE_ID_PK_2 FOREIGN KEY (ENTITY_ATTRIBUTE_ID) REFERENCES ENTITY_ATTRIBUTE(ENTITY_ATTRIBUTE_ID) ON DELETE CASCADE	
);
CREATE TABLE ATTRIBUTE (
	ATTRIBUTE_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	POPULATION_GENERATOR_ID INT NOT NULL,
	NAME_ON_DATA VARCHAR(100) NOT NULL,
	NAME_ON_ENTITY VARCHAR(100) NOT NULL,
	DATA_TYPE SMALLINT NOT NULL,
	VALUE_TYPE_ON_DATA SMALLINT NOT NULL,
	VALUE_TYPE_ON_ENTITY SMALLINT NOT NULL,
	CONSTRAINT POPULATION_GENERATION_ID_PK FOREIGN KEY (POPULATION_GENERATOR_ID) REFERENCES POPULATION_GENERATOR(POPULATION_GENERATOR_ID) ON DELETE CASCADE		
);
CREATE TABLE UNIQUE_VALUE (
	UNIQUE_VALUE_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	ATTRIBUTE_ID INT NOT NULL,
	STRING_VALUE VARCHAR(100) NOT NULL,
	CONSTRAINT ATTRIBUTE_ID_FK3 FOREIGN KEY (ATTRIBUTE_ID) REFERENCES ATTRIBUTE(ATTRIBUTE_ID) ON DELETE CASCADE	
);
CREATE TABLE RANGE_VALUE (
	RANGE_VALUE_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	ATTRIBUTE_ID INT NOT NULL,
	MIN_STRING_VALUE VARCHAR(100) NOT NULL,
	MAX_STRING_VALUE VARCHAR(100) NOT NULL,
	CONSTRAINT ATTRIBUTE_ID_FK4 FOREIGN KEY (ATTRIBUTE_ID) REFERENCES ATTRIBUTE(ATTRIBUTE_ID) ON DELETE CASCADE	
);
CREATE TABLE GENERATION_RULE (
	GENERATION_RULE_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	POPULATION_GENERATOR_ID INT NOT NULL,
	NAME VARCHAR(100) NOT NULL,
	RULE_ORDER INT NOT NULL,
	RULE_TYPE INT NOT NULL,
	CONSTRAINT POPULATION_GENERATOR_ID_FK1 FOREIGN KEY (POPULATION_GENERATOR_ID) REFERENCES POPULATION_GENERATOR(POPULATION_GENERATOR_ID) ON DELETE CASCADE,
	CONSTRAINT RULE_ORDER_CK CHECK (RULE_ORDER >= 1)
);
CREATE TABLE INPUT_OUTPUT_ATTRIBUTE (
	ATTRIBUTE_ID INT NOT NULL,
	GENERATION_RULE_ID INT NOT NULL,
	ATTRIBUTE_ORDER INT NOT NULL,
	IS_INPUT_ATTRIBUTE BOOLEAN NOT NULL,
	CONSTRAINT INPUT_OUTPUT_ATTRIBUTE_PK1 PRIMARY KEY (ATTRIBUTE_ID, GENERATION_RULE_ID),
	CONSTRAINT ATTRIBUTE_ID_FK1 FOREIGN KEY (ATTRIBUTE_ID) REFERENCES ATTRIBUTE(ATTRIBUTE_ID) ON DELETE CASCADE,
	CONSTRAINT GENERATION_RULE_ID_FK2 FOREIGN KEY (GENERATION_RULE_ID) REFERENCES GENERATION_RULE(GENERATION_RULE_ID) ON DELETE CASCADE,
	CONSTRAINT ATTRIBUTE_ORDER_CK1 CHECK (ATTRIBUTE_ORDER >= 1)	
);
CREATE TABLE FREQUENCY_DISTRIBUTION_ELEMENT (
	FREQUENCY_DISTRIBUTION_ELEMENT_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	GENERATION_RULE_ID INT NOT NULL,
	FREQUENCY INT NOT NULL,
	CONSTRAINT GENERATION_RULE_ID_FK_4 FOREIGN KEY (GENERATION_RULE_ID) REFERENCES GENERATION_RULE(GENERATION_RULE_ID)	
);
CREATE TABLE FREQUENCY_DISTRIBUTION_ELEMENT_ATTRIBUTE_VALUE (
	FREQUENCY_DISTRIBUTION_ELEMENT_ATTRIBUTE_VALUE_ID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	FREQUENCY_DISTRIBUTION_ELEMENT_ID INT NOT NULL,
	ATTRIBUTE_ID INT NOT NULL,
	UNIQUE_VALUE_ID INT,
	RANGE_VALUE_ID INT,
	CONSTRAINT FREQUENCY_DISTRIBUTION_ELEMENT_ID_FK FOREIGN KEY (FREQUENCY_DISTRIBUTION_ELEMENT_ID) REFERENCES FREQUENCY_DISTRIBUTION_ELEMENT(FREQUENCY_DISTRIBUTION_ELEMENT_ID) ON DELETE CASCADE,
	CONSTRAINT ATTRIBUTE_ID_FK_3 FOREIGN KEY (ATTRIBUTE_ID) REFERENCES ATTRIBUTE(ATTRIBUTE_ID) ON DELETE CASCADE,
	CONSTRAINT UNIQUE_VALUE_ID_FK FOREIGN KEY (UNIQUE_VALUE_ID) REFERENCES UNIQUE_VALUE(UNIQUE_VALUE_ID) ON DELETE CASCADE,
	CONSTRAINT RANGE_VALUE_ID_FK FOREIGN KEY (RANGE_VALUE_ID) REFERENCES RANGE_VALUE(RANGE_VALUE_ID) ON DELETE CASCADE
);
CREATE TABLE ATTRIBUTE_INFERENCE_GENERATION_RULE (
	GENERATION_RULE_ID INT NOT NULL PRIMARY KEY,
	INFERRING_ATTRIBUTE_ID INT NOT NULL,
	INFERRED_ATTRIBUTE_ID INT NOT NULL,
	CONSTRAINT GENERATION_RULE_ID_FK6 FOREIGN KEY (GENERATION_RULE_ID) REFERENCES GENERATION_RULE(GENERATION_RULE_ID) ON DELETE CASCADE,
	CONSTRAINT INFERRING_ATTRIBUTE_ID_FK FOREIGN KEY (INFERRING_ATTRIBUTE_ID) REFERENCES ATTRIBUTE(ATTRIBUTE_ID) ON DELETE CASCADE,
	CONSTRAINT INFERRED_ATTRIBUTE_ID_FK5 FOREIGN KEY (INFERRED_ATTRIBUTE_ID) REFERENCES ATTRIBUTE(ATTRIBUTE_ID) ON DELETE CASCADE
);
CREATE TABLE INFERENCE_VALUE_ATTRIBUTE_DATA1 (
	GENERATION_RULE_ID INT NOT NULL,
	INFERRING_UNIQUE_VALUE_ID INT NOT NULL,
	INFERRED_UNIQUE_VALUE_ID INT NOT NULL,
	CONSTRAINT INFERENCE_VALUE_ATTRIBUTE_DATA1_PK PRIMARY KEY (GENERATION_RULE_ID, INFERRING_UNIQUE_VALUE_ID, INFERRED_UNIQUE_VALUE_ID),
	CONSTRAINT GENERATION_RULE_ID_FK7 FOREIGN KEY (GENERATION_RULE_ID) REFERENCES ATTRIBUTE_INFERENCE_GENERATION_RULE(GENERATION_RULE_ID) ON DELETE CASCADE,
	CONSTRAINT INFERRING_UNIQUE_VALUE_ID_FK1 FOREIGN KEY (INFERRING_UNIQUE_VALUE_ID) REFERENCES UNIQUE_VALUE(UNIQUE_VALUE_ID) ON DELETE CASCADE,
	CONSTRAINT INFERRED_UNIQUE_VALUE_ID_FK1 FOREIGN KEY (INFERRED_UNIQUE_VALUE_ID) REFERENCES UNIQUE_VALUE(UNIQUE_VALUE_ID) ON DELETE CASCADE
);
CREATE TABLE INFERENCE_VALUE_ATTRIBUTE_DATA2 (
	GENERATION_RULE_ID INT NOT NULL,
	INFERRING_RANGE_VALUE_ID INT NOT NULL,
	INFERRED_UNIQUE_VALUE_ID INT NOT NULL,	
	CONSTRAINT INFERENCE_VALUE_ATTRIBUTE_DATA2_PK PRIMARY KEY (GENERATION_RULE_ID, INFERRING_RANGE_VALUE_ID, INFERRED_UNIQUE_VALUE_ID),
	CONSTRAINT GENERATION_RULE_ID_FK8 FOREIGN KEY (GENERATION_RULE_ID) REFERENCES ATTRIBUTE_INFERENCE_GENERATION_RULE(GENERATION_RULE_ID) ON DELETE CASCADE,
	CONSTRAINT INFERRING_RANGE_VALUE_ID_FK1 FOREIGN KEY (INFERRING_RANGE_VALUE_ID) REFERENCES RANGE_VALUE(RANGE_VALUE_ID) ON DELETE CASCADE,
	CONSTRAINT INFERRED_UNIQUE_VALUE_ID_FK2 FOREIGN KEY (INFERRED_UNIQUE_VALUE_ID) REFERENCES UNIQUE_VALUE(UNIQUE_VALUE_ID) ON DELETE CASCADE
);
CREATE TABLE INFERENCE_RANGE_ATTRIBUTE_DATA1 (
	GENERATION_RULE_ID INT NOT NULL,
	INFERRING_UNIQUE_VALUE_ID INT NOT NULL,
	INFERRED_RANGE_VALUE_ID INT NOT NULL,	
	CONSTRAINT INFERENCE_RANGE_ATTRIBUTE_DATA1_PK PRIMARY KEY (GENERATION_RULE_ID, INFERRING_UNIQUE_VALUE_ID, INFERRED_RANGE_VALUE_ID),
	CONSTRAINT GENERATION_RULE_ID_FK9 FOREIGN KEY (GENERATION_RULE_ID) REFERENCES ATTRIBUTE_INFERENCE_GENERATION_RULE(GENERATION_RULE_ID) ON DELETE CASCADE,
	CONSTRAINT INFERRING_UNIQUE_VALUE_ID_FK2 FOREIGN KEY (INFERRING_UNIQUE_VALUE_ID) REFERENCES UNIQUE_VALUE(UNIQUE_VALUE_ID) ON DELETE CASCADE,
	CONSTRAINT INFERRED_RANGE_VALUE_ID_FK1 FOREIGN KEY (INFERRED_RANGE_VALUE_ID) REFERENCES RANGE_VALUE(RANGE_VALUE_ID) ON DELETE CASCADE
);
CREATE TABLE INFERENCE_RANGE_ATTRIBUTE_DATA2 (
	GENERATION_RULE_ID INT NOT NULL,
	INFERRING_RANGE_VALUE_ID INT NOT NULL,
	INFERRED_RANGE_VALUE_ID INT NOT NULL,
	CONSTRAINT INFERENCE_RANGE_ATTRIBUTE_DATA2_PK PRIMARY KEY (GENERATION_RULE_ID, INFERRING_RANGE_VALUE_ID, INFERRED_RANGE_VALUE_ID),
	CONSTRAINT GENERATION_RULE_ID_FK10 FOREIGN KEY (GENERATION_RULE_ID) REFERENCES ATTRIBUTE_INFERENCE_GENERATION_RULE(GENERATION_RULE_ID) ON DELETE CASCADE,
	CONSTRAINT INFERRING_RANGE_VALUE_ID_FK2 FOREIGN KEY (INFERRING_RANGE_VALUE_ID) REFERENCES RANGE_VALUE(RANGE_VALUE_ID) ON DELETE CASCADE,
	CONSTRAINT INFERRED_RANGE_VALUE_ID_FK2 FOREIGN KEY (INFERRED_RANGE_VALUE_ID) REFERENCES RANGE_VALUE(RANGE_VALUE_ID) ON DELETE CASCADE
);
