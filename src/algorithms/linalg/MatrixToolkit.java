package algorithms.linalg;

import helpers.exceptions.ShapeException;
import mathobjects.MExpression;
import mathobjects.MMatrix;
import mathobjects.MReal;

public class MatrixToolkit {
	double[][] matrix;
	int rows, cols, augmcols = 0;
	
	public MatrixToolkit(double[][] matrix) {
		this.matrix = matrix;
		rows = matrix.length;
		cols = matrix[0].length;
	}
	
	public void setAugmCols(int cols) {
		augmcols = cols;
	}
	
	public MatrixToolkit(MMatrix m) {
		matrix = new double[m.shape().rows()][m.shape().cols()];
		for(int i = 0; i < m.shape().rows(); i++) {
			for(int j = 0; j < m.shape().cols(); j++) {
				if(m.get(i, j) instanceof MReal)
					matrix[i][j] = ((MReal) m.get(i, j)).getValue();
				else if(m.get(i, j) instanceof MExpression)
					matrix[i][j] = ((MReal) m.evaluate()).getValue();
				else
					throw new IllegalArgumentException("This toolkit only supports scalar-valued matrices, got " + m.getClass());
			}
		}
		rows = matrix.length;
		cols = matrix[0].length;
	}

	/**
	 * Switches rows i and j.
	 * @param i index of the row to be replaced by the <tt>j</tt>-th row.
	 * @param j index of the row to be replaced by the <tt>i</tt>-th row.
	 */
	public void switchRows(int i, int j) {
		if (i == j)
			return;
		double[] temp = matrix[i];
		matrix[i] = matrix[j];
		matrix[j] = temp;
	}

	/**
	 * Adds the i-th row multiplied with c to the n-th row. Like so: <br/>
	 * <tt>M_nj -> M_nj+c*M_ij   with   j=1....m</tt>
	 * @param n the index of the row to which the <tt>i</tt>-th row will be added.
	 * @param i the row to be added to the <tt>n</tt>-th row
	 * @param c a constant with which the i-th row will be multiplied before being added to the <tt>n</tt>-th row.
	 */
	public void addToRow(int n, int i, double c) {
		for(int j = 0; j < matrix[n].length; j++)
			matrix[n][j] += c*matrix[i][j];
	}
	
	/**
	 * Multiplies the <tt>n</tt>-th row with a constant scalar value.
	 * @param n the index of the row to be multiplied with <tt>c</tt>
	 * @param c the value with which the row will be multiplied.
	 */
	public void multiplyRow(int n, double c) {
		for(int j = 0; j < matrix[n].length; j++)
			matrix[n][j] *= c;
	}
	
	/**
	 * Multiplies the matrix (from the right) with the given matrix using a matrix product.
	 * In Einstein notation: M_ij=A_ik*B_kj
	 * Note that this method does not take the augmented columns into account.
	 * @param M
	 */
	public void multiply(double[][] M) {
		if(cols != M.length)
			throw new ShapeException("Can't multiply matrices of size ("
					+ rows + "x" + cols + ") and (" + M.length + "x" + M[0].length + ")");
		double[][] result = new double[rows][M[0].length];
		for(int i = 0; i < result.length; i++) {
			for(int j = 0; j < result[0].length; j++) {
				result[i][j] = 0;
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
	public void reorder(int maxRow) {
		if(maxRow == 0) return;
		int prevZeros = 0, curZeros = 0;
		for(int i = 0; i < maxRow; i++) {
			int j = curZeros = 0;
			while(matrix[i][j] == 0 && j < cols-augmcols) { j++; curZeros++;}
			if(curZeros < prevZeros)
				switchRows(i, i-1);
			else
				prevZeros = curZeros;
		}
		reorder(maxRow-1);
	}	
	
	public double[][] getPivotMatrix() {
		double[][] P = new double[rows][cols];
		for(int i = 0; i < P.length; i++)
			for(int j = 0; j < P[i].length; j++)
				P[i][j] = i==j ? 1 : 0;
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
				double[] tmp = P[col];
				P[col] = P[max_row];
				P[max_row] = tmp;
			}
		}
		return P;
	}
	
	/**
	 * Multiplies the matrix (from the right) with the given matrix using a matrix product.
	 * In Einstein notation: M_ij=A_ik*B_kj
	 * Note that this method does not take the augmented columns into account.
	 * @param A
	 * @param B
	 * @return A*B
	 */
	public static double[][] multiply(double[][] A, double[][] B) {
		if(A[0].length != B.length)
			throw new ShapeException("Can't multiply matrices of size ("
					+ A.length + "x" + A[0].length + ") and (" + B.length + "x" + B[0].length + ")");
		double[][] result = new double[A.length][B[0].length];
		for(int i = 0; i < result.length; i++) {
			for(int j = 0; j < result[0].length; j++) {
				result[i][j] = 0;
				for(int k = 0; k < B.length; k++)
					result[i][j] += A[i][k]*B[k][j];
			}
		}
		return result;
	}
}
