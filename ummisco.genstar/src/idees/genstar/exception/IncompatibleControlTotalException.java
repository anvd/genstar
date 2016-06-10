package idees.genstar.exception;

import idees.genstar.control.AControl;

public class IncompatibleControlTotalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	AControl<? extends Number> control1, control2;
	
	public IncompatibleControlTotalException(AControl<? extends Number> control1,
			AControl<? extends Number> control2) {
		super("Attribut's control totals are not consistant: v1 = "+control1.getValue()+" | v2 = "+control2.getValue());
		this.control1 = control1;
		this.control2 = control2;
	}

}
