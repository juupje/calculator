package com.github.juupje.calculator.mathobjects;

import com.github.juupje.calculator.helpers.exceptions.InvalidOperationException;

public class MRealError extends MReal {
	double err = 0;
	public MRealError(double d, double err) {
		value = d;
		this.err = Math.abs(err);
	}
	
	/**
	 * Adds the value of the given {@code MScalar} to the value of this one.
	 * @param other the {@code MScalar} containing the value to be added.
	 * @return {@code this}
	 */
	public MRealError add(MScalar other) {
		if(other.isComplex())
			throw new InvalidOperationException("Cannot add a complex number to a number with an error");
		value += other.real();
		if(other.hasError())
			err = sqAdd(err, ((MRealError) other).err());	
		return this;
	}
	
	/**
	 * Subtracts the value of the given {@code MScalar} off the value of this one.
	 * @param other the {@code MScalar} containing the value to be subtracted.
	 * @return {@code this}
	 */
	public MRealError subtract(MScalar other) {
		if(other.isComplex())
			throw new InvalidOperationException("Cannot subtract a complex number from a number with an error");
		value -= other.real();
		if(other.hasError())
			err = sqAdd(err, ((MRealError) other).err());
		return this;
	}

	/**
	 * Multiplies the value of this {@code MScalar} with the value of the given one.
	 * @param other the {@code MScalar} containing the value be multiplied with.
	 * @return {@code this}
	 */
	public MRealError multiply(MScalar other) {
		if(other.isComplex())
			throw new InvalidOperationException("Cannot multiply a complex number with a number with an error");
		if(other.hasError())
			err = sqAdd(other.real()*err, value*((MRealError) other).err());
		else
			err *= Math.abs(other.real());
		value *= other.real();
		return this;
	}
	
	/**
	 * Multiplies the value of this {@code MScalar} with the given {@code double}.
	 * @param d the {@code double} to be multiplied with.
	 * @return {@code this}
	 */
	public MRealError multiply(double d) {
		value *= d;
		err *= d;
		return this;
	}
	
	/**
	 * Divides the value of this {@code MScalar} by the value of the given one.
	 * @param other the {@code MScalar} containing the value of the quotient.
	 * @return {@code this}
	 */
	public MRealError divide(MScalar other) {
		if(other.isComplex())
			throw new InvalidOperationException("Cannot divide a number with an error by a complex number");
		if(other.hasError())
			err = sqAdd(err/other.real(), value/(other.real()*other.real())*((MRealError)other).err());
		else
			err /= Math.abs(other.real());
		value /= other.real();
		return this;
	}
	
	/**
	 * Divides the value of this {@code MScalar} by the given {@code double}.
	 * @param d the quotient.
	 * @return {@code this}
	 */
	public MRealError divide(double d) {
		value /= d;
		err /= d;
		return this;
	}
	
	/**
	 * Raises the value of this {@code MScalar} to the power of the value of the given one.
	 * @param other the {@code MScalar} containing the value of the exponent.
	 * @return {@code this}
	 */
	public MRealError power(MScalar other) {
		if(other.isComplex())
			throw new InvalidOperationException("Cannot raise a number with an error to a complex power");			
		if(other.hasError()) {
			err = sqAdd(other.real()/value*err, Math.log(value)*((MRealError) other).err());
			value = Math.pow(value, other.real());
			err *= Math.abs(value);
		} else {
			err = other.real()*err/value;
			value = Math.pow(value, other.real());
			err *= value;
		}
		return this;
	}
	
	@Override
	public MRealError sqrt() {
		if(value<0)
			throw new InvalidOperationException("Invalid value in square root");
		value = Math.sqrt(value);
		err = Math.abs(err/(2*value));
		return this;
	}
	
	/**
	 * Raises the value of this {@code MReal} to the power the given {@code double}.
	 * @param d the exponent.
	 * @return {@code this}
	 */
	public MRealError power(double d) {
		err = d*err/value;
		value = Math.pow(value, d);
		err *= value;
		return this;
	}
	
	/**
	 * This operatio is not supported for {@code MRealError}
	 * @throw InvalidOperationException
	 */
	public MReal mod(MReal other) {
		throw new InvalidOperationException("Can't perform modulo operation on number with error");
	}

	/**
	 * This operatio is not supported for {@code MRealError}
	 * @throw InvalidOperationException
	 */
	public MReal mod(int d) {
		throw new InvalidOperationException("Can't perform modulo operation on number with error");
	}
	
	@Override
	public boolean isInteger() {
		return false;
	}
	
	@Override
	public boolean isPosInteger() {
		return false;
	}
	
	@Override
	public boolean hasError() {
		return true;
	}
	
	public double err() {
		return err;
	}
	
	@Override
	public void setValue(double d) {
		throw new InvalidOperationException("Cannot set value without uncertainty");
	}
	
	public void setValue(double d, double e) {
		value = d;
		err = Math.abs(e);
	}

	/**
	 * Inverts the {#code double}. The new value will be {#code value_new=1/value_old},
	 * @return {@code this}
	 */
	@Override
	public MRealError invert() {
		err = Math.abs(err/(value*value));
		value = 1/value;
		return this;
	}
	
	/**
	 * Turns this number into its complex conjugate.
	 * This operation does nothing to real numbers.
	 * @return {@code this}
	 */
	@Override
	public MRealError conjugate() {
		return this;
	}

	@Override
	public double abs() {
		return Math.abs(getValue());
	}
	
	/**
	 * Calculates the square of the absolute value of this number (for the real case, that is just the number squared)
	 * @return {@code value^2}
	 */
	@Override
	public double abs2() {
		double v = getValue(); //for future compatibility with subclasses
		return v*v;
	}
	
	/**
	 * @return a new {@code MScalar} with the same value as this one.
	 */
	@Override
	public MRealError copy() {
		return new MRealError(value, err);
	}
	
	@Override
	public MRealError evaluate() {
		return copy();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof MRealError)
			return (((MRealError) other).getValue() == getValue() &&
				((MRealError) other).err() == err()) ||
					(isNaN() && ((MReal) other).isNaN());
		return false;
	}
	
	private static double sqAdd(double a, double b) {
		return Math.sqrt(a*a+b*b);
	}

	public static MRealError power(MReal a, MRealError b) {
		double value = Math.pow(a.getValue(),b.getValue());
		return new MRealError(value, value*Math.log(a.getValue())*b.err()); //absolute value is calculated in the constructor
	}
}
