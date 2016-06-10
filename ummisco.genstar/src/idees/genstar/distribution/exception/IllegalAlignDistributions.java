package idees.genstar.distribution.exception;

import java.util.Set;

import idees.genstar.configuration.GSMetaDataType;
import ummisco.genstar.metamodel.attributes.AbstractAttribute;

public class IllegalAlignDistributions extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public IllegalAlignDistributions(Set<AbstractAttribute> dimensions, Set<AbstractAttribute> dimensions2) {
		super("Distributions are not alignable !");
	}

	public IllegalAlignDistributions(String message, GSMetaDataType metaDataType) {
		super(message);
	}

}
