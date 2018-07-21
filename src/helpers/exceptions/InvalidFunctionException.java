package helpers.exceptions;

public class InvalidFunctionException extends Exception {
	private static final long serialVersionUID = -7809633612614368685L;

	public InvalidFunctionException(String func) {
		super("Unknown function: " + func);
	}
}
