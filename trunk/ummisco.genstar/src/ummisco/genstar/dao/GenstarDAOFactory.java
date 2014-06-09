package ummisco.genstar.dao;

import ummisco.genstar.dao.derby.DerbyGenstarDAOFactory;
import ummisco.genstar.exception.GenstarDAOException;

public abstract class GenstarDAOFactory {

	public static GenstarDAOFactory getDAOFactory() {
		return DerbyGenstarDAOFactory.getInstance();
	}
	
	public abstract AttributeDAO getAttributeDAO() throws GenstarDAOException;
	
	public abstract AttributeInferenceGenerationRuleDAO getAttributeInferenceGenerationRuleDAO() throws GenstarDAOException;
	
	public abstract EntityAttributeDAO getEntityAttributeDAO() throws GenstarDAOException;
	
	public abstract EntityAttributeRangeValueDAO getEntityAttributeRangeValueDAO() throws GenstarDAOException;
	
	public abstract EntityAttributeUniqueValueDAO getEntityAttributeUniqueValueDAO() throws GenstarDAOException;
	
	public abstract EntityDAO getEntityDAO() throws GenstarDAOException;
	
	public abstract FrequencyDistributionElementDAO getFrequencyDistributionElementDAO() throws GenstarDAOException;
	
	public abstract FrequencyDistributionGenerationRuleDAO getFrequencyDistributionGenerationRuleDAO() throws GenstarDAOException;
	
	public abstract GenerationRuleDAO getGenerationRuleDAO() throws GenstarDAOException;
	
	public abstract AttributeInferenceDataDAO getAttributeInferenceDataDAO() throws GenstarDAOException;
	
	public abstract InputOutputAttributeDAO getInputOutputAttributeDAO() throws GenstarDAOException;
	
	public abstract SyntheticPopulationGeneratorDAO getSyntheticPopulationGeneratorDAO() throws GenstarDAOException;
	
	public abstract RangeValueDAO getRangeValueDAO() throws GenstarDAOException;
	
	public abstract SyntheticPopulationDAO getSyntheticPopulationDAO() throws GenstarDAOException;
	
	public abstract UniqueValueDAO getUniqueValueDAO() throws GenstarDAOException;
	
	
}
