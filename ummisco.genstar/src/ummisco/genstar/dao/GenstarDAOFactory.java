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
	
	public abstract EnumerationValueAttributeDAO getEnumerationValueAttributeDAO() throws GenstarDAOException;
	
	public abstract FrequencyDistributionGenerationRuleDAO getFrequencyDistributionGenerationRuleDAO() throws GenstarDAOException;
	
	public abstract GenerationRuleDAO getGenerationRuleDAO() throws GenstarDAOException;
	
	public abstract InferenceRangeAttributeData1DAO getInferenceRangeAttributeData1DAO() throws GenstarDAOException;
	
	public abstract InferenceRangeAttributeData2DAO getInferenceRangeAttributeData2DAO() throws GenstarDAOException;
	
	public abstract InferenceValueAttributeData1DAO getInferenceValueAttributeData1DAO() throws GenstarDAOException;
	
	public abstract InferenceValueAttributeData2DAO getInferenceValueAttributeData2DAO() throws GenstarDAOException;
	
	public abstract InferredAttributeDAO getInferredAttributeDAO() throws GenstarDAOException;
	
	public abstract InputAttributeDAO getInputAttributeDAO() throws GenstarDAOException;
	
	public abstract OutputAttributeDAO getOutputAttributeDAO() throws GenstarDAOException;
	
	public abstract SyntheticPopulationGeneratorDAO getSyntheticPopulationGeneratorDAO() throws GenstarDAOException;
	
	public abstract RangeValueDAO getRangeValueDAO() throws GenstarDAOException;
	
	public abstract SyntheticPopulationDAO getSyntheticPopulationDAO() throws GenstarDAOException;
	
	public abstract UniqueValueDAO getUniqueValueDAO() throws GenstarDAOException;
	
	
}
