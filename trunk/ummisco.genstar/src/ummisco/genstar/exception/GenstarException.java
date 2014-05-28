package ummisco.genstar.exception;

public class GenstarException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public GenstarException(final String message) {
		super(message);
	}
	
	public GenstarException(final String message, final Throwable cause) {
		super(message, cause);
	}
	
	public GenstarException(final Throwable cause) {
		super(cause);
	}

}
