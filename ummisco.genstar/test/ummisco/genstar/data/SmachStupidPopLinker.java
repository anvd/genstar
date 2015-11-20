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
import ummisco.genstar.metamodel.Entity;
import ummisco.genstar.metamodel.ISyntheticPopulation;
import ummisco.genstar.metamodel.attributes.AttributeValue;
import ummisco.genstar.metamodel.attributes.DataType;
import ummisco.genstar.metamodel.attributes.EntityAttributeValue;
import ummisco.genstar.metamodel.attributes.ProbabilityMassFunction;
import ummisco.genstar.metamodel.attributes.RangeValue;
import ummisco.genstar.metamodel.attributes.UniqueValue;
import ummisco.genstar.util.SharedInstances;


public class SmachStupidPopLinker extends AbstractPopulationsLinker {

	// Required populations : "household" and "inhabitant"
	private ISyntheticPopulation householdPopulation = null, inhabitantPopulation = null;
	
	private ProbabilityMassFunction coupleAgeDifferencePmf;
	private ProbabilityMassFunction firstBirthOrderPmf, secondBirthOrderPmf, thirdBirthOrderPmf, fourthBirthOrderPmf;
	
	
	// internal data
	List<Entity> successfulHouseholds = null;
	List<Entity> successfulInhabitants = null;
	
	@Override public void establishRelationship(final List<ISyntheticPopulation> populations) throws GenstarException {
		if (populations == null) { throw new IllegalArgumentException("'populations' parameter can not be null"); }
		if (populations.size() != 2) { throw new IllegalArgumentException("'populations' must contain 2 populations"); }

		ISyntheticPopulation tmpPop1 = populations.get(0);
		ISyntheticPopulation tmpPop2 = populations.get(1);
		
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
		List<Entity> availableHouseholds;
		
		for (int iteration=0; iteration<totalRound; iteration++) {
			availableHouseholds = new ArrayList<Entity>(householdPopulation.getEntities());
			System.out.println("Start iteration " + (iteration + 1) + " with successfulHouseholds : " + successfulHouseholds.size()
					+ "; successfulInhabitants : " + successfulInhabitants.size() + "; availableHouseholds : " + availableHouseholds.size());
			
			for (Entity householdEntity : availableHouseholds) {
				hhTypeEntityAttrValue = householdEntity.getEntityAttributeValue("household_type");
				hhTypeAttrValue = (UniqueValue) hhTypeEntityAttrValue.getAttributeValueOnEntity();
				hhTypeIntValue = Integer.parseInt(hhTypeAttrValue.getStringValue());
				
				switch (hhTypeIntValue) {
					case 1: // couple
						buildFamilyCoupleHousehold(householdEntity);
						break;
					
					case 2: // mono-parental
						buildFamilyMonoParentHousehold(householdEntity);
						break;
						
					case 3: // co-locations
						buildNonFamilySeveralMembersHousehold(householdEntity);
						break;
						
					case 4: // one-person
						buildNonFamilyOneMemberHousehold(householdEntity);
						break;
				}
			}
			
			System.out.println("Finish iteration " + (iteration + 1) + " with successfulHouseholds : " + successfulHouseholds.size()
					+ "; successfulInhabitants : " + successfulInhabitants.size());

			currentRound++;
		}
		
		// put back the entities into the populations
		householdPopulation.putBack(successfulHouseholds);
		inhabitantPopulation.putBack(successfulInhabitants);
	}

	
	private void buildFamilyCoupleHousehold(final Entity householdEntity) throws GenstarException {
		
		// pick the householdHead by age(range)
		UniqueValue headAgeOnHouseholdAttrValue = (UniqueValue) householdEntity.getEntityAttributeValue("age").getAttributeValueOnEntity();
		Map<String, AttributeValue> headAgeMap1 = new HashMap<String, AttributeValue>();
		headAgeMap1.put("age", headAgeOnHouseholdAttrValue);
		Entity householdHead = inhabitantPopulation.pick(headAgeMap1);
		
		if (householdHead != null) {
			
			// pick householdHead partner
			boolean isMaleHead = false;
			int ageDifference = Integer.parseInt(( (UniqueValue) coupleAgeDifferencePmf.nextValue() ).getStringValue());
			int headPartnerAge;
			UniqueValue headSexAttrValue = (UniqueValue) householdHead.getEntityAttributeValue("sex").getAttributeValueOnEntity();
			if (headSexAttrValue.getStringValue().equals("true")) { // "male" head
				headPartnerAge = Integer.parseInt(headAgeOnHouseholdAttrValue.getStringValue()) + ageDifference;
				isMaleHead = true;
			} else { // "female" head
				headPartnerAge = Integer.parseInt(headAgeOnHouseholdAttrValue.getStringValue()) - ageDifference;
				isMaleHead = false;
			}
			
			Map<String, AttributeValue> headPartnerAgeMap = new HashMap<String, AttributeValue>();
			UniqueValue headPartnerAgeAttrValue = new UniqueValue(DataType.INTEGER, Integer.toString(headPartnerAge));
			headPartnerAgeMap.put("age", headPartnerAgeAttrValue);
			Entity headPartner = inhabitantPopulation.pick(headPartnerAgeMap);
			List<Entity> pickedChildren = Collections.EMPTY_LIST;
			
			if (headPartner != null) {
				UniqueValue householdSizeValue = (UniqueValue) householdEntity.getEntityAttributeValue("size").getAttributeValueOnEntity();
				int nbOfChildren = Integer.parseInt(householdSizeValue.getStringValue()) - 2;
				if (nbOfChildren > 0) {
					pickedChildren = this.pickChildren(isMaleHead ? headPartner : householdHead, nbOfChildren);
					if (pickedChildren.size() < nbOfChildren) { // can not pick enough children
						pickedChildren.add(householdHead);
						pickedChildren.add(headPartner);
						inhabitantPopulation.putBack(pickedChildren);
						
//						householdPopulation.putBack(householdEntity); not picked -> no need a "put back"
						return;
					} 
				} 
				
				// establish the relationship between the household and its members -> built-in variable "members"
				householdEntity.addMember(householdHead);
				householdEntity.addMember(headPartner);
				householdEntity.addMembers(pickedChildren);
				successfulInhabitants.add(householdHead);
				successfulInhabitants.add(headPartner);
				successfulInhabitants.addAll(pickedChildren);
				
				successfulHouseholds.add(householdPopulation.pick(householdEntity));
				
				return;	
			} else {
				inhabitantPopulation.putBack(householdHead); // putback the householdHead
//				householdPopulation.putBack(householdEntity); not picked -> no need a "put back"
			}
			
		} else {
			// putback the household
//			householdPopulation.putBack(householdEntity); not picked -> no need a "put back"
		}
	}
	
	private void buildFamilyMonoParentHousehold(final Entity householdEntity) throws GenstarException {
		// pick the householdHead by age(range)
		UniqueValue headAgeOnHouseholdAttrValue = (UniqueValue) householdEntity.getEntityAttributeValue("age").getAttributeValueOnEntity();
		Map<String, AttributeValue> headAgeMap1 = new HashMap<String, AttributeValue>();
		headAgeMap1.put("age", headAgeOnHouseholdAttrValue);
		Entity householdHead = inhabitantPopulation.pick(headAgeMap1);

		if (householdHead != null) {
			
			UniqueValue householdSizeValue = (UniqueValue) householdEntity.getEntityAttributeValue("size").getAttributeValueOnEntity();
			int nbOfChildren = Integer.parseInt(householdSizeValue.getStringValue()) - 1;
			
			List<Entity> pickedChildren = Collections.EMPTY_LIST; 
			if (nbOfChildren > 0) {
				pickedChildren = this.pickChildren(householdHead, nbOfChildren);
				if (pickedChildren.size() < nbOfChildren) { // can not pick enough children
					inhabitantPopulation.putBack(householdHead);
					inhabitantPopulation.putBack(pickedChildren);
//					householdPopulation.putBack(householdEntity); not picked -> no need a "put back"
					
					return;
				}
			}
			
			// establish the relationship between the household and its members
			householdEntity.addMember(householdHead);
			householdEntity.addMembers(pickedChildren);
			successfulInhabitants.add(householdHead);
			successfulInhabitants.addAll(pickedChildren);
			
			successfulHouseholds.add(householdPopulation.pick(householdEntity));
			
		} else {
//			householdPopulation.putBack(householdEntity); not picked -> no need a "put back"
		}
	}
	
	private void buildNonFamilySeveralMembersHousehold(final Entity householdEntity) {
		
		// pick the householdHead by age(range)
		UniqueValue headAgeOnHouseholdAttrValue = (UniqueValue) householdEntity.getEntityAttributeValue("age").getAttributeValueOnEntity();
		Map<String, AttributeValue> headAgeMap1 = new HashMap<String, AttributeValue>();
		headAgeMap1.put("age", headAgeOnHouseholdAttrValue);
		Entity householdHead = inhabitantPopulation.pick(headAgeMap1);

		if (householdHead != null) {
			// pick other members
			UniqueValue householdSizeValue = (UniqueValue) householdEntity.getEntityAttributeValue("size").getAttributeValueOnEntity();
			int otherMems = Integer.parseInt(householdSizeValue.getStringValue()) - 1;
			List<Entity> otherMembers = new ArrayList<Entity>();
			Entity other = null;
			for (int i=0; i<otherMems; i++) {
				other = inhabitantPopulation.pick();
				if (other != null) { otherMembers.add(other); }
				else { // no more inhabitant to pick -> put back  
					inhabitantPopulation.putBack(householdHead);
					inhabitantPopulation.putBack(otherMembers);
					
//					householdPopulation.putBack(householdEntity); not picked -> no need a "put back"
					
					return;
				}
			}
			
			// establish the relationship between the household and its members
			householdEntity.addMember(householdHead);
			householdEntity.addMembers(otherMembers);
			successfulInhabitants.add(householdHead);
			successfulInhabitants.addAll(otherMembers);
			
			successfulHouseholds.add(householdPopulation.pick(householdEntity));
		} else {
//			householdPopulation.putBack(householdEntity); not picked -> no need a "put back"
		}
	}
	
	private void buildNonFamilyOneMemberHousehold(final Entity householdEntity) {
		
		// pick the householdHead by age(range)
		UniqueValue headAgeOnHouseholdAttrValue = (UniqueValue) householdEntity.getEntityAttributeValue("age").getAttributeValueOnEntity();
		Map<String, AttributeValue> headAgeMap1 = new HashMap<String, AttributeValue>();
		headAgeMap1.put("age", headAgeOnHouseholdAttrValue);
		Entity householdHead = inhabitantPopulation.pick(headAgeMap1);

		if (householdHead != null) {
			// establish the relationship
			householdEntity.addMember(householdHead);
			successfulInhabitants.add(householdHead);
			
			successfulHouseholds.add(householdPopulation.pick(householdEntity));
		} else {
//			householdPopulation.putBack(householdEntity); not picked -> no need a "put back"
		}
	}
	
	private Entity pickChild(final Entity mother, final int previousChildAge, final ProbabilityMassFunction liveBirthOrder) throws GenstarException {
		
		SortedMap<AttributeValue, UniqueValue> liveBirthData = liveBirthOrder.getData();
		
		RangeValue headKey = null, tailKey = null;
		UniqueValue motherAgeValue = (UniqueValue) mother.getEntityAttributeValue("age").getAttributeValueOnEntity();
		
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
		Map<String, AttributeValue> childAgeAttributeValues = new HashMap<String, AttributeValue>();
		childAgeAttributeValues.put("age", childAgeValue);
		return inhabitantPopulation.pick(childAgeAttributeValues);
	}
	
	private List<Entity> pickChildren(final Entity mother, final int nbOfChildren) throws GenstarException {
		
		if (mother == null) { throw new IllegalArgumentException("'mother' parameter must not be null"); }
		if (nbOfChildren <= 0) { throw new IllegalArgumentException("'nbOfChildren' parameter must be positive"); }
		
		int tobePicked = nbOfChildren;
		
		List<Entity> pickedChildren = new ArrayList<Entity>();
		int previousChildAge = 0;
		
		// first child
		Entity aChild = null;
		if (tobePicked > 0) {
			aChild = this.pickChild(mother, 0, firstBirthOrderPmf);
			if (aChild != null) {
				pickedChildren.add(aChild);
				previousChildAge = Integer.parseInt( ( (UniqueValue) aChild.getEntityAttributeValue("age").getAttributeValueOnEntity()).getStringValue() );
			} else {
				return pickedChildren;
			}
			
			tobePicked--;
		}
		
		// second child
		if (tobePicked > 0) {
			aChild = this.pickChild(mother, previousChildAge, secondBirthOrderPmf);
			if (aChild != null) {
				pickedChildren.add(aChild);
				previousChildAge = Integer.parseInt( ( (UniqueValue) aChild.getEntityAttributeValue("age").getAttributeValueOnEntity()).getStringValue() );
			} else {
				return pickedChildren;
			}
			
			tobePicked--;
		}
		
		// third child
		if (tobePicked > 0) {
			aChild = this.pickChild(mother, previousChildAge, thirdBirthOrderPmf);
			if (aChild != null) {
				pickedChildren.add(aChild);
				previousChildAge = Integer.parseInt( ( (UniqueValue) aChild.getEntityAttributeValue("age").getAttributeValueOnEntity()).getStringValue() );
			} else {
				return pickedChildren;
			}
			
			tobePicked--;
		}
		
		
		// fourth child
		if (tobePicked > 0) {
			aChild = this.pickChild(mother, previousChildAge, fourthBirthOrderPmf);
			if (aChild != null) {
				pickedChildren.add(aChild);
				previousChildAge = Integer.parseInt( ( (UniqueValue) aChild.getEntityAttributeValue("age").getAttributeValueOnEntity()).getStringValue() );
			} else {
				return pickedChildren;
			}
			
			tobePicked--;
		}

		return pickedChildren;
	}

}
