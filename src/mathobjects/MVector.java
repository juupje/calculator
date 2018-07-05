package mathobjects;

import helpers.InvalidOperationException;
import main.Operator;

public class MVector extends MathObject {
	MathObject[] v;
	int size;
	
	public MVector(int size) {
		this.size = size;
		v = new MathObject[size];
		for(int i = 0; i < size; i++)
			v[i] = new MScalar();
	}
	
	public MVector(double[] list) {
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
	 * If that is not your wish, consider using {@link Operator.ADD}
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
	 * returns the i-th element of the vector. (Note that the vector starts at index 0).
	 * @param i the element's index.
	 * @return the i-the element.
	 */
	public MathObject get(int i) {
		return v[i];
	}
	
	/**
	 * @return the length of the vector. This equals the mathematical dimension.
	 */
	public int size() {
		return size;
	}
	
	
	/**
	 * Inverts the vector. This means that every component will inverted separately.
	 */
	@Override
	public void invert() {
		for(MathObject element : v)
			element.invert();
	}
	
	@Override
	public MathObject copy() {
		return new MVector(v);
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
