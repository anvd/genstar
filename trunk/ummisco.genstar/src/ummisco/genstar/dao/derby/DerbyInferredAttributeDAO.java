package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.InferredAttributeDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyInferredAttributeDAO extends AbstractDerbyDAO implements InferredAttributeDAO {

	public DerbyInferredAttributeDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

}
