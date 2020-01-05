package com.github.juupje.calculator.helpers.exceptions;

public class IndexException extends RuntimeException {

	private static final long serialVersionUID = -429956861941929659L;

	public IndexException(String s) {
		super(s);
	}
	
	public IndexException(String index, String min, String max) {
		super("Index " + index + " out of bounds: min=" + min + ", max="+ max);
	}
}
