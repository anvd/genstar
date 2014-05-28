package ummisco.genstar.dao.derby;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import ummisco.genstar.dao.AttributeDAO;
import ummisco.genstar.dao.AttributeInferenceGenerationRuleDAO;
import ummisco.genstar.dao.GenstarDAOFactory;
import ummisco.genstar.dao.EntityAttributeDAO;
import ummisco.genstar.dao.EntityAttributeRangeValueDAO;
import ummisco.genstar.dao.EntityAttributeUniqueValueDAO;
import ummisco.genstar.dao.EntityDAO;
import ummisco.genstar.dao.EnumerationValueAttributeDAO;
import ummisco.genstar.dao.FrequencyDistributionGenerationRuleDAO;
import ummisco.genstar.dao.GenerationRuleDAO;
import ummisco.genstar.dao.InferenceRangeAttributeData1DAO;
import ummisco.genstar.dao.InferenceRangeAttributeData2DAO;
import ummisco.genstar.dao.InferenceValueAttributeData1DAO;
import ummisco.genstar.dao.InferenceValueAttributeData2DAO;
import ummisco.genstar.dao.InferredAttributeDAO;
import ummisco.genstar.dao.InputAttributeDAO;
import ummisco.genstar.dao.OutputAttributeDAO;
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
	
	private static String DATABASE_NAME = "./dbms/genstar_db";
	
	private Connection connection = null;

	private Properties systemProperties = null;
	
	private AttributeDAO attributeDAO = null;
	
	private AttributeInferenceGenerationRuleDAO attributeInferenceGenerationRuleDAO = null;
	
	private EntityAttributeDAO entityAttributeDAO = null;
	
	private EntityAttributeRangeValueDAO entityAttributeRangeValueDAO = null;
	
	private EntityAttributeUniqueValueDAO entityAttributeUniqueValueDAO = null;
	
	private EntityDAO entityDAO = null;
	
	private EnumerationValueAttributeDAO enumerationValueAttributeDAO = null;
	
	private FrequencyDistributionGenerationRuleDAO frequencyDistributionGenerationRuleDAO = null; 
	
	private GenerationRuleDAO generationRuleDAO = null;
	
	private InferenceRangeAttributeData1DAO inferenceRangeAttributeData1DAO = null;
	
	private InferenceRangeAttributeData2DAO inferenceRangeAttributeData2DAO = null;
	
	private InferenceValueAttributeData1DAO inferenceValueAttributeData1DAO = null;
	
	private InferenceValueAttributeData2DAO inferenceValueAttributeData2DAO = null;
	
	private InferredAttributeDAO inferredAttributeDAO = null;
	
	private InputAttributeDAO inputAttributeDAO = null;
	
	private OutputAttributeDAO outputAttributeDAO = null;
	
	private SyntheticPopulationGeneratorDAO populationGeneratorDAO = null;
	
	private RangeValueDAO rangeValueDAO = null;
	
	private SyntheticPopulationDAO syntheticPopulationDAO = null;
	
	private UniqueValueDAO uniqueValueDAO = null;
	
	
	private DerbyGenstarDAOFactory() {
		systemProperties = System.getProperties();
		systemProperties.put("derby.system.home", "./lib");
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
		// TODO Auto-generated method stub
		
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
	public EnumerationValueAttributeDAO getEnumerationValueAttributeDAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return enumerationValueAttributeDAO;
	}

	@Override
	public FrequencyDistributionGenerationRuleDAO getFrequencyDistributionGenerationRuleDAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return frequencyDistributionGenerationRuleDAO;
	}

	@Override
	public GenerationRuleDAO getGenerationRuleDAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return generationRuleDAO;
	}

	@Override
	public InferenceRangeAttributeData1DAO getInferenceRangeAttributeData1DAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return inferenceRangeAttributeData1DAO;
	}

	@Override
	public InferenceRangeAttributeData2DAO getInferenceRangeAttributeData2DAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return inferenceRangeAttributeData2DAO;
	}

	@Override
	public InferenceValueAttributeData1DAO getInferenceValueAttributeData1DAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return inferenceValueAttributeData1DAO;
	}

	@Override
	public InferenceValueAttributeData2DAO getInferenceValueAttributeData2DAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return inferenceValueAttributeData2DAO;
	}

	@Override
	public InferredAttributeDAO getInferredAttributeDAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return inferredAttributeDAO;
	}

	@Override
	public InputAttributeDAO getInputAttributeDAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return inputAttributeDAO;
	}

	@Override
	public OutputAttributeDAO getOutputAttributeDAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return outputAttributeDAO;
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
		// TODO Auto-generated method stub
		
		return rangeValueDAO;
	}

	@Override
	public SyntheticPopulationDAO getSyntheticPopulationDAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return syntheticPopulationDAO;
	}

	@Override
	public UniqueValueDAO getUniqueValueDAO() throws GenstarDAOException {
		// TODO Auto-generated method stub
		
		return uniqueValueDAO;
	}
}
