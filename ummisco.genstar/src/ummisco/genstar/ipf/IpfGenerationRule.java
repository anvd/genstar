package ummisco.genstar.ipf;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISingleRuleGenerator;
import ummisco.genstar.util.GenstarCsvFile;

public class IpfGenerationRule extends SampleDataGenerationRule {
	
	public static final String RULE_TYPE_NAME = "Iterative Proportional Fitting";
	

	public IpfGenerationRule(ISingleRuleGenerator populationGenerator,
			String name, GenstarCsvFile controlledAttributesFile,
			GenstarCsvFile controlTotalsFile,
			GenstarCsvFile supplementaryAttributesFile, int maxIterations)
			throws GenstarException {
		super(populationGenerator, name, controlledAttributesFile, controlTotalsFile,
				supplementaryAttributesFile, maxIterations);
		// TODO Auto-generated constructor stub
	}

}
