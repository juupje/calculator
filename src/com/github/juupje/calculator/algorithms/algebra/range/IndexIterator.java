package com.github.juupje.calculator.algorithms.algebra.range;

import java.util.ArrayList;

import com.github.juupje.calculator.main.Variables;
import com.github.juupje.calculator.mathobjects.MReal;

public class IndexIterator {
    
	int[] indices;
	MReal[] values;
	ArrayList<Index> indexList;
	int n;
	/**
	 * Creates a kind of 'iterator' over all the combinations of the values of the given indices
	 * Note that this initializer already sets the first value of the iterator.
	 * This means that the first call of {@link #next()} calculates the 2nd combination!
	 * The index values are directly stored in a list of {@code MReal}s, as well as in the {@code Variables}
	 * @param indexList
	 */
	public IndexIterator(ArrayList<Index> indexList) {
		this.indexList = indexList;
		n = indexList.size();
		indices = new int[n];
		values = new MReal[n];
		// Initialize with first element's index
		for(int i = 0; i < n; i++) {
	        indices[i] = 0;
	        Index index = indexList.get(i);
	        index.getRange().reset();
	        values[i] = new MReal(index.getRange().next());
	        Variables.set(index.getName(), values[i]);
		}
	}
 
	/**
	 * Finds the next combination of index values.
	 * To iterate through all values, first use the values set 
	 * in the constructor and then call this method until it returns false
	 * @return {@code true} if the next combination has been found {@code false} otherwise.
	 */
	public boolean next() {
		// Find the rightmost array that has more
		// elements left after the current element in that array
		int next = n - 1;
		while (next >= 0 && !indexList.get(next).getRange().hasNext())
			next--;
			
		// No such array is found so no more combinations left
		if (next < 0)
			return false;
		
		// If found move to next element in that array
		indexList.get(next).getRange().next();
		
		// For all arrays to the right of this array 
		// let the current index again point to first element
		for(int i = next + 1; i < n; i++) {
			indexList.get(i).getRange().reset(); //reset the iterator
			indexList.get(i).getRange().next(); //select the first element
		}
		
		// Save current combination to the variables
		for(int i = 0; i < n; i++)
			values[i].setValue(indexList.get(i).getRange().get());
		return true;
	}
	
	public int[] getIndices() {
		int[] indices = new int[n];
		for(int i =0; i < n; i++)
			indices[i] = (int) values[i].getValue();
		return indices;
	}
}