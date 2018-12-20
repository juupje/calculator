package mathobjects;

import helpers.exceptions.InvalidOperationException;

public class MReal extends MScalar {
	double value = 0;
	public MReal() {}
	public MReal(double d) {
		value = d;
	}
	
	/**
	 * Adds the value of the given {@code MScalar} to the value of this one.
	 * @param other the {@code MScalar} containing the value to be added.
	 * @return {@code this}
	 */
	public MScalar add(MScalar other) {
		if(other.isComplex())
			return new MComplex(this).add(other);
		value += ((MReal) other).getValue();
		return this;
	}
	
	/**
	 * Adds the given {@code double} to the value of this {@code MScalar}.
	 * @param d the {@code double} to be added.
	 * @return {#code this}
	}
	 */
	public MScalar add(double d) {
		value += d;
		return this;
	}
	
	/**
	 * Subtracts the value of the given {@code MScalar} off the value of this one.
	 * @param other the {@code MScalar} containing the value to be subtracted.
	 * @return {@code this}
	 */
	public MScalar subtract(MScalar other) {
		if(other.isComplex())
			return new MComplex(this).subtract(other);
		value -= ((MReal) other).getValue();
		return this;
	}
	
	/**
	 * Subtracts the given {@code double} off the value of this {@code MScalar}.
	 * @param d the {@code double} to be subtracted.
	 * @return {@code this}
	 */
	public MScalar subtract(double d) {
		value -= d;
		return this;
	}
	
	/**
	 * Multiplies the value of this {@code MScalar} with the value of the given one.
	 * @param other the {@code MScalar} containing the value be multiplied with.
	 * @return {@code this}
	 */
	public MScalar multiply(MScalar other) {
		if(other.isComplex())
			return new MComplex(this).multiply(other);
		value *= ((MReal) other).getValue();
		return this;
	}
	
	/**
	 * Multiplies the value of this {@code MScalar} with the given {@code double}.
	 * @param d the {@code double} to be multiplied with.
	 * @return {@code this}
	 */
	public MScalar multiply(double d) {
		value *= d;
		return this;
	}
	
	/**
	 * Divides the value of this {@code MScalar} by the value of the given one.
	 * @param other the {@code MScalar} containing the value of the quotient.
	 * @return {@code this}
	 */
	public MScalar divide(MScalar other) {
		if(other.isComplex())
			return new MComplex(this).divide(other);
		else
			value /= ((MReal) other).getValue();
		return this;
	}
	
	/**
	 * Divides the value of this {@code MScalar} by the given {@code double}.
	 * @param d the quotient.
	 * @return {@code this}
	 */
	public MScalar divide(double d) {
		value /= d;
		return this;
	}
	
	/**
	 * Raises the value of this {@code MScalar} to the power of the value of the given one.
	 * @param other the {@code MScalar} containing the value of the exponent.
	 * @return {@code this}
	 */
	public MScalar power(MScalar other) {
		if(!other.isComplex())
			value = Math.pow(value, ((MReal) other).getValue());
		else {
			double a = ((MComplex) other).real();
			double b = ((MComplex) other).imag();
			return MComplex.fromPolar(Math.pow(value, a), b*Math.log(value));
		}
		return this;
	}
	
	/**
	 * Raises the value of this {@code MScalar} to the power the given {@code double}.
	 * @param d the exponent.
	 * @return {@code this}
	 */
	public MReal power(double d) {
		value = Math.pow(value, d);
		return this;
	}
	
	/**
	 * Converts the value of this {@code MScalar} into the module of the other.
	 * @param other
	 * @return {@code this}
	 */
	public MReal mod(MReal other) {
		if(!other.isInteger())
			throw new InvalidOperationException("Can't perform modulo operation on double and non-integer");
		value %= other.getValue();
		return this;
	}
	
	/**
	 * Converts the value of this {@code MScalar} into the module of the given {@code int}.
	 * @param d.
	 * @return {@code this}
	 */
	public MScalar mod(int d) {
		value %= d;
		return this;
	}
	
	@Override
	public boolean isComplex() {
		return false;
	}
	
	public boolean isInteger() {
		return Math.floor(value) == Math.ceil(value);
	}
	
	/**
	 * Seriously? Checking the JavaDoc for a getter? What do you think this does?!
	 * @return Dude... think for yourself for once.
	 */
	public double getValue() {
		return value;
	}
	
	/**
	 * See the JavaDoc for the getter {@link MScalar.getValue}.
	 * @param d
	 */
	public void setValue(double d) {
		value = d;
	}
	
	/**
	 * Negates the value of this {@code MScalar}. The new value will be {@code value_new = -value_old}
	 * @return {@code this}
	 */
	@Override
	public MReal negate() {
		value = -value;
		return this;
	}
	/**
	 * Inverts the {#code double}. The new value will be {#code value_new=1/value_old},
	 * @return {@code this}
	 */
	@Override
	public MReal invert() {
		value = 1/value;
		return this;
	}
	
	@Override
	public double abs() {
		return value < 0 ? -1*value : value;
	}
	
	/**
	 * @return a new {@code MScalar} with the same value as this one.
	 */
	@Override
	public MReal copy() {
		return new MReal(value);
	}
	
	@Override
	public MReal evaluate() {
		return copy();
	}
	
	//######## static methods #####
	@Override
	public boolean equals(Object other) {
		if (other instanceof MReal)
			return ((MReal) other).getValue() == value;
		if (other instanceof Number)
			return ((Number) other).doubleValue() == value;
		if(other instanceof MComplex) {
			MComplex z = (MComplex) other;
			return (z.polar ? z.arg()==0 && z.getR()==value : z.imag()==0 && z.real()==value);
		}
		return false;
	}

}
