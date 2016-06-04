package ummisco.genstar.metamodel.sample_data;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.sample_data.CompoundSampleData;
import ummisco.genstar.metamodel.sample_data.ISampleData;
import ummisco.genstar.metamodel.sample_data.SampleData;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCsvFile;

public class CompoundSampleDataTest {

	@Test public void testBuildGroupComponentSampleEntities() throws GenstarException {
		
		// group generator
		GenstarCsvFile groupAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/metamodel/sample_data/CompoundSampleData/group_attributes.csv", true);
		SampleBasedGenerator groupGenerator = new SampleBasedGenerator("group generator");
		AttributeUtils.createAttributesFromCsvFile(groupGenerator, groupAttributesFile);
		groupGenerator.setPopulationName("household");
		
		// component generator
		GenstarCsvFile componentAttributesFile = new GenstarCsvFile("test_data/ummisco/genstar/metamodel/sample_data/CompoundSampleData/component_attributes.csv", true);
		SampleBasedGenerator componentGenerator = new SampleBasedGenerator("component generator");
		AttributeUtils.createAttributesFromCsvFile(componentGenerator, componentAttributesFile);
		componentGenerator.setPopulationName("people");
		
		// sample files
		GenstarCsvFile groupSampleFile = new GenstarCsvFile("test_data/ummisco/genstar/metamodel/sample_data/CompoundSampleData/group_sample.csv", true);
		GenstarCsvFile componentSampleFile = new GenstarCsvFile("test_data/ummisco/genstar/metamodel/sample_data/CompoundSampleData/component_sample.csv", true);
		
		// groupIdAttributeOnGroup, groupIdAttributeOnComponent
		AbstractAttribute groupIdAttributeOnGroup = groupGenerator.getAttributeByNameOnData("Household_ID");
		AbstractAttribute groupIdAttributeOnComponent = componentGenerator.getAttributeByNameOnData("Household_ID");
		
		ISampleData groupSampleData = new SampleData(groupGenerator.getPopulationName(), groupGenerator.getAttributes(), groupSampleFile);
		ISampleData componentSampleData = new SampleData(componentGenerator.getPopulationName(), componentGenerator.getAttributes(), componentSampleFile);
		
		ISampleData compoundSampleData = new CompoundSampleData(groupSampleData, componentSampleData, groupIdAttributeOnGroup, groupIdAttributeOnComponent);
		

		IPopulation buildGroupEntityPopulation = compoundSampleData.getSampleEntityPopulation();
		List<Entity> builtGroupEntities = buildGroupEntityPopulation.getEntities();
		
		// verify that the number of sample entities in the built group population is equal to the number sample entities in the group population 
		assertTrue(builtGroupEntities.size() == groupSampleData.getSampleEntityPopulation().getEntities().size());
		
		// verify that the number of sample entities in the built group population is "consistent" with the CSV sample file
		assertTrue(builtGroupEntities.size() == groupSampleFile.getRows() - 1);

		// verify that the number of people of households is consistent with "Household_ID" attribute value
		Map<AbstractAttribute, AttributeValue> matchingCriteria = new HashMap<AbstractAttribute, AttributeValue>();
		for (List<String> row : groupSampleFile.getContent()) {
			// Header: Household_ID,Household Size,Household Income,Household Type,Number Of Cars
			UniqueValue householdIdValue = new UniqueValue(DataType.INTEGER, row.get(0));			
			Integer householdSize = Integer.parseInt(row.get(1));
			
			matchingCriteria.put(groupIdAttributeOnGroup, householdIdValue);
			List<Entity> matchingHouseholdEntities = buildGroupEntityPopulation.getMatchingEntitiesByAttributeValuesOnEntity(matchingCriteria);
			
			assertTrue(matchingHouseholdEntities.size() == 1);
			
			IPopulation peopleComponentPopulation = matchingHouseholdEntities.get(0).getComponentPopulation("people");
			assertTrue(peopleComponentPopulation.getEntities().size() == householdSize);
		}
	}
}
