package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.SharedInstances;

public class SampleDataGenerationRule extends GenerationRule {

	public static final String RULE_TYPE_NAME = "Sample Data";
	
	private IPF ipf;
	
	private ISampleData sampleData;
	
	private GenstarCSVFile controlTotalsFile;
	
	private ControlTotals controlTotals;
	
	private GenstarCSVFile controlledAttributesFile;
	
	private GenstarCSVFile supplementaryAttributesFile;
	
	private ControlledAndSupplementaryAttributes controlledAndSupplementaryAttributes;
	
	private List<AbstractAttribute> attributes;
	
	private boolean ipfRun = false;
	
	private List<AttributeValuesFrequency> selectionProbabilities;
	
	private Map<AttributeValuesFrequency, List<SampleEntity>> sampleEntityCategories;
	
	private SampleEntityPopulation internalSampleEntityPopulation;
	
	private int currentEntityIndex = -1;
	
	private List<SampleEntity> internalSampleEntities;
	
	
	public SampleDataGenerationRule(final ISingleRuleGenerator populationGenerator, final String name,
			final GenstarCSVFile controlledAttributesFile, final GenstarCSVFile controlTotalsFile, final GenstarCSVFile supplementaryAttributesFile) throws GenstarException {
		
		super(populationGenerator, name);
		
		if (controlledAttributesFile == null) { throw new GenstarException("'controlledAttributesFile' can not be null"); }
		if (controlTotalsFile == null) { throw new GenstarException("'controlsFile' can not be null"); }
		if (supplementaryAttributesFile == null) { throw new GenstarException("'supplementaryAttributesFile' can not be null"); }
		
		this.controlledAttributesFile = controlledAttributesFile;
		this.controlTotalsFile = controlTotalsFile;
		this.supplementaryAttributesFile = supplementaryAttributesFile;
		
		this.controlledAndSupplementaryAttributes = new ControlledAndSupplementaryAttributes(this);
		this.controlTotals = new ControlTotals(this);
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
	
	private void buildSampleEntityCategories() {
		sampleEntityCategories = new HashMap<AttributeValuesFrequency, List<SampleEntity>>();
		
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
			sampleEntityCategories.put(selectProba, sampleData.getSampleEntityPopulation().getMatchingEntities(selectProba.getAttributeValuesWithAttributeNamesAsKey()));
		}
	}
	
	private void runInternalGeneration() throws GenstarException {
		internalSampleEntityPopulation = new SampleEntityPopulation(sampleData.getSampleEntityPopulation().getPopulationName(), sampleData.getSampleEntityPopulation().getAttributes());
		
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
			List<SampleEntity> selectedSampleCategory = sampleEntityCategories.get(selectProba);
			
			for (int agentNo=0; agentNo<selectProba.getFrequency(); agentNo++) {
				int selectedSampleEntityIndex = SharedInstances.RandomNumberGenerator.nextInt(selectedSampleCategory.size());
				SampleEntity sourceSampleEntity = selectedSampleCategory.get(selectedSampleEntityIndex);
				generateInternalSampleEntity(sourceSampleEntity, internalSampleEntityPopulation);
			}
		}
	}
	
	private void generateInternalSampleEntity(final SampleEntity sourceSampleEntity, final SampleEntityPopulation targetSamplePopulation) throws GenstarException {
		SampleEntity targetSampleEntity = targetSamplePopulation.createSampleEntity(sourceSampleEntity.getAttributeValues());
		
		List<SampleEntityPopulation> sourceComponentPopulations = new ArrayList<SampleEntityPopulation>(sourceSampleEntity.getComponentSampleEntityPopulations().values());
		for (SampleEntityPopulation sourceComponentPop : sourceComponentPopulations) {
			SampleEntityPopulation tagetComponentSamplePopulation = targetSampleEntity.createComponentPopulation(sourceComponentPop.getPopulationName(), sourceComponentPop.getAttributes());
			
			for (SampleEntity sourceComponentEntity : sourceComponentPop.getSampleEntities()) {
				generateInternalSampleEntity(sourceComponentEntity, tagetComponentSamplePopulation);
			}
		}
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
			
			internalSampleEntities = internalSampleEntityPopulation.getSampleEntities();
			currentEntityIndex = 0;

			ipfRun = true;
		}
		
		if (currentEntityIndex == internalSampleEntities.size()) { throw new GenstarException("Out of sample entities"); }
		
		// transfer attribute values from SampleEntity to Entity
		SampleEntity pickedSampleEntity = internalSampleEntities.get(currentEntityIndex);
		currentEntityIndex++;
		
		transferData(pickedSampleEntity, entity);
	}
	
	
	private void transferData(final SampleEntity source, final Entity target) throws GenstarException {
		
		// 1. transfer data from SampleEntity to Entity
		Map<String, AttributeValue> attributeValuesOnSampleEntity = source.getAttributeValues();
		for (String attribute : attributeValuesOnSampleEntity.keySet()) {
			target.setAttributeValueOnEntity(attribute, attributeValuesOnSampleEntity.get(attribute));
		}
		
		// 2. recursively, transfer component sample entities to entities
		for (String componentPopulationName : source.getComponentSampleEntityPopulations().keySet()) {
			SampleEntityPopulation componentSampleEntityPopulation = source.getComponentSampleEntityPopulations().get(componentPopulationName);
			
			ISyntheticPopulation componentEntityPopulation = target.getComponentPopulation(componentPopulationName);
			if (componentEntityPopulation == null) {
				componentEntityPopulation = target.createComponentPopulation(componentSampleEntityPopulation.getPopulationName(), componentSampleEntityPopulation.getAttributes());
			}
			
			for (SampleEntity componentSampleEntity : componentSampleEntityPopulation.getSampleEntities()) {
				List<Entity> componentEntities = componentEntityPopulation.createEntities(1);
				transferData(componentSampleEntity, componentEntities.get(0));
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
	
	public ControlTotals getControlTotals() {
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
	
}
