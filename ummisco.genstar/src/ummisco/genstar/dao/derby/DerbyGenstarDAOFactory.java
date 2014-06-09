package ummisco.genstar.dao.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import ummisco.genstar.dao.AttributeDAO;
import ummisco.genstar.dao.AttributeInferenceDataDAO;
import ummisco.genstar.dao.AttributeInferenceGenerationRuleDAO;
import ummisco.genstar.dao.FrequencyDistributionElementDAO;
import ummisco.genstar.dao.GenstarDAOFactory;
import ummisco.genstar.dao.EntityAttributeDAO;
import ummisco.genstar.dao.EntityAttributeRangeValueDAO;
import ummisco.genstar.dao.EntityAttributeUniqueValueDAO;
import ummisco.genstar.dao.EntityDAO;
import ummisco.genstar.dao.FrequencyDistributionGenerationRuleDAO;
import ummisco.genstar.dao.GenerationRuleDAO;
import ummisco.genstar.dao.InputOutputAttributeDAO;
import ummisco.genstar.dao.SyntheticPopulationGeneratorDAO;
import ummisco.genstar.dao.RangeValueDAO;
import ummisco.genstar.dao.SyntheticPopulationDAO;
import ummisco.genstar.dao.UniqueValueDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyGenstarDAOFactory extends GenstarDAOFactory {
	
	private static GenstarDAOFactory instance = null;
	
	private static Object lock = new Object();

	public static GenstarDAOFactory getInstance() {
		synchronized (lock) {
			if (instance == null) { instance = new DerbyGenstarDAOFactory(); }
		}
		
		return instance;
	}

	
	private static String DERBY_BASE_URL = "jdbc:derby:";
	
	private static String DATABASE_NAME = "genstar_db";
	
	private Connection connection = null;

	private Properties systemProperties = null;
	
	private AttributeDAO attributeDAO = null;
	
	private AttributeInferenceGenerationRuleDAO attributeInferenceGenerationRuleDAO = null;
	
	private AttributeInferenceDataDAO attributeInferenceDataDAO = null;
	
	private EntityAttributeDAO entityAttributeDAO = null;
	
	private EntityAttributeRangeValueDAO entityAttributeRangeValueDAO = null;
	
	private EntityAttributeUniqueValueDAO entityAttributeUniqueValueDAO = null;
	
	private EntityDAO entityDAO = null;
	
	private FrequencyDistributionElementDAO frequencyDistributionElementDAO = null;
	
	private FrequencyDistributionGenerationRuleDAO frequencyDistributionGenerationRuleDAO = null; 
	
	private GenerationRuleDAO generationRuleDAO = null;
	
	private InputOutputAttributeDAO inputOutputAttributeDAO = null;
	
	private SyntheticPopulationGeneratorDAO populationGeneratorDAO = null;
	
	private RangeValueDAO rangeValueDAO = null;
	
	private SyntheticPopulationDAO syntheticPopulationDAO = null;
	
	private UniqueValueDAO uniqueValueDAO = null;
	
	
	private DerbyGenstarDAOFactory() {
		systemProperties = System.getProperties();
		systemProperties.put("derby.system.home", "./dbms");
	}

	public synchronized Connection getConnection() throws GenstarDAOException {
		if (connection == null) {
			try {
				connection = DriverManager.getConnection(DERBY_BASE_URL + DATABASE_NAME + ";create=true", systemProperties);
			} catch (SQLException e) {
				e.printStackTrace();
				throw new GenstarDAOException(e);
			}
		}
		
		return connection;
	}

	@Override
	public AttributeDAO getAttributeDAO() throws GenstarDAOException {
		if (attributeDAO == null) { attributeDAO = new DerbyAttributeDAO(this); }
		
		return attributeDAO;
	}

	@Override
	public AttributeInferenceGenerationRuleDAO getAttributeInferenceGenerationRuleDAO() throws GenstarDAOException {
		if (attributeInferenceGenerationRuleDAO == null) { attributeInferenceGenerationRuleDAO = new DerbyAttributeInferenceGenerationRuleDAO(this); }
		
		return attributeInferenceGenerationRuleDAO;
	}

	@Override
	public EntityAttributeDAO getEntityAttributeDAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return entityAttributeDAO;
	}

	@Override
	public EntityAttributeRangeValueDAO getEntityAttributeRangeValueDAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return entityAttributeRangeValueDAO;
	}

	@Override
	public EntityAttributeUniqueValueDAO getEntityAttributeUniqueValueDAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return entityAttributeUniqueValueDAO;
	}

	@Override
	public EntityDAO getEntityDAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return entityDAO;
	}

	@Override
	public FrequencyDistributionGenerationRuleDAO getFrequencyDistributionGenerationRuleDAO() throws GenstarDAOException {
		if (frequencyDistributionGenerationRuleDAO == null) { frequencyDistributionGenerationRuleDAO = new DerbyFrequencyDistributionGenerationRuleDAO(this); }
		
		return frequencyDistributionGenerationRuleDAO;
	}

	@Override
	public GenerationRuleDAO getGenerationRuleDAO() throws GenstarDAOException {
		if (generationRuleDAO == null) { generationRuleDAO = new DerbyGenerationRuleDAO(this); }
		
		return generationRuleDAO;
	}

	@Override
	public InputOutputAttributeDAO getInputOutputAttributeDAO() throws GenstarDAOException {
		if (inputOutputAttributeDAO == null) { inputOutputAttributeDAO = new DerbyInputOutputAttributeDAO(this); }
		
		return inputOutputAttributeDAO;
	}


	@Override
	public SyntheticPopulationGeneratorDAO getSyntheticPopulationGeneratorDAO() throws GenstarDAOException {
		synchronized (this) {
			if (populationGeneratorDAO == null) { populationGeneratorDAO = new DerbySyntheticPopulationGeneratorDAO(this); }
		}
		
		return populationGeneratorDAO;
	}

	@Override
	public RangeValueDAO getRangeValueDAO() throws GenstarDAOException {
		if (rangeValueDAO == null) { rangeValueDAO = new DerbyRangeValueDAO(this); }
		
		return rangeValueDAO;
	}

	@Override
	public SyntheticPopulationDAO getSyntheticPopulationDAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return syntheticPopulationDAO;
	}

	@Override
	public UniqueValueDAO getUniqueValueDAO() throws GenstarDAOException {
		if (uniqueValueDAO == null) { uniqueValueDAO = new DerbyUniqueValueDAO(this); }
		
		return uniqueValueDAO;
	}

	@Override
	public AttributeInferenceDataDAO getAttributeInferenceDataDAO() throws GenstarDAOException {
		if (attributeInferenceDataDAO == null) { attributeInferenceDataDAO = new DerbyAttributeInferenceDataDAO(this); }
		
		return attributeInferenceDataDAO;
	}

	@Override
	public FrequencyDistributionElementDAO getFrequencyDistributionElementDAO() throws GenstarDAOException {
		if (frequencyDistributionElementDAO == null) { frequencyDistributionElementDAO = new DerbyFrequencyDistributionElementDAO(this); }
		
		return frequencyDistributionElementDAO;
	}
}
