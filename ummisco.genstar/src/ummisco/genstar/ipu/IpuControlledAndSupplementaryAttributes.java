package ummisco.genstar.ipu;

import java.util.ArrayList;
import java.util.List;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.util.GenstarCsvFile;

public class IpuControlledAndSupplementaryAttributes {
	
	private IpuGenerationRule generationRule;
	
	private GenstarCsvFile groupControlledAttributesFile;
	
	private GenstarCsvFile groupSupplementaryAttributesFile;
	
	private GenstarCsvFile componentControlledAttributesFile;
	
	private GenstarCsvFile componentSupplementaryAttributesFile;
	
	
	private List<AbstractAttribute> groupControlledAttributes;
	
	private List<AbstractAttribute> groupSupplementaryAttributes;
	
	private List<AbstractAttribute> componentControlledAttributes;
	
	private List<AbstractAttribute> componentSupplementaryAttributes;
	

	public IpuControlledAndSupplementaryAttributes(final IpuGenerationRule generationRule) throws GenstarException {
		if (generationRule == null) { throw new GenstarException("Parameter generationRule can not be null"); }
		
		this.generationRule = generationRule;
		
		this.groupControlledAttributesFile = generationRule.getGroupControlledAttributesFile();
		if (this.groupControlledAttributesFile == null) { throw new GenstarException("Parameter groupControlledAttributesFile can not be null"); }
		
		this.groupSupplementaryAttributesFile = generationRule.getGroupSupplementaryAttributesFile();
		if (this.groupSupplementaryAttributesFile == null) { throw new GenstarException("Parameter groupSupplementaryAttributesFile can not be null"); }
		
		this.componentControlledAttributesFile = generationRule.getComponentControlledAttributesFile();
		if (this.componentControlledAttributesFile == null) { throw new GenstarException("Parameter componentControlledAttributesFile can not be null"); }

		this.componentSupplementaryAttributesFile = generationRule.getComponentSupplementaryAttributesFile();
		if (this.componentSupplementaryAttributesFile == null) { throw new GenstarException("Parameter componentSupplementaryAttributesFile can not be null"); }
		
		// parse attributes
		groupControlledAttributes = readControlledAttributes(generationRule.getGenerator(), groupControlledAttributesFile);
		groupSupplementaryAttributes = readSupplementaryAttributes(generationRule.getGenerator(), groupSupplementaryAttributesFile, groupControlledAttributes);
		componentControlledAttributes = readControlledAttributes(generationRule.getComponentGenerator(), componentControlledAttributesFile);
		componentSupplementaryAttributes = readSupplementaryAttributes(generationRule.getComponentGenerator(), componentSupplementaryAttributesFile, componentControlledAttributes);
	}
	
	
	private List<AbstractAttribute> readControlledAttributes(final ISyntheticPopulationGenerator generator, final GenstarCsvFile controlledAttributesListFile) throws GenstarException {
		List<AbstractAttribute> controlledAttributes = new ArrayList<AbstractAttribute>();
		
		if (controlledAttributesListFile.getRows() == 0) { throw new GenstarException("Controlled attributes file can not be empty. (File: " + controlledAttributesListFile.getPath() + ")"); }
		
		String attrName;
		AbstractAttribute groupControlledAttr;
		int lineNo = 1;
		for (List<String> line : controlledAttributesListFile.getContent()) {
			if (line.size() != 1) { throw new GenstarException("Invalid group controlled attribute file format. One line can only contain one attribute. File: " + controlledAttributesListFile.getPath() + ", line: " + lineNo); }
			
			attrName = line.get(0);
			groupControlledAttr = generator.getAttributeByNameOnData(attrName);
			if (groupControlledAttr == null) { 
				throw new GenstarException("'" + attrName + "' attribute not found in the generator. \n\tFile: " + controlledAttributesListFile.getPath() + ", line: " + lineNo); 
			}
			if (!controlledAttributes.contains(groupControlledAttr)) { controlledAttributes.add(groupControlledAttr); }
			
			lineNo++;
		}
		
		return controlledAttributes;
	}
	
	
	private List<AbstractAttribute> readSupplementaryAttributes(final ISyntheticPopulationGenerator generator, final GenstarCsvFile supplementaryAttributesListFile, List<AbstractAttribute> controlledAttributes) throws GenstarException {
		List<AbstractAttribute> supplementaryAttributes = new ArrayList<AbstractAttribute>();
		String attrName;
		AbstractAttribute supplementaryAttr;
		int lineNo=1;
		for (List<String> line : supplementaryAttributesListFile.getContent()) {
			if (line.size() != 1) { throw new GenstarException("Invalid supplementary attribute file format. One line can only contain one attribute. File: " + supplementaryAttributesListFile.getPath() + ", line: " + lineNo); }
			
			attrName = line.get(0);
			supplementaryAttr = generator.getAttributeByNameOnData(attrName);
			if (supplementaryAttr == null) { throw new GenstarException("'" + attrName + "' attribute not found in the generator. File: " + supplementaryAttributesListFile.getPath() + ", line: " + lineNo); }
			if (controlledAttributes.contains(supplementaryAttr)) { throw new GenstarException("'" + attrName + "' attribute has already been a controlled attribute. File: " + supplementaryAttributesListFile.getPath() + ", line: " + lineNo); }
			if (!supplementaryAttributes.contains(supplementaryAttr)) supplementaryAttributes.add(supplementaryAttr);
			
			lineNo++;
		}
		
		return supplementaryAttributes;
	}


	public List<AbstractAttribute> getGroupControlledAttributes() {
		return new ArrayList<AbstractAttribute>(groupControlledAttributes);
	}


	public List<AbstractAttribute> getComponentControlledAttributes() {
		return new ArrayList<AbstractAttribute>(componentControlledAttributes);
	}
}
