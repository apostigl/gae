package it.revevol.simulation.exception;

/**
 * The exception thrown when a registration operation fails.
 * @author Angelo
 *
 */

public class RegisterException extends Exception {

	private static final long serialVersionUID = -1536794992208081047L;

	public RegisterException(String message){
		super(message);
	}
}
