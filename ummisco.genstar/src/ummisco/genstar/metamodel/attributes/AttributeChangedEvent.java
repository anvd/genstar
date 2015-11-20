package ummisco.genstar.metamodel.attributes;

import java.util.EventObject;

public class AttributeChangedEvent extends EventObject {

	public AttributeChangedEvent(final AbstractAttribute source) {
		super(source);
	}

	@Override public AbstractAttribute getSource() {
		return (AbstractAttribute) source;
	}
}
