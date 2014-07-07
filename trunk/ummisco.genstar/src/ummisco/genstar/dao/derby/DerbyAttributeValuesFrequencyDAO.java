package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ummisco.genstar.dao.AttributeValuesFrequencyDataDAO;
import ummisco.genstar.dao.AttributeValuesFrequencyDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.ATTRIBUTE_VALUES_FREQUENCY_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AttributeValuesFrequency;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;

public class DerbyAttributeValuesFrequencyDAO extends AbstractDerbyDAO implements AttributeValuesFrequencyDAO {

	private PreparedStatement createAttributeValuesFrequeciesStmt, populateAttributeValuesFrequenciesStmt;
	
	private AttributeValuesFrequencyDataDAO attributeValuesFrequencyDataDAO;
	
	
	public DerbyAttributeValuesFrequencyDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.ATTRIBUTE_VALUES_FREQUENCY_TABLE.TABLE_NAME);
		
		try {
			createAttributeValuesFrequeciesStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
					+ ATTRIBUTE_VALUES_FREQUENCY_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
					+ ATTRIBUTE_VALUES_FREQUENCY_TABLE.FREQUENCY_COLUMN_NAME + ") VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			populateAttributeValuesFrequenciesStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME 
					+ " WHERE " + ATTRIBUTE_VALUES_FREQUENCY_TABLE.GENERATION_RULE_ID_COLUMN_NAME + " = ?");
			
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
		
		attributeValuesFrequencyDataDAO = daoFactory.getAttributeValuesFrequencyDataDAO();
	}

	@Override
	public void createAttributeValuesFrequecies(final FrequencyDistributionGenerationRule generationRule) throws GenstarDAOException {
		try {
			createAttributeValuesFrequeciesStmt.setInt(1, generationRule.getGenerationRuleID());
			
			for (AttributeValuesFrequency distributionElement : generationRule.getAttributeValuesFrequencies()) {
				createAttributeValuesFrequeciesStmt.setInt(2, distributionElement.getFrequency());
				createAttributeValuesFrequeciesStmt.executeUpdate();
				
				// retrieve the ID of the newly created FrequencyDistributionElement
				ResultSet generatedKeySet = createAttributeValuesFrequeciesStmt.getGeneratedKeys();
				if (generatedKeySet.next()) { distributionElement.setID(generatedKeySet.getInt(1)); }
				generatedKeySet.close();
				generatedKeySet = null;
				 
				
				// Save the attribute values
				attributeValuesFrequencyDataDAO.createAttributeValuesFrequency(distributionElement);
			}
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public void populateAttributeValuesFrequencies(final FrequencyDistributionGenerationRule rule) throws GenstarDAOException {
		try {
			int atttributeValuesFrequencyID;
			int frequency;
			
			populateAttributeValuesFrequenciesStmt.setInt(1, rule.getGenerationRuleID());
			ResultSet resultSet = populateAttributeValuesFrequenciesStmt.executeQuery();
			while (resultSet.next()) {
				atttributeValuesFrequencyID = resultSet.getInt(ATTRIBUTE_VALUES_FREQUENCY_TABLE.ATTRIBUTE_VALUES_FREQUENCY_ID_COLUMN_NAME);
				frequency = resultSet.getInt(ATTRIBUTE_VALUES_FREQUENCY_TABLE.FREQUENCY_COLUMN_NAME);
				
				attributeValuesFrequencyDataDAO.populateAttributeValuesFrequency(rule, atttributeValuesFrequencyID, frequency);
			}
			
			resultSet.close();
			resultSet = null;
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

}
