package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.EntityAttributeRangeValueDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyEntityAttributeRangeValueDAO extends AbstractDerbyDAO implements EntityAttributeRangeValueDAO {

	public DerbyEntityAttributeRangeValueDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.ENTITY_ATTRIBUTE_RANGE_VALUE_TABLE.TABLE_NAME);
	}

}
