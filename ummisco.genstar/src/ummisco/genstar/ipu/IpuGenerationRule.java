package ummisco.genstar.ipu;

import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.GroupComponentSampleData;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.IPopulation;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.Population;
import ummisco.genstar.metamodel.PopulationType;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.util.GenstarCsvFile;
import ummisco.genstar.util.GenstarUtils;

public class IpuGenerationRule extends GenerationRule {

	public static final String RULE_TYPE_NAME = "Iterative Proportional Updating";

	private Ipu ipu;
	
	private boolean ipuRun = false;
	
	
	private GenstarCsvFile groupControlTotalsFile;
	
	private GenstarCsvFile groupControlledAttributesFile;
	
	private GenstarCsvFile groupSupplementaryAttributesFile;
	
	
	private GenstarCsvFile componentControlTotalsFile;
	
	private GenstarCsvFile componentControlledAttributesFile;
	
	private GenstarCsvFile componentSupplementaryAttributesFile;
	
	private ISingleRuleGenerator componentPopulationGenerator;
	

	private IpuControlTotals ipuControlTotals;
	
	private int maxIterations = 3;
	
	private IpuControlledAndSupplementaryAttributes controlledAndSupplementaryAttributes;
	
	private GroupComponentSampleData sampleData;

	private IPopulation internalGeneratedPopulation;
	
	private int currentEntityIndex = -1;
	
	private List<Entity> internalSampleEntities;
	
	
	public IpuGenerationRule(final ISingleRuleGenerator groupPopulationGenerator, final ISingleRuleGenerator componentPopulationGenerator, final String name, 
			final GenstarCsvFile groupControlledAttributesFile, final GenstarCsvFile groupControlTotalsFile, final GenstarCsvFile groupSupplementaryAttributesFile,
			final GenstarCsvFile componentControlledAttributesFile, final GenstarCsvFile componentControlTotalsFile, final GenstarCsvFile componentSupplementaryAttributesFile,
			final int maxIterations) throws GenstarException {

		super(groupPopulationGenerator, name);
		
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getRuleTypeName() {
		return RULE_TYPE_NAME;
	}

	@Override
	public void generate(Entity entity) throws GenstarException {
		if (sampleData == null) { throw new GenstarException("sampleData can not be null"); }
		
		if (!ipuRun) { // run the fitting if necessary
			ipu.fit();
			
			Map<Entity, Integer> selectionProbabilities = ipu.getSelectionProbabilities();
			runInternalGeneration(selectionProbabilities);
			
			internalSampleEntities = internalGeneratedPopulation.getEntities();
			currentEntityIndex = 0;

			ipuRun = true;
		}
		
		if (currentEntityIndex == internalSampleEntities.size()) { throw new GenstarException("Out of sample entities"); }
		
		// transfer attribute values from SampleEntity to Entity
		Entity pickedSampleEntity = internalSampleEntities.get(currentEntityIndex);
		currentEntityIndex++;
		
		GenstarUtils.transferData(pickedSampleEntity, entity);
		
		// heuristic: only inject group & component reference once
		if (currentEntityIndex == 1) {
			IPopulation entityPopulation = entity.getPopulation();
			IPopulation sampleEntityPopulation = pickedSampleEntity.getPopulation();
			
			entityPopulation.addGroupReferences(sampleEntityPopulation.getGroupReferences());
			entityPopulation.addComponentReferences(sampleEntityPopulation.getComponentReferences());
		}
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
	
	public void setSampleData(final GroupComponentSampleData sampleData) throws GenstarException {
		if (sampleData == null) { throw new GenstarException("Parameter sampleData can not be null"); }
		
		this.sampleData = sampleData;
		this.ipu = new Ipu(this);
		ipuRun = false;
	}
	
	public GroupComponentSampleData getSampleData() {
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

	public void setMaxIterations(final int maxIterations) {
		if (maxIterations <= 0) { throw new IllegalArgumentException("'maxIterations' parameter must be a positive integer."); }
		
		this.maxIterations = maxIterations;
	}
	
	public int getMaxIterations() {
		return maxIterations;
	}
}
