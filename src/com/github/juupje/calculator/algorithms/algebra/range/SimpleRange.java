package com.github.juupje.calculator.algorithms.algebra.range;

import java.util.NoSuchElementException;

import com.github.juupje.calculator.helpers.exceptions.IndexException;

public class SimpleRange extends IndexRange {

	int begin, end;
	/**
	 * Creates a range [begin, end] with step 1
	 * @param begin the first value in the range (inclusive)
	 * @param end the last value in the range (inclusive)
	 */
	public SimpleRange(int begin, int end) {
		if(end<begin) {
			throw new IndexException("End of range must be greater than start: " + end + "<" + begin);
		}
		this.begin = begin;
		this.end = end;
		idx = begin-1;
	}
	
	/**
	 * Creates a range [0, end]] with step 1
	 * @param end the last value in the range (inclusive)
	 */
	public SimpleRange(int end) {
		this(0, end);
	}

	@Override
	public boolean hasNext() {
		return idx<=end-1;
	}

	@Override
	public Integer next() {
		if(idx>=end)
			throw new NoSuchElementException("Requested non-existing element of range.");
		return ++idx;
	}
	
	@Override
	public int get() throws IllegalStateException {
		if(idx<begin)
			throw new IllegalStateException("next() has not yet been called");
		return idx;
	}
	
	@Override
	public int length() {
		return end-begin+1;
	}
	
	@Override
	public void reset() {
		idx = begin-1;
	}
	
	@Override
	public String toString() {
		return "["+begin+":"+end+"]";
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof SimpleRange)
			return ((SimpleRange) other).begin==begin && ((SimpleRange) other).end==end;
		return false;
	}
}
