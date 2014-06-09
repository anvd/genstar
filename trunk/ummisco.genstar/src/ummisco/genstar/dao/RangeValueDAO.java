package ummisco.genstar.dao;

import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.RangeValuesAttribute;
import ummisco.genstar.metamodel.RangeValue;

public interface RangeValueDAO {

	public abstract void createRangeValues(final RangeValuesAttribute rangeAttribute) throws GenstarDAOException;
}
