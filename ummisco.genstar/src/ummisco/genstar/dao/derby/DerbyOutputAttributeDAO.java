package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.OutputAttributeDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyOutputAttributeDAO extends AbstractDerbyDAO implements OutputAttributeDAO {

	public DerbyOutputAttributeDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

}
