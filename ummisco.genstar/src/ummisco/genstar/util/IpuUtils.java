package ummisco.genstar.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import ummisco.genstar.exception.GenstarException;
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
				if (avf.matchAttributeValues(attributeValues)) {
					matched = avf;
					break;
				}
			}
			
			// attribute values is not recognized
			if (matched == null) { throw new GenstarException("Line " + line + " is not a valid controlled total. File: " + ipuControlTotalsFile.getPath()); }
			
			// duplication
			for (AttributeValuesFrequency avf : alreadyValidControlledAvfFrequencies) {
				if (avf.matchAttributeValues(attributeValues)) {
					throw new GenstarException("Duplicated control totals. Line: " + line + ", file: " + ipuControlTotalsFile.getPath());
				}
			}
			
			alreadyValidControlledAvfFrequencies.add(matched);
			generatedControlledAVsFrequencies.remove(matched);
			
			ipuControlTotals.add(new AttributeValuesFrequency(attributeValues, frequency));
		}	
		
		
		return ipuControlTotals;
	}
	
	/*
	public static List<List<String>> generateIpfControlTotals(final GenstarCSVFile attributesFile, final int total) throws GenstarException {
	 */
	public static List<List<String>> generateIpuControlTotals(final GenstarCSVFile attributesFile, final int total) throws GenstarException {
		
		
		
		throw new UnsupportedOperationException("Not yet implemented");
	}
}
