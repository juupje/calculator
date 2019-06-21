package algorithms.linalg;

import mathobjects.MMatrix;
import mathobjects.MReal;

public abstract class MatrixToolkit<T> {
	T[][] matrix;
	int rows, cols, augmcols = 0;
	
	public static MatrixToolkit<?> getToolkit(MMatrix m) {
		boolean allReal = true;
		for(int i = 0; i < m.shape().rows(); i++) {
			for(int j = 0; j < m.shape().cols(); j++) {
				if(!(m.get(i, j) instanceof MReal)) {
					allReal = false;
					break;
				}
			}
			if(!allReal)
				break;
		}
		if(allReal)
			return new DoubleMatrixToolkit(m);
		else
			return new AlgebraicMatrixToolkit(m);
	}
	
	public void setAugmCols(int cols) {
		augmcols = cols;
	}

	/**
	 * Switches rows i and j.
	 * @param i index of the row to be replaced by the <tt>j</tt>-th row.
	 * @param j index of the row to be replaced by the <tt>i</tt>-th row.
	 */
	public void switchRows(int i, int j) {
		if (i == j)
			return;
		T[] temp = matrix[i];
		matrix[i] = matrix[j];
		matrix[j] = temp;
	}
	
	public abstract MMatrix toMMatrix();
	
	public abstract boolean isReal();

	/**
	 * Adds the i-th row multiplied with c to the n-th row. Like so: <br/>
	 * <tt>M_nj -> M_nj+c*M_ij   with   j=1....m</tt>
	 * @param n the index of the row to which the <tt>i</tt>-th row will be added.
	 * @param i the row to be added to the <tt>n</tt>-th row
	 * @param c a constant with which the i-th row will be multiplied before being added to the <tt>n</tt>-th row.
	 */
	public abstract void addToRow(int n, int i, T c);
	
	/**
	 * Multiplies the <tt>n</tt>-th row with a constant scalar value.
	 * @param n the index of the row to be multiplied with <tt>c</tt>
	 * @param c the value with which the row will be multiplied.
	 */
	public abstract void multiplyRow(int n, T c);
	
	/**
	 * Multiplies the matrix (from the right) with the given matrix using a matrix product.
	 * In Einstein notation: M_ij=A_ik*B_kj
	 * Note that this method does not take the augmented columns into account.
	 * @param M
	 */
	public abstract void multiply(T[][] M);
	
	/**
	 * Recursively reorders the rows of this matrix.
	 * This means that the rows with the most leading zeros will end up on the bottom,
	 * and the rows with no leading zeros will end up at the top.
	 * @author Siemen Geurts
	 * @param maxRow the row at which the reordering stops (used for the recursion. To reorder the whole matrix, set <tt>maxRow=matrix.rows-1</tt>)
	 */
	public abstract void reorder(int maxRow);
}
