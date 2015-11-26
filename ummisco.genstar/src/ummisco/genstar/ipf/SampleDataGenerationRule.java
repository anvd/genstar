package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.AttributeValuesFrequency;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.SharedInstances;

public class SampleDataGenerationRule extends GenerationRule {

	public static final String RULE_TYPE_NAME = "Sample Data";
	
	private IPF ipf;
	
	private ISampleData sampleData; // this field should be set/injected dynamically before the generation
	
	private GenstarCSVFile controlTotalsFile;
	
	private ControlTotals controlTotals;
	
	private GenstarCSVFile controlledAttributesFile;
	
	private GenstarCSVFile supplementaryAttributesFile;
	
	private ControlledAndSupplementaryAttributes controlledAndSupplementaryAttributes;
	
	private List<AbstractAttribute> attributes;
	
	private boolean ipfRun = false;
	
	private List<AttributeValuesFrequency> selectionProbabilities;
	
	private Map<AttributeValuesFrequency, List<SampleEntity>> sampleEntityCategories;
	
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
//		this.sampleData = new SampleData(this, sampleDataFile);
//		this.ipf = IPFFactory.createIPF(this);
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
			sampleEntityCategories.put(selectProba, sampleData.getMatchingEntities(selectProba.getAttributeValues()));
		}
	}
	
	private void runInternalGeneration() {
		internalSampleEntities = new ArrayList<SampleEntity>();
		
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
			
			List<SampleEntity> selectedSampleCategory = sampleEntityCategories.get(selectProba);
			
			for (int agentNo=0; agentNo<selectProba.getFrequency(); agentNo++) {
				int selectedSampleEntityIndex = SharedInstances.RandomNumberGenerator.nextInt(selectedSampleCategory.size());
				internalSampleEntities.add(selectedSampleCategory.get(selectedSampleEntityIndex));
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

			ipfRun = true;
		}
		
		if (internalSampleEntities.isEmpty()) { throw new GenstarException("Out of sample entities"); }
		
		// transfer attribute values from SampleEntity to Entity
		SampleEntity pickedSampleEntity = internalSampleEntities.remove(0);
		Map<AbstractAttribute, AttributeValue> attributeValues = pickedSampleEntity.getAttributeValues();
		for (AbstractAttribute attribute : attributeValues.keySet()) {
			entity.putAttributeValue(new EntityAttributeValue(attribute, attributeValues.get(attribute)));
		}
		
		// TODO member generations
	}

	@Override
	public AbstractAttribute getAttribute(final String attributeNameOnData) {
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
