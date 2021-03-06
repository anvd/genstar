package ummisco.genstar.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.IpfGenerationRule;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttributeWithRangeInput;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.Population;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.metamodel.sample_data.CompoundSampleData;
import ummisco.genstar.metamodel.sample_data.ISampleData;
import ummisco.genstar.metamodel.sample_data.SampleData;

import com.google.common.collect.Sets;

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
				
				int rndNumber = SharedInstances.RandomNumberGenerator.nextInt(centerNumber);
				result.add(1 + rndNumber);
				internalTotal -= rndNumber;
			}
		}
		
		result.add(1 + internalTotal);
		
		return result;
	}
	
	
	public static IPopulation extractIpfSinglePopulation(final IPopulation originalPopulation, final float percentage, final Set<AbstractAttribute> ipfControlledAttributes, 
			final UniqueValuesAttributeWithRangeInput idAttribute) throws GenstarException {
		
		// 0. parameters validation
		if (originalPopulation == null) { throw new GenstarException("'originalPopulation' parameter can not be null"); }
		if (percentage <= 0 || percentage > 100) { throw new GenstarException("value of 'percentage' parameter must be in (0, 100] range"); }
		if (ipfControlledAttributes == null || ipfControlledAttributes.isEmpty()) { throw new GenstarException("'ipfControlledAttributes' parameter can neither be null nor empty"); }
		
		for (AbstractAttribute controlledAttr : ipfControlledAttributes) {
			if (!originalPopulation.containAttribute(controlledAttr)) {
				throw new GenstarException("\'" + controlledAttr.getNameOnData() + "\' is not a valid attribute of the population");
			}
		}
		
		if (idAttribute != null) {
			if (!originalPopulation.containAttribute(idAttribute)) {
				throw new GenstarException("\'" + idAttribute.getNameOnData() + "\' is not a valid attribute of the population");
			}
			
			if (ipfControlledAttributes.contains(idAttribute)) {
				throw new GenstarException("\'" + idAttribute.getNameOnData() + "\' can not be a controlled attribute");
			}
		}
		
		
		// 1. build entity categories (note that each entity can belong to several categories at the same time)
		Map<AttributeValuesFrequency, List<Entity>> entityCategories = buildIpfEntityCategories(originalPopulation, ipfControlledAttributes);
		
		
		// 2. Build extracted population
		IPopulation extractedPopulation = new Population(PopulationType.SAMPLE_DATA_POPULATION, originalPopulation.getName(), originalPopulation.getAttributes());
		extractedPopulation.addGroupReferences(originalPopulation.getGroupReferences());
		extractedPopulation.addComponentReferences(originalPopulation.getGroupReferences());
		
		
		// 2.1. firstly, try to satisfy that each attributeValueSet has one selected entity in the extracted population
		int recodedIdAttributeValue = -1;
		if (idAttribute != null) { recodedIdAttributeValue = idAttribute.getMinValue().getIntValue(); }
		for (List<Entity> eCategory : entityCategories.values()) {
			int entityIndex = SharedInstances.RandomNumberGenerator.nextInt(eCategory.size());
			Entity selectedEntity = eCategory.get(entityIndex);
			Entity createdEntity = extractedPopulation.createEntity(selectedEntity);
			
			// recode id attribute
			if (idAttribute != null) {
				GenstarUtils.recodeIdAttribute(createdEntity, idAttribute, null, null, recodedIdAttributeValue);
				recodedIdAttributeValue++;
			}
		}

		
		// 2.2. secondly, satisfy the "percentage" condition if possible
		int alreadyExtractedEntities = entityCategories.size();
		int nbOfAvailableEntities = 0; // note that nbOfAvailableEntities can be bigger then originalPopulation.getNbOfEntities()
		List<AttributeValuesFrequency> avfList = new ArrayList<AttributeValuesFrequency>(entityCategories.keySet());
		for (AttributeValuesFrequency avf : avfList) { nbOfAvailableEntities += avf.getFrequency(); }
		int tobeExtractedEntities = (int) (((float)originalPopulation.getNbOfEntities()) * percentage / 100);
		if (tobeExtractedEntities > alreadyExtractedEntities) {
			int entitiesLeftToExtract = tobeExtractedEntities - alreadyExtractedEntities;
			
			for (int i=0; i<entitiesLeftToExtract; i++) {
				int entityIndex = SharedInstances.RandomNumberGenerator.nextInt(nbOfAvailableEntities);
				
				AttributeValuesFrequency selectedAvf = null;
				for (AttributeValuesFrequency avf : avfList) {
					if (entityIndex >= avf.getFrequency()) {
						entityIndex -= avf.getFrequency();
					} else {
						selectedAvf = avf;
						break;
					}
				}
				
				Entity selectedEntity = entityCategories.get(selectedAvf).get(entityIndex);
				Entity createdEntity = extractedPopulation.createEntity(selectedEntity);
				
				// recode id attribute
				if (idAttribute != null) {
					GenstarUtils.recodeIdAttribute(createdEntity, idAttribute, null, null, recodedIdAttributeValue);
					recodedIdAttributeValue++;
				}
			}
		}
		
		return extractedPopulation;
	}
	
	
	public static IPopulation extractIpfCompoundPopulation(final IPopulation originalCompoundPopulation, final float percentage, final Set<AbstractAttribute> ipfControlledAttributes,
			final UniqueValuesAttributeWithRangeInput groupIdAttributeOnGroupEntity, final UniqueValuesAttributeWithRangeInput groupIdAttributeOnComponentEntity, final String componentPopulationName) throws GenstarException {
		
		// 0. parameters validation
		if (originalCompoundPopulation == null) { throw new GenstarException("'originalCompoundPopulation' parameter can not be null"); }
		if (percentage <= 0 || percentage > 100) { throw new GenstarException("value of 'percentage' parameter must be in (0, 100] range"); }
		if (ipfControlledAttributes == null || ipfControlledAttributes.isEmpty()) { throw new GenstarException("'ipfControlledAttributes' parameter can neither be null nor empty"); }
		if (groupIdAttributeOnGroupEntity == null) { throw new GenstarException("'groupIdAttributeOnGroupEntity' parameter can not be null"); }
		if (groupIdAttributeOnComponentEntity == null) { throw new GenstarException("'groupIdAttributeOnComponentEntity' can not be null"); }
		if (componentPopulationName == null) { throw new GenstarException("'componentPopulationName' can not be null"); }
		
		for (AbstractAttribute controlledAttr : ipfControlledAttributes) {
			if (!originalCompoundPopulation.containAttribute(controlledAttr)) {
				throw new GenstarException("\'" + controlledAttr.getNameOnData() + "\' is not a valid attribute of the population");
			}
		}
		
		if (ipfControlledAttributes.contains(groupIdAttributeOnGroupEntity)) {
			throw new GenstarException("'groupIdAttributeOnGroupEntity' can not be a controlled attribute");
		}
		
		
		// 1. build entity categories (note that each entity can belong to several categories at the same time)
		Map<AttributeValuesFrequency, List<Entity>> entityCategories = buildIpfEntityCategories(originalCompoundPopulation, ipfControlledAttributes);

		
		// 2. Build extracted population
		IPopulation extractedPopulation = new Population(PopulationType.SAMPLE_DATA_POPULATION, originalCompoundPopulation.getName(), originalCompoundPopulation.getAttributes());
		extractedPopulation.addGroupReferences(originalCompoundPopulation.getGroupReferences());
		extractedPopulation.addComponentReferences(originalCompoundPopulation.getComponentReferences());
		

		// 2.1. firstly, try to satisfy that each attributeValueSet has one selected entity in the extracted population
		int recodedIdAttributeValue = groupIdAttributeOnGroupEntity.getMinValue().getIntValue(); // recoded ID attribute value begins with the minimal supported value of the ID attribute
		for (List<Entity> eCategory : entityCategories.values()) {
			int entityIndex = SharedInstances.RandomNumberGenerator.nextInt(eCategory.size());
			
			Entity selectedEntity = eCategory.get(entityIndex);
			Entity createdEntity = extractedPopulation.createEntity(selectedEntity);

			// recode id attribute
			GenstarUtils.recodeIdAttribute(createdEntity, groupIdAttributeOnGroupEntity, 
					 groupIdAttributeOnComponentEntity, componentPopulationName, recodedIdAttributeValue);
			recodedIdAttributeValue++;
		}

		
		// 2.2. secondly, satisfy the "percentage" condition if possible
		int alreadyExtractedEntities = entityCategories.size();
		int nbOfAvailableEntities = 0; // note that nbOfAvailableEntities can be bigger then originalPopulation.getNbOfEntities()
		List<AttributeValuesFrequency> avfList = new ArrayList<AttributeValuesFrequency>(entityCategories.keySet());
		for (AttributeValuesFrequency avf : avfList) { nbOfAvailableEntities += avf.getFrequency(); }
		int tobeExtractedEntities = (int) (((float)originalCompoundPopulation.getNbOfEntities()) * percentage / 100);
		if (tobeExtractedEntities > alreadyExtractedEntities) {
			int entitiesLeftToExtract = tobeExtractedEntities - alreadyExtractedEntities;
			
			for (int i=0; i<entitiesLeftToExtract; i++) {
				int entityIndex = SharedInstances.RandomNumberGenerator.nextInt(nbOfAvailableEntities);
				
				AttributeValuesFrequency selectedAvf = null;
				for (AttributeValuesFrequency avf : avfList) {
					if (entityIndex >= avf.getFrequency()) {
						entityIndex -= avf.getFrequency();
					} else {
						selectedAvf = avf;
						break;
					}
				}
				
				Entity selectedEntity = entityCategories.get(selectedAvf).get(entityIndex);
				Entity createdEntity = extractedPopulation.createEntity(selectedEntity);

				// recode id attribute
				GenstarUtils.recodeIdAttribute(createdEntity, groupIdAttributeOnGroupEntity, 
						 groupIdAttributeOnComponentEntity, componentPopulationName, recodedIdAttributeValue);
				recodedIdAttributeValue++;
			}
		}

		
		return extractedPopulation;
	}
	
	
	public static Map<AttributeValuesFrequency, List<Entity>> buildIpfEntityCategories(final IPopulation population, final Set<AbstractAttribute> ipfControlledAttributes) throws GenstarException {
		
		// 0. TODO parameters validation
		
		
		// 1. build attributeValuesFrequencies (GenstarUtils.generateAttributeValuesFrequencies(attributes))
		Set<AttributeValuesFrequency> attributeValuesFrequencies = new HashSet<AttributeValuesFrequency>();
		
		List<List<Map<AbstractAttribute, AttributeValue>>> ipfControlledAttributesValuesSubsets = buildIpfControlledAttributesValuesSubsets(ipfControlledAttributes);
		for (List<Map<AbstractAttribute, AttributeValue>> ipfControlledAttributesValuesSubset : ipfControlledAttributesValuesSubsets) {
			for (Map<AbstractAttribute, AttributeValue > ipfControlledAttributesValues : ipfControlledAttributesValuesSubset) {
				attributeValuesFrequencies.add(new AttributeValuesFrequency(ipfControlledAttributesValues));
			}
		}
		
		// 2. build entityCategories
		Map<AttributeValuesFrequency, List<Entity>> entityCategories = new HashMap<AttributeValuesFrequency, List<Entity>>();
		for (AttributeValuesFrequency avf : attributeValuesFrequencies) {
			entityCategories.put(avf, new ArrayList<Entity>());
		}
		
		List<Entity> entities = population.getEntities();
		for (AttributeValuesFrequency avf : entityCategories.keySet()) {
			List<Entity> avfEntities = entityCategories.get(avf);
			
			for (Entity e : entities) {
				if (avf.matchEntity(e)) {
					avf.increaseFrequency();
					avfEntities.add(e);
				}
			}
		}
		
		
		// 3. if there is no matching entity for any attribute values sets, then throw exception
		for (AttributeValuesFrequency avf : entityCategories.keySet()) {
			if (avf.getFrequency() == 0) {
				throw new GenstarException("No matching entity for attribute values set: " + avf.toString());
			}
		}
		 
		
		return entityCategories;
	}


	public static List<List<Map<AbstractAttribute, AttributeValue>>> buildIpfControlledAttributesValuesSubsets(final Set<AbstractAttribute> controlledAttributes) throws GenstarException {
		
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
			for (AbstractAttribute attribute : validSubsetAttributes) {  attributesPossibleValues.add(attribute.valuesOnData());  }
			List<Map<AbstractAttribute, AttributeValue>> attributeValuesMaps = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
			for (List<AttributeValue> cartesian : Sets.cartesianProduct(attributesPossibleValues)) {
				attributeValuesMaps.add(GenstarUtils.buildAttributeValueMap(validSubsetAttributes, cartesian));
			}
			
			
			List<Map<AbstractAttribute, AttributeValue>> controlledAttributesValuesSubset = new ArrayList<Map<AbstractAttribute, AttributeValue>>();
			
			for (Map<AbstractAttribute, AttributeValue> attributeValuesMap : attributeValuesMaps) {
				Set<AbstractAttribute> subsetAttributes = attributeValuesMap.keySet();
				
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
	
		
	public static List<List<String>> generateIpfControlTotalsFromTotal(final GenstarCsvFile attributesFile, final int total) throws GenstarException {
		// parameters validation
		if (attributesFile == null) { throw new GenstarException("Parameter attributesFile can not be null"); }
		if (total < 1) { throw new GenstarException("Parameter controlTotal must be positive"); }
		
		SampleBasedGenerator generator = new SampleBasedGenerator("dummy single rule generator");
		AttributeUtils.createAttributesFromCsvFile(generator, attributesFile);
		
		// generate frequencies / control totals
		List<List<Map<AbstractAttribute, AttributeValue>>> controlledAttributesValuesSubsets = buildIpfControlledAttributesValuesSubsets(new HashSet<AbstractAttribute>(generator.getAttributes()));
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


	public static List<Integer> analyseIpfPopulation(final IPopulation population, final GenstarCsvFile controlledAttributesListFile, 
			final GenstarCsvFile controlTotalsFile, final String analysisOutputFilePath) throws GenstarException {
		
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
		List<AttributeValuesFrequency> attributeValuesFrequencies = parseAttributeValuesFrequenciesFromIpfControlTotalsFile(controlledAttributes, controlTotalsFile);
		List<Integer> generatedFrequencies = new ArrayList<Integer>();
		List<Entity> entities = population.getEntities();
		for (AttributeValuesFrequency avf : attributeValuesFrequencies) {
			int matched = 0;
			for (Entity e : entities) { if (avf.matchEntity(e)) { matched++; } }
			generatedFrequencies.add(matched);
		}
		
		// write the analysis result to file if necessary
		if (analysisOutputFilePath != null) {
			List<List<String>> analysisResultContent = new ArrayList<List<String>>();
			List<List<String>> ipfControlTotalsContent = controlTotalsFile.getContent();
			for (int i=0; i<generatedFrequencies.size(); i++) {
				List<String> analysisResultRowContent = new ArrayList<String>(ipfControlTotalsContent.get(i));
				analysisResultRowContent.add(Integer.toString(generatedFrequencies.get(i)));
				
				analysisResultContent.add(analysisResultRowContent);
			}
			
			GenstarUtils.writeStringContentToCsvFile(analysisResultContent, analysisOutputFilePath);			
		}
		
		
		return generatedFrequencies;
	}
	

	public static List<AttributeValuesFrequency> parseAttributeValuesFrequenciesFromIpfControlTotalsFile(final List<AbstractAttribute> controlledAttributes, 
			final GenstarCsvFile ipfControlTotalsFile) throws GenstarException {
		
		// parameters validation
		if (ipfControlTotalsFile == null || controlledAttributes == null) {
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
		List<List<Map<AbstractAttribute, AttributeValue>>> controlledAttributesValuesSubsets = buildIpfControlledAttributesValuesSubsets(new HashSet<AbstractAttribute>(controlledAttributes));
		int controlledAttributesValuesSubsetsSize = 0;
		for (List<Map<AbstractAttribute, AttributeValue>> subset : controlledAttributesValuesSubsets) { controlledAttributesValuesSubsetsSize += subset.size(); }
		
		// size verification
		List<List<String>> controlTotalsFileContent = ipfControlTotalsFile.getContent();
		if (controlledAttributesValuesSubsetsSize != controlTotalsFileContent.size()) {
			throw new GenstarException("Mismatched between required/valid number of controlled totals and supplied number of controlled totals. Required values: " 
						+ controlledAttributesValuesSubsetsSize + ", supplied values (in control total file): " + controlTotalsFileContent.size()
						+ ". File: " + ipfControlTotalsFile.getPath());
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
			if (aRow.size() != controlTotalLineLength) throw new GenstarException("Invalid attribute values frequency format. File: " + ipfControlTotalsFile.getPath() + ", line: " + line);
			line++;
			
			attributeValues.clear();
			for (int col=0; col<(aRow.size() - 1); col+=2) { // Parse each line of the file
				// 1. parse the attribute name column
				attribute = generator.getAttributeByNameOnData(aRow.get(col));
				if (attribute == null) { throw new GenstarException("'" + aRow.get(col) + "' is not a valid attribute. File: " + ipfControlTotalsFile.getPath() + ", line: " + line + "."); }
				if (!controlledAttributes.contains(attribute)) { throw new GenstarException("'" + aRow.get(col) + "' is not a controlled attribute."); }
				if (attributeValues.containsKey(attribute)) { throw new GenstarException("Duplicated attribute : '" + attribute.getNameOnData() + "'"); }
				
				// 2. parse the attribute value column
				valueList.clear();
				String attributeValueString = aRow.get(col+1);
				if (attributeValueString.contains(CSV_FILE_FORMATS.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER)) { // range value
					StringTokenizer rangeValueToken = new StringTokenizer(attributeValueString, CSV_FILE_FORMATS.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER);
					if (rangeValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value: '" + attributeValueString + "'. File: " + ipfControlTotalsFile.getPath()); }
					valueList.add(rangeValueToken.nextToken());
					valueList.add(rangeValueToken.nextToken());
				} else { // unique value
					valueList.add(attributeValueString);
				}
				
				
				attributeValue = attribute.getMatchingAttributeValueOnData(valueList);
				if (attributeValue == null) { throw new GenstarException("Attribute value '" + aRow.get(col+1) + "' not found in valid attribute values of " + attribute.getNameOnData()
						+ ". File: " + ipfControlTotalsFile.getPath() + ", line: " + line); }
				
				attributeValues.put(attribute, attributeValue);
			}
			
			// "frequency" is the last column
			int frequency = Integer.parseInt(aRow.get(aRow.size() - 1));
			if (frequency <= 0) { throw new GenstarException("frequency value must be positive. File: " + ipfControlTotalsFile.getPath() + ", line: " + line); }
			
			
			// verify attributeValues
			AttributeValuesFrequency matched = null;
			int matchedGroupIdentity = -1;
			for (Integer groupIdentity : generatedAvfsByGroups.keySet()) {
				for (AttributeValuesFrequency avf : generatedAvfsByGroups.get(groupIdentity)) {
					if (avf.matchAttributeValuesOnData(attributeValues)) {
						matched = avf;
						matchedGroupIdentity = groupIdentity;
						break;
					}
				}
				
				if (matched != null) { break; }
			}
			
			
			// attribute values is not recognized
			if (matched == null) { throw new GenstarException("Line " + line + " is not a valid controlled total. File: " + ipfControlTotalsFile.getPath()); }
			
			// duplication
			for (Integer groupIdentity : alreadyValidAvfsByGroups.keySet()) {
				for (AttributeValuesFrequency avf : alreadyValidAvfsByGroups.get(groupIdentity)) {
					if (avf.matchAttributeValuesOnData(attributeValues)) {
						throw new GenstarException("Duplicated control totals. Line: " + line + ", file: " + ipfControlTotalsFile.getPath());
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
					+ " . File: " + ipfControlTotalsFile.getPath()); 
		}
		
		
		return avFrequencies;
	}

	
	public static void createIpfGenerationRule(final SampleBasedGenerator generator, final String ruleName, final GenstarCsvFile sampleFile,
			final GenstarCsvFile controlledAttributesFile, final GenstarCsvFile controlledTotalsFile, 
			final GenstarCsvFile supplementaryAttributesFile, final int maxIterations) throws GenstarException {
		
		IpfGenerationRule rule = new IpfGenerationRule(generator, ruleName, controlledAttributesFile, controlledTotalsFile, supplementaryAttributesFile, maxIterations);
		
		ISampleData sampleData = new SampleData(generator.getPopulationName(), generator.getAttributes(), sampleFile);
		rule.setSampleData(sampleData);
		
		generator.setGenerationRule(rule);
		generator.setNbOfEntities(rule.getIPF().getNbOfEntitiesToGenerate());
	}
	
	
	public static void createCompoundIpfGenerationRule(final SampleBasedGenerator groupGenerator, final String ruleName, final GenstarCsvFile groupSampleFile,
			final GenstarCsvFile groupControlledAttributesFile, final GenstarCsvFile groupControlledTotalsFile, final GenstarCsvFile groupSupplementaryAttributesFile,
			final String componentReferenceOnGroup, final String groupIdAttributeNameOnDataOfGroupEntity,
			final GenstarCsvFile componentSampleFile, final GenstarCsvFile componentAttributesFile, final String componentPopulationName, 
			final String groupReferenceOnComponent, final String groupIdAttributeNameOnDataOfComponentEntity, final int maxIterations) throws GenstarException {

		IpfGenerationRule rule = new IpfGenerationRule(groupGenerator, ruleName, groupControlledAttributesFile, groupControlledTotalsFile, groupSupplementaryAttributesFile, maxIterations);

		SampleBasedGenerator componentGenerator = new SampleBasedGenerator("Component Generator");
		AttributeUtils.createAttributesFromCsvFile(componentGenerator, componentAttributesFile);
		componentGenerator.setPopulationName(componentPopulationName);
		
		AbstractAttribute groupIdAttributeOnGroup = rule.getAttributeByNameOnData(groupIdAttributeNameOnDataOfGroupEntity);
		if (groupIdAttributeOnGroup == null) { throw new GenstarException("'" + groupIdAttributeNameOnDataOfGroupEntity + "' is not a valid attribute"); }
		
		AbstractAttribute groupIdAttributeOnComponent = componentGenerator.getAttributeByNameOnData(groupIdAttributeNameOnDataOfComponentEntity);
		if (groupIdAttributeOnComponent == null) { throw new GenstarException("'" + groupIdAttributeOnComponent + "' is not a valid attribute"); }
		
		ISampleData groupSampleData = new SampleData(groupGenerator.getPopulationName(), groupGenerator.getAttributes(), groupSampleFile);
		if (componentReferenceOnGroup != null) { 
			groupSampleData.getSampleEntityPopulation().addComponentReference(componentGenerator.getPopulationName(), componentReferenceOnGroup);
		}
		
		ISampleData componentSampleData = new SampleData(componentGenerator.getPopulationName(), componentGenerator.getAttributes(), componentSampleFile);
		if (groupReferenceOnComponent != null) {
			componentSampleData.getSampleEntityPopulation().addGroupReference(groupGenerator.getPopulationName(), groupReferenceOnComponent);
		}
		
		ISampleData compoundSampleData = new CompoundSampleData(groupSampleData, componentSampleData, groupIdAttributeOnGroup, groupIdAttributeOnComponent);
		rule.setSampleData(compoundSampleData);

		groupGenerator.setGenerationRule(rule);
		groupGenerator.setNbOfEntities(rule.getIPF().getNbOfEntitiesToGenerate());
	}
	
}
