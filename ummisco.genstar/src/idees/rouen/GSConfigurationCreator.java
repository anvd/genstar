package idees.rouen;

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

public class GSConfigurationCreator {

	public static String CLASS_PATH = "test_data/idees/Rouen/";

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

			List<GSDataFile> individualDataFiles = new ArrayList<>();
			Set<AbstractAttribute> indivAttributes = new HashSet<>();
			
			List<GSDataFile> householdDataFiles = new ArrayList<>();
			Set<AbstractAttribute> householdAttributes = new HashSet<>();
			
			individualDataFiles.add(new GSDataFile(CLASS_PATH+File.separator+"Age & Couple-Tableau 1.csv",
					GSMetaDataType.ContingenceTable, 1, 1, ';'));
			individualDataFiles.add(new GSDataFile(CLASS_PATH+File.separator+"Age & Sexe & CSP-Tableau 1.csv",
					GSMetaDataType.ContingenceTable, 2, 1, ';'));
			individualDataFiles.add(new GSDataFile(CLASS_PATH+File.separator+"Age & Sexe-Tableau 1.csv",
					GSMetaDataType.ContingenceTable, 1, 1, ';'));
			
			householdDataFiles.add(new GSDataFile(CLASS_PATH+File.separator+"Ménage & Enfants-Tableau 1.csv",
					GSMetaDataType.ContingenceTable, 1, 1, ';'));
			householdDataFiles.add(new GSDataFile(CLASS_PATH+File.separator+"Taille ménage & CSP référent-Tableau 1.csv", 
					GSMetaDataType.ContingenceTable, 1, 1, ';'));
			householdDataFiles.add(new GSDataFile(CLASS_PATH+File.separator+"Taille ménage & Sex & Age-Tableau 1.csv", 
					GSMetaDataType.ContingenceTable, 2, 1, ';'));

			try {
				indivAttributes.add(createAttribute("Age", "age", DataType.INTEGER, 
						Arrays.asList("15 à 19 ans", "20 à 24 ans", "25 à 39 ans", "40 à 54 ans", "55 à 64 ans",
								"65 à 79 ans", "80 ans ou plus"), GSAttDataType.range));
				indivAttributes.add(createAttribute("Age2", "age", DataType.INTEGER, 
						Arrays.asList("Moins de 5 ans", "5 à 9 ans", "10 à 14 ans", "15 à 19 ans", "20 à 24 ans", 
								"25 à 29 ans", "30 à 34 ans", "35 à 39 ans", "40 à 44 ans", "45 à 49 ans", 
								"50 à 54 ans", "55 à 59 ans", "60 à 64 ans", "65 à 69 ans", "70 à 74 ans", "75 à 79 ans", 
								"80 à 84 ans", "85 à 89 ans", "90 à 94 ans", "95 à 99 ans", "100 ans ou plus"), GSAttDataType.range));
				indivAttributes.add(createAttribute("Couple", "couple", DataType.STRING, 
						Arrays.asList("Vivant en couple", "Ne vivant pas en couple"), 
						GSAttDataType.unique));
				indivAttributes.add(createAttribute("CSP", "csp", DataType.STRING, 
						Arrays.asList("Agriculteurs exploitants", "Artisans. commerçants. chefs d'entreprise", 
								"Cadres et professions intellectuelles supérieures", "Professions intermédiaires", 
								"Employés", "Ouvriers", "Retraités", "Autres personnes sans activité professionnelle"), 
						GSAttDataType.unique));
				indivAttributes.add(createAttribute("Sexe", "sexe", DataType.STRING,
						Arrays.asList("Hommes", "Femmes"), GSAttDataType.unique));
				
				householdAttributes.add(createAttribute("Ménage", "ménage", DataType.STRING, 
						Arrays.asList("Couple sans enfant", "Couple avec enfant(s)", 
								"Famille monoparentale composée d'un homme avec enfant(s)", "Famille monoparentale composée d'une femme avec enfant(s)"), 
						GSAttDataType.unique));
				householdAttributes.add(createAttribute("Enfants", "enfants", DataType.STRING, 
						Arrays.asList("Aucun enfant de moins de 25 ans", "1 enfant de moins de 25 ans", 
								"2 enfants de moins de 25 ans", "3 enfants de moins de 25 ans", 
								"4 enfants ou plus de moins de 25 ans"), GSAttDataType.unique));
				householdAttributes.add(createAttribute("Taille", "taille", DataType.INTEGER, 
						Arrays.asList("1 personne", "2 personnes", "3 personnes", "4 personnes", "5 personnes", "6 personnes ou plus"), 
						GSAttDataType.unique));
				householdAttributes.add(createAttribute("CSP référent", "csp référent", DataType.STRING, 
						Arrays.asList("Agriculteurs exploitants", "Artisans. commerçants. chefs d'entreprise", 
								"Cadres et professions intellectuelles supérieures", "Professions intermédiaires", 
								"Employés", "Ouvriers", "Retraités", "Autres personnes sans activité professionnelle"), 
						GSAttDataType.unique));
				householdAttributes.add(createAttribute("Sexe référent", "sexe référent", DataType.STRING, 
						Arrays.asList("Hommes", "Femmes"), GSAttDataType.unique));
				householdAttributes.add(createAttribute("Age référent", "age référent", DataType.INTEGER, 
						Arrays.asList("Moins de 20 ans", "20 à 24 ans", "25 à 39 ans", 
								"40 à 54 ans", "55 à 64 ans", "65 à 79 ans", "80 ans ou plus"), 
						GSAttDataType.unique));
				
			} catch (GenstarException | GenstarIllegalRangedData e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				GSConfiguration gsd = new GSConfiguration(individualDataFiles, indivAttributes);
				gxs.serializeGSConfig(gsd, "Rouen_dataConfiguration");
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
	 * FIXME: a more flexible factory to create AbstractAttribute
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
