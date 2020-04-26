package com.github.juupje.calculator.mathobjects;

import java.util.function.Function;

import com.github.juupje.calculator.algorithms.Norm;
import com.github.juupje.calculator.helpers.exceptions.IndexException;
import com.github.juupje.calculator.helpers.exceptions.InvalidOperationException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.settings.Settings;

public class MVector implements MathObject{
	MathObject[] v;
	int size;
	boolean transposed = false;
	Shape shape;
	
	public MVector(Shape s) {
		if(s.dim()==1)
			for(int i = 0; i < s.size(); i++)
				if(s.get(i)!=1) {
					size = s.get(i);
					v = new MathObject[size];
					for(int j = 0; j < size; j++)
						v[j] = new MReal();
				}
		shape = s;
	}
	
	public MVector(int size) {
		this(new Shape(size));
	}
	
	public MVector(int size, boolean transposed) {
		this(size);
		if(transposed)
			transpose();
	}
	
	public MVector(boolean transposed, double... list){
		this(list);
		if(transposed)
			transpose();
	}
	
	public MVector(double... list) {
		size = list.length;
		v = new MathObject[size];
		for(int i = 0; i < size; i++)
			v[i] = new MReal(list[i]);
		shape = new Shape(size);
	}
	
	public MVector(boolean transposed, MathObject... list) {
		this(list);
		if(transposed)
			transpose();
	}
	
	public MVector(MathObject... list) {
		v = list;
		size = list.length;
		shape = new Shape(size);
	}
	

	public MVector(int size, MathObject c) {
		v = new MathObject[size];
		for(int i = 0; i < size; i++)
			v[i] = c.copy();
		shape = new Shape(size);
	}
	
	public MVector transpose() {
		transposed = !transposed;
		shape = shape.transpose();
		return this;
	}
	
	public boolean isTransposed() {
		return transposed;
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
		return multiply(new MReal(d));
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
		return divide(new MReal(d));
	}
	
	/**
	 * Multiplies this vector with another. Both vectors are left unchanged.
	 * If the vectors are both column or both row vectors, the scalar product ({@link #dot(MVector)}) is returned.
	 * Let {@code this} be a and the other vector be b.<br/>
	 * If a is a row vector with shape (1 x m) and b is a column vector with shape (m x 1), the product ab is again the dot product.
	 * If, however, a is a column vector of shape (n x 1) and b is a row vector with shape (1 x m), the product ab is <br/>
	 * {@code (ab)_ij = a_i*b_j for i=1,...,n and j=1,...,m}
	 * where ab is a matrix (also called a dyadic) the shape (n x m)
	 * @param other the vector b
	 * @return the dyadic product if a is a column vector and b is a row vector. Otherwise the dot product.
	 * @see #dot(MVector)
	 */
	public MathObject multiply(MVector other) {
		if(!transposed && other.isTransposed()) {//column times row vector
			MathObject[][] m = new MathObject[size][other.size];
			for(int i = 0; i < size; i++) {
				for(int j = 0; j < other.size; j++)
					m[i][j] = Operator.MULTIPLY.evaluate(v[i], other.get(j));
			}
			return new MMatrix(m);
		} else
			return dot(other); //row times column vector
	}
	
	public MathObject cross(MVector other) {
		if(other.size() != size || (size != 2 && size!=3))
			throw new InvalidOperationException("The cross product is only definef for vectors of the same size, which can be 2 or 3. Sizes: " + size + ", " + other.size());
		if(size == 2)
			return Operator.SUBTRACT.evaluate(Operator.MULTIPLY.evaluate(get(0), other.get(1)), Operator.MULTIPLY.evaluate(get(1), other.get(0)));
		else //size==3
			return new MVector(new MathObject[] {
					Operator.SUBTRACT.evaluate(Operator.MULTIPLY.evaluate(get(1), other.get(2)), Operator.MULTIPLY.evaluate(get(2), other.get(1))),
					Operator.SUBTRACT.evaluate(Operator.MULTIPLY.evaluate(get(2), other.get(0)), Operator.MULTIPLY.evaluate(get(0), other.get(2))),
					Operator.SUBTRACT.evaluate(Operator.MULTIPLY.evaluate(get(0), other.get(1)), Operator.MULTIPLY.evaluate(get(1), other.get(0)))
			});
			
	}
	
	/**
	 * Builds the dot product of {@code this} and the given {@code MVector} and returns it.
	 * @param other the vector that needs to be dot-multiplied with {@code this}.
	 * @return the resulting dot product.
	 * @throws ShapeException if the size of {@code other} does not match the size of {@code this}.
	 */
	public MathObject dot(MVector other) {
		if(other.size() != size)
			throw new ShapeException("The dot product is only defined for vectors of the same length. Lengths: " + size + ", " + other.size());
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
	 * if <tt>n</tt> is an integer: <tt>v^n=|v|^n</tt>
	 * @param n the <tt>MScalar</tt> containing <tt>n</tt>.
	 * @return <tt>this</tt>
	 * @throws InvalidOperationException if <tt>n</tt> is not an positive integer value.
	 */
	public MReal power(MReal n) {
		if(Math.ceil(n.value) != Math.floor(n.value) || n.value<=0)
			throw new InvalidOperationException("Vectors can only be raised to positive integer powers.");
		return Norm.eucl(this).power((int) n.getValue());
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
			throw new IndexException("You're trying to access component " + i + " of a vector with " + size + " elements.");
		return v[i];
	}
	
	public void set(int index, MathObject m) {
		v[index] = m;
	}
	
	public void set(int index, double d) {
		v[index] = new MReal(d);
	}
	
	/**
	 * Applies a function to each element one by one.
	 * The results are then packed into a new MVector, leaving this one unchanged.
	 * In order to ensure that this vector does not change, the function should not
	 * affect its arguments.
	 * @param f a function which takes a MathObject as its argument and returns another as its result.
	 * @return the newly created MVector of the same shape.
	 */
	public MVector forEach(Function<MathObject, MathObject> f) {
		MathObject[] v2 = new MathObject[v.length];
		for(int i = 0; i < v2.length; i++)
				v2[i] = f.apply(v[i]);
		return new MVector(v2);
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
	public MVector negate() {
		for(MathObject element : v)
			element.negate();
		return this;
	}
	
	/**
	 * Inverts the vector. This means that every component will inverted separately.
	 * Note, though this operation is not mathematically defined, this results in a*inv(a)=1.
	 * @return {@code this}
	 * @see MathObject#invert()
	 */
	@Override
	public MVector invert() {
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
	public MVector copy() {
		MathObject v2[] = new MathObject[size];
		for(int i = 0; i < size; i++)
			v2[i] = v[i].copy();
		return new MVector(transposed, v2);
	}
	
	@Override
	public boolean isNumeric() {
		for(MathObject element : v)
			if(!element.isNumeric()) return false;
		return true;
	}
	
	@Override
	public Shape shape() {
		return shape;
	}
	
	/**
	 * Returns a new <tt>MMatrix</tt> consisting of the evaluated elements in <tt>this</tt>.
	 * @
	 */
	@Override
	public MVector evaluate() {
		MVector v = new MVector(size);
		for(int i = 0; i < v.size; i++)
			v.set(i, get(i).evaluate());
		return v;
	}
	
	public boolean isOfType(Class<? extends MathObject> c) {
		for(MathObject mo : v)
			if(!mo.getClass().isAssignableFrom(c))
				return false;
		return true;
	}
	
	@Override
	public String toString() {
		String s = "(";
		for(MathObject element : v) {
			s += element.toString() + ", ";
			if(Settings.getBool(Settings.MULTILINE_MATRIX) && element instanceof MMatrix)
				s += System.lineSeparator();
		}
		return s.substring(0, s.lastIndexOf(',')) + ")" + (transposed ? "'" : "");
	}
	
	//###### static methods #####	
	public static MVector empty(int size) {
		return new MVector(new MathObject[size]);
	}
	
	/**
	 * @param size length of the vector to be made.
	 * @return a new {@code MVector} of length {@code size} consisting of {@code MConst}s with value 0.
	 */
	public static MVector zero(int size) {
		return new MVector(size);
	}
	
	/**
	 * @param size the length of the vector to be made
	 * @return a new {@code MVector} of length {@code size} consisting of {@code MConst}s with value 1.
	 */
	public static MVector ones(int size) {
		return new MVector(size, new MReal(1));
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
