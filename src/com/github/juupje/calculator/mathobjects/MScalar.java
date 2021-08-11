package com.github.juupje.calculator.mathobjects;

import com.github.juupje.calculator.printer.Printer;

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
	 * @return {@code this} or a new {@code MScalar} if {@code this} is an {@code MReal} and {@code other} is complex.
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
	 * @return {@code this} or a new {@code MScalar} if {@code this} is an {@code MReal} and {@code other} is complex.
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
	 * @return {@code this} or a new {@code MScalar} if {@code this} is an {@code MReal} and {@code other} is complex.
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
	 * @return {@code this} or a new {@code MScalar} if {@code this} is an {@code MReal} and {@code other} is complex.
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
	 * @return {@code this} or a new {@code MScalar} if {@code this} is an {@code MReal} and {@code other} is complex.
	 */
	public abstract MScalar power(MScalar other);
	
	/**
	 * Raises the value of this {@code MScalar} to the power the given {@code double}.
	 * @param d the exponent.
	 * @return {@code this}
	 */
	public abstract MScalar power(double d);
	
	public abstract MScalar sqrt();
	
	@Override
	public abstract MScalar invert();
	@Override
	public abstract MScalar negate();
	@Override
	public abstract MScalar evaluate();
	
	@Override
	public boolean isNumeric() {return true;}
	
	public boolean isFraction() {return false;}
	public boolean hasError() {return false;}
	
	public abstract MScalar copy();
	
	public abstract MScalar conjugate();

	public abstract double abs();
	
	/**
	 * @return the square of the absolute value.
	 */
	public abstract double abs2();
	
	public boolean isComplex() { return false;}
	
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
	
	@SuppressWarnings("unchecked")
	public static <T extends MScalar> T conj(T z) {
		if(z instanceof MReal)
			return (T)z.copy();
		return (T) new MComplex(z.real(),-z.imag());
	}
	
	/**
	 * Computes the sum of two MScalars
	 * @param a
	 * @param b
	 * @return a+b
	 */
	public static MScalar add(MScalar a, MScalar b) {
		if(a.hasError())
			return a.copy().add(b);
		if(b.hasError())
			return b.copy().add(a);
		if(a.isFraction())
			return a.copy().add(b);
		if(b.isFraction())
			return b.copy().add(a);
		if(!a.isComplex())
			return a.copy().add(b);
		if(!b.isComplex())
			return b.copy().add(a);
		return a.copy().add(b);
	}
	
	/**
	 * Computes the difference of two MScalars
	 * @param a
	 * @param b
	 * @return a-b
	 */
	public static MScalar subtract(MScalar a, MScalar b) {
		if(a.hasError())
			return a.copy().subtract(b);
		if(b.hasError())
			return b.copy().subtract(a).negate();
		if(a.isFraction())
			return a.copy().subtract(b);
		if(b.isFraction())
			return b.copy().subtract(a).negate();
		if(!a.isComplex())
			return a.copy().subtract(b);
		if(!b.isComplex())
			return b.copy().subtract(a).negate();
		return a.copy().subtract(b);
	}
	
	/**
	 * Computes the product of two MScalars
	 * @param a
	 * @param b
	 * @return a*b
	 */
	public static MScalar multiply(MScalar a, MScalar b) {
		if(a.hasError())
			return a.copy().multiply(b);
		if(b.hasError())
			return b.copy().multiply(a);
		if(a.isFraction())
			return a.copy().multiply(b);
		if(b.isFraction())
			return b.copy().multiply(a);
		if(!a.isComplex())
			return a.copy().multiply(b);
		if(!b.isComplex())
			return b.copy().multiply(a);
		return a.copy().multiply(b);
	}
	
	/**
	 * Computes the product of two MScalars
	 * @param a
	 * @param b
	 * @return a/b
	 */
	public static MScalar divide(MScalar a, MScalar b) {
		if(a.hasError())
			return a.copy().divide(b);
		if(b.hasError())
			return b.copy().divide(a).invert();
		if(a.isFraction())
			return a.copy().divide(b);
		if(b.isFraction())
			return b.copy().divide(a).invert();
		if(!a.isComplex())
			return a.copy().divide(b);
		if(!b.isComplex())
			return b.copy().divide(a).invert();
		return a.copy().divide(b);
	}
	
	/**
	 * Computes the product of two MScalars
	 * @param a
	 * @param b
	 * @return a/b
	 */
	public static MScalar power(MScalar a, MScalar b) {
		if(a.hasError())
			return a.copy().power(b);
		if(b.hasError())
			return MRealError.power(a, (MRealError)b);
		if(a.isFraction())
			return a.copy().power(b);
		return a.copy().power(b);
	}
}