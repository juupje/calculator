package mathobjects;

import java.util.List;

import algorithms.linalg.JordanElimination;
import helpers.Dimension;
import helpers.exceptions.InvalidOperationException;
import main.Operator;

public class MMatrix implements MathObject {
	
	MathObject m[][];
	Dimension dim;
	
	public MMatrix(MathObject[][] m) {
		this.m = m;
		dim = new Dimension(m.length, m[0].length);
	}
	
	public MMatrix(double[][] m) {
		this.m = new MathObject[m.length][m[0].length];
		for(int i = 0; i < m.length; i++)
			for(int j = 0; j < m[i].length; j++)
				this.m[i][j] = new MScalar(m[i][j]);
		dim = new Dimension(m.length, m[0].length);
	}
	
	/**
	 * Constructs an empty (all entries are <tt>null</tt>) matrix with the given {@code Dimension}. 
	 * @param dim the {@link Dimension} of the matrix to be made.
	 */
	public MMatrix(Dimension dim) {
		this.dim = dim;
		m = new MathObject[dim.rows()][dim.cols()];
	}
	
	/**
	 * Creates a matrix of the vectors in the list. Note that the result from this constructor will be a column vector of row vectors,
	 * in which the latter are the entries in the list.
	 * So, if the list is [v1, v2, v3], than the matrix will be: <br/>
	 * | v1[0]  v1[1]  v1[2] | <br/>
	 * | v2[0]  v2[1]  v2[2] | <br/>
	 * | v3[0]  v3[1]  v3[2] | <br/>
	 * @param list a <tt>List</tt> of <tt>MVector</tt>s
	 */
	public MMatrix(List<MVector> list) {
		m = new MathObject[list.size()][];
		for(int i = 0; i < list.size(); i++) {
			if(i < list.size()-1 && list.get(i).size() != list.get(i+1).size())
				throw new IllegalArgumentException("Can't create matrix with non-constant row lengths!");
			m[i] = list.get(i).elements();
		}
		dim = new Dimension(m.length, m[0].length);
	}
	
	/**
	 * Creates a matrix of the vectors in the array. Note that the result from this constructor will be a row vector of column vectors,
	 * in which the latter are the entries in the array.
	 * So, if the array is [v1, v2, v3], the matrix will be:<br/>
	 * | v1[0]  v2[0]  v3[0] | <br/>
	 * | v1[2]  v2[1]  v3[1] | <br/>
	 * | v1[2]  v2[2]  v3[2] | <br/>
	 * @param v an array <tt>MVector[]</tt>
	 */
	public MMatrix(MVector[] v) {
		m = new MathObject[v[0].size()][v.length];
		for(int i = 0; i < v.length; i++) {
			if(i<v.length-1 && v[i].size() != v[i+1].size())
				throw new IllegalArgumentException("Can't create matrix with non-constant row lengths!");
			for(int j = 0; j < v[i].size(); j++)
				m[j][i] = v[i].get(j);
		}
		dim = new Dimension(m.length, m[0].length);
	}
	
	/**
	 * Returns the dimension of this matrix. <tt>(a x b)</tt> means <tt>a</tt> rows and <tt>b</tt> columns.
	 * @return a  
	 */
	public Dimension dim() {
		return dim;
	}
	
	/**
	 * Returns the component at thg i-th row and j-th column of this matrix.
	 * @param i the row of the component
	 * @param j the column of the component
	 * @return the component at [i,j] as a {@link MathObject}
	 */
	public MathObject get(int i, int j) {
		return m[i][j];
	}
	
	/**
	 * Sets the component at thg i-th row and j-th column of the matrix to the given {@link MathObject}.
	 * @param i the row of the component
	 * @param j the column of the component
	 * @param mo the <tt>MathObject</tt> to be set at [i,j]
	 */
	public void set(int i, int j, MathObject mo) {
		m[i][j] = mo;
	}
	
	/**
	 * Returns the i-th row of the matrix as a {@link MVector}.
	 * @param i index of the row requested
	 * @param mo a <tt>MVector</tt> containing the values in the i-th row.
	 */
	public MVector getRow(int i) {
		MathObject[] v = new MathObject[m[i].length];
		for(int j = 0; j < m[i].length; j++)
			v[j] = m[i][j];
		return new MVector(v);
	}
	
	/**
	 * Returns the j-th column of the matrix as a {@link MVector}.
	 * @param j index of the column requested
	 * @param mo a <tt>MVector</tt> containing the values in the j-th column.
	 */
	public MVector getColumn(int j) {
		MathObject[] v = new MathObject[m.length];
		for(int i = 0; i < m.length; i++)
			v[i] = m[i][j];
		return new MVector(v);
	}
	
	/**
	 * Returns the elements in this matrix.
	 * @return a <tt>MathObject[][]</tt> of the elements in the matrix.
	 */
	public MathObject[][] elements() {
		return m;
	}
	
	public MMatrix getSubMatrix(int lefttoprow, int lefttopcol, int rightbottomrow, int rightbottomcol) {
		if(lefttoprow < 0 || lefttopcol < 0 || rightbottomrow >= dim.rows() || rightbottomcol >= dim.cols())
			throw new IndexOutOfBoundsException("Bounds of submatrix out of bounds! (" + lefttoprow + ", " + lefttopcol + ", " + rightbottomrow + ", " + rightbottomcol +
					") does not fit inside matrix of dimension " + dim);
		MMatrix result = new MMatrix(new Dimension(rightbottomrow-lefttoprow+1, rightbottomcol - lefttopcol+1));
		for(int i = lefttoprow; i <= rightbottomrow; i++)
			for(int j = lefttopcol; j <= rightbottomcol; j++)
				result.set(i-lefttoprow, j-lefttopcol, m[i][j]);
		return result;
	}
	
	/**
	 * Adds another vector element-wise to this one. Note that this will change the vector on which this method is called.
	 * If that is not your wish, consider using {@link Operator#ADD}
	 * @param other the {@code MVvector} to be added, this one will not be changed.
	 * @return {@code this} after the addition.
	 */
	public MMatrix add(MMatrix other) {
		if(!other.dim().equals(dim()))
			throw new InvalidOperationException("To add two Matrixes, they need to be the same dimension! Dimensions: " + dim() + " and " +other.dim());
		for(int i = 0; i < m.length; i++)
			for(int j = 0; j < m[i].length; j++)
				m[i][j] = Operator.ADD.evaluate(m[i][j], other.get(i, j));
		return this;
	}
	
	/**
	 * Subtracts another vector element-wise from this one. Note that this will change the vector on which this method is called.
	 * If that is not your wish, consider using {@link Operator.SUBTRACT}
	 * @param other the {@code MVvector} to be subtracted, this one will not be changed.
	 * @return {@code this} after the subtraction.
	 */
	public MMatrix subtract(MMatrix other) {
		if(!other.dim().equals(dim()))
			throw new InvalidOperationException("To subtract two Matrixes, they need to be the same dimension! Dimensions: " + dim() + " and " +other.dim());
		for(int i = 0; i < m.length; i++)
			for(int j = 0; j < m[i].length; j++)
				m[i][j] = Operator.SUBTRACT.evaluate(m[i][j], other.get(i, j));
		return this;
	}

	/**
	 * Multiplies this {@code MVector} element-wise with the value in the given {@code MScalar}
	 * @param other the {@code MScalar} to be multiplied with.
	 * @return {@code this}
	 */
	public MMatrix multiply(MScalar other) {
		for(MathObject[] row : m)
			for(int i = 0; i < row.length; i++)
				row[i] = Operator.MULTIPLY.evaluate(row[i], other);
		return this;
	}
	
	/**
	 * Calls {@code multiply(new MScalar(d));} where d is the given double.
	 * @param d the value to be passed as an {@code MScalar} to {@code multiply()}
	 * @return the result of the call: {@code this}.
	 * @see #multiply(MScalar)
	 */
	public MMatrix multiply(double d) {
		return multiply(new MScalar(d));
	}
	
	/**
	 * Builds the matrix product of {@code this} and the given {@code MMatrix} and returns it.
	 * The matrix product of A (m x n) and B (n x k) is defined as:
	 * {@code C_ij = (AB)_ij = Sum(l=1 to n, A_il*B_lj), i=1,...,m  j=1,...,k}
	 * @param other the matrix B, that will be multiplied with <tt>this</tt> (=A).
	 * @return the resulting (m x k)-matrix.
	 * @throws InvalidOperationException if the columncount of A does not equal the rowcount of B.
	 */
	public MathObject multiply(MMatrix other) {
		if(dim.cols() != other.dim().rows())
			throw new InvalidOperationException("Matrix product is only defined for n x m and m x k Matrices. Dimensions: " + dim() + ", " + other.dim());
		MMatrix C = new MMatrix(new Dimension(dim.rows(), other.dim().cols()));
		for(int i = 0; i < C.dim().rows(); i++) {
			for(int j = 0; j < C.dim().cols(); j++) {
				MathObject C_ij = null;
				for(int k = 0; k < dim.cols(); k++)
					C_ij = Operator.ADD.evaluate(C_ij, Operator.MULTIPLY.evaluate(get(i,k), other.get(k, j)));
				C.set(i, j, C_ij);
			}
		}				
		return C;
	}
	
	/**
	 * Builds the matrix-vector product of {@code this} and the given {@code MVector} and returns it.
	 * The matrix-vector product of matrix A (m x n) and Vector b (size n) is defined as:
	 * {@code v_i = (Ab)_i = Sum(j=1 to n, A_ij*b_j), i=1,..,m}
	 * @param other the matrix B, that will be multiplied with <tt>this</tt> (=A).
	 * @return the resulting (m x k)-matrix.
	 * @throws InvalidOperationException if the columncount of A does not equal the rowcount of B.
	 */
	public MathObject multiply(MVector other) {
		if(dim.cols() != other.size())
			throw new InvalidOperationException("The Matrix-Vector product is only defined for an (m x n)-matrix and n-Vector. Dimensions: " + dim() + ", " + other.size());
		MVector b = new MVector(dim.rows());
		for(int i = 0; i < b.size(); i++) {
			MathObject b_i = null;
			for(int j = 0; j < dim().cols(); j++) {
				b_i = Operator.ADD.evaluate(b_i, Operator.MULTIPLY.evaluate(get(i,j), other.get(j)));
				b.set(i, b_i);
			}
		}				
		return b;
	}
	
	/**
	 * Divides this {@code MVector} element-wise by the value in the given {@code MScalar}.
	 * @param other the {@code MScalar containing the value to be divided by.
	 * @return {@code this}
	 */
	public MMatrix divide(MScalar other) {
		for(MathObject[] row : m)
			for(int i = 0; i < row.length; i++)
				row[i] = Operator.DIVIDE.evaluate(row[i], other);
		return this;
	}
	
	/**
	 * Calls {@code divide(new MScalar(d));} where d is the given double.
	 * @param d the value to be passed as an {@code MScalar} to {@code divide()}
	 * @return the result of the call: {@code this}.
	 * @see #divide(MScalar)
	 */
	public MMatrix divide(double d) {
		return divide(new MScalar(d));
	}
	
	/**
	 * Creates an augmented matrix of <tt>this</tt> and <tt>b</tt>.
	 * The augmented matrix is basically two matrices glued together.
	 * Note that the elements of <tt>this</tt> and <tt>b</tt> are copied into the augmented matrix,
	 * so any changes made to the elements in the augmented matrix, will not affect the originals.
	 * @param b the <tt>MMatrix</tt> this one should be augmented with.
	 * @return a new, augmented <tt>MMatrix</tt>, or <tt>this</tt> if <tt>b==null</tt>.
	 */
	public MMatrix augment(MMatrix b) {
		if(b == null) return this;
		MathObject augm[][] = new MathObject[dim.rows()][dim.cols()+b.dim().cols()];
		for(int i = 0; i < dim.rows(); i++) {
			for(int j = 0; j < dim.cols(); j++)
				augm[i][j]=m[i][j].copy();
			for(int k = dim.cols; k < dim.cols + b.dim().cols; k++) {
				augm[i][k] = b.get(i, k-dim.cols).copy();
			}
		}
		return new MMatrix(augm);
	}
	
	/**
	 * Creates an augmented matrix of <tt>this</tt> and a <tt>MVector b</tt>.
	 * The augmented matrix is basically the matrix and the vector glued together.
	 * Note that the elements of <tt>this</tt> and <tt>b</tt> are copied into the augmented matrix,
	 * so any changes made to the elements in the augmented matrix, will not affect the originals.
	 * @param b the <tt>MVector</tt> this matrix should be augmented with.
	 * @return a new, augmented <tt>MMatrix</tt>, or <tt>this</tt> if <tt>b==null</tt>.
	 */
	public MMatrix augment(MVector b) {
		if(b == null) return this;
		MathObject augm[][] = new MathObject[dim.rows()][dim.cols()+1];
		for(int i = 0; i < dim.rows(); i++) {
			for(int j = 0; j < dim.cols(); j++)
				augm[i][j]=m[i][j].copy();
			augm[i][dim.cols()] = b.get(i).copy();
		}
		return new MMatrix(augm);
	}

	//TODO method to raise matrix to a integer power. First the algorithm for Jordan-Normal Form is needed.
	
	/**
	 * Negates the <tt>MMatrix</tt>. <br/>
	 * Every element in the matrix will be negated using {@link MathObject#negate()}.
	 * @return this
	 * @see MathObject#negate()
	 */
	@Override
	public MathObject negate() {
		for(MathObject[] row : m)
			for(MathObject mo : row)
				mo.negate();
		return this;
	}

	/**
	 * Sets this <tt>MMatrix</tt> to its inverse.
	 * @return <tt>this</tt>
	 * @see MathObject#invert()
	 * @throws InvalidOperationException if the matrix is not square.
	 */
	@Override
	public MathObject invert() {
		if(dim.cols() != dim.rows())
			throw new InvalidOperationException("Only square matrices can be inverted: dimension" + dim);
		m = new JordanElimination(this, identity(dim.rows())).execute().getSubMatrix(0, dim.cols(), dim.rows()-1, dim.cols()*2-1).elements();
		return this;
	}

	/**
	 * Sets this <tt>MMatrix</tt> to its inverse.
	 * @return <tt>this</tt>
	 * @see MathObject#invert()
	 * @throws InvalidOperationException if the matrix is not square.
	 */
	@Override
	public MathObject copy() {
		MathObject[][] co = new MathObject[m.length][m[0].length];
		for(int i =0; i < m.length; i++)
			for(int j = 0; j < m[i].length; j++)
				co[i][j] = m[i][j].copy();
		return new MMatrix(co);
	}

	/**
	 * Evaluates every element in the Matrix 
	 */
	@Override
	public MathObject evaluate() {
		MMatrix copy = (MMatrix) copy();
		for(MathObject[] row : copy.elements())
			for(int i = 0; i < row.length; i++)
				row[i] = row[i].evaluate();
		return copy;
	}
	
	public static MMatrix identity(int size) {
		MMatrix matrix = new MMatrix(new Dimension(size, size));
		for(int i = 0; i < size; i++)
			for(int  j = 0; j < size; j++)
				matrix.set(i, j, new MScalar(i == j ? 1 : 0));
		return matrix;
	}
	
	@Override
	public String toString() {
		String s = "[";
		for(MathObject[] row : m) {
			for(MathObject mo : row)
				s += mo.toString() + ", ";
			s = s.substring(0, s.length()-2) + ";";
		}
		return s.substring(0, s.length()-1) + "]";
	}
}
