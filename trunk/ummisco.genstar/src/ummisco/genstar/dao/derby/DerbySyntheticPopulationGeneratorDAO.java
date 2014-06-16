package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ummisco.genstar.dao.AttributeDAO;
import ummisco.genstar.dao.GenerationRuleDAO;
import ummisco.genstar.dao.SyntheticPopulationGeneratorDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.SYNTHETIC_POPULATION_GENERATOR_TABLE;
import ummisco.genstar.data.BondyData;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public class DerbySyntheticPopulationGeneratorDAO extends AbstractDerbyDAO implements SyntheticPopulationGeneratorDAO {
	
	private PreparedStatement findSyntheticPopulationGeneratorByNameStmt;
	private PreparedStatement createSyntheticPopulationGeneratorStmt;
	
	
	private AttributeDAO attributeDAO;
	private GenerationRuleDAO generationRuleDAO;
	

	public DerbySyntheticPopulationGeneratorDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.SYNTHETIC_POPULATION_GENERATOR_TABLE.TABLE_NAME);
		
		try {
			findSyntheticPopulationGeneratorByNameStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ SYNTHETIC_POPULATION_GENERATOR_TABLE.NAME_COLUMN_NAME + " = ?");
			
			createSyntheticPopulationGeneratorStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " (" + SYNTHETIC_POPULATION_GENERATOR_TABLE.NAME_COLUMN_NAME + ", " 
					+ SYNTHETIC_POPULATION_GENERATOR_TABLE.INITIAL_NUMBER_OF_ENTITIES_COLUMN_NAME + ") VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			
			attributeDAO = daoFactory.getAttributeDAO();
			generationRuleDAO = daoFactory.getGenerationRuleDAO();
		} catch (final SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public ISyntheticPopulationGenerator findSyntheticPopulationGeneratorByName(final String populationGeneratorName) throws GenstarDAOException {
		
		// mockup for testing
		try {
			BondyData bondyData = new BondyData();
			return bondyData.getHouseholdPopGenerator();
		} catch (GenstarException e) {
			throw new GenstarDAOException(e);
		}
		
		
		
		// TODO make a "real" implementation
		
	}

	@Override
	public ISyntheticPopulationGenerator findSyntheticPopulationGeneratorByID(final int populationGeneratorID) throws GenstarDAOException {
		
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void createSyntheticPopulationGenerator(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException {
		try {
			connection.setAutoCommit(false);
			
			createSyntheticPopulationGeneratorStmt.setString(1, syntheticPopulationGenerator.getName());
			createSyntheticPopulationGeneratorStmt.setInt(2, syntheticPopulationGenerator.getNbOfEntities());
			createSyntheticPopulationGeneratorStmt.executeUpdate();
			
			// retrieve the ID of the newly created syntheticPopulationGenerator
			ResultSet generatedKeySet = createSyntheticPopulationGeneratorStmt.getGeneratedKeys();
			if (generatedKeySet.next()) { syntheticPopulationGenerator.setID(generatedKeySet.getInt(1)); }
			generatedKeySet.close();
			generatedKeySet = null;
			
			
			attributeDAO.createAttributes(syntheticPopulationGenerator); // 1. create attributes
			generationRuleDAO.createGenerationRules(syntheticPopulationGenerator); // 2. create generation rules
			
			
			connection.commit();
			
			// TODO fire event?
			
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				throw new GenstarDAOException(e);
			}
		}
	}

	@Override
	public void updateSyntheticPopulationGenerator(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void deleteSyntheticPopulationGenerator(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void deleteSyntheticPopulationGenerator(final int populationGeneratorID) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
