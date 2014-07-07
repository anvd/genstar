package ummisco.genstar.dao;

import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;

public interface AttributeValuesFrequencyDAO {

	public abstract void createAttributeValuesFrequecies(final FrequencyDistributionGenerationRule generationRule) throws GenstarDAOException;

	public abstract void populateAttributeValuesFrequencies(final FrequencyDistributionGenerationRule rule) throws GenstarDAOException;
}
