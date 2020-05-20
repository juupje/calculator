package com.github.juupje.calculator.algorithms.algebra;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;

public class Anticommutator extends Algorithm {
	
	MathObject A, B;
	
	@Override
	public MathObject execute() {
		return Operator.ADD.evaluate(
				Operator.MULTIPLY.evaluate(A, B),
				Operator.MULTIPLY.evaluate(B, A));
	}

	@Override
	protected MathObject execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	protected void prepare(MathObject[] args) {
		if(args.length == 2) {
			A = args[0];
			B = args[1];
			prepared = true;
		} else
			throw new IllegalArgumentException("Anticommutator works on two objects, got " + args.length);
	}

	@Override
	public Shape shape(Shape... shapes) {
		if(shapes.length == 2)
			return Shape.add(Shape.multiply(shapes[0], shapes[1]), Shape.multiply(shapes[1], shapes[0]));
		throw new ShapeException("Anticommutator works on two objects, got " + shapes.length);
	}
}