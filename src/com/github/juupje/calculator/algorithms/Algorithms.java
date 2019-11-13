package com.github.juupje.calculator.algorithms;

import java.util.HashMap;
import java.util.Map;

import com.github.juupje.calculator.algorithms.algebra.*;
import com.github.juupje.calculator.algorithms.calculus.*;
import com.github.juupje.calculator.algorithms.linalg.*;
import com.github.juupje.calculator.helpers.exceptions.PluginException;

public enum Algorithms {
	INTEGRAL(new Integrator()),
	DERIVATIVE(new Deriver()),
	GRAD(new Gradient()),
	
	REF(new GaussianElimination()),
	RREF(new JordanElimination()),
	LU(new LUDecomposition()),
	DIAG(new Diagonal()),
	ID(new Identity()),
	TRACE(new Trace()),
	EIG(new Eigenvalues()),
	
	ABC(new ABCFormula()),
	SIMPLIFY(new Simplifier()),
	REORDER(new Reorderer()),
	COM(new Commutator()),
	ANS(new Ans());
	
	Algorithm a;
	Algorithms(Algorithm a) {
		this.a = a;
	}
	
	public Algorithm getAlgorithm() {
		return a;
	}
	
	public static final Map<String, Algorithm> algorithms = new HashMap<>();
	static {
		for(Algorithms a : values())
			algorithms.put(a.toString().toLowerCase(), a.getAlgorithm());
	}
	
	public static boolean isAlgorithm(String name) {
		return algorithms.containsKey(name.toLowerCase());
	}
	
	public static Algorithm getAlgorithm(String name) {
		return algorithms.get(name.toLowerCase());
	}
	
	public static void insertAlgorithm(String name, Algorithm a) {
		Algorithm old = algorithms.put(name, a);
		if(old != null)
			throw new PluginException("Could not assign algorithm '" + name + "' as it already exists.");
		algorithms.put(name, old);
	}
}
