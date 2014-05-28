package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.AttributeInferenceGenerationRuleDAO;
import ummisco.genstar.exception.GenstarDAOException;

public class DerbyAttributeInferenceGenerationRuleDAO extends AbstractDerbyDAO implements AttributeInferenceGenerationRuleDAO {

	public DerbyAttributeInferenceGenerationRuleDAO(DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory);
	}

}
