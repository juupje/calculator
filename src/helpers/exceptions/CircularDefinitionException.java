package helpers.exceptions;

import mathobjects.MathObject;

public class CircularDefinitionException extends Exception {
	private static final long serialVersionUID = 3339286700533190017L;

	public CircularDefinitionException(MathObject obj) {
		super("Found a circular definition while processing " + obj.toString());
	}
}
