package ummisco.genstar.gama;

import java.util.ArrayList;
import java.util.List;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.validator;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.compilation.IDescriptionValidator;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.types.IType;
import ummisco.genstar.gama.GenstarCreateStatement.GenstarCreateValidator;
import ummisco.genstar.metamodel.ISyntheticPopulation;


@symbol(
	name = GenstarCreateStatement.GENSTAR_CREATE,
	kind = ISymbolKind.SEQUENCE_STATEMENT,
	with_sequence = true,
	with_args = false,
	remote_context = false
)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = {
	@facet(name = GenstarCreateStatement.SYNTHETIC_POPULATION,
		type = IType.LIST,
		optional = false,
		doc = @doc("an expression that evaluates to a list representing the Genstar synthetic population")) },
		omissible = GenstarCreateStatement.SYNTHETIC_POPULATION)
@validator(GenstarCreateValidator.class)
public class GenstarCreateStatement extends AbstractStatementSequence {

	public static class GenstarCreateValidator implements IDescriptionValidator<StatementDescription> {

		@Override
		public void validate(StatementDescription description) {
			// TODO what to do?
		}
		
	}

	
	public static final String GENSTAR_CREATE = "genstar_create";
	
	public static final String SYNTHETIC_POPULATION = "synthetic_population";
	
	private IExpression syntheticPopulation;
	
	
	public GenstarCreateStatement(final IDescription desc) {
		super(desc);
		
		syntheticPopulation = getFacet(SYNTHETIC_POPULATION);
		setName(GENSTAR_CREATE);
	}

	@Override
	public IList<? extends IAgent> privateExecuteIn(final IScope scope) {
		IList genstarPopulation = (IList) syntheticPopulation.value(scope);
		
		String speciesName = (String) genstarPopulation.get(0);
		IPopulation population = scope.getSimulationScope().getPopulationFor(speciesName);
		
		if (population == null) { throw GamaRuntimeException.error(speciesName + " species not found", scope); }
		
		// extract init values
		IList<GamaMap> inits = GamaListFactory.create();
		inits.addAll(genstarPopulation.subList(1, genstarPopulation.size()-1));
		
		createAgents(scope, population, inits);
		
		return null;
	}
	
	private void createAgents(final IScope scope, final IPopulation gamaPopulation, final IList<GamaMap> inits) {
		
		// each element of inits is a map containing
		//		attribute values of an agent to be created
		//		if an agent has component populations, then this in information is the last element of the list
		
		for (GamaMap element : inits) {
			
			IList genstarComponentPopulations = (IList) element.get(ISyntheticPopulation.class);
			if (genstarComponentPopulations == null) { // without Genstar component populations
				List initialValues = new ArrayList(); 
				initialValues.add(element);
				
				IList<? extends IAgent> newAgents = gamaPopulation.createAgents(scope, 1, initialValues, false);
			} else { // with Genstar component populations
				GamaMap copyElement = GamaMapFactory.create();
				copyElement.putAll(element);
				copyElement.remove(ISyntheticPopulation.class);
				
				List initialValues = new ArrayList(); 
				initialValues.add(copyElement);

				// create "group" agent in GAMA
				IList<? extends IAgent> newGroupAgents = gamaPopulation.createAgents(scope, 1, initialValues, false);
				
				// create component agents in GAMA
				for (Object genstarComponentPopulation : genstarComponentPopulations) {
					List genstarComPopulation = (List)genstarComponentPopulation;
					
					// first element is the population/species name
					String componentSpeciesName = (String)genstarComPopulation.get(0);
					IPopulation componentGamaPopulation = scope.getSimulationScope().getPopulationFor(componentSpeciesName);
					if (componentGamaPopulation == null) { throw GamaRuntimeException.error(componentSpeciesName + " species not found", scope); }
					
					// extract init values
					IList<GamaMap> componentInitialValues = GamaListFactory.create();
					componentInitialValues.addAll(genstarComPopulation.subList(1, genstarComPopulation.size()-1));
					
					// recursively create component agents in GAMA
					this.createAgents(scope, componentGamaPopulation, componentInitialValues);
					
					// TODO establish the link between "group" agent and "component" agents
				}
			}
		}
		
	}
}
