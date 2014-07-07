package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ummisco.genstar.dao.InputOutputAttributeDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.INPUT_OUTPUT_ATTRIBUTE_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public class DerbyInputOutputAttributeDAO extends AbstractDerbyDAO implements InputOutputAttributeDAO {
	
	private PreparedStatement createInputOutputAttributesStmt, populateInputOutputAttributesStmt;
	

	public DerbyInputOutputAttributeDAO(final DerbyGenstarDAOFactory daoFactory) throws GenstarDAOException {
		super(daoFactory, DBMS_Tables.INPUT_OUTPUT_ATTRIBUTE_TABLE.TABLE_NAME);
		
		try {
			createInputOutputAttributesStmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " (" 
					+ INPUT_OUTPUT_ATTRIBUTE_TABLE.ATTRIBUTE_ID_COLUMN_NAME + ", "
					+ INPUT_OUTPUT_ATTRIBUTE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + ", "
					+ INPUT_OUTPUT_ATTRIBUTE_TABLE.ATTRIBUTE_ORDER_COLUMN_NAME + ", "
					+ INPUT_OUTPUT_ATTRIBUTE_TABLE.IS_INPUT_ATTRIBUTE_COLUMN_NAME
					+ ") VALUES (?, ?, ?, ?)");
			
			populateInputOutputAttributesStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ INPUT_OUTPUT_ATTRIBUTE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + " = ? ORDER BY "
					+ INPUT_OUTPUT_ATTRIBUTE_TABLE.ATTRIBUTE_ORDER_COLUMN_NAME);
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

	@Override
	public void createInputOutputAttributes(final FrequencyDistributionGenerationRule rule) throws GenstarDAOException {
		
		try {
			createInputOutputAttributesStmt.setInt(2, rule.getGenerationRuleID());

			// create input attributes
			createInputOutputAttributesStmt.setBoolean(4, true);
			int inputAttrOrder = 0;
			for (AbstractAttribute intputAttr : rule.getOrderedInputAttributes()) {
				createInputOutputAttributesStmt.setInt(1, intputAttr.getAttributeID());
				createInputOutputAttributesStmt.setInt(3, inputAttrOrder);
				createInputOutputAttributesStmt.executeUpdate();
				
				inputAttrOrder++;
			}
			
			// create output attributes
			createInputOutputAttributesStmt.setBoolean(4, false);
			int outputAttrOrder = 0;
			for (AbstractAttribute outputAttr : rule.getOrderedOutputAttributes()) {
				createInputOutputAttributesStmt.setInt(1, outputAttr.getAttributeID());
				createInputOutputAttributesStmt.setInt(3, outputAttrOrder);
				createInputOutputAttributesStmt.executeUpdate();
				
				outputAttrOrder++;
			}
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
		
	}

	@Override
	public void populateInputOutputAttributes(final FrequencyDistributionGenerationRule rule) throws GenstarDAOException {
		try {
			int attributeID;
			boolean isInputAttribute;
			ISyntheticPopulationGenerator populationGenerator = rule.getGenerator();
			
			populateInputOutputAttributesStmt.setInt(1, rule.getGenerationRuleID());
			ResultSet resultSet = populateInputOutputAttributesStmt.executeQuery();
			while (resultSet.next()) {
				attributeID = resultSet.getInt(INPUT_OUTPUT_ATTRIBUTE_TABLE.ATTRIBUTE_ID_COLUMN_NAME);
				isInputAttribute = resultSet.getBoolean(INPUT_OUTPUT_ATTRIBUTE_TABLE.IS_INPUT_ATTRIBUTE_COLUMN_NAME);
				
				
				// find attribute on the population generator;
				for (AbstractAttribute attr : populationGenerator.getAttributes()) {
					if (attr.getAttributeID() == attributeID) {
						if (isInputAttribute) {
							rule.appendInputAttribute(attr);
						} else {
							rule.appendOutputAttribute(attr);
						}
						
						break;
					}
				}
			}
			
			resultSet.close();
			resultSet = null;
		} catch (Exception e) {
			throw new GenstarDAOException(e);
		}
	}

}
