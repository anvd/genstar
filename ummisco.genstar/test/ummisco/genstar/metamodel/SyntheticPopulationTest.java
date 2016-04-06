package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCSVFile;

public class SyntheticPopulationTest {

	@Test public void testCreateEntityWithAttributeValuesOnEntity() throws GenstarException {
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/metamodel/synthetic_population/attributes.csv", true);
		
		ISingleRuleGenerator generator = new SingleRuleGenerator("dummy generator");
		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		ISyntheticPopulation population = new SyntheticPopulation("people", generator.getAttributes());
		
		AttributeValue ageValue0 = new UniqueValue(DataType.INTEGER, "0");
		Map<String, AttributeValue> attributeValuesOnEntity = new HashMap<String, AttributeValue>();
		attributeValuesOnEntity.put("Age", ageValue0);
		
		for (int i=0; i<10; i++) { population.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity); }
		
		assertTrue(population.getMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity).size() == 10);
	}
}
