package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.EntityDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyEntityDAO extends AbstractDerbyDAO implements EntityDAO {

	public DerbyEntityDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.ENTITY_TABLE.TABLE_NAME);
	}

}
