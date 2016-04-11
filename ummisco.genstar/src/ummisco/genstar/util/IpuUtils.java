package ummisco.genstar.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.IPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.util.GenstarUtils.CSV_FILE_FORMATS;

public class IpuUtils {

	public static List<AttributeValuesFrequency> parseIpuControlTotalsFile(final ISyntheticPopulationGenerator generator, final List<AbstractAttribute> controlledAttributes, final GenstarCSVFile ipuControlTotalsFile) throws GenstarException {
		
		// 1. parameters validation
		if (generator == null || controlledAttributes == null || ipuControlTotalsFile == null) {
			throw new GenstarException("Parameters generator, controlledAttributes, ipuControlTotalsFile can not be null");
		}
		
		if (controlledAttributes.size() < 2) { throw new GenstarException("controlledAttributes must have at least two attributes/elements"); }
		
		// 2. ensure that controlledAttributes doesn't contain duplicated (controlled) attributes or belong to different population generators
		Map<String, AbstractAttribute> controlledAttributesMap = new HashMap<String, AbstractAttribute>();
		for (AbstractAttribute controlledAttr : controlledAttributes) {
			if (controlledAttributesMap.containsKey(controlledAttr.getNameOnData())) { throw new GenstarException("Duplicated controlled attributes (" + controlledAttr.getNameOnData() + ")"); }
			if (!controlledAttr.getPopulationGenerator().equals(generator)) { throw new GenstarException("Controlled attributes belong to different population generator"); }
			
			controlledAttributesMap.put(controlledAttr.getNameOnData(), controlledAttr);
		}
		
		// 3. generate IPU controlled attributes values frequencies to verify the validity of control totals (file content)
		Set<AttributeValuesFrequency> generatedControlledAVsFrequencies = GenstarUtils.generateAttributeValuesFrequencies(new HashSet<AbstractAttribute>(controlledAttributes));
		Set<AttributeValuesFrequency> alreadyValidControlledAvfFrequencies = new HashSet<AttributeValuesFrequency>();
		
		// 4. IPU control totals file size verification
		List<List<String>> ipuControlTotalsFileContent = ipuControlTotalsFile.getContent();
		if (generatedControlledAVsFrequencies.size() != ipuControlTotalsFileContent.size()) {
			throw new GenstarException("Mismatched between required/valid number of IPU controlled totals and supplied number of IPU controlled totals. Required values: " 
				+ generatedControlledAVsFrequencies.size() + ", supplied values (in control total file): " + ipuControlTotalsFileContent.size() + ". File: " + ipuControlTotalsFile.getPath());
		}
		
		// 5. read the IPU control totals file line by line to initialize AttributeValueFrequencies
		int ipuControlTotalLineLength = (controlledAttributes.size() * 2) + 1;
		List<AttributeValuesFrequency> ipuControlTotals = new ArrayList<AttributeValuesFrequency>();
		AbstractAttribute attribute;
		AttributeValue attributeValue;
		List<String> valueList = new ArrayList<String>();
		int line = 1;
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (List<String> aRow : ipuControlTotalsFileContent) {
			if (aRow.size() != ipuControlTotalLineLength) throw new GenstarException("Invalid attribute values frequency format. File: " + ipuControlTotalsFile.getPath() + ", line: " + line);
			line++;
			
			attributeValues.clear();
			for (int col=0; col<(aRow.size() - 1); col+=2) { // Parse each line of the file
				// 1. parse the attribute name column
				attribute = generator.getAttributeByNameOnData(aRow.get(col));
				if (attribute == null) { throw new GenstarException("'" + aRow.get(col) + "' is not a valid attribute. File: " + ipuControlTotalsFile.getPath() + ", line: " + line + "."); }
				if (!controlledAttributes.contains(attribute)) { throw new GenstarException("'" + aRow.get(col) + "' is not a controlled attribute."); }
				if (attributeValues.containsKey(attribute)) { throw new GenstarException("Duplicated attribute : '" + attribute.getNameOnData() + "'"); }
				
				// 2. parse the attribute value column
				valueList.clear();
				String attributeValueString = aRow.get(col+1);
				if (attributeValueString.contains(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER)) { // range value
					StringTokenizer rangeValueToken = new StringTokenizer(attributeValueString, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
					if (rangeValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value: '" + attributeValueString + "'. File: " + ipuControlTotalsFile.getPath()); }
					valueList.add(rangeValueToken.nextToken());
					valueList.add(rangeValueToken.nextToken());
				} else { // unique value
					valueList.add(attributeValueString);
				}
				
				
				attributeValue = attribute.findCorrespondingAttributeValue(valueList);
				if (attributeValue == null) { throw new GenstarException("Attribute value '" + aRow.get(col+1) + "' not found in valid attribute values of " + attribute.getNameOnData()
						+ ". File: " + ipuControlTotalsFile.getPath() + ", line: " + line); }
				
				attributeValues.put(attribute, attributeValue);
			}
			
			// "frequency" is the last column
			int frequency = Integer.parseInt(aRow.get(aRow.size() - 1));
			if (frequency <= 0) { throw new GenstarException("frequency value must be positive. File: " + ipuControlTotalsFile.getPath() + ", line: " + line); }
			
			
			// verify attributeValues
			AttributeValuesFrequency matched = null;
			for (AttributeValuesFrequency avf : generatedControlledAVsFrequencies) {
				if (avf.matchAttributeValuesOnData(attributeValues)) {
					matched = avf;
					break;
				}
			}
			
			// attribute values is not recognized
			if (matched == null) { throw new GenstarException("Line " + line + " is not a valid controlled total. File: " + ipuControlTotalsFile.getPath()); }
			
			// duplication
			for (AttributeValuesFrequency avf : alreadyValidControlledAvfFrequencies) {
				if (avf.matchAttributeValuesOnData(attributeValues)) {
					throw new GenstarException("Duplicated control totals. Line: " + line + ", file: " + ipuControlTotalsFile.getPath());
				}
			}
			
			alreadyValidControlledAvfFrequencies.add(matched);
			generatedControlledAVsFrequencies.remove(matched);
			
			ipuControlTotals.add(new AttributeValuesFrequency(attributeValues, frequency));
		}	
		
		
		return ipuControlTotals;
	}
	
	
	public void buildIpuControlTotalsOfCompoundPopulation(IPopulation compoundPopulation, final String componentPopulationName, final GenstarCSVFile groupControlledAttributesListFile, 
			final GenstarCSVFile componentControlledAttributesListFile, final List<List<String>> groupControlTotalsToBeBuilt, final List<List<String>> componentControlTotalsToBeBuilt) throws GenstarException {
		
		// parameters validation
		if (compoundPopulation == null || groupControlledAttributesListFile == null || componentControlledAttributesListFile == null || groupControlTotalsToBeBuilt == null || componentControlTotalsToBeBuilt == null) {
			throw new GenstarException("Parameters can not be null");
		}
		
		if (!groupControlTotalsToBeBuilt.isEmpty() || !componentControlTotalsToBeBuilt.isEmpty()) {
			throw new GenstarException("Both 'groupControlTotalsToBeBuilt' and 'componentControlTotalsToBeBuilt' must be empty list");
		}
		
		// cache component attributes
		List<AbstractAttribute> componentAttributes = null;
		for (Entity groupEntity : compoundPopulation.getEntities()) {
			IPopulation componentPopulation = groupEntity.getComponentPopulation(componentPopulationName);
			if (componentPopulation != null) {
				componentAttributes = componentPopulation.getAttributes();
				break;
			}
		}
		
		if (componentAttributes == null) {
			throw new GenstarException("No component population '" + componentPopulationName + "' found in compound population '" + compoundPopulation.getName() + "'");
		}

		
		// parse group controlled attributes
		List<List<String>> groupControlledAttributesFileContent = groupControlledAttributesListFile.getContent();
		List<AbstractAttribute> groupControlledAttributes = new ArrayList<AbstractAttribute>();
		for (int line=0; line<groupControlledAttributesFileContent.size(); line++) {
			List<String> row = groupControlledAttributesFileContent.get(line);
			if (row.size() > 1) { throw new GenstarException("Invalid groupControlledAttributesListFile file format (file: " + groupControlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			
			String controlledAttrName = row.get(0);
			AbstractAttribute groupControlledAttribute = compoundPopulation.getAttributeByNameOnData(controlledAttrName);
			if (groupControlledAttribute == null) { throw new GenstarException("Invalid (group) controlled attribute: " + controlledAttrName + ". File: " + groupControlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			if (groupControlledAttributes.contains(groupControlledAttribute)) { throw new GenstarException("Duplicated (group) controlled attribute: " + controlledAttrName + ". File: " + groupControlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			
			groupControlledAttributes.add(groupControlledAttribute);
		}
		 
		// parse component controlled attributes
		Map<String, AbstractAttribute> componentAttributesNameMap = new HashMap<String, AbstractAttribute>();
		for (AbstractAttribute cAttr : componentAttributes) { componentAttributesNameMap.put(cAttr.getNameOnData(), cAttr); }
		List<List<String>> componentControlledAttributesFileContent = componentControlledAttributesListFile.getContent();
		List<AbstractAttribute> componentControlledAttributes = new ArrayList<AbstractAttribute>();
		for (int line=0; line<componentControlledAttributesFileContent.size(); line++) {
			List<String> row = componentControlledAttributesFileContent.get(line);
			if (row.size() > 1) { throw new GenstarException("Invalid componentControlledAttributesListFile file format (file: " + componentControlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			
			String controlledAttrName = row.get(0);
			AbstractAttribute componentControlledAttribute = componentAttributesNameMap.get(controlledAttrName);
			if (componentControlledAttribute == null) { throw new GenstarException("Invalid (component) controlled attribute: " + controlledAttrName + ". File: " + groupControlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			if (componentControlledAttributes.contains(componentControlledAttribute)) { throw new GenstarException("Duplicated (component) controlled attribute: " + controlledAttrName + ". File: " + componentControlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			
			componentControlledAttributes.add(componentControlledAttribute);
		}
		
		// generate attribute values frequencies
		Set<AttributeValuesFrequency> groupAvfs = GenstarUtils.generateAttributeValuesFrequencies(new HashSet<AbstractAttribute>(groupControlledAttributes));
		Set<AttributeValuesFrequency> componentAvfs = GenstarUtils.generateAttributeValuesFrequencies(new HashSet<AbstractAttribute>(componentControlledAttributes));
		
		// compute/update frequencies
		for (Entity groupEntity : compoundPopulation.getEntities()) {
			
			// group frequencies
			for (AttributeValuesFrequency groupAvf : groupAvfs) {
				if (groupAvf.matchEntity(groupControlledAttributes, groupEntity)) {
					groupAvf.increaseFrequency();
				}
			}
			
			// component frequencies (of each group)
			IPopulation componentPopulation = groupEntity.getComponentPopulation(componentPopulationName);
			if (componentPopulation != null) {
				for (Entity componentEntity : componentPopulation.getEntities()) {
					for (AttributeValuesFrequency componentAvf : componentAvfs) {
						if (componentAvf.matchEntity(componentControlledAttributes, componentEntity)) {
							componentAvf.increaseFrequency();
						}
					}
				}
			}
		}
		
		
		// fill groupControlTotalsToBeBuilt
		List<AttributeValuesFrequency> sortedGroupAvfs = new ArrayList<AttributeValuesFrequency>(groupAvfs);
		Collections.sort(sortedGroupAvfs, new GenstarUtils.AttributeValuesFrequencyComparator(groupControlledAttributes));
		for (AttributeValuesFrequency groupAvf : sortedGroupAvfs) {
			List<String> groupControlTotal = new ArrayList<String>();
			for (AbstractAttribute groupControlledAttr : groupControlledAttributes) {
				groupControlTotal.add(groupControlledAttr.getNameOnData());
				groupControlTotal.add(groupAvf.getAttributeValueOnData(groupControlledAttr).toCsvString());
			}
			
			groupControlTotal.add(Integer.toString(groupAvf.getFrequency()));
			groupControlTotalsToBeBuilt.add(groupControlTotal);
		}
		
		
		// fill componentControlTotalsToBeBuilt
		List<AttributeValuesFrequency> sortedComponentAvfs = new ArrayList<AttributeValuesFrequency>(componentAvfs);
		Collections.sort(sortedComponentAvfs, new GenstarUtils.AttributeValuesFrequencyComparator(componentControlledAttributes));
		for (AttributeValuesFrequency componentAvf : componentAvfs) {
			List<String> componentControlTotal = new ArrayList<String>();
			for (AbstractAttribute componentControlledAttr : componentControlledAttributes) {
				componentControlTotal.add(componentControlledAttr.getNameOnData());
				componentControlTotal.add(componentAvf.getAttributeValueOnData(componentControlledAttr).toCsvString());
			}
			
			componentControlTotal.add(Integer.toString(componentAvf.getFrequency()));
			componentControlTotalsToBeBuilt.add(componentControlTotal);
		}
		
		// use GenstarUtils.writeContentToCsvFile(List<List<String>>, String) to write the control totals to CSV file(s)
	}

	
	/*
	public static List<List<String>> generateIpfControlTotals(final GenstarCSVFile attributesFile, final int total) throws GenstarException {
	 */
	public static List<List<String>> generateIpuControlTotals(final GenstarCSVFile attributesFile, final int total) throws GenstarException {
		
		// TODO
		
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
