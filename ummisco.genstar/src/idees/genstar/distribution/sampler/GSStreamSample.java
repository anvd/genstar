package idees.genstar.distribution.sampler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import idees.genstar.distribution.innerstructure.ACoordinate;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

/**
 * Sample method to draw from a discrete distribution, based on binary search algorithm
 * <p>
 * Default random engine is {@link ThreadLocalRandom} current generator 
 * 
 * TODO: 
 * <ul> 
 *  <li> method reset sampler
 *  <li> random engine abstraction should extends another one than java {@link Random} 
 * </ul>
 * 
 * @author kevinchapuis
 *
 * @param <T>
 */
public class GSStreamSample implements IGSSampler<ACoordinate<AbstractAttribute, AttributeValue>> {
	
	private final List<ACoordinate<AbstractAttribute, AttributeValue>> indexedKey;
	private final List<Double> indexedProbabilitySum;
	
	private final Random random;
	
	public GSStreamSample(Map<ACoordinate<AbstractAttribute, AttributeValue>, Double> distribution){
		this(ThreadLocalRandom.current(), distribution);
	}
	
	public GSStreamSample(Random random, Map<ACoordinate<AbstractAttribute, AttributeValue>, Double> distribution){
		this.random = random;
		this.indexedKey = new ArrayList<>(distribution.size());
		this.indexedProbabilitySum = new ArrayList<>(distribution.size());
		double sumOfProbabilities = 0d;
		for(Entry<ACoordinate<AbstractAttribute, AttributeValue>, Double> entry : distribution.entrySet()){
			indexedKey.add(entry.getKey());
			sumOfProbabilities += entry.getValue();
			indexedProbabilitySum.add(sumOfProbabilities);
		}
	}
	
	@Override
	public ACoordinate<AbstractAttribute,AttributeValue> draw(){
		double rand = random.nextDouble();
		int floor = 0;
		int top = indexedKey.size() - 1;
		int searchIndex = indexedKey.size() % 2 == 0 ? indexedKey.size() / 2 : (indexedKey.size() - 1) / 2;
		while(floor < top){
			if(indexedProbabilitySum.get(searchIndex) < rand)
				floor = searchIndex + 1;
			else if(indexedProbabilitySum.get(searchIndex) > rand)
				top = searchIndex - 1;
			else if(indexedProbabilitySum.get(searchIndex) == rand)
				return indexedKey.get(searchIndex);
			searchIndex = (floor + top) / 2;
		}
		return indexedKey.get(searchIndex);
	}
	
}
