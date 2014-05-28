package ummisco.genstar.gama;

import java.util.Map;

import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaList;
import msi.gama.util.GamaMap;
import msi.gama.util.IList;
import msi.gaml.types.IType;
import ummisco.genstar.dao.GenstarDAOFactory;
import ummisco.genstar.dao.SyntheticPopulationGeneratorDAO;
import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.EntityAttributeValue;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.metamodel.ISyntheticPopulationGenerator;

@species(name = "genstar")
public class GenstarAgent extends GamlAgent {

	public GenstarAgent(IPopulation s) {
		super(s);
	}


	@action(name = "get_population",
		args = {
			@arg(name = "name", type = IType.STRING, optional = false, doc = @doc("Name of the synthetic population")),
			@arg(name = "number", type = IType.INT, optional = true, doc = @doc("Number of agents to be created"))
	})
	public IList getSyntheticPopulation(final IScope scope) throws GamaRuntimeException {
		/*
	create inhabitant from: my_generator get_population 'inhabitant'; // case 1
	create inhabitant from: my_generator get_population 'inhabitant' number: 1000; // case 2
	create inhabitant from: my_generator generate_population 'inhabitant'; // case 3
	create inhabitant from: my_generator generate_population 'inhabitant' number: 1000; // case 4
		 */
		
		// TODO
		
		return null;
	}
	
	
	@action(name = "generate_population",
		args = {
			@arg(name = "name", type = IType.STRING, optional = false, doc = @doc("Name of the synthetic population")),
			@arg(name = "number", type = IType.INT, optional = true, doc = @doc("Number of agents to be created"))
			// TODO "save" argument!?
	})
	public IList generateSyntheticPopulation(final IScope scope) throws GamaRuntimeException {
		
		// TODO 
		// 1. Use the DAO service to query the "population generator" by name
		// 2. Instantiate the GenstarGenerator and ask it to generate the "population generator" returned by step 1.
		
		try {
			
			final String populationGeneratorName = scope.getStringArg("name");
			if (populationGeneratorName == null) { // necessary or not?
				GamaRuntimeException.error("'name' argument can not be null");
			}
			
			
			GenstarDAOFactory daoFactory = GenstarDAOFactory.getDAOFactory();
			SyntheticPopulationGeneratorDAO populationGeneratorDAO = daoFactory.getSyntheticPopulationGeneratorDAO();
			ISyntheticPopulationGenerator populationGenerator = populationGeneratorDAO.findSyntheticPopulationGenerator(populationGeneratorName);
			
			ISyntheticPopulation mockPopulation = populationGenerator.generate();
			
			// TODO what is the structure of the returned list?
			// returns a List with
			// first element is a string : "genstar_population"
			// other elements are maps
			IList syntheticPopulation = new GamaList();
			syntheticPopulation.add(new String("genstar_population"));
			
			// convert entities' attributes to map
			int n = 2, i = 0;
			
			Map<String, Object> map;
			for (Entity entity : mockPopulation.getEntities()) {
				map = new GamaMap<String, Object>();
				for (Map.Entry<String, EntityAttributeValue> entry : entity.getAttributes().entrySet()) {
					map.put(entry.getKey(), Genstar2GamaTypeConversion.convertGenstar2GamaType(entry.getValue().getAttributeValueOnEntity()));
				}
				syntheticPopulation.add(map);

				i++;
				if (i == n) { break; }
			}

			return syntheticPopulation;
		} catch (final GenstarException e) {
			throw new GamaRuntimeException(e);
		}
		
	}
}
