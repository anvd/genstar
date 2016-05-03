package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.List;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.util.GenstarCsvFile;

// TODO change name to IpfControlledAndSupplementaryAttributes
public class ControlledAndSupplementaryAttributes {
	
	private IpfGenerationRule generationRule;
	
	private GenstarCsvFile controlledAttributesFile;
	
	private GenstarCsvFile supplementaryAttributesFile;
	
	private List<AbstractAttribute> controlledAttributes;
	
	private List<AbstractAttribute> supplementaryAttributes;
	
	
	public ControlledAndSupplementaryAttributes(final IpfGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("'generationRule' can not be null"); }
		
		this.generationRule = generationRule;
		this.controlledAttributesFile = generationRule.getControlledAttributesFile();
		this.supplementaryAttributesFile = generationRule.getSupplementaryAttributesFile();
		
		parseAttributes();
	}
	
	private void parseAttributes() throws GenstarException {
		if (controlledAttributesFile.getRows() == 0) { throw new GenstarException("Controlled attributes file can not be empty. (File: " + controlledAttributesFile.getPath() + ")"); }
		
		controlledAttributes = new ArrayList<AbstractAttribute>();
		
		// 1. parse the controlled attributes
		String attrName;
		AbstractAttribute controlledAttr;
		int lineNo = 1;
		for (List<String> line : controlledAttributesFile.getContent()) {
			if (line.size() != 1) { throw new GenstarException("Invalid controlled attribute file format. One line can only contain one attribute. File: " + controlledAttributesFile.getPath() + ", line: " + lineNo); }
			
			attrName = line.get(0);
			controlledAttr = generationRule.getGenerator().getAttributeByNameOnData(attrName);
			if (controlledAttr == null) { 
				throw new GenstarException("'" + attrName + "' attribute not found in the generator. \n\tFile: " + controlledAttributesFile.getPath() + ", line: " + lineNo); 
			}
			if (!controlledAttributes.contains(controlledAttr)) { controlledAttributes.add(controlledAttr); }
			
			lineNo++;
		}
		
		// 2. parse the supplementary attributes
		supplementaryAttributes = new ArrayList<AbstractAttribute>();
		AbstractAttribute supplementaryAttr;
		lineNo=1;
		for (List<String> line : supplementaryAttributesFile.getContent()) {
			if (line.size() != 1) { throw new GenstarException("Invalid supplementary attribute file format. One line can only contain one attribute. File: " + supplementaryAttributesFile.getPath() + ", line: " + lineNo); }
			
			attrName = line.get(0);
			supplementaryAttr = generationRule.getGenerator().getAttributeByNameOnData(attrName);
			if (supplementaryAttr == null) { throw new GenstarException("'" + attrName + "' attribute not found in the generator. File: " + supplementaryAttributesFile.getPath() + ", line: " + lineNo); }
			if (controlledAttributes.contains(supplementaryAttr)) { throw new GenstarException("'" + attrName + "' attribute has already been a controlled attribute. File: " + supplementaryAttributesFile.getPath() + ", line: " + lineNo); }
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
