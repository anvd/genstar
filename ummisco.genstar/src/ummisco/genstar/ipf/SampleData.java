package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.GenstarFactoryUtils;

public class SampleData extends AbstractSampleData implements ISampleData { // TODO change to CSVSampleData
	
	private List<AbstractAttribute> attributes;
	
	private GenstarCSVFile data = null;
	
	private SortedMap<Integer, AbstractAttribute> attributeIndexes;
	
	private String populationName;
	
	private SampleEntityPopulation sampleEntityPopulation;

	
	public SampleData(final String populationName, final List<AbstractAttribute> attributes, GenstarCSVFile data) throws GenstarException {
		if (populationName == null || populationName.isEmpty()) { throw new GenstarException("Parameter populationName can neither be null nor empty"); }
		if (attributes == null) { throw new GenstarException("Parameter attributes can not be null"); }
		if (data == null) { throw new GenstarException("Parameter data can not be null"); }
		
		this.populationName = populationName;
		this.attributes = new ArrayList<AbstractAttribute>();
		this.attributes.addAll(attributes);
		this.data = data;
		
		initializeSampleEntities();
	}
	
	private void initializeSampleEntities() throws GenstarException {
		this.sampleEntityPopulation = new SampleEntityPopulation(populationName, attributes);
		
		Map<String, AbstractAttribute> attributeMap = new HashMap<String, AbstractAttribute>(); // attributeNameOnEntity :: attribute
		for (AbstractAttribute attr : attributes) { attributeMap.put(attr.getNameOnEntity(), attr); }
		
		// 1. parse CSV file header
		List<String> sampleDataHeaders = data.getHeaders();
		attributeIndexes = new TreeMap<Integer, AbstractAttribute>();
		for (int col=0; col<sampleDataHeaders.size(); col++) {
			String attributeNameOnSampleEntity = sampleDataHeaders.get(col);
			AbstractAttribute attribute = attributeMap.get(attributeNameOnSampleEntity);
			if (attribute != null) { attributeIndexes.put(col, attribute); }
		}
		
		// 2. verify that the CSV file contains all the "required" attributes
		if (attributeMap.size() != attributeIndexes.size()) {
			List<String> attributeOnSampleFile = new ArrayList<String>();
			for (AbstractAttribute attr : attributeIndexes.values()) { attributeOnSampleFile.add(attr.getNameOnEntity()); }
			
			List<String> attributeOnSEPopulation = new ArrayList<String>(attributeMap.keySet());
			
			attributeOnSEPopulation.removeAll(attributeOnSampleFile);
			if (!attributeOnSEPopulation.isEmpty()) {
				StringBuffer missingAttributes = new StringBuffer();
				
				int size = 0;
				for (String attrName : attributeOnSEPopulation) {
					missingAttributes.append(attrName);
					if (size < attributeOnSEPopulation.size() - 1) { missingAttributes.append(", "); }
					size++;
				}
				
				throw new GenstarException("Missing required attribute(s) : " + missingAttributes.toString() + ". Sample data file : " + data.getPath());
			}
		}
		
		// 3. initialize sample entities
		List<String> rowContent;
		AbstractAttribute attribute;
		AttributeValue value;
		String valueStr;
		List<Map<String, AttributeValue>> sampleEntitiesAttributeValues = new ArrayList<Map<String, AttributeValue>>();
		Map<String, AttributeValue> sampleAttributes;
		for (int row=0; row<(data.getRows()-1); row++) { // first line is the header
			rowContent = data.getRow(row);
			
			sampleAttributes = new HashMap<String, AttributeValue>();
			for (int attributeColumn : attributeIndexes.keySet()) { // only care about "recognized" attributes
				attribute = attributeIndexes.get(attributeColumn);
				valueStr = rowContent.get(attributeColumn);
				List<String> valueStrList = new ArrayList<String>();
				valueStrList.add(valueStr);
				
				value = attribute.findCorrespondingAttributeValue(valueStrList); // ensure that the value is accepted by the attribute
				if (value == null) { 
					throw new GenstarException("'" + valueStr + "' defined in the sample data is not recognized as a value of " + attribute.getNameOnEntity()
								+ " attribute. File: " + data.getPath() + " at row: " + (row + 1) + ", column: " + (attributeColumn) + "."); 
				}
				
				if (attribute.getValueClassOnData().equals(attribute.getValueClassOnEntity())) { // valueOnClass == valueOnEntity
					sampleAttributes.put(attributeIndexes.get(attributeColumn).getNameOnEntity(), value);
				} else {
					sampleAttributes.put(attributeIndexes.get(attributeColumn).getNameOnEntity(), 
							GenstarFactoryUtils.createAttributeValue(attribute.getValueClassOnEntity(), attribute.getDataType(), valueStrList));
				}
			}
			sampleEntitiesAttributeValues.add(sampleAttributes);
		}
		
		sampleEntityPopulation.createSampleEntities(sampleEntitiesAttributeValues);
	}
	
	@Override
	public SampleEntityPopulation getSampleEntityPopulation() {
		return sampleEntityPopulation;
	}

	@Override
	public void recodeIdAttributes(final SampleEntity targetEntity) throws GenstarException {
		// TODO find the (only) ID attribute then recode it
	}
}
