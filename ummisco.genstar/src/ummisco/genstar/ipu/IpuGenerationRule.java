package ummisco.genstar.ipu;

import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.generation_rules.SampleBasedGenerationRule;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.generators.SampleBasedGenerator;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.metamodel.population.Population;
import ummisco.genstar.metamodel.population.PopulationType;
import ummisco.genstar.metamodel.sample_data.CompoundSampleData;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.GenstarUtils;

public class IpuGenerationRule extends SampleBasedGenerationRule {

	public static final String RULE_TYPE_NAME = "Iterative Proportional Updating";
	

	private Ipu ipu;
	
	private boolean ipuRun = false;
	

	private GenstarCsvFile groupControlledAttributesFile;
	
	private GenstarCsvFile groupControlTotalsFile;
	
	private GenstarCsvFile groupSupplementaryAttributesFile;
	
	
	private GenstarCsvFile componentControlledAttributesFile;
	
	private GenstarCsvFile componentControlTotalsFile;
	
	private GenstarCsvFile componentSupplementaryAttributesFile;
	
	
	private SampleBasedGenerator componentPopulationGenerator;
	

	private IpuControlTotals ipuControlTotals;
	
	private IpuControlledAndSupplementaryAttributes controlledAndSupplementaryAttributes;
	
	private CompoundSampleData sampleData;

	private IPopulation internalGeneratedPopulation;
	
	
	public IpuGenerationRule(final String ruleName, final SampleBasedGenerator groupPopulationGenerator,  final GenstarCsvFile groupControlledAttributesFile, 
			final GenstarCsvFile groupControlTotalsFile, final GenstarCsvFile groupSupplementaryAttributesFile,
			final SampleBasedGenerator componentPopulationGenerator, final GenstarCsvFile componentControlledAttributesFile, 
			final GenstarCsvFile componentControlTotalsFile, final GenstarCsvFile componentSupplementaryAttributesFile,
			final int maxIterations) throws GenstarException {

		super(groupPopulationGenerator, ruleName);
		
		if (componentPopulationGenerator == null) { throw new GenstarException("'componentPopulationGenerator' parameter can not be null"); }
		
		if (groupControlledAttributesFile == null) { throw new GenstarException("'groupControlledAttributesFile' parameter can not be null"); }
		if (groupControlTotalsFile == null) { throw new GenstarException("'groupControlTotalsFile' parameter can not be null"); }
		if (groupSupplementaryAttributesFile == null) { throw new GenstarException("'groupSupplementaryAttributesFile' parameter can not be null"); }
		
		if (componentControlledAttributesFile == null) { throw new GenstarException("'componentControlledAttributesFile' parameter can not be null"); }
		if (componentControlTotalsFile == null) { throw new GenstarException("'componentControlTotalsFile' parameter can not be null"); }
		if (componentSupplementaryAttributesFile == null) { throw new GenstarException("'componentSupplementaryAttributesFile' parameter can not be null"); }
		
		this.componentPopulationGenerator = componentPopulationGenerator;

		this.groupControlledAttributesFile = groupControlledAttributesFile;
		this.groupControlTotalsFile = groupControlTotalsFile;
		this.groupSupplementaryAttributesFile = groupSupplementaryAttributesFile;
		
		this.componentControlledAttributesFile = componentControlledAttributesFile;
		this.componentControlTotalsFile = componentControlTotalsFile;
		this.componentSupplementaryAttributesFile = componentSupplementaryAttributesFile;
		
		this.controlledAndSupplementaryAttributes = new IpuControlledAndSupplementaryAttributes(this);
		this.ipuControlTotals = new IpuControlTotals(this);
		this.setMaxIterations(maxIterations);
	}

	@Override
	public List<AbstractAttribute> getAttributes() {
		return this.getGenerator().getAttributes();
	}

	@Override
	public AbstractAttribute getAttributeByNameOnData(String attributeNameOnData) {
		return this.getGenerator().getAttributeByNameOnData(attributeNameOnData);
	}

	@Override
	public AbstractAttribute getAttributeByNameOnEntity(String attributeNameOnEntity) {
		return this.getGenerator().getAttributeByNameOnEntity(attributeNameOnEntity);
	}

	@Override
	public int getRuleTypeID() {
		return 0;
	}

	@Override
	public String getRuleTypeName() {
		return RULE_TYPE_NAME;
	}
	
	
	@Override public IPopulation generate() throws GenstarException {
		
		if (sampleData == null) { throw new GenstarException("sampleData can not be null"); }
		
		if (!ipuRun) { // run the fitting if necessary
			ipu.fit();
			
			Map<Entity, Integer> selectionProbabilities = ipu.getSelectionProbabilities();
			runInternalGeneration(selectionProbabilities);

			ipuRun = true;
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
	
	private void runInternalGeneration(final Map<Entity, Integer> selectionProbabilities) throws GenstarException {
		
		// create the internal population
		internalGeneratedPopulation = new Population(PopulationType.SYNTHETIC_POPULATION, sampleData.getSampleEntityPopulation().getName(), sampleData.getSampleEntityPopulation().getAttributes());
		internalGeneratedPopulation.addGroupReferences(sampleData.getSampleEntityPopulation().getGroupReferences());
		internalGeneratedPopulation.addComponentReferences(sampleData.getSampleEntityPopulation().getComponentReferences());
		

		// create entities of the internal population with entities of selectionProbabilities
		for (Entity sourceSampleEntity : sampleData.getSampleEntityPopulation().getEntities()) {
			int nbEntities = selectionProbabilities.get(sourceSampleEntity);
			for (int i=0; i<nbEntities; i++) {
				Entity targetGeneratedEntity = GenstarUtils.replicateSampleEntity(sourceSampleEntity, internalGeneratedPopulation);
				sampleData.recodeIdAttributes(targetGeneratedEntity);
			}
		}
	}
	
	public void setSampleData(final CompoundSampleData sampleData) throws GenstarException {
		if (sampleData == null) { throw new GenstarException("Parameter sampleData can not be null"); }
		
		this.sampleData = sampleData;
		this.ipu = new Ipu(this);
		ipuRun = false;
	}
	
	@Override public CompoundSampleData getSampleData() {
		return sampleData;
	}

	public GenstarCsvFile getGroupControlTotalsFile() {
		return groupControlTotalsFile;
	}

	public GenstarCsvFile getGroupControlledAttributesFile() {
		return groupControlledAttributesFile;
	}

	public GenstarCsvFile getGroupSupplementaryAttributesFile() {
		return groupSupplementaryAttributesFile;
	}

	public List<AbstractAttribute> getGroupControlledAttributes() {
		return controlledAndSupplementaryAttributes.getGroupControlledAttributes();
	}

	public GenstarCsvFile getComponentControlTotalsFile() {
		return componentControlTotalsFile;
	}

	public GenstarCsvFile getComponentControlledAttributesFile() {
		return componentControlledAttributesFile;
	}

	public GenstarCsvFile getComponentSupplementaryAttributesFile() {
		return componentSupplementaryAttributesFile;
	}

	public List<AbstractAttribute> getComponentControlledAttributes() {
		return controlledAndSupplementaryAttributes.getComponentControlledAttributes();
	}
	
	public ISyntheticPopulationGenerator getComponentGenerator() {
		return componentPopulationGenerator;
	}

	public IpuControlTotals getControlTotals() {
		return ipuControlTotals;
	}

	@Override
	public boolean containAttribute(final AbstractAttribute attribute) throws GenstarException {
		return getGenerator().containAttribute(attribute);
	}
}
