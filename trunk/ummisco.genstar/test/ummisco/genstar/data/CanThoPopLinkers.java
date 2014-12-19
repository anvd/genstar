package ummisco.genstar.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractPopulationsLinker;
import ummisco.genstar.metamodel.AttributeValue;
import ummisco.genstar.metamodel.DataType;
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.EntityAttributeValue;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.metamodel.UniqueValue;

public class CanThoPopLinkers {

	public class Scenario1PopLinker extends AbstractPopulationsLinker {
		
		private ISyntheticPopulation inhabitantPopulation, householdPopulation;

		// internal data
		private List<Entity> pickedHouseholds = null;
		private List<Entity> pickedInhabitants = null;

		@Override
		public void establishRelationship(final List<ISyntheticPopulation> populations) throws GenstarException {
			// populations:
			// 		1. population 1: Scenario1's inhabitant population
			//		2. population 2: Scenario1's household population
			
			if (populations == null) { throw new IllegalArgumentException("'populations' parameter can not be null"); }
			if (populations.size() != 2) { throw new IllegalArgumentException("'populations' must contain 2 populations"); }
			
			ISyntheticPopulation tmpPop1 = populations.get(0);
			ISyntheticPopulation tmpPop2 = populations.get(1);
			
			if (tmpPop1.getName().equals("Scenario1's inhabitant population")) { 
				inhabitantPopulation = tmpPop1; 
				householdPopulation = tmpPop2;
			} else {
				inhabitantPopulation = tmpPop2; 
				householdPopulation = tmpPop1;
			}
			
			if (householdPopulation == null || inhabitantPopulation == null) {
				throw new IllegalArgumentException("'populations' doesn't contain required populations");
			}
			
			this.populations = new ArrayList<ISyntheticPopulation>(populations);
			
			// pick inhabitants into households
			EntityAttributeValue hhSizeEntityAttrValue;
			UniqueValue hhSizeAttrValue;
			int hhSizeIntValue;
			pickedHouseholds = new ArrayList<Entity>();
			pickedInhabitants = new ArrayList<Entity>();
			List<Entity> availableHouseholds;
			
			for (int iteration=0; iteration<totalRound; iteration++) {
				availableHouseholds = new ArrayList<Entity>(householdPopulation.getEntities());
				
				for (Entity householdEntity : availableHouseholds) {
					hhSizeEntityAttrValue = householdEntity.getEntityAttributeValue("size");
					hhSizeAttrValue = (UniqueValue) hhSizeEntityAttrValue.getAttributeValueOnEntity();
					hhSizeIntValue = Integer.parseInt(hhSizeAttrValue.getStringValue());
					
					buildHousehold(householdEntity, hhSizeIntValue);
				}
			}
		}
		
		// put inhabitants into household basing on household's size
		private void buildHousehold(final Entity householdEntity, final int size) {
			List<Entity> members = new ArrayList<Entity>();
			for (int i=0; i<size; i++) {
				Entity inhabitant = inhabitantPopulation.pick();
				
				if (inhabitant == null) { 
					inhabitantPopulation.putBack(members);
					return; 
				}
				
				members.add(inhabitant);
			}
			
			householdEntity.addMembers(members);
			householdPopulation.pick(householdEntity);
			
			pickedInhabitants.addAll(members);
			pickedHouseholds.add(householdEntity);
		}
		
		public List<Entity> getPickedHouseholds() {
			return pickedHouseholds;
		}
		
		public List<Entity> getPickedInhabitants() {
			return pickedInhabitants;
		}
		
	}
	
	public class Scenario2PopLinker extends AbstractPopulationsLinker {

		private ISyntheticPopulation inhabitantPopulation, householdPopulation;

		// internal data
		private List<Entity> pickedHouseholds = null;
		private List<Entity> pickedInhabitants = null;

		@Override
		public void establishRelationship(final List<ISyntheticPopulation> populations) throws GenstarException {
			// populations:
			// 		1. population 1: Scenario2's inhabitant population
			//		2. population 2: Scenario2's household population
			
			if (populations == null) { throw new IllegalArgumentException("'populations' parameter can not be null"); }
			if (populations.size() != 2) { throw new IllegalArgumentException("'populations' must contain 2 populations"); }
			
			ISyntheticPopulation tmpPop1 = populations.get(0);
			ISyntheticPopulation tmpPop2 = populations.get(1);
			
			if (tmpPop1.getName().equals("Scenario2's inhabitant population")) { 
				inhabitantPopulation = tmpPop1; 
				householdPopulation = tmpPop2;
			} else {
				inhabitantPopulation = tmpPop2; 
				householdPopulation = tmpPop1;
			}
			
			if (householdPopulation == null || inhabitantPopulation == null) {
				throw new IllegalArgumentException("'populations' doesn't contain required populations");
			}
			
			this.populations = new ArrayList<ISyntheticPopulation>(populations);
			
			// pick inhabitants into households
			int hhSizeIntValue;
			String livingPlace;
			
			pickedHouseholds = new ArrayList<Entity>();
			pickedInhabitants = new ArrayList<Entity>();
			List<Entity> availableHouseholds;
			
			for (int iteration=0; iteration<totalRound; iteration++) {
				availableHouseholds = new ArrayList<Entity>(householdPopulation.getEntities());
				
				for (Entity householdEntity : availableHouseholds) {
					hhSizeIntValue = ((UniqueValue) householdEntity.getEntityAttributeValue("size").getAttributeValueOnEntity()).getIntValue();
					livingPlace = ((UniqueValue) householdEntity.getEntityAttributeValue("living_place").getAttributeValueOnEntity()).getStringValue();
					
					buildHousehold(householdEntity, hhSizeIntValue, livingPlace);
				}
			}
		}
		
		// put inhabitants into household basing on household's size and living_place
		private void buildHousehold(final Entity householdEntity, final int size, final String livingPlace) throws GenstarException {
			Map<String, AttributeValue> livingPlaceMap = new HashMap<String, AttributeValue>();
			UniqueValue livingPlaceAttrValue = new UniqueValue(DataType.STRING, livingPlace);
			livingPlaceMap.put("living_place", livingPlaceAttrValue);

			List<Entity> members = new ArrayList<Entity>();
			for (int i=0; i<size; i++) {
				
				Entity inhabitant = inhabitantPopulation.pick(livingPlaceMap);
				if (inhabitant == null) {
					inhabitantPopulation.putBack(members);
					return;
				}
				
				members.add(inhabitant);
			}
			
			householdEntity.addMembers(members);
			householdPopulation.pick(householdEntity);
			
			pickedInhabitants.addAll(members);
			pickedHouseholds.add(householdEntity);
		}

		public List<Entity> getPickedHouseholds() {
			return pickedHouseholds;
		}
		
		public List<Entity> getPickedInhabitants() {
			return pickedInhabitants;
		}
	}
	
	
	private Scenario1PopLinker scenario1Linker;
	private Scenario2PopLinker scenario2Linker;
	
	public CanThoPopLinkers() {
		scenario1Linker = new Scenario1PopLinker();
		scenario2Linker = new Scenario2PopLinker();
	}
	
	public Scenario1PopLinker getScenario1PopLinker() {
		return scenario1Linker;
	}
	
	public Scenario2PopLinker getScenario2PopLinker() {
		return scenario2Linker;
	}
}
