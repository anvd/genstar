package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.GenerationRuleDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyGenerationRuleDAO extends AbstractDerbyDAO implements GenerationRuleDAO {

	public DerbyGenerationRuleDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

}
