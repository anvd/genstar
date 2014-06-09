package ummisco.genstar.dao.derby;

import java.sql.Connection;

import ummisco.genstar.exception.GenstarDAOException;

public abstract class AbstractDerbyDAO {
	
	protected DerbyGenstarDAOFactory daoFactory = null;

	protected Connection connection = null;
	
	protected String TABLE_NAME;
	
	public AbstractDerbyDAO(final DerbyGenstarDAOFactory daoFactory, final String tableName) throws GenstarDAOException {
		if (daoFactory == null) { throw new IllegalArgumentException("'daoFactory' parameter can not be null"); }
		if (tableName == null) { throw new IllegalArgumentException("tableName' parameter can not be null"); }
		
		this.daoFactory = daoFactory;
		this.connection = daoFactory.getConnection();
		this.TABLE_NAME = tableName;
	}
}
