package com.github.juupje.calculator.algorithms.algebra.range;

import java.util.Iterator;

public abstract class IndexRange implements Iterator<Integer> {
	int idx;
	
	/**
	 * @return the length of the index range (e.g. the count of the indices in the range)
	 */
	public abstract int length();
	
	/**
	 * @return the current value of the iterator.
	 * @throws IllegalStateException if {@code next()] has not yet been called after initialization
	 * or the previous call of {@code reset()}
	 */
	public abstract int get() throws IllegalStateException;
	
	/**
	 * Resets the iterator to it's initial condition.
	 */
	public abstract void reset();
}
