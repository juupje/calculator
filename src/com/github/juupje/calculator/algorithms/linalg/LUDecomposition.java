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
		return lu();
	}
	
	protected MVector lu() {
		int n = mtk.cols;
		double[][] L = new double[n][n];
		double[][] U = new double[n][n];
		Double[][] P = mtk.getPivotMatrix();
		mtk.matrix = DoubleMatrixToolkit.multiply(P, mtk.matrix);
		for(int col = 0; col < n; col++) {
			L[col][col]=1;
			for(int row = 0; row < col+1; row++) {
				double s = 0;
				for(int k = 0; k < row; k++) {
					s += U[k][col]*L[row][k];
				}
				U[row][col] = mtk.matrix[row][col]-s;
			}
			for(int row = col; row < n; row++) {
				double s = 0;
				for(int k = 0; k < col; k++)
					s += U[k][col] * L[row][k];
				L[row][col] = (mtk.matrix[row][col] - s)/U[col][col];
			}
		}
		return new MVector(new MMatrix(L), new MMatrix(U), new DoubleMatrixToolkit(P).toMMatrix());
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
