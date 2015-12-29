package ummisco.genstar.ipf;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarFactoryUtils;
import mockit.Mocked;
import mockit.integration.junit4.JMockit;

@RunWith(JMockit.class)
public class SampleEntityTest {
	
	@Rule public ExpectedException thrown = ExpectedException.none();
	

	@Test public void initializeSampleEntity(@Mocked final SampleEntityPopulation population) throws GenstarException {
		new SampleEntity(population);
	}
	
	@Test(expected = GenstarException.class) public void initializeSampleEntityUnsuccessfully() throws GenstarException {
		new SampleEntity(null);
	}
	
	@Test public void testSetValidAttributeValues() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SingleRuleGenerator("dummy generator");
		
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_entity/testSetValidAttributeValues/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		SampleEntityPopulation population = new SampleEntityPopulation("dummy population", generator.getAttributes());
		
		SampleEntity entity = new SampleEntity(population);
		
		assertTrue(entity.getAttributeValuesOnEntity().size() == 0);
		assertTrue(entity.getAttributeValueOnEntity("numberOfCars") == null);
		
		// Number Of Cars
		Map<String, AttributeValue> attributeValues = new HashMap<String, AttributeValue>();
		attributeValues.put("numberOfCars", new UniqueValue(DataType.INTEGER, "0"));
		entity.setAttributeValuesOnEntity(attributeValues);
		
		assertTrue(entity.getAttributeValueOnEntity("numberOfCars") != null);
	}
	
	
	@Test public void testSetNullAttributeValues() throws GenstarException {
		
		ISyntheticPopulationGenerator generator = new SingleRuleGenerator("dummy generator");
		
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_entity/testSetValidAttributeValues/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		SampleEntityPopulation population = new SampleEntityPopulation("dummy population", generator.getAttributes());
		
		SampleEntity entity = new SampleEntity(population);
		
		thrown.expect(GenstarException.class);
		entity.setAttributeValuesOnEntity(null);
	}
	
	@Test public void testSetNotRecognizedAttributeValues() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SingleRuleGenerator("dummy generator");
		
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_entity/testSetValidAttributeValues/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		SampleEntityPopulation population = new SampleEntityPopulation("dummy population", generator.getAttributes());
		
		SampleEntity entity = new SampleEntity(population);

		Map<String, AttributeValue> values = new HashMap<String, AttributeValue>();
		values.put("non existing attribute", new UniqueValue(DataType.INTEGER, "1"));
		
		thrown.expect(GenstarException.class);
		entity.setAttributeValuesOnEntity(values);
	}
	
	
	@Test public void testIsMatched() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SingleRuleGenerator("dummy generator");
		
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_entity/testSetValidAttributeValues/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		SampleEntityPopulation population = new SampleEntityPopulation("dummy population", generator.getAttributes());
		
		SampleEntity entity = new SampleEntity(population);
		
		Map<String, AttributeValue> attributeValues = new HashMap<String, AttributeValue>();
		attributeValues.put("numberOfCars", new UniqueValue(DataType.INTEGER, "0")); // Number Of Cars
		attributeValues.put("householdType", new UniqueValue(DataType.STRING, "type1")); // Household Type
		attributeValues.put("householdIncome", new UniqueValue(DataType.STRING, "High")); // Household Income

		entity.setAttributeValuesOnEntity(attributeValues);
		
		assertTrue(entity.isMatched(null));
		assertTrue(entity.isMatched(new HashMap<String, AttributeValue>()));
		
		Map<String, AttributeValue> criteria1 = new HashMap<String, AttributeValue>();
		criteria1.put("numberOfCars", new UniqueValue(DataType.INTEGER, "1")); // Number Of Cars
		assertFalse(entity.isMatched(criteria1));
		
		criteria1.put("numberOfCars", new UniqueValue(DataType.INTEGER, "0")); // Number Of Cars
		assertTrue(entity.isMatched(criteria1));
		
		criteria1.put("householdType", new UniqueValue(DataType.STRING, "type1")); // Household Type
		assertTrue(entity.isMatched(criteria1));
		
		criteria1.put("householdIncome", new UniqueValue(DataType.STRING, "Low")); // Household Income
		assertFalse(entity.isMatched(criteria1));

		criteria1.put("householdIncome", new UniqueValue(DataType.STRING, "High")); // Household Income
		assertTrue(entity.isMatched(criteria1));
		
	}
	
	@Test public void testCreateComponentPopulation() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SingleRuleGenerator("dummy generator");
		
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_entity/testSetValidAttributeValues/attributes.csv", true);
		GenstarFactoryUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		SampleEntityPopulation population = new SampleEntityPopulation("dummy population", generator.getAttributes());
		
		SampleEntity entity = new SampleEntity(population);
		
		String componentPopulationName = "component population";
		
		assertNull(entity.getComponentPopulation(componentPopulationName));
		
		List<AbstractAttribute> componentAttributes = generator.getAttributes();
		entity.createComponentPopulation(componentPopulationName, componentAttributes);
		SampleEntityPopulation componentPopulation = entity.getComponentPopulation(componentPopulationName);
		
		assertTrue(componentPopulation.getName().equals(componentPopulationName));
		assertTrue(componentPopulation.getAttributes().size() == componentAttributes.size());
	}
	
}
