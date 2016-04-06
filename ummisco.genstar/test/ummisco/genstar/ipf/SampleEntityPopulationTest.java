package ummisco.genstar.ipf;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCSVFile;

public class SampleEntityPopulationTest {

	@Rule public ExpectedException thrown = ExpectedException.none();
	
	@Test public void createSampleEntityPopulationWithValidParameters() throws GenstarException {
		new SampleEntityPopulation("dummy population", new ArrayList<AbstractAttribute>());
	}
	
	@Test(expected = GenstarException.class) public void createSampleEntityPopulationWithNullPopulationName() throws GenstarException {
		new SampleEntityPopulation(null, new ArrayList<AbstractAttribute>());
	}
	
	@Test(expected = GenstarException.class) public void createSampleEntityPopulationWithNullAttributes() throws GenstarException {
		new SampleEntityPopulation("dummy population", null);
	}
	
	@Test public void testGetMatchingEntities() throws GenstarException {
		
		ISyntheticPopulationGenerator generator = new SingleRuleGenerator("dummy generator");
		
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_entity/testSetValidAttributeValues/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		SampleEntityPopulation population = new SampleEntityPopulation("dummy population", generator.getAttributes());
		
		Map<String, AttributeValue> attributeValues1 = new HashMap<String, AttributeValue>();
		attributeValues1.put("householdSize", new UniqueValue(DataType.INTEGER, "1")); // Household Size
		attributeValues1.put("householdIncome", new UniqueValue(DataType.STRING, "High")); // Household Income
		population.createSampleEntity(attributeValues1);
		
		Map<String, AttributeValue> attributeValues2 = new HashMap<String, AttributeValue>();
		attributeValues2.put("householdSize", new UniqueValue(DataType.INTEGER, "1")); // Household Size
		attributeValues2.put("householdIncome", new UniqueValue(DataType.STRING, "Low")); // Household Income
		population.createSampleEntity(attributeValues2);
		
		
		Map<String, AttributeValue> criteria = new HashMap<String, AttributeValue>();
		criteria.put("householdSize", new UniqueValue(DataType.INTEGER, "0"));
		assertTrue(population.getMatchingEntities(criteria).isEmpty());

		criteria.put("householdSize", new UniqueValue(DataType.INTEGER, "1"));
		assertTrue(population.getMatchingEntities(criteria).size() == 2);

		criteria.put("householdIncome", new UniqueValue(DataType.STRING, "Low"));
		assertTrue(population.getMatchingEntities(criteria).size() == 1);
	}
	
	@Test public void testCreateSampleEntity() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SingleRuleGenerator("dummy generator");
		
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_entity/testSetValidAttributeValues/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		SampleEntityPopulation population = new SampleEntityPopulation("dummy population", generator.getAttributes());
		
		assertTrue(population.getNbOfEntities() == 0);

		Map<String, AttributeValue> attributeValues1 = new HashMap<String, AttributeValue>();
		attributeValues1.put("householdSize", new UniqueValue(DataType.INTEGER, "1")); // Household Size
		attributeValues1.put("householdIncome", new UniqueValue(DataType.STRING, "High")); // Household Income
		population.createSampleEntity(attributeValues1);
		
		assertTrue(population.getNbOfEntities() == 1);
		
		Map<String, AttributeValue> attributeValues2 = new HashMap<String, AttributeValue>();
		attributeValues2.put("householdSize", new UniqueValue(DataType.INTEGER, "1")); // Household Size
		attributeValues2.put("householdIncome", new UniqueValue(DataType.STRING, "Low")); // Household Income
		population.createSampleEntity(attributeValues2);		

		assertTrue(population.getNbOfEntities() == 2);
	}
	
	@Test public void testCreateSampleEntities() throws GenstarException {
		ISyntheticPopulationGenerator generator = new SingleRuleGenerator("dummy generator");
		
		GenstarCSVFile attributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/sample_entity/testSetValidAttributeValues/attributes.csv", true);
		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		SampleEntityPopulation population = new SampleEntityPopulation("dummy population", generator.getAttributes());
		
		assertTrue(population.getNbOfEntities() == 0);
		
		List<Map<String, AttributeValue>> attributeValues = new ArrayList<Map<String, AttributeValue>>();

		Map<String, AttributeValue> attributeValues1 = new HashMap<String, AttributeValue>();
		attributeValues1.put("householdSize", new UniqueValue(DataType.INTEGER, "1")); // Household Size
		attributeValues1.put("householdIncome", new UniqueValue(DataType.STRING, "High")); // Household Income
		attributeValues.add(attributeValues1);
		
		Map<String, AttributeValue> attributeValues2 = new HashMap<String, AttributeValue>();
		attributeValues2.put("householdSize", new UniqueValue(DataType.INTEGER, "1")); // Household Size
		attributeValues2.put("householdIncome", new UniqueValue(DataType.STRING, "Low")); // Household Income
		attributeValues.add(attributeValues2);

		population.createSampleEntities(attributeValues);		

		assertTrue(population.getNbOfEntities() == 2);
	}
}
