package ummisco.genstar.gama.populations_linker;

import java.util.List;

import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.precompiler.GamlAnnotations.populations_linker;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gaml.extensions.genstar.IGamaPopulationsLinker;
import msi.gaml.types.Types;


@populations_linker(name = "miro_household_size_linker")
public class MiroPopulationsLinker implements IGamaPopulationsLinker {

	@Override
	public void setTotalRound(int totalRound) {
	}

	@Override
	public int getTotalRound() {
		return 0;
	}

	@Override
	public int getCurrentRound() {
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
		
		int memberAttributeSet = 0;
		int peoplePopulationIndex = 0;
		for (IMacroAgent household : householdPopulation) {
			Integer householdSize = (Integer) household.getAttribute("householdSize");
			
			if (householdSize > 0) {
				System.out.println("householdSize = " + householdSize);
			}
			
			// GAML field name: "member_people"
			IList<IMacroAgent> member_people = GamaListFactory.create(Types.AGENT);
			
			for (int peopleCurrentIndex = peoplePopulationIndex; peopleCurrentIndex < (peoplePopulationIndex + householdSize);  peopleCurrentIndex++) {
				if (peopleCurrentIndex >= peoplePopulation.size()) { 
					return; 
				} // out of people
				
				member_people.add(peoplePopulation.get(peopleCurrentIndex));
			}
			peoplePopulationIndex += member_people.size();
			
			household.setAttribute("member_people", member_people);
			memberAttributeSet++;
		}
	}
}
