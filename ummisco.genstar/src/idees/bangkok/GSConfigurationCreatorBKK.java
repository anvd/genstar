package idees.bangkok;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import idees.genstar.configuration.GSAttDataType;
import idees.genstar.configuration.GSConfiguration;
import idees.genstar.configuration.GSDataFile;
import idees.genstar.configuration.GSMetaDataType;
import idees.genstar.configuration.xml.GenstarXmlSerializer;
import idees.genstar.datareader.GSDataParser;
import idees.genstar.datareader.exception.GenstarIllegalRangedData;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.RangeValuesAttribute;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.attributes.UniqueValuesAttribute;

public class GSConfigurationCreatorBKK {

	public static String CLASS_PATH = "data_test/BKK/";

	public static void main(String[] args) throws InvalidFormatException {
		// TODO Auto-generated method stub

		GenstarXmlSerializer gxs = null;
		try {
			gxs = new GenstarXmlSerializer();
			gxs.setMkdir(Paths.get(CLASS_PATH));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(new ArrayList<>(Arrays.asList(args)).isEmpty()){

			List<GSDataFile> dataFiles = new ArrayList<>();
			Set<AbstractAttribute> attributes = new HashSet<>();
			
			dataFiles.add(new GSDataFile(CLASS_PATH+File.separator+"BKK 160 NSO10 DEM.csv",
					GSMetaDataType.ContingenceTable, 1, 4, ';'));
			dataFiles.add(new GSDataFile(CLASS_PATH+File.separator+"BKK 160 NSO10 NATREL.csv",
					GSMetaDataType.IndivFrequenceTable, 1, 4, ';'));
			dataFiles.add(new GSDataFile(CLASS_PATH+File.separator+"BKK 160 NSO10 WRK.csv",
					GSMetaDataType.IndivFrequenceTable, 1, 4, ';'));
			dataFiles.add(new GSDataFile(CLASS_PATH+File.separator+"BKK 160 NSO10EDU.csv",
					GSMetaDataType.IndivFrequenceTable, 1, 4, ';'));
			dataFiles.add(new GSDataFile(CLASS_PATH+File.separator+"Districts.csv", 
					GSMetaDataType.ContingenceTable, 1, 4, ';'));

			try {
				attributes.add(createAttribute("PAT NAME", "canton", DataType.STRING, 
						Arrays.asList("Phra Borom Maha Ratchawang", "Wang Burapha Phirom", "Wat Ratchabophit", "Samran Rat", "San Chao Pho Suea",
								"Sao Chingcha", "Bowon Niwet", "Talat Yot", "Chana Songkhram", "Ban Phan Thom", "Bang Khun Phrom", "Wat Sam Phraya",
								"Dusit", "Wachira Phayaban", "Suan Chitlada", "Si Yaek Mahanak", "Thanon Nakhon Chai Si", "Krathum Rai", "Nong Chok",
								"Khlong Sip", "Khlong Sip Song", "Khok Faet", "Khu Fang Nuea", "Lam Phak Chi", "Lam Toiting", "Maha Phruettharam", 
								"Si Lom", "Suriyawong", "Bang Rak", "Si Phraya", "Anusawari", "Tha Raeng", "Khlong Chan", "Hua Mak", "Rong Mueang",
								"Wang Mai", "Pathum Wan", "Lumphini", "Pom Prap", "Wat Thep Sirin", "Khlong Maha Nak", "Ban Bat", "Wat Sommanat",
								"Bang Chak", "Min Buri", "Saen Saep", "Lat Krabang", "Khlong Song Ton Nun", "Khlong Sam Prawet", "Lam Pla Thio",
								"Thap Yao", "Khum Thong", "Chong Nonsi", "Bang Phongphang", "Chakkrawat", "Samphanthawong", "Talat Noi", "Sam Sen Nai",
								"Wat Kanlaya", "Hiran Ruchi", "Bang Yi Ruea", "Bukkhalo", "Talat Phlu", "Dao Khanong", "Samre", "Wat Arun", "Wat Tha Phra",
								"Huai Khwang", "Bang Kapi", "Sam Sen Nok", "Somdet Chao Phraya", "Khlong San", "Bang Lamphu Lang", "Khlong Ton Sai", 
								"Khlong Chak Phra", "Taling Chan", "Chimphli", "Bang Phrom", "Bang Ramat", "Bang Chueak Nang", "Sirirat", "Ban Chang Lo",
								"Bang Khun Non", "Bang Khun Si", "Arun Amarin", "Tha Kham", "Samae Dam", "Bang Wa", "Bang Duan", "Bang Chak", "Bang Waek",
								"Khlong Khwang", "Pak Khlong Phasi Charoen", "Khuha Sawan", "Nong Khaem", "Nong Khang Phlu", "Rat Burana", "Bang Pa Kok",
								"Bang Phlat", "Bang O", "Bang Bamru", "Bang Yi Khan", "Din Daeng", "Khlong Kum", "Thung Wat Don", "Yan Nawa", "Thung Maha Mek",
								"Bang Sue", "Lat Yao", "Sena Nikhom", "Chan Kasem", "Chom Phon", "Chatuchak", "Bang Kho Laem", "Wat Phraya Krai", "Bang Khlo",
								"Prawet", "Nong Bon", "Dok Mai", "Khlong Toei", "Khlong Tan", "Phra Khanong", "Suan Luang", "Bang Khun Thian", "Bang Kho",
								"Bang Mot", "Chom Thong", "Si Kan", "Thung Phaya Thai", "Thanon Phaya Thai", "Thanon Phetchaburi", "Makkasan", "Lat Phrao",
								"Chorakhe Bua", "Khlong Toei Nuea", "Khlong Tan Nuea", "Phra Khanong Nuea", "Bang Khae", "Bang Khae Nuea", "Bang Phai",
								"Lak Song", "Thung Song Hong", "Talat Bang Khen", "Sai Mai", "O Ngoen", "Khlong Thanon", "Khan Na Yao", "Saphan Sung",
								"Wang Thonglang", "Sam Wa Tawantok", "Sam Wa Tawan-ok", "Bang Chan", "Sai Kong Din", "Sai Kong Din Tai", "Bang Na",
								"Thawi Watthana", "Sala Thammasop", "Bang Mot", "Thung Khru", "Bang Bon"), GSAttDataType.unique));
				attributes.add(createAttribute("PA NAME", "region", DataType.STRING, 
						Arrays.asList("Phra Nakhon", "Dusit", "Nong Chok", "Bang Rak", "Bang Khen", "Bang Kapi", "Pathum Wan", "Pom Prap Sattru Phai",
								"Phra Khanong", "Min Buri", "Lat Krabang", "Yan Nawa", "Samphanthawong", "Phaya Thai", "Thon Buri", "Bangkok Yai",
								"Huai Khwang", "Khlong San", "Taling Chan", "Bangkok Noi", "Bang Khun Thian", "Phasi Charoen", "Nong Khaem", "Rat Burana",
								"Bang Phlat", "Din Daeng", "Bueng Kum", "Sathon", "Bang Sue", "Chatuchak", "Bang Kho Laem", "Prawet", "Khlong Toei",
								"Suan Luang", "Chom Thong", "Don Mueang", "Ratchathewi", "Lat Phrao", "Watthana", "Bang Khae", "Lak Si", "Sai Mai",
								"Khan Na Yao", "Saphan Sung", "Wang Thonglang", "Khlong Sam Wa", "Bang Na", "Thawi Watthana", "Thung Khru", "Bang Bon"), 
						GSAttDataType.unique));
				attributes.add(createAttribute("population", "population", DataType.STRING,
						Arrays.asList("POP"), GSAttDataType.unique));
				/*attributes.add(createAttribute("nationality", "nationality", DataType.STRING, 
						Arrays.asList("NAT1", "NATCHN", "NATBURM", "NATKAM", "NATLAO", "NATOTH", "NAT3"), GSAttDataType.unique));
				attributes.add(createAttribute("religion", "religion", DataType.STRING, 
						Arrays.asList("RG1", "RG2", "RG3", "RG4", "RG5", "RG6+7+8"), GSAttDataType.unique));*/
				attributes.add(createAttribute("education", "education", DataType.STRING, 
						Arrays.asList("GC00", "GC01", "GC02", "GC03", "GC04", "GC05", "GC06"), GSAttDataType.unique));
				attributes.add(createAttribute("csp", "csp", DataType.STRING,
						Arrays.asList("TOCC1", "TOCC2", "TOCC3", "TOCC4", "TOCC5", "TOCC6", "TOCC7", "TOCC8", "TOCC9", "TTOCC"), 
						GSAttDataType.unique)); 
				attributes.add(createAttribute("tranche age", "age", DataType.INTEGER,
						Arrays.asList("0-4", "5-9", "10-14", "15-19", "20-24", "25-29", "30-34", "35-39", "40-44", "45-49", "50-54", "55-59",
								"60-64", "65-69", "70-74", "75-79", "80-84", "85-89", "90-94", "95-99", "100+"), 
						GSAttDataType.range));
				attributes.add(createAttribute("genre", "genre", DataType.STRING,
						Arrays.asList("Male", "Female"),
						GSAttDataType.unique));
			} catch (GenstarException | GenstarIllegalRangedData e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				GSConfiguration gsd = new GSConfiguration(dataFiles, attributes);
				gxs.serializeGSConfig(gsd, "MO3_Config");
				System.out.println("Serialize Genstar data with:\n"+
						gsd.getAttributes().size()+" attributs\n"+
						gsd.getDataFiles().size()+" data files");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			GSConfiguration gsd = null;
			try {
				gsd = gxs.deserializeGSConfig(Paths.get(args[0].trim()));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Deserialize Genstar data configuration contains:\n"+
					gsd.getAttributes().size()+" attributs\n"+
					gsd.getDataFiles().size()+" data files");
		}
	}

	/*
	 * TODO: a more flexible factory to create AbstractAttribute
	 */
	public static AbstractAttribute createAttribute(String nameOnData, String nameOnEntity, DataType dataType, List<String> values, 
			GSAttDataType valueType) throws GenstarException, GenstarIllegalRangedData{
		AbstractAttribute att = null;
		GSDataParser parser = new GSDataParser();

		if(valueType.equals(GSAttDataType.unique)){
			att = new UniqueValuesAttribute(nameOnData, nameOnEntity, dataType, valueType.getAttributeOnEntityMetaDataClass());
			for(String value : values){
				att.add(new UniqueValue(dataType, value.trim(), att));
			}
		} else if (valueType.equals(GSAttDataType.range)){
			att = new RangeValuesAttribute(nameOnData, nameOnEntity, dataType, valueType.getAttributeOnEntityMetaDataClass());
			for(String val : values){
				if(dataType.equals(DataType.INTEGER)){
					List<Integer> intVal = parser.getRangedIntegerData(val, false, 0, 150);
					att.add(new RangeValue(dataType, intVal.get(0).toString(), intVal.get(1).toString(), val, att));
				}
				if(dataType.equals(DataType.DOUBLE) || dataType.equals(DataType.FLOAT)){
					List<Double> doublVal = parser.getRangedDoubleData(val, false, 0d, 150d);
					att.add(new RangeValue(dataType, doublVal.get(0).toString(), doublVal.get(1).toString(), val, att));
				}
			}
		} else {
			throw new GenstarException("The attribute meta data type "+valueType+" is not applicable !");
		}

		return att;
	}

}
