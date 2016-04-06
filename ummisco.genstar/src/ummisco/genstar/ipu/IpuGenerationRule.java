package ummisco.genstar.ipu;

import java.util.List;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.ipf.GroupComponentSampleData;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.GenerationRule;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.util.GenstarCSVFile;

public class IpuGenerationRule extends GenerationRule {

	public static final String RULE_TYPE_NAME = "Iterative Proportional Updating";

	private Ipu ipu;
	
	
	private GenstarCSVFile groupControlTotalsFile;
	
	private GenstarCSVFile groupControlledAttributesFile;
	
	private GenstarCSVFile groupSupplementaryAttributesFile;
	
	private List<AbstractAttribute> groupAttributes;
	
	
	private GenstarCSVFile componentControlTotalsFile;
	
	private GenstarCSVFile componentControlledAttributesFile;
	
	private GenstarCSVFile componentSupplementaryAttributesFile;
	
	private List<AbstractAttribute> componentAttributes;
	
	private ISingleRuleGenerator componentPopulationGenerator;
	

	private IpuControlTotals ipuControlTotals;
	
	
	private int maxIterations = 3;
	
	
	private IpuControlledAndSupplementaryAttributes controlledAndSupplementaryAttributes;
	
	
	private GroupComponentSampleData sampleData;

	
	public IpuGenerationRule(final ISingleRuleGenerator groupPopulationGenerator, final ISingleRuleGenerator componentPopulationGenerator, final String name, 
			final GenstarCSVFile groupControlledAttributesFile, final GenstarCSVFile groupControlTotalsFile, final GenstarCSVFile groupSupplementaryAttributesFile,
			final GenstarCSVFile componentControlledAttributesFile, final GenstarCSVFile componentControlTotalsFile, final GenstarCSVFile componentSupplementaryAttributesFile,
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
		// TODO Auto-generated method stub
		
	}
	
	public void setSampleData(final GroupComponentSampleData sampleData) throws GenstarException {
		if (sampleData == null) { throw new GenstarException("Parameter sampleData can not be null"); }
		
		this.sampleData = sampleData;
		this.ipu = new Ipu(this);
		
		// TODO initialize Ipu
		
		// ipfRun = false;
		
		/*
		if (sampleData == null) { throw new GenstarException("Parameter sampleData can not be null"); }
		
		this.sampleData = sampleData;
		this.ipf = IPFFactory.createIPF(this);
		ipfRun = false;
		 */
	}
	
	public GroupComponentSampleData getSampleData() {
		return sampleData;
	}

	public GenstarCSVFile getGroupControlTotalsFile() {
		return groupControlTotalsFile;
	}

	public GenstarCSVFile getGroupControlledAttributesFile() {
		return groupControlledAttributesFile;
	}

	public GenstarCSVFile getGroupSupplementaryAttributesFile() {
		return groupSupplementaryAttributesFile;
	}

	public List<AbstractAttribute> getGroupControlledAttributes() {
		return controlledAndSupplementaryAttributes.getGroupControlledAttributes();
	}

	public GenstarCSVFile getComponentControlTotalsFile() {
		return componentControlTotalsFile;
	}

	public GenstarCSVFile getComponentControlledAttributesFile() {
		return componentControlledAttributesFile;
	}

	public GenstarCSVFile getComponentSupplementaryAttributesFile() {
		return componentSupplementaryAttributesFile;
	}

//	public List<AbstractAttribute> getComponentAttributes() {
//		if (componentAttributes == null) {
//			componentAttributes = new ArrayList<AbstractAttribute>();
//			componentAttributes.addAll(controlledAndSupplementaryAttributes.getComponentControlledAttributes());
//			componentAttributes.addAll(controlledAndSupplementaryAttributes.get)
//		}
//		return componentAttributes; // ?
//	}
	
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
