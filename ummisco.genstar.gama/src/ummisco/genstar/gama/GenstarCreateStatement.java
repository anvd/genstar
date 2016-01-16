package ummisco.genstar.gama;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
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
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.AbstractStatementSequence;
import msi.gaml.statements.RemoteSequence;
import msi.gaml.types.IType;
import ummisco.genstar.gama.GenstarCreateStatement.GenstarCreateValidator;
import ummisco.genstar.metamodel.ISyntheticPopulation;


@symbol(
	name = GenstarCreateStatement.GENSTAR_CREATE,
	kind = ISymbolKind.SEQUENCE_STATEMENT,
	with_sequence = true,
	with_args = false,
	remote_context = true
)
@inside(kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SEQUENCE_STATEMENT })
@facets(value = {
			@facet(name = GenstarCreateStatement.SYNTHETIC_POPULATION,
			type = IType.LIST,
			optional = false,
			doc = @doc("an expression that evaluates to a list representing the Genstar synthetic population")),
			@facet(name = IKeyword.RETURNS,
			type = IType.NEW_TEMP_ID,
			optional = true,
			doc = @doc("a new temporary variable name containing the list of created agents (a lsit even if only one agent has been created)"))
		},
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
	
	private final RemoteSequence sequence;

	private final String returns;

	
	public GenstarCreateStatement(final IDescription desc) {
		super(desc);
		
		syntheticPopulation = getFacet(SYNTHETIC_POPULATION);
		sequence = new RemoteSequence(description);
		sequence.setName("commands of genstar_create");
		setName(GENSTAR_CREATE);
		returns = getLiteral(IKeyword.RETURNS);
	}

	@Override
	public void setChildren(final List<? extends ISymbol> com) {
		sequence.setChildren(com);
	}

	@Override
	public void enterScope(final IScope scope) {
		if ( returns != null ) {
			scope.addVarWithValue(returns, null);
		}
		super.enterScope(scope);
	}

	@Override
	public IList<? extends IAgent> privateExecuteIn(final IScope scope) {
		IList genstarPopulation = (IList) syntheticPopulation.value(scope);
		
		// First three elements of a GAMA synthetic population
		String speciesName = (String) genstarPopulation.get(0); // first element is the population/species name
		Map<String, String> groupReferences = (Map<String, String>) genstarPopulation.get(1); // second element contains references to "group" agents
		Map<String, String> componentReferences = (Map<String, String>) genstarPopulation.get(2); // third element contains references to "component" agents
		
		IPopulation population = scope.getSimulationScope().getPopulationFor(speciesName);
		if (population == null) { throw GamaRuntimeException.error(speciesName + " species not found", scope); }
		
		// extract init values
		IList<GamaMap> inits = GamaListFactory.create();
		inits.addAll(genstarPopulation.subList(3, genstarPopulation.size()));
		
		IList<? extends IAgent> createdAgents = createAgents(null, scope, population, inits, groupReferences, componentReferences);
		
		if ( !sequence.isEmpty() ) {
			for ( final IAgent remoteAgent : createdAgents ) {
				Object[] result = new Object[1];
				if ( !scope.execute(sequence, remoteAgent, null, result) ) {
					break;
				}
			}
		}
		
		if ( returns != null ) {
			scope.setVarValue(returns, createdAgents);
		}

		return createdAgents;
	}
	
	private IList<? extends IAgent> createAgents(final IAgent groupAgent, final IScope scope, final IPopulation gamaPopulation, final IList<GamaMap> inits, 
			final Map<String, String> groupReferences, final Map<String, String> componentReferences) {
		
		// each element of IList inits is a map containing
		//		attribute values of an agent to be created
		//			if the agent has component populations, then the map contains an element with ISyntheticPopulation.class as key
		
		IList<IAgent> outerMostCreatedAgents = GamaListFactory.create();
		for (GamaMap element : inits) {
			
			// extract initial values
			List initialValues = new ArrayList();
			IList genstarComponentPopulations = (IList) element.get(ISyntheticPopulation.class);
			if (genstarComponentPopulations == null) { // without Genstar component populations
				initialValues.add(element);
			} else {
				GamaMap copyElement = GamaMapFactory.create();
				copyElement.putAll(element);
				copyElement.remove(ISyntheticPopulation.class);

				initialValues.add(copyElement);
			}
			
			// create agent
			IAgent newAgent = gamaPopulation.createAgents(scope, 1, initialValues, false).get(0);
			outerMostCreatedAgents.add(newAgent);
			
			// establish "group" reference (of newAgents)
			if (groupAgent != null && groupReferences.containsKey(groupAgent.getSpeciesName()) 
					&& gamaPopulation.getSpecies().getVarNames().contains(groupReferences.get(groupAgent.getSpeciesName()))) {
				newAgent.setAttribute(groupReferences.get(groupAgent.getSpeciesName()), groupAgent);
			}

			// create component agents if necessary
			if (genstarComponentPopulations != null) {
				
				for (Object genstarComponentPopulation : genstarComponentPopulations) {
					List genstarComPopulation = (List)genstarComponentPopulation;
					
					// First three elements of a GAMA synthetic population
					String componentSpeciesName = (String)genstarComPopulation.get(0); // first element is the population/species name
					Map<String, String> componentGroupReferences = (Map<String, String>) genstarComPopulation.get(1); // second element contains references to "group" agents
					Map<String, String> componentComponentReferences = (Map<String, String>) genstarComPopulation.get(2); // third element contains references to "component" agents
					
					IPopulation componentGamaPopulation = scope.getSimulationScope().getPopulationFor(componentSpeciesName);
					if (componentGamaPopulation == null) { throw GamaRuntimeException.error(componentSpeciesName + " species not found", scope); }
					
					
					// extract init values
					IList<GamaMap> componentInitialValues = GamaListFactory.create();
					componentInitialValues.addAll(genstarComPopulation.subList(3, genstarComPopulation.size()));
					
					// recursively create component agents in GAMA
					IList<? extends IAgent> componentAgents = this.createAgents(newAgent, scope, componentGamaPopulation, componentInitialValues, componentGroupReferences, componentComponentReferences);
					
					// establish the link between the agent and its "component" agents
					if (componentReferences.containsKey(componentGamaPopulation.getSpecies().getName())) {
						newAgent.setAttribute(componentReferences.get(componentGamaPopulation.getSpecies().getName()), componentAgents);
					}
				}
			}
		}
		
		return outerMostCreatedAgents;
	}
}
