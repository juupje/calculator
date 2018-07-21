package main;

import helpers.exceptions.UndefinedException;
import mathobjects.MathObject;

/**
 * Placeholder class for variables.
 * This class only contains a <tt>String</tt> containing the variable name.
 * When the value of this variable is requested, it will return the <tt>MathObject</tt> saved under that name in the {@link Variables} class
 * @author Joep Geuskens
 */
public class Variable {
	String name = "";
	
	/**
	 * Simple constructor to set the name of the variable.
	 * @param name
	 */
	public Variable(String name) {
		this.name = name;
	}
	
	public Variable(String name, MathObject mo) {
		Variables.set(name, mo);
		this.name = name;
	}
	
	/**
	 * Evaluates the <tt>MathObject</tt> saved under the name of this variable, and returns it.
	 * @return {@code Variables.get(name).evaluate();}
	 * @throws UndefinedException if no variable with this name exists.
	 */
	public MathObject evaluate() {
		try {
			return Variables.get(name).evaluate();
		} catch(NullPointerException e) {
			throw new UndefinedException("Variable with name " + name + " is not defined.");
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
}
