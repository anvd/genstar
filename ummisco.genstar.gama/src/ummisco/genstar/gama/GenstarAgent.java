package ummisco.genstar.gama;

import java.util.Collections;
import java.util.List;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gaml.types.IType;
import ummisco.genstar.dao.GenstarDAOFactory;
import ummisco.genstar.dao.SyntheticPopulationGeneratorDAO;
import ummisco.genstar.exception.GenstarDAOException;

/**
 * A set of Genstar built-in variables.
 */
@species(name = "genstar")
@vars({ 
	@var(name = Genstars.POPULATION_GENERATORS, type = IType.LIST), 
	@var(name = Genstars.SYNTHETIC_POPULATIONS, type = IType.LIST),
})
public class GenstarAgent extends GamlAgent {

	public static final String POPULATION_GENERATORS = "population_generators";
	
	public static final String SYNTHETIC_POPULATIONS = "synthetic_populations";
	
	private SyntheticPopulationGeneratorDAO syntheticPopulationGeneratorDAO = null;
	

	public GenstarAgent(IPopulation s) {
		super(s);

		try {
			syntheticPopulationGeneratorDAO = GenstarDAOFactory.getDAOFactory().getSyntheticPopulationGeneratorDAO();
		} catch (final GenstarDAOException e) {
			GamaRuntimeException.error(e.getMessage(), this.getScope());
		}
	}
	
	/**
	 * Returns a list of available population generator names in the DBMS.
	 * 
	 * @return
	 */
	@getter(POPULATION_GENERATORS)
	public List<String> getPopulationGenerators() {
		try {
			return syntheticPopulationGeneratorDAO.getPopulationGeneratorNames();
		} catch (Exception e) {
			GamaRuntimeException.error(e.getMessage(), GAMA.getRuntimeScope());
			return GamaList.EMPTY_LIST;
		}
	}

	/**
	 * Returns a list of available synthetic population names available in the DBMS.
	 * @return
	 */
	@getter(SYNTHETIC_POPULATIONS)
	public List<String> getSyntheticPopulations() {
		return Collections.EMPTY_LIST;
	}

//	
//	
//	// 2. generates the population of a generator then returns the synthetic population
//	
//	// 3. GenstarGenerator
//	//		+ member population generators
//	//		+ IPopulationLinkers
//	//		+ target GAMA species? 
//
//
//	@action(name = "query_genstar_population",
//		args = {
//			@arg(name = "name", type = IType.STRING, optional = false, doc = @doc("Name of the synthetic population")),
//			@arg(name = "number", type = IType.INT, optional = true, doc = @doc("Number of agents to be created"))
//		},
//		doc = @doc(examples = {},
//			value = "Returns a synthetic population with the specified name"
//		)
//	)
//	public IList getSyntheticPopulation(final IScope scope) throws GamaRuntimeException {
//		/*
//	create inhabitant from: my_generator get_population 'inhabitant'; // case 1
//	create inhabitant from: my_generator get_population 'inhabitant' number: 1000; // case 2
//	create inhabitant from: my_generator generate_population 'inhabitant'; // case 3
//	create inhabitant from: my_generator generate_population 'inhabitant' number: 1000; // case 4
//		 */
//		
//		// TODO
//		// "Building population"
//		final String populationName = scope.getStringArg("name");
//		if (populationName == null) { GamaRuntimeException.error("'name' argument can not be null", scope); }
//		
//		int number = scope.hasArg("number") ? scope.getIntArg("name") : -1;
//		
//		// TODO query the DAO
//		
//		IList syntheticPopulation = new GamaList();
//		syntheticPopulation.add(new String("genstar_population"));
//
//		try {
//			// mock population
//			UrbanEvolutionData urbanPopulationData = new UrbanEvolutionData();
//			ISyntheticPopulation urbanPopulation = urbanPopulationData.getPopulationGenerator().generate();
//			
//			return convertGenstarPopulation2GamaPopulation(scope, urbanPopulation);
//		} catch (GenstarException e) {
//			GamaRuntimeException.error(e.getMessage(), scope);
//		}
//				
//		return null;
//	}
//	
//	
//	@action(name = "generate_genstar_population",
//		args = {
//			@arg(name = "name", type = IType.STRING, optional = false, doc = @doc("Name of the synthetic population")),
//			@arg(name = "number", type = IType.INT, optional = true, doc = @doc("Number of agents to be created"))
//			// TODO "save" argument!?
//		},
//		doc = @doc(examples = {},
//			value = ""
//			)
//	)
//	public IList generateSyntheticPopulation(final IScope scope) throws GamaRuntimeException {
//		
//		// TODO 
//		// 1. Use the DAO service to query the "population generator" by name
//		// 2. Instantiate the GenstarGenerator and ask it to generate the "population generator" returned by step 1.
//		
//		try {
//			
//			final String populationGeneratorName = scope.getStringArg("name");
//			if (populationGeneratorName == null) { // necessary or not?
//				GamaRuntimeException.error("'name' argument can not be null", scope);
//			}
//			
//			
//			GenstarDAOFactory daoFactory = GenstarDAOFactory.getDAOFactory();
//			SyntheticPopulationGeneratorDAO populationGeneratorDAO = daoFactory.getSyntheticPopulationGeneratorDAO();
//			ISyntheticPopulationGenerator populationGenerator = populationGeneratorDAO.findSyntheticPopulationGeneratorByName(populationGeneratorName);
//			
//			ISyntheticPopulation mockPopulation = populationGenerator.generate();
//			
//			// TODO what is the structure of the returned list?
//			// returns a List with
//			// first element is a string : "genstar_population"
//			// other elements are maps in which each map entry is <attribute name, attribute value>
//			IList syntheticPopulation = new GamaList();
//			syntheticPopulation.add(new String("genstar_population"));
//			
//			// convert entities' attributes to map
//			int n = 2, i = 0;
//			
//			Map<String, Object> map;
//			for (Entity entity : mockPopulation.getEntities()) {
//				map = new GamaMap<String, Object>();
//				for (Map.Entry<String, EntityAttributeValue> entry : entity.getAttributeValues().entrySet()) {
//					map.put(entry.getKey(), Genstar2GamaTypeConversion.convertGenstar2GamaType(entry.getValue().getAttributeValueOnEntity()));
//				}
//				syntheticPopulation.add(map);
//
//				i++;
//				if (i == n) { break; }
//			}
//
//			return syntheticPopulation;
//		} catch (final GenstarException e) {
//			GamaRuntimeException.error(e.getMessage(), scope);
//		}
//		
//		return GamaList.EMPTY_LIST;
//	}
//	
//	private final String GENSTAR_POPULATION_NAME = "genstar_population";
//	
//	/**
//	 * Converts a Gen* population to a GAMA population.
//	 * The struture of the GAMA population is a list in which
//	 * 		the first element is the GENSTAR_POPULATION_NAME string
//	 * 		other elements are maps. Each map represents attribute values of an agent (a map element is <attribute name, attribute value>).
//	 * 
//	 * @param scope
//	 * @param genstarPopulation the Gen* population 
//	 * @return a list, representing the GAMA population.
//	 */
//	private GamaList convertGenstarPopulation2GamaPopulation(final IScope scope, final ISyntheticPopulation genstarPopulation) {
//		GamaList gamaPopulation = new GamaList();
//		gamaPopulation.add(GENSTAR_POPULATION_NAME);
//		
//		try {
//			
//			// TODO support nested populations, e.g., household <-> inhabitant
//			
//			Map<String, Object> map;
//			for (Entity entity : genstarPopulation.getEntities()) {
//				map = new GamaMap<String, Object>();
//				for (Map.Entry<String, EntityAttributeValue> entry : entity.getAttributeValues().entrySet()) {
//					map.put(entry.getKey(), Genstar2GamaTypeConversion.convertGenstar2GamaType(entry.getValue().getAttributeValueOnEntity()));
//				}
//				gamaPopulation.add(map);
//			}
//		} catch (final GenstarException e) {
//			GamaRuntimeException.error(e.getMessage(), scope);
//		}
//		
//		return gamaPopulation;
//	}
}