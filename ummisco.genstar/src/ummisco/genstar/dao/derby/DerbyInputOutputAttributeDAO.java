package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.InputOutputAttributeDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyInputOutputAttributeDAO extends AbstractDerbyDAO implements InputOutputAttributeDAO {

	public DerbyInputOutputAttributeDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.INPUT_OUTPUT_ATTRIBUTE_TABLE.TABLE_NAME);
	}

}
