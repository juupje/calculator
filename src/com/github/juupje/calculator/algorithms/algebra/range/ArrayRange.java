package com.github.juupje.calculator.algorithms.algebra.range;

import java.util.NoSuchElementException;

import com.github.juupje.calculator.helpers.Tools;

public class ArrayRange extends IndexRange {

	int[] array;
	int idx = -1;
	public ArrayRange(int[] array) {
		this.array = array;
	}
	
	@Override
	public boolean hasNext() {
		return idx < array.length-1;
	}

	@Override
	public Integer next() {
		if(idx>=array.length-1)
			throw new NoSuchElementException("Requested non-existing element of range.");				
		return array[++idx];
	}
	
	@Override
	public int get() {
		if(idx<0)
			throw new IllegalStateException("next() has not yet been called");
		return array[idx];
	}
	
	@Override
	public int length() {
		return array.length;
	}
	
	@Override
	public void reset() {
		idx = -1;
	}
	
	@Override
	public String toString() {
		return "["+Tools.join(", ", array)+"]";
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof ArrayRange) {
			int[] arr = ((ArrayRange) other).array;
			if(arr.length==array.length)
				for(int i = 0; i < arr.length; i++) {
					if(arr[i]!=array[i])
						return false;
				}
			else
				return false;
			return true;
		}
		return false;
	}
}