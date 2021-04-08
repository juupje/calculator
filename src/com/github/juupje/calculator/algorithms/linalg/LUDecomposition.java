package com.github.juupje.calculator.algorithms.linalg;

import com.github.juupje.calculator.algorithms.Algorithm;
import com.github.juupje.calculator.helpers.Tools;
import com.github.juupje.calculator.helpers.exceptions.ShapeException;
import com.github.juupje.calculator.mathobjects.MMatrix;
import com.github.juupje.calculator.mathobjects.MVector;
import com.github.juupje.calculator.mathobjects.MathObject;
import com.github.juupje.calculator.mathobjects.Shape;

public class LUDecomposition extends Algorithm {

	Shape shape;
	DoubleMatrixToolkit mtk;
	int permutations = 0;
	
	public LUDecomposition() {}
	
	public LUDecomposition(MMatrix m) {
		MatrixToolkit<?> tk = MatrixToolkit.getToolkit(m);
		if(!(tk instanceof DoubleMatrixToolkit))
			throw new IllegalArgumentException("LU-Decomposition only works for real-values matrices.");
		mtk = (DoubleMatrixToolkit) tk;
		shape = new Shape(mtk.rows, mtk.cols);
		prepared = true;
	}
	
	@Override
	public MVector execute() {
		if(!prepared) return null;
		permutations = 0;
		return lu();
	}
	
	protected MVector lu() {
		int n = mtk.cols;
		double[][] L = new double[n][n];
		int[][] P = new int[n][n];
		int[] order = new int[n];
		for(int i = 0; i < n; i++) order[i] = i;
		
		for(int col = 0; col < n; col++) {
			int pivotIndex = pivotColumn(col);
			if(pivotIndex != col) {
				//Switch order[col] with order[pivotIndex]
				permutations++;
				int temp = order[col]; order[col] = order[pivotIndex]; order[pivotIndex] = temp;		
			}
			L[col][col]=1;
			for(int row = col+1; row < mtk.rows; row++) {
				L[row][col] = mtk.matrix[row][col]/mtk.matrix[col][col];
				mtk.matrix[row][col] = 0d;
				for(int i = col+1; i < mtk.cols; i++)
					mtk.matrix[row][i] -= L[row][col]*mtk.matrix[col][i];
			}
		}
		for(int row = 0; row < mtk.rows; row++) {
			P[row][order[row]] = 1;
		}
		return new MVector(new MMatrix(L), new MMatrix(mtk.matrix), new MMatrix(P));
	}
	
	/**
	 * Switches the current row (and index {@code col}) with the row containing the absolute highest entry in the same column below it.
	 * @return the index of the row with which the row at index {@code col} was switched.
	 */
	private int pivotColumn(int col) {
		double max = Math.abs(mtk.matrix[col][col]);
		int indexMax = col;
		for(int i = col+1; i < mtk.rows; i++)
			if(Math.abs(mtk.matrix[i][col])>max) {
				max = Math.abs(mtk.matrix[i][col]);
				indexMax = i;
			}
		mtk.switchRows(col, indexMax);
		return indexMax;
	}
	
	public int getPermutationCount() {
		return permutations;
	}
	
	@Override
	public MVector execute(MathObject... args) {
		prepare(args);
		return execute();
	}
	
	@Override
	public void prepare(MathObject[] args) {
		super.prepare(args);
		if(args.length == 1 && args[0] instanceof MMatrix) {
			//no need to copy as the toolkit only works with the double values.
			MatrixToolkit<?> tk = MatrixToolkit.getToolkit((MMatrix) args[0]);
			if(!(tk instanceof DoubleMatrixToolkit))
				throw new IllegalArgumentException("LU-Decomposition only works for real-values matrices.");
			mtk = (DoubleMatrixToolkit) tk; 
		}else
			throw new IllegalArgumentException("Arguments " + argTypesToString(args) + " not applicable for LU-decomposition, see help for correct use.");
		shape = new Shape(mtk.rows, mtk.cols);
	}
	
	@Override
	public Shape shape(Shape... shapes)  {
		if(shapes.length==0)
			throw new IllegalArgumentException("No arguments found.");
		if(shapes.length==1 && shapes[0].dim()==2) {
			if(shapes[0].rows() == shapes[0].cols())
				return shapes[0];
			else
				throw new ShapeException("LU-decomposition only works for square matrices.");
		}
		throw new IllegalArgumentException("Algorithm not defined for shapes " + Tools.join(", ", (Object[]) shapes));
	}

}
