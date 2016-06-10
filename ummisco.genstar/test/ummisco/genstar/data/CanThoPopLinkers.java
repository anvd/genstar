package ummisco.genstar.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractPopulationsLinker;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import msi.gama.precompiler.GamlAnnotations.populations_linker;

public class CanThoPopLinkers {

	@populations_linker(name = "pops_linker2")
	public class Scenario1PopLinker extends AbstractPopulationsLinker {
		
		private IPopulation inhabitantPopulation, householdPopulation;

		// internal data
		private List<Entity> pickedHouseholds = null;
		private List<Entity> pickedInhabitants = null;

		@Override
		public void establishRelationship(final List<IPopulation> populations) throws GenstarException {
			// populations:
			// 		1. population 1: Scenario1's inhabitant population
			//		2. population 2: Scenario1's household population
			
			if (populations == null) { throw new IllegalArgumentException("'populations' parameter can not be null"); }
			if (populations.size() != 2) { throw new IllegalArgumentException("'populations' must contain 2 populations"); }
			
			IPopulation tmpPop1 = populations.get(0);
			IPopulation tmpPop2 = populations.get(1);
			
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
			
			this.populations = new ArrayList<IPopulation>(populations);
			
			// pick inhabitants into households
			EntityAttributeValue hhSizeEntityAttrValue;
			UniqueValue hhSizeAttrValue;
			int hhSizeIntValue;
			pickedHouseholds = new ArrayList<Entity>();
			pickedInhabitants = new ArrayList<Entity>();
			
			List<Entity> availableHouseholds = householdPopulation.getEntities();
			List<Entity> availableInhabitants = inhabitantPopulation.getEntities();
			
			for (int iteration=0; iteration<totalRound; iteration++) {
				// availableHouseholds = new ArrayList<Entity>(householdPopulation.getEntities());
				
				for (Entity householdEntity : availableHouseholds) {
					hhSizeEntityAttrValue = householdEntity.getEntityAttributeValueByNameOnData("size");
					hhSizeAttrValue = (UniqueValue) hhSizeEntityAttrValue.getAttributeValueOnEntity();
					hhSizeIntValue = Integer.parseInt(hhSizeAttrValue.getStringValue());
					
					buildHousehold(householdEntity, hhSizeIntValue, availableInhabitants);
				}
				
				availableHouseholds.removeAll(pickedHouseholds);
			}
		}
		
		// put inhabitants into household basing on household's size
		private void buildHousehold(final Entity householdEntity, final int size, final List<Entity> availableInhabitants) throws GenstarException {
			List<Entity> members = new ArrayList<Entity>();
			for (int i=0; i<size; i++) {
				Entity inhabitant = null;
				if (!availableInhabitants.isEmpty()) { inhabitant = availableInhabitants.remove(0); }
				
				if (inhabitant == null) { 
					availableInhabitants.addAll(members);
					return; 
				}
				
				members.add(inhabitant);
			}
		
			if (size > 0) {
				IPopulation memberPopulation = members.get(0).getPopulation();
				IPopulation memberNewPopulation = householdEntity.getComponentPopulation(memberPopulation.getName());
				if (memberNewPopulation == null) {  memberNewPopulation = householdEntity.createComponentPopulation(memberPopulation.getName(), memberPopulation.getAttributes()); }
				
				List<Entity> membersOfNewPopulation = memberNewPopulation.createEntities(members.size());
				for (int i=0; i<members.size(); i++) {
					membersOfNewPopulation.get(i).setEntityAttributeValues(members.get(i).getEntityAttributeValues());
				}

				availableInhabitants.removeAll(members);
				pickedInhabitants.addAll(members);
				pickedHouseholds.add(householdEntity);
			}
		}
		
		public List<Entity> getPickedHouseholds() {
			return pickedHouseholds;
		}
		
		public List<Entity> getPickedInhabitants() {
			return pickedInhabitants;
		}
		
	}
	
	public class Scenario2PopLinker extends AbstractPopulationsLinker {

		private IPopulation inhabitantPopulation, householdPopulation;

		// internal data
		private List<Entity> pickedHouseholds = null;
		private List<Entity> pickedInhabitants = null;

		@Override
		public void establishRelationship(final List<IPopulation> populations) throws GenstarException {
			// populations:
			// 		1. population 1: Scenario2's inhabitant population
			//		2. population 2: Scenario2's household population
			
			if (populations == null) { throw new IllegalArgumentException("'populations' parameter can not be null"); }
			if (populations.size() != 2) { throw new IllegalArgumentException("'populations' must contain 2 populations"); }
			
			IPopulation tmpPop1 = populations.get(0);
			IPopulation tmpPop2 = populations.get(1);
			
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
			
			this.populations = new ArrayList<IPopulation>(populations);
			
			// pick inhabitants into households
			int hhSizeIntValue;
			String livingPlace;
			
			pickedHouseholds = new ArrayList<Entity>();
			pickedInhabitants = new ArrayList<Entity>();
			List<Entity> availableHouseholds = householdPopulation.getEntities();
			List<Entity> availableInhabitants = inhabitantPopulation.getEntities();
			
			for (int iteration=0; iteration<totalRound; iteration++) {
				
				for (Entity householdEntity : availableHouseholds) {
					hhSizeIntValue = ((UniqueValue) householdEntity.getEntityAttributeValueByNameOnData("size").getAttributeValueOnEntity()).getIntValue();
					livingPlace = ((UniqueValue) householdEntity.getEntityAttributeValueByNameOnData("living_place").getAttributeValueOnEntity()).getStringValue();
					
					buildHousehold(householdEntity, hhSizeIntValue, livingPlace, availableInhabitants);
				}
				
				availableHouseholds.removeAll(pickedHouseholds);
			}
		}
		
		// put inhabitants into household basing on household's size and living_place
		private void buildHousehold(final Entity householdEntity, final int size, final String livingPlace, final List<Entity> availableInhabitants) throws GenstarException {
			if (size == 0) { return; }
			if (availableInhabitants.isEmpty()) { return; }
			
			Map<AbstractAttribute, AttributeValue> livingPlaceMap = new HashMap<AbstractAttribute, AttributeValue>();
			AbstractAttribute livingPlaceAttr = availableInhabitants.get(0).getPopulation().getAttributeByNameOnEntity("living_place");
			UniqueValue livingPlaceAttrValue = new UniqueValue(DataType.STRING, livingPlace, livingPlaceAttr);
			livingPlaceMap.put(livingPlaceAttr, livingPlaceAttrValue);

			List<Entity> members = new ArrayList<Entity>();
			
			for (Entity inhabitant : availableInhabitants) {
				if (inhabitant.matchAttributeValuesOnEntity(livingPlaceMap)) { members.add(inhabitant); }
				if (members.size() == size) { break; }
			}
			
			if (members.size() == size) {
				
				IPopulation memberPopulation = members.get(0).getPopulation();
				IPopulation memberNewPopulation = householdEntity.getComponentPopulation(memberPopulation.getName());
				if (memberNewPopulation == null) {  memberNewPopulation = householdEntity.createComponentPopulation(memberPopulation.getName(), memberPopulation.getAttributes()); }
				
				List<Entity> membersOfNewPopulation = memberNewPopulation.createEntities(members.size());
				for (int i=0; i<members.size(); i++) {
					membersOfNewPopulation.get(i).setEntityAttributeValues(members.get(i).getEntityAttributeValues());
				}
				
				availableInhabitants.removeAll(members);
				pickedInhabitants.addAll(members);
				pickedHouseholds.add(householdEntity);
			}
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
