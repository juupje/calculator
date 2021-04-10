package com.github.juupje.calculator.algorithms.linalg;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.InvalidOperationException;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;

public class LUSolver extends Algorithm {
	
	MMatrix A;
	MVector b;
	
	@Override
	public MVector execute() {
		if(!prepared) return null;
		return new LUDecomposition(A).solve(b);
	}

	@Override
	protected MVector execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	protected void prepare(MathObject[] args) {
		prepared = false;
		if(args.length==2) {
			if(args[0] instanceof MMatrix)
				A = (MMatrix) args[0];
			else
				throw new IllegalArgumentException("Solve expected first argument to be matrix, got " + Tools.type(args[0]));
			if(args[1] instanceof MVector)
				b = (MVector) args[1];
			else
 				throw new IllegalArgumentException("Solve expected second argument to be vector, got " + Tools.type(args[1]));
			if(A.shape().cols() == b.size())			
				prepared = true;
			else
				throw new IllegalArgumentException("Shape mismatch: " + A.shape() + " and " + b.shape());
		} else
			throw new IllegalArgumentException("Solve expected two arguments, got " + args.length);
	}

	@Override
	public Shape shape(Shape... shapes) {
		if(shapes.length!=2)
			throw new InvalidOperationException("Solve expected two arguments, got " + shapes.length);
		if(shapes[0].dim()!=2)
			throw new InvalidOperationException("Solve expected first argument to be a matrix, got shape " + shapes[0]);
		if(shapes[1].dim()!=1 || shapes[1].dim()==2 && shapes[1].get(0)==1)
			throw new InvalidOperationException("Solve expected second argument to be a vector, got shape " + shapes[1]);
		if(shapes[1].dim()==1 && shapes[0].get(1)!=shapes[1].get(0) || shapes[1].dim()==2 && shapes[0].get(1)!=shapes[1].get(1))
			throw new InvalidOperationException("Shapes do not match: " + shapes[0] + " and " + shapes[1]);
		return new Shape(shapes[0].get(0));
	}

}
