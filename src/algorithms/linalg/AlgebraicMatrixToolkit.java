package algorithms.linalg;

import helpers.exceptions.ShapeException;
import main.Operator;
import mathobjects.MMatrix;
import mathobjects.MathObject;

public class AlgebraicMatrixToolkit extends MatrixToolkit<MathObject> {
	
	public AlgebraicMatrixToolkit(MathObject[][] matrix) {
		this.matrix = matrix;
		rows = matrix.length;
		cols = matrix[0].length;
	}
	
	public AlgebraicMatrixToolkit(MMatrix m) {
		matrix = m.copy().elements();
		rows = matrix.length;
		cols = matrix[0].length;
	}

	/**
	 * Adds the i-th row multiplied with c to the n-th row. Like so: <br/>
	 * <tt>M_nj -> M_nj+c*M_ij   with   j=1....m</tt>
	 * @param n the index of the row to which the <tt>i</tt>-th row will be added.
	 * @param i the row to be added to the <tt>n</tt>-th row
	 * @param c a constant with which the i-th row will be multiplied before being added to the <tt>n</tt>-th row.
	 */
	@Override
	public void addToRow(int n, int i, MathObject c) { 
		for(int j = 0; j < matrix[n].length; j++)
			matrix[n][j] = Operator.ADD.evaluate(Operator.MULTIPLY.evaluate(c, matrix[i][j]));
	}
	
	/**
	 * Multiplies the <tt>n</tt>-th row with a constant scalar value.
	 * @param n the index of the row to be multiplied with <tt>c</tt>
	 * @param c the value with which the row will be multiplied.
	 */
	@Override
	public void multiplyRow(int n, MathObject c) {
		for(int j = 0; j < matrix[n].length; j++)
			matrix[n][j] = Operator.MULTIPLY.evaluate(matrix[n][j], c);
	}
	
	/**
	 * Multiplies the matrix (from the right) with the given matrix using a matrix product.
	 * In Einstein notation: M_ij=A_ik*B_kj
	 * Note that this method does not take the augmented columns into account.
	 * @param M
	 */
	@Override
	public void multiply(MathObject[][] M) {
		if(cols != M.length)
			throw new ShapeException("Can't multiply matrices of size ("
					+ rows + "x" + cols + ") and (" + M.length + "x" + M[0].length + ")");
		MathObject[][] result = new MathObject[rows][M[0].length];
		for(int i = 0; i < result.length; i++) {
			for(int j = 0; j < result[0].length; j++) {
				result[i][j] = null;
				for(int k = 0; k < cols; k++)
					result[i][j] = Operator.ADD.evaluate(result[i][j], Operator.MULTIPLY.evaluate(matrix[i][k],M[k][j]));
			}
		}
		rows = result.length;
		cols = result[0].length;
		matrix = result;
	}
	
	@Override
	public MMatrix toMMatrix() {
		return new MMatrix(matrix);
	}
	
	@Override
	public boolean isReal() {
		return false;
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
			while(matrix[i][j].equals(0) && j < cols-augmcols) { j++; curZeros++;}
			if(curZeros < prevZeros)
				switchRows(i, i-1);
			else
				prevZeros = curZeros;
		}
		reorder(maxRow-1);
	}
}