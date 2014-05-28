package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.FrequencyDistributionGenerationRuleDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyFrequencyDistributionGenerationRuleDAO extends AbstractDerbyDAO implements FrequencyDistributionGenerationRuleDAO {

	public DerbyFrequencyDistributionGenerationRuleDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

}
