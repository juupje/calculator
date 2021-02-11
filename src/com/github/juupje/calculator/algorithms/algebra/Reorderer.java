package com.github.juupje.calculator.algorithms.algebra;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.mathobjects.MFunction;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;
import com.github.juupje.calculator.tree.Tree;

public class Reorderer extends Algorithm {
	
	MFunction func;
	
	@Override
	public MathObject execute() {
		Tree tr = func.getTree().copy();
		tr.DFS(Simplifier.sort);
		return new MFunction(func.getParameters(), func.getParamShapes(), tr, func.isDefined());
	}

	@Override
	protected MathObject execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	protected void prepare(MathObject[] args) {
		if(args.length == 1 && args[0] instanceof MFunction) {
			func = (MFunction) args[0];
			prepared = true;
		} else
			throw new IllegalArgumentException("Only functions can be reordered. Got " + argTypesToString(args));
	}

	@Override
	public Shape shape(Shape... shapes) {
		if(shapes.length == 1 && shapes[0].equals(0))
			return shapes[0];
		throw new ShapeException("Only functions (shape=0) can be sorted.");
	}
}