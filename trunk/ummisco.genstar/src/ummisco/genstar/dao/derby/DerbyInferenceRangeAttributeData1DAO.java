package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.InferenceRangeAttributeData1DAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyInferenceRangeAttributeData1DAO extends AbstractDerbyDAO implements InferenceRangeAttributeData1DAO {

	public DerbyInferenceRangeAttributeData1DAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

}
