package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.InferenceValueAttributeData2DAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyInferenceValueAttributeData2DAO extends AbstractDerbyDAO implements InferenceValueAttributeData2DAO {

	public DerbyInferenceValueAttributeData2DAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

}
