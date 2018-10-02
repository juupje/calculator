package helpers.exceptions;

public class UnexpectedCharacterException extends Exception {
	private static final long serialVersionUID = 7536284287269689946L;

	public UnexpectedCharacterException(String msg) {
		super(msg);
	}
	
	public UnexpectedCharacterException(String expr, int pos) {
		super(composeMessage(expr, pos));
	}
	private static String composeMessage(String expr, int pos) {
		String msg = "Encountered an unexpected character:\n" + expr+"\n";
		for(int i = 0; i < pos; i++)
			msg += " ";
		msg += "^";
		return msg;
	}
}
