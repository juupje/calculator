package mathobjects;

import helpers.Shape;
import helpers.exceptions.ShapeException;

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
	
	public abstract Shape shape() ;
	
	@Override
	public abstract String toString();
}
