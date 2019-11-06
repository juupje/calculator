package com.github.juupje.calculator.helpers.exceptions;

public class InvalidFunctionException extends RuntimeException {
	private static final long serialVersionUID = -7809633612614368685L;

	public InvalidFunctionException(String func) {
		super("Unknown function: " + func);
	}
}
