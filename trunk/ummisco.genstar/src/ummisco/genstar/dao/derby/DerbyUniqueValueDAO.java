package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ummisco.genstar.dao.UniqueValueDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.UNIQUE_VALUE_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.UniqueValuesAttribute;
import ummisco.genstar.metamodel.UniqueValue;

public class DerbyUniqueValueDAO extends AbstractDerbyDAO implements UniqueValueDAO {
	
	private PreparedStatement createUniqueValuesStmt;

	public DerbyUniqueValueDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.UNIQUE_VALUE_TABLE.TABLE_NAME);
		
		try {
			createUniqueValuesStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " (" 
					+ UNIQUE_VALUE_TABLE.ATTRIBUTE_ID_COLUMN_NAME + ", "
					+ UNIQUE_VALUE_TABLE.STRING_VALUE_COLUMN_NAME
					+ ") VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public void createUniqueValues(final UniqueValuesAttribute uniqueValueAttribute) throws GenstarDAOException {
		try {
			createUniqueValuesStmt.setInt(1, uniqueValueAttribute.getAttributeID());
			
			for (AttributeValue attributeValue : uniqueValueAttribute.values()) {
				createUniqueValuesStmt.setString(2, ((UniqueValue) attributeValue).getStringValue());
				createUniqueValuesStmt.executeUpdate();
				
				// retrieve the ID of the newly created uniqueValue
				ResultSet generatedKeySet = createUniqueValuesStmt.getGeneratedKeys();
				if (generatedKeySet.next()) { attributeValue.setAttributeValueID(generatedKeySet.getInt(1)); }
				generatedKeySet.close();
				generatedKeySet = null;
			}
			
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

}
