package mathobjects;

public class MScalar extends MathObject {
	
	double value = 0;
	public MScalar() {}
	public MScalar(double d) {
		value = d;
	}
	
	/**
	 * Adds the value of the given {@code MScalar} to the value of this one.
	 * @param other the {#code MScalar} containing the value to be added.
	 * @return {#code this}
	 */
	public MScalar add(MScalar other) {
		value += other.getValue();
		return this;
	}
	
	/**
	 * Adds the given double to the value of this {@code MScalar}.
	 * @param d the double to be added.
	 * @return {#code this}
	 */
	public MScalar add(double d) {
		value += d;
		return this;
	}
	
	/**
	 * Seriously? Checking the JavaDoc for a getter? What do you think this does>!
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
	 * returns the {#code double} stored in this variable as a {@code String}.
	 */
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	/**
	 * inverts the {#code double}. The new value will be {#code value_new=1/value_old},
	 */
	@Override
	public void invert() {
		value = 1/value;
	}

	@Override
	public MathObject copy() {
		return new MScalar(value);
	}

}
