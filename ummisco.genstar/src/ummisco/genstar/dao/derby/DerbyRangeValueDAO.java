package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import ummisco.genstar.dao.RangeValueDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.RANGE_VALUE_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.RangeValuesAttribute;
import ummisco.genstar.metamodel.RangeValue;

public class DerbyRangeValueDAO extends AbstractDerbyDAO implements RangeValueDAO {
	
	private PreparedStatement createRangeValuesStmt;
	

	public DerbyRangeValueDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.RANGE_VALUE_TABLE.TABLE_NAME);
		
		try {
			createRangeValuesStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " ("
					+ RANGE_VALUE_TABLE.ATTRIBUTE_ID_COLUMN_NAME + ", "
					+ RANGE_VALUE_TABLE.MIN_STRING_VALUE_COLUMN_NAME + ", "
					+ RANGE_VALUE_TABLE.MAX_STRING_VALUE_COLUMN_INDEX
					+ " ) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public void createRangeValues(final RangeValuesAttribute rangeAttribute) throws GenstarDAOException {
		try {
			createRangeValuesStmt.setInt(1, rangeAttribute.getAttributeID());
			
			for (AttributeValue attributeValue : rangeAttribute.values()) {
				createRangeValuesStmt.setString(2, ( (RangeValue) attributeValue).getMinStringValue());
				createRangeValuesStmt.setString(3, ( (RangeValue) attributeValue).getMaxStringValue());
				createRangeValuesStmt.executeUpdate();

			
				// retrieve the ID of the newly created rangeValue
				ResultSet generatedKeySet = createRangeValuesStmt.getGeneratedKeys();
				if (generatedKeySet.next()) { attributeValue.setAttributeValueID(generatedKeySet.getInt(1)); }
				generatedKeySet.close();
				generatedKeySet = null;
			}
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

}
