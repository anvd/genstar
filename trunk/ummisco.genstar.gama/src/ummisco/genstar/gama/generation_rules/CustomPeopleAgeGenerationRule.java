package ummisco.genstar.gama.generation_rules;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.IList;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.types.Types;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.CustomGenerationRule;
import ummisco.genstar.metamodel.DataType;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.EntityAttributeValue;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.RangeValue;
import ummisco.genstar.metamodel.RangeValuesAttribute;
import ummisco.genstar.util.SharedInstances;

public class CustomPeopleAgeGenerationRule extends CustomGenerationRule {
	
	private List<AbstractAttribute> attributes;
	
	private RangeValuesAttribute ageAttribute;
	
	private Map<RangeValue, Integer> valueFrequencies;
	
	private int total = 0;
	
	public CustomPeopleAgeGenerationRule(final ISyntheticPopulationGenerator populationGenerator, final String ruleName, final String parameterValues) throws GenstarException {
		super(populationGenerator, ruleName, parameterValues);		
	}
	
	@Override public void initializeData() throws GenstarException {
		valueFrequencies = new HashMap<RangeValue, Integer>();
		
		attributes = new ArrayList<AbstractAttribute>();
		ageAttribute = (RangeValuesAttribute) populationGenerator.getAttribute("Age");
		attributes.add(ageAttribute);

		String ruleDataFileRelativePath = parameterValuesMap.get("rule_data_file");
		if (ruleDataFileRelativePath == null) { throw new GenstarException("Required 'rule_data_file' parameter not found"); }
		IScope scope = GAMA.getRuntimeScope();
		String modelWorkingPath = GAMA.getModel().getWorkingPath();
		GamaCSVFile ruleDataCSVFile = new GamaCSVFile(scope, modelWorkingPath + File.separatorChar + ruleDataFileRelativePath, ",", Types.STRING, true);
		
		// 1. parse ruleDataCSVFile header
		IList<String> attributes  = ruleDataCSVFile.getAttributes(scope);
		if (attributes.size() != 2 || !attributes.get(0).equals("Age:Output") || !attributes.get(1).equals("Frequency")) {
			
			System.out.println("attributes.get(0).charAt(0) = " + attributes.get(0).charAt(0));
			System.out.println("attributes.get(0).charAt(attributes.get(0).length() - 1) = " + attributes.get(0).charAt(attributes.get(0).length() - 1));
			
			System.out.println("attributes.get(1).charAt(0) = " + attributes.get(1).charAt(0));
			System.out.println("attributes.get(1).charAt(attributes.get(1).length() - 1) = " + attributes.get(1).charAt(attributes.get(1).length() - 1));

			throw new GenstarException("Invalid rule data file header. Expected header: " + "'Age:Output,Frequency'. File: " + ruleDataCSVFile.getPath());
		}
		
		
		// 2. parse ruleDataCSVFile content
		DataType dataType = ageAttribute.getDataType();
		IMatrix fileContent = ruleDataCSVFile.getContents(scope);
		if ( fileContent == null || fileContent.isEmpty(scope) ) { throw new GenstarException("Empty attribute data file. File: " + ruleDataCSVFile.getPath()); }
		int rows = fileContent.getRows(scope);
		for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
			IList frequencyInfo = fileContent.getRow(scope, rowIndex);

			if (frequencyInfo.size() != 2) { throw new GenstarException("Invalid attribute data file. Line " + (rowIndex + 2) + " must have 2 elements. File: " + ruleDataCSVFile.getPath()); }
			
			// 2.1. parse attribute value
			StringTokenizer minMaxValueToken = new StringTokenizer((String)frequencyInfo.get(0), ":");
			if (minMaxValueToken.countTokens() != 2) { throw new GenstarException("Invalid range attribute value: '" + (String)frequencyInfo.get(0) + "'. File: " + ruleDataCSVFile.getPath()); }
			
			String minValue = minMaxValueToken.nextToken().trim();
			String maxValue = minMaxValueToken.nextToken().trim();
			RangeValue rangeValue = new RangeValue(dataType, minValue, maxValue);
			
			if (!ageAttribute.containsValueOfAttributeValue(rangeValue)) { throw new GenstarException(rangeValue.toString() + " not found in 'Age' attribute."); }
			if (valueFrequencies.containsKey(rangeValue)) { throw new GenstarException("Duplicated attribute value: " + rangeValue.toString() + ". File: " + ruleDataCSVFile.getPath()); }
			
			
			// 2.2. parse frequency
			int frequency = new Integer((String) frequencyInfo.get(1));
			if (frequency < 0) { throw new GenstarException("Negative frequency value: " + Integer.toString(frequency) + ". File: " + ruleDataCSVFile.getPath()); }
			valueFrequencies.put(rangeValue, frequency);
			
			total += frequency;
		}

	}

	@Override
	public List<AbstractAttribute> getAttributes() {
		return attributes;
	}

	@Override
	public int getRuleTypeID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void generate(Entity entity) throws GenstarException {
		int selectedTotal = SharedInstances.RandomNumberGenerator.nextInt(total);
		
		int currentTotal = 0;
		for (RangeValue value : valueFrequencies.keySet()) {
			if (currentTotal > selectedTotal) {
				entity.putAttributeValue(new EntityAttributeValue(ageAttribute, value));
				break;
			}
			
			currentTotal += valueFrequencies.get(value);
		}
	}

	@Override
	public AbstractAttribute findAttributeByNameOnData(
			String attributeNameOnData) {
		// TODO Auto-generated method stub
		return null;
	}

}
