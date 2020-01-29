package com.github.juupje.calculator.helpers.exceptions;

public class SyntaxException extends RuntimeException {

	private static final long serialVersionUID = 6923133639234006795L;

	public SyntaxException() {
		super();
	}
	
	public SyntaxException(String s) {
		super(s);
	}
}
