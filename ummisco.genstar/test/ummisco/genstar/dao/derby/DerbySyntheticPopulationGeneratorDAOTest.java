package ummisco.genstar.dao.derby;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ummisco.genstar.dao.GenstarDAOFactory;
import ummisco.genstar.dao.SyntheticPopulationGeneratorDAO;
import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.DataType;
import ummisco.genstar.metamodel.AttributeValuesFrequency;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.IMultipleRulesGenerator;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.RangeValue;
import ummisco.genstar.metamodel.SyntheticPopulationGenerator;
import ummisco.genstar.metamodel.UniqueValue;

public class DerbySyntheticPopulationGeneratorDAOTest {
	
	private GenstarDAOFactory daoFactory = null;
	private SyntheticPopulationGeneratorDAO syntheticPopulationGeneratorDAO = null;

	
	public DerbySyntheticPopulationGeneratorDAOTest() throws GenstarDAOException {
		daoFactory = GenstarDAOFactory.getDAOFactory();
		syntheticPopulationGeneratorDAO = daoFactory.getSyntheticPopulationGeneratorDAO();
	}
	
	@BeforeClass public static void setup() throws GenstarDAOException {
		DatabaseResetter.resetDatabase();
	}
	
	@AfterClass public static void tearDown() throws GenstarDAOException {
		DatabaseResetter.resetDatabase();
	}
	
	@Test public void testCreateSyntheticPopulationGenerator() throws GenstarException {
		BondyData bondyData = new BondyData();
		IMultipleRulesGenerator bondyHouseholdPopulationGenerator = bondyData.getHouseholdPopGenerator();
		
		//  assert that IDs not yet set
		assertTrue(bondyHouseholdPopulationGenerator.getID() == -1);
		for (AbstractAttribute a : bondyHouseholdPopulationGenerator.getAttributes()) { 
			assertTrue(a.getAttributeID() == -1);
			
			for (AttributeValue v : a.values()) { assertTrue(v.getAttributeValueID() == -1); }
		}
		for (GenerationRule r : bondyHouseholdPopulationGenerator.getGenerationRules()) { assertTrue(r.getGenerationRuleID() == -1); }
		
		syntheticPopulationGeneratorDAO.createSyntheticPopulationGenerator(bondyHouseholdPopulationGenerator);
		
		//  assert that IDs correctly set
		assertTrue(bondyHouseholdPopulationGenerator.getID() != -1);
		for (AbstractAttribute a : bondyHouseholdPopulationGenerator.getAttributes()) {
			assertTrue(a.getAttributeID() != -1);
			
			for (AttributeValue v : a.values()) { assertTrue(v.getAttributeValueID() != -1); }
		}
		
		for (GenerationRule r : bondyHouseholdPopulationGenerator.getGenerationRules()) { assertTrue(r.getGenerationRuleID() != -1); }
		
		syntheticPopulationGeneratorDAO.deleteSyntheticPopulationGenerator(bondyHouseholdPopulationGenerator);
	}


	
//	@Test public void testFindSyntheticPopulationGeneratorByName() throws GenstarException {
//		BondyData bondyData = new BondyData();
//		syntheticPopulationGeneratorDAO.createSyntheticPopulationGenerator(bondyData.getInhabitantPopGenerator());
//		
//		IMultipleRulesGenerator bondyInhabitantPopulationGenerator = syntheticPopulationGeneratorDAO.findSyntheticPopulationGeneratorByName(bondyData.getInhabitantPopGenerator().getGeneratorName());
//		
//		//  assert that IDs correctly set
//		assertTrue(bondyInhabitantPopulationGenerator.getID() != -1);
//		for (AbstractAttribute a : bondyInhabitantPopulationGenerator.getAttributes()) { 
//			assertTrue(a.getAttributeID() != -1);
//		
//			for (AttributeValue v : a.values()) { assertTrue(v.getAttributeValueID() != -1); }
//		}
//		for (GenerationRule r : bondyInhabitantPopulationGenerator.getGenerationRules()) { 
//			assertTrue(r.getGenerationRuleID() != -1);
//			
//			if (r.getName().equals(BondyData.RULE1_NAME)) {
//				
//				AbstractAttribute ageRangeAttr = r.findAttributeByNameOnData("age_range_1");
//				AbstractAttribute sexAttr = r.findAttributeByNameOnData("sex");
//				Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
//				AttributeValue sexAttrValue;
//				for (int[] range : BondyData.age_ranges_1) {
//					attributeValues.clear();
//					
//					attributeValues.put(ageRangeAttr, new RangeValue(DataType.INTEGER, Integer.toString(range[0]), Integer.toString(range[1])));
//					sexAttrValue = new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[1]));
//					attributeValues.put(sexAttr, sexAttrValue);
//					
//					List<AttributeValuesFrequency> frequencies = ((FrequencyDistributionGenerationRule) r).findAttributeValuesFrequencies(attributeValues);
//					
//					assertTrue(frequencies.size() == 1);
//					
//					AttributeValuesFrequency found = frequencies.get(0);
//					
//					// id
//					assertTrue(found.getID() != -1);
//
//					// "male" frequency
//					assertTrue(found.getFrequency() == range[2]);
//					
//					// "female" frequency
//					attributeValues.put(sexAttr, new UniqueValue(DataType.BOOL, Boolean.toString(BondyData.sexes[0])));
//					frequencies = ((FrequencyDistributionGenerationRule) r).findAttributeValuesFrequencies(attributeValues);
//					assertTrue(frequencies.size() == 1);
//					found = frequencies.get(0);
//					assertTrue(found.getFrequency() == range[3]);
//				}
//			}
//
//			if (r.getName().equals(BondyData.RULE2_NAME)) {
//				// TODO implement it
//				
//			}
//
//			if (r.getName().equals(BondyData.RULE3_NAME)) {
//				// TODO implement it
//			}
//		}
//		
//		syntheticPopulationGeneratorDAO.deleteSyntheticPopulationGenerator(bondyInhabitantPopulationGenerator);
//	}
	
	@Test public void testDeleteSyntheticPopulationGenerator() throws GenstarException {
		BondyData bondyData = new BondyData();
		ISyntheticPopulationGenerator generator = bondyData.getHouseholdPopGenerator();
		syntheticPopulationGeneratorDAO.createSyntheticPopulationGenerator(generator);
		
		assertTrue(generator.getID() != -1);
		
		syntheticPopulationGeneratorDAO.deleteSyntheticPopulationGenerator(generator);
		
		generator = syntheticPopulationGeneratorDAO.findSyntheticPopulationGeneratorByName(bondyData.getHouseholdPopGenerator().getGeneratorName());
		assertTrue(generator == null);

	}
}
