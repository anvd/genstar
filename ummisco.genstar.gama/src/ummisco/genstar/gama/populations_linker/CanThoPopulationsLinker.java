package ummisco.genstar.gama.populations_linker;

import java.util.ArrayList;
import java.util.List;

import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.precompiler.GamlAnnotations.populations_linker;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.extensions.genstar.IGamaPopulationsLinker;
import msi.gaml.types.Types;


@populations_linker(name = "cantho_linker")
public class CanThoPopulationsLinker implements IGamaPopulationsLinker {

	@Override
	public void setTotalRound(int totalRound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getTotalRound() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCurrentRound() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void establishRelationship(final IScope scope, final IList<IList<IMacroAgent>> populations) {
		if (populations.size() != 2) { GamaRuntimeException.error("'populations' must contain 2 populations.", scope); }
		
		List<IMacroAgent> firstPopulation = populations.get(0);
		List<IMacroAgent> secondPopulation = populations.get(1);
		
		if (firstPopulation.isEmpty() || secondPopulation.isEmpty()) { return; }
		
		String firstSpeciesName = firstPopulation.get(0).getSpeciesName();
		String secondSpeciesName = secondPopulation.get(0).getSpeciesName();
		
		String GAML_PEOPLE_SPECIES = "people";
		String GAML_HOUSEHOLD_SPECIES = "household";
		
		if ( ( !firstSpeciesName.equals(GAML_PEOPLE_SPECIES) && !firstSpeciesName.equals(GAML_HOUSEHOLD_SPECIES) ) 
				|| (!secondSpeciesName.equals(GAML_PEOPLE_SPECIES) && !secondSpeciesName.equals(GAML_HOUSEHOLD_SPECIES)) ) {
			GamaRuntimeException.error("Invalid GAML species. Expected [" + GAML_PEOPLE_SPECIES + ", " + GAML_HOUSEHOLD_SPECIES + "] species.", scope);
		}
		
		List<IMacroAgent> peoplePopulation;
		List<IMacroAgent> householdPopulation;
		if (firstSpeciesName.equals(GAML_PEOPLE_SPECIES)) {
			peoplePopulation = firstPopulation;
			householdPopulation = secondPopulation;
		} else {
			peoplePopulation = secondPopulation;
			householdPopulation = firstPopulation;
		}
		

		List<IMacroAgent> availablePeople = new ArrayList<IMacroAgent>(peoplePopulation);
		for (IMacroAgent household : householdPopulation) {
			Integer householdSize = (Integer) household.getAttribute("householdSize");
			String livingPlace = (String) household.getAttribute("livingPlace");
			
			if (householdSize > 0) {
				// GAML field name: "my_members"
				IList<IMacroAgent> my_members = findMembers(availablePeople, householdSize, livingPlace);
				
				if (my_members.size() == householdSize) {
					household.setAttribute("my_members", my_members);
					availablePeople.removeAll(my_members);
					
					for (IMacroAgent member : my_members) {
						member.setAttribute("my_household", household); // GAML field name: "my_household"
					}
				}
			}
		}
	}

	private IList<IMacroAgent> findMembers(final List<IMacroAgent> peoplePopulation, final int householdSize, final String livingPlace) {
		IList<IMacroAgent> my_members = GamaListFactory.create(Types.AGENT);
		
		for (IMacroAgent person : peoplePopulation) {
			if (my_members.size() == householdSize) { break; }
			if (livingPlace.equals(person.getAttribute("livingPlace"))) { my_members.add(person); }
		}

		return my_members;
	}
}
