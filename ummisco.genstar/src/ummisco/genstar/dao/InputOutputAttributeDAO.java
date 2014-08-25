package ummisco.genstar.dao;

import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;

public interface InputOutputAttributeDAO {

	public abstract void createInputOutputAttributes(final FrequencyDistributionGenerationRule rule) throws GenstarDAOException;
	
	public abstract void updateInputOutputAttributes(final FrequencyDistributionGenerationRule rule) throws GenstarDAOException;

	public abstract void populateInputOutputAttributes(final FrequencyDistributionGenerationRule rule) throws GenstarDAOException;
}
