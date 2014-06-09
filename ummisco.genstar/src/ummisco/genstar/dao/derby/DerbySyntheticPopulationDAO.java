package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.SyntheticPopulationDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbySyntheticPopulationDAO extends AbstractDerbyDAO implements SyntheticPopulationDAO {

	public DerbySyntheticPopulationDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.SYNTHETIC_POPULATION_TABLE.TABLE_NAME);
	}

}
