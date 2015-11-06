package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.AttributeValuesFrequency;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.EntityAttributeValue;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.util.GenstarCSVFile;
import ummisco.genstar.util.SharedInstances;

public class SampleDataGenerationRule extends GenerationRule {

	public static final String RULE_TYPE_NAME = "Sample Data";
	
	private IPF ipf;
	
	private GenstarCSVFile sampleDataFile;
	
	private SampleData sampleData;
	
	private GenstarCSVFile controlTotalsFile;
	
	private ControlTotals controlTotals;
	
	private GenstarCSVFile controlledAttributesFile;
	
	private GenstarCSVFile supplementaryAttributesFile;
	
	private ControlledAndSupplementaryAttributes controlledAndSupplementaryAttributes;
	
	private List<AbstractAttribute> attributes;
	
	private boolean ipfRun = false;
	
	private List<AttributeValuesFrequency> selectionProbabilities;
	
	private Map<AttributeValuesFrequency, List<SampleEntity>> sampleEntityCategories;
	
	private int totalEntitiesToGenerate = 0;
	
	private int alreadyGeneratedEntities = 0;

	
	public SampleDataGenerationRule(final ISyntheticPopulationGenerator populationGenerator, final String name, final GenstarCSVFile sampleDataFile,
			final GenstarCSVFile controlledAttributesFile, final GenstarCSVFile controlsFile, final GenstarCSVFile supplementaryAttributesFile) throws GenstarException {
		
		super(populationGenerator, name);
		
		if (sampleDataFile == null) { throw new GenstarException("'sampleDataFile' can not be null"); }
		if (controlledAttributesFile == null) { throw new GenstarException("'controlledAttributesFile' can not be null"); }
		if (controlsFile == null) { throw new GenstarException("'controlsFile' can not be null"); }
		if (supplementaryAttributesFile == null) { throw new GenstarException("'supplementaryAttributesFile' can not be null"); }
		
		this.sampleDataFile = sampleDataFile;
		this.controlledAttributesFile = controlledAttributesFile;
		this.controlTotalsFile = controlsFile;
		this.supplementaryAttributesFile = supplementaryAttributesFile;
		
		this.controlTotals = new ControlTotals(this);
		this.controlledAndSupplementaryAttributes = new ControlledAndSupplementaryAttributes(this);
		this.sampleData = new SampleData(this);
		this.ipf = IPFFactory.createIPF(this);
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

	/**
	 * TODO describe how the method works
	 */
	@Override
	public void generate(Entity entity) throws GenstarException {
		if (!ipfRun) { // run the fitting if necessary
			ipf.fit();
			selectionProbabilities = ipf.getSelectionProbabilities();
			buildSampleEntityCategories();
			for (AttributeValuesFrequency selectProba : selectionProbabilities) { totalEntitiesToGenerate += selectProba.getFrequency(); }
			alreadyGeneratedEntities = 0;

			ipfRun = true;
		}
		
		// recycling
		if (alreadyGeneratedEntities == totalEntitiesToGenerate) { alreadyGeneratedEntities = 0; }
		
		int accumulatedFrequency = 0;
		AttributeValuesFrequency choosenProba = null;
		for (AttributeValuesFrequency selectProba : selectionProbabilities) {
			choosenProba = selectProba;
			if (accumulatedFrequency >= alreadyGeneratedEntities) { break; }
			accumulatedFrequency += selectProba.getFrequency();
		}
		
		List<SampleEntity> selectedSampleCategory = sampleEntityCategories.get(choosenProba);
		int selectedSampleEntityIndex = SharedInstances.RandomNumberGenerator.nextInt(selectedSampleCategory.size());
		SampleEntity pickedSampleEntity = selectedSampleCategory.get(selectedSampleEntityIndex);
		
		// transfer attribute values from SampleEntity to Entity
		Map<AbstractAttribute, AttributeValue> attributeValues = pickedSampleEntity.getAttributeValues();
		for (AbstractAttribute attribute : attributeValues.keySet()) {
			entity.putAttributeValue(new EntityAttributeValue(attribute, attributeValues.get(attribute)));
		}
		
		alreadyGeneratedEntities++;
	}

	@Override
	public AbstractAttribute findAttributeByNameOnData(final String attributeNameOnData) {
		for (AbstractAttribute a : getAttributes()) {
			if (a.getNameOnData().equals(attributeNameOnData)) { return a; }
		}
		
		return null;
	}
	
	
	public IPF getIPF() {
		return ipf;
	}
	
	public GenstarCSVFile getSampleDataFile() {
		return sampleDataFile;
	}

	public SampleData getSampleData() {
		return sampleData;
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
	
}
