package algorithms.algebra;

import algorithms.Algorithm;
import helpers.Shape;
import helpers.exceptions.ShapeException;
import mathobjects.MFunction;
import mathobjects.MathObject;
import tree.Tree;

public class Reorderer extends Algorithm {
	
	MFunction func;
	
	@Override
	public MathObject execute() {
		Tree tr = func.getTree().copy();
		tr.DFS(Simplifier.sort);
		return new MFunction(func.getParameters(), tr, func.isDefined());
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
