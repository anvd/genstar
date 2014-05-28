package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.UniqueValueDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyUniqueValueDAO extends AbstractDerbyDAO implements UniqueValueDAO {

	public DerbyUniqueValueDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

}
