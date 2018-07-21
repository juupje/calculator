package helpers.exceptions;

public class UnexpectedCharacterException extends Exception {
	private static final long serialVersionUID = 7536284287269689946L;

	public UnexpectedCharacterException(String msg) {
		super(msg);
	}
	public static UnexpectedCharacterException create(String expr, int pos) {
		String msg = "Encountered an unexpected character:\n" + expr+"\n";
		for(int i = 0; i < pos; i++)
			msg += " ";
		msg += "^";
		return new UnexpectedCharacterException(msg);
	}
}
