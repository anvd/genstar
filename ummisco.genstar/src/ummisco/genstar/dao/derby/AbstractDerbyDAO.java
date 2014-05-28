package ummisco.genstar.dao.derby;

import java.sql.Connection;

import ummisco.genstar.exception.GenstarDAOException;

public abstract class AbstractDerbyDAO {
	
	protected DerbyGenstarDAOFactory daoFactory = null;

	protected Connection connection = null;
	
	public AbstractDerbyDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		if (daoFactory == null) { throw new IllegalArgumentException("'daoFactory' parameter can not be null"); }
		
		this.daoFactory = daoFactory;
		this.connection = daoFactory.getConnection();
	}
}
