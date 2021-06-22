package com.github.juupje.calculator.helpers.exceptions;

public class UnexpectedCharacterException extends RuntimeException {
	private static final long serialVersionUID = 7536284287269689946L;

	public UnexpectedCharacterException(String msg) {
		super(msg);
	}
	
	public UnexpectedCharacterException(String expr, int pos) {
		super(composeMessage(expr, pos, pos+1));
	}
	
	public UnexpectedCharacterException(String expr, int begin, int end) {
		super(composeMessage(expr, begin, end));
	}
	private static String composeMessage(String expr, int begin, int end) {
		String msg = "Encountered an unexpected character" + (end-begin<=1 ? "" : " sequence") + ":\n" + expr+"\n";
		for(int i = 0; i < begin; i++)
			msg += " ";
		for(int i = begin; i < end; i++)
			msg += "^";
		return msg;
	}
}
