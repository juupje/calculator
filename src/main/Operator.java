package main;

import helpers.InvalidOperationException;
import mathobjects.MScalar;
import mathobjects.MVector;
import mathobjects.MathObject;

public enum Operator {
	ADD {
		@Override
		public MathObject evaluate(MathObject a, MathObject b) {
			if(a instanceof MVector) 
				if(b instanceof MVector)
					return ((MVector)a.copy()).add((MVector) b);
				else throw new InvalidOperationException("Only other vectors can be added to vectors. You're trying to add " + b.getClass() + " to a vector.");
			else if(a instanceof MScalar)
				if(b instanceof MScalar)
					return ((MScalar)a.copy()).add((MScalar) b);
				else throw new InvalidOperationException("Only other scalars can be added to scalars. You're trying to add " + b.getClass() + " to a scalar.");
			throw new InvalidOperationException("ADD operator is not defined for " + a.getClass() + " and " + b.getClass());
		}
	},
	
	SUBSTACT {
		@Override
		public MathObject evaluate(MathObject a, MathObject b) {
			return null;
		}
	};
	
	public abstract MathObject evaluate(MathObject a, MathObject b);
}
