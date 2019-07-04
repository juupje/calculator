package algorithms.linalg;

import algorithms.Algorithm;
import helpers.Shape;
import mathobjects.MMatrix;
import mathobjects.MReal;
import mathobjects.MathObject;

public class Identity extends Algorithm {

	int size = 0;
	
	@Override
	public MathObject execute() {
		return MMatrix.identity(size);
	}

	@Override
	protected MathObject execute(MathObject... args) {
		prepare(args);
		return execute();
	}

	@Override
	protected void prepare(MathObject[] args) {
		size = 0;
		if (args.length == 1 && args[0] instanceof MReal && ((MReal) args[0]).isInteger())
			size = (int) ((MReal) args[0]).getValue();
		if(size==0)
			throw new IllegalArgumentException(
					"ID requires one real, positive, non-zero integer, got " + argTypesToString(args) + ". See help(id).");
		prepared = true;
	}

	@Override
	public Shape shape(Shape... shapes) {
		return new Shape(0, 0);
	}
}
 