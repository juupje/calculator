package algorithms;

import mathobjects.MathObject;
import algorithms.algebra.*;
import algorithms.calculus.*;
import algorithms.linalg.*;

public enum AlgorithmEnum {
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
	AlgorithmEnum(Algorithm a) {
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
	
	public static AlgorithmEnum getAlgorithm(String s) {
		return valueOf(s.toUpperCase());
	}
}
