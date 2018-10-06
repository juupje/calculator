package algorithms;

import mathobjects.MathObject;
import algorithms.calculus.Integrator;

public enum Algorithms {
	INTEGRATE(new Integrator());
	
	Algorithm a;
	Algorithms(Algorithm a) {
		this.a = a;
	}
	
	public MathObject execute(MathObject... args) {
		return a.execute(args);
	}
	
	public static boolean isAlgorithm(String s) {
		return getAlgorithm(s) != null;
	}
	
	public static Algorithms getAlgorithm(String s) {
		return valueOf(s.toUpperCase());
	}
}
