package idees.rouen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import idees.genstar.configuration.GSAttDataType;
import idees.genstar.configuration.GSConfiguration;
import idees.genstar.configuration.GSDataFile;
import idees.genstar.configuration.GSMetaDataType;
import idees.genstar.configuration.xml.GenstarXmlSerializer;
import idees.genstar.datareader.exception.GenstarIllegalRangedData;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeFactory;
import ummisco.genstar.metamodel.attributes.DataType;

public class GSConfigurationCreator {

	public static String INDIV_CLASS_PATH = "test_data/idees/Rouen/insee_indiv";
	public static String HHOLD_CLASS_PATH = "test_data/idees/Rouen/insee_ménage";

	public static void main(String[] args) throws InvalidFormatException {
		// TODO Auto-generated method stub

		GenstarXmlSerializer gxs = null;
		try {
			gxs = new GenstarXmlSerializer();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		AttributeFactory attf = new AttributeFactory();
		if(new ArrayList<>(Arrays.asList(args)).isEmpty()){

			List<GSDataFile> individualDataFiles = new ArrayList<>();
			Set<AbstractAttribute> indivAttributes = new HashSet<>();
			
			List<GSDataFile> householdDataFiles = new ArrayList<>();
			Set<AbstractAttribute> householdAttributes = new HashSet<>();
			
			individualDataFiles.add(new GSDataFile(INDIV_CLASS_PATH+File.separator+"Age & Couple-Tableau 1.csv",
					GSMetaDataType.ContingenceTable, 1, 1, ';'));
			individualDataFiles.add(new GSDataFile(INDIV_CLASS_PATH+File.separator+"Age & Sexe & CSP-Tableau 1.csv",
					GSMetaDataType.ContingenceTable, 2, 1, ';'));
			individualDataFiles.add(new GSDataFile(INDIV_CLASS_PATH+File.separator+"Age & Sexe-Tableau 1.csv",
					GSMetaDataType.ContingenceTable, 1, 1, ';'));
			
			householdDataFiles.add(new GSDataFile(HHOLD_CLASS_PATH+File.separator+"Ménage & Enfants-Tableau 1.csv",
					GSMetaDataType.ContingenceTable, 1, 1, ';'));
			householdDataFiles.add(new GSDataFile(HHOLD_CLASS_PATH+File.separator+"Taille ménage & CSP référent-Tableau 1.csv", 
					GSMetaDataType.ContingenceTable, 1, 1, ';'));
			householdDataFiles.add(new GSDataFile(HHOLD_CLASS_PATH+File.separator+"Taille ménage & Sex & Age-Tableau 1.csv", 
					GSMetaDataType.ContingenceTable, 2, 1, ';'));

			try {
				// Instantiate a referent attribute
				AbstractAttribute referentAgeAttribute = attf.createAttribute("Age", "age", DataType.INTEGER, 
						Arrays.asList("Moins de 5 ans", "5 à 9 ans", "10 à 14 ans", "15 à 19 ans", "20 à 24 ans", 
								"25 à 29 ans", "30 à 34 ans", "35 à 39 ans", "40 à 44 ans", "45 à 49 ans", 
								"50 à 54 ans", "55 à 59 ans", "60 à 64 ans", "65 à 69 ans", "70 à 74 ans", "75 à 79 ans", 
								"80 à 84 ans", "85 à 89 ans", "90 à 94 ans", "95 à 99 ans", "100 ans ou plus"), GSAttDataType.range);
				indivAttributes.add(referentAgeAttribute);
				// Create a mapper
				Map<String, Set<String>> mapperA1 = new HashMap<>();
				mapperA1.put("15 à 19 ans", new HashSet<>(Arrays.asList("15 à 19 ans")));
				mapperA1.put("20 à 24 ans", new HashSet<>(Arrays.asList("20 à 24 ans")));
				mapperA1.put("25 à 39 ans", new HashSet<>(Arrays.asList("25 à 29 ans", "30 à 34 ans", "35 à 39 ans")));
				mapperA1.put("40 à 54 ans", new HashSet<>(Arrays.asList("40 à 44 ans", "45 à 49 ans", "50 à 54 ans")));
				mapperA1.put("55 à 64 ans", new HashSet<>(Arrays.asList("55 à 59 ans", "60 à 64 ans")));
				mapperA1.put("65 à 79 ans", new HashSet<>(Arrays.asList("65 à 69 ans", "70 à 74 ans", "75 à 79 ans")));
				mapperA1.put("80 ans ou plus", new HashSet<>(Arrays.asList("80 à 84 ans", "85 à 89 ans", "90 à 94 ans", 
						"95 à 99 ans", "100 ans ou plus")));
				// Instantiate an aggregated attribute using previously referent attribute
				indivAttributes.add(attf.createAttribute("Age2", "age", DataType.INTEGER,
						new ArrayList<>(mapperA1.keySet()), GSAttDataType.range, referentAgeAttribute,
						mapperA1));
				// Create another mapper
				Map<String, Set<String>> mapperA2 = new HashMap<>();
				mapperA2.put("15 à 19 ans", new HashSet<>(Arrays.asList("15 à 19 ans")));
				mapperA2.put("20 à 24 ans", new HashSet<>(Arrays.asList("20 à 24 ans")));
				mapperA2.put("25 à 39 ans", new HashSet<>(Arrays.asList("25 à 29 ans", "30 à 34 ans", "35 à 39 ans")));
				mapperA2.put("40 à 54 ans", new HashSet<>(Arrays.asList("40 à 44 ans", "45 à 49 ans", "50 à 54 ans")));
				mapperA2.put("55 à 64 ans", new HashSet<>(Arrays.asList("55 à 59 ans", "60 à 64 ans")));
				mapperA2.put("65 ans ou plus", new HashSet<>(Arrays.asList("65 à 69 ans", "70 à 74 ans", "75 à 79 ans", 
						"80 à 84 ans", "85 à 89 ans", "90 à 94 ans", "95 à 99 ans", "100 ans ou plus")));
				indivAttributes.add(attf.createAttribute("Age3", "age", DataType.INTEGER,
						new ArrayList<>(mapperA2.keySet()), GSAttDataType.range, referentAgeAttribute,
						mapperA2));		
				indivAttributes.add(attf.createAttribute("Couple", "couple", DataType.STRING, 
						Arrays.asList("Vivant en couple", "Ne vivant pas en couple"), 
						GSAttDataType.unique));
				indivAttributes.add(attf.createAttribute("CSP", "csp", DataType.STRING, 
						Arrays.asList("Agriculteurs exploitants", "Artisans. commerçants. chefs d'entreprise", 
								"Cadres et professions intellectuelles supérieures", "Professions intermédiaires", 
								"Employés", "Ouvriers", "Retraités", "Autres personnes sans activité professionnelle"), 
						GSAttDataType.unique));
				indivAttributes.add(attf.createAttribute("Sexe", "sexe", DataType.STRING,
						Arrays.asList("Hommes", "Femmes"), GSAttDataType.unique));
				
				householdAttributes.add(attf.createAttribute("Ménage", "ménage", DataType.STRING, 
						Arrays.asList("Couple sans enfant", "Couple avec enfant(s)", 
								"Famille monoparentale composée d'un homme avec enfant(s)", "Famille monoparentale composée d'une femme avec enfant(s)"), 
						GSAttDataType.unique));
				householdAttributes.add(attf.createAttribute("Enfants", "enfants", DataType.STRING, 
						Arrays.asList("Aucun enfant de moins de 25 ans", "1 enfant de moins de 25 ans", 
								"2 enfants de moins de 25 ans", "3 enfants de moins de 25 ans", 
								"4 enfants ou plus de moins de 25 ans"), GSAttDataType.unique));
				householdAttributes.add(attf.createAttribute("Taille", "taille", DataType.INTEGER, 
						Arrays.asList("1 personne", "2 personnes", "3 personnes", "4 personnes", "5 personnes", "6 personnes ou plus"), 
						GSAttDataType.unique));
				householdAttributes.add(attf.createAttribute("CSP référent", "csp référent", DataType.STRING, 
						Arrays.asList("Agriculteurs exploitants", "Artisans. commerçants. chefs d'entreprise", 
								"Cadres et professions intellectuelles supérieures", "Professions intermédiaires", 
								"Employés", "Ouvriers", "Retraités", "Autres personnes sans activité professionnelle"), 
						GSAttDataType.unique));
				householdAttributes.add(attf.createAttribute("Sexe référent", "sexe référent", DataType.STRING, 
						Arrays.asList("Hommes", "Femmes"), GSAttDataType.unique));
				
				// Another mapper
				Map<String, Set<String>> mapper = new HashMap<>();
				mapper.put("Moins de 20 ans", new HashSet<>(Arrays.asList("Moins de 5 ans", "5 à 9 ans", "10 à 14 ans",
						"15 à 19 ans")));
				mapper.put("20 à 24 ans", new HashSet<>(Arrays.asList("20 à 24 ans")));
				mapper.put("25 à 39 ans", new HashSet<>(Arrays.asList("25 à 29 ans", "30 à 34 ans", "35 à 39 ans")));
				mapper.put("40 à 54 ans", new HashSet<>(Arrays.asList("40 à 44 ans", "45 à 49 ans", "50 à 54 ans")));
				mapper.put("55 à 64 ans", new HashSet<>(Arrays.asList("55 à 59 ans", "60 à 64 ans")));
				mapper.put("65 à 79 ans", new HashSet<>(Arrays.asList("65 à 69 ans", "70 à 74 ans", "75 à 79 ans")));
				mapper.put("80 ans ou plus", new HashSet<>(Arrays.asList("80 à 84 ans", "85 à 89 ans", "90 à 94 ans", 
						"95 à 99 ans", "100 ans ou plus")));
				householdAttributes.add(attf.createAttribute("Age référent", "age référent", DataType.INTEGER, 
						new ArrayList<>(mapper.keySet()), GSAttDataType.range, referentAgeAttribute,
						mapper));
				
			} catch (GenstarException | GenstarIllegalRangedData e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				gxs.setMkdir(Paths.get(INDIV_CLASS_PATH));
				GSConfiguration gsdI = new GSConfiguration(individualDataFiles, indivAttributes);
				gxs.serializeGSConfig(gsdI, "GSC_RouenIndividual");
				System.out.println("Serialize Genstar individual data with:\n"+
						gsdI.getAttributes().size()+" attributs\n"+
						gsdI.getDataFiles().size()+" data files");
				
				gxs.setMkdir(Paths.get(HHOLD_CLASS_PATH));
				GSConfiguration gsdHH = new GSConfiguration(householdDataFiles, householdAttributes);
				gxs.serializeGSConfig(gsdHH, "GSC_RouenHoushold");
				System.out.println("Serialize Genstar household"
						+ " data with:\n"+
						gsdHH.getAttributes().size()+" attributs\n"+
						gsdHH.getDataFiles().size()+" data files");
				
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

}