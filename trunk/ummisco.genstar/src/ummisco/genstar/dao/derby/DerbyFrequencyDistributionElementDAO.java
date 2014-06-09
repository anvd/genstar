package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Map;

import ummisco.genstar.dao.FrequencyDistributionElementDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.FREQUENCY_DISTRIBUTION_ELEMENT_ATTRIBUTE_VALUE_TABLE;
import ummisco.genstar.dao.derby.DBMS_Tables.FREQUENCY_DISTRIBUTION_ELEMENT_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.FrequencyDistributionElement;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.UniqueValue;

public class DerbyFrequencyDistributionElementDAO extends AbstractDerbyDAO implements FrequencyDistributionElementDAO {

	private PreparedStatement createFrequencyDistributionElementsStmt;
	
	private PreparedStatement createFrequencyDistributionElementAttributeValuesStmt;
	
	
	public DerbyFrequencyDistributionElementDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.FREQUENCY_DISTRIBUTION_ELEMENT_TABLE.TABLE_NAME);
		
		try {
			createFrequencyDistributionElementsStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
					+ FREQUENCY_DISTRIBUTION_ELEMENT_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
					+ FREQUENCY_DISTRIBUTION_ELEMENT_TABLE.FREQUENCY_COLUMN_NAME + ") VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			createFrequencyDistributionElementAttributeValuesStmt = connection.prepareStatement("INSERT INTO " + DBMS_Tables.FREQUENCY_DISTRIBUTION_ELEMENT_ATTRIBUTE_VALUE_TABLE.TABLE_NAME
					+ " (" + FREQUENCY_DISTRIBUTION_ELEMENT_ATTRIBUTE_VALUE_TABLE.FREQUENCY_DISTRIBUTION_ELEMENT_ID_COLUMN_NAME + ", "
					+ FREQUENCY_DISTRIBUTION_ELEMENT_ATTRIBUTE_VALUE_TABLE.ATTRIBUTE_ID_COLUMN_NAME + ", "
					+ FREQUENCY_DISTRIBUTION_ELEMENT_ATTRIBUTE_VALUE_TABLE.UNIQUE_VALUE_ID_COLUMN_NAME + ", "
					+ FREQUENCY_DISTRIBUTION_ELEMENT_ATTRIBUTE_VALUE_TABLE.RANGE_VALUE_ID_COLUMN_NAME
					+ ") VALUES (?, ?, ?, ?)");
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public void createFrequencyDistributionElements(final FrequencyDistributionGenerationRule generationRule) throws GenstarDAOException {
		try {
			createFrequencyDistributionElementsStmt.setInt(1, generationRule.getGenerationRuleID());
			
			for (FrequencyDistributionElement distributionElement : generationRule.getDistributionElements()) {
				createFrequencyDistributionElementsStmt.setInt(2, distributionElement.getFrequency());
				createFrequencyDistributionElementsStmt.executeUpdate();
				
				// retrieve the ID of the newly created FrequencyDistributionElement
				ResultSet generatedKeySet = createFrequencyDistributionElementsStmt.getGeneratedKeys();
				if (generatedKeySet.next()) { distributionElement.setFrequencyDistributionElementID(generatedKeySet.getInt(1)); }
				generatedKeySet.close();
				generatedKeySet = null;
				 
				
				// Save the attribute values
				createFrequencyDistributionElementAttributeValuesStmt.setInt(1, distributionElement.getFrequencyDistributionElementID());
				Map<AbstractAttribute, AttributeValue> attributeValues = distributionElement.getAttributeValues(); 
				for (AbstractAttribute attribute : attributeValues.keySet()) {
					createFrequencyDistributionElementAttributeValuesStmt.setInt(2, attribute.getAttributeID());
					
					if (attribute.getValueClassOnData().equals(UniqueValue.class)) {
						createFrequencyDistributionElementAttributeValuesStmt.setInt(3, attributeValues.get(attribute).getAttributeValueID());
						createFrequencyDistributionElementAttributeValuesStmt.setNull(4, Types.INTEGER);
					} else { // RangeValue.class
						createFrequencyDistributionElementAttributeValuesStmt.setNull(3, Types.INTEGER);
						createFrequencyDistributionElementAttributeValuesStmt.setInt(4, attributeValues.get(attribute).getAttributeValueID());
					}
					
					createFrequencyDistributionElementAttributeValuesStmt.executeUpdate();
				}
			}
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

}
