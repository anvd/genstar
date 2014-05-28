package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.RangeValueDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyRangeValueDAO extends AbstractDerbyDAO implements RangeValueDAO {

	public DerbyRangeValueDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

}
