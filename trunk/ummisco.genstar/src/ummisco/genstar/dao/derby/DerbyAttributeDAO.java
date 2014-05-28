package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.AttributeDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyAttributeDAO extends AbstractDerbyDAO implements AttributeDAO {
	
	public DerbyAttributeDAO(final DerbyGenstarDAOFactory derbyDAOFactory) throws GenstarDAOException {
		super(derbyDAOFactory);
	}

}
