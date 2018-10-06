package algorithms;

import mathobjects.MathObject;

public abstract class Algorithm {
	public abstract MathObject execute(MathObject... args);
	protected abstract void prepare(MathObject[] args);
}
