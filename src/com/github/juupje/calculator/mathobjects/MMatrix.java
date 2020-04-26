package com.github.juupje.calculator.mathobjects;

import java.util.List;
import java.util.function.Function;

import com.github.juupje.calculator.algorithms.linalg.JordanElimination;
import com.github.juupje.calculator.algorithms.linalg.LUDecomposition;
import com.github.juupje.calculator.helpers.Printer;
import com.github.juupje.calculator.helpers.exceptions.IndexException;
import com.github.juupje.calculator.helpers.exceptions.InvalidOperationException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.main.Operator;

public class MMatrix implements MathObject {

	MathObject m[][];
	Shape shape;

	public MMatrix(MathObject[][] m) {
		this.m = m;
		shape = new Shape(m.length, m[0].length);
	}

	public MMatrix(double[][] m) {
		this.m = new MathObject[m.length][m[0].length];
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[i].length; j++)
				this.m[i][j] = new MReal(m[i][j]);
		shape = new Shape(m.length, m[0].length);
	}
	
	public MMatrix(int[][] m) {
		this.m = new MathObject[m.length][m[0].length];
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[i].length; j++)
				this.m[i][j] = new MReal(m[i][j]);
		shape = new Shape(m.length, m[0].length);
	}
	
	public MMatrix(Double[][] m) {
		this.m = new MathObject[m.length][m[0].length];
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[i].length; j++)
				this.m[i][j] = new MReal(m[i][j]);
		shape = new Shape(m.length, m[0].length);
	}

	/**
	 * Constructs an empty (all entries are <tt>null</tt>) matrix with the given
	 * {@code Dimension}.
	 * 
	 * @param dim the {@link Dimension} of the matrix to be made.
	 */
	public MMatrix(Shape shape) {
		this.shape = shape;
		m = new MathObject[shape.get(0)][shape.get(1)];
	}

	/**
	 * Creates a matrix of the vectors in the list. Note that the result from this
	 * constructor will be a column vector of row vectors, in which the latter are
	 * the entries in the list. So, if the list is [v1, v2, v3], than the matrix
	 * will be: <br/>
	 * | v1[0] v1[1] v1[2] | <br/>
	 * | v2[0] v2[1] v2[2] | <br/>
	 * | v3[0] v3[1] v3[2] | <br/>
	 * 
	 * @param list a <tt>List</tt> of <tt>MVector</tt>s
	 */
	public MMatrix(List<MVector> list) {
		m = new MathObject[list.size()][];
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() - 1 && list.get(i).size() != list.get(i + 1).size())
				throw new IllegalArgumentException("Can't create matrix with non-constant row lengths!");
			m[i] = list.get(i).elements();
		}
		shape = new Shape(m.length, m[0].length);
	}

	/**
	 * Creates a matrix of the vectors in the array. Note that the result from this
	 * constructor will be a row vector of column vectors, in which the latter are
	 * the entries in the array. So, if the array is [v1, v2, v3], the matrix will
	 * be:<br/>
	 * | v1[0] v2[0] v3[0] | <br/>
	 * | v1[2] v2[1] v3[1] | <br/>
	 * | v1[2] v2[2] v3[2] | <br/>
	 * 
	 * @param v an array <tt>MVector[]</tt>
	 */
	public MMatrix(MVector[] v) {
		m = new MathObject[v[0].size()][v.length];
		for (int i = 0; i < v.length; i++) {
			if (i < v.length - 1 && v[i].size() != v[i + 1].size())
				throw new IllegalArgumentException("Can't create matrix with non-constant row lengths!");
			for (int j = 0; j < v[i].size(); j++)
				m[j][i] = v[i].get(j);
		}
		shape = new Shape(m.length, m[0].length);
	}

	/**
	 * Returns the component at the i-th row and j-th column of this matrix.
	 * 
	 * @param i the row of the component
	 * @param j the column of the component
	 * @return the component at [i,j] as a {@link MathObject}
	 */
	public MathObject get(int i, int j) {
		return m[i][j];
	}

	/**
	 * Sets the component at the i-th row and j-th column of the matrix to the given
	 * {@link MathObject}.
	 * 
	 * @param i  the row of the component
	 * @param j  the column of the component
	 * @param mo the <tt>MathObject</tt> to be set at [i,j]
	 */
	public void set(int i, int j, MathObject mo) {
		m[i][j] = mo;
	}

	/**
	 * Returns the i-th row of the matrix as a {@link MVector}.
	 * 
	 * @param i  index of the row requested
	 * @param mo a <tt>MVector</tt> containing the values in the i-th row.
	 */
	public MVector getRow(int i) {
		MathObject[] v = new MathObject[m[i].length];
		for (int j = 0; j < m[i].length; j++)
			v[j] = m[i][j];
		return new MVector(v);
	}

	/**
	 * Returns the j-th column of the matrix as a {@link MVector}.
	 * 
	 * @param j  index of the column requested
	 * @param mo a <tt>MVector</tt> containing the values in the j-th column.
	 */
	public MVector getColumn(int j) {
		MathObject[] v = new MathObject[m.length];
		for (int i = 0; i < m.length; i++)
			v[i] = m[i][j];
		return new MVector(v);
	}

	/**
	 * Returns the elements in this matrix.
	 * 
	 * @return a <tt>MathObject[][]</tt> of the elements in the matrix.
	 */
	public MathObject[][] elements() {
		return m;
	}
	
	/**
	 * Returns {@code true} if the matrix is square (that is, if {@code shape.cols()==shape.rows();}), or {@code false} otherwise. 
	 * @return {@code shape.cols()==shape.rows();}
	 */
	public boolean isSquare() {
		return shape.cols() == shape.rows();
	}

	/**
	 * extracts a submatrix from this matrix.
	 * The submatrix is a {@code MathObject} and is linked to this matrix: all changes in either of them will affect the other.
	 * @param startrow the first row of the submatrix (inclusive)
	 * @param endrow the last row of the submatrix (exclusive)
	 * @param startcol the first column of the submatrix (inclusive)
	 * @param endcol the last column of the submatrix (exclusive)
	 * @return A {@code MSubMatrix} defined as {@code A[startrow:endrow+1, startcol:endcol+1]} in a NumPy context.
	 */
	public MSubMatrix getSubMatrix(int startrow, int endrow, int startcol, int endcol) {
		return new MSubMatrix(new Slice(startrow, endrow, startcol, endcol));
	}
	
	public MMatrix extractSubMatrix(int lefttoprow, int lefttopcol, int rightbottomrow, int rightbottomcol) {
		if (lefttoprow < 0 || lefttopcol < 0 || rightbottomrow >= shape.rows() || rightbottomcol >= shape.cols())
			throw new IndexOutOfBoundsException("Bounds of submatrix out of bounds! (" + lefttoprow + ", " + lefttopcol
					+ ", " + rightbottomrow + ", " + rightbottomcol + ") does not fit inside matrix of shape " + shape);
		MMatrix result = new MMatrix(new Shape(rightbottomrow - lefttoprow + 1, rightbottomcol - lefttopcol + 1));
		for (int i = lefttoprow; i <= rightbottomrow; i++)
			for (int j = lefttopcol; j <= rightbottomcol; j++)
				result.set(i - lefttoprow, j - lefttopcol, m[i][j]);
		return result;
	}

	/**
	 * Adds another vector element-wise to this one. Note that this will change the
	 * vector on which this method is called. If that is not your wish, consider
	 * using {@link Operator#ADD}
	 * 
	 * @param other the {@code MVvector} to be added, this one will not be changed.
	 * @return {@code this} after the addition. 
	 * @throws ShapeException if the other matrix's shape does
	 * not equal this one's.
	 */
	public MMatrix add(MMatrix other) {
		if (!other.shape().equals(shape()))
			throw new ShapeException(
					"To add two matrices, they need to be the same shape! Shape: " + shape() + " and " + other.shape());
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[i].length; j++)
				m[i][j] = Operator.ADD.evaluate(m[i][j], other.get(i, j));
		return this;
	}

	/**
	 * Subtracts another vector element-wise from this one. Note that this will
	 * change the vector on which this method is called. If that is not your wish,
	 * consider using {@link Operator.SUBTRACT}
	 * 
	 * @param other the {@code MVvector} to be subtracted, this one will not be
	 *              changed.
	 * @return {@code this} after the subtraction. 
	 * @throws ShapeException if the other matrix's shape
	 * does not equal this one's.
	 */
	public MMatrix subtract(MMatrix other) {
		if (!other.shape().equals(shape()))
			throw new ShapeException("To subtract two matrices, they need to be the same shape! Shape: "
					+ shape() + " and " + other.shape());
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[i].length; j++)
				m[i][j] = Operator.SUBTRACT.evaluate(m[i][j], other.get(i, j));
		return this;
	}

	/**
	 * Multiplies this {@code MVector} element-wise with the value in the given
	 * {@code MScalar}
	 * 
	 * @param other the {@code MScalar} to be multiplied with.
	 * @return {@code this}
	 */
	public MMatrix multiply(MScalar other) {
		for (MathObject[] row : m)
			for (int i = 0; i < row.length; i++)
				row[i] = Operator.MULTIPLY.evaluate(row[i], other);
		return this;
	}

	/**
	 * Calls {@code multiply(new MScalar(d));} where d is the given double.
	 * 
	 * @param d the value to be passed as an {@code MScalar} to {@code multiply()}
	 * @return the result of the call: {@code this}.
	 * @see #multiply(MScalar)
	 */
	public MMatrix multiply(double d) {
		return multiply(new MReal(d));
	}

	/**
	 * Multiplies a given matrix from the left with this one, neither matrix will be
	 * changed. The matrix product of A (m x n) and B (n x k) is defined as:
	 * {@code C_ij = (AB)_ij = Sum(l=1 to n, A_il*B_lj), i=1,...,m  j=1,...,k}
	 * 
	 * @param other the matrix B, that will be multiplied with <tt>this</tt> (=A).
	 * @return the resulting (m x k)-matrix. @ if the columncount of A does not
	 * equal the rowcount of B.
	 */
	public MMatrix multiplyLeft(MMatrix other) {
		if (shape.cols() != other.shape().rows())
			throw new ShapeException("Matrix product is only defined for n x m and m x k Matrices. Shapes: " + shape()
					+ ", " + other.shape());
		MathObject[][] C = new MathObject[shape.rows()][other.shape().cols()];
		for (int i = 0; i < C.length; i++) {
			for (int j = 0; j < C[0].length; j++) {
				for (int k = 0; k < shape().cols(); k++)
					C[i][j] = Operator.ADD.evaluate(C[i][j], Operator.MULTIPLY.evaluate(m[i][k], other.get(k, j)));
			}
		}
		return new MMatrix(C);
	}

	public MMatrix multiplyRight(MMatrix other) {
		return other.multiplyLeft(this); //no need to copy, as multiplyleft creates a new matrix
	}

	/**
	 * Multiplies this matrix with the vector on the left and returns the result.
	 * 'Left' indicates the location of the matrix in the notation below.
	 * The left matrix-vector product of matrix A (m x 1)
	 * and the row vector b (size 1 x n) is defined as:<br/>
	 * {@code v_ij = (Ab)_ij = A_i1*b_j, i=1,..,m, j=1,...,n}<br/>
	 * where v has the shape (m x n)<br/>
	 * 
	 * The left matrix-vector product of matrix A (m x n)
	 * and the column vector b (size n) is defined as:<br/>
	 * {@code v_i = (Ab)_ij = Sum(j=1 to n | A_ij*b_j), i=1,..,m}<br/>
	 * where v has the shape (m)
	 * @param other the vector b, that will be multiplied with <tt>this</tt> (=A).
	 * @return the resulting vector v 
	 * @throws ShapeException if the columncount of A does not equal
	 * the rowcount of b.
	 * @see MSubMatrix#multiplyRight(MVector)
	 */
	public MathObject multiplyLeft(MVector other) {
		if (other.isTransposed()) { // Matrix times row vector
			if (shape.cols() == 1) {
				MathObject[][] matrix = new MathObject[shape.rows()][other.size()];
				for (int i = 0; i < matrix.length; i++)
					for (int j = 0; j < matrix[0].length; j++)
						matrix[i][j] =  Operator.MULTIPLY.evaluate(m[i][0], other.get(j));
				return new MMatrix(matrix);
			} else
				throw new ShapeException("matrix/row-vector product is only defined for an (n x 1)-matrix and n-vector, got " + shape + " and " + other.shape());
		} else {
			if (shape.cols() != other.shape().rows())
				throw new ShapeException(
						"The Matrix-Vector product is only defined for an (m x n)-matrix and n-Vector. Shapes: "
								+ shape() + ", " + other.shape());
			MVector b = new MVector(shape().rows());
			for (int i = 0; i < b.size(); i++) {
				MathObject b_i = null;
				for (int j = 0; j < shape().cols(); j++) {
					b_i = Operator.ADD.evaluate(b_i, Operator.MULTIPLY.evaluate(get(i, j), other.get(j)));
				}
				b.set(i, b_i);
			}
			return b;
		}
	}

	/**
	 * Multiplies this matrix with the vector on the right and returns the result.
	 * 'Right' indicates the location of the matrix in the notation below.
	 * The right matrix-vector product of matrix A (n x m)
	 * and row vector b (size 1 x n) is defined as:<br/>
	 * {@code v_j = (bA)_j = Sum(i=1 to n, b_i*A_ij), j=1,..,m}<br/>
	 * where v has the shape (1 x m) <br/>
	 * 
	 * The right matrix-vector product of matrix A (1 x m)
	 * and column vector b (size n x 1) is defined as:<br/>
	 * {@code v_ij = (bA)_ij = b_i*A_1j, i=1,..,n, j=1,..,m}<br/>
	 * where v has the shape (n x m)
	 * 
	 * @param other the vector b, that will be multiplied with <tt>this</tt> (=A).
	 * @return the resulting vector v. 
	 * @throws ShapeException if the columncount of b does not equal
	 * the rowcount of A.
	 */
	public MathObject multiplyRight(MVector other) {
		if (other.isTransposed()) {
			if (other.size() == shape.rows()) {
				MathObject[] v = new MathObject[shape.cols()];
				for (int i = 0; i < shape.cols(); i++) {
					MathObject mo = null;
					for (int j = 0; j < shape.rows(); j++)
						mo = Operator.ADD.evaluate(mo, Operator.MULTIPLY.evaluate(get(j, i), other.get(j)));
					v[i] = mo;
				}
				return new MVector(v).transpose();
			} else
				throw new ShapeException(
						"The row-vector/matrix product is only defined for a n-vector and (n x m)-matrix, got "
								+ other.shape() + " and " + shape);
		} else {
			if (shape.rows() == 1) {
				MathObject[][] m = new MathObject[other.size][shape.cols()];
				for (int i = 0; i < m.length; i++)
					for (int j = 0; j < m[0].length; j++)
						m[i][j] = Operator.MULTIPLY.evaluate(other.get(i), m[0][j]);
				return new MMatrix(m);
			} else
				throw new ShapeException(
						"The column-vector/matrix product is only defined for an n-vector and (1 x m)-matrix, got "
								+ other.shape + " and " + shape);
		}
	}

	/**
	 * Divides this {@code MVector} element-wise by the value in the given
	 * {@code MScalar}.
	 * 
	 * @param other the
	 *              {@code MScalar containing the value to be divided by. @return
	 *              {@code this}
	 */
	public MMatrix divide(MScalar other) {
		for (MathObject[] row : m)
			for (int i = 0; i < row.length; i++)
				row[i] = Operator.DIVIDE.evaluate(row[i], other);
		return this;
	}

	/**
	 * Calls {@code divide(new MScalar(d));} where d is the given double.
	 * 
	 * @param d the value to be passed as an {@code MScalar} to {@code divide()}
	 * @return the result of the call: {@code this}.
	 * @see #divide(MScalar)
	 */
	public MMatrix divide(double d) {
		return divide(new MReal(d));
	}

	/**
	 * Creates an augmented matrix of <tt>this</tt> and <tt>b</tt>. The augmented
	 * matrix is basically two matrices glued together. Note that the elements of
	 * <tt>this</tt> and <tt>b</tt> are copied into the augmented matrix, so any
	 * changes made to the elements in the augmented matrix, will not affect the
	 * originals.
	 * 
	 * @param b the <tt>MMatrix</tt> this one should be augmented with.
	 * @return a new, augmented <tt>MMatrix</tt>, or <tt>this</tt> if
	 *         <tt>b==null</tt>.
	 */
	public MMatrix augment(MMatrix b) {
		if (b == null)
			return this;
		MathObject augm[][] = new MathObject[shape.rows()][shape.cols() + b.shape().cols()];
		for (int i = 0; i < shape.rows(); i++) {
			for (int j = 0; j < shape.cols(); j++)
				augm[i][j] = m[i][j].copy();
			for (int k = shape.cols(); k < shape.cols() + b.shape().cols(); k++) {
				augm[i][k] = b.get(i, k - shape.cols()).copy();
			}
		}
		return new MMatrix(augm);
	}

	/**
	 * Creates an augmented matrix of <tt>this</tt> and a <tt>MVector b</tt>. The
	 * augmented matrix is basically the matrix and the vector glued together. Note
	 * that the elements of <tt>this</tt> and <tt>b</tt> are copied into the
	 * augmented matrix, so any changes made to the elements in the augmented
	 * matrix, will not affect the originals.
	 * 
	 * @param b the <tt>MVector</tt> this matrix should be augmented with.
	 * @return a new, augmented <tt>MMatrix</tt>, or <tt>this</tt> if
	 *         <tt>b==null</tt>.
	 */
	public MMatrix augment(MVector b) {
		if (b == null)
			return this;
		MathObject augm[][] = new MathObject[shape.rows()][shape.cols() + 1];
		for (int i = 0; i < shape.rows(); i++) {
			for (int j = 0; j < shape.cols(); j++)
				augm[i][j] = m[i][j].copy();
			augm[i][shape.cols()] = b.get(i).copy();
		}
		return new MMatrix(augm);
	}

	public MMatrix transpose() {
		MathObject[][] matrix = new MathObject[m[0].length][m.length];
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				matrix[i][j] = m[j][i];
		m = matrix;
		shape.transpose();
		return this;
	}

	public MScalar det() {
		if(!isSquare())
			throw new ShapeException("Determinant is only defined for square matrices. Shape: " + shape);
		//Decompose the matrix into LU decomposition and use Det(A)=Det(PLU)=Det(P)Det(L)Det(U)=Det(L)Det(U)
		if(m.length==2) {
			try {
				return ((MScalar) m[0][0].evaluate()).multiply((MScalar) m[1][1].evaluate()).subtract(((MScalar)m[0][1].evaluate()).multiply((MScalar)m[1][0].evaluate()));
			} catch(ClassCastException e) {
				throw new InvalidOperationException("Cannot calculate determinant of non-numeric matrix");
			}
		}
		MVector lup = new LUDecomposition(evaluate()).execute();
		MMatrix L = (MMatrix) lup.get(0);
		MMatrix U = (MMatrix) lup.get(1);
		MReal det = new MReal(1);
		for(int i = 0; i < L.shape.rows(); i++) {
			det.multiply(((MScalar) L.get(i, i))).multiply(((MScalar) U.get(i, i)));
		}
		return det;	
	}
	
	public MMatrix pow(int i) {
		if(!isSquare())
			throw new ShapeException("Only square matrices can raised to a power, shape: " + shape);
		if(i<0)
			return power(-i).invert();
		return power(i);
	}
	
	private MMatrix power(int i) {
		if(i==1) return copy();
		if(i==0) return identity(shape.rows());
		int a = (int) Math.floor(Math.log(i)/Math.log(2)+1);
		boolean[] bits = new boolean[a];
		for(int j = a-1; j>=0; j--)
			bits[j] = (i & (1<<j)) != 0;
		MMatrix B = this;
		MMatrix result = bits[0] ? B : identity(shape.rows());
		for(int j = 1; j < a; j++) {
			B = B.multiplyLeft(B);
			if(bits[j]) result = result.multiplyLeft(B);
		}
		
		/*if(i%2!=0) {
			odd = true;
			i-=1;
		}
		MMatrix B = this;
		while(i>1) {
			B = B.multiplyLeft(B);
			i/=2;
		}
		if(odd) return B.multiplyLeft(this);*/
		return result;
	}

	/**
	 * Applies a function to each element one by one.
	 * The results are then packed into a new MMatrix, leaving this one unchanged.
	 * In order to ensure that this matrix does not change, the function should not
	 * affect its arguments.
	 * @param f a function which takes a MathObject as its argument and returns another as its result.
	 * @return the newly created MMatrix of the same shape.
	 */
	public MMatrix forEach(Function<MathObject, MathObject> f) {
		MathObject[][] m2 = new MathObject[m.length][m[0].length];
		for(int i = 0; i < m2.length; i++)
			for(int j = 0; j < m2[0].length; j++)
				m2[i][j] = f.apply(m[i][j]);
		return new MMatrix(m2);
	}
	
	/**
	 * Negates the <tt>MMatrix</tt>. <br/>
	 * Every element in the matrix will be negated using
	 * {@link MathObject#negate()}.
	 * 
	 * @return this
	 * @see MathObject#negate()
	 */
	@Override
	public MMatrix negate() {
		for (MathObject[] row : m)
			for (MathObject mo : row)
				mo.negate();
		return this;
	}

	/**
	 * Sets this <tt>MMatrix</tt> to its inverse.
	 * 
	 * @return <tt>this</tt>
	 * @see MathObject#invert()
	 * @throws InvalidOperationException if the matrix is not square.
	 */
	@Override
	public MMatrix invert() {
		if (shape.cols() != shape.rows())
			throw new InvalidOperationException("Only square matrices can be inverted: dimension=" + shape);
		m = new JordanElimination(this, identity(shape.rows())).execute()
				.extractSubMatrix(0, shape.cols(), shape.rows() - 1, shape.cols() * 2 - 1).elements();
		return this;
	}

	/**
	 * Sets this <tt>MMatrix</tt> to its inverse.
	 * 
	 * @return <tt>this</tt>
	 * @see MathObject#invert()
	 * @throws InvalidOperationException if the matrix is not square.
	 */
	@Override
	public MMatrix copy() {
		MathObject[][] co = new MathObject[m.length][m[0].length];
		for (int i = 0; i < m.length; i++)
			for (int j = 0; j < m[i].length; j++)
				co[i][j] = m[i][j].copy();
		return new MMatrix(co);
	}

	/**
	 * Evaluates every element in the Matrix
	 */
	@Override
	public MMatrix evaluate() {
		MMatrix copy = (MMatrix) copy();
		for (MathObject[] row : copy.elements())
			for (int i = 0; i < row.length; i++)
				row[i] = row[i].evaluate();
		return copy;
	}
	
	@Override
	public boolean isNumeric() {
		for(MathObject[] row : m)
			for(MathObject element : row)
				if(!element.isNumeric()) return false;
		return true;
	}

	@Override
	public Shape shape() {
		return shape;
	}

	@Override
	public String toString() {
		return Printer.toText(m);
	}

	public static MMatrix identity(int size) {
		MMatrix matrix = new MMatrix(new Shape(size, size));
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				matrix.set(i, j, new MReal(i == j ? 1 : 0));
		return matrix;
	}
	
	public static MMatrix zeros(int rows, int cols) {
		MMatrix matrix = new MMatrix(new Shape(rows, cols));
		for (int i = 0; i < rows; i++)
			for (int j = 0; j < cols; j++)
				matrix.set(i, j, new MReal(0));
		return matrix;
	}

	public static MMatrix empty(int rows, int cols) {
		return new MMatrix(new MathObject[rows][cols]);
	}
	
	class Slice {
		int startrow, endrow, startcol, endcol;
		private Slice(int startrow, int endrow, int startcol, int endcol) {
			if(endrow<=startrow || endcol<=startcol)
				throw new IndexException("End index greater than start index");
			this.startrow = startrow; this.endrow = endrow; this.startcol = startcol; this.endcol = endcol;
		}
		
		public MMatrix getMatrix() {
			return MMatrix.this;
		}
	}
}