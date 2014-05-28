package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.InferenceValueAttributeData1DAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyInferenceValueAttributeData1DAO extends AbstractDerbyDAO implements InferenceValueAttributeData1DAO {

	public DerbyInferenceValueAttributeData1DAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

}
