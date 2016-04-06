package ummisco.genstar.ipf;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.util.AttributeUtils;
import ummisco.genstar.util.GenstarCSVFile;

public class GroupComponentSampleDataTest {

	@Test public void testBuildGroupComponentSampleEntities() throws GenstarException {
		
		// group generator
		GenstarCSVFile groupAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/group_component_sample_data/group_attributes.csv", true);
		ISingleRuleGenerator groupGenerator = new SingleRuleGenerator("group generator");
		AttributeUtils.createAttributesFromCSVFile(groupGenerator, groupAttributesFile);
		groupGenerator.setPopulationName("household");
		
		// component generator
		GenstarCSVFile componentAttributesFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/group_component_sample_data/component_attributes.csv", true);
		ISingleRuleGenerator componentGenerator = new SingleRuleGenerator("component generator");
		AttributeUtils.createAttributesFromCSVFile(componentGenerator, componentAttributesFile);
		componentGenerator.setPopulationName("people");
		
		// sample files
		GenstarCSVFile groupSampleFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/group_component_sample_data/group_sample.csv", true);
		GenstarCSVFile componentSampleFile = new GenstarCSVFile("test_data/ummisco/genstar/ipf/group_component_sample_data/component_sample.csv", true);
		
		// groupIdAttributeOnGroup, groupIdAttributeOnComponent
		AbstractAttribute groupIdAttributeOnGroup = groupGenerator.getAttributeByNameOnData("Household_ID");
		groupIdAttributeOnGroup.setIdentity(true);
		AbstractAttribute groupIdAttributeOnComponent = componentGenerator.getAttributeByNameOnData("Household_ID");
		groupIdAttributeOnComponent.setIdentity(true);
		
		ISampleData groupSampleData = new SampleData(groupGenerator.getPopulationName(), groupGenerator.getAttributes(), groupSampleFile);
		ISampleData componentSampleData = new SampleData(componentGenerator.getPopulationName(), componentGenerator.getAttributes(), componentSampleFile);
		
		ISampleData groupComponentSampleData = new GroupComponentSampleData(groupSampleData, componentSampleData, groupIdAttributeOnGroup, groupIdAttributeOnComponent);
		

		SampleEntityPopulation buildGroupEntityPopulation = groupComponentSampleData.getSampleEntityPopulation();
		List<SampleEntity> builtGroupEntities = buildGroupEntityPopulation.getSampleEntities();
		
		// verify that the number of sample entities in the built group population is equal to the number sample entities in the group population 
		assertTrue(builtGroupEntities.size() == groupSampleData.getSampleEntityPopulation().getSampleEntities().size());
		
		// verify that the number of sample entities in the built group population is "consistent" with the CSV sample file
		assertTrue(builtGroupEntities.size() == groupSampleFile.getRows() - 1);

		// verify that the number of people of households is consistent with "Household_ID" attribute value
		Map<String, AttributeValue> matchingCriteria = new HashMap<String, AttributeValue>();
		for (List<String> row : groupSampleFile.getContent()) {
			// Header: Household_ID,Household Size,Household Income,Household Type,Number Of Cars
			UniqueValue householdIdValue = new UniqueValue(DataType.INTEGER, row.get(0));			
			Integer householdSize = Integer.parseInt(row.get(1));
			
			matchingCriteria.put(groupIdAttributeOnGroup.getNameOnEntity(), householdIdValue);
			List<SampleEntity> matchingHouseholdEntities = buildGroupEntityPopulation.getMatchingEntities(matchingCriteria);
			
			assertTrue(matchingHouseholdEntities.size() == 1);
			
			SampleEntityPopulation peopleComponentPopulation = matchingHouseholdEntities.get(0).getComponentPopulation("people");
			assertTrue(peopleComponentPopulation.getSampleEntities().size() == householdSize);
		}
	}
}
