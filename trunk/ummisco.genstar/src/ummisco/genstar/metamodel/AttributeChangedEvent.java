package ummisco.genstar.metamodel;

import java.util.EventObject;

public class AttributeChangedEvent extends EventObject {

	public AttributeChangedEvent(final AbstractAttribute source) {
		super(source);
	}

	@Override public AbstractAttribute getSource() {
		return (AbstractAttribute) source;
	}
}
