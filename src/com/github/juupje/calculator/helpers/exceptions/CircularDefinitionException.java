package com.github.juupje.calculator.helpers.exceptions;

import com.github.juupje.calculator.mathobjects.MathObject;

public class CircularDefinitionException extends RuntimeException {
	private static final long serialVersionUID = 3339286700533190017L;

	public CircularDefinitionException(MathObject obj) {
		super("Found a circular definition while processing " + obj.toString());
	}
}
