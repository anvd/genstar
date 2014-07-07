package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.dao.AttributeValuesFrequencyDataDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.ATTRIBUTE_VALUES_FREQUENCY_DATA_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.AttributeValuesFrequency;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.UniqueValue;

public class DerbyAttributeValuesFrequencyDataDAO extends AbstractDerbyDAO implements AttributeValuesFrequencyDataDAO {

	private PreparedStatement createAttributeValuesFrequencyStmt, setAttributeValuesFrequencyStmt;

	
	public DerbyAttributeValuesFrequencyDataDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.ATTRIBUTE_VALUES_FREQUENCY_DATA_TABLE.TABLE_NAME);
		
		try {
			createAttributeValuesFrequencyStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME
					+ " (" + ATTRIBUTE_VALUES_FREQUENCY_DATA_TABLE.ATTRIBUTE_VALUES_FREQUENCY_ID_COLUMN_NAME + ", "
					+ ATTRIBUTE_VALUES_FREQUENCY_DATA_TABLE.ATTRIBUTE_ID_COLUMN_NAME + ", "
					+ ATTRIBUTE_VALUES_FREQUENCY_DATA_TABLE.UNIQUE_VALUE_ID_COLUMN_NAME + ", "
					+ ATTRIBUTE_VALUES_FREQUENCY_DATA_TABLE.RANGE_VALUE_ID_COLUMN_NAME
					+ ") VALUES (?, ?, ?, ?)");
			
			setAttributeValuesFrequencyStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " 
					+ ATTRIBUTE_VALUES_FREQUENCY_DATA_TABLE.ATTRIBUTE_VALUES_FREQUENCY_ID_COLUMN_NAME + " = ?");
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public void createAttributeValuesFrequency(final AttributeValuesFrequency distributionElement) throws GenstarDAOException {
		try {
			createAttributeValuesFrequencyStmt.setInt(1, distributionElement.getID());
			Map<AbstractAttribute, AttributeValue> attributeValues = distributionElement.getAttributeValues(); 
			for (AbstractAttribute attribute : attributeValues.keySet()) {
				createAttributeValuesFrequencyStmt.setInt(2, attribute.getAttributeID());
				
				if (attribute.getValueClassOnData().equals(UniqueValue.class)) {
					createAttributeValuesFrequencyStmt.setInt(3, attributeValues.get(attribute).getAttributeValueID());
					createAttributeValuesFrequencyStmt.setNull(4, Types.INTEGER);
				} else { // RangeValue.class
					createAttributeValuesFrequencyStmt.setNull(3, Types.INTEGER);
					createAttributeValuesFrequencyStmt.setInt(4, attributeValues.get(attribute).getAttributeValueID());
				}
				
				createAttributeValuesFrequencyStmt.executeUpdate();
			}
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
		
	}

	@Override
	public void populateAttributeValuesFrequency(final FrequencyDistributionGenerationRule rule,
			final int attributeValuesFrequencyID, final int frequency) throws GenstarDAOException {
		
		try {
			int attributeID;
			AbstractAttribute attribute;
			AttributeValue attributeValue;
			int uniqueValueID, rangeValueID, attributeValueID;
			
			
			setAttributeValuesFrequencyStmt.setInt(1, attributeValuesFrequencyID);
			ResultSet resultSet = setAttributeValuesFrequencyStmt.executeQuery();
			Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
			while (resultSet.next()) {
				attribute = null;
				attributeValue = null;
				uniqueValueID = 0;
				rangeValueID = 0;
				attributeValueID = 0;

				attributeID = resultSet.getInt(ATTRIBUTE_VALUES_FREQUENCY_DATA_TABLE.ATTRIBUTE_ID_COLUMN_NAME);
				uniqueValueID = resultSet.getInt(ATTRIBUTE_VALUES_FREQUENCY_DATA_TABLE.UNIQUE_VALUE_ID_COLUMN_NAME);
				rangeValueID = resultSet.getInt(ATTRIBUTE_VALUES_FREQUENCY_DATA_TABLE.RANGE_VALUE_ID_COLUMN_NAME);
				
				for (AbstractAttribute attr : rule.getAttributes()) {
					if (attributeID == attr.getAttributeID()) {
						attribute = attr;
						break;
					}
				}
				
				attributeValueID = (uniqueValueID != 0) ? uniqueValueID : rangeValueID;
				for (AttributeValue attrValue : attribute.values()) {
					if (attrValue.getAttributeValueID() == attributeValueID) {
						attributeValue = attrValue;
						break;
					}
				}
				
				attributeValues.put(attribute, attributeValue);
			}
			
			List<AttributeValuesFrequency> attributeValuesFrequecies = rule.findAttributeValuesFrequencies(attributeValues);
			
			int size = attributeValuesFrequecies.size();
			if (size > 1) { throw new GenstarDAOException("Can not populate AttributeValuesFrequency : there are several matches of attributeValues on the GenerationRule"); }
			
			if (size == 1) {
				AttributeValuesFrequency avf = attributeValuesFrequecies.get(0);
				avf.setID(attributeValuesFrequencyID);
				avf.setFrequency(frequency);
			}
			
			
			resultSet.close();
			resultSet = null;
		} catch (Exception e) {
			if (e instanceof GenstarDAOException) { throw (GenstarDAOException)e; }
			throw new GenstarDAOException(e);
		}
	}

}
