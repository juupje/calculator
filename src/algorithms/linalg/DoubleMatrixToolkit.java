package algorithms.linalg;

import helpers.exceptions.ShapeException;
import mathobjects.MExpression;
import mathobjects.MMatrix;
import mathobjects.MReal;

public class DoubleMatrixToolkit extends MatrixToolkit<Double> {

	public DoubleMatrixToolkit(double[][] matrix) {
		rows = matrix.length;
		cols = matrix[0].length;
		this.matrix = new Double[rows][cols];
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				this.matrix[i][j] = matrix[i][j];
	}
	
	public DoubleMatrixToolkit(Double[][] matrix) {
		this.matrix = matrix;
		rows = matrix.length;
		cols = matrix[0].length;
	}
	
	public DoubleMatrixToolkit(MMatrix m) {
		rows = m.shape().rows();
		cols = m.shape().cols();
		matrix = new Double[rows][cols];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				if(m.get(i, j) instanceof MReal)
					matrix[i][j] = ((MReal) m.get(i, j)).getValue();
				else if(m.get(i, j) instanceof MExpression)
					matrix[i][j] = ((MReal) m.get(i,j).evaluate()).getValue();
				else
					throw new IllegalArgumentException("This toolkit only supports scalar-valued matrices, got " + m.getClass());
			}
		}
	}

	/**
	 * Adds the i-th row multiplied with c to the n-th row. Like so: <br/>
	 * <tt>M_nj -> M_nj+c*M_ij   with   j=1....m</tt>
	 * @param n the index of the row to which the <tt>i</tt>-th row will be added.
	 * @param i the row to be added to the <tt>n</tt>-th row
	 * @param c a constant with which the i-th row will be multiplied before being added to the <tt>n</tt>-th row.
	 */
	@Override
	public void addToRow(int n, int i, Double c) { 
		for(int j = 0; j < matrix[n].length; j++)
			matrix[n][j] += c*matrix[i][j];
	}
	
	/**
	 * Multiplies the <tt>n</tt>-th row with a constant scalar value.
	 * @param n the index of the row to be multiplied with <tt>c</tt>
	 * @param c the value with which the row will be multiplied.
	 */
	@Override
	public void multiplyRow(int n, Double c) {
		for(int j = 0; j < matrix[n].length; j++)
			matrix[n][j] *= c;
	}
	
	/**
	 * Multiplies the matrix (from the right) with the given matrix using a matrix product.
	 * In Einstein notation: M_ij=A_ik*B_kj
	 * Note that this method does not take the augmented columns into account.
	 * @param M
	 */
	@Override
	public void multiply(Double[][] M) {
		if(cols != M.length)
			throw new ShapeException("Can't multiply matrices of size ("
					+ rows + "x" + cols + ") and (" + M.length + "x" + M[0].length + ")");
		Double[][] result = new Double[rows][M[0].length];
		for(int i = 0; i < result.length; i++) {
			for(int j = 0; j < result[0].length; j++) {
				result[i][j] = 0d;
				for(int k = 0; k < cols; k++)
					result[i][j] += matrix[i][k]*M[k][j];
			}
		}
		rows = result.length;
		cols = result[0].length;
		matrix = result;
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
		if(maxRow == 0) return;
		int prevZeros = 0, curZeros = 0;
		for(int i = 0; i < maxRow; i++) {
			int j = curZeros = 0;
			while(j < cols-augmcols && matrix[i][j] == 0) { j++; curZeros++;}
			if(curZeros < prevZeros)
				switchRows(i, i-1);
			else
				prevZeros = curZeros;
		}
		reorder(maxRow-1);
	}	
	
	public Double[][] getPivotMatrix() {
		Double[][] P = new Double[rows][cols];
		for(int i = 0; i < P.length; i++)
			for(int j = 0; j < P[i].length; j++)
				P[i][j] = i==j ? 1d : 0d;
		for(int col = 0; col < rows; col++) {
			double colmax = matrix[col][col]; //max element in this column
			int max_row = col;
			for(int i = col; i < rows; i++) {
				if(matrix[i][col]>colmax) {
					colmax = matrix[i][col];
					max_row = i;
				}
			}
			if(max_row != col) {
				Double[] tmp = P[col];
				P[col] = P[max_row];
				P[max_row] = tmp;
			}
		}
		return P;
	}
	
	@Override
	public MMatrix toMMatrix() {
		MReal[][] m = new MReal[rows][cols];
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				m[i][j]= new MReal(matrix[i][j]);
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
	public boolean isHermitian(int mask) {
		return (mask & SYMMETRIC) == SYMMETRIC;
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
}
