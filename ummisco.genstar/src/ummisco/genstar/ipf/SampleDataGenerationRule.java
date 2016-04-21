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
	
	private Map<AttributeValuesFrequency, List<Entity>> sampleEntityCategories;
	
	private IPopulation internalGeneratedPopulation;
	
	private int currentEntityIndex = -1;
	
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
		sampleEntityCategories = new HashMap<AttributeValuesFrequency, List<Entity>>();
		
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
			sampleEntityCategories.put(selectProba, sampleData.getSampleEntityPopulation().getMatchingEntitiesByAttributeValuesOnData(selectProba.getAttributeValuesOnData()));
		}
	}
	
	private void runInternalGeneration() throws GenstarException {
		internalGeneratedPopulation = new Population(PopulationType.SYNTHETIC_POPULATION, sampleData.getSampleEntityPopulation().getName(), sampleData.getSampleEntityPopulation().getAttributes());
		internalGeneratedPopulation.addGroupReferences(sampleData.getSampleEntityPopulation().getGroupReferences());
		internalGeneratedPopulation.addComponentReferences(sampleData.getSampleEntityPopulation().getComponentReferences());
		
		
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
			List<Entity> selectedSampleCategory = sampleEntityCategories.get(selectProba);
			
			for (int agentNo=0; agentNo<selectProba.getFrequency(); agentNo++) {
				int selectedSampleEntityIndex = SharedInstances.RandomNumberGenerator.nextInt(selectedSampleCategory.size());
				Entity sourceSampleEntity = selectedSampleCategory.get(selectedSampleEntityIndex);
				
				Entity targetGeneratedEntity = replicateSampleEntity(sourceSampleEntity, internalGeneratedPopulation);
				sampleData.recodeIdAttributes(targetGeneratedEntity);
			}
		}
	}
	
	private Entity replicateSampleEntity(final Entity sourceSampleEntity, final IPopulation targetPopulation) throws GenstarException {
		Entity replicatedEntity = targetPopulation.createEntity(sourceSampleEntity.getEntityAttributeValues());
		
		List<IPopulation> sourceComponentPopulations = sourceSampleEntity.getComponentPopulations();
		for (IPopulation sourceComponentPop : sourceComponentPopulations) {
			IPopulation tagetComponentSamplePopulation = replicatedEntity.createComponentPopulation(sourceComponentPop.getName(), sourceComponentPop.getAttributes());
			tagetComponentSamplePopulation.addGroupReferences(sourceComponentPop.getGroupReferences());
			tagetComponentSamplePopulation.addComponentReferences(sourceComponentPop.getComponentReferences());
			
			for (Entity sourceComponentEntity : sourceComponentPop.getEntities()) {
				replicateSampleEntity(sourceComponentEntity, tagetComponentSamplePopulation);
			}
		}
		
		return replicatedEntity;
	}


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
			
			internalSampleEntities = internalGeneratedPopulation.getEntities();
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
