package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ummisco.genstar.dao.AttributeDAO;
import ummisco.genstar.dao.GenerationRuleDAO;
import ummisco.genstar.dao.SyntheticPopulationGeneratorDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.SYNTHETIC_POPULATION_GENERATOR_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SyntheticPopulationGenerator;

public class DerbySyntheticPopulationGeneratorDAO extends AbstractDerbyDAO implements SyntheticPopulationGeneratorDAO {
	
	private PreparedStatement findSyntheticPopulationGeneratorByNameStmt;
	private PreparedStatement createSyntheticPopulationGeneratorStmt;
	private PreparedStatement deleteSyntheticPopulationGeneratorStmt;
	private PreparedStatement updateSyntheticPopulationGeneratorStmt;
	
	
	private AttributeDAO attributeDAO;
	private GenerationRuleDAO generationRuleDAO;
	

	public DerbySyntheticPopulationGeneratorDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.SYNTHETIC_POPULATION_GENERATOR_TABLE.TABLE_NAME);
		
		try {
			findSyntheticPopulationGeneratorByNameStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ SYNTHETIC_POPULATION_GENERATOR_TABLE.NAME_COLUMN_NAME + " = ?");
			
			createSyntheticPopulationGeneratorStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " (" + SYNTHETIC_POPULATION_GENERATOR_TABLE.NAME_COLUMN_NAME + ", " 
					+ SYNTHETIC_POPULATION_GENERATOR_TABLE.INITIAL_NUMBER_OF_ENTITIES_COLUMN_NAME + ") VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			deleteSyntheticPopulationGeneratorStmt = connection.prepareStatement("DELETE FROM " + TABLE_NAME + " WHERE " + SYNTHETIC_POPULATION_GENERATOR_TABLE.POPULATION_GENERATOR_ID_COLUMN_NAME + " = ?");
			
			updateSyntheticPopulationGeneratorStmt = connection.prepareStatement("UPDATE " + TABLE_NAME + " SET " + SYNTHETIC_POPULATION_GENERATOR_TABLE.NAME_COLUMN_NAME + " = ?, "
					+ SYNTHETIC_POPULATION_GENERATOR_TABLE.INITIAL_NUMBER_OF_ENTITIES_COLUMN_NAME + " = ? WHERE "
					+ SYNTHETIC_POPULATION_GENERATOR_TABLE.POPULATION_GENERATOR_ID_COLUMN_NAME + " = ?");
			
			
			attributeDAO = daoFactory.getAttributeDAO();
			generationRuleDAO = daoFactory.getGenerationRuleDAO();
		} catch (final SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public ISyntheticPopulationGenerator findSyntheticPopulationGeneratorByName(final String populationGeneratorName) throws GenstarDAOException {
		if (populationGeneratorName == null || populationGeneratorName.trim().length() == 0) { return null; }
		
		ISyntheticPopulationGenerator populationGenerator = null;
		try {
			findSyntheticPopulationGeneratorByNameStmt.setString(1, populationGeneratorName);
			ResultSet resultSet = findSyntheticPopulationGeneratorByNameStmt.executeQuery();
			if (resultSet.next()) {
				String name = resultSet.getString(SYNTHETIC_POPULATION_GENERATOR_TABLE.NAME_COLUMN_NAME);
				int nbOfEntities = resultSet.getInt(SYNTHETIC_POPULATION_GENERATOR_TABLE.INITIAL_NUMBER_OF_ENTITIES_COLUMN_NAME);
				int id = resultSet.getInt(SYNTHETIC_POPULATION_GENERATOR_TABLE.POPULATION_GENERATOR_ID_COLUMN_NAME);
						
				populationGenerator = new SyntheticPopulationGenerator(name, nbOfEntities);
				populationGenerator.setID(id);
				
				resultSet.close();
				resultSet = null;
				
				// populate attributes & generation rules
				attributeDAO.populateAttributes(populationGenerator);
				generationRuleDAO.populateGenerationRules(populationGenerator);
			}
		} catch (Exception e) {
			if (e instanceof GenstarDAOException) { throw (GenstarDAOException)e; }
			throw new GenstarDAOException(e);
		}
		
		return populationGenerator;
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
			
		} catch (final Exception e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new GenstarDAOException(e1);
			}
			
			if (e instanceof GenstarDAOException) { throw (GenstarDAOException)e; }
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
		try {
			connection.setAutoCommit(false);
			
			updateSyntheticPopulationGeneratorStmt.setString(1, syntheticPopulationGenerator.getName());
			updateSyntheticPopulationGeneratorStmt.setInt(2, syntheticPopulationGenerator.getNbOfEntities());
			updateSyntheticPopulationGeneratorStmt.setInt(3, syntheticPopulationGenerator.getID());
			updateSyntheticPopulationGeneratorStmt.executeUpdate();
			
			// 1. update attributes
			attributeDAO.updateAttributes(syntheticPopulationGenerator);
			
			// 2. update generation rules
			generationRuleDAO.updateGenerationRules(syntheticPopulationGenerator);
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
	public void deleteSyntheticPopulationGenerator(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException {
		try {
			deleteSyntheticPopulationGeneratorStmt.setInt(1, syntheticPopulationGenerator.getID());
			deleteSyntheticPopulationGeneratorStmt.executeUpdate();
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

}
