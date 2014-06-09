package ummisco.genstar.dao;

import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;

public interface FrequencyDistributionElementDAO {

	public abstract void createFrequencyDistributionElements(final FrequencyDistributionGenerationRule generationRule) throws GenstarDAOException;
}
