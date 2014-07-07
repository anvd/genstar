package ummisco.genstar.dao;

import ummisco.genstar.exception.GenstarDAOException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

public interface AttributeDAO {

	public abstract AbstractAttribute findAttributeByName(final String attributeName) throws GenstarDAOException; // FIXME the uniqueness 'attributeName' is only true in the scope of a population?
	
	public abstract AbstractAttribute findAttributeByID(final int attributeID) throws GenstarDAOException;
	
//	public abstract void createAttribute(final AbstractAttribute attribute) throws GenstarDAOException;
//	
//	public abstract void updateAttribute(final AbstractAttribute attribute) throws GenstarDAOException;
//	
//	public abstract void deleteAttribute(final AbstractAttribute attribute) throws GenstarDAOException;
//
//	public abstract void deleteAttribute(final int attributeID) throws GenstarDAOException;
	
	public abstract void createAttributes(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException;
	
	public abstract void updateAttributes(final ISyntheticPopulationGenerator syntheticPopulationGenerator) throws GenstarDAOException;

	public abstract void populateAttributes(final ISyntheticPopulationGenerator populationGenerator) throws GenstarDAOException;
}
