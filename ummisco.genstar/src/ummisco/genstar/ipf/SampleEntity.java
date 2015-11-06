package ummisco.genstar.ipf;

import java.util.HashMap;
import java.util.Map;

import ummisco.genstar.metamodel.AbstractAttribute;
import ummisco.genstar.metamodel.AttributeValue;

public class SampleEntity {

	private Map<AbstractAttribute, AttributeValue> attributes; // attribute name on data : attribute value; "value on data" or "value on entity"?

	
	public SampleEntity(final Map<AbstractAttribute, AttributeValue> attributes) {
		if (attributes == null) { throw new IllegalArgumentException("'attributes' parameter can not be null"); }
		
		this.attributes = attributes;
	}
	
	public boolean isMatch(final Map<AbstractAttribute, AttributeValue> criteria) {
		if (criteria == null || criteria.isEmpty()) { return true; }
		
		AttributeValue criterionValue, sampleEntityValue;
		
		for (AbstractAttribute criterionAttr : criteria.keySet()) {
			sampleEntityValue = attributes.get(criterionAttr);
			if (sampleEntityValue != null) {
				criterionValue = criteria.get(criterionAttr);
				if (!criterionValue.isValueMatch(sampleEntityValue)) { return false; }
			}
		}
		
		return true;
	}
	
	
	public Map<AbstractAttribute, AttributeValue> getAttributeValues() {
		Map<AbstractAttribute, AttributeValue> copy = new HashMap<AbstractAttribute, AttributeValue>();
		copy.putAll(attributes);
		
		return copy;
	}
}
