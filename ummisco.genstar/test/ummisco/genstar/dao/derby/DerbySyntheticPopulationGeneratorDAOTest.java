package ummisco.genstar.dao.derby;

import static org.junit.Assert.*;

import org.junit.Test;

import ummisco.genstar.dao.GenstarDAOFactory;
import ummisco.genstar.dao.SyntheticPopulationGeneratorDAO;
import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public class DerbySyntheticPopulationGeneratorDAOTest {
	
	private GenstarDAOFactory daoFactory = null;
	private SyntheticPopulationGeneratorDAO syntheticPopulationGeneratorDAO = null;

	
	public DerbySyntheticPopulationGeneratorDAOTest() throws GenstarDAOException {
		daoFactory = GenstarDAOFactory.getDAOFactory();
		syntheticPopulationGeneratorDAO = daoFactory.getSyntheticPopulationGeneratorDAO();
	}
	
//	@Test public void testCreateSyntheticPopulationGenerator1() throws GenstarException {
//		BondyData bondyData = new BondyData();
//		ISyntheticPopulationGenerator bondyHouseholdPopulationGenerator = bondyData.getHouseholdPopGenerator();
//		
//		//  assert that IDs not yet set
//		assertTrue(bondyHouseholdPopulationGenerator.getID() == -1);
//		for (AbstractAttribute a : bondyHouseholdPopulationGenerator.getAttributes()) { 
//			assertTrue(a.getAttributeID() == -1);
//			
//			for (AttributeValue v : a.values()) { assertTrue(v.getAttributeValueID() == -1); }
//		}
//		for (GenerationRule r : bondyHouseholdPopulationGenerator.getGenerationRules()) { assertTrue(r.getGenerationRuleID() == -1); }
//		
//		syntheticPopulationGeneratorDAO.createSyntheticPopulationGenerator(bondyHouseholdPopulationGenerator);
//		
//		//  assert that IDs correctly set
//		assertTrue(bondyHouseholdPopulationGenerator.getID() != -1);
//		for (AbstractAttribute a : bondyHouseholdPopulationGenerator.getAttributes()) {
//			assertTrue(a.getAttributeID() != -1);
//			
//			for (AttributeValue v : a.values()) { assertTrue(v.getAttributeValueID() != -1); }
//		}
//		
//		for (GenerationRule r : bondyHouseholdPopulationGenerator.getGenerationRules()) { assertTrue(r.getGenerationRuleID() != -1); }
//	}

	@Test public void testCreateSyntheticPopulationGenerator2() throws GenstarException {
		BondyData bondyData = new BondyData();
		ISyntheticPopulationGenerator bondyInhabitantPopulationGenerator = bondyData.getInhabitantPopGenerator();
		
		//  assert that IDs not yet set
		assertTrue(bondyInhabitantPopulationGenerator.getID() == -1);
		for (AbstractAttribute a : bondyInhabitantPopulationGenerator.getAttributes()) { 
			assertTrue(a.getAttributeID() == -1);
		
			for (AttributeValue v : a.values()) { assertTrue(v.getAttributeValueID() == -1); }
		}
		for (GenerationRule r : bondyInhabitantPopulationGenerator.getGenerationRules()) { assertTrue(r.getGenerationRuleID() == -1); }
		
		syntheticPopulationGeneratorDAO.createSyntheticPopulationGenerator(bondyInhabitantPopulationGenerator);
		
		//  assert that IDs correctly set
		assertTrue(bondyInhabitantPopulationGenerator.getID() != -1);
		for (AbstractAttribute a : bondyInhabitantPopulationGenerator.getAttributes()) { 
			assertTrue(a.getAttributeID() != -1);
		
			for (AttributeValue v : a.values()) { assertTrue(v.getAttributeValueID() != -1); }
		}
		for (GenerationRule r : bondyInhabitantPopulationGenerator.getGenerationRules()) { assertTrue(r.getGenerationRuleID() != -1); }
	}
}
