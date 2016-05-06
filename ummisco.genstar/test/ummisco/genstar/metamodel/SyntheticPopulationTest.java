package ummisco.genstar.metamodel;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.Population;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;

public class SyntheticPopulationTest {

	@Test public void testCreateEntityWithAttributeValuesOnEntity() throws GenstarException {
		GenstarCsvFile attributesFile = new GenstarCsvFile("test_data/ummisco/genstar/metamodel/synthetic_population/attributes.csv", true);
		
		SampleBasedGenerator generator = new SampleBasedGenerator("dummy generator");
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		IPopulation population = new Population(PopulationType.SYNTHETIC_POPULATION, "people", generator.getAttributes());
		
		AttributeValue ageValue0 = new UniqueValue(DataType.INTEGER, "0");
		Map<AbstractAttribute, AttributeValue> attributeValuesOnEntity = new HashMap<AbstractAttribute, AttributeValue>();
		attributeValuesOnEntity.put(generator.getAttributeByNameOnEntity("Age"), ageValue0);
		
		for (int i=0; i<10; i++) { population.createEntityWithAttributeValuesOnEntity(attributeValuesOnEntity); }
		
		assertTrue(population.getMatchingEntitiesByAttributeValuesOnEntity(attributeValuesOnEntity).size() == 10);
	}
	
	@Test public void testCreateEntityWithEntityAttributeValues() throws GenstarException {
		fail("not yet implemented");
	}
}
