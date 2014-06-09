package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.EntityAttributeDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyEntityAttributeDAO extends AbstractDerbyDAO implements EntityAttributeDAO {

	public DerbyEntityAttributeDAO(DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.ENTITY_ATTRIBUTE_TABLE.TABLE_NAME);
	}

}
