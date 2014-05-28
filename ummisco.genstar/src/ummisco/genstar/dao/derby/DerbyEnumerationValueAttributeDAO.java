package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.EnumerationValueAttributeDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyEnumerationValueAttributeDAO extends AbstractDerbyDAO implements EnumerationValueAttributeDAO {

	public DerbyEnumerationValueAttributeDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

}
