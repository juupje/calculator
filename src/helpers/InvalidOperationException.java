package helpers;

public class InvalidOperationException extends RuntimeException {
	private static final long serialVersionUID = -6711004550271986717L;
	
	public InvalidOperationException() {
		super();
	}
	
	public InvalidOperationException(String s) {
		super(s);
	}
}
