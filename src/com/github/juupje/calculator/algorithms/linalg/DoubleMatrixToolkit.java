package com.github.juupje.calculator.algorithms.linalg;

import com.github.juupje.calculator.helpers.Printer;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MReal;

public class DoubleMatrixToolkit extends MatrixToolkit<Double> {

	public DoubleMatrixToolkit(double[][] matrix) {
		super(matrix.length, matrix[0].length);
		this.matrix = new Double[rows][cols];
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				this.matrix[i][j] = matrix[i][j];
	}
	
	public DoubleMatrixToolkit(Double[][] matrix) {
		super(matrix);
	}
	
	/**
	 * Constructs a submatrix from the given matrix
	 * @param matrix
	 * @param rstart the index of the first row (inclusive)
	 * @param rend the index of the last row (inclusive)
	 * @param cstart the index of the first column (inclusive)
	 * @param cend the index of the last column (inclusive)
	 */
	public DoubleMatrixToolkit(Double[][] matrix, int rstart, int rend, int cstart, int cend) {
		super(matrix, rstart, rend, cstart, cend);
	}
	
	/*public DoubleMatrixToolkit(MMatrix m) {
		rows = m.shape().rows();
		cols = m.shape().cols();
		matrix = new Double[rows][cols];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				if(m.get(i, j) instanceof MReal)
					matrix[i][j] = ((MReal) m.get(i, j)).getValue();
				else if(m.get(i, j) instanceof MExpression) {
					try {
						matrix[i][j] = ((MReal) m.get(i,j).evaluate()).getValue();
					} catch(ClassCastException e) {
						throw new IllegalArgumentException("This toolkit only supports scalar-valued matrices, got " + Tools.type(m));
					}
				} else
					throw new IllegalArgumentException("This toolkit only supports scalar-valued matrices, got " + Tools.type(m));
			}
		}
	}*/

	/**
	 * Adds the i-th row multiplied with c to the n-th row. Like so: <br/>
	 * <tt>M_nj -> M_nj+c*M_ij   with   j=1....m</tt>
	 * @param n the index of the row to which the <tt>i</tt>-th row will be added.
	 * @param i the row to be added to the <tt>n</tt>-th row
	 * @param c a constant with which the i-th row will be multiplied before being added to the <tt>n</tt>-th row.
	 */
	@Override
	public void addToRow(int n, int i, Double c) {
		n += rstart; i += rstart;
		for(int j = cstart; j <= cend; j++)
			matrix[n][j] += c*matrix[i][j];
	}
	
	/**
	 * Multiplies the <tt>n</tt>-th row with a constant scalar value.
	 * @param n the index of the row to be multiplied with <tt>c</tt>
	 * @param c the value with which the row will be multiplied.
	 */
	@Override
	public void multiplyRow(int n, Double c) {
		n += rstart;
		for(int j = cstart; j <= cend; j++)
			matrix[n][j] *= c;
	}
	
	/**
	 * Multiplies the matrix (from the right) with the given matrix using a matrix product.
	 * In Einstein notation: M_ij=A_ik*B_kj
	 * Note that this method does not take the augmented columns into account.
	 * @param M the matrix B
	 */
	public void multiplyRight(double[][] M) {
		if(cols != M.length)
			throw new ShapeException("Can't multiply matrices of size ("
					+ rows + "x" + cols + ") and (" + M.length + "x" + M[0].length + ")");
		if(isSubMatrix && M[0].length != cols)
			throw new ShapeException("Cannot multiply submatrix with non-square matrix! Shape (" + M.length + ", "  +M[0].length + ")");
		Double[][] result = new Double[rows][M[0].length];
		for(int i = 0; i < result.length; i++) {
			for(int j = 0; j < result[0].length; j++) {
				result[i][j] = 0d;
				for(int k = 0; k < cols; k++)
					result[i][j] += matrix[i+rstart][k+cstart]*M[k][j];
			}
		}
		rows = result.length;
		cols = result[0].length;
		if(isSubMatrix) {
			for(int i = 0; i < rows; i++)
				for(int j = 0; j < cols; j++)
					matrix[i+rstart][j+cstart] = result[i][j];
		} else			
			matrix = result;
	}
	
	/**
	 * Multiplies the matrix (from the left) with the given matrix using a matrix product.
	 * In Einstein notation: M_ij=B_ik*A_kj
	 * Note that this method does not take the augmented columns into account.
	 * @param M the matrix B
	 */
	public void multiplyLeft(double[][] M) {
		if(M[0].length != rows)
			throw new ShapeException("Can't multiply matrices of size ("
					+ M.length + "x" + M[0].length + ") and (" + rows + "x" + cols + ")");
		if(isSubMatrix && M.length != rows)
			throw new ShapeException("Cannot multiply submatrix with non-square matrix! Shape (" + M.length + ", "  +M[0].length + ")");
		Double[][] result = new Double[M.length][cols];
		for(int i = 0; i < result.length; i++) {
			for(int j = 0; j < result[0].length; j++) {
				result[i][j] = 0d;
				for(int k = 0; k < rows; k++)
					result[i][j] += M[i][k]*matrix[k+rstart][j+cstart];
			}
		}
		rows = result.length;
		cols = result[0].length;
		if(isSubMatrix) {
			for(int i = 0; i < rows; i++)
				for(int j = 0; j < cols; j++)
					matrix[i+rstart][j+cstart] = result[i][j];
		} else			
			matrix = result;
	}
	
	/**
	 * Multiplies the matrix (from the right) with the given vector using a matrix product.
	 * In Einstein notation: w_i=A_ij*v_j
	 * Note that this method does not take the augmented columns into account.
	 * @param M the matrix B
	 */
	public double[] multiplyRight(double[] v) {
		if(v.length != cols)
			throw new ShapeException("Can't multiply matrix of size ("
					+ rows + "x" + cols + ") with vector of size (" + v.length + ") on the right");
		double[] w = new double[rows];
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				w[i] += matrix[i+rstart][j+cstart]*v[j];
		return w;
	}
	
	/**
	 * Multiplies the matrix (from the left) with the given vector using a matrix product.
	 * In Einstein notation: w_i=v_j*A_ji
	 * Note that this method does not take the augmented columns into account.
	 * @param M the matrix B
	 */
	public double[] multiplyLeft(double[] v) {
		if(v.length != rows)
			throw new ShapeException("Can't multiply matrix of size ("
					+ rows + "x" + cols + ") with vector of size (" + v.length + ") on the left");
		double[] w = new double[cols];
		for(int i = 0; i < cols; i++)
			for(int j = 0; j < rows; j++)
				w[i] += v[j]*matrix[j+rstart][i+cstart];
		return w;
	}
	
	public void add(double[][] A) {
		if(A.length != rows || A[0].length != cols)
			throw new ShapeException("Cannot add matrices with different shapes: (" + rows + "x" + cols + ") and (" + A.length + "x" + A[0].length + ")");
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				matrix[i+rstart][j+cstart] += A[i][j];
	}
	
	/**
	 * Recursively reorders the rows of this matrix.
	 * This means that the rows with the most leading zeros will end up on the bottom,
	 * and the rows with no leading zeros will end up at the top.
	 * @author Siemen Geurts
	 * @param maxRow the row at which the reordering stops (used for the recursion. To reorder the whole matrix, set <tt>maxRow=matrix.rows-1</tt>)
	 */
	@Override
	public void reorder(int maxRow) {
		if(maxRow <= rstart) return;
		maxRow = Math.min(maxRow, rows);
		int prevZeros = 0, curZeros = 0;
		for(int i = rstart; i < maxRow; i++) {
			int j = cstart;
			curZeros = 0;
			while(j < cols-augmcols && matrix[i][j] == 0) { j++; curZeros++;}
			if(curZeros < prevZeros)
				switchRows(i-rstart, i-rstart-1);
			else
				prevZeros = curZeros;
		}
		reorder(maxRow-1);
	}
	
	public double[] equalize() {
		double[] s = new double[rows];
		for(int i = 0; i < rows; i++) {
			s[i] = 0;
			for(int j = cstart; j <= cend; j++)
				s[i] += Math.abs(matrix[i+rstart][j]);
			s[i] = 1/s[i];
			for(int j = cstart; j <= cend; j++)
				matrix[i+rstart][j]*=s[i];
		}
		return s;
	}
	
	public int[][] getPivotMatrix() {
		int[][] P = new int[rows][cols];
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				P[i][j] = i==j ? 1 : 0;
		//P=identity
		for(int col = 0; col < rows; col++) {
			double colmax = matrix[col+rstart][col+cstart]; //max element in this column
			int max_row = col;
			for(int i = col; i < rows; i++) {
				if(matrix[i+rstart][col+cstart]>colmax) {
					colmax = matrix[i+rstart][col+cstart];
					max_row = i;
				}
			}
			if(max_row != col) {
				int[] tmp = P[col];
				P[col] = P[max_row];
				P[max_row] = tmp;
			}
		}
		return P;
	}
	
	public void applyPivot(int[][] pivot) {
		Double[][] m = new Double[rows][cols];
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				if(pivot[i][j]==1)
					m[i] = matrix[j+rstart];
		if(!isSubMatrix)
			matrix = m;
		else {
			for(int row = rstart; row<= rend; row++)
				matrix[row] = m[row-rstart];
		}
	}
	
	@Override
	public MMatrix toMMatrix() {
		MReal[][] m = new MReal[rows][cols];
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				m[i][j]= new MReal(matrix[i+rstart][j+cstart]);
		return new MMatrix(m);
	}
	
	@Override
	public boolean isReal() {
		return true;
	}
	
	/**
	 * Checks if the matrix is hermitian. This means that the matrix equals its conjugated transpose.
	 * As this matrix consists only of doubles, this means that the matrix is hermitian iff its symmetric.
	 * @param mask the mask integer holding (at least) the flag SYMMETRIC.
	 * @return {@code true} if the flag SYMMETRIC is on, {@code false} otherwise.
	 */
	@Override
	protected final boolean isHermitian(int mask) {
		return (mask & SYMMETRIC) == SYMMETRIC;
	}
	
	public DoubleMatrixToolkit sub(int rstart, int rend, int cstart, int cend) {
		return new DoubleMatrixToolkit(matrix, rstart+this.rstart, rend+this.rstart, cstart+this.cstart, cend+this.cstart);
	}
	
	/**
	 * Multiplies the matrix (from the right) with the given matrix using a matrix product.
	 * In Einstein notation: M_ij=A_ik*B_kj
	 * Note that this method does not take the augmented columns into account.
	 * @param A
	 * @param B
	 * @return A*B
	 */
	public static Double[][] multiply(Double[][] A, Double[][] B) {
		if(A[0].length != B.length)
			throw new ShapeException("Can't multiply matrices of size ("
					+ A.length + "x" + A[0].length + ") and (" + B.length + "x" + B[0].length + ")");
		Double[][] result = new Double[A.length][B[0].length];
		for(int i = 0; i < result.length; i++) {
			for(int j = 0; j < result[0].length; j++) {
				result[i][j] = 0d;
				for(int k = 0; k < B.length; k++)
					result[i][j] += A[i][k]*B[k][j];
			}
		}
		return result;
	}
	
	/**
	 * Multiplies the matrix (from the right) with the given matrix using a matrix product.
	 * In Einstein notation: M_ij=A_ik*B_kj
	 * Note that this method does not take the augmented columns into account.
	 * @param A
	 * @param B
	 * @return A*B
	 */
	public static Double[][] multiply(double[][] A, Double[][] B) {
		if(A[0].length != B.length)
			throw new ShapeException("Can't multiply matrices of size ("
					+ A.length + "x" + A[0].length + ") and (" + B.length + "x" + B[0].length + ")");
		Double[][] result = new Double[A.length][B[0].length];
		for(int i = 0; i < result.length; i++) {
			for(int j = 0; j < result[0].length; j++) {
				result[i][j] = 0d;
				for(int k = 0; k < B.length; k++)
					result[i][j] += A[i][k]*B[k][j];
			}
		}
		return result;
	}
	
	public static double[][] dyadic(double[] v) {
		double[][] A = new double[v.length][v.length];
		for(int i = 0; i < v.length; i++) {
			A[i][i] = v[i]*v[i];
			for(int j = i+1; j < v.length; j++) {
				A[j][i] = A[i][j] = v[i]*v[j];
			}
		}
		return A;
	}
	
	public static Double[][] multiply(Double[][] A, double d) {
		for(int i = 0; i < A.length; i++)
			for(int j = 0; j < A[0].length; j++)
				A[i][j] *= d;
		return A;
	}
	
	public static Double[][] identity(int n) {
		Double[][] A = new Double[n][n];
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				A[i][j] = (i==j ? 1d : 0d);
		return A;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Printer.toText(sb, matrix, rstart, rend, cstart, cend);
		return sb.toString();
	}
}
