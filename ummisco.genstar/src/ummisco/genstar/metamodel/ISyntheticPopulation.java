package ummisco.genstar.metamodel;

import java.util.List;
import java.util.Map;

public interface ISyntheticPopulation {
	
	public abstract String getName();
	
	public abstract int getInitialNbOfEntities();
	
	public abstract List<Entity> getEntities();

	public abstract Entity pick(final Map<String, AttributeValue> matchingAttributeValues); // <attribute name on entity, attribute value on entity>
	
	public abstract Entity pick();
	
	public abstract Entity pick(final Entity entity);
	
	public abstract void putBack(final Entity entity);
	
	public abstract void putBack(final List<Entity> entities);
}
