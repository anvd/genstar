package ummisco.genstar.dao;

import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.UniqueValuesAttribute;

public interface UniqueValueDAO {

	public abstract void createUniqueValues(final UniqueValuesAttribute uniqueValueAttribute) throws GenstarDAOException;
}
