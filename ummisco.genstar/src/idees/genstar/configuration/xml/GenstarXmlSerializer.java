package idees.genstar.configuration.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.NamedMapConverter;
import com.thoughtworks.xstream.io.xml.DomDriver;

import idees.genstar.configuration.GSMetaDataType;
import idees.genstar.configuration.GSConfiguration;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;
import ummisco.genstar.util.GenstarCsvFile;

public class GenstarXmlSerializer {

	private static final String GS_CONFIG_ALIAS = "GS_data_Configuration";
	
	private static final String GS_METADATA_FILE_ALIAS = "GS_data_meta_type";		
	private static final String GS_FILE_ALIAS = "GS_data_file";
	private static final String GS_FILE_LIST_ALIAS = "GS_data_files";

	private static final String GS_ATTRIBUTE_LIST_ALIAS = "GS_attributs";
	private static final String GS_UNIQUE_ATTRIBUTE_ALIAS = "GS_unique_attribut";
	private static final String GS_UNIQUE_VALUE_ALIAS = "GS_unique_value";
	private static final String GS_RANGE_ATTRIBUTE_ALIAS = "GS_range_attribut";
	private static final String GS_RANGE_VALUE_ALIAS = "GS_range_value";

	
	private XStream xs = null;
	private File mkdir = null;
	
	public GenstarXmlSerializer() throws FileNotFoundException{
		this.mkdir = new File(System.getProperty("user.dir"));
		this.xs = new XStream(new DomDriver());
		
		/*
		 * Class alias for xml record
		 */
		xs.alias(GS_CONFIG_ALIAS, GSConfiguration.class);
		xs.alias(GS_FILE_ALIAS, GenstarCsvFile.class);
		xs.alias(GS_METADATA_FILE_ALIAS, GSMetaDataType.class);
		xs.alias(GS_UNIQUE_ATTRIBUTE_ALIAS, UniqueValuesAttribute.class);
		xs.alias(GS_UNIQUE_VALUE_ALIAS, UniqueValue.class);
		xs.alias(GS_RANGE_ATTRIBUTE_ALIAS, RangeValuesAttribute.class);
		xs.alias(GS_RANGE_VALUE_ALIAS, RangeValue.class);
		
		/*
		 * Map Converter
		 */
		xs.registerConverter(new NamedMapConverter(xs.getMapper(), null, "Relative_path", Path.class, "Meta_data_type", GSMetaDataType.class));
		
		/*
		 * field alias for xml record
		 */
		xs.aliasField(GS_FILE_LIST_ALIAS, GSConfiguration.class, "dataFiles");
		xs.aliasField(GS_ATTRIBUTE_LIST_ALIAS, GSConfiguration.class, "attributes");
		
		/*
		 * Field-to-attribute in xml
		 */
		xs.useAttributeFor(AttributeValue.class, "dataType");
	}
	
	public void serializeGSConfig(GSConfiguration gsd, String xmlName) throws IOException{
		Writer w = new StringWriter();
		xs.toXML(gsd, w);
		Files.write(Paths.get(mkdir+FileSystems.getDefault().getSeparator()+xmlName+".xml"), w.toString().getBytes());
	}
	
	public GSConfiguration deserializeGSConfig(Path xmlFilePath) throws FileNotFoundException{
		File valideXmlFile = xmlFilePath.toFile();
		if(!valideXmlFile.exists())
			throw new FileNotFoundException(xmlFilePath.toString());
		else if(!xmlFilePath.toString().toLowerCase().endsWith(".xml"))
			throw new FileNotFoundException("The file "+xmlFilePath+" is not an xml file");  
		else
			return (GSConfiguration) xs.fromXML(valideXmlFile);
	}
	
	public XStream getXStream(){
		return xs;
	}
	
	public void setMkdir(Path filePath) throws FileNotFoundException{
		this.mkdir = new File(filePath.toString());
		if(!mkdir.exists()){
			throw new FileNotFoundException("the file "+mkdir+" does not exist");
		} else if(!mkdir.isDirectory()){
			throw new FileNotFoundException("the file "+mkdir+" is not a directory");
		}
	}
	
}
