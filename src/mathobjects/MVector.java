package mathobjects;

import algorithms.Norm;
import helpers.exceptions.InvalidOperationException;
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
		for(int i = 0; i < size; i++)
			v[i] = Operator.MULTIPLY.evaluate(v[i], other);
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
		for(int i = 0; i < size; i++)
			v[i] = Operator.DIVIDE.evaluate(v[i], other);
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
	 * Raises this <tt>MVector</tt> to the <tt>n</tt>-th power, where n is a positive integer. Note that this method applies the mathematical definition of raising a vector to a power,
	 * meaning that this is not an element-wise operation.
	 * Let <tt>v</tt> be a vector, than the follow holds:
	 * <ul><li>if <tt>n</tt> is even: <tt>v^n=|v|^n</tt></li><li>if <tt>n</tt> is uneven: <tt>v^n=v|v|^(n-1)</tt></li><ul>
	 * @param n the <tt>MScalar</tt> containing <tt>n</tt>.
	 * @return <tt>this</tt>
	 * @throws InvalidOperationException if <tt>n</tt> is not an positive integer value.
	 */
	public MathObject power(MScalar n) {
		if(Math.ceil(n.value) != Math.floor(n.value) || n.value<=0)
			throw new InvalidOperationException("Vectors can only be raised to positive integer powers.");
		int exp = (int) n.value;
		if(exp % 2 == 0) //even power
			return Norm.eucl(this).power(exp);
		else //uneven power
			return multiply(Norm.eucl(this).power((int) Math.floor(exp/2)).value);
	}
	
	/**
	 * Returns the elements in this <tt>MVector</tt> as an 1D array.
	 * @return an 1D array containing the elements.
	 */
	public MathObject[] elements() {
		return v;
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
	 * @see MathObject#negate()
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
	 * @see MathObject#invert()
	 */
	@Override
	public MathObject invert() {
		for(MathObject element : v)
			element.invert();
		return this;
	}
	
	/**
	 * Copies the <tt>MVector</tt> element-wise.
	 * @return a new <tt>MVector</tt> of the copied elements.
	 * @see MathObject#copy()
	 */
	@Override
	public MathObject copy() {
		MathObject v2[] = new MathObject[size];
		for(int i = 0; i < size; i++)
			v2[i] = v[i].copy();
		return new MVector(v2);
	}
	
	/**
	 * Returns a new <tt>MMatrix</tt> consisting of the evaluated elements in <tt>this</tt>.
	 * @
	 */
	@Override
	public MathObject evaluate() {
		MVector v = new MVector(size);
		for(int i = 0; i < v.size; i++)
			v.set(i, get(i).evaluate());
		return v;
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
	public static MathObject ones(int size) {
		return new MVector(size, new MScalar(1));
	}
	
	/**
	 * Creates an MVector of the elements in the given list.
	 * @param list an <tt>MathObject[]</tt> to be turned into a vector.
	 * @return the created <tt>MVector</tt>
	 */
	public static MVector createMVector(MathObject[] list) {
		MathObject[] list2 = new MathObject[list.length];
		for(int i = 0; i < list.length; i++)
			list2[i] = list[i].copy();
		return new MVector(list2);
	}
}
