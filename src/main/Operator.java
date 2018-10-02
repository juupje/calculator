package main;

import helpers.exceptions.InvalidOperationException;
import mathobjects.MExpression;
import mathobjects.MMatrix;
import mathobjects.MScalar;
import mathobjects.MVector;
import mathobjects.MathObject;

public enum Operator {
	// ###### double argument operators #######
	/**
	 * Contains a method overriding
	 * {@link Operator#evaluate(MathObject, MathObject...) which adds two
	 * MathObjects: a and b[0].
	 * 
	 * @see Operator#ADD.evaluate(MathObject, MathObject...)
	 */
	ADD {
		/**
		 * {@inheritDoc} Overrides {@link Operator#evaluate(MathObject, MathObject...)}.
		 * Note that neither a not b will be changed in this process.
		 * 
		 * @param a the {@code MathObject} to which b[0] should be added.
		 * @param b a list of {@code MathObject}, expected to have length 1.
		 * @return a+b[0]
		 */
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if (b.length != 1)
				throw new IllegalArgumentException("You can only add exactly two MathObjects, got " + (1 + b.length));
			if (a == null)
				return b[0];
			if (b[0] == null)
				return a;
			if (a instanceof MExpression) {
				if (b[0] instanceof MExpression)
					return ((MExpression) a.copy()).add((MExpression) b[0]);
				else if (b[0] instanceof MScalar)
					return ((MExpression) a.copy()).add((MScalar) b[0]);
			}
			if (a instanceof MVector) {
				if (b[0] instanceof MVector)
					return ((MVector) a.copy()).add((MVector) b[0]);
				else
					throw new InvalidOperationException(
							"Only other vectors can be added to vectors. You're trying to add " + b[0].getClass()
									+ " to a vector.");
			} else if (a instanceof MScalar) {
				if (b[0] instanceof MScalar)
					return ((MScalar) a.copy()).add((MScalar) b[0]);
				else if (b[0] instanceof MExpression)
					return ((MExpression) a.copy()).add((MScalar) b[0]);
				else
					throw new InvalidOperationException(
							"Only other scalars can be added to scalars. You're trying to add " + b[0].getClass()
									+ " to a scalar.");
			} else if (a instanceof MMatrix) {
				if (b[0] instanceof MMatrix)
					return ((MMatrix) a.copy()).add((MMatrix) b[0]);
				else
					throw new InvalidOperationException(
							"Only other matrices can be added to matrices. You're trying to add " + b[0].getClass()
									+ " to a matrix.");
			}
			throw new InvalidOperationException(
					"ADD operator is not defined for " + a.getClass() + " and " + b[0].getClass());
		}
	},

	SUBTRACT {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if (b.length != 1)
				throw new IllegalArgumentException(
						"You can only substract exactly two MathObjects, got " + (1 + b.length));
			if (a instanceof MVector) {
				if (b[0] instanceof MVector)
					return ((MVector) a.copy()).subtract((MVector) b[0]);
				else
					throw new InvalidOperationException(
							"Only other vectors can be subtracted off vectors. You're trying to subtract "
									+ b[0].getClass() + " off a vector.");
			} else if (a instanceof MScalar) {
				if (b[0] instanceof MScalar)
					return ((MScalar) a.copy()).subtract((MScalar) b[0]);
				else
					throw new InvalidOperationException(
							"Only other scalars can be subtracted off scalars. You're trying to subtract "
									+ b[0].getClass() + " off a scalar.");
			} else if (a instanceof MMatrix) {
				if (b[0] instanceof MMatrix)
					return ((MMatrix) a.copy()).subtract((MMatrix) b[0]);
				else
					throw new InvalidOperationException(
							"Only other matrices can be subtracted off matrices. You're trying to subtract "
									+ b[0].getClass() + " off a matrix.");
			}
			throw new InvalidOperationException(
					"SUBTRACT operator is not defined for " + a.getClass() + " and " + b[0].getClass());
		}
	},

	MULTIPLY {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if (b.length != 1)
				throw new IllegalArgumentException(
						"You can only multiply exactly two MathObjects, got " + (1 + b.length));
			if (a instanceof MVector) {
				if (b[0] instanceof MVector)
					return ((MVector) a).dot((MVector) b[0]); // copy is not needed, because MVector.dot() doesn't
																// change the MVector.
				if (b[0] instanceof MScalar)
					return ((MVector) a.copy()).multiply((MScalar) b[0]);
				if (b[0] instanceof MMatrix)
					return ((MMatrix) b[0].copy()).multiply((MVector) a);
			} else if (a instanceof MScalar) {
				if (b[0] instanceof MScalar)
					return ((MScalar) a.copy()).multiply((MScalar) b[0]);
				if (b[0] instanceof MVector)
					return ((MVector) b[0].copy()).multiply((MScalar) a);
				if (b[0] instanceof MMatrix)
					return ((MMatrix) b[0].copy()).multiply((MScalar) a);
			} else if (a instanceof MMatrix) {
				if (b[0] instanceof MScalar)
					return ((MMatrix) a.copy()).multiply((MScalar) b[0]);
				if (b[0] instanceof MVector)
					return ((MMatrix) a).multiply((MVector) b[0]);
				if (b[0] instanceof MMatrix)
					return ((MMatrix) a).multiply((MMatrix) b[0]);
			}
			throw new InvalidOperationException(
					"MULTIPLY operator is not defined for " + a.getClass() + " and " + b[0].getClass());
		}
	},

	DIVIDE {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if (b.length != 1)
				throw new IllegalArgumentException(
						"You can only divide exactly two MathObjects, got " + (1 + b.length));
			if (a instanceof MVector) {
				if (b[0] instanceof MScalar)
					return ((MVector) a.copy()).divide((MScalar) b[0]);
			} else if (a instanceof MScalar) {
				if (b[0] instanceof MScalar)
					return ((MScalar) a.copy()).divide((MScalar) b[0]);
			} else if (a instanceof MMatrix) {
				if (b[0] instanceof MScalar)
					return ((MMatrix) a.copy()).divide((MScalar) b[0]);
			}
			throw new InvalidOperationException(
					"DIVIDE operator is not defined for " + a.getClass() + " and " + b[0].getClass());
		}
	},

	POWER {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if (b.length != 1)
				throw new IllegalArgumentException("You can only raise one MathObject to the power of one other, got "
						+ (1 + b.length) + " arguments, expected 2");
			if (a instanceof MVector) {
				if (b[0] instanceof MScalar)
					return ((MVector) a.copy()).power((MScalar) b[0]);
			} else if (a instanceof MScalar) {
				if (b[0] instanceof MScalar)
					return ((MScalar) a.copy()).power((MScalar) b[0]);
			} else if (a instanceof MMatrix) {
				if (b[0] instanceof MScalar)
					if (((MScalar) b[0]).getValue() == -1)
						return ((MMatrix) a.copy()).invert();
				// return ((MMatrix) a.copy()).power((MScalar) b[0]);
			}
			throw new InvalidOperationException(
					"POWER operator is not defined for " + a.getClass() + " and " + b[0].getClass());
		}
	},

	MOD {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if (b.length != 1)
				throw new IllegalArgumentException(
						"You can only convert one MathObject to the modulo of one other, got " + (1 + b.length)
								+ " arguments, expected 2");
			if (a instanceof MScalar) {
				if (b[0] instanceof MScalar)
					return ((MScalar) a.copy()).mod((MScalar) b[0]);
			}
			throw new InvalidOperationException(
					"MODULO operator is not defined for " + a.getClass() + " and " + b[0].getClass());
		}
	},

	// ###### single argument operators #######
	INVERT {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if (b.length != 0)
				throw new IllegalArgumentException("You can only invert exactly one MathObject, got " + (1 + b.length));
			return a.copy().invert();
		}
	},

	NEGATE {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if (b.length != 0)
				throw new IllegalArgumentException("You can only negate exactly one MathObject, got " + (1 + b.length));
			return a.copy().negate();
		}
	},

	ELEMENT {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if (a instanceof MVector) {
				if (b.length != 1)
					throw new IllegalArgumentException("A vector only has 1 index, got " + (b.length));
				if (b[0] instanceof MScalar)
					return ((MVector) a).get((int) ((MScalar) b[0]).getValue());
				else
					throw new IllegalArgumentException(
							"Vector index needs to be an (integer) scalar value, got " + b[0].toString());
			} else if (a instanceof MMatrix) {
				if (b.length == 1) {
					if (b[0] instanceof MScalar)
						return ((MMatrix) a).getRow((int) ((MScalar) b[0]).getValue());
					else
						throw new IllegalArgumentException(
								"Matrix index needs to be an (integer) scalar value, got " + b[0].toString());
				} else if (b.length == 2) {
					if (b[0] == null && b[1] instanceof MScalar)
						return ((MMatrix) a).getColumn((int) ((MScalar) b[1]).getValue());
					else if (b[0] instanceof MScalar && b[1] instanceof MScalar)
						return ((MMatrix) a).get((int) ((MScalar) b[0]).getValue(), (int) ((MScalar) b[1]).getValue());
					else
						throw new IllegalArgumentException(
								"Matrix indices need to be one or two (integer) scalar value(s) (first one may be null/empty), got "
										+ (b[0] == null ? "null" : b[0].getClass()) + " and " + b[1].getClass());
				}
			}
			throw new IllegalArgumentException(
					"Only matrices and vectors have indexed components, got " + a.getClass());
		}
	};

	public abstract MathObject evaluate(MathObject a, MathObject... b);
}
