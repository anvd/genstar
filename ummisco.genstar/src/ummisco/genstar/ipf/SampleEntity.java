package ummisco.genstar.ipf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ummisco.genstar.metamodel.attributes.AbstractAttribute;
import ummisco.genstar.metamodel.attributes.AttributeValue;

public class SampleEntity {

	private Map<AbstractAttribute, AttributeValue> attributes; // attribute name on data : attribute value; "value on data" or "value on entity"?
	
	private List<SampleEntity> members = Collections.EMPTY_LIST;

	
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
	
	public AttributeValue getAttributeValue(final AbstractAttribute attribute) {
		return attributes.get(attribute);
	}
	
	public List<SampleEntity> getMembers() {
		return members;
	}
	
	public void addMember(final SampleEntity member) {
		if (member == null) { throw new IllegalArgumentException("'member' parameter can not be null"); }
		if (members == Collections.EMPTY_LIST) { members = new ArrayList<SampleEntity>(5); }
		if (!members.contains(member)) { members.add(member); }
	}
	
	public void addMembers(final List<SampleEntity> members) {
		if (members == null) { throw new IllegalArgumentException("'members' parameter can not be null"); }
		if (this.members == Collections.EMPTY_LIST) { this.members = new ArrayList<SampleEntity>(members.size()); }
		for (SampleEntity m : members) { this.addMember(m); }
	}
}
