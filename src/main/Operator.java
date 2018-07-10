package main;

import helpers.InvalidOperationException;
import mathobjects.MScalar;
import mathobjects.MVector;
import mathobjects.MathObject;

public enum Operator {
	//###### double argument operators #######
	/**
	 * Contains a method overriding {@link Operator#evaluate(MathObject, MathObject...) which adds two MathObjects: a and b[0].
	 * @see Operator#ADD.evaluate(MathObject, MathObject...)
	 */
	ADD {
		/**
		 * {@inheritDoc}
		 * Overrides {@link Operator#evaluate(MathObject, MathObject...)}.
		 * Note that neither a not b will be changed in this process.
		 * @param a the {@code MathObject} to which b[0] should be added.
		 * @param b a list of {@code MathObject}, expected to have length 1.
		 * @return a+b
		 */
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if(b.length != 1) throw new IllegalArgumentException("You can only add exactly two MathObjects, got " + (1+b.length));
			if(a instanceof MVector) 
				if(b[0] instanceof MVector)
					return ((MVector)a.copy()).add((MVector) b[0]);
				else throw new InvalidOperationException("Only other vectors can be added to vectors. You're trying to add " + b[0].getClass() + " to a vector.");
			else if(a instanceof MScalar)
				if(b[0] instanceof MScalar)
					return ((MScalar)a.copy()).add((MScalar) b[0]);
				else throw new InvalidOperationException("Only other scalars can be added to scalars. You're trying to add " + b[0].getClass() + " to a scalar.");
			throw new InvalidOperationException("ADD operator is not defined for " + a.getClass() + " and " + b[0].getClass());
		}
	},
	
	SUBTRACT {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if(b.length != 1) throw new IllegalArgumentException("You can only substract exactly two MathObjects, got " + (1+b.length));
			if(a instanceof MVector) 
				if(b[0] instanceof MVector)
					return ((MVector)a.copy()).subtract((MVector) b[0]);
				else throw new InvalidOperationException("Only other vectors can be subtracted off vectors. You're trying to subtract " + b[0].getClass() + " off a vector.");
			else if(a instanceof MScalar)
				if(b[0] instanceof MScalar)
					return ((MScalar)a.copy()).subtract((MScalar) b[0]);
				else throw new InvalidOperationException("Only other scalars can be subtracted off scalars. You're trying to subtract " + b[0].getClass() + " off a scalar.");
			throw new InvalidOperationException("SUBTRACT operator is not defined for " + a.getClass() + " and " + b[0].getClass());
		}
	},
	
	MULTIPLY {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if(b.length != 1) throw new IllegalArgumentException("You can only multiply exactly two MathObjects, got " + (1+b.length));
			if(a instanceof MVector) 
				if(b[0] instanceof MVector)
					return ((MVector) a).dot((MVector) b[0]); //copy is not needed, because MVector.dot() doesn't change the MVector.
				if(b[0] instanceof MScalar)
					return ((MVector) a.copy()).multiply((MScalar) b[0]);
			else if(a instanceof MScalar)
				if(b[0] instanceof MScalar)
					return ((MScalar)a.copy()).multiply((MScalar) b[0]);
				if(b[0] instanceof MVector)
					return ((MVector) b[0].copy()).multiply((MScalar) a);
			throw new InvalidOperationException("MULTIPLY operator is not defined for " + a.getClass() + " and " + b[0].getClass());
		}
	},
	
	DIVIDE {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if(b.length != 1) throw new IllegalArgumentException("You can only divide exactly two MathObjects, got " + (1+b.length));
			if(a instanceof MVector) 
				if(b[0] instanceof MScalar)
					return ((MVector) a.copy()).divide((MScalar) b[0]);
			else if(a instanceof MScalar)
				if(b[0] instanceof MScalar)
					return ((MScalar)a.copy()).divide((MScalar) b[0]);
			throw new InvalidOperationException("MULTIPLY operator is not defined for " + a.getClass() + " and " + b[0].getClass());
		}
	},
	
	//###### single argument operators #######
	INVERT {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if(b.length != 0) throw new IllegalArgumentException("You can only invert exactly one MathObject, got " + (1+b.length));
			return a.copy().invert();
		}
	},
	
	NEGATE {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if(b.length != 0) throw new IllegalArgumentException("You can only negate exactly one MathObject, got " + (1+b.length));
			return a.copy().negate();
		}
	};
	
	/**
	 * stuff
	 * @param a
	 * @param b
	 * @return
	 */
	public abstract MathObject evaluate(MathObject a, MathObject... b);
}
