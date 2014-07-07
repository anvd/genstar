package ummisco.genstar.dao;

import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.RangeValuesAttribute;

public interface RangeValueDAO {

	public abstract void createRangeValues(final RangeValuesAttribute rangeAttribute) throws GenstarDAOException;

	public abstract void populateRangeValues(final RangeValuesAttribute rangeValuesAttribute) throws GenstarDAOException;
}
