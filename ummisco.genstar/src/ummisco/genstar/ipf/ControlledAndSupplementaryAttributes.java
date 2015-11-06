package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.List;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.util.GenstarCSVFile;

public class ControlledAndSupplementaryAttributes {
	
	private SampleDataGenerationRule generationRule;
	
	private GenstarCSVFile controlledAttributesData;
	
	private GenstarCSVFile supplementaryAttributesData;
	
	private List<AbstractAttribute> controlledAttributes;
	
	private List<AbstractAttribute> supplementaryAttributes;
	
	
	public ControlledAndSupplementaryAttributes(final SampleDataGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' can not be null"); }
		
		this.generationRule = generationRule;
		this.controlledAttributesData = generationRule.getControlledAttributesFile();
		this.supplementaryAttributesData = generationRule.getSupplementaryAttributesFile();
		
		parseAttributes();
	}
	
	private void parseAttributes() throws GenstarException {
		if (controlledAttributesData.getRows() == 0) { throw new GenstarException("Controlled attributes file doesn't contain any controlled attribute. (File: " + controlledAttributesData.getFilePath() + ")"); }
		
		controlledAttributes = new ArrayList<AbstractAttribute>();
		
		// 1. parse the controlled attributes
		String attrName;
		AbstractAttribute controlledAttr;
		int lineNo = 1;
		for (List<String> line : controlledAttributesData.getContent()) {
			if (line.size() != 1) { throw new GenstarException("Invalid controlled attribute file format. One line can only contain one attribute. File: " + controlledAttributesData.getFilePath() + ", line: " + lineNo); }
			
			attrName = line.get(0);
			controlledAttr = generationRule.getGenerator().getAttribute(attrName);
			if (controlledAttr == null) { throw new GenstarException("'" + attrName + "' attribute not found in the generator. File: " + controlledAttributesData.getFilePath() + ", line: " + lineNo); }
			if (!controlledAttributes.contains(controlledAttr)) { controlledAttributes.add(controlledAttr); }
			
			lineNo++;
		}
		
		// 2. parse the supplementary attributes
		supplementaryAttributes = new ArrayList<AbstractAttribute>();
		AbstractAttribute supplementaryAttr;
		lineNo=1;
		for (List<String> line : supplementaryAttributesData.getContent()) {
			if (line.size() != 1) { throw new GenstarException("Invalid supplementary attribute file format. One line can only contain one attribute. File: " + supplementaryAttributesData.getFilePath() + ", line: " + lineNo); }
			
			attrName = line.get(0);
			supplementaryAttr = generationRule.getGenerator().getAttribute(attrName);
			if (supplementaryAttr == null) { throw new GenstarException("'" + attrName + "' attribute not found in the generator. File: " + supplementaryAttributesData.getFilePath() + ", line: " + lineNo); }
			if (controlledAttributes.contains(supplementaryAttr)) { throw new GenstarException("'" + attrName + "' attribute has already been a controlled attribute. File: " + supplementaryAttributesData.getFilePath() + ", line: " + lineNo); }
			if (!supplementaryAttributes.contains(supplementaryAttr)) supplementaryAttributes.add(supplementaryAttr);
			
			lineNo++;
		}
	}
	
	public List<AbstractAttribute> getControlledAttributes() {
		List<AbstractAttribute> copy = new ArrayList<AbstractAttribute>();
		copy.addAll(controlledAttributes);
		
		return copy;
	}
	
	public List<AbstractAttribute> getSupplementaryAttributes() {
		List<AbstractAttribute> copy = new ArrayList<AbstractAttribute>();
		copy.addAll(supplementaryAttributes);
		
		return copy;
	}
}
