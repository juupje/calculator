package algorithms;

import mathobjects.MathObject;
import algorithms.algebra.ABCFormula;
import algorithms.calculus.Integrator;
import algorithms.calculus.Deriver;
import algorithms.calculus.Gradient;
import algorithms.linalg.GaussianElimination;
import algorithms.linalg.JordanElimination;

public enum Algorithms {
	INTEGRAL(new Integrator()),
	DERIVATIVE(new Deriver()),
	GRAD(new Gradient()),
	REF(new GaussianElimination()),
	RREF(new JordanElimination()),
	ABC(new ABCFormula());
	
	Algorithm a;
	Algorithms(Algorithm a) {
		this.a = a;
	}
	
	public MathObject execute(MathObject... args) {
		return a.execute(args);
	}
	
	public MathObject execute(String... args) {
		return a.execute(args);
	}
	
	public static boolean isAlgorithm(String s) {
		try {return getAlgorithm(s) != null;}catch(IllegalArgumentException e) {return false;}
	}
	
	public static Algorithms getAlgorithm(String s) {
		return valueOf(s.toUpperCase());
	}
}
