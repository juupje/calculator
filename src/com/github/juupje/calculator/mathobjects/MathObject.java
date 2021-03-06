package com.github.juupje.calculator.mathobjects;

public interface MathObject {
	/**
	 * Changes the current <tt>MathObject</tt> (called <tt>a</tt>) to <tt>b</tt>, such that <tt>a+b</tt>=0.
	 * @return <tt>this</tt>
	 */
	public abstract MathObject negate();
	
	/**
	 * Changes the current <tt>MathObject</tt> (called <tt>a</tt>) to <tt>b</tt>, such that <tt>a*b</tt>=I, where I is the multiplication identity.
	 * @return <tt>this</tt>
	 */
	public abstract MathObject invert();
	
	/**
	 * Creates a new <tt>MathObject</tt> containing exactly the same as <tt>this</tt>.
	 * @return the newly created <tt>MathObject</tt>
	 */
	public abstract MathObject copy();
	
	/**
	 * Evaluates the current <tt>MathObject</tt>, so that it will be entirely numeric.
	 * @return A copy of the current <tt>MathObject</tt> with the corresponding numeric value(s).
	 */
	public abstract MathObject evaluate();
	
	public abstract MathObject multiply(MScalar s);
	default MathObject multiply(double d) {
		return multiply(new MReal(d));
	}
	
	public abstract Shape shape();
	public abstract boolean isNumeric();
	
	@Override
	public abstract String toString();
}
