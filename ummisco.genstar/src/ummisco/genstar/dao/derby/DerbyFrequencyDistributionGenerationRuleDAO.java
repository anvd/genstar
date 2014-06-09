package ummisco.genstar.dao.derby;

import ummisco.genstar.dao.FrequencyDistributionElementDAO;
import ummisco.genstar.dao.FrequencyDistributionGenerationRuleDAO;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;

public class DerbyFrequencyDistributionGenerationRuleDAO extends AbstractDerbyDAO implements FrequencyDistributionGenerationRuleDAO {
	
	private FrequencyDistributionElementDAO frequencyDistributionElementDAO;
	

	public DerbyFrequencyDistributionGenerationRuleDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, "");
		
		frequencyDistributionElementDAO = daoFactory.getFrequencyDistributionElementDAO();
	}

	@Override
	public FrequencyDistributionGenerationRule findFrequencyDistributionGenerationRuleByName(final String frequencyDistributionGenerationRuleName) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public FrequencyDistributionGenerationRule findFrequencyDistributionGenerationRuleByID(final int frequencyDistributionGenerationRuleID) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void createFrequencyDistributionGenerationRule(final FrequencyDistributionGenerationRule frequencyDistributionGenerationRule) throws GenstarDAOException {
		frequencyDistributionElementDAO.createFrequencyDistributionElements(frequencyDistributionGenerationRule);
	}

	@Override
	public void updateFrequencyDistributionGenerationRule(final FrequencyDistributionGenerationRule frequencyDistributionGenerationRule) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void deleteFrequencyDistributionGenerationRule(final FrequencyDistributionGenerationRule frequencyDistributionGenerationRule) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void deleteFrequencyDistributionGenerationRule(final int frequencyDistributionGenerationRuleID) throws GenstarDAOException {
		throw new UnsupportedOperationException("Not yet implemented");
	}

}
