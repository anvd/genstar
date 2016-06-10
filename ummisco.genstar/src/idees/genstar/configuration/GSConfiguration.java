package idees.genstar.configuration;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ummisco.genstar.metamodel.attributes.AbstractAttribute;

/*
 * TODO: 1) The ability to create GenstarCSVFile that can have multiple header. <br/> 
 * 			For example, change the parameter type of *header* from boolean <br/>
 * 			to integer, with convention as: 0 means no header, 1 for first line header, 2 for two-first lines header, etc. 
 * 
 * 		2) Provide a Factory to construct AbstractAttributes rather than a AttributUtils static method: <br/>
 * 		This is more API friendly !
 * 
 * 		3) Why AbstractAttributes should be init with a ISyntheticPopulationGenerator
 */
public class GSConfiguration {

	List<GSDataFile> dataFiles = new ArrayList<>();
	
	Set<AbstractAttribute> attributes = new HashSet<>();
	
	public GSConfiguration(List<GSDataFile> dataFiles, Set<AbstractAttribute> attributes){
		this.dataFiles.addAll(dataFiles);
		this.attributes.addAll(attributes);
	}
	
	public List<GSDataFile> getDataFiles(){
		return dataFiles;
	}
	
	public Set<AbstractAttribute> getAttributes(){
		return attributes;
	}

	/*
	 * Method that enable a safe serialization / deserialization of this java class <br/>
	 * The serialization process end up in xml file that represents a particular java <br/>
	 * object of this class; and the way back from xml file to java object. 
	 */
	protected Object readResolve() throws ObjectStreamException {
		List<GSDataFile> dataFiles = getDataFiles();
		Set<AbstractAttribute> attributes = getAttributes();
		return new GSConfiguration(dataFiles, attributes);
	}
}
