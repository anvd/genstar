package ummisco.genstar.dao.derby;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import ummisco.genstar.dao.InputOutputAttributeDAO;
import ummisco.genstar.dao.derby.DBMS_Tables.GENERATION_RULE_TABLE;
import ummisco.genstar.dao.derby.DBMS_Tables.INPUT_OUTPUT_ATTRIBUTE_TABLE;
import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeInferenceGenerationRule;
import ummisco.genstar.metamodel.FrequencyDistributionGenerationRule;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.util.PersistentObject;

public class DerbyInputOutputAttributeDAO extends AbstractDerbyDAO implements InputOutputAttributeDAO {
	
	private PreparedStatement createInputOutputAttributesStmt, populateInputOutputAttributesStmt, updateInputOutputAttributesStmt;
	

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
			
			updateInputOutputAttributesStmt = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE "
					+ INPUT_OUTPUT_ATTRIBUTE_TABLE.GENERATION_RULE_ID_COLUMN_NAME + " = ? FOR UPDATE", 
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
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

	@Override
	public void updateInputOutputAttributes(final FrequencyDistributionGenerationRule rule) throws GenstarDAOException {
		try {
			updateInputOutputAttributesStmt.setInt(1, rule.getGenerationRuleID());
			ResultSet resultSet = updateInputOutputAttributesStmt.executeQuery();
			
			int attributeID;
			List<Integer> attributeIDsInDBMS = new ArrayList<Integer>();
			while (resultSet.next()) { attributeIDsInDBMS.add(resultSet.getInt(INPUT_OUTPUT_ATTRIBUTE_TABLE.ATTRIBUTE_ID_COLUMN_NAME)); }
			
			
			// 1. build the list of inputAttributeIDs & outputAttributeIDs to create, update and delete
			resultSet.beforeFirst();
			NavigableSet<AbstractAttribute> attributesToCreate = new TreeSet<AbstractAttribute>();
			Map<Integer, AbstractAttribute> attributesToUpdate = new HashMap<Integer, AbstractAttribute>();
			for (AbstractAttribute attr : rule.getOrderedInputAttributes()) {
				attributeID = attr.getAttributeID();
				
				if (attributeID == PersistentObject.NEW_OBJECT_ID) {
					attributesToCreate.add(attr);
				} else {
					attributesToUpdate.put(attributeID, attr);
				}
			}
			 
			
			// 2. remove "deleted" (input & output) attributes or update existing (input & output) attributes
			AbstractAttribute attribute;
			int attributeOrder, iAttrOrder, oAttrOrder;
			boolean isInputAttribute;
			Set<Integer> attributeIDsToUpdate = new HashSet<Integer>(attributesToUpdate.keySet());
			
			resultSet.beforeFirst();
			while (resultSet.next()) {
				attributeID = resultSet.getInt(INPUT_OUTPUT_ATTRIBUTE_TABLE.ATTRIBUTE_ID_COLUMN_NAME);
				
				if (attributeIDsToUpdate.contains(attributeID)) { // update the attribute
					attribute = attributesToUpdate.get(attributeID);
					
					iAttrOrder = rule.getInputAttributeOrder(attribute);
					oAttrOrder = rule.getOutputAttributeOrder(attribute);
					attributeOrder = iAttrOrder != -1 ? iAttrOrder : oAttrOrder;
					isInputAttribute = iAttrOrder != -1;
					
					// update the attribute
					resultSet.updateInt(INPUT_OUTPUT_ATTRIBUTE_TABLE.ATTRIBUTE_ORDER_COLUMN_NAME, attributeOrder);
					resultSet.updateBoolean(INPUT_OUTPUT_ATTRIBUTE_TABLE.IS_INPUT_ATTRIBUTE_COLUMN_NAME, isInputAttribute);
					resultSet.updateRow();
					
				} else { // delete the attribute
					resultSet.deleteRow();
				}
			}
			
			
			// 3. create new attributes
			for (AbstractAttribute attr : attributesToCreate) {
				resultSet.moveToInsertRow();
				
				iAttrOrder = rule.getInputAttributeOrder(attr);
				oAttrOrder = rule.getOutputAttributeOrder(attr);
				attributeOrder = iAttrOrder != -1 ? iAttrOrder : oAttrOrder;
				isInputAttribute = iAttrOrder != -1;

				resultSet.updateInt(INPUT_OUTPUT_ATTRIBUTE_TABLE.ATTRIBUTE_ID_COLUMN_NAME, attr.getAttributeID());
				resultSet.updateInt(INPUT_OUTPUT_ATTRIBUTE_TABLE.GENERATION_RULE_ID_COLUMN_NAME, rule.getGenerationRuleID());
				resultSet.updateInt(INPUT_OUTPUT_ATTRIBUTE_TABLE.ATTRIBUTE_ORDER_COLUMN_NAME, attributeOrder);
				resultSet.updateBoolean(INPUT_OUTPUT_ATTRIBUTE_TABLE.IS_INPUT_ATTRIBUTE_COLUMN_NAME, isInputAttribute);
				
				resultSet.insertRow();
			}
			
			resultSet.close();
			resultSet = null;
		} catch (SQLException e) {
			throw new GenstarDAOException(e);
		}
	}

}
