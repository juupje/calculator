package mathobjects;

public class MScalar implements MathObject{
	
	double value = 0;
	public MScalar() {}
	public MScalar(double d) {
		value = d;
	}
	
	/**
	 * Adds the value of the given {@code MScalar} to the value of this one.
	 * @param other the {@code MScalar} containing the value to be added.
	 * @return {@code this}
	 */
	public MScalar add(MScalar other) {
		value += other.getValue();
		return this;
	}
	
	/**
	 * Adds the given {@code double} to the value of this {@code MScalar}.
	 * @param d the {@code double} to be added.
	 * @return {#code this}
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
		value -= other.getValue();
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
		value *= other.getValue();
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
		value /= other.getValue();
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
	
	//######## static methods #####
	@Override
	public boolean equals(Object other) {
		if(other instanceof MScalar)
			return ((MScalar) other).getValue() == value;
		if(other instanceof Number)
			return ((Number) other).doubleValue() == value;
		return false;
	}
	
	/**
	 * @return the {#code double} stored in this variable as a {@code String}.
	 */
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	/**
	 * Negates the value of this {@code MScalar}. The new value will be {@code value_new = -value_old}
	 * @return {@code this}
	 */
	@Override
	public MathObject negate() {
		value = -value;
		return this;
	}
	/**
	 * Inverts the {#code double}. The new value will be {#code value_new=1/value_old},
	 * @return {@code this}
	 */
	@Override
	public MathObject invert() {
		value = 1/value;
		return this;
	}

	/**
	 * @return a new {@code MScalar} with the same value as this one.
	 */
	@Override
	public MathObject copy() {
		return new MScalar(value);
	}
}
