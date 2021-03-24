package com.github.juupje.calculator.mathobjects;

import com.github.juupje.calculator.algorithms.linalg.JordanElimination;
import com.github.juupje.calculator.helpers.Printer;
import com.github.juupje.calculator.helpers.exceptions.IndexException;
import com.github.juupje.calculator.helpers.exceptions.InvalidOperationException;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.main.Operator;
import com.github.juupje.calculator.mathobjects.MMatrix.Slice;

public class MSubMatrix extends MIndexable {
	private int rstart, rend, cstart, cend;
	private int rows, cols;
	private MMatrix p; //parent
	
	/**
	 * @param startrow inclusive
	 * @param endrow exclusive
	 * @param startcol inclusive
	 * @param endcol exclusive
	 */
	public MSubMatrix(Slice slice) {
		p = slice.getMatrix();
		rstart = Math.max(0, slice.startrow);
		rend = Math.min(p.shape().rows(), slice.endrow);
		cstart = Math.max(0, slice.startcol); 
		cend = Math.min(p.shape().cols(), slice.endcol);
		rows = rend-rstart;
		cols = cend-cstart;
		shape = new Shape(rows, cols);
	}
	
	@Override
	boolean checkindex(int... indices) {
		if(indices.length != 2)
			throw new IndexException("Too many indices ("+indices.length+") for submatrix of dimension 2");
		if(indices[0]>=rows || indices[1]>=cols)
			throw new IndexException("Index [" + indices[0] +", " + indices[1] + "] out of bounds for submatrix of size (" + rows + ", " + cols + ")");
		return true;
	}
	
	/**
	 * Returns the component at the i-th row and j-th column of this submatrix.
	 * 
	 * @param i the row of the component
	 * @param j the column of the component
	 * @return the component at [i,j] as a {@link MathObject}
	 */
	public MathObject get(int i, int j) {
		checkindex(i,j);
		return p.get(i+rstart, j+cstart);
	}
	
	@Override
	public MathObject get(int... index) {
		checkindex(index);
		return get(index[0], index[1]);
		
	}
	
	/**
	 * Sets the component at the i-th row and j-th column of the submatrix to the given
	 * {@link MathObject}.
	 * 
	 * @param i  the row of the component
	 * @param j  the column of the component
	 * @param mo the <tt>MathObject</tt> to be set at [i,j]
	 */
	public void set(int i, int j, MathObject obj) {
		checkindex(i,j);
		p.set(i+rstart, j+cstart, obj);
	}
	
	@Override
	public void set(MathObject obj, int... index) {
		checkindex(index);
		set(index[0], index[1], obj);
	}
	
	
	/**
	 * Copies the given matrix into this submatrix.
	 * @param m the matrix to be copied
	 * @throws ShapeException if the shape of {@code m} does not match to {@code this.shape}.
	 */
	public void set(MMatrix m) {
		if(!m.shape().equals(shape))
			throw new ShapeException("Cannot set submatrix of shape " + shape + " to matrix of size " + m.shape());
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				p.m[i+rstart][j+cstart] = m.get(i, j).copy();
	}
	
	/**
	 * Sets this submatrix to the given matrix (note that the elements are not copied!)
	 * @param m the matrix to be moved into the submatrix
	 * @throws ShapeException if the shape of {@code m} does not match to {@code this.shape}.
	 */
	public void setNoCopy(MMatrix m) {
		if(!m.shape().equals(shape))
			throw new ShapeException("Cannot set submatrix of shape " + shape + " to matrix of size " + m.shape());
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				p.m[i+rstart][j+cstart] = m.get(i, j);
	}
	
	/**
	 * Returns the i-th row of the submatrix as a {@link MVector}.
	 * 
	 * @param i  index of the row requested
	 * @param mo a <tt>MVector</tt> containing the values in the i-th row.
	 */
	public MVector getRow(int i) {
		i += rstart;
		MathObject[] v = new MathObject[cols];
		for (int j = cstart; j < cend; j++)
			v[j] = p.m[i][j];
		return new MVector(v);
	}

	/**
	 * Returns the j-th column of the submatrix as a {@link MVector}.
	 * 
	 * @param j  index of the column requested
	 * @param mo a <tt>MVector</tt> containing the values in the j-th column.
	 */
	public MVector getColumn(int j) {
		j += cstart;
		MathObject[] v = new MathObject[rows];
		for (int i = rstart; i < rend; i++)
			v[i] = p.m[i][j];
		return new MVector(v);
	}
	
	/**
	 * Returns {@code true} if the submatrix is square or {@code false} otherwise. 
	 * @return {@code cols==rows;}
	 */
	public boolean isSquare() {
		return rows==cols;
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
	public MSubMatrix add(MMatrix other) {
		if (!other.shape().equals(shape))
			throw new ShapeException(
					"To add two matrices, they need to be the same shape! Shape: " + shape + " and " + other.shape());
		for (int i = rstart; i < rend; i++)
			for (int j = cstart; j < cend; j++)
				p.m[i][j] = Operator.ADD.evaluate(p.m[i][j], other.get(i-rstart, j-cstart));
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
	public MSubMatrix subtract(MMatrix other) {
		if (!other.shape().equals(shape))
			throw new ShapeException("To subtract two matrices, they need to be the same shape! Shape: "
					+ shape + " and " + other.shape());
		for (int i = rstart; i < rend; i++)
			for (int j = cstart; j < cend; j++)
				p.m[i][j] = Operator.SUBTRACT.evaluate(p.m[i][j], other.get(i-rstart, j-cstart));
		return this;
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
					C[i][j] = Operator.ADD.evaluate(C[i][j], Operator.MULTIPLY.evaluate(get(i, k), other.get(k, j)));
			}
		}
		return new MMatrix(C);
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
	 * {@code v_i = (Ab)_i = Sum(j=1 to n | A_ij*b_j), i=1,..,m}<br/>
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
				MathObject[][] matrix = new MathObject[rows][other.size()];
				for (int i = 0; i < matrix.length; i++)
					for (int j = 0; j < matrix[0].length; j++)
						matrix[i][j] = Operator.MULTIPLY.evaluate(p.m[i+rstart][cstart], other.get(j));
				return new MMatrix(matrix);
			} else
				throw new ShapeException("matrix/row-vector product is only defined for an (n x 1)-matrix and n-vector, got " + shape + " and " + other.shape());
		} else {
			if (shape.cols() != other.shape().rows())
				throw new ShapeException(
						"The Matrix-Vector product is only defined for an (m x n)-matrix and n-Vector. Shapes: "
								+ shape() + ", " + other.shape());
			MathObject[] b = new MathObject[rows];
			for (int i = rstart; i < rend; i++) {
				MathObject b_i = null;
				for (int j = 0; j < shape().cols(); j++) {
					b_i = Operator.ADD.evaluate(b_i, Operator.MULTIPLY.evaluate(p.m[i+rstart][j+cstart], other.get(j)));
				}
				b[i-rstart] = b_i;
			}
			return new MVector(b);
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
		if (other.isTransposed()) { //row vector times matrix
			if (other.size() == shape.rows()) {
				MathObject[] v = new MathObject[cols];
				for (int i = 0; i < shape.cols(); i++) {
					MathObject mo = null;
					for (int j = 0; j < shape.rows(); j++)
						mo = Operator.ADD.evaluate(mo, Operator.MULTIPLY.evaluate(p.m[rstart+j][cstart+i], other.get(j)));
					v[i] = mo;
				}
				return new MVector(v).transpose();
			} else
				throw new ShapeException(
						"The row-vector/matrix product is only defined for a n-vector and (n x m)-matrix, got "
								+ other.shape() + " and " + shape);
		} else {
			if (shape.rows() == 1) {
				MathObject[][] m = new MathObject[other.size][cols];
				for (int i = 0; i < m.length; i++)
					for (int j = 0; j < m[0].length; j++)
						m[i][j] = Operator.MULTIPLY.evaluate(other.get(i), p.m[rstart][cstart+j]);
				return new MMatrix(m);
			} else
				throw new ShapeException(
						"The column-vector/matrix product is only defined for an n-vector and (1 x m)-matrix, got "
								+ other.shape + " and " + shape);
		}
	}
	
	/**
	 * Multiplies this {@code MSubMatrix} element-wise with the value in the given
	 * {@code MScalar}
	 * 
	 * @param other the {@code MScalar} to be multiplied with.
	 * @return {@code this}
	 */
	@Override
	public MSubMatrix multiply(MScalar other) {
		for (int i = rstart; i < rend; i++)
			for(int j = cstart; j < cend; j++)
				p.m[i][j].multiply(other);
		return this;
	}
	
	/**
	 * Returns the elements in this submatrix.
	 * 
	 * @return a <tt>MathObject[][]</tt> of the elements in the submatrix.
	 */
	public MathObject[][] elements() {
		MathObject[][] m = new MathObject[rows][cols];
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				m[i][j] = p.m[i+rstart][j+cstart];
		return m;
	}
	
	/**
	 * Copies the submatrix into a normal {@code MMatrix}.
	 * @return a {@code MMatrix} with a copy of this {@code MSubMatrix}.
	 */
	public MMatrix asMMatrix() {
		return new MMatrix(elements());
	}

	@Override
	public MSubMatrix negate() {
		for(int i = rstart; i < rend; i++)
			for(int j = cstart; j < cend; j++)
				p.m[i][j].negate();
		return this;
	}

	@Override
	public MathObject invert() {
		if(rows != cols)
			throw new InvalidOperationException("Only square matrices can be inverted: dimension=" + shape);
		setNoCopy(new JordanElimination(asMMatrix(), MMatrix.identity(shape.rows())).execute());
		return null;
	}

	@Override
	public MMatrix copy() {
		MathObject[][] m = new MathObject[rows][cols];
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				m[i][j] = p.m[i+rstart][j+cstart].copy();
		return new MMatrix(m);
	}

	@Override
	public MathObject evaluate() {
		for(int i = rstart; i < rend; i++)
			for(int j = cstart; j < cend; j++)
				p.m[i][j] = p.m[i][j].evaluate();
		return this;
	}

	@Override
	public Shape shape() {
		return shape;
	}

	@Override
	public boolean isNumeric() {
		for(int i = rstart; i < rend; i++)
			for(int j = cstart; j < cend; j++)
				if(!p.m[i][j].isNumeric()) return false;
		return true;
	}
	
	@Override
	public String toString() {
		return Printer.toText(p.m, rstart, rend-1, cstart, cend-1);
	}
}
