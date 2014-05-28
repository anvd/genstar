package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.InputAttributeDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyInputAttributeDAO extends AbstractDerbyDAO implements InputAttributeDAO {

	public DerbyInputAttributeDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

}
