package com.github.juupje.calculator.main;

import java.util.ArrayList;
import java.util.function.Function;

import com.github.juupje.calculator.algorithms.algebra.range.ArrayRange;
import com.github.juupje.calculator.algorithms.algebra.range.IndexRange;
import com.github.juupje.calculator.algorithms.algebra.range.RangeIterator;
import com.github.juupje.calculator.algorithms.algebra.range.SimpleRange;
import com.github.juupje.calculator.algorithms.linalg.LUDecomposition;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.IndexException;
import com.github.juupje.calculator.helpers.exceptions.InvalidOperationException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.mathobjects.MExpression;
import com.github.juupje.calculator.mathobjects.MFunction;
import com.github.juupje.calculator.mathobjects.MIndexable;
import com.github.juupje.calculator.mathobjects.MIndexedObject;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;
import com.github.juupje.calculator.mathobjects.MScalar;
import com.github.juupje.calculator.mathobjects.MSequence;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;
import com.github.juupje.calculator.tree.Node;
import com.github.juupje.calculator.tree.Tree;

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
		 * @ 
		 */
		@Override
		public MathObject evaluate(MathObject a, MathObject... b)  {
			if (b.length != 1)
				throw new IllegalArgumentException("You can only add exactly two MathObjects, got " + (1 + b.length));
			if (a == null)
				return b[0];
			if (b[0] == null)
				return a;
		
			if(a instanceof MExpression || b[0] instanceof MExpression)
				return applyOnExpression(a, b[0], this);
			else if (a instanceof MVector) {
				if (b[0] instanceof MVector)
					return ((MVector) a.copy()).add((MVector) b[0]);
				else
					throw new InvalidOperationException(
							"Only other vectors can be added to vectors. You're trying to add " + Tools.type(b[0])
									+ " to a vector.");
			} else if (a instanceof MScalar) {
				if (b[0] instanceof MScalar)
					return MScalar.add((MScalar)a, (MScalar)b[0]);
				else
					throw new InvalidOperationException(
							"Only other scalars can be added to scalars. You're trying to add " + Tools.type(b[0])
									+ " to a scalar.");
			} else if (a instanceof MMatrix) {
				if (b[0] instanceof MMatrix)
					return ((MMatrix) a.copy()).add((MMatrix) b[0]);
				else
					throw new InvalidOperationException(
							"Only other matrices can be added to matrices. You're trying to add " + Tools.type(b[0])
									+ " to a matrix.");
			}
			throw new InvalidOperationException(
					"ADD operator is not defined for " + Tools.type(a) + " and " + Tools.type(b[0]));
		}
		
		@Override
		public Shape shape(Shape a, Shape... b)  {
			if(b.length==1)
				return Shape.add(a, b[0]);
			if(b.length==0)
				return a;
			throw new InvalidOperationException("Can only add two mathobjects, got " + (b.length+1));
		}
	},

	SUBTRACT {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b)  {
			if (b.length != 1)
				throw new IllegalArgumentException(
						"You can only substract exactly two MathObjects, got " + (1 + b.length));
			if(a instanceof MExpression || b[0] instanceof MExpression)
				return applyOnExpression(a, b[0], this);
			else if (a instanceof MVector) {
				if (b[0] instanceof MVector)
					return ((MVector) a.copy()).subtract((MVector) b[0]);
				else
					throw new InvalidOperationException(
							"Only other vectors can be subtracted off vectors. You're trying to subtract "
									+ Tools.type(b[0]) + " off a vector.");
			} else if (a instanceof MScalar) {
				if (b[0] instanceof MScalar)
					return MScalar.subtract((MScalar)a, (MScalar)b[0]);
				else
					throw new InvalidOperationException(
							"Only other scalars can be subtracted off scalars. You're trying to subtract "
									+ Tools.type(b[0]) + " off a scalar.");
			} else if (a instanceof MMatrix) {
				if (b[0] instanceof MMatrix)
					return ((MMatrix) a.copy()).subtract((MMatrix) b[0]);
				else
					throw new InvalidOperationException(
							"Only other matrices can be subtracted off matrices. You're trying to subtract "
									+ Tools.type(b[0]) + " off a matrix.");
			}
			throw new InvalidOperationException(
					"SUBTRACT operator is not defined for " + Tools.type(a) + " and " + Tools.type(b[0]));
		}
		
		@Override
		public Shape shape(Shape a, Shape... b)  {
			if(b.length==1)
				return Shape.subtract(a, b[0]);
			if(b.length==0)
				return a;
			throw new InvalidOperationException("Can only subtract two mathobjects, got " + (b.length+1));
		}
	},

	MULTIPLY {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b)  {
			if (b.length != 1)
				throw new IllegalArgumentException(
						"You can only multiply exactly two MathObjects, got " + (1 + b.length));
			if(a instanceof MScalar && b[0] instanceof MScalar) {
				return MScalar.multiply((MScalar) a, (MScalar)b[0]);
			}
			if(a instanceof MScalar)
				return b[0].copy().multiply((MScalar) a);
			if(b[0] instanceof MScalar)
				return a.copy().multiply((MScalar) b[0]);
			if(a instanceof MExpression || b[0] instanceof MExpression)
				return applyOnExpression(a, b[0], this);
			else if (a instanceof MVector) {
				if (b[0] instanceof MVector)
					return ((MVector) a).multiply((MVector) b[0]); // copy is not needed, because MVector.multiply(MVector) doesn't
																// change either of the MVectors.
				if (b[0] instanceof MMatrix)
					return ((MMatrix) b[0].copy()).multiplyRight((MVector) a);
			} else if (a instanceof MMatrix) {
				if (b[0] instanceof MVector)
					return ((MMatrix) a).multiplyLeft((MVector) b[0]);
				if (b[0] instanceof MMatrix)
					return ((MMatrix) a).multiplyLeft((MMatrix) b[0]);
			}
			throw new InvalidOperationException(
					"MULTIPLY operator is not defined for " + Tools.type(a) + " and " + Tools.type(b[0]));
		}
		
		@Override
		public Shape shape(Shape a, Shape... b)  {
			if(b.length==1)
				return Shape.multiply(a, b[0]);
			throw new InvalidOperationException("Can only multiply two mathobjects, got " + (b.length+1));
		}
	},
	
	CROSS {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b)  {
			if (b.length != 1)
				throw new IllegalArgumentException(
						"You can only cross multiply exactly two vectors, got " + (1 + b.length));
			if (a instanceof MVector && b[0] instanceof MVector) {
				return ((MVector) a).cross((MVector) b[0]);
			} else if(a instanceof MExpression || b[0] instanceof MExpression)
				return applyOnExpression(a, b[0], this);
			throw new InvalidOperationException(
					"CROSS operator is not defined for " + Tools.type(a) + " and " + Tools.type(b[0]));
		}
		
		@Override
		public Shape shape(Shape a, Shape... b)  {
			if(b.length==1) {
				if(a.dim()==1 && a.equals(b[0]) && (a.get(0) == 3 || a.get(0) == 2))
					return a.copy();
				else
					throw new ShapeException("Cross product is only defined for vector shaped object of length 2 or 3, got " + a + " and " + b);
			}
			throw new InvalidOperationException("Can only cross multiply two objects, got " + (b.length+1));
		}
	},

	DIVIDE {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if (b.length != 1)
				throw new IllegalArgumentException(
						"You can only divide exactly two MathObjects, got " + (1 + b.length));
			if(b[0] instanceof MScalar) {
				if(a instanceof MScalar)
					return MScalar.divide((MScalar) a, (MScalar)b[0]);
				return a.copy().multiply(((MScalar) b[0]).invert());
			} else if(a instanceof MExpression || b[0] instanceof MExpression)
				return applyOnExpression(a, b[0], this);
			throw new InvalidOperationException(
					"DIVIDE operator is not defined for " + Tools.type(a) + " and " + Tools.type(b[0]));
		}
		
		@Override
		public Shape shape(Shape a, Shape... b)  {
			if(b.length==1)
				return Shape.divide(a, b[0]);
			throw new InvalidOperationException("Can only divide two mathobjects, got " + (b.length+1));
		}
	},

	POWER {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if (b.length != 1)
				throw new IllegalArgumentException("You can only raise one MathObject to the power of one other, got "
						+ (1 + b.length) + " arguments, expected 2");
			if (a instanceof MScalar) {
				if (b[0] instanceof MScalar)
					return MScalar.power((MScalar)a, (MScalar)b[0]);
			} else if (a instanceof MMatrix) {
				if (b[0] instanceof MReal) {
					if (((MReal) b[0]).getValue() == -1)
						return ((MMatrix) a.copy()).invert();
					else if(((MReal) b[0]).isInteger())
						return ((MMatrix) a).pow((int) ((MReal) b[0]).getValue());
					else
						throw new InvalidOperationException("Matrices can only be raised to integer powers, got " + b[0]);
				}
			} else if(a instanceof MExpression || b[0] instanceof MExpression)
				return applyOnExpression(a, b[0], this);
			throw new InvalidOperationException(
					"POWER operator is not defined for " + Tools.type(a) + " and " + Tools.type(b[0]));
		}
		
		@Override
		public Shape shape(Shape a, Shape... b)  {
			if(b.length==1)
				return Shape.power(a, b[0]);
			throw new InvalidOperationException("Can only raise one mathobject to the power of one other, got " + (b.length+1));
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
				if (b[0] instanceof MReal)
					return ((MReal) a.copy()).mod((MReal) b[0]);
			}
			throw new InvalidOperationException(
					"MODULO operator is not defined for " + Tools.type(a) + " and " + Tools.type(b[0]));
		}
		
		@Override
		public Shape shape(Shape a, Shape... b)  {
			if(b.length==1)
				return Shape.mod(a, b[0]);
			throw new InvalidOperationException("Modulo operation requires one object and one operant, got " + b.length + " operants.");
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
		
		@Override
		public Shape shape(Shape a, Shape... b)  {
			if(b.length==0)
				return Shape.invert(a);
			throw new InvalidOperationException("Can only invert one mathobject to  got " + (b.length+1));
		}
	},

	NEGATE {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if (b.length != 0)
				throw new IllegalArgumentException("You can only negate exactly one MathObject, got " + (1 + b.length));
			return a.copy().negate();
		}
		
		@Override
		public Shape shape(Shape a, Shape... b)  {
			if(b.length==1)
				return Shape.negate(a);
			throw new InvalidOperationException("Can only raise one mathobject to the power of one other, got " + (b.length+1));
		}
	},

	TRANSPOSE {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if(b.length!=0)
				throw new InvalidOperationException("Can transpose only one mathobject, got " + (b.length+1));
			if(a instanceof MMatrix)
				return ((MMatrix) a.copy()).transpose();
			else if(a instanceof MVector)
				return ((MVector) a.copy()).transpose();
			else if(a instanceof MExpression || b[0] instanceof MExpression)
				return applyOnExpression(a, null, this);
			throw new InvalidOperationException("Can't transpose object " + Tools.type(a));
		}

		@Override
		public Shape shape(Shape a, Shape... b) {
			if(b.length==0)
			return a.transpose(); //doesn't change a
		throw new InvalidOperationException("Can only transpose one mathobject, got " + (b.length+1));
		}
	},
	
	CONJUGATE {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if(b.length!=0)
				throw new InvalidOperationException("Can conjugate only one mathobject, got " + (b.length+1));
			if(a instanceof MMatrix || a instanceof MVector) {
				Function<MathObject, MathObject> f = new Function<MathObject, MathObject>() {
					@Override
					public MathObject apply(MathObject mo) {
						return Operator.CONJUGATE.evaluate(mo);
					}
				};
				return a instanceof MMatrix ? ((MMatrix) a).forEach(f) : ((MVector) a).forEach(f);
			} else if(a instanceof MScalar)
				return ((MScalar) a).copy().conjugate();
			else if(a instanceof MExpression || b[0] instanceof MExpression)
				return applyOnExpression(a, null, this);
			throw new InvalidOperationException("Can't conjugate object " + Tools.type(a));
		}

		@Override
		public Shape shape(Shape a, Shape... b) {
			if(b.length==0)
			return a;
		throw new InvalidOperationException("Can only transpose one mathobject, got " + (b.length+1));
		}
	},
	
	SOLVE {
		@Override
		public MVector evaluate(MathObject a, MathObject... b) {
			if(b.length!=1)
  				throw new InvalidOperationException("Solve operator expected two arguments, got " + (b.length+1));
			if(a instanceof MMatrix && b[0] instanceof MVector) {
				return new LUDecomposition((MMatrix) a).solve((MVector) b[0]);
			} else
				throw new InvalidOperationException("Solve operator expected arguments matrix and vector, got " + Tools.type(a) + ", " + Tools.type(b[0]));				
		}
		
		@Override
		public Shape shape(Shape a, Shape... b) {
			if(b.length!=1)
				throw new InvalidOperationException("Solve operator expected two arguments, got " + (b.length+1));
			if(a.dim()!=2)
				throw new InvalidOperationException("Solve operator expected left-hand side to be a matrix, got shape " + a);
			if(b[0].dim()!=1)
 				throw new InvalidOperationException("Solve operator expected right-hand side to be a vector, got shape " + b[0]);
			if(a.get(1)!=b[0].get(0))
				throw new InvalidOperationException("Shapes do not match: " + a + " and " + b[0]);
			return new Shape(a.get(0));
		}
	},
	
	ELEMENT {
		@Override
		public MathObject evaluate(MathObject a, MathObject... b) {
			if (a instanceof MVector) {
				MVector v = (MVector) a;
				if (b.length != 1)
					throw new IndexException("A vector only has 1 index, got " + (b.length));
				if (MReal.isPosInteger(b[0]))
					return v.get((int) ((MReal) b[0]).getValue());
				else if(b[0] instanceof MVector) { //b[0] is a slice
					MVector slice = (MVector) b[0];
					int[] beginEnd = getBeginEnd(slice);
					MathObject[] result = new MathObject[beginEnd[1]-beginEnd[0]];
					for(int k = 0, j = beginEnd[0]; j < beginEnd[1]; j++, k++)
						result[k] = v.get(j);
					return new MVector(result);
				} else
					throw new IndexException(
							"Vector index needs to be a positive integer scalar value or a slice, got " + b[0].toString());
			} else if (a instanceof MSequence) {
				if (b.length != 1)
					throw new IndexException("A sequence only has 1 index, got " + (b.length));
				if (MReal.isPosInteger(b[0]))
					return ((MSequence) a).get((int) ((MReal) b[0]).getValue());
				else if(b[0] instanceof MVector) {
					MVector slice = (MVector) b[0];
					int[] beginEnd = getBeginEnd(slice);
					if(beginEnd[1] == Integer.MAX_VALUE)
						throw new IndexException("Cannot create infinite slice.");
					MathObject[] result = new MathObject[beginEnd[1]-beginEnd[0]];
					MSequence s = (MSequence) a;
					for(int k = 0, j = beginEnd[0]; j < beginEnd[1]; j++, k++)
						result[k] = s.get(j);
					return new MVector(result);
				} else
					throw new IndexException(
							"Index needs to be a positive integer scalar value or a slice, got " + b[0].toString());
			} else if (a instanceof MMatrix) {
				if (b.length == 1) {
					if (MReal.isPosInteger(b[0]))
						return ((MMatrix) a).getRow((int) ((MReal) b[0]).getValue());
					else if(b[0] instanceof MVector && ((MVector) b[0]).size()==2) {
						MathObject c = ((MVector) b[0]).get(0);
						MathObject d = ((MVector) b[0]).get(1);
						if(c instanceof MReal && ((MReal) c).isPosInteger() && d instanceof MReal && ((MReal) d).isPosInteger())
							return ((MMatrix) a).get((int) ((MReal) c).getValue(), (int) ((MReal) d).getValue());
						else
							throw new IndexException("Matrix index needs to contain integer values, got " + b[0]);
					}
					else
						throw new IndexException(
								"Matrix index needs to be an (integer) scalar value or a size 2 vector, got " + b[0].toString());
				} else if (b.length == 2) {
					if (b[0] == null && MReal.isPosInteger(b[1]))
						return ((MMatrix) a).getColumn((int) ((MReal) b[1]).getValue());
					else if (MReal.isPosInteger(b[0])) {
						if(MReal.isPosInteger(b[1]))
							return ((MMatrix) a).get((int) ((MReal) b[0]).getValue(), (int) ((MReal) b[1]).getValue());
						else if(b[1] instanceof MVector) {
							MVector slice = (MVector) b[1];
							int[] beginEnd = getBeginEnd(slice);
							int row = (int) ((MReal) b[0]).getValue();
							MathObject[] result = new MathObject[beginEnd[1]-beginEnd[0]];
							MMatrix m = (MMatrix) a;
							for(int k = 0, j = beginEnd[0]; j < beginEnd[1]; j++, k++)
								result[k] = m.get(row, j);
							return new MVector(result).transpose();//this is a row vector
						} else
							throw new IndexException("Expected 2nd argument to be positive integer or vector/slice, got " + b[1]);
					} else if(b[0] instanceof MVector) {
						int[] beginEnd = getBeginEnd((MVector) b[0]);
						MMatrix m = (MMatrix) a;
						if(MReal.isPosInteger(b[1])) {
							int col = (int) ((MReal) b[1]).getValue();
							MathObject[] result = new MathObject[beginEnd[1]-beginEnd[0]];
							for(int k = 0, j = beginEnd[0]; j < beginEnd[1]; j++, k++)
								result[k] = m.get(j, col);
							return new MVector(result).transpose();	
						} else if(b[1] instanceof MVector) {
							int[] beginEndCol = getBeginEnd((MVector) b[1]);
							MathObject[][] result = new MathObject[beginEnd[1]-beginEnd[0]][beginEndCol[1]-beginEndCol[0]];
							for(int k = 0, i = beginEnd[0]; i < beginEnd[1]; i++, k++)
								for(int l = 0, j = beginEndCol[0]; j < beginEndCol[1]; l++, j++)
									result[k][l] = m.get(i,j);
							return new MMatrix(result);
						} else {
							throw new IndexException("Expected second index to be positive integer or vector/slice, got " + b[1]);
						}
					} else 
						throw new IllegalArgumentException(
								"Unexpected index arguments, see the help for syntax.");
				}
			}else if(a instanceof MExpression || b[0] instanceof MExpression) {
				return applyOnExpression(a, b[0], this);
			} else if(a instanceof MIndexable) {
				MIndexable indexable = (MIndexable) a;
				//Check if each index is a single value
				boolean containsSlice = false;
				if(b.length==a.shape().size()) {
					for(MathObject obj : b)
						if(!(obj instanceof MReal)) {
							containsSlice = true;
							break;
						}
				} else
					containsSlice = true;
				if(!containsSlice) {
					//If the result should be a single value (so all indices are single/scalar values)
					//Put them in a list and use get() method from MIndexable
					int[] indices = new int[b.length];
					for(int i = 0; i < b.length; i++) {
						if(((MReal) b[i]).isPosInteger())
							indices[i] = (int) ((MReal) b[i]).getValue();
						else
							throw new IndexException("Index value needs to be a positive integer, got " + b[i]);
					}
					return indexable.get(indices);
				} else {
					//The indices contains one or more slices
					//Find the ranges of those slices
					IndexRange[] ranges = new IndexRange[a.shape().size()];
					ArrayList<Integer> sizes = new ArrayList<>(); //to save the shape of the resulting object
					for(int i = 0; i < b.length; i++) {
						if(b[i] instanceof MReal) {
							if(((MReal) b[i]).isPosInteger()) {
								ranges[i] = new ArrayRange(new int[] {(int) ((MReal) b[i]).getValue()});
							} else
								throw new IndexException("Index value needs to be a positive integer, got " + b[i]);
						} else if(b[i] instanceof MVector) {
							int[] slice = getBeginEnd((MVector) b[i]);
							ranges[i] = new SimpleRange(slice[0], slice[1]-1);
							sizes.add(slice[1]-slice[0]);
						}
					}
					//create an range iterator
					RangeIterator iter = new RangeIterator(ranges);
					//Convert sizes to an array (in order to be passed to the constructor of Shape())
					int[] arr_sizes = new int[sizes.size()];
					int len = 1;
					for(int i = 0; i < sizes.size(); i++) {
						len *= sizes.get(i);
						arr_sizes[i] = sizes.get(i);
					}
					Shape shape = new Shape(arr_sizes);
					//Save the selected elements to a new MIndexedObject
					MathObject[] result = new MathObject[len];
					int i = 0;
					while(true) {
						result[i++] = indexable.get(iter.getIndices());
						if(!iter.next())
							break;
					}
					return new MIndexedObject(shape, result);
				}
				
			}
			throw new IllegalArgumentException(
					"Object of type " + Tools.type(a) + " is not indexable");
		}
		
		private int[] getBeginEnd(MVector slice) {
			if(slice.size()!=2)
				throw new IndexException("Expected slice to have 2 values, got " + slice.size());
			if(!MReal.isPosInteger(slice.get(0)) || !MReal.isPosInteger(slice.get(1)))
				throw new IndexException("Expected slice to have 2 positive integers, got " + slice);
			return new int[] {(int)((MReal)slice.get(0)).getValue(), (int) ((MReal)slice.get(1)).getValue()};
		}
		
		@Override
		/**
		 * If it works, it works. Don't touch it.
		 */
		public Shape shape(Shape a, Shape... b) {
			if(b.length==0)
				throw new IllegalArgumentException("No index argument found.");
			if(a.equals(null))
				throw new InvalidOperationException("Object of shape " + a + " has no indexed components.");
			if(b.length > a.dim())
				throw new IllegalArgumentException("Got " + b.length +"D index for a " + a.dim() + "D shape.");
			ArrayList<Integer> nullIndices = new ArrayList<>();
			if(b.length > 1) {
				for(int i = 0; i < b.length; i++) {
					if(b[i] == null)
						nullIndices.add(i);
					else if(!b[i].equals(null))
						throw new IllegalArgumentException("If multiple indices are given, they all must be either null or a scalar value.");
				}
				int[] shape = new int[nullIndices.size()];
				for(int i = 0; i < shape.length; i++)
					shape[i] = a.get(nullIndices.get(i));
				return new Shape(shape);
			} else { //b==1
				if(b[0].dim() > 1)
					throw new IllegalArgumentException("Only a vector or scalar shaped object can point to an index. Got shape: " + b[0]);
				int indexCount = b[0].dim()==0 ? 1 : b[0].get(0); //if b is a scalar, there is only one index. If b is a vector of shape (n), there are n indices.
				if(indexCount>a.dim())
					throw new IllegalArgumentException("Got " + b.length +"D index for a " + a.dim() + "D shape.");
				int[] shape = new int[a.dim()-indexCount];
				for(int i = indexCount; i < a.dim(); i++)
					shape[i] = a.get(i);
				return new Shape(shape);
			}
		}
	};

	public abstract MathObject evaluate(MathObject a, MathObject... b) ;
	public abstract Shape shape(Shape a, Shape... b) ;
	
	private static MExpression applyOnExpression(MathObject a, MathObject b, Operator op) {
		if(a instanceof MExpression) {
			if(b == null) {
				Tree tr = ((MExpression) a.copy()).getTree();
				tr.insert(tr.getRoot(), new Node<Operator>(op), Node.LEFT);
			} else if(b instanceof MFunction) {
				throw new InvalidOperationException("Can't add expression to function.");
			} else if(b instanceof MExpression)
				return ((MExpression) a.copy()).addOperation(op, (MExpression) b);
			else
				return ((MExpression) a.copy()).addOperation(op, b);
		} else if(b instanceof MExpression)
			return ((MExpression) b.copy()).addOperationRight(op, a);
		throw new IllegalArgumentException("Something went wrong while processing and operator...");
	}
}
