package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.IPopulation;
import ummisco.genstar.metamodel.Population;
import ummisco.genstar.metamodel.PopulationType;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.SharedInstances;

// TODO make SampleDataGenerationRule a super class (of IpfGenerationRule and IpuGenerationRule), change this class to IpfGenerationRule
public class SampleDataGenerationRule extends GenerationRule {

	public static final String RULE_TYPE_NAME = "Sample Data";
	
	public static final int DEFAULT_MAX_ITERATIONS = 3;
	
	
	private IPF ipf;
	
	private ISampleData sampleData;
	
	private GenstarCSVFile controlTotalsFile;
	
	private IpfControlTotals controlTotals;
	
	private GenstarCSVFile controlledAttributesFile;
	
	private GenstarCSVFile supplementaryAttributesFile;
	
	private ControlledAndSupplementaryAttributes controlledAndSupplementaryAttributes;
	
	private List<AbstractAttribute> attributes;
	
	private int maxIterations = DEFAULT_MAX_ITERATIONS;
	
	private boolean ipfRun = false;
	
	private List<AttributeValuesFrequency> selectionProbabilities;
	
//	private Map<AttributeValuesFrequency, List<SampleEntity>> sampleEntityCategories;
	private Map<AttributeValuesFrequency, List<Entity>> sampleEntityCategories;
	
//	private SampleEntityPopulation internalSampleEntityPopulation;
	private IPopulation internalSampleEntityPopulation;
	
	private int currentEntityIndex = -1;
	
//	private List<SampleEntity> internalSampleEntities;
	private List<Entity> internalSampleEntities;
	
	
	public SampleDataGenerationRule(final ISingleRuleGenerator populationGenerator, final String name,
			final GenstarCSVFile controlledAttributesFile, final GenstarCSVFile controlTotalsFile, 
			final GenstarCSVFile supplementaryAttributesFile, final int maxIterations) throws GenstarException {
		
		super(populationGenerator, name);
		
		if (controlledAttributesFile == null) { throw new GenstarException("'controlledAttributesFile' can not be null"); }
		if (controlTotalsFile == null) { throw new GenstarException("'controlTotalsFile' can not be null"); }
		if (supplementaryAttributesFile == null) { throw new GenstarException("'supplementaryAttributesFile' can not be null"); }
		
		this.controlledAttributesFile = controlledAttributesFile;
		this.controlTotalsFile = controlTotalsFile;
		this.supplementaryAttributesFile = supplementaryAttributesFile;
		
		this.controlledAndSupplementaryAttributes = new ControlledAndSupplementaryAttributes(this);
		this.controlTotals = new IpfControlTotals(this);
		this.setMaxIterations(maxIterations);
	}

	@Override
	public List<AbstractAttribute> getAttributes() {
		if (attributes == null) {
			attributes = new ArrayList<AbstractAttribute>();
			attributes.addAll(controlledAndSupplementaryAttributes.getControlledAttributes());
			attributes.addAll(controlledAndSupplementaryAttributes.getSupplementaryAttributes());
		}
		
		return attributes;
	}

	@Override
	public int getRuleTypeID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getRuleTypeName() {
		return RULE_TYPE_NAME;
	}
	
	private void buildSampleEntityCategories() throws GenstarException {
//		sampleEntityCategories = new HashMap<AttributeValuesFrequency, List<SampleEntity>>();
		sampleEntityCategories = new HashMap<AttributeValuesFrequency, List<Entity>>();
		
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
//			sampleEntityCategories.put(selectProba, sampleData.getSampleEntityPopulation().getMatchingEntities(selectProba.getAttributeValuesWithNamesOnEntityAsKey()));
			sampleEntityCategories.put(selectProba, sampleData.getSampleEntityPopulation().getMatchingEntitiesByAttributeValuesOnData(selectProba.getAttributeValuesOnData()));
		}
	}
	
	private void runInternalGeneration() throws GenstarException {
		internalSampleEntityPopulation = new Population(PopulationType.SYNTHETIC_POPULATION, sampleData.getSampleEntityPopulation().getName(), sampleData.getSampleEntityPopulation().getAttributes());
		internalSampleEntityPopulation.addGroupReferences(sampleData.getSampleEntityPopulation().getGroupReferences());
		internalSampleEntityPopulation.addComponentReferences(sampleData.getSampleEntityPopulation().getComponentReferences());
		
		
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
//			List<SampleEntity> selectedSampleCategory = sampleEntityCategories.get(selectProba);
			List<Entity> selectedSampleCategory = sampleEntityCategories.get(selectProba);
			
			for (int agentNo=0; agentNo<selectProba.getFrequency(); agentNo++) {
				int selectedSampleEntityIndex = SharedInstances.RandomNumberGenerator.nextInt(selectedSampleCategory.size());
//				SampleEntity sourceSampleEntity = selectedSampleCategory.get(selectedSampleEntityIndex);
				Entity sourceSampleEntity = selectedSampleCategory.get(selectedSampleEntityIndex);
				
//				SampleEntity targetEntity = generateInternalSampleEntity(sourceSampleEntity, internalSampleEntityPopulation);
//				sampleData.recodeIdAttributes(targetEntity);
				Entity targetEntity = generateInternalSampleEntity(sourceSampleEntity, internalSampleEntityPopulation);
				sampleData.recodeIdAttributes(targetEntity);
			}
		}
	}
	
	private Entity generateInternalSampleEntity(final Entity sourceSampleEntity, final IPopulation targetSamplePopulation) throws GenstarException {
		Entity targetSampleEntity = targetSamplePopulation.createEntity(sourceSampleEntity.getEntityAttributeValues());
		
		List<IPopulation> sourceComponentPopulations = sourceSampleEntity.getComponentPopulations();
		for (IPopulation sourceComponentPop : sourceComponentPopulations) {
			IPopulation tagetComponentSamplePopulation = targetSampleEntity.createComponentPopulation(sourceComponentPop.getName(), sourceComponentPop.getAttributes());
			tagetComponentSamplePopulation.addGroupReferences(sourceComponentPop.getGroupReferences());
			tagetComponentSamplePopulation.addComponentReferences(sourceComponentPop.getComponentReferences());
			
			for (Entity sourceComponentEntity : sourceComponentPop.getEntities()) {
				generateInternalSampleEntity(sourceComponentEntity, tagetComponentSamplePopulation);
			}
		}
		
		return targetSampleEntity;
	}


	//	private SampleEntity generateInternalSampleEntity(final SampleEntity sourceSampleEntity, final SampleEntityPopulation targetSamplePopulation) throws GenstarException {
//		SampleEntity targetSampleEntity = targetSamplePopulation.createSampleEntity(sourceSampleEntity.getAttributeValuesOnEntity());
//		
//		List<SampleEntityPopulation> sourceComponentPopulations = new ArrayList<SampleEntityPopulation>(sourceSampleEntity.getComponentSampleEntityPopulations().values());
//		for (SampleEntityPopulation sourceComponentPop : sourceComponentPopulations) {
//			SampleEntityPopulation tagetComponentSamplePopulation = targetSampleEntity.createComponentPopulation(sourceComponentPop.getName(), sourceComponentPop.getAttributes());
//			tagetComponentSamplePopulation.addGroupReferences(sourceComponentPop.getGroupReferences());
//			tagetComponentSamplePopulation.addComponentReferences(sourceComponentPop.getComponentReferences());
//			
//			for (SampleEntity sourceComponentEntity : sourceComponentPop.getSampleEntities()) {
//				generateInternalSampleEntity(sourceComponentEntity, tagetComponentSamplePopulation);
//			}
//		}
//		
//		return targetSampleEntity;
//	}

	/**
	 * TODO describe how the method works
	 */
	@Override
	public void generate(final Entity entity) throws GenstarException {
		if (sampleData == null) { throw new GenstarException("sampleData can not be null"); }
		
		if (!ipfRun) { // run the fitting if necessary
			ipf.fit();
			selectionProbabilities = ipf.getSelectionProbabilitiesOfLastIPFIteration();
			buildSampleEntityCategories();
			runInternalGeneration();
			
			// TODO why internalSampleEntityPopulation is empty?
			internalSampleEntities = internalSampleEntityPopulation.getEntities();
			currentEntityIndex = 0;

			ipfRun = true;
		}
		
		if (currentEntityIndex == internalSampleEntities.size()) { throw new GenstarException("Out of sample entities"); }
		
		// transfer attribute values from SampleEntity to Entity
		Entity pickedSampleEntity = internalSampleEntities.get(currentEntityIndex);
		currentEntityIndex++;
		
		transferData(pickedSampleEntity, entity);
		
		// heuristic: only inject group & component reference once
		if (currentEntityIndex == 1) {
			IPopulation entityPopulation = entity.getPopulation();
			IPopulation sampleEntityPopulation = pickedSampleEntity.getPopulation();
			
			entityPopulation.addGroupReferences(sampleEntityPopulation.getGroupReferences());
			entityPopulation.addComponentReferences(sampleEntityPopulation.getComponentReferences());
		}
	}
	
	
	private void transferData(final Entity source, final Entity target) throws GenstarException {
		
		// 1. transfer data from SampleEntity to Entity
		target.setEntityAttributeValues(source.getEntityAttributeValues());
		
		// 2. recursively, transfer component sample entities to entities
		for (IPopulation sourceComponentPopulation : source.getComponentPopulations()) {
			
			IPopulation targetComponentPopulation = target.getComponentPopulation(sourceComponentPopulation.getName());
			if (targetComponentPopulation == null) {
				targetComponentPopulation = target.createComponentPopulation(sourceComponentPopulation.getName(), sourceComponentPopulation.getAttributes());
				targetComponentPopulation.addGroupReferences(sourceComponentPopulation.getGroupReferences());
				targetComponentPopulation.addComponentReferences(sourceComponentPopulation.getComponentReferences());
			}
			
			for (Entity sourceComponentSampleEntity : sourceComponentPopulation.getEntities()) {
				List<Entity> targetComponentEntities = targetComponentPopulation.createEntities(1);
				transferData(sourceComponentSampleEntity, targetComponentEntities.get(0));
			}
		}
	}
	
	
//	private void transferData(final SampleEntity source, final Entity target) throws GenstarException {
//		
//		// 1. transfer data from SampleEntity to Entity
//		Map<String, AttributeValue> attributeValuesOnSampleEntity = source.getAttributeValuesOnEntity(); // = attribute value on entity
//		for (String attributeNameOnData : attributeValuesOnSampleEntity.keySet()) {
//			target.setAttributeValueOnEntity(attributeNameOnData, attributeValuesOnSampleEntity.get(attributeNameOnData));
//		}
//		
//		// 2. recursively, transfer component sample entities to entities
//		for (String componentPopulationName : source.getComponentSampleEntityPopulations().keySet()) {
//			SampleEntityPopulation componentSampleEntityPopulation = source.getComponentSampleEntityPopulations().get(componentPopulationName);
//			
//			IPopulation componentEntityPopulation = target.getComponentPopulation(componentPopulationName);
//			if (componentEntityPopulation == null) {
//				componentEntityPopulation = target.createComponentPopulation(componentSampleEntityPopulation.getName(), componentSampleEntityPopulation.getAttributes());
//				componentEntityPopulation.addGroupReferences(componentSampleEntityPopulation.getGroupReferences());
//				componentEntityPopulation.addComponentReferences(componentSampleEntityPopulation.getComponentReferences());
//			}
//			
//			for (SampleEntity componentSampleEntity : componentSampleEntityPopulation.getSampleEntities()) {
//				List<Entity> componentEntities = componentEntityPopulation.createEntities(1);
//				transferData(componentSampleEntity, componentEntities.get(0));
//			}
//		}
//	}
	

	@Override
	public AbstractAttribute getAttributeByNameOnData(final String attributeNameOnData) {
		for (AbstractAttribute a : getAttributes()) {
			if (a.getNameOnData().equals(attributeNameOnData)) { return a; }
		}
		
		return null;
	}
	

	@Override
	public AbstractAttribute getAttributeByNameOnEntity(final String attributeNameOnEntity) {
		for (AbstractAttribute a : getAttributes()) {
			if (a.getNameOnEntity().equals(attributeNameOnEntity)) { return a; }
		}
		
		return null;
	}
	
	public IPF getIPF() {
		return ipf;
	}
	
	public ISampleData getSampleData() {
		return sampleData;
	}
	
	public void setSampleData(final ISampleData sampleData) throws GenstarException {
		if (sampleData == null) { throw new GenstarException("Parameter sampleData can not be null"); }
		
		this.sampleData = sampleData;
		this.ipf = IPFFactory.createIPF(this);
		ipfRun = false;
	}
	
	public GenstarCSVFile getControlTotalsFile() {
		return controlTotalsFile;
	}
	
	public IpfControlTotals getControlTotals() {
		 return controlTotals;
	}
	
	public GenstarCSVFile getControlledAttributesFile() {
		return controlledAttributesFile;
	}
	
	public GenstarCSVFile getSupplementaryAttributesFile() {
		return supplementaryAttributesFile;
	}
	
	public List<AbstractAttribute> getControlledAttributes() {
		return controlledAndSupplementaryAttributes.getControlledAttributes();
	}
	
	public List<AbstractAttribute> getSupplementaryAttributes() {
		return controlledAndSupplementaryAttributes.getSupplementaryAttributes();
	}
	
	@Override public ISingleRuleGenerator getGenerator() {
		return (ISingleRuleGenerator) populationGenerator;
	}
	
	public void setMaxIterations(final int maxIterations) {
		if (maxIterations <= 0) { throw new IllegalArgumentException("'maxIterations' parameter must be a positive integer."); }
		
		this.maxIterations = maxIterations;
	}
	
	public int getMaxIterations() {
		return maxIterations;
	}
}
