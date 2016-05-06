package ummisco.genstar.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import ummisco.genstar.exception.GenstarException;
import ummisco.genstar.metamodel.AbstractPopulationsLinker;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.attributes.ProbabilityMassFunction;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.metamodel.population.Entity;
import ummisco.genstar.metamodel.population.IPopulation;
import ummisco.genstar.util.SharedInstances;


public class SmachStupidPopLinker extends AbstractPopulationsLinker {

	// Required populations : "household" and "inhabitant"
	private IPopulation householdPopulation = null, inhabitantPopulation = null;
	
	private ProbabilityMassFunction coupleAgeDifferencePmf;
	private ProbabilityMassFunction firstBirthOrderPmf, secondBirthOrderPmf, thirdBirthOrderPmf, fourthBirthOrderPmf;
	
	
	// internal data
	List<Entity> successfulHouseholds = null;
	List<Entity> successfulInhabitants = null;
	
	@Override public void establishRelationship(final List<IPopulation> populations) throws GenstarException {
		if (populations == null) { throw new IllegalArgumentException("'populations' parameter can not be null"); }
		if (populations.size() != 2) { throw new IllegalArgumentException("'populations' must contain 2 populations"); }

		IPopulation tmpPop1 = populations.get(0);
		IPopulation tmpPop2 = populations.get(1);
		
		if (tmpPop1.getName().equals("Population of Bondy's Households")) { 
			householdPopulation = tmpPop1; 
			inhabitantPopulation = tmpPop2;
		} else {
			householdPopulation = tmpPop2; 
			inhabitantPopulation = tmpPop1;
		}
		
		if (householdPopulation == null || inhabitantPopulation == null) {
			throw new IllegalArgumentException("'populations' doesn't contain required populations");
		}
		
		this.populations = populations;
		
		coupleAgeDifferencePmf = new ProbabilityMassFunction(NationalLevelDistribution.coupleAgeDifferencesAttributeValuesMap); 
		
		firstBirthOrderPmf = new ProbabilityMassFunction(NationalLevelDistribution.firstBirthOrder);
		secondBirthOrderPmf = new ProbabilityMassFunction(NationalLevelDistribution.secondBirthOrder);
		thirdBirthOrderPmf = new ProbabilityMassFunction(NationalLevelDistribution.thirdBirthOrder);
		fourthBirthOrderPmf = new ProbabilityMassFunction(NationalLevelDistribution.fourthBirthOrder);

		EntityAttributeValue hhTypeEntityAttrValue;
		UniqueValue hhTypeAttrValue;
		int hhTypeIntValue;
		successfulHouseholds = new ArrayList<Entity>();
		successfulInhabitants = new ArrayList<Entity>();
		List<Entity> availableHouseholds = householdPopulation.getEntities();
		List<Entity> availableInhabitants = inhabitantPopulation.getEntities();
		
		for (int iteration=0; iteration<totalRound; iteration++) {
//			availableHouseholds = new ArrayList<Entity>(householdPopulation.getEntities());
			System.out.println("Start iteration " + (iteration + 1) + " with successfulHouseholds : " + successfulHouseholds.size()
					+ "; successfulInhabitants : " + successfulInhabitants.size() + "; availableHouseholds : " + availableHouseholds.size());
			
			for (Entity householdEntity : availableHouseholds) {
				hhTypeEntityAttrValue = householdEntity.getEntityAttributeValueByNameOnData("household_type");
				hhTypeAttrValue = (UniqueValue) hhTypeEntityAttrValue.getAttributeValueOnEntity();
				hhTypeIntValue = Integer.parseInt(hhTypeAttrValue.getStringValue());
				
				switch (hhTypeIntValue) {
					case 1: // couple
						buildFamilyCoupleHousehold(householdEntity, availableInhabitants);
						break;
					
					case 2: // mono-parental
						buildFamilyMonoParentHousehold(householdEntity, availableInhabitants);
						break;
						
					case 3: // co-locations
						buildNonFamilySeveralMembersHousehold(householdEntity, availableInhabitants);
						break;
						
					case 4: // one-person
						buildNonFamilyOneMemberHousehold(householdEntity, availableInhabitants);
						break;
				}
			}
			
			System.out.println("Finish iteration " + (iteration + 1) + " with successfulHouseholds : " + successfulHouseholds.size()
					+ "; successfulInhabitants : " + successfulInhabitants.size());

			currentRound++;
		}
		
	}

	
	private void buildFamilyCoupleHousehold(final Entity householdEntity, final List<Entity> availableInhabitants) throws GenstarException {
		if (availableInhabitants.isEmpty()) { return; }
		
		List<Entity> pickedInhabitants = new ArrayList<Entity>();
		
		// pick the householdHead by age(range)
		UniqueValue headAgeOnHouseholdAttrValue = (UniqueValue) householdEntity.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity();
		Map<AbstractAttribute, AttributeValue> headAgeMap1 = new HashMap<AbstractAttribute, AttributeValue>();
		AbstractAttribute ageAttribute = availableInhabitants.get(0).getPopulation().getAttributeByNameOnEntity("age");
		headAgeMap1.put(ageAttribute, headAgeOnHouseholdAttrValue);
		Entity householdHead = null;
		for (Entity inhabitant : availableInhabitants) {
			if (inhabitant.matchAttributeValuesOnEntity(headAgeMap1)) {
				availableInhabitants.remove(inhabitant);
				householdHead = inhabitant;
				pickedInhabitants.add(inhabitant);
				break;
			}
		}
		
		if (householdHead != null) {
			
			// pick householdHead partner
			boolean isMaleHead = false;
			int ageDifference = Integer.parseInt(( (UniqueValue) coupleAgeDifferencePmf.nextValue() ).getStringValue());
			int headPartnerAge;
			UniqueValue headSexAttrValue = (UniqueValue) householdHead.getEntityAttributeValueByNameOnData("sex").getAttributeValueOnEntity();
			if (headSexAttrValue.getStringValue().equals("true")) { // "male" head
				headPartnerAge = Integer.parseInt(headAgeOnHouseholdAttrValue.getStringValue()) + ageDifference;
				isMaleHead = true;
			} else { // "female" head
				headPartnerAge = Integer.parseInt(headAgeOnHouseholdAttrValue.getStringValue()) - ageDifference;
				isMaleHead = false;
			}
			
			Map<AbstractAttribute, AttributeValue> headPartnerAgeMap = new HashMap<AbstractAttribute, AttributeValue>();
			UniqueValue headPartnerAgeAttrValue = new UniqueValue(DataType.INTEGER, Integer.toString(headPartnerAge));
			headPartnerAgeMap.put(ageAttribute, headPartnerAgeAttrValue);
			
			Entity headPartner = null;
			for (Entity inhabitant : availableInhabitants) {
				if (inhabitant.matchAttributeValuesOnEntity(headPartnerAgeMap)) {
					headPartner = inhabitant;
					availableInhabitants.remove(headPartner);
					pickedInhabitants.add(headPartner);
					break;
				}
			}
			
			
			List<Entity> pickedChildren = Collections.EMPTY_LIST;
			
			if (headPartner != null) {
				UniqueValue householdSizeValue = (UniqueValue) householdEntity.getEntityAttributeValueByNameOnData("size").getAttributeValueOnEntity();
				int nbOfChildren = Integer.parseInt(householdSizeValue.getStringValue()) - 2;
				if (nbOfChildren > 0) {
					pickedChildren = this.pickChildren(isMaleHead ? headPartner : householdHead, nbOfChildren, availableInhabitants);
					if (pickedChildren.size() < nbOfChildren) { // can not pick enough children
						pickedChildren.add(householdHead);
						pickedChildren.add(headPartner);
						
						availableInhabitants.addAll(pickedChildren);
						availableInhabitants.addAll(pickedInhabitants);
						return;
					} 
				} 
				
				// establish the relationship between the household and its members -> built-in variable "members"
				
				IPopulation componentPopulation = householdEntity.getComponentPopulation(householdHead.getPopulation().getName());
				if (componentPopulation == null) {
					List<Entity> householdMembers = componentPopulation.createEntities(pickedChildren.size() + 2);
					
					householdMembers.get(0).setEntityAttributeValues(householdHead.getEntityAttributeValues());
					householdMembers.get(1).setEntityAttributeValues(headPartner.getEntityAttributeValues());
					
					for (int i=2; i< householdMembers.size(); i++) {
						householdMembers.get(i).setEntityAttributeValues(pickedChildren.get(i-2).getEntityAttributeValues());
					}
				}
				
				successfulInhabitants.add(householdHead);
				successfulInhabitants.add(headPartner);
				successfulInhabitants.addAll(pickedChildren);
				
				return;	
			} else {
				availableInhabitants.addAll(pickedInhabitants);
			}
			
		} else {
			// putback the household
			availableInhabitants.addAll(pickedInhabitants);
//			householdPopulation.putBack(householdEntity); not picked -> no need a "put back"
		}
	}
	
	private void buildFamilyMonoParentHousehold(final Entity householdEntity, final List<Entity> availableInhabitants) throws GenstarException {
		if (availableInhabitants.isEmpty()) { return; }
		AbstractAttribute ageAttribute = availableInhabitants.get(0).getPopulation().getAttributeByNameOnEntity("age");
		
		// pick the householdHead by age(range)
		UniqueValue headAgeOnHouseholdAttrValue = (UniqueValue) householdEntity.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity();
		Map<AbstractAttribute, AttributeValue> headAgeMap1 = new HashMap<AbstractAttribute, AttributeValue>();
		headAgeMap1.put(ageAttribute, headAgeOnHouseholdAttrValue);
		
		Entity householdHead = null;
		for (Entity inhabitant : availableInhabitants) {
			if (inhabitant.matchAttributeValuesOnEntity(headAgeMap1)) {
				householdHead = inhabitant;
				availableInhabitants.remove(inhabitant);
				break;
			}
		}

		if (householdHead != null) {
			
			UniqueValue householdSizeValue = (UniqueValue) householdEntity.getEntityAttributeValueByNameOnData("size").getAttributeValueOnEntity();
			int nbOfChildren = Integer.parseInt(householdSizeValue.getStringValue()) - 1;
			
			List<Entity> pickedChildren = Collections.EMPTY_LIST; 
			if (nbOfChildren > 0) {
				pickedChildren = this.pickChildren(householdHead, nbOfChildren, availableInhabitants);
				if (pickedChildren.size() < nbOfChildren) { // can not pick enough children
					availableInhabitants.add(householdHead);
					availableInhabitants.addAll(pickedChildren);
//					householdPopulation.putBack(householdEntity); not picked -> no need a "put back"
					
					return;
				}
			}
			
			// establish the relationship between the household and its members
			IPopulation memberPopulation = householdEntity.getComponentPopulation(householdHead.getPopulation().getName());
			if (memberPopulation == null) {
				memberPopulation = householdEntity.createComponentPopulation(householdHead.getPopulation().getName(), householdHead.getPopulation().getAttributes());
			}
			
			List<Entity> householdMembers = memberPopulation.createEntities(1 + pickedChildren.size());
			householdMembers.get(0).setEntityAttributeValues(householdHead.getEntityAttributeValues());
			for (int i=1; i<householdMembers.size(); i++) {
				householdMembers.get(i).setEntityAttributeValues(pickedChildren.get(i-1).getEntityAttributeValues());
			}
			
			successfulInhabitants.add(householdHead);
			successfulInhabitants.addAll(pickedChildren);
			
			successfulHouseholds.add(householdEntity);
			
		} else {
			availableInhabitants.add(householdHead);
//			householdPopulation.putBack(householdEntity); not picked -> no need a "put back"
		}
	}
	
	private void buildNonFamilySeveralMembersHousehold(final Entity householdEntity, final List<Entity> availableInhabitants) throws GenstarException {
		if (availableInhabitants.isEmpty()) { return; }
		AbstractAttribute ageAttribute = availableInhabitants.get(0).getPopulation().getAttributeByNameOnEntity("age");
		
		// pick the householdHead by age(range)
		UniqueValue headAgeOnHouseholdAttrValue = (UniqueValue) householdEntity.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity();
		Map<AbstractAttribute, AttributeValue> headAgeMap1 = new HashMap<AbstractAttribute, AttributeValue>();
		headAgeMap1.put(ageAttribute, headAgeOnHouseholdAttrValue);
		
		Entity householdHead = null;
		for (Entity inhabitant : availableInhabitants) {
			if (inhabitant.matchAttributeValuesOnEntity(headAgeMap1)) {
				householdHead = inhabitant;
				availableInhabitants.remove(inhabitant);
				break;
			}
		}

		if (householdHead != null) {
			// pick other members
			UniqueValue householdSizeValue = (UniqueValue) householdEntity.getEntityAttributeValueByNameOnData("size").getAttributeValueOnEntity();
			int otherMems = Integer.parseInt(householdSizeValue.getStringValue()) - 1;
			List<Entity> otherMembers = new ArrayList<Entity>();
			Entity other = null;
			for (int i=0; i<otherMems; i++) {
				if (!availableInhabitants.isEmpty()) {
					other = availableInhabitants.get(0);
					availableInhabitants.remove(other);
				}
				
				if (other != null) { otherMembers.add(other); }
				else { // no more inhabitant to pick -> put back  
					availableInhabitants.add(householdHead);
					availableInhabitants.addAll(otherMembers);
					
					return;
				}
			}
			
			// establish the relationship between the household and its members
			IPopulation memberPopulation = householdEntity.getComponentPopulation(householdHead.getPopulation().getName());
			if (memberPopulation == null) {
				memberPopulation = householdEntity.createComponentPopulation(householdHead.getPopulation().getName(), householdHead.getPopulation().getAttributes());
			}
			List<Entity> householdMembers = memberPopulation.createEntities(1 + otherMembers.size());
			householdMembers.get(0).setEntityAttributeValues(householdHead.getEntityAttributeValues());
			for (int i=1; i<householdMembers.size(); i++) {
				householdMembers.get(i).setEntityAttributeValues(otherMembers.get(i-1).getEntityAttributeValues());
			}
			
			
			successfulInhabitants.add(householdHead);
			successfulInhabitants.addAll(otherMembers);
			successfulHouseholds.add(householdEntity);
		} else {
			availableInhabitants.add(householdEntity);
		}
	}
	
	private void buildNonFamilyOneMemberHousehold(final Entity householdEntity, final List<Entity> availableInhabitants) throws GenstarException {
		if (availableInhabitants.isEmpty()) { return; }
		AbstractAttribute ageAttribute = availableInhabitants.get(0).getPopulation().getAttributeByNameOnEntity("age");
		
		// pick the householdHead by age(range)
		UniqueValue headAgeOnHouseholdAttrValue = (UniqueValue) householdEntity.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity();
		Map<AbstractAttribute, AttributeValue> headAgeMap1 = new HashMap<AbstractAttribute, AttributeValue>();
		headAgeMap1.put(ageAttribute, headAgeOnHouseholdAttrValue);

		Entity householdHead = null;
		for (Entity inhabitant : availableInhabitants) {
			if (inhabitant.matchAttributeValuesOnEntity(headAgeMap1)) {
				householdHead = inhabitant;
				availableInhabitants.remove(inhabitant);
				break;
			}
		}
		
		if (householdHead != null) {
			// establish the relationship
			IPopulation memberPopulation = householdEntity.getComponentPopulation(householdHead.getPopulation().getName());
			if (memberPopulation == null) {
				memberPopulation = householdEntity.createComponentPopulation(householdHead.getPopulation().getName(), householdHead.getPopulation().getAttributes());
			}

			List<Entity> householdMembers = memberPopulation.createEntities(1);
			householdMembers.get(0).setEntityAttributeValues(householdHead.getEntityAttributeValues());

			successfulInhabitants.add(householdHead);
			successfulHouseholds.add(householdEntity);
		} else {
//			householdPopulation.putBack(householdEntity); not picked -> no need a "put back"
		}
	}
	
	private Entity pickChild(final Entity mother, final int previousChildAge, final ProbabilityMassFunction liveBirthOrder, final List<Entity> availableInhabitants) throws GenstarException {
		if (availableInhabitants.isEmpty()) { return null; }
		AbstractAttribute ageAttribute = availableInhabitants.get(0).getPopulation().getAttributeByNameOnEntity("age");
		
		SortedMap<AttributeValue, UniqueValue> liveBirthData = liveBirthOrder.getData();
		
		RangeValue headKey = null, tailKey = null;
		UniqueValue motherAgeValue = (UniqueValue) mother.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity();
		
		// pick the first child
		if (previousChildAge == 0) {
			headKey = (RangeValue) liveBirthData.firstKey();
			if ( headKey.isSuperior(new UniqueValue(motherAgeValue))) { return null; }
		}
		
		for (AttributeValue ageRange : liveBirthData.keySet()) {
			if (previousChildAge > 0 && ( (RangeValue) ageRange).cover(new UniqueValue(DataType.INTEGER, 
					Integer.toString((Integer.parseInt(motherAgeValue.getStringValue()) - previousChildAge) ) ) ) ) {
				headKey = (RangeValue) ageRange;
			}
			
			if ( ( (RangeValue) ageRange).cover(motherAgeValue) ) { tailKey = (RangeValue) ageRange; }
		}
		
		// mother's age is bigger than "the oldest" birth age range
		if (tailKey == null) { tailKey = (RangeValue) liveBirthData.lastKey(); }
		
		
		// rebuild a liveBirthOrder according to the mother's age
		// FIXME fix the following line: might be a performance issue -> don't create object
		SortedMap<AttributeValue, UniqueValue> rebuiltLiveBirthOrderData = new TreeMap<AttributeValue, UniqueValue>(liveBirthData.subMap(headKey, tailKey));
		rebuiltLiveBirthOrderData.put(tailKey, liveBirthData.get(tailKey));
		ProbabilityMassFunction rebuiltLiveBirthOrder = new ProbabilityMassFunction(rebuiltLiveBirthOrderData);
		

		// pick the child's age
		int motherAge = Integer.parseInt(motherAgeValue.getStringValue());
		int previousGiveBirthAge = (previousChildAge > 0) ? (motherAge - previousChildAge) : 0;
		
		RangeValue giveBirthAgeRange = (RangeValue) rebuiltLiveBirthOrder.nextValue();
		int minGiveBirthAgeRange = Integer.parseInt(giveBirthAgeRange.getMinStringValue());
		int maxGiveBirthAgeRange = Integer.parseInt(giveBirthAgeRange.getMaxStringValue());

		int currentGiveBirthAge = ( (minGiveBirthAgeRange >= previousGiveBirthAge) ? minGiveBirthAgeRange : previousGiveBirthAge ) 
				+ SharedInstances.RandomNumberGenerator.nextInt( ( (maxGiveBirthAgeRange <= motherAge) ? maxGiveBirthAgeRange : motherAge ) -
						( ( (minGiveBirthAgeRange >= previousGiveBirthAge) ? minGiveBirthAgeRange : previousGiveBirthAge ) ) + 1 );
		int childAge = motherAge - currentGiveBirthAge;
		
		
		// pick a child according to the age from the InhabitantPopulation
		UniqueValue childAgeValue = new UniqueValue(DataType.INTEGER, Integer.toString(childAge));
		Map<AbstractAttribute, AttributeValue> childAgeAttributeValues = new HashMap<AbstractAttribute, AttributeValue>();
		childAgeAttributeValues.put(ageAttribute, childAgeValue);
		
		for (Entity inhabitant : availableInhabitants) {
			if (inhabitant.matchAttributeValuesOnEntity(childAgeAttributeValues)) {
				availableInhabitants.remove(inhabitant);
				return inhabitant;
			}
		}
		return null;
	}
	
	private List<Entity> pickChildren(final Entity mother, final int nbOfChildren, final List<Entity> availableInhabitants) throws GenstarException {
		
		if (mother == null) { throw new IllegalArgumentException("'mother' parameter must not be null"); }
		if (nbOfChildren <= 0) { throw new IllegalArgumentException("'nbOfChildren' parameter must be positive"); }
		
		int tobePicked = nbOfChildren;
		
		List<Entity> pickedChildren = new ArrayList<Entity>();
		int previousChildAge = 0;
		
		// first child
		Entity aChild = null;
		if (tobePicked > 0) {
			aChild = this.pickChild(mother, 0, firstBirthOrderPmf, availableInhabitants);
			if (aChild != null) {
				pickedChildren.add(aChild);
				previousChildAge = Integer.parseInt( ( (UniqueValue) aChild.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity()).getStringValue() );
			} else {
				return pickedChildren;
			}
			
			tobePicked--;
		}
		
		// second child
		if (tobePicked > 0) {
			aChild = this.pickChild(mother, previousChildAge, secondBirthOrderPmf, availableInhabitants);
			if (aChild != null) {
				pickedChildren.add(aChild);
				previousChildAge = Integer.parseInt( ( (UniqueValue) aChild.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity()).getStringValue() );
			} else {
				return pickedChildren;
			}
			
			tobePicked--;
		}
		
		// third child
		if (tobePicked > 0) {
			aChild = this.pickChild(mother, previousChildAge, thirdBirthOrderPmf, availableInhabitants);
			if (aChild != null) {
				pickedChildren.add(aChild);
				previousChildAge = Integer.parseInt( ( (UniqueValue) aChild.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity()).getStringValue() );
			} else {
				return pickedChildren;
			}
			
			tobePicked--;
		}
		
		
		// fourth child
		if (tobePicked > 0) {
			aChild = this.pickChild(mother, previousChildAge, fourthBirthOrderPmf, availableInhabitants);
			if (aChild != null) {
				pickedChildren.add(aChild);
				previousChildAge = Integer.parseInt( ( (UniqueValue) aChild.getEntityAttributeValueByNameOnData("age").getAttributeValueOnEntity()).getStringValue() );
			} else {
				return pickedChildren;
			}
			
			tobePicked--;
		}

		return pickedChildren;
	}

}
