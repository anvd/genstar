package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.InferenceRangeAttributeData2DAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyInferenceRangeAttributeData2DAO extends AbstractDerbyDAO implements InferenceRangeAttributeData2DAO {

	public DerbyInferenceRangeAttributeData2DAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

}
