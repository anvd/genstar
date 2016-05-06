package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.generation_rules.SampleBasedGenerationRule;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.Population;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.metamodel.sample_data.ISampleData;
import ummisco.genstar.metamodel.sample_data.SampleData;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.GenstarUtils;
import ummisco.genstar.util.SharedInstances;


public class IpfGenerationRule extends SampleBasedGenerationRule {
	
	public static final int DEFAULT_MAX_ITERATIONS = 3;

	public static final String RULE_TYPE_NAME = "Sample Data";
	
	
	private Ipf ipf;
	
	private ISampleData sampleData;
	
	private GenstarCsvFile controlTotalsFile;
	
	private IpfControlTotals controlTotals;
	
	private GenstarCsvFile controlledAttributesFile;
	
	private GenstarCsvFile supplementaryAttributesFile;
	
	private ControlledAndSupplementaryAttributes controlledAndSupplementaryAttributes;
	
	private boolean ipfRun = false;
	
	private List<AttributeValuesFrequency> selectionProbabilities;
	
	private Map<AttributeValuesFrequency, List<Entity>> sampleEntityCategories;
	
	private IPopulation internalGeneratedPopulation;
	
	
	public IpfGenerationRule(final SampleBasedGenerator populationGenerator, final String name,
			final GenstarCsvFile controlledAttributesFile, final GenstarCsvFile controlTotalsFile, 
			final GenstarCsvFile supplementaryAttributesFile, final int maxIterations) throws GenstarException {
		
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
		List<AbstractAttribute>	attributes = new ArrayList<AbstractAttribute>();
		attributes.addAll(controlledAndSupplementaryAttributes.getControlledAttributes());
		attributes.addAll(controlledAndSupplementaryAttributes.getSupplementaryAttributes());
		
		return attributes;
	}

	@Override
	public int getRuleTypeID() {
		return 0;
	}

	@Override
	public String getRuleTypeName() {
		return RULE_TYPE_NAME;
	}
	
	// TODO use IpuUtils.buildEntityCategories instead
	private void buildSampleEntityCategories() throws GenstarException {
		sampleEntityCategories = new HashMap<AttributeValuesFrequency, List<Entity>>();
		
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
			sampleEntityCategories.put(selectProba, sampleData.getSampleEntityPopulation().getMatchingEntitiesByAttributeValuesOnData(selectProba.getAttributeValuesOnData()));
		}
	}
	
	private void runInternalGeneration() throws GenstarException {
		internalGeneratedPopulation = new Population(PopulationType.SYNTHETIC_POPULATION, sampleData.getSampleEntityPopulation().getName(), sampleData.getSampleEntityPopulation().getAttributes());
		internalGeneratedPopulation.addGroupReferences(sampleData.getSampleEntityPopulation().getGroupReferences()); // BUG here: sampleData.getSampleEntityPopulation().getGroupReferences() == null
		internalGeneratedPopulation.addComponentReferences(sampleData.getSampleEntityPopulation().getComponentReferences()); // BUG here sampleData.getSampleEntityPopulation().getComponentReferences() == null
		
		
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
			List<Entity> selectedSampleCategory = sampleEntityCategories.get(selectProba);
			
			for (int agentNo=0; agentNo<selectProba.getFrequency(); agentNo++) {
				int selectedSampleEntityIndex = SharedInstances.RandomNumberGenerator.nextInt(selectedSampleCategory.size());
				Entity sourceSampleEntity = selectedSampleCategory.get(selectedSampleEntityIndex);
				
				Entity targetGeneratedEntity = GenstarUtils.replicateSampleEntity(sourceSampleEntity, internalGeneratedPopulation);
				sampleData.recodeIdAttributes(targetGeneratedEntity);
			}
		}
	}
	

	@Override public IPopulation generate() throws GenstarException {
		if (sampleData == null) { throw new GenstarException("sampleData can not be null"); }
		
		if (!ipfRun) { // run the fitting if necessary
			ipf.fit();
			selectionProbabilities = ipf.getSelectionProbabilitiesOfLastIPFIteration();
			buildSampleEntityCategories();
			runInternalGeneration();

			ipfRun = true;
		}
		
		
		IPopulation resultingPopulation = new Population(PopulationType.SYNTHETIC_POPULATION, sampleData.getSampleEntityPopulation().getName(), sampleData.getSampleEntityPopulation().getAttributes());
		resultingPopulation.addGroupReferences(internalGeneratedPopulation.getGroupReferences());
		resultingPopulation.addComponentReferences(internalGeneratedPopulation.getComponentReferences());
		List<Entity> sourceEntities = internalGeneratedPopulation.getEntities();
		List<Entity> targetEntities = resultingPopulation.createEntities(sourceEntities.size());
		for (int index=0; index<sourceEntities.size(); index++) {
			GenstarUtils.transferData(sourceEntities.get(index), targetEntities.get(index));
		}
		
		return resultingPopulation;
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
	
	public Ipf getIPF() {
		return ipf;
	}
	
	@Override public ISampleData getSampleData() {
		return sampleData;
	}
	
	public void setSampleData(final ISampleData sampleData) throws GenstarException {
		if (sampleData == null) { throw new GenstarException("Parameter sampleData can not be null"); }
		
		this.sampleData = sampleData;
		this.ipf = IpfFactory.createIPF(this);
		ipfRun = false;
	}
	
	public GenstarCsvFile getControlTotalsFile() {
		return controlTotalsFile;
	}
	
	public IpfControlTotals getControlTotals() {
		 return controlTotals;
	}
	
	public GenstarCsvFile getControlledAttributesFile() {
		return controlledAttributesFile;
	}
	
	public GenstarCsvFile getSupplementaryAttributesFile() {
		return supplementaryAttributesFile;
	}
	
	public List<AbstractAttribute> getControlledAttributes() {
		return controlledAndSupplementaryAttributes.getControlledAttributes();
	}
	
	public List<AbstractAttribute> getSupplementaryAttributes() {
		return controlledAndSupplementaryAttributes.getSupplementaryAttributes();
	}

	@Override
	public boolean containAttribute(final AbstractAttribute attribute) throws GenstarException {
		return getControlledAttributes().contains(attribute) || getSupplementaryAttributes().contains(attribute);
	}

}
