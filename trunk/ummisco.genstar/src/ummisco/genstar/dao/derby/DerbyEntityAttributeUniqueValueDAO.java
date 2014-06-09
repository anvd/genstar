package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.EntityAttributeUniqueValueDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyEntityAttributeUniqueValueDAO extends AbstractDerbyDAO implements EntityAttributeUniqueValueDAO {

	public DerbyEntityAttributeUniqueValueDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.ENTITY_ATTRIBUTE_UNIQUE_VALUE_TABLE.TABLE_NAME);
	}

}
