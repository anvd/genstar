package idees.genstar.distribution.exception;

import java.util.Set;

import idees.genstar.configuration.GSDataFile;
import idees.genstar.distribution.innerstructure.AGSFullNDimensionalMatrix;

public class IllegalDistributionCreation extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public <T extends Number> IllegalDistributionCreation(String message, Set<AGSFullNDimensionalMatrix<T>> jointDistributionSet) {
		super(message);
	}

	public IllegalDistributionCreation(String message, GSDataFile file) {
		super(message);
	}

}
