package mathobjects;

import java.util.List;

import algorithms.linalg.JordanElimination;
import helpers.Shape;
import helpers.exceptions.InvalidOperationException;
import helpers.exceptions.ShapeException;
import main.Operator;

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

	public MMatrix getSubMatrix(int lefttoprow, int lefttopcol, int rightbottomrow, int rightbottomcol) {
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
	 * @return {@code this} after the addition. @ if the other matrix's shape does
	 * not equal this one's.
	 */
	public MMatrix add(MMatrix other) {
		if (!other.shape().equals(shape()))
			throw new InvalidOperationException(
					"To add two Matrixes, they need to be the same shape! Shape: " + shape() + " and " + other.shape());
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
	 * @return {@code this} after the subtraction. @ if the other matrix's shape
	 * does not equal this one's.
	 */
	public MMatrix subtract(MMatrix other) {
		if (!other.shape().equals(shape()))
			throw new InvalidOperationException("To subtract two Matrixes, they need to be the same shape! Shape: "
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
	public MathObject multiplyLeft(MMatrix other) {
		if (shape.cols() != other.shape().rows())
			throw new ShapeException("Matrix product is only defined for n x m and m x k Matrices. Shapes: " + shape()
					+ ", " + other.shape());
		MMatrix C = new MMatrix(new Shape(shape.rows(), other.shape().cols()));
		for (int i = 0; i < C.shape().rows(); i++) {
			for (int j = 0; j < C.shape().cols(); j++) {
				MathObject C_ij = null;
				for (int k = 0; k < shape().cols(); k++)
					C_ij = Operator.ADD.evaluate(C_ij, Operator.MULTIPLY.evaluate(get(i, k), other.get(k, j)));
				C.set(i, j, C_ij);
			}
		}
		return C;
	}

	public MathObject multiplyRight(MMatrix other) {
		return other.multiplyLeft(this);
	}

	/**
	 * Builds the matrix-vector product of {@code this} and the given
	 * {@code MVector} and returns it. The matrix-vector product of matrix A (m x n)
	 * and Vector b (size n) is defined as:
	 * {@code v_i = (Ab)_i = Sum(j=1 to n, A_ij*b_j), i=1,..,m}
	 * 
	 * @param other the matrix B, that will be multiplied with <tt>this</tt> (=A).
	 * @return the resulting (m)-vector. @ if the columncount of A does not equal
	 * the rowcount of B.
	 */
	public MathObject multiplyLeft(MVector other) {
		if (other.isTransposed()) { // Matrix times row vector
			if (shape.cols() == 1) {
				MMatrix matrix = empty(shape.rows(), other.size());
				for (int i = 0; i < matrix.shape.rows(); i++)
					for (int j = 0; j < matrix.shape.cols(); j++)
						matrix.set(i, j, Operator.MULTIPLY.evaluate(get(i, 0), other.get(j)));
				return matrix;
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
					b.set(i, b_i);
				}
			}
			return b;
		}
	}

	public MathObject multiplyRight(MVector other) {
		if (other.isTransposed()) {
			if (other.size() == shape.rows()) {
				MVector v = MVector.empty(shape.cols());
				for (int i = 0; i < shape.cols(); i++) {
					MathObject mo = null;
					for (int j = 0; j < shape.rows(); j++)
						mo = Operator.ADD.evaluate(mo, Operator.MULTIPLY.evaluate(get(j, i), other.get(j)));
					v.set(i, mo);
				}
				return v;
			} else
				throw new ShapeException(
						"The row-vector/matrix product is only defined for a n-vector and (n x m)-matrix, got "
								+ other.shape() + " and " + shape);
		} else {
			if (shape.rows() == 1) {
				MMatrix m = empty(other.size(), shape.cols());
				for (int i = 0; i < m.shape().rows(); i++)
					for (int j = 0; j < m.shape.cols(); j++)
						m.set(i, j, Operator.MULTIPLY.evaluate(other.get(i), get(0, j)));
				return m;
			} else
				throw new ShapeException(
						"The column-vector/matrix product is only defined for an n-vector and (1 x m)-matrix, got "
								+ other.shape + " and " + shape);
		}

		// throw new ShapeException("The Vector-Matrix product is only defined for an
		// size n column-vector and (n x 1)-matrix or size n row-vector and (1 x
		// n)-matrix ")
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

	// TODO method to raise matrix to a integer power. First the algorithm for
	// Jordan-Normal Form is needed.

	/**
	 * Negates the <tt>MMatrix</tt>. <br/>
	 * Every element in the matrix will be negated using
	 * {@link MathObject#negate()}.
	 * 
	 * @return this
	 * @see MathObject#negate()
	 */
	@Override
	public MathObject negate() {
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
	public MathObject invert() {
		if (shape.cols() != shape.rows())
			throw new InvalidOperationException("Only square matrices can be inverted: dimension" + shape);
		m = new JordanElimination(this, identity(shape.rows())).execute()
				.getSubMatrix(0, shape.cols(), shape.rows() - 1, shape.cols() * 2 - 1).elements();
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
	public MathObject copy() {
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
	public MathObject evaluate() {
		MMatrix copy = (MMatrix) copy();
		for (MathObject[] row : copy.elements())
			for (int i = 0; i < row.length; i++)
				row[i] = row[i].evaluate();
		return copy;
	}

	@Override
	public Shape shape() {
		return shape;
	}

	@Override
	public String toString() {
		String s = "[";
		for (MathObject[] row : m) {
			for (MathObject mo : row)
				s += mo.toString() + ", ";
			s = s.substring(0, s.length() - 2) + ";";
		}
		return s.substring(0, s.length() - 1) + "]";
	}

	public static MMatrix identity(int size) {
		MMatrix matrix = new MMatrix(new Shape(size, size));
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				matrix.set(i, j, new MReal(i == j ? 1 : 0));
		return matrix;
	}

	public static MMatrix empty(int rows, int cols) {
		return new MMatrix(new MathObject[rows][cols]);
	}
}