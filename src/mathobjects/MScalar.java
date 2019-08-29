package mathobjects;

import helpers.Printer;
import helpers.Shape;

public abstract class MScalar implements MathObject {
	
	/**
	 * returns the real component of the complex number.
	 * This recalculates the Cartesian form.
	 * @return {@code Re(z)}, where z is this number.
	 */
	public abstract double real();
	
	/**
	 * Returns the imaginary component of this number.
	 * This recalculates the Cartesian form.
	 * @return {@code Im(z)}, where z is this number.
	 */
	public abstract double imag();
	
	/**
	 * Adds the value of the given {@code MScalar} to the value of this one.
	 * @param other the {@code MScalar} containing the value to be added.
	 * @return {@code this}
	 */
	public abstract MScalar add(MScalar other);
	
	/**
	 * Adds the given {@code double} to the value of this {@code MScalar}.
	 * @param d the {@code double} to be added.
	 * @return {#code this}
	 */
	public abstract MScalar add(double d);
	
	/**
	 * Subtracts the value of the given {@code MScalar} off the value of this one.
	 * @param other the {@code MScalar} containing the value to be subtracted.
	 * @return {@code this}
	 */
	public abstract MScalar subtract(MScalar other);
	
	/**
	 * Subtracts the given {@code double} off the value of this {@code MScalar}.
	 * @param d the {@code double} to be subtracted.
	 * @return {@code this}
	 */
	public abstract MScalar subtract(double d);
	
	/**
	 * Multiplies the value of this {@code MScalar} with the value of the given one.
	 * @param other the {@code MScalar} containing the value be multiplied with.
	 * @return {@code this}
	 */
	public abstract MScalar multiply(MScalar other);
	
	/**
	 * Multiplies the value of this {@code MScalar} with the given {@code double}.
	 * @param d the {@code double} to be multiplied with.
	 * @return {@code this}
	 */
	public abstract MScalar multiply(double d);
	
	/**
	 * Divides the value of this {@code MScalar} by the value of the given one.
	 * @param other the {@code MScalar} containing the value of the quotient.
	 * @return {@code this}
	 */
	public abstract MScalar divide(MScalar other);
	
	/**
	 * Divides the value of this {@code MScalar} by the given {@code double}.
	 * @param d the quotient.
	 * @return {@code this}
	 */
	public abstract MScalar divide(double d);
	
	/**
	 * Raises the value of this {@code MScalar} to the power of the value of the given one.
	 * @param other the {@code MScalar} containing the value of the exponent.
	 * @return {@code this}
	 */
	public abstract MScalar power(MScalar other);
	
	/**
	 * Raises the value of this {@code MScalar} to the power the given {@code double}.
	 * @param d the exponent.
	 * @return {@code this}
	 */
	public abstract MScalar power(double d);
	
	@Override
	public abstract MScalar invert();
	@Override
	public abstract MScalar negate();
	@Override
	public abstract MScalar evaluate();
	
	@Override
	public boolean isNumeric() {return true;}
	
	public abstract MScalar copy();
	
	public abstract MScalar conjugate();

	public abstract double abs();
	
	public abstract boolean isComplex();
	
	public abstract boolean isInteger();
	
	public abstract boolean isNaN();
	
	/**
	 * @return {@code Printer.numToString(this);}
	 * @see Printer#numToString(MScalar)
	 */
	@Override
	public String toString() {
		return Printer.numToString(this);
	}
	
	@Override
	public Shape shape() {
		return Shape.SCALAR;
	}

}
