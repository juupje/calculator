package com.github.juupje.calculator.mathobjects;

public abstract class MIndexable implements MathObject {

	Shape shape;
	
	/**
	 * Gets the element specified by the given index
	 * @param index the index of the element which should be returned
	 * @return the element specified by {@code index}
	 */
	public abstract MathObject get(int... index);
	
	/**
	 * Sets the element specified by the given index to the given object
	 * @param m the object to be set
	 * @param index the index of the element which will be set to {@code m}
	 */
	public abstract void set(MathObject m, int... index);
	
	boolean checkindex(int... index) {
		if(index.length!=shape.dim()) return false;
		for(int i = 0; i < index.length; i++)
			if(index[i]>=shape.get(i))
				return false;
		return true;
	}
	
	public abstract MIndexable multiply(MScalar s);
	public MIndexable multiply(double d) {
		return multiply(new MReal(d));
	}
	
	@Override
	public Shape shape() {
		return shape;
	}
}
