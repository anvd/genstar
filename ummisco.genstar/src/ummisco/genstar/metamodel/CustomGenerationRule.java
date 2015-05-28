package ummisco.genstar.metamodel;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import ummisco.genstar.exception.GenstarException;

public abstract class CustomGenerationRule extends GenerationRule {

	public static final String RULE_TYPE_NAME = "Custom Generation Rule";
	
	public static final String PARAMETER_DELIMITER = "&";
	
	public static final String PARAMETER_VALUE_DELIMITER = "=";
	
	protected Map<String, String> parameterValuesMap;

	public CustomGenerationRule(final ISyntheticPopulationGenerator populationGenerator, final String ruleName, final String parameterValues) throws GenstarException {
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
