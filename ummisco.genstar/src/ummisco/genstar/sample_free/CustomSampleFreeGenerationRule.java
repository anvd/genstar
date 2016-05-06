package ummisco.genstar.sample_free;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.generation_rules.SampleFreeGenerationRule;
import ummisco.genstar.metamodel.generators.SampleFreeGenerator;

public abstract class CustomSampleFreeGenerationRule extends SampleFreeGenerationRule {

	public static final String RULE_TYPE_NAME = "Custom Generation Rule"; // TODO change to "Custom Sample Free Generation Rule"
	
	public static final String PARAMETER_DELIMITER = "&";
	
	public static final String PARAMETER_VALUE_DELIMITER = "=";
	
	protected Map<String, String> parameterValuesMap;

	public CustomSampleFreeGenerationRule(final SampleFreeGenerator populationGenerator, final String ruleName, final String parameterValues) throws GenstarException {
		super(populationGenerator, ruleName);
		
		readParameterValues(parameterValues);
		initializeData();
	}

	@Override public String getRuleTypeName() {
		return RULE_TYPE_NAME;
	}
	
	protected void readParameterValues(final String parameterValues) throws GenstarException {
		parameterValuesMap = new HashMap<String, String>();
		
		StringTokenizer parameterTokenizer = new StringTokenizer(parameterValues, PARAMETER_DELIMITER);
		while (parameterTokenizer.hasMoreTokens()) {
			String paramValuePair = parameterTokenizer.nextToken();
			StringTokenizer parameterValuePairTokenizer = new StringTokenizer(paramValuePair, PARAMETER_VALUE_DELIMITER);
			
			if (parameterValuePairTokenizer.countTokens() != 2) { throw new GenstarException("Invalid parameter value format found in " + name + " generation rule."); }
			
			parameterValuesMap.put(parameterValuePairTokenizer.nextToken(), parameterValuePairTokenizer.nextToken());
		}
	}
	
	public abstract void initializeData() throws GenstarException;
}
