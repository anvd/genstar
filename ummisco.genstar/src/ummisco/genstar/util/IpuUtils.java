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
import ummisco.genstar.ipu.IpuGenerationRule;
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

public class IpuUtils {

	public static List<AttributeValuesFrequency> parseAttributeValuesFrequenciesFromIpuControlTotalsFile(final List<AbstractAttribute> controlledAttributes, final GenstarCsvFile ipuControlTotalsFile) throws GenstarException {
		
		// 1. parameters validation
		if (controlledAttributes.isEmpty()) { throw new GenstarException("controlledAttributes must have at least one attributes/elements"); }
		
		// 2. ensure that controlledAttributes doesn't contain duplicated (controlled) attributes or belong to different population generators
		final ISyntheticPopulationGenerator generator = controlledAttributes.get(0).getPopulationGenerator();
		Map<String, AbstractAttribute> controlledAttributesMap = new HashMap<String, AbstractAttribute>();
		for (AbstractAttribute controlledAttr : controlledAttributes) {
			if (controlledAttributesMap.containsKey(controlledAttr.getNameOnData())) { throw new GenstarException("Duplicated controlled attributes (" + controlledAttr.getNameOnData() + ")"); }
			if (!controlledAttr.getPopulationGenerator().equals(generator)) { throw new GenstarException("Controlled attributes belong to different population generator"); }
			
			controlledAttributesMap.put(controlledAttr.getNameOnData(), controlledAttr);
		}
		
		// 3. ensure that controlledAttributes doesn't contain any instance of UniqueValueWithRangeInput
		for (AbstractAttribute controlledAttr : controlledAttributes) {
			if (controlledAttr instanceof UniqueValuesAttributeWithRangeInput) {
				throw new GenstarException("UniqueValuesAttributeWithRangeInput can not be controlled attribute (" + controlledAttr.getNameOnData() + ")");
			}
		}
		
		// 4. generate IPU controlled attributes values frequencies to verify the validity of control totals (file content)
		Set<AttributeValuesFrequency> generatedControlledAVsFrequencies = GenstarUtils.generateAttributeValuesFrequencies(new HashSet<AbstractAttribute>(controlledAttributes));
		Set<AttributeValuesFrequency> alreadyValidControlledAvfFrequencies = new HashSet<AttributeValuesFrequency>();
		
		// 5. IPU control totals file size verification
		List<List<String>> ipuControlTotalsFileContent = ipuControlTotalsFile.getContent();
		if (generatedControlledAVsFrequencies.size() != ipuControlTotalsFileContent.size()) {
			throw new GenstarException("Mismatched between required/valid number of IPU controlled totals and supplied number of IPU controlled totals. Required values: " 
				+ generatedControlledAVsFrequencies.size() + ", supplied values (in control total file): " + ipuControlTotalsFileContent.size() + ". File: " + ipuControlTotalsFile.getPath());
		}
		
		// 6. read the IPU control totals file line by line to initialize AttributeValueFrequencies
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
				if (attributeValueString.contains(CSV_FILE_FORMATS.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER)) { // range value
					StringTokenizer rangeValueToken = new StringTokenizer(attributeValueString, CSV_FILE_FORMATS.ATTRIBUTES.MIN_MAX_VALUE_DELIMITER);
					if (rangeValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value: '" + attributeValueString + "'. File: " + ipuControlTotalsFile.getPath()); }
					valueList.add(rangeValueToken.nextToken());
					valueList.add(rangeValueToken.nextToken());
				} else { // unique value
					valueList.add(attributeValueString);
				}
				
				
				attributeValue = attribute.getMatchingAttributeValueOnData(valueList);
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
	
	
	public static void buildIpuControlTotalsOfCompoundPopulation(final IPopulation compoundPopulation, final String componentPopulationName, final GenstarCsvFile groupControlledAttributesListFile, 
			final GenstarCsvFile componentControlledAttributesListFile, final List<AttributeValuesFrequency> groupControlTotalsToBeBuilt, final List<AttributeValuesFrequency> componentControlTotalsToBeBuilt) throws GenstarException {
		
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
		Map<String, AbstractAttribute> componentAttributesNameOnDataMap = new HashMap<String, AbstractAttribute>();
		for (AbstractAttribute cAttr : componentAttributes) { componentAttributesNameOnDataMap.put(cAttr.getNameOnData(), cAttr); }
		List<List<String>> componentControlledAttributesFileContent = componentControlledAttributesListFile.getContent();
		List<AbstractAttribute> componentControlledAttributes = new ArrayList<AbstractAttribute>();
		for (int line=0; line<componentControlledAttributesFileContent.size(); line++) {
			List<String> row = componentControlledAttributesFileContent.get(line);
			if (row.size() > 1) { throw new GenstarException("Invalid componentControlledAttributesListFile file format (file: " + componentControlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			
			String controlledAttrNameOnData = row.get(0);
			AbstractAttribute componentControlledAttribute = componentAttributesNameOnDataMap.get(controlledAttrNameOnData);
			if (componentControlledAttribute == null) { throw new GenstarException("Invalid (component) controlled attribute: " + controlledAttrNameOnData + ". File: " + groupControlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			if (componentControlledAttributes.contains(componentControlledAttribute)) { throw new GenstarException("Duplicated (component) controlled attribute: " + controlledAttrNameOnData + ". File: " + componentControlledAttributesListFile.getPath() + " at line " + (line + 1) + ")"); }
			
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
		groupControlTotalsToBeBuilt.addAll(groupAvfs);
		Collections.sort(groupControlTotalsToBeBuilt, new GenstarUtils.AttributeValuesFrequencyComparator(groupControlledAttributes));
		/*
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
		*/
		
		
		// fill componentControlTotalsToBeBuilt
		componentControlTotalsToBeBuilt.addAll(componentAvfs);
		Collections.sort(componentControlTotalsToBeBuilt, new GenstarUtils.AttributeValuesFrequencyComparator(componentControlledAttributes));
		/*
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
		*/
	}

	
	public static List<AttributeValuesFrequency> generateIpuControlTotals(final GenstarCsvFile attributesFile, final int total) throws GenstarException {
		
		// TODO
		
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	
	public static void writeIpuControlTotalsToCsvFile(final List<AttributeValuesFrequency> ipuControlTotals, final String csvOutputFile) throws GenstarException  {
		
		// convert ipuControlTotals to List<List<String>>
		List<List<String>> controlTotalStrings = new ArrayList<List<String>>();
		for (AttributeValuesFrequency controlTotal : ipuControlTotals) {
			List<String> controlTotalString = new ArrayList<String>();
			
			for (AbstractAttribute controlledAttr : controlTotal.getAttributes()) {
				controlTotalString.add(controlledAttr.getNameOnData());
				controlTotalString.add(controlTotal.getAttributeValueOnData(controlledAttr).toCsvString());
			}
			
			controlTotalString.add(Integer.toString(controlTotal.getFrequency()));
			controlTotalStrings.add(controlTotalString);
		}

		GenstarUtils.writeStringContentToCsvFile(controlTotalStrings, csvOutputFile);
	}
	
		
	public static Map<AttributeValuesFrequency, List<Entity>> buildEntityCategories(final IPopulation population, final Set<AbstractAttribute> ipuControlledAttributes) throws GenstarException {
		Map<AttributeValuesFrequency, List<Entity>> entityCategories = new HashMap<AttributeValuesFrequency, List<Entity>>();
		
		// 0. parameters validations
		if (population == null) { throw new GenstarException("'population' parameter can not be null"); }
		
		if (ipuControlledAttributes == null || ipuControlledAttributes.isEmpty()) { throw new GenstarException("'ipuControlledAttributes' parameter can neither be null nor empty"); }
		
		// attributes in ipuControlledAttributes belong to population
		List<AbstractAttribute> populationAttributes = population.getAttributes();
		if (!populationAttributes.containsAll(ipuControlledAttributes)) {
			throw new GenstarException("One or several IPU controlled attributes don't belong to the population");
		}
		
		
		// 1. generate attribute values frequencies GenstarUtils.generateAttributeValuesFrequencies(ipuControlledAttributes)
		Set<AttributeValuesFrequency> attributeValuesFrequencies = GenstarUtils.generateAttributeValuesFrequencies(ipuControlledAttributes);
		for (AttributeValuesFrequency avf : attributeValuesFrequencies) {
			entityCategories.put(avf, new ArrayList<Entity>());
		}
		
		List<Entity> entities = population.getEntities();
		List<Entity> alreadyMatchedEntities = new ArrayList<Entity>();
		
		for (AttributeValuesFrequency avf : entityCategories.keySet()) {
			
			List<Entity> avfEntities = entityCategories.get(avf);
			
			for (Entity e : entities) {
				if (avf.matchEntity(e)) {
					avf.increaseFrequency();
					alreadyMatchedEntities.add(e);
				}
			}
			
			avfEntities.addAll(alreadyMatchedEntities);
			entities.removeAll(alreadyMatchedEntities);
			alreadyMatchedEntities.clear();
		}
		
		
		// 2. if there is no matching entity for any attribute values sets, then throw exception
		for (AttributeValuesFrequency avf : entityCategories.keySet()) {
			if (avf.getFrequency() == 0) {
				throw new GenstarException("No matching entity for attribute values set: " + avf.toString());
			}
		}
		
		
		return entityCategories;
	}
	
	
	// TODO does this method belong to this class?
	public static IPopulation extractIpuPopulation(final IPopulation originalPopulation, final float percentage, final Set<AbstractAttribute> ipuControlledAttributes) throws GenstarException {
		
		// 0. parameters validation
		if (originalPopulation == null) { throw new GenstarException("'originalPopulation' parameter can not be null"); }
		if (percentage <= 0 || percentage > 100) { throw new GenstarException("value of 'percentage' parameter must be in (0, 100] range"); }
		if (ipuControlledAttributes == null || ipuControlledAttributes.isEmpty()) { throw new GenstarException("'ipuControlledAttributes' parameter can neither be null nor empty"); }
		
	
		// 1. Build entity categories: group entities into categories with respect to attribute value sets of IPU controlled totals
		Map<AttributeValuesFrequency, List<Entity>> entityCategories = buildEntityCategories(originalPopulation, ipuControlledAttributes);
		
		
		// 2. Build sample population
		IPopulation extractedPopulation = new Population(PopulationType.SAMPLE_DATA_POPULATION, originalPopulation.getName(), originalPopulation.getAttributes());
		extractedPopulation.addGroupReferences(originalPopulation.getGroupReferences());
		extractedPopulation.addComponentReferences(originalPopulation.getGroupReferences());
		
		// 2.1. firstly, try to satisfy that each attributeValueSet has one selected entity in the extracted sample population
		for (List<Entity> eCategory : entityCategories.values()) {
			int entityIndex = SharedInstances.RandomNumberGenerator.nextInt(eCategory.size());
			Entity selectedEntity = eCategory.get(entityIndex);
			extractedPopulation.createEntity(selectedEntity);
		}
		
		// 2.2. secondly, satisfy the "percentage" condition if possible
		int alreadyExtractedEntities = entityCategories.size();
		int originalPopulationSize = originalPopulation.getNbOfEntities();
		int tobeExtractedEntities = (int) (((float)originalPopulationSize) * percentage / 100);
		if (tobeExtractedEntities > alreadyExtractedEntities) {
			int entitiesLeftToExtract = tobeExtractedEntities - alreadyExtractedEntities;
			List<AttributeValuesFrequency> avfList = new ArrayList<AttributeValuesFrequency>(entityCategories.keySet());
			
			for (int i=0; i<entitiesLeftToExtract; i++) {
				int entityIndex = SharedInstances.RandomNumberGenerator.nextInt(originalPopulationSize);
				
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
				extractedPopulation.createEntity(selectedEntity);
			}
		}
		
		return extractedPopulation;
	}


	public static void createIpuGenerationRule(final SampleBasedGenerator groupPopulationGenerator, final GenstarCsvFile groupSampleDataFile, 
			final String groupIdAttributeNameOnGroup, final GenstarCsvFile groupControlledAttributesFile,
			final GenstarCsvFile groupControlTotalsFile, final GenstarCsvFile groupSupplementaryAttributesFile, final String componentReferenceOnGroup,
			final GenstarCsvFile componentAttributesFile, final String componentPopulationName, final GenstarCsvFile componentSampleDataFile, final String groupIdAttributeNameOnComponent,
			final GenstarCsvFile componentControlledAttributesFile, final GenstarCsvFile componentControlTotalsFile, final GenstarCsvFile componentSupplementaryAttributesFile, final String groupReferenceOnComponent, 
			final int maxIterations) throws GenstarException {
	
		// 1. create component population generator
		SampleBasedGenerator componentPopulationGenerator = new SampleBasedGenerator("Component Generator");
		AttributeUtils.createAttributesFromCsvFile(componentPopulationGenerator, componentAttributesFile);
		componentPopulationGenerator.setPopulationName(componentPopulationName);
		
		// 2. initialize Ipu generation rule
		IpuGenerationRule generationRule = new IpuGenerationRule("Ipu generation rule", groupPopulationGenerator, groupControlledAttributesFile, groupControlTotalsFile, groupSupplementaryAttributesFile, 
				componentPopulationGenerator, componentControlledAttributesFile, componentControlTotalsFile, componentSupplementaryAttributesFile, maxIterations);
		
		// 3. initialize sample data
		ISampleData groupSample = new SampleData(groupPopulationGenerator.getPopulationName(), groupPopulationGenerator.getAttributes(), groupSampleDataFile);
		if (componentReferenceOnGroup != null) {
			groupSample.getSampleEntityPopulation().addComponentReference(componentPopulationName, componentReferenceOnGroup);
		}
		
		ISampleData componentSample = new SampleData(componentPopulationName, componentPopulationGenerator.getAttributes(), componentSampleDataFile);
		if (groupReferenceOnComponent != null) {
			componentSample.getSampleEntityPopulation().addGroupReference(groupSample.getSampleEntityPopulation().getName(), groupReferenceOnComponent);
		}

		AbstractAttribute groupIdAttributeOnGroupEntity = groupPopulationGenerator.getAttributeByNameOnData(groupIdAttributeNameOnGroup);
		AbstractAttribute groupIdAttributeOnComponentEntity = componentPopulationGenerator.getAttributeByNameOnData(groupIdAttributeNameOnComponent);
		final CompoundSampleData sampleData = new CompoundSampleData(groupSample, componentSample, groupIdAttributeOnGroupEntity, groupIdAttributeOnComponentEntity);

		// 4. set sample data to the generation rule
		generationRule.setSampleData(sampleData);
		
		groupPopulationGenerator.setGenerationRule(generationRule);
	}
}
