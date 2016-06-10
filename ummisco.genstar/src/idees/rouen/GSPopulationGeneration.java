package idees.rouen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import idees.genstar.DistributionBasedGenerator;
import idees.genstar.DistributionFactory;
import idees.genstar.configuration.GSConfiguration;
import idees.genstar.configuration.xml.GenstarXmlSerializer;
import idees.genstar.datareader.exception.InputFileNotSupportedException;
import idees.genstar.distribution.exception.IllegalAlignDistributions;
import idees.genstar.distribution.exception.IllegalDistributionCreation;
import idees.genstar.distribution.exception.IllegalNDimensionalMatrixAccess;
import idees.genstar.distribution.innerstructure.InDimensionalMatrix;
import idees.genstar.exception.IncompatibleControlTotalException;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.generators.ISyntheticPopulationGenerator;
import ummisco.genstar.metamodel.population.IPopulation;

public class GSPopulationGeneration {
	
	public static void main(String[] args) {
		// THE POPULATION TO BE GENERATED
		IPopulation population = null;

		// LOAD CONFIGURATION
		GenstarXmlSerializer gxs = null;
		GSConfiguration gsd = null;
		try {
			gxs = new GenstarXmlSerializer();
			gsd = gxs.deserializeGSConfig(Paths.get(args[0].trim()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// SETUP THE BUILDER WITH CONFIGURATION FILE
		DistributionFactory builder = new DistributionFactory();
		builder.setGenstarConfiguration(gsd);

		// RETRIEV INFORMATION FROM DATA IN FORM OF A SET OF JOINT DISTRIBUTIONS 
		// TODO: sample are not yet allowed
		Set<InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number>> jointDistributionsSet = null;
		try {
			jointDistributionsSet = builder.buildDistributions();
		} catch (InvalidFormatException | IOException | InputFileNotSupportedException e1) {
			e1.printStackTrace();
		} catch (IllegalNDimensionalMatrixAccess e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalDistributionCreation e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		// INFER DISTRIBUTION: joint or conditional (with independance when no data are available)
		// TODO: inference is very limited -> implement baysian network and markov chain monte carlos inference algorithm
		InDimensionalMatrix<AbstractAttribute, AttributeValue, Double> distribution = null;
		try {
			distribution = builder.getDistribution();
		} catch (IncompatibleControlTotalException e) {
			for(InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> gdct : jointDistributionsSet){
				System.out.println(gdct.getMetaDataType());
				for(AbstractAttribute att : gdct.getDimensions()){
					try {
						System.out.println("\t"+att.getNameOnData()+" = "+gdct.getVal(att.valuesOnData()).getValue());
					} catch (IllegalNDimensionalMatrixAccess e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			e.printStackTrace();
		} catch (IllegalDistributionCreation e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalNDimensionalMatrixAccess e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAlignDistributions e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Export control totals to csv
		try {
			int i = 1;
			for(InDimensionalMatrix<AbstractAttribute, AttributeValue, ? extends Number> ct : jointDistributionsSet)
				Files.write(Paths.get("./data_test/BKK/ControlTable/logBKK_t"+(i++) +".csv"), ct.toCsv(';').getBytes());
			// TODO better toString method for InDimensionalMatrix
			//Files.write(Paths.get("./data_test/BKK/Distribution/distBKK.txt"), distribution.toString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// POPULATION SYNTHESIS
		int popSize = 1000000;
		int realBKKPopSize = 8305213;
		ISyntheticPopulationGenerator dbg = new DistributionBasedGenerator(distribution, realBKKPopSize);
		try {
			population = dbg.generate();
		} catch (GenstarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(population.getNbOfEntities() != dbg.getNbOfEntities())
			System.out.println("Pas le bon nombre d'agents généré: "+population.getNbOfEntities()+" plutôt que "+popSize);
		else
			try {
				Files.write(Paths.get("./data_test/BKK/Population/BKKscale1_popReport.csv"), population.csvReport("; ").getBytes());
				System.out.println("Done !");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
