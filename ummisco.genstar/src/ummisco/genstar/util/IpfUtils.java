package ummisco.genstar.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.SingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.util.GenstarUtils.CSV_FILE_FORMATS;

import com.google.common.collect.Sets;
import com.sun.tools.javac.comp.Todo;

public class IpfUtils {

	// TODO improve this algo to remove bias
	public static List<Integer> findSubsetSum(final int total, final int numberOfElements) throws GenstarException {
		// parameters validation
		if (total < 1) { throw new GenstarException("Parameter total must be positive"); }
		if (numberOfElements < 1) { throw new GenstarException("Parameter numberOfElements must be positive"); }
		if (total < numberOfElements) { throw new GenstarException("total can not be smaller than numberOfElements"); }
		
		int internalTotal = total; 
		
		internalTotal -= (1 * numberOfElements);
		
		List<Integer> result = new ArrayList<Integer>();
		for (int i=0; i<numberOfElements-1; i++) {
			if (internalTotal == 0) { result.add(1); }
			else {

				int centerNumber = internalTotal / (numberOfElements - i);
				
//				int rndNumber = SharedInstances.RandomNumberGenerator.nextInt(internalTotal);
				int rndNumber = SharedInstances.RandomNumberGenerator.nextInt(centerNumber);
				result.add(1 + rndNumber);
				internalTotal -= rndNumber;
			}
		}
		
		result.add(1 + internalTotal);
		
		return result;
	}

	// TODO change name to buildIpfControlledAttributesValuesSubsets
	public static List<List<Map<AbstractAttribute, AttributeValue>>> buildControlledAttributesValuesSubsets(final Set<AbstractAttribute> controlledAttributes) throws GenstarException {
		
		// parameters validation
		if (controlledAttributes == null) { throw new GenstarException("Parameter controlledAttributes can not be null"); }
		if (controlledAttributes.size() < 2) { throw new GenstarException("controlledAttributes must contain at least 2 attributes"); }
		
		Set<Set<AbstractAttribute>> controlledAttributesSubsets = Sets.powerSet(controlledAttributes);
		
		Set<Set<AbstractAttribute>> validControlledAttributesSubSets = new HashSet<Set<AbstractAttribute>>();
		int validSubsetSize = controlledAttributes.size() - 1;
		for (Set<AbstractAttribute> subset : controlledAttributesSubsets) {
			if (subset.size() == validSubsetSize) { validControlledAttributesSubSets.add(subset); }
		}
		
		List<List<Map<AbstractAttribute, AttributeValue>>> resultingControlledAttributesValuesSubsets = new ArrayList<List<Map<AbstractAttribute, AttributeValue>>>();
		for (Set<AbstractAttribute> validSubsetAttributes : validControlledAttributesSubSets) {
			
			// build attributesValuesMaps
			List<Set<AttributeValue>> attributesPossibleValues = new ArrayList<Set<AttributeValue>>();
			for (AbstractAttribute attribute : validSubsetAttributes) {  attributesPossibleValues.add(attribute.values());  }
			List<Map<AbstractAttribute, AttributeValue>> attributeValuesMaps = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
			for (List<AttributeValue> cartesian : Sets.cartesianProduct(attributesPossibleValues)) {
				attributeValuesMaps.add(GenstarUtils.buildAttributeValueMap(validSubsetAttributes, cartesian));
			}
			
			
			List<Map<AbstractAttribute, AttributeValue>> controlledAttributesValuesSubset = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
			
			for (Map<AbstractAttribute, AttributeValue> attributeValuesMap : attributeValuesMaps) {
				Set<AbstractAttribute> subsetAttributes = attributeValuesMap.keySet();
				
//				if (validSubsetAttributes.size() == subsetAttributes.size() && subsetAttributes.containsAll(validSubsetAttributes)) { // TODO redundant condition: validSubsetAttributes.size() == subsetAttributes.size()
				if (subsetAttributes.containsAll(validSubsetAttributes)) {
					controlledAttributesValuesSubset.add(attributeValuesMap);
				}
			}
			
			if (!controlledAttributesValuesSubset.isEmpty()) {
				resultingControlledAttributesValuesSubsets.add(controlledAttributesValuesSubset);
			}
		}
		
		return resultingControlledAttributesValuesSubsets;
	}
	
	
	public void buildControlTotalsOfCompoundPopulation(ISyntheticPopulation compoundPopulation, final String componentPopulationName, final GenstarCSVFile groupControlledAttributesListFile, 
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
			ISyntheticPopulation componentPopulation = groupEntity.getComponentPopulation(componentPopulationName);
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
		Set<AbstractAttribute> groupControlledAttributes = new HashSet<AbstractAttribute>();
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
		Set<AbstractAttribute> componentControlledAttributes = new HashSet<AbstractAttribute>();
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
		Set<AttributeValuesFrequency> groupAvfs = GenstarUtils.generateAttributeValuesFrequencies(groupControlledAttributes);
		Set<AttributeValuesFrequency> componentAvfs = GenstarUtils.generateAttributeValuesFrequencies(componentControlledAttributes);
		
		// compute/update frequencies
		for (Entity groupEntity : compoundPopulation.getEntities()) {
			
			// group frequencies
			for (AttributeValuesFrequency groupAvf : groupAvfs) {
				if (groupAvf.matchEntity(groupControlledAttributes, groupEntity)) {
					groupAvf.increaseFrequency();
				}
			}
			
			// component frequencies (of each group)
			ISyntheticPopulation componentPopulation = groupEntity.getComponentPopulation(componentPopulationName);
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
		
		
		// TODO fill groupControlTotalsToBeBuilt and componentControlTotalsToBeBuilt
		/*
			List<AttributeValuesFrequency> sortedAttributeValueFrequencies = new ArrayList<AttributeValuesFrequency>(fdGenerationRule.getAttributeValuesFrequencies());
			Collections.sort(sortedAttributeValueFrequencies, new GenstarUtils.AttributeValuesFrequencyComparator(generationRuleAttributes));
		 */
		List<AttributeValuesFrequency> sortedGroupAvfs = new ArrayList<AttributeValuesFrequency>(groupAvfs);
//		List<AbstractAttribute> groupControlledAttributesList = new ArrayList<AbstractAttribute>(groupControlledAttributes);
		Collections.sort(sortedGroupAvfs, new GenstarUtils.AttributeValuesFrequencyComparator(new ArrayList<AbstractAttribute>(groupControlledAttributes)));
		for (AttributeValuesFrequency groupAvf : sortedGroupAvfs) {
			
		}
		
		
		List<AttributeValuesFrequency> sortedComponentAvfs = new ArrayList<AttributeValuesFrequency>(componentAvfs);
		Collections.sort(sortedComponentAvfs, new GenstarUtils.AttributeValuesFrequencyComparator(new ArrayList<AbstractAttribute>(componentControlledAttributes)));
		
		// use GenstarUtils.writeControlTotalsToCsvFile(List<List<String>>, String) to write the control totals to CSV file(s)
	}
	
	
	// ? TODO refactor to List<AttributeValuesFrequency> generateIpfControlTotals(final GenstarCSVFile attributesFile, final int total)
	public static List<List<String>> generateIpfControlTotals(final GenstarCSVFile attributesFile, final int total) throws GenstarException {
		// parameters validation
		if (attributesFile == null) { throw new GenstarException("Parameter attributesFile can not be null"); }
		if (total < 1) { throw new GenstarException("Parameter controlTotal must be positive"); }
		
		ISingleRuleGenerator generator = new SingleRuleGenerator("dummy single rule generator");
		AttributeUtils.createAttributesFromCSVFile(generator, attributesFile);
		
		// generate frequencies / control totals
		List<List<Map<AbstractAttribute, AttributeValue>>> controlledAttributesValuesSubsets = buildControlledAttributesValuesSubsets(new HashSet<AbstractAttribute>(generator.getAttributes()));
		List<List<String>> controlTotals = new ArrayList<List<String>>();
		for (List<Map<AbstractAttribute, AttributeValue>> controlledAttributesValuesSubset : controlledAttributesValuesSubsets) {

			List<Integer> subsetSum = findSubsetSum(total, controlledAttributesValuesSubset.size());
			int i=0;
			for (Map<AbstractAttribute, AttributeValue> attributeValueMap : controlledAttributesValuesSubset) {
				
				// write each row
				List<String> controlTotal = new ArrayList<String>();
				for (Map.Entry<AbstractAttribute, AttributeValue> entry : attributeValueMap.entrySet()) {
					controlTotal.add(entry.getKey().getNameOnData());
					controlTotal.add(entry.getValue().toCsvString());
				}
				
				// write frequency
				controlTotal.add(Integer.toString(subsetSum.get(i)));
				i++;

				controlTotals.add(controlTotal);
			}
		}
		
		return controlTotals;
	}


	public static List<Integer> analyseIpfPopulation(final ISyntheticPopulation population, final GenstarCSVFile controlledAttributesListFile, 
			final GenstarCSVFile controlTotalsFile) throws GenstarException {
		
		// parameters validation
		if (population == null || controlledAttributesListFile == null || controlTotalsFile == null) {
			throw new GenstarException("Parameters population, controlledAttributesListFile, controlTotalsFile can not be null");
		}
		
		List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
		List<List<String>> controlledAttributesList = controlledAttributesListFile.getContent();
		if (controlledAttributesList.size() < 2) { throw new GenstarException("The number of controlled attributes must be at least 2 (file: " + controlledAttributesListFile.getPath() + ")."); }
		for (int line=0; line<controlledAttributesList.size(); line++) {
			List<String> row = controlledAttributesList.get(line);
			if (row.size() > 1) { throw new GenstarException("Invalid controlledAttributesListFile file format (file: " + controlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			
			String controlledAttrName = row.get(0);
			AbstractAttribute controlledAttribute = population.getAttributeByNameOnData(controlledAttrName);
			if (controlledAttribute == null) { throw new GenstarException("Invalid controlled attribute: " + controlledAttrName + ". File: " + controlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			if (controlledAttributes.contains(controlledAttribute)) { throw new GenstarException("Duplicated controlled attribute: " + controlledAttrName + ". File: " + controlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			
			controlledAttributes.add(controlledAttribute);
		}

		// read attribute values frequencies from control totals file then do the analysis
		List<AttributeValuesFrequency> attributeValuesFrequencies = parseIpfControlTotalsFile(controlTotalsFile, controlledAttributes);
		List<Integer> generatedFrequencies = new ArrayList<Integer>();
		List<Entity> entities = population.getEntities();
		for (AttributeValuesFrequency avf : attributeValuesFrequencies) {
			int matched = 0;
			for (Entity e : entities) { if (avf.matchEntity(e)) { matched++; } }
			generatedFrequencies.add(matched);
		}
		
		return generatedFrequencies;
	}
	
	// TODO change name to readAttributeValuesFrequenciesFromIpfControlTotalsFile or parseIpfControlTotals
	public static List<AttributeValuesFrequency> parseIpfControlTotalsFile(final GenstarCSVFile controlTotalsFile, 
			final List<AbstractAttribute> controlledAttributes) throws GenstarException {
		
		// parameters validation
		if (controlTotalsFile == null || controlledAttributes == null) {
			throw new GenstarException("Parameters controlTotalsFile, controlledAttributes can not be null");
		}
		
		if (controlledAttributes.size() < 2) { throw new GenstarException("controlledAttributes must have at least two attributes/elements"); }
		
		// ensure that controlledAttributes doesn't contain duplicated (controlled) attributes or belong to different population generators
		ISyntheticPopulationGenerator generator = controlledAttributes.get(0).getPopulationGenerator();
		Map<String, AbstractAttribute> controlledAttributesMap = new HashMap<String, AbstractAttribute>();
		for (AbstractAttribute controlledAttr : controlledAttributes) {
			if (controlledAttributesMap.containsKey(controlledAttr.getNameOnData())) { throw new GenstarException("Duplicated controlled attributes (" + controlledAttr.getNameOnData() + ")"); }
			if (!controlledAttr.getPopulationGenerator().equals(generator)) { throw new GenstarException("Controlled attributes belong to different population generator"); }
			
			controlledAttributesMap.put(controlledAttr.getNameOnData(), controlledAttr);
		}
		
		
		// build controlled attributes values subsets to verify the validity of control totals (file content)
		List<List<Map<AbstractAttribute, AttributeValue>>> controlledAttributesValuesSubsets = buildControlledAttributesValuesSubsets(new HashSet<AbstractAttribute>(controlledAttributes));
		int controlledAttributesValuesSubsetsSize = 0;
		for (List<Map<AbstractAttribute, AttributeValue>> subset : controlledAttributesValuesSubsets) { controlledAttributesValuesSubsetsSize += subset.size(); }
		
		// size verification
		List<List<String>> controlTotalsFileContent = controlTotalsFile.getContent();
		if (controlledAttributesValuesSubsetsSize != controlTotalsFileContent.size()) {
			throw new GenstarException("Mismatched between required/valid number of controlled totals and supplied number of controlled totals. Required values: " 
						+ controlledAttributesValuesSubsetsSize + ", supplied values (in control total file): " + controlTotalsFileContent.size()
						+ ". File: " + controlTotalsFile.getPath());
		}
		
		
		// build generatedAvfsByGroups from controlledAttributesValuesSubsets
		Map<Integer, List<AttributeValuesFrequency>> generatedAvfsByGroups = new HashMap<Integer, List<AttributeValuesFrequency>>();
		Map<Integer, List<AttributeValuesFrequency>> alreadyValidAvfsByGroups = new HashMap<Integer, List<AttributeValuesFrequency>>();
		Map<Integer, Integer> parsedAvfsByGroupsTotalFrequencies = new HashMap<Integer, Integer>();
		int groupId = 0;
		for (List<Map<AbstractAttribute, AttributeValue>> subset : controlledAttributesValuesSubsets) {
			List<AttributeValuesFrequency> avfs = new ArrayList<AttributeValuesFrequency>();
			for (Map<AbstractAttribute, AttributeValue> attributeValues : subset) {
				avfs.add(new AttributeValuesFrequency(attributeValues));
			}
			
			generatedAvfsByGroups.put(groupId, avfs);
			alreadyValidAvfsByGroups.put(groupId, new ArrayList<AttributeValuesFrequency>());
			parsedAvfsByGroupsTotalFrequencies.put(groupId, 0);
			groupId++;
		}
		
		
		// read the control totals file line by line to initialize AttributeValueFrequencies
		int controlTotalLineLength = ((controlledAttributes.size() - 1) * 2) + 1;
		List<AttributeValuesFrequency> avFrequencies = new ArrayList<AttributeValuesFrequency>();
		AbstractAttribute attribute;
		AttributeValue attributeValue;
		List<String> valueList = new ArrayList<String>();
		int line = 1;
		Map<AbstractAttribute, AttributeValue> attributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		for (List<String> aRow : controlTotalsFileContent) {
			if (aRow.size() != controlTotalLineLength) throw new GenstarException("Invalid attribute values frequency format. File: " + controlTotalsFile.getPath() + ", line: " + line);
			line++;
			
			attributeValues.clear();
			for (int col=0; col<(aRow.size() - 1); col+=2) { // Parse each line of the file
				// 1. parse the attribute name column
				attribute = generator.getAttributeByNameOnData(aRow.get(col));
				if (attribute == null) { throw new GenstarException("'" + aRow.get(col) + "' is not a valid attribute. File: " + controlTotalsFile.getPath() + ", line: " + line + "."); }
				if (!controlledAttributes.contains(attribute)) { throw new GenstarException("'" + aRow.get(col) + "' is not a controlled attribute."); }
				if (attributeValues.containsKey(attribute)) { throw new GenstarException("Duplicated attribute : '" + attribute.getNameOnData() + "'"); }
				
				// 2. parse the attribute value column
				valueList.clear();
				String attributeValueString = aRow.get(col+1);
				if (attributeValueString.contains(CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER)) { // range value
					StringTokenizer rangeValueToken = new StringTokenizer(attributeValueString, CSV_FILE_FORMATS.ATTRIBUTE_METADATA.MIN_MAX_VALUE_DELIMITER);
					if (rangeValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value: '" + attributeValueString + "'. File: " + controlTotalsFile.getPath()); }
					valueList.add(rangeValueToken.nextToken());
					valueList.add(rangeValueToken.nextToken());
				} else { // unique value
					valueList.add(attributeValueString);
				}
				
				
				attributeValue = attribute.findCorrespondingAttributeValue(valueList);
				if (attributeValue == null) { throw new GenstarException("Attribute value '" + aRow.get(col+1) + "' not found in valid attribute values of " + attribute.getNameOnData()
						+ ". File: " + controlTotalsFile.getPath() + ", line: " + line); }
				
				attributeValues.put(attribute, attributeValue);
			}
			
			// "frequency" is the last column
			int frequency = Integer.parseInt(aRow.get(aRow.size() - 1));
			if (frequency <= 0) { throw new GenstarException("frequency value must be positive. File: " + controlTotalsFile.getPath() + ", line: " + line); }
			
			
			// verify attributeValues
			AttributeValuesFrequency matched = null;
			int matchedGroupIdentity = -1;
			for (Integer groupIdentity : generatedAvfsByGroups.keySet()) {
				for (AttributeValuesFrequency avf : generatedAvfsByGroups.get(groupIdentity)) {
					if (avf.matchAttributeValues(attributeValues)) {
						matched = avf;
						matchedGroupIdentity = groupIdentity;
						break;
					}
				}
				
				if (matched != null) { break; }
			}
			
			
			// attribute values is not recognized
			if (matched == null) { throw new GenstarException("Line " + line + " is not a valid controlled total. File: " + controlTotalsFile.getPath()); }
			
			// duplication
			for (Integer groupIdentity : alreadyValidAvfsByGroups.keySet()) {
				for (AttributeValuesFrequency avf : alreadyValidAvfsByGroups.get(groupIdentity)) {
					if (avf.matchAttributeValues(attributeValues)) {
						throw new GenstarException("Duplicated control totals. Line: " + line + ", file: " + controlTotalsFile.getPath());
					}
				}
			}
			
			alreadyValidAvfsByGroups.get(matchedGroupIdentity).add(matched);
			generatedAvfsByGroups.get(matchedGroupIdentity).remove(matched);
			
			avFrequencies.add(new AttributeValuesFrequency(attributeValues, frequency));
			int groupTotalFrequency = parsedAvfsByGroupsTotalFrequencies.get(matchedGroupIdentity) + frequency;
			parsedAvfsByGroupsTotalFrequencies.put(matchedGroupIdentity, groupTotalFrequency);
		}	
		

		// verify that totals of all groups are equal 
		int groupTotal = -1;
		boolean allTotalsAreEqual = true;
		for (Integer groupIdentity : parsedAvfsByGroupsTotalFrequencies.keySet()) {
			if (groupTotal == -1) {
				groupTotal = parsedAvfsByGroupsTotalFrequencies.get(groupIdentity);
			} else {
				if (groupTotal != parsedAvfsByGroupsTotalFrequencies.get(groupIdentity)) {
					allTotalsAreEqual = false;
					break;
				}
			}
		}
		
		if (!allTotalsAreEqual) {
			
			// construct detail error message 
			StringBuffer messageDetail = new StringBuffer();
			int groupIndex = 0;
			Set<Integer> groupIDs = alreadyValidAvfsByGroups.keySet();
			for (int groupIdentity : groupIDs) {
				messageDetail.append("[");
				
				messageDetail.append("(");
				Set<AbstractAttribute> attributes = alreadyValidAvfsByGroups.get(groupIdentity).get(0).getAttributes();
				int attrIndex = 0;
				for (AbstractAttribute attr : attributes) {
					messageDetail.append(attr.getNameOnData());
					if (attrIndex < attributes.size() - 1) { messageDetail.append(", "); }
					attrIndex++;
				}
				messageDetail.append(" : " + parsedAvfsByGroupsTotalFrequencies.get(groupIdentity));
				messageDetail.append(")");

				messageDetail.append("]");
				if (groupIndex < groupIDs.size() - 1) { messageDetail.append(", "); }
				
				groupIndex++;
			}
			
			throw new GenstarException("Sum control totals of controlled attribute groups are not equal: " + messageDetail.toString()
					+ " . File: " + controlTotalsFile.getPath()); 
		}
		
		
		return avFrequencies;
	}

	
	// TODO remove, no class uses this method
	public static GenstarCSVFile writeAnalysisResultToFile(final GenstarCSVFile controlTotalsFile, final List<Integer> analysisResult, final String csvOutputFilePath) throws GenstarException {
		
		// parameters validation
		if (controlTotalsFile == null || analysisResult == null || csvOutputFilePath == null) {
			throw new GenstarException("Parameters controlTotalsFile, analysisResult, csvOutputFilePath can not be null");
		}
		
		if (controlTotalsFile.getRows() != analysisResult.size()) {
			throw new GenstarException("controlTotalsFile's row is different from analysisResult's size (" + controlTotalsFile.getRows() + " v.s. " + analysisResult.size() + ")");
		}
		
		// write analysis result to file
		CsvWriter writer = new CsvWriter(csvOutputFilePath);
		try {
			int line=0;
			for (List<String> row : controlTotalsFile.getContent()) {
				String[] newRow = new String[row.size() + 1];
				newRow = Arrays.copyOf(row.toArray(new String[0]), newRow.length);
				newRow[newRow.length - 1] = analysisResult.get(line).toString();
				
				writer.writeRecord(newRow);
				line++;
			}
		} catch (IOException ioe) {
			throw new GenstarException(ioe);
		} finally {
			writer.close();
		}
		
		return new GenstarCSVFile(csvOutputFilePath, false);
	}
}