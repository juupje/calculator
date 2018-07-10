package mathobjects;

import helpers.InvalidOperationException;
import main.Operator;

public class MVector implements MathObject {
	MathObject[] v;
	int size;
	
	public MVector(int size) {
		this.size = size;
		v = new MathObject[size];
		for(int i = 0; i < size; i++)
			v[i] = new MScalar();
	}
	
	public MVector(double... list) {
		size = list.length;
		v = new MathObject[size];
		for(int i = 0; i < size; i++)
			v[i] = new MScalar(list[i]);
	}
	
	public MVector(MathObject[] list) {
		v = list;
		size = list.length;
	}

	public MVector(int size, MathObject c) {
		v = new MathObject[size];
		for(int i = 0; i < size; i++)
			v[i] = c.copy();
	}
	
	/**
	 * Adds another vector element-wise to this one. Note that this will change the vector on which this method is called.
	 * If that is not your wish, consider using {@link Operator#ADD}
	 * @param other the {@code MVvector} to be added, this one will not be changed.
	 * @return {@code this} after the addition.
	 */
	public MVector add(MVector other) {
		if(other.size() != size)
			throw new InvalidOperationException("To add two vectors, they need to be the same size! Sizes: " + size + " and " +other.size());
		for(int i = 0; i < size; i++)
			v[i] = Operator.ADD.evaluate(v[i], other.get(i));
		return this;
	}
	
	/**
	 * Subtracts another vector element-wise from this one. Note that this will change the vector on which this method is called.
	 * If that is not your wish, consider using {@link Operator.SUBTRACT}
	 * @param other the {@code MVvector} to be subtracted, this one will not be changed.
	 * @return {@code this} after the subtraction.
	 */
	public MVector subtract(MVector other) {
		if(other.size() != size)
			throw new InvalidOperationException("To subtract two vectors, they need to be the same size! Sizes: " + size + " and " +other.size());
		for(int i = 0; i < size; i++)
			v[i] = Operator.SUBTRACT.evaluate(v[i], other.get(i));
		return this;
	}

	/**
	 * Multiplies this {@code MVector} element-wise with the value in the given {@code MScalar}
	 * @param other the {@code MScalar} to be multiplied with.
	 * @return {@code this}
	 */
	public MVector multiply(MScalar other) {
		for(MathObject element : v)
			element = Operator.MULTIPLY.evaluate(element, other);
		return this;
	}
	
	/**
	 * Calls {@code multiply(new MScalar(d));} where d is the given double.
	 * @param d the value to be passed as an {@code MScalar} to {@code multiply()}
	 * @return the result of the call: {@code this}.
	 * @see #multiply(MScalar)
	 */
	public MVector multiply(double d) {
		return multiply(new MScalar(d));
	}
	
	/**
	 * Divides this {@code MVector} element-wise by the value in the given {@code MScalar}.
	 * @param other the {@code MScalar containing the value to be divided by.
	 * @return {@code this}
	 */
	public MVector divide(MScalar other) {
		for(MathObject element : v)
			element = Operator.DIVIDE.evaluate(element, other);
		return this;
	}
	
	/**
	 * Calls {@code divide(new MScalar(d));} where d is the given double.
	 * @param d the value to be passed as an {@code MScalar} to {@code divide()}
	 * @return the result of the call: {@code this}.
	 * @see #divide(MScalar)
	 */
	public MVector divide(double d) {
		return divide(new MScalar(d));
	}
	/**
	 * Builds the dot product of {@code this} and the given {@code MVector} and returns it.
	 * @param other the vector that needs to be dot-multiplied with {@code this}.
	 * @return the resulting dot product.
	 */
	public MathObject dot(MVector other) {
		if(other.size() != size)
			throw new InvalidOperationException("The dot product is only defined for vectors of the same size. Sizes: " + size + ", " + other.size());
		MathObject mo = null;
		for(int i = 0; i < size; i++)
			if(i == 0)
				mo = Operator.MULTIPLY.evaluate(v[i], other.get(i));
			else
				mo = Operator.ADD.evaluate(mo, Operator.MULTIPLY.evaluate(v[i], other.get(i)));
		return mo;
	}
	
	/**
	 * returns the i-th element of the vector. (Note that the vector starts at index 0).
	 * @param i the element's index.
	 * @return the i-the element.
	 */
	public MathObject get(int i) {
		if(i < 0 || i >=size)
			throw new IndexOutOfBoundsException("You're trying to access the " + i + "-th component of a vector with " + size + " elements.");
		return v[i];
	}
	
	public void set(int index, MathObject m) {
		v[index] = m;
	}
	
	public void set(int index, double d) {
		v[index] = new MScalar(d);
	}
	/**
	 * @return the length of the vector. This equals the mathematical dimension.
	 */
	public int size() {
		return size;
	}
	
	/**
	 * Negates the vector. To do so, every component will be negated separately.
	 * @return {@code this}
	 */
	@Override
	public MathObject negate() {
		for(MathObject element : v)
			element.negate();
		return this;
	}
	
	/**
	 * Inverts the vector. This means that every component will inverted separately.
	 * @return {@code this}
	 */
	@Override
	public MathObject invert() {
		for(MathObject element : v)
			element.invert();
		return this;
	}
	
	/**
	 * Returns a copy of the vector.
	 */
	@Override
	public MathObject copy() {
		MathObject v2[] = new MathObject[size];
		for(int i = 0; i < size; i++)
			v2[i] = v[i].copy();
		return new MVector(v2);
	}
	
	@Override
	public String toString() {
		String s = "(";
		for(MathObject element : v)
			s += element.toString() + ", ";
		return s.substring(0, s.length() - 2) + ")";
	}
	
	//###### static methods #####
	/**
	 * @param size length of the vector to be made.
	 * @return a new {@code MVector} of length {@code size} consisting of {@code MConst}s with value 0.
	 */
	public static MathObject zero(int size) {
		return new MVector(size);
	}
	
	/**
	 * @param size the length of the vector to be made
	 * @return a new {@code MVector} of length {@code size} consisting of {@code MConst}s with value 1.
	 */
	public static MathObject identity(int size) {
		return new MVector(size, new MScalar(1));
	}
	
	public static MVector createMVector(MathObject[] list) {
		MathObject[] list2 = new MathObject[list.length];
		for(int i = 0; i < list.length; i++)
			list2[i] = list[i].copy();
		return new MVector(list2);
	}
}
