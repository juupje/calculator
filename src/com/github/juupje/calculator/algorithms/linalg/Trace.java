package com.github.juupje.calculator.algorithms.linalg;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;

public class Trace extends Algorithm {

	MMatrix m;
	public Trace() {}
	
	public Trace(MMatrix m) {
		prepare(new MMatrix[] {m});
	}
	
	@Override
	public MathObject execute() {
		MathObject result = null;
		for(int i = 0; i < m.shape().cols(); i++)
			result = Operator.ADD.evaluate(m.get(i, i),  result);
		return result;
	}

	@Override
	protected MathObject execute(MathObject... args) {
		prepare(args);
		return execute();
	}

	@Override
	protected void prepare(MathObject[] args) {
		m = null;
		if(args.length == 1)
			if(args[0] instanceof MMatrix && ((MMatrix) args[0]).isSquare()) {
					m = (MMatrix) args[0];
					prepared = true;
					return;
			} else
				throw new IllegalArgumentException("Trace is only defined for square matrices.");
		throw new IllegalArgumentException("Trace is not applicable to arguments " + argTypesToString(args) + ". See help(trace)");
	}

	@Override
	public Shape shape(Shape... shapes) {
		if(shapes.length == 1 && shapes[0].dim() == 2 && shapes[0].isSquare())	
			return new Shape(0, 0);
		throw new IllegalArgumentException("Trace expects one argument of shape (n, n), got " + Tools.join(", ", (Object[]) shapes));
	}
}