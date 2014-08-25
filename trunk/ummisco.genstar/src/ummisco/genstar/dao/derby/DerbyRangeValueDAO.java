package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ummisco.genstar.dao.RangeValueDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.RANGE_VALUE_TABLE;
import ummisco.genstar.dao.derby.DBMS_Tables.UNIQUE_VALUE_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.DataType;
import ummisco.genstar.metamodel.RangeValue;
import ummisco.genstar.metamodel.RangeValuesAttribute;
import ummisco.genstar.metamodel.UniqueValue;
import ummisco.genstar.util.PersistentObject;

public class DerbyRangeValueDAO extends AbstractDerbyDAO implements RangeValueDAO {
	
	private PreparedStatement createRangeValuesStmt, populateRangeValuesStmt, updateRangeValuesStmt;
	

	public DerbyRangeValueDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.RANGE_VALUE_TABLE.TABLE_NAME);
		
		try {
			createRangeValuesStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
					+ RANGE_VALUE_TABLE.ATTRIBUTE_ID_COLUMN_NAME + ", "
					+ RANGE_VALUE_TABLE.MIN_STRING_VALUE_COLUMN_NAME + ", "
					+ RANGE_VALUE_TABLE.MAX_STRING_VALUE_COLUMN_NAME
					+ " ) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			populateRangeValuesStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ RANGE_VALUE_TABLE.ATTRIBUTE_ID_COLUMN_NAME + " = ?");
			
			updateRangeValuesStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ RANGE_VALUE_TABLE.ATTRIBUTE_ID_COLUMN_NAME + " = ? FOR UPDATE", 
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public void createRangeValues(final RangeValuesAttribute rangeAttribute) throws GenstarDAOException {
		
		
		
		try {
			Set<RangeValue> rangeValues = new HashSet<RangeValue>();
			for (AttributeValue value : rangeAttribute.values()) { rangeValues.add((RangeValue) value); }
			
			internalCreateRangeValues(rangeAttribute.getAttributeID(), rangeValues);
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}
	
	private void internalCreateRangeValues(final int attributeID, final Set<RangeValue> rangeValues) throws SQLException {
		createRangeValuesStmt.setInt(1, attributeID);
		
		for (RangeValue rValue : rangeValues) {
			createRangeValuesStmt.setString(2, rValue.getMinStringValue());
			createRangeValuesStmt.setString(3, rValue.getMaxStringValue());
			createRangeValuesStmt.executeUpdate();

		
			// retrieve the ID of the newly created rangeValue
			ResultSet generatedKeySet = createRangeValuesStmt.getGeneratedKeys();
			if (generatedKeySet.next()) { rValue.setAttributeValueID(generatedKeySet.getInt(1)); }
			generatedKeySet.close();
			generatedKeySet = null;
		}
	}

	@Override
	public void populateRangeValues(final RangeValuesAttribute rangeValuesAttribute) throws GenstarDAOException {
		try {
			DataType dataType = rangeValuesAttribute.getDataType();
			int rangeValueID;
			String minStringValue, maxStringValue; 
			RangeValue rangeValue;
			
			populateRangeValuesStmt.setInt(1, rangeValuesAttribute.getAttributeID());
			ResultSet resultSet = populateRangeValuesStmt.executeQuery();
			while (resultSet.next()) {
				rangeValueID = resultSet.getInt(RANGE_VALUE_TABLE.RANGE_VALUE_ID_COLUMN_NAME);
				minStringValue = resultSet.getString(RANGE_VALUE_TABLE.MIN_STRING_VALUE_COLUMN_NAME);
				maxStringValue = resultSet.getString(RANGE_VALUE_TABLE.MAX_STRING_VALUE_COLUMN_NAME);
				
				rangeValue = new RangeValue(dataType, minStringValue, maxStringValue);
				rangeValue.setAttributeValueID(rangeValueID);
				
				rangeValuesAttribute.add(rangeValue);
			}
			
			resultSet.close();
			resultSet = null;
		} catch (final Exception e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public void updateRangeValues(final RangeValuesAttribute rangeValuesAttribute) throws GenstarDAOException {
		try {
			updateRangeValuesStmt.setInt(1, rangeValuesAttribute.getAttributeID());
			ResultSet resultSet = updateRangeValuesStmt.executeQuery();
			
			int rangeValueID;
			List<Integer> rangeValueIDsInDBMS = new ArrayList<Integer>();
			while (resultSet.next()) { rangeValueIDsInDBMS.add(resultSet.getInt(RANGE_VALUE_TABLE.RANGE_VALUE_ID_COLUMN_NAME)); }
			
			// 1. build the list of rangeValueIDs to create, update and delete
			Set<RangeValue> rangeValuesToCreate = new HashSet<RangeValue>();
			Map<Integer, RangeValue> rangeValuesToUpdate = new HashMap<Integer, RangeValue>();
			for (AttributeValue value : rangeValuesAttribute.values()) {
				rangeValueID = value.getAttributeValueID();
				
				if (rangeValueID == PersistentObject.NEW_OBJECT_ID) {
					rangeValuesToCreate.add((RangeValue) value);
				} else {
					rangeValuesToUpdate.put(rangeValueID, (RangeValue) value);
				}
			}
			 
			
			// 2. remove "deleted" RangeValues or update existing RangeValues
			RangeValue rValue;
			resultSet.beforeFirst();
			Set<Integer> rangeValueIDsToUpdate = new HashSet<Integer>(rangeValuesToUpdate.keySet());
			while (resultSet.next()) {
				rangeValueID = resultSet.getInt(RANGE_VALUE_TABLE.RANGE_VALUE_ID_COLUMN_NAME);
				
				if (rangeValueIDsToUpdate.contains(rangeValueID)) { // update the RangeValue
					rValue = rangeValuesToUpdate.get(rangeValueID);
					
					// update the RangeValue
					resultSet.updateString(RANGE_VALUE_TABLE.MIN_STRING_VALUE_COLUMN_NAME, rValue.getMinStringValue());
					resultSet.updateString(RANGE_VALUE_TABLE.MAX_STRING_VALUE_COLUMN_NAME, rValue.getMaxStringValue());
					resultSet.updateRow();
					
				} else { // delete the RangeValue
					resultSet.deleteRow();
				}
			}
			 

			// 3. create new RangeValues
			internalCreateRangeValues(rangeValuesAttribute.getAttributeID(), rangeValuesToCreate);

		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
		
	}

}
