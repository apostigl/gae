package it.revevol.simulation.exception;

/**
 * The exception thrown when a login operation fails.
 * @author Angelo
 *
 */
public class LoginInvalidException extends Exception {

	private static final long serialVersionUID = -1536794992208081047L;

	public LoginInvalidException(String message){
		super(message);
	}
}
