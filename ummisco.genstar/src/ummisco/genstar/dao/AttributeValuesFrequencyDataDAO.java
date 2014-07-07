package ummisco.genstar.dao;

import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AttributeValuesFrequency;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;

public interface AttributeValuesFrequencyDataDAO {

	public abstract void createAttributeValuesFrequency(final AttributeValuesFrequency attributeValuesFrequency) throws GenstarDAOException;

	public abstract void populateAttributeValuesFrequency(final FrequencyDistributionGenerationRule rule,
			final int attributeValuesFrequencyID, final int frequency) throws GenstarDAOException;
}
