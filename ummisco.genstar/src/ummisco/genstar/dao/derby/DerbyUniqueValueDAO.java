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

import ummisco.genstar.dao.UniqueValueDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.UNIQUE_VALUE_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.DataType;
import ummisco.genstar.metamodel.UniqueValue;
import ummisco.genstar.metamodel.UniqueValuesAttribute;
import ummisco.genstar.util.PersistentObject;

public class DerbyUniqueValueDAO extends AbstractDerbyDAO implements UniqueValueDAO {
	
	private PreparedStatement createUniqueValuesStmt, populateUniqueValuesStmt, updateUniqueValuesStmt;

	public DerbyUniqueValueDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.UNIQUE_VALUE_TABLE.TABLE_NAME);
		
		try {
			createUniqueValuesStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " (" 
					+ UNIQUE_VALUE_TABLE.ATTRIBUTE_ID_COLUMN_NAME + ", "
					+ UNIQUE_VALUE_TABLE.STRING_VALUE_COLUMN_NAME
					+ ") VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			populateUniqueValuesStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ UNIQUE_VALUE_TABLE.ATTRIBUTE_ID_COLUMN_NAME + " = ?");
			
			updateUniqueValuesStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ UNIQUE_VALUE_TABLE.ATTRIBUTE_ID_COLUMN_NAME + " = ? FOR UPDATE", 
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public void createUniqueValues(final UniqueValuesAttribute uniqueValueAttribute) throws GenstarDAOException {
		
		try {
			Set<UniqueValue> uniqueValues = new HashSet<UniqueValue>();
			for (AttributeValue value : uniqueValueAttribute.values()) { uniqueValues.add((UniqueValue) value); }

			internalCreateUniqueValues(uniqueValueAttribute.getAttributeID(), uniqueValues);
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}
	
	private void internalCreateUniqueValues(final int attributeID, final Set<UniqueValue> uniqueValues) throws SQLException {
		createUniqueValuesStmt.setInt(1, attributeID);
		
		for (UniqueValue uValue : uniqueValues) {
			createUniqueValuesStmt.setString(2, uValue.getStringValue());
			createUniqueValuesStmt.executeUpdate();
			
			// retrieve the ID of the newly created uniqueValue
			ResultSet generatedKeySet = createUniqueValuesStmt.getGeneratedKeys();
			if (generatedKeySet.next()) { uValue.setAttributeValueID(generatedKeySet.getInt(1)); }
			generatedKeySet.close();
			generatedKeySet = null;
		}		 
	}

	@Override
	public void populateUniqueValues(final UniqueValuesAttribute uniqueValuesAttribute) throws GenstarDAOException {
		try {
			populateUniqueValuesStmt.setInt(1, uniqueValuesAttribute.getAttributeID());
			ResultSet resultSet = populateUniqueValuesStmt.executeQuery();
			
			DataType dataType = uniqueValuesAttribute.getDataType();
			UniqueValue uniqueValue;
			int uniqueValueID;
			String stringValue;
			while (resultSet.next()) {
				uniqueValueID = resultSet.getInt(UNIQUE_VALUE_TABLE.UNIQUE_VALUE_ID_COLUMN_NAME);
				stringValue = resultSet.getString(UNIQUE_VALUE_TABLE.STRING_VALUE_COLUMN_NAME);
				
				uniqueValue = new UniqueValue(dataType, stringValue);
				uniqueValue.setAttributeValueID(uniqueValueID);
				
				uniqueValuesAttribute.add(uniqueValue);
			}
			
			resultSet.close();
			resultSet = null;
		} catch (final Exception e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public void updateUniqueValues(final UniqueValuesAttribute uniqueValuesAttribute) throws GenstarDAOException {
		
		try {
			updateUniqueValuesStmt.setInt(1, uniqueValuesAttribute.getAttributeID());
			ResultSet resultSet = updateUniqueValuesStmt.executeQuery();
			
			int uniqueValueID;
			List<Integer> uniqueValueIDsInDBMS = new ArrayList<Integer>();
			while (resultSet.next()) { uniqueValueIDsInDBMS.add(resultSet.getInt(UNIQUE_VALUE_TABLE.UNIQUE_VALUE_ID_COLUMN_NAME)); }
			
			// 1. build the list of uniqueValueIDs to create, update and delete
			Set<UniqueValue> uniqueValuesToCreate = new HashSet<UniqueValue>();
			Map<Integer, UniqueValue> uniqueValuesToUpdate = new HashMap<Integer, UniqueValue>();
			for (AttributeValue value : uniqueValuesAttribute.values()) {
				uniqueValueID = value.getAttributeValueID();
				
				if (uniqueValueID == PersistentObject.NEW_OBJECT_ID) {
					uniqueValuesToCreate.add((UniqueValue) value);
				} else {
					uniqueValuesToUpdate.put(uniqueValueID, (UniqueValue) value);
				}
			}

			
			// 2. remove "deleted" UniqueValues or update existing UniqueValues
			UniqueValue uValue;
			resultSet.beforeFirst();
			Set<Integer> uniqueValueIDsToUpdate = new HashSet<Integer>(uniqueValuesToUpdate.keySet());
			while (resultSet.next()) {
				uniqueValueID = resultSet.getInt(UNIQUE_VALUE_TABLE.UNIQUE_VALUE_ID_COLUMN_NAME);
				
				if (uniqueValueIDsToUpdate.contains(uniqueValueID)) { // update the UniqueValue
					uValue = uniqueValuesToUpdate.get(uniqueValueID);
					
					// update the UniqueValue
					resultSet.updateString(UNIQUE_VALUE_TABLE.STRING_VALUE_COLUMN_NAME, uValue.getStringValue());
					resultSet.updateRow();
					
				} else { // delete the UniqueValue
					resultSet.deleteRow();
				}
			}
			
			
			// 3. create new UniqueValues
			this.internalCreateUniqueValues(uniqueValuesAttribute.getAttributeID(), uniqueValuesToCreate);
			
		} catch (Exception e) {
			if (e instanceof GenstarDAOException) { throw (GenstarDAOException)e; }
			throw new GenstarDAOException(e);
		}
		
	}

}
