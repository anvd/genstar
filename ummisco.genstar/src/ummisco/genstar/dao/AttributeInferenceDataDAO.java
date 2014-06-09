package ummisco.genstar.dao;

import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;

public interface AttributeInferenceDataDAO {

	public abstract void createInferenceData(final AttributeInferenceGenerationRule attributeInferenceGenerationRule) throws GenstarDAOException;
}
