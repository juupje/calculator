package com.github.juupje.calculator.algorithms.algebra.range;

public class Index {
	String name;
	IndexRange range;
	
	public Index(String name, IndexRange range) {
		this.range = range;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public IndexRange getRange() {
		return range;
	}
	
	@Override
	public boolean equals(Object other) {
		if(other instanceof Index)
			return ((Index) other).getName().equals(name);
		return false;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public void setName(String name) {
		this.name = name;;
	}
	
	@Override
	public String toString() {
		return "Index [name="+name+", indices="+range.toString()+"]";
	}
}